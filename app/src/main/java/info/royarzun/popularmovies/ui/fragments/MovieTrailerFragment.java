package info.royarzun.popularmovies.ui.fragments;

import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.adapters.TrailersRecyclerViewAdapter;
import info.royarzun.popularmovies.data.provider.MoviesContract;


public class MovieTrailerFragment extends Fragment implements LoaderCallbacks<Cursor> {
    private static final String TAG = MovieTrailerFragment.class.getSimpleName();
    private static final String ARG_MOVIE_ID = "id";
    private static final int MOVIE_TRAILER_LOADER = 3;

    private int movieId;
    private TrailersRecyclerViewAdapter mAdapter;

    @Bind(R.id.fragment_movietrailer_listview) RecyclerView movieTrailerList;

    public MovieTrailerFragment() {
    }

    public static MovieTrailerFragment newInstance(int movieId) {
        MovieTrailerFragment fragment = new MovieTrailerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getArguments() != null) {
            movieId = getArguments().getInt(ARG_MOVIE_ID);
        }
        getLoaderManager().initLoader(MOVIE_TRAILER_LOADER, null, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movietrailer_list, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new TrailersRecyclerViewAdapter();
        movieTrailerList.setHasFixedSize(true);
        movieTrailerList.setAdapter(mAdapter);
        movieTrailerList.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.d(TAG, "onCreateView");
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri movieTrailersUri = ContentUris.withAppendedId(MoviesContract.Trailers.CONTENT_URI,
                movieId);
        return new CursorLoader(getActivity(), movieTrailersUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mAdapter !=null){
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

}
