package com.myproject.thepodcast.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "history_episodes", inheritSuperIndices = true)
public class HistoryEpisode extends Episode {


    @Ignore
    public HistoryEpisode() {
        super();
    }

    public HistoryEpisode(long uniqueId, String id, String audio, String image, String title,
            String thumbnail,
            String description, long pubDateMs, long audioLengthSec, boolean maybeAudioInvalid,
            String listenNotesEditUrl, String podcastTitle, String podcastPublisher,
            long createOn) {
        super(uniqueId, id, audio, image, title, thumbnail, description, pubDateMs, audioLengthSec,
                maybeAudioInvalid, listenNotesEditUrl, podcastTitle, podcastPublisher, createOn);
    }


    public HistoryEpisode(@NonNull final Episode episode) {
        super(episode);
    }

}
