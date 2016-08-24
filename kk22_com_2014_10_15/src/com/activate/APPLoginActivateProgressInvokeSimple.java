package com.activate;

import com.activate.data.ActivateResponseData;

import android.content.Context;

public class APPLoginActivateProgressInvokeSimple extends
		APPActivateProgressInvokeSimple {

	public APPLoginActivateProgressInvokeSimple(Context context) {
		this.context = context;
	}

	public void errorInvoke(ActivateResponseData mActivateResponseData) {

	}

	public void successInvoke(ActivateResponseData mActivateResponseData) {
		try {
			long data = (long) Long.parseLong(mActivateResponseData
					.getErrormsg());
			AppActivateTool.storeData(context, data);
		} catch (Exception e) {
		}
	}
}