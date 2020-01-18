package com.myproject.thepodcast.viewmodel;

import androidx.annotation.Nullable;

public class Event<T> {

    private T mT;
    private boolean mHasBeenConsumed;

    public Event(T t) {
        mT = t;
    }


    public boolean hasBeenConsumed() {
        return mHasBeenConsumed;
    }


    public T getPayload() {
        mHasBeenConsumed = true;
        return mT;
    }


    @Nullable
    public T getPayloadIfNotConsumed() {
        if (!mHasBeenConsumed) {
            mHasBeenConsumed = true;
            return mT;
        }
        return null;
    }

}
