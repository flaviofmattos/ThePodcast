package com.myproject.thepodcast.model;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.myproject.thepodcast.util.ModelParseUtils;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

public class Episode implements Parcelable {


    public static final Parcelable.Creator<Episode> CREATOR
            = new Parcelable.Creator<Episode>() {
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uniqueId", typeAffinity = ColumnInfo.INTEGER)
    private long mUniqueId;

    @SerializedName("id")
    @ColumnInfo(name = "id")
    private String mId;

    @ColumnInfo(name = "audio")
    @SerializedName("audio")
    private String mAudio;

    @ColumnInfo(name = "image")
    @SerializedName("image")
    private String mImage;

    @ColumnInfo(name = "title")
    @SerializedName("title")
    private String mTitle;

    @ColumnInfo(name = "thumbnail")
    @SerializedName("thumbnail")
    private String mThumbnail;

    @ColumnInfo(name = "description")
    @SerializedName("description")
    private String mDescription;

    @ColumnInfo(name = "pub_date_ms")
    @SerializedName("pub_date_ms")
    private long mPubDateMs;

    @ColumnInfo(name = "audio_length_sec")
    @SerializedName("audio_length_sec")
    private long mAudioLengthSec;

    @ColumnInfo(name = "maybe_audio_invalid")
    @SerializedName("maybe_audio_invalid")
    private boolean mMaybeAudioInvalid;

    @ColumnInfo(name = "listennotes_edit_url")
    @SerializedName("listennotes_edit_url")
    private String mListenNotesEditUrl;

    @ColumnInfo(name = "podcast_title_original")
    @SerializedName("podcast_title_original")
    private String mPodcastTitle;

    @ColumnInfo(name = "publisher_original")
    @SerializedName("publisher_original")
    private String mPodcastPublisher;

    @ColumnInfo(name = "created_on")
    private long mCreateOn;

    @SerializedName("podcast")
    @Ignore
    private Podcast mPodcast;


    public Episode(long uniqueId, String id, String audio, String image, String title, String thumbnail,
            String description, long pubDateMs, long audioLengthSec, boolean maybeAudioInvalid,
            String listenNotesEditUrl, String podcastTitle, String podcastPublisher, long createOn) {
        mUniqueId = uniqueId;
        mId = id;
        mAudio = audio;
        mImage = image;
        mTitle = title;
        mThumbnail = thumbnail;
        mDescription = description;
        mPubDateMs = pubDateMs;
        mAudioLengthSec = audioLengthSec;
        mMaybeAudioInvalid = maybeAudioInvalid;
        mListenNotesEditUrl = listenNotesEditUrl;
        mPodcastTitle = podcastTitle;
        mPodcastPublisher = podcastPublisher;
        mCreateOn = createOn;
    }

    @Ignore
    public Episode() {
        mCreateOn = Calendar.getInstance().getTimeInMillis();
    }


    @Ignore
    public Episode(@NonNull final Map<String, Object> map) {
        this();
        mId = ModelParseUtils.toOptString(map.get("id"));
        mAudio = ModelParseUtils.toOptString(map.get("audio"));
        mThumbnail = ModelParseUtils.toOptString(map.get("thumbnail"));
        mImage = ModelParseUtils.toOptString(map.get("image"));
        mTitle = ModelParseUtils.toOptString(map.get("title_original"));
        mDescription = ModelParseUtils.toOptString(map.get("description_original"));
        mPodcastTitle = ModelParseUtils.toOptString(map.get("podcast_title_original"));
        mPodcastPublisher = ModelParseUtils.toOptString(map.get("publisher_original"));
        mPubDateMs = ModelParseUtils.toOptLong(map.get("pub_date_ms"));
        mAudioLengthSec = ModelParseUtils.toOptLong(map.get("audio_length_sec"));

    }


    public Episode(@NonNull final Episode episode) {
        this();
        setUniqueId(episode.getUniqueId());
        setId(episode.getId());
        setAudio(episode.getAudio());
        setImage(episode.getImage());
        setTitle(episode.getTitle());
        setThumbnail(episode.getThumbnail());
        setDescription(episode.getDescription());
        setPubDateMs(episode.getPubDateMs());
        setAudioLengthSec(episode.getAudioLengthSec());
        setMaybeAudioInvalid(episode.getMaybeAudioInvalid());
        setListenNotesEditUrl(episode.getListenNotesEditUrl());
        setPodcastTitle(episode.getPodcastTitle());
        setPodcastPublisher(episode.getPodcastPublisher());
        setCreateOn(episode.getCreateOn());
        setPodcast(episode.getPodcast());

    }


    @Ignore
    Episode(Parcel in) {
        this();
        mUniqueId = in.readLong();
        mId = in.readString();
        mAudio = in.readString();
        mThumbnail = in.readString();
        mImage = in.readString();
        mTitle = in.readString();
        mDescription = in.readString();
        mPodcastTitle = in.readString();
        mPodcastPublisher = in.readString();
        mPubDateMs = in.readLong();
        mAudioLengthSec = in.readLong();
        mCreateOn = in.readLong();
        mMaybeAudioInvalid = in.readByte() != 0;
        mPodcast = in.readParcelable(getClass().getClassLoader());
    }

    public long getUniqueId() {
        return mUniqueId;
    }

    public void setUniqueId(long uniqueId) {
        mUniqueId = uniqueId;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getAudio() {
        return mAudio;
    }

    public void setAudio(String mAudio) {
        this.mAudio = mAudio;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(String mThumbnail) {
        this.mThumbnail = mThumbnail;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public long getPubDateMs() {
        return mPubDateMs;
    }

    public void setPubDateMs(long mPubDateMs) {
        this.mPubDateMs = mPubDateMs;
    }

    public long getAudioLengthSec() {
        return mAudioLengthSec;
    }

    public void setAudioLengthSec(long mAudioLengthSec) {
        this.mAudioLengthSec = mAudioLengthSec;
    }

    public boolean getMaybeAudioInvalid() {
        return mMaybeAudioInvalid;
    }

    public void setMaybeAudioInvalid(boolean mMaybeAudioInvalid) {
        this.mMaybeAudioInvalid = mMaybeAudioInvalid;
    }

    public String getListenNotesEditUrl() {
        return mListenNotesEditUrl;
    }

    public void setListenNotesEditUrl(String mListenNotesEditUrl) {
        this.mListenNotesEditUrl = mListenNotesEditUrl;
    }

    public String getPodcastTitle() {
        return mPodcastTitle;
    }

    public void setPodcastTitle(String podcastTitle) {
        mPodcastTitle = podcastTitle;
    }

    public String getPodcastPublisher() {
        return mPodcastPublisher;
    }

    public void setPodcastPublisher(String podcastPublisher) {
        mPodcastPublisher = podcastPublisher;
    }

    public long getCreateOn() {
        return mCreateOn;
    }

    public void setCreateOn(long createOn) {
        this.mCreateOn = createOn;
    }

    public Podcast getPodcast() {
        return mPodcast;
    }

    public void setPodcast(Podcast podcast) {
        mPodcast = podcast;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Episode episode = (Episode) o;
        return Objects.equals(mId, episode.mId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mUniqueId);
        dest.writeString(mId);
        dest.writeString(mAudio);
        dest.writeString(mThumbnail);
        dest.writeString(mImage);
        dest.writeString(mTitle);
        dest.writeString(mDescription);
        dest.writeString(mPodcastTitle);
        dest.writeString(mPodcastPublisher);
        dest.writeLong(mPubDateMs);
        dest.writeLong(mAudioLengthSec);
        dest.writeLong(mCreateOn);
        dest.writeByte((byte) (mMaybeAudioInvalid ? 1 : 0));
        dest.writeParcelable(mPodcast, 0);
    }
}
