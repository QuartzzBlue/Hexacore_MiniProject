package com.example.adminapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    TextView state;
    EditText FLid,ecuid,data;
    Button sendbt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        state = findViewById(R.id.state);
        sendbt = findViewById(R.id.sendbt);
        FLid = findViewById(R.id.flid);
        ecuid = findViewById(R.id.ecuid);
        data = findViewById(R.id.control);

        sendbt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(data.getText().toString().equals("0")||data.getText().toString().equals("1")){
                    new SendServer(FLid.getText().toString(),ecuid.getText().toString(),data.getText().toString()).start();
                }else {
                    Toast.makeText(MainActivity.this, "유효한 Controller 를 입력해 주세요!", Toast.LENGTH_SHORT).show();
                }


            }
        });


    }

    class SendServer extends Thread {

        //final String WebServer = "http://13.209.113.212:8080/";
        final String WebServer = "http://70.12.113.200/"; //Web Server 의 IP
        String urlstr = WebServer+"/AdminWeb/webapp.mc";
        URL url;
        HttpURLConnection con;

        public SendServer(){

        }

        public SendServer(String FLid,String ecuid, String data) {
            urlstr += "?FLid="+FLid+"&ecuid=" + ecuid + "&data=" + data;
        }

        @Override
        public void run() {
            try {
                url = new URL(urlstr);
                con = (HttpURLConnection) url.openConnection();

                if(con!=null) {
                    con.getInputStream();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            state.setText(WebServer+" 접속 성공!");
                        }
                    });
                }
            }catch(Exception e){
                int i =0;

                while(true){
                    i++;
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            state.setText(WebServer+"접속 실패! 다시 시도합니다 "+ finalI);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                        url = new URL(urlstr);
                        con = (HttpURLConnection) url.openConnection();
                        if( con!=null) {
                            con.getInputStream();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    state.setText(WebServer+" 접속 성공!");
                                }
                            });
                            break;
                        }
                    }catch(Exception e1){ }
                }

            }


        }
    }
}
