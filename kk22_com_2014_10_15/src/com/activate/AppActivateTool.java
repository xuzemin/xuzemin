package com.activate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import net.nmss.nice.bean.NiceUserInfo;

import com.activate.data.ActivateTotal;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.provider.Settings.Global;

public class AppActivateTool {
	public static long Days = 7 * 24 * 60 * 60 * 1000;
	public static String HOST = "http://stat.kk22.com";

	private static String getAppActUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(HOST);
		sb.append("/mstat/index.php/api/user_act");
		return sb.toString();
	}

	private static String getAppStatUrl() {
		StringBuilder sb = new StringBuilder();
		sb.append(HOST);
		sb.append("/mstat/index.php/api/app_stat");
		return sb.toString();
	}

	public static void doStartActivate(Context context) {
		try {
			new ActivateTotal(context,
					new APPStartActivateProgressInvokeSimple(context),
					new APPStartActivateDataSimpleAdapter(context,
							getAppStatUrl())).performActivateThread(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doLoginActivate(Context context, String user_act,
			String uid, String year) {
		try {
			if (ifOverTime(context)) {
				new ActivateTotal(context,
						new APPStartActivateProgressInvokeSimple(context),
						new APPDoActivateDataSimpleAdapter(context,
								getAppActUrl(), user_act, uid, year))
						.performActivateThread(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void doDoActivate(Context context, String user_act) {
//		doDoActivate(context, user_act, GlobalParams.uid+"", GlobalParams.birthday);
		doDoActivate(context, user_act, NiceUserInfo.getInstance().getUId()+"", NiceUserInfo.getInstance().getBirthday());
	}

	public static void doDoActivate(Context context, String user_act,
			String uid, String year) {
		try {
			if (ifOverTime(context)) {
				new ActivateTotal(context,
						new APPDoActivateProgressInvokeSimple(context),
						new APPDoActivateDataSimpleAdapter(context,
								getAppActUrl(), user_act, uid, year))
						.performActivateThread(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean ifOverTime(Context context) {
		Long time = getTime(context);
		if (time == 0L) {
			time = System.currentTimeMillis();
			storeData(context, time);
		}
		return (System.currentTimeMillis() - time) <= Days;
	}
	public  static   String CACHE_PIC_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/mymint/doc/";
	public  static   String CACHE_PIC_DIR_PATH =  "config";
	public static synchronized Long getTime(Context context) {
		SharedPreferences config = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Long time = config.getLong("time", 0L);
		try {
			if (time == 0L && sdIfex()) {
				File file = new File(CACHE_PIC_DIR);
				if (!file.exists()) {
					file.mkdirs();
				}
				file = new File(file,CACHE_PIC_DIR_PATH );
				if (file.exists()) {
					InputStream in = new FileInputStream(file);
					Properties p = new Properties();
					p.load(in);
					String data = p.getProperty("time", "");
					if (!"".equals(data)) {
						time = Long.parseLong(data);
					}
					in.close();
				}
				if (time != 0L) {
					storeRom(context, time);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return time;
	}

	public static boolean sdIfex() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static void storeData(Context context, Long data) {
		if (data != null && !"".equals(data)) {
			storeRom(context, data);
			storeSD(data);
		}
	}

	public static synchronized void storeSD(Long data) {
		try {
			if (sdIfex()) {
				File file = new File(CACHE_PIC_DIR);
				if (!file.exists()) {
					file.mkdirs();
				}
				file = new File(CACHE_PIC_DIR_PATH);
				OutputStream out = new FileOutputStream(file);
				out.write(("time=" + data + "\n").getBytes("utf-8"));
				out.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static synchronized void storeRom(Context context, Long data) {
		SharedPreferences config = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		Editor editor = config.edit();
		editor.putLong("time", data);
		editor.commit();
	}
}
