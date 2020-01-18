package com.myproject.thepodcast.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PodcastResultList extends ResultList<Podcast> {

    @SerializedName("podcasts")
    private List<Podcast> mDataList;

    public PodcastResultList() {}

    public PodcastResultList(List<Map<String, Object>> listMap) {
        mDataList = new ArrayList<>(listMap.size());
        for (Map<String, Object> map : listMap) {
            mDataList.add(new Podcast(map));
        }

    }

    @Override
    public List<Podcast> getDataList() {
        return mDataList;
    }

    public void setDataList(@NonNull final List<Podcast> list) {
        mDataList = list;
    }

}
