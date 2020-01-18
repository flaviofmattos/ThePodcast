package com.myproject.thepodcast.view.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.myproject.thepodcast.R;
import com.myproject.thepodcast.view.PodcastMainActivity;

public class LibrarySelectionFragment extends Fragment {

    public static final String TAG = LibrarySelectionFragment.class.getSimpleName();

    private ListView mListView;
    private LibraryListAdapter mLibraryListAdapter;
    private NavController mNavController;

    public static LibrarySelectionFragment newInstance() {
        return new LibrarySelectionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetToolbarTitle();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_library_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLibraryListAdapter = new LibraryListAdapter();
        mListView = view.findViewById(R.id.lv_library);
        mListView.setAdapter(mLibraryListAdapter);
        mNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
    }

    private void resetToolbarTitle() {
        if (requireActivity() instanceof PodcastMainActivity) {
            ((PodcastMainActivity) requireActivity()).setCollapsingToolbarTitle(
                    getString(R.string.library));
        }
    }

    private class LibraryListAdapter extends BaseAdapter {

        private int[] descriptions = new int[]{
                R.string.history,
                R.string.favourites,
                R.string.downloaded,
                R.string.following

        };
        private int[] icons = new int[]{
                R.drawable.ic_baseline_history_24px,
                R.drawable.ic_baseline_favorite_24px,
                R.drawable.ic_baseline_cloud_download_24px,
                R.drawable.ic_baseline_grade_24px
        };

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object getItem(int position) {
            return descriptions[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.library_selection_item, parent,
                        false);
            }
            ImageView imageView = convertView.findViewById(R.id.iv_item);
            imageView.setImageResource(icons[position]);
            TextView textView = convertView.findViewById(R.id.tv_item);
            textView.setText(descriptions[position]);

            convertView.setClickable(true);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    int resId = descriptions[position];
                    bundle.putString(LibraryEpisodeListFragment.PODCAST_SEARCH_ARGUMENT,
                            getString(resId));
                    mNavController.navigate(
                            R.id.action_librarySelectionFragment_to_libraryEpisodeListFragment,
                            bundle);
                }
            });
            return convertView;
        }
    }
}
