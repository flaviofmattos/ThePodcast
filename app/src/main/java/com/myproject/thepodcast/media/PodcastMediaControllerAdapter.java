package com.myproject.thepodcast.media;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.myproject.thepodcast.R;

import java.util.List;

public class PodcastMediaControllerAdapter extends MediaControllerCompat.Callback {

    private boolean mIsPlaying;
    private ImageView mMediaControlPlayIcon;
    private ImageView mMediaControlLikedIcon;
    private TextView mMediaPublisher;
    private TextView mMediaTitle;
    private SeekBar mSeekBar;
    private ValueAnimator mValueAnimator;

    public PodcastMediaControllerAdapter(@NonNull final View view) {

        mMediaControlPlayIcon = view.findViewById(R.id.iv_play_control);
        mMediaControlLikedIcon = view.findViewById(R.id.iv_like_episode);
        mMediaPublisher = view.findViewById(R.id.tv_play_control_publisher);
        mMediaTitle = view.findViewById(R.id.tv_play_control_title);
        mSeekBar = view.findViewById(R.id.seekBar);
    }

    @Override
    public void onPlaybackStateChanged(@Nullable final PlaybackStateCompat playbackState) {
        setupSeekBar(playbackState);
        boolean playing = isPlaying(playbackState);
        if (playing) {
            mMediaControlPlayIcon.setImageResource(R.drawable.ic_baseline_pause_24px);
        } else if (isStopped(playbackState)) {
            mMediaControlPlayIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24px);
        }
        mIsPlaying = playing;
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onMetadataChanged(MediaMetadataCompat mediaMetadata) {
        if (mediaMetadata == null) {
            return;
        }
        final int max = (int) mediaMetadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION);
        mSeekBar.setProgress(0);
        mSeekBar.setMax(max);
        mMediaTitle.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE));
        mMediaPublisher.setText(mediaMetadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST));
        long liked = mediaMetadata.getLong("METADATA_KEY_LIKED");
        mMediaControlLikedIcon.setImageResource(liked > 0 ? R.drawable.ic_baseline_favorite_24px
                : R.drawable.ic_baseline_favorite_border_24px);
    }

    @Override
    public void onSessionDestroyed() {
        super.onSessionDestroyed();
    }

    @Override
    public void onQueueChanged(List<MediaSessionCompat.QueueItem> queue) {
        super.onQueueChanged(queue);
    }


    public void setOnPlayPauseListener(final @NonNull OnPlayPauseListener listener) {
        mMediaControlPlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsPlaying) {
                    mIsPlaying = true;
                    mMediaControlPlayIcon.setImageResource(R.drawable.ic_baseline_pause_24px);
                    listener.onPlay();
                } else {
                    mIsPlaying = false;
                    mMediaControlPlayIcon.setImageResource(R.drawable.ic_baseline_play_arrow_24px);
                    listener.onPause();
                }

            }
        });
    }

    public void setOnEpisodeFavouriteListener(final @NonNull OnFavouriteListener listener) {
        mMediaControlLikedIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.likeUnLike();
            }
        });

    }


    public void onEpisodeFavouriteCallback(boolean result) {
        mMediaControlLikedIcon.setImageResource(result ? R.drawable.ic_baseline_favorite_24px
                : R.drawable.ic_baseline_favorite_border_24px);
    }



    private void setupSeekBar(@Nullable final PlaybackStateCompat playbackState) {

        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }

        if (playbackState == null) {
            mSeekBar.setProgress(0);
            return;
        }

        final int progress = (int) playbackState.getPosition();
        mSeekBar.setProgress(progress);

        if (playbackState.getState() == PlaybackStateCompat.STATE_PLAYING) {
            float speed = playbackState.getPlaybackSpeed();
            final long timeToEnd = (long) Math.ceil((mSeekBar.getMax() - progress) / speed);
            mValueAnimator = ValueAnimator.ofInt(progress, mSeekBar.getMax()).setDuration(
                    timeToEnd);
            mValueAnimator.setInterpolator(new LinearInterpolator());
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    final int animatedIntValue = (int) animation.getAnimatedValue();
                    mSeekBar.setProgress(animatedIntValue);
                }
            });
            mValueAnimator.start();
        }
    }


    private boolean isPlaying(@Nullable final PlaybackStateCompat playbackState) {
        return playbackState != null
                && playbackState.getState() == PlaybackStateCompat.STATE_PLAYING;
    }

    private boolean isStopped(@Nullable final PlaybackStateCompat playbackState) {
        return playbackState != null
                && (playbackState.getState() == PlaybackStateCompat.STATE_PAUSED
                || playbackState.getState() == PlaybackStateCompat.STATE_STOPPED);
    }


    public interface OnPlayPauseListener {
        void onPlay();

        void onPause();
    }


    public interface OnFavouriteListener {
        void likeUnLike();
    }

}
