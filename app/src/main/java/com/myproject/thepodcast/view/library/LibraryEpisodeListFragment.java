package com.myproject.thepodcast.view.library;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.myproject.thepodcast.R;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.HistoryEpisode;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.repository.EpisodeRepository;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.PodcastRepository;
import com.myproject.thepodcast.repository.RepositoryFactory;
import com.myproject.thepodcast.util.BroadcastUtils;
import com.myproject.thepodcast.view.EpisodeListFragment;
import com.myproject.thepodcast.view.EpisodeListResultAdapter;
import com.myproject.thepodcast.view.PodcastListResultAdapter;
import com.myproject.thepodcast.view.PodcastMainActivity;
import com.myproject.thepodcast.view.SearchPodcastResultAdapter;
import com.myproject.thepodcast.viewmodel.Event;
import com.myproject.thepodcast.viewmodel.LibraryViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LibraryEpisodeListFragment extends Fragment {

    public static final String TAG = LibraryEpisodeListFragment.class.getSimpleName();
    public static final String PODCAST_SEARCH_ARGUMENT = "LIBRARY_SEARCH_TYPE";


    private RecyclerView mRecyclerView;
    private EpisodeListResultAdapter mEpisodeListResultAdapter;
    private SearchPodcastResultAdapter mPodcastListResultAdapter;
    private LibraryViewModel mLibraryViewModel;
    private PodcastRepository mPodcastRepository;
    private EpisodeRepository mEpisodeRepository;
    private String mSearchType;
    private NavController mNavController;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchType = getArguments().getString(PODCAST_SEARCH_ARGUMENT);

        PodcastDatabase database = PodcastDatabase.getInstance(requireContext());
        RepositoryFactory factory = RepositoryFactory.getInstance(database);

        mPodcastRepository = factory.getPodcastRepositoryInstance();
        mEpisodeRepository = factory.getEpisodeRepositoryInstance();
        mLibraryViewModel = ViewModelProviders.of(this,
                new LibraryViewModel.Factory(mPodcastRepository, mEpisodeRepository)).get(
                LibraryViewModel.class);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(
                true);
        setToolbarTitle();

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PODCAST_SEARCH_ARGUMENT, mSearchType);
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mSearchType = savedInstanceState.getString(PODCAST_SEARCH_ARGUMENT);
        }
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library_list_episode, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.rv_library_episode_list);
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        handleEpisodes();
        handlePodcasts();
        handleRecyclerViewAdapter();
        performSearch();
    }


    private void setToolbarTitle() {
        if (requireActivity() instanceof PodcastMainActivity) {
            ((PodcastMainActivity) requireActivity()).setCollapsingToolbarTitle(mSearchType);
        }
    }


    private void handleEpisodes() {

        mLibraryViewModel.getLibraryEpisodeList().observe(this,
                new Observer<List<? extends Episode>>() {
                    @Override
                    public void onChanged(List<? extends Episode> historyEpisodes) {
                        if (historyEpisodes == null) {
                            historyEpisodes = new ArrayList<>();
                        }
                        mEpisodeListResultAdapter.replaceData(historyEpisodes);
                    }
                });


        mLibraryViewModel.getSelectedEpisode().observe(this, new Observer<Event<Episode>>() {
            @Override
            public void onChanged(Event<Episode> episodeEvent) {
                if (!episodeEvent.hasBeenConsumed()) {
                    BroadcastUtils.sendBroadcast(LibraryEpisodeListFragment.this,
                            episodeEvent.getPayload());
                    mEpisodeRepository.addHistory(episodeEvent.getPayload(), null);
                }
            }
        });
    }

    private void handlePodcasts() {

        mLibraryViewModel.getLibraryPodcastList().observe(this, new Observer<List<Podcast>>() {
            @Override
            public void onChanged(List<Podcast> podcasts) {
                if (podcasts == null) {
                    podcasts = new ArrayList<>();
                }
                mPodcastListResultAdapter.replaceData(podcasts);
            }
        });

        mLibraryViewModel.getSelectedPodcast().observe(this, new Observer<Event<Podcast>>() {
            @Override
            public void onChanged(Event<Podcast> podcastEvent) {
                if (!podcastEvent.hasBeenConsumed()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EpisodeListFragment.PODCAST_ID_ARGUMENT,
                            podcastEvent.getPayload().getId());
                    mNavController.navigate(R.id.action_libraryEpisodeListFragment_to_episodeListFragment,
                            bundle);
                }
            }
        });
    }


    private void handleRecyclerViewAdapter() {
        mLibraryViewModel.getDisplayPodcasts().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean shouldDisplayPodcasts) {
                if (shouldDisplayPodcasts) {
                    mPodcastListResultAdapter = new SearchPodcastResultAdapter(mLibraryViewModel, new ArrayList<Podcast>());
                    mRecyclerView.setAdapter(mPodcastListResultAdapter);
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, RecyclerView.VERTICAL, false));
                    mPodcastListResultAdapter.notifyDataSetChanged();

                } else {
                    mEpisodeListResultAdapter = new EpisodeListResultAdapter(mLibraryViewModel,
                            new ArrayList<Episode>(),
                            new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()));
                    mRecyclerView.setAdapter(mEpisodeListResultAdapter);
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    mEpisodeListResultAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void performSearch() {
        if (getString(R.string.history).equals(mSearchType)) {
            mLibraryViewModel.loadEpisodeHistory();
        } else if (getString(R.string.favourites).equals(mSearchType)) {
            mLibraryViewModel.loadFavouriteEpisodes();
        } else if (getString(R.string.downloaded).equals(mSearchType)) {
            mLibraryViewModel.loadDownloadedEpisodes();
        } else if (getString(R.string.following).equals(mSearchType)) {
            mLibraryViewModel.loadFollowingShows();
        }

    }

}
