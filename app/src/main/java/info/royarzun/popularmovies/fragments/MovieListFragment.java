package info.royarzun.popularmovies.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import info.royarzun.popularmovies.services.MoviesSyncService;


public class MovieListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = MovieListFragment.class.getSimpleName();

    private MoviesRecyclerViewAdapter mAdapter;
    private static final int MOVIE_LOADER = 1;
    private static final String[] FROM = {};

    @Bind(R.id.movie_recycler_view)
    RecyclerView mMovieRecView;

    public MovieListFragment() {
    }

    public static MovieListFragment newInstance() {
        return new MovieListFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        updateMoviesServiceDB();
        Log.d(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);

        mAdapter = new MoviesRecyclerViewAdapter(getContext());
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

    private void updateMoviesServiceDB() {
        Intent alarmIntent = new Intent(getActivity(), MoviesSyncService.AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getActivity()
                .getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, pendingIntent);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentUri = MoviesContract.Movies.CONTENT_URI;
        return new CursorLoader(getActivity(), contentUri, null, null, null, null);
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
