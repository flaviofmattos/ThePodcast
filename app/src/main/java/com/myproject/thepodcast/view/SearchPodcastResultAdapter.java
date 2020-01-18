package com.myproject.thepodcast.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.thepodcast.R;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.viewmodel.PodcastSelectorHandler;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchPodcastResultAdapter extends RecyclerView.Adapter<SearchPodcastResultAdapter.ViewHolder> {

    private PodcastSelectorHandler mPodcastSelectorHandler;
    private List<Podcast> mPodcasts;

    public SearchPodcastResultAdapter(
            @NonNull final PodcastSelectorHandler podcastSelectorHandler,
            @NonNull final List<Podcast> podcasts) {
        mPodcastSelectorHandler = podcastSelectorHandler;
        mPodcasts = new ArrayList<>();
        mPodcasts.addAll(podcasts);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.podcast_result_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Podcast podcast = mPodcasts.get(position);
        Picasso.get().load(podcast.getThumbnail()).into(holder.mImage);
        holder.mName.setText(podcast.getTitle());
        holder.mDescription.setText(podcast.getPublisher());
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

    public void replaceData(@NonNull final List<Podcast> podcasts) {
        mPodcasts.clear();
        notifyDataSetChanged();
        mPodcasts.addAll(podcasts);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mName;
        private TextView mDescription;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.iv_podcast_item);
            mName = itemView.findViewById(R.id.tv_podcast_item_name);
            mDescription = itemView.findViewById(R.id.tv_podcast_item_publisher);
        }
    }
}
