package com.protruly.floatwindowlib.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvPictureManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.protruly.floatwindowlib.activity.HideActivity;
import com.protruly.floatwindowlib.activity.SettingActivity;
import com.protruly.floatwindowlib.activity.SettingsNewActivity;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.ui.SignalUpdateDialogLayout;
import com.protruly.floatwindowlib.ui.ThemometerLayout;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.SPUtil;

/**
 * 帮助工具类
 * Created by wang on 2017/7/5.
 */

public class MyUtils {
    public static final String client = SystemProperties.get("client.config","EDU");
    public static final String clientBoard = SystemProperties.get("client.board","CV648H_I");
    /**
     * 判断是否在PC界面
     * @return
     */
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
        String action = "com.protruly.floatwindowlib.action.SETTINGS";
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 打开设置界面
     * @param context
     */
    public static void openSettingActivity(Context context){
        String action = "com.protruly.floatwindowlib.action.SETTINGS";
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 关闭设置界面
     */
    public static void closeSettingActivity(){
        if (SettingsNewActivity.mHandler != null){
            Message message = SettingsNewActivity.mHandler.obtainMessage(SettingsNewActivity.KEY_MSG_FINISH);
            SettingsNewActivity.mHandler.sendMessage(message);
        }
    }

    /**
     * 改变串口 UART触控的状态
     * @return
     */
    public static void uartTouchChange(boolean isUartWork){
        try{
            String command = "SetUARTTOUCH_OFF"; // 关闭UART
            if (isUartWork){ // 开启UART
                command = "SetUARTTOUCH_ON";
            }
            TvManager.getInstance().setTvosCommonCommand(command);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 开启或者关闭usb触摸
     */
    public static void openUSBTouch(boolean isOpen){
        Log.d("zhu","currentSource:screenTouchOpen isOpen->" + isOpen);
        if (isOpen){ // 开启
            SystemProperties.set("ctv.usbTouch", "open_usb_touch");
        } else { // 关闭
            SystemProperties.set("ctv.usbTouch", "close_usb_touch");
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
     * 显示温度
     */
    public static void showTemperature(Context context){
        float tmpValue = CmdUtils.getTmpValue();

        boolean isFirst = false;
        ThemometerLayout thmometerWindow = FloatWindowManager.getThmometerWindow();
        if (thmometerWindow == null) {
            thmometerWindow = FloatWindowManager.createThemometerWindow(context.getApplicationContext());
            isFirst = true;
        }

        int visibility = thmometerWindow.getVisibility();
        if (!isFirst && visibility == View.VISIBLE){ // 若当前是显示，则隐藏
            thmometerWindow.setVisibility(View.INVISIBLE);
        } else { // 若当前是隐藏，则显示
            thmometerWindow.setVisibility(View.VISIBLE);
            // 更新进度
            if (ThemometerLayout.mHandler != null){
                Message message = ThemometerLayout.mHandler.obtainMessage(1, tmpValue);
                ThemometerLayout.mHandler.sendMessage(message);

                // 延迟消失
                ThemometerLayout.mHandler.postDelayed(()->{
                    ThemometerLayout thmometer = FloatWindowManager.getThmometerWindow();
                    if (thmometer != null){
                        thmometer.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            }
        }
    }

    /**
     * 显示自定义信号源界面
     */
    public static void showSignalUpdate(Context context){
        boolean isFirst = false;
        SignalUpdateDialogLayout signalUpdateWindow = FloatWindowManager.getSignalUpdateWindow();
        if (signalUpdateWindow == null) {
            signalUpdateWindow = FloatWindowManager.createSignalUpdateWindow(context.getApplicationContext());
            isFirst = true;
        }

        int visibility = signalUpdateWindow.getVisibility();
        if (!isFirst && visibility == View.VISIBLE){ // 若当前是显示，则隐藏
            signalUpdateWindow.setVisibility(View.INVISIBLE);
        } else { // 若当前是隐藏，则显示
            signalUpdateWindow.setVisibility(View.VISIBLE);
        }
    }
}
