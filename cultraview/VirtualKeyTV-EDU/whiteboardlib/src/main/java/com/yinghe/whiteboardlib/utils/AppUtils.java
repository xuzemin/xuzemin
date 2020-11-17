package com.yinghe.whiteboardlib.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Instrumentation;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.SeekBar;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvChannelManager;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvPictureManager;
import com.cultraview.tv.utils.CtvCommonUtils;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.PanelProperty;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用系统接口
 *
 * @author wang
 * @time on 2017/3/14.
 */
public class AppUtils {
    private static final String TAG = AppUtils.class.getSimpleName();
    public static final String client = SystemProperties.get("client.config", "EDU");
    public static final String clientBoard = SystemProperties.get("client.board", "CV648H_I");

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
            LogUtils.d("info.get(0).topActivity ->%s", topActivityName);
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

            LogUtils.d("info.get(0).topActivity ->%s", component.getClassName());
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

    /**
     * 切换信号
     *
     * @param sourceIndex
     */
    public static void changeSource(Context context, int sourceIndex) {
        if (sourceIndex == -1 || sourceIndex == 34) { // 跳转到主页
            TvCommonManager.getInstance().setInputSource(34);
            AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);
        } else { // 其他信号切换
            AppUtils.changeSignal(context, sourceIndex);
        }
        Log.d(TAG, "sourceIndex->" + sourceIndex);
    }

    /**
     * 切换信号通道
     *
     * @param sourceIndex
     * @param context
     */
    public static void changeSignal(Context context, int sourceIndex) {
        // 发送SOURCE广播
        if (sourceIndex == 25) { // OPS时
            Intent intent = new Intent("android.intent.action.OPS_BOOT");
            context.sendBroadcast(intent);
        }

        new Thread(() -> {
            // 交换VGA和DTV
            int inputSource;
            switch (sourceIndex) {
                case 0: { // DTV
                    inputSource = TvCommonManager.INPUT_SOURCE_DTV;
                    break;
                }
                case 28: { // VGA
                    inputSource = TvCommonManager.INPUT_SOURCE_VGA;
                    break;
                }
                default:
                    inputSource = sourceIndex;
                    break;
            }

            SystemClock.sleep(1000);
            Intent intent = new Intent();
            intent.setAction("com.cultraview.ctvmenu.ui.intent.action.ProgressActivity");
            intent.putExtra("task_tag", "input_source_changed");
            intent.putExtra("inputSource", inputSource);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            try {
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

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
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), CommConst.IS_EYE_CARE, 0);

        if (eyeCare == 1) { // 护眼模式打开
            flag = true;
        }
        LogUtils.d("护眼模式 flag:" + flag);
        return flag;
    }

    /**
     * 护眼处理
     *
     * @param mContext
     * @param isChange
     */
    public static void eyeCareHandle(Context mContext, boolean isChange) {
        // 获得护眼模式
        int eyeCare = Settings.System.getInt(mContext.getContentResolver(), "isEyecare", 0);

        CtvPictureManager mTvPictureManager = CtvPictureManager.getInstance();
        if (mTvPictureManager == null) {
            return;
        }

        if (eyeCare == 1) { // 护眼模式打开
            if (isChange) { // 按下在绘制过程中, 降低背光值
                int curBacklight = mTvPictureManager.getBacklight();
                if (curBacklight > 50) {
                    Settings.System.putInt(mContext.getContentResolver(), "lastBlackLight", curBacklight);
//                    SPUtil.saveData(mContext, CommConst.LAST_BLACK_LIGHT, curBacklight);
                    mTvPictureManager.setBacklight(50);
                    LogUtils.d("护眼模式 降低light setBacklight 50, curBacklight:" + curBacklight);
                }
            } else { // 抬起绘制结束时
                // 恢复背光
                int lastBlackLight = Settings.System.getInt(mContext.getContentResolver(), "lastBlackLight", 50);
//                int lastBlackLight = (Integer) SPUtil.getData(mContext, CommConst.LAST_BLACK_LIGHT, 50);
                if (lastBlackLight != 50) {
                    mTvPictureManager.setBacklight(lastBlackLight);
                    LogUtils.d("护眼模式 恢复light setBacklight lastBlackLight:" + lastBlackLight);
                }

            }
        }
    }

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
     * 获得当前sourceIndex
     *
     * @param context
     * @return
     */
    public static int getCurrentSourceIndex(Context context) {
        int currentSource = AppUtils.getCurrentSource(context);
        int sourceIndex = currentSource;
        switch (currentSource) {
            case 0: { // VGA
                sourceIndex = 28;
                int vgaInfo = Settings.System.getInt(context.getContentResolver(), CommConst.VGA_INFO, 0);
                if (vgaInfo == 1) {
                    sourceIndex = 31;
                }
                break;
            }
            case 24: {
                int hdmiInfo = Settings.System.getInt(context.getContentResolver(), CommConst.SOURCE_INFO, 0);
                switch (hdmiInfo) {
                    case 0: // PreHDMI
                        sourceIndex = 30;
                        break;
                    case 3: // DP
                        sourceIndex = 29;
                        break;
                }
                break;
            }
            case 28: {
                sourceIndex = 0;
                break;
            }
        }

        return sourceIndex;
    }

    /**
     * 获得当前的intputSource
     *
     * @return
     */
    public static int getCurrentSource() {
        // 获得当前source
        return TvCommonManager.getInstance().getCurrentTvInputSource();
    }

    /**
     * 判断是否为INPUT_SOURCE_STORAGE
     *
     * @return
     */
    public static boolean isSourceStorage() {
        boolean flag = false;
        // 获得当前source
        int inputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        // 获得存储的source
        if (inputSource == CtvCommonManager.INPUT_SOURCE_STORAGE) {
            flag = true;
        }

        return flag;
    }

    /**
     * 获得背光
     *
     * @return
     */
    public static int getBacklight() {
        int light = 0;
        try {
            String backlight = SystemProperties.get("persist.sys.backlight", "" + 0);
            light = Integer.parseInt(backlight);
        } catch (Exception e) {
            Log.e(TAG, "getBacklight e" + e);
        }
        return light;

    }

    /**
     * 获得背光
     *
     * @return
     */
    public static void setBacklight(int value) {
        SystemProperties.set("persist.sys.backlight", "" + value);
        CtvPictureManager.getInstance().setBacklight(value);
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

    /**
     * 搜台过程中
     *
     * @return
     */
    public static boolean isChannetuning() {
        int status = CtvChannelManager.getInstance().getTuningStatus();
        if (status == CtvChannelManager.TUNING_STATUS_ATV_AUTO_TUNING
                || status == CtvChannelManager.TUNING_STATUS_DTV_AUTO_TUNING
                || status == CtvChannelManager.TUNING_STATUS_DTV_FULL_TUNING) {
            return true;
        } else {
            return false;
        }
    }

    // Dialog APP
    public static final String[] dialogApps = new String[]{
            "com.protruly.floatwindowlib",
            "com.ctv.imageselect",
            "com.ctv.screencomment",
            "com.example.cutcapture",
            "com.cultraview.ctvmenu",
            "mstar.factorymenu.ui",
    };
    public static final List<String> DIALOG_APP_LIST = java.util.Arrays.asList(dialogApps);

    /**
     * 判断TvMenu是否显示
     *
     * @param mContext
     * @return
     */
    public static boolean isTvMenuShow(Context mContext) {
        boolean flag = false;

        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> infos = activityManager.getRunningTasks(10);
        if (infos != null && infos.size() > 0) {
            String packageNameTop = infos.get(0).topActivity.getPackageName();
//            Log.d(TAG, "isEasyTouchHide packageNameTop->" + packageNameTop);
            if (MstarConst.TV_PLAY_PACKAGE.equals(packageNameTop)) { // 若Top 为TV_PLAY_PACKAGE时,则为false
                return true;
            }

            if (MstarConst.LAUNCH_PACKAGE.equals(packageNameTop)) { // 若Top 为LAUNCH_PACKAGE时,则为true
                return false;
            }

            // 若Top 为DIALOG_APP，Last APP为TV_PLAY_PACKAGE时, 则为false
            int size = infos.size();
            if (size >= 2) { // 若Top 为DIALOG_APP时,Last为为TV_PLAY_PACKAGE时，则为false
                if (DIALOG_APP_LIST.contains(packageNameTop)) { // 判断是否为dialog activity
                    String packageLast = infos.get(1).baseActivity.getPackageName();
//                    Log.d(TAG, "isEasyTouchHide packageLast->" + packageLast);
                    if (MstarConst.TV_PLAY_PACKAGE.equals(packageLast)) { // Last为为TV_PLAY_PACKAGE时
                        flag = true;
                    }
                }
            }
        }

        return flag;
    }

    /**
     * 开启或者关闭EasyTouch
     *
     * @param context
     * @param isOpen
     */
    public static void openOrCloseEasyTouch(final Context context, final boolean isOpen) {
        String action = "";
        if (isOpen) { // 打开EasyTouch
            if (!AppUtils.isServiceRunning(context, MstarConst.EASY_TOUCH_SERVICE)) {
                LogUtils.i("---start EASY_TOUCH_START");
                action = MstarConst.EASY_TOUCH_START;
            }
        } else { // 关闭EasyTouch
            if (AppUtils.isServiceRunning(context, MstarConst.EASY_TOUCH_SERVICE)) {
                LogUtils.i("---stop EASY_TOUCH_STOP");
                action = MstarConst.EASY_TOUCH_STOP;
            }
        }

        if (!TextUtils.isEmpty(action)) {
            Intent fsIntent = new Intent(action);
            fsIntent.setComponent(new ComponentName(MstarConst.EASY_TOUCH_PACKAGE,
                    MstarConst.EASY_TOUCH_SERVICE));
            fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(fsIntent);
        }
    }

    public static void setInputSourceAndroid() {
        TvCommonManager.getInstance().setInputSource(34);
    }

    /**
     * 显示截图
     */
    public static void showScreenshot(Context context) {
        // 截图或者批注显示时
        Settings.System.putInt(context.getContentResolver(),
                CommConst.STATUS_START, CommConst.STATUS_SCREENSHOT);
        CmdUtils.changeUSBTouch(context, false);

        Log.i(TAG, "screenshot start");
        String mPackageName = "com.example.cutcapture";
        String mActivityName = "com.example.cutcapture.MainActivity";
        AppUtils.gotoOtherApp(context, mPackageName, mActivityName);
        AppUtils.openOrCloseEasyTouch(context, false);
    }

    /**
     * 显示批注
     */
    public static void showComment(Context context) {
        Log.i(TAG, "ScreenComment start");
        // 截图或者批注显示时
        Settings.System.putInt(context.getContentResolver(),
                CommConst.STATUS_START, CommConst.STATUS_COMMENT);

        // 关闭USB触控
        CmdUtils.changeUSBTouch(context, false);

        // 发送广播，启动批注
        // 启动批注
        // final String CTV_ACTION_PEN_OPEN_OR_HIDE_WRITING = "com.cultraview.annotate.broadcast.OPEN";
        //context.sendBroadcast(new Intent(CTV_ACTION_PEN_OPEN_OR_HIDE_WRITING));
        Intent fsIntent = new Intent("android.intent.action.open.annotation");
        fsIntent.setComponent(new ComponentName("com.ctv.annotation", "com.ctv.annotation.AnnotationService"));
        fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(fsIntent);
        AppUtils.openOrCloseEasyTouch(context, false);
    }

    // 触控UART开启和关闭
    public static final String TVOS_COMMON_CMD_SETONOFF_UARTTOUCH_ON = "SetUARTTOUCH_ON";
    public static final String TVOS_COMMON_CMD_SETONOFF_UARTTOUCH_OFF = "SetUARTTOUCH_OFF";

    public static void setUARTTouch(boolean open) {
        try {
            String cmd = open ? TVOS_COMMON_CMD_SETONOFF_UARTTOUCH_ON : TVOS_COMMON_CMD_SETONOFF_UARTTOUCH_OFF;
            TvManager.getInstance().setTvosCommonCommand(cmd);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "关闭串口触控失败!");
        }
    }

    /**
     * 获得半屏状态 0:全屏 1:半屏
     *
     * @return
     */
    public static int getHalfScreenStatus() {
        int status = 0;
        try {
            short[] shorts = TvManager.getInstance().setTvosCommonCommand("GetScreen_STATUS");
            if (shorts != null && shorts.length >= 1) {
                status = shorts[0];
                LogUtils.d("getHalfScreenStatus  shorts[0]->%s", status);
            }

            LogUtils.d("getHalfScreenStatus->%s", status);
            return status;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 设置半屏
     *
     * @param ishalf
     */
    public static void setPipscale(boolean ishalf) {
        String cmdMsg = "SetHalfScreen_OFF";
        if (ishalf) { // 半屏
            cmdMsg = "SetHalfScreen_ON";
        }

        try {
            TvManager.getInstance().setTvosCommonCommand(cmdMsg);

            VideoWindowType videoWindowType = new VideoWindowType();
            PictureManager pm = TvManager.getInstance().getPictureManager();
            PanelProperty pp = pm.getPanelWidthHeight();
            if (ishalf) { //  1/3屏时
                videoWindowType.x = 0;
                videoWindowType.y = 1080;
                videoWindowType.width = 3840;
                videoWindowType.height = 1080;
            } else { // 全屏时
                pm.freezeImage();
                videoWindowType.x = 0;
                videoWindowType.y = 0;
                videoWindowType.width = 0;
                videoWindowType.height = 0;
            }
            if (!(TvManager.getInstance() == null || TvManager.getInstance().getPictureManager() == null)) {
                TvManager.getInstance().getPictureManager()
                        .selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
            }
            if (!(TvManager.getInstance() == null || TvManager.getInstance().getPictureManager() == null)) {
                Log.i("RootActivity", "Various parameters x : " + videoWindowType.x + " y : "
                        + videoWindowType.y + " w : " + videoWindowType.width + " h : "
                        + videoWindowType.height);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
            }
            if (TvManager.getInstance() != null
                    && TvManager.getInstance().getPictureManager() != null) {
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭半屏
     */
    public static void closeHalfScreen() {
        int isHalfScreen = getHalfScreenStatus();
        LogUtils.d("change Source isHalfScreen->" + isHalfScreen);
        if (isHalfScreen == 1) {
            AppUtils.setPipscale(false);
        }
    }
}
