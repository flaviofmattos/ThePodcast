package com.myproject.thepodcast.media.download;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;

import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.repository.EpisodeRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PodcastMediaDownloadManager {

    private EpisodeRepository mEpisodeRepository;
    private DownloadCompleteCallback mDownloadCompleteCallback;
    private Handler mHandler;
    private ExecutorService mExecutorService = Executors.newSingleThreadExecutor();


    public PodcastMediaDownloadManager(@NonNull final EpisodeRepository episodeRepository,
            @NonNull final Handler handler,
            @NonNull final DownloadCompleteCallback callback) {

        mEpisodeRepository = episodeRepository;
        mHandler = handler;
        mDownloadCompleteCallback = callback;

    }

    void handleDownloadComplete(@NonNull final Context context, final long requestId) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {

                final DownloadedEpisode downloadedEpisode = mEpisodeRepository.getDownloaded(
                        requestId);
                if (downloadedEpisode != null ) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mDownloadCompleteCallback.onComplete();
                        }
                    });
                }
            }
        });
    }


}
