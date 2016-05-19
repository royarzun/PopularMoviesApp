package info.royarzun.popularmovies.utils;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {
    private static final String TAG = Utils.class.getSimpleName();
    private static final String imageBaseUrl = "http://image.tmdb.org/t/p/";

    public static Uri getPosterUri(String suffix) {
        String size = "w300";
        Uri posterUri = Uri.parse(imageBaseUrl).buildUpon().appendPath(size)
                .appendPath(suffix.substring(1))
                .build();
        return posterUri;
    }

    public static Uri getBackDropUri(String suffix) {
        String size = "w1280";
        Uri posterUri = Uri.parse(imageBaseUrl).buildUpon().appendPath(size)
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

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
