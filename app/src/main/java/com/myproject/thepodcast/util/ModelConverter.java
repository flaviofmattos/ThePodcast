package com.myproject.thepodcast.util;

import android.annotation.SuppressLint;
import android.support.v4.media.MediaMetadataCompat;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.util.StringUtil;

import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.FavouriteEpisode;

import java.util.concurrent.TimeUnit;

public final class ModelConverter {

    @SuppressLint("WrongConstant")
    public static MediaMetadataCompat convert(@NonNull final Episode episode) {
        return new MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, episode.getId())
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, episode.getPodcastTitle())
                .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, getPublisher(episode))
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,
                        TimeUnit.MILLISECONDS.convert(episode.getAudioLengthSec(),
                                TimeUnit.SECONDS))
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, episode.getImage())
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI,
                        episode.getThumbnail())
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, episode.getAudio())
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.getTitle())
                .putLong("METADATA_KEY_LIKED", (episode instanceof FavouriteEpisode) ? 1L : 0L) //TODO crete constant classes
                .putLong("METADATA_KEY_DOWNLOAD_ID", (episode instanceof DownloadedEpisode) ? ((DownloadedEpisode) episode).getDownloadId() : -1L) //TODO crete constant classes
                .build();
    }



    private static String getPublisher(@NonNull final Episode episode) {
        String publisher = "";
        if (TextUtils.isEmpty(episode.getPodcastPublisher())) {
            publisher = episode.getPodcast() != null ? episode.getPodcast().getTitle() : "";
        } else {
            publisher = episode.getPodcastPublisher();
        }
        return publisher;
    }
}
