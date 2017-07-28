package com.android.lottery.lottery.data;

public class ActivateResponseData {
	public static int ERRORNO0 = 0;
	public static int ERRORNO1 = 1;
	public static int ERRORNO2 = 2;
	public static int ERRORNO3 = 3;
	public static int ERRORNO4 = 4;
	private int error;
	private String errormsg;

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getErrormsg() {
		return errormsg;
	}

	public void setErrormsg(String errormsg) {
		this.errormsg = errormsg;
	}

}
