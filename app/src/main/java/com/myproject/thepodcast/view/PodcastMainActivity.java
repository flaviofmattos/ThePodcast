package com.myproject.thepodcast.view;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.myproject.thepodcast.R;
import com.myproject.thepodcast.media.download.PodcastMediaDownloadBroadcastReceiver;
import com.myproject.thepodcast.media.PodcastMediaBrowserManager;
import com.myproject.thepodcast.media.PodcastMediaControllerAdapter;
import com.myproject.thepodcast.media.PodcastMediaService;
import com.myproject.thepodcast.media.download.PodcastMediaDownloadManager;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.RepositoryFactory;
import com.myproject.thepodcast.view.detail.PodcastDetailActivity;
import com.myproject.thepodcast.viewmodel.Event;
import com.myproject.thepodcast.viewmodel.MediaControlViewModel;

public class PodcastMainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private BottomNavigationView mBottomNavigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private PodcastMediaBrowserManager mPodcastMediaBrowserManager;
    private NavController mNavController;
    private View mMediaControlView;
    private PodcastMediaControllerAdapter mPodcastMediaControllerAdapter;
    private MediaControlViewModel mMediaControlViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(mToolbar);
        mCollapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        mCollapsingToolbarLayout.setTitle(getString(R.string.recommended));
        setupBottomNavigation();
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance(
                PodcastDatabase.getInstance(this));
        mMediaControlViewModel = ViewModelProviders.of(this, new MediaControlViewModel.Factory(
                repositoryFactory.getEpisodeRepositoryInstance())).get(MediaControlViewModel.class);
        setupMediaController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPodcastMediaBrowserManager.onStart();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mPodcastMediaBrowserManager.onStop();
    }



    @Override
    public boolean onSupportNavigateUp() {
        if (shouldDisableNavigateUp()) {
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            return false;
        } else {
            boolean navigateUp = mNavController.navigateUp();
            if (shouldDisableNavigateUp()) {
                getSupportActionBar().setHomeButtonEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
            return navigateUp;
        }
    }

    public void setCollapsingToolbarTitle(@NonNull final String title) {
        mCollapsingToolbarLayout.setTitle(title);
    }

    private void setupMediaController() {
        mMediaControlView = findViewById(R.id.media_control);
        mMediaControlView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PodcastMainActivity.this, PodcastDetailActivity.class);
                startActivity(intent);
            }
        });
        mPodcastMediaControllerAdapter = new PodcastMediaControllerAdapter(mMediaControlView);
        mPodcastMediaControllerAdapter.setOnEpisodeFavouriteListener(
                new PodcastMediaControllerAdapter.OnFavouriteListener() {
                    @Override
                    public void likeUnLike() {
                        mMediaControlViewModel.likeUnlike();
                    }
                });

        mMediaControlViewModel.getLikedUnLiked().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                mPodcastMediaControllerAdapter.onEpisodeFavouriteCallback(aBoolean);
            }
        });

        mPodcastMediaBrowserManager = new PodcastMediaBrowserManager(this,
                mPodcastMediaControllerAdapter, PodcastMediaService.class);
    }

    private void setupBottomNavigation() {
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mOnNavigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.home_page:
                                beforeNavigate(R.string.recommended);
                                mNavController.navigate(R.id.recommendedPodcastFragment);
                                return true;
                            case R.id.library_page:
                                beforeNavigate(R.string.library);
                                mNavController.navigate(R.id.librarySelectionFragment);
                                return true;
                            case R.id.search_page:
                                beforeNavigate(R.string.search);
                                mNavController.navigate(R.id.searchFragment);
                                return true;
                            default:
                                return false;
                        }
                    }
                };
        mBottomNavigation = findViewById(R.id.navigation_bar);
        mBottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private void beforeNavigate(int resId) {
        mCollapsingToolbarLayout.setTitle(getString(resId));
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private boolean shouldDisableNavigateUp() {
        return mNavController.getCurrentDestination().getId() == R.id.recommendedPodcastFragment ||
                mNavController.getCurrentDestination().getId() == R.id.librarySelectionFragment ||
                mNavController.getCurrentDestination().getId() == R.id.searchFragment;
    }

}
