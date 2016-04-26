package info.royarzun.popularmovies.data.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;
import retrofit2.Call;
import retrofit2.Response;


public class MovieTrailer {
    private final static String TAG = MovieTrailer.class.getSimpleName();
    @SerializedName("key")
    public String key;

    public MovieTrailer() {
        //retrofitManager=RetrofitManager.getInstance();
    }

    public String getMovieTrailerKey(int movieId) {

        final String[] result = {null};
        retrofit2.Callback<MovieTrailers> movieTrailersCallback = new retrofit2.Callback<MovieTrailers>() {

            @Override
            public void onFailure(Call<MovieTrailers> call, Throwable t) {
                Log.e(TAG, "Failure requesting for trailers data...");
            }

            @Override
            public void onResponse(Call<MovieTrailers> call, Response<MovieTrailers> response) {
                if (response.isSuccessful() && response.body().movieTrailers.size() > 0) {
                    result[0] = response.body().movieTrailers.get(0).key;
                }
            }
        };
        return result[0];
    }
}
