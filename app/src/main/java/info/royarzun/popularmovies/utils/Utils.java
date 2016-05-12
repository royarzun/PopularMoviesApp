package info.royarzun.popularmovies.utils;

import android.net.Uri;


public class Utils {
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
}
