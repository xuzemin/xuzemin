package com.protruly.floatwindowlib.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.activity.SettingsNewActivity;
import com.protruly.floatwindowlib.broadcast.BootReceiver;
import com.protruly.floatwindowlib.broadcast.ShowHideReceiver;
import com.protruly.floatwindowlib.constant.CommConsts;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.fragment.MoreFragment;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.protruly.floatwindowlib.ui.BottomMenuLayout;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.protruly.floatwindowlib.ui.SignalUpdateDialogLayout;
import com.protruly.floatwindowlib.ui.ThemometerLayout;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.protruly.floatwindowlib.utils.SystemUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.MstarConst;
import com.yinghe.whiteboardlib.utils.SPUtil;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 后台服务
 */
public class FloatWindowService extends Service {
    private final static String TAG = FloatWindowService.class.getSimpleName();
    public final static String KEY_UPDATE_APP_URL = "KEY_UPDATE_APP_URL";
    public final static String KEY_TV_OS_CMD = "KEY_TV_OS_CMD"; // TVOS命令

    public final static String START_ACTION = "com.protruly.floatwindowlib.action.VIRTUAL_KEY"; // 启动虚拟按键

    public final static String START_TMP_ACTION = "com.ctv.FloatWindowService.START_TMP_ACTION"; // 查看整机温度
    public final static String SIGNAL_UPDATE_ACTION = "com.ctv.FloatWindowService.SIGNAL_UPDATE_ACTION"; // 自定义信号源
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

//    public static boolean isHide; // 判断虚拟按键是否隐藏

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
    public static Handler mHandler = null;

    /**
     * 定时器，定时进行检测当前应该创建还是移除悬浮窗。
     */
    private Timer timer;

    private volatile boolean isFirstStart = true;
    private volatile boolean isDestroy = false;

    long startTime = 0;
    long endTime;
    static int lastLightSense;

    public static final String ANNOTATE_FINISH_ACTION = "com.ctv.annotate.FINISH"; // 批注时发送的通知广播
    public static final String FINISH_ACTION = "com.ctv.launcher.FINISH"; // 截图APP关闭

    public HandlerThread mDataThread;
    public static Handler mDataHandler;// 数据处理

    private ScheduledExecutorService threadPool; // 线程池
    private Future future;

    private static final Object LOCK = new Object();
    //不操作5秒隐藏
    public static final int hideTime = 5 * 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.d("FloatWindowService onCreate init.....");

        threadPool = Executors.newScheduledThreadPool(3);

        mDataThread = new HandlerThread(TAG);
        mDataThread.start();
        mDataHandler = new DataHandler(this, mDataThread.getLooper());

        mHandler = new UIHandler(this);
        isFirstStart = true;
        isDestroy = false;

