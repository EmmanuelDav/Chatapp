package com.rad5.chatapp.Notification;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;



public class FCMSERVICE extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMess";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        token(s);
    }

    private void token(String s) {
        Log.d(TAG, "TokenFromService" + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String notificationBody = "";
        String notificationTitle ="";
        String notificationdata = "";

        try{
            notificationdata = remoteMessage.getData().toString();
            notificationBody = remoteMessage.getNotification().getBody();
            notificationTitle = remoteMessage.getNotification().getTitle();

        }catch (NullPointerException exception){

            Log.d(TAG,"service error" + exception.getMessage());

        }
        Log.d(TAG,"NotificationBody :" + notificationBody);
        Log.d(TAG,"NotificationData :" + notificationdata);
        Log.d(TAG," notification text:" + notificationTitle);

    }
}
