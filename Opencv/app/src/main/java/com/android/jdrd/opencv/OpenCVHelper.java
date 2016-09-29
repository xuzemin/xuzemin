package com.android.jdrd.opencv;


import org.opencv.core.Mat;

import java.util.ArrayList;

/**
 * Created by xuzemin on 16/9/8.
 */
public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCV");
    }
    public static native int[] gray(int[] buf, int w, int h);

    public static native int init();
    public static native int stop();

    public static native int test();
    public static native int get();
    public static native int send();
    public static native int getdata(long gray);

}
