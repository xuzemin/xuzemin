package com.youkes.browser.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import static android.content.Context.AUDIO_SERVICE;
import static android.media.AudioManager.STREAM_MUSIC;

public class Constant {
    public static final Boolean isDebug = true;
    public static final Boolean isCN = false;
    public static final String TAG = "Lightning";
    public static int CurrentNumber = 0;
//    public static boolean isInit = false;
    public static final int VIDEO = 0;
    public static final int NEW = 1;
    public static final int MESSAGE = 2;
    public static final int CONSTACT = 3;
    public static final int CONTENT = 4;
//    public static final int SETTINGS = 5;
    public static final int BACKGROUND = 5;
    public static final int ANIMATIONTIME = 1000;
    public static final String fileName = "filename";
    public static final String ApkDir = "/sdcard/Apk/";
    public static final String ApkName = "MyWebLauncher.apk";
    public static final String Package = "com.youkes.browser";
    public static final String Default = "default";
    public static final String AdminPassword = "active99";
    public static final String AdminPassword_bak = "1";
    public static void debugLog(String str){
        if(isDebug){
            Log.e(TAG,"Launcher"+str);
        }
    }

    @SuppressLint("NewApi")
    public static boolean isPlay(Activity context){
        AudioManager audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        LogUtil.e("isPlay"+ audioManager.isMusicActive());
        LogUtil.e("isPlay"+ audioManager.getMode());
        return audioManager.isMusicActive();
    }

    public static synchronized String getAppName(Context context,String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    packageName, 0);
            String labelRes = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            return labelRes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final int OUTTIME = 10;//30;
    public static final int EVENT_START_VIDEO = 1;
    public static final int EVENT_GETEVENT = 2;
    public static final int EVENT_TO_MAIN = 3;
    public static final int EVENT_TO_THREAD = 4;
    public static final int SHOWTIME = 3 * 1000;
    public static boolean isResetPlay = false;
    public static boolean isVideoPlay = true;
    public static boolean isImagePlay = false;
    public static int Current_Video = 0;
    public static int Current_Image = 0;
    public static boolean isApplicationPause = false;
    public static final String VideoDir = "/sdcard/Video/";
    public static final String ImgDir = "/sdcard/Image/";
    public static final String EventPath = "/dev/input/event0";
    public static final String EventPath1 = "/dev/input/event3";
    public static final String EventPath2 = "/dev/input/event4";
}
