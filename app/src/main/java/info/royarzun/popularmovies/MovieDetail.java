package info.royarzun.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.Bind;
import com.squareup.picasso.Picasso;


public class MovieDetail extends AppCompatActivity {

    @Bind(R.id.detail_view_movie_poster) ImageView poster;
    @Bind(R.id.detail_view_movie_title) TextView title;
    @Bind(R.id.detail_view_movie_release) TextView release;
    @Bind(R.id.detail_view_movie_rating) TextView rating;
    @Bind(R.id.detail_view_movie_popularity) TextView popularity;
    @Bind(R.id.detail_view_movie_overview) TextView description;

    public MovieDetail(){
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();

        ButterKnife.bind(this);
        title.setText(intent.getStringExtra("movie_title"));
        release.setText(getString(R.string.label_release_date) +
                intent.getStringExtra("movie_release_date"));
        rating.setText(getString(R.string.label_rating) +
                intent.getStringExtra("movie_rating"));
        popularity.setText(getString(R.string.label_votes) +
                intent.getStringExtra("movie_popularity"));
        description.setText(intent.getStringExtra("movie_description"));
        String poster_url = intent.getStringExtra("movie_poster_url");
        Picasso.with(this).load(Uri.parse(poster_url)).into(poster);

    }
}
