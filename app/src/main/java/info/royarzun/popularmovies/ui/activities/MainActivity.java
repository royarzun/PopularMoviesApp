package info.royarzun.popularmovies.ui.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.Bind;
import info.royarzun.popularmovies.R;
import info.royarzun.popularmovies.ui.fragments.MovieListFragment;
import info.royarzun.popularmovies.data.sync.services.MoviesSyncService;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.main_view_toolbar) Toolbar toolbar;
    @Bind(R.id.main_view_collapse_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.main_view_container) ViewPager mViewPager;
    @Bind(R.id.main_view_tabs) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateMoviesServiceDB();

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        assert collapsingToolbar != null;
        collapsingToolbar.setTitle(getString(R.string.app_name));

        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void updateMoviesServiceDB() {
        Intent alarmIntent = new Intent(this, MoviesSyncService.AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MovieListFragment.newInstance(MovieListFragment.POPULAR_MOVIES);
                case 1:
                    return MovieListFragment.newInstance(MovieListFragment.TOP_RATED_MOVIES);
                case 2:
                    return MovieListFragment.newInstance(MovieListFragment.MY_FAVORITE_MOVIES);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.string_most_popular);
                case 1:
                    return getString(R.string.string_top_rated);
                case 2:
                    return getString(R.string.string_my_favorites);
            }
            return null;
        }
    }
}
