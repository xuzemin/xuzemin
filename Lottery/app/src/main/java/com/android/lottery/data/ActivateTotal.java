package com.android.lottery.data;


import android.content.Context;

public class ActivateTotal {
	private Context context;
	private ActivateProgressInvoke mActivateProgressInvoke;
	private ActivateDataAdapter mActivateDataAdapter;

	public ActivateTotal(Context context) {
		this.context = context;
	}

	public ActivateTotal(Context context,
			ActivateDataAdapter mActivateDataAdapter) {
		this.context = context;
		this.mActivateDataAdapter = mActivateDataAdapter;
	}

	public ActivateTotal(Context context,
			ActivateProgressInvoke mActivateProgressInvoke,
			ActivateDataAdapter mActivateDataAdapter) {
		this.context = context;
		this.mActivateProgressInvoke = mActivateProgressInvoke;
		this.mActivateDataAdapter = mActivateDataAdapter;
	}

	/**
	 * 
	 * @param methodId
	 *            0 GET 1 POST
	 */
	public void performActivateThread(int methodId) {
		if (mActivateDataAdapter == null) {
			return;
		}
		if (mActivateProgressInvoke != null) {
			mActivateProgressInvoke.activateBef();
		}
		if (mActivateDataAdapter != null) {
			new ActivateThread(methodId, context, mActivateProgressInvoke,
					mActivateDataAdapter.generateUrl(),
					mActivateDataAdapter.generatePostData()).start();
		}
	}

	public ActivateProgressInvoke getmActivateProgressInvoke() {
		return mActivateProgressInvoke;
	}

	public void setmActivateProgressInvoke(
			ActivateProgressInvoke mActivateProgressInvoke) {
		this.mActivateProgressInvoke = mActivateProgressInvoke;
	}

	public ActivateDataAdapter getmActivateDataAdapter() {
		return mActivateDataAdapter;
	}

	public void setmActivateDataAdapter(ActivateDataAdapter mActivateDataAdapter) {
		this.mActivateDataAdapter = mActivateDataAdapter;
	}
}
