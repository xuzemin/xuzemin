package com.android.lottery.share;

public class Conf {
	
	public final static String APIKEY = "qpgr4qlXirSnGLoKYFaPF0Eb";
	//social demo
	public final static String SINA_APP_KEY = "4292548621";
    
	public static final String APP_ID = "wx17fd97457e2e77a6";//微信appid
	public static final String APP_Secret = "f846ea77027f5c833b2ff4e6384f1749";//微信唯一标示
    public static String APP_Code;
	//personal file storage demo
    public final static String PERSON_STORAGE_DIR_NAME = "/apps/FrontiaDevDemo/pic";

    public final static String PERSON_STORAGE_FILE_NAME = "/apps/FrontiaDevDemo/pic/custom.jpg";
	
    public final static String LOCAL_FILE_NAME = "/sdcard/Download/custom.jpg";
    public final static String FILE1 = "/sdcard/Download/custom.jpg";
    public final static String FILE2 = "/sdcard/Download/custom.jpg";
    public final static String FILE3 = "/sdcard/Download/custom.jpg";
    
    //app file storage demo
    public final static String APP_STORAGE_FILE_NAME = "custom.jpg";

    //statistics demo
    public static final String eventId = "1";//提醒
    public static final String reportId = "fd4278e8f6";
   
    public static final String APP_KEY  = "4292548621";
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    
    public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}
}
