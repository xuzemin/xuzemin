package com.protruly.floatwindowlib.control;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.service.UpdateAPKService;
import com.protruly.floatwindowlib.ui.BacklightLayout;
import com.protruly.floatwindowlib.ui.BottomMenuLayout;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.protruly.floatwindowlib.ui.DownloadingLayout;
import com.protruly.floatwindowlib.ui.DrawContentLayout;
import com.protruly.floatwindowlib.ui.SignalUpdateDialogLayout;
import com.protruly.floatwindowlib.ui.ThemometerLayout;
import com.yinghe.whiteboardlib.utils.ACache;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.DrawConsts;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;

import static com.protruly.floatwindowlib.service.FloatWindowService.KEY_UPDATE_APP_URL;

/**
 * 创建和控制浮框
 *
 * @author wang
 * @time on 2017/3/10.
 */
public class FloatWindowManager {

    // 绘制主界面的悬浮窗
    private static DrawContentLayout contentWindow;
    private static LayoutParams contentParams;

    // 底部菜单悬浮窗
    private static BottomMenuLayout bottomMenuLayout;
    private static LayoutParams bottomMenuParams;

    // 底部背光对话框悬浮窗
    private static BacklightLayout backlightLayout;
    private static LayoutParams backlightParams;

    // 菜单悬浮窗
    private static ControlMenuLayout menuWindow;
    private static LayoutParams menuParams;

    // 左边菜单悬浮窗
    private static ControlMenuLayout menuWindowLeft;
    private static LayoutParams menuParamsLeft;

    // 绘制下载进度条的悬浮窗
    private static DownloadingLayout downloadWindow;
    private static LayoutParams downloadParams;

    // 绘制温度计的悬浮窗
    private static ThemometerLayout thmometerWindow;
    private static LayoutParams thmometerParams;

    // 绘制自定义信号的悬浮窗
    private static SignalUpdateDialogLayout signalUpdateWindow;
    private static LayoutParams signalUpdateParams;

    private static WindowManager mWindowManager;// 用于控制在屏幕上添加或移除悬浮窗

    public static String updateAPPUrl;// 下载apk的URL
    public static String updateAPPMd5;// 下载apk的md5

    // 上一次屏幕方向,false默认为横屏LANDSCAPE；true为竖屏PORTRAIT
    public static boolean lastScreenOrientation = false;


    /**
     * 创建一个画板悬浮窗。位置为屏幕正中间。
     *
     * @param context 必须为应用程序的Context.
     */
    public static DrawContentLayout createContentWindow(Context context, boolean isRight) {
        WindowManager windowManager = getWindowManager(context);

        // 判断是否为横竖屏
        boolean currentOrientation = AppUtils.isScreenOrientationPortrait(context);
        if (lastScreenOrientation != currentOrientation) {
            LogUtils.d("屏幕方向不一致时，重新布局");
            lastScreenOrientation = currentOrientation;
            contentParams = null;// 竖屏时，重新布局
        }

        // 初始化布局参数
        if (contentParams == null) {
            int screenWidth = windowManager.getDefaultDisplay().getWidth();
            int screenHeight = windowManager.getDefaultDisplay().getHeight();

            contentParams = new LayoutParams();
            contentParams.x = 0;
            contentParams.y = 0;
            contentParams.type = LayoutParams.TYPE_PHONE;

            // 所有其它程序是可点击的，悬浮窗不获取焦点
            contentParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;

            contentParams.format = PixelFormat.RGBA_8888;
            contentParams.gravity = Gravity.LEFT | Gravity.TOP;

            // 获得状态栏高度
            int dHeight = ScreenUtils.getStatusBarHeight(context);
            int dWidth = dHeight;
            if (dHeight <= 0) {
                dHeight = 0;//ScreenUtils.dip2px(context, 25);
                dWidth = ScreenUtils.dip2px(context, 25);
            }

            contentParams.width = screenWidth;
            contentParams.height = screenHeight - dHeight;
        }

        // 初始化UI
        if (contentWindow == null) {
            contentWindow = new DrawContentLayout(context);
            windowManager.addView(contentWindow, contentParams);
        } else {
            windowManager.updateViewLayout(contentWindow, contentParams);
        }

        contentWindow.setRight(isRight);
        return contentWindow;
    }

