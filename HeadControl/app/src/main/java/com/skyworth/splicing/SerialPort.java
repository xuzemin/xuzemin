package com.skyworth.splicing;

import com.android.jdrd.headcontrol.util.Constant;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 串口操作
 * 
 * @author guoxiao
 * 
 */
class SerialPort {

	private FileDescriptor mFd = null;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	private SerialPort(File device, int baudrate) throws SecurityException, IOException {
		//设置串口设备节点和波特率
		mFd = open(device.getAbsolutePath(), baudrate);
		if (mFd != null) {
			try {
				Constant.debugLog(mFd.toString());
			/* 获取输入输出流 */
				mFileInputStream = new FileInputStream(mFd);
				mFileOutputStream = new FileOutputStream(mFd);
			}catch (Exception e){
				Constant.debugLog(e.toString());
			}
		}
	}

	InputStream getInputStream() {
		return mFileInputStream;
	}

	OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	private native FileDescriptor open(String path, int baudrate);
	public native int close();

	static {
		System.loadLibrary("serial");
	}
}
