package com.myproject.thepodcast.repository;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.myproject.thepodcast.http.PodcastHttpClient;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.EpisodeResultList;
import com.myproject.thepodcast.model.GenericResultList;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.model.PodcastResultList;
import com.myproject.thepodcast.model.TypeAheadSearchResultList;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SearchRepositoryImpl implements SearchRepository {

    private static SearchRepositoryImpl sSearchRepository;

    private TypeAheadSearchResultList mTypeAheadSearchResultList;
    private String mTypeAheadSearchTerm;
    private PodcastResultList mPodcastResultList;
    private EpisodeResultList mEpisodeResultList;
    private String mSearchTerm;

    private SearchRepositoryImpl() {
    }

    public synchronized static SearchRepository getInstance() {
        if (sSearchRepository == null) {
            sSearchRepository = new SearchRepositoryImpl();
        }
        return sSearchRepository;

    }

    @Override
    public void typeAheadSearch(@NonNull final String search,
            @NonNull final TypeAheadSearchCallback callback) {

        if (search.equalsIgnoreCase(mTypeAheadSearchTerm)) {
            callback.onSearchResult(mTypeAheadSearchResultList);
        } else {
            mTypeAheadSearchTerm = search;
            QueryParam queryParam = new QueryParam.QueryParamBuilder()
                    .put("q", mTypeAheadSearchTerm).build();
            PodcastHttpClient.get("/typeahead", queryParam.getMapParams(), new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    //todo handle errors
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response)
                        throws IOException {
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    mTypeAheadSearchResultList = gson.fromJson(response.body().string(),
                            TypeAheadSearchResultList.class);
                    callback.onSearchResult(mTypeAheadSearchResultList);
                }
            });
        }


    }

    @SuppressWarnings("unchecked")
    @Override
    public void searchPodcasts(@NonNull final String search,
            @NonNull final SearchCallback callback) {
        QueryParam queryParam = new QueryParam.QueryParamBuilder()
                .put("q", mTypeAheadSearchTerm)
                .put("type", "podcast")
                .put("language", "English")
                .build();

        PodcastHttpClient.get("/search", queryParam.getMapParams(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                GenericResultList resultList = gson.fromJson(response.body().string(),
                        GenericResultList.class);
                mPodcastResultList = new PodcastResultList(resultList.getDataList());
                callback.onSearchResult(mPodcastResultList);

            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void searchEpisodes(@NonNull final String search,
            @NonNull final SearchCallback callback) {
        QueryParam queryParam = new QueryParam.QueryParamBuilder()
                .put("q", mTypeAheadSearchTerm)
                .put("type", "episode")
                .put("language", "English")
                .build();

        PodcastHttpClient.get("/search", queryParam.getMapParams(), new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response)
                    throws IOException {

                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                GenericResultList resultList = gson.fromJson(response.body().string(),
                        GenericResultList.class);
                mEpisodeResultList = new EpisodeResultList(resultList.getDataList());
                callback.onSearchResult(mEpisodeResultList);

            }
        });
    }


}
