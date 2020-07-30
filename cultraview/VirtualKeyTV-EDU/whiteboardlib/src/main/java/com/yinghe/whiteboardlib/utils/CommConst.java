package com.yinghe.whiteboardlib.utils;

/**
 * Desc:常量
 *
 * @author wang
 * @time 2017/7/24.
 */
public interface CommConst {
    // 上下翻页，发送广播的action字段
    String ACTION_PAGE_UP = "bql_keycode_page_up";
    String ACTION_PAGE_DOWN = "bql_keycode_page_down";

    String KEY_NEXT_MEETING_DIR = "KEY_NEXT_MEETING_DIR";
    // 会议记录的文件夹名
    String MEETING_DIR_NAME = "%s批注%s";

    // 屏幕截图中
    String IS_SCREEN_SHOT_ING = "IS_SCREEN_SHOT_ING";

    String IS_ANNOTATE_ON = "IS_ANNOTATE_ON";

    String LAST_BLACK_LIGHT = "lastBlackLight";

    // 快捷键
    int ONE_KEY_CLOSE_LIGHT = 0; // 关背光
    int ONE_KEY_WHITEBROAD = 1; // 一键白板
    int ONE_KEY_ANNOTATE = 2; // 一键批注
    int ONE_KEY_PC = 3; // 一键PC
    int ONE_KEY_VAG = 4; // 一键VAG
    int ONE_KEY_HDMI1 = 5; // 一键HDMI1
    int ONE_KEY_MEDIA = 6; // 一键Media

    String SOURCE_INFO = "source_info";
    String VGA_INFO = "vga_info";
    String SHORT_SOURCE_INDEX = "SHORT_SOURCE_INDEX";

    // 底部菜单显示状态
//    int status = 0; // 默认0隐藏，1隐藏中，2隐藏，3显示过程中，4显示
    int MENU_STATUS_DEFAULT = 0; // 默认
    int MENU_STATUS_HIDING = 1; // 1隐藏中
    int MENU_STATUS_HIDDEN = 2; // 2隐藏
    int MENU_STATUS_SHOWING = 3; // 3显示过程中
    int MENU_STATUS_SHOWED = 4; // 4显示

	// 批注或者截图显示状态
	String STATUS_START = "annotate.start";
	int STATUS_DEFAULT = -1;
	int STATUS_VALID = 0; // 一般情况，批注和截图都不显示
	int STATUS_COMMENT = 1; // 批注显示
	int STATUS_SCREENSHOT = 2; // 截图显示

	// 护眼
	String IS_EYE_CARE = "isEyecare"; // "isEyecare"
	// 移动按钮开关
	String EASY_TOUCH_OPEN = "EASY_TOUCH_OPEN";
	int STATUS_CLOSE = 0; // 关
	int STATUS_OPEN = 1; // 开
	String STATUS_CLOSE_STR = "off"; // 关
	String STATUS_OPEN_STR = "on"; // 开

	// 上一次背光
	String STATUS_LAST_BLACKLIGHT = "lastBlackLight";
}
