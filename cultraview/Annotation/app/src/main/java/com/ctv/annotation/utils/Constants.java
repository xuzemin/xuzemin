package com.ctv.annotation.utils;

import android.graphics.Color;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.os.SystemProperties;
import android.text.TextUtils;

/**
 * Created by Dong.Daoping on 2018/4/9 0009
 * 说明：
 */
public class Constants {
    public static final boolean IS_AH_EDU_QD = TextUtils.equals(SystemProperties.get("ro.build.display.id", ""), "CN8386_AH_EDU_QD");
    public static final int ERASE_VALUE_DEFAULT = 50;
    public static final int BG_COLOLR_DEFAULT = -13419714;
    public static final String BG_COLOLR = "BG_COLOR";
    public static final String TAG = "TFF_WB";
    public static int SCREEN_WIDTH = 1920;
    public static int SCREEN_HEIGHT = 1080;
    public static final String TAG_TOUCH = "Touch_LOG";

    public static final String TAG_PAINT = "PAINT_LOG";

    public static final float BORDER_WIDTH_MEDIUM = 2.0f;
    public static final float BORDER_WIDTH_THICK = 3.0f;
    public static final float BORDER_WIDTH_THIN = 1.0f;

    public static final int CODE_SIP_USER_REJECTED = 361;

    /**画笔宽度*/
    public static final float PEN_WIDTH_LITTLE = 2f;
    public static final float PEN_WIDTH_MIDDLE = 8f;
    public static final float PEN_WIDTH_LARGE =15f;
    /**
     * 画笔默认颜色
     */
    public static final int PEN_COLOLR_DEFAULT = Color.WHITE;
    public static final int PEN_COLOLR_AH_EDU_QD = Color.RED;


    public static final int INK_SELECTED_COLOR_MASK = Color.argb(0, MotionEventCompat.ACTION_MASK, 125, MotionEventCompat.ACTION_MASK);

    /**点的最小存储距离*/
    public static final float MIN_DISTANCE_BETWEEN_POINT = 30.0f;

    /**通用序列化ID*/
    public static final int serialVersionUID = 20180420;

    /**可回退次数*/
    public static final int MAX_UNDO_COUNT = 20;

    /**最大页数*/
    public static final int MAX_PAGE_COUNT = 50;

    /**放大缩小限制*/
    public static final float MAX_SCALE_SIZE = 3.0f;
    public static final float MIN_SCALE_SIZE = 0.3f;

    /**最大文件存储数量*/
    public static final int MAX_FILE_COUNT= 500;

    /**支持同时画线点数*/
    public static final int MULTI_FINGER_NUMBER = 10;

    public static final float MIN_WIDTH_ACTIVATE_ERASER = 5.0f;

    public static float erase_value = 60.0f;
    public static final float ERASER_SIZE_RATE = 5.5f;
//  private static float sAutoScaleRatioX = (getDeviceType() == DeviceType.C105ME ? 0.7619048f : 1.0f);
    private static float sAutoScaleRatioX = 0.7619048f;
    private static final int ERASER_SHAPE_WIDTH = ((int) (89.0f * sAutoScaleRatioX));
    public static final float ERASER_RATIO = (((float) ERASER_SHAPE_WIDTH) / 135.0f);

    public static final int ERASER_WIDTH = 100;
    public static final float ERASER_HEIGHT = 50.0f;
    public static final float MAX_ERASER_WIDTH = 50.0f;
    public static final float MAX_ERASER_HEIGHT = 50.0f;

    public static final String IS_MULTI_PEN = "IS_MULTI_PEN";
    public static final String IS_MARKER = "IS_MARKER";
//    public static boolean mIsTransformer = false;

    public static float touchSlop = 0.0f;

    public static final String ROOT = "/";

    public static final String ROOTPATH = "/mnt";
    public static final String USB_PATH = "/mnt/usb";
    public static final String WHITE_PATH = Environment.getExternalStorageDirectory().toString();
//Annotation
    public static final String WHITE_BOARD_PATH = WHITE_PATH + "/Annotation/";
    public static final String WHITE_BOARD_MRC_PATH = WHITE_PATH + "/Annotation/mrc";
    public static final String WHITE_BOARD_IMAGE_PATH = WHITE_PATH + "/Annotation/image";
    public static final String WHITE_BOARD_SHOT_SCREEN_PATH = WHITE_PATH + "/Annotation/screen";
    public static final String WHITE_BOARD_PDF_PATH = WHITE_PATH + "/Annotation/pdf";

    /**自动橡皮檫启动默认宽度**/
    public static final float MIN_WIDTH_ACTIVATE_ERASER_DEFAULT = 6.0f;





    public static String SYSTEM_CLIENT_CONFIG_NAME = "EDU";


    public static final String PEN_COLOLR = "PEN_COLOR";

    public static final String BG_STYLE = "BG_STYLE";
    public static final String PEN_SIZE_ID = "PEN_SIZE_ID";
    public static final String ERASE_WIDTH_SIZE = "persist.sys.pen.eraser";
    public static final String WRITE_PEN = "WRITE_PEN";

    public static final String PATH = "/mnt";

    public static final String SD_PATH = "/mnt/other_sdcard";

    public static final int NO_ERASE_VALUE_DEFAULT = 70;
    public static volatile boolean mIsEraser = false;
}
