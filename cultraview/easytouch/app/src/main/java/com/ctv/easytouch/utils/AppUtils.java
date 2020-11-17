package com.ctv.easytouch.utils;

import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ctv.easytouch.been.AppInfo;
import com.cultraview.tv.CtvChannelManager;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.utils.CtvCommonUtils;
import com.mstar.android.tv.TvCommonManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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
     */
    public static void gotoHome() {
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
     */
    public static void keyEvent(final int keycode, final int delayTime) {
        new Thread(() -> {
            try {
                if (delayTime > 0) {
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
     */
    public static void keyEventBySystem(final int keycode) {
        new Thread(() -> {
            try {
                Instrumentation inst = new Instrumentation();
                inst.sendKeyDownUpSync(keycode);
            } catch (Exception e) {
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
    public static boolean isActivityRunning(Context mContext, String... activityClassName) {
        boolean flag = false;
        if (activityClassName == null || activityClassName.length == 0) {
            return flag;
        }

        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if (info != null && info.size() > 0) {
            ComponentName component = info.get(0).topActivity;
            String topActivityName = component.getClassName();
            Log.d(TAG, "info.get(0).topActivity ->" + topActivityName);
            for (int i = 0; i < activityClassName.length; i++) {
                String activityClass = activityClassName[i];
                if (TextUtils.equals(activityClass, topActivityName)) { // 包名相等时，则该包在顶层运行
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    /**
     * 判断activity是否运行
     *
     * @param mContext
     * @param activityClassName
     * @return
     */
    public static boolean isActivityRunning(Context mContext, String activityClassName) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if (info != null && info.size() > 0) {
            ComponentName component = info.get(0).topActivity;

            Log.d(TAG, "info.get(0).topActivity ->" + component.getClassName());
            if (activityClassName.equals(component.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断package是否运行
     *
     * @param mContext
     * @param packageName
     * @return
     */
    public static boolean isTopRunning(Context mContext, String packageName) {
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if (info != null && info.size() > 0) {
            String curPackageName = info.get(0).topActivity.getPackageName();

//            LogUtils.d("curPackageName ->%s", curPackageName);
            if (packageName.equals(curPackageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断package是否运行
     *
     * @param mContext
     * @param packageNames 包名集合
     * @return
     */
    public static boolean isTopRunning(Context mContext, String... packageNames) {
        boolean flag = false;
        if (packageNames == null || packageNames.length == 0) {
            return flag;
        }

        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = activityManager.getRunningTasks(1);
        if (info != null && info.size() > 0) {
            String curPackageName = info.get(0).topActivity.getPackageName();

//            LogUtils.d("curPackageName ->%s", curPackageName);
            for (int i = 0; i < packageNames.length; i++) {
                String packageName = packageNames[i];
                if (TextUtils.equals(packageName, curPackageName)) { // 包名相等时，则该包在顶层运行
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    /**
     * 用来判断服务是否运行.
     *
     * @param mContext
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
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
    public static void noticeMediaScan(Context context, String filePath) {
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

//    /**
//     * 切换信号
//     * @param source
//     */
//    public static void changeSource(Context context, int source, int position){
//        if (source == -1 || source == 30){ // 跳转到主页
//            AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);
//        } else { // 其他信号切换
//            // HDMI2 DP 前置HDMI
//            if (source == 24){
//                int type = 2; // HDMI2
//                int prevHdmiIndex = (Integer) SPUtil.getData(context, CommConst.SOURCE_PREV_HDMI_INDEX, 6);
//                int dpIndex = (Integer) SPUtil.getData(context, CommConst.SOURCE_DP_INDEX, 11);
//                if (position == prevHdmiIndex){ // 前置HDMI
//                    type = 0;
//                } else if (position == dpIndex){ // DP
//                    type = 3;// DP
//                }
//                CmdUtils.changeTIPort(context, type); // 切换TI Port
//            }
//
//            // 前置VGA VGA
//            if (source == 0){
//                int prevVgaIndex = (Integer) SPUtil.getData(context, CommConst.SOURCE_PREV_VGA_INDEX, 9);
//                int type = (position == prevVgaIndex) ?  1 : 0;// 前置VGA VGA
//                CmdUtils.switchVGA(context, type); // 切换VGA
//            }
//
//            AppUtils.changeSignal(context, source);
//        }
//
//        // 发送SOURCE广播
//        AppUtils.noticeChangeSignal(context, source);
//        Log.d(TAG, "source->" + source);
//    }
//
//    /**
//     * 切换信号通道
//     *
//     * @param inputSource
//     * @param context
//     */
//    public static void changeSignal(Context context, int inputSource){
//        new Thread(()->{
//            Intent intent = new Intent(MstarConst.ACTION_START_TV_PLAYER);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//            intent.putExtra(MstarConst.KEY_START_TV_PLAYER_TASK_TAG, MstarConst.VALUE_START_TV_PLAYER_TASK_TAG);
//            intent.putExtra(MstarConst.KEY_START_TV_PLAYER_INPUT_SRC, inputSource);
//
//            try {
//                context.startActivity(intent);
//            } catch (Exception e){
//                e.printStackTrace();
//                LogUtils.e("changeSignal error->" + e.getMessage());
//            }
//
//            try {
//                Intent targetIntent;
//
//                targetIntent = new Intent("mstar.tvsetting.ui.intent.action.RootActivity");
//                targetIntent.putExtra("task_tag", "input_source_changed");
//                /* DO NOT remove on_change_source extra!, it will cause mantis:1088498. */
//                targetIntent.putExtra("no_change_source", true);
//                targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                if (targetIntent != null){
//                    context.startActivity(targetIntent);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                LogUtils.e("changeSignal error->" + e.getMessage());
//            }
//        }).start();
//    }

    /**
     * 检测APP是否与硬件匹配
     */
    public static void checkPermission() {
        // 检测是否有meeting文件夹
        File meetingDir = new File("/meeting");
        if (!meetingDir.exists()) {
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
     *
     * @param mContext
     */
    public static boolean isEyeCare(Context mContext) {
        boolean flag = false;
        // 获得护眼模式
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), "isEyecare", 0);

        if (eyeCare == 1) { // 护眼模式打开
            flag = true;
        }
        Log.d(TAG, "护眼模式 flag:" + flag);
        return flag;
    }

//    /**
//     * 护眼处理
//     * @param mContext
//     * @param isChange
//     */
//    public static void eyeCareHandle(Context mContext, boolean isChange){
//        // 获得护眼模式
//        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), "isEyecare", 0);
//
//        CtvPictureManager mTvPictureManager = CtvPictureManager.getInstance();
//        if (mTvPictureManager == null){
//            return;
//        }
//
//        if (eyeCare == 1) { // 护眼模式打开
//            if (isChange){ // 按下在绘制过程中, 降低背光值
//                int curBacklight = mTvPictureManager.getBacklight();
//                if (curBacklight > 50){
//                    Settings.System.putInt(mContext.getContentResolver(), "lastBlackLight", curBacklight);
////                    SPUtil.saveData(mContext, CommConst.LAST_BLACK_LIGHT, curBacklight);
//                    mTvPictureManager.setBacklight(50);
//                    LogUtils.d("护眼模式 降低light setBacklight 50, curBacklight:" + curBacklight);
//                }
//            } else { // 抬起绘制结束时
//                // 恢复背光
//                int lastBlackLight = Settings.System.getInt(mContext.getContentResolver(), "lastBlackLight", 50);
////                int lastBlackLight = (Integer) SPUtil.getData(mContext, CommConst.LAST_BLACK_LIGHT, 50);
//                if (lastBlackLight != 50) {
//                    mTvPictureManager.setBacklight(lastBlackLight);
//                    LogUtils.d("护眼模式 恢复light setBacklight lastBlackLight:" + lastBlackLight);
//                }
//
//            }
//        }
//    }

    /**
     * 改变网络类型：0->ETH eth0, 1->USB Net eth1
     */
    public static void changeETHType(Context mContext) {
        int ethType = Settings.System.getInt(mContext.getContentResolver(), "CTV_ETH_TYPE", 0); // 0:default eth0; 1:USB_Net eth1
        ethType = (ethType + 1) % 2;
        Settings.System.putInt(mContext.getContentResolver(), "CTV_ETH_TYPE", ethType);

        Log.d("AppUtils", "CTV_ETH_TYPE->" + ethType);
    }

    /**
     * 设置进度条
     *
     * @param bar
     * @param progress
     * @param isClick
     */
    public static void setProgress(SeekBar bar, int progress, boolean isClick) {
        bar.setClickable(isClick);
        bar.setEnabled(isClick);
        bar.setSelected(isClick);
        bar.setFocusable(isClick);
        bar.setProgress(progress);
    }

    /**
     * 获得当前的intputSource
     *
     * @param context
     * @return
     */
    public static int getCurrentSource(Context context) {
        // 获得当前source
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        // 获得存储的source
        if (inputSource == CtvCommonManager.INPUT_SOURCE_STORAGE) {
            inputSource = CtvCommonUtils.getCurrentSourceFromDb(context);
        }

        return inputSource;
    }

    /**
     * 发送SOURCE广播通知
     *
     * @param context
     * @param source
     */
    public static void noticeChangeSignal(Context context, int source) {
        // 发送SOURCE广播
        Intent intent = new Intent("android.intent.action.USB_UART_TOUCH");
        intent.putExtra("type", "Android");
        if (source >= 0 && source != 34) { // 其他通道
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
    public static void gotoOtherApp(Context context, String mPackageName, String mActivityName) {
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
     * @param mPackageName
     * @param mActivityName
     */
    public static void gotoOtherApp(Context context, Bundle data, String mPackageName, String mActivityName) {
        Log.i(TAG, "gotoOtherApp, mPackageName->" + mPackageName + " mActivityName" + mActivityName);
        Intent intent = new Intent();
        intent.putExtras(data);
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
    public static void gotoOtherApp(Context context, String action) {
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
     * 将隐式启动转换为显式启动,兼容编译sdk5.0以后版本
     *
     * @param context
     * @param implicitIntent
     * @return
     */
    public Intent getExplicitIntent(Context context, Intent implicitIntent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(implicitIntent, 0);
        if (resolveInfos == null || resolveInfos.size() != 1) {
            return null;
        }
        Intent explicitIntent = null;
        ResolveInfo info = resolveInfos.get(0);
        String packageName = info.serviceInfo.packageName;
        String className = info.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    /**
     * 改变锁状态值
     *
     * @param context
     * @param isPlus
     */
    public static void changeLockCount(Context context, boolean isPlus) {
        int lockCount = Settings.System.getInt(context.getContentResolver(), "IS_LOCK", -1);
        if (isPlus) {
            lockCount++;
        } else {
            lockCount--;
        }

        Log.i(TAG, "changeLockCount, isPlus->" + isPlus + " lockCount->" + lockCount);
        Settings.System.putInt(context.getContentResolver(), "IS_LOCK", lockCount);
    }

    public static boolean isLocked(Context context) {
        boolean flag = false;
        int lockCount = Settings.System.getInt(context.getContentResolver(), "IS_LOCK", -1);

        if (lockCount > -1) {
            flag = true;
        }
        Log.i(TAG, "isLocked, isLocked->" + flag);
        return flag;
    }

    public static final String[] ignoreApps = new String[]{
            "com.android.cultraview.launcher.whiteboard",
            "com.android.cultraview.floatbuttonview",
            "com.protruly.floatwindowlib",
            "com.ctv.imageselect",
            "com.cultraview.annotate",
            "com.ctv.whiteboard",
            "com.jrm.localmm",
            "com.android.toofifi",
            "com.android.camera2"
    };

    public static List<AppInfo> scanInstallApp(Context context) {
        List<String> ignoreList = java.util.Arrays.asList(ignoreApps);

        List<AppInfo> appInfos = new ArrayList<>();
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        List<ApplicationInfo> listAppcations = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            if (app.packageName == null) {
                Log.d(TAG, "appPackName:" + "null");
            } else {
                Log.d(TAG, "appPackName:" + app.packageName);
            }

            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent != null) {
                if (ignoreList.contains(app.packageName)) {
                    continue;
                }
                appInfos.add(getAppInfo(app, pm));
            }
        }
        return appInfos;
    }

    private static AppInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(pm.getApplicationLabel(app).toString());//应用名称
        appInfo.setAppIcon(app.loadIcon(pm));//应用icon
        appInfo.setPackName(app.packageName);//应用包名，用来卸载
        return appInfo;
    }

    /**
     * 启动apk
     *
     * @param context
     * @param packageName
     */
    public static void startApp(Context context, String packageName) {
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        final Intent intent = pm.getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    public static void getUninstallApkInfo(Context context, String apkPath) {
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        PackageInfo packageInfo = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
        if (packageInfo != null) {
            ApplicationInfo info1 = packageInfo.applicationInfo;
            info1.publicSourceDir = apkPath;

        }
    }

    /**
     * 改变USB触控
     *
     * @param isOpen
     */
    public static void changeUSBTouch(Context context, boolean isOpen) {
        try {
//            Class<?> bookClass = Class.forName("android.os.SystemProperties");//完整类名
//            Object book = bookClass.newInstance();//获得实例
//            Method getAuthor = bookClass.getDeclaredMethod("set", String.class, String.class);//获得私有方法
//            getAuthor.setAccessible(true);//调用方法前，设置访问标志

//
//            Log.d("qkmin", "setUSB isOpen" );
//            if (isOpen) {
//                getAuthor.invoke(book, "ctv.sendKeyCode", "on");//使用方法
//                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_ON");
//                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_ON");
//                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_ON");
//            } else {
//                getAuthor.invoke(book, "ctv.sendKeyCode", "off");//使用方法
//                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_OFF");
//                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_OFF");
//                TvCommonManager.getInstance().setTvosCommonCommand("SetUSBTOUCH_OFF");
//            }
            TvCommonManager.getInstance().setUsbTouch(context, isOpen);
        } catch (Exception e) {
            Log.e("qkmin", "setUSB error:" + e);
            e.printStackTrace();
        }
    }

//    /**
//     * 搜台过程中
//     * @return
//     */
//    public static boolean isChannetuning() {
//        int status = CtvChannelManager.getInstance().getTuningStatus();
//        if(status == CtvChannelManager.TUNING_STATUS_ATV_AUTO_TUNING
//                ||status == CtvChannelManager.TUNING_STATUS_DTV_AUTO_TUNING
//                ||status ==CtvChannelManager.TUNING_STATUS_DTV_FULL_TUNING){
//            return true;
//        }else{
//            return false;
//        }
//    }

    public static boolean isChannetuning(Context mContext) {
        // TODO: 2019-10-28 8386
//        int status = CtvChannelManager.getInstance().getTuningStatus();
//        boolean ret= false;
//        boolean dialogShow = (1 == Settings.System.getInt(mContext.getContentResolver(),"auto.dialog.show",0));
//        if(status == CtvChannelManager.TUNING_STATUS_ATV_AUTO_TUNING
//                ||status == CtvChannelManager.TUNING_STATUS_DTV_AUTO_TUNING
//                ||status ==CtvChannelManager.TUNING_STATUS_DTV_FULL_TUNING){
//            ret = true;
//        }else{
//            ret = false;
//        }
//        return ret || dialogShow;
        return false;
    }

}
