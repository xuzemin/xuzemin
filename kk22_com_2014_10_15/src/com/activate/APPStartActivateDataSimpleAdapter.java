package com.activate;

import android.content.Context;

public class APPStartActivateDataSimpleAdapter extends
		APPActivateDataSimpleAdapter {
	private String url;
	private Context context;

	public APPStartActivateDataSimpleAdapter(Context context, String url) {
		this.url = url;
		this.context = context;
	}

	@Override
	public String generateUrl() {
		return url;
	}

	@Override
	public String generatePostData() {
		if (context != null) {
			return generateURLparams_start(context);
		}
		return "";
	}
}