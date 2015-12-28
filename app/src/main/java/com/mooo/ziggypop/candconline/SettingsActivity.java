package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by ziggypop on 4/17/15.
 * Contains settings
 */
public class SettingsActivity extends AppCompatActivity{





    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        mToolBar.setTitle("Settings");
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);


        mToolBar.setBackgroundResource(R.color.grey_blue);



        getFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingsFragment()).commit();



    }


    public static class SettingsFragment extends PreferenceFragment{
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);


            Preference dialogPref = getPreferenceScreen().findPreference("dialog_preference");
            dialogPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.licence);
                    builder.setMessage(R.string.BSD);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });


        }


    }
}
