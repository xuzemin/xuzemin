package com.android.lottery.lottery.data;

public interface ActivateProgressInvoke {
	boolean activateBef();
	void errorInvoke(ActivateResponseData mActivateResponseData);
	void successInvoke(ActivateResponseData mActivateResponseData);
}
