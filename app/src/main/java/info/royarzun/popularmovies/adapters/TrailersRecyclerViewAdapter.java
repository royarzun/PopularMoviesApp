package info.royarzun.popularmovies.adapters;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import info.royarzun.popularmovies.BuildConfig;
import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.data.provider.MoviesContract;


interface OnVHTrailerClickedListener {
    void onVHTrailerClicked(TrailerVH vh);
}

class TrailerVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final OnVHTrailerClickedListener mListener;

    TextView trailerTitle;
    TextView trailerType;
    YouTubeThumbnailView youTubeThumbnailView;
    String youtubeKey;

    public TrailerVH(View itemView, OnVHTrailerClickedListener listener) {
        super(itemView);
        trailerTitle = (TextView) itemView.findViewById(R.id.trailer_title);
        trailerType = (TextView) itemView.findViewById(R.id.trailer_type);
        youTubeThumbnailView = (YouTubeThumbnailView)
                itemView.findViewById(R.id.trailer_youtube_thumbnail);

        mListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onVHTrailerClicked(this);
    }
}


public class TrailersRecyclerViewAdapter extends RecyclerView.Adapter<TrailerVH> {
    private Cursor mItems;

    public TrailersRecyclerViewAdapter() {
    }

    @Override
    public TrailerVH onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movietrailer_item, parent, false);
        return new TrailerVH(view, new OnVHTrailerClickedListener() {
            @Override
            public void onVHTrailerClicked(TrailerVH vh) {
                String videoId = vh.youtubeKey;
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                intent.putExtra("VIDEO_ID", videoId);
                parent.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(final TrailerVH holder, int position) {
        mItems.moveToPosition(position);
        holder.youtubeKey = mItems.getString(mItems.getColumnIndex(
                MoviesContract.Trailers.COLUMN_TRAILER_KEY));
        holder.trailerTitle.setText(mItems.getString(
                mItems.getColumnIndex(MoviesContract.Trailers.COLUMN_TRAILER_NAME)));
        holder.trailerType.setText(mItems.getString(
                mItems.getColumnIndex(MoviesContract.Trailers.COLUMN_TRAILER_TYPE)));
        holder.youTubeThumbnailView.initialize(BuildConfig.YOUTUBE_API_KEY,
                new YouTubeThumbnailView.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView,
                                                final YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(holder.youtubeKey);
                youTubeThumbnailLoader.setOnThumbnailLoadedListener(
                        new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                    @Override
                    public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView,
                                                  String s) {
                        youTubeThumbnailLoader.release();
                    }

                    @Override
                    public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView,
                                                 YouTubeThumbnailLoader.ErrorReason errorReason) {
                    }
                });

            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView,
                                                YouTubeInitializationResult youTubeInitializationResult) {
            }
        });
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