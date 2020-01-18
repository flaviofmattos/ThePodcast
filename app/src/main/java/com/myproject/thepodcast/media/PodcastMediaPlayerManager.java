package com.myproject.thepodcast.media;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;

import androidx.annotation.NonNull;

import com.myproject.thepodcast.MediaPlayerListener;


public class PodcastMediaPlayerManager {

    private final MediaSessionCompat mMediaSession;
    private final PodcastMediaPlayer mPodcastMediaPlayer;
    private final Context mContext;
    private final MediaPlayerListener mMediaPlayerListener;

    public PodcastMediaPlayerManager(@NonNull final Context context,
            @NonNull final MediaSessionCompat mediaSession,
            @NonNull final MediaPlayerListener mediaPlayerListener) {
        mContext = context.getApplicationContext();
        mMediaSession = mediaSession;
        mMediaPlayerListener = mediaPlayerListener;
        mPodcastMediaPlayer = new PodcastMediaPlayer(mContext, mMediaSession, mMediaPlayerListener);

    }

    public void play(@NonNull final MediaMetadataCompat mediaMetadataCompat) {

        Uri mediaUri = Uri.parse(
                mediaMetadataCompat.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI));
        mPodcastMediaPlayer.playFromUri(mediaUri);
    }

    public void play(final long audioId) {
        mPodcastMediaPlayer.playFromFile(audioId);
    }

    public void pause() {
        mPodcastMediaPlayer.pause();
    }

    public void stop() {
        mPodcastMediaPlayer.pause();
        mMediaSession.setActive(false);
    }


}
