package com.activate.data;

public interface ActivateProgressInvoke {
	boolean activateBef();
	void errorInvoke(ActivateResponseData mActivateResponseData);
	void successInvoke(ActivateResponseData mActivateResponseData);
}
