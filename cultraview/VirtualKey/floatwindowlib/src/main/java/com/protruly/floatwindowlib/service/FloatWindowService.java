package com.protruly.floatwindowlib.service;

import android.app.Dialog;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvPictureManager;
import com.mstar.android.tv.TvCommonManager;
import com.protruly.floatwindowlib.activity.SettingNewActivity;

import android.os.SystemProperties;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.constant.CommConsts;
import com.protruly.floatwindowlib.control.ActivityCollector;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.protruly.floatwindowlib.ui.SettingsDialogLayout;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.protruly.floatwindowlib.utils.SystemUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.MstarConst;
import com.yinghe.whiteboardlib.utils.ScreenUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.protruly.floatwindowlib.ui.ThemometerLayout;

import android.os.Message;
import android.widget.Toast;

/**
 * 后台服务
 */
public class FloatWindowService extends Service {
    private final static String TAG = FloatWindowService.class.getSimpleName();

    public static String updateAPPUrl = "";// 下载最新版本
    public final static String KEY_UPDATE_APP_URL = "KEY_UPDATE_APP_URL";

    private static String versionName; //版本号
    private final static int timeDefault = 1000 * 30;
    public static int timeUpdate = timeDefault;

//    public static boolean isHide; // 判断虚拟按键是否隐藏

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    private Handler mHandler = new Handler();

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;

    // 对话框UI
    Dialog dialog;
    TextView mTitle;
    TextView mMessage;
    EditText mEditText;
    Button mConfirm; //确定按钮
    Button mCancel; //取消按钮

    private boolean isFirstFlag = false;

    public static final String FINISH_ACTION = "com.ctv.launcher.FINISH";
    public final static String KEY_TV_OS_CMD = "KEY_TV_OS_CMD"; // TVOS命令
    public final static String KEY_INPUTSOURCE_SWITCH = "KEY_INPUTSOURCE_SWITCH";
    public final static String KEY_PORT_SWITCH = "KEY_PORT_SWITCH";
    public final static String START_ACTION = "com.protruly.floatwindowlib.action.VIRTUAL_KEY"; // 启动虚拟按键
    public final static String CLOSE_BACK = "com.protruly.floatwindowlib.action.VIRTUAL_KEY_CLOSE"; //关闭背光


    public final static String SHUTDOWN_ALL = "com.protruly.floatwindowlib.action.SHUTDOWN_ALL"; //全部关机

    public final static String SHUTDOWN_OPS = "com.protruly.floatwindowlib.action.SHUTDOWN_OPS"; //关闭背光


    public final static String START_TMP_ACTION = "com.ctv.FloatWindowService.START_TMP_ACTION"; // 查看整机温度
    public final static String START_COMMENT_ACTION = "com.ctv.FloatWindowService.START_COMMENT_ACTION"; // 启动批注
    public final static String HIDE_ACTION = "com.ctv.FloatWindowService.HIDE_ACTION"; // 隐藏
    public final static String SHOW_ACTION = "com.ctv.FloatWindowService.SHOW_ACTION";// 显示
    public final static String CLOSE_ACTION = "com.ctv.FloatWindowService.CLOSE_ACTION"; // 关闭
    public final static String LOCK_ACTION = "com.ctv.FloatWindowService.LOCK_ACTION"; //锁屏

    public final static String LIGHT_SENSE_ACTION = "com.ctv.FloatWindowService.LIGHT_SENSE_ACTION"; // 打开和关闭光感
    public static final String UPDATE_BLACK_LIGHT_ACTION = "com.ctv.FloatWindowService.UPDATE_BLACK_LIGHT_ACTION"; // 更新背光值
    public final static String SWIPE_BLACK_LIGHT_ACTION = "com.ctv.FloatWindowService.SWIPE_BLACK_LIGHT_ACTION"; // 滑动背光进度条

    public final static String SWIPE_ACTION = "com.ctv.FloatWindowService.SWIPE_ACTION"; // 底部滑动
    public final static String START_EASY_TOUCH_ACTION = "com.ctv.FloatWindowService.START_EASY_TOUCH_ACTION"; // 启动悬浮按钮
    public final static String STOP_EASY_TOUCH_ACTION = "com.ctv.FloatWindowService.STOP_EASY_TOUCH_ACTION"; // 停止悬浮按钮


