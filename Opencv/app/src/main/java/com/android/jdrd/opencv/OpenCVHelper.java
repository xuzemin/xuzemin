package com.android.jdrd.opencv;


import org.opencv.core.Mat;

import java.io.FileDescriptor;
import java.util.ArrayList;

/**
 * Created by xuzemin on 16/9/8.
 */
public class OpenCVHelper {
    static {
        System.loadLibrary("OpenCVDemo");
    }
    public static native int[] gray(int[] buf, int w, int h);

    public static native int init();//初始化打开摄像头
    public static native int stop();//释放关闭摄像头
    public static native int getdata(long gray);//获取摄像头底层一帧画面

    public static native int test();
    public static native int send();

    public static native int get();//初始化获取虚拟底层驱动接口
    public static native int keyDownPress();//键盘方向下键按下
    public static native int keyDownInstitute();//键盘方向下键松开
    public static native int keyUpPress();//键盘方向上键按下
    public static native int keyUpInstitute();//键盘方向上键松开
    public static native int keyLeftPress();//键盘方向左键按下
    public static native int keyLeftInstitute();//键盘方向左键松开
    public static native int keyRightPress();//键盘方向右键按下
    public static native int keyRightInstitute();//键盘方向右键松开
    public static native int keyReturn();//键盘回车键按下松开
    public static native int closeget();//键盘驱动接口关闭

    public static native int sendABSData(int mouse_x,int mouse_y,int mouse_press);
    public static native int sendData(int mouse_x,int mouse_y,int mouse_press);
    public static native int MousePress(int mouse_press);
    public static native int initMouse();
    public static native int closeMouse();

    public static native FileDescriptor openUsb(String path, int baudrate, int flags);
}
