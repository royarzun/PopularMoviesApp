package info.royarzun.popularmovies.data.sync.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

import info.royarzun.popularmovies.BuildConfig;
import info.royarzun.popularmovies.data.models.Movie;
import info.royarzun.popularmovies.data.models.Movies;
import info.royarzun.popularmovies.data.provider.MoviesContract;
import info.royarzun.popularmovies.data.sync.IMovieApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MoviesSyncService extends IntentService {
    private static final String TAG = MoviesSyncService.class.getSimpleName();

    public MoviesSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IMovieApiService service = retrofit.create(IMovieApiService.class);
        Call<Movies> popularMovies = service.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
        Call<Movies> topRatedMovies = service.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);

        popularMovies.enqueue(new DBStoreCallback());
        topRatedMovies.enqueue(new DBStoreCallback());
    }

    private void getData(List<Movie> movieList){
        Vector<ContentValues> contentValuesVectorVector =
                new Vector<ContentValues>(movieList.size());
        for (Movie movie: movieList) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_ID, movie.id);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_FAVORED, false);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_OVERVIEW, movie.overview);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_POPULARITY, movie.popularity);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_POSTER_PATH, movie.posterPath);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_BACKDROP_PATH, movie.backdropPath);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_RELEASE_DATE, movie.releaseDate);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_TITLE, movie.title);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_VOTE_AVERAGE, movie.voteAverage);
            movieValues.put(MoviesContract.Movies.COLUMN_MOVIE_VOTE_COUNT, movie.voteCount);

            contentValuesVectorVector.add(movieValues);
        }
        for (ContentValues values: contentValuesVectorVector) {
            this.getContentResolver().insert(MoviesContract.Movies.CONTENT_URI, values);
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

    private class DBStoreCallback implements Callback<Movies> {

        @Override
        public void onResponse(Call<Movies> call, Response<Movies> response) {
            if (!response.isSuccessful()) {
                try {
                    System.out.println(response.errorBody().string());
                } catch (IOException e) {
                    // do nothing
                }
                return;
            }
            Movies movies = response.body();
            getData(movies.movieList);
        }

        @Override
        public void onFailure(Call<Movies> call, Throwable t) {

        }
    }
}