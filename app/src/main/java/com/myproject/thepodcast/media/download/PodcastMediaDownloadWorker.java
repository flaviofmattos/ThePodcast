package com.myproject.thepodcast.media.download;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PodcastMediaDownloadWorker extends Worker {

    private Context mContext;
    private WorkerParameters mParameters;

    public PodcastMediaDownloadWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
        mContext = context;
        mParameters = params;
    }

    @NonNull
    @Override
    public Result doWork() {

        return null;
    }


}
