package com.hht.android.sdk.service.utils;

import android.content.Context;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;

import com.cultraview.tv.common.vo.CtvEnumInputSource;
import com.hht.android.sdk.boardInfo.HHTConstant;
import com.hht.android.sdk.device.HHTCommonManager;
import com.mstar.android.tv.TvPictureManager;

import java.util.concurrent.ExecutorService;
import com.mstar.android.tv.TvCommonManager;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

import static mstar.tvsetting.factory.desk.IFactoryDesk.EN_MS_COLOR_TEMP.MS_COLOR_TEMP_NATURE;
import static mstar.tvsetting.factory.desk.IFactoryDesk.EN_MS_COLOR_TEMP.MS_COLOR_TEMP_USER2;


public class EyeProtectionUtils {
    private final static String TAG = EyeProtectionUtils.class.getSimpleName();
    private static EyeProtectionUtils instance = null;

    private IFactoryDesk mFactoryDesk;

    private static final int[] SOURCE_IDS = new int[]{
            0, 1,2,16,23,24,25,28,34
    };

//    /**
//     * default color gain offset
//     */
//    final static int[][] DEFAULT_COLORS = new int[][]{
//            {1, 1042, 952, 965, 1024, 1024, 1024},// NATURE VGA
//            {1, 1008, 952, 970, 1024, 1024, 1024},// NATURE HDMI
//    };
//
//    /**
//     * eye plus color gain offset
//     */
//    final static int[][] EYE_PLUS_COLORS = new int[][]{
//            {4, 930, 1024, 760, 1024, 1024, 1024},// USER2 VGA
//            {4, 1012, 1024, 821, 1024, 1024, 1024},// USER2 HDMI
//    };

