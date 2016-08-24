/**
 * Copyright (c) 2012-2013, Michael Yang 杨福海 (www.yangfuhai.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.tsz.afinal.http;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import net.nmss.nice.utils.LogUtil;
import net.tsz.afinal.http.AjaxCallBack;

/**
 * 
 * @author michael
 * 
 * @param <T>
 *            目前泛型支持 String,File, 以后扩展：JSONObject,Bitmap,byte[],XmlDom
 */
public abstract class MyAjaxCallBack extends AjaxCallBack<String> {
	private final static String LOG_TAG = "MyAjaxCallBack";
//	public boolean showProgressBar = false;
//	public View pbView;
//	public Activity activity;
//	public ViewGroup showInMiddle;

	public MyAjaxCallBack(Activity activity, ViewGroup showInMiddle,boolean showProgressBar) {
//		this.showProgressBar = showProgressBar;
//		this.activity = activity;
//		this.showInMiddle = showInMiddle;
	}

//	/**
//	 * 设置进度,而且只有设置了这个了以后，onLoading才能有效。
//	 * 
//	 * @param progress
//	 *            是否启用进度显示
//	 * @param rate
//	 *            进度更新频率
//	 */
//	@Override
//	public MyAjaxCallBack progress(boolean progress, int rate) {
//		this.progress = progress;
//		this.rate = rate;
//		return this;
//	}

	@Override
	public void onStart() {
//		if(showProgressBar){
//			showProgressBar(activity, showInMiddle);
//		}
//		super.onStart();
	}

	// 在子线程中调用
	@Override
	public final Object onInBackground(Object t) {
		LogUtil.i(LOG_TAG, "onInBackground:"+t);
		JSONObject jObj = null;
		try {
			jObj = new JSONObject((String) t);
			int r_e = jObj.getInt("r_e");
			if (r_e == 0) {
				onPinBackground(jObj);
			}
		} catch (JSONException e) {
			jObj = new JSONObject();
			try {
				jObj.put("r_e", 1);
				jObj.put("r_c", "服务器返回数据格式错误!!!");
				Log.e("MyAjaxCallBack", e.getLocalizedMessage());
			} catch (JSONException e0) {
				e0.printStackTrace();
			}
		}
		return jObj;
	}

	@Override
	public final void onSuccess(Object t) {
		LogUtil.i(LOG_TAG, "onSuccess:"+t);
//		if (showProgressBar) 
//			dissmissProgressBar(showInMiddle);
		try {
			if (t instanceof JSONObject) {
				JSONObject jobj = (JSONObject) t;
				if (jobj.getInt("r_e") == 0) {
					onPSuccess(jobj);
				} else {
					onPFailure(jobj);
				}
				return;
			}
		} catch (JSONException e0) {
			JSONObject jObj = new JSONObject();
			try {
				jObj.put("r_e", 1);
				jObj.put("r_c", "服务器返回数据格式错误！");
				Log.e("MyAjaxCallBack", ((JSONObject) t).toString());
				onPFailure(jObj);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		} 
	}

	@Override
	public final void onFailure(Throwable t, int errorNo, String strMsg) {
		LogUtil.i(LOG_TAG, "onFailure:"+t);
//		if (showProgressBar) 
//			dissmissProgressBar(showInMiddle);
		JSONObject jObj = new JSONObject();
		try {
			jObj.put("r_e", 1);
			jObj.put("r_c", "请求服务器出错！");
			Log.e("MyAjaxCallBack", "请求服务器出错！");
			onPFailure(jObj);
		} catch (JSONException e) {
			e.printStackTrace();
		} 

	}

	public abstract void onPSuccess(JSONObject t);

	public abstract void onPFailure(JSONObject t);

	public abstract void onPinBackground(JSONObject jObj) throws JSONException;


//	public boolean isShowProgressBar() {
////		return showProgressBar;
//	}

//	public void setShowProgressBar(boolean showProgressBar) {
//		this.showProgressBar = showProgressBar;
//	}
	
//	/**
//	 * 显示progressbar
//	 */
//	public void showProgressBar(Activity activity,ViewGroup showInMiddle) {
//		
//		pbView = View.inflate(activity, R.layout.progressbar,null);
//		ViewGroup view = (ViewGroup) activity.findViewById(android.R.id.content);
//		view.addView(pbView);
//	}
//	/**
//	 * dissmiss progressbar
//	 */
//	public void dissmissProgressBar(ViewGroup showInMiddle) {
//		ViewGroup view = (ViewGroup) activity.findViewById(android.R.id.content);
//		view .removeView(pbView);
//	}
}
