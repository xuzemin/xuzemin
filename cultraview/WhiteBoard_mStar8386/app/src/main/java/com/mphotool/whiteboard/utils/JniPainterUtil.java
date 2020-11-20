package com.mphotool.whiteboard.utils;


import android.graphics.Bitmap;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by toofifi on 2018/5/23.
 */

public class JniPainterUtil {

    static
    {
        System.loadLibrary("mypainter");
    }

    public static Object getSystemProperties(String id)
            throws Exception
    {
        Object m = new Object();
        Class clazz = Class.forName("android.os.SystemProperties");
        Method mMethod = clazz.getMethod("get", String.class);
        return mMethod.invoke(m,id);
    }

    public static void nativeInitFrameBuffer(int widthPixels, int heightPixels){
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            nativeInitFrameBuffer(widthPixels,heightPixels,1);
        }else{
            nativeInitFrameBuffer(widthPixels,heightPixels,1);
        }
    }

    public static void EnableFrameBufferPost(int tagPost){
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            nativeEnableFrameBufferPost(tagPost,1);
        }else{
            nativeEnableFrameBufferPost(tagPost,1);
        }
    }

    public static void WriteRect(int eraser_type,int pixel_type,int left, int top, int right,int bottom,int[] pixels,int pentype,int pX, int pY, Bitmap mBackgroundBitmap){
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            nativeWriteRect(eraser_type,pixel_type,left, top, right,bottom, pixels, 3,pX, pY, mBackgroundBitmap);
        }else{
            nativeWriteRect(eraser_type,pixel_type,left, top, right,bottom, pixels, 2,pX, pY, mBackgroundBitmap);
        }
    }

    public static void nativeExitFrameBuffer(){
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            nativeExitFrameBuffer(1);
        }else{
            nativeExitFrameBuffer(1);
        }
    }

    private static native int nativeInitFrameBuffer(int widthPixels, int heightPixels,int mapEnbled);//call

    private static native int nativeExitFrameBuffer(int mapEnbled);//call

    private static native void nativeEnableFrameBufferPost(int tagPost,int mapEnbled);

    private static native void nativeWriteRect(int eraser_type,int pixel_type,int left, int top, int right,int bottom,int[] pixels,int pentype, int pX, int pY, Bitmap mBackgroundBitmap);//call

    public static native void nativeSetBackgroundBitmap(Bitmap mBackgroundBitmap);
    public static native void nativeUnSetBackgroundBitmap(Bitmap mBackgroundBitmap);

    public static native int nativeEraserSizeColor(int eraserWidth, int eraserHeight,int eraserColor);//call

    public static native void nativeEraserDown(int pid, float pX, float pY, float mEraserHW, Bitmap mBackgroundBitmap);

    public static native void nativeEraserMove(int pid, float pX, float pY);

    public static native void nativeEraserUp(int pid, float pX, float pY, Bitmap mBackgroundBitmap);

    public static native void nativeSetSysProperty(String key,String value);

    public static native String nativeGetSysProperty(String key,String value);
}
