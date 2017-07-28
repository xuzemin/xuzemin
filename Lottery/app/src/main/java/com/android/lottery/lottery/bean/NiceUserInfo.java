package com.android.lottery.lottery.bean;


import com.android.lottery.lottery.util.LogUtil;

public class NiceUserInfo {
	private final static String LOG_TAG = "NiceUserInfo";
	private String name;
	private String pwd;
	private String uId;
	private String head_pic;
	private double latitude = 0.0;//纬度
	private double longitude = 0.0;//经度
	private String declaration;
	private boolean locationSuccess;//是否定位成功
	private String versionCode;
	private String userAgent;
	private String gender;//姓别
	private String loginType;//登陆类型
	private String height;//身高
	private String weight;//体重
	private String birthday;//生日
	private String area;//
	private String user_time;
	private int taskNum;
	private String coin;//K币
	private String telephone;//号码
	private String integral;//积分
	private String memlevel;//等级
	private int upgrade;//还差多少分升级
	private String mood_num;
	private String realtion_num;
	private String fans_num;
	private String target_weight;
	private String target_time;
	
	public final static String MAN = "0";
	public final static String LADY = "1";
	
	public final static String weiboIn = "1";
	public final static String qqIn = "0";
	public final static String regIn = "-1";//注册登入
	
	
	
	private static NiceUserInfo instance = new NiceUserInfo();
	private NiceUserInfo() {
		locationSuccess = false;
		loginType = regIn;
		memlevel = "";
	}
	
	public static NiceUserInfo getInstance() {
		return instance;
	}

	public String getMoodNum() {
		return mood_num;
	}

	public void setMoodNum(String mood_num) {
		this.mood_num = mood_num;
	}
	
	public String getRealtionNum() {
		return realtion_num;
	}

	public void setRealtionNum(String realtion_num) {
		this.realtion_num = realtion_num;
	}
	
	public String getFansNum() {
		return fans_num;
	}

	public void setFansNum(String fans_num) {
		this.fans_num = fans_num;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getUId() {
//		return "3006543";//测试
		return uId;
	}

	public void setUId(String uId) {
		this.uId = uId;
	}

	public String getHead_pic() {
		return head_pic;
	}

	public void setHead_pic(String head_pic) {
		LogUtil.i(LOG_TAG, "head_pic:"+head_pic);
		this.head_pic = head_pic;
	}

	public String getLatitude() {
		return String.valueOf(latitude);
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return String.valueOf(longitude);
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getDeclaration() {
		return declaration;
	}

	public void setDeclaration(String declaration) {
		if(declaration != null) {
			declaration = declaration.trim();
		}
		this.declaration = declaration;
	}

	
	public boolean isLocationSuccess() {
		return locationSuccess;
	}

	public void setLocationSuccess(boolean locationSuccess) {
		this.locationSuccess = locationSuccess;
	}

	
	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	
	public  String getUserAgent() {
		return userAgent;
	}

	public  void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}
	
	public String getUserTime() {
		return user_time;
	}

	public void setUserTime(String time) {
		this.user_time = time;
	}
	
	public String getLoginType() {
		return loginType;
	}

	public void setLoginType(String loginType) {
		this.loginType = loginType;
	}

	
	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	
	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}
	
	public String getBirthday() {
		return birthday;
	}
	
	public void setTargetWeight(String weight){
		this.target_weight = weight;
	}
	
	public String getTargetWeight(){
		return target_weight;
	}
	
	public void setTargetTime(String time){
		this.target_time = time;
	}
	
	public String getTargetTime(){
		return target_time;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	
	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	

	public String getIntegral() {
		return integral;
	}

	public void setIntegral(String integral) {
		this.integral = integral;
	}

	
	
	public String getMemlevel() {
		return memlevel;
	}

	public void setMemlevel(String memlevel) {
		if(memlevel.equals("4")){
			memlevel = "K";
		}
		this.memlevel = memlevel;
	}

	public int getUpgrade() {
		return upgrade;
	}

	public void setUpgrade(int upgrade) {
		this.upgrade = upgrade;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("name:").append(name).append('\n');
		sb.append("pwd:").append(pwd).append('\n');
		sb.append("uId:").append(uId).append('\n');
		sb.append("head_pic:").append(head_pic).append('\n');
		sb.append("latitude:").append(latitude).append('\n');
		sb.append("longitude:").append(longitude).append('\n');
		sb.append("declaration:").append(declaration).append('\n');
		sb.append("locationSuccess:").append(locationSuccess).append('\n');
		return sb.toString();
	}
}


