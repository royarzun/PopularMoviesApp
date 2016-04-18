package info.royarzun.popularmovies.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.royarzun.popularmovies.Movie;
import info.royarzun.popularmovies.MoviesRecyclerViewAdapter;
import info.royarzun.popularmovies.R;


public class MovieListFragment extends Fragment {

    private static final String TAG = MovieListFragment.class.getSimpleName();
    private static final String ORDER_BY = "order_by";

    private List<Movie> movieList;
    @Bind(R.id.movie_recycler_view)
    RecyclerView movieRecView;

    public MovieListFragment() {
    }

    public static MovieListFragment newInstance(String orderBy) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putString(ORDER_BY, orderBy);
        fragment.setArguments(args);
        return fragment;
    }

    private List<Movie> populate(Uri url) {
        List<Movie> movieList = new ArrayList<>();
        JSONArray dataArray;
        try {
            dataArray = new FetchMovieJSONData().execute(url).get();
            for(int i=0; i < dataArray.length(); i++) {
                try {
                    Movie movie = new Movie(getActivity(), dataArray.getJSONObject(i));
                    movieList.add(movie);

                } catch (org.json.JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();

        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage());
        }
        return movieList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String orderBy = getArguments().getString(ORDER_BY);
            Uri builtUri = Uri.parse(getString(R.string.url_base) + orderBy)
                    .buildUpon()
                    .appendQueryParameter("api_key", getString(R.string.API_KEY))
                    .build();
            movieList = populate(builtUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, rootView);
        MoviesRecyclerViewAdapter adapter = new MoviesRecyclerViewAdapter(getContext(), movieList);
        movieRecView.setAdapter(adapter);
        movieRecView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        return rootView;
    }

    public class FetchMovieJSONData extends AsyncTask<Uri, Void, JSONArray> {
        private final String TAG = FetchMovieJSONData.class.getSimpleName();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
        }

        @Override
        protected JSONArray doInBackground(Uri... params) {
            HttpURLConnection connection;
            JSONArray results = null;

            try {
                URL url = new URL(params[0].toString());
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                StringBuffer sBuffer = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = br.readLine()) != null) {
                    sBuffer.append(line.trim());
                }
                results = new JSONObject(sBuffer.toString()).getJSONArray("results");

            } catch (java.io.IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();

            } catch (org.json.JSONException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
            return results;
        }
    }
}
