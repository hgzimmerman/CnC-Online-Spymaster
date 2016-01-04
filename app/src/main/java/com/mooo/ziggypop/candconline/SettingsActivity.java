package com.mooo.ziggypop.candconline;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.Preference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.content.SharedPreferences;

import java.util.prefs.Preferences;

/**
 * Created by ziggypop on 4/17/15.
 * Contains settings
 */
public class SettingsActivity extends AppCompatActivity{

    public static final String TAG = "SettingsActivity";
    static public int current_game;
    Toolbar mToolBar;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        mToolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        mToolBar.setTitle(R.string.settings);
        mToolBar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(mToolBar);
        assert getSupportActionBar() != null; // I'm only doing this to make the IDE stop yelling at me, I know its not null, I just set it...
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);




        if (getIntent().getExtras() != null ) {
            // set the color of the toolbar based on the current game.
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
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Log.v(TAG, "Current interval = " + preferences.getString(getString(R.string.time_interval_pref), "15"));

        //swap in the settings fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingsFragment()).commit();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // go to previous screen when app icon in action bar is clicked
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("current_game", current_game);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * The fragment that is inside of the activity.
     */
    public static class SettingsFragment extends PreferenceFragmentCompat {
        protected SharedPreferences.OnSharedPreferenceChangeListener mListener;



        @Override
        public void onCreatePreferences(Bundle bundle, String s) {
            addPreferencesFromResource(R.xml.preferences);
        }

        /**
         * Sets up all of the preferences.
         * @param savedInstanceState The bundle!
         */
        public void onCreate(Bundle savedInstanceState) {
            getActivity().setTheme(R.style.AppTheme);
            super.onCreate(savedInstanceState);

            // Show the Licence
            Preference licencePreference = getPreferenceScreen().findPreference(getString(R.string.licence_pref));
            licencePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.licence);
                    String divider = "\n\n=====================\n";
                    String licenseString = getString(R.string.BSD)
                            + divider + getString(R.string.AndroidAssetStudio)
                            + divider + getString(R.string.AndroidIcons);
                    builder.setMessage(licenseString);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) { } // do nothing.
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
            });

            // get pref early so receiveNotifications can interact with it and change its state.

            // Receive notifications
            Preference receiveNotifications = getPreferenceScreen().findPreference(getString(R.string.receive_notifications_pref));
            receiveNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isActive = (boolean) newValue;
                    if (isActive) {
                        Log.v(TAG, "Enable notification alarm");
                        //Start Alarm (will be enabled at boot. Will stay on until disabled)
                        ComponentName receiver =
                                new ComponentName(getContext(), AlarmArmingBootReceiver.class);
                        PackageManager pm = getContext().getPackageManager();

                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                        AlarmArmingBootReceiver.setAlarm(getContext());
                    } else {
                        Log.v(TAG, "Disable notification alarm");
                        ComponentName receiver = new ComponentName(getContext(), AlarmArmingBootReceiver.class);
                        PackageManager pm = getContext().getPackageManager();

                        pm.setComponentEnabledSetting(receiver,
                                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                PackageManager.DONT_KILL_APP);
                        AlarmArmingBootReceiver.stopAlarm(getContext());
                    }
                    return true;
                }
            });

            //Notification if you are online.
            Preference notifyIfOnline = getPreferenceScreen().findPreference(getString(R.string.notify_if_online_pref));
            notifyIfOnline.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    return true;
                }
            });



            //Notification interval
            TimeIntervalPreference notifyIntervalPref =
                    (TimeIntervalPreference) getPreferenceScreen().findPreference(getString(R.string.time_interval_pref));
            //Todo: this doesn't seem to fire, investigate why that is.
            notifyIntervalPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    // For some reason, this is never called, so I implemented what SHOULD be done here
                    // In TimeIntervalPreference.doActionOnItemSelected, which is called from
                    // the abstract SpinnerPreference.OnItemSelected.
                    return true;
                }
            });


            // Manage DB friends
            Preference manageFriends = getPreferenceScreen().findPreference(getString(R.string.manage_friends_pref));
            manageFriends.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    // launch an activity to show all players in the database (regardless of their online status)
                    Intent intent = new Intent(getContext().getApplicationContext(), PlayerDatabaseViewerActivity.class);
                    intent.putExtra("current_game", current_game);
                    getActivity().startActivity(intent);
                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mListener);
        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mListener);
            super.onPause();
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    //Preferences.sync(getPreferenceManager(), key);
                    if (key.equals(getActivity().getString(R.string.time_interval_pref))) {
                        //AppUtils.restart(getActivity());
                        Log.v(TAG, "Shared prefs changed time interval");
                        AlarmArmingBootReceiver.setAlarm(getContext());
                    }
                }
            };
        }
    }


    /**
     * Sets the actionbar and the status bar (if the build allows it) to the provided color id.
     * @param colorResourceId The id of the color to which the bars will be set.
     */
    private void setBarColors(int colorResourceId){
        // Set the action bar.
        mToolBar.setBackgroundResource(colorResourceId);
        //Set the status bar.
        Window window = getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.setStatusBarColor(getResources().getColor(colorResourceId, getTheme()));
            } else{
                window.setStatusBarColor(getResources().getColor(colorResourceId));
            }
    }
}