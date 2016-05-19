package info.royarzun.popularmovies.ui.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;
import butterknife.Bind;
import butterknife.ButterKnife;

import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.data.provider.MoviesContract;
import info.royarzun.popularmovies.utils.Utils;


public class MovieFragment extends Fragment implements LoaderCallbacks<Cursor> {
    public static final String ARGS_ID_PARAM = "movie_id";

    private static final int MOVIE_LOADER = 21;
    private static final int REVIEWS_LOADER = 22;

    @Bind(R.id.fragment_movie_poster) ImageView poster;
    @Bind(R.id.fragment_movie_backdrop) ImageView backdrop;
    @Bind(R.id.fragment_movie_title) TextView title;
    @Bind(R.id.fragment_movie_release) TextView release;
    @Bind(R.id.fragment_movie_rating_bar) RatingBar rating;
    @Bind(R.id.fragment_movie_popularity) TextView popularity;
    @Bind(R.id.fragment_movie_overview) TextView description;
    @Bind(R.id.fragment_movie_favorite_button) ToggleButton favButton;
    @Bind(R.id.fragment_movie_trailers_button) Button trailersButton;
    @Bind(R.id.fragment_movie_comments_list) ListView commentList;


    public MovieFragment() {
    }

    public static MovieFragment newInstance(int movie_id) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_ID_PARAM, movie_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER, getArguments(), this);
        getLoaderManager().initLoader(REVIEWS_LOADER, getArguments(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void updateDetails(Cursor mCursor) {
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return;
        }
        final int id = mCursor.getInt(mCursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_ID));

        Picasso.with(getActivity()).load(Utils.getBackDropUri(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_BACKDROP_PATH)))).into(backdrop);
        Picasso.with(getActivity()).load(Utils.getPosterUri(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_POSTER_PATH)))).into(poster);
        title.setText(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_TITLE)));
        release.setText(Utils.getDateInNiceFormat(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_RELEASE_DATE))));
        Float cRating = mCursor.getFloat(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_VOTE_AVERAGE));

        rating.setNumStars(5);
        rating.setRating(cRating / 2);

        popularity.setText(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_POPULARITY)));
        description.setText(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_OVERVIEW)));

        int fav = mCursor.getInt(mCursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_FAVORED));
        if (fav == 0) favButton.setChecked(false);
        else favButton.setChecked(true);

        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                int favVal = 0;
                if (favButton.isChecked()) favVal = 1;
                values.put(MoviesContract.Movies.COLUMN_MOVIE_FAVORED, String.valueOf(favVal));
                String mSelectionClause = MoviesContract.Movies.COLUMN_MOVIE_ID + "= ?";
                String[] mSelectionArgs = {String.valueOf(id)};
                getActivity().getContentResolver().update(MoviesContract.Movies.CONTENT_URI,
                        values, mSelectionClause, mSelectionArgs);
            }
        });
        mCursor.close();
        trailersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieTrailerFragment fragment = MovieTrailerFragment.newInstance(id);
                FragmentTransaction ft = getFragmentManager().beginTransaction();

                if (getActivity().findViewById(R.id.fragment_detail) != null)
                    ft.replace(R.id.fragment_detail, fragment, "trailers").addToBackStack(null);
                else
                    ft.replace(R.id.fragment_detail_activity, fragment, "trailers").addToBackStack(null);

                ft.commit();

            }
        });
    }

    private void updateReviews(Cursor rCursor) {
        String[] from = new String[]{
                MoviesContract.Reviews.COLUMN_REVIEW_AUTHOR,
                MoviesContract.Reviews.COLUMN_REVIEW_CONTENT
        };
        int[] to = new int[]{
                R.id.review_author,
                R.id.review_content
        };
        if (!rCursor.moveToFirst()) {
            rCursor.close();
            return;
        }
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.comment_item, rCursor, from, to, 0);
        commentList.setAdapter(adapter);
        Utils.setListViewHeightBasedOnChildren(commentList);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ButterKnife.unbind(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        int movieId = args.getInt(ARGS_ID_PARAM);
        switch (id){
            case MOVIE_LOADER:
                Uri movieUri = ContentUris.withAppendedId(MoviesContract.Movies.CONTENT_URI,
                        movieId);
                return new CursorLoader(getActivity(), movieUri, null, null, null, null);
            case REVIEWS_LOADER:
                Uri reviewsUri = ContentUris.withAppendedId(MoviesContract.Reviews.CONTENT_URI,
                        movieId);
                return new CursorLoader(getActivity(), reviewsUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case MOVIE_LOADER:
                updateDetails(data);
                break;
            case REVIEWS_LOADER:
                updateReviews(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
