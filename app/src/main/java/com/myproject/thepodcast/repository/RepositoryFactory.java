package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;

public final class RepositoryFactory {

    private static RepositoryFactory sRepositoryFactory;
    private static PodcastDatabase sPodcastDatabase;

    private EpisodeRepository mEpisodeRepository;
    private PodcastRepository mPodcastRepository;

    private RepositoryFactory() {

    }

    public static synchronized RepositoryFactory getInstance(
            @NonNull final PodcastDatabase podcastDatabase) {
        if (sRepositoryFactory == null) {
            sRepositoryFactory = new RepositoryFactory();
            sPodcastDatabase = podcastDatabase;
        }
        return sRepositoryFactory;
    }


    public synchronized EpisodeRepository getEpisodeRepositoryInstance() {
        if (mEpisodeRepository == null) {
            mEpisodeRepository = new EpisodeRepositoryImpl(sPodcastDatabase);
        }
        return mEpisodeRepository;
    }


    public synchronized PodcastRepository getPodcastRepositoryInstance() {
        if (mPodcastRepository == null) {
            mPodcastRepository = new PodcastRepositoryImpl(sPodcastDatabase);
        }
        return mPodcastRepository;
    }

}
