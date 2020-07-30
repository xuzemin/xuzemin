package com.ctv.settings.device;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import com.ctv.settings.utils.L;
import com.cultraview.tv.CtvPictureManager;


/**
 * 亮度、声音调节实现
 */
public class DeviceManager {
    /**
     * 获取当前屏幕亮度
     *
     * @param context
     * @return
     */
    public static int getScreenBrightness(Context context) {

        // TODO: 2019-10-23 8386  
        CtvPictureManager mTvPictureManager = CtvPictureManager.getInstance();
        return mTvPictureManager.getBacklight();
    }

    /**
     * 设置屏幕亮度 start========================================================
     *
     * @param brightness
     */
    public static void setBrightness(Context mContext, int brightness) {


        // TODO: 2019-10-23 8385 
        
        CtvPictureManager mTvPictureManager = CtvPictureManager.getInstance();
        try {
            Settings.System.putInt(mContext.getContentResolver(), "backlight", brightness);
            //SystemProperties.set("persist.sys.backlight",""+progress);
            mTvPictureManager.setBacklight(brightness);
        } catch (Exception e) {
            L.e("qkmin------设置亮度出现问题");
            Toast.makeText(mContext,"qkmin------设置亮度出现问题"+e,Toast.LENGTH_SHORT).show();
        }
        updatePicModeSetting(mContext, brightness);

    }

    public static void updatePicModeSetting(Context mContext, int value) {
        int inputSrcType = queryCurInputSrc(mContext);
        int pictureModeType = queryePicMode(mContext, inputSrcType);
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u8Backlight", value);
        try {
            ret = mContext.getContentResolver().update(
                    Uri.parse("content://mstar.tv.usersetting/picmode_setting/inputsrc/"
                            + inputSrcType + "/picmode/" + pictureModeType), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            System.out.println("update tbl_PicMode_Setting ignored");
        }
    }

    public static int queryCurInputSrc(Context mContext) {
        int value = 0;
        Cursor cursor = mContext.getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/systemsetting"), null, null, null, null);
        if (cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        cursor.close();
        return value;
    }

    public static int queryePicMode(Context mContext, int inputSrcType) {
        Cursor cursorVideo = mContext.getContentResolver().query(
                Uri.parse("content://mstar.tv.usersetting/videosetting/inputsrc/" + inputSrcType),
                null, null, null, null);
        int value = -1;
        if (cursorVideo.moveToNext()) {
            value = cursorVideo.getInt(cursorVideo.getColumnIndex("ePicture"));
        }
        cursorVideo.close();
        return value;
    }
    //设置屏幕亮度 end=============================================================

    /**
     * 获取当前音量
     *
     * @param mContext
     */
    public static int getVolume(Context mContext) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 获取最大音量
     *
     * @param mContext
     */
    public static int getMaxVolume(Context mContext) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        return audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 设置声音
     *
     * @param mContext
     */
    public static void setVolume(Context mContext, int progress) {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }


}
