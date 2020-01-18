package com.myproject.thepodcast.media;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

public class PodcastMediaBrowserManager {

    private Context mContext;
    private PodcastMediaBrowser mPodcastMediaBrowser;
    private PodcastMediaControllerAdapter mPodcastMediaControllerAdapter;
    private final Class<? extends MediaBrowserServiceCompat> mClass;


    public PodcastMediaBrowserManager(@NonNull final Context context,
            @NonNull final PodcastMediaControllerAdapter podcastMediaControllerAdapter,
            @NonNull final Class<? extends MediaBrowserServiceCompat> clazz) {
        mContext = context;
        mPodcastMediaControllerAdapter = podcastMediaControllerAdapter;
        mClass = clazz;
        mPodcastMediaBrowser = new PodcastMediaBrowser(mContext, mPodcastMediaControllerAdapter, mClass);
    }


    public void onStart() {
        mPodcastMediaBrowser.connect();
    }

    public void onStop() {
        mPodcastMediaBrowser.disconnect();
    }


}
