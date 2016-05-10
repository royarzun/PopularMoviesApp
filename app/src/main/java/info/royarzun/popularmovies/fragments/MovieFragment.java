package info.royarzun.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.royarzun.popularmovies.R;


public class MovieFragment extends Fragment {

    @Bind(R.id.detail_view_movie_poster) ImageView poster;
    @Bind(R.id.detail_view_movie_title) TextView title;
    @Bind(R.id.detail_view_movie_release) TextView release;
    @Bind(R.id.detail_view_movie_rating) TextView rating;
    @Bind(R.id.detail_view_movie_popularity) TextView popularity;
    @Bind(R.id.detail_view_movie_overview) TextView description;
    //@Bind(R.id.detail_view_movie_trailer) YouTubePlayerView trailerView;

    private static final String YOUTUBE_API_KEY = "AIzaSyBaDBMb3FjtUFYUcrXsU6Zl6qIqTZU-54I";
    private static final int RECOVERY_REQUEST = 1;

    private static final String ID_PARAM = "movie_id";
    private int mId;

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
        if (getArguments() != null) {
            mId = getArguments().getInt(ID_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        ButterKnife.bind(this, rootView);
        title.setText("");
        release.setText("");
        rating.setText("");
        popularity.setText("");
        description.setText("");
        String poster_url = "";
        Picasso.with(getActivity()).load(Uri.parse(poster_url)).into(poster);
        //trailerView.initialize(YOUTUBE_API_KEY, this);

        return rootView;
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
