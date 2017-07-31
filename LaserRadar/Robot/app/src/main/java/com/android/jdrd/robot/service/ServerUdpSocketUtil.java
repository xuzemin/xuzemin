package com.android.jdrd.robot.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.ContentValues.TAG;

public class ServerUdpSocketUtil extends Service {
    private RobotDBHelper robotDBHelper;
    public DatagramSocket udpServerSocket;
    private static InputStream in = null;
    public static Intent intent;
//    private MyReceiver receiver;
    IntentFilter filter;
    public static List<Map> socketlist = new ArrayList<>();

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

//        receiver = new MyReceiver();
//        filter = new IntentFilter();
//        filter.addAction("com.jdrd.activity.Main");
//        registerReceiver(receiver, filter);
    }

//    public class MyReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            String camera = intent.getStringExtra("camera");
////            Constant.debugLog("收到摄像头数据" + camera);
////            if (camera != null) {
////                try {
////                    sendDateToClient(camera, Constant.ip_ros);
////                } catch (IOException e) {
////                    e.printStackTrace();
////                }
////            }
//        }
//    }

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

    /*public static void sendDateToClient(String str, String ip) throws IOException {

        String str2 = "*" + str + "#";
        if (ip.equals("/192.168.1.100")) {
            try {
                out1.write(str2.getBytes());
                out2.write(str2.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (ip.equals("/192.168.1.102")) {
            try {
                out1.write(str2.getBytes());
                out2.write(str2.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Constant.debugLog("IP不对");
        }
    }*/



    public void connect(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    udpServerSocket = new DatagramSocket(8839);
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
            byte data[] = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            udpServerSocket.receive(packet);
            byte[] byc = packet.getData();
            int len2 = packet.getLength();
            String ss = new String(byc, 0, len2,"GBK");
            Constant.debugLog("packet"+ss);
            //把接收到的data转换为String字符串
            Constant.debugLog("packet"+packet.getAddress().getHostAddress()+""+packet.getPort()+""+packet.getSocketAddress());

            boolean IsHave = false;
            final String ip = packet.getAddress().getHostAddress();
            final int port = packet.getPort();
            List<Map> robotList = robotDBHelper.queryListMap("select * from robot " ,null);
            if(robotList !=null && robotList.size() > 0) {
                for (int i = 0, size = robotList.size(); i < size; i++) {
                    if (robotList.get(i).get("ip").equals(ip)) {
                        IsHave = true;
                        robotDBHelper.execSQL("update robot set outline = '1' where ip= '"+ ip +"'");
                        break;
                    }
                }
                if(!IsHave){
                    robotDBHelper.execSQL("insert into  robot (name,ip,state,outline,electric,robotstate,obstacle," +
                            "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation,area) values " +
                            "('新机器人','"+ip+"',0,1,100,0,0,0,0,0,0,0,0,0)");
                }
            }else{
                robotDBHelper.execSQL("insert into  robot (name,ip,state,outline,electric,robotstate,obstacle," +
                        "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation,area) values " +
                        "('新机器人','"+ip+"',0,1,100,0,0,0,0,0,0,0,0,0)");
            }
            boolean flag = true;
            if(socketlist!=null && socketlist.size()>0){
                for(int i = 0,size = socketlist.size();i<size;i++){
                    if(socketlist.get(i).get("ip").equals(ip)){
                        ((Task)socketlist.get(i).get("task")).cancel();
                        ((Task)socketlist.get(i).get("task")).thread = new Thread();
                        ((Task)socketlist.get(i).get("task")).send = false;
                        socketlist.get(i).put("task",
                                new Task(ip, port));
                        socketlist.get(i).put("port",port);
                        flag = false;
                        break;
                    }
                }
            }
            if(flag){
                Map<String, Object> map;
                map = new HashMap<>();
                map.put("ip", ip);
                map.put("task",new Task(ip,port));
                map.put("port",port);
                socketlist.add(map);
            }
            if(ss!=null){
                if(ss.equals("heartbeat")){
                    senddata(ss,ip,port);
                }
            }
            sendBroadcastMain("robot_connect");
            sendBroadcastRobot("robot");
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public void senddata(String str,String ip,int port){
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

    class Task extends TimerTask {
        private String ip = null;
        private Timer timer;
        private Thread thread;
        private boolean send;
        public Task(final String ip, final int port) {
            this.ip = ip;
            timer = new Timer(true);
            timer.schedule(this,9*1000);
            send = true;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(send){
                        senddata("*heartbeat#",ip,port);
                        try {
                            thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            thread.start();
        }
        @Override
        public void run() {
            send = false;
            if(thread!=null){
                if(thread.isAlive()){
                    thread = new Thread();
                }
            }
            removeSocket(ip);
        }
    }

    public void removeSocket(String ip){
        if(socketlist!=null && socketlist.size()>0){
            for(int i = 0,size = socketlist.size();i<size;i++){
                if(socketlist.get(i).get("ip").equals(ip)){
                    socketlist.remove(i);
                    break;
                }
            }
        }
        robotDBHelper.execSQL("update robot set outline = '0' where ip= '"+ ip +"'");
        sendBroadcastMain("robot_connect");
        sendBroadcastRobot("robot");
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
        Intent serverSocket = new Intent(this, ServerUdpSocketUtil.class);
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
