package com.protruly.floatwindowlib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.apkfuns.logutils.LogUtils;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.protruly.floatwindowlib.utils.SystemUtils;

/**
 * Desc:启动和关闭应用的广播
 *
 * @author wang
 * @time 2017/3/29.
 */
public class BootReceiver extends BroadcastReceiver {
    private final static String TAG = BootReceiver.class.getSimpleName();

    public static final String FIRST_START_KEY = "FIRST_START";

    public static String AUTOFACMODE_BURNING = "4";
    public static String FACTORY_MODE_FACTEST = "1";
    public static String BURINGMODE_MODE_FACTEST = "1";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "开机自动启动.....");

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)){ // 开机广播
            // 判断是否为工厂老化测试

            String burningMode = SystemUtils.getBurningMode();
            Log.d(TAG, "SystemUtils burningMode->" + burningMode);
            if (burningMode.equals(BURINGMODE_MODE_FACTEST)) {
                LogUtils.d("工厂老化测试.....");
                return;
            }

//            // 开机自启动时，升级时间间隔设置为2s
//            Intent tempIntent = new Intent(context, FloatWindowService.class);
//            Log.d(TAG, "开机自动服务自动启动.....");
//            context.startService(tempIntent);
        }
    }

}
