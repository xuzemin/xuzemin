package mstar.factorymenu.ui.utils;

import android.util.Log;

public class LogUtils {
    static boolean DEBUG = false;

    static final String TAG = "LogUtils";

    public static void v(String TAG, String msg) {
        if (DEBUG) Log.v(TAG, msg);
    }

    public static void v(String msg) {
        if (DEBUG) Log.v(TAG, msg);
    }


    public static void d(String TAG, String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    public static void d(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }

    public static void e( String msg) {
        Log.e(TAG, msg);
    }
    public static void e(String TAG, String msg) {
        Log.e(TAG, msg);
    }
}
