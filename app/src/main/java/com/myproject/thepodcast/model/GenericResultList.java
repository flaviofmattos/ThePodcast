package com.myproject.thepodcast.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GenericResultList<T> extends ResultList {


    @SerializedName("results")
    private List<T> mList;

    @Override
    public List<T> getDataList() {
        return mList;
    }
}
