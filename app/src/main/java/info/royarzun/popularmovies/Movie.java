package info.royarzun.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Movie {

    final static String TAG = Movie.class.getSimpleName();

    private String mTitle;
    private String mDescription;
    private Float mRating;
    private Float mPopularity;
    private String mPosterUrl;
    private Context mContext;

    public Movie(Context context, JSONObject movieData) {
        mContext = context;

        try{
            mTitle = movieData.getString(
                    getStringResource(R.string.json_data_movie_title));
            mDescription = movieData.getString(
                    getStringResource(R.string.json_data_movie_overview));
            mRating = Float.parseFloat(movieData.getString(getStringResource(R.string.json_data_movie_rating)));
            mPopularity = Float.parseFloat(movieData.getString(
                    getStringResource(R.string.json_data_movie_popularity)));
            mPosterUrl = movieData.getString(
                    getStringResource(R.string.json_data_movie_poster));

        } catch (org.json.JSONException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public String getTitle() { return mTitle; }

    public String getDescription() { return mDescription; }

    public Float getRating() {return mRating; }

    public Float getPopularity() { return mPopularity; }

    public Uri getPosterUri(){
        Uri posterUri = null;

        try{
            posterUri = Uri.parse(getImageUrl(mPosterUrl));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return posterUri;
    }

    private String getImageUrl(String imageUrl) throws UnsupportedEncodingException {
        String base = getStringResource(R.string.url_images_base);
        String size = getStringResource(R.string.url_images_size);
        Uri builtUri = Uri.parse(base).buildUpon()
                .appendPath(size)
                .build();

        return builtUri.toString() + imageUrl;
    }

    private String getStringResource(int resource) {
        return mContext.getResources().getString(resource);
    }
}
