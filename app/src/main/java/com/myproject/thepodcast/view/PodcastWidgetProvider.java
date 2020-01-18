package com.myproject.thepodcast.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.media.MediaMetadataCompat;
import android.widget.RemoteViews;

import com.myproject.thepodcast.R;


public class PodcastWidgetProvider extends AppWidgetProvider {

    private String mEpisodeTitle;
    private String mPodcastPublisher;
    private String mNowPlaying;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int widgets = appWidgetIds.length;

        for (int i = 0; i < widgets; i++) {
            int appWidgetId = appWidgetIds[i];
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        MediaMetadataCompat metadataCompat = intent.getParcelableExtra("data");
        if (metadataCompat != null) {
            mEpisodeTitle = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            mPodcastPublisher = metadataCompat.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            mNowPlaying = context.getResources().getString(R.string.now_playing);
            ComponentName name = new ComponentName(context, getClass());
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            int[] ids = widgetManager.getAppWidgetIds(name);
            onUpdate(context, widgetManager, ids);
        }
    }


    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent = new Intent(context, PodcastMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.podcast_widget_layout);
        views.setTextViewText(R.id.tv_playing_episode_widget, mEpisodeTitle);
        views.setTextViewText(R.id.tv_playing_podcast_widget, mPodcastPublisher);
        views.setTextViewText(R.id.tv_now_playing_podcast_widget, mNowPlaying != null ? mNowPlaying
                : context.getResources().getString(R.string.not_playing));
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }


}
