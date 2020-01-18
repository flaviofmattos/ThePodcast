package com.myproject.thepodcast.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.myproject.thepodcast.repository.EpisodeRepository;

public class MediaControlViewModel extends ViewModel {

    private MutableLiveData<Boolean> likedUnLiked = new MutableLiveData<>();
    private EpisodeRepository mEpisodeRepository;


    public MediaControlViewModel(@NonNull final EpisodeRepository episodeRepository) {
        mEpisodeRepository = episodeRepository;
    }

    public void likeUnlike() {
        mEpisodeRepository.likeUnlike(new EpisodeRepository.LikeUnlikeEpisodeCallback() {
            @Override
            public void onLike() {
                likedUnLiked.postValue(true);
            }

            @Override
            public void onUnLike() {
                likedUnLiked.postValue(false);
            }

            @Override
            public void onError() {

            }
        });
    }


    public LiveData<Boolean> getLikedUnLiked() {
        return likedUnLiked;
    }


    public static class Factory implements ViewModelProvider.Factory {

        private EpisodeRepository mEpisodeRepository;

        public Factory(@NonNull final EpisodeRepository episodeRepository) {
            mEpisodeRepository = episodeRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new MediaControlViewModel(mEpisodeRepository);
        }
    }
}
