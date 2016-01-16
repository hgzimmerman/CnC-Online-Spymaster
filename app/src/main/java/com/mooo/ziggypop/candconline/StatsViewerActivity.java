package com.mooo.ziggypop.candconline;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

/**
 * Created by ziggypop on 1/13/16.
 * Activity for viewing stats.
 */
public class StatsViewerActivity extends AppCompatActivity {

    Toolbar toolbar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<PlayerStats> playerStats;
    ArrayList<LadderStats> ladderStats;

    PlayerStats.StatsFragment statsFragment;
    LadderStats.LadderStatsFragment ladderStatsFragment;



    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_viewer_activity);

        toolbar = (Toolbar) findViewById(R.id.db_toolbar);
        toolbar.setTitle("Stats");
        Drawable backButton = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.my_abc_ic_ab_back_mtrl_am_alpha);
        backButton.setColorFilter(getResources().getColor(R.color.black), PorterDuff.Mode.MULTIPLY);
        toolbar.setNavigationIcon(backButton);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        playerStats = getIntent().getExtras().getParcelableArrayList("statsPlayers");
        ladderStats = getIntent().getExtras().getParcelableArrayList("statsLadder");

        //Set up the activity depending on which stats type was sent
        if (playerStats != null) {
            statsFragment = new PlayerStats.StatsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.db_players_fragment, statsFragment);
            ft.commit();
            //set title
            String playerName = getIntent().getExtras().getString("player_name");
            String playerStatsString = getString(R.string.player_stats);
            String forPreposition = getString(R.string.preposition_for);
            String title = String.format("%s %s %s",
                    playerStatsString,
                    forPreposition,
                    playerName
            );
            toolbar.setTitle(title);

            statsFragment.setData(playerStats, this);
        } else if (ladderStats != null){
            ladderStatsFragment = new LadderStats.LadderStatsFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.db_players_fragment, ladderStatsFragment);
            ft.commit();
            //set title
            String playerName = getIntent().getExtras().getString("player_name");
            String ladderStatsString = getString(R.string.ladder_stats);
            String forPreposition = getString(R.string.preposition_for);
            String title = String.format("%s %s %s",
                    ladderStatsString,
                    forPreposition,
                    playerName
            );
            toolbar.setTitle(title);

            ladderStatsFragment.setData(ladderStats, this);
        }


        // set the color of the toolbar based on the current game.
        switch (getIntent().getExtras().getInt("current_game")) {
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

        // Disable the refresh layout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.db_swipe_refresh_layout);
        mSwipeRefreshLayout.setEnabled(false);

    }

    @Override
    public void onResume(){
        super.onResume();
        if (playerStats != null){
            statsFragment.setData(playerStats, this);
        } else if (ladderStats != null){
            ladderStatsFragment.setData(ladderStats, this);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked
                finish();
                return true;
            default:
                return true;
        }
    }


    /**
     * Sets the actionbar and the status bar (if the build allows it) to the provided color id.
     * @param colorResourceId The id of the color to which the bars will be set.
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
        //Set the status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.setStatusBarColor(getResources().getColor(colorResourceId));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            window.setStatusBarColor(getResources().getColor(colorResourceId, getTheme()));
    }

}
