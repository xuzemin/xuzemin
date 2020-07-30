package com.yinghe.whiteboardlib.utils;

/**
 * Desc:常量
 *
 * @author wang
 * @time 2017/7/24.
 */
public interface MstarConst {
    // 启动信号源界面
    String ACTION_START_TV_PLAYER = "com.mstar.android.intent.action.START_TV_PLAYER";
    String KEY_START_TV_PLAYER_INPUT_SRC = "inputSrc";
    String KEY_START_TV_PLAYER_TASK_TAG = "task_tag";

    String VALUE_START_TV_PLAYER_TASK_TAG = "input_source_changed";

    String LAUNCH_PACKAGE = "com.example.newfourview"; // Launch应用
    String TV_PLAY_PACKAGE = "com.mstar.tv.tvplayer.ui"; // TV Play包名
    String SCREENCAP_PACKAGE = "com.example.cutcapture"; // 截图包名
    String SCREEN_COMMENT_PACKAGE = "com.ctv.screencomment"; // 批注包名

    String AUTO_SEARCH_PACKAGE = "com.cultraview.ctvmenu.ui.channel.AutoChannelActivity"; // 自动搜台
    String DTV_SEARCH_PACKAGE = "com.cultraview.ctvmenu.ui.channel.DTVManualTuning"; // DTV搜台
    String ATV_SEARCH_PACKAGE = "com.cultraview.ctvmenu.ui.channel.ATVManualTuning"; // ATV搜台

    String RECENTS_ACTIVITY = "com.android.systemui.recents.RecentsActivity"; // 最近应用

    String EASY_TOUCH_PACKAGE = "com.ctv.easytouch"; // 悬浮助手包名
    String EASY_TOUCH_SERVICE = "com.ctv.easytouch.service.FloatWindowService"; // 悬浮助手
    String EASY_TOUCH_START = "com.ctv.easytouch.START_ACTION"; // 悬浮启动
    String EASY_TOUCH_STOP = "com.ctv.easytouch.CLOSE_ACTION"; // 悬浮停止

    String COMMENT_ENTER_SERVICE = "com.ctv.annotation.AnnotationService"; // 批注绘画服务
}
