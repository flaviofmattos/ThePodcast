package com.myproject.thepodcast.media;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.media.session.MediaButtonReceiver;

import com.myproject.thepodcast.R;

public class PodcastMediaNotificationManager {

    private static final String TAG = PodcastMediaNotificationManager.class.getSimpleName();

    public static final int NOTIFICATION_ID = 222;
    private static final String CHANNEL_ID = "com.myproject.thepodcast.media";

    private final NotificationManager mNotificationManager;
    private final PodcastMediaService mMediaService;

    public PodcastMediaNotificationManager(@NonNull final PodcastMediaService mediaService) {
        mMediaService = mediaService;
        mNotificationManager =
                (NotificationManager) mMediaService.getSystemService(Context.NOTIFICATION_SERVICE);


        //create notification channel if needed
        if (isChannelNeeded()) {
            createChannel();
        }
        mNotificationManager.cancelAll();
    }


    public Notification getNotificationWhenPlaying() {

        MediaControllerCompat controller = mMediaService.getMediaSession().getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mMediaService, CHANNEL_ID);
        builder
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mMediaService,
                        PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_baseline_music_note_24px)
                .setColor(ContextCompat.getColor(mMediaService, R.color.primaryDarkColor))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_baseline_pause_24px, mMediaService.getString(R.string.pause_description),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mMediaService,
                                PlaybackStateCompat.ACTION_PAUSE)))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaService.getSessionToken())
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mMediaService,
                                PlaybackStateCompat.ACTION_STOP)));
        return builder.build();
    }


    public Notification getNotificationWhenPaused() {

        MediaControllerCompat controller = mMediaService.getMediaSession().getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mMediaService, CHANNEL_ID);
        builder
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setLargeIcon(description.getIconBitmap())
                .setContentIntent(controller.getSessionActivity())
                .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mMediaService,
                        PlaybackStateCompat.ACTION_STOP))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_baseline_music_off_24px)
                .setColor(ContextCompat.getColor(mMediaService, R.color.primaryDarkColor))
                .addAction(new NotificationCompat.Action(
                        R.drawable.ic_baseline_play_arrow_24px, mMediaService.getString(R.string.play_description),
                        MediaButtonReceiver.buildMediaButtonPendingIntent(mMediaService,
                                PlaybackStateCompat.ACTION_PLAY)))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setMediaSession(mMediaService.getSessionToken())
                        .setShowActionsInCompactView(0)
                        .setShowCancelButton(true)
                        .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(mMediaService,
                                PlaybackStateCompat.ACTION_STOP)));
        return builder.build();
    }


    public void notify(Notification notification) {
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        if (mNotificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            CharSequence name = "The Podcast MediaSession";
            String description = "The Podcast MediaSession and MediaPlayer";
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);

        }
    }

    private boolean isChannelNeeded() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }




}
