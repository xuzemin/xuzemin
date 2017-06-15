package com.android.jdrd.robot.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;
import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerSocketUtil extends Service {

    private Context mContext;
    private RobotDBHelper robotDBHelper;
    private static ServerSocket serverSocket;
    private static Socket socket1;
    private static Socket socket2;
    private static InputStream in1 = null;
    private static InputStream in2 = null;
    private static OutputStream out1 = null;
    private static OutputStream out2 = null;
    private static String msg = null;
    public static Intent intent;
    private MyReceiver receiver;
    IntentFilter filter;
    private static List<Map> socketlist = new ArrayList<>();
    private String function;

    @Override
    public void onCreate() {
        super.onCreate();
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
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

        receiver = new MyReceiver();
        filter = new IntentFilter();
        filter.addAction("com.jdrd.activity.Main");
        registerReceiver(receiver, filter);
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String camera = intent.getStringExtra("camera");
//            Constant.debugLog("收到摄像头数据" + camera);

//            if (camera != null) {
//                try {
//                    sendDateToClient(camera, Constant.ip_ros);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }

    public String startServerSocket(int port) throws IOException {

        //提升service进程优先级
        setServiceForeground();

        serverSocket = new ServerSocket(port);
        Socket socket;
        Constant.debugLog("serverSocket is create....");
        while (true) {
            Constant.debugLog("waiting for connect....");
            socket = serverSocket.accept();
            new Thread(new Task(socket)).start();
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

    /**
     * 发送数据给ros和大屏，其他类可通过调用ServerSocketUtil.sendDateToClient(String str, String ip)来发送数据
     * @param str：要发送的字符串
     * @param ip： 要发送的客户端的IP, Constant.ip_ros为小屏IP, Constant.ip_bigScreen为大屏IP
     */
    public static synchronized void sendDateToClient(String str, String ip,Socket socket) throws IOException {

        try {
            out1 = socket.getOutputStream();
            if (ip != null) {
                if (out1 != null) {
                    out1.write(str.getBytes());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Task implements Runnable {
        private Socket socket;
        public Task(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            String str = socket.getInetAddress().toString();
            final String  ip = str.substring(1,str.length());
            Constant.debugLog(ip);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接客户端IP为： " + ip, Toast.LENGTH_LONG).show();
                }
            });
            boolean IsHave = false;
            List<Map> robotList = robotDBHelper.queryListMap("select * from robot " ,null);
            if(robotList !=null && robotList.size() > 0) {
                for (int i = 0, size = robotList.size(); i < size; i++) {
                    if (robotList.get(i).get("ip").equals(ip)) {
                        IsHave = true;
                        robotDBHelper.execSQL("update robot set outline= '1' where ip= '"+ ip +"'");
                        break;
                    }
                }
                if(!IsHave){
                    robotDBHelper.execSQL("insert into  robot (name,ip,state,outline,electric,state," +
                            "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation) values " +
                            "('"+ip+"','"+ip+"',0,1,100,0,0,0,0,0,0,0)");
                }
            }else{
                robotDBHelper.execSQL("insert into  robot (name,ip,state,outline,electric,state," +
                        "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation) values " +
                        "('"+ip+"','"+ip+"',0,1,100,0,0,0,0,0,0,0)");
            }
            intent.putExtra("msg", "robot_connect");
            intent.setAction("com.jdrd.activity.Main");
            sendBroadcast(intent);

            Map<String, Object> map;
            map = new HashMap<>();
            map.put("socket", socket);
            map.put("ip", ip);
            socketlist.add(map);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Socket socket_cache = socket;
                    final String socket_ip = ip;
                    while(true){
                        try{
                            sendDateToClient("heartbeat",socket_ip,socket_cache);
                            Thread.sleep(3000);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            Constant.debugLog(socketlist.toString());
            try {
                in1 = socket.getInputStream();
                out1 = socket.getOutputStream();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        inputStreamParse(in1,ip);
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void inputStreamParse(InputStream in,String ip) {
        byte[] buffer = new byte[1024];
        int i = 0;
        boolean flag = false;
        boolean flag2 = false;
        int len = 0;
        while (true) {
            byte buf = 0;
            try {
                buf = (byte) in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Constant.debugLog("buf内容：" + buf);
            if(buf == -1){
                Constant.debugLog("socketlist"+socketlist.toString());
                int j = 0;
                while(j < socketlist.size()){
                    if(socketlist.get(j).get("ip").equals(ip)){
                        socketlist.remove(j);
                        robotDBHelper.execSQL("update robot set outline= '0' where ip= '"+ ip +"'");
                        Constant.debugLog("socketlist"+socketlist.toString());
                        intent.putExtra("msg", "robot_unconnect");
                        intent.setAction("com.jdrd.activity.Main");
                        sendBroadcast(intent);
                        break;
                    }
                }
            }
            if ('*' == buf) {
                flag = true;
                flag2 = true;
            }
            if ('#' == buf) {
                flag = false;
            }
            if (flag) {
                buffer[i] = buf;
                i++;
            } else if (flag == false && flag2) {
                msg = new String(buffer, 1, i);
                msg = msg.trim();
                if (msg != null) {
                    ++len;
                    Constant.debugLog("msg的内容： " + msg + "  次数：" + len);
                    function = getJSONString(msg, "function");
                    intent.putExtra("msg", msg);
                    intent.setAction("com.jdrd.activity.Main");
                    sendBroadcast(intent);

                    i = 0;
                    for (int j = 0; j < buffer.length; j++) {
                        buffer[j] = 0;
                    }
                    flag = false;
                    flag2 = false;
                }
            } else {
                Constant.debugLog((char) buf + "");
                Constant.debugLog("数据格式不对");
            }
            if (buf == -1) {
                break;
            }

            /*} catch (IOException e) {
                Constant.debugLog("异常了");
                e.printStackTrace();
            }*/

            /*msg = new String(buffer, 0, len);
            if (msg != null) {
                Constant.debugLog("msg = " + msg.toString() + "  ip地址： " + ip);
            } else {
                Constant.debugLog("hehe, msg为空");
            }
            intent.putExtra("msg", msg);
            intent.setAction("com.jdrd.fragment.Map");
            sendBroadcast(intent);*/

        }
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
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        startService(serverSocket);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
