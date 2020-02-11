package com.protruly.floatwindowlib.control;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.been.NotificationInfo;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.protruly.floatwindowlib.service.UpdateAPKService;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.protruly.floatwindowlib.ui.DownloadingLayout;
import com.protruly.floatwindowlib.ui.NewSignalDialogLayout;
import com.protruly.floatwindowlib.ui.SettingsDialogLayout;
import com.protruly.floatwindowlib.ui.SignalDialogLayout;
import com.protruly.floatwindowlib.ui.ThemometerLayout;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.DrawConsts;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static android.content.Context.MODE_PRIVATE;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.protruly.floatwindowlib.service.FloatWindowService.KEY_UPDATE_APP_URL;

/**
 * 创建和控制浮框
 *
 * @author wang
 * @time on 2017/3/10.
 */
public class FloatWindowManager {

    // 菜单悬浮窗
    private static ControlMenuLayout menuWindow;
    private static LayoutParams menuParams;

    // 左边菜单悬浮窗
    private static ControlMenuLayout menuWindowLeft;
    private static LayoutParams menuParamsLeft;

    private static DownloadingLayout downloadWindow;
    private static LayoutParams downloadParams;
    // 绘制下载进度条的悬浮窗

    // 绘制温度计的悬浮窗
    private static ThemometerLayout thmometerWindow;
    private static LayoutParams thmometerParams;

    // 信号的悬浮窗
    private static SignalDialogLayout signalDialog;
    private static NewSignalDialogLayout newSignalDialog;
    private static LayoutParams signalParams;
    private static LayoutParams newSignalParams;

    // 设置的悬浮窗
    private static SettingsDialogLayout settingsDialog;
    private static LayoutParams settingsDialogParams;


    private static WindowManager mWindowManager;// 用于控制在屏幕上添加或移除悬浮窗

    public static String updateAPPUrl;// 下载apk的URL
    public static String updateAPPMd5;// 下载apk的md5

    // 上一次屏幕方向,false默认为横屏LANDSCAPE；true为竖屏PORTRAIT
    public static boolean lastScreenOrientation = false;

    /**
     * 创建左边的控制菜单
     *
     * @param context
     */
    public static ControlMenuLayout createMenuWindowLeft(Context context){
        WindowManager windowManager = getWindowManager(context);

        // 判断是否为横竖屏
        boolean currentOrientation = AppUtils.isScreenOrientationPortrait(context);
        if (lastScreenOrientation != currentOrientation){
            LogUtils.d("屏幕方向不一致时，重新布局");
            lastScreenOrientation = currentOrientation;
            menuParamsLeft = null;// 竖屏时，重新布局
        }

        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (menuWindowLeft == null) {
            menuWindowLeft = new ControlMenuLayout(context, false);
            initMenuLeftParams(context, screenWidth, screenHeight);

            windowManager.addView(menuWindowLeft, menuParamsLeft);
        } else {
            initMenuLeftParams(context, screenWidth, screenHeight);
            windowManager.updateViewLayout(menuWindowLeft, menuParamsLeft);
        }

        return menuWindowLeft;
    }



    /**
     * 移动工具栏位置
     * @param context
     * @param isRight
     * @param width
     * @param height
     */
    private static void updateMenuWindow(Context context, boolean isRight, int width, int height){
        if (menuWindow == null || menuParams == null){
            return;
        }

        if (menuWindowLeft == null || menuParamsLeft == null){
            return;
        }

        // 更新位置
        WindowManager windowManager = getWindowManager(context);
        if (isRight){ // 右边
            menuParams.width = width;
            menuParams.height = height;

            Log.i("gyx","menuWindow.getY();="+menuWindow.getY());
            windowManager.updateViewLayout(menuWindow, menuParams);
        } else { // 左边
            menuParamsLeft.width = width;
            menuParamsLeft.height = height;
            windowManager.updateViewLayout(menuWindowLeft, menuParamsLeft);
        }
    }

    /**
     * 移动工具栏位置
     * @param context
     * @param isRight
     * @param isShrink
     */
    public static void showMenuWindow(Context context, boolean isRight, boolean isShrink){
        WindowManager windowManager = getWindowManager(context);
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (isShrink){ // 收缩
            updateMenuWindow(context, isRight, 78, screenHeight);
        } else { // 展开
            updateMenuWindow(context, isRight, 78, screenHeight);
        }
    }

