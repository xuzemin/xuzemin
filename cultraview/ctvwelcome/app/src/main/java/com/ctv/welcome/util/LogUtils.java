
package com.ctv.welcome.util;

import android.util.Log;

public class LogUtils {
    public static final int LEVEL_DEBUG = 2;

    public static final int LEVEL_ERROR = 5;

    public static final int LEVEL_INFO = 3;

    public static final int LEVEL_NONE = 0;

    public static final int LEVEL_VERBOSE = 1;

    public static final int LEVEL_WARN = 4;

    private static int mDebuggable = 5;

    private static final Object mLogLock = new Object();

    private static String mTag = "LogUtils";

    private static long mTimestamp = 0;

    public static void v(String msg) {
        if (mDebuggable >= 1) {
            Log.v(mTag, msg);
        }
    }

    public static void d(String msg) {
        if (mDebuggable >= 2) {
            Log.d(mTag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (mDebuggable >= 2) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        if (mDebuggable >= 3) {
            Log.i(mTag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (mDebuggable >= 3) {
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        if (mDebuggable >= 4) {
            Log.w(mTag, msg);
        }
    }

    public static void w(Throwable tr) {
        if (mDebuggable >= 4) {
            Log.w(mTag, "", tr);
        }
    }

    public static void w(String msg, Throwable tr) {
        if (mDebuggable >= 4 && msg != null) {
            Log.w(mTag, msg, tr);
        }
    }

    public static void e(String msg) {
        if (mDebuggable >= 5) {
            Log.e(mTag, msg);
        }
    }

    public static void e(Throwable tr) {
        if (mDebuggable >= 5) {
            Log.e(mTag, "", tr);
        }
    }

    public static void e(String msg, Throwable tr) {
        if (mDebuggable >= 5 && msg != null) {
            Log.e(mTag, msg, tr);
        }
    }
}
