package com.youkes.browser.callback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.youkes.browser.activity.MainActivity;
import com.youkes.browser.utils.Constant;
import com.youkes.browser.utils.FileHandle;
import com.youkes.browser.utils.LogUtil;
import com.youkes.browser.utils.RootCmd;

import static com.youkes.browser.utils.Constant.EVENT_GETEVENT;
import static com.youkes.browser.utils.Constant.EVENT_TO_MAIN;

public class SimpleLifecyclecallbacl implements Application.ActivityLifecycleCallbacks {
    private static Thread threadMain;
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
        if(MainActivity.videoView !=null && MainActivity.videoView.isPlaying()){
            MainActivity.videoView.stopBackgroundPlay();
            MainActivity.videoView.pause();
            MainActivity.videoView.stopBackgroundPlay();
        }
        FileHandle.stopFileHandle();
    }

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case EVENT_GETEVENT:
                    FileHandle.readFile(Constant.EventPath);
                    break;
                case EVENT_TO_MAIN:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RootCmd.execRootCmdSilent("am start -n com.youkes.browser/.activity.MainActivity");
                        }
                    }).start();
                    break;
            }
        }
    };

    @Override
    public void onActivityPaused(final Activity activity) {
        if(MainActivity.videoView != null && MainActivity.videoView.isPlaying()){
            MainActivity.videoView.pause();
            MainActivity.videoView.stopBackgroundPlay();
        }
        if(MainActivity.VideoNameList != null && MainActivity.ImageNameList != null) {
            Constant.CurrentNumber = 0;
            Constant.isApplicationPause = true;
            threadMain = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (Constant.isApplicationPause) {
                        Constant.debugLog("onActivityPaused Constant.CurrentNumber" + Constant.CurrentNumber);
                        try {
                            Thread.sleep(2000);
                            if (Constant.CurrentNumber >= Constant.OUTTIME) {
                                if (!Constant.isPlay(activity)) {
                                    mhandler.sendEmptyMessage(EVENT_TO_MAIN);
                                    Constant.isApplicationPause = false;
                                    Constant.CurrentNumber = 0;
                                    Constant.isVideoPlay = true;
                                }else{
                                    Constant.CurrentNumber = 0;
                                }
                            } else {
                                if (Constant.isPlay(activity)) {
                                    Constant.CurrentNumber = 0;
                                }
                                Constant.CurrentNumber++;
                            }
                            if(!FileHandle.isIsRun()){
                                mhandler.sendEmptyMessage(EVENT_GETEVENT);
                            }
                        } catch (InterruptedException e) {
                            LogUtil.e("e"+e.toString());
                            e.printStackTrace();
                        }
                    }
                }
            });
            threadMain.start();
        }

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
