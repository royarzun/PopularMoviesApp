package info.royarzun.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static Uri getPosterUri(String suffix) {
        String base = "http://image.tmdb.org/t/p/";
        String size = "w300";
        Uri posterUri = Uri.parse(base).buildUpon().appendPath(size)
                .appendPath(suffix.substring(1))
                .build();
        return posterUri;
    }

    public static Uri getBackDropUri(String suffix) {
        String base = "http://image.tmdb.org/t/p/";
        String size = "w1280";
        Uri posterUri = Uri.parse(base).buildUpon().appendPath(size)
                .appendPath(suffix.substring(1))
                .build();
        return posterUri;
    }

    public static String getDateInNiceFormat(String date) {
        try {
            Date releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy");
            return format.format(releaseDate);

        } catch (ParseException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
