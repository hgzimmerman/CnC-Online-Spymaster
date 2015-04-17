package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    public ActionBarDrawerToggle mDrawerToggle;

    public DrawerLayout mDrawerLayout;
    public ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition ; //skip the header picture
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    private ArrayList<DrawerItem> gameNames;
    private ImageView imageHeaderView;
    private View settingsView;
    private DrawerAdapter mAdapter;


    private Bitmap kw_bitmap;
    private Bitmap cnc3_bitmap;
    private Bitmap generals_bitmap;
    private Bitmap zh_bitmap;
    private Bitmap ra3_bitmap;


    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        Log.v("DEFAULT_GAME", sp.getString("default_game", "1"));
        mCurrentSelectedPosition = Integer.parseInt(sp.getString("default_game", "1"));
        mCurrentSelectedPosition = 1;


        kw_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.kw_cropped);
        //cnc3_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cnc3_cropped);
        //generals_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.generals_crop);
        //zh_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.zh_box_art_crop);
        //ra3_bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.red_alert_crop);



        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gameNames = new ArrayList<>();
        gameNames.add(new DrawerItem(getString(R.string.KanesWrath),0));
        gameNames.add(new DrawerItem(getString(R.string.CandC3),0));
        gameNames.add(new DrawerItem(getString(R.string.Generals),0));
        gameNames.add(new DrawerItem(getString(R.string.ZeroHour), 0));
        gameNames.add(new DrawerItem(getString(R.string.RedAlert3),0));


        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mAdapter =  new DrawerAdapter(
                getActivity().getApplicationContext(),
                android.R.layout.simple_list_item_activated_1,
                gameNames);

        //set the header to an imageView
        imageHeaderView = new ImageView(getActivity());
        imageHeaderView.setImageBitmap(BitmapFactory
                .decodeResource(getResources(), R.drawable.kw_cropped));
        imageHeaderView.setAdjustViewBounds(true); //This fixes a padding issue
        mDrawerListView.addHeaderView(imageHeaderView, null, true);
        //Add settings footer
        settingsView = inflater.inflate(R.layout.drawer_item_layout, mDrawerLayout);
        TextView settingsText = (TextView) settingsView.findViewById(R.id.drawer_game_title);
        settingsText.setText("Settings");
        settingsText.setTextColor(getResources().getColor(R.color.offwhite)); //I shouldn't have to do this TODO: fix this
        mDrawerListView.addFooterView(settingsView);

        mDrawerListView.setAdapter( mAdapter );
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        //ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        //actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                //R.mipmap.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.mipmap.ic_image_dehaze,
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                showGlobalContextActionBar();

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        //actionBar.setDisplayShowTitleEnabled(true);
        //actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }


    /*
     * Updates the names for the nav drawer to reflect the numbers of players currently online.
     */
    public void updateNames(ArrayList<Integer> numbers){
        Log.v("NAV_DRAWER", "UPDATING");
        for (int i = 0; i < numbers.size(); i++) {
            gameNames.get(i).updatePlayerCount(numbers.get(i));
        }

        mAdapter.notifyDataSetChanged();
    }

    /*
     * Sets the header in the NavDrawer to a picture based on the GameTitle.
     */
    public void updateHeader(GameTitle gameTitle){
        switch (gameTitle){
            case KW:    imageHeaderView.setImageBitmap(kw_bitmap);
                break;
            case CnC3:  imageHeaderView.setImageBitmap(
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.cnc3_cropped));
                break;
            case Generals: imageHeaderView.setImageBitmap(
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.generals_crop));
                break;
            case ZH:    imageHeaderView.setImageBitmap(
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.zh_box_art_crop));
                break;
            case RA3:   imageHeaderView.setImageBitmap(
                    BitmapFactory.decodeResource(getResources(),
                            R.drawable.red_alert_crop));
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    public enum GameTitle{
        KW,
        CnC3,
        Generals,
        ZH,
        RA3
    }
}
