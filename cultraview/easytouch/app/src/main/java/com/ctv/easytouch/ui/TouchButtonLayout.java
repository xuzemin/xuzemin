package com.ctv.easytouch.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.easytouch.Constants;
import com.ctv.easytouch.R;
import com.ctv.easytouch.been.AppInfo;
import com.ctv.easytouch.control.FloatWindowManager;
import com.ctv.easytouch.utils.*;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Desc: 温度计布局
 *
 * @author wang
 * @time 2017/4/13.
 */
public class TouchButtonLayout extends FrameLayout {
    static String TAG = TouchButtonLayout.class.getSimpleName();

    private ImageView btnImage;
    private View menuView;

    View btnBack;// 返回x
    View btnHome;// 回到主页
    View btnAppSwitch;// 多功能切换键
    View btnShowSetting;// 显示设置
    View rlBtnWhiteBoard;//批注按钮的父布局
    ImageView btnUsered;//批注按钮的父布局
    View btnCenter;// 中心按钮
    ImageView btnUserHide;
    private ApkInfoUtils apkInfoUtils;

    // 对话框UI
    SelectAppDialog dialogView;
    Dialog selectDialog;

    private static volatile boolean isSmall = true; // 显示方式:默认显示小图标
    private volatile int showType = 1; // 显示方式: 0半隐藏小图标 1显示小图标 2显示大图标

    private static volatile boolean isCloseUSBTouch = false; // 是否改变USBTouch

    private Timer timer;
    public static Handler mHandler;
    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager.LayoutParams mParams;// 浮窗的参数
    public static boolean userFlag = false;
    private TextView tvSetting;
    private TextView tvFavorites;
    private TextView tvAnnotation;
    private TextView tvAppSwitch;
    private TextView tvBack;
    private TextView tvHome;
    private final WindowManager wm;

    public TouchButtonLayout(Context context) {
        super(context);
        View root = LayoutInflater.from(context).inflate(R.layout.easy_touch_view, this);
        btnImage = root.findViewById(R.id.easy_touch);
        menuView = root.findViewById(R.id.touch_menu);

        mHandler = new UIHandler(this);
        setOnTouchListener(mTouchListener);
        wm = (WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE);
        initMenu();
    }

    private void initMenu() {
        apkInfoUtils = new ApkInfoUtils();

        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btn_home);
        btnAppSwitch = findViewById(R.id.btn_switch);
        btnShowSetting = findViewById(R.id.btn_setting);
        btnShowSetting = findViewById(R.id.btn_setting);
        rlBtnWhiteBoard = findViewById(R.id.btn_pan);
        btnUsered = findViewById(R.id.btn_usered);
        btnCenter = findViewById(R.id.btn_center);
        btnUserHide = findViewById(R.id.btn_user_hide_tips);

        tvSetting = findViewById(R.id.txt_setting);
        tvFavorites = findViewById(R.id.id_tv_favorites);
        tvAnnotation = findViewById(R.id.id_tv_annotation);
        tvAppSwitch = findViewById(R.id.id_tv_app_switch);
        tvBack = findViewById(R.id.id_tv_back);
        tvHome = findViewById(R.id.id_tv_home);

        if (userFlag) {
            btnUserHide.setVisibility(VISIBLE);
        } else {
            btnUserHide.setVisibility(GONE);
        }
        updateUseredIcon();

        setListener();

        initSelectDialog();
    }

    public boolean isSmall() {
        return isSmall;
    }

    /**
     * 更新图标
     */
    private void updateUseredIcon() {
        String packageName = (String) SPUtil.getData(getContext(), Constants.Companion.getUSERED_PACKAGE_NAME(), "");
        if (!TextUtils.isEmpty(packageName)) {
            Log.d(TAG, "updateUseredIcon start");
            AppInfo appInfo = apkInfoUtils.scanInstallApp(getContext(), packageName);
            if (appInfo != null) {
                Log.d(TAG, "updateUseredIcon change icon");
                ((ImageView) btnUsered).setImageDrawable(appInfo.getAppIcon());
            }
        }
    }

    /**
     * 设置监听
     */
    private void setListener() {
        btnBack.setOnClickListener(mOnClickListener);
        btnHome.setOnClickListener(mOnClickListener);
        btnAppSwitch.setOnClickListener(mOnClickListener);

        btnShowSetting.setOnClickListener(mOnClickListener);
        rlBtnWhiteBoard.setOnClickListener(mOnClickListener);
        btnUsered.setOnClickListener(mOnClickListener);
        btnUsered.setOnLongClickListener(mOnLongClickListener);
        btnUserHide.setOnClickListener(mOnClickListener);
        btnCenter.setOnClickListener(mOnClickListener);
    }

    public static long startTime = 0L;
    public static long endTime = 0L;

    /**
     * 开始任务
     */
    public void startTimetask() {
        // 开启定时器，每隔5秒刷新一次
        if (timer == null) {
            startTime = System.currentTimeMillis();
            endTime = System.currentTimeMillis();
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 10, 500);
        }
    }

    /**
     * 停止任务
     */
    public void stopTimetask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 显示大图标或者小图标
     *
     * @param isSmall
     */
    private void showWindow(boolean isSmall) {
        userFlag = false;
        this.isSmall = isSmall;
        if (isSmall) { // 显示小图标
            btnImage.setVisibility(View.VISIBLE);
            menuView.setVisibility(View.GONE);

        } else { // 显示大图标
            btnImage.setVisibility(View.GONE);
            menuView.setVisibility(View.VISIBLE);
            refreshLocal();
        }
        btnUserHide.setVisibility(GONE);
        startTime = System.currentTimeMillis();
        endTime = System.currentTimeMillis();
        FloatWindowManager.showWindow(getContext(), isSmall);
    }

    private void refreshLocal() {
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();

        DisplayMetrics metrics= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        config.locale = Locale.getDefault();
        //resources.updateConfiguration(config, metrics);
        tvSetting.setText(getResources().getString(R.string.setting));
        tvAnnotation.setText(getResources().getString(R.string.annotation));
        tvHome.setText(R.string.home);
        tvBack.setText(R.string.back);
        tvAppSwitch.setText(R.string.multitasking);
        tvFavorites.setText(R.string.favorites);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private static volatile int mMoveTag = 0; //
    private int mOldOffsetX;
    private int mOldOffsetY;
    private OnTouchListener mTouchListener = new OnTouchListener() {
        float lastX, lastY;
        int paramX, paramY;

        public boolean onTouch(View v, MotionEvent event) {
            final int action = event.getAction();
            // 展开时，点击外部区域，显示小图标
            if (!isSmall) {
                if (action == MotionEvent.ACTION_OUTSIDE) {
                    Log.d(TAG, "点击了外部区域");
                    stopTimetask();
                    showWindow(true);

                    // 恢复USB触控
                    closeUSBTouch(false);
                }

                return true;
            }

            // 搜台过程中，点击失效
            if (AppUtils.isChannetuning(getContext())) {
                return false;
            }

            float x = event.getRawX();
            float y = event.getRawY();

            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    downEvent(x, y);
                    break;

                case MotionEvent.ACTION_MOVE:
                    moveEvent(x, y);
                    break;

                case MotionEvent.ACTION_UP:
                    upEvent(x, y);
                    break;
                default:
                    break;
            }

            return true;
        }

        private void downEvent(float x, float y) {
            if (mMoveTag == 0) {
                mOldOffsetX = (int) x; // 偏移量
                mOldOffsetY = (int) y; // 偏移量
            }
            lastX = x;
            lastY = y;
            paramX = mParams.x;
            paramY = mParams.y;

//            tag("downEvent mMoveTag->%x, mOldOffsetX->%s, mOldOffsetY->%s", mMoveTag, mOldOffsetX, mOldOffsetY);
//			tag("downEvent, X->%s, Y->%s, paramX->%s, paramY->%s", x, y, paramX, paramY);
        }

        private void moveEvent(float x, float y) {
            int dx = (int) (x - lastX);
            int dy = (int) (y - lastY);

//            tag("moveEvent, X->%s, Y->%s, dx->%s，dy->%s", x, y, dx, dy);
            // 判断是否滑动
            if (mMoveTag == 0) {
                if (Math.abs(x - mOldOffsetX) >= 10 || Math.abs(y - mOldOffsetY) >= 10) {
                    Log.d(TAG, "mMoveTag is 1 ,start move");
                    mMoveTag = 1; // 开始滑动
                    // 关闭USB触控
                    closeUSBTouch(true);
                }
            }

            // 滑动处理
            if (mMoveTag == 1) {
                mParams.x = paramX + dx;
                mParams.y = paramY + dy;

                // 更新悬浮窗位置
                FloatWindowManager.updateTouchWindow(getContext());

//                lastX = x;
//                lastY = y;
            }
        }

        private void upEvent(float x, float y) {
            int newOffsetX = mParams.x;
            int newOffsetY = mParams.y;

//            tag("downEvent, X->%s, Y->%s", x, y);
            if (mMoveTag == 1) {
                Log.d(TAG, "upEvent mMoveTag is 1 ,stop move");
                // 恢复了USB触控
                closeUSBTouch(false);
                mMoveTag = 0;
            } else {
                Log.d(TAG, "upEvent mMoveTag is 0");
//                if(Math.abs(newOffsetX - mOldOffsetX) < 20 || Math.abs(newOffsetY - mOldOffsetY) < 20){
                // 显示大图标
                showWindow(false);

                // 启动监听
                startTimetask();

                // 关闭USB触控
                closeUSBTouch(true);
//                }
            }
        }
    };

    /**
     * 设置参数
     *
     * @param menuParams
     */
    public void setParams(WindowManager.LayoutParams menuParams) {
        this.mParams = menuParams;
    }


    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<TouchButtonLayout> weakReference;

        public UIHandler(TouchButtonLayout downloadingLayout) {
            super();
            this.weakReference = new WeakReference<>(downloadingLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TouchButtonLayout layout = weakReference.get();

            if (layout == null) {
                return;
            }
        }
    }

    /**
     * 跳转到更多设置界面
     */
    private void gotoMoreSettings() {
        Intent intent = new Intent("com.cultraview.settings.CTVSETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            getContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "未找到设置界面",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 初始化对话框UI
     */
    private void initSelectDialog() {
        dialogView = new SelectAppDialog(getContext());

        selectDialog = new Dialog(this.getContext(), R.style.dialog);
        selectDialog.setContentView(dialogView);
        selectDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        selectDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        selectDialog.setCanceledOnTouchOutside(true);

        // 设置对话框的大小
        Window dialogWindow = selectDialog.getWindow();

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = ScreenUtils.dip2px(this.getContext(), 180); // 宽度
        lp.height = ScreenUtils.dip2px(this.getContext(), 300); // 高度

        dialogWindow.setAttributes(lp);
        dialogView.setCallback(mCallback);
    }

    /**
     * 设置自定义应用
     */
    private void setUserAPPShow() {
        String packageName = (String) SPUtil.getData(getContext(), Constants.Companion.getUSERED_PACKAGE_NAME(), "");
        // 若是没有选择APP，则弹出选择对话框;反之，则启动APP
        if (TextUtils.isEmpty(packageName)) {
            initSelectDialog();
            selectDialog.show();
        } else {
            // 启动APP
            apkInfoUtils.startApp(getContext(), packageName);
        }
    }

    private void removeApp() {
        SPUtil.saveData(getContext(), Constants.Companion.getUSERED_PACKAGE_NAME(), "");
    }

    private void tag(String format, Object... strs) {
        String msg = String.format(format, strs);
        Log.d(TAG, msg);
    }

    private OnLongClickListener mOnLongClickListener = (v) -> {
        // 搜台过程中，点击失效
        if (AppUtils.isChannetuning(getContext())) {
            return false;
        }

        int id = v.getId();
        switch (id) {
            case R.id.btn_usered: {// 自定义
                String packageName = (String) SPUtil.getData(getContext(), Constants.Companion.getUSERED_PACKAGE_NAME(), "");
                if (!TextUtils.isEmpty(packageName)) {
                    userFlag = true;
                    btnUserHide.setVisibility(VISIBLE);
                }
                break;
            }
        }

        return true;
    };

    /**
     * 选择应用的监听回调
     */
    SelectAppDialog.Callback mCallback = (appInfo) -> {
        if (selectDialog.isShowing()) {
            selectDialog.dismiss();
        }

        if (appInfo != null) {
            SPUtil.saveData(getContext(), Constants.Companion.getUSERED_PACKAGE_NAME(), appInfo.getPackName());
            ((ImageView) btnUsered).setImageDrawable(appInfo.getAppIcon());
        }
    };

    /**
     * 点击事件监听
     */
    private OnClickListener mOnClickListener = (v) -> {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }

        // 搜台过程中，点击失效
        if (AppUtils.isChannetuning(getContext())) {
            return;
        }

        Log.d(TAG, "MenuLayout mOnClickListener");

        int id = v.getId();
        switch (id) {
            case R.id.btn_back: { // 返回
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_BACK);
                break;
            }
            case R.id.btn_home: { // 回到主页
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);
                // 发送SOURCE广播
                AppUtils.noticeChangeSignal(getContext(), 34);
                break;
            }
            case R.id.btn_switch: {// 多任务列表
                // 任务界面
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_APP_SWITCH);
                // 发送SOURCE广播
                AppUtils.noticeChangeSignal(getContext(), 34);
                break;
            }
            case R.id.btn_setting: {// 显示更多设置界面
                if (AppUtils.isActivityRunning(getContext(), "com.android.systemui.recents.RecentsActivity")) {
                    // 任务界面
                    AppUtils.keyEventBySystem(KeyEvent.KEYCODE_APP_SWITCH);

                    mHandler.postDelayed(() -> {
                        gotoMoreSettings();
                    }, 500);
                } else {
                    gotoMoreSettings();
                }
                break;
            }
            case R.id.btn_pan: {// 打开批注
                Intent fsIntent = new Intent("android.intent.action.open.annotation");
                fsIntent.setComponent(new ComponentName("com.ctv.annotation", "com.ctv.annotation.AnnotationService"));
                fsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startService(fsIntent);

                break;
            }
            case R.id.btn_user_hide_tips:
            case R.id.btn_usered: {// 自定义
                if (!userFlag) {
                    setUserAPPShow();
                } else {
                    userFlag = false;
                    removeApp();
                    btnUserHide.setVisibility(GONE);
                    btnUsered.setImageResource(R.drawable.btn_user_defined_normal);
                }

                break;
            }
            case R.id.btn_center: {// 点击中心，显示小图标

                break;
            }
            default:
                break;
        }

        showWindow(true);
        // 恢复USB触控
        closeUSBTouch(false);

        stopTimetask();
    };

    /**
     * 关闭USB触控
     *
     * @param isCloseUSBTouch
     */
    private void closeUSBTouch(boolean isCloseUSBTouch) {
        if (isCloseUSBTouch) { // 关闭USB触控
            Log.d(TAG, "关闭USB触控");
            this.isCloseUSBTouch = true;
            AppUtils.changeUSBTouch(getContext(), false);
        } else { // 恢复USB触控
            if (this.isCloseUSBTouch) {
                Log.d(TAG, "恢复USB触控");
                this.isCloseUSBTouch = false;
                AppUtils.changeUSBTouch(getContext(), true);
            }
        }

    }

    /**
     * 定时监听任务
     */
    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            endTime = System.currentTimeMillis();
            if (endTime - startTime > 1000 * 3) {
                // 停止资任务
                stopTimetask();

                // 恢复USB触控
                closeUSBTouch(false);
                mHandler.postDelayed(() -> {
                    showWindow(true);
                }, 50);
            }
        }
    }
}
