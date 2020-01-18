package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myproject.thepodcast.http.PodcastHttpClient;
import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.FavouriteEpisode;
import com.myproject.thepodcast.model.HistoryEpisode;
import com.myproject.thepodcast.model.Podcast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EpisodeRepositoryImpl implements
        EpisodeRepository { //todo move DAO logic to view model

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private PodcastDatabase mDatabase;

    private EpisodeRepositoryImpl() {

    }

    EpisodeRepositoryImpl(@NonNull final PodcastDatabase database) {
        mDatabase = database;
    }


    @Override
    public void listFavourites(@NonNull final ListFavouriteEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<FavouriteEpisode> favouriteEpisodes = mDatabase.podcastDao().listFavourites();
                callback.onEpisodeListLoaded(favouriteEpisodes);
            }
        });
    }


    @Override
    public void likeUnlike(@NonNull final LikeUnlikeEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HistoryEpisode historyEpisode = mDatabase.podcastDao().getLatest();
                FavouriteEpisode favouriteEpisode = mDatabase.podcastDao().getFavourite(
                        historyEpisode.getId());
                if (favouriteEpisode != null) {
                    mDatabase.podcastDao().unFavourite(favouriteEpisode);
                    callback.onUnLike();
                } else {
                    mDatabase.podcastDao().favourite(new FavouriteEpisode(historyEpisode));
                    callback.onLike();
                }

            }
        });
    }


    @Override
    public void getEpisodeList(@NonNull final String podcastId,
            final @NonNull ListEpisodeCallback callback) {

        PodcastHttpClient.get("/podcasts/" + podcastId, new HashMap<String, String>(),
                new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        //todo handle exceptions
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response)
                            throws IOException {
                        GsonBuilder builder = new GsonBuilder();
                        Gson gson = builder.create();
                        Podcast podcast = gson.fromJson(response.body().string(), Podcast.class);
                        setPodcastToEpisode(podcast, podcast.getEpisodeList());
                        callback.onEpisodeListLoaded(podcast.getEpisodeList());
                    }
                });

    }

    @Override
    public void addHistory(@NonNull final Episode episode,
            @Nullable final InsertEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HistoryEpisode historyEpisode = new HistoryEpisode(episode);
                long result = mDatabase.podcastDao().addHistory(historyEpisode);
                if (callback == null) {
                    return;
                }
                if (result > 0L) {
                    callback.onSuccess();
                } else {
                    callback.onError();
                }
            }
        });
    }

    @Override
    public void listHistory(@NonNull final ListHistoryEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<HistoryEpisode> result = mDatabase.podcastDao().listHistory();
                    callback.onEpisodeListLoaded(result);
                } catch (Exception e) {
                    e.printStackTrace(); //todo add error handler
                }

            }
        });
    }

    @Override
    public void getFavourite(@NonNull final String episodeId,
            @NonNull final GetFavouriteEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                FavouriteEpisode favouriteEpisode = mDatabase.podcastDao().getFavourite(episodeId);
                callback.onFavouriteEpisodeLoaded(favouriteEpisode);
            }
        });
    }

    @Override
    public void getCurrent(@NonNull final GetEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HistoryEpisode historyEpisode = mDatabase.podcastDao().getLatest();
                PodcastHttpClient.get("/episodes/" + historyEpisode.getId(),
                        new HashMap<String, String>(),
                        new Callback() {
                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                //todo handle exceptions
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response)
                                    throws IOException {
                                GsonBuilder builder = new GsonBuilder();
                                Gson gson = builder.create();
                                Episode episode = gson.fromJson(response.body().string(),
                                        Episode.class);
                                callback.onEpisodeLoaded(episode);
                            }
                        });
            }
        });
    }

    @Override
    public void addDownloaded(@NonNull final DownloadedEpisode episode,
            @Nullable final InsertEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                long result = mDatabase.podcastDao().addDownload(episode);
                if (callback == null) {
                    return;
                }
                if (result > 0L) {
                    callback.onSuccess();
                } else {
                    callback.onError();
                }
            }
        });
    }


    @Override
    public void listDownloaded(
            @NonNull final ListDownloadedEpisodeCallback callback) {

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<DownloadedEpisode> episodes = mDatabase.podcastDao().listDownloaded();
                callback.onEpisodeListLoaded(episodes);
            }
        });

    }

    @Override
    public void isDownloaded(@NonNull final String id, @NonNull final GetEpisodeCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                DownloadedEpisode episode = mDatabase.podcastDao().getDownloadedById(id);
                callback.onEpisodeLoaded(episode);
            }
        });

    }


    @Override
    public DownloadedEpisode getDownloaded(long requestId) {
        return mDatabase.podcastDao().getDownloadedByRequestId(requestId);
    }


    private void setPodcastToEpisode(@Nullable final Podcast podcast,
            @Nullable final List<Episode> episodeList) {
        if (podcast != null && episodeList != null) {
            for (Episode episode : episodeList) {
                episode.setPodcast(podcast);
            }
        }
    }
}
