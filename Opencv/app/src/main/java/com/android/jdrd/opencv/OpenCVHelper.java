package com.android.jdrd.opencv;

/**
 * Created by xuzemin on 16/9/8.
 */
public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }
    public static native int[] gray(int[] buf, int w, int h);
    public static native int test();
    public static native int get();
    public static native int send();

    public static native int getdata();

}
