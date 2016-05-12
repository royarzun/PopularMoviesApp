package info.royarzun.popularmovies.adapters;

import android.app.Activity;
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
import info.royarzun.popularmovies.data.provider.MoviesContract;
import info.royarzun.popularmovies.fragments.MovieFragment;
import info.royarzun.popularmovies.utils.Utils;


interface OnVHClickedListener {
    void onVHClicked(MovieVH vh);
}

class MovieVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final OnVHClickedListener mListener;
    private Uri mPosterUri;
    private int mId;

    CardView mCardView;
    ImageView mPoster;

    public MovieVH(View itemView, OnVHClickedListener listener) {
        super(itemView);
        mCardView = (CardView) itemView.findViewById(R.id.card_view_item);
        mPoster = (ImageView) itemView.findViewById(R.id.movie_grid_item_poster_view);
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onVHClicked(this);
    }

    public void setMovieID(int id) {
        mId = id;
    }

    public int getMovieID() {
        return mId;
    }

    public void setPosterUri(Uri uri) {
        mPosterUri = uri;
    }

    public Uri getPosterUri() {
        return mPosterUri;
    }
}


public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MovieVH> {
    private static final String TAG = MoviesRecyclerViewAdapter.class.getSimpleName();
    private Cursor mItems;
    private Activity mContext;
    private boolean mTwoPane;
    LayoutInflater layoutInflater;

    public MoviesRecyclerViewAdapter(Activity context, boolean twoPane) {
        mContext =  context;
        layoutInflater = LayoutInflater.from(mContext);
        mTwoPane = twoPane;
    }

    @Override
    public MovieVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieVH(view, new OnVHClickedListener() {
            @Override
            public void onVHClicked(MovieVH vh) {
                if (mTwoPane) {
                    MovieFragment fragment = MovieFragment.newInstance(vh.getMovieID());
                    mContext.getFragmentManager().beginTransaction()
                            .replace(R.id.fragment_detail, fragment).commit();
                }
                else {
                    Log.d(TAG, "One panes detected");
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(MovieVH holder, int position) {
        mItems.moveToPosition(position);
        holder.setMovieID(mItems.getInt(mItems.getColumnIndex(MoviesContract.Movies.COLUMN_MOVIE_ID)));
        holder.setPosterUri(Utils.getPosterUri(mItems.getString(mItems.getColumnIndex(
                MoviesContract.Movies.COLUMN_MOVIE_POSTER_PATH))));
        Picasso.with(mContext.getApplicationContext()).load(holder.getPosterUri()).into(holder.mPoster);
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