package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;

import com.myproject.thepodcast.model.ResultList;
import com.myproject.thepodcast.model.TypeAheadSearchResultList;

public interface SearchRepository {

    void typeAheadSearch(@NonNull final String search, TypeAheadSearchCallback callback);

    void searchPodcasts(@NonNull final String search, SearchCallback callback);

    void searchEpisodes(@NonNull final String search, SearchCallback callback);


    interface TypeAheadSearchCallback {

        void onSearchResult(TypeAheadSearchResultList searchPagedList);

        void onError();
    }


    interface SearchCallback<T> {

        void onSearchResult(ResultList<T> resultList);

        void onError();
    }

}
