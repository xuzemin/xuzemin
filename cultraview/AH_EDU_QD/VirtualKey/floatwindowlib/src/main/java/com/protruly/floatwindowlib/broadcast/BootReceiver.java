package com.protruly.floatwindowlib.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
    public static final String FIRST_START_KEY = "FIRST_START";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d("开机自动启动.....");

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)){ // 开机广播
            // 判断是否为工厂老化测试 自动化测试
            if (SystemUtils.isAutoTestOrBurning()) {
                LogUtils.d("工厂老化或者自动化测试.....");
                return;
            }

            // 开机自启动时，升级时间间隔设置为2s
            Intent tempIntent = new Intent(context, FloatWindowService.class);
            LogUtils.d("开机自动服务自动启动.....");
            context.startService(tempIntent);
        }
    }

}
