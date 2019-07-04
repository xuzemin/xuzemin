package com.xuzemin.joinus.utils;

import android.util.Log;

public class LogUtil {
    private static String TAG = "Joinus";
    public static void i(String log){
        if (Constant.isDebug){
            Log.i(TAG,log);
        }
    }
}
