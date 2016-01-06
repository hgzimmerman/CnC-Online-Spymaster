package com.mooo.ziggypop.candconline;


import java.util.Locale;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity
    implements  NavigationDrawerFragment.NavigationDrawerCallbacks,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "MainActivity";

    public NavigationDrawerFragment mNavigationDrawerFragment;
    private NavigationDrawerFragment.GameTitle mGameTitle;
    private String queryJsonString = "";// String used to choose what game to sample from the JSON
    private JsonHandler jsonHandler;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private int currentNavDrawerIndex = 0;
    public Toolbar topToolbar;
    public Toolbar bottomToolbar;
    public PlayerDatabaseHandler dbHandler;


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
        dbHandler = new PlayerDatabaseHandler(getApplicationContext());

        //Normal setup:
        setContentView(R.layout.activity_main);

        //get the toolbars
        topToolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(topToolbar);

         bottomToolbar = (Toolbar) findViewById(R.id.toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        //Prevent the SwipeRefreshLayout from activating when swiping horizontally on the ViewPager.
        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                setSafeToRefresh(false, -1);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        setSafeToRefresh(true, -1);
                        break;
                    case MotionEvent.ACTION_MOVE: // When the user scrolls side to side
                        setSafeToRefresh(false, -1);
                        break;
                }
                return false;
            }
        });

        // Set up the SlidingTabLayout
        SlidingTabLayout mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);



        // Set up nav-drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //set up swipe down to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshAndUpdateViews();}
        });
        mSwipeRefreshLayout.setProgressBackgroundColor(R.color.light_grey);

        // Handle intent from settings, set the nav drawer and selected game to the one presented in settings
        // Todo: look into intent filters
        if (getIntent().getExtras() != null) {
            int currentGame = getIntent().getExtras().getInt("current_game");
            Log.v(TAG, "currentGame: " + currentGame);
            onSectionAttached(currentGame);
            currentNavDrawerIndex = currentGame;
            mNavigationDrawerFragment.mDrawerListView.setItemChecked(currentGame, true);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNavigationDrawerFragment.isDrawerOpen()) {
            // Inflate the menu; this adds items to the action bar if it is present.
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_main, menu);
            restoreActionBar();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_help:
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
                helpBuilder.setTitle(R.string.help);
                View dialogView = getLayoutInflater().inflate(R.layout.main_help_alert_dialog, null);
                helpBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { } // do nothing
                });
                helpBuilder.setView(dialogView);
                AlertDialog helpDialog = helpBuilder.create();
                helpDialog.show();

                return true;
            /*
            case R.id.action_graph:
                Log.v(TAG, "Graph selected");
                Intent alarmIntent = new Intent(this, NotificationMessage.class);
                sendBroadcast(alarmIntent);
                return true;
                */
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    /**
     * Navigation Bar setup
     * Starts an animation for the "transition"
     * Then the method updates the views using the jsonHandler.
     * @param position An int representing which item was chosen (starting at 0).
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position);
        //Handle animation for the "scene" change
        final View pager = findViewById(R.id.pager);
        // if the pager exists, the selected drawer isn't the picture, and isn't the current drawer, animate the transition.
        final Animation returnAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.up_from_bottom);

        if(jsonHandler == null){
            jsonHandler = new JsonHandler(this);
        }
        if (position != 0 && position != currentNavDrawerIndex
                && getSectionsPagerAdapter()!= null && pager != null ) {
            // handle the case where you animate on a transition away from a page with no items.
            // If the away and return animations are played in these cases, the new page automatically
            // populates with info, then slides away and slides back, when it should only return.
            if (mViewPager.getCurrentItem() == 0
                    && mSectionsPagerAdapter.player.getListView().getChildCount() == 0 ){
                updateViews();
                pager.startAnimation(returnAnimation);
                Log.v(TAG, "Animating return from a page with no data");
            }
            else if (mViewPager.getCurrentItem() == 1
                    && mSectionsPagerAdapter.lobby.getListView().getChildCount() == 0 ){
                updateViews();
                pager.startAnimation(returnAnimation);
                Log.v(TAG, "Animating return from a page with no data");
            }
            else if (mViewPager.getCurrentItem() == 2
                    && mSectionsPagerAdapter.inGame.getListView().getChildCount() == 0 ){
                updateViews();
                pager.startAnimation(returnAnimation);
                Log.v(TAG, "Animating return from a page with no data");
            }
            else { // Animate the pager sliding away and returning, this is the normal, expected behavior.
                Animation awayAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.down_from_top);
                awayAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    // instead of using an AnimationSet, we update the views after the first animation ends.
                    public void onAnimationEnd(Animation animation) {
                        updateViews();
                        pager.startAnimation(returnAnimation); }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                pager.startAnimation(awayAnimation);
                Log.v(TAG, "Animating transition between game titles");
            }
        }
        // Save which drawer was opened to prevent re-animating if you select the same one.
        if (position !=0 && position!=6)
            currentNavDrawerIndex = position;
    }

    /**
     * Updates the state of what game statistics to display
     * based on the number passed in.
     * @param number An int representing which item was chosen (starting at 1)
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 0: //Header Picture
                mNavigationDrawerFragment.mDrawerLayout.closeDrawers();
                break;
            case 1:
                queryJsonString = getString(R.string.KanesWrathJSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.KW;
                break;
            case 2:
                queryJsonString = getString(R.string.CandC3JSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.CnC3;
                break;
            case 3:
                queryJsonString = getString(R.string.GeneralsJSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.Generals;
                break;
            case 4:
                queryJsonString = getString(R.string.ZeroHourJSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.ZH;
                break;
            case 5:
                queryJsonString = getString(R.string.RedAlert3JSON);
                mGameTitle = NavigationDrawerFragment.GameTitle.RA3;
                break;
            case 6: //settings
                Intent intentSetPref = new Intent(this, SettingsActivity.class);
                intentSetPref.putExtra("current_game", currentNavDrawerIndex);
                startActivityForResult(intentSetPref, 0);
                break;
        }
    }

    /**
     * Sets the background for the Toolbars after a game is selected.
     * Also sets the color of the refresh icon.
     */
    public void restoreActionBar() {
        ActionBar aBar = getSupportActionBar();

        assert aBar != null;
        switch(mGameTitle){
            case KW:
                aBar.setTitle(getString(R.string.KanesWrath));
                setBarColors(R.color.kw_red);
                break;
            case CnC3:
                aBar.setTitle(getString(R.string.CandC3));
                setBarColors(R.color.cnc3_green);
                break;
            case Generals:
                aBar.setTitle(getString(R.string.Generals));
                setBarColors(R.color.generals_yellow);
                break;
            case ZH:
                aBar.setTitle(getString(R.string.ZeroHour));
                setBarColors(R.color.zh_orange);
                break;
            case RA3:
                aBar.setTitle(getString(R.string.RedAlert3));
                setBarColors(R.color.ra3_red);
                break;
        }
        mNavigationDrawerFragment.updateHeader(mGameTitle);
    }

    /**
     * Sets the color of the status bar and action bar.
     * Helper method for restoreActionBar()
     * @param colorResourceId The color id that the bars will be set to.
     */
    private void setBarColors(int colorResourceId){
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        //noinspection deprecation
        findViewById(R.id.top_toolbar).setBackgroundColor(getResources().getColor(colorResourceId));
        //noinspection deprecation
        findViewById(R.id.sliding_tabs).setBackgroundColor( getResources().getColor(colorResourceId));
        mSwipeRefreshLayout.setColorSchemeResources(colorResourceId);
        bottomToolbar.setBackgroundColor(getResources().getColor(colorResourceId));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.setStatusBarColor(getResources().getColor(colorResourceId));
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
            if (fragment.getArguments() == null) {
                fragment.setArguments(args);
            }
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
                default:
                    return "error".toUpperCase(l);
            }
        }
    }

    /**
     * Returns the string used to query individual games.
     * This string will be changed based upon the selection in the nav-drawer.
     */
    public String getQueryJsonString(){
        return queryJsonString;
    }

    public SectionsPagerAdapter getSectionsPagerAdapter() {return mSectionsPagerAdapter;}


    @Override
    public void onRefresh() {
        if (mSwipeRefreshLayout.isEnabled()) {
            refreshAndUpdateViews();
        }
    }

    /**
     * Singleton wrapper method to help access the jsonHandler for calls that don't have access to the MainActivity
     */
    private void updateViews(){
        jsonHandler.updateViews(this);
    }

    /**
     * Singleton wrapper method to help access the jsonHandler for calls that don't have access to the MainActivity
     */
    private void refreshAndUpdateViews(){
        jsonHandler.refreshAndUpdateViews(this);
    }

    /**
     * Filters requests from the fragments in order to enable the refresh ability.
     * For some reason, all fragments check onScroll when you switch them.
     * So this method filters the requests to avoid the refreshLayout being set to true when it
     * should be false.
     *
     * @param isSafe bool indicating that it is ok to refresh true == yes, false == no
     * @param fragmentId The fragment index. -1 is generic, 0 is players, 1 is lobby, 2 is in progress.
     */
    public void setSafeToRefresh(boolean isSafe, int fragmentId){
        if (isSafe && (fragmentId == mViewPager.getCurrentItem() || fragmentId == -1)) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }

    }
}