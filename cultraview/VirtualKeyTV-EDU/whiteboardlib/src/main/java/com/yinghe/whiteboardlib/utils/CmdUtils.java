package com.yinghe.whiteboardlib.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
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

	final static String SetTIPORT_0 = "SetTIPORT-0";// 前置HDMI
	final static String SetTIPORT_2 = "SetTIPORT-2";// HDMI2
	final static String SetTIPORT_3 = "SetTIPORT-3";// DP

	final static String SetUSBTOUCH_ON = "SetUSBTOUCH_ON";// 开启USB触摸
	final static String SetUSBTOUCH_OFF = "SetUSBTOUCH_OFF";// 关闭USB触摸

	final static String TVOS_COMMON_CMD_VGA_SWITCH_PIN_LOW = "SetVGA_SWITCH_Low"; // Prev VGA
	final static String TVOS_COMMON_CMD_VGA_SWITCH_PIN_HIGH = "SetVGA_SWITCH_High";// VGA

	// USB切换类型
	public final static int USB_TYPE_ANDROID = 0;
	public final static int USB_TYPE_OTHER = 1;
	public final static int USB_TYPE_OPS = 2;

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
	 * 切换TIPort
	 *
	 * @param type 0为HDMI2, 1为DP
	 */
	public static void changeTIPort(Context context, int type){
		String typeCommand = SetTIPORT_2; // 默认HDMI2
		switch (type){
			case 0: // 前置HDMI
				typeCommand = SetTIPORT_0;
				break;
			case 3: // DP
				typeCommand = SetTIPORT_3;
				break;
		}

		Settings.System.putInt(context.getContentResolver(), CommConst.SOURCE_INFO, type);
		LogUtils.d("changeTIPort typeCommand->%s source_info->%s" , typeCommand, type);
		try {
			TvManager.getInstance().setTvosCommonCommand(typeCommand);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 切换TIPort
	 *
	 * @param type 0为VGA, 1为前置VGA
	 */
	public static void switchVGA(Context context, int type){
		// 0为前置VGA, 1为VGA
		String typeCommand = (type == 1) ?
				TVOS_COMMON_CMD_VGA_SWITCH_PIN_LOW : TVOS_COMMON_CMD_VGA_SWITCH_PIN_HIGH ;
		LogUtils.d("switchVGA typeCommand->%s， vga_info->%s" , typeCommand, type);

		Settings.System.putInt(context.getContentResolver(), CommConst.VGA_INFO, type); // vga_info 0为VGA, 1为前置VGA
		try {
			TvManager.getInstance().setTvosCommonCommand(typeCommand);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

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
	 * 改变USB触控
	 *
	 * @param isOpen
	 */
	public static void changeUSBTouch(Context context, boolean isOpen){
		try {
			if(isOpen){
				if(isTopActivity(context,"com.mstar.tv.tvplayer.ui.RootActivity")&&!isServiceRunning(context,"com.ctv.annotation.AnnotationService")){
					TvCommonManager.getInstance().setTvosCommonCommand(
							"SetUSBTOUCH_ON");
				}
			}else{
				TvCommonManager.getInstance().setTvosCommonCommand(
						"SetUSBTOUCH_OFF");
			}
			//TvCommonManager.getInstance().setUsbTouch(context, isOpen);
			// isOpen true:表示打开usb touch；false关闭
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * 用来判断服务是否运行.
	 * @param mContext
	 * @param className 判断的服务名字
	 * @return true 在运行 false 不在运行
	 */
	public static boolean isServiceRunning(Context mContext, String className) {
		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager)
				mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList
				= activityManager.getRunningServices(30);
		if (!(serviceList.size()>0)) {
			return false;
		}
		for (int i=0; i<serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().equals(className) == true) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}
	private static boolean  isTopActivity(Context context,String name)
	{
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		String activity = cn.getClassName();
		return activity.contains(name);
	}
	/**
	 * 改变USB触控:直接控制
	 *
	 * @param isOpen
	 */
	public static void setUSBTouch(boolean isOpen){
		try {
			// isOpen true:表示打开usb touch；false关闭
			String usbStr = isOpen ? "SetUSBTOUCH_ON" : "SetUSBTOUCH_OFF";
			TvManager.getInstance().setTvosCommonCommand(usbStr);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 在TVPlay下，在OPS或者VAG或者HDMI通道下时，在顶层显示时
	 * @param context
	 */
	public static boolean isInOpsHDMIVgaTopShow(Context context){
		boolean flag = false;
		// 当前source为OPS\HDMI\VAG时，并且top Activity是Tv Play时
		int source = AppUtils.getCurrentSource(context);
		boolean isInOpsHDMIVga = (source >= 23 && source <= 25) || (source == 0);
		if (isInOpsHDMIVga
				&& AppUtils.isTopRunning(context, MstarConst.TV_PLAY_PACKAGE)){
			flag = true;
		}

		return flag;
	}

	/**
	 * 改变UART串口触控
	 *
	 * @param isOpen
	 */
	public static void changeUARTTouch(boolean isOpen){
		try{
			String command = "SetUARTTOUCH_OFF"; // 关闭UART
			if (isOpen){ // 开启UART
				command = "SetUARTTOUCH_ON";
			}
			TvManager.getInstance().setTvosCommonCommand(command);
			Log.d("zhu","Whiteboard changeUARTTouch() isOpen->" + isOpen);
		}catch (Exception e){
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

			tmpValue = (float)(Math.round(tmpValue*10)/10);
			LogUtils.d("tmpValue->%s ℃", tmpValue);
			return tmpValue;
		} catch (Exception e){
			e.printStackTrace();
			return 0.0f;
		}
	}

	public static boolean excuteShellCommand(String cmd){
		Runtime r = Runtime.getRuntime();
		Process p = null;
		try {
			p = r.exec(cmd);
			p.waitFor();
			if(p.exitValue() == 0){
				return true;
			}else
			{
				return false;
			}
		}catch(Exception e)
		{
			return false;
		} finally {
			if (p != null) {
				p.destroy();
			}
		}
	}

	public static boolean installApp(String apkPath) {
		Process process = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		try {
			process = new ProcessBuilder("pm", "install","-r", apkPath).start();
			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s;
			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}
			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		} catch (Exception e) {

		} finally {
			try {
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (Exception e) {

			}
			if (process != null) {
				process.destroy();
			}
		}
		Log.e("result",""+errorMsg.toString());
		//如果含有“success”单词则认为安装成功
		return successMsg.toString().equalsIgnoreCase("success");
	}

	//静默安装
	public static void installSlient() {
		LogUtils.i("installSlient, path->/mnt/usb/3636-4754/HelloV7_sign.apk");
		String cmd = "pm install -r /mnt/usb/3636-4754/HelloV7_sign.apk";
		Process process = null;
		DataOutputStream os = null;
		BufferedReader successResult = null;
		BufferedReader errorResult = null;
		//获取返回结果
		StringBuilder successMsg = new StringBuilder();
		StringBuilder errorMsg = new StringBuilder();
		try {
			//静默安装需要root权限
			process = Runtime.getRuntime().exec("su");
			os = new DataOutputStream(process.getOutputStream());
			os.write(cmd.getBytes());
			os.writeBytes("\n");
			os.writeBytes("exit\n");
			os.flush();
			//执行命令
			process.waitFor();

			successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
			errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String s;
			while ((s = successResult.readLine()) != null) {
				successMsg.append(s);
			}
			while ((s = errorResult.readLine()) != null) {
				errorMsg.append(s);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (process != null) {
					process.destroy();
				}
				if (successResult != null) {
					successResult.close();
				}
				if (errorResult != null) {
					errorResult.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//显示结果
		Log.i(TAG, "installSlient, 成功消息：" + successMsg.toString() + "\n" + "错误消息: " + errorMsg.toString());
	}

	/**
	 * 执行root命令
	 * @param cmd
	 */
	public static String rootExec(String cmd) {
		Log.e("CmdUtils", "=====start========"+cmd);
		long startTime = System.currentTimeMillis();
		if (cmd.startsWith("su") || cmd.startsWith("ping")) {
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
			Log.e(TAG, "=====cmdutils========"+cmd);
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
			Log.e("CmdUtils", "=====end======== result->"+ result);
		}

		 return result;
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

	public static void execCommand(boolean isRoot,
	                               ShellCommandListener listener, String... commands) throws IOException,
			InterruptedException, TimeoutException {
		Log.d(TAG, "execCommand start");
		int exitCode = -1;
		CommandResult result = null;
		if (commands == null || commands.length == 0) {
			result = new CommandResult(exitCode, null, null);
			listener.onCommandFinished(result);
		}

		Process process = null;
		BufferedReader successReader = null;
		BufferedReader errorReader = null;
		StringBuilder successMsg = null;
		StringBuilder errorMsg = null;

		DataOutputStream os = null;
		process = Runtime.getRuntime().exec(isRoot ? "su" : "sh");
		os = new DataOutputStream(process.getOutputStream());
		for (String command : commands) {
			if (command == null) {
				continue;
			}

			Log.d(TAG, "execCommand command->" + command);
			os.write(command.getBytes());
			os.writeBytes("\n");
			os.flush();
		}
		os.writeBytes("exit\n");
		os.flush();

		exitCode = process.waitFor();
		successMsg = new StringBuilder();
		errorMsg = new StringBuilder();

		successReader = new BufferedReader(new InputStreamReader(
				process.getInputStream()));
		errorReader = new BufferedReader(new InputStreamReader(
				process.getErrorStream()));
		String s = null;
		while ((s = successReader.readLine()) != null) {
			successMsg.append(s + "\n");
		}
		while ((s = errorReader.readLine()) != null) {
			errorMsg.append(s + "\n");
		}

		Log.d(TAG, "successMsg->" + successMsg + " ,errorMsg->" + errorMsg);

		if (exitCode == -257) {
			throw new TimeoutException();
		}

		try {
			if (os != null) {
				os.close();
			}
			if (successReader != null) {
				successReader.close();
			}
			if (errorReader != null) {
				errorReader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (process != null) {
			process.destroy();
		}
		result = new CommandResult(exitCode, successMsg == null ? null
				: successMsg.toString(), errorMsg == null ? null
				: errorMsg.toString());

		listener.onCommandFinished(result);
	}

	/**
	 * result of command,
	 *
	 * @author Trinea 2013-5-16
	 */
	public static class CommandResult {

		/** result of command **/
		public int exitCode;
		/** success message of command result **/
		public String successMsg;
		/** error message of command result **/
		public String errorMsg;

		public CommandResult(int result) {
			this.exitCode = result;
		}

		public CommandResult(int result, String successMsg, String errorMsg) {
			this.exitCode = result;
			this.successMsg = successMsg;
			this.errorMsg = errorMsg;
		}

		@Override
		public String toString() {
			return "exitCode=" + exitCode + "; successMsg=" + successMsg
					+ "; errorMsg=" + errorMsg;
		}
	}

	public interface ShellCommandListener {
		public void onCommandFinished(CommandResult result);
	}

}