        // 初始化状态: 批注和截图是否显示
        int isStart = Settings.System.getInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_DEFAULT);
        if (isStart == -1) {
            Settings.System.putInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_VALID);
        }
        // 获得底部menu
        BottomMenuLayout menuLayout = FloatWindowManager.getBottomMenuLayout();//FloatWindowManager.createBottomMenuLayout(getApplicationContext());
        if (menuLayout == null) {
            LogUtils.d("onCreate menuLayout.....");
            menuLayout = FloatWindowManager.createBottomMenuLayout(getApplicationContext());
            menuLayout.justHideMenu();
        }

        receiver = mReceiver;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ANNOTATE_FINISH_ACTION);
        filter.addAction(FINISH_ACTION);
        registerReceiver(receiver, filter);
    }

    BroadcastReceiver receiver;
    /**
     * 截图APP监听
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ANNOTATE_FINISH_ACTION.equals(action) || FINISH_ACTION.equals(action)) { // 批注关闭 或者 截图APP关闭
                Log.d(TAG, "批注或者截图关闭的广播!");
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
        int isStart = Settings.System.getInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_DEFAULT);
        boolean flag = false;
        // 判断截图是否运行
        switch (isStart) {
            case CommConst.STATUS_COMMENT: { // 前面打开批注时  CommConst.STATUS_COMMENT
                flag = AppUtils.isTopRunning(this, MstarConst.SCREENCAP_PACKAGE);
                if (flag) {
                    Settings.System.putInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_SCREENSHOT);
                }
                break;
            }
            case CommConst.STATUS_SCREENSHOT: { // 前面打开截图时
                flag = AppUtils.isServiceRunning(this, MstarConst.COMMENT_ENTER_SERVICE);
                if (flag) {
                    Settings.System.putInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_COMMENT);
                }
                break;
            }
            default:
                Settings.System.putInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_VALID);
                break;
        }

        if (!flag) {
            Settings.System.putInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_VALID);
        }

        isStart = Settings.System.getInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_DEFAULT);
        Log.d(TAG, "closeCommentOrScreencap annotate.start->" + isStart);
        CmdUtils.changeUSBTouch(this, true);

        checkEasyTouch();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            // 初始化操作
            init(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 初始化操作
     *
     * @param intent
     */
    private void init(Intent intent) {
        Log.d(TAG, "onStartCommand init");
        String factoryMode = SystemUtils.getFactoryMode();
        if ("1".equals(factoryMode)) { // 工厂模式时
            // 老化模式
            String burningMode = SystemUtils.getBurningMode();
            boolean isBurning = (BootReceiver.BURINGMODE_MODE_FACTEST.equals(burningMode));

            // 自动化工厂测试
            String autoFacMode = SystemUtils.getAutoFacMode();
            boolean isAutoFacMode = ("5".equals(autoFacMode)
                    || "4".equals(autoFacMode));

//        Log.d(TAG,"SystemUtils burningMode->" + burningMode);
            if (isBurning || isAutoFacMode) {
//            LogUtils.d("工厂测试或者老化测试.....");

                // 关闭悬浮按钮
                AppUtils.openOrCloseEasyTouch(getApplicationContext(), false);

                isDestroy = true;
                isFirstStart = false;
                mDataHandler.postDelayed(() -> {
                    stopSelf();
                }, 200);
                return;
            }
        }

        // 处理Action
        handleAction(intent);

        // 第一次启动时
        if (isFirstStart) {
            isFirstStart = false;
            mHandler.postDelayed(() -> {
                // 判断是否开启光感
                checkLightSense();

                // 检测背光值
                checkEyecare();

                // 判断是否开启悬浮助手
                checkEasyTouch();
            }, 1000);

            // 开启定时器，每隔5秒刷新一次
/*            if (timer == null) {
                timer = new Timer();
                timer.scheduleAtFixedRate(new RefreshTask(), 0, 5000);
            }*/
        }
    }

    /**
     * 检测是否开启光感
     */
    private void checkLightSense() {
        if (MyUtils.isSupportLightSense()) {
            // 判断是否开启光感
            boolean isStartLightSense = Settings.System.getInt(FloatWindowService.this.getContentResolver(),
                    CommConsts.IS_LIGHTSENSE, 0) == 1;
            if (isStartLightSense) { // 打开
                Log.d(TAG, "检测是否开启光感 isStartLightSense->" + isStartLightSense);
                startLightSense();
            }
        }
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
        if (easyTouchOpen == 1 && !TextUtils.equals(lockStatus, "on")) { // 开启
            AppUtils.openOrCloseEasyTouch(this, true);
        }
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
                AppUtils.setBacklight(lastBlackLight);
            }
        }
    }

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

        if (START_ACTION.equals(action)) { // 关闭
            LogUtils.d("START_ACTION .....");
            String cmdStr = intent.getStringExtra(KEY_TV_OS_CMD);
            if (!TextUtils.isEmpty(cmdStr)) {
                LogUtils.d("START_ACTION .....cmd->" + cmdStr);
                CmdUtils.setTvosCommonCommand(cmdStr);
            }
            return;
        } else if (CLOSE_ACTION.equals(action)) { // 关闭
            LogUtils.d("CLOSE_ACTION .....");
            isDestroy = true;
            isFirstStart = false;
            mDataHandler.postDelayed(() -> {
                stopSelf();
                // android.os.Process.killProcess(android.os.Process.myPid());
            }, 100);
            return;
        } else if (LIGHT_SENSE_ACTION.equals(action)) { // 打开和关闭光感
            LogUtils.d("LIGHT_SENSE_ACTION .....");
            // 判断是否开启光感
            changeLightSenseStatus();
            return;
        } else if (UPDATE_BLACK_LIGHT_ACTION.equals(action)) { // 更新背光值
            LogUtils.d("UPDATE_BLACK_LIGHT_ACTION, Backlight->" + AppUtils.getBacklight());
            updateBlackLightSeekbar(AppUtils.getBacklight());
            return;
        } else if (SWIPE_BLACK_LIGHT_ACTION.equals(action)) { // 滑动背光进度条
            LogUtils.d("SWIPE_BLACK_LIGHT_ACTION...");
            closeEyeCareLightSense();
            return;
        } else if (START_COMMENT_ACTION.equals(action)) { // 启动批注
            int isStart = Settings.System.getInt(getContentResolver(), "annotate.start", 0);
            LogUtils.d("START_COMMENT_ACTION  .....isStart->" + isStart);
            if (isStart == 1) { // 批注显示时，关闭批注
                LogUtils.d("START_COMMENT_ACTION  关闭批注.....");
                sendBroadcast(new Intent("com.cultraview.annotate.broadcast.CLOSE"));
            } else { // 批注不显示时，打开批注
                LogUtils.d("START_COMMENT_ACTION 打开批注.....");
                // 启动批注
                AppUtils.showComment(getApplicationContext());
            }
            return;
        } else if (START_TMP_ACTION.equals(action)) { // 整机温度
            LogUtils.d("START_TMP_ACTION .....");
            //  整机温度
            MyUtils.showTemperature(this);
            return;
        } else if (SIGNAL_UPDATE_ACTION.equals(action)) { // 自定义信号源
            LogUtils.d("SIGNAL_UPDATE_ACTION .....");
            MyUtils.showSignalUpdate(this);
            return;
        } else if (START_EASY_TOUCH_ACTION.equals(action)) { // 开启悬浮按钮
            LogUtils.d("START_EASY_TOUCH_ACTION .....");
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

        // 获得底部menu
        BottomMenuLayout menuLayout = FloatWindowManager.getBottomMenuLayout();//FloatWindowManager.createBottomMenuLayout(getApplicationContext());
        if (menuLayout == null) {
            LogUtils.d("menuLayout == null .....");
            menuLayout = FloatWindowManager.createBottomMenuLayout(getApplicationContext());
            menuLayout.justHideMenu();
        }

        // 处理各种action
        int curVisible = menuLayout.getVisibility();
        int newVisible = View.GONE;

        if (HIDE_ACTION.equals(action)) { // 隐藏
            LogUtils.d("HIDE_ACTION .....");
            newVisible = View.GONE;
        } else if (SHOW_ACTION.equals(action)) { // 显示
            LogUtils.d("SHOW_ACTION .....");
            newVisible = View.VISIBLE;
        } else if (SWIPE_ACTION.equals(action)) { // 侧滑
            // 滑动显示广播
            int swipeType = intent.getIntExtra(ShowHideReceiver.SWIPE_TYPE, -1);
            LogUtils.d("SWIPE_ACTION .....swipeType->%s, curVisible:%s, MENU_STATUS:%s",
                    swipeType, curVisible, BottomMenuLayout.MENU_STATUS);

            boolean isNeedShow = (BottomMenuLayout.MENU_STATUS == CommConst.MENU_STATUS_HIDDEN) ||
                    (curVisible == View.GONE);
            if (swipeType == 2
                    && isNeedShow) { // 滑动显示
                newVisible = View.VISIBLE;

                // 检测批注和截图状态，检测是否在搜台
                if (checkStatus()) {
                    return;
                }
                FloatWindowManager.createBottomMenuLayout(getApplicationContext())
                        .showMenu();
                // 发送隐藏动作
                mDataHandler.sendEmptyMessageDelayed(BOTTOM_MENU_HIDE_DELAY, 50);
//                LogUtils.d("SWIPE_ACTION .....BottomMenuLayout is show --VISIBLE");
            } else {
                return;
            }
        }

        /*// 需要显示时
        if (newVisible == View.VISIBLE){
            // 检测批注和截图状态，检测是否在搜台
            if (checkStatus()){
                return;
            }
            FloatWindowManager.createBottomMenuLayout(getApplicationContext())
                    .showMenu();
            // 发送隐藏动作
            mDataHandler.sendEmptyMessageDelayed(2, 200);
        } else { // 需要隐藏时
            if (curVisible == View.VISIBLE){ // 当前正在显示，需要隐藏
                // 发送隐藏动作
                mDataHandler.sendEmptyMessageDelayed(3, 50);
            }
        }*/
    }

    /**
     * 检测批注和截图状态，检测是否在搜台
     *
     * @return
     */
    private boolean checkStatus() {
        int isStart = Settings.System.getInt(getContentResolver(), CommConst.STATUS_START, CommConst.STATUS_DEFAULT);
        if (isStart > 0) {
            // 修正状态
            switch (isStart) {
                case CommConst.STATUS_COMMENT: { // 批注状态时
                    // 若在批注下时，批注服务没有运行了
                    if (!AppUtils.isServiceRunning(this, MstarConst.COMMENT_ENTER_SERVICE)) {
                        isStart = CommConst.STATUS_VALID;
                        LogUtils.d("状态异常 ..... 在批注下时，批注服务没有运行了");
                    }
                    break;
                }
                case CommConst.STATUS_SCREENSHOT: { // 截图状态时
                    // 若在截图下时，截图APP没有运行了
                    if (!AppUtils.isTopRunning(this, MstarConst.SCREENCAP_PACKAGE)) {
                        isStart = CommConst.STATUS_VALID;
                        LogUtils.d("状态异常 ..... 在截图下时，截图APP没有运行了");
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            if (isStart == CommConst.STATUS_VALID) {
                Settings.System.putInt(getContentResolver(), CommConst.STATUS_START, isStart);
                // 打开USB触控
                CmdUtils.changeUSBTouch(this, true);

                checkEasyTouch();
            }

            // 截图或者批注运行时
            if (isStart > CommConst.STATUS_VALID) {
                LogUtils.d("annotate.start ..... 截图或者批注 isStart > 0");
                return true;
            }
        }

        // 正在搜台时, 不显示
        if (AppUtils.isChannetuning()) {
            LogUtils.d("source display 或者正在搜台时, 不显示 .....");
            return true;
        }

        return false;
    }

    /**
     * 滑动背光进度条，关闭护眼和光感
     */
    private void closeEyeCareLightSense() {
        // 关闭自动光感
        closeLightSense();

        // 关闭护眼
        if (AppUtils.isEyeCare(FloatWindowService.this)) {
            LogUtils.d(TAG, "亮度设置值，关闭护眼");
            Settings.System.putInt(getContentResolver(), CommConsts.IS_EYECARE, 0);
            if (MoreFragment.mHandler != null) {
                Message msg = MoreFragment.mHandler.obtainMessage(2, false);
                MoreFragment.mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * 调节亮度时，关闭光感
     */
    private void closeLightSense() {
        int lightSense = Settings.System.getInt(getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);

        if (lightSense == 1) { // 当前光感开启时，关闭光感
            Settings.System.putInt(getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);

            if (MoreFragment.mHandler != null) {
                Message msg = Message.obtain();
                msg.what = MoreFragment.KEY_CHANGE_LIGHT_SENSE;
                msg.obj = false;
                MoreFragment.mHandler.sendMessage(msg);
            }

            if (FloatWindowService.mHandler != null) {
                Message msg = Message.obtain();
                msg.what = FloatWindowService.KEY_CHANGE_LIGHT_SENSE;
                msg.obj = false;
                FloatWindowService.mHandler.sendMessage(msg);
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
            if (isStartLightSense) {
                startLightSense();
            } else {
                stopLightSense();
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

//        // 开启光感定时器，每隔1.5秒刷新一次
//        if (timerLight == null) {
//            timerLight = new Timer();
//            timerLight.scheduleAtFixedRate(new LightSenseTask(), 0, 1500);
//        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.d("onDestroy");

        isDestroy = true;

        // 释放资源
        releaseData();
    }

    /**
     * 释放资源
     */
    private void releaseData() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }

        if (mDataHandler != null) {
            mDataHandler.removeCallbacksAndMessages(null);
            mDataThread.quit();
        }

        FloatWindowManager.removeThmometerWindow(getApplicationContext());
        FloatWindowManager.removeBottomMenuLayout(getApplicationContext());
    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            // 当前没有浮框，直接创建浮框
            if (!isDestroy && !FloatWindowManager.isMenuShowing() && mHandler != null) {
                mHandler.post(() -> {
                    // 创建左右两边的控制菜单
                    FloatWindowManager.createBottomMenuLayout(getApplicationContext()).justHideMenu();
                    SystemClock.sleep(500);
                });
            }
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
                            if (mHandler != null) {
                                // 禁止背光进度条滑动
                                mHandler.post(() -> {
                                    if (SettingsNewActivity.mHandler != null) {
                                        Message msg = SettingsNewActivity.mHandler.obtainMessage(SettingsNewActivity.MSG_UPDATE_LIGHT,
                                                false);
                                        SettingsNewActivity.mHandler.sendMessage(msg); // 更新亮度进度条
                                    }
                                });
                            }
                            changeLightSense(lightSense);
                            lastLightSense = lightSense;

                            if (mHandler != null) {
                                // 允许背光进度条滑动
                                mHandler.post(() -> {
                                    if (SettingsNewActivity.mHandler != null) {
                                        Message msg = SettingsNewActivity.mHandler.obtainMessage(SettingsNewActivity.MSG_UPDATE_LIGHT,
                                                true);
                                        SettingsNewActivity.mHandler.sendMessage(msg); // 更新亮度进度条
                                    }
                                });
                            }
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
     * 更新光感UI
     *
     * @param blackLight
     */
    private void updateBlackLightSeekbar(final int blackLight) {
        if (blackLight <= 0) {
            return;
        }

        AppUtils.setBacklight(blackLight);
        if (mHandler != null) {
            mHandler.post(() -> {
                if (SettingsNewActivity.mHandler != null) {
                    SettingsNewActivity.mHandler.sendEmptyMessage(SettingsNewActivity.MSG_UPDATE_LIGHT); // 更新亮度进度条
                }
            });
        }
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
                default:
                    break;
            }
        }
    }

    /**
     * 隐藏
     */
    Runnable hideRunnable = () -> {
        if (mHandler == null) {
            return;
        }

        mHandler.post(() -> {
            // 在信号源UI时
            ControlMenuLayout left = FloatWindowManager.createMenuWindowLeft(FloatWindowService.this);
            ControlMenuLayout right = FloatWindowManager.createMenuWindow(FloatWindowService.this);

            int visible = View.GONE;

            if (left.getVisibility() != visible) {
                left.setVisibility(visible);
            }
            if (right.getVisibility() != visible) {
                right.setVisibility(visible);
            }
        });
    };

    /**
     * 隐藏底部菜单栏
     */
    Runnable hideBottomRunnable = () -> {
        LogUtils.d("hideBottomRunnable 隐藏菜单");

        if (mHandler == null) {
            return;
        }

        mHandler.post(() -> {
            FloatWindowManager.createBottomMenuLayout(FloatWindowService.this)
                    .hideMenu();
        });

        mDataHandler.removeCallbacks(this.hideBottomRunnable);
    };
    public static final int MENU_ACTION_HIDE_REMOVE = 0; // 移除隐藏动作
    public static final int MENU_ACTION_HIDE_DELAY = 1; // 处理隐藏动作
    public static final int BOTTOM_MENU_HIDE_DELAY = 2; // 延迟5s隐藏底部菜单
    public static final int BOTTOM_MENU_HIDE_IMMEDIATE = 3; // 立即隐藏底部菜单

    /**
     * data异步处理
     */
    public static final class DataHandler extends Handler {
        WeakReference<FloatWindowService> weakReference;

        public DataHandler(FloatWindowService service, Looper looper) {
            super(looper);
            this.weakReference = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FloatWindowService service = weakReference.get();

            if (service == null) {
                return;
            }

            // 开始处理
            switch (msg.what) {
                case MENU_ACTION_HIDE_REMOVE:{ // 移除隐藏动作
                    removeCallbacks(service.hideRunnable);
                    break;
                }
//                case 1:{ // 处理隐藏动作
//                    removeCallbacks(service.hideRunnable);
//                    postDelayed(service.hideRunnable, 5000);
//                    break;
//                }
                case BOTTOM_MENU_HIDE_DELAY: { // 延时处理隐藏底部动作

                    Log.d(TAG,"BOTTOM_MENU_HIDE_DELAY");
                    removeCallbacks(service.hideBottomRunnable);
                    postDelayed(service.hideBottomRunnable, hideTime);
                    break;
                }
                case BOTTOM_MENU_HIDE_IMMEDIATE: { // 立即隐藏底部动作
                    Log.d(TAG,"BOTTOM_MENU_HIDE_IMMEDIATE");
                    removeCallbacks(service.hideBottomRunnable);
                    postDelayed(service.hideBottomRunnable, 1);
                    break;
                }
                default:
                    break;
            }
        }
    }
}
