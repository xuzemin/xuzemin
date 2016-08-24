package net.nmss.nice.bean;

public class CodeBean {
	private String access_token;
	private String refresh_token;
	private String openid;
	
	public void setAccess(String access){
		this.access_token = access;
	}
	
	public String getAccess(){
		return access_token;
	}
	
	public void setRefresh(String refresh){
		this.refresh_token = refresh;
	}
	
	public String getRefresh(){
		return refresh_token;
	}
	
	public void setOpenId(String id){
		this.openid = id;
	}
	
	public String getOpenId(){
		return openid;
	}

}
