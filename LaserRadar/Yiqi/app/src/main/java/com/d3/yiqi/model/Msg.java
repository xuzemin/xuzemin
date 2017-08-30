package com.d3.yiqi.model;


/**
 * 聊天对话用到的
*/
public class Msg {
	private String username;
	private String msg;
	private String sendDate;
	private String inOrOut;

	public Msg(String username, String msg, String sendDate, String inOrOut) {
		this.username = username;     //send man
		this.msg = msg;    		  //body
		this.sendDate = sendDate;         //send time
		this.inOrOut = inOrOut;        //in or out
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSendDate() {
		return sendDate;
	}

	public void setSendDate(String sendDate) {
		this.sendDate = sendDate;
	}

	public String getInOrOut() {
		return inOrOut;
	}

	public void setInOrOut(String inOrOut) {
		this.inOrOut = inOrOut;
	}

	
	
	
}