package info.royarzun.popularmovies.ui.fragments;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import butterknife.Bind;
import butterknife.ButterKnife;

import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.data.provider.MoviesContract;
import info.royarzun.popularmovies.utils.Utils;


public class MovieFragment extends Fragment {
    private static final String TAG = MovieFragment.class.getSimpleName();
    @Bind(R.id.fragment_movie_poster) ImageView poster;
    @Bind(R.id.fragment_movie_backdrop) ImageView backdrop;
    @Bind(R.id.fragment_movie_title) TextView title;
    @Bind(R.id.fragment_movie_release) TextView release;
    @Bind(R.id.fragment_movie_rating_bar) RatingBar rating;
    @Bind(R.id.fragment_rating_text) TextView ratingText;
    @Bind(R.id.fragment_movie_popularity) TextView popularity;
    @Bind(R.id.fragment_movie_overview) TextView description;
    @Bind(R.id.fragment_movie_favorite_button) FloatingActionButton favButton;

    public static final String ARGS_ID_PARAM = "movie_id";
    private static final int BACKDROP_LOADER = 2;

    private OnFragmentInteractionListener mListener;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        ButterKnife.bind(this, rootView);
        title.setText("");
        release.setText("");
        popularity.setText("");
        description.setText("");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            updateView(getArguments().getInt(ARGS_ID_PARAM));
        }
        else {
            updateView(-1);
        }
    }

    private void updateView(long id) {
        if (id == -1) {
            title.setText("");
            release.setText("");
            popularity.setText("");
            description.setText("");
            Log.d(TAG, "on updateView(" + String.valueOf(id) + ")");
        }

        Uri uri = ContentUris.withAppendedId(MoviesContract.Movies.CONTENT_URI, id);
        final Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (!cursor.moveToFirst()){
            cursor.close();
            return;
        }

        String cBackdropUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_BACKDROP_PATH));
        Picasso.with(getActivity()).load(Utils.getBackDropUri(cBackdropUrl)).into(backdrop);
        String cTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_TITLE));
        String cRelease = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_RELEASE_DATE));
        Float cRating = cursor.getFloat(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_VOTE_AVERAGE));
        String cPopularity = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_POPULARITY));
        String cPosterUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_POSTER_PATH));
        String cDescription = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_OVERVIEW));
        String cIsFavored = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_FAVORED));
        Picasso.with(getActivity()).load(Utils.getPosterUri(cPosterUrl)).into(poster);
        title.setText(cTitle);
        release.setText(getString(R.string.label_release_date) + Utils.getDateInNiceFormat(cRelease));
        rating.setNumStars(5);
        rating.setRating(cRating/2);
        ratingText.setText("(" + getString(R.string.label_rating) + String.valueOf(cRating) + ")");
        popularity.setText(getString(R.string.label_popularity) + cPopularity);
        description.setText(cDescription);
        Log.d(TAG, cIsFavored);
        favButton.setSelected(Boolean.parseBoolean(cIsFavored));
        cursor.close();
        //Log.d(TAG, favButton.get);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
