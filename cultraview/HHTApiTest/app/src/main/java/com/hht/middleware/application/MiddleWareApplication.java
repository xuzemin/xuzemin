package com.hht.middleware.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Author: chenhu
 * Time: 2019/12/12 15:39
 * Description do somethings
 */
public class MiddleWareApplication extends Application {
    private static MiddleWareApplication instance;
    private int mScreenWidth;
    private int mScreenHeight;

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        getScreenWidthAndHeight();
    }

    public static MiddleWareApplication getInstance() {
        return instance;
    }

    /**
     * 得到宽度与高度
     */
    private void getScreenWidthAndHeight() {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels; // 屏幕宽度（像素）
        mScreenHeight = metric.heightPixels; // 屏幕高度（像素）
    }
}
