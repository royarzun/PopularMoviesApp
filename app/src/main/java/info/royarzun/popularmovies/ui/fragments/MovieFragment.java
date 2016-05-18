package info.royarzun.popularmovies.ui.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
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


public class MovieFragment extends Fragment {
    public static final String ARGS_ID_PARAM = "movie_id";
    private static final String TAG = MovieFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;

    @Bind(R.id.fragment_movie_poster) ImageView poster;
    @Bind(R.id.fragment_movie_backdrop) ImageView backdrop;
    @Bind(R.id.fragment_movie_title) TextView title;
    @Bind(R.id.fragment_movie_release) TextView release;
    @Bind(R.id.fragment_movie_rating_bar) RatingBar rating;
    @Bind(R.id.fragment_rating_text) TextView ratingText;
    @Bind(R.id.fragment_movie_popularity) TextView popularity;
    @Bind(R.id.fragment_movie_overview) TextView description;
    @Bind(R.id.fragment_movie_favorite_button) ToggleButton favButton;
    @Bind(R.id.fragment_movie_trailers_button) Button trailersButton;
    @Bind(R.id.fragment_movie_review_label) TextView reviewLabel;
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
        if (getArguments() != null) {
            updateView(getArguments().getInt(ARGS_ID_PARAM));
        }
        else {
            updateView(-1);
        }

    }

    private void updateView(final int id) {
        if (id == -1) {
            title.setText("");
            release.setText("");
            popularity.setText("");
            description.setText("");
            Log.d(TAG, "on updateView(" + String.valueOf(id) + ")");
        }

        Uri movieUri = ContentUris.withAppendedId(MoviesContract.Movies.CONTENT_URI, id);
        Uri reviewsUri = ContentUris.withAppendedId(MoviesContract.Reviews.CONTENT_URI, id);

        // Movie Details
        final Cursor mCursor = getActivity().getContentResolver().query(movieUri, null, null, null, null);
        if (!mCursor.moveToFirst()){
            mCursor.close();
            return;
        }

        Picasso.with(getActivity()).load(Utils.getBackDropUri(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_BACKDROP_PATH)))).into(backdrop);
        Picasso.with(getActivity()).load(Utils.getPosterUri(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_POSTER_PATH)))).into(poster);
        title.setText(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_TITLE)));
        release.setText(getString(R.string.label_release_date) + Utils.getDateInNiceFormat(mCursor.getString(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_RELEASE_DATE))));
        Float cRating = mCursor.getFloat(
                mCursor.getColumnIndex(
                        MoviesContract.Movies.COLUMN_MOVIE_VOTE_AVERAGE));

        rating.setNumStars(5);
        rating.setRating(cRating/2);
        ratingText.setText("(" + getString(R.string.label_rating) + String.valueOf(cRating) + ")");

        popularity.setText(getString(R.string.label_popularity) + mCursor.getString(
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
                String mSelectionClause = MoviesContract.Movies.COLUMN_MOVIE_ID +  "= ?";
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
                else ft.replace(R.id.fragment_detail_activity, fragment, "trailers").addToBackStack(null);

                ft.commit();

            }
        });

        // Reviews list
        String[] columns = new String[] {
                MoviesContract.Reviews.COLUMN_REVIEW_AUTHOR,
                MoviesContract.Reviews.COLUMN_REVIEW_CONTENT
        };
        int[] to = new int[] {
                R.id.review_author,
                R.id.review_content
        };

        Cursor rCursor = getActivity().getContentResolver().query(reviewsUri,
                null, null, null, null);
        if (rCursor != null) {
            reviewLabel.setText(getString(R.string.label_review));
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                    R.layout.comment_item,rCursor, columns, to, 0);
            commentList.setAdapter(adapter);
            setListViewHeightBasedOnChildren(commentList);
        }
        rCursor.close();
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

    private static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
