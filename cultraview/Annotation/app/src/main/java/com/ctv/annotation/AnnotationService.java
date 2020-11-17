package com.ctv.annotation;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;


import com.ctv.annotation.mananger.AnnotationManager;
import com.ctv.annotation.mananger.FloatWindowManager;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.view.FounctionView;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class AnnotationService extends Service {
    private static boolean isLastVisible = true; // 上一次是否显示
    private MyRun myRun = new MyRun();
    private WindowManager mWindowManager;
    private View inflate;
    private WindowManager.LayoutParams layoutParams_inflate;

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    public static Handler mHandler = null;

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。悬浮框
     */
    private Timer timer;
    private boolean isFirstStart = true;



    public AnnotationService() {
    }

    public class MyRun implements Runnable {

        @Override
        public void run() {
            FloatWindowManager.createTouchWindow(AnnotationService.this);
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            if (intent != null){
            if (AnnotationManager.annotationView ==null){
                BaseUtils.dbg("hong","creat annotationview");
                AnnotationManager.creatView(getApplicationContext());
                init(intent);

            }
        }

        Log.d("hong", "MyService--------onStartCommand: ");
        return START_NOT_STICKY;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new UIHandler(this);
        isFirstStart = true;
    }
    /**
     * 初始化操作
     *
     * @param intent
     */
    private void init(Intent intent) {

        if (isFirstStart) {
            isFirstStart = false;
          //  Log.d(TAG, "isFirstStart  .....");
            // 开启定时器，每隔5秒刷新一次
            if (timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new RefreshTask(), new Date(), 5000);
            }
        }
    }
    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            // 刷新window；
            if (!FloatWindowManager.isWindowShowing()) {
                BaseUtils.dbg("hong", "FloatWindowManager.isWindowShowing()  .....");
                if (mHandler != null) {
                    BaseUtils.dbg("hong", "FloatWindowManager.isWindowShowing()  mHandler!=NULL....");
                    mHandler.removeCallbacks(myRun);
                    mHandler.postDelayed(myRun, 50);
                    isLastVisible = true;
                    SystemClock.sleep(500);
                }
            }
        }
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
    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<AnnotationService> weakReference;

        public UIHandler(AnnotationService service) {
            super();
            this.weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            AnnotationService service = weakReference.get();
            if (service == null) {
                return;
            }
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseData();
    }
}
