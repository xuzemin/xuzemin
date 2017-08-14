package com.android.jdrd.robotclient.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import com.android.jdrd.robotclient.R;
import com.android.jdrd.robotclient.helper.RobotDBHelper;
import com.android.jdrd.robotclient.util.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class ClientUdpSocketUtil extends Service {
    private RobotDBHelper robotDBHelper;
    public static DatagramSocket udpServerSocket;
    public static Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        intent = new Intent();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startServerUdpSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        connect();
    }


    public void startServerUdpSocket() throws IOException {
        //提升service进程优先级
        setServiceForeground();
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

    public void connect(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    udpServerSocket = new DatagramSocket(8839);
                    senddata("*heartbeat#",Constant.ServerUdpIp,51111);
                    while(true) {
                        ReceiveServerSocketData();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

            }
        }.start();
    }

    public void ReceiveServerSocketData() {
        try {
            //实例化的端口号要和发送时的socket一致，否则收不到data
            Log.e(TAG, "接受开始");
            byte data[] = new byte[102400];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            udpServerSocket.receive(packet);
            byte[] byc = packet.getData();
            int len2 = packet.getLength();
            String ss = new String(byc, 0, len2,"GBK");
            Constant.debugLog("packet"+ss);
            //把接收到的data转换为String字符串
            Constant.debugLog("len2"+len2+"packet"+packet.getAddress().getHostAddress()+""+packet.getPort()+""+packet.getSocketAddress());
            JSONObject object = null;
            int code = -1;
            try {
                object = new JSONObject(ss);
                code = object.getInt("returnCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(0 == code){
                JSONArray nameList = null;
                try {
                    nameList = object.getJSONArray("lineGroup");
                    Constant.debugLog("lineGroup"+(nameList==null));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(nameList==null){
                    try {
                        nameList = object.getJSONArray("carList");
                        Constant.debugLog("carList"+(nameList==null));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(nameList!=null){
                        robotDBHelper.execSQL("delete from robot");
                        JSONObject jsonObject;
                        for(int k = 0 ,mark = nameList.length();k<mark;k++){
                            jsonObject  = (JSONObject) nameList.get(k);
                            robotDBHelper.execSQL("insert into  robot (id,state,electric,obstacle,lastlocation) values " +
                                    "('" + jsonObject.getInt("carNo")+ "','" + jsonObject.getString("status")  + "'" +
                                    ",'" + jsonObject.getString("voltage") + "','" + jsonObject.getString("warning") +"','"+jsonObject.getString("position")+ "')");
                        }
                    }
                }else {
                    robotDBHelper.execSQL("delete from area");
                    robotDBHelper.execSQL("delete from desk");
                    Constant.debugLog("nameList" + nameList.length());
                    JSONObject jsonObject, jsonObject1;
                    JSONArray jsonArray;
                    for (int i = 0, size = nameList.length(); i < size; i++) {
                        try {
                            jsonObject = new JSONObject(nameList.get(i).toString());
                            robotDBHelper.execSQL("insert into  area (id,name) values " +
                                    "('" + i + "','" + jsonObject.getString("name") + "')");
                            jsonArray = jsonObject.getJSONArray("lines");
                            Constant.debugLog("jsonArray.length()" + jsonArray.length());
                            for (int j = 0, length = jsonArray.length(); i < length; j++) {
                                jsonObject1 = (JSONObject) jsonArray.get(j);
                                robotDBHelper.execSQL("insert into  desk (id,name,area,type) values " +
                                        "('" + jsonObject1.getInt("lineNo") + "','" + jsonObject1.getString("name") + "','" + i + "','" + jsonObject1.getString("lever") + "')");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }else{
                Toast.makeText(getApplicationContext(),"指令错误",Toast.LENGTH_SHORT).show();
            }

            sendBroadcastMain("robot_connect");
            sendBroadcastRobot("robot");
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void sendDesk(){
        Gson gson = new Gson();
        Map map = new LinkedHashMap();
        map.put("action", "getLineList");
        String string = gson.toJson(map);
        senddata(string,Constant.ServerUdpIp,Constant.ServerUdpPort);
    }
    public static void sendRobot(){
        Gson gson = new Gson();
        Map map = new LinkedHashMap();
        map.put("action", "getCarList");
        String string = gson.toJson(map);
        senddata(string,Constant.ServerUdpIp,Constant.ServerUdpPort);
    }
    public static void sendCommand(int card,int line){
        Gson gson = new Gson();
        Map map = new LinkedHashMap();
        map.put("action", "run");
        map.put("cardNo",card);
        map.put("lineNo",line);
        String string = gson.toJson(map);
        senddata(string,Constant.ServerUdpIp,Constant.ServerUdpPort);
    }

    public static void senddata(String str, String ip, int port){
        byte data[] ;
        try {
            data = str.getBytes("GBK");
            DatagramPacket packet  = new DatagramPacket(data,data.length,InetAddress.getByName(ip),port);
            udpServerSocket.send(packet);
        } catch (UnknownHostException e) {
            Constant.debugLog("UnknownHostException"+e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            Constant.debugLog("IOException"+e.toString());
            e.printStackTrace();
        }
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


    @Override
    public void onDestroy() {
        //若通讯服务挂掉，再次开启服务
        Intent serverSocket = new Intent(this, ClientUdpSocketUtil.class);
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
