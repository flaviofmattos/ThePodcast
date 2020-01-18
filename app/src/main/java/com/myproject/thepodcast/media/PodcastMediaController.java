package com.myproject.thepodcast.media;

import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;

public class PodcastMediaController {

    private MediaControllerCallback mMediaControllerCallback;
    private MediaControllerCompat mMediaControllerCompat;
    private PodcastMediaControllerAdapter mPodcastMediaControllerAdapter;
    private PodcastMediaControllerAdapter.OnPlayPauseListener mOnPlayPauseListener =
            new PodcastMediaControllerAdapter.OnPlayPauseListener() {
                @Override
                public void onPlay() {
                    getMediaControllerCompat().getTransportControls().play();
                }

                @Override
                public void onPause() {
                    getMediaControllerCompat().getTransportControls().pause();
                }
            };


    public PodcastMediaController(
            @NonNull final PodcastMediaControllerAdapter podcastMediaControllerAdapter) {
        mPodcastMediaControllerAdapter = podcastMediaControllerAdapter;
        mMediaControllerCallback = new MediaControllerCallback(mPodcastMediaControllerAdapter);
    }


    public void setupMediaController(@NonNull final Context context,
            @NonNull final MediaSessionCompat.Token sessionToken) {
        try {
            mMediaControllerCompat = new MediaControllerCompat(context, sessionToken);
            mMediaControllerCompat.registerCallback(mMediaControllerCallback);
            mMediaControllerCallback.onMetadataChanged(mMediaControllerCompat.getMetadata());
            mMediaControllerCallback.onPlaybackStateChanged(
                    mMediaControllerCompat.getPlaybackState());
            mPodcastMediaControllerAdapter.setOnPlayPauseListener(mOnPlayPauseListener);
        } catch (RemoteException re) {
            throw new RuntimeException(re);
        }
    }

    public void tearDownMediaController() {
        mMediaControllerCompat.unregisterCallback(mMediaControllerCallback);
        mMediaControllerCompat = null;
    }


    public MediaControllerCompat getMediaControllerCompat() {
        if (mMediaControllerCompat == null) {
            throw new IllegalStateException("Object hasn't been initialized yet");
        }
        return mMediaControllerCompat;
    }

    private static class MediaControllerCallback extends MediaControllerCompat.Callback {

        private PodcastMediaControllerAdapter mPodcastMediaControllerAdapter;

        private MediaControllerCallback(
                PodcastMediaControllerAdapter podcastMediaControllerAdapter) {
            mPodcastMediaControllerAdapter = podcastMediaControllerAdapter;
        }

        @Override
        public void onMetadataChanged(final MediaMetadataCompat metadata) {
            mPodcastMediaControllerAdapter.onMetadataChanged(metadata);
        }

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {
            mPodcastMediaControllerAdapter.onPlaybackStateChanged(state);
        }

        @Override
        public void onSessionDestroyed() {
            onPlaybackStateChanged(null);
            mPodcastMediaControllerAdapter.onPlaybackStateChanged(null);
        }


    }


}
