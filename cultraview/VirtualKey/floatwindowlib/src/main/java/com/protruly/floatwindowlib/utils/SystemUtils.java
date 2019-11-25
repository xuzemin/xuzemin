package com.protruly.floatwindowlib.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.util.Locale;

/**
 * 系统工具：获得系统属性
 */
public class SystemUtils {
    public static final String TAG = SystemUtils.class.getSimpleName();

    /**
     * 获得工厂模式
     * @return
     */
    public static String getFactoryMode() {
        String factoryMode = "0";
        try {
            if (TvManager.getInstance() != null) {
                factoryMode = TvManager.getInstance().getEnvironment("factory_mode");
                Log.i(TAG, "tvmanager instance is not null");
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return factoryMode;
    }

    /**
     * 获得自动化工厂模式
     * @return
     */
    public static String getAutoFacMode() {
        String autoFacMode = "0";
        try {
            if (TvManager.getInstance()!= null) {
                autoFacMode = TvManager.getInstance().getEnvironment("AutoFacMode");
                Log.i(TAG, "tvmanager instance is not null");
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return autoFacMode;
    }

    /**
     * 老化测试
     * @return
     */
    public static String getBurningMode() {
        String str = "0";
        try {
            if (TvManager.getInstance()!= null) {
                str = CtvCommonManager.getInstance().getEnvironment("factory_burningmode");
                Log.i(TAG, "tvmanager instance is not null");
            }
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    public static void initAppLanguage(Context context) {
        if (context == null) {
            return;
        }

        Locale.setDefault(Locale.CHINA);
        Configuration config = context.getResources().getConfiguration();
        context.getResources().updateConfiguration(config
                , context.getResources().getDisplayMetrics());
    }
}
