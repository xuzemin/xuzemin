package com.activate;

import android.content.Context;

public class APPDoActivateDataSimpleAdapter extends
		APPActivateDataSimpleAdapter {
	private String url;
	private Context context;
	private String user_act;
	private String uid;
	private String year;

	public APPDoActivateDataSimpleAdapter(Context context, String url,
			String user_act, String uid, String year) {
		this.url = url;
		this.context = context;
		this.user_act = user_act;
		this.uid = uid;
		this.year = year;
	}

	@Override
	public String generateUrl() {
		return url;
	}

	@Override
	public String generatePostData() {
		if (context != null) {
			return generateURLparams_do(context, user_act, uid, year);
		}
		return "";
	}
}