package info.royarzun.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;


public class MovieAdapter extends BaseAdapter {

    public static final String TAG = MovieAdapter.class.getSimpleName();

    private Context context;
    private JSONArray movieList;
    private LayoutInflater inflater;

    public MovieAdapter(Context context, JSONArray movieList) {
        super();
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.movieList = movieList;
    }

    @Override
    public int getCount() {
        return this.movieList.length();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = convertView;
        JSONObject movieData;

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.grid_item, parent, false);
        }
        ImageView moviePoster = (ImageView) rootView.findViewById(R.id.movie_grid_item_poster_view);
        TextView movieName = (TextView) rootView.findViewById(R.id.movie_grid_item_title_view);

        try{
            movieData = this.movieList.getJSONObject(position);

            Picasso.with(this.context)
                    .load(buildImageUrl(movieData.
                            getString(getStringResource(R.string.json_data_movie_poster))))
                    .into(moviePoster);
            moviePoster.setAdjustViewBounds(true);
            movieName.setText(movieData.
                    getString(getStringResource(R.string.json_data_movie_title)));

        } catch (Exception e){
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return this.movieList.length();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    private String buildImageUrl(String image) throws UnsupportedEncodingException {
        String base = getStringResource(R.string.url_images_base);
        String size = getStringResource(R.string.url_images_size);
        Uri builtUri = Uri.parse(base).buildUpon()
                .appendPath(size)
                .build();

        return builtUri.toString() + image;
    }

    private String getStringResource(int resource){
        return this.context.getResources().getString(resource);
    }
}
