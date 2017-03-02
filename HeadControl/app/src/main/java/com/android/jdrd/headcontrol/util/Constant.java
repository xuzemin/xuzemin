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

    public static int ServerPort = 12345;
    public static String ip_bigScreen = "/192.168.1.102";
    public static String ip_ros = "/192.168.1.100";

    public static boolean isDebug = true;
    public static String TAG = "HeadControl";
    public static String filePath = "data/data/com.android.jdrd.headcontrol/cache/map.xml";
    public static String Type = "type";
    public static String Function = "function";
    public static String Data = "data";
    public static String Command = "command";
    public static String State = "state";
    public static String Param = "param";
    public static String Navigation = "navigation";
    public static String Peoplesearch = "peoplesearch";
    public static String StopSearch = "stop";
    public static String Result = "result";
    public static String Camera = "3dcamera";
    private static Constant constant;
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
    public  void showWarn(Context context, final Handler handler){
        CustomDialog dialog = new CustomDialog(context);
        dialog.builder.setTitle("提醒")
                .setMessage("正在执行路线,点击确定按钮停止")
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Constant.debugLog("确定");
                        handler.sendEmptyMessage(3);
                    }
                }).create().show();
    }
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
            ServerSocketUtil.sendDateToClient(s);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBundle(String type,String function,String data){
        try {
            Gson gson = new Gson();
            Map map = new LinkedHashMap();
            map.put(Constant.Type, type);
            map.put(Constant.Function, function);
            map.put(Constant.Data, data);
            String s = gson.toJson(map);
            ServerSocketUtil.sendDateToClient(s);
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
            ServerSocketUtil.sendDateToClient(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
