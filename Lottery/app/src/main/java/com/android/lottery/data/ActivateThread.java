package com.android.lottery.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.android.lottery.util.LoadUtils;
import com.android.lottery.util.LogUtil;

public class ActivateThread extends Thread {
	private Context context;
	private String url;
	private String params;
	private ActivateProgressInvoke mActivateProgressInvoke;
	private int methodId;

	public ActivateThread(int methodId, Context context,
			ActivateProgressInvoke mActivateProgressInvoke, String url,
			String params) {
		this.methodId = methodId;
		this.context = context;
		this.url = url;
		this.params = params;
		this.mActivateProgressInvoke = mActivateProgressInvoke;
	}

	@Override
	public void run() {
		super.run();
		LogUtil.e("url_activate", url + "?" + params);
		ActivateResponseData mActivateResponseData = null;
		switch (methodId) {
		case 0:
			mActivateResponseData = getGetRequestContent(context, url, params);
			break;
		case 1:
			mActivateResponseData = getPostRequestContent(context, url, params);
			break;

		default:
			break;
		}
		if (mActivateResponseData != null && mActivateProgressInvoke != null) {
			switch (mActivateResponseData.getError()) {
			case 0:
				mActivateProgressInvoke.successInvoke(mActivateResponseData);
				break;
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			default:
				mActivateProgressInvoke.errorInvoke(mActivateResponseData);
				break;
			}
		}
	}

	/**
	 * 获取GET请求内容
	 * 
	 * @param context
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static ActivateResponseData getGetRequestContent(Context context,
			String url, String text) {
		ActivateResponseData resp_data = new ActivateResponseData();
		try {
			HttpURLConnection conn = getConnection(url + "?" + text, "GET");
			parseRespData(resp_data, conn);
		} catch (IOException e) {
			e.printStackTrace();
			setResponseData(resp_data, 800, "URL格式不正确！");
		}
		return resp_data;
	}

	/**
	 * 获取POST请求内容
	 * 
	 * @param context
	 * @param url
	 * @return
	 * @throws IOException
	 * @throws JSONException
	 */
	public static ActivateResponseData getPostRequestContent(Context context,
			String url, String text) {
		ActivateResponseData resp_data = new ActivateResponseData();
		try {
			byte[] data_source = text.getBytes("UTF-8");
			HttpURLConnection conn = getConnection(url, "POST");
			conn.setDoOutput(true);
			conn.getOutputStream().write(data_source);
			parseRespData(resp_data, conn);
		} catch (IOException e) {
			e.printStackTrace();
			setResponseData(resp_data, 800, "URL格式不正确！");
		}
		return resp_data;
	}

	/**
	 * 获取HTTP连接
	 * 
	 * @param url
	 * @param method
	 * @return
	 * @throws IOException
	 *             网络连接异常
	 */
	public static HttpURLConnection getConnection(String url, String method)
			throws IOException {
		URL mUrl = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
		if (method == null) {
			method = "GET";
		}
		conn.setRequestMethod(method);
		conn.setConnectTimeout(5000);
		return conn;
	}

	private static void parseRespData(ActivateResponseData resp_data,
			HttpURLConnection conn) {
		int return_type;
		String return_content;
		try {
			int code = conn.getResponseCode();
			if (code == 200) {
				String json_str = LoadUtils.loadString(conn);
				JSONObject jsonobj = new JSONObject(json_str);

				return_type = jsonobj.getInt("errorno");
				return_content = jsonobj.getString("errormsg");
			} else {
				return_type = code;
				return_content = "资源没有找到";
			}
		} catch (IOException e) {
			e.printStackTrace();
			// ActivateResponseData 设置网络错误
			return_type = 800;
			return_content = "网络连接出错";
		} catch (JSONException e) {
			e.printStackTrace();
			// 解析错误
			return_type = 800;
			return_content = "数据解析出错";
		}
		setResponseData(resp_data, return_type, return_content);
	}

	private static void setResponseData(ActivateResponseData resp_data,
			int return_type, String return_content) {
		resp_data.setError(return_type);
		resp_data.setErrormsg(return_content);

	}
}
