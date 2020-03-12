package com.example.tabserver;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("===", "Refreshed token: " + token);

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("===", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = "Instanced ID : " + token;
                        Log.d("===", msg);
                        Toast.makeText(MyFirebaseMessagingService.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String dev;
        String controller;

        dev = remoteMessage.getNotification().getTitle();
        controller = remoteMessage.getNotification().getBody();

        Log.d("===", "device : " + dev + " controller : " + controller);


    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }


}
