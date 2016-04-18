package info.royarzun.popularmovies.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Bind;
import info.royarzun.popularmovies.R;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;


public class MovieDetail extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    @Bind(R.id.detail_view_movie_poster) ImageView poster;
    @Bind(R.id.detail_view_movie_title) TextView title;
    @Bind(R.id.detail_view_movie_release) TextView release;
    @Bind(R.id.detail_view_movie_rating) TextView rating;
    @Bind(R.id.detail_view_movie_popularity) TextView popularity;
    @Bind(R.id.detail_view_movie_overview) TextView description;
    @Bind(R.id.detail_view_movie_trailer) YouTubePlayerView trailerView;

    private static final String YOUTUBE_API_KEY = "AIzaSyBaDBMb3FjtUFYUcrXsU6Zl6qIqTZU-54I";
    private static final int RECOVERY_REQUEST = 1;


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
        release.setText(getString(R.string.label_release_date) + intent.getStringExtra("movie_release_date"));
        rating.setText(getString(R.string.label_rating) + intent.getStringExtra("movie_rating"));
        popularity.setText(getString(R.string.label_votes) + intent.getStringExtra("movie_popularity"));
        description.setText(intent.getStringExtra("movie_description"));
        String poster_url = intent.getStringExtra("movie_poster_url");
        Picasso.with(this).load(Uri.parse(poster_url)).into(poster);
        trailerView.initialize(YOUTUBE_API_KEY, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if (!b) {
            youTubePlayer.cueVideo("fhWaJi1Hsfo"); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = errorReason.toString();
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }
}
