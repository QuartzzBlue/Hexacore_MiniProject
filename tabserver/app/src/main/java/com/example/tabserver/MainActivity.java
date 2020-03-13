package com.example.tabserver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import msg.Msg;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    TextView name1, name2, name3, name4,
            data1, data2, data3, data4,status;

    public static HashMap<String, ObjectOutputStream>
            maps = new HashMap<String,ObjectOutputStream>();
    public static HashMap<String, String>
            ids = new HashMap<String, String>();

    ArrayAdapter<String> adapter;

    ServerSocket serverSocket;
    boolean aflag = true;
    int port = 8888;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        makeUi();
        new serverReady().start();
    }

    class serverReady extends Thread{

        public serverReady(){
            try {
                serverSocket = new ServerSocket(port);
                Log.d("-----","Server Start..");
            } catch (IOException e) {

                Log.d("-----","Error..");            }
        }

        @Override
        public void run() {
            while(aflag) {
                Socket socket = null;

                Log.d("-----","Server Ready..");
                try {
                    socket = serverSocket.accept();
                    new Receiver(socket).start();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setList();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setList(){
        adapter =
                new ArrayAdapter<String>(
                        MainActivity.this,
                        android.R.layout.simple_list_item_1,
                        getIds()
                );
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
    }

    public ArrayList<String> getIds() {
        Collection<String>
                id = ids.values();
        Iterator<String> it = id.iterator();
        ArrayList<String> list = new ArrayList<String>();
        while(it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
    public void sendIp() {
        Set<String>
                keys = maps.keySet();
        Iterator<String>
                its = keys.iterator();
        ArrayList<String> list = new ArrayList<String>();
        while(its.hasNext()) {
            list.add(its.next());
        }
    }

    public void displayData(final Msg msg){

                if(msg.getId().equals("Device1")) {
                    name1.setText(msg.getId());
                    data1.setText(msg.getTxt());
                }
                if (msg.getId().equals("Device2")){
                    name2.setText(msg.getId());
                    data2.setText(msg.getTxt());
                }
                if (msg.getId().equals("Device3")){
                    name3.setText(msg.getId());
                    data3.setText(msg.getTxt());
                }
                if (msg.getId().equals("Device4")){
                    name4.setText(msg.getId());
                    data4.setText(msg.getTxt());
                }

        new SendServer(msg.getId(),msg.getTxt()).start();

    }

    class Receiver extends Thread{

        InputStream is;
        ObjectInputStream ois;

        OutputStream os;
        ObjectOutputStream oos;

        Socket socket;
        public Receiver(Socket socket) throws IOException {
            this.socket = socket;
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);

            os = socket.getOutputStream();
            oos = new ObjectOutputStream(os);
            maps.put(socket.getInetAddress().toString(),
                    oos);
            try {
                Msg msg = (Msg) ois.readObject();
                ids.put(socket.getInetAddress().toString(),
                        msg.getId());

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while(ois != null) {
                Msg msg = null;
                try {

                    msg = (Msg) ois.readObject();

                    final Msg finalMsg = msg;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(finalMsg.getTxt()!=null||finalMsg.getTxt()==""){
                                displayData(finalMsg);
                            }
                            }
                        });
                } catch (Exception e) {
                    maps.remove(
                            socket.getInetAddress().toString()
                    );

                    ids.remove(socket.getInetAddress().toString()
                    );
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setList();
                        }
                    });
                    break;
                }
            } // end while
            try {
                if(ois != null) {
                    ois.close();
                }
                if(socket != null) {
                    socket.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

    }

    class Sender extends Thread{
        Msg msg;
        public Sender(Msg msg) {
            this.msg = msg;
        }
        @Override
        public void run() {

            Collection<ObjectOutputStream>
                    cols = maps.values();
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
                        col = ids.keySet();
                Iterator<String> it = col.iterator();
                String sip = "";
                while(it.hasNext()) {
                    String key = it.next();
                    if(ids.get(key).equals(tid)) {
                        sip = key;
                    }
                }
                System.out.println(sip);
                maps.get(sip).writeObject(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendServer extends Thread {

        String urlstr = "http://70.12.113.200/AdminWeb/httpconnection.mc";

        public SendServer(String id, String txt) {
            urlstr += "?id=" + id + "&txt=" + txt;
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




    private void makeUi() {
        listView = findViewById(R.id.listView);
        name1 = findViewById(R.id.textView);
        name2 = findViewById(R.id.textView2);
        name3 = findViewById(R.id.textView3);
        name4 = findViewById(R.id.textView4);
        data1 = findViewById(R.id.textView5);
        data2 = findViewById(R.id.textView6);
        data3 = findViewById(R.id.textView7);
        data4 = findViewById(R.id.textView8);
        status = findViewById(R.id.textView9);
        new ConnectThread(sip,port,null).start();
        Log.d("------------","TCP/IP Connection start");
    }

    public void ckbt(View v){
        Msg msg = null;
        if(v.getId() == R.id.button){
            msg = new Msg("server","1",null);


        }else if(v.getId() == R.id.button2){
            msg = new Msg("server","0",null);

        }
        sendMsg(msg);

    }

    //------------------------Client---------------from TCP/IP Server////////////////////////////////

    String tabid = "tab1";
    String sip = "70.12.113.200"; //웹서버의 ip
    int sport = 8888;

    Socket ssocket;

//    @Override
//    protected void onStart() {
//        super.onStart();
//        new ConnectThread(sip,port,id).start();
//        Log.d("------------","start");
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        try {
//            ssocket.close();
//            Log.d("------------","close");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    class ConnectThread extends Thread{

        String ip;
        int port;
        String id;

        public ConnectThread(){

        }
        public ConnectThread(String ip, int port,String id){
            this.ip = ip;
            this.port = port;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                ssocket.setSoTimeout(2000); //서버가 죽어있는데 찾고잇어서.. 2초동안 못찾으면 Exception
                ssocket = new Socket(ip, port);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText(ip+"Connected \n"+status.getText());
                    }
                });

            }catch(Exception e) {
                int i = 0;
                while(true) {
                    i++;
                    final int finalI = i;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            status.setText(ip+"Retry... "+ finalI);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                        ssocket = new Socket(ip, port);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                status.setText(ip+"Connected \n"+status.getText());
                            }
                        });

                        break;
                    } catch (Exception e1) {
                        //e1.printStackTrace();
                    }
                }
            }
            try {

                SReceiver receiver = new SReceiver(ssocket);
                Log.d("---","SReceiver excute");
                receiver.execute();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } // end run

    }

    class SReceiver extends AsyncTask<Void,Msg,Void> {

        InputStream is;
        ObjectInputStream ois;

        public SReceiver(Socket socket) throws IOException {
            is = socket.getInputStream();
            ois = new ObjectInputStream(is);
            Log.d("---","SReceiver 생성자");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(ois != null) {
                Msg msg = null;
                try {
                    msg = (Msg) ois.readObject();

                    publishProgress(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    msg = new Msg("System",
                            "Server Die",null);
                    publishProgress(msg);
                    break;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                if(ois != null) {
                    ois.close();
                }
                if(ssocket != null) {
                    ssocket.close();
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Msg... values) {
            //문제가 생기면
         if(values[0].getId().equals("System")){
             if(ssocket != null){
                 try {
                     ssocket.close();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }

             new ConnectThread(sip,sport,null).start();
             return; // 리턴 하면 이 함수 종료
         }

         Msg msg = null;
         String state;
         String tid ;
         if (values[0].getTxt().equals("1")){
             state = "Started";
         }else if (values[0].getTxt().equals("0")) {
             state = "Stopped";
         }else{
             state = "Error";
         }

         String tip = values[0].getTid();
         tid = ids.get(tip);
         msg = new Msg("server",values[0].getTxt(),tid);
         sendMsg(msg);

         if(tid==null||tid.equals("")){
                 tid = "All";
         }
         status.setText(tid+" : "+state + "\n"+status.getText());

        }
    }







}
