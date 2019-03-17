package com.youkes.browser.utils;

import android.util.Log;

public class Constant {
    public static final Boolean isDebug = true;
    public static final Boolean isCN = false;
    public static final String TAG = "Lightning";
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
    public static void debugLog(String str){
        if(isDebug){
            Log.e(TAG,"Launcher"+str);
        }
    }
}
