package com.myproject.thepodcast.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.myproject.thepodcast.model.DownloadedEpisode;
import com.myproject.thepodcast.model.FavouriteEpisode;
import com.myproject.thepodcast.model.HistoryEpisode;
import com.myproject.thepodcast.model.Podcast;

@Database(entities = {Podcast.class, FavouriteEpisode.class, HistoryEpisode.class,
        DownloadedEpisode.class}, version = 1, exportSchema = false)
@TypeConverters({Converter.class})
public abstract class PodcastDatabase extends RoomDatabase {

    private static final String DB_NAME = "podcast.db";

    private static PodcastDatabase sPodcastDatabase;

    public synchronized static PodcastDatabase getInstance(@NonNull final Context context) {

        if (sPodcastDatabase == null) {
            sPodcastDatabase = Room.databaseBuilder(context.getApplicationContext(),
                    PodcastDatabase.class, DB_NAME).build();
        }

        return sPodcastDatabase;
    }


    public abstract PodcastDao podcastDao();
}
