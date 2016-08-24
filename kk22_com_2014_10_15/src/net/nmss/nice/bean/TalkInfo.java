package net.nmss.nice.bean;

import android.widget.TextView;

public class TalkInfo {
	private int id;
	private String headPic;
	private String username;
	private String createTime;
	private String text;
	private String firsttextname;
	private String talk;
	public TalkInfo(int id,String headPic,String username,String createTime,String text,String firsttextname,String talk) {
		this.id = id;
		this.createTime = createTime;
		this.headPic = headPic;
		this.username =username;
		this.text = text;
		this.talk = talk;
		this.firsttextname = firsttextname;
	}
	public String getFirsttextname() {
		return firsttextname;
	}
	public void setFirsttextname(String firsttextname) {
		this.firsttextname = firsttextname;
	}
	public String getTalk() {
		return talk;
	}
	public void setTalk(String talk) {
		this.talk = talk;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHeadPic() {
		return headPic;
	}
	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
