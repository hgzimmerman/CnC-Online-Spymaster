package com.mooo.ziggypop.candconline;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by ziggypop on 1/2/16.
 * set an alarm.
 */
public class AlarmArmingBootReceiver extends BroadcastReceiver {

    private static final int ALARM_ID = 12027;
    private static final String TAG = "AlarmBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            setAlarm(context, false);
        }
    }

    public static void setAlarm(Context context, boolean activateIfTrue){
        Log.v(TAG, "Setting Alarm");
        AlarmManager alarmMgr =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationMessage.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int interval = Integer.parseInt(preferences.getString("time_interval_pref", "10"));

        if (preferences.getBoolean("receive_notifications_pref", false) || activateIfTrue) {
            alarmMgr.setInexactRepeating(AlarmManager.RTC,
                    SystemClock.elapsedRealtime(), interval * 60 * 1000, pi);
            Log.v(TAG, "Actually set Alarm");
        }
    }

    public static void stopAlarm(Context context){
        Log.v(TAG, "Stopping Alarm");
        AlarmManager alarmMgr =  (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, NotificationMessage.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, ALARM_ID, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmMgr.cancel(pi);
    }
}
