package com.myproject.thepodcast.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EpisodeResultList extends ResultList<Episode> {

    @SerializedName("episodes")
    private List<Episode> mDataList;

    public EpisodeResultList() {}

    public EpisodeResultList(List<Map<String, Object>> listMap) {
        mDataList = new ArrayList<>(listMap.size());
        for (Map<String, Object> map : listMap) {
            mDataList.add(new Episode(map));
        }

    }

    @Override
    public List<Episode> getDataList() {
        return mDataList;
    }

    public void setDataList(@NonNull final List<Episode> list) {
        mDataList = list;
    }

}
