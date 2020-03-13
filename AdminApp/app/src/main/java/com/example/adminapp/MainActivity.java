package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    EditText tip,control;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        tip = findViewById(R.id.tip);
        control = findViewById(R.id.control);
        button = findViewById(R.id.button);

    }

    public void ckbt(View v){
        if(v.getId()==R.id.button){
            new SendServer(tip.getText().toString(),control.getText().toString()).start();
        }
    }


    class SendServer extends Thread {

        String urlstr = "http://70.12.113.200/AdminWeb/webapp.mc";

        public SendServer(String id, String txt) {
            urlstr += "?ip=" + id + "&txt=" + txt;
        }

        @Override
        public void run() {
            try {
                URL url = new URL(urlstr);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.getInputStream();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
