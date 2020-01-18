package com.myproject.thepodcast.http;


import androidx.annotation.NonNull;

import java.net.URL;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class PodcastHttpClient {

    private static final String API_URL = "https://listen-api.listennotes.com/api/v2";
    private static final String API_HEADER = "X-ListenAPI-Key";
    private static final String API_KEY = "";

    private static final OkHttpClient client = new OkHttpClient();


    public static void get(@NonNull final String url,
            @NonNull final Map<String, String> queryParams,
            @NonNull final Callback callback) {

        URL httpUrl = buildUrl(url, queryParams);
        Request request = new Request.Builder()
                .url(httpUrl)
                .addHeader(API_HEADER, API_KEY)
                .build();
        client.newCall(request).enqueue(callback);
    }


    private static URL buildUrl(@NonNull final String url,
            @NonNull final Map<String, String> queryParams) {
        HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(API_URL + url).newBuilder();
        HttpUrl httpUrl = convertQueryParameters(httpUrlBuilder, queryParams);
        return httpUrl.url();
    }

    private static HttpUrl convertQueryParameters(@NonNull final HttpUrl.Builder httpUrlBuilder,
            @NonNull final Map<String, String> queryParams) {
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            httpUrlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return httpUrlBuilder.build();
    }

}
