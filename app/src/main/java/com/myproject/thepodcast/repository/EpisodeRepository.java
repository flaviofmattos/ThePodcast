package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.FavouriteEpisode;
import com.myproject.thepodcast.model.HistoryEpisode;

import java.util.List;

public interface EpisodeRepository {


    void listFavourites(@NonNull ListFavouriteEpisodeCallback callback);

    void likeUnlike(@NonNull LikeUnlikeEpisodeCallback callback);

    void getEpisodeList(@NonNull String podcastId,
            @NonNull  ListEpisodeCallback callback);

    void addHistory(@NonNull Episode episode, @Nullable InsertEpisodeCallback callback);

    void listHistory(@NonNull ListHistoryEpisodeCallback callback);

    void getFavourite(@NonNull String episodeId, @NonNull GetFavouriteEpisodeCallback callback);

    void getCurrent(@NonNull GetEpisodeCallback callback);

    void addDownloaded(@NonNull DownloadedEpisode episode, @Nullable InsertEpisodeCallback callback);

    void listDownloaded(@NonNull ListDownloadedEpisodeCallback callback);

    void isDownloaded(@NonNull String id, @NonNull GetEpisodeCallback callback);

    interface ListEpisodeCallback {

        void onEpisodeListLoaded(@Nullable List<Episode> episodeList);

        void onError();
    }

    DownloadedEpisode getDownloaded(long requestId);

    interface GetFavouriteEpisodeCallback {
        void onFavouriteEpisodeLoaded(@Nullable FavouriteEpisode favouriteEpisode);
    }


    interface GetEpisodeCallback {
        void onEpisodeLoaded(@Nullable Episode episode);
    }


    interface ListHistoryEpisodeCallback {
        void onEpisodeListLoaded(@Nullable List<HistoryEpisode> episodeList);
    }


    interface ListFavouriteEpisodeCallback {
        void onEpisodeListLoaded(@Nullable List<FavouriteEpisode> episodeList);
    }


    interface ListDownloadedEpisodeCallback {
        void onEpisodeListLoaded(@Nullable List<DownloadedEpisode> episodeList);
    }

    interface InsertEpisodeCallback {
        void onSuccess();

        void onError();
    }


    interface LikeUnlikeEpisodeCallback {
        void onLike();

        void onUnLike();

        void onError();
    }

}
