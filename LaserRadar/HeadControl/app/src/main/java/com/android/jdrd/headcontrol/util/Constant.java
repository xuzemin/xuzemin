package com.android.jdrd.headcontrol.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.android.jdrd.headcontrol.dialog.CustomDialog;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Constant {

    //wifi静态ip参数
    static final String dns1 = "192.168.1.1";
    static final String dns2 = "192.168.0.1";
    static String gateway = "";
    static int prefix = 24;
    public static final int SCALE_NUMBER = 150;
    static String IP = "";
    static final String IPLast = "106";
    static String isConnectSocket = "";
    public static final String wifiname = "TimeBox_8b319a";
    public static final String password = "12345678";
//    public static final String wifiname = "HUAWEI-GDRD";
//    public static final String password = "88391477";
    //测试用WI-FI
//    public static String wifiname = "GDRD-3F";
//    public static String password = "88391477";
//    public static String Reason = "reason";
//    public static double Current_x = 0;
    public static int CURRENTINDEX = 0;
    public static int CURRENTINDEX_MAP = 0;
    public static boolean DIALOG_SHOW = false;
    //    public static double Current_y = 0;
//    public static double Current_x_sur = 0;
//    public static double Current_y_sur = 0;
//    private static float Current_degree = 90;
    public static int ServerPort = 12345;
    public static String ip_bigScreen = "/192.168.88.188";
    public static String ip_ros = "/192.168.88.101";
    public static float Scale = 1 ;
    private static final boolean isDebug = true;
    private static final String TAG = "HeadControl";
    public static String filePath = "data/data/com.android.jdrd.headcontrol/cache/map.xml";
    public static String Type = "type";
    public static String Function = "function";
    public static String Data = "data";
    public static String Command = "command";
    public static String State = "state";
    //	public static String Param = "param";
//    public static String Walk = "walk";
    public static String Navigation = "navigation";
    public static String Peoplesearch = "peoplesearch";
    //    public static String Turn = "turn";
    public static String StopSearch = "stop";
    public static String Result = "result";
    //    public static String Distance = "distance";
//    public static String Degree = "degree";
    public static String Camera = "3dcamera";
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
    public  void showWarntext(Context context, final Handler handler){
        CustomDialog dialog = new CustomDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.builder.setTitle("提醒")
                .setMessage("是否离开并删除路线规划")
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Constant.debugLog("确定");
                        handler.sendEmptyMessage(1);
                    }
                }).setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                handler.sendEmptyMessage(2);
                Constant.debugLog("取消");
            }
        }).create().show();
    }
//    public static void showWarn(Context context, final Handler handler){
//        CustomDialog dialog = new CustomDialog(context);
//        dialog.builder.setTitle("提醒")
//                .setMessage("正在执行路线,点击确定按钮停止")
//                .setPositiveButton("", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        Constant.debugLog("确定");
//                        handler.sendEmptyMessage(3);
//                    }
//                }).create().show();
//    }
    public void sendCamera(Float scope,Context context){
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

    public void sendBundle(String type,String function,Map data){
        try {
            Gson gson = new Gson();
            Map map = new LinkedHashMap();
            map.put("type", type);
            map.put("function", function);
            map.put("data", data);
            String s = gson.toJson(map);
            ServerSocketUtil.sendDateToClient(s, ip_ros);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void getDegree(){
//        Current_degree = spu.getBean().pose[2];
//    }

    public void sendBundle(String type,String function,String data){
        try {
            Gson gson = new Gson();
            Map map = new LinkedHashMap();
            map.put(Constant.Type, type);
            map.put(Constant.Function, function);
            map.put(Constant.Data, data);
            String s = gson.toJson(map);
            ServerSocketUtil.sendDateToClient(s, ip_ros);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendBundle(String type,String function,Float data){
        try {
            Gson gson = new Gson();
            Map map = new LinkedHashMap();
            map.put(Constant.Type, type);
            map.put(Constant.Function, function);
            map.put(Constant.Data, data);
            String s = gson.toJson(map);
            ServerSocketUtil.sendDateToClient(s, ip_ros);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