    /**
     * 移动工具栏位置
     * @param context
     * @param x
     * @param y
     */
    public static void updateMenuWindow(Context context, int x, int y){
        if (menuWindow == null || menuParams == null){
            return;
        }

        if (menuWindowLeft == null || menuParamsLeft == null){
            return;
        }

        // 更新位置
        WindowManager windowManager = getWindowManager(context);
        menuParamsLeft.y = y;
        windowManager.updateViewLayout(menuWindowLeft, menuParamsLeft);
        menuParams.y = y;
        windowManager.updateViewLayout(menuWindow, menuParams);

        // 保存Y坐标信息
	    SPUtil.saveData(context, DrawConsts.LAST_POINT_Y_KEY, menuParams.y);
    }

    /**
     *
     * @param screenWidth
     * @param screenHeight
     */
    private static void initMenuLeftParams(Context context, int screenWidth, int screenHeight){
        if (menuParamsLeft == null) {
            menuParamsLeft = new LayoutParams();
            menuParamsLeft.x = 0;
            // 默认位置
            int defaultY = 294;
            menuParamsLeft.y = (int) SPUtil.getData(context, DrawConsts.LAST_POINT_Y_KEY, defaultY);
            updateDy();
            menuParamsLeft.type = LayoutParams.TYPE_PHONE;

            // 所有其它程序是可点击的，悬浮窗不获取焦点
            menuParamsLeft.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;
            menuParamsLeft.format = PixelFormat.RGBA_8888;
            menuParamsLeft.gravity = Gravity.LEFT | Gravity.TOP;
            menuParamsLeft.width = ControlMenuLayout.viewWidth;
            menuParamsLeft.height = ControlMenuLayout.viewHeight;
        }
    }

    static int lastY;

    static public void updateDy() {
        lastY = menuParamsLeft.y;
    }

    public static void updateMenuLeftParams(Context context, int y) {
        if (menuParamsLeft != null) {
            // 默认位置
            menuParamsLeft.y = lastY + y;
            getWindowManager(context).updateViewLayout(menuWindowLeft, menuParamsLeft);
        }
    }

    public static void updateMenuParams(Context mContext, int dy) {
        if (menuParamsLeft != null) {
            // 默认位置
            menuParams.y = lastY + dy;
            getWindowManager(mContext).updateViewLayout(menuWindow, menuParams);
        }

    }

    /**
     * 将左边的控制菜单从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeMenuWindowLeft(Context context) {
        MyUtils.checkUSB(true);
        if (menuWindowLeft != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(menuWindowLeft);
            menuParamsLeft = null;
            menuWindowLeft = null;
        }
    }

    /**
     * 创建右边的控制菜单
     *
     * @param context
     */
    public static ControlMenuLayout createMenuWindow(Context context){
        WindowManager windowManager = getWindowManager(context);

        // 判断是否为横竖屏
        boolean currentOrientation = AppUtils.isScreenOrientationPortrait(context);
        if (lastScreenOrientation != currentOrientation){
            LogUtils.d("屏幕方向不一致时，重新布局");
            lastScreenOrientation = currentOrientation;
            menuParams = null;// 竖屏时，重新布局
        }

        // 初始化布局参数
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        // 初始化UI
        if (menuWindow == null) {
            menuWindow = new ControlMenuLayout(context, true);
            initMenuRightParams(context, screenWidth, screenHeight);
            windowManager.addView(menuWindow, menuParams);
        } else {
            initMenuRightParams(context, screenWidth, screenHeight);
            windowManager.updateViewLayout(menuWindow, menuParams);
        }

        return menuWindow;
    }

    /**
     *
     * @param screenWidth
     * @param screenHeight
     */
    private static void initMenuRightParams(Context context, int screenWidth, int screenHeight){
        if (menuParams == null) {
            menuParams = new LayoutParams();
            menuParams.x =  screenWidth;
	        // 默认位置
	        int defaultY = (int)((screenHeight / 4.0f)) + 24;
            menuParams.y = (int)SPUtil.getData(context, DrawConsts.LAST_POINT_Y_KEY, defaultY);
            menuParams.type = LayoutParams.TYPE_PHONE;

            // 所有其它程序是可点击的，悬浮窗不获取焦点
            menuParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;
            menuParams.format = PixelFormat.RGBA_8888;
            menuParams.gravity = Gravity.LEFT | Gravity.TOP;
            menuParams.width = ControlMenuLayout.viewWidth;
            menuParams.height = ControlMenuLayout.viewHeight;
        }
    }




