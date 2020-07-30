package com.protruly.floatwindowlib.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.broadcast.BootReceiver;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.yinghe.whiteboardlib.utils.ACache;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * 设置界面
 *
 * @author wang
 * @time on 2017/7/20.
 */
public class SettingActivity extends Activity {
    private boolean isRight = true;
    public static Handler mHandler;

    public final static int KEY_MSG_FINISH = 0;
    private ACache aCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.activity_setting);

        // 初始化
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            LogUtils.d("点击了外部");
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 初始化
     */
    private void init(){
        // 初始化
        mHandler = new UIHandler(this);

        Intent intent = getIntent();
        if (intent != null){
            isRight = intent.getBooleanExtra("isRight", true);
            LogUtils.d("isRight->%s", isRight);
        }

//        // 若是在PC界面，关闭PC触摸
//        MyUtils.openAndClosePCTouch(false);

        //窗口对齐屏幕宽度
        Window win = this.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        Point point = ScreenUtils.getScreenSize(this);
        lp.width = (int)(point.x * 0.25);//ScreenUtils.dip2px(this, 300);
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = isRight ? Gravity.RIGHT : Gravity.LEFT;//设置对话框置顶显示
        win.setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("SettingActivity,onDestroy");

        ACache aCache = ACache.get(getApplicationContext());
        String isScreenshoting = aCache.getAsString(CommConst.IS_SCREEN_SHOT_ING);
        if (!TextUtils.isEmpty(isScreenshoting) && "true".equals(isScreenshoting)){  // 截图时
            ControlMenuLayout controlMenu = FloatWindowManager.getMenuWindow();
            if (controlMenu != null){
                controlMenu.screenshot();
            }
        } else {
            // 显示虚拟按键界面
            ControlMenuLayout controlMenu = isRight ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
            if (controlMenu != null){
                controlMenu.setVisibility(View.VISIBLE);
            }
        }

        // 打开PC触摸板
//        MyUtils.openAndClosePCTouch(true);

        // 移除所有的消息
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    public boolean isRight() {
        return isRight;
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<SettingActivity> weakReference;

        public UIHandler(SettingActivity activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingActivity activity = weakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            // 开始处理更新UI
            switch (msg.what){
                case KEY_MSG_FINISH:{// 退出
                    activity.finish();
                    break;
                }
                default:
                    break;
            }
        }
    }

}
