package com.myproject.thepodcast.media;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myproject.thepodcast.MediaPlayerListener;
import com.myproject.thepodcast.media.download.PodcastDownloadService;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PodcastMediaPlayer {

    private static final String TAG = PodcastMediaPlayer.class.getSimpleName();
    private final AudioBecomeNoisyReceiver mAudioBecomeNoisyReceiver;
    private final Context mContext;
    private final MediaPlayerListener mMediaPlayerListener;

    private MediaSessionCompat mMediaSessionCompat;
    private MediaPlayer mMediaPlayer;
    private MediaPlayerState mMediaPlayerState;
    private Uri mPlayingUri;
    private long mPlayingFile = -1L;


    public PodcastMediaPlayer(@NonNull final Context context,
            @NonNull final MediaSessionCompat mediaSessionCompat,
            @NonNull final MediaPlayerListener mediaPlayerListener) {
        mContext = context;
        mMediaSessionCompat = mediaSessionCompat;
        mMediaPlayerListener = mediaPlayerListener;
        mAudioBecomeNoisyReceiver = new AudioBecomeNoisyReceiver(mContext, mMediaSessionCompat.getSessionToken());
    }


    public void play() {
        mAudioBecomeNoisyReceiver.register();
        onPlay();

    }


    public void pause() {
        mAudioBecomeNoisyReceiver.unregister();
        onPause();
    }


    public void stop() {
        mAudioBecomeNoisyReceiver.unregister();
        onStop();
    }


    private void onPlay() {

        if (mMediaPlayer != null && !mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            mMediaPlayerState.toPlaying();
            mMediaSessionCompat.setPlaybackState(mMediaPlayerState.getPlaybackState());
            mMediaPlayerListener.onPlaybackStateChange(mMediaPlayerState.getPlaybackState());
        }
    }


    private void onPause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mMediaPlayerState.toPaused();
            mMediaSessionCompat.setPlaybackState(mMediaPlayerState.getPlaybackState());
            mMediaPlayerListener.onPlaybackStateChange(mMediaPlayerState.getPlaybackState());
        }
    }


    private void onStop() {
        mMediaPlayerState.toStopped();
        mMediaSessionCompat.setPlaybackState(mMediaPlayerState.getPlaybackState());
        mMediaPlayerListener.onPlaybackStateChange(mMediaPlayerState.getPlaybackState());
        release();
    }


    private void initializeMediaPlayer() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayerState = new MediaPlayerState(mMediaPlayer);
        }
    }


    private void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }


    private boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }


    public void playFromUri(@NonNull final Uri mediaUri) {
        mPlayingFile = -1L;
        boolean mediaChanged = !mediaUri.equals(mPlayingUri);

        if (!mediaChanged) {
            if (!isPlaying()) {
                play();
            }
            return;
        } else {
            release();
        }

        mPlayingUri = mediaUri;
        initializeMediaPlayer();
        try {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    play();
                }
            });
            Map<String, String> map = new HashMap<>();
            map.put("Accept", "*/*");
            mMediaPlayer.setDataSource(mContext, mPlayingUri, map);
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed to select uri: " + mPlayingUri, e);
        }

    }


    public void playFromFile(final long downloadId) {

        mPlayingUri = null;
        boolean mediaChanged = downloadId != mPlayingFile;

        if (!mediaChanged) {
            if (!isPlaying()) {
                play();
            }
            return;
        } else {
            release();
        }

        mPlayingFile = downloadId;
        initializeMediaPlayer();
        try {
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    play();
                }
            });
            DownloadManager manager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            mMediaPlayer.setDataSource(manager.openDownloadedFile(downloadId).getFileDescriptor());
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            throw new RuntimeException("Failed to select uri: " + mPlayingUri, e);
        }


    }


    private static class MediaPlayerState {

        private long mAvailableActions = 0L;
        private @PlaybackStateCompat.State int mState = PlaybackStateCompat.STATE_PAUSED;
        private MediaPlayer mMediaPlayer;

        public MediaPlayerState(@NonNull final MediaPlayer mediaPlayer) {
            mMediaPlayer = mediaPlayer;
        }

        private void toStopped() {
            mState = PlaybackStateCompat.STATE_STOPPED;
            mAvailableActions = 0L;
            mAvailableActions |= PlaybackStateCompat.ACTION_PLAY
                    | PlaybackStateCompat.ACTION_PAUSE;
        }


        private void toPlaying() {
            mState = PlaybackStateCompat.STATE_PLAYING;
            mAvailableActions = 0L;
            mAvailableActions |= PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_PAUSE;
        }

        private void toPaused() {
            mState = PlaybackStateCompat.STATE_PAUSED;
            mAvailableActions = 0L;
            mAvailableActions |= PlaybackStateCompat.ACTION_STOP
                    | PlaybackStateCompat.ACTION_PLAY;
        }


        private PlaybackStateCompat getPlaybackState() {
            final PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder();
            stateBuilder.setActions(mAvailableActions);
            stateBuilder.setState(mState,
                    mMediaPlayer.getCurrentPosition(),
                    1.0f,
                    SystemClock.elapsedRealtime());
            return stateBuilder.build();
        }


    }


    private static class AudioBecomeNoisyReceiver extends BroadcastReceiver {

        private boolean mIsRegistered;
        private final MediaControllerCompat mMediaControllerCompat;
        private final Context mContext;
        private final IntentFilter mIntentFilter = new IntentFilter(
                AudioManager.ACTION_AUDIO_BECOMING_NOISY);


        private AudioBecomeNoisyReceiver(@NonNull final Context context,
                @NonNull final MediaSessionCompat.Token sessionToken) {
            try {
                mMediaControllerCompat = new MediaControllerCompat(context, sessionToken);
            } catch (RemoteException e) {
                Log.e(TAG, "Error initializing media controller", e);
                throw new RuntimeException(e);
            }
            mContext = context;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                mMediaControllerCompat.getTransportControls().pause();
            }
        }

        private synchronized void register() {
            if (!mIsRegistered) {
                mContext.registerReceiver(this, mIntentFilter);
                mIsRegistered = true;
            }

        }

        private synchronized void unregister() {
            if (mIsRegistered) {
                mContext.unregisterReceiver(this);
                mIsRegistered = false;
            }
        }

    }

}
