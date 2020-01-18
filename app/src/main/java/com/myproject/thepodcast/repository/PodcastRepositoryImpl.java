package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myproject.thepodcast.http.PodcastHttpClient;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.model.PodcastResultList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PodcastRepositoryImpl implements PodcastRepository {

    private PodcastResultList mRecommendedPodcastPagedList;
    private PodcastDatabase mDatabase;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    PodcastRepositoryImpl(@NonNull final PodcastDatabase database) {
        mDatabase = database;
    }


    @Override
    public void listRecommended(@NonNull final ListPodcastCallback callback) {
        if (mRecommendedPodcastPagedList != null && !mRecommendedPodcastPagedList.isEmpty()) {
            callback.onPodcastListLoaded(mRecommendedPodcastPagedList);
        } else {
            QueryParam.QueryParamBuilder builder = new QueryParam.QueryParamBuilder();
            QueryParam queryParam = builder.put("region", "us").build();
            Map<String, String> params = queryParam.getMapParams();
            PodcastHttpClient.get("/best_podcasts", params, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //todo handle exceptions
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response)
                        throws IOException {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    String res = response.body().string();
                    mRecommendedPodcastPagedList = gson.fromJson(res, PodcastResultList.class);

                    callback.onPodcastListLoaded(mRecommendedPodcastPagedList);
                }
            });
        }
    }

    @Override
    public void listFollowingShows(@NonNull final LoadFollowingPodcastListCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                List<Podcast> podcasts = mDatabase.podcastDao().listFollowingShows();
                callback.onPodcastListLoaded(podcasts);
            }
        });

    }


    @Override
    public void followUnFollow(@NonNull final Podcast podcast,
            @NonNull final FollowUnFollowPodcastCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Podcast following = mDatabase.podcastDao().getFollowingShow(podcast.getId());
                if (following == null) {
                    long result = mDatabase.podcastDao().follow(podcast);
                    if (result > 0L) {
                        callback.onFollow();
                    } else {
                        callback.onError();
                    }

                } else {
                    long result = mDatabase.podcastDao().unFollow(podcast);
                    if (result > 0L) {
                        callback.onUnFollow();
                    } else {
                        callback.onError();
                    }
                }
            }
        });
    }


    @Override
    public void isFollowing(@NonNull final String id, final LoadFollowingPodcastCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                Podcast podcast = mDatabase.podcastDao().getFollowingShow(id);
                callback.onPodcastLoaded(podcast);
            }
        });
    }


}
