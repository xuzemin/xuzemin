package com.android.zbrobot.util;

import android.graphics.Bitmap;
import com.ls.lsros.callback.CallBack;
import com.ls.lsros.data.provide.bean.ImageInfo;
import com.ls.lsros.data.provide.bean.MapOperationResult;
import com.ls.lsros.data.provide.bean.NavigationResult;
import com.ls.lsros.data.provide.bean.RobotNaviStatusResult;
import com.ls.lsros.data.provide.bean.RobotPoseResult;
import com.ls.lsros.data.provide.bean.RobotSensorResult;
import com.ls.lsros.helper.ROSConnectHelper;
import com.ls.lsros.helper.RobotInfoHelper;
import com.ls.lsros.helper.RobotMapOperationHelper;
import com.ls.lsros.helper.RobotNavigationHelper;
import com.ls.lsros.websocket.ROSClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RobotUtils {
    public static int STEP = 0;
    private static RobotUtils robotUtils = null;
    public static ImageInfo imageInfo;
    public static boolean isRunning = false;
    public static String fileName = "/data/data/com.android.zbrobot/cache/map.JPEG";
    public static double originX;
    public static double originY;
    private int width;
    private int height;
    public RobotSensorResult robotSensorResult;
    public RobotPoseResult robotPoseResult;
    public static RobotNaviStatusResult robotStatus ;
    public static RobotUtils getInstance(){
        return robotUtils==null? new RobotUtils() :robotUtils;
    }
    public void Connect(){
        if(isRunning){
            return;
        }
        isRunning = true;
        ROSConnectHelper.getInstance().disconnect();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ROSConnectHelper.getInstance().connect("192.168.106.1", 8080, new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                Constant.debugLog( "连接至机器人服务器成功!");
                STEP = 1;
                isRunning = false;
            }

            @Override
            public void onDisconnect(boolean b, String s, int i) {
                Constant.debugLog( "断开机器人服务器连接");
                STEP = 3;
                isRunning = false;
            }

            @Override
            public void onError(Exception e) {
                STEP = 3;
                isRunning = false;
                Constant.debugLog("Error"+e.toString());
                Constant.debugLog("与机器人服务器连接失败");
            }
        });
    }

    public void setImageInfo(){
        isRunning = true;
        RobotMapOperationHelper.getInstance().getMap(new CallBack<ImageInfo>() {
            @Override
            public void call(final ImageInfo data) {
                Constant.debugLog("请求获取地图结果--->" + data);
                imageInfo = data;
                width = data.getWidth();
                height = data.getHeight();
                originX = data.getOriginX();
                originY = data.getOriginY();
                saveBitmap(imageInfo.getBitmap());
                RobotMapOperationHelper.getInstance().stopGetMap();
                STEP = 4;
                isRunning =false;

            }
        });
    }

    public void importMap(){
        isRunning = true;
        RobotMapOperationHelper.getInstance().importMap("/home/fa/map/1", new CallBack<MapOperationResult>() {
            @Override
            public void call(MapOperationResult mapOperationResult) {
                STEP = 2;
                Constant.debugLog("mapOperationResult"+mapOperationResult);
            }
        });
        isRunning = false;
    }

    public void setInitPose(double x,double y,double z){
        isRunning = true;
        RobotNavigationHelper.getInstance().setInitPose( x, y, z);
        STEP = 5;
        isRunning = false;
    }

    public void setGoal(double x,double y,double z){
        RobotNavigationHelper.getInstance().sendGoal(x, y, z);
    }

    public void startNav(){
        isRunning = true;
        RobotNavigationHelper.getInstance().startNav(new CallBack<NavigationResult>() {
            @Override
            public void call(NavigationResult data) {
                Constant.debugLog( "开始导航-->" + data);
                switch (data.getCode()){

                }
                isRunning = false;
            } });
    }

    public void stopNav(){
        RobotNavigationHelper.getInstance().stopNav(new CallBack<NavigationResult>() {
            @Override
            public void call(NavigationResult data) {
                Constant.debugLog("结束导航-->" + data);
                if(data.isSuccess()){
                    isRunning = false;
                }else{
                    switch (data.getCode()){
                        default:
                            Constant.debugLog("结束导航-->" + data);
                            break;
                    }
                }
            }
        });
    }

    public void startGetstat(){
        RobotNavigationHelper.getInstance().getNaviStatus(1000, new CallBack<RobotNaviStatusResult>() {
            @Override
            public void call(RobotNaviStatusResult robotNaviStatusResult) {
                Constant.debugLog("获取机器人导航状态--->"+robotNaviStatusResult);
                robotStatus = robotNaviStatusResult;
            }
        });
//        RobotInfoHelper.getInstance().getRobotCurrentStatus(2 * 1000, new
//                CallBack<RobotStatus>() {
//                    @Override
//                    public void call(RobotStatus data) {
//                        Constant.debugLog("获取机器人导航状态--->"+data);
//                    }
//
//                });
//        RobotInfoHelper.getInstance().getRobotPose(2 * 1000, new
//                CallBack<RobotPoseResult>() {
//                    @Override
//                    public void call(RobotPoseResult data) {
//                        Constant.debugLog("获取机器人位置信息--->" + data);
//                        robotPoseResult = data;
//
//
//                    } });
//        RobotInfoHelper.getInstance().getRobotSensor(2 * 1000, new
//                CallBack<RobotSensorResult>() {
//                    @Override
//                    public void call(RobotSensorResult data) {
//                        Constant.debugLog("获取机器人传感器信息--->" + data);
//                        robotSensorResult = data;
//                    } });
        STEP = 6;

    }

    public void stopGetstat(){
        RobotInfoHelper.getInstance().stopGetRobotPose();

        RobotInfoHelper.getInstance().stopGetNaviStatus();

        RobotInfoHelper.getInstance().stopGetRobotSensor();
    }



    public void saveBitmap(Bitmap bitmap){
        File file ;
        file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out))
            {
                Constant.debugLog("file"+file.getAbsolutePath());
                out.flush();
                out.close();
//// 插入图库
//                MediaStore.Images.Media.insertImage(ZB_MainActivity.getContentResolver(), file.getAbsolutePath(), bitName, null);

            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Constant.debugLog(e.toString());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Constant.debugLog(e.toString());
        }

    }

}
