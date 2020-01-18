package com.myproject.thepodcast.media.download;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

public class PodcastMediaDownloadBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = PodcastMediaDownloadBroadcastReceiver.class.getSimpleName();

    private PodcastMediaDownloadManager mPodcastMediaDownloadManager;

    public PodcastMediaDownloadBroadcastReceiver(@NonNull final PodcastMediaDownloadManager podcastMediaDownloadManager) {
        mPodcastMediaDownloadManager = podcastMediaDownloadManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        mPodcastMediaDownloadManager.handleDownloadComplete(context, id);
    }

}
