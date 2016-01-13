package com.mooo.ziggypop.candconline;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ziggypop on 1/13/16.
 * Activity for viewing stats.
 */
public class StatsViewerActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener{

    Toolbar toolbar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ArrayList<PlayerStats> playerStats;

    PlayerStats.StatsFragment statsFragment;

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


        statsFragment = new PlayerStats.StatsFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.db_players_fragment, statsFragment);
        ft.commit();

        statsFragment.setData(playerStats, this);



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
    public void onResume(){
        super.onResume();
        onRefresh();
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



    @Override
    public void onRefresh() {
        //Collections.sort(playerStats);
        statsFragment.setData(playerStats, this);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
