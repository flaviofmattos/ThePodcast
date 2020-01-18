package com.myproject.thepodcast.media;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.media.MediaBrowserServiceCompat;

import com.myproject.thepodcast.MediaPlayerListener;

import java.io.FileNotFoundException;
import java.util.List;


public class PodcastMediaService extends MediaBrowserServiceCompat {

    private static final String MY_MEDIA_ROOT_ID = "media_root_id";
    private static final String MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id";

    private boolean mServiceInStartedState;
    private PodcastMediaPlayerManager mPodcastMediaPlayerManager;
    private MediaSessionCompat mMediaSession;
    private PodcastMediaSessionCallback mPodcastMediaSessionCallback;
    private PodcastEpisodeSelectedBroadcastReceiver mPodcastEpisodeSelectedBroadcastReceiver;
    private PodcastMediaNotificationManager mPodcastMediaNotificationManager;
    private IntentFilter mIntentFilter = new IntentFilter(
            "com.myproject.thepodcast.action.PODCAST_EPISODE_SELECTED");
    HandlerThread mHandler = new HandlerThread("ht");


    @Override
    public void onCreate() {
        super.onCreate();

        mMediaSession = new MediaSessionCompat(this, "PodcastMediaService");
        mPodcastMediaSessionCallback = new PodcastMediaSessionCallback();
        mMediaSession.setCallback(mPodcastMediaSessionCallback);
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
                | MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS);
        setSessionToken(mMediaSession.getSessionToken());

        mPodcastMediaPlayerManager = new PodcastMediaPlayerManager(this, mMediaSession,
                new PodcastMediaPlayerListener());
        mPodcastEpisodeSelectedBroadcastReceiver = new PodcastEpisodeSelectedBroadcastReceiver();
        mPodcastMediaNotificationManager = new PodcastMediaNotificationManager(this);

        mHandler.start();
        Looper looper = mHandler.getLooper();
        Handler handler = new Handler(looper);
        registerReceiver(mPodcastEpisodeSelectedBroadcastReceiver, mIntentFilter, null, handler);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mPodcastMediaPlayerManager.stop();
        mMediaSession.release();
        unregisterReceiver(mPodcastEpisodeSelectedBroadcastReceiver);
    }


    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid,
            @Nullable Bundle rootHints) {
        return new BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId,
            @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.detach();
    }

    public MediaSessionCompat getMediaSession() {
        return mMediaSession;
    }

    private class PodcastMediaSessionCallback extends MediaSessionCompat.Callback {

        private MediaMetadataCompat mPlayingMetadata;
        private long mDownloadId;

        @Override
        public void onAddQueueItem(MediaDescriptionCompat description) {
        }


        @Override
        public void onRemoveQueueItem(MediaDescriptionCompat description) {
        }


        private void onItemSelected(@NonNull final MediaMetadataCompat mediaMetadataCompat,
                final long downloadId) {
            mPlayingMetadata = mediaMetadataCompat;
            mDownloadId = downloadId;
            mPodcastMediaSessionCallback.onAddQueueItem(mediaMetadataCompat.getDescription());
            mMediaSession.getController().getTransportControls().pause();
            mMediaSession.getController().getTransportControls().prepare();
            mMediaSession.getController().getTransportControls().play();
        }

        @Override
        public void onPrepare() {

            if (mPlayingMetadata == null) {
                return;
            }

            mMediaSession.setMetadata(mPlayingMetadata);

            if (!mMediaSession.isActive()) {
                mMediaSession.setActive(true);
            }
        }


        @Override
        public void onPlay() {

            if (mPlayingMetadata == null) {
                onPrepare();
            }

            if (mDownloadId != -1L) {
                mPodcastMediaPlayerManager.play(mDownloadId);
            }
            if (mPlayingMetadata != null) {
                mPodcastMediaPlayerManager.play(mPlayingMetadata);
            }
        }


        @Override
        public void onPause() {
            mPodcastMediaPlayerManager.pause();
        }


        @Override
        public void onStop() {
            mPodcastMediaPlayerManager.stop();
        }


    }


    private class PodcastEpisodeSelectedBroadcastReceiver extends BroadcastReceiver {

        @SuppressLint("WrongConstant")
        @Override
        public void onReceive(Context context, Intent intent) {
            MediaMetadataCompat episode = intent.getParcelableExtra("data");
            if (shouldPlayFromInternet(episode)) {
                mPodcastMediaSessionCallback.onItemSelected(episode, -1L);
            } else {
                long downloadId = episode.getLong("METADATA_KEY_DOWNLOAD_ID");
                mPodcastMediaSessionCallback.onItemSelected(episode, downloadId);
            }

        }

        @SuppressLint("WrongConstant")
        private boolean shouldPlayFromInternet(
                @NonNull final MediaMetadataCompat mediaMetadataCompat) {
            boolean shouldPlayFromInternet = true;
            long downloadId = mediaMetadataCompat.getLong("METADATA_KEY_DOWNLOAD_ID");
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            try {
                manager.openDownloadedFile(downloadId);
                shouldPlayFromInternet = false;
            } catch (FileNotFoundException | SecurityException e) {

            }
            return shouldPlayFromInternet;
        }

    }

    public class PodcastMediaPlayerListener extends MediaPlayerListener {

        private final PodcastMediaServiceManager mPodcastMediaServiceManager;

        PodcastMediaPlayerListener() {
            mPodcastMediaServiceManager = new PodcastMediaServiceManager();
        }

        @Override
        public void onPlaybackStateChange(@NonNull final PlaybackStateCompat state) {

            mMediaSession.setPlaybackState(state);
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mPodcastMediaServiceManager.moveServiceToStartedState();
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                    mPodcastMediaServiceManager.updateNotificationForPause();
                    break;
                case PlaybackStateCompat.STATE_STOPPED:
                    mPodcastMediaServiceManager.moveServiceOutOfStartedState();
                    break;
            }
        }

        class PodcastMediaServiceManager {

            private void moveServiceToStartedState() {
                Notification notification =
                        mPodcastMediaNotificationManager.getNotificationWhenPlaying();

                if (!mServiceInStartedState) {
                    ContextCompat.startForegroundService(
                            PodcastMediaService.this,
                            new Intent(PodcastMediaService.this, PodcastMediaService.class));
                    mServiceInStartedState = true;
                }

                startForeground(PodcastMediaNotificationManager.NOTIFICATION_ID, notification);
            }

            private void updateNotificationForPause() {
                stopForeground(false);
                Notification notification =
                        mPodcastMediaNotificationManager.getNotificationWhenPaused();

                mPodcastMediaNotificationManager.notify(notification);
            }

            private void moveServiceOutOfStartedState() {
                stopForeground(true);
                stopSelf();
                mServiceInStartedState = false;
            }
        }

    }

}
