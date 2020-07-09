package com.ctv.easytouch.utils;

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

    String TV_PLAY_PACKAGE = "com.mstar.tv.tvplayer.ui"; // TV Play包名
    String TV_MENU_PACKAGE = "com.cultraview.ctvmenu"; // CTV Menu

    String SCREENCAP_PACKAGE = "com.example.cutcapture"; // 截图包名
    String SCREEN_COMMENT_PACKAGE = "com.ctv.screencomment"; // 批注包名

    String LAUNCH_PACKAGE = "com.dazzle.mlauncher"; // launch包名

    String AUTO_SEARCH_PACKAGE = "com.cultraview.ctvmenu.ui.channel.AutoChannelActivity"; // 自动搜台
    String DTV_SEARCH_PACKAGE = "com.cultraview.ctvmenu.ui.channel.DTVManualTuning"; // DTV搜台
    String ATV_SEARCH_PACKAGE = "com.cultraview.ctvmenu.ui.channel.ATVManualTuning"; // ATV搜台

    String RECENTS_ACTIVITY = "com.android.systemui.recents.RecentsActivity"; // 最近应用

    String EASY_TOUCH_SERVICE = "com.ctv.easytouch.service.FloatWindowService"; // 悬浮助手
}