    /**
     * 将控制菜单从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeContentWindow(Context context) {
        if (contentWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(contentWindow);
            contentWindow = null;
            contentParams = null;
        }
    }

    /**
     * 创建一个亮度对话框悬浮窗。位置为屏幕底部。
     *
     * @param context 必须为应用程序的Context.
     */
    public static BacklightLayout createBacklightLayout(Context context) {
        WindowManager windowManager = getWindowManager(context);

        if (backlightLayout == null) {
            backlightLayout = new BacklightLayout(context);
            if (backlightParams == null) {
                backlightParams = new LayoutParams();
                backlightParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

                backlightParams.format = PixelFormat.TRANSLUCENT;
                backlightParams.type = LayoutParams.TYPE_PHONE;

                backlightParams.setTitle(BacklightLayout.class.getSimpleName());
                backlightParams.gravity = Gravity.BOTTOM;
                backlightParams.x = 15;
                backlightParams.y = 200;

                backlightParams.windowAnimations = -1;

                // 初始化布局参数
                backlightParams.width = 600 * 2;//BacklightLayout.viewWidth; // screenWidth
                backlightParams.height = 100 * 2;//BacklightLayout.viewHeight; // ScreenUtils.dip2px(context, 60);
                LogUtils.d("backlightParams width->%s, height->%s", backlightParams.width, backlightParams.height);
            }
            windowManager.addView(backlightLayout, backlightParams);
        }

        return backlightLayout;
    }

    /**
     * 将亮度对话框从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeBacklightLayout(Context context) {
        if (backlightLayout != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(backlightLayout);
            backlightLayout = null;
            backlightParams = null;
        }
    }

    /**
     * 创建一个底部菜单悬浮窗。位置为屏幕底部。
     *
     * @param context 必须为应用程序的Context.
     */
    public static BottomMenuLayout createBottomMenuLayout(Context context) {
        WindowManager windowManager = getWindowManager(context);

        if (bottomMenuLayout == null) {
            bottomMenuLayout = new BottomMenuLayout(context);
            if (bottomMenuParams == null) {
                bottomMenuParams = new LayoutParams();
                bottomMenuParams.x = 0;
                bottomMenuParams.y = 0;
                bottomMenuParams.type = LayoutParams.TYPE_PHONE;

                // 所有其它程序是可点击的，悬浮窗不获取焦点
//                bottomMenuParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | LayoutParams.FLAG_NOT_FOCUSABLE;

                bottomMenuParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

                bottomMenuParams.format = PixelFormat.RGBA_8888;
                bottomMenuParams.gravity = Gravity.BOTTOM;

                // 初始化布局参数
                int screenWidth = windowManager.getDefaultDisplay().getWidth();
                bottomMenuParams.width = BottomMenuLayout.viewWidth; // screenWidth
                bottomMenuParams.height = BottomMenuLayout.viewHeight; // ScreenUtils.dip2px(context, 60);
                LogUtils.d("createBottomMenuLayout width->%s, height->%s", BottomMenuLayout.viewWidth, BottomMenuLayout.viewHeight);
            }
            windowManager.addView(bottomMenuLayout, bottomMenuParams);
        }

        return bottomMenuLayout;
    }

    /**
     * 将窗口从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeBottomMenuLayout(Context context) {
        if (bottomMenuLayout != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bottomMenuLayout);
            bottomMenuLayout = null;
        }
    }

    /**
     * 创建左边的控制菜单
     *
     * @param context
     */
    public static ControlMenuLayout createMenuWindowLeft(Context context) {
        WindowManager windowManager = getWindowManager(context);

        // 判断是否为横竖屏
        boolean currentOrientation = AppUtils.isScreenOrientationPortrait(context);
        if (lastScreenOrientation != currentOrientation) {
            LogUtils.d("屏幕方向不一致时，重新布局");
            lastScreenOrientation = currentOrientation;
            menuParamsLeft = null;// 竖屏时，重新布局
        }

        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (menuWindowLeft == null) {
            menuWindowLeft = new ControlMenuLayout(context, false);
            initMenuLeftParams(context, screenWidth, screenHeight);

            menuWindowLeft.setMenuParams(menuParamsLeft);
            windowManager.addView(menuWindowLeft, menuParamsLeft);
        } else {
            initMenuLeftParams(context, screenWidth, screenHeight);
            windowManager.updateViewLayout(menuWindowLeft, menuParamsLeft);
        }

        return menuWindowLeft;
    }

