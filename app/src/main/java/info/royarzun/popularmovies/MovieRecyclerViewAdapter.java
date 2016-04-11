package info.royarzun.popularmovies;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;


class MovieVH extends RecyclerView.ViewHolder {
    CardView cardView;
    ImageView poster;
    TextView title;
    TextView overview;

    public MovieVH(View itemView) {
        super(itemView);
        cardView = (CardView) itemView.findViewById(R.id.card_view_item);
        poster = (ImageView) itemView.findViewById(R.id.movie_detail_picture);
        title = (TextView) itemView.findViewById(R.id.movie_grid_item_title_view);
        overview = (TextView) itemView.findViewById(R.id.movie_grid_item_description_view);
    }
}


public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieVH> {

    private Context mContext;
    List<Movie> list = Collections.emptyList();


    public MovieRecyclerViewAdapter(Context context, List<Movie> movieList) {
        mContext = context;
        list = movieList;
    }

    @Override
    public MovieVH onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        return new MovieVH(v);
    }

    @Override
    public void onViewAttachedToWindow(MovieVH holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onBindViewHolder(MovieVH holder, int position) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.title.setText(list.get(position).getTitle());
        holder.overview.setText(list.get(position).getDescription());
        //animate(holder);

    }

    @Override
    public int getItemCount() {
        //returns the number of elements the RecyclerView will display
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(int position, Movie data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(Movie data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }
}
