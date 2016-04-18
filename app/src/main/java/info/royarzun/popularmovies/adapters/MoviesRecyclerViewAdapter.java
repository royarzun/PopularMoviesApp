package info.royarzun.popularmovies.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import info.royarzun.popularmovies.Movie;
import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.activities.MovieDetail;


interface OnVHClickedListener {
    void onVHClicked(MovieVH vh);
}

class MovieVH extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final OnVHClickedListener mListener;

    CardView cardView;
    ImageView poster;
    TextView title;

    public MovieVH(View itemView, OnVHClickedListener listener) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.card_view_item);
        poster = (ImageView) itemView.findViewById(R.id.movie_grid_item_poster_view);
        title = (TextView) itemView.findViewById(R.id.movie_grid_item_title_view);
        mListener = listener;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        mListener.onVHClicked(this);
    }
}


public class MoviesRecyclerViewAdapter extends RecyclerView.Adapter<MovieVH> {

    private List<Movie> list = Collections.emptyList();
    private Context mContext;
    LayoutInflater layoutInflater;

    public MoviesRecyclerViewAdapter(Context context, List<Movie> movieList) {
        mContext = context;
        list = movieList;
        layoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public MovieVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.movie_item, parent, false);
        return new MovieVH(view, new OnVHClickedListener() {
            @Override
            public void onVHClicked(MovieVH vh) {
                Intent intent = new Intent(mContext.getApplicationContext(), MovieDetail.class);
                Movie movie = list.get(vh.getAdapterPosition());
                intent.putExtra("movie_title", movie.getTitle());
                intent.putExtra("movie_description", movie.getDescription());
                intent.putExtra("movie_poster_url", movie.getPosterUri().toString());
                intent.putExtra("movie_rating", String.valueOf(movie.getRating()));
                intent.putExtra("movie_popularity", String.valueOf(movie.getPopularity()));
                intent.putExtra("movie_release_date", movie.getReleaseDate());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void onBindViewHolder(MovieVH holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        Picasso.with(mContext.getApplicationContext()).load(list.get(position).getPosterUri()).into(holder.poster);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}