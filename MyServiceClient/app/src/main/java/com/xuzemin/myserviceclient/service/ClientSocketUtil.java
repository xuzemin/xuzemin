package com.xuzemin.myserviceclient.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.xuzemin.myserviceclient.R;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * 客户端
 */
public class ClientSocketUtil extends Service {
    //发送的消息
    private static String msg = null;

    private MyReceiver receiver;
    IntentFilter filter;
    String CHANNEL_ONE_ID = "com.primedu.cn";
    String CHANNEL_ONE_NAME = "Channel One";

    private static final String TAG = "TAG";
    private static final String HOST = "192.168.1.102";
    private static final int PORT = 20123;
    private PrintWriter printWriter;
    private BufferedReader in;
    private String receiveMsg;


    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        setServiceForeground();
        //线程启动
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startClientSocket();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
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
        }
    }

    public void startClientSocket() throws Exception {
        //提升service进程优先级
        Socket socket = new Socket(HOST, PORT);      //步骤一
        socket.setSoTimeout(60000);
        printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(   //步骤二
                socket.getOutputStream(), "UTF-8")), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
        receiveMsg();

    }

    private void receiveMsg() {
        try {
            while (true) {                                      //步骤三
                if ((receiveMsg = in.readLine()) != null) {
                    Log.d(TAG, "receiveMsg:" + receiveMsg);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "receiveMsg: ");
            e.printStackTrace();
        }
    }


    /**
     * 为此服务设置一个状态栏，使服务始终处于前台，提高服务等级
     */
    private void setServiceForeground() {
        //系统通知栏
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);//通知图标
        builder.setContentTitle("Socket通讯服务");//通知标题
        builder.setContentText("此服务用于通讯，请勿关闭");//通知内容

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//修改安卓8.1以上系统报错
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME,NotificationManager.IMPORTANCE_MIN);
            notificationChannel.enableLights(false);//如果使用中的设备支持通知灯，则说明此通知通道是否应显示灯
            notificationChannel.setShowBadge(false);//是否显示角标
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            builder.setChannelId(CHANNEL_ONE_ID);
        }


        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        //启动到前台
        startForeground(1, notification);
    }

    @Override
    public void onDestroy() {
        //若通讯服务挂掉，再次开启服务
        Intent serverSocket = new Intent(this, ClientSocketUtil.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serverSocket);
        }else{
            startService(serverSocket);
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
