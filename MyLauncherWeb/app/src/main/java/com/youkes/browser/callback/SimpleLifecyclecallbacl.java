package com.youkes.browser.callback;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.youkes.browser.utils.Constant;
import com.youkes.browser.utils.FileHandle;

public class SimpleLifecyclecallbacl implements Application.ActivityLifecycleCallbacks {
    private static Thread threadMain;
    private static Thread threadKey1;
    private static Thread threadKey2;
    private static Thread threadKey3;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        Constant.debugLog("onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Constant.debugLog("onActivityResumed");
        Constant.isApplicationPause = false;
        threadMain = null;
        threadKey1 = null;
        threadKey2 = null;
        threadKey3 = null;
        Constant.debugLog("onActivityResumed"+threadMain+
                threadKey1+threadKey2+threadKey3);
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        Constant.CurrentNumber = 0;
        Constant.isApplicationPause = true;
        threadMain = new Thread(new Runnable() {
            @Override
            public void run() {
                while(Constant.isApplicationPause){
                    Constant.debugLog("onActivityPaused Constant.CurrentNumber"+Constant.CurrentNumber);
                    try {
                        Thread.sleep(1000);
                        if(Constant.CurrentNumber >= Constant.OUTTIME){
                            ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                            activityManager.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                            Constant.isApplicationPause = false;
                            Constant.CurrentNumber = 0;
                            Constant.isVideoPlay = true;
                        }else{
                            Constant.CurrentNumber ++;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        threadKey1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(Constant.isApplicationPause){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Constant.debugLog("threadKey1");
                    FileHandle.getFileHandle().readFile(Constant.EventPath);
                }
            }
        });
        threadKey2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(Constant.isApplicationPause){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Constant.debugLog("threadKey2");
                    FileHandle.getFileHandle().readFile(Constant.EventPath1);
                }
            }
        });
        threadKey3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(Constant.isApplicationPause){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Constant.debugLog("threadKey3");
                    FileHandle.getFileHandle().readFile(Constant.EventPath2);
                }
            }
        });
        threadMain.start();
        threadKey1.start();
        threadKey2.start();
        threadKey3.start();

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
