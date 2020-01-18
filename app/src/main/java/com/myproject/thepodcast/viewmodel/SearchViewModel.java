package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.model.ResultList;
import com.myproject.thepodcast.model.TypeAheadSearchResultList;
import com.myproject.thepodcast.repository.SearchRepository;

import java.util.List;

public class SearchViewModel extends ViewModel implements EpisodeSelectorHandler,
        PodcastSelectorHandler {

    private MutableLiveData<List<String>> mTypeAheadSearchResult = new MutableLiveData<>();
    private SearchRepository mSearchRepository;


    private String mSearchTerm;
    private ResultList<Episode> mEpisodeResultList;
    private ResultList<Podcast> mPodcastResultList;

    private MutableLiveData<Event<Episode>> mSelectedEpisode = new MutableLiveData<>();
    private MutableLiveData<Event<Podcast>> mSelectedPodcast = new MutableLiveData<>();
    private MutableLiveData<ResultList<Episode>> mEpisodes = new MutableLiveData<>();
    private MutableLiveData<ResultList<Podcast>> mPodcasts = new MutableLiveData<>();


    public SearchViewModel(@NonNull final SearchRepository searchRepository) {
        mSearchRepository = searchRepository;
    }


    public void performTypeAheadSearch(@NonNull final String term) {
        mSearchRepository.typeAheadSearch(term, new SearchRepository.TypeAheadSearchCallback() {
            @Override
            public void onSearchResult(TypeAheadSearchResultList searchPagedList) {
                mTypeAheadSearchResult.postValue(searchPagedList.getDataList());
            }

            @Override
            public void onError() {
                //todo handle errors
            }
        });
    }


    public void selectTerm(@NonNull final String term) {
        mSearchTerm = term;
        searchByTerm(mSearchTerm);
    }


    public LiveData<List<String>> getTypeAheadSearchResult() {
        return mTypeAheadSearchResult;
    }

    public MutableLiveData<ResultList<Podcast>> getPodcasts() {
        return mPodcasts;
    }

    public MutableLiveData<ResultList<Episode>> getEpisodes() {
        return mEpisodes;
    }

    public MutableLiveData<Event<Episode>> getSelectedEpisode() {
        return mSelectedEpisode;
    }

    public MutableLiveData<Event<Podcast>> getSelectedPodcast() {
        return mSelectedPodcast;
    }

    private void searchByTerm(@NonNull final String term) {
        searchPodcasts(term);
        searchEpisodes(term);
    }

    private void searchPodcasts(@NonNull final String term) {
        mSearchRepository.searchPodcasts(term,
                new SearchRepository.SearchCallback<Podcast>() {
                    @Override
                    public void onSearchResult(ResultList<Podcast> resultList) {
                        mPodcastResultList = resultList;
                        mPodcasts.postValue(mPodcastResultList);

                    }

                    @Override
                    public void onError() {
                        //todo handle error
                    }
                });
    }


    private void searchEpisodes(@NonNull final String term) {
        mSearchRepository.searchEpisodes(term, new SearchRepository.SearchCallback<Episode>() {
            @Override
            public void onSearchResult(ResultList<Episode> resultList) {
                mEpisodeResultList = resultList;
                mEpisodes.postValue(mEpisodeResultList);
            }

            @Override
            public void onError() {
                //todo handle error
            }
        });
    }

    @Override
    public void select(@NonNull final Episode episode) {
        mSelectedEpisode.postValue(new Event<>(episode));
    }

    @Override
    public void select(@NonNull final Podcast podcast) {
        mSelectedPodcast.postValue(new Event<>(podcast));
    }


    public static class Factory implements ViewModelProvider.Factory {

        private SearchRepository mSearchRepository;

        public Factory(@NonNull final SearchRepository searchRepository) {
            mSearchRepository = searchRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SearchViewModel(mSearchRepository);
        }
    }
}
