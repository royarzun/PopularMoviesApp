package info.royarzun.popularmovies.data.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Bundle;
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

import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.data.provider.MoviesContract;


public class MoviesSyncAdapter extends AbstractThreadedSyncAdapter{
    private static final String TAG = MoviesSyncAdapter.class.getSimpleName();

    public MoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        HttpURLConnection connection;
        String[] urlStrings = {"https://api.themoviedb.org/3/movie/top_rated",
                "https://api.themoviedb.org/3/movie/top_rated"};

        for (String urlString: urlStrings)
            try {
                Uri builtUri = Uri.parse(urlString).buildUpon()
                        .appendQueryParameter("api_key", getContext()
                                .getResources().getString(R.string.API_KEY)).build();
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
                String releaseDate = JSONMovie.getString(movie_release_date);
                String title = JSONMovie.getString(movie_title);
                String voteAverage = JSONMovie.getString(movie_rating);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_ID, id);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_FAVORED, false);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_OVERVIEW, overview);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_POPULARITY, popularity);
                movieValues.put(MoviesContract.MoviesColumns.COLUMN_MOVIE_POSTER_PATH, posterPath);
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
            getContext().getContentResolver().insert(MoviesContract.Movies.CONTENT_URI,
                    contentValuesVectorVector.get(i));
            Log.d(TAG, "Movie data inserted...");
        }
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                MoviesContract.CONTENT_AUTHORITY, bundle);
    }

    public static Account getSyncAccount(Context context) {

        AccountManager accountManager = (AccountManager) context
                .getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        if (null == accountManager.getPassword(newAccount)) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
        }
        return newAccount;
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }


}
