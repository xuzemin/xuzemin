package com.android.lottery.data;

public interface ActivateProgressInvoke {
	boolean activateBef();
	void errorInvoke(ActivateResponseData mActivateResponseData);
	void successInvoke(ActivateResponseData mActivateResponseData);
}
