package com.protruly.floatwindowlib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.protruly.floatwindowlib.service.FloatWindowService;

/**
 * Desc:显示和隐藏的广播
 *
 * @author Administrator
 * @time 2017/6/17.
 */
public class ShowHideReceiver extends BroadcastReceiver {
    // 显示和隐藏的广播
    public static final String SHOW_ACTION = "com.ctv.launcher.SHOW";
    public static final String HIDE_ACTION = "com.ctv.launcher.HIDE";
    public static final String CLOSE_ACTION = "com.ctv.launcher.CLOSE";

    // 锁定和解锁的广播
    public static final String LOCK_ACTION = "com.ctv.launcher.LOCK";
    public static final String UNLOCK_ACTION = "com.ctv.launcher.UNLOCK";

    public static final String SWIPE_ACTION = "LYD_SHOW_NAVIGATION_BAR";
    public static final String SWIPE_TYPE = "SWIPE_TYPE";

    public static final String RESET_LIGHT_ACTION = "com.ctv.launcher.RESET_LIGHT";
    public static final String LIGHT_SENSE_ACTION = "com.ctv.launcher.LIGHT_SENSE";

    public static final String IS_HIDE = "IS_HIDE";
    public static final String IS_LOCK = "IS_LOCK";


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (HIDE_ACTION.equals(action)){ // 隐藏界面
            LogUtils.d("隐藏界面的广播.....");

            // 开机自启动时，升级时间间隔设置为2s
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.setAction("com.ctv.FloatWindowService.HIDE_ACTION");
            context.startService(tempIntent);
        }
//        else if (SHOW_ACTION.equals(action)){ // 显示界面的广播
//            LogUtils.d("显示界面的广播.....");
//            Intent tempIntent = new Intent(context, FloatWindowService.class);
//            tempIntent.setAction("com.ctv.FloatWindowService.SHOW_ACTION");
//            context.startService(tempIntent);
//        }
        else if (CLOSE_ACTION.equals(action)){ // 关闭广播
            LogUtils.d("关闭的广播.....");

            // 关闭批注
            context.sendBroadcast(new Intent("com.cultraview.annotate.broadcast.CLOSE"));

            // 开机自启动时，升级时间间隔设置为2s
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.setAction("com.ctv.FloatWindowService.CLOSE_ACTION");
            context.startService(tempIntent);
        }
        else if (LIGHT_SENSE_ACTION.equals(action)){ // 更新光感的广播
            LogUtils.d("更新光感的广播.....");
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.setAction("com.ctv.FloatWindowService.LIGHT_SENSE_ACTION");
            context.startService(tempIntent);
        }
        else if (LOCK_ACTION.equals(action)){ // 锁住界面的广播
            LogUtils.d("锁住界面的广播.....隐藏移动按钮");
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.putExtra(IS_LOCK, 1);
            tempIntent.setAction(FloatWindowService.STOP_EASY_TOUCH_ACTION);
            context.startService(tempIntent);
        }
        else if (UNLOCK_ACTION.equals(action)){ // 解锁界面的广播
            LogUtils.d("解锁界面的广播.....显示移动按钮");
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.putExtra(IS_LOCK, 0);
            tempIntent.setAction(FloatWindowService.START_EASY_TOUCH_ACTION);
            context.startService(tempIntent);
        }
        else if (SWIPE_ACTION.equals(action)){ // 侧滑SWIPE的广播
            LogUtils.d("SWIPE_ACTION的广播.....");

            int swipe = intent.getIntExtra("SWIPE_TYPE", -1);
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.putExtra(SWIPE_TYPE, swipe);
            tempIntent.setAction("com.ctv.FloatWindowService.SWIPE_ACTION");
            context.startService(tempIntent);
        }
        else if (RESET_LIGHT_ACTION.equals(action)){ // 恢复背光的广播
            LogUtils.d("恢复背光的广播.....");
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.setAction(FloatWindowService.UPDATE_BLACK_LIGHT_ACTION);
            context.startService(tempIntent);
        }
        else if (Intent.ACTION_LOCALE_CHANGED.equals(action)){ // 恢复背光的广播
	        LogUtils.d("切换语言 Language change");
            //DBHelper.refreshSignalInfo(context.getApplicationContext());
	        FloatWindowManager.removeBottomMenuLayout(context.getApplicationContext());
        }
    }
}
