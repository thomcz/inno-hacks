package hrv.band.aurora.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.stkent.amplify.prompt.DefaultLayoutPromptView;
import com.github.stkent.amplify.tracking.Amplify;

import java.util.Date;

import hrv.band.aurora.R;
import hrv.band.aurora.RRInterval.Interval;
import hrv.band.aurora.storage.SQLite.SQLiteStorageController;
import hrv.band.aurora.view.fragment.MeasuringFragment;
import hrv.band.aurora.view.fragment.OverviewFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String HRV_VALUE_ID = "HRV_VALUE";
    public static final String HRV_PARAMETER_ID = "HRV_PARAMETER";
    public static final String HRV_DATE = "HRV_DATE";
    public static final String HRV_VALUE = "hrv_rr_value";
    private static final String WEBSITE_URL = "https://thomcz.github.io/aurora";
    private static final String WEBSITE_PRIVACY_URL = "https://thomcz.github.io/aurora";
    private static final String WEBSITE_IMPRINT_URL = "https://thomcz.github.io/aurora";

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    //private IRRInterval rrInterval;
    private MeasuringFragment measureFragment;
    private OverviewFragment overviewFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //NavigationDrawer

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;

        navigationView.setNavigationItemSelectedListener(this);

        //Fragment
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.main_viewpager);
        assert mViewPager != null;

        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        assert tabLayout != null;

        tabLayout.setupWithViewPager(mViewPager);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.menu_help) {
            Intent intent = new Intent(this, HelpActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_website) {
            openWebsite(WEBSITE_URL);
        } else if (id == R.id.menu_share) {
            openShareIntent();
        } else if (id == R.id.menu_privacy) {
            openWebsite(WEBSITE_PRIVACY_URL);
        } else if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.menu_feedback) {

            DefaultLayoutPromptView promptView = (DefaultLayoutPromptView) findViewById(R.id.prompt_view);
            assert promptView != null;
            Amplify.getSharedInstance().promptIfReady(promptView);

        } else if (id == R.id.menu_imprint) {
            openWebsite(WEBSITE_IMPRINT_URL);
        } else if (id == R.id.sample_data) {
            Context context = getApplicationContext();
            context.deleteDatabase(SQLiteStorageController.DATABASE_NAME);


        } else if (id == R.id.test_function) {
//            Context context = getApplicationContext();
//            IStorage storage2 = new SQLController();
//
//            ISampleDataFactory factory = new RichSampleDataFactory();
//            List<HRVParameters> parameters = factory.create(30);
//
//            List<HRVParameters> params = storage2.loadData(context, parameters.get(1).getTime());
//            double a = params.get(0).getBaevsky();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openWebsite(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void openShareIntent() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        Resources resources = getResources();
        String shareBody = resources.getString(R.string.share_body);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, resources.getString(R.string.share_subject));
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, resources.getString(R.string.share_via)));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                return measureFragment = new MeasuringFragment();
            }
            return overviewFragment = new OverviewFragment();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MEASURING";
                case 1:
                    return "OVERVIEW";
            }
            return null;
        }
    }

    public void startMeasuring(View view) {
        measureFragment.startAnimation(new Interval(new Date()));
    }

    public void getDevicePermission(View view) {
        measureFragment.getRRInterval().getDevicePermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //reset to default
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (measureFragment != null) {
            measureFragment.getRRInterval().pauseMeasuring();
        }
    }

    @Override
    protected void onDestroy() {
        if (measureFragment != null) {
            measureFragment.getRRInterval().destroy();
        }
        super.onDestroy();
    }
}
