package com.android.androidlauncher.utils;

import android.util.Log;

import com.android.androidlauncher.R;

public class MyConstant {
    public static final String TAG = "Launcher";
    private static final boolean IsDebug = true;
    public static boolean isResetPlay = false;
    public static boolean isVideoPlay = false;
    public static int Current_Video = 0;
    public static boolean isApplicationPause = false;
    public static int CurrentNumber = 0;
    public static final int EVENT_START_VIDEO = 0;
    public static final String VideoDir = "android.resource://com.android.androidlauncher/";
    public static final String EventPath = "/dev/input/event1";
    public static final String EventPath1 = "/dev/input/event2";
    public static final String EventPath2 = "/dev/input/event3";
    public static String VideoPath = MyConstant.VideoDir+R.raw.one;
    public static void debugLog(String log){
        if(IsDebug) {
            Log.e(TAG, log);
        }
    }
}
