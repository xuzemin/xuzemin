package com.android.androidlauncher.utils;

import android.util.Log;

public class MyConstant {
    public static final String TAG = "Launcher";
    private static final boolean IsDebug = true;
    public static boolean isResetPlay = false;
    public static boolean isVideoPlay = false;
    public static int Current_Video = 0;
    public static boolean isApplicationPause = false;
    public static int CurrentNumber = 0;
    public static final int EVENT_START_VIDEO = 0;
    public static String VideoPath = "";
    public static final String VideoDir = "android.resource://com.android.androidlauncher/";
    public static final String EventPath = "/dev/input/event3";
    public static void debugLog(String log){
        if(IsDebug) {
            Log.e(TAG, log);
        }
    }
}
