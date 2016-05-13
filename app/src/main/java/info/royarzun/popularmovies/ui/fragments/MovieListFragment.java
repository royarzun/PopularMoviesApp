package info.royarzun.popularmovies.ui.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.royarzun.popularmovies.adapters.MoviesRecyclerViewAdapter;
import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.data.provider.MoviesContract;


public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MovieListFragment.class.getSimpleName();

    public static final int POPULAR_MOVIES = 0;
    public static final int TOP_RATED_MOVIES = 1;
    public static final int MY_FAVORITE_MOVIES = 2;

    private static final int MOVIE_LOADER = 1;
    private static final String ARGS_LIST_TYPE_PARAM = "listType";

    private MoviesRecyclerViewAdapter mAdapter;
    private boolean mTwoPane = false;
    private int mListType;

    @Bind(R.id.movie_recycler_view)
    RecyclerView mMovieRecView;

    public MovieListFragment() {
    }

    public static MovieListFragment newInstance(int listType) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_LIST_TYPE_PARAM, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListType = getArguments().getInt(ARGS_LIST_TYPE_PARAM);
        }
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);
        if (getActivity().findViewById(R.id.fragment_detail) != null) {
            mTwoPane = true;
        }
        mAdapter = new MoviesRecyclerViewAdapter(getActivity(), mTwoPane);
        mMovieRecView.setAdapter(mAdapter);
        mMovieRecView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        Log.d(TAG, "onCreateView");
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        Log.d(TAG, "Fragment destroyed");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentUri = MoviesContract.Movies.CONTENT_URI;
        switch (mListType){
            case POPULAR_MOVIES:
                Log.d(TAG, "loading popular movies fragment");
                return new CursorLoader(getActivity(), contentUri, null, null, null,
                        MoviesContract.Movies.POPULARITY_SORT);
            case TOP_RATED_MOVIES:
                Log.d(TAG, "loading top rated movies fragment");
                return new CursorLoader(getActivity(), contentUri, null, null, null,
                        MoviesContract.Movies.VOTE_SORT);
            case MY_FAVORITE_MOVIES:
                Log.d(TAG, "loading my favorite movies list");
                return new CursorLoader(getActivity(), contentUri, null, null, null, null);
            default:
                return new CursorLoader(getActivity(), contentUri, null, null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter!=null) {
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
