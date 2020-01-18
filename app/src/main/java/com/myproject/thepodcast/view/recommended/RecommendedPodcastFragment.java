package com.myproject.thepodcast.view.recommended;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.thepodcast.R;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.RepositoryFactory;
import com.myproject.thepodcast.view.EpisodeListFragment;
import com.myproject.thepodcast.view.PodcastListResultAdapter;
import com.myproject.thepodcast.viewmodel.Event;
import com.myproject.thepodcast.viewmodel.PodcastSelectorHandler;
import com.myproject.thepodcast.viewmodel.RecommendedViewModel;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecommendedPodcastFragment extends Fragment {

    public static final String TAG = RecommendedPodcastFragment.class.getSimpleName();

    private RecyclerView mRecommendedList;
    private RecommendedViewModel mRecommendedViewModel;
    private PodcastListResultAdapter mAdapter;
    private NavController mNavController;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepositoryFactory factory = RepositoryFactory.getInstance(
                PodcastDatabase.getInstance(requireContext()));
        mRecommendedViewModel = ViewModelProviders.of(this,
                new RecommendedViewModel.Factory(factory.getPodcastRepositoryInstance())).get(
                RecommendedViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_recommended_podcast, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        mAdapter = new PodcastListResultAdapter(mRecommendedViewModel, new ArrayList<Podcast>(),
                new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()));
        mRecommendedList = view.findViewById(R.id.rv_recommended);
        mRecommendedList.setAdapter(mAdapter);
        mRecommendedList.setLayoutManager(new LinearLayoutManager(requireContext()));
        handlePodcastSelection();

    }


    private void handlePodcastSelection() {
        mRecommendedViewModel.getRecommendedPodcastList().observe(this,
                new Observer<List<Podcast>>() {
                    @Override
                    public void onChanged(List<Podcast> podcasts) {
                        if (podcasts != null) {
                            mAdapter.replaceData(podcasts);
                        }
                    }
                });


        mRecommendedViewModel.getSelectedPodcast().observe(this, new Observer<Event<Podcast>>() {
            @Override
            public void onChanged(Event<Podcast> podcastEvent) {
                if (!podcastEvent.hasBeenConsumed()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EpisodeListFragment.PODCAST_ID_ARGUMENT,
                            podcastEvent.getPayload().getId());
                    mNavController.navigate(
                            R.id.action_recommendedPodcastFragment_to_episodeListFragment, bundle);
                }
            }
        });
    }

}
