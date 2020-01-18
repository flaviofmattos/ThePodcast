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
import com.myproject.thepodcast.util.PeriodParseUtils;
import com.myproject.thepodcast.viewmodel.EpisodeSelectorHandler;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EpisodeListResultAdapter extends RecyclerView.Adapter<EpisodeListResultAdapter.ViewHolder> {

    private List<Episode> mEpisodes;
    private DateFormat mDateFormat;
    private EpisodeSelectorHandler mEpisodeSelectorHandler;

    public EpisodeListResultAdapter(@NonNull final EpisodeSelectorHandler episodeSelectorHandler,
            @NonNull final List<Episode> episodes,
            @NonNull final DateFormat dateFormat) {
        mEpisodeSelectorHandler = episodeSelectorHandler;
        mEpisodes = new ArrayList<>();
        mEpisodes.addAll(episodes);
        mDateFormat = dateFormat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.episode_result_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Episode episode = mEpisodes.get(position);

        Picasso.get().load(episode.getThumbnail()).into(holder.mImage);
        holder.mTitle.setText(episode.getTitle());
        holder.mReleaseDate.setText(mDateFormat.format(new Date(episode.getPubDateMs())));
        holder.mDuration.setText(PeriodParseUtils.toHourMinSec(holder.mDuration.getContext(),
                episode.getAudioLengthSec()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEpisodeSelectorHandler.select(episode);
            }
        });

    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        holder.itemView.setOnClickListener(null);
    }

    @Override
    public int getItemCount() {
        return mEpisodes.size();
    }

    public void replaceData(@NonNull final List<? extends Episode> episodes) {
        mEpisodes.clear();
        notifyDataSetChanged();
        mEpisodes.addAll(episodes);
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mReleaseDate;
        private TextView mDuration;
        private TextView mTitle;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.iv_episode_item);
            mReleaseDate = itemView.findViewById(R.id.tv_episode_date);
            mDuration = itemView.findViewById(R.id.tv_episode_duration);
            mTitle = itemView.findViewById(R.id.tv_episode_name);
        }
    }

}
