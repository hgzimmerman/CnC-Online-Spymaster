package com.mooo.ziggypop.candconline;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ziggypop on 12/31/15.
 */
public class PlayerDatabaseViewerActivity extends AppCompatActivity{
    public static final String TAG = "DatabaseViewerActivity";

    PlayerDatabaseHandler dbHandler;
    int current_game;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_viewer_activity);

        dbHandler = new PlayerDatabaseHandler(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.db_toolbar);
        toolbar.setTitle("Manage Database");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        current_game = getIntent().getExtras().getInt("current_game");
        switch (current_game) {
            case 1:
                toolbar.setBackgroundResource(R.color.kw_red);
                break;
            case 2:
                toolbar.setBackgroundResource(R.color.cnc3_green);
                break;
            case 3:
                toolbar.setBackgroundResource(R.color.generals_yellow);
                break;
            case 4:
                toolbar.setBackgroundResource(R.color.zh_orange);
                break;
            case 5:
                toolbar.setBackgroundResource(R.color.ra3_red);
                break;
        }




        Player.PlayersFragment playerFragment = new Player.PlayersFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.db_players_fragment, playerFragment);
        ft.commit();

        ArrayList<Player> players = dbHandler.getAllPlayers();

        Collections.sort(players);
        Log.v(TAG, players.get(0).getNickname());
        playerFragment.refreshData(players, this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("current_game", current_game);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
