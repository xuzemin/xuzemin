package com.android.androidlauncher.callback;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.android.androidlauncher.utils.FileHandle;
import com.android.androidlauncher.utils.MyConstant;

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
        MyConstant.debugLog("onActivityStarted");
    }

    @Override
    public void onActivityResumed(Activity activity) {
        MyConstant.debugLog("onActivityResumed");
        MyConstant.isApplicationPause = false;
        threadMain = null;
        threadKey1 = null;
        threadKey2 = null;
        threadKey3 = null;
        MyConstant.debugLog("onActivityResumed"+threadMain+
                threadKey1+threadKey2+threadKey3);
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        MyConstant.CurrentNumber = 0;
        MyConstant.isApplicationPause = true;
        threadMain = new Thread(new Runnable() {
            @Override
            public void run() {
                while(MyConstant.isApplicationPause){
                    MyConstant.debugLog("onActivityPaused MyConstant.CurrentNumber"+MyConstant.CurrentNumber);
                    try {
                        threadMain.sleep(1000);
                        if(MyConstant.CurrentNumber > 5){
                            ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                            activityManager.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                            MyConstant.isApplicationPause = false;
                            MyConstant.CurrentNumber = 0;
                        }else{
                            MyConstant.CurrentNumber ++;
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
                while(MyConstant.isApplicationPause){
                    try {
                        threadKey1.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MyConstant.debugLog("threadKey1");
                    FileHandle.getFileHandle().readFile(MyConstant.EventPath);
                }
            }
        });
        threadKey2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(MyConstant.isApplicationPause){
                    try {
                        threadKey3.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MyConstant.debugLog("threadKey2");
                    FileHandle.getFileHandle().readFile(MyConstant.EventPath1);
                }
            }
        });
        threadKey3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(MyConstant.isApplicationPause){
                    try {
                        threadKey3.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MyConstant.debugLog("threadKey3");
                    FileHandle.getFileHandle().readFile(MyConstant.EventPath2);
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
