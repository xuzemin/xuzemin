package com.yinghe.whiteboardlib.utils;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvPictureManager;
import com.cultraview.tv.utils.CtvCommonUtils;
import com.mstar.android.tv.TvCommonManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 调用系统接口
 *
 * @author wang
 * @time on 2017/3/14.
 */
public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();

    /*
	 * 调用系统设置app函数
	 */
    public static void callSysSettingApp(Context mContext) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName cn = new ComponentName("com.android.settings",
                "com.android.settings.Settings");
        intent.setComponent(cn);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
//            ToastUtils.showLongMessage(mContext, R.string.str_no_apk);
        }
    }

    /**
     * 回到桌面
     *
     */
    public static void gotoHome(){
        new Thread(() -> {
            try {
                Runtime.getRuntime().exec("input keyevent 3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 回到桌面
     *
     */
    public static void keyEvent(final int keycode, final int delayTime){
        new Thread(() -> {
            try {
                if (delayTime > 0){
                    Thread.sleep(delayTime);
                }

                Runtime.getRuntime().exec("input keyevent " + keycode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 键盘按键事件调用
     *
     */
    public static void keyEventBySystem(final int keycode){
        new Thread(() -> {
            try {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(keycode);
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 判断activity是否运行
     *
     * @param mContext
     * @param activityClassName
     * @return
     */
    public static boolean isActivityRunning(Context mContext, String activityClassName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if(info != null && info.size() > 0){
            ComponentName component = info.get(0).topActivity;

            LogUtils.d("info.get(0).topActivity ->%s", component.getClassName());
            if(activityClassName.equals(component.getClassName())){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断activity是否运行
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static boolean isTopRunning(Context mContext, String packageName){
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if(info != null && info.size() > 0){
            String curPackageName = info.get(0).topActivity.getPackageName();

            LogUtils.d("curPackageName ->%s", curPackageName);
            if(packageName.equals(curPackageName)){
                return true;
            }
        }
        return false;
    }

    /**
     * 用来判断服务是否运行.
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        assert activityManager != null;
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    /**
     * 保存完图片之后，可以在图库中查看
     *
     * @param context
     * @param filePath
     */
    public static void noticeMediaScan(Context context, String filePath){
        //发送Sd卡的就绪广播,要不然在手机图库中不存在
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(new File(filePath));
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + filePath)));
        }
    }
    /**
     * 设置进度条
     * @param bar
     * @param progress
     * @param isClick
     */
    public static void setProgress(SeekBar bar, int progress, boolean isClick){
        bar.setClickable(isClick);
        bar.setEnabled(isClick);
        bar.setSelected(isClick);
        bar.setFocusable(isClick);
        bar.setProgress(progress);
    }

    /**
     * 切换信号通道
     *
     * @param sourceIndex
     * @param context
     */

    /**
     * 切换信号通道
     *
     * @param inputSource
     * @param context
     */
    public static void changeSignal(Context context, int inputSource){
        new Thread(()->{
            Intent intent = new Intent(MstarConst.ACTION_START_TV_PLAYER);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            intent.putExtra(MstarConst.KEY_START_TV_PLAYER_TASK_TAG, MstarConst.VALUE_START_TV_PLAYER_TASK_TAG);
            intent.putExtra(MstarConst.KEY_START_TV_PLAYER_INPUT_SRC, inputSource);
            Log.i("CommonCommandsourceInde","changeSignal->" +inputSource);
            try {
                context.startActivity(intent);
            } catch (Exception e){
                e.printStackTrace();
                LogUtils.e("CommonCommand","changeSignal error->" + e.getMessage());
            }
        }).start();
    }

    /**
     * 检测APP是否与硬件匹配
     */
    public static void checkPermission(){
        // 检测是否有meeting文件夹
        File meetingDir = new File("/meeting");
        if (!meetingDir.exists()){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    /**
     *  * 返回当前屏幕是否为竖屏。
     *  * @param context
     *  * @return 当且仅当当前屏幕为竖屏时返回true,否则返回false。
     *  
     */
    public static boolean isScreenOrientationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * 判断当前界面是否是桌面
     */
    public static boolean isHome(Context context) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert mActivityManager != null;
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes(context).contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    public static List<String> getHomes(Context context) {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断是否为护眼模式
     * @param mContext
     */
    public static boolean isEyeCare(Context mContext){
        boolean flag = false;
        // 获得护眼模式
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), "isEyecare", 0);

        if (eyeCare == 1) { // 护眼模式打开
            flag = true;
        }
//        LogUtils.d("护眼模式 flag:" + flag);
        return flag;
    }

    /**
     * 护眼处理
     * @param mContext
     * @param isChange
     */
    public static void eyeCareHandle(Context mContext, boolean isChange){
        // 获得护眼模式
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), "isEyecare", 0);

        CtvPictureManager mTvPictureManager = CtvPictureManager.getInstance();
        if (mTvPictureManager == null){
            return;
        }

        if (eyeCare == 1) { // 护眼模式打开
            if (isChange){ // 按下在绘制过程中, 降低背光值
                int curBacklight = mTvPictureManager.getBacklight();
                if (curBacklight > 50){
                    SPUtil.saveData(mContext, "lastBlackLight", curBacklight);
                    mTvPictureManager.setBacklight(50);
//                    LogUtils.d("护眼模式 降低light setBacklight 50, curBacklight:" + curBacklight);
                }
            } else { // 抬起绘制结束时
                // 恢复背光
                int lastBlackLight = (Integer) SPUtil.getData(mContext, "lastBlackLight", 50);
                mTvPictureManager.setBacklight(lastBlackLight);

//                LogUtils.d("护眼模式 恢复light setBacklight lastBlackLight:" + lastBlackLight);
            }
        }
    }

	    /**
     * 改变网络类型：0->ETH eth0, 1->USB Net eth1
     */
    public static void changeETHType(Context mContext){
        int ethType = Settings.System.getInt(mContext.getContentResolver(), "CTV_ETH_TYPE", 0); // 0:default eth0; 1:USB_Net eth1
        ethType = (ethType + 1) % 2;
        Settings.System.putInt(mContext.getContentResolver(), "CTV_ETH_TYPE", ethType);

        Log.d("AppUtils", "CTV_ETH_TYPE->" + ethType);
    }


    /**
     * 中间件命令
     */
    public static void sendCommand(String command){
        Log.v("CommonCommandsourceInde","sendCommand:"+command);
        TvCommonManager.getInstance().setTvosCommonCommand(command);
    }

    public static int[] getCommand(String command){

        Log.v("CommonCommandsourceInde","getCommand:"+command);
        int[] get = TvCommonManager.getInstance().setTvosCommonCommand(command);
        Log.v("CommonCommandsourceInde","getCommand get:"+ Arrays.toString(get));
        return get;
    }


    /**
     * 发送SOURCE广播通知
     *
     * @param context
     * @param source
     */
    public static void noticeChangeSignal(Context context, int source){
        // 发送SOURCE广播
        Intent intent = new Intent("android.intent.action.USB_UART_TOUCH");
        intent.putExtra("type", "Android");
        if (source >= 0 && source != 34){ // 其他通道
            intent.putExtra("type", "Other");
            intent.putExtra("Source", source + "");
        }
        context.sendBroadcast(intent);
    }


    /**
     * 发送SOURCE广播通知
     *
     * @param context
     * @param mPackageName
     * @param mActivityName
     */
    public static void gotoOtherApp(Context context, String mPackageName, String mActivityName){
        Log.i(TAG, "gotoOtherApp, mPackageName->" + mPackageName + " mActivityName" + mActivityName);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            intent.setComponent(new ComponentName(mPackageName,
                    mActivityName));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "There is not the APP of " + mPackageName,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 发送SOURCE广播通知
     *
     * @param context
     * @param action
     */
    public static void gotoOtherApp(Context context, String action){
        Log.i(TAG, "gotoOtherApp, action->" + action);
        Intent intent = new Intent(action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "There is not the APP of " + action,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 开启或者关闭EasyTouch
     * @param context
     * @param isOpen
     */
    public static void openOrCloseEasyTouch(final Context context, final boolean isOpen){
        if (isOpen){ // 打开EasyTouch
           // if (!AppUtils.isServiceRunning(context, MstarConst.EASY_TOUCH_SERVICE)){
                LogUtils.i("---start EASY_TOUCH_START");
                Intent fsIntent = new Intent(MstarConst.EASY_TOUCH_START);
                fsIntent.setComponent(new ComponentName(MstarConst.EASY_TOUCH_PACKAGE, MstarConst.EASY_TOUCH_SERVICE));
                fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(fsIntent);
          //  }
        } else { // 关闭EasyTouch
            if (AppUtils.isServiceRunning(context, MstarConst.EASY_TOUCH_SERVICE)){
                LogUtils.i("---stop EASY_TOUCH_STOP");
                Intent fsIntent = new Intent(MstarConst.EASY_TOUCH_STOP);
                fsIntent.setComponent(new ComponentName(MstarConst.EASY_TOUCH_PACKAGE, MstarConst.EASY_TOUCH_SERVICE));
                fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(fsIntent);
            }
        }
    }

    /**
     * 获得背光
     * @return
     */
    public static int getBacklight(){
        return CtvPictureManager.getInstance().getBacklight();
    }
    /**
     * 设置背光
     * @return
     */
    public static void setBacklight(int value){
        CtvPictureManager.getInstance().setBacklight(value);
    }

    /**
     * 打开和关闭背光处理
     * @param context
     * @param isOpen
     */
    public static void openOrCloseLightSense(Context context, boolean isOpen){
        CtvPictureManager mTvPictureManager = CtvPictureManager.getInstance();
        if (mTvPictureManager == null){
            return;
        }

        if (isOpen){ // 打开自动光感时，保存当前背光值
            int curBacklight = mTvPictureManager.getBacklight();
            curBacklight = curBacklight == 50 ? 51 : curBacklight;
            Settings.System.putInt(context.getContentResolver(), "OLD_LIGHTSENSE", curBacklight);
        } else { // 关闭自动光感时，恢复背光值
            // 恢复背光
            int lastBlackLight = Settings.System.getInt(context.getContentResolver(), "OLD_LIGHTSENSE", 50);
            lastBlackLight = lastBlackLight == 50 ? 51 : lastBlackLight;
            mTvPictureManager.setBacklight(lastBlackLight);
            LogUtils.d("恢复light setBacklight OLD_LIGHTSENSE:" + lastBlackLight);
        }
    }

    public static int getCurrentSource(Context context){
        // 获得当前source
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        // 获得存储的source
        if (inputSource == CtvCommonManager.INPUT_SOURCE_STORAGE){
            inputSource = CtvCommonUtils.getCurrentSourceFromDb(context);
        }

        return inputSource;
    }

    /**
     * 获得当前sourceIndex
     * @param context
     * @return
     */
    public static int getCurrentSourceIndex(Context context){
        int currentSource = AppUtils.getCurrentSource(context);
        int sourceIndex = currentSource;
        switch (currentSource) {
            case 0:{ // VGA
                sourceIndex = 28;
                int vgaInfo = Settings.System.getInt(context.getContentResolver(), CommConst.VGA_INFO, 0);
                if (vgaInfo == 1){
                    sourceIndex = 31;
                }
                break;
            }
            case 23:{
                sourceIndex = 30;
                break;
            }
            case 24:{
                int hdmiInfo = Settings.System.getInt(context.getContentResolver(), CommConst.SOURCE_INFO, 0);
                switch (hdmiInfo){
                    case 0: // DP
                        sourceIndex = 29;
                        break;
                    case 2: // HDMI1
                        sourceIndex = 23;
                        break;
                    case 3: // HDMI2
                        sourceIndex = 24;
                        break;
                }
                break;
            }
            case 28:{
                sourceIndex = 0;
                break;
            }
        }

        return sourceIndex;
    }

    /**
     * 显示截图
     */
    public static void showScreenshot(Context context) {
        // 截图或者批注显示时
        Settings.System.putInt(context.getContentResolver(),
                "annotate.start", 2);
        CmdUtils.changeUSBTouch(context, false);

        Log.i(TAG, "screenshot start");
        String mPackageName = "com.example.cutcapture";
        String mActivityName = "com.example.cutcapture.MainActivity";
        AppUtils.gotoOtherApp(context, mPackageName, mActivityName);
        AppUtils.openOrCloseEasyTouch(context, false);
    }

    /**
     * 启动批注
     * @param context
     */
    public static void showComment(final Context context) {
        // 启动批注
        switch (MstarConst.MARK_TYPE){
            case MstarConst.MARK_TYPE_CTV:{ // CTV批注
                Log.i(TAG, "Mark ScreenComment start CTV批注");
                // 发送广播，启动批注
                // 启动批注
                context.sendBroadcast(new Intent(MstarConst.ACTION_CTV_MARK_OPEN));
                break;
            }
            case MstarConst.MARK_TYPE_BOZEE:{ // 宝泽
                Log.i(TAG, "Mark ScreenComment start 宝泽批注");
                try {
                    Intent markIntent = new Intent(MstarConst.ACTION_START_MARK);
                    markIntent.setClassName("com.bozee.meetingmark", "com.bozee.meetingmark.MeetingMarkService");
                    context.startService(markIntent);
                } catch (Exception e){
                    e.printStackTrace();
                    return;
                }
                break;
            }
            case MstarConst.MARK_TYPE_ANNOTATION:
                Log.i(TAG, "Mark ScreenComment start 单独批注");
                try {
                    Intent AnnotationIntent = new Intent("android.intent.action.open.annotation");
                    AnnotationIntent.setComponent(new ComponentName("com.ctv.annotation", "com.ctv.annotation.AnnotationService"));
//                    AnnotationIntent.setClassName("com.ctv.annotation", "com.ctv.annotation.AnnotationService");
                    AnnotationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(AnnotationIntent);
                } catch (Exception e){
                    e.printStackTrace();
                    return;
                }
                break;
        }

        // 截图或者批注显示时
        Settings.System.putInt(context.getContentResolver(),
                "annotate.start", 1);

        // 关闭USB触控
        CmdUtils.changeUSBTouch(context, false);

        AppUtils.openOrCloseEasyTouch(context, false);
    }

    /**
     * 关闭批注
     * @param context
     */
    public static void closeComment(final Context context) {
        Settings.System.putInt(context.getContentResolver(), "annotate.start", 0);
        // 启动批注
        switch (MstarConst.MARK_TYPE){
            case MstarConst.MARK_TYPE_CTV:{ // CTV批注
                LogUtils.d("CTV批注 START_COMMENT_ACTION  关闭批注.....");

                context.sendBroadcast(new Intent("com.cultraview.annotate.broadcast.CLOSE"));
                break;
            }
            case MstarConst.MARK_TYPE_BOZEE:{ // 宝泽
                LogUtils.d("宝泽 ACTION_CLOSE_MARK  关闭批注.....");
                try {
                    Intent markIntent = new Intent(MstarConst.ACTION_CLOSE_MARK);
                    markIntent.setClassName("com.bozee.meetingmark", "com.bozee.meetingmark.MeetingMarkService");
                    context.startService(markIntent);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
