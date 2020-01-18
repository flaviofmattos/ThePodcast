package com.myproject.thepodcast.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.thepodcast.R;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.repository.EpisodeRepository;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.RepositoryFactory;
import com.myproject.thepodcast.util.BroadcastUtils;
import com.myproject.thepodcast.viewmodel.EpisodeListViewModel;
import com.myproject.thepodcast.viewmodel.Event;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EpisodeListFragment extends Fragment {

    public static final String TAG = EpisodeListFragment.class.getSimpleName();
    public static final String PODCAST_ID_ARGUMENT = "PODCAST_ID_ARGUMENT";

    private RecyclerView mRecyclerView;
    private EpisodeListResultAdapter mEpisodeListResultAdapter;
    private EpisodeListViewModel mEpisodeListViewModel;
    private EpisodeRepository mEpisodeRepository;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String podcastId = getArguments().getString(PODCAST_ID_ARGUMENT);

        PodcastDatabase database = PodcastDatabase.getInstance(requireContext());
        mEpisodeRepository = RepositoryFactory.getInstance(database).getEpisodeRepositoryInstance();
        EpisodeListViewModel.Factory factory = new EpisodeListViewModel.Factory(podcastId,
                mEpisodeRepository);
        mEpisodeListViewModel = ViewModelProviders.of(this, factory).get(
                EpisodeListViewModel.class);

        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_list_episode, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.rv_episode_selection_list);
        mEpisodeListResultAdapter = new EpisodeListResultAdapter(mEpisodeListViewModel,
                new ArrayList<Episode>(),
                new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()));
        mRecyclerView.setAdapter(mEpisodeListResultAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        handleEpisodes();

    }


    private void handleEpisodes() {
        mEpisodeListViewModel.getEpisodeList().observe(this, new Observer<List<Episode>>() {
            @Override
            public void onChanged(List<Episode> episodeList) {
                mEpisodeListResultAdapter.replaceData(episodeList);
            }
        });

        mEpisodeListViewModel.getSelectedEpisode().observe(this,
                new Observer<Event<? extends Episode>>() {
                    @Override
                    public void onChanged(Event<? extends Episode> event) {
                        if (!event.hasBeenConsumed()) {
                            BroadcastUtils.sendBroadcast(EpisodeListFragment.this,
                                    event.getPayload());
                            mEpisodeRepository.addHistory(event.getPayload(), null);
                        }
                    }
                });

    }


}
