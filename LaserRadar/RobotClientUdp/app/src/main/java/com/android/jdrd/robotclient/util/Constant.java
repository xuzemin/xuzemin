package com.android.jdrd.robotclient.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class Constant {

    //wifi静态ip参数
    static final String dns1 = "192.168.1.1";
    static final String dns2 = "192.168.0.1";
    static String gateway = "";
    static int prefix = 24;
    static String IP = "";
    //
    static final String IPLast = "178";
    static String isConnectSocket = "";
//    public static final String wifiname = "TimeBox_8b319a";
//    public static final String password = "12345678";
//    public static final String wifiname = "HUAWEI-GDRD";
//    public static final String password = "88391477";
    //测试用WI-FI01
//    public static String wifiname = "GDRD-3F";
//    public static String password = "88391477";
    //测试用WI-FI02
    public static String wifiname = "tianxianbaobao";
    public static String password = "88391477";
    //当前缓存状态
    //地图状态缓存
    //Socket服务器端口
    public static int ServerPort = 1477;
    public static int ClientPort = 8839;

    public static int ServerUdpPort = 51111;
    public static String ServerUdpIp = "192.168.1.4";
//    public static String ip_bigScreen = "/192.168.88.110";
//    public static String ip_ros = "/192.168.88.101";
//    测试用IP
    //是否打印日志
    private static final boolean isDebug = true;
    //日志标题
    private static final String TAG = "Robot";
    //地图默认配置地图
    public static String filePath = "data/data/com.android.jdrd.headcontrol/cache/map.xml";
    public static int linearWidth;
    //消息解析字段
    public static String Type = "type";
    public static String Function = "function";
    public static String Data = "data";
    public static String Command = "command";
    public static String State = "state";
    //	public static String Param = "param";
//    public static String Walk = "walk";
    public static String Navigation = "navigation";
    public static String Peoplesearch = "peoplesearch";
    public static String Roamsearch = "roamsearch";
    //    public static String Turn = "turn";
    public static String StopSearch = "stop";
    public static String Result = "result";
    //    public static String Distance = "distance";
//    public static String Degree = "degree";
    public static String Camera = "3dcamera";
    public static String Obstacle = "obstacle";

    //唯一对象
    private static Constant constant;
//    private static SerialPortUtil spu  = SerialPortUtil.getInstance();

    public static Constant getConstant(){
        if(constant != null){
            return constant;
        }else {
            constant = new Constant();
            return constant;
        }
    }

    public static void debugLog(String string){
        if(isDebug){
            Log.e(TAG,string);
        }
    }

    public void sendCamera(float scope,Context context){
        Intent intent = new Intent();
        if(scope == 0){
            intent.putExtra("msg", "远");
        }else if(scope == 1){
            intent.putExtra("msg", "中");
        }else if(scope == 2){
            intent.putExtra("msg", "近");
        }else{
            intent.putExtra("msg", "关闭");
        }
        intent.setAction("com.jdrd.CursorSDKExample.TD_CAMERA");
        context.sendBroadcast(intent);
    }


}
