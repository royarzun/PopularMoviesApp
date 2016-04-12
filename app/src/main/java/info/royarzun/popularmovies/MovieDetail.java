package info.royarzun.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class MovieDetail extends AppCompatActivity {


    public MovieDetail(){
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        ImageView poster = (ImageView) findViewById(R.id.detail_view_movie_poster);

        TextView title = (TextView) findViewById(R.id.detail_view_movie_title);
        title.setText(intent.getStringExtra("movie_title"));

        TextView rating = (TextView) findViewById(R.id.detail_view_movie_rating);
        rating.setText("Rating :\t" + intent.getStringExtra("movie_rating"));

        TextView popularity = (TextView) findViewById(R.id.detail_view_movie_popularity);
        popularity.setText("Popularity :\t" + intent.getStringExtra("movie_popularity"));

        TextView description = (TextView) findViewById(R.id.detail_view_movie_overview);
        description.setText(intent.getStringExtra("movie_description"));
        String poster_url = intent.getStringExtra("movie_poster_url");

        Picasso.with(this).load(Uri.parse(poster_url)).into(poster);

    }
}
