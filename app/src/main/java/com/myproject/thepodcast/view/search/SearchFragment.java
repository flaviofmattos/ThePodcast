package com.myproject.thepodcast.view.search;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myproject.thepodcast.R;
import com.myproject.thepodcast.model.Episode;
import com.myproject.thepodcast.model.Podcast;
import com.myproject.thepodcast.model.ResultList;
import com.myproject.thepodcast.repository.EpisodeRepository;
import com.myproject.thepodcast.repository.PodcastDatabase;
import com.myproject.thepodcast.repository.RepositoryFactory;
import com.myproject.thepodcast.repository.SearchRepositoryImpl;
import com.myproject.thepodcast.util.BroadcastUtils;
import com.myproject.thepodcast.view.EpisodeListFragment;
import com.myproject.thepodcast.view.EpisodeListResultAdapter;
import com.myproject.thepodcast.view.SearchPodcastResultAdapter;
import com.myproject.thepodcast.viewmodel.Event;
import com.myproject.thepodcast.viewmodel.PodcastSelectorHandler;
import com.myproject.thepodcast.viewmodel.SearchViewModel;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchFragment extends Fragment {

    public static final String TAG = SearchFragment.class.getSimpleName();

    private static final int TRIGGER_API_CODE = 100;
    private static final long AUTO_COMPLETE_DELAY = 275;

    private ArrayAdapter<String> mSearchOptionsAdapter;
    private SearchPodcastResultAdapter mPodcastAdapter;
    private EpisodeListResultAdapter mEpisodeAdapter;
    private RecyclerView mPodcastRecyclerView;
    private RecyclerView mEpisodeRecyclerView;
    private MultiAutoCompleteTextView mMultiAutoCompleteTextView;
    private SearchViewModel mSearchViewModel;
    private Handler mHandler;
    private NavController mNavController;
    private EpisodeRepository mEpisodeRepository;
    private View mDivider;
    private TextView mShows;
    private TextView mEpisodes;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PodcastDatabase database = PodcastDatabase.getInstance(requireContext());
        mEpisodeRepository = RepositoryFactory.getInstance(database).getEpisodeRepositoryInstance();
        mSearchViewModel = ViewModelProviders.of(requireActivity(),
                new SearchViewModel.Factory(
                        SearchRepositoryImpl.getInstance())).get(SearchViewModel.class);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        handleTextSearchElements(view);
        handlePodcastElements(view);
        handleEpisodeElements(view);
    }


    private void handleTextSearchElements(@NonNull final View view) {
        mSearchOptionsAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        mMultiAutoCompleteTextView = view.findViewById(R.id.tv_auto_complete);
        mMultiAutoCompleteTextView.setAdapter(mSearchOptionsAdapter);
        mMultiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        mMultiAutoCompleteTextView.setThreshold(2);
        mSearchViewModel.getTypeAheadSearchResult().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                mSearchOptionsAdapter.clear();
                mSearchOptionsAdapter.addAll(strings);
            }
        });
        mDivider = view.findViewById(R.id.search_result_divider);
        mShows = view.findViewById(R.id.tv_search_result_shows);
        mEpisodes = view.findViewById(R.id.tv_search_result_episodes);
        registerTextListeners();
    }


    private void handlePodcastElements(@NonNull final View view) {

        mPodcastAdapter = new SearchPodcastResultAdapter(mSearchViewModel, new ArrayList<Podcast>());
        mPodcastRecyclerView = view.findViewById(R.id.rv_podcast_result_list);
        mPodcastRecyclerView.setLayoutManager(
                new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));
        mPodcastRecyclerView.setAdapter(mPodcastAdapter);
        mSearchViewModel.getPodcasts().observe(this, new Observer<ResultList<Podcast>>() {
            @Override
            public void onChanged(ResultList<Podcast> podcastResultList) {
                mPodcastAdapter.replaceData(podcastResultList.getDataList());
                displayLabels();
            }
        });

        mSearchViewModel.getSelectedPodcast().observe(this, new Observer<Event<Podcast>>() {
            @Override
            public void onChanged(Event<Podcast> podcastEvent) {
                if (!podcastEvent.hasBeenConsumed()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EpisodeListFragment.PODCAST_ID_ARGUMENT,
                            podcastEvent.getPayload().getId());
                    mNavController.navigate(R.id.action_searchFragment_to_episodeListFragment,
                            bundle);
                }
            }
        });
    }

    private void handleEpisodeElements(@NonNull final View view) {
        mEpisodeAdapter = new EpisodeListResultAdapter(mSearchViewModel, new ArrayList<Episode>(),
                new SimpleDateFormat(getString(R.string.date_format), Locale.getDefault()));
        mEpisodeRecyclerView = view.findViewById(R.id.rv_episode_result_list);
        mEpisodeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mEpisodeRecyclerView.setAdapter(mEpisodeAdapter);
        mSearchViewModel.getEpisodes().observe(this, new Observer<ResultList<Episode>>() {
            @Override
            public void onChanged(ResultList<Episode> episodeResultList) {
                mEpisodeAdapter.replaceData(episodeResultList.getDataList());
                displayLabels();
            }
        });


        mSearchViewModel.getSelectedEpisode().observe(this, new Observer<Event<Episode>>() {
            @Override
            public void onChanged(Event<Episode> episodeEvent) {
                if (!episodeEvent.hasBeenConsumed()) {
                    BroadcastUtils.sendBroadcast(SearchFragment.this, episodeEvent.getPayload());
                    mEpisodeRepository.addHistory(episodeEvent.getPayload(), null);
                }
            }
        });
    }


    private void displayLabels() {
        mDivider.setVisibility(View.VISIBLE);
        mShows.setVisibility(View.VISIBLE);
        mEpisodes.setVisibility(View.VISIBLE);
    }


    private void registerTextListeners() {
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_API_CODE) {
                    if (!TextUtils.isEmpty(mMultiAutoCompleteTextView.getText())) {
                        mSearchViewModel.performTypeAheadSearch(
                                mMultiAutoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });

        mMultiAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mHandler.removeMessages(TRIGGER_API_CODE);
                mHandler.sendEmptyMessageDelayed(TRIGGER_API_CODE, AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mMultiAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mSearchViewModel.selectTerm(parent.getItemAtPosition(position).toString());
                hideKeyboard();
            }
        });

    }

    private void hideKeyboard() {
        final InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


}
