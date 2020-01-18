package com.myproject.thepodcast.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public abstract class ResultList<T> {

    @SerializedName("total")
    private int mTotal;

    @SerializedName("has_next")
    private boolean mHasNext;

    @SerializedName("has_previous")
    private boolean mHasPrevious;

    @SerializedName("page_number")
    private int mPageNumber;


    @SerializedName("previous_page_number")
    private int mPreviousPageNumber;

    @SerializedName("next_page_number")
    private int mNextPageNumber;

    public abstract List<T> getDataList();

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int mTotal) {
        this.mTotal = mTotal;
    }

    public boolean isHasNext() {
        return mHasNext;
    }

    public void setHasNext(boolean mHasNext) {
        this.mHasNext = mHasNext;
    }

    public boolean ismHasPrevious() {
        return mHasPrevious;
    }

    public void setHasPrevious(boolean mHasPrevious) {
        this.mHasPrevious = mHasPrevious;
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(int mPageNumber) {
        this.mPageNumber = mPageNumber;
    }

    public int getPreviousPageNumber() {
        return mPreviousPageNumber;
    }

    public void setPreviousPageNumber(int mPreviousPageNumber) {
        this.mPreviousPageNumber = mPreviousPageNumber;
    }

    public int getNextPageNumber() {
        return mNextPageNumber;
    }

    public void setNextPageNumber(int mNextPageNumber) {
        this.mNextPageNumber = mNextPageNumber;
    }

    public boolean isEmpty() {
        return getDataList() != null && getDataList().isEmpty();
    }

}
