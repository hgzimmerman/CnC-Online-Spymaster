package com.mooo.ziggypop.candconline;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ziggypop on 12/31/15.
 * Activity to show all players that have been added to the database.
 */
public class PlayerDatabaseViewerActivity extends AppCompatActivity{
    public static final String TAG = "DatabaseViewerActivity";

    PlayerDatabaseHandler dbHandler;
    int current_game;
    Toolbar toolbar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_viewer_activity);

        dbHandler = new PlayerDatabaseHandler(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.db_toolbar);
        toolbar.setTitle("Manage Database");
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        setSupportActionBar(toolbar);
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

}
