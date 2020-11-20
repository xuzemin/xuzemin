package com.ctv.Jni;

import android.graphics.Canvas;

public class WbJniCall {
    static {
        System.loadLibrary("whiteboard");
    }

    public static native void fbStart(int fbmode);
    public static native void fbStop();
    public static native void fbLock(int mark);
    public static native void fbSetCanvas(Canvas canvas0, Canvas canvas1, Canvas canvas2);
    public static native String nativeGetSysProperty(String key, String value);
}
