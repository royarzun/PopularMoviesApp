package info.royarzun.popularmovies.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import info.royarzun.popularmovies.BuildConfig;
import info.royarzun.popularmovies.data.provider.MoviesContract;


public class MoviesSyncService extends IntentService {
    private static final String TAG = MoviesSyncService.class.getSimpleName();

    public MoviesSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        HttpURLConnection connection;
        JSONArray results = null;
        String[] urlStrings = {
                "https://api.themoviedb.org/3/movie/top_rated",
                "https://api.themoviedb.org/3/movie/popular"
        };

        for (String urlString: urlStrings)
            try {
                Uri builtUri = Uri.parse(urlString).buildUpon()
                        .appendQueryParameter("api_key", BuildConfig.MOVIE_DB_API_KEY).build();
                URL url = new URL(builtUri.toString());
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();

                StringBuffer sBuffer = new StringBuffer();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = br.readLine()) != null) {
                    sBuffer.append(line.trim());
                }
                getDataFromJSON(sBuffer.toString());

            } catch (java.io.IOException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
    }

    private void getDataFromJSON(String jsonString) {
        final String movie_id = "id";
        final String movie_title = "original_title";
        final String movie_overview = "overview";
        final String movie_rating = "vote_average";
        final String movie_poster = "poster_path";
        final String movie_backdrop = "backdrop_path";
        final String movie_popularity = "popularity";
        final String movie_release_date = "release_date";


        // final String movie_videos = "videos";
        // final String movie_reviews = "reviews";

        JSONArray jsonArray = null;
        try {
            JSONObject jsonData = new JSONObject(jsonString);
            jsonArray = jsonData.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Vector<ContentValues> contentValuesVectorVector =
                new Vector<ContentValues>(jsonArray.length());

        for (int i=0; i<jsonArray.length(); i++) {
            try {
                JSONObject JSONMovie = jsonArray.getJSONObject(i);
                int id = JSONMovie.getInt(movie_id);
                String overview = JSONMovie.getString(movie_overview);
                String popularity = JSONMovie.get(movie_popularity).toString();
                String posterPath = JSONMovie.getString(movie_poster);
                String backdrop = JSONMovie.getString(movie_backdrop);
                String releaseDate = JSONMovie.getString(movie_release_date);
                String title = JSONMovie.getString(movie_title);
                String voteAverage = JSONMovie.getString(movie_rating);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_ID, id);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_FAVORED, false);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_OVERVIEW, overview);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_POPULARITY, popularity);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_POSTER_PATH, posterPath);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_BACKDROP_PATH, backdrop);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_RELEASE_DATE,
                        releaseDate);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_TITLE, title);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_VOTE_AVERAGE,
                        voteAverage);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_VOTE_COUNT, 1);

                contentValuesVectorVector.add(movieValues);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Problem with the JSON object...");
            }
        }
        for (int i = 0; i < contentValuesVectorVector.size(); i++) {
            this.getContentResolver().insert(MoviesContract.Movies.CONTENT_URI,
                    contentValuesVectorVector.get(i));
        }
        Log.d(TAG, "Movie data inserted...");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        public AlarmReceiver() {
            super();
        }

        @Override
        public IBinder peekService(Context myContext, Intent service) {
            return super.peekService(myContext, service);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Intent sendIntent = new Intent(context, MoviesSyncService.class);
            context.startService(sendIntent);
            Log.d(TAG, "Alarm was triggered...");
        }
    }
}