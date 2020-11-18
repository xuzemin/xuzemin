package com.protruly.floatwindowlib.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.protruly.floatwindowlib.activity.HideActivity;
import com.protruly.floatwindowlib.activity.SettingActivity;
import com.protruly.floatwindowlib.activity.SettingNewActivity;
import com.protruly.floatwindowlib.control.ActivityCollector;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.yinghe.whiteboardlib.utils.AppUtils;

/**
 * 帮助工具类
 * Created by wang on 2017/7/5.
 */

public class MyUtils {

    /**
     * 判断是否在PC界面
     * @return
     */
    public static boolean isOpencheck = true;

    public static boolean IsPc(){
        try {
            if (TvCommonManager.getInstance().getCurrentTvInputSource()
                    == TvCommonManager.INPUT_SOURCE_HDMI3){// 正在PC界面中
                LogUtils.d("在PC界面中,白板按钮隐藏");
                return true;
            } else {// 不在PC界面中
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 关闭和打开PC通道
     * @param isOpen
     */
    public static void openAndClosePCTouch(boolean isOpen){
        // 若是在PC界面，关闭PC触摸
        if (IsPc()) {
            try {
//                CtvManager.getInstance().setTouchPanelUSBEnable(isOpen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示HideActivity
     * @param context
     */
    public static void openHideActivity(Context context, boolean isRight){
        if (IsPc()){
            LogUtils.d("显示HideActivity");

            // 显示隐藏的HideActivity
            String action = "com.protruly.floatwindowlib.action.HIDE_DIALOG";
            Intent intent = new Intent(action);
            intent.putExtra("isRight", isRight);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }



    public static void checkUSB(Context context,boolean isOpen){
        //TvCommonManager.getInstance().setUsbTouch(context, isOpen);
        if (isOpen) {
            TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_ON");
        }else{
            TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_OFF");
        }
    }

    public static void resetIO(){
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                TvManager.getInstance().setGpioDeviceStatus(0x67,false);
                Thread.sleep(50);
                TvManager.getInstance().setGpioDeviceStatus(0x67,true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 关闭隐藏Activity
     */
    public static void closeHideActivity(){
        // 关闭隐藏界面
        if (HideActivity.mHandler != null){
            Message message = HideActivity.mHandler.obtainMessage(HideActivity.KEY_MSG_FINISH);
            HideActivity.mHandler.sendMessage(message);
        }
    }

    /**
     * 关闭隐藏Activity
     */
    public static void closeHideActivityNotClosePCTouch(){
        // 关闭隐藏界面
        if (HideActivity.mHandler != null){
            Message message = HideActivity.mHandler.obtainMessage(HideActivity.KEY_MSG_FINISH_NOT_CLOSE_PC_TOUCH);
            HideActivity.mHandler.sendMessage(message);
        }
    }

    /**
     * 打开设置界面
     * @param context
     */
    public static void openSettingActivity(Context context, boolean isRight){
        String action = "com.protruly.floatwindowlib.action.SETTING";
        Intent intent = new Intent(action);
        intent.putExtra("isRight", isRight);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 关闭设置界面
     */
    public static void closeSettingActivity(){
        if (SettingNewActivity.mHandler != null){
            Message message = SettingNewActivity.mHandler.obtainMessage(SettingNewActivity.KEY_MSG_FINISH);
            SettingNewActivity.mHandler.sendMessage(message);
        }
    }

    /**
     * 判断是否支持光感
     * @return
     */
    public static boolean isSupportLightSense(){
        return SystemProperties.get("service.light.sense.enable", "0").equals("1");
    }

    /**
     * 获取版型
     * @return
     */
    public static String getBoard(){
        Log.e("Virtur",SystemProperties.get("ro.product.board", "CV8386H_AH"));
        return SystemProperties.get("ro.product.board", "CV8386H_AH");
    }

}
