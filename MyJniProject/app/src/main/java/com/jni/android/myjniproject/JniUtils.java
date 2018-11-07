package com.jni.android.myjniproject;

public class JniUtils {
    static {
        System.loadLibrary("native-jni");
    }
    public static native String stringFromJNI();
}
