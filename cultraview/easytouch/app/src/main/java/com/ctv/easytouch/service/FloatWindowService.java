package com.ctv.easytouch.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ctv.easytouch.control.FloatWindowManager;
import com.ctv.easytouch.ui.TouchButtonLayout;
import com.ctv.easytouch.utils.AppUtils;
import com.ctv.easytouch.utils.MstarConst;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 后台服务
 */
public class FloatWindowService extends Service {
    private final static String TAG = FloatWindowService.class.getSimpleName();

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    public static Handler mHandler = null;

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。悬浮框
     */
    private Timer timer;

    private boolean isFirstStart = true;

    public final static String START_ACTION = "com.ctv.easytouch.START_ACTION"; // 开启
    public final static String CLOSE_ACTION = "com.ctv.easytouch.CLOSE_ACTION"; // 关闭

    public final static String CHANNETUNING_ACTION = "com.ctv.easytouch.CHANNETUNING_ACTION"; // 搜台中
    public final static String CHANNEPAUSE_ACTION = "com.ctv.easytouch.CHANNEPAUSE_ACTION"; // 停止搜台

    private static boolean isLastVisible = true; // 上一次是否显示
    private MyRun myRun = new MyRun();

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new UIHandler(this);
        isFirstStart = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 初始化操作
        init(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化操作
     *
     * @param intent
     */
    private void init(Intent intent) {
        Log.d(TAG, "onStartCommand init  easyTouch");

        handleAction(intent);
        Log.d(TAG, "qkmin onStartCommand init  easyTouch isFirstStart" + isFirstStart);

        if (isFirstStart) {
            isFirstStart = false;

            Log.d(TAG, "isFirstStart  .....");

            // 开启定时器，每隔5秒刷新一次
            if (timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new RefreshTask(), new Date(), 5000);
            }
        }
    }

    /**
     * 处理intent
     */
    private void handleAction(Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        if (CLOSE_ACTION.equals(action)) { // 关闭
            Log.d(TAG, "CLOSE_ACTION .....");
            mHandler.postDelayed(() -> {
                stopSelf();
            }, 100);
        } else if (CHANNETUNING_ACTION.equals(action)) { // 搜台中
            // 隐藏悬浮按钮
            TouchButtonLayout buttonLayout = FloatWindowManager.getTouchWindow();
            if (buttonLayout != null) {
                buttonLayout.setVisibility(View.GONE);
                isLastVisible = false;
            }
        } else if (CHANNEPAUSE_ACTION.equals(action)) { // 停止搜台
            // 显示悬浮按钮
            TouchButtonLayout buttonLayout = FloatWindowManager.getTouchWindow();
            if (buttonLayout != null) {
                buttonLayout.setVisibility(View.VISIBLE);
                isLastVisible = true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 释放资源
        releaseData();
    }

    /**
     * 释放资源
     */
    private void releaseData() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        FloatWindowManager.removeTouchWindow(getApplicationContext());
    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            // 当前没有浮框，直接创建浮框
            if (!FloatWindowManager.isWindowShowing()) {
                Log.d(TAG, "FloatWindowManager.isWindowShowing()  .....");
                if (mHandler != null) {
                    Log.d(TAG, "FloatWindowManager.isWindowShowing()  mHandler!=NULL....");
                    mHandler.removeCallbacks(myRun);
                    mHandler.postDelayed(myRun, 50);
                    isLastVisible = true;
                    SystemClock.sleep(500);
                }
            }
//            else {
//                boolean isChannetuning = AppUtils.isChannetuning();
//                if (isChannetuning) {// 搜台过程中
//                    if (isLastVisible){ // 当前可见时
//                        if (mHandler != null){
//                            mHandler.postDelayed(() -> {
//                                TouchButtonLayout buttonLayout = FloatWindowManager.getTouchWindow();
//                                if (buttonLayout == null) {
//                                    buttonLayout = FloatWindowManager.createTouchWindow(getApplicationContext());
//                                }
//                                buttonLayout.setVisibility(View.GONE);
//                            }, 50);
//                        }
//
//                        isLastVisible = false;
//                    }
//                } else { // 没有搜台时
//                    if (!isLastVisible){
//                        if (mHandler != null){
//                            mHandler.postDelayed(() -> {
//                                TouchButtonLayout buttonLayout = FloatWindowManager.getTouchWindow();
//                                if (buttonLayout == null) {
//                                    buttonLayout = FloatWindowManager.createTouchWindow(getApplicationContext());
//                                }
//                                buttonLayout.setVisibility(View.VISIBLE);
//                            }, 50);
//                        }
//
//                        isLastVisible = true;
//                    }
//                }
//            }
        }
    }

    public class MyRun implements Runnable {

        @Override
        public void run() {
            FloatWindowManager.createTouchWindow(FloatWindowService.this);
        }
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<FloatWindowService> weakReference;

        public UIHandler(FloatWindowService service) {
            super();
            this.weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            FloatWindowService service = weakReference.get();
            if (service == null) {
                return;
            }
        }
    }
}
