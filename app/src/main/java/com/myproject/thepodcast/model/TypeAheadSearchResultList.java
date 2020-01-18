package com.myproject.thepodcast.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TypeAheadSearchResultList extends ResultList<String> {


    @SerializedName("terms")
    private List<String> mTerms;

    @Override
    public List<String> getDataList() {
        return mTerms;
    }


}
