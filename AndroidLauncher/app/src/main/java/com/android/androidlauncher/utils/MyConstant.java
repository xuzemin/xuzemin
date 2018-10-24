package com.android.androidlauncher.utils;

import android.util.Log;

import com.android.androidlauncher.R;

public class MyConstant {
    public static final String TAG = "Launcher";
    private static final boolean IsDebug = true;
    public static boolean isResetPlay = false;
    public static boolean isVideoPlay = false;
    public static boolean isApplicationPause = false;
    public static int CurrentNumber = 0;
    public static final int EVENT_START_VIDEO = 0;
    public static final String VideoPath = "android.resource://com.android.androidlauncher/"+ R.raw.twotwo;
    public static final String EventPath = "/dev/input/event3";
    public static void debugLog(String log){
        if(IsDebug) {
            Log.e(TAG, log);
        }
    }
}
