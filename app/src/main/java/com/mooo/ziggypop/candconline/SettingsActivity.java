package com.mooo.ziggypop.candconline;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by ziggypop on 4/17/15.
 * Contains settings
 */
public class SettingsActivity extends AppCompatActivity{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        mToolBar.setTitle(R.string.settings);
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        // set the color of the toolbar based on the current game.
        int current_game = getIntent().getExtras().getInt("current_game");
        switch (current_game) {
            case 1:
                mToolBar.setBackgroundResource(R.color.kw_red);
                break;
            case 2:
                mToolBar.setBackgroundResource(R.color.cnc3_green);
                break;
            case 3:
                mToolBar.setBackgroundResource(R.color.generals_yellow);
                break;
            case 4:
                mToolBar.setBackgroundResource(R.color.zh_orange);
                break;
            case 5:
                mToolBar.setBackgroundResource(R.color.ra3_red);
                break;
        }

        //swap in the settings fragment
        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingsFragment()).commit();

    }


    public static class SettingsFragment extends PreferenceFragment{
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


            // Show the Licence
            Preference licencePreference = getPreferenceScreen().findPreference("licence_preference");
            licencePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.licence);
                    builder.setMessage(R.string.BSD);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) { } // do nothing.
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            // Reset the DB.
            Preference resetDBPref = getPreferenceScreen().findPreference("reset_db_pref");
            resetDBPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Reset Player Database");
                    builder.setMessage(getActivity().getString(R.string.reset_db_disclaimer));
                    builder.setPositiveButton(getActivity().getText(R.string.ok),
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // delete the database.
                            PlayerDatabaseHandler db = new PlayerDatabaseHandler(getActivity().getApplicationContext());
                            db.resetDB(db.getReadableDatabase());
                        }
                    });
                    builder.setNegativeButton(getActivity().getText(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) { } // do nothing, do not delete the DB.
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });
        }
    }
}
