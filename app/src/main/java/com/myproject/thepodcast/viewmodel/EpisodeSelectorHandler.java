package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;

import com.myproject.thepodcast.model.Episode;

public interface EpisodeSelectorHandler {

    void select(@NonNull final Episode episode);

}
