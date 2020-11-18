package com.protruly.floatwindowlib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.activity.MainActivity;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.yinghe.whiteboardlib.utils.AppUtils;

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

    public static final String IS_HIDE = "IS_HIDE";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (HIDE_ACTION.equals(action)){ // 隐藏界面
            LogUtils.d("隐藏界面的广播.....");

            // 开机自启动时，升级时间间隔设置为2s
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.putExtra(IS_HIDE, true);
            context.startService(tempIntent);
        } else if (SHOW_ACTION.equals(action)){ // 显示界面的广播
            LogUtils.d("显示界面的广播.....");
//            Toast toast = Toast.makeText(context, "显示界面的广播....",Toast.LENGTH_LONG);
//            toast.setGravity(Gravity.CENTER, 0, 0);
//            toast.show();

            Intent tempIntent = new Intent(context, FloatWindowService.class);
            tempIntent.putExtra(IS_HIDE, false);
            context.startService(tempIntent);
        }

        if(intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
            Log.e("LocaleChangeReceiver","Language change");
         //   FloatWindowManager.removeSettingsDialog(context.getApplicationContext());
//            SystemUtil.initAppLanguage(context)

            FloatWindowManager.removeSignalDialog(context.getApplicationContext());

            //new Thread(()->{
            //    AppUtils.openOrCloseEasyTouch(context, false);
            //    SystemClock.sleep(1000);
            //    checkEasyTouch(context.getApplicationContext());
            //}).start();
        }
    }

    /**
     * 检测是否开启悬浮助手
     */
    private void checkEasyTouch(Context context){
        // 判断是否开启悬浮助手
        int easyTouchOpen = Settings.System.getInt(context.getContentResolver(),
                "EASY_TOUCH_OPEN", 0);
        String lockStatus = SystemProperties.get("persist.sys.lockScreen", "off");
        LogUtils.d("easyTouchOpen->%s, lockStatus->%s" ,easyTouchOpen, lockStatus);
        // 悬浮助手开关开启，并且不在锁屏状态下时
        if (easyTouchOpen == 1 && !TextUtils.equals(lockStatus, "on")) { // 开启
            AppUtils.openOrCloseEasyTouch(context, true);
        }
    }
}
