package com.android.jdrd.robotclient.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.jdrd.robotclient.R;
import com.android.jdrd.robotclient.helper.RobotDBHelper;
import com.android.jdrd.robotclient.util.Constant;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientSocketUtil extends Service {

    private Context mContext;
    private String TAG = "TcpClient";
    private RobotDBHelper robotDBHelper;
    public static Intent intent;
    private String rcvMsg,end_cache,top_cache;
    private MyReceiver receiver;
    private static PrintWriter pw;
    private InputStream is;
    private String  serverIP = "192.168.88.191";
    private int serverPort = 8839;
    private Socket socket = null;
    IntentFilter filter;
    private String function;
    private DataInputStream dis;
    private boolean isRun = true;
    private int rcvLen;
    byte buff[]  = new byte[4096];

    @Override
    public void onCreate() {
        super.onCreate();
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        intent = new Intent();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startServerSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        receiver = new MyReceiver();
        filter = new IntentFilter();
        filter.addAction("com.jdrd.activity.Main");
        registerReceiver(receiver, filter);
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String camera = intent.getStringExtra("camera");
        }
    }

    public static void sendRobot(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(pw !=null) {
                    Gson gson = new Gson();
                    Map map = new LinkedHashMap();
                    map.put("function", "robot");
                    String string = gson.toJson(map);
                    pw.println("*" + string + "#");
                    pw.flush();
                }
            }
        }).start();
    }
    public static void sendType(final String robotip, final int type){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(pw !=null) {
                    Gson gson = new Gson();
                    Map ma = new LinkedHashMap();
                    ma.put("robotip", robotip);
                    ma.put("deskid", type);
                    Map map = new LinkedHashMap();
                    map.put("function", "command");
                    map.put("data", ma);
                    String string = gson.toJson(map);
                    pw.println("*" + string + "#");
                    pw.flush();
                }
            }
        }).start();
    }
    public static void sendCommand(final String robotip, final int deskid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(pw !=null) {
                    Gson gson = new Gson();
                    Map ma = new LinkedHashMap();
                    ma.put("robotip", robotip);
                    ma.put("deskid", deskid);
                    Map map = new LinkedHashMap();
                    map.put("function", "command");
                    map.put("data", ma);
                    String string = gson.toJson(map);
                    pw.println("*" + string + "#");
                    pw.flush();
                }
            }
        }).start();
    }
    public static void sendDesk(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(pw !=null){
                    Gson gson = new Gson();
                    Map map = new LinkedHashMap();
                    map.put("function", "desk");
                    String string = gson.toJson(map);
                    pw.println("*"+string+"#");
                    pw.flush();
                }
            }
        }).start();
    }
    public static void sendArea(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(pw !=null) {
                    Gson gson = new Gson();
                    Map map = new LinkedHashMap();
                    map.put("function", "area");
                    String string = gson.toJson(map);
                    pw.println("*" + string + "#");
                    pw.flush();
                }
            }
        }).start();
    }

    public void startServerSocket() throws IOException {
        //提升service进程优先级
        setServiceForeground();
        try {
            socket = new Socket(serverIP, serverPort);
//            socket.setSoTimeout(5000);
            pw = new PrintWriter(socket.getOutputStream(), true);
            is = socket.getInputStream();
            dis = new DataInputStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (isRun) {
            try {
                if(dis!=null) {
                    rcvLen = dis.read(buff);
                    Log.e("buff", "rcvLen");
                    if (rcvLen > 2) {
                        rcvMsg = new String(buff, 0, rcvLen, "utf-8");
                        if (buff != null) {
                            Constant.debugLog("buff" + buff[0] + " " + buff[rcvLen]);
                        }
                        Constant.debugLog("run: 收到消息:" + rcvMsg);
                        if (rcvMsg != null) {
                            top_cache = new String(rcvMsg.getBytes(), 0, 1, "utf-8");
                            Constant.debugLog("rcvMsg.length()"+rcvMsg.length());
                            end_cache = new String(buff, rcvLen-1, rcvLen, "utf-8");
                            end_cache = new String(end_cache.getBytes(), 0,1, "utf-8");
                            if ((("*").equals(top_cache))&& (("#").equals(end_cache))){
                                if ("heartbeat".equals(rcvMsg)) {
                                } else {
                                    rcvMsg = new String(rcvMsg.getBytes(),1,rcvLen -1, "utf-8");
                                    JSONObject object;
                                    if ("robot".equals(getJSONString(rcvMsg, "function"))) {
                                        try {
                                            object = new JSONObject(rcvMsg);
                                            JSONArray nameList = object.getJSONArray("data");
                                            Log.e("robot", "" + nameList.toString());
                                            if (nameList != null && nameList.length() > 0) {
                                                robotDBHelper.execSQL("delete from robot");
                                                for (int i = 0; i < nameList.length(); i++) {//遍历JSONArray
                                                    JSONObject oj = nameList.getJSONObject(i);
                                                    robotDBHelper.execSQL("insert into  robot (id,name,ip,state,outline,electric,robotstate,obstacle," +
                                                            "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation,area) values " +
                                                            "('" + oj.getString("id") + "','" + oj.getString("name") + "','" + oj.getString("ip") + "','" + oj.getString("state") + "','" + oj.getString("outline") + "'" +
                                                            ",'" + oj.getString("electric") + "','" + oj.getString("robotstate") + "','" + oj.getString("obstacle") + "','" + oj.getString("commandnum")
                                                            + "','" + oj.getString("excute") + "','" + oj.getString("excutetime") + "','" + oj.getString("commandstate") + "','" + oj.getString("lastcommandstate")
                                                            + "','" + oj.getString("lastlocation") + "','" + oj.getString("area") + "')");
                                                }
                                            }
                                            if (pw != null) {
                                                pw.println("heartbeat");
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        sendBroadcastMain("robot");
                                    } else if ("area".equals(getJSONString(rcvMsg, "function"))) {
                                        try {
                                            object = new JSONObject(rcvMsg);
                                            JSONArray nameList = object.getJSONArray("data");
                                            Log.e("area", "" + nameList.toString());
                                            if (nameList != null && nameList.length() > 0) {
                                                robotDBHelper.execSQL("delete from area");
                                                for (int i = 0; i < nameList.length(); i++) {//遍历JSONArray
                                                    JSONObject oj = nameList.getJSONObject(i);
                                                    robotDBHelper.execSQL("insert into  area (id,name) values " +
                                                            "('" + oj.getString("id") + "','" + oj.getString("name") + "')");
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        sendBroadcastMain("area");
                                    } else if ("desk".equals(getJSONString(rcvMsg, "function"))) {
                                        try {
                                            object = new JSONObject(rcvMsg);
                                            JSONArray nameList = object.getJSONArray("data");
                                            if (nameList != null && nameList.length() > 0) {
                                                robotDBHelper.execSQL("delete from desk");
                                                for (int i = 0; i < nameList.length(); i++) {//遍历JSONArray
                                                    JSONObject oj = nameList.getJSONObject(i);
                                                    Log.e("desk", "" + oj.getString("name"));
                                                    robotDBHelper.execSQL("insert into  desk (id,name,area) values " +
                                                            "('" + oj.getString("id") + "','" + oj.getString("name") + "','" + oj.getString("area") + "')");
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        sendBroadcastMain("desk");
                                    }
                                }
                            }
                        }
                        if (rcvMsg.equals("QuitClient")) {   //服务器要求客户端结束
                            isRun = false;
                            startServerSocket();
                        }
                    } else {
                        isRun = false;
                        startServerSocket();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            pw.close();
            is.close();
            dis.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 为此服务设置一个状态栏，使服务始终处于前台，提高服务等级
     */
    private void setServiceForeground() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Socket通讯服务");
        builder.setContentText("此服务用于通讯，请勿关闭");
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        //启动到前台
        startForeground(1, notification);
    }

    private void sendBroadcastMain(String str){
        intent.putExtra("msg", str);
        intent.setAction("com.jdrd.activity.Main");
        sendBroadcast(intent);
    }
    private void sendBroadcastRobot(String str){
        intent.putExtra("msg", str);
        intent.setAction("com.jdrd.activity.Robot");
        sendBroadcast(intent);
    }

    public static String getJSONString(String str, String key) {
        JSONObject object = null;
        String jsonString = null;
        try {
            object = new JSONObject(str);
            jsonString = object.getString(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    public static int getJSONInt(String data, String key) {
        JSONObject object = null;
        int jsonString = 0;
        try {
            object = new JSONObject(data);
            jsonString = object.getInt(key);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    @Override
    public void onDestroy() {
        //若通讯服务挂掉，再次开启服务
        Intent serverSocket = new Intent(this, ClientSocketUtil.class);
        startService(serverSocket);
        intent.putExtra("msg", "robot_destory");
        intent.setAction("com.jdrd.activity.Main");
        sendBroadcast(intent);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