    // 批注时发送的通知广播  宝泽批注
    public static String ACTION_MARK_EXIT = MstarConst.ACTION_CTV_MARK_HIDE;// "com.ctv.annotate.FINISH"; // 批注时发送的通知广播
    // 截图关闭时
    public static String ACTION_SHORTCAP_HIDE = MstarConst.ACTION_CTV_SHORTCAP_HIDE; // 截图APP关闭
    private Future future;
    static int lastLightSense;
    long startTime = 0;
    long endTime;
    private ScheduledExecutorService threadPool; // 线程池
    private static final Object LOCK = new Object();
    private Thread closeSystemThread;

    @Override
    public void onCreate() {
        super.onCreate();

        if (SystemUtils.isAutoTestOrBurning()) {
            LogUtils.d("onCreate 工厂老化或者自动化测试.....");
            return;
        }

        isFirstFlag = true;
        mHandler = new UIHandler(this);
        mHandler.sendEmptyMessageDelayed(0,5000);
        threadPool = Executors.newScheduledThreadPool(3);
        initReceiver();
    }

    private void SystemInit() throws Settings.SettingNotFoundException {

        Settings.Global.putInt(getContentResolver(), Settings.Global.DEVICE_PROVISIONED, 1);

        if (Settings.Secure.getInt(getContentResolver(), "user_setup_complete") == 0) {
            Settings.Secure.putInt(getContentResolver(), "user_setup_complete", 1);
        }

        if (Settings.Secure.getString(getContentResolver(), "tv_user_setup_complete") == null
                || Settings.Secure.getInt(getContentResolver(), "tv_user_setup_complete") == 0) {
            Settings.Secure.putInt(getContentResolver(), "tv_user_setup_complete", 1);
        }

        if (Settings.Secure.getString(getContentResolver(), "default_input_method") == null ||
                Settings.Secure.getString(getContentResolver(), "default_input_method").equals("")
        ) {
            Settings.Secure.putString(getContentResolver(), "default_input_method", "com.keanbin.pinyinime/.PinyinIME");
        }
    }

