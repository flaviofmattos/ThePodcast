package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.model.PodcastResultList;

import java.util.List;

public interface PodcastRepository {

    void listRecommended(@NonNull ListPodcastCallback callback);

    void listFollowingShows(@NonNull LoadFollowingPodcastListCallback callback);

    void followUnFollow(@NonNull Podcast podcast, FollowUnFollowPodcastCallback callback);

    void isFollowing(@NonNull String id, LoadFollowingPodcastCallback callback);

    interface ListPodcastCallback {

        void onPodcastListLoaded(PodcastResultList pagedList);

        void onError();
    }


    interface LoadFollowingPodcastListCallback {

        void onPodcastListLoaded(List<Podcast> pagedList);
    }

    interface FollowUnFollowPodcastCallback {
        void onFollow();

        void onUnFollow();

        void onError();
    }

    interface LoadFollowingPodcastCallback {

        void onPodcastLoaded(@Nullable Podcast podcast);
    }

}
