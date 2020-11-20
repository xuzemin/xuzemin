package com.mphotool.whiteboard.utils;

/**
 * 敏感信息获取
 * @author wanghang
 * @date 2019/10/30
 */
public class SensitiveConstants {
	// CO_SDK
	public final static String COS_URL = "COS_URL";
	public final static String COS_REGION = "COS_REGION";

	public final static String COS_BUCKET = "COS_BUCKET"; // bucket
	public final static String COS_SECRETID = "COS_SECRETID"; // secretid
	public final static String COS_SECRETKEY = "COS_SECRETKEY"; // seretkey
	public final static String COS_KEY_DURATION = "COS_KEY_DURATION";

	static {
		System.loadLibrary("Sensitive_jni");
	}

	/**
	 * 获得值
	 * @param key
	 * @return
	 */
	public static native String stringFromJNI(String key);
}
