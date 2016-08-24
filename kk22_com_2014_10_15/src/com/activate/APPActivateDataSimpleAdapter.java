package com.activate;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.UUID;

import com.activate.data.ActivateDataSimpleAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;

public abstract class APPActivateDataSimpleAdapter extends
		ActivateDataSimpleAdapter {
	String appkey = "";
	String imei = "";
	String mac = "";
	String app_sid = "";
	String phone_model = "";
	String os = "";
	String dpi_width = "";
	String dpi_height = "";
	String sid = "";
	String from = "093";
	String SPECIAL_IMEI = "";
	String SPECIAL_ANDROID_ID = "";

	public String generateURLparams_start(Context context) {
		String[] paramName = new String[] { "imei", "mac", "app_sid",
				"phone_model", "os", "dpi_width", "dpi_height", "sid", "from" };
		getBaseUrlParams(context);
		sid = generateSID(context);

		String[] paramsValue = { imei, mac, app_sid, phone_model, os,
				dpi_width, dpi_height, sid, from };
		return appkey + generateUrlParas(paramName, paramsValue);
	}

	public String generateURLparams_do(Context context, String user_act,
			String uid, String year) {
		String[] paramName = new String[] { "user_act", "imei", "uid", "year",
				"mac", "app_sid", "phone_model", "os", "dpi_width",
				"dpi_height", "sid", "from" };
		getBaseUrlParams(context);
		sid = md5(uid + year);

		String[] paramsValue = { user_act, imei, uid, year, mac, app_sid,
				phone_model, os, dpi_width, dpi_height, sid, from };
		return appkey + generateUrlParas(paramName, paramsValue);
	}

	public void getBaseUrlParams(Context context) {
		imei = generateIMEI(context);

		mac = generateMAC(context);
		// 获取 手机型号
		phone_model = android.os.Build.MODEL;
		// 获取 手机SDK版本号
		os = "android" + android.os.Build.VERSION.RELEASE;

		// 获取 手机分辨率
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		// 获得手机的宽带和高度像素单位为px
		String str = "手机屏幕分辨率为:" + dm.widthPixels + " * " + dm.heightPixels;
		dpi_width = dm.widthPixels + "";
		dpi_height = dm.heightPixels + "";
		appkey = getAppKey(context);
		app_sid = getApp_sid(context);
	}

	public String generateSID(Context context) {
		String sid = "";
		String appkey = getKey(context);
		// 拼装 SID
		if (TextUtils.isEmpty(imei)) {
			if (TextUtils.isEmpty(mac)) {
				if (TextUtils.isEmpty(getApp_sid(context))) {
					sid = md5(appkey + getApp_sid(context));
				} else {
					sid = md5(appkey + getApp_sid(context));
				}
			} else {
				sid = md5(appkey + mac);
			}
		} else {
			sid = md5(appkey + imei);
		}
		return sid;
	}

	public String generateMAC(Context context) {
		// 获取 MAC 地址
		WifiManager manager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = manager.getConnectionInfo();
		String macAddress = wifiInfo.getMacAddress();
		if (null != macAddress) {
			String mac = macAddress.replace(".", "").replace(":", "")
					.replace("-", "").replace("_", "");

			return mac;
		}
		return "";
	}

	public String generateIMEI(Context context) {
		// 获取 IMEI
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);// 获取当前手机管理器
		String imei = telephonyManager.getDeviceId();
		return imei;
	}

	// 获取 app_sid
	private String getApp_sid(Context context) {
		SharedPreferences config = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		String app_sid = config.getString("app_sid", "");
		if ("".equals(app_sid)) {
			app_sid = generateApp_sid(context);
			Editor editor = config.edit();
			editor.putString("app_sid", app_sid);
			editor.commit();
		}
		return app_sid;
	}

	private String generateApp_sid(Context context) {
		String uuid = UUID.randomUUID().toString();
		return uuid;
	}

	public static String md5(String s) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			s = s + "1JZ#r!ZhYIfb";
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();
			return toHexString(messageDigest);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String toHexString(byte[] keyData) {
		if (keyData == null) {
			return null;
		}
		int expectedStringLen = keyData.length * 2;
		StringBuilder sb = new StringBuilder(expectedStringLen);
		for (int i = 0; i < keyData.length; i++) {
			String hexStr = Integer.toString(keyData[i] & 0x00FF, 16);
			if (hexStr.length() == 1) {
				hexStr = "0" + hexStr;
			}
			sb.append(hexStr);
		}
		return sb.toString();
	}

	public static String generateUrlParas(String[] name, String[] value) {
		StringBuilder sb = new StringBuilder();
		sb.append(name[0] + "=" + value[0]);
		for (int i = 1; i < name.length; i++) {
			sb.append("&" + name[i] + "=" + value[i]);
		}
		return sb.toString();
	}

	private String getAppKey(Context context) {
		String st = "";
		String wf = "";
		String sid1 = "";
		try {
			String appKey = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).metaData
					.getString("UMENG_CHANNEL");
			if (!TextUtils.isEmpty(appKey)) {
				String[] appkeys = appKey.split("_");
				for (int i = 0; i < appkeys.length; i++) {
					switch (i) {
					case 0:
						st = appkeys[i];
						break;
					case 1:
						wf = appkeys[i];
						break;
					case 2:
						sid1 = appkeys[i];
						break;

					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("st=");
		sb.append(st);
		sb.append("&wf=");
		sb.append(wf);
		sb.append("&sid1=");
		sb.append(sid1);
		sb.append("&");
		return sb.toString();
	}

	private String getKey(Context context) {
		String st = "";
		String wf = "";
		String sid1 = "";
		try {
			String appKey = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).metaData
					.getString("UMENG_CHANNEL");
			if (!TextUtils.isEmpty(appKey)) {
				String[] appkeys = appKey.split("_");
				for (int i = 0; i < appkeys.length; i++) {
					switch (i) {
					case 0:
						st = appkeys[i];
						break;
					case 1:
						wf = appkeys[i];
						break;
					case 2:
						sid1 = appkeys[i];
						break;

					default:
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append(st);
		sb.append(wf);
		sb.append(sid1);
		return sb.toString();
	}
}