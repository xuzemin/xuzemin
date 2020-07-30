package com.protruly.floatwindowlib.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.Settings;
import android.util.Log;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvDatabaseManager;
import com.cultraview.tv.CtvPictureManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.common.vo.CtvEnumMfcMode;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

/**
 * 系统工具：获得系统属性
 */
public class SystemUtils {
    public static final String TAG = SystemUtils.class.getSimpleName();

    /**
     * 获得工厂模式
     *
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
     *
     * @return
     */
    public static String getAutoFacMode() {
        String autoFacMode = "0";
        try {
            if (TvManager.getInstance() != null) {
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
     *
     * @return
     */
    public static String getBurningMode() {
        String str = "0";
        try {
            if (TvManager.getInstance() != null) {
                str = CtvCommonManager.getInstance().getEnvironment("factory_burningmode");
                Log.i(TAG, "tvmanager instance is not null");
            }
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获取信号跳转开关
     *
     * @param context
     * @return
     */
    public static int getSignalSwitchMode(Context context) {
        int auto_enable = Settings.System.getInt(context.getContentResolver(), "auto_enable", 1);
        return auto_enable;
    }

    /**
     * 切换信号跳转开关
     *
     * @param context
     * @param bAutoSourceSwitch 0 关 1开
     */
    public static void updateSignalSwitchMode(Context context, int bAutoSourceSwitch) {
        Settings.System.putInt(context.getContentResolver(), "auto_enable", bAutoSourceSwitch);
        int bAutoSo = (bAutoSourceSwitch == 0) ? 0 : 1;
        TvCommonManager.getInstance().setSourceSwitchState(bAutoSo);
    }

    /**
     * 获取图像降噪模式
     *
     * @return
     */
    public static int getImgNoiseMode() {
        int pos = 0;
        switch (CtvPictureManager.getInstance().getNoiseReduction()) {
            case CtvPictureManager.NR_MODE_OFF:
                pos = 0;
                break;
            case CtvPictureManager.NR_MODE_LOW:
                pos = 1;
                break;
            case CtvPictureManager.NR_MODE_MIDDLE:
                pos = 2;
                break;
            case CtvPictureManager.NR_MODE_HIGH:
                pos = 3;
                break;
            default:
                break;
        }
        Log.d(TAG,"getImgNoiseMode-->"+pos);

        return pos;
    }


    public static void setImgNoiseMode(int pos) {
        int imgNoiseMode = CtvPictureManager.NR_MODE_OFF;
        switch (pos) {
            case 0:
                imgNoiseMode = CtvPictureManager.NR_MODE_OFF;
                break;
            case 1:
                imgNoiseMode = CtvPictureManager.NR_MODE_LOW;
                break;
            case 2:
                imgNoiseMode = CtvPictureManager.NR_MODE_MIDDLE;
                break;
            case 3:
                imgNoiseMode = CtvPictureManager.NR_MODE_HIGH;
                break;

            default:
                break;
        }
        CtvPictureManager.getInstance().setNoiseReduction(imgNoiseMode);
    }

    /**
     * 获取比例模式
     *
     * @return
     */
    public static int getZoomMode() {
        int pos = 0;
        switch (CtvPictureManager.getInstance().getVideoArcType()) {
            case CtvPictureManager.VIDEO_ARC_16x9:
                pos = 0;
                break;
            case CtvPictureManager.VIDEO_ARC_4x3:
                pos = 1;
                break;
            default:
                break;
        }
        Log.d(TAG,"getZoomMode-->"+pos);
        return pos;
    }

    /**
     * 设置比例模式
     *
     * @param pos
     */
    public static void setZoomMode(int pos) {
        int zoomMode = CtvPictureManager.VIDEO_ARC_16x9;
        switch (pos) {
            case 0:
                zoomMode = CtvPictureManager.VIDEO_ARC_16x9;
                break;
            case 1:
                zoomMode = CtvPictureManager.VIDEO_ARC_4x3;
                break;
            default:
                break;
        }
        CtvPictureManager.getInstance().setVideoArcType(zoomMode);
    }
}
