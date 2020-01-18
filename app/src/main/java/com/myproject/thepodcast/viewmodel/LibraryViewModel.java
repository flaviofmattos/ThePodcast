package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.FavouriteEpisode;
import com.myproject.thepodcast.model.HistoryEpisode;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.repository.EpisodeRepository;
import com.myproject.thepodcast.repository.PodcastRepository;

import java.util.List;

public class LibraryViewModel extends ViewModel implements EpisodeSelectorHandler, PodcastSelectorHandler {

    private MutableLiveData<Event<Episode>> mSelectedEpisode = new MutableLiveData<>();
    private MutableLiveData<List<? extends Episode>> mLibraryEpisodeList = new MutableLiveData<>();

    private MutableLiveData<List<Podcast>> mLibraryPodcastList = new MutableLiveData<>();
    private MutableLiveData<Event<Podcast>> mSelectedPodcast = new MutableLiveData<>();

    private MutableLiveData<Boolean> mDisplayPodcasts = new MutableLiveData<>();

    private PodcastRepository mPodcastRepository;
    private EpisodeRepository mEpisodeRepository;

    public LibraryViewModel(@NonNull final PodcastRepository podcastRepository,
            @NonNull final EpisodeRepository episodeRepository) {
        mPodcastRepository = podcastRepository;
        mEpisodeRepository = episodeRepository;

    }


    public void loadEpisodeHistory() {
        mDisplayPodcasts.postValue(false);
        mEpisodeRepository.listHistory(new EpisodeRepository.ListHistoryEpisodeCallback() {
            @Override
            public void onEpisodeListLoaded(@Nullable List<HistoryEpisode> episodeList) {
                mLibraryEpisodeList.postValue(episodeList);
            }

        });
    }


    public void loadFavouriteEpisodes() {
        mDisplayPodcasts.postValue(false);
        mEpisodeRepository.listFavourites(new EpisodeRepository.ListFavouriteEpisodeCallback() {
            @Override
            public void onEpisodeListLoaded(@Nullable List<FavouriteEpisode> episodeList) {
                mLibraryEpisodeList.postValue(episodeList);
            }
        });
    }


    public void loadDownloadedEpisodes() {
        mDisplayPodcasts.postValue(false);
        mEpisodeRepository.listDownloaded(new EpisodeRepository.ListDownloadedEpisodeCallback() {
            @Override
            public void onEpisodeListLoaded(@Nullable List<DownloadedEpisode> episodeList) {
                mLibraryEpisodeList.postValue(episodeList);
            }
        });
    }


    public void loadFollowingShows() {
        mDisplayPodcasts.postValue(true);
        mPodcastRepository.listFollowingShows(new PodcastRepository.LoadFollowingPodcastListCallback() {
            @Override
            public void onPodcastListLoaded(List<Podcast> podcastList) {
                mLibraryPodcastList.postValue(podcastList);
            }
        });
    }


    @Override
    public void select(@NonNull final Episode episode) {
        mSelectedEpisode.postValue(new Event<>(episode));
    }


    @Override
    public void select(@NonNull Podcast podcast) {
        mSelectedPodcast.postValue(new Event<>(podcast));
    }


    public LiveData<List<? extends Episode>> getLibraryEpisodeList() {
        return mLibraryEpisodeList;
    }


    public LiveData<Event<Episode>> getSelectedEpisode() {
        return mSelectedEpisode;
    }


    public MutableLiveData<List<Podcast>> getLibraryPodcastList() {
        return mLibraryPodcastList;
    }


    public MutableLiveData<Event<Podcast>> getSelectedPodcast() {
        return mSelectedPodcast;
    }

    public LiveData<Boolean> getDisplayPodcasts() {
        return mDisplayPodcasts;
    }

    public static class Factory implements ViewModelProvider.Factory {

        private PodcastRepository mPodcastRepository;
        private EpisodeRepository mEpisodeRepository;

        public Factory(@NonNull final PodcastRepository podcastRepository,
                @NonNull final EpisodeRepository episodeRepository) {
            mPodcastRepository = podcastRepository;
            mEpisodeRepository = episodeRepository;
        }


        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LibraryViewModel(mPodcastRepository, mEpisodeRepository);
        }
    }

}
