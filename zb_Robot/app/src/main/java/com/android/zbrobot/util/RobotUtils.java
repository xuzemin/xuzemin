package com.android.zbrobot.util;

import android.content.Context;

import com.android.zbrobot.helper.RobotDBHelper;
import com.ls.lsros.callback.CallBack;
import com.ls.lsros.data.provide.bean.ImageInfo;
import com.ls.lsros.data.provide.bean.MapOperationResult;
import com.ls.lsros.data.provide.bean.NavigationResult;
import com.ls.lsros.data.provide.bean.RobotPoseResult;
import com.ls.lsros.data.provide.bean.RobotSensorResult;
import com.ls.lsros.data.provide.bean.RobotStatus;
import com.ls.lsros.helper.ROSConnectHelper;
import com.ls.lsros.helper.RobotInfoHelper;
import com.ls.lsros.helper.RobotMapOperationHelper;
import com.ls.lsros.helper.RobotNavigationHelper;
import com.ls.lsros.websocket.ROSClient;

public class RobotUtils {
    public static int STEP = 0;
    private static RobotUtils robotUtils = null;
    private static ImageInfo imageInfo;
    private static boolean isRunning = false;
    private int width;
    private int height;
    private int getstat;
    private RobotSensorResult robotSensorResult;
    private RobotPoseResult robotPoseResult;
    private RobotStatus robotStatus;
    private static RobotDBHelper robotDBHelper;
    public static RobotUtils getInstance(){
        return robotUtils==null? new RobotUtils() :robotUtils;
    }
    public void Connect(){
        if(isRunning){
            return;
        }
        isRunning = true;

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
        RobotMapOperationHelper.getInstance().getMap(new CallBack<ImageInfo>() {
            @Override
            public void call(final ImageInfo data) {
                Constant.debugLog("请求获取地图结果--->" + data);
                imageInfo = data;
                width = data.getWidth();
                height = data.getHeight();
                RobotMapOperationHelper.getInstance().stopGetMap();
                STEP = 4;
            }
        });
    }

    public void importMap(){
        RobotMapOperationHelper.getInstance().importMap("/home/fa/map/ditu", new CallBack<MapOperationResult>() {
            @Override
            public void call(MapOperationResult mapOperationResult) {
                STEP = 2;
                Constant.debugLog("mapOperationResult"+mapOperationResult);
            }
        });
    }

    public void setInitPose(double x,double y,double z){
        RobotNavigationHelper.getInstance().setInitPose( x, y, z);
        STEP = 5;
    }

    public void setGoal(double x,double y,double z){
        RobotNavigationHelper.getInstance().sendGoal(x, y, z);
    }

    public void startNav(){
        RobotNavigationHelper.getInstance().startNav(new CallBack<NavigationResult>() {
            @Override
            public void call(NavigationResult data) {
                Constant.debugLog( "开始导航-->" + data);
                switch (data.getCode()){

                }
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
        RobotInfoHelper.getInstance().getRobotCurrentStatus(2 * 1000, new
                CallBack<RobotStatus>() {
                    @Override
                    public void call(RobotStatus data) {
                        Constant.debugLog("获取机器人导航状态--->"+data);
                        robotStatus = data;
                    }

                });
        RobotInfoHelper.getInstance().getRobotPose(2 * 1000, new
                CallBack<RobotPoseResult>() {
                    @Override
                    public void call(RobotPoseResult data) {
                        Constant.debugLog("获取机器人位置信息--->" + data);
                        robotPoseResult = data;
                    } });
        RobotInfoHelper.getInstance().getRobotSensor(2 * 1000, new
                CallBack<RobotSensorResult>() {
                    @Override
                    public void call(RobotSensorResult data) {
                        Constant.debugLog("获取机器人传感器信息--->" + data);
                        robotSensorResult = data;
                    } });
    }

    public void stopGetstat(){
        RobotInfoHelper.getInstance().stopGetRobotPose();

        RobotInfoHelper.getInstance().stopGetNaviStatus();

        RobotInfoHelper.getInstance().stopGetRobotSensor();
    }
}
