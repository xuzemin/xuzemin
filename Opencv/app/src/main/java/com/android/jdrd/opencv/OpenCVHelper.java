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

    static public native int open(byte[] devname);
    static public native int qbuf(int index);
    static public native int streamon();
    static public native int streamoff();
    static public native int dqbuf(byte[] videodata);
    static public native int release();
    static public native int init(int width, int height,int numbuf);
}
