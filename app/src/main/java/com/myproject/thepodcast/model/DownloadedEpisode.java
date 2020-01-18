package com.myproject.thepodcast.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

@Entity(tableName = "downloaded_episodes", inheritSuperIndices = true)
public class DownloadedEpisode extends Episode {

    public static final Parcelable.Creator<DownloadedEpisode> CREATOR
            = new Parcelable.Creator<DownloadedEpisode>() {
        public DownloadedEpisode createFromParcel(Parcel in) {
            return new DownloadedEpisode(in);
        }

        public DownloadedEpisode[] newArray(int size) {
            return new DownloadedEpisode[size];
        }
    };

    @ColumnInfo(name = "download_id")
    private long downloadId;

    @Ignore
    public DownloadedEpisode() {
        super();
    }

    public DownloadedEpisode(@NonNull final Episode episode) {
        super(episode);
    }

    public DownloadedEpisode(long uniqueId, String id, String audio, String image, String title,
            String thumbnail,
            String description, long pubDateMs, long audioLengthSec, boolean maybeAudioInvalid,
            String listenNotesEditUrl, String podcastTitle, String podcastPublisher,
            long createOn) {
        super(uniqueId, id, audio, image, title, thumbnail, description, pubDateMs, audioLengthSec,
                maybeAudioInvalid, listenNotesEditUrl, podcastTitle, podcastPublisher, createOn);
    }

    @Ignore
    private DownloadedEpisode(Parcel in) {
        super(in);
        downloadId = in.readLong();
    }


    public long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(long downloadId) {
        this.downloadId = downloadId;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeLong(downloadId);
    }
}
