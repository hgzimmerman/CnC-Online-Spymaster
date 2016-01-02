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
    private static final String ACTION_DISMISS = "Dismiss";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "Intent received " + intent.getAction());
        if (intent.getAction() != null && intent.getAction().equals(ACTION_DISMISS)){
            Log.v(TAG, "Dismissing Notification");
            NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNM.cancel(R.string.friends_online_service_label);
        }else {
            new JsonHandler.ServiceJsonGetter(context).execute();
        }
    }

    public static void showNotification(Context context, ArrayList<Player> players) {
        Log.v(TAG, "showing the notification");

        NotificationManager mNM = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent dismissIntent = new Intent(context, NotificationMessage.class);
        dismissIntent.setAction(NotificationMessage.ACTION_DISMISS);
        PendingIntent piDismiss = PendingIntent.getService(context, 0, dismissIntent, 0);
        PendingIntent piD = PendingIntent.getActivity(context, 0, new Intent(context.getApplicationContext(), NotificationMessage.class).setAction(ACTION_DISMISS),0);


        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent appIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

        String contentText = new String();
        for (Player player: players){
            contentText += player.getNickname()+ ", ";
        }
        if (players.size() > 0 ) {

            contentText = contentText.substring(0, contentText.length()-2);

            // Set the info for the views that show in the notification panel.
            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.mipmap.ic_stat_gdi_original_transparent)  // the status icon
                    .setTicker("Players Online")  // the status text
                    .setWhen(System.currentTimeMillis())  // the time stamp
                    .setContentTitle(context.getText(R.string.friends_online_service_label))  // the label of the entry
                    .setContentText(contentText)  // the contents of the entry
                    .setContentIntent(appIntent)  // The intent to send when the entry is clicked
                    .setStyle(new Notification.BigTextStyle().bigText(contentText))
                    //.addAction(R.mipmap.ic_stat_gdi_original_transparent, "Dismiss", piD)
                    //.addAction(R.mipmap.ic_stat_gdi_original_transparent, "Launch App", appIntent)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;

            // Send the notification.
            mNM.notify(R.string.friends_online_service_label, notification);
        }
    }
}
