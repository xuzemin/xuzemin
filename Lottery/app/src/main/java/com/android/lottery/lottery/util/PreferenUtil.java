package com.android.lottery.lottery.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenUtil {
	private final SharedPreferences preferences;
	private final String NAME = "KK22_COM";
	private final String VERSION_CODE = "versionCode";
	private final String USER_NAME = "user_name";
	private final String USER_PWD = "user_pwd";
	private final String UID = "uid";
	private final String USER_PIC = "user_pic";
	private final String VISITOR_NAME = "visitor_name";
	private final String VISITOR_PWD = "visitor_pwd";
	private final String VISITOR_UID = "visitor_uid";
	private final String LOGIN_STATE = "login_state";
	private final String VISITOR_DEL = "visitor_del";
	public PreferenUtil(Context context) {
		preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
	}
	
	/**
	 * 保存版本号
	 * @param code
	 */
	public void setVersionCode(int code) {
		preferences.edit().putInt(VERSION_CODE, code).commit();
	}
	
	/**
	 * 获取版本号
	 * @return 版本号，如果无则返回-1
	 */
	public int getversionCode() {
		return preferences.getInt(VERSION_CODE, -1);
	}
	
	public void setLoginState(String state){
		preferences.edit().putString(LOGIN_STATE, state).commit();
	}
	
	public String getLoginState(){
		return preferences.getString(LOGIN_STATE, "");
	}
	/**
	 * 保存存用户名
	 * @param userName
	 */
	public void setUserNmae(String userName) {
		preferences.edit().putString(USER_NAME, userName).commit();
	}
	
	/**
	 * 获取保存下来的用户名
	 * @return
	 */
	public String getUserName() {
		return preferences.getString(USER_NAME, "");
	}
	
	/**
	 * 保存存用户密码
	 * @param userName
	 */
	public void setUserPwd(String userPwd) {
		preferences.edit().putString(USER_PWD, userPwd).commit();
	}
	
	/**
	 * 获取保存下来的用户密码
	 * @return
	 */
	public String getUserPwd() {
		return preferences.getString(USER_PWD, "");
	}
	
	public void setVisitDel(String del){
		preferences.edit().putString(VISITOR_DEL, del).commit();
	}
	
	public String getVisitDel(){
		return preferences.getString(VISITOR_DEL, "");
	}

	public String getUID() {
		return preferences.getString(UID, "");
	}
	
	public void setUid(String uid) {
		preferences.edit().putString(UID, uid).commit();
	}
	
	public void setVisitName(String name){
		preferences.edit().putString(VISITOR_NAME,name).commit();
	}
	
	public String getVisitName(){
		return preferences.getString(VISITOR_NAME, "");
	}
	
	public void setVisitPwd(String pwd){
		preferences.edit().putString(VISITOR_PWD,pwd).commit();
	}
	
	public String getVisitPwd(){
		return preferences.getString(VISITOR_PWD, "");
	}
	
	public void setVisitUid(String uid){
		preferences.edit().putString(VISITOR_UID,uid).commit();
	}
	
	public String getVisitUid(){
		return preferences.getString(VISITOR_UID, "");
	}
	
	public String getUserHeadPic() {
		return preferences.getString(USER_PIC, "");
	}
	
	public void setUserHeadPic(String url) {
		preferences.edit().putString(USER_PIC, url).commit();
	}
}
