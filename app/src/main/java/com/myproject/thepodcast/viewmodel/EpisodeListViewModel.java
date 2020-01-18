package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.FavouriteEpisode;
import com.myproject.thepodcast.repository.EpisodeRepository;

import java.util.List;

public class EpisodeListViewModel extends ViewModel implements EpisodeSelectorHandler {

    private String mPodcastId;
    private EpisodeRepository mEpisodeRepository;
    private MutableLiveData<Event<? extends Episode>> mSelectedEpisode = new MutableLiveData<>();
    private MutableLiveData<List<Episode>> mEpisodeList = new MutableLiveData<>();

    public EpisodeListViewModel(@NonNull final String podcastId,
            @NonNull final EpisodeRepository episodeRepository) {
        mPodcastId = podcastId;
        mEpisodeRepository = episodeRepository;
        loadEpisodes();
    }


    public MutableLiveData<List<Episode>> getEpisodeList() {
        return mEpisodeList;
    }

    private void loadEpisodes() {
        mEpisodeRepository.getEpisodeList(mPodcastId, new EpisodeRepository.ListEpisodeCallback() {
            @Override
            public void onEpisodeListLoaded(List<Episode> episodeList) {
                mEpisodeList.postValue(episodeList);
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void select(@NonNull final Episode episode) {
        mEpisodeRepository.getFavourite(episode.getId(), new EpisodeRepository.GetFavouriteEpisodeCallback() {
            @Override
            public void onFavouriteEpisodeLoaded(@Nullable FavouriteEpisode favouriteEpisode) {
                if (favouriteEpisode != null) {
                    mSelectedEpisode.postValue(new Event<>(favouriteEpisode));
                } else {
                    mSelectedEpisode.postValue(new Event<>(episode));
                }
            }
        });

    }


    public MutableLiveData<Event<? extends Episode>> getSelectedEpisode() {
        return mSelectedEpisode;
    }


    public static class Factory implements ViewModelProvider.Factory {

        private String mPodcastId;
        private EpisodeRepository mEpisodeRepository;

        public Factory(@NonNull final String podcastId,
                @NonNull final EpisodeRepository episodeRepository) {
            mPodcastId = podcastId;
            mEpisodeRepository = episodeRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new EpisodeListViewModel(mPodcastId, mEpisodeRepository);
        }
    }
}
