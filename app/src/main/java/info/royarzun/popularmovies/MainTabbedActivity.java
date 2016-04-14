package info.royarzun.popularmovies;

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


public class MainTabbedActivity extends AppCompatActivity {

    @Bind(R.id.main_view_toolbar) Toolbar toolbar;
    @Bind(R.id.main_view_collapse_toolbar) CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.main_view_container) ViewPager mViewPager;
    @Bind(R.id.main_view_tabs) TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        assert collapsingToolbar != null;
        collapsingToolbar.setTitle(getString(R.string.app_name));

        SectionsPagerAdapter mSectionsPagerAdapter =
                new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.icon_most_popular);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_best_rating);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MovieListFragment.newInstance(getString(R.string.url_order_by_popular));
                case 1:
                    return MovieListFragment.newInstance(getString(R.string.url_order_by_rating));
                default:
                    return MovieListFragment.newInstance(getString(R.string.url_order_by_popular));
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.string_most_popular);
                case 1:
                    return getString(R.string.string_top_rated);
            }
            return null;
        }
    }
}
