package com.protruly.floatwindowlib;

import android.app.Application;
import android.content.Context;

import com.protruly.floatwindowlib.control.SerialPort;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Desc:全局上下文
 *
 * @author Administrator
 * @time 2017/4/13.
 */
public class MyApplication extends Application {

    public  static boolean IsTouchSeting= false;
    public static SerialPort mSerialPort = null;
    @Override
    public void onCreate() {

        super.onCreate();
    }

    public static SerialPort getSerialPort() throws SecurityException, IOException,
            InvalidParameterException {
        if (mSerialPort == null) {
			/* Open the serial port */
            mSerialPort = new SerialPort(new File("/dev/ttyS2"), 115200, 0);
        }
        return mSerialPort;
    }

    public static void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

}