    /**
     * 将控制菜单从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeMenuWindow(Context context) {
        MyUtils.checkUSB(true);
        if (menuWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(menuWindow);
            menuParams = null;
            menuWindow = null;
        }
    }

    /**
     * 创建一个进度条悬浮窗。位置为屏幕正中间。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static ThemometerLayout createThemometerWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);

        if (thmometerWindow == null) {
            thmometerWindow = new ThemometerLayout(context);
            if (thmometerParams == null) {
                thmometerParams = new LayoutParams();
                thmometerParams.x = 0;
                thmometerParams.y = 0;
                thmometerParams.type = LayoutParams.TYPE_PHONE;

                // 所有其它程序是可点击的，悬浮窗不获取焦点
                thmometerParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

                thmometerParams.format = PixelFormat.RGBA_8888;
                thmometerParams.gravity = Gravity.CENTER;
                thmometerParams.width = ScreenUtils.dip2px(context, 200);//UpdateLayout.viewWidth;
                thmometerParams.height = ScreenUtils.dip2px(context, 150);//UpdateLayout.viewHeight;
            }
            windowManager.addView(thmometerWindow, thmometerParams);
        }

        return thmometerWindow;
    }

    /**
     * 将窗口从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeThmometerWindow(Context context) {
        MyUtils.checkUSB(true);
        if (thmometerWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(thmometerWindow);
            thmometerWindow = null;
        }
    }

    /**
     * 创建一个信号源悬浮框。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static SignalDialogLayout createSignalDialog(Context context, boolean isRight) {
        WindowManager windowManager = getWindowManager(context);

        // 初始化布局参数
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        // 初始化UI
        if (signalDialog == null) {
            signalDialog = new SignalDialogLayout(context);
            initSignalParams(context, screenWidth, screenHeight, isRight);
            windowManager.addView(signalDialog, signalParams);
        } else {
            initSignalParams(context, screenWidth, screenHeight, isRight);
            windowManager.updateViewLayout(signalDialog, signalParams);
        }

        return signalDialog;
    }

    public static NewSignalDialogLayout createNewSignalDialog(Context context, boolean isRight) {
        WindowManager windowManager = getWindowManager(context);

        // 初始化布局参数
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        // 初始化UI
        if (newSignalDialog == null) {
            newSignalDialog = new NewSignalDialogLayout(context);
            initNewSignalParams(context, screenWidth, screenHeight, isRight);
            windowManager.addView(newSignalDialog, newSignalParams);
        } else {
            initNewSignalParams(context, screenWidth, screenHeight, isRight);
            windowManager.updateViewLayout(newSignalDialog, newSignalParams);
        }

        return newSignalDialog;
    }


    private static void initSignalParams(Context context, int screenWidth, int screenHeight, boolean isRight){
        if (signalParams == null) {
            signalParams = new LayoutParams();
            if (isRight){
                signalParams.x = screenWidth - ScreenUtils.dip2px(context, 150);
            } else {
                signalParams.x = ScreenUtils.dip2px(context, 57);
            }
            int defaultY = (int)((screenHeight / 4.0f));
            signalParams.y = ScreenUtils.dip2px(context, 115) + (int)SPUtil.getData(context, DrawConsts.LAST_POINT_Y_KEY, defaultY);
            signalParams.type = LayoutParams.TYPE_PHONE;


            // 所有其它程序是可点击的，悬浮窗不获取焦点
            signalParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

            signalParams.format = PixelFormat.RGBA_8888;
            signalParams.gravity = Gravity.LEFT | Gravity.TOP;
            signalParams.width = ScreenUtils.dip2px(context, 93.3F);
            signalParams.height = ScreenUtils.dip2px(context, 340);
        }
    }
    private static void initNewSignalParams(Context context, int screenWidth, int screenHeight, boolean isRight){
        if (newSignalParams == null) {
            newSignalParams = new LayoutParams();
            Log.i("gyx","isRight="+isRight+"screenWidth="+screenWidth);
            if (isRight){
                newSignalParams.x = screenWidth - 450;
            } else {
                newSignalParams.x = ScreenUtils.dip2px(context, 57);
            }
            int defaultY = (int)((screenHeight / 4.0f));
            newSignalParams.y =  menuParams.y-10;
            newSignalParams.type = LayoutParams.TYPE_PHONE;


            // 所有其它程序是可点击的，悬浮窗不获取焦点
            newSignalParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

            newSignalParams.format = PixelFormat.RGBA_8888;
            newSignalParams.gravity = Gravity.LEFT | Gravity.TOP;
            newSignalParams.width = 340;
            newSignalParams.height = WRAP_CONTENT;
        }
    }

    /**
     * 显示对话框
     * @param context
     */
    public static void updateSignalDialog(Context context, boolean isRight){
        if (signalDialog == null || signalParams == null){
            return;
        }

        // 更新位置
        WindowManager windowManager = getWindowManager(context);
	    int screenWidth = windowManager.getDefaultDisplay().getWidth();
        if (isRight){
            signalParams.x = screenWidth - ScreenUtils.dip2px(context, 150);
        } else {
            signalParams.x = ScreenUtils.dip2px(context, 57);
        }

        // 初始化布局参数
        signalParams.y = ScreenUtils.dip2px(context, 115) + menuParams.y;
        windowManager.updateViewLayout(signalDialog, signalParams);
    }

