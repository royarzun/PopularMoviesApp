package info.royarzun.popularmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import info.royarzun.popularmovies.R;


interface OnVHClickedListener {
    void onVHClicked(MovieVH vh);
}

class MovieVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final OnVHClickedListener mListener;

    CardView cardView;
    ImageView poster;

    public MovieVH(View itemView, OnVHClickedListener listener) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.card_view_item);
        poster = (ImageView) itemView.findViewById(R.id.movie_grid_item_poster_view);
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onVHClicked(this);
    }
}


public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MovieVH> {
    private static final String TAG = MoviesRecyclerViewAdapter.class.getSimpleName();
    private Cursor mItems;
    private Context mContext;
    LayoutInflater layoutInflater;

    public MoviesRecyclerViewAdapter(Context context) {
        mContext = context;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MovieVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieVH(view, new OnVHClickedListener() {
            @Override
            public void onVHClicked(MovieVH vh) {

                Log.d(TAG, "Blah");
            }
        });
    }

    @Override
    public void onBindViewHolder(MovieVH holder, int position) {
        mItems.moveToPosition(position);
        String base = "http://image.tmdb.org/t/p/";
        String size = "w300";
        Uri posterUri = Uri.parse(base).buildUpon()
                .appendPath(size)
                .appendPath(mItems.getString(7).substring(1))
                .build();
        Picasso.with(mContext.getApplicationContext()).load(posterUri).into(holder.poster);
    }

    public Cursor getCursor() {
        return mItems;
    }

    @Override
    public int getItemCount() {
        if (mItems != null)
            return mItems.getCount();
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor != null) {
            mItems = newCursor;
            notifyDataSetChanged();
        }
    }
}