package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.repository.EpisodeRepository;
import com.myproject.thepodcast.repository.PodcastRepository;

public class PodcastDetailViewModel extends ViewModel {

    private EpisodeRepository mEpisodeRepository;
    private PodcastRepository mPodcastRepository;

    private MutableLiveData<Episode> mCurrentEpisode = new MutableLiveData<>();
    private MutableLiveData<Event<Episode>> mShareEpisode = new MutableLiveData<>();
    private MutableLiveData<Event<Episode>> mDownloadEpisode = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsFollowing = new MutableLiveData<>();
    private MutableLiveData<Boolean> mIsDownloaded = new MutableLiveData<>();
    private MutableLiveData<Event<Boolean>> mFollowUnFollow = new MutableLiveData<>();



    public PodcastDetailViewModel(@NonNull final EpisodeRepository episodeRepository,
            @NonNull final PodcastRepository podcastRepository) {
        mEpisodeRepository = episodeRepository;
        mPodcastRepository = podcastRepository;
        loadCurrent();
    }


    public void downloadEpisode() {
        if (mIsDownloaded.getValue() == null || !mIsDownloaded.getValue()) {
            mDownloadEpisode.postValue(new Event<>(mCurrentEpisode.getValue()));
        }

    }

    public void followUnFollowPodcast() {
        Episode episode = mCurrentEpisode.getValue();
        if (episode != null) {
            final Podcast podcast = episode.getPodcast();
            if (podcast != null) {

                mPodcastRepository.followUnFollow(podcast,
                        new PodcastRepository.FollowUnFollowPodcastCallback() {
                            @Override
                            public void onFollow() {
                                mIsFollowing.postValue(true);
                                mFollowUnFollow.postValue(new Event<>(true));
                            }

                            @Override
                            public void onUnFollow() {
                                mIsFollowing.postValue(false);
                                mFollowUnFollow.postValue(new Event<>(false));
                            }

                            @Override
                            public void onError() {
                                //todo throw an error (display error message)
                            }
                        });

            } else {
                //todo throw an error (display error message)
            }
        }
    }


    public void shareEpisode() {
        mShareEpisode.postValue(new Event<>(mCurrentEpisode.getValue()));
    }


    private void loadCurrent() {
        mEpisodeRepository.getCurrent(new EpisodeRepository.GetEpisodeCallback() {
            @Override
            public void onEpisodeLoaded(@Nullable Episode current) {

                if (current != null && current.getPodcast() != null) {
                    mPodcastRepository.isFollowing(current.getPodcast().getId(),
                            new PodcastRepository.LoadFollowingPodcastCallback() {
                                @Override
                                public void onPodcastLoaded(@Nullable Podcast podcast) {
                                    mIsFollowing.postValue(podcast != null);
                                }
                            });
                }
                mEpisodeRepository.isDownloaded(current.getId(),
                        new EpisodeRepository.GetEpisodeCallback() {
                            @Override
                            public void onEpisodeLoaded(@Nullable Episode episode) {
                                mIsDownloaded.postValue(episode != null);
                            }
                        });
                mCurrentEpisode.postValue(current);
            }
        });
    }


    public MutableLiveData<Event<Episode>> getDownloadEpisode() {
        return mDownloadEpisode;
    }

    public LiveData<Episode> getCurrentEpisode() {
        return mCurrentEpisode;
    }

    public LiveData<Event<Episode>> getShareEpisode() {
        return mShareEpisode;
    }

    public LiveData<Boolean> getIsFollowing() {
        return mIsFollowing;
    }

    public LiveData<Boolean> getIsDownloaded() {
        return mIsDownloaded;
    }

    public MutableLiveData<Event<Boolean>> getFollowUnFollow() {
        return mFollowUnFollow;
    }

    public static class Factory implements ViewModelProvider.Factory {

        private EpisodeRepository mEpisodeRepository;
        private PodcastRepository mPodcastRepository;

        public Factory(@NonNull final EpisodeRepository episodeRepository,
                @NonNull final PodcastRepository podcastRepository) {
            mEpisodeRepository = episodeRepository;
            mPodcastRepository = podcastRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new PodcastDetailViewModel(mEpisodeRepository, mPodcastRepository);
        }

    }

}
