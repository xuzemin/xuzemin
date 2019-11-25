package com.yinghe.whiteboardlib.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *  屏幕工具
 * Created by nereo on 15/11/19.
 * Updated by nereo on 2016/1/19.
 */
public class ScreenUtils {
    private static int statusBarHeight = 0;// 状态栏的高度

    /**
     * 获取屏幕原始尺寸高度，包括虚拟功能键高度
     *
     * @param context
     * @return
     */
    public static Point getRealScreen(Context context){
        Point out = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();

        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics",DisplayMetrics.class);
            method.invoke(display, displayMetrics);

            out.x = displayMetrics.widthPixels;
            out.y = displayMetrics.heightPixels;
            return out;
        } catch(Exception e){
            e.printStackTrace();
            return out;
        }
    }

    /**
     * 获得屏幕宽和高,不包括虚拟按键
     *
     * @param context
     * @return
     */
    public static Point getScreenSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(out);
        }else{
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }

        return out;
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight != 0){
            return statusBarHeight;
        }

        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            Log.e("getStatusBarHight()", "get status bar height fail");
            e1.printStackTrace();
        }
        int tmpStatusBarHeight = sbar;
        statusBarHeight = tmpStatusBarHeight;
        return tmpStatusBarHeight;
    }

    /**
     * 获取 虚拟按键的高度
     * @param context
     * @return
     */
    public static int getBottomStatusHeight(Context context){
        int dHeight = 0;
        Point realPoint = getRealScreen(context);
        Point point = getScreenSize(context);
        if (realPoint.x == point.x){
            dHeight = realPoint.y - point.y;
        } else {
            dHeight = realPoint.x - point.x;
        }

        return dHeight;
    }

    public static void hideInput(View v) {
        InputMethodManager inputManager = (InputMethodManager) v
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //调用系统输入法
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }
    public static void showInput(View v) {
        InputMethodManager inputManager = (InputMethodManager) v
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        //调用系统输入法
        inputManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
    }

    public static void toggleInput(Context context) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
