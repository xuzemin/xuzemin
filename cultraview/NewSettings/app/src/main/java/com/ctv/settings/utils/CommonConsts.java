package com.ctv.settings.utils;

import android.os.Environment;

/**
 * Desp:常量
 * @author wanghang
 * @date 2019/09/12
 */
public interface CommonConsts {
    // sdcard目录
    String SDCARD_DIR = Environment.getExternalStorageDirectory().toString();

    public static final String HOURS_12 = "12";

    public static final String HOURS_24 = "24";

    public static final String KEY_DISPLAYNAME = "name";

    public static final String KEY_GMT = "gmt";

    public static final String XMLTAG_TIMEZONE = "timezone";

    public static final String KEY_OFFSET = "offset";

    public static final String KEY_ID = "id";

    public static final int HOURS_1 = 60 * 60000;

    public static final int UPDATE_DATE_FORMAT = 3;

    public static final int REFRESH_TIMEZONE=4;
    public static final int REFRESH_DATEFORMAT=5;
    public static final int REFRESH_DATEF_AND_TIME=10086;
    public static final int BOOTOPTION_ITEM1=6;
    public static final int REQUEST_SHARE_USB_OPTIONS=7;
    public static final int REQUEST_EYE_PLUS_OPTIONS = 8;
    public static final String[] DATE_FORMAT_STRINGS = {
            "MM-dd-yyyy", "dd-MM-yyyy", "yyyy-MM-dd"
    };
	
	public static final String DEVICE_TITLE = "DEVICE_TITLE";
	public static final int DEVICE_NAME_CHANGE = 0;
	public static final int DIALOG_DISMISS = 200;

    String THEME_DEFAULT = "MyBg"; // 默认背景
    String STYLE_INDEX = "styleIndex"; // 主题索引
}
