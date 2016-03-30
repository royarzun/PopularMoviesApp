package info.royarzun.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MovieListFragment extends Fragment {

    private static final String TAG = MovieListFragment.class.getSimpleName();
    private static final String ORDER_BY = "order_by";

    private GridView movieGrid;
    private JSONArray movieArray;

    public MovieListFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param orderBy orders movies by popularity or ratings.
     * @return A new instance of fragment MovieListFragment.
     */
    public static MovieListFragment newInstance(String orderBy) {
        MovieListFragment fragment = new MovieListFragment();
        Bundle args = new Bundle();
        args.putString(ORDER_BY, orderBy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String orderBy = getArguments().getString(ORDER_BY);
            Uri builtUri = Uri.parse(getString(R.string.url_base) + orderBy).buildUpon()
                    .appendQueryParameter("api_key", getString(R.string.API_KEY))
                    .build();
            try{
                this.movieArray = new FetchMovieJSONData().execute(builtUri).get();
            } catch (Exception e){
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }

        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        this.movieGrid = (GridView) rootView.findViewById(R.id.movie_grid);
        this.movieGrid.setAdapter(new MovieAdapter(getActivity(), this.movieArray));
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
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

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
                e.printStackTrace();

            }
            return results;
        }
    }
}
