package com.mooo.ziggypop.candconline;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ziggypop on 1/2/16.
 */
public class NotificationMessage extends BroadcastReceiver {
    public static final String TAG = "NotificationMessage";


    @Override
    public void onReceive(Context context, Intent intent) {
        new JsonHandler.ServiceJsonGetter(context).execute();
    }

    public static void showNotification(Context context, ArrayList<Player> players) {
        Log.v(TAG, "showing the notification");

        NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        String contentText = new String();
        for (Player player: players){
            contentText.concat(player.getNickname() + " ,");
        }

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(context)
                .setSmallIcon(R.mipmap.ic_stat_gdi_original_transparent)  // the status icon
                .setTicker("Players Online")  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(context.getText(R.string.friends_online_service_label))  // the label of the entry
                .setContentText(contentText)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(R.string.friends_online_service_label, notification);
    }
}
