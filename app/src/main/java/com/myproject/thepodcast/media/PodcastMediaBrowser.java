package com.myproject.thepodcast.media;

import android.content.ComponentName;
import android.content.Context;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;

import androidx.annotation.NonNull;
import androidx.media.MediaBrowserServiceCompat;

import java.util.List;

public class PodcastMediaBrowser {

    private Context mContext;
    private MediaBrowserCompat mMediaBrowser;
    private PodcastMediaControllerAdapter mPodcastMediaControllerAdapter;
    private PodcastMediaController mPodcastMediaController;
    private PodcastMediaBrowserConnectionCallback mMediaBrowserConnectionCallback;
    private PodcastMediaBrowserSubscriptionCallback mPodcastMediaBrowserSubscriptionCallback;

    private final Class<? extends MediaBrowserServiceCompat> mMediaServiceClass;

    public PodcastMediaBrowser(@NonNull final Context context,
            @NonNull final PodcastMediaControllerAdapter podcastMediaControllerAdapter,
            @NonNull final Class<? extends MediaBrowserServiceCompat> clazz) {
        mContext = context;
        mPodcastMediaControllerAdapter = podcastMediaControllerAdapter;
        mMediaServiceClass = clazz;
        mMediaBrowserConnectionCallback = new PodcastMediaBrowserConnectionCallback();
        mPodcastMediaBrowserSubscriptionCallback = new PodcastMediaBrowserSubscriptionCallback();
    }


    public void connect() {
        if (mMediaBrowser == null) {
            mMediaBrowser =
                    new MediaBrowserCompat(
                            mContext,
                            new ComponentName(mContext, mMediaServiceClass),
                            mMediaBrowserConnectionCallback,
                            null);
            mMediaBrowser.connect();
        }
    }


    public void disconnect() {
        if (mPodcastMediaController != null) {
            mPodcastMediaController.tearDownMediaController();
            mPodcastMediaController = null;
        }
        if (mMediaBrowser != null && mMediaBrowser.isConnected()) {
            mMediaBrowser.disconnect();
            mMediaBrowser = null;
        }
        mPodcastMediaControllerAdapter.onPlaybackStateChanged(null);
    }






    private class PodcastMediaBrowserConnectionCallback extends
            MediaBrowserCompat.ConnectionCallback {

        @Override
        public void onConnected() {
            mPodcastMediaController = new PodcastMediaController(mPodcastMediaControllerAdapter);
            mPodcastMediaController.setupMediaController(mContext, mMediaBrowser.getSessionToken());
            mPodcastMediaControllerAdapter.onMetadataChanged(mPodcastMediaController.getMediaControllerCompat().getMetadata());
            mPodcastMediaControllerAdapter.onPlaybackStateChanged(
                    mPodcastMediaController.getMediaControllerCompat().getPlaybackState());
            mMediaBrowser.subscribe(mMediaBrowser.getRoot(), mPodcastMediaBrowserSubscriptionCallback);
        }
    }

    private class PodcastMediaBrowserSubscriptionCallback extends
            MediaBrowserCompat.SubscriptionCallback {

        @Override
        public void onChildrenLoaded(@NonNull String parentId,
                @NonNull List<MediaBrowserCompat.MediaItem> children) {
            final MediaControllerCompat mediaController =
                    mPodcastMediaController.getMediaControllerCompat();
            for (final MediaBrowserCompat.MediaItem mediaItem : children) {
                mediaController.addQueueItem(mediaItem.getDescription());
            }
            mediaController.getTransportControls().prepare();
        }
    }

}
