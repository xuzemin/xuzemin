package com.android.zbrobot.service;

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
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.android.zbrobot.R;
import com.android.zbrobot.activity.ZB_MainActivity;
import com.android.zbrobot.dialog.ZB_RobotDialog;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.util.Constant;
import com.android.zbrobot.util.RobotUtils;
import com.ls.lsros.callback.CallBack;
import com.ls.lsros.data.provide.bean.NavigationResult;
import com.ls.lsros.helper.RobotNavigationHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 服务端
 */
public class ServerSocketUtil extends Service {

    //数据库帮助类
    private static RobotDBHelper robotDBHelper;
    //创建一个服务器端的Socket，即ServerSocket
    private static ServerSocket serverSocket;
    //获取输入流
    private static InputStream in = null;

    public static int serverLoop = 0;
    //获取输出流
    private static OutputStream out = null;
    //消息
    private static String msg = null;

    public static int LsCurrent = 0;

    private static final int END = 100;

    public static Intent intent;
    private MyReceiver receiver;
    IntentFilter filter;
    public static List<Map> socketList = new ArrayList<>();

    public static boolean sendHeartBeat = true;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
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
        builder.setSmallIcon(R.mipmap.zb_logo);//通知栏图片
        builder.setContentTitle("Socket通讯服务");//通知栏标题
        builder.setContentText("正在通讯，请勿关闭");//通知栏内容

        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
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
            List<Map> robotList = robotDBHelper.queryListMap("select * from robot ", null);
            if (robotList != null && robotList.size() > 0) {
                for (int i = 0, size = robotList.size(); i < size; i++) {
                    if (robotList.get(i).get("ip").equals(ip)) {
                        IsHave = true;
                        //修改运行路线
                        robotDBHelper.execSQL("update robot set outline = '1' where ip= '" + ip + "'");
                        //打印日志
                        Constant.debugLog("socketList----->" + socketList.toString());
                        break;
                    }
                }
                //收到客户端的连接之后，添加新的机器人
                if (!IsHave) {
                    robotDBHelper.execSQL("insert into  robot (name,ip,state,outline,electric,robotstate,obstacle," +
                            "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation,area,pathway,outtime" +
                            ",turnback,goal,up_obstacle,down_obstacle ,side_obstacle,loop) values " +
                            "('新机器人','" + ip + "',0,1,100,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
                }
            } else {
                robotDBHelper.execSQL("insert into  robot (name,ip,state,outline,electric,robotstate,obstacle," +
                        "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation,area,pathway,outtime" +
                        ",turnback,goal,up_obstacle,down_obstacle ,side_obstacle) values " +
                        "('新机器人','" + ip + "',0,1,100,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
            }
            //广播发送连接
            sendBroadcastMain("robot_connect");
            sendBroadcastRobot("robot");
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
                                //发送心跳包    用于测试服务端与客户端是否在连接状态 每隔3秒发送一次
                                //sendDateToClient("*heartbeat#", socket_ip, socket_cache);

                                //发送命令测试
                                // 设置成磁导航模式
                                //btSendBytes(Protocol.getSendData(Protocol.MN_PATTERN_FUNCTION, Protocol.getCommandData(Protocol.MN_PATTERN)), socket_ip, socket_cache);
                                // 设置成脱轨运行模式
                                //btSendBytes(Protocol.getSendData(Protocol.MN_PATTERN_FUNCTION, Protocol.getCommandData(Protocol.MN_PATTERN_FUNCTION)), socket_ip, socket_cache);
                                // 设置前进速度为500
                                //btSendBytes(Protocol.getSendData(Protocol.UP_SPEED, Protocol.getCommandData(Protocol.UP_SPEED)), socket_ip, socket_cache);
                                // 清除故障
                                // btSendBytes(Protocol.getSendData(Protocol.CLEAR_FAULT, Protocol.getCommandData(Protocol.CLEAR_FAULT)), socket_ip, socket_cache);
                                // 前进
                                //btSendBytes(Protocol.getSendData(Protocol.UP, Protocol.getCommandData(Protocol.ROBOT_UP)), socket_ip, socket_cache);
                                // 后退
                                //btSendBytes(Protocol.getSendData(Protocol.DOWN, Protocol.getCommandData(Protocol.ROBOT_DOWN)), socket_ip, socket_cache);
                                // 左转
                                //btSendBytes(Protocol.getSendData(Protocol.LEFT, Protocol.getCommandData(Protocol.ROBOT_LEFT)), socket_ip, socket_cache);
                                // 右转
                                //btSendBytes(Protocol.getSendData(Protocol.RIGHT, Protocol.getCommandData(Protocol.ROBOT_RIGHT)), socket_ip, socket_cache);
                                // 停止
                                //btSendBytes(Protocol.getSendData(Protocol.STOP, Protocol.getCommandData(Protocol.ROBOT_STOP)), socket_ip, socket_cache);

                                // 测试最长数据
                                if(sendHeartBeat) {
                                    btSendBytes(Protocol.getSendData(Protocol.HEART_BEAT, Protocol.getCommandData(Protocol.ROBOT_HRARTBEAD)), socket_ip, socket_cache);
                                }
                                Thread.sleep(3*1000);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            //打印异常
                            Constant.debugLog("异常信息----->" + e.toString());
                            try {
                                socket.close();
                                robotDBHelper.execSQL("update robot set outline= '0' where ip= '" + ip + "'");
                                sendBroadcastMain("robot_connect");
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
            Constant.debugLog("socketList.size()-1"+socketList.size());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    inputStreamParse(map,socketList.size()-1);
                }
            }).start();
        }
    }

    //解析输入流
    public void inputStreamParse(final Map<String, Object> map, final int socketnumber) {
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
            if (b < 0  || b == 1 || b == 0) {
                try {
                    in.close();
                    out.close();
//                    if(socketList.get(socketnumber)!=null) {
//                        socketList.remove(socketnumber);
                    removeSocket(ip);
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            } else if (b != -1 && b > 0) {
                Constant.debugLog("socket"+b);
                // 当读不到数据时就返回-1
                // 第一个参数:bytes:要解码为字符的 byte
                // 第二个参数:offset:要解码的第一个 byte 的索引
                // 第三个参数:length:要解码的 byte 数 的长度
                msg = new String(buffer, 0, b);
                // 将byte[]转化十六进制的字符串
                String ret = "";
                for (int i = 0; i < msg.length(); i++) {
                    // 显示一个byte型的单字节十六进制(两位十六进制表示)的编码
                    String hex = Integer.toHexString(buffer[i] & 0xFF);
                    if (hex.length() == 1) {
                        hex = '0' + hex;
                    }
                    // 读取的数据全部转换为大写
                    ret += hex.toUpperCase();

                }
                // 十六进制String转byte[]
                byte[] byteArray = new byte[ret.length() / 2];
                for (int i = 0; i < byteArray.length; i++) {
                    String subStr = ret.substring(2 * i, 2 * i + 2);
                    byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
                }

                // TODO 做是否以*<开始 获取buffer[0]  和  buffer[1]的值做比较 * = 42  < = 60

                // 判断长度是否相等
                if (byteArray.length == buffer[2] + 5) {
                    Constant.debugLog("- - - - - 数据长度正确 - - - - -");
                    switch (buffer[3]) {
                        case 97:
                            Constant.debugLog("下标为3的长度:" + buffer[3]);
                            // 修改电量值
                            robotDBHelper.execSQL("update robot set electric = '" + buffer[5] + "',pathway = '" + buffer[4] + "' where ip= '" + ip + "'");
                            Constant.debugLog("buffer[5]下标为5的值" + buffer[5]);
                            // 发送广播
                            sendBroadcastRobot("robot");
                            sendBroadcastMain("robot_connect");
                            // 判断是否为空
                            if (out != null) {
                                // 发送的数据
                                data = Protocol.getSendData(Protocol.RECEIVE, Protocol.getCommandData(Protocol.ROBOT_RAIL_SUCCESS));
                                try {
                                    // 发送接收应答成功
                                    out.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Constant.debugLog("电量获取成功");
                            break;

                        // 运动状态   0->直行前进  1->左转   2->右转   3->旋转
                        case 98:
                            Constant.debugLog("下标为3的长度:" + buffer[3]);
                            // 修改运动状态
                            robotDBHelper.execSQL("update robot set state = '" + buffer[5] + "',pathway = '" + buffer[4] + "' where ip= '" + ip + "'");
                            Constant.debugLog("buffer[5]下标为5的值" + buffer[5]);
                            // 发送广播
                            sendBroadcastRobot("robot");
                            sendBroadcastMain("robot_connect");
                            // 判断是否为空
                            if (out != null) {
                                // 发送的数据
                                data = Protocol.getSendData(Protocol.RECEIVE, Protocol.getCommandData(Protocol.ROBOT_RAIL_SUCCESS));
                                try {
                                    // 发送接收应答成功
                                    out.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Constant.debugLog("运动状态获取成功");
                            break;

                        // 机器人运行状态  0->空闲   1->送餐   2->故障
                        case 99:
                            Constant.debugLog("下标为3的长度:" + buffer[3]);
                            // 修改机器人运行状态
                            robotDBHelper.execSQL("update robot set robotstate = '" + buffer[5] + "',pathway = '" + buffer[4] + "' where ip= '" + ip + "'");
                            Constant.debugLog("buffer[5]下标为5的值" + buffer[5]);
                            // 发送广播
                            sendBroadcastRobot("robot");
                            sendBroadcastMain("robot_connect");
                            // 判断是否为空
                            if (out != null) {
                                // 发送的数据
                                data = Protocol.getSendData(Protocol.RECEIVE, Protocol.getCommandData(Protocol.ROBOT_RAIL_SUCCESS));
                                try {
                                    // 发送接收应答成功
                                    out.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Constant.debugLog("机器人运行状态获取成功");
                            break;

                        // 是否有障碍物 0->无 1->有
                        case 100:
                            Constant.debugLog("下标为3的长度:" + buffer[3]);
                            // 修改是否有障碍物
                            robotDBHelper.execSQL("update robot set obstacle = '" + buffer[5] + "',pathway = '" + buffer[4] + "' where ip= '" + ip + "'");
                            Constant.debugLog("buffer[5]下标为5的值" + buffer[5]);
                            Constant.debugLog("buffer[4]下标为6的值" + buffer[4]);
                            // 发送广播
                            sendBroadcastRobot("robot");
                            sendBroadcastMain("robot_connect");
                            // 判断是否为空
                            if (out != null) {
                                // 发送的数据
                                data = Protocol.getSendData(Protocol.RECEIVE, Protocol.getCommandData(Protocol.ROBOT_RAIL_SUCCESS));
                                try {
                                    // 发送接收应答成功
                                    out.write(data);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Constant.debugLog("障碍物状态获取成功");
                            break;

                        // 最后坐标
                        case 101:
                            Constant.debugLog("下标为3的长度:" + buffer[3]);
                            // 修改最后坐标
                            if (buffer[4] == 0) {
                                robotDBHelper.execSQL("update robot set lastlocation = '" + buffer[5] + "',pathway = '" + buffer[4] + "' where ip= '" + ip + "'");
                                Constant.debugLog("buffer[5]下标为5的值" + buffer[5]);
                            } else {
                                Constant.debugLog(buffer.toString());
                                robotDBHelper.execSQL("update robot set point_x = '" + byteArrayToIntX(buffer)
                                        + "',pathway = '" + buffer[4] + "',point_y = '" + byteArrayToIntY(buffer) +
                                        "',direction = '" + byteArrayToIntZ(buffer) + "' where ip= '" + ip + "'");
                            }
                            // 发送广播
                            sendBroadcastRobot("robot");
                            sendBroadcastMain("robot_connect");
                            // 判断是否为空
                            if (out != null) {
                                // 发送的数据
                                data = Protocol.getSendData(Protocol.RECEIVE, Protocol.getCommandData(Protocol.ROBOT_RAIL_SUCCESS));
//                                try {
//                                    // 发送接收应答成功
//                                    out.write(data);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
                                try {
                                    btSendBytes(data,map);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Constant.debugLog("最后坐标状态获取成功");
                            break;
                        case 65:
                            robotDBHelper.execSQL("update robot set robotstate = '" + buffer[4]+"' where ip= '" + ip + "'");
                            Constant.debugLog("buffer[5]下标为5的值" + buffer[5]);
                            break;
                        // 接收应答成功
                        case 40:
                        case 41:
                        case 42:
                        case 50:
                        case 44:
                        case 43:
                        case 45:
                        case 114:
                            if (buffer[4] == 0) {
                                sendBroadcastMain("robot_receive_succus");
                            } else {
                                sendBroadcastMain("robot_receive_fail");
                            }
                            break;
                        case 51:
                            Constant.debugLog("socketlist  收到反馈");
                            // 判断
//                            if (buffer[5] == 0) {
                            if (socketList.get(socketnumber)!=null) {
                                final Map CurrentMap = socketList.get(socketnumber);
                                if(CurrentMap.get("Cunrrent")!=null) {
                                    int number = (int) CurrentMap.get("Cunrrent");
                                    List list = (List) CurrentMap.get("list");
                                    Constant.debugLog("socketlist" + number);
                                    Constant.debugLog("socketlist" + list.size());
                                    if (number != END) {
                                        number++;
                                        if (number >= list.size()) {
                                            number = END;
                                        }
                                        socketList.get(socketnumber).put("Cunrrent", number);
                                        int waitime = (int) CurrentMap.get("waittime");
                                        Constant.debugLog("socketlist waitime" + waitime);
                                        if (waitime > 0) {
                                            Timer timer = new Timer();
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {
                                                    sendlist(ip);
                                                }
                                            };
                                            timer.schedule(task, waitime * 1000);
                                        } else {
                                            sendlist(ip);
                                        }
                                    } else {
                                        socketList.get(socketnumber).put("Cunrrent", 0);
                                    }
                                }
                            }
//                            }else {
//                                sendlist(ip);
////                                sendBroadcastMain("robot_receive_fail");
//                            }
                            // 没有找到对应的命令
                            break;
                        default:
                            Constant.debugLog("- - - - - 命令值错误 - - - - -");
                            Constant.debugLog("4"+buffer[4]);
                            Constant.debugLog("3"+buffer[3]);
                            // 判断是否为空
//                            if (out != null) {
//                                // 发送的数据
//                                data = Protocol.getSendData(Protocol.RECEIVE, Protocol.getCommandData(Protocol.ROBOT_RAIL_FAIL));
//                                try {
//                                    // 发送接收应答失败
//                                    out.write(data);
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
                            break;
                    }
                } else {
                    Constant.debugLog("- - - - - 长度错误 - - - - -");
                    // 判断是否为空
                    if (out != null) {
                        // 发送的数据
                        data = Protocol.getSendData(Protocol.RECEIVE, Protocol.getCommandData(Protocol.ROBOT_RAIL_FAIL));
                        try {
                            // 发送接收应答失败
                            out.write(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                byteArray = new byte[ret.length() / 2];
                // TODO 做是否以*<开始
            } else {
                // 断开Socket
                removeSocket(ip);
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
                robotDBHelper.execSQL("update robot set outline= '0' where ip= '" + ip + "'");
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



    public static void sendCommandCoordinate(String IP, final List list){
        // 检查IP是否相同
        for (int i = 0,size = socketList.size();i<size; i++) {
            Constant.debugLog("socketlist"+socketList.toString());
            if (socketList.get(i).get("ip").equals(IP)) {
                socketList.get(i).put("list",list);
                socketList.get(i).put("Cunrrent",0);
                Constant.debugLog("socketList.get(i)"+socketList.get(i).get("list").toString());
                sendlist(IP);
                break;
            }
        }
    }
    public static void sendLSList(final List list){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map deskmap;
                if(LsCurrent < list.size()){
                    Constant.debugLog("LsCurrent"+LsCurrent);
                    List<Map> desk_list = robotDBHelper.queryListMap("select * from desk where id = '" + list.get(LsCurrent) + "'", null);
                    deskmap = desk_list.get(0);
                    Constant.debugLog("list"+deskmap.toString());
                    if(deskmap!=null) {
                        if (deskmap.get("pointx") != null && deskmap.get("pointy") != null && deskmap.get("derection") != null) {
                            RobotNavigationHelper.getInstance().sendGoal(Double.parseDouble(deskmap.get("pointx").toString().trim())
                                    , Double.parseDouble(deskmap.get("pointy").toString().trim()),
                                    Double.parseDouble(deskmap.get("derection").toString().trim())*Math.PI/180);


                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RobotNavigationHelper.getInstance().startNav(new CallBack<NavigationResult>() {
                                @Override
                                public void call(NavigationResult data) {
                                    Constant.debugLog("开始导航-->" + data.getCode() +
                                            "isSuccess" + data.isSuccess());
                                }
                            });
                        }
                    }
                }else if(LsCurrent == list.size() && ZB_RobotDialog.loop == 1){
                    Map areamap;
                    List<Map> area_list = robotDBHelper.queryListMap("select * from area where id = '" + ZB_MainActivity.CURRENT_AREA_id + "'", null);
                    if (area_list != null && area_list.size() > 0) {
                        areamap = area_list.get(0);
                        Constant.debugLog("areamap" + areamap.toString());
                        if (areamap.get("point_x_back") != null && areamap.get("point_y_back") != null && areamap.get("derection") != null) {
                            RobotNavigationHelper.getInstance().sendGoal(Double.parseDouble(areamap.get("point_x_back").toString().trim())
                                    , Double.parseDouble(areamap.get("point_y_back").toString().trim()),
                                    Double.parseDouble(areamap.get("derection").toString().trim())*Math.PI/180);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            RobotNavigationHelper.getInstance().startNav(new CallBack<NavigationResult>() {
                                @Override
                                public void call(NavigationResult data) {
                                    Constant.debugLog("开始导航-->" + data.getCode() +
                                            "isSuccess" + data.isSuccess());
                                }
                            });
                        }
                    }
                }else{
                    if (list.size() > 1) {
                        if (ZB_RobotDialog.loop == 2) {
                                LsCurrent = 0;
                                ServerSocketUtil.sendLSList(list);
                        }
                    }
                }
            }
        }).start();
    }
    public static void sendlist(final String IP){
        Constant.debugLog("socketList  sendlist"+ IP);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map CurrentMap = null;
                int numberid = 0;
                int Cunrrent = 0;
                for (int i = 0,size = socketList.size();i<size; i++) {
                    if (socketList.get(i).get("ip").equals(IP)) {
                        numberid = i;
                        Cunrrent = (int) socketList.get(numberid).get("Cunrrent");
                        break;
                    }
                }
                CurrentMap = socketList.get(numberid);
                Map deskmap,areamap;
                int areaid;
                if(CurrentMap.get("list")!=null) {
                    List list = (List) CurrentMap.get("list");
                    if (list != null && list.size() > 0) {
                        if (Cunrrent == END) {
                            areaid = (int) socketList.get(numberid).get("areaid");
                            Constant.debugLog("areaid" + areaid);
                            List<Map> area_list = robotDBHelper.queryListMap("select * from area where id = '" + areaid + "'", null);
                            if (area_list != null && area_list.size() > 0) {
                                areamap = area_list.get(0);
                                Constant.debugLog("areamap" + areamap.toString());
                                if (areamap.get("point_x_back") != null && areamap.get("point_y_back") != null && areamap.get("derection") != null) {
                                    Protocol.coordinate_x = Integer.valueOf(areamap.get("point_x_back").toString().trim()) * 100;
                                    Protocol.coordinate_y = Integer.valueOf(areamap.get("point_y_back").toString().trim()) * 100;
                                    Protocol.orientation = 450 - Integer.valueOf(areamap.get("derection").toString().trim());
                                    if (Protocol.orientation > 360) {
                                        Protocol.orientation -= 360;
                                    }
                                    try {
                                        out.write(Protocol.getSendData(Protocol.COORDINATE_CONFIG, Protocol.getCommandDataByte(Protocol.CONFIG_COORDINATE)));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Constant.debugLog("areamap Protocol.coordinate_x" + Protocol.coordinate_x + "Protocol.coordinate_y" +
                                    Protocol.coordinate_y + "Protocol.orientation" + Protocol.orientation);
                        } else {
                            try {
                                List<Map> desk_list = robotDBHelper.queryListMap("select * from desk where id = '" + list.get(Cunrrent) + "'", null);
                                if (desk_list != null && desk_list.size() > 0) {
                                    deskmap = desk_list.get(0);
                                    Constant.debugLog("areamap Protocol.coordinate_x" + Protocol.coordinate_x + "Protocol.coordinate_y" +
                                            Protocol.coordinate_y + "Protocol.orientation" + Protocol.orientation);
                                    if (deskmap.get("pointx") != null && deskmap.get("pointy") != null && deskmap.get("derection") != null
                                            && deskmap.get("waittime") != null) {
                                        areaid = (int) deskmap.get("area");
                                        Protocol.coordinate_x = (int) (Float.valueOf(deskmap.get("pointx").toString().trim()) * 100);
                                        Protocol.coordinate_y = (int) (Float.valueOf(deskmap.get("pointy").toString().trim()) * 100);
                                        Protocol.orientation = 450 - Integer.valueOf(deskmap.get("derection").toString().trim());
                                        Protocol.wait_time = Integer.valueOf(deskmap.get("waittime").toString().trim());
                                        socketList.get(numberid).put("waittime", Integer.valueOf(deskmap.get("waittime").toString().trim()));
                                        Constant.debugLog("socketlist"+socketList.get(numberid).get("waittime"));
                                        socketList.get(numberid).put("areaid", areaid);
                                        if (Protocol.orientation > 360) {
                                            Protocol.orientation -= 360;
                                        }
                                        out.write(Protocol.getSendData(Protocol.COORDINATE_RUN, Protocol.getCommandDataByte(Protocol.RUN_COORDINATE)));
                                    }
                                    Constant.debugLog("areamap Protocol.coordinate_x" + Protocol.coordinate_x + "Protocol.coordinate_y" +
                                            Protocol.coordinate_y + "Protocol.orientation" + Protocol.orientation);
                                }
                            } catch (Exception e) {
                                Constant.debugLog(e.toString());
                            }
                        }
                    }
                }
            }
        }).start();
    }
}
