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

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;

import msg.Msg;

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
        Msg msg;
        dev = remoteMessage.getNotification().getTitle();
        controller = remoteMessage.getNotification().getBody();

        Log.d("===", "device IP : " + dev + " controller : " + controller);

        if(dev == null || dev.equals("")){
            msg = new Msg("System",controller,null);
        }else{
            msg = new Msg("System", controller, dev);
        }
        sendMsg(msg);


    }

    public static String getToken(Context context) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty");
    }

    public void sendMsg(Msg msg) {
        String tid = msg.getTid();

        if(tid == null || tid.equals("")) {
            Sender sender =
                    new Sender(msg);
            sender.start();
        }else {
            Sender2 sender2 =
                    new Sender2(msg);

            sender2.start();
        }

    } // end sendMsg
    class Sender extends Thread{
        Msg msg;
        public Sender(Msg msg) {
            this.msg = msg;
        }
        @Override
        public void run() {

            Collection<ObjectOutputStream>
                    cols = MainActivity.maps.values();
            Iterator<ObjectOutputStream>
                    its = cols.iterator();
            while(its.hasNext()) {
                try {
                    its.next().writeObject(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class Sender2 extends Thread{
        Msg msg;
        public Sender2(Msg msg) {
            this.msg = msg;
        }
        @Override
        public void run() {
            String tid = msg.getTid();
            try {
                Collection<String>
                        col = MainActivity.ids.keySet();
                Iterator<String> it = col.iterator();
                String sip = "";
                while(it.hasNext()) {
                    String key = it.next();
                    if(MainActivity.ids.get(key).equals(tid)) {
                        sip = key;
                    }
                }
                System.out.println(sip);
                MainActivity.maps.get(sip).writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
