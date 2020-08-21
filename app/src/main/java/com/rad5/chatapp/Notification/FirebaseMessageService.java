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
import com.rad5.chatapp.Utils.ChannelId;

import java.util.Map;


public class FirebaseMessageService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "FirebaseMess";
    private static final int BROADCAST_NOTIFICATION_ID = 1;
    private static  final String mNotificationId=  "app_notification_channel";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        final Map<String, String> data = remoteMessage.getData();
        String NotiMessage = data.get("message");
        String NotifiSender = data.get("sendersName");
        sendBroadcastNotification(NotifiSender, NotiMessage);
    }


    private void sendBroadcastNotification(String title, String message) {
        Log.d(TAG, "sendBroadcastNotification: building an admin broadcast notification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ChannelId.CHANNEL_ID);
        Intent notifyIntent = new Intent(this, MessageActivity.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        notifyIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
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


}
