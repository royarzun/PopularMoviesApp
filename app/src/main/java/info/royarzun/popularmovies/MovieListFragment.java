package info.royarzun.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MovieListFragment extends Fragment {

    private static final String TAG = MovieListFragment.class.getSimpleName();
    private static final String ORDER_BY = "order_by";

    private List<Movie> movieList;
    private RecyclerView movieRecView;

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
            movieRecView = (RecyclerView) getActivity().findViewById(R.id.movie_recycler_view);
            String orderBy = getArguments().getString(ORDER_BY);
            Uri builtUri = Uri.parse(getString(R.string.url_base) + orderBy).buildUpon()
                    .appendQueryParameter("api_key", getString(R.string.API_KEY))
                    .build();
            movieList = populate(builtUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_list, container, false);
        movieRecView = (RecyclerView) rootView.findViewById(R.id.movie_recycler_view);

        MovieRecyclerViewAdapter adapter = new MovieRecyclerViewAdapter(movieList);
        movieRecView.setAdapter(adapter);
        movieRecView.setLayoutManager(new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL));
        return rootView;
    }

    interface OnVHClickedListener {
        void onVHClicked(MovieVH vh);
    }

    static class MovieVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final OnVHClickedListener mListener;

        CardView cardView;
        ImageView poster;
        TextView title;

        public MovieVH(View itemView, OnVHClickedListener listener) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view_item);
            poster = (ImageView) itemView.findViewById(R.id.movie_grid_item_poster_view);
            title = (TextView) itemView.findViewById(R.id.movie_grid_item_title_view);
            mListener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onVHClicked(this);
        }
    }


    class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieVH> {

        List<Movie> list = Collections.emptyList();

        public MovieRecyclerViewAdapter(List<Movie> movieList) {
            list = movieList;
        }

        @Override
        public MovieVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.grid_item, parent, false);

            return new MovieVH(v, new OnVHClickedListener() {
                @Override
                public void onVHClicked(MovieVH vh) {
                    Intent intent = new Intent(getActivity(), MovieDetail.class);
                    Movie movie = list.get(vh.getAdapterPosition());
                    intent.putExtra("movie_title", movie.getTitle());
                    intent.putExtra("movie_description", movie.getDescription());
                    intent.putExtra("movie_poster_url", movie.getPosterUri().toString());
                    intent.putExtra("movie_rating", String.valueOf(movie.getRating()));
                    intent.putExtra("movie_popularity", String.valueOf(movie.getPopularity()));
                    intent.putExtra("movie_release_date", movie.getReleaseDate());
                    startActivity(intent);
                }
            });
        }

        @Override
        public void onBindViewHolder(MovieVH holder, int position) {
            holder.title.setText(list.get(position).getTitle());
            Picasso.with(getActivity()).load(list.get(position).getPosterUri()).into(holder.poster);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
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
