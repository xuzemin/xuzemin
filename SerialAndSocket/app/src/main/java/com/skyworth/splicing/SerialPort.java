package com.skyworth.splicing;

import android.util.Log;

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
public class SerialPort {

	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;

	public SerialPort(File device, int baudrate) throws SecurityException, IOException {
		mFd = open(device.getAbsolutePath(), baudrate);
		if (mFd == null) {
			Log.e("SerialPortUtil",mFd.toString());
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}

	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}

	private native FileDescriptor open(String path, int baudrate);
	public native int close();

	static {
		System.loadLibrary("serial_port");
	}
}
