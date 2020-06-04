package com.protruly.floatwindowlib.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cultraview.tv.CtvPictureManager;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.ActivityCollector;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.protruly.floatwindowlib.ui.SettingsDialogLayout;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.DrawConsts;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * 设置界面
 *
 * @author wang
 * @time on 2017/7/20.
 */
public class SettingNewActivity extends AppCompatActivity {
    private boolean isRight = true;
    public static Handler mHandler;

    public final static int KEY_MSG_FINISH = 0;
    SettingsDialogLayout layout;
    public final static int MSG_UPDATE_LIGHT = 1;
    SeekBar sound;
    TextView soundNumTv;
    SeekBar light;
    TextView lightNumTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        layout = new SettingsDialogLayout(this);
        setContentView(layout);
        // 初始化
        init();
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ){
//            layout.updateVoiceUI();
//        }
        if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            layout.updateVoiceUI(true);
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            layout.updateVoiceUI(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 初始化
     */
    private void init(){
        MyUtils.checkUSB(false);
        // 初始化
        mHandler = new UIHandler(this);

        sound = (SeekBar) findViewById(R.id.pup_seekbar2);
        soundNumTv = (TextView) findViewById(R.id.tv_sound);
        light = (SeekBar) findViewById(R.id.pup_seekbar1);
        lightNumTv = (TextView) findViewById(R.id.tv_light);

        Intent intent = getIntent();
        if (intent != null){
            isRight = intent.getBooleanExtra("isRight", true);
//            LogUtils.d("isRight->%s", isRight);

            layout.setRightShow(isRight);
        }

        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        Point point = ScreenUtils.getScreenSize(this);
        int defaultY = (int)((point.y / 4.0f));
        lp.x=78;
        lp.y = (int) SPUtil.getData(this, DrawConsts.LAST_POINT_Y_KEY, defaultY)
                - ScreenUtils.dip2px(this, 50);
        lp.width = ScreenUtils.dip2px(this, 256);
        lp.height = point.y;
        lp.gravity = isRight ? Gravity.RIGHT : Gravity.LEFT;//设置对话框置顶显示
        win.setAttributes(lp);
    }

	@Override
	protected void onResume() {
		super.onResume();
		layout.autoDelayHide();
        ActivityCollector.addActivity(this);
	}

	@Override
    protected void onDestroy() {
        super.onDestroy();
//        LogUtils.d("SettingNewActivity,onDestroy");
        // 显示虚拟按键界面
        ControlMenuLayout controlMenu = isRight ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
        if (controlMenu != null){
            controlMenu.changeIndexBg(true);
            controlMenu.setVisibility(View.VISIBLE);
        }

        // 打开PC触摸板
//        MyUtils.openAndClosePCTouch(true);
        MyUtils.checkUSB(true);

	    layout.destroy();

        // 移除所有的消息
		if (mHandler != null){
			mHandler.removeCallbacksAndMessages(null);
			mHandler = null;
		}

        ActivityCollector.finishAll();


    }

    /**
     * 更新背光
     */
    private void updateBlackLightSeekbar(boolean flag){
        try {
            int blackLight = CtvPictureManager.getInstance().getBacklight();
            AppUtils.setProgress(light, blackLight, flag);
            lightNumTv.setText(blackLight + "");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * 更新背光
     */
    private void updateBlackLightSeekbar(){
        try {
            int blackLight = CtvPictureManager.getInstance().getBacklight();
            light.setProgress(blackLight);
            lightNumTv.setText(blackLight + "");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isRight() {
        return isRight;
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<SettingNewActivity> weakReference;

        public UIHandler(SettingNewActivity activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingNewActivity activity = weakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            // 开始处理更新UI
            switch (msg.what){
                case KEY_MSG_FINISH:{// 退出
                    activity.finish();
                    break;
                }
                case MSG_UPDATE_LIGHT:{ // 自动更新背光
                    if ((msg.obj != null)
                            && (msg.obj instanceof  Boolean)){
                        boolean flag = (boolean)msg.obj;
                        activity.updateBlackLightSeekbar(flag);
                    } else {
                        activity.updateBlackLightSeekbar();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    }

}
