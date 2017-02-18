package com.android.jdrd.headcontrol.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.android.jdrd.headcontrol.util.Constant;
import com.android.jdrd.headcontrol.util.Contact;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketUtil extends Service {

    private static ServerSocket serverSocket;
    private static Socket socket;
    private static InputStream in = null;
    private static OutputStream out = null;
    private static String msg = null;
    public static Intent intent;
    private MyReceiver receiver;
    IntentFilter filter;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("hehe", "oncreate执行了");
        intent = new Intent();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startServerSocket(Constant.ServerPort);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        receiver=new MyReceiver();
        filter=new IntentFilter();
        filter.addAction("android.intent.action.searchPeople");
        registerReceiver(receiver, filter);


    }


    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String camera = intent.getStringExtra("camera");
            if (out != null) {
                try {
                    sendDateToClient(camera);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public String startServerSocket(int port) throws IOException {

        serverSocket = new ServerSocket(port);


        while (true) {
            socket = serverSocket.accept();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        in = socket.getInputStream();
                        out = socket.getOutputStream();

                        byte[] buffer = new byte[1024];

                        while (true) {
                            int len = 0;
                            len = in.read(buffer);

                            if (len == -1) {
                                break;
                            }
                            msg = new String(buffer, 0, len);

                            if (msg != null) {
                                Log.e("hehe", msg.toString());
                            } else {
                                Log.e("hehe", "msg为空");
                            }


                            Contact.debugLog("msg = "+msg.toString());

                            intent.putExtra("msg", msg);
                            intent.setAction("com.jdrd.fragment.Map");
                            sendBroadcast(intent);

                        }

                        socket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            return msg;


        }
    }


    public static void sendDateToClient(String str) throws IOException {
        str = "*" + str + "#";
        out.write(str.getBytes());
    }

    public static String getJSONString(String str) {
        JSONObject object = null;
        String jsonString = null;
        try {
            object = new JSONObject(msg);
            jsonString = object.getString("str");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
