package com.myproject.thepodcast.util;

import android.content.Intent;
import android.support.v4.media.MediaMetadataCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.RepositoryFactory;

public final class BroadcastUtils {

    private BroadcastUtils() {}

    public static void sendBroadcast(@NonNull final Fragment fragment, @NonNull final Episode episode) {
        MediaMetadataCompat metadataCompat = ModelConverter.convert(episode);
        Intent mediaPlayerIntent = new Intent();
        mediaPlayerIntent.setAction("com.myproject.thepodcast.action.PODCAST_EPISODE_SELECTED");
        mediaPlayerIntent.putExtra("data", metadataCompat);


        Intent widgetIntent = new Intent();
        widgetIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        widgetIntent.putExtra("data", metadataCompat);

        fragment.requireActivity().sendBroadcast(mediaPlayerIntent);
        fragment.requireActivity().sendBroadcast(widgetIntent);
    }

}
