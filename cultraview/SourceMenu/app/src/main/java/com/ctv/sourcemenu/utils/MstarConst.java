package com.ctv.sourcemenu.utils;

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

    String RECENTS_ACTIVITY = "com.android.systemui.recents.RecentsActivity"; // 最近应用

    String LAUNCH_PACKAGE = "com.android.cultraview.launcher.whiteboard"; // Launch应用
    String TV_PLAY_PACKAGE = "com.mstar.tv.tvplayer.ui"; // TV Play包名
    String SCREENCAP_PACKAGE = "com.example.cutcapture"; // 截图包名
    String SCREEN_COMMENT_PACKAGE = "com.ctv.screencomment"; // 批注包名

    String EASY_TOUCH_PACKAGE = "com.ctv.easytouch"; // 悬浮助手包名
    String EASY_TOUCH_SERVICE = "com.ctv.easytouch.service.FloatWindowService"; // 悬浮助手
    String EASY_TOUCH_START = "com.ctv.easytouch.START_ACTION"; // 悬浮启动
    String EASY_TOUCH_STOP = "com.ctv.easytouch.CLOSE_ACTION"; // 悬浮停止

    String COMMENT_ENTER_SERVICE = "xy.screencomment.CommentEnter"; // 批注绘画服务

    // 白板
    int MARK_TYPE_CTV = 0;// 0:CTV批注
    int MARK_TYPE_BOZEE = 1;// 1:宝泽白板
    int MARK_TYPE_ANNOTATION = 2; // 单独批注
    int MARK_TYPE = MARK_TYPE_ANNOTATION;// 默认0:CTV批注，1:宝泽白板

    // CTV批注和截图
    String ACTION_CTV_MARK_OPEN = "com.cultraview.annotate.broadcast.OPEN"; // 启动批注
    String ACTION_CTV_MARK_HIDE = "com.ctv.annotate.FINISH"; // 批注时发送的通知广播
    String ACTION_CTV_SHORTCAP_HIDE = "com.ctv.launcher.FINISH"; // 截图APP关闭

    // 宝泽批注
    String ACTION_BOOZE_MARK_OPEN = "com.bozee.action.PEN_OPEN_OR_HIDE_WRITING";
    String ACTION_MARK_STARTED = "com.bozee.newmark.action.MARK_STARTED";
    String ACTION_BOOZE_MARK_EXIT = "com.bozee.newmark.action.MARK_EXIT";

    String ACTION_START_MARK = "com.bozee.meetingmark.ACTION_START_MARK";
    String ACTION_CLOSE_MARK = "com.bozee.meetingmark.ACTION_CLOSE_MARK";

    String ACTION_START_ANNOTATION = "com.ctv.annotation.ACTION_START_MARK";
    String ACTION_CLOSE_ANNOTATION = "com.ctv.annotation.ACTION_CLOSE_MARK";

    // 宝泽白板
    String ACTION_BOOZE_NOTE_SHOW = "com.bozee.newmark.action.NOTE_SHOW";
    String ACTION_BOOZE_NOTE_HIDE = "com.bozee.newmark.action.NOTE_HIDE";
}
