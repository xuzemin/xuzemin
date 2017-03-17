package com.skyworth.splicing;


import android.util.Log;

import com.android.jdrd.headcontrol.util.Constant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 串口操作
 * @author guoxiao
 */
public class SerialUtil {
	private String TAG = SerialUtil.class.getSimpleName();
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private static final String path = "/dev/ttyUSB1";
	private static final int baudrate = 115200;
	private static SerialUtil portUtil;
	private OnDataReceiveListener onDataReceiveListener = null;
	private boolean isStop = false;
	/**
	 * 创建数据接受接口
	 */
	public interface OnDataReceiveListener {
		 void onDataReceive(byte[] buffer, int size);
	}

	/**
	 * 获取数据接受监听
	 *
     */
	public void setOnDataReceiveListener(
			OnDataReceiveListener dataReceiveListener) {
		onDataReceiveListener = dataReceiveListener;
	}



	public static SerialUtil getInstance() {
		if (null == portUtil) {
			portUtil = new SerialUtil();
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
	 *
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

	public boolean sendBuffer(byte[] mBuffer) {
		boolean result = true;
//		String tail = "";
//		byte[] tailBuffer = {'\r'};
//		byte[] mBufferTemp = new byte[mBuffer.length+tailBuffer.length];
//		System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
//		System.arraycopy(tailBuffer, 0, mBufferTemp, mBuffer.length, tailBuffer.length);
		try {
			if (mOutputStream != null) {
				mOutputStream.write(mBuffer);
				mOutputStream.flush();
			} else {
				result = false;
			}
		} catch (IOException e) {
			Log.e(TAG,"e"+e.toString());
			e.printStackTrace();
			result = false;
		}
		return result;
	}

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
					int read1 = mInputStream.read();
					if(read1 == 0x0B){
						Constant.debugLog("read"+read1);
					}
					if (size > 0) {
//							String str = new String(buffer, 0, size);
//							Logger.d("length is:"+size+",data is:"+new String(buffer, 0, size));
						if (null != onDataReceiveListener) {
							//监听返回数据
							onDataReceiveListener.onDataReceive(buffer, size);
							Constant.debugLog("size"+size);
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
