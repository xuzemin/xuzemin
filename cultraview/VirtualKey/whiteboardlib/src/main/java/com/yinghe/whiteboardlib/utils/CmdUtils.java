package com.yinghe.whiteboardlib.utils;

import android.content.Context;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

/**
 * 命令操作工具
 *
 * @author wang
 * @time on 2018/34/17.
 */
public class CmdUtils {
	private static final String TAG = CmdUtils.class.getSimpleName();

	// OE 相互切换
	final static String TVOS_COMMON_CMD_USB_OE_SETONOFF_HIGH = "SetUSBONOFF_OE_High";
	final static String TVOS_COMMON_CMD_USB_OE_SETONOFF_LOW = "SetUSBONOFF_OE_Low";

	// SEL 一直为低
	final static String TVOS_COMMON_CMD_USB_SEL_SETONOFF_HIGH = "SetUSBONOFF_SEL_High";
	final static String TVOS_COMMON_CMD_USB_SEL_SETONOFF_LOW = "SetUSBONOFF_SEL_Low";

	// USB切换类型
	public final static int USB_TYPE_ANDROID = 0;
	public final static int USB_TYPE_OTHER = 1;
	public final static int USB_TYPE_OPS = 2;

	/**
	 * 切换USB设置
	 *
	 * @param type
	 */
	public static void changeGPIO(int type){
		String OE_STR;
		String SEL_STR;
		switch (type) {
			case USB_TYPE_ANDROID: // Android 应用下
				OE_STR = TVOS_COMMON_CMD_USB_OE_SETONOFF_HIGH;
				SEL_STR = TVOS_COMMON_CMD_USB_SEL_SETONOFF_LOW;
				break;
			case USB_TYPE_OTHER: // 其他
				OE_STR = TVOS_COMMON_CMD_USB_OE_SETONOFF_LOW;
				SEL_STR = TVOS_COMMON_CMD_USB_SEL_SETONOFF_LOW;
				break;
			case USB_TYPE_OPS: // OPS通道下
				OE_STR = TVOS_COMMON_CMD_USB_OE_SETONOFF_LOW;
				SEL_STR = TVOS_COMMON_CMD_USB_SEL_SETONOFF_HIGH;
				break;
			default: // Android 应用下
				OE_STR = TVOS_COMMON_CMD_USB_OE_SETONOFF_HIGH;
				SEL_STR = TVOS_COMMON_CMD_USB_SEL_SETONOFF_LOW;
				break;
		}

		// USB切换触屏设置
		try {
			TvManager.getInstance().setTvosCommonCommand(OE_STR);
			TvManager.getInstance().setTvosCommonCommand(SEL_STR);
			LogUtils.d("OE->%s, SEL->%s", OE_STR, SEL_STR);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 获取温度
	 */
	public static Float getTmpValue(){
		Float tmpValue = 0f;
		try {
			short[] shorts = TvManager.getInstance().setTvosCommonCommand("GetTmpValue");
			if (shorts != null && shorts.length >= 2){
				tmpValue = shorts[0] + ((shorts[1] + 1f) / 100f);
			}

			LogUtils.d("tmpValue->%s ℃", tmpValue);
			return tmpValue;
		} catch (Exception e){
			e.printStackTrace();
			return 0.0f;
		}
	}

	/**
	 * 执行root命令
	 * @param cmd
	 */
	public static void rootExec(String cmd) {
		Log.e("CmdUtils", "=====start========"+cmd);
		long startTime = System.currentTimeMillis();
		if (cmd.startsWith("su")|cmd.startsWith("ping")) {
			//return "不允许执行的命令";
		}
		String result = "";
		DataOutputStream dos = null;
		DataInputStream dis = null;
		try {
			Process ps = Runtime.getRuntime().exec(cmd);
			dos = new DataOutputStream(ps.getOutputStream());
			dis = new DataInputStream(ps.getInputStream());
			dos.writeBytes(cmd + "\n");
			dos.flush();
			Log.e("wy", "=====cmdutils========"+cmd);
			dos.writeBytes("exit\n");
			dos.flush();
			String line = null;
			while ((line = dis.readLine())!=null) {
				result += "\n"+line;
				//   Message ms = new Message();
				//   ms.obj = line;
				//   handler.sendMessageDelayed(ms,1000);
			}

			ps.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			long endTime = System.currentTimeMillis();
			Log.e("CmdUtils", "=====end======== dTime->"+ (endTime - startTime));
		}
		// return result;
	}

	/**
	 * 判断是否在Android通道下
	 * @return
	 */
	public static boolean isAndroidChannel(){
		int inputSource = -1;
		try {
			inputSource = TvManager.getInstance().getCurrentInputSource().ordinal();
		} catch (Exception e){
			e.printStackTrace();
		}

		return inputSource == -1 || inputSource == 34;
	}

	/**
	 * 设置命令
	 *
	 * @param cmdStr
	 */
	public static void setTvosCommonCommand(String cmdStr){
		try {
			TvManager.getInstance().setTvosCommonCommand(cmdStr);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 改变UART触控的状态
	 * @return
	 */
	public static void changeUARTTouch(boolean isUARTTouch){
		try{
			String command = "SetUARTTOUCH_OFF"; // 关闭UART
			if (isUARTTouch){ // 开启UART
				command = "SetUARTTOUCH_ON";
			}
			TvManager.getInstance().setTvosCommonCommand(command);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 改变USB触控
	 *
	 * @param isOpen
	 */
	public static void changeUSBTouch(Context context, boolean isOpen){
		try {
			// isOpen true:表示打开usb touch；false关闭
//			TvCommonManager.getInstance().setUsbTouch(context, isOpen);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
