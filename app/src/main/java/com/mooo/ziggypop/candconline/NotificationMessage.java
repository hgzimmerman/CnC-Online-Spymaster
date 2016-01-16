package com.mooo.ziggypop.candconline;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;

import java.util.ArrayList;

/**
 * Created by ziggypop on 1/2/16.
 */
public class NotificationMessage extends BroadcastReceiver {
    public static final String TAG = "NotificationMessage";
    private static final String ACTION_DISMISS = "Dismiss";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Intent received " + intent.getAction());
        new JsonHandler.ServiceJsonGetter(context).execute();
    }

    public static void showNotification(Context context, ArrayList<Player> players) {
        Log.d(TAG, "Attempting to show the notification");

        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, iFilter);

        int status;
        if (batteryStatus != null) {
            status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        } else {
            status = BatteryManager.BATTERY_STATUS_CHARGING; // Fail to a working state
            // TODO: Low priority: find a way to notify the user that this has failed
        }

        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        boolean userMustBeCharging = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("notify_if_charging_pref", false);

        //  If the flag is set, the user must be charging
        if (!(userMustBeCharging && !isCharging)) {
            Log.d(TAG, "The device is charging or the device is allowed to show notifications if not charging");

            NotificationManagerCompat mNM = NotificationManagerCompat.from(context);

            Intent dismissIntent = new Intent(context, NotificationMessage.class);
            dismissIntent.setAction(NotificationMessage.ACTION_DISMISS);


            // The PendingIntent to launch our activity if the user selects this notification
            PendingIntent appIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

            String contentText = new String();
            String separator = ",  ";

            boolean userMustBeOnline = PreferenceManager.getDefaultSharedPreferences(context)
                    .getBoolean("notify_if_online_pref", false);
            boolean userIsOnline = false;
            for (Player player : players) {
                contentText += player.getNickname() + separator;
                if (player.getIsYourself())
                    userIsOnline = true;
            }

            //If there are players and if the user must be online, the user is online
            if (players.size() > 0 && !(userMustBeOnline && !userIsOnline)) {
                Log.d(TAG, "Actually showing the notification: userMustBeOnLine"+ userMustBeOnline + "userIsOnline" + userIsOnline);

                contentText = contentText.substring(0, contentText.length() - separator.length());


                NotificationCompat.WearableExtender wearableExtender =
                        new WearableExtender()
                                .setBackground(BitmapFactory.decodeResource(context.getResources(),
                                        R.drawable.gdi_wear_640x400));


                // Set the info for the views that show in the notification panel.
                Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_stat_gdi_cropped)  // the status icon
                        .setTicker("CnC Players Online")  // the status text
                        .setWhen(System.currentTimeMillis())  // the time stamp
                        .setContentTitle(context.getText(R.string.friends_online_service_label))  // the label of the entry
                        .setContentText(contentText)  // the contents of the entry
                        .setContentIntent(appIntent)  // The intent to send when the entry is clicked
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
                        .extend(wearableExtender)
                        .build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;

                // Send the notification.
                mNM.notify(R.string.friends_online_service_label, notification);
            }
        }
    }
}