    public static EyeProtectionUtils getInstance() {
        if (instance == null) {
            synchronized (EyeProtectionUtils.class) {
                if (instance == null) {
                    instance = new EyeProtectionUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 设置护眼+或者恢复
     *
     * @param context
     * @param isEyePlus
     */
    public void setEyePlusMode(final Context context, final boolean isEyePlus) {
        try {
            L.d(TAG, "setEyePlusMode start");
            if (mFactoryDesk == null){
                mFactoryDesk = FactoryDeskImpl.getInstance(context);
                mFactoryDesk.loadFactoryColorTempExDataFromDB();
            }

            // if isEyePlus is true, change ColorTmpIdx to USER2;else change to nature
            IFactoryDesk.EN_MS_COLOR_TEMP colorTmp = isEyePlus ? MS_COLOR_TEMP_USER2 :MS_COLOR_TEMP_NATURE ;
            for (int inputSource:SOURCE_IDS) {
                if(inputSource != TvCommonManager.getInstance().getCurrentTvInputSource()){
                    // mFactoryDesk.setWBIdx(CtvEnumInputSource.values()[inputSource]);
                    mFactoryDesk.updateColorTmpIdx(colorTmp,CtvEnumInputSource.values()[inputSource]);
                }
            }
            mFactoryDesk.setWBIdx(CtvEnumInputSource.values()[TvCommonManager.getInstance().getCurrentTvInputSource()]);
            mFactoryDesk.setColorTmpIdx(colorTmp);
        } catch (Exception e){
            e.printStackTrace();
            L.d(TAG, "setEyePlusMode error:" + e.getMessage());
        }
        L.d(TAG, "setEyePlusMode end");
    }

    /**
     * set current mode
     *
     * @param context
     * @param lastMode
     */
    private void resetLastMode(Context context, int lastMode){
        L.d(TAG, "resetLastMode lastMode:" + lastMode);
        if (lastMode == HHTCommonManager.EnumEyeProtectionMode.EYE_OFF.ordinal()){ // EYE_OFF
            L.d(TAG, "resetLastMode lastMode is EYE_OFF");
            // save backlight value
            int backlight = SystemProperties.getInt(HHTConstant.SYS_BACKLIGHT,50);//TvPictureManager.getInstance().getBacklight();
            Settings.System.putInt(context.getContentResolver(), HHTConstant.EYE_LAST_BACKLIGHT, backlight);
        }
        else if (lastMode == HHTCommonManager.EnumEyeProtectionMode.EYE_RGB.ordinal()){ // EYE_RGB
            // reset to PICTURE_MODE_NORMAL
            Tools.setPictureModeForAllSource(context, TvPictureManager.PICTURE_MODE_NORMAL);
//            TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_NORMAL);
            L.d(TAG, "resetLastMode lastMode is EYE_RGB");
        }
        else if (lastMode == HHTCommonManager.EnumEyeProtectionMode.EYE_PLUS.ordinal()) { // EYE_PLUS
            // reset color gain and offset
            setEyePlusMode(context,false);
            L.d(TAG, "resetLastMode lastMode is EYE_PLUS");
        }
        else if (lastMode == HHTCommonManager.EnumEyeProtectionMode.EYE_DIMMING.ordinal()) { // EYE_DIMMING
            L.d(TAG, "resetLastMode lastMode is EYE_DIMMING");
            int lastbacklight = Settings.System.getInt(context.getContentResolver(), HHTConstant.EYE_LAST_BACKLIGHT, 50);
            SystemProperties.set(HHTConstant.SYS_BACKLIGHT, "" + lastbacklight);
            TvPictureManager.getInstance().setBacklight(lastbacklight);
        }
        else if (lastMode == HHTCommonManager.EnumEyeProtectionMode.EYE_WRITE_PROTECT.ordinal()) { // EYE_WRITE_PROTECT
            L.d(TAG, "resetLastMode lastMode is EYE_WRITE_PROTECT");
        }
    }

    /**
     * set current mode
     *
     * @param context
     * @param mode
     */
    private void setCurrentMode(Context context, int mode){
        L.d(TAG, "setCurrentMode mode:" + mode);
        // set current eye mode
        SystemProperties.set(HHTConstant.EYE_PROTECTION_MODE, "" + mode);
        if (mode == HHTCommonManager.EnumEyeProtectionMode.EYE_RGB.ordinal()) {// EYE_RGB
            L.d(TAG, "setCurrentMode mode:EYE_RGB");
            Tools.setPictureModeForAllSource(context, TvPictureManager.PICTURE_MODE_SOFT);
//            TvPictureManager.getInstance().setPictureMode(TvPictureManager.PICTURE_MODE_SOFT);
        }
        else if (mode == HHTCommonManager.EnumEyeProtectionMode.EYE_PLUS.ordinal()) { // EYE_PLUS
            L.d(TAG, "setCurrentMode mode:EYE_PLUS");
            setEyePlusMode(context,true);
        }
        else if (mode == HHTCommonManager.EnumEyeProtectionMode.EYE_OFF.ordinal()){// EYE_OFF
            L.d(TAG, "setCurrentMode mode:EYE_OFF");
            // reset to last backlight
            int lastBacklight = Settings.System.getInt(context.getContentResolver(), HHTConstant.EYE_LAST_BACKLIGHT, 50);
            SystemProperties.set(HHTConstant.SYS_BACKLIGHT, "" + lastBacklight);
            TvPictureManager.getInstance().setBacklight(lastBacklight);
        }
    }

    /**
     * 设置护眼模式
     * @param mode
     * @return
     */
    public boolean setEyeProtectionMode(final Context context, final int mode, final ExecutorService mThreadPool){
        final int lastMode = SystemProperties.getInt(HHTConstant.EYE_PROTECTION_MODE,
                HHTCommonManager.EnumEyeProtectionMode.EYE_OFF.ordinal());

        // if last mode is equals current mode, do nothing
        if (lastMode == mode){
            L.d(TAG, "setEyeProtectionMode last mode is equals current mode, do nothing");
            return false;
        }

        L.d(TAG, "setEyeProtectionMode lastMode:%s, mode:%s", lastMode, mode);
        // if last mode or current mode is EYE_RGB or EYE_PLUS
        // last mode will handle db
        boolean isLastModeHandleDB = (lastMode == HHTCommonManager.EnumEyeProtectionMode.EYE_RGB.ordinal()
                || lastMode == HHTCommonManager.EnumEyeProtectionMode.EYE_PLUS.ordinal());
        // current mode will handle db
        boolean isModeHandleDB = (mode == HHTCommonManager.EnumEyeProtectionMode.EYE_RGB.ordinal()
                || mode == HHTCommonManager.EnumEyeProtectionMode.EYE_PLUS.ordinal());
        L.d(TAG, "setEyeProtectionMode isLastModeHandleDB:%s, isModeHandleDB:%s", isLastModeHandleDB, isModeHandleDB);
        if (isLastModeHandleDB){
            mThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // reset last mode
                    resetLastMode(context, lastMode);
                    // set current mode
                    setCurrentMode(context, mode);
                }
            });
        } else {
            if (isModeHandleDB){
                // reset last mode
                resetLastMode(context, lastMode);
                mThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        // set current mode
                        setCurrentMode(context, mode);
                    }
                });
            } else {
                // reset last mode
                resetLastMode(context, lastMode);
                // set current mode
                setCurrentMode(context, mode);
            }
        }
        return true;
    }
}