    /**
     * 移动工具栏位置
     *
     * @param context
     * @param x
     * @param y
     */
    public static void updateMenuWindow(Context context, int x, int y) {
        if (menuWindow == null || menuParams == null) {
            return;
        }

        if (menuWindowLeft == null || menuParamsLeft == null) {
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
     * @param screenWidth
     * @param screenHeight
     */
    private static void initMenuLeftParams(Context context, int screenWidth, int screenHeight) {
        if (menuParamsLeft == null) {
            menuParamsLeft = new LayoutParams();
            menuParamsLeft.x = 0;
            // 默认位置
            int defaultY = (int) ((screenHeight / 4.0f));
            menuParamsLeft.y = (int) SPUtil.getData(context, DrawConsts.LAST_POINT_Y_KEY, defaultY);
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

    /**
     * 将左边的控制菜单从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeMenuWindowLeft(Context context) {
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
    public static ControlMenuLayout createMenuWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);

        // 判断是否为横竖屏
        boolean currentOrientation = AppUtils.isScreenOrientationPortrait(context);
        if (lastScreenOrientation != currentOrientation) {
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

            menuWindow.setMenuParams(menuParams);
            windowManager.addView(menuWindow, menuParams);
        } else {
            initMenuRightParams(context, screenWidth, screenHeight);
            windowManager.updateViewLayout(menuWindow, menuParams);
        }

        return menuWindow;
    }

    /**
     * @param screenWidth
     * @param screenHeight
     */
    private static void initMenuRightParams(Context context, int screenWidth, int screenHeight) {
        if (menuParams == null) {
            menuParams = new LayoutParams();
            menuParams.x = screenWidth;
            // 默认位置
            int defaultY = (int) ((screenHeight / 4.0f));
            menuParams.y = (int) SPUtil.getData(context, DrawConsts.LAST_POINT_Y_KEY, defaultY);
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
     * @param context 必须为应用程序的Context.
     */
    public static void removeMenuWindow(Context context) {
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
     * @param context 必须为应用程序的Context.
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
     * @param context 必须为应用程序的Context.
     */
    public static void removeThmometerWindow(Context context) {
        if (thmometerWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(thmometerWindow);
            thmometerWindow = null;
        }
    }

    /**
     * 创建一个信号自定义悬浮窗。位置为屏幕正中间。
     *
     * @param context 必须为应用程序的Context.
     */
    public static SignalUpdateDialogLayout createSignalUpdateWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);

        if (signalUpdateWindow == null) {
            signalUpdateWindow = new SignalUpdateDialogLayout(context);
            if (signalUpdateParams == null) {
                signalUpdateParams = new LayoutParams();
                signalUpdateParams.x = 0;
                signalUpdateParams.y = 0;
                signalUpdateParams.type = LayoutParams.TYPE_PHONE;

                // 所有其它程序是可点击的，悬浮窗不获取焦点
                signalUpdateParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

                signalUpdateParams.format = PixelFormat.RGBA_8888;
                signalUpdateParams.gravity = Gravity.CENTER;
                signalUpdateParams.width = ScreenUtils.dip2px(context, 200);//UpdateLayout.viewWidth;
                signalUpdateParams.height = ScreenUtils.dip2px(context, 320);//UpdateLayout.viewHeight;
            }
            windowManager.addView(signalUpdateWindow, signalUpdateParams);
        }

        return signalUpdateWindow;
    }

    /**
     * 将窗口从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeSignalUpdateWindow(Context context) {
        if (signalUpdateWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(signalUpdateWindow);
            signalUpdateWindow = null;
        }
    }

    /**
     * 创建一个进度条悬浮窗。位置为屏幕正中间。
     *
     * @param context 必须为应用程序的Context.
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
     * @param context 必须为应用程序的Context.
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
        return contentWindow != null || menuWindow != null || menuWindowLeft != null;
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isMenuShowing() {
        return bottomMenuLayout != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    public static DrawContentLayout getContentWindow() {
        return contentWindow;
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

    public static SignalUpdateDialogLayout getSignalUpdateWindow() {
        return signalUpdateWindow;
    }

    public static BottomMenuLayout getBottomMenuLayout() {
        return bottomMenuLayout;
    }

    /**
     * 启动升级
     *
     * @param context
     */
    public static void startUpdateService(Context context) {
        // 开启服务进行更新
        Intent intent = new Intent(context, UpdateAPKService.class);
        intent.putExtra(KEY_UPDATE_APP_URL, updateAPPUrl);
        context.startService(intent);
    }
}