    public class LocaleChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "mReceiver  onReceive  intent.getAction(): " + intent.getAction());

            if (intent.getAction().equals(Intent.ACTION_LOCALE_CHANGED)) {
                Log.e("LocaleChangeReceiver", "Language change");
            }
        }
    }


    private void initReceiver() {
        // 批注时发送的通知广播  宝泽批注
        switch (MstarConst.MARK_TYPE) {
            case MstarConst.MARK_TYPE_CTV: { // CTV批注
                ACTION_MARK_EXIT = MstarConst.ACTION_CTV_MARK_OPEN;
                break;
            }
            case MstarConst.MARK_TYPE_BOZEE: { // 宝泽
                ACTION_MARK_EXIT = MstarConst.ACTION_BOOZE_MARK_EXIT;
                break;
            }
        }
        // 截图关闭时
        ACTION_SHORTCAP_HIDE = MstarConst.ACTION_CTV_SHORTCAP_HIDE; // 截图APP关闭

        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_MARK_EXIT);
        filter.addAction(ACTION_SHORTCAP_HIDE);
        registerReceiver(receiver, filter);
    }

    /**
     * 截图APP监听
     */
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_MARK_EXIT.equals(action) || ACTION_SHORTCAP_HIDE.equals(action)) { // 批注关闭 或者 截图APP关闭
                Log.d(TAG, "批注或者截图关闭的广播!" + action);
                new Thread(() -> {
                    SystemClock.sleep(100);
                    closeCommentOrScreencap();
                }).start();
            }
        }
    };

    /**
     * 批注或者截图关闭
     */
    private void closeCommentOrScreencap() {
        Log.d(TAG, "批注或者截图关闭！");
        int isStart = Settings.System.getInt(getContentResolver(), "annotate.start", 0);
        boolean flag = false;
        // 判断截图是否运行
        switch (isStart) {
            case 1: { // 前面打开批注时
                flag = AppUtils.isTopRunning(this, MstarConst.SCREENCAP_PACKAGE);
                if (flag) {
                    Settings.System.putInt(getContentResolver(), "annotate.start", 2);
                }
                break;
            }
            case 2: { // 前面打开截图时
                flag = AppUtils.isServiceRunning(this, MstarConst.COMMENT_ENTER_SERVICE);
                if (flag) {
                    Settings.System.putInt(getContentResolver(), "annotate.start", 1);
                }
                break;
            }
            default:
                Settings.System.putInt(getContentResolver(), "annotate.start", 0);
                break;
        }

        if (!flag) {
            Settings.System.putInt(getContentResolver(), "annotate.start", 0);
        }

        isStart = Settings.System.getInt(getContentResolver(), "annotate.start", 0);
        Log.d(TAG, "closeCommentOrScreencap annotate.start->" + isStart);

        CmdUtils.changeUSBTouch(this, true);

        checkEasyTouch();
    }

    /**
     * 检测是否开启悬浮助手
     */
    private void checkEasyTouch() {
        // 判断是否开启悬浮助手
        int easyTouchOpen = Settings.System.getInt(this.getContentResolver(),
                "EASY_TOUCH_OPEN", 0);
        String lockStatus = SystemProperties.get("persist.sys.lockScreen", "off");
        LogUtils.d("easyTouchOpen->%s, lockStatus->%s", easyTouchOpen, lockStatus);
        // 悬浮助手开关开启，并且不在锁屏状态下时
        if (easyTouchOpen == 1
                && !TextUtils.equals(lockStatus, "on")
        ) { // 开启
            AppUtils.openOrCloseEasyTouch(this, true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (SystemUtils.isAutoTestOrBurning()) {
                LogUtils.d("onStartCommand 工厂老化或者自动化测试.....");
                return super.onStartCommand(intent, flags, startId);
            }

            // 初始化操作
            init(intent);
            try {
                SystemInit();
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化操作
     *
     * @param intent
     */
    private void init(Intent intent) {
        LogUtils.tag(TAG).d("onStartCommand init");

        // 初始化dialog
        initDialogView();

        handleAction(intent);

        // 开启定时器，每隔5秒刷新一次
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 5000);
        }

        // 首次启动时
        if (isFirstFlag) {
            isFirstFlag = false;
            FloatWindowManager.createMenuWindow(getApplicationContext()).setVisibility(View.VISIBLE);
            FloatWindowManager.createMenuWindowLeft(getApplicationContext()).setVisibility(View.VISIBLE);
            try {
                Class clazz = Class.forName("android.service.notification.NotificationListenerService");
                Method registerAsSystemService = clazz.getDeclaredMethod("registerAsSystemService", Context.class, ComponentName.class, int.class);
                registerAsSystemService.invoke(mNotificationListener, this, new ComponentName(getPackageName(), getClass().getCanonicalName()), -1);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("gyx", "e.printStackTrace()=" + e.getMessage());
            }
            mHandler.postDelayed(() -> {
                // 判断是否开启光感
                changeLightSenseStatus();
                // 检测护眼
                checkEyecare();

                // 判断是否开启悬浮助手
                checkEasyTouch();            }, 1000);
        }
    }


    NotificationListenerService mNotificationListener =
            new NotificationListenerService() {
                @Override
                public void onNotificationPosted(StatusBarNotification sbn) {
                    super.onNotificationPosted(sbn);
                    Log.i("gyx", "onNotificationPosted");
                    Bundle extras = sbn.getNotification().extras;
                    if (extras != null) {
                        String notificationText = extras.getString(Notification.EXTRA_TEXT);
                        if (notificationText != null) {
                            FloatWindowManager.addSBN(FloatWindowService.this, sbn);
                        }
                    } else {
                        Log.i("gyx", "sbn.getNotification().extras==null");
                    }

                }

                @Override
                public void onNotificationRemoved(StatusBarNotification sbn) {
                    super.onNotificationRemoved(sbn);
                    Log.i("gyx", "onNotificationRemoved");
                    FloatWindowManager.delSBN(FloatWindowService.this, sbn);
                }
            };


    /**
     * 处理intent
     */
    private void handleAction(Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }

        if (START_ACTION.equals(action)) { // 打开
            LogUtils.d("START_ACTION .....");
            String cmdStr = intent.getStringExtra(KEY_TV_OS_CMD);
            if (!TextUtils.isEmpty(cmdStr)) {
                LogUtils.d("START_ACTION .....cmd->" + cmdStr);
                CmdUtils.setTvosCommonCommand(cmdStr);
            } else {
                int inputSourceID = intent.getIntExtra(KEY_INPUTSOURCE_SWITCH,40);
//                CtvPictureManager.getInstance().enableBacklight();
                LogUtils.d("START_ACTION ....."+inputSourceID);
                if (inputSourceID != 40) {
                    if(inputSourceID == 0 ){
                        TvCommonManager.getInstance().setTvosCommonCommand("SetVGA0");
                        TvCommonManager.getInstance().setInputSource(0);
                    }else if(inputSourceID > 0 && inputSourceID < 34){
                        TvCommonManager.getInstance().setInputSource(inputSourceID);
                    }
                }else{
                    int port = intent.getIntExtra(KEY_PORT_SWITCH,0);
                    LogUtils.d("START_ACTION ....."+port);
                    if(port == 2){
                        TvCommonManager.getInstance().setTvosCommonCommand("SetTIPORT2");
                    }else{
                        TvCommonManager.getInstance().setTvosCommonCommand("SetTIPORT0");
                    }
                    TvCommonManager.getInstance().setInputSource(23);
                }

            }
        } else if (CLOSE_ACTION.equals(action)) { // 关闭
            LogUtils.d("CLOSE_ACTION .....");
            mHandler.postDelayed(() -> {
                stopSelf();
                // android.os.Process.killProcess(android.os.Process.myPid());
            }, 100);
            return;
        } else if (CLOSE_BACK.equals(action)) {
            CtvPictureManager.getInstance().disableBacklight();
        } else if (SHUTDOWN_ALL.equals(action)) { //全部关机
            isCloseALL = true;
            LogUtils.d("qkmin ---SHUTDOWN_ALL .....");
            int[] GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand("GetOPSDEVICESTATUS");
            int[] GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand("GetOPSPOWERSTATUS");
            Log.d("qkmin", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
            Log.d("qkmin", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);

            if (GetOPSPOWERSTATUS[0] == 0 && closeSystemThread == null) {//0,表示有OPS设备接入；1，表示没有OPS设备接入。
                LogUtils.d("qkmin ---have ops shutdown .....");
                closeSystemThread = new Thread(closeSystemRunnable);
                closeSystemThread.start();
            } else {
                LogUtils.d("qkmin ---android shutdown .....");
                Thread thr = new Thread("ShutdownActivity") {
                    @Override
                    public void run() {
                        IPowerManager pm = IPowerManager.Stub.asInterface(
                                ServiceManager.getService(Context.POWER_SERVICE));
                        try {

                            pm.shutdown(false,
                                    false ? PowerManager.SHUTDOWN_USER_REQUESTED : null,
                                    false);

                        } catch (RemoteException e) {
                        }
                    }
                };
                thr.start();
            }

        } else if (SHUTDOWN_OPS.equals(action)) { //OPS关机
            LogUtils.d("qkmin ---SHUTDOWN_OPS .....");
            isCloseALL = false;

            int[] GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand("GetOPSDEVICESTATUS");
            int[] GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand("GetOPSPOWERSTATUS");
            //if(GetOPSDEVICESTATUS[0] == 1){//0,表示有OPS设备接入；1，表示没有OPS设备接入。
            //     Toast.makeText(mContext,"找不到OPS设备", Toast.LENGTH_SHORT).show();
            // }else{

            if (GetOPSPOWERSTATUS[0] == 1) {//0,表示OPS开机；1，表示OPS关机。
                Toast.makeText(this, R.string.close_ops, Toast.LENGTH_SHORT).show();
            } else {
//                Toast.makeText(this, R.string.ops_closing_msg, Toast.LENGTH_SHORT).show();
                closeSystemThread = new Thread(closeSystemRunnable);
                closeSystemThread.start();
            }


        } else if (LIGHT_SENSE_ACTION.equals(action)) { // 打开和关闭光感
            LogUtils.d("LIGHT_SENSE_ACTION .....");
            // 判断是否开启光感
            changeLightSenseStatus();
            return;
        } else if (UPDATE_BLACK_LIGHT_ACTION.equals(action)) { // 更新背光值
            LogUtils.d("UPDATE_BLACK_LIGHT_ACTION, Backlight->" + AppUtils.getBacklight());
            updateBlackLightSeekbar();
            return;
        } else if (SWIPE_BLACK_LIGHT_ACTION.equals(action)) { // 滑动背光进度条
            LogUtils.d("SWIPE_BLACK_LIGHT_ACTION...");
//            closeEyeCareLightSense();
            return;
        } else if (START_COMMENT_ACTION.equals(action)) { // 启动批注
            int isStart = Settings.System.getInt(getContentResolver(), "annotate.start", 0);
            LogUtils.d("START_COMMENT_ACTION  .....isStart->" + isStart);
            if (isStart == 1) { // 批注显示时，关闭批注
                LogUtils.d("START_COMMENT_ACTION 关闭批注.....");
                AppUtils.closeComment(this.getApplicationContext());
            } else { // 批注不显示时，打开批注
                LogUtils.d("START_COMMENT_ACTION 打开批注.....");
                AppUtils.showComment(this.getApplicationContext());
            }
            return;
        } else if (START_TMP_ACTION.equals(action)) { // 整机温度
            LogUtils.d("START_TMP_ACTION .....");
            //  整机温度
            showTemperature();
            return;
        } else if (START_EASY_TOUCH_ACTION.equals(action)) { // 开启悬浮按钮
            LogUtils.d("START_EASY_TOUCH_ACTION .....");
//	        changeEasyTouch();

            // 判断是否开启悬浮助手
            int easyTouchOpen = Settings.System.getInt(this.getContentResolver(),
                    "EASY_TOUCH_OPEN", 0);
            String lockStatus = SystemProperties.get("persist.sys.lockScreen", "off");
            LogUtils.d("easyTouchOpen->%s, lockStatus->%s", easyTouchOpen, lockStatus);
            if (easyTouchOpen == 1 && !TextUtils.equals(lockStatus, "on")) { // 开启
                AppUtils.openOrCloseEasyTouch(this, true);
            }
            return;
        } else if (STOP_EASY_TOUCH_ACTION.equals(action)) { // 停止悬浮按钮
            LogUtils.d("STOP_EASY_TOUCH_ACTION .....");
            AppUtils.openOrCloseEasyTouch(this, false);
            return;
        }
//        else if (SHOW_SOURCE_ACTION.equals(action)) { // 显示信号通道列表
//            LogUtils.d("SHOW_SOURCE_ACTION .....");
//            int isRight = Settings.System.getInt(this.getContentResolver(), "showSignalList", 0);
//            FloatWindowManager.showSignal(this.getApplicationContext(), (isRight == 0));
//            return;
//        }
//        else if (SWIPE_ACTION.equals(action)) { // 滑动悬浮按钮
//            LogUtils.d("STOP_EASY_TOUCH_ACTION .....");
//            int swipeType = intent.getIntExtra("SWIPE_TYPE", -1);
//            switch (swipeType){
//                case 3:{ // right
//                    ControlMenuLayout right = FloatWindowManager.getMenuWindow();
//                    if (right == null){
//                        right = FloatWindowManager.createMenuWindow(getApplicationContext());
//                    }
//                    right.showSetting();
//                    break;
//                }
//                case 4:{ // left
//                    ControlMenuLayout left = FloatWindowManager.getMenuWindowLeft();
//                    if (left == null){
//                        left = FloatWindowManager.createMenuWindowLeft(getApplicationContext());
//                    }
//                    left.showSetting();
//                    break;
//                }
//                default:{
//                    break;
//                }
//            }
//            return;
//        }
    }

    /**
     * 更新光感UI
     *
     * @param blackLight
     */
    private void updateBlackLightSeekbar(final int blackLight) {
        if (blackLight <= 0) {
            return;
        }
        SystemProperties.set("persist.sys.backlight",""+blackLight);
        AppUtils.setBacklight(blackLight);
        mHandler.post(() -> {
            if (SettingNewActivity.mHandler != null) {
                SettingNewActivity.mHandler.sendEmptyMessage(SettingNewActivity.MSG_UPDATE_LIGHT); // 更新亮度进度条
            }
        });
    }

    /**
     * 更新背光进度条值
     */
    private void updateBlackLightSeekbar() {
        mHandler.post(() -> {
            if (SettingsDialogLayout.mHandler != null) {
                SettingsDialogLayout.mHandler.sendEmptyMessage(SettingsDialogLayout.MSG_UPDATE_LIGHT); // 更新亮度进度条
            }
        });
    }

    /**
     * 显示温度
     */
    private void showTemperature() {
        float tmpValue = CmdUtils.getTmpValue();

        boolean isFirst = false;
        ThemometerLayout thmometerWindow = FloatWindowManager.getThmometerWindow();
        if (thmometerWindow == null) {
            thmometerWindow = FloatWindowManager.createThemometerWindow(getApplicationContext());
            isFirst = true;
        }

        int visibility = thmometerWindow.getVisibility();
        if (!isFirst && visibility == View.VISIBLE) { // 若当前是显示，则隐藏
            thmometerWindow.setVisibility(View.INVISIBLE);
        } else { // 若当前是隐藏，则显示
            thmometerWindow.setVisibility(View.VISIBLE);
            // 更新进度
            if (ThemometerLayout.mHandler != null) {
                Message message = ThemometerLayout.mHandler.obtainMessage(1, tmpValue);
                ThemometerLayout.mHandler.sendMessage(message);

                // 延迟消失
                ThemometerLayout.mHandler.postDelayed(() -> {
                    ThemometerLayout thmometer = FloatWindowManager.getThmometerWindow();
                    if (thmometer != null) {
                        thmometer.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            }
        }
    }

    /**
     * 改变接受光感
     */
    private void changeLightSenseStatus() {
        // 判断是否开启光感
        boolean isStartLightSense = Settings.System.getInt(FloatWindowService.this.getContentResolver(),
                CommConsts.IS_LIGHTSENSE, 0) == 1;
//        Log.d(TAG,"是否开启光感 isStartLightSense->" + isStartLightSense);
        if (MyUtils.isSupportLightSense()) {
            boolean isOpen;
            if (isStartLightSense) { // 打开
                startLightSense();
                isOpen = true;
            } else { // 关闭
                stopLightSense();
                isOpen = false;
            }

            AppUtils.openOrCloseLightSense(this, isOpen);
            if (!isOpen) { // 关闭自动光感时，恢复背光值
                updateBlackLightSeekbar();
            }
        }
    }

    /**
     * 开启光感
     */
    private void startLightSense() {
        stopLightSense();
        lastLightSense = AppUtils.getBacklight();
        future = threadPool.scheduleAtFixedRate(lightSenseRunnable, 500, 2000, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止光感
     */
    private void stopLightSense() {
        try {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得光感值
     *
     * @return
     */
    private int getLightSense() {
        String lightSenseStr = SystemProperties.get("LIGHT_SENSE", "0");
        int num = 0;

        try {
            int lightSense = Integer.parseInt(lightSenseStr);
            num = lightSense;
        } catch (Exception e) {
            num = 0;
            e.printStackTrace();
            return num;
        }
        return num;
    }

    Runnable lightSenseRunnable = new Runnable() {
        @Override
        public void run() {
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
                endTime = System.currentTimeMillis();
            }

            int isLightSense = Settings.System.getInt(FloatWindowService.this.getContentResolver(),
                    CommConsts.IS_LIGHTSENSE, 0);
//            Log.d(TAG, "isLightSense->" + isLightSense);
            if ((isLightSense == 1) && (endTime - startTime > 200)) { // 开启了自动光感
                // 渐变设置背光
                synchronized (LOCK) {
                    int lightSense = getLightSense(); // Settings.System.getInt(FloatWindowService.this.getContentResolver(),"LIGHT_SENSE", -1);

                    // lightSense 在1至4直接，光感异常
                    if (lightSense > 0
                            && lightSense <= 100) {
                        if (lastLightSense != lightSense) {
                            // 禁止背光进度条滑动
                            mHandler.post(() -> {
                                if (SettingNewActivity.mHandler != null) {
                                    Message msg = SettingNewActivity.mHandler.obtainMessage(SettingNewActivity.MSG_UPDATE_LIGHT,
                                            false);
                                    SettingNewActivity.mHandler.sendMessage(msg); // 更新亮度进度条
                                }
                            });
                            changeLightSense(lightSense);
                            lastLightSense = lightSense;

                            // 允许背光进度条滑动
                            mHandler.post(() -> {
                                if (SettingNewActivity.mHandler != null) {
                                    Message msg = SettingNewActivity.mHandler.obtainMessage(SettingNewActivity.MSG_UPDATE_LIGHT,
                                            true);
                                    SettingNewActivity.mHandler.sendMessage(msg); // 更新亮度进度条
                                }
                            });
                        }
                    }
                }
            }

            endTime = System.currentTimeMillis();
            if (endTime - startTime < 1000) {
                SystemClock.sleep(endTime - startTime);
                startTime = endTime;
            }
        }
    };

    /**
     * 改变光感值
     *
     * @param end
     */
    private void changeLightSense(final int end) {
        int backlight = AppUtils.getBacklight();
        int start = backlight;

        // 渐变设置背光
        final int dlen = end - start;

        if (Math.abs(dlen) > 10) {
            int maxTime = Math.abs(dlen) / 10;
            if (maxTime > 5) { // 最大5次
                maxTime = 5;
            }

            int step = dlen / maxTime; // 步长
            for (int i = 0; i < maxTime; i++) {
                start += step;
                boolean flag = (dlen > 0) ? (start < end) : (start > end);
                if (flag) {
                    updateBlackLightSeekbar(start);
                    SystemClock.sleep(200);
                }
            }
        }

        updateBlackLightSeekbar(end);

        Log.d(TAG, "change start " + backlight
                + " to end->" + end);
    }


    /**
     * 检测背光值
     */
    private void checkEyecare() {
        if (AppUtils.isEyeCare(this)) {
            // 验证背光值
            int lastBlackLight = Settings.System.getInt(this.getContentResolver(),
                    "lastBlackLight", 50);
            int blackLight = AppUtils.getBacklight();
            if (lastBlackLight > 50 && blackLight == 50) {
                SystemProperties.set("persist.sys.backlight",""+blackLight);
                AppUtils.setBacklight(lastBlackLight);
            }
        }
    }

    private void showOrHide(boolean isHide) {
        int visible = isHide ? View.GONE : View.VISIBLE;

        FloatWindowManager.createMenuWindow(getApplicationContext()).setVisibility(visible);
        FloatWindowManager.createMenuWindowLeft(getApplicationContext()).setVisibility(visible);
    }

    /**
     * 初始化对话框UI
     */
    private void initDialogView() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.update_dialog_layout, null);
        mTitle = (TextView) dialogView.findViewById(R.id.title);
        mEditText = (EditText) dialogView.findViewById(R.id.message_edit);
        mMessage = (TextView) dialogView.findViewById(R.id.message);
        mConfirm = (Button) dialogView.findViewById(R.id.positiveButton);
        mCancel = (Button) dialogView.findViewById(R.id.negativeButton);

        dialog = new Dialog(this);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(false);

        // 设置对话框的大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = ScreenUtils.dip2px(this, 400); // 宽度
        lp.height = ScreenUtils.dip2px(this, 300); // 高度

        dialogWindow.setAttributes(lp);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.tag(TAG).d("onDestroy");
        // 释放资源
        releaseData();
    }

    /**
     * 释放资源
     */
    private void releaseData() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        FloatWindowManager.removeMenuWindow(getApplicationContext());
        FloatWindowManager.removeMenuWindowLeft(getApplicationContext());
        FloatWindowManager.removeThmometerWindow(getApplicationContext());

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        unregisterReceiver(receiver);
    }

    public static final int KEY_CHANGE_LIGHT_SENSE = 1; // 切换光感

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<FloatWindowService> weakReference;

        public UIHandler(FloatWindowService service) {
            super();
            this.weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            FloatWindowService service = weakReference.get();
            if (service == null) {
                return;
            }

            switch (msg.what) {
                case KEY_CHANGE_LIGHT_SENSE: // 改变光感
                    if (msg.obj instanceof Boolean) {
                        boolean isOpen = (boolean) msg.obj;
                        int lightSense = isOpen ? 1 : 0;
                        Settings.System.putInt(service.getContentResolver(), CommConsts.IS_LIGHTSENSE, lightSense);
                        service.changeLightSenseStatus();
                    }
                    break;
                case 0:
                    if(!MyUtils.isOpencheck){
                        MyUtils.checkUSB(true);
                    }
                    this.sendEmptyMessageDelayed(0,3000);
                    break;
                default:
                    break;
            }
        }
    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            // 当前没有浮框，直接创建浮框
            if (!FloatWindowManager.isWindowShowing()) {
                mHandler.post(() -> {
                    // 创建左右两边的控制菜单
                    FloatWindowManager.createMenuWindowLeft(getApplicationContext());
                    FloatWindowManager.createMenuWindow(getApplicationContext());
                    SystemClock.sleep(500);
                });
            }
        }
    }

    boolean isCloseALL = false;

    //Start WhiteBoard Patch
    Runnable closeSystemRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d("chen_powerdown", "start");
                Log.d("chen_powerdown", "close ops");
                int[] GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSDEVICESTATUS");
                int[] GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSPOWERSTATUS");
                Log.d("chen_powerdown", "GetOPSDEVICESTATUS:init:" + GetOPSDEVICESTATUS[0]);
                Log.d("chen_powerdown", "GetOPSPOWERSTATUS:init:" + GetOPSPOWERSTATUS[0]);
                CtvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSPOWERSTATUS");
                Log.d("chen_powerdown", "GetOPSPOWERSTATUS:first:" + GetOPSPOWERSTATUS[0]);
                Thread.sleep(200);
                CtvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSPOWERSTATUS");
                Log.d("chen_powerdown", "GetOPSPOWERSTATUS:setover" + GetOPSPOWERSTATUS[0]);
                Log.d("chen_powerdown", "ops:state");
                Thread.sleep(2000);
                GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSDEVICESTATUS");
                GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSPOWERSTATUS");
                Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
                int count = 0;

                while (GetOPSPOWERSTATUS[0] == 0) {
                    Log.d("chen_powerdown", "checkops state start ");
                    Log.d("chen_powerdown", "checkops time count : " + count);
                    Thread.sleep(1000);
                    count++;
                    if (count == 30) {
                        break;
                    }
                    Log.d("chen_powerdown", "change ops state start");
                    GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSDEVICESTATUS");
                    GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSPOWERSTATUS");
                    Log.d("chen_powerdown", "change ops state resutl:");
                    Log.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
                    Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
                }
                Log.d("chen_powerdown", "close ops sucess");

                if (isCloseALL) {
                    Log.d("chen_powerdown", "start to close Android");
//                    Thread.sleep(2000);
//                    Intent intent = new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN");
//                    intent.putExtra("android.intent.extra.KEY_CONFIRM", false);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                    //Android 关机
                    closeSystemThread = null;
                    IPowerManager pm = IPowerManager.Stub.asInterface(
                            ServiceManager.getService(Context.POWER_SERVICE));
                    try {
                        pm.shutdown(false,
                                false ? PowerManager.SHUTDOWN_USER_REQUESTED : null,
                                false);
                    } catch (RemoteException e) {
                        Log.e("chen_powerdown", "RemoteException e");
                    }
                } else {
                    closeSystemThread = null;
                    Log.e("qkmin", "isCloseALL " + isCloseALL);
                    Looper.prepare();
                    Toast.makeText(getApplicationContext(), R.string.close_ops, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            } catch (Exception e) {

                Log.e("qkmin", "Exception e" + e);
                e.printStackTrace();
            }
        }
    };
    //End WhiteBoard Patch
}
