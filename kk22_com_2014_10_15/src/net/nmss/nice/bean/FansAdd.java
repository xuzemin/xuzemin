package net.nmss.nice.bean;

import android.widget.TextView;

public class FansAdd {
	private int id;
	private String headPic;
	private String username;
	private String createTime;
	public FansAdd(int id,String headPic,String username,String createTime) {
		this.id = id;
		this.headPic = headPic;
		this.username =username;
		this.createTime=createTime;
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
}
