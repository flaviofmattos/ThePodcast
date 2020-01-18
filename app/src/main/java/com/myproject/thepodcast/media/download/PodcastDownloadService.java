package com.myproject.thepodcast.media.download;

import static com.myproject.thepodcast.Constants.EPISODE_EXTRA;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.myproject.thepodcast.R;
import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.repository.EpisodeRepository;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.RepositoryFactory;

public class PodcastDownloadService extends IntentService {

    private static final String TAG = PodcastDownloadService.class.getSimpleName();

    private Episode mEpisode;
    private EpisodeRepository mEpisodeRepository;


    public PodcastDownloadService() {
        super("PodcastDownloadService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mEpisodeRepository = RepositoryFactory.getInstance(
                PodcastDatabase.getInstance(this)).getEpisodeRepositoryInstance();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Episode episode = getEpisodeFromIntent(intent);
        if (episode != null) {
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = buildRequest(episode);
            long requestId = manager.enqueue(request);
            addDownloadedEpisode(requestId);
        }

    }


    private DownloadManager.Request buildRequest(Episode episode) {

        return new DownloadManager.Request(Uri.parse(episode.getAudio()))
                .setTitle(episode.getTitle())
                .setDescription(getString(R.string.downloading, episode.getTitle()))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setAllowedOverMetered(true)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                        | DownloadManager.Request.NETWORK_WIFI)
                .setMimeType("audio/mpeg")
                .setVisibleInDownloadsUi(true)
                .setAllowedOverRoaming(true);
    }


    @Nullable
    private Episode getEpisodeFromIntent(@Nullable Intent intent) {
        if (intent != null) {
            mEpisode = intent.getParcelableExtra(EPISODE_EXTRA);
        }
        return mEpisode;
    }




    private void addDownloadedEpisode(long downloadRequestId) {
        DownloadedEpisode downloadedEpisode = new DownloadedEpisode(mEpisode);
        downloadedEpisode.setDownloadId(downloadRequestId);
        mEpisodeRepository.addDownloaded(downloadedEpisode,
                new EpisodeRepository.InsertEpisodeCallback() {
                    @Override
                    public void onSuccess() {
                        //do nothing - wait until the download is complete
                    }

                    @Override
                    public void onError() {

                    }
                });
    }


}
