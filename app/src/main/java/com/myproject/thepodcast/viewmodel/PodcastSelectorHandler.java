package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;

import com.myproject.thepodcast.model.Podcast;

public interface PodcastSelectorHandler {

    void select(@NonNull final Podcast podcast);
}
