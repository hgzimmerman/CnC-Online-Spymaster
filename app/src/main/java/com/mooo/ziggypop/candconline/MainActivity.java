package com.mooo.ziggypop.candconline;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
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
    private Menu mymenu;
    public Player.PlayersAdapter playersArrayAdapter;
    public ArrayList<Player> wordsArr1;

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
        mViewPager.setOffscreenPageLimit(3);


        // Set up the SlidingTabLayout
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);



        // Set up nav-drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //playersArrayAdapter = Player.PlayersAdapter(this, R.layout.players_layout, Player.wordsArr);


    }
    @Override
    public void onStart(){
        super.onStart();
        //jsonHandler = new JsonHandler(this);
        //jsonHandler.refreshAndUpdateViews();
    }

    @Override
    protected void onResume() {
        if (mSectionsPagerAdapter.player == null){finish();}
        super.onResume();
        jsonHandler.refreshAndUpdateViews();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNavigationDrawerFragment.isDrawerOpen()) {
            // Inflate the menu; this adds items to the action bar if it is present.
            //getMenuInflater().inflate(R.menu.menu_main, menu);
            getMenuInflater().inflate(R.menu.refresh, menu); // find some src for this
            restoreActionBar();
            mymenu = menu;
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
            // stagger the initial update cycle. - this is a hacky method of preventing a crash
            // resulting from "this" being null on app startup.
            if(jsonHandler == null){
                jsonHandler = new JsonHandler(this);
            } else {
                // Do animation start
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView)inflater.inflate(R.layout.iv_refresh, null);
                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_refresh);
                rotation.setRepeatCount(Animation.INFINITE);
                iv.startAnimation(rotation);
                item.setActionView(iv);

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

    public void resetUpdating()
    {
        // Get our refresh item from the menu
        if (mymenu!=null) {
            MenuItem m = mymenu.findItem(R.id.refresh_button);
            if (m.getActionView() != null) {
                // Remove the animation.
                m.getActionView().clearAnimation();
                m.setActionView(null);
            }
        }
    }


    /**
     * Navigation Bar setup
     * Starts an animation for the "transition"
     * Then the method updates the views using the jsonHandler.
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position+1);

        //Handle animation for the "scene" change
        View pager = findViewById(R.id.pager);
        if (pager != null) {
            AnimationSet as = new AnimationSet(true);
            Animation awayAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.down_from_top);
            as.addAnimation(awayAnimation);
            Animation returnAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.up_from_bottom);
            returnAnimation.setStartOffset(550);
            as.addAnimation(returnAnimation);
            pager.startAnimation(as);
            Log.e("animate","Should be animating");
        }
        // Update the tabs with new data.
        // Note: This appears to run at startup.
        if(jsonHandler == null){
            jsonHandler = new JsonHandler(this);
        }
        else{
            jsonHandler.updateViews();
        }

    }

    /**
     * Updates the state of what game statistics to display
     * based on the number passed in.
     *
     */
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

    /**
     * Sets the background for the Toolbars after a game is selected.
     */
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
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
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


        /**
         * This prevents the Player view from not showing up after the activity is killed.
         * I have very little idea how exactly this works, but it appears to do the job.
         * Here is a link to S.O. for reference:
         * http://stackoverflow.com/a/14849050/4979936
         * */
        @Override
        public Parcelable saveState() {
            return null;
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