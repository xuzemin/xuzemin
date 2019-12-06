package com.android.chooseserver.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;


import androidx.core.app.NotificationCompat;

import com.android.chooseserver.R;
import com.android.chooseserver.utils.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static com.android.chooseserver.utils.Constant.CHANNEL_ONE_ID;
import static com.android.chooseserver.utils.Constant.CHANNEL_ONE_NAME;

/**
 * 服务端
 */
public class ServerSocketUtil extends Service {

    //创建一个服务器端的Socket，即ServerSocket
    private static ServerSocket serverSocket;
    //获取输入流
    private static InputStream in = null;

    public static boolean isRunLS = false;
    public static int serverLoop = 0;
    //获取输出流
    private static OutputStream out = null;
    //消息
    private static String msg = null;

    public static int LsCurrent = 0;

    private static final int END = 100;

    public static Intent intent;

    public static List<Map> socketList = new ArrayList<>();

    public static boolean sendHeartBeat = true;

    @Override
    public void onCreate() {
        super.onCreate();
//        startForeground(1349, new Notification());
        //初始化Intent
        intent = new Intent();
        //启动线程
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

    }


    //启动ServerSocket
    public String startServerSocket(int port) throws IOException {

        //提升service进程优先级
        setServiceForeground();
        //创建ServerSocket对象
        serverSocket = new ServerSocket(port);
        //创建Socket对象
        Socket socket;
        //打印Log
        Constant.debugLog("serverSocket正在创建......");
        //死循环
        while (true) {
            Constant.debugLog("正在等待连接......");
            //接收请求
            socket = serverSocket.accept();
            //作用:每隔一段时间检查服务器是否处于活动状态，如果服务器端长时间没响应，自动关闭客户端socket
            //防止服务器端无效时，客户端长时间处于连接状态
            socket.setKeepAlive(true);

            //客户端socket在接收数据时，有两种超时:
            // 1.连接服务器超时，即连接超时;
            // 2.连接服务器成功后，接收服务器数据超时，即接收超时
            //设置socket 读取数据流的超时时间
            //socket.setSoTimeout(9000);

            //开启线程
            new Thread(new Task(socket)).start();

/*            // 启动发送设置磁导航模式
            btSendBytes(Protocol.getSendData(16, Protocol.getCommandData(Protocol.MN_PATTERN)), socket);*/
        }
    }

    /**
     * 为此服务设置一个状态栏，使服务始终处于前台，提高服务等级
     */
    private void setServiceForeground() {
        //系统通知栏
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);//通知栏图片
        builder.setContentTitle("Socket通讯服务");//通知栏标题
        builder.setContentText("正在通讯，请勿关闭");//通知栏内容

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

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

        Notification notification = builder.build();
        //启动到前台
        startForeground(1, notification);
    }

    /**
     * @param str：要发送的字符串
     * @param ip：发送的客户端的IP
     */
    public static synchronized void sendDateToClient(String str, String ip, Socket socket) throws IOException {

        try {
            if (socket.isClosed()) {

            } else {
                //获取输出流
                out = socket.getOutputStream();
                if (ip != null) {
                    if (out != null) {
                        //写入数据
                        out.write(str.getBytes());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试
     */
    public void btSendBytes(byte[] data, String ip, Socket socket) throws IOException {
        try {
            if (socket.isClosed()) {

            } else {
                //获取输出流
                out = socket.getOutputStream();
                if (ip != null) {
                    if (out != null) {
                        //写入数据
                        out.write(data);
                        Constant.debugLog("测试数据长度:" + data.length);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     */

    public void btSendBytes(byte[] data, Socket socket) throws IOException {
        try {
            if (socket.isClosed()) {

            } else {
                //获取输出流
                out = socket.getOutputStream();
                if (out != null) {
                    //写入数据
                    out.write(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btSendBytes(byte[] data, Map map) throws IOException {
        try {
            if (((Socket)map.get("socket")).isClosed()) {

            } else {
                //获取输出流
//                out = socket.getOutputStream();
                if (out != null) {
                    //写入数据
                    out.write(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建Task线程
    class Task implements Runnable {
        private Socket socket;

        //构造方法
        public Task(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            //获取本机IP地址
            String str = socket.getInetAddress().toString();
            //截取IP地址
            final String ip = str.substring(1, str.length());
            //打印IP地址
            Constant.debugLog("连接客户端的IP为:----->" + ip);
            //子线程更新UI  调用Looper.getMainLooper()
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "连接客户端IP为:" + ip, Toast.LENGTH_LONG).show();
                    // 启动发送设置磁导航模式
/*                    try {
                        // btSendBytes(Protocol.getSendData(16, Protocol.getCommandData(Protocol.MN_PATTERN)), socket);
                        btSendBytes(Protocol.getSendData(1, Protocol.getCommandDataByte(Protocol.MN_PATTERN)), socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
                }
            });

            boolean IsHave = false;
            //查询机器人列表
            //广播发送连接
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Socket socket_cache = socket;
                    final String socket_ip = ip;
                    while (true) {
                        try {
                            if (socket_cache.isClosed()) {
                                break;
                            } else {
                                // 测试最长数据
                                if(sendHeartBeat) {
                                    btSendBytes("hearbeat".getBytes(), socket_ip, socket_cache);
                                }
                                Thread.sleep(3*1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //打印异常
                            Constant.debugLog("异常信息----->" + e.toString());
                            try {
                                socket.close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }).start();


            // 获取消息
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //打印日志
            Constant.debugLog("IsHAVE----->" + IsHave);

            if (IsHave) {
                int j = 0;
                Constant.debugLog("IsHAVE----->" + socketList.size());
                while (j < socketList.size()) {
                    //打印日志
                    Constant.debugLog(socketList.get(j).get("ip") + "<---------->" + ip + "ip");
                    if (socketList.get(j).get("ip").equals(ip)) {
                        try {
                            // 打印Log
                            Constant.debugLog("inClose");
                            ((InputStream) socketList.get(j).get("in")).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        try {
                            // 打印Log
                            Constant.debugLog("socketClose");

                            ((Socket) socketList.get(j).get("socket")).close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        socketList.remove(j);
                        // 打印Log
                        Constant.debugLog("socketList----->" + socketList.toString());
                        break;
                    }
                    j++;
                    // 打印Log
                    Constant.debugLog("j socketList----->" + j);
                }
            }

            // 传递数据 以键值对的形式
            final Map<String, Object> map;
            map = new HashMap<>();
            map.put("socket", socket);
            map.put("ip", ip);
            map.put("in", in);
            map.put("out", out);
            map.put("Cunrrent",0);
            map.put("Cunrrent",null);
            socketList.add(map);
            Constant.debugLog("socketList.size() =="+socketList.size());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        inputStreamParse(map,socketList.size()-1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    //解析输入流
    public void inputStreamParse(final Map<String, Object> map, final int socketnumber) throws IOException {
        // 定义byte[]字节数组
        byte[] buffer = new byte[1024];
        InputStream in = (InputStream) map.get("in");
        final String ip = (String) map.get("ip");
        OutputStream out = (OutputStream) map.get("out");
        int b = 0;
        byte[] data;
        // 循环读取
        while (true) {
            try {
                //读取输入流
                if(!((Socket)map.get("socket")).isClosed()){
                    b = (byte) in.read(buffer);
                }else{
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 断开Socket
//                socketList.remove(socketnumber);
                removeSocket(ip);
            }
            // 获取读取到的字节长度
            Constant.debugLog("b的长度:----->" + b);
            if (b <= 0) {
                try {
                    in.close();
                    out.close();
                    removeSocket(ip);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            } else  {
                Constant.debugLog("socket"+b);
                // 当读不到数据时就返回-1
                // 第一个参数:bytes:要解码为字符的 byte
                // 第二个参数:offset:要解码的第一个 byte 的索引
                // 第三个参数:length:要解码的 byte 数 的长度
                msg = new String(buffer, 0, b);
                // 将byte[]转化十六进制的字符串
                Constant.debugLog("下标为3的长度:" + msg.length());
                Constant.debugLog("下标为3的长度:" + Arrays.toString(buffer));
                // TODO 做是否以*<开始 获取buffer[0]  和  buffer[1]的值做比较 * = 42  < = 60
                // 判断长度是否相等
                if (3 == b && buffer[0] == 42 && buffer[2] == 35) {
                    switch (buffer[1]) {
                        case 97:
                            break;
                        // 最后坐标
                        case 101:
                        case 65:
                        case 51:
                        case 49:
                            if(out != null){
                                out.write("get".getBytes());
                            }
                            break;
                        default:
                            Constant.debugLog("- - - - - 命令值错误 - - - - -");
                            Constant.debugLog("4"+buffer[2]);
                            break;
                    }
                } else {
                    Constant.debugLog("- - - - - 长度错误 - - - - -");
                    // 判断是否为空
                    if (out != null) {
                    }
                }
                // TODO 做是否以*<开始
            }
        }
    }

    //移除Socket
    public void removeSocket(String ip) {
        Socket socket;
        int j = 0;
        while (j < socketList.size()) {
            if (socketList.get(j).get("ip").equals(ip)) {
                socket = (Socket) socketList.get(j).get("socket");
                socketList.remove(j);
                //修改运行轨迹
                //断开连接
                sendBroadcastMain("robot_unconnect");
                sendBroadcastRobot("robot");
                //关闭流
                try {
                    in.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            j++;
        }
    }


    //广播发送
    private void sendBroadcastMain(String str) {
        intent.putExtra("msg", str);
        intent.setAction("com.jdrd.activity.Main");
        sendBroadcast(intent);
    }

    //广播发送
    private void sendBroadcastRobot(String str) {
        intent.putExtra("msg", str);
        intent.setAction("com.jdrd.activity.Robot");
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        //若通讯服务挂掉，再次开启服务
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serverSocket);
        }else {
            startService(serverSocket);
        }
        intent.putExtra("msg", "robot_destory");
        intent.setAction("com.jdrd.activity.Main");
        sendBroadcast(intent);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static double byteArrayToIntX(byte[] b) {
        return (b[8] & 0xFF |
                (b[7] & 0xFF) << 8 |
                (b[6] & 0xFF) << 16 |
                (b[5] & 0xFF) << 24) / 100.00;
    }

    public static double byteArrayToIntY(byte[] b) {
        return (b[12] & 0xFF |
                (b[11] & 0xFF) << 8 |
                (b[10] & 0xFF) << 16 |
                (b[9] & 0xFF) << 24) / 100.00;
    }

    public static double byteArrayToIntZ(byte[] b) {
        return (b[14] & 0xFF |
                (b[13] & 0xFF) << 8) /100.00;
    }


}