    /**
     * 显示对话框
     * @param context
     */
    public static void updateNewSignalDialog(Context context, boolean isRight){
        if (newSignalDialog == null || newSignalParams == null){
            return;
        }

        // 更新位置
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        Log.i("gyx","isRight="+isRight+"screenWidth="+screenWidth);
        if (isRight){
            newSignalParams.x = screenWidth - 450;
        } else {
            newSignalParams.x = ScreenUtils.dip2px(context, 57);
        }

        // 初始化布局参数
        newSignalParams.y =  menuParams.y-10;
        windowManager.updateViewLayout(newSignalDialog, newSignalParams);
    }

    /**
     * 将窗口从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeSignalDialog(Context context) {
        MyUtils.checkUSB(true);
        if (signalDialog != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(signalDialog);
            signalDialog = null;
        }
        if (newSignalDialog != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(newSignalDialog);
            newSignalDialog = null;
        }
    }

    /**
     * 创建一个设置悬浮框。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static SettingsDialogLayout createSettingsDialog(Context context, boolean isRight) {
        WindowManager windowManager = getWindowManager(context);

        // 初始化布局参数
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        // 初始化UI
        if (settingsDialog == null) {
            settingsDialog = new SettingsDialogLayout(context);
            initSettingsParams(context, screenWidth, screenHeight, isRight);
            windowManager.addView(settingsDialog, settingsDialogParams);
        } else {
            initSettingsParams(context, screenWidth, screenHeight, isRight);
            windowManager.updateViewLayout(settingsDialog, settingsDialogParams);
        }

        return settingsDialog;
    }

    private static void initSettingsParams(Context context, int screenWidth, int screenHeight, boolean isRight){
        if (settingsDialogParams == null) {
            settingsDialogParams = new LayoutParams();
            if (isRight){
                settingsDialogParams.x = screenWidth - ScreenUtils.dip2px(context, 0);
            } else {
                settingsDialogParams.x = ScreenUtils.dip2px(context, 0);
            }

            int defaultY = (int)((screenHeight / 4.0f));
            settingsDialogParams.y = (int)SPUtil.getData(context, DrawConsts.LAST_POINT_Y_KEY, defaultY)
                    - ScreenUtils.dip2px(context, 50);
            settingsDialogParams.type = LayoutParams.TYPE_PHONE;

            // 所有其它程序是可点击的，悬浮窗不获取焦点
            settingsDialogParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

            settingsDialogParams.format = PixelFormat.RGBA_8888;
            settingsDialogParams.gravity = Gravity.LEFT | Gravity.TOP;
            settingsDialogParams.width = ScreenUtils.dip2px(context, 333);
            settingsDialogParams.height = ScreenUtils.dip2px(context, 582);
        }
    }

    /**
     * 显示对话框
     * @param context
     */
    public static void updateSettingsDialog(Context context, boolean isRight){
        if (settingsDialog == null || settingsDialogParams == null){
            return;
        }

        // 更新位置
        WindowManager windowManager = getWindowManager(context);
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int defaultY = (int)((screenHeight / 4.0f));
        if (isRight){
            settingsDialogParams.x = screenWidth - ScreenUtils.dip2px(context, 0);
        } else {
            settingsDialogParams.x = ScreenUtils.dip2px(context, 0);
        }

        // 初始化布局参数
        settingsDialogParams.y = (int)SPUtil.getData(context, DrawConsts.LAST_POINT_Y_KEY, defaultY)
                - ScreenUtils.dip2px(context, 50);
        windowManager.updateViewLayout(settingsDialog, settingsDialogParams);
    }

