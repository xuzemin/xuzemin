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
    private static Thread threadKey;
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if(threadMain!=null){
            threadMain.interrupt();
            threadMain = null;
        }
        if(threadKey!=null){
            threadKey.interrupt();
            threadKey = null;
        }
        MyConstant.isApplicationPause = false;
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(final Activity activity) {
        MyConstant.CurrentNumber = 0;
        MyConstant.isApplicationPause = true;
        threadMain = new Thread(new Runnable() {
            @Override
            public void run() {
                while(MyConstant.isApplicationPause){
                    MyConstant.debugLog("MyConstant.CurrentNumber"+MyConstant.CurrentNumber);

                    try {
                        threadMain.sleep(1000);
                        MyConstant.CurrentNumber ++;
                        if(MyConstant.CurrentNumber > 5){
                            ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
                            activityManager.moveTaskToFront(activity.getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
                            MyConstant.isApplicationPause = false;
                            MyConstant.CurrentNumber = 0;
                        }
//                        FileHandle.getFileHandle().readFile("");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                threadMain.interrupt();
            }
        });
        threadMain.start();
//        threadKey = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    FileHandle.getFileHandle().readFile(MyConstant.EventPath);
//                }
//            }
//        });
//        threadKey.start();

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
