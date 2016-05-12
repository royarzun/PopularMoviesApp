package info.royarzun.popularmovies.fragments;

import android.app.Fragment;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
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
   // @Bind(R.id.detail_view_movie_trailer) YouTubePlayerView trailerView;

    public static final String ID_PARAM = "movie_id";
    private static final int RECOVERY_REQUEST = 1;

    private OnFragmentInteractionListener mListener;

    public MovieFragment() {
    }

    public static MovieFragment newInstance(int movie_id) {
        MovieFragment fragment = new MovieFragment();
        Bundle args = new Bundle();
        args.putInt(ID_PARAM, movie_id);
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
            updateView(getArguments().getInt(ID_PARAM));
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
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        if (!cursor.moveToFirst()){
            return;
        }
        String cTitle = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_TITLE));
        String cRelease = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_RELEASE_DATE));
        Float cRating = cursor.getFloat(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_VOTE_AVERAGE));
        String cPopularity = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_POPULARITY));
        String cPosterUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_POSTER_PATH));
        String cBackdropUrl = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_BACKDROP_PATH));
        String cDescription = cursor.getString(cursor.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_OVERVIEW));
        cursor.close();

        title.setText(cTitle);
        release.setText(cRelease);
        rating.setNumStars(5);
        rating.setRating(cRating/2);
        ratingText.setText("(" + getString(R.string.label_rating) + " : " + String.valueOf(cRating) + ")");
        popularity.setText(cPopularity);
        description.setText(cDescription);

        Log.d(TAG, Utils.getPosterUri(cBackdropUrl).toString());
        Picasso.with(getActivity()).load(Utils.getBackDropUri(cBackdropUrl)).into(backdrop);
        Picasso.with(getActivity()).load(Utils.getPosterUri(cPosterUrl)).into(poster);
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
