package com.activate;

import com.activate.data.ActivateProgressInvokeSimple;
import com.activate.data.ActivateResponseData;

import android.content.Context;

public class APPDoActivateProgressInvokeSimple extends
		ActivateProgressInvokeSimple {
	private Context context;

	public APPDoActivateProgressInvokeSimple(Context context) {
		this.context = context;
	}

	public void errorInvoke(ActivateResponseData mActivateResponseData) {

	}

	public void successInvoke(ActivateResponseData mActivateResponseData) {
		if (mActivateResponseData.getError() == ActivateResponseData.ERRORNO3) {
			try {
				long data = (long) Long.parseLong(mActivateResponseData
						.getErrormsg());
				AppActivateTool.storeData(context, 1L);
			} catch (Exception e) {
			}
		}
	}
}