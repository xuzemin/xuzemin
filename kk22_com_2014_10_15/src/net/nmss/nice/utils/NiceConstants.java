package net.nmss.nice.utils;

import java.util.HashMap;

import android.os.Environment;

public class NiceConstants {
	public final static String username ="username";
	public final static String pwd ="pwd";
	public final static String longitude ="longitude";
	public final static String latitude ="latitude";
	public final static String sign ="sign";
	public final static String age = "age";
	public final static String gender = "gender";
	public final static String registerChannel = "register_channel";
	
	public final static String KEY = "4006073093";
	public final static String TYPE  = "type";
	public final static String RECODE  = "r_e";
	public final static String RECONTENT  = "r_c";
	public final static String weight  = "weight";
	public final static String height  = "height";
	
	public final static String NICKNAME = "nickname";
	public final static String TELEPHONE = "telephone";
	public final static String CODE = "code";
	public final static String NEW_PWD = "new_pwd";
	
	public final static String UID = "uid";
	public final static String PAGE = "page";
	public final static String HEAD_PIC = "head_pic";
	public final static String BIG_PIC = "big_pic";
	public final static String SMALL_PIC = "small_pic";
	public final static String MESSAGE_TIME = "message_time";
	public final static String MESSAGE_CONTENT = "message_content";
	public final static String DIGG_NUM = "digg_num";
	public final static String COMMENT_NUM = "comment_num";
	public final static String MESSAGE_ID = "message_id";
	public final static String ITEM_ID = "item_id";
	public final static String CONTENT = "content";
	public final static String MOOD_ID = "mood_id";
	public final static String SPORT_ID = "sport_id";
	public final static String SPORT_TIME = "sport_time";
	public final static String SPORT_HEAT = "heat";
	public final static String DATE = "date";
	public final static String WEIGHT = "weight";
	
	public final static String DIGG_STATUS = "digg_status";
	public final static String LIMIT = "limit";
	
	
	public final static String ACCESS_TOKEN = "access_token";
	public final static String WEIBO_USER_SHOW_URL = "https://api.weibo.com/2/users/show.json";
	
	
	public final static String PARTNERS_DYNAMIC_FLAG = "PARTNERS_DYNAMIC_FLAG";


	public final static int ON_SUCCESS = 0;
	public final static int ON_START = 1;
	public final static int ON_FINISH = 2;
	public final static int ON_FAILURE = 4;
	
	public static final String CACHE_PIC_DIR = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/mymint/pic/";
	
	public static boolean isChkingAppVersion = false;//是否正在检查
	public static boolean isChecked = false;//是否检查过。
	
	public static final String PK_ACTION = "PK_ACTION";
	public static final HashMap<String, Object> chooseCourse = new HashMap<String, Object>();
	
	public static boolean isUpdateing = false;

	public static String resolution_480_800;
	public static String resolution_540_960;
	public static String resolution_768_1024;
	public static String resolution_800_1280;
	public static String resolution_1080_1800;
	public static String resolution_1080_1920;
	public static String rate;
	public static int W;
	public static int H;
	public static String LoginType;
}
