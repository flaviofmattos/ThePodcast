package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.model.PodcastResultList;
import com.myproject.thepodcast.repository.EpisodeRepository;
import com.myproject.thepodcast.repository.PodcastRepository;

import java.util.List;

public class RecommendedViewModel extends ViewModel implements PodcastSelectorHandler {

    private MutableLiveData<List<Podcast>> mRecommendedPodcastList = new MutableLiveData<>();
    private MutableLiveData<Event<Podcast>> mSelectedPodcast = new MutableLiveData<>();
    private PodcastRepository mPodcastRepository;

    public RecommendedViewModel(@NonNull final PodcastRepository podcastRepository) {
        mPodcastRepository = podcastRepository;
        loadRecommended();
    }


    public LiveData<List<Podcast>> getRecommendedPodcastList() {
        return mRecommendedPodcastList;
    }


    private void loadRecommended() {
        mPodcastRepository.listRecommended(new PodcastRepository.ListPodcastCallback() {
            @Override
            public void onPodcastListLoaded(PodcastResultList pagedList) {
                mRecommendedPodcastList.postValue(pagedList.getDataList());
            }

            @Override
            public void onError() {

            }
        });
    }

    @Override
    public void select(@NonNull Podcast podcast) {
        mSelectedPodcast.postValue(new Event<>(podcast));
    }

    public MutableLiveData<Event<Podcast>> getSelectedPodcast() {
        return mSelectedPodcast;
    }

    public static class Factory implements ViewModelProvider.Factory {

        private PodcastRepository mPodcastRepository;

        public Factory(@NonNull final PodcastRepository podcastRepository) {
            mPodcastRepository = podcastRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new RecommendedViewModel(mPodcastRepository);
        }
    }

}
