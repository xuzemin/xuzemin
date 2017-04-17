package com.android.jdrd.headcontrol.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.database.HeadControlBean;
import com.android.jdrd.headcontrol.database.HeadControlDao;
import com.android.jdrd.headcontrol.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static android.R.attr.data;

public class ServerSocketUtil extends Service {

    private Context mContext;

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
    private String function;

    @Override
    public void onCreate() {
        super.onCreate();

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
        filter.addAction("com.jdrd.fragment.Map");
        registerReceiver(receiver, filter);
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            String camera = intent.getStringExtra("camera");
//            Constant.debugLog("收到摄像头数据" + camera);

            if (camera != null) {
                try {
                    sendDateToClient(camera, Constant.ip_ros);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
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
        builder.setSmallIcon(R.mipmap.qi);
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
    public static synchronized void sendDateToClient(String str, String ip) throws IOException {

        try {
            if (ip != null) {
                if (ip.equals(Constant.ip_bigScreen)) {
                    if (out1 != null) {
                        out1.write(str.getBytes());
                    }

                } else if (ip.equals(Constant.ip_ros)) {
                    str = "*" + str + "#";
                    if (out2 != null) {
                        out2.write(str.getBytes());
                    }
                } else {
                    Constant.debugLog("IP不对");
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
            String ip = socket.getInetAddress().toString();
            Constant.debugLog(ip);

            if (ip.equals(Constant.ip_bigScreen)) {
                socket1 = socket;
            } else if (ip.equals(Constant.ip_ros)) {
                socket2 = socket;
            } else {
                Constant.debugLog("IP不对");
            }

            try {

                if (ip.equals(Constant.ip_bigScreen)) {
                    in1 = socket1.getInputStream();
                    out1 = socket1.getOutputStream();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            inputStreamParse(in1);
                        }
                    }).start();
                } else if (ip.equals(Constant.ip_ros)) {
                    in2 = socket2.getInputStream();
                    out2 = socket2.getOutputStream();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            inputStreamParse(in2);
                        }
                    }).start();
                } else {
                    Constant.debugLog("流为空");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void inputStreamParse(InputStream in) {
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
//            Constant.debugLog("buf内容：" + buf);

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
                    savePowerData();
                    saveWaterStatus();
                    intent.putExtra("msg", msg);
                    intent.setAction("com.jdrd.fragment.Map");
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

    /**
     * 将电量数据存入数据库中，并实时更新
     */
    private void savePowerData() {
        if("power".equals(function)) {
            String data = getJSONString(msg, "data");
            int power = getJSONInt(data, "power");
            Constant.debugLog("电量数据是： " + power);

            HeadControlDao headControlDao = new HeadControlDao(getApplicationContext());
            HeadControlBean bean  = headControlDao.query("power");
            if(bean == null) {
                bean = new HeadControlBean();
                bean.setFunction("power");
                bean.setDataInt(power);
                headControlDao.add(bean);
            }else {
                bean.setFunction("power");
                bean.setDataInt(power);
                headControlDao.update(bean);
            }
        }
    }

    private void saveWaterStatus() {
        if("clean".equals(function)) {
            int waterState = getJSONInt(msg, "data");
            Constant.debugLog("水量状态： " + data);

            HeadControlDao headControlDao = new HeadControlDao(getApplicationContext());
            HeadControlBean bean  = headControlDao.query("clean");
            if(bean == null) {
                bean = new HeadControlBean();
                bean.setFunction("clean");
                bean.setDataInt(waterState);
                headControlDao.add(bean);
            }else {
                bean.setFunction("clean");
                bean.setDataInt(waterState);
                headControlDao.update(bean);
            }
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
