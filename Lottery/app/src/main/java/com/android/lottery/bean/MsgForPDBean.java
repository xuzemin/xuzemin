package com.android.lottery.bean;

import java.io.Serializable;

public class MsgForPDBean implements Serializable{

	private static final long serialVersionUID = -2782888888754641232L;
	
	private boolean isSuccess;
	private int rCode;
	private String content;
	private int position;
	private int state;
	
	public MsgForPDBean() {
		
	}


	public boolean isSuccess() {
		return isSuccess;
	}

	public void isSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getrCode() {
		return rCode;
	}

	public void setrCode(int rCode) {
		this.rCode = rCode;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}
	
	
	
}
