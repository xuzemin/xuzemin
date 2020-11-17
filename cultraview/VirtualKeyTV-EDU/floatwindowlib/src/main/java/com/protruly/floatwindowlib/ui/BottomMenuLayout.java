package com.protruly.floatwindowlib.ui;

import android.app.Dialog;
import android.app.Instrumentation;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.MstarConst;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.yinghe.whiteboardlib.utils.ViewUtils;

import java.lang.ref.WeakReference;

import static com.protruly.floatwindowlib.service.FloatWindowService.BOTTOM_MENU_HIDE_DELAY;
import static com.protruly.floatwindowlib.service.FloatWindowService.MENU_ACTION_HIDE_REMOVE;

/**
 * 底部控制栏
 *
 * @author wanghang
 * @time 2018/8/24
 */
public class BottomMenuLayout extends FrameLayout implements View.OnLongClickListener {
    private final static String TAG = BottomMenuLayout.class.getSimpleName();
    private Context mContext;
    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private View rootView;
    View rlBtnBack;// 返回
    View rlBtnHome;// 回到主页
    View rlBtnAppSwitch;// 多功能切换键
    View rlSource; // 输入源
    View rlBtnShowSetting;// 显示设置

    View rlCloseHalfScreen; // 1/3屏
    ImageView btnHalfScreen;
    TextView txtHalfScreen;

    View rlVolumeSub;// 音量-
    View rlVolumePlus;// 音量+
    View rlBacklightPlus;// 亮度-
    View rlBacklightSub;// 亮度+

    View rlBtnWhiteBoard;// 白板
    View rlBtnComment;// 批注
    View rlBtnShortCut;// 截图
    //View shortcutDividerRight;// 截图
    View rlUserDefined; //用户自定义

    // 对话框UI
    Dialog dialog;
    TextView mTitle;
    TextView mMessage;
    EditText mEditText;
    Button mConfirm; //确定按钮
    Button mCancel; //取消按钮

    Animation showAnim;
    Animation hideAnim;

    public static Handler mHandler;

    // 对话框UI
    private int shortSource = 25;

    /**
     * 默认0隐藏，1隐藏中，2隐藏，3显示过程中，4显示
     */
    public static volatile int MENU_STATUS = CommConst.MENU_STATUS_HIDDEN;

    public static volatile long showMenuStart = 0;
    public static volatile long hideMenuStart = 0;
    private boolean isVolSubLongPressed = false;
    private boolean isVolPLULongPressed = false;


    public BottomMenuLayout(@NonNull Context context) {
        super(context);
        this.mContext = context;
        MENU_STATUS = CommConst.MENU_STATUS_HIDDEN;
        init(context);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    View.OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_OUTSIDE) { // 点击了外部区域
                Log.d(TAG, "点击了外部区域");

                // 显示计时器在0.5s至4s内时
                if ((checkInShowPeriod() == 2)
                        && MENU_STATUS == CommConst.MENU_STATUS_SHOWED) { // 若不是收缩过程中
                    hideMenuStart = showMenuStart = 0;
                    setOnTouchListener(null);
                    if (FloatWindowService.mDataHandler != null) {
                        FloatWindowService.mDataHandler.sendEmptyMessageDelayed(
                                FloatWindowService.BOTTOM_MENU_HIDE_IMMEDIATE, 50);
                        MENU_STATUS = CommConst.MENU_STATUS_HIDING;
                    }
                }
            }
            return false;
        }
    };

    /**
     * 检测在显示菜单时时间范围在0.5s至4s
     *
     * @return
     */
    private int checkInShowPeriod() {
        // 显示计时器在0.5s至4s内时
        hideMenuStart = System.currentTimeMillis();
        int periodType = 0;
        long dotal = hideMenuStart - showMenuStart;

        if (dotal <= 500) { // 0.5s已内
            periodType = 1;
        } else if (dotal <= 4000) { // 0.5s-4.2s
            periodType = 2;
        } else { //  // 4s-5s
            periodType = 3;
        }

        return periodType;
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.bottom_menu, this);
        mHandler = new BottomMenuLayout.UIHandler(this);
        setOnTouchListener(mOnTouchListener);

        initView();

        initDialogView();

        setListener();

    }

    public void releaseData() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 初始化UI
     */
    private void initView() {
        rootView = findViewById(R.id.root_menu);

        viewWidth = rootView.getLayoutParams().width;
        viewHeight = rootView.getLayoutParams().height;

        rlBtnBack = findViewById(R.id.rl_btn_back);
        rlBtnHome = findViewById(R.id.rl_btn_home);
        rlBtnAppSwitch = findViewById(R.id.rl_btn_app_switch);
        rlSource = findViewById(R.id.rl_btn_source);
        rlBtnShowSetting = findViewById(R.id.rl_btn_show_setting);


        rlVolumeSub = findViewById(R.id.rl_btn_volume_sub);
        rlVolumePlus = findViewById(R.id.rl_btn_volume_plus);
        rlBacklightSub = findViewById(R.id.rl_btn_backlight_sub);
        rlBacklightPlus = findViewById(R.id.rl_btn_backlight_plus);

        rlBtnWhiteBoard = findViewById(R.id.rl_btn_whiteboard);
        rlBtnComment = findViewById(R.id.rl_btn_comment);
        rlBtnShortCut = findViewById(R.id.rl_btn_shortcut);

        rlUserDefined = findViewById(R.id.rl_btn_user_defined);
//		rlOSDMenu = findViewById(R.id.rl_btn_osd_menu);

        rlCloseHalfScreen = findViewById(R.id.rl_btn_half_screen);

        btnHalfScreen = (ImageView) findViewById(R.id.btn_half_screen);
        txtHalfScreen = (TextView) findViewById(R.id.txt_half_screen);

        // 添加menu菜单隐藏和显示的动画
        int inAnimResId = R.anim.up_in;
        int outAnimResId = R.anim.down_out;
        showAnim = AnimationUtils.loadAnimation(getContext(), inAnimResId);
        showAnim.setAnimationListener(showAnimationListener);
        hideAnim = AnimationUtils.loadAnimation(getContext(), outAnimResId);
        hideAnim.setAnimationListener(hideAnimationListener);
    }

    /**
     * 初始化对话框UI
     */
    private void initDialogView() {
        View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.update_dialog_layout, null);
        mTitle = (TextView) dialogView.findViewById(R.id.title);
        mEditText = (EditText) dialogView.findViewById(R.id.message_edit);
        mMessage = (TextView) dialogView.findViewById(R.id.message);
        mConfirm = (Button) dialogView.findViewById(R.id.positiveButton);
        mCancel = (Button) dialogView.findViewById(R.id.negativeButton);

        dialog = new Dialog(this.getContext(), R.style.DialogStyle);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(true);

        // 设置对话框的大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = ScreenUtils.dip2px(this.getContext(), 400); // 宽度
        lp.height = ScreenUtils.dip2px(this.getContext(), 300); // 高度

        dialogWindow.setAttributes(lp);
    }

    /**
     * 设置监听
     */
    private void setListener() {
        rlBtnBack.setOnClickListener(mOnClickListener);
        rlBtnHome.setOnClickListener(mOnClickListener);
        rlBtnAppSwitch.setOnClickListener(mOnClickListener);
        rlBtnShowSetting.setOnClickListener(mOnClickListener);
        rlSource.setOnClickListener(mOnClickListener);

        rlVolumeSub.setOnClickListener(mOnClickListener);
        rlVolumePlus.setOnClickListener(mOnClickListener);
        rlBacklightSub.setOnClickListener(mOnClickListener);
        rlBacklightPlus.setOnClickListener(mOnClickListener);

        rlBtnWhiteBoard.setOnClickListener(mOnClickListener);
        rlBtnComment.setOnClickListener(mOnClickListener);
        rlBtnShortCut.setOnClickListener(mOnClickListener);
        rlUserDefined.setOnClickListener(mOnClickListener);

        rlCloseHalfScreen.setOnClickListener(mOnClickListener);

    }

    /**
     * 设置监听
     */
    private void setListener(boolean isActive) {
        Log.d(TAG, "isCanClick->" + isActive);
        rlBtnBack.setClickable(isActive);
        rlBtnHome.setClickable(isActive);
        rlBtnAppSwitch.setClickable(isActive);
        rlBtnShowSetting.setClickable(isActive);
        rlSource.setClickable(isActive);

//        rlVolumeSub.setClickable(isActive);
//        rlVolumePlus.setClickable(isActive);
//        rlBacklightSub.setClickable(isActive);
//        rlBacklightPlus.setClickable(isActive);

        rlBtnWhiteBoard.setClickable(isActive);
        rlBtnComment.setClickable(isActive);
        rlBtnShortCut.setClickable(isActive);
        rlUserDefined.setClickable(isActive);

        rlCloseHalfScreen.setClickable(isActive);
    }

    /**
     * 设置监听
     */
    private void setVolBackLightClickable(boolean isActive) {
        Log.d(TAG, "isCanClick->" + isActive);
        rlVolumeSub.setClickable(isActive);
        rlVolumePlus.setClickable(isActive);
        rlBacklightSub.setClickable(isActive);
        rlBacklightPlus.setClickable(isActive);
    }

    /**
     * 仅仅显示菜单
     */
    public void justShowMenu() {
        SystemProperties.set("BOTTOM_MENU_STATUS", "1"); // 底部菜单状态 0:隐藏， 1:显示
        this.setVisibility(View.VISIBLE);
        rootView.setVisibility(View.VISIBLE);
        MENU_STATUS = CommConst.MENU_STATUS_SHOWED;
    }

    /**
     * 显示菜单
     */
    public void showMenu() {
        if (rootView.getVisibility() != View.VISIBLE) {
            showMenuStart = System.currentTimeMillis();
            hideMenuStart = showMenuStart;

            MENU_STATUS = CommConst.MENU_STATUS_SHOWING;
            CmdUtils.changeUSBTouch(this.getContext().getApplicationContext(), false); // 关闭USB触控

            // 在云视听下，批注不显示
            int shortcutVisible = View.VISIBLE;
            if (AppUtils.isTopRunning(getContext(), "com.ktcp.video")) {
                shortcutVisible = View.INVISIBLE;
            }

            rlBtnShortCut.setVisibility(shortcutVisible);
//            shortcutDividerRight.setVisibility(shortcutVisible);
//			rlOSDMenu.setVisibility(visible);
            if (!"JPE".equals(AppUtils.client)) {
                int visible = View.INVISIBLE;
                if (AppUtils.isTvMenuShow(getContext())) {
                    visible = View.VISIBLE;
                    //closeUSBTouch = true;
                    //CmdUtils.changeUSBTouch(getContext(), false); // 关闭USB触控
                }
                //	rlOSDMenu.setVisibility(visible);
                //rlCloseHalfScreen.setVisibility(visible);
            }


            this.setVisibility(View.VISIBLE);
            rootView.setVisibility(View.VISIBLE);
            switchHalfScreen(false);
//            rootView.clearAnimation();
//            rootView.startAnimation(showAnim);
            MENU_STATUS = CommConst.MENU_STATUS_SHOWED;
            BottomMenuLayout.this.setListener(true);
            setOnTouchListener(mOnTouchListener);
            SystemProperties.set("BOTTOM_MENU_STATUS", "1"); // 底部菜单状态 0:隐藏， 1:显示
            Log.d(TAG, "showAnimation  onAnimationEnd MENU_STATUS:" + MENU_STATUS);

            // updateSignalText();
            Log.d(TAG, "showMenu MENU_STATUS:" + MENU_STATUS);
        }
    }

    /**
     * 隐藏菜单
     */
    public void justHideMenu() {
        SystemProperties.set("BOTTOM_MENU_STATUS", "0"); // 底部菜单状态 0:隐藏， 1:显示
        rootView.setVisibility(View.GONE);
        this.setVisibility(View.GONE);
        MENU_STATUS = CommConst.MENU_STATUS_HIDDEN;
        hideMenuStart = showMenuStart = 0;
    }

    /**
     * 隐藏菜单
     */
    public void hideMenu() {
        if (rootView.getVisibility() == View.VISIBLE) {
            hideMenuStart = showMenuStart = 0;

            CmdUtils.changeUSBTouch(getContext().getApplicationContext(), true);

            // 取消触摸外部监听
//			setOnTouchListener(null);
//			MENU_STATUS = CommConst.MENU_STATUS_HIDING;
////			SystemProperties.set("BOTTOM_MENU_STATUS", "0"); // 底部菜单状态 0:隐藏， 1:显示
//
//			rootView.clearAnimation();
//			rootView.startAnimation(hideAnim);

            setOnTouchListener(null);
            rootView.setVisibility(View.GONE);
            BottomMenuLayout.this.setVisibility(View.GONE);
            SystemProperties.set("BOTTOM_MENU_STATUS", "0"); // 底部菜单状态 0:隐藏， 1:显示
            MENU_STATUS = CommConst.MENU_STATUS_HIDDEN;

            Log.d(TAG, "hideMenu MENU_STATUS:" + MENU_STATUS);
        }
    }

    /**
     * 显示用户自定义信号源对话框
     */
    private void showUserDefined() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        } else {
            mEditText.setVisibility(View.VISIBLE);

            // 获得当前source
//            int inputSource = AppUtils.getCurrentSource(getContext());


//			Log.d(TAG, "显示用户自定义对话框, getCurrentSource->" + inputSource);

            SignalInfo signalInfo = DBHelper.querySourceInfo(getContext());
            mEditText.setText(signalInfo.getName());
            mEditText.setSelection(signalInfo.getName().length());//将光标移至文字末尾
            mEditText.setFilters(new InputFilter[]{new MaxTextLengthFilter(11)});
            mTitle.setVisibility(View.VISIBLE);
            mTitle.setText(R.string.title_tip_user_defined);

            mConfirm.setOnClickListener(v -> {
                dialog.dismiss();
                String text = mEditText.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    updateSourceName(signalInfo.getSourceId(), text);
                }
            });

            mCancel.setOnClickListener(v -> {
                Log.d(TAG, "取消修改..");
                dialog.dismiss();
            });

            showDialog(dialog);
        }
    }

    /**
     * 修改source名称
     *
     * @param inputSource
     * @param sourceName
     */
    public void updateSourceName(int inputSource, String sourceName) {
        Log.d(TAG, "inputSource->" + inputSource + " sourceName->" + sourceName);

        ContentResolver contentResolver = getContext().getContentResolver();
        String uriString = "content://com.cultraview.ctvmenu/sourcename/update";
        Uri uri = Uri
                .parse(uriString);
        ContentValues values = new ContentValues();
        values.put("editName", sourceName);
        String where = "source_id=?";
        String[] where_args = {inputSource + ""};
        contentResolver.update(uri, values, where, where_args);
    }

    /**
     * 显示对话框
     *
     * @param dialog
     */
    private void showDialog(Dialog dialog) {
        dialog.setOnDismissListener((d) -> {
            Log.d(TAG, "setOnDismissListener");
        });
        dialog.show();
    }

    /**
     * 切换半屏开关
     *
     * @param isChange
     */
    public void switchHalfScreen(boolean isChange) {
        int isHalfScreen = AppUtils.getHalfScreenStatus();
        LogUtils.d("switchHalfScreen old isHalfScreen->" + isHalfScreen);
        int resId = (isHalfScreen == 1) ? R.mipmap.screen_big : R.mipmap.screen_small;
        btnHalfScreen.setImageResource(resId);
        int txtId = (isHalfScreen == 1) ? R.string.screen_restore : R.string.screen_drop_down;
        txtHalfScreen.setText(getResources().getString(txtId));

        if (isChange) { // 切换
            isHalfScreen = (isHalfScreen == 0) ? 1 : 0;
            LogUtils.d("switchHalfScreen new isHalfScreen->" + isHalfScreen);
            // 设置半屏
            AppUtils.setPipscale(isHalfScreen == 1);
        }
    }

    /**
     * 点击事件监听
     */
    private OnClickListener mOnClickListener = v -> {
        if (ViewUtils.isFastDoubleClick()) {
            return;
        }

        // 显示计时器在0.5s至4s内时
        setOnTouchListener(null);
        MENU_STATUS = CommConst.MENU_STATUS_HIDING;
//        setListener(false);
//        setVolBackLightClickable(true);
        int id = v.getId();

        if (id != R.id.rl_btn_show_setting) {
            MyUtils.closeSettingActivity();
        }


        switch (id) {
            case R.id.rl_btn_back: { // 返回
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_BACK);
                break;
            }
            case R.id.rl_btn_home: { // 回到主页
/*                if("JPE".equals(AppUtils.client)){
				// 关闭半屏
				AppUtils.closeHalfScreen();
				}*/
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);
                //AppUtils.setInputSourceAndroid();
                // 发送SOURCE广播
                AppUtils.noticeChangeSignal(getContext(), -1);
                break;
            }
            case R.id.rl_btn_app_switch: {// 多任务列表
                if ("JPE".equals(AppUtils.client)) {
                    // 关闭半屏
                    AppUtils.closeHalfScreen();
                }
                // 任务界面
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_APP_SWITCH);
                // 发送SOURCE广播
                AppUtils.noticeChangeSignal(getContext(), -1);
                break;
            }
            case R.id.rl_btn_show_setting: {// 显示更多设置界面
                MyUtils.openSettingActivity(getContext());
                break;
            }
            case R.id.rl_btn_whiteboard: {// 白板显示
                String mPackageName = "com.mphotool.whiteboard";
                String mActivityName = "com.mphotool.whiteboard.activity.MainActivity";
                AppUtils.gotoOtherApp(getContext(), mPackageName, mActivityName);
                break;
            }
            case R.id.rl_btn_comment: {// 批注显示
                AppUtils.showComment(this.getContext().getApplicationContext());

//				// 在4s以内，直接收缩
//				if ((checkInShowPeriod() <= 2) && FloatWindowService.mDataHandler != null){
//					hideMenuStart = showMenuStart = 0;
//					FloatWindowService.mDataHandler.sendEmptyMessageDelayed(
//							FloatWindowService.BOTTOM_MENU_HIDE_IMMEDIATE, 1);
//				}
                break;
            }
            case R.id.rl_btn_shortcut: { //截图
                AppUtils.showScreenshot(this.getContext().getApplicationContext());

//				// 在4s以内，直接收缩
//				if ((checkInShowPeriod() <= 2) && FloatWindowService.mDataHandler != null){
//					hideMenuStart = showMenuStart = 0;
//					FloatWindowService.mDataHandler.sendEmptyMessageDelayed(
//							FloatWindowService.BOTTOM_MENU_HIDE_IMMEDIATE, 1);
//				}
                break;
            }
            case R.id.rl_btn_source: {// 输入源
                //AppUtils.keyEventBySystem(KeyEvent.KEYCODE_TV_INPUT);
                Intent fsIntent = new Intent();
                fsIntent.setComponent(new ComponentName("com.ctv.sourcemenu",
                        "com.ctv.sourcemenu.MainActivity"));
                mContext.startActivity(fsIntent);
                break;
            }
            case R.id.rl_btn_volume_sub: {// 音量-
                Log.d(TAG, "rl_btn_volume_sub remove backlight");
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_VOLUME_DOWN);
                FloatWindowManager.removeBacklightLayout(getContext());
                break;
            }
            case R.id.rl_btn_volume_plus: {// 音量+
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_VOLUME_UP);
                FloatWindowManager.removeBacklightLayout(getContext());
                break;
            }
            case R.id.rl_btn_backlight_sub: {// 亮度-
                LogUtils.d("Backlight sub");
                BacklightLayout backlightLayout = FloatWindowManager.createBacklightLayout(getContext());
                int lightNum = AppUtils.getBacklight() - 1;
                if (lightNum < 0) {
                    lightNum = 0;
                } else {
                    AppUtils.setBacklight(lightNum);
                }
                backlightLayout.refreshBacklight(lightNum);
                backlightLayout.setPostVisibility();
                if (BacklightLayout.mDataHandler != null) {
                    BacklightLayout.mDataHandler.sendEmptyMessageDelayed(2, 200);
                }

                getContext().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)); //关闭音量Dialog
                break;
            }
            case R.id.rl_btn_backlight_plus: {// 亮度+
                LogUtils.d("Backlight plus");
                BacklightLayout backlightLayout = FloatWindowManager.createBacklightLayout(getContext());
                int lightNum = AppUtils.getBacklight() + 1;
                if (lightNum > 100) {
                    lightNum = 100;
                } else {
                    AppUtils.setBacklight(lightNum);
                }

                backlightLayout.refreshBacklight(lightNum);
                backlightLayout.setPostVisibility();
                if (BacklightLayout.mDataHandler != null) {
                    BacklightLayout.mDataHandler.sendEmptyMessageDelayed(2, 200);
                }
                getContext().sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));//关闭音量Dialog
                break;
            }
            case R.id.rl_btn_user_defined: {// 自定义信号源
//				MyUtils.showSignalUpdate(getContext());
                showUserDefined();
                break;
            }
            case R.id.rl_btn_half_screen: {// 半屏
                switchHalfScreen(true);
                break;
            }
            default: {
                break;
            }
        }


//        if (id == R.id.rl_btn_volume_sub || id == R.id.rl_btn_volume_plus ||
//                id == R.id.rl_btn_backlight_plus || id == R.id.rl_btn_backlight_sub) {
//            // 延时5秒
//            Log.d(TAG, "sendEmptyMessageDelayed BOTTOM_MENU_HIDE_DELAY");
//            FloatWindowService.mDataHandler.sendEmptyMessage(MENU_ACTION_HIDE_REMOVE);
//            FloatWindowService.mDataHandler.sendEmptyMessageDelayed(BOTTOM_MENU_HIDE_DELAY, FloatWindowService.hideTime);
//        } else {
            // 在4s以内，直接收缩 声音 亮度不隐藏
            if ((checkInShowPeriod() <= 2)) {
                hideMenuStart = showMenuStart = 0;
                if (FloatWindowService.mDataHandler != null) {
                    FloatWindowService.mDataHandler.sendEmptyMessage(
                            FloatWindowService.BOTTOM_MENU_HIDE_IMMEDIATE);
                }
            }
//        }

    };

    /**
     * 隐藏的动画
     */
    private Animation.AnimationListener hideAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            MENU_STATUS = CommConst.MENU_STATUS_HIDING;
            setOnTouchListener(null);
            Log.d(TAG, "hideAnimation  onAnimationStart MENU_STATUS:" + MENU_STATUS);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            rootView.setVisibility(View.GONE);
            BottomMenuLayout.this.setVisibility(View.GONE);
            SystemProperties.set("BOTTOM_MENU_STATUS", "0"); // 底部菜单状态 0:隐藏， 1:显示
            MENU_STATUS = CommConst.MENU_STATUS_HIDDEN;
            Log.d(TAG, "hideAnimation  onAnimationEnd MENU_STATUS:" + MENU_STATUS);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    /**
     * 显示的动画
     */
    private Animation.AnimationListener showAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            MENU_STATUS = CommConst.MENU_STATUS_SHOWING;
            Log.d(TAG, "showAnimation  onAnimationStart MENU_STATUS:" + MENU_STATUS);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            MENU_STATUS = CommConst.MENU_STATUS_SHOWED;
            BottomMenuLayout.this.setListener(true);
            setOnTouchListener(mOnTouchListener);
            SystemProperties.set("BOTTOM_MENU_STATUS", "1"); // 底部菜单状态 0:隐藏， 1:显示
            Log.d(TAG, "showAnimation  onAnimationEnd MENU_STATUS:" + MENU_STATUS);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.rl_btn_volume_plus:
                Log.d(TAG, "onLongClick rl_btn_volume_plus");
                return true;
            case R.id.rl_btn_volume_sub:
                Log.d(TAG, "onLongClick rl_btn_volume_sub");
                return true;
            case R.id.rl_btn_backlight_plus:
                Log.d(TAG, "onLongClick rl_btn_backlight_plus");
                return true;
            case R.id.rl_btn_backlight_sub:
                Log.d(TAG, "onLongClick rl_btn_backlight_sub");
                return true;
        }
        return false;
    }

    class MaxTextLengthFilter implements InputFilter {
        private int mMaxLength;
        private Toast toast;

        public MaxTextLengthFilter(int max) {
            mMaxLength = max - 1;
            toast = Toast.makeText(getContext(), getContext().getResources().getString(R.string.tip_max_text, "" + mMaxLength), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 300);
        }

        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int keep = mMaxLength - (dest.length() - (dend - dstart));
            if (keep < (end - start)) {
                toast.show();
            }
            if (keep <= 0) {
                return "";
            } else if (keep >= end - start) {
                return null;
            } else {
                return source.subSequence(start, start + keep);
            }
        }
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<BottomMenuLayout> weakReference;

        public UIHandler(BottomMenuLayout activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            BottomMenuLayout menuLayout = weakReference.get();
            if (menuLayout == null) {
                return;
            }
        }
    }
}
