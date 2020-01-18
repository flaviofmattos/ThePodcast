package com.myproject.thepodcast.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.thepodcast.R;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.util.PeriodParseUtils;
import com.myproject.thepodcast.view.recommended.RecommendedPodcastFragment;
import com.myproject.thepodcast.viewmodel.EpisodeSelectorHandler;
import com.myproject.thepodcast.viewmodel.PodcastSelectorHandler;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PodcastListResultAdapter extends RecyclerView.Adapter<PodcastListResultAdapter.ViewHolder> {

    private List<Podcast> mPodcasts;
    private DateFormat mDateFormat;
    private PodcastSelectorHandler mPodcastSelectorHandler;

    public PodcastListResultAdapter(
            @NonNull final PodcastSelectorHandler podcastSelectorHandler,
            @NonNull final List<Podcast> data,
            @NonNull final DateFormat dateFormat) {
        mPodcastSelectorHandler = podcastSelectorHandler;
        mPodcasts = data;
        mDateFormat = dateFormat;
    }


    @NonNull
    @Override
    public PodcastListResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.recommended_list_item,
                parent, false);
        return new PodcastListResultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PodcastListResultAdapter.ViewHolder holder, int position) {
        final Podcast podcast = mPodcasts.get(position);
        Picasso.get().load(podcast.getThumbnail()).into(holder.mImage);
        holder.mTitle.setText(podcast.getTitle());
        holder.mPublisher.setText(podcast.getPublisher());
        int count = podcast.getTotalEpisodes();
        holder.mTotalEpisodes.setText(holder.mTotalEpisodes.getResources().getQuantityString(
                R.plurals.number_of_episodes, count, count));
        holder.mLatestPublication.setText(
                holder.mLatestPublication.getResources().getString(R.string.dash_separator,
                        mDateFormat.format(new Date(podcast.getLatestPubDateMs()))));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPodcastSelectorHandler.select(podcast);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPodcasts.size();
    }

    public void replaceData(@NonNull final List<Podcast> data) {
        notifyDataSetChanged();
        mPodcasts = new ArrayList<>(data);
    }

    @Override
    public void onViewRecycled(@NonNull PodcastListResultAdapter.ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setOnClickListener(null);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mTitle;
        private TextView mPublisher;
        private TextView mTotalEpisodes;
        private TextView mLatestPublication;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.iv_item);
            mTitle = itemView.findViewById(R.id.tv_title);
            mPublisher = itemView.findViewById(R.id.tv_publisher);
            mTotalEpisodes = itemView.findViewById(R.id.tv_num_episodes);
            mLatestPublication = itemView.findViewById(R.id.tv_latest_publication);
        }
    }
}

