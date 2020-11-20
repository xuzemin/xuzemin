package com.mphotool.whiteboard.utils;

import android.graphics.Color;
import android.os.Environment;
import android.provider.CalendarContract;
import android.support.v4.view.MotionEventCompat;

/**
 * Created by Dong.Daoping on 2018/4/9 0009
 * 说明：
 */
public class Constants {

    public static final String TAG = "TFF_WB";

    public static final String TAG_PAINT = "PAINT_LOG";

    public static final float BORDER_WIDTH_MEDIUM = 2.0f;
    public static final float BORDER_WIDTH_THICK = 3.0f;
    public static final float BORDER_WIDTH_THIN = 1.0f;

    public static final int CODE_SIP_USER_REJECTED = 361;

    /**画笔默认宽度ID*/
    public static final int PEN_WIDTH_DEFAULT = 1;
    /**画笔宽度*/
    public static final float PEN_WIDTH_LITTLE = 3.0f;
    public static final float PEN_WIDTH_MIDDLE = 5.0f;
    public static final float PEN_WIDTH_LARGE = 7.0f;
    public static final float PEN_WIDTH_OVERBIG = 9.0f;
    /**画笔默认颜色*/
    public static final int PEN_COLOLR_DEFAULT = Color.WHITE;

    /**背景默认颜色*/
    public static final int BG_COLOLR_DEFAULT = -13419714;

    /**背景默认样式*/
    public static final int BG_STYLE_DEFAULT = 0;

    /**有宽度的橡皮檫默认大小*/
    public static final int ERASE_VALUE_DEFAULT = 100;
    /**无宽度橡皮檫默认大小*/
    public static final int NO_ERASE_VALUE_DEFAULT = 100;

    public static int erase_value = ERASE_VALUE_DEFAULT;

    public static final int INK_SELECTED_COLOR_MASK = Color.argb(0, MotionEventCompat.ACTION_MASK, 125, MotionEventCompat.ACTION_MASK);

    /**点的最小存储距离*/
    public static final float MIN_DISTANCE_BETWEEN_POINT = 30.0f;

    /**点的最大存储距离*/
    public static final int MAX_DISTANCE_BETWEEN_POINT = 50;

    /**通用序列化ID*/
    public static final int serialVersionUID = 20180420;

    /**可回退次数*/
    public static final int MAX_UNDO_COUNT = 20;

    /**最大页数*/
    public static final int MAX_PAGE_COUNT = 50;

    /**支持同时画线点数*/
    public static final int MULTI_FINGER_NUMBER = 20;

    /**放大缩小限制*/
    public static final float MAX_SCALE_SIZE = 3.0f;
    public static final float MIN_SCALE_SIZE = 0.3f;

    /*默认笔类型*/
    public static final String CTV_PEN_TYPE = "0";

    /**最大文件存储数量*/
    public static final int MAX_FILE_COUNT= 500;

    public static int SCREEN_WIDTH = 1920;
    public static int SCREEN_HEIGHT = 1080;

    /**自动橡皮檫启动默认宽度**/
    public static final String MIN_WIDTH_ACTIVATE_ERASER_DEFAULT = "6";

    public static float MIN_WIDTH_ACTIVATE_ERASER = 6.0f;
    private static float sAutoScaleRatioX = 0.7619048f;
    private static final int ERASER_SHAPE_WIDTH = ((int) (89.0f * sAutoScaleRatioX));

    public static String SYSTEM_CLIENT_CONFIG_NAME = "EDU";

    public static final String IS_MULTI_PEN = "IS_MULTI_PEN";
    public static final String PEN_COLOLR = "PEN_COLOR";
    public static final String BG_COLOLR = "BG_COLOR";
    public static final String BG_STYLE = "BG_STYLE";
    public static final String PEN_SIZE_ID = "PEN_SIZE_ID";
    public static final String ERASE_WIDTH_ID = "persist.sys.wb.sensitivity";
    public static final String PEN_TYPE = "persist.sys.pen.type";
    public static final String HAVE_ERASE = "persist.sys.have.erase";

    public static final String PATH = "/mnt";
    public static final String USB_PATH = "/mnt/usb";
    public static final String SD_PATH = "/mnt/other_sdcard";
    public static final String WHITE_PATH = Environment.getExternalStorageDirectory().toString();

    public static final String WHITE_BOARD_PATH = WHITE_PATH + "/Whiteboard/";
    public static final String WHITE_BOARD_MRC_PATH = WHITE_PATH + "/Whiteboard/mrc";
    public static final String WHITE_BOARD_IMAGE_PATH = WHITE_PATH + "/Whiteboard/image";
    public static final String WHITE_BOARD_SHOT_SCREEN_PATH = WHITE_PATH + "/Whiteboard/screen";
    public static final String WHITE_BOARD_PDF_PATH = WHITE_PATH + "/Whiteboard/pdf";


    public static volatile boolean mIsEraser = false;
    public static boolean is4K = false;
}
