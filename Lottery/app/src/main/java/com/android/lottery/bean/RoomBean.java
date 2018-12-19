package com.android.lottery.bean;

import java.io.Serializable;


public class RoomBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8975044978461198421L;
	
	/**
	 * 用户及好友发表的状态
	 */
	public RoomBean() {
		// TODO Auto-generated constructor stub
	}

	private String uid;
	private String headPic;
	private String nickname;
	private String messageTime;
	private String messageContent;
	private String messageId;
	private String bigPic;
	private String smallPic;
	private String type;
	private int diggNum;//赞个数
	private int commentNum;
	private boolean isDigg;
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String head_pic) {
		this.headPic = head_pic;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(String message_time) {
		this.messageTime = message_time;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String message_content) {
		this.messageContent = message_content;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String message_id) {
		this.messageId = message_id;
	}

	public String getBigPic() {
		return bigPic;
	}

	public void setBigPic(String big_pic) {
		this.bigPic = big_pic;
	}

	public String getSmallPic() {
		return smallPic;
	}

	public void setSmallPic(String small_pic) {
		this.smallPic = small_pic;
	}

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public int getDiggNum() {
		return diggNum;
	}

	public void setDiggNum(int diggNum) {
		this.diggNum = diggNum;
	}

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}
	
	
	public boolean isDigg() {
		return isDigg;
	}

	public void setDigg(boolean isDigg) {
		this.isDigg = isDigg;
	}

}
