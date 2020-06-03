package com.skyworth.splicing;


import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 串口操作
 * @author guoxiao
 */
public class SerialPortUtil {
	private String TAG = SerialPortUtil.class.getSimpleName();
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String path = "/dev/ttyUSB0";
	private int baudrate = 115200;
	private static SerialPortUtil portUtil;
	private OnDataReceiveListener onDataReceiveListener = null;
	private boolean isStop = false;
	/**
	 * 创建数据接受接口
	 */
	public interface OnDataReceiveListener {
		public void onDataReceive(byte[] buffer, int size);
	}

	/**
	 * 获取数据接受监听
	 * @param dataReceiveListener
     */
	public void setOnDataReceiveListener(
			OnDataReceiveListener dataReceiveListener) {
		onDataReceiveListener = dataReceiveListener;
	}



	public static SerialPortUtil getInstance() {
		if (null == portUtil) {
			portUtil = new SerialPortUtil();
			portUtil.onCreate();
		}
		return portUtil;
	}

	private void onCreate() {
		try {
			//实例串口对象并获取输入输出流
			mSerialPort = new SerialPort(new File(path), baudrate);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			//开启循环通过输入流接受数据
			mReadThread = new ReadThread();
			isStop = false;
			mReadThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * 通过输出流发送数据
	 * @param cmd
	 * @return
     */
	public boolean sendCmds(String cmd) {
		boolean result = true;
		byte[] mBuffer = cmd.getBytes();
		try {
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
				mOutputStream.flush();
			} else {
				result = false;
			}
		} catch (IOException e) {
			Log.e(TAG,""+e.toString());
			e.printStackTrace();
			result = false;
		}
		return result;
	}

//	public boolean sendBuffer(byte[] mBuffer) {
//		boolean result = true;
////		String tail = "";
//		byte[] tailBuffer = {'\r'};
//		byte[] mBufferTemp = new byte[mBuffer.length+tailBuffer.length];
//		System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
//		System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length, tailBuffer.length);
//		try {
//			if (mOutputStream != null) {
//				mOutputStream.write(mBufferTemp);
//				mOutputStream.flush();
//				Log.e(TAG,"mBufferTemp"+mBufferTemp);
//			} else {
//				result = false;
//			}
//		} catch (IOException e) {
//			Log.e(TAG,"e"+e.toString());
//			e.printStackTrace();
//			result = false;
//		}
//		return result;
//	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isStop && !isInterrupted()) {
				int size;
				try {
					if (mInputStream == null)
						return;
					byte[] buffer = new byte[512];
					size = mInputStream.read(buffer);
					if (size > 0) {
//							String str = new String(buffer, 0, size);
//							Logger.d("length is:"+size+",data is:"+new String(buffer, 0, size));
						if (null != onDataReceiveListener) {
							//监听返回数据
							onDataReceiveListener.onDataReceive(buffer, size);
							Log.e(TAG,"buffer"+buffer);
						}
					}
					Thread.sleep(10);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	/**
	 * 关闭串口
	 */
	public void closeSerialPort() {
		isStop = true;
		if (mReadThread != null) {
			Log.e(TAG,"interrupt");
			mReadThread.interrupt();
		}
		if (mSerialPort != null) {
			Log.e(TAG,"close");
			mSerialPort.close();
		}
	}
	
}
