package com.myproject.thepodcast.view.detail;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.myproject.thepodcast.Constants;
import com.myproject.thepodcast.R;
import com.myproject.thepodcast.media.download.DownloadCompleteCallback;
import com.myproject.thepodcast.media.download.PodcastDownloadService;
import com.myproject.thepodcast.media.download.PodcastMediaDownloadBroadcastReceiver;
import com.myproject.thepodcast.media.download.PodcastMediaDownloadManager;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.RepositoryFactory;
import com.myproject.thepodcast.viewmodel.Event;
import com.myproject.thepodcast.viewmodel.PodcastDetailViewModel;
import com.squareup.picasso.Picasso;

public class PodcastDetailFragment extends DialogFragment implements DownloadCompleteCallback {

    private PodcastDetailViewModel mPodcastDetailViewModel;
    private ImageView mPodcastImage;
    private TextView mPodcastName;
    private TextView mPodcastDescription;
    private ImageView mDismissImage;
    private ImageView mShareImage;
    private ImageView mDownloadImage;
    private ImageView mFollowImage;
    private ProgressBar mProgressBar;

    private PodcastMediaDownloadManager mPodcastMediaDownloadManager;
    private PodcastMediaDownloadBroadcastReceiver mDownloadBroadcastReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepositoryFactory repositoryFactory = RepositoryFactory.getInstance(
                PodcastDatabase.getInstance(requireContext()));
        PodcastDetailViewModel.Factory factory = new PodcastDetailViewModel.Factory(
                repositoryFactory.getEpisodeRepositoryInstance(),
                repositoryFactory.getPodcastRepositoryInstance());
        mPodcastDetailViewModel = ViewModelProviders.of(this, factory).get(
                PodcastDetailViewModel.class);
        mPodcastMediaDownloadManager = new PodcastMediaDownloadManager(
                repositoryFactory.getEpisodeRepositoryInstance(), new Handler(), this);
        mDownloadBroadcastReceiver = new PodcastMediaDownloadBroadcastReceiver(
                mPodcastMediaDownloadManager);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_podcast_detail, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPodcastImage = view.findViewById(R.id.iv_podcast_detail_image);
        mDismissImage = view.findViewById(R.id.iv_podcast_detail_dismiss);
        mPodcastName = view.findViewById(R.id.tv_podcast_detail_name);
        mPodcastDescription = view.findViewById(R.id.tv_podcast_detail_description);
        mShareImage = view.findViewById(R.id.iv_podcast_detail_share);
        mDownloadImage = view.findViewById(R.id.iv_podcast_detail_download);
        mFollowImage = view.findViewById(R.id.iv_podcast_detail_follow);
        mProgressBar = view.findViewById(R.id.pb_download_status);

        registerObservers();
        registerListeners();
    }


    @Override
    public void onResume() {
        super.onResume();
        requireContext().registerReceiver(mDownloadBroadcastReceiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(mDownloadBroadcastReceiver);
    }


    private void registerObservers() {
        mPodcastDetailViewModel.getCurrentEpisode().observe(this, new Observer<Episode>() {
            @Override
            public void onChanged(Episode episode) {
                Picasso.get().load(episode.getThumbnail()).into(mPodcastImage);
                mPodcastName.setText(
                        episode.getPodcast() != null ? episode.getPodcast().getPublisher()
                                : episode.getPodcastPublisher());
                mPodcastDescription.setText(episode.getPodcast() != null ? Html.fromHtml(
                        episode.getPodcast().getDescription()) : "");
            }
        });
        mPodcastDetailViewModel.getShareEpisode().observe(this, new Observer<Event<Episode>>() {
            @Override
            public void onChanged(Event<Episode> episodeEvent) {
                if (!episodeEvent.hasBeenConsumed()) {
                    share(episodeEvent.getPayload());
                }
            }
        });
        mPodcastDetailViewModel.getDownloadEpisode().observe(this, new Observer<Event<Episode>>() {
            @Override
            public void onChanged(Event<Episode> episodeEvent) {
                if (!episodeEvent.hasBeenConsumed()) {
                    startDownload(episodeEvent.getPayload());
                }
            }
        });
        mPodcastDetailViewModel.getIsFollowing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mFollowImage.setImageResource(R.drawable.ic_baseline_grade_24px);
                } else {
                    mFollowImage.setImageResource(R.drawable.ic_baseline_star_border_24px);
                }

            }
        });
        mPodcastDetailViewModel.getFollowUnFollow().observe(this, new Observer<Event<Boolean>>() {
            @Override
            public void onChanged(Event<Boolean> booleanEvent) {
                if (!booleanEvent.hasBeenConsumed()) {
                    if (booleanEvent.getPayload()) {
                        Toast.makeText(PodcastDetailFragment.this.requireContext(),
                                R.string.followed_successfully, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(PodcastDetailFragment.this.requireContext(),
                                R.string.unFollowed_successfully, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        mPodcastDetailViewModel.getIsDownloaded().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    mDownloadImage.setImageResource(R.drawable.ic_baseline_cloud_done_24px);
                } else {
                    mDownloadImage.setImageResource(R.drawable.ic_baseline_cloud_download_24px);
                }
            }
        });


    }



    private void registerListeners() {
        mDismissImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });
        mShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPodcastDetailViewModel.shareEpisode();
            }
        });
        mDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPodcastDetailViewModel.downloadEpisode();
            }
        });

        mFollowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPodcastDetailViewModel.followUnFollowPodcast();
            }
        });

    }


    private void share(@Nullable Episode episode) {
        if (episode != null) {
            String publisher = episode.getPodcast() != null ? episode.getPodcast().getPublisher()
                    : episode.getPodcastPublisher();
            String text = getString(R.string.share_text, episode.getTitle(), publisher);
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.share)));
        }
    }

    private void startDownload(@Nullable Episode episode) {
        mProgressBar.setVisibility(View.VISIBLE);
        mDownloadImage.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(requireActivity(), PodcastDownloadService.class);
        intent.putExtra(Constants.EPISODE_EXTRA, episode);
        requireContext().startService(intent);
    }

    @Override
    public void onComplete() {
        if (isAdded()) {
            mDownloadImage.setImageResource(R.drawable.ic_baseline_cloud_done_24px);
            mProgressBar.setVisibility(View.INVISIBLE);
            mDownloadImage.setVisibility(View.VISIBLE);
            Toast.makeText(requireContext(), R.string.download_successful,
                    Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onFail() {
        if (isAdded()) {
            Toast.makeText(requireContext(), R.string.download_failed, Toast.LENGTH_LONG).show();
            mDownloadImage.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

}
