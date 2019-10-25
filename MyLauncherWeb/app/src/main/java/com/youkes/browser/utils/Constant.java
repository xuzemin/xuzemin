package com.youkes.browser.utils;

import android.util.Log;

public class Constant {
    public static final Boolean isDebug = true;
    public static final Boolean isCN = false;
    public static final String TAG = "Lightning";
    public static int CurrentNumber = 0;
//    public static boolean isInit = false;
    public static final int VIDEO = 0;
    public static final int NEW = 1;
    public static final int MESSAGE = 2;
    public static final int CONSTACT = 3;
    public static final int CONTENT = 4;
//    public static final int SETTINGS = 5;
    public static final int BACKGROUND = 5;
    public static final int ANIMATIONTIME = 1000;
    public static final String fileName = "filename";
    public static final String ApkDir = "/sdcard/Apk/";
    public static final String ApkName = "MyWebLauncher.apk";
    public static final String Package = "com.youkes.browser";
    public static final String Default = "default";
    public static final String AdminPassword = "active99";
    public static final String AdminPassword_bak = "1";
    public static void debugLog(String str){
        if(isDebug){
            Log.e(TAG,"Launcher"+str);
        }
    }

    public static final int OUTTIME = 10;
    public static int Current_Show = 0;
    public static final int EVENT_START_VIDEO = 1;
    public static final int SHOWTIME = 3 * 1000;
    public static boolean isResetPlay = false;
    public static boolean isVideoPlay = true;
    public static boolean isImagePlay = false;
    public static int Current_Video = 0;
    public static int Current_Image = 0;
    public static boolean isApplicationPause = false;
    public static final String VideoDir = "/sdcard/Video/";
    public static final String ImgDir = "/sdcard/Image/";
    public static final String EventPath = "/dev/input/event3";
    public static final String EventPath1 = "/dev/input/event2";
    public static final String EventPath2 = "/dev/input/event4";
}
