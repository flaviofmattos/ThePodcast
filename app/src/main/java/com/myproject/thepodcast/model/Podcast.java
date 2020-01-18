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
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity(tableName = "podcasts")
public class Podcast implements Parcelable {


    public static final Parcelable.Creator<Podcast> CREATOR
            = new Parcelable.Creator<Podcast>() {
        public Podcast createFromParcel(Parcel in) {
            return new Podcast(in);
        }

        public Podcast[] newArray(int size) {
            return new Podcast[size];
        }
    };

    @PrimaryKey
    @SerializedName("id")
    @NonNull
    private String mId;

    @SerializedName("rss")
    private String mRss;

    @SerializedName("email")
    private String mEmail;

    @SerializedName("image")
    private String mImage;

    @SerializedName("title")
    private String mTitle;

    @SerializedName("country")
    private String mCountry;

    @SerializedName("website")
    private String mWebsite;

    @SerializedName("language")
    private String mLanguage;

    @SerializedName("publisher")
    private String mPublisher;

    @SerializedName("thumbnail")
    private String mThumbnail;

    @SerializedName("description")
    private String mDescription;

    @SerializedName("total_episodes")
    private int mTotalEpisodes;

    @SerializedName("listennotes_url")
    private String mListenNotesUrl;


    @SerializedName("latest_pub_date_ms")
    private long mLatestPubDateMs;

    @SerializedName("earliest_pub_date_ms")
    private long mEarliestPubDateMs;

    @ColumnInfo(name = "created_on")
    private long mCreateOn;

    @SerializedName("episodes")
    @Ignore
    private List<Episode> mEpisodeList;

    public Podcast() {
        mCreateOn = Calendar.getInstance().getTimeInMillis();
    }

    @Ignore
    public Podcast(@NonNull final Map<String, Object> map) {
        mId = ModelParseUtils.toOptString(map.get("id"));
        mRss = ModelParseUtils.toOptString(map.get("rss"));
        mImage = ModelParseUtils.toOptString(map.get("image"));
        mTitle = ModelParseUtils.toOptString(map.get("title_original"));
        mWebsite = ModelParseUtils.toOptString(map.get("website"));
        mLanguage = ModelParseUtils.toOptString(map.get("language"));
        mPublisher = ModelParseUtils.toOptString(map.get("publisher_original"));
        mThumbnail = ModelParseUtils.toOptString(map.get("thumbnail"));
        mDescription = ModelParseUtils.toOptString(map.get("description_original"));
        mListenNotesUrl = ModelParseUtils.toOptString(map.get("listennotes_url"));
    }


    @Ignore
    private Podcast(Parcel in) {
        mId = in.readString();
        mRss = in.readString();
        mImage = in.readString();
        mTitle = in.readString();
        mWebsite = in.readString();
        mLanguage = in.readString();
        mPublisher = in.readString();
        mThumbnail = in.readString();
        mDescription = in.readString();
        mListenNotesUrl = in.readString();
        mCreateOn = in.readLong();
        in.readList(mEpisodeList, getClass().getClassLoader());
    }


    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getRss() {
        return mRss;
    }

    public void setRss(String mRss) {
        this.mRss = mRss;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
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

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String mCountry) {
        this.mCountry = mCountry;
    }

    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(String mWebsite) {
        this.mWebsite = mWebsite;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public void setPublisher(String mPublisher) {
        this.mPublisher = mPublisher;
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

    public int getTotalEpisodes() {
        return mTotalEpisodes;
    }

    public void setTotalEpisodes(int mTotalEpisodes) {
        this.mTotalEpisodes = mTotalEpisodes;
    }

    public String getListenNotesUrl() {
        return mListenNotesUrl;
    }

    public void setListenNotesUrl(String mListenNotesUrl) {
        this.mListenNotesUrl = mListenNotesUrl;
    }

    public long getLatestPubDateMs() {
        return mLatestPubDateMs;
    }

    public void setLatestPubDateMs(long mLatestPubDateMs) {
        this.mLatestPubDateMs = mLatestPubDateMs;
    }

    public long getEarliestPubDateMs() {
        return mEarliestPubDateMs;
    }

    public void setEarliestPubDateMs(long mEarliestPubDateMs) {
        this.mEarliestPubDateMs = mEarliestPubDateMs;
    }

    public long getCreateOn() {
        return mCreateOn;
    }

    public void setCreateOn(long createOn) {
        this.mCreateOn = createOn;
    }

    public List<Episode> getEpisodeList() {
        return mEpisodeList;
    }

    public void setEpisodeList(List<Episode> episodeList) {
        mEpisodeList = episodeList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Podcast podcast = (Podcast) o;
        return Objects.equals(mId, podcast.mId);
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
        dest.writeString(mId);
        dest.writeString(mRss);
        dest.writeString(mImage);
        dest.writeString(mTitle);
        dest.writeString(mWebsite);
        dest.writeString(mLanguage);
        dest.writeString(mPublisher);
        dest.writeString(mThumbnail);
        dest.writeString(mDescription);
        dest.writeString(mListenNotesUrl);
        dest.writeLong(mCreateOn);
        dest.writeTypedList(mEpisodeList);
    }
}
