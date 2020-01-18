package com.myproject.thepodcast;

import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;

public abstract class MediaPlayerListener {

    public abstract void onPlaybackStateChange(@NonNull PlaybackStateCompat state);
}
