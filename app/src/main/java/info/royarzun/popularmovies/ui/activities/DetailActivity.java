package info.royarzun.popularmovies.ui.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.ui.fragments.MovieFragment;
import info.royarzun.popularmovies.ui.fragments.MovieTrailerFragment;


public class DetailActivity extends AppCompatActivity {
    private static final String TAG = DetailActivity.class.getSimpleName();
    @Bind(R.id.detail_activity_toolbar) Toolbar toolbar;

    public static final String ARG_MOVIE_ID = "movie_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Bundle b = getIntent().getExtras();
        MovieFragment fragment = MovieFragment.newInstance(b.getInt(ARG_MOVIE_ID));
        getFragmentManager().beginTransaction().add(R.id.fragment_detail_activity, fragment)
                .addToBackStack("detail").commit();
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MovieTrailerFragment tFragment = (MovieTrailerFragment) getFragmentManager().findFragmentByTag("trailers");
        switch (item.getItemId()) {
            case android.R.id.home:
                if (tFragment!=null && tFragment.isVisible()){
                    getFragmentManager().popBackStack();
                    return true;
                }
                else {
                    NavUtils.navigateUpFromSameTask(this);
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
