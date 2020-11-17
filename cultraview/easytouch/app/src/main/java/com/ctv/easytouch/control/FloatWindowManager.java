package com.ctv.easytouch.control;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.ctv.easytouch.ui.TouchButtonLayout;
import com.ctv.easytouch.utils.SPUtil;
import com.ctv.easytouch.utils.ScreenUtils;

/**
 * 创建和控制浮框
 *
 * @author wang
 * @time on 2017/3/10.
 */
public class FloatWindowManager {
    private static String TAG = FloatWindowManager.class.getSimpleName();

    private static WindowManager mWindowManager;// 用于控制在屏幕上添加或移除悬浮窗

    // 按钮的悬浮窗
    private static TouchButtonLayout touchWindow;
    private static LayoutParams touchParams;

    /**
     * 将窗口从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeTouchWindow(Context context) {
        if (touchWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(touchWindow);
            touchWindow = null;
        }
    }

    /**
     * 创建右边的控制菜单
     *
     * @param context
     */
    public static TouchButtonLayout createTouchWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);

        // 初始化布局参数
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();

        // 初始化UI
        if (touchWindow == null) {
            touchWindow = new TouchButtonLayout(context);
            initTouchRightParams(context, screenWidth, screenHeight);

            touchWindow.setParams(touchParams);
            windowManager.addView(touchWindow, touchParams);
        } else {
            initTouchRightParams(context, screenWidth, screenHeight);
            windowManager.updateViewLayout(touchWindow, touchParams);
        }

        return touchWindow;
    }

	/**
	 *
	 * @param screenWidth
	 * @param screenHeight
	 */
	private static void initTouchRightParams(Context context, int screenWidth, int screenHeight){
		if (touchParams == null) {
			touchParams = new LayoutParams();

            // 默认位置
            int defaultX = (int) (screenWidth * 0.75f);
            touchParams.x = (int) SPUtil.getData(context, "LAST_POINT_X_KEY", defaultX);
            // 默认位置
            int defaultY = (int) ((screenHeight / 4.0f));
            touchParams.y = (int) SPUtil.getData(context, "LAST_POINT_Y_KEY", defaultY);
            touchParams.type = LayoutParams.TYPE_PHONE;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                touchParams.type = LayoutParams.TYPE_PHONE;
            } else {
                touchParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY; //8。0 8386
            }

            // 所有其它程序是可点击的，悬浮窗不获取焦点
            touchParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            touchParams.format = PixelFormat.RGBA_8888;
            touchParams.gravity = Gravity.LEFT | Gravity.TOP;
            touchParams.width = ScreenUtils.dip2px(context, 50);//UpdateLayout.viewWidth;
            touchParams.height = ScreenUtils.dip2px(context, 50);//UpdateLayout.viewHeight;

            Log.d(TAG, "width->" + touchWindow.getWidth() + " height->" + touchWindow.getHeight());
        }
    }

    /**
     * 移动工具栏位置
     *
     * @param context
     */
    public static void updateTouchWindow(Context context) {
        if (touchWindow == null || touchParams == null) {
            return;
        }

        // 更新位置
        WindowManager windowManager = getWindowManager(context);

        windowManager.updateViewLayout(touchWindow, touchParams);

//		Log.d(TAG, " x->" + touchParams.x + " y->" + touchParams.y);

        // 保存Y坐标信息
        SPUtil.saveData(context, "LAST_POINT_X_KEY", touchParams.x);
        // 保存Y坐标信息
        SPUtil.saveData(context, "LAST_POINT_Y_KEY", touchParams.y);
    }

    public static void showWindow(Context context, boolean isSmall) {
        if (touchWindow == null || touchParams == null) {
            return;
        }

        // 更新位置
        WindowManager windowManager = getWindowManager(context);
        if (isSmall) {
            touchParams.width = ScreenUtils.dip2px(context, 50);
            touchParams.height = ScreenUtils.dip2px(context, 50);

            touchParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE;
        } else {
            touchParams.width = ScreenUtils.dip2px(context, 120);
            touchParams.height = ScreenUtils.dip2px(context, 120);

            touchParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        }

        windowManager.updateViewLayout(touchWindow, touchParams);
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

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return touchWindow != null;
    }

    public static TouchButtonLayout getTouchWindow() {
        return touchWindow;
    }

}
