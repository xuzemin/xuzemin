package com.android.lottery.util;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

public class AsyncHttpRequestUtil {

	public static AsyncHttpClient client;// = new AsyncHttpClient();

	private AsyncHttpRequestUtil() {
		
	}
	
	public static RequestHandle  get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if(client == null) {
			client = new AsyncHttpClient();
		}
		return client.get(url, params, responseHandler);
	}

	public static RequestHandle  post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		if(client == null) {
			client = new AsyncHttpClient();
		}
		return client.post(url, params, responseHandler);
	}
	
	public static void dispose(Context context) {
		if(client != null) {
			client.cancelRequests(context, true);
			client = null;
		}
	}
}
