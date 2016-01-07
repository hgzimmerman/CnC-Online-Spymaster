package com.mooo.ziggypop.candconline;

import android.content.DialogInterface;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.PorterDuff.Mode;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ziggypop on 12/31/15.
 * Activity to show all players that have been added to the database.
 */
public class PlayerDatabaseViewerActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener{

    public static final String TAG = "DatabaseViewerActivity";

    PlayerDatabaseHandler dbHandler;
    int current_game = 22;
    Toolbar toolbar;
    Player.PlayersFragment playerFragment;
    SwipeRefreshLayout mSwipeRefreshLayout;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_viewer_activity);

        dbHandler = new PlayerDatabaseHandler(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.db_toolbar);
        toolbar.setTitle("Manage Database");
        Drawable backButton = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.my_abc_ic_ab_back_mtrl_am_alpha);
        backButton.setColorFilter(getResources().getColor(R.color.black), Mode.MULTIPLY);
        toolbar.setNavigationIcon(backButton);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        current_game = getIntent().getExtras().getInt("current_game");
        switch (current_game) {
            case 1:
                setBarColors(R.color.kw_red);
                break;
            case 2:
                setBarColors(R.color.cnc3_green);
                break;
            case 3:
                setBarColors(R.color.generals_yellow);
                break;
            case 4:
                setBarColors(R.color.zh_orange);
                break;
            case 5:
                setBarColors(R.color.ra3_red);
                break;
        }


        playerFragment = new Player.PlayersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.db_players_fragment, playerFragment);
        ft.commit();

        ArrayList<Player> players = dbHandler.getAllPlayers();

        Collections.sort(players);
        playerFragment.refreshData(players, this);

        //set up swipe down to refresh
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.db_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                //do nothing
                }
        });
        mSwipeRefreshLayout.setProgressBackgroundColor(R.color.light_grey);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked
                finish();
                return true;
            case R.id.menu_help:
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
                helpBuilder.setTitle(R.string.help);
                helpBuilder.setMessage(getString(R.string.player_db_help_message));
                helpBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { } // do nothing
                });
                AlertDialog helpDialog = helpBuilder.create();
                helpDialog.show();

                return true;
            case R.id.menu_reset_db:
                // show dialog to destroy db
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.reset_player_db));
                builder.setMessage(getString(R.string.reset_db_disclaimer));
                builder.setPositiveButton(getText(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // delete the database.
                                PlayerDatabaseHandler db = new PlayerDatabaseHandler(getApplicationContext());
                                db.resetDB(db.getReadableDatabase());
                                //Exit the activity, to avoid a crash caused by updating (that is, if I refreshed the views).
                                finish();
                            }
                        });
                builder.setNegativeButton(getText(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { } // do nothing, do not delete the DB.
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, R.id.menu_help, Menu.NONE, R.string.help);
        menu.add(Menu.NONE, R.id.menu_reset_db, Menu.NONE, R.string.reset_player_db);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Sets the color of the Actionbar and status bar.
     * @param colorResourceId The Id of the color to which the bars will be set.
     */
    private void setBarColors(int colorResourceId){
        // Set the action bar.
        toolbar.setBackgroundResource(colorResourceId);
        //Set the status bar.
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.setStatusBarColor(getResources().getColor(colorResourceId));
    }


    @Override
    public void onRefresh() {
        ArrayList<Player> players = dbHandler.getAllPlayers();
        Collections.sort(players);
        playerFragment.refreshData(players, this);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void setSafeToRefresh(boolean isSafe){
        mSwipeRefreshLayout.setEnabled(isSafe);
    }

}
