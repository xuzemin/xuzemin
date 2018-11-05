package com.android.androidlauncher.utils;

import android.util.Log;

public class MyConstant {
    public static final String TAG = "Launcher";
    public static final int OUTTIME = 30;
    public static boolean isInit =false;
    private static final boolean IsDebug = true;
    public static boolean isResetPlay = false;
    public static boolean isVideoPlay = false;
    public static int Current_Video = 0;
    public static int Current_Image = 0;
    public static boolean isApplicationPause = false;
    public static int CurrentNumber = 0;
    public static final int EVENT_START_VIDEO = 0;
//    public static final String VideoDir = "android.resource://com.android.androidlauncher/";
    public static final String VideoDir = "/sdcard/Video/";
    public static final String ApkDir = "/sdcard/apk/";
    public static final String ImgDir = "/sdcard/img/";
    public static final String EventPath = "/dev/input/event3";
    public static final String EventPath1 = "/dev/input/event2";
    public static final String EventPath2 = "/dev/input/event4";
//    public static String VideoPath = MyConstant.VideoDir+R.raw.one;
    public static String VideoPath = "/sdcard/Video/video.mp4";
    public static void debugLog(String log){
        if(IsDebug) {
            Log.e(TAG, log);
        }
    }


}