    /**
     * 将窗口从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeSettingsDialog(Context context) {
        MyUtils.checkUSB(true);
        if (settingsDialog != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(settingsDialog);
            settingsDialog = null;
        }
    }

    /**
     * 创建一个进度条悬浮窗。位置为屏幕正中间。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static DownloadingLayout createDownloadWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);

        if (downloadWindow == null) {
            downloadWindow = new DownloadingLayout(context);
            if (downloadParams == null) {
                downloadParams = new LayoutParams();
                downloadParams.x = 0;
                downloadParams.y = 0;
                downloadParams.type = LayoutParams.TYPE_PHONE;

                // 所有其它程序是可点击的，悬浮窗不获取焦点
                downloadParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;

                downloadParams.format = PixelFormat.RGBA_8888;
                downloadParams.gravity = Gravity.CENTER;
                downloadParams.width = ScreenUtils.dip2px(context, 300);//UpdateLayout.viewWidth;
                downloadParams.height = ScreenUtils.dip2px(context, 60);//UpdateLayout.viewHeight;
            }
            windowManager.addView(downloadWindow, downloadParams);
        }



        return downloadWindow;
    }

    /**
     * 将升级窗口从屏幕上移除。
     *
     * @param context
     *            必须为应用程序的Context.
     */
    public static void removeDownloadWindow(Context context) {
        if (downloadWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(downloadWindow);
            downloadWindow = null;
        }
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return menuWindow != null || menuWindowLeft != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context
     *            必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    public static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static ControlMenuLayout getMenuWindow() {
        return menuWindow;
    }

    public static ControlMenuLayout getMenuWindowLeft() {
        return menuWindowLeft;
    }

    public static DownloadingLayout getDownloadWindow() {
        return downloadWindow;
    }

    public static ThemometerLayout getThmometerWindow() {
        return thmometerWindow;
    }

    public static SignalDialogLayout getSignalDialog() {
        return signalDialog;
    }

    public static NewSignalDialogLayout getNewSignalDialog() {
        return newSignalDialog;
    }

    public static SettingsDialogLayout getSettingsDialog() {
        return settingsDialog;
    }

    /**
     * 启动升级
     *
     * @param context
     */
    public static void startUpdateService(Context context){
        // 开启服务进行更新
        Intent intent = new Intent(context, UpdateAPKService.class);
        intent.putExtra(KEY_UPDATE_APP_URL, updateAPPUrl);
        context.startService(intent);
    }

    public static ArrayList<NotificationInfo> list = new ArrayList<>();

    public static ArrayList<NotificationInfo> getNotificationList() {
        return list;
    }

    public static ArrayList<String> keyList = new ArrayList<>();

    public static void addSBN(Context context, StatusBarNotification sbn) {
        Log.i("gyx", "addSBN");
        String key = sbn.getKey();
        boolean isUpdate = keyList.contains(key);
        if (isUpdate) {
            for (int i = 0; i < list.size(); i++) {
                Log.i("gyx", "list.size=" + list.size());
                NotificationInfo notificationInfo = list.get(i);
                if (notificationInfo.getSbn().getKey().equals(sbn.getKey())) {
                    NotificationInfo updateInfo = new NotificationInfo(context, sbn);
                    list.set(i, updateInfo);
                }
            }
        } else {
            Log.i("gyx", "not Update");
            NotificationInfo notificationInfo = new NotificationInfo(context, sbn);
            list.add(0, notificationInfo);
            keyList.add(sbn.getKey());
        }

        context.sendBroadcast(new Intent("com.ctv.UPDATE_NOTIFICATION"));
    }

    public static void delSBN(Context context, StatusBarNotification sbn) {
        for (int i = 0; i < list.size(); i++) {
            NotificationInfo notificationInfo = list.get(i);
            Log.i("gyx", "key1 =" + notificationInfo.getSbn().getKey());
            Log.i("gyx", "key2=" + sbn.getKey());
            if (notificationInfo.getSbn().getKey().equals(sbn.getKey())) {
                list.remove(i);
                for (int j = 0; j < keyList.size(); j++) {
                    String key = keyList.get(j);
                    if (key.equals(sbn.getKey())) {
                        keyList.remove(j);
                    }
                }

            }

        }
        context.sendBroadcast(new Intent("com.ctv.UPDATE_NOTIFICATION"));
    }

    public static void updateNotificationList(Context context, ArrayList<NotificationInfo> mList, String key) {
        list = mList;
        keyList.remove(key);
        context.sendBroadcast(new Intent("com.ctv.UPDATE_NOTIFICATION"));
    }

    public static void clearALL(Context context) {
        list.clear();
        keyList.clear();
        context.sendBroadcast(new Intent("com.ctv.UPDATE_NOTIFICATION"));
    }

}
