package info.royarzun.popularmovies.data.sync;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import info.royarzun.popularmovies.data.models.Movies;
import info.royarzun.popularmovies.data.models.MovieComments;
import info.royarzun.popularmovies.data.models.MovieTrailers;


public interface IMovieApiService {

    @GET("movie/popular")
    Call<Movies> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Call<Movies> getTopRatedMovies(@Query("api_key") String apiKey);


    @GET("movie/{id}/reviews")
    Call<MovieComments> getComments(@Path("id") int id, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<MovieTrailers> getMovieTrailer(@Path("id") int id, @Query("api_key") String apiKey);
}