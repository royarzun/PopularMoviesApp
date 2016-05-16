package info.royarzun.popularmovies.data.sync.services;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import info.royarzun.popularmovies.data.models.MovieTrailer;
import info.royarzun.popularmovies.data.models.MovieTrailers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import info.royarzun.popularmovies.BuildConfig;
import info.royarzun.popularmovies.data.models.Movie;
import info.royarzun.popularmovies.data.models.MovieComment;
import info.royarzun.popularmovies.data.models.MovieComments;
import info.royarzun.popularmovies.data.models.Movies;
import info.royarzun.popularmovies.data.provider.MoviesContract;
import info.royarzun.popularmovies.data.sync.IMovieApiService;


public class MoviesSyncService extends IntentService {
    private static final String TAG = MoviesSyncService.class.getSimpleName();
    private IMovieApiService apiService;

    private Call<Movies> popularMovies;
    private Call<Movies> topRatedMovies;
    private Call<MovieComments> movieComments;
    private Call<MovieTrailers> movieTrailers;
    private static Set<Integer> moviesId = new HashSet<>(); // To avoid repeating http requests

    public MoviesSyncService() {
        super(TAG);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IMovieApiService.class);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        popularMovies = apiService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
        topRatedMovies = apiService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);
        popularMovies.enqueue(new MovieStorageCallback());
        topRatedMovies.enqueue(new MovieStorageCallback());
        moviesId.clear();
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

    private class MovieReviewsStorageCallback implements Callback<MovieComments> {

        @Override
        public void onResponse(Call<MovieComments> call, Response<MovieComments> response) {
            if (!response.isSuccessful()) {
                try {
                    System.out.println(response.errorBody().string());
                } catch (IOException e) {
                    // do nothing
                }
                return;
            }

            MovieComments movieComments = response.body();
            for (MovieComment comment: movieComments.movieCommentList) {
                ContentValues reviewValues = new ContentValues();
                reviewValues.put(MoviesContract.Reviews.COLUMN_MOVIE_ID, movieComments.movie_id);
                reviewValues.put(MoviesContract.Reviews.COLUMN_REVIEW_ID, comment.id);
                reviewValues.put(MoviesContract.Reviews.COLUMN_REVIEW_AUTHOR, comment.author);
                reviewValues.put(MoviesContract.Reviews.COLUMN_REVIEW_CONTENT, comment.content);

                getContentResolver().insert(MoviesContract.Reviews.CONTENT_URI, reviewValues);
            }
        }

        @Override
        public void onFailure(Call<MovieComments> call, Throwable t) {

        }
    }

    private class MovieTrailersStorageCallback implements Callback<MovieTrailers> {

        @Override
        public void onResponse(Call<MovieTrailers> call, Response<MovieTrailers> response) {
            if (!response.isSuccessful()) {
                try {
                    System.out.println(response.errorBody().string());
                } catch (IOException e) {
                    // do nothing
                }
                return;
            }

            MovieTrailers movieTrailers = response.body();
            for (MovieTrailer movieTrailer: movieTrailers.movieTrailers) {
                ContentValues trailerValues = new ContentValues();
                trailerValues.put(MoviesContract.Trailers.COLUMN_MOVIE_ID, movieTrailers.id);
                trailerValues.put(MoviesContract.Trailers.COLUMN_TRAILER_ID, movieTrailer.id);
                trailerValues.put(MoviesContract.Trailers.COLUMN_TRAILER_KEY, movieTrailer.key);
                trailerValues.put(MoviesContract.Trailers.COLUMN_TRAILER_NAME, movieTrailer.name);
                trailerValues.put(MoviesContract.Trailers.COLUMN_TRAILER_SITE, movieTrailer.site);
                trailerValues.put(MoviesContract.Trailers.COLUMN_TRAILER_SIZE, movieTrailer.size);
                trailerValues.put(MoviesContract.Trailers.COLUMN_TRAILER_TYPE, movieTrailer.type);
                getContentResolver().insert(MoviesContract.Trailers.CONTENT_URI, trailerValues);
            }
        }

        @Override
        public void onFailure(Call<MovieTrailers> call, Throwable t) {

        }
    }

    private class MovieStorageCallback implements Callback<Movies> {

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
            for (Movie movie: movies.movieList) {
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

                getContentResolver().insert(MoviesContract.Movies.CONTENT_URI, movieValues);
                if (!moviesId.contains(movie.id)){
                    movieComments = apiService.getComments(movie.id, BuildConfig.MOVIE_DB_API_KEY);
                    movieComments.enqueue(new MovieReviewsStorageCallback());
                    movieTrailers = apiService.getMovieTrailers(movie.id, BuildConfig.MOVIE_DB_API_KEY);
                    movieTrailers.enqueue(new MovieTrailersStorageCallback());
                }
                moviesId.add(movie.id);
            }
            Log.d(TAG, moviesId.toString());
            Log.d(TAG, String.valueOf(moviesId.size()));
            //Log.d(TAG, "Movies sync finished...");
        }

        @Override
        public void onFailure(Call<Movies> call, Throwable t) {

        }
    }
}