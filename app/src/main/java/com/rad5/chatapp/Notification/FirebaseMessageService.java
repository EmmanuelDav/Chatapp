package com.rad5.chatapp.Notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;
import com.rad5.chatapp.Login;
import com.rad5.chatapp.MainActivity;
import com.rad5.chatapp.MessageActivity;
import com.rad5.chatapp.R;
import com.rad5.chatapp.Register;

import java.util.Map;


public class FirebaseMessageService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMess";
    private static final int BROADCAST_NOTIFICATION_iD = 2;
    private  static final int BROADCAST_NOTIFICATION_ID = 1;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);


    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        final Map<String,String> data = remoteMessage.getData();
        String NotiMessage = data.get("message");
        String NotifiSender = data.get("sendersName");
        Log.d(TAG,"NotifiSender = "+ NotifiSender + "  NotifiMessage =" + NotiMessage);
        sendBroadcastNotification(NotifiSender,NotiMessage);



    }


    private void sendBroadcastNotification(String title, String message) {
        Log.d(TAG, "sendBroadcastNotification: building an admin broadcast notification");

        // Instantiate a Builder object.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "NotificationId");
        // Creates an Intent for the Activity
        Intent notifyIntent = new Intent(this, MessageActivity.class);
        // Sets the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Creates the PendingIntent
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        //add properties to the builder
        builder.setSmallIcon(R.drawable.common_full_open_on_phone)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_all_out_black_24dp))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true);

        builder.setContentIntent(notifyPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(BROADCAST_NOTIFICATION_ID, builder.build());
    }




    private boolean isApplicationInForeground() {
        //check all the activities to see if any of them are running
        boolean isActivityRunning = MainActivity.isActivityRunning
                || Login.isActivityRunning
                || MessageActivity.isActivityRunning
                || Register.isActivityRunning;
        if (isActivityRunning) {
            Log.d(TAG, "isApplicationInForeground: application is in foreground.");
            return true;
        }
        Log.d(TAG, "isApplicationInForeground: application is in background or closed.");
        return false;
    }

}
