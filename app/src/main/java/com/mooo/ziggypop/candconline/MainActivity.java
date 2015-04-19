package com.mooo.ziggypop.candconline;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import org.json.JSONObject;


public class MainActivity extends ActionBarActivity
    implements  NavigationDrawerFragment.NavigationDrawerCallbacks {

    public NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;
    private NavigationDrawerFragment.GameTitle mGameTitle;
    //private ActionBar actionBar;
    private String queryJsonString = "";
    private JSONObject jsonCache;
    private Toolbar toolbar;
    private Toolbar topToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private JsonHandler jsonHandler;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //request ability to show progressbar
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setSupportProgressBarIndeterminateVisibility(true);
        //Normal setup:
        setContentView(R.layout.activity_main);

        //get the toolbars
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        topToolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(topToolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        // Set up the SlidingTabLayout
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);


        // Set up nav-drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));



    }
    @Override
    public void onStart(){
        super.onStart();
        //jsonHandler = new JsonHandler(this);
        jsonHandler.refreshAndUpdateViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNavigationDrawerFragment.isDrawerOpen()) {
            // Inflate the menu; this adds items to the action bar if it is present.
            //getMenuInflater().inflate(R.menu.menu_main, menu);
            getMenuInflater().inflate(R.menu.refresh, menu); // find some src for this
            restoreActionBar();
        }
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
        if (id  == R.id.refresh_button){
            //stagger the initial update cycle - this is a hacky method of preventing a crash
            // resulting from "this" being null on app startup.
            if(jsonHandler == null){
                jsonHandler = new JsonHandler(this);
            } else {
                jsonHandler.refreshAndUpdateViews();
            }
        } else {
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            if( mDrawerLayout.isDrawerOpen(Gravity.START)){
                mDrawerLayout.closeDrawer(Gravity.START);
            } else {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        }

        return super.onOptionsItemSelected(item);
    }


    /*
     * Navigation Bar setup
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position+1);
        // Update the tabs with new data.
        // This appears to run at startup.
        if(jsonHandler == null){
            jsonHandler = new JsonHandler(this);
        }
        else{
            jsonHandler.updateViews();
        }

    }

    //TODO: find a way to add transitions, and not break version numbers.
    public void onSectionAttached(int number) {
        switch (number) {
            case 1: //Header Picture
                mNavigationDrawerFragment.mDrawerLayout.closeDrawers();
                break;
            case 2:
                queryJsonString = getString(R.string.KanesWrathJSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.KW;
                break;
            case 3:
                queryJsonString = getString(R.string.CandC3JSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.CnC3;
                break;
            case 4:
                queryJsonString = getString(R.string.GeneralsJSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.Generals;
                break;
            case 5:
                queryJsonString = getString(R.string.ZeroHourJSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.ZH;
                break;
            case 6:
                queryJsonString = getString(R.string.RedAlert3JSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.RA3;
                break;
            case 7: //settings
                Intent intentSetPref = new Intent(this, SettingsActivity.class);
                startActivityForResult(intentSetPref, 0);
                break;

        }
    }

    public void restoreActionBar() {
        switch(mGameTitle){
            case KW:
                getSupportActionBar().setTitle(getString(R.string.KanesWrath));
                findViewById(R.id.top_toolbar).setBackgroundColor(
                getResources().getColor(R.color.kw_red));
                findViewById(R.id.sliding_tabs).setBackgroundColor(
                getResources().getColor(R.color.kw_red));
                break;
            case CnC3:
                getSupportActionBar().setTitle(getString(R.string.CandC3));
                findViewById(R.id.top_toolbar).setBackgroundColor(
                getResources().getColor(R.color.cnc3_green));
                findViewById(R.id.sliding_tabs).setBackgroundColor(
                getResources().getColor(R.color.cnc3_green));
                break;
            case Generals:
                getSupportActionBar().setTitle(getString(R.string.Generals));
                findViewById(R.id.top_toolbar).setBackgroundColor(
                getResources().getColor(R.color.generals_yellow));
                findViewById(R.id.sliding_tabs).setBackgroundColor(
                getResources().getColor(R.color.generals_yellow));
                break;
            case ZH:
                getSupportActionBar().setTitle(getString(R.string.ZeroHour));
                findViewById(R.id.top_toolbar).setBackgroundColor(
                getResources().getColor(R.color.zh_orange));
                findViewById(R.id.sliding_tabs).setBackgroundColor(
                getResources().getColor(R.color.zh_orange));
                break;
            case RA3:
                getSupportActionBar().setTitle(getString(R.string.RedAlert3));
                findViewById(R.id.top_toolbar).setBackgroundColor(
                getResources().getColor(R.color.ra3_red));
                findViewById(R.id.sliding_tabs).setBackgroundColor(
                getResources().getColor(R.color.ra3_red));
                break;
        }
        mNavigationDrawerFragment.updateHeader(mGameTitle);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        // The three fragments to be adapted.
        Player.PlayersFragment player;
        Game.GamesFragment lobby;
        Game.GamesInProgressFragment inGame;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            player = new Player.PlayersFragment();
            lobby = new Game.GamesFragment();
            inGame = new Game.GamesInProgressFragment();
        }

        @Override
        public ListFragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            ListFragment fragment;
            if(position == 2) {
                fragment = inGame;
            } else if( position == 1) {
                fragment = lobby;
            } else {
                fragment = player;
            }
            Bundle args = new Bundle();
            args.putInt(Player.PlayersFragment.ARG_SECTION_NUMBER, position+1);
            fragment.setArguments(args);
            return fragment;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /*
     * Returns the string used to query individual games.
     * This string will be changed based upon the selection in the nav-drawer.
     */
    public String getQueryJsonString(){
        return queryJsonString;
    }


}