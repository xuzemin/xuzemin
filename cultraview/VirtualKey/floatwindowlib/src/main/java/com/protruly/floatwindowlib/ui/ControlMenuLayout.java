package com.protruly.floatwindowlib.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.activity.PictureChangeActivity;
import com.protruly.floatwindowlib.control.ActivityCollector;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.R;
import com.yinghe.whiteboardlib.utils.ACache;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.BitmapUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.DrawConsts;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.yinghe.whiteboardlib.utils.TimeUtils;
import com.yinghe.whiteboardlib.utils.ViewUtils;

import java.io.File;
import java.lang.ref.WeakReference;


/**
 * 控制菜单布局
 *
 * @author wang
 * @time on 2017/3/17.
 */
public class ControlMenuLayout extends FrameLayout {
    private static final String TAG = ControlMenuLayout.class.getSimpleName();

    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private boolean isRight = false;// 控制菜单的位置:true为右边菜单，false为左边菜单
    private boolean isShrink = true;// 判断菜单是否收缩

    private View rootView;
    public RelativeLayout menuLayout;

    View rlBtnArrow;// 箭头按钮的父布局
    ImageView btnArrow;// 箭头按钮:控制菜单显示和隐藏
//    ImageView btnArrowShrink;// 箭头按钮隐藏

    View btnBack;// 返回
    View btnHome;// 回到主页
    View btnAppSwitch;// 多功能切换键
    View btnShowSetting;// 显示设置
    View rlBtnWhiteBoard;//白板按钮的父布局
    View btnShowSignal;// 信号源按钮

    Animation animHide;
    Animation animShow;

    private Context mContext;

    public static Handler mHandler;

    private ACache mACache;

    private float xInScreen;// 记录当前手指位置在屏幕上的横坐标值
    private float yInScreen;// 记录当前手指位置在屏幕上的纵坐标值
    private float xDownInScreen;// 记录手指按下时在屏幕上的横坐标的值
    private float yDownInScreen;// 记录手指按下时在屏幕上的纵坐标的值
    private float xInView;// 记录手指按下时在小悬浮窗的View上的横坐标的值
    private float yInView;// 记录手指按下时在小悬浮窗的View上的纵坐标的值

    private static boolean isMenuMove = true;// 菜单可以移动的标识
    private final static float MIN_CLICK_SCALE = 0.05f;
    private final static float MAN_CLICK_SCALE = 0.99f;

    BitmapDrawable defaultBG = new BitmapDrawable();// 空背景
    private NewSignalDialogLayout newSignalDialogLayout;
    private Dialog signaltDialog;


    public ControlMenuLayout(Context context, boolean isRight) {
	    super(context);
	    this.isRight = isRight;
        this.mContext=context;
	    int layout = R.layout.left_btn_menu;
	    if (isRight){ // 右边
		    layout = R.layout.right_btn_menu;
	    }
	    LayoutInflater.from(context).inflate(layout, this);

	    mACache = ACache.get(context);
	    mHandler = new UIHandler(this);

	    initView();
	    setListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 初始化UI
     */
    private void initView() {
        rootView = findViewById(R.id.root_right_menu);
        viewWidth = rootView.getLayoutParams().width;
        viewHeight = rootView.getLayoutParams().height;
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        // 箭头按钮
        rlBtnArrow = findViewById(R.id.rl_btn_arrow);
        this.setOnTouchListener(new View.OnTouchListener(){
            int paramY;
            float  lastY;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float y = event.getRawY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        lastY = y;
                        //获取当前按下的坐标

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获取移动后的坐标
                        int dy = (int) (y - lastY);
                        Log.i("gyx","dy="+dy);
                        FloatWindowManager.updateMenuLeftParams(mContext,dy);
                        FloatWindowManager.updateMenuParams(mContext,dy);
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("打印操作：", "抬起了");
                        FloatWindowManager.updateDy();
                        break;
                }
                return true;
            }
        });
//        rlBtnArrow.setBackground(defaultBG);
        btnArrow = (ImageView) findViewById(R.id.btn_arrow);
        if (isRight) { // 右边的控制菜单
            btnArrow.setImageResource(R.drawable.btn_arrow_left_normal);
        } else { // 左边的控制菜单
            btnArrow.setImageResource(R.drawable.btn_arrow_right_normal);
        }
//        btnArrowShrink = (ImageView) findViewById(R.id.btn_arrow_shrink);
////        btnArrowShrink.setVisibility(View.VISIBLE);
//        if (isRight) { // 右边
//            btnArrowShrink.setImageResource(R.drawable.btn_arrow_left_normal);
//        } else { // 左边
//            btnArrowShrink.setImageResource(R.drawable.btn_arrow_right_normal);
//        }

        // 菜单按钮
        menuLayout = (RelativeLayout) findViewById(R.id.menu_layout);

        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btn_home);
        btnAppSwitch = findViewById(R.id.btn_app_switch);
        btnShowSetting = findViewById(R.id.btn_show_setting);

        rlBtnWhiteBoard = findViewById(R.id.btn_whiteboard);
        btnShowSignal = findViewById(R.id.btn_signal);

        // 添加menu菜单隐藏和显示的动画
        // 添加menu菜单隐藏和显示的动画
        int inAnimResId =  R.anim.left_in;
        int outAnimResId =  R.anim.left_out;
        if (isRight){ // 右边
            inAnimResId =  R.anim.right_in;
            outAnimResId =  R.anim.right_out;
        }
        animShow = AnimationUtils.loadAnimation(getContext(), inAnimResId);
//        animShow.setAnimationListener(showAnimationListener);

        animHide = AnimationUtils.loadAnimation(getContext(), outAnimResId);
        animHide.setAnimationListener(hideAnimationListener);

    }




    /**
     * 设置监听
     */
    private void setListener() {
        btnArrow.setOnClickListener(mOnClickListener);
//        btnArrowShrink.setOnClickListener(mOnClickListener);
        btnBack.setOnClickListener(mOnClickListener);
        btnHome.setOnClickListener(mOnClickListener);
        btnAppSwitch.setOnClickListener(mOnClickListener);
        btnShowSetting.setOnClickListener(mOnClickListener);
        rlBtnWhiteBoard.setOnClickListener(mOnClickListener);
        btnShowSignal.setOnClickListener(mOnClickListener);
    }

    /**
     * 改变菜单栏
     */
    private void changeMenuView() {
        LogUtils.d("changeMenu isShrink:" + isShrink);
        if (!isShrink) { // 收起菜单
            shrinkMenu();
        } else {  // 展开菜单
            unfoldMenu();
        }
    }

    /**
     * 收缩菜单
     */
    public void shrinkMenu() {
        // 显示小图标
        if (isRight) { // 右边
            btnArrow.setImageResource(R.drawable.btn_arrow_left_normal);
        } else { // 左边
            btnArrow.setImageResource(R.drawable.btn_arrow_right_normal);
        }

	    if (!isShrink){ // 若是展开时，则收缩
		    isShrink = true;
		    if (menuLayout.getVisibility() == View.VISIBLE){
			    menuLayout.startAnimation(animHide);
		    }
	    }
    }

    /**
     * 展开菜单
     */
    public void unfoldMenu() {
        // 改变箭头按钮的状态
        rlBtnArrow.setBackgroundResource(R.drawable.dialog_bg_shape);
        if (isRight) { // 右边
            btnArrow.setImageResource(R.drawable.btn_arrow_right_normal);
        } else { // 左边
            btnArrow.setImageResource(R.drawable.btn_arrow_left_normal);
        }

	    if (isShrink){ // 若是收缩时，则展开
		    isShrink = false;
		    if (menuLayout.getVisibility() != View.VISIBLE){
			    menuLayout.setVisibility(View.VISIBLE);
			    menuLayout.startAnimation(animShow);
		    }
	    }

        // 一方展开，另一方收起
        if (isRight) { // 当前为右边菜单，则左边菜单收起
            if (!FloatWindowManager.getMenuWindowLeft().isShrink){
                FloatWindowManager.getMenuWindowLeft().shrinkMenu();
            }
        } else { // 当前为左边菜单，则右边菜单收起
            if (!FloatWindowManager.getMenuWindow().isShrink){
                FloatWindowManager.getMenuWindow().shrinkMenu();
            }
        }
    }

    /**
     * 改变背景
     * @param isHasBg
     */
    public void changeIndexBg(boolean isHasBg){
        if (isHasBg){
            rlBtnArrow.setBackgroundResource(R.drawable.dialog_bg_shape);
        } else {
            rlBtnArrow.setBackground(defaultBG);
        }
    }

    /**
     * 显示设置界面
     */
    public void showSetting() {
        MyUtils.openSettingActivity(getContext(), isRight);

        shrinkMenu();
        changeIndexBg(false);
    }

    /**
     * 显示信号源弹框
     */
    private void showSignal() {
        SignalDialogLayout signalDialog = FloatWindowManager.getSignalDialog();

        if (signalDialog == null){
            FloatWindowManager.createSignalDialog(getContext().getApplicationContext(), isRight);
        } else {
            int visible = signalDialog.getVisibility();
            if (visible != View.VISIBLE){
                visible = View.VISIBLE;
            } else {
                visible = View.GONE;
            }

            FloatWindowManager.updateSignalDialog(getContext(), isRight);
            signalDialog.setRightShow(isRight);
            signalDialog.setVisibility(visible);
        }
    }
    private void showNewSignal() {
        NewSignalDialogLayout newSignalDialog = FloatWindowManager.getNewSignalDialog();
        if (newSignalDialog == null){
            FloatWindowManager.createNewSignalDialog(getContext().getApplicationContext(), isRight);
        } else {
            int visible = newSignalDialog.getVisibility();
            if (visible != View.VISIBLE){
                visible = View.VISIBLE;
            } else {
                visible = View.GONE;
            }

            FloatWindowManager.updateNewSignalDialog(getContext(), isRight);
            newSignalDialog.setRightShow(isRight);
            newSignalDialog.setVisibility(visible);
        }
    }

    /**
     * 关闭其他界面
     */
    private void closeOtherUI(){
        ActivityCollector.finishAll();
        // 收起来更新窗口
        if (FloatWindowManager.getDownloadWindow() != null) {
            FloatWindowManager.getDownloadWindow().setVisibility(View.GONE);
        }

        // 关闭设置界面
        MyUtils.closeSettingActivity();
    }

    /**
     * 是否收缩菜单
     * @return
     */
    public boolean isShrink() {
        return isShrink;
    }

    /**
     * 截图操作
     */
    public void screenshot(){
        long startTime = System.currentTimeMillis();
        FloatWindowManager.getMenuWindowLeft().setVisibility(View.GONE);
        FloatWindowManager.getMenuWindow().setVisibility(View.GONE);

        new Thread(() -> {
            SystemClock.sleep(60);
            ACache aCache = ACache.get(getContext().getApplicationContext());

            String mSavedPath = DrawConsts.SCREEN_SOT_DIR  + TimeUtils.getNowTimeString() + DrawConsts.IMAGE_SAVE_SUFFIX;
            File file = new File(mSavedPath);
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            boolean isFlag = false;
            try {
                isFlag = BitmapUtils.saveInOI(mSavedPath, BitmapUtils.srceenshot(getContext()), 80);
                SystemClock.sleep(60);
//                CmdUtils.rootExec("screencap -p " + mSavedPath);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                String fileInfo = "sdcard/screenshot/" + file.getName();
                String tips = getResources().getString(R.string.tip_save_successfully, fileInfo);// file.getAbsolutePath()
                if (!isFlag){ // 截屏失败
                    tips = getResources().getString(R.string.tip_screenshot_failed);
                }
                final String msg = tips;
                mHandler.post(()->{
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                    // 显示虚拟按键界面
                    FloatWindowManager.getMenuWindowLeft().setVisibility(View.VISIBLE);
                    FloatWindowManager.getMenuWindow().setVisibility(View.VISIBLE);
                });

                aCache.put(CommConst.IS_SCREEN_SHOT_ING, "false");

                long endTime = System.currentTimeMillis();
                LogUtils.d("截图所花的时间->" + (endTime - startTime));
            }
        }).start();
    }

    Runnable shrinkRunnable = ()-> {
        SignalDialogLayout signalDialog = FloatWindowManager.getSignalDialog();
        NewSignalDialogLayout newSignalDialog = FloatWindowManager.getNewSignalDialog();

        if (signalDialog != null){
            signalDialog.setVisibility(View.GONE);
        }
        if (newSignalDialog != null){
            newSignalDialog.setVisibility(View.GONE);
        }

        shrinkMenu();
    };

    /**
     * 点击事件监听
     */
    private OnClickListener mOnClickListener = v -> {
        int id = v.getId();
        // 除了回退键/左右箭头/主页键时，其他按键都不能快速点击两次
	    if (!((id == R.id.btn_back)
			    || (id == R.id.btn_arrow)
                || (id == R.id.btn_home))){
		    if ( ViewUtils.isFastDoubleClick()){
			    return;
		    }
	    }

        mHandler.removeCallbacks(shrinkRunnable);

        // 关闭其他界面
        closeOtherUI();

        switch (id) {
            case R.id.btn_arrow: {// 收缩菜单
                if(!("on".equals(SystemProperties.get("persist.sys.lockScreen")))) {
                    changeMenuView();
                }
                break;
            }
            case R.id.btn_whiteboard: {// 白板显示和隐藏
                mHandler.postDelayed(shrinkRunnable, 5);

                mHandler.postDelayed(()->{
                    AppUtils.showComment(getContext());
                },100);
                return;
            }
            case R.id.btn_back: { // 返回
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_BACK);
                NewSignalDialogLayout newSignalDialog = FloatWindowManager.getNewSignalDialog();
                if(newSignalDialog!=null)
                newSignalDialog.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_home: { // 回到主页
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_HOME);

                // 发送SOURCE广播
                AppUtils.noticeChangeSignal(getContext(), -1);
                NewSignalDialogLayout newSignalDialog = FloatWindowManager.getNewSignalDialog();
                if(newSignalDialog!=null)
                newSignalDialog.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_app_switch: {// 多任务列表
                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_APP_SWITCH);

                // 发送SOURCE广播
                AppUtils.noticeChangeSignal(getContext(), -1);
                NewSignalDialogLayout newSignalDialog = FloatWindowManager.getNewSignalDialog();
                if(newSignalDialog!=null)
                newSignalDialog.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_show_setting: {// 显示更多设置界面
                showSetting();
                NewSignalDialogLayout newSignalDialog = FloatWindowManager.getNewSignalDialog();
                if(newSignalDialog!=null)
                newSignalDialog.setVisibility(View.GONE);
                break;
            }
            case R.id.btn_signal: {// 显示信号源弹框
//                showSignal();
                showNewSignal();
//                AppUtils.keyEventBySystem(KeyEvent.KEYCODE_TV_INPUT);
                break;
            }
            default: {
                break;
            }
        }

        mHandler.postDelayed(shrinkRunnable, 5000);
    };


    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<ControlMenuLayout> weakReference;

        public UIHandler(ControlMenuLayout activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    /**
     * 隐藏的动画
     */
    private Animation.AnimationListener hideAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            menuLayout.setVisibility(View.GONE);
         //   FloatWindowManager.showMenuWindow(getContext(), isRight, isShrink);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
//
    /**
     * 显示的动画
     */
    private Animation.AnimationListener showAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
}
