package com.protruly.floatwindowlib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.ui.ControlMenuLayout;
import com.yinghe.whiteboardlib.utils.ScreenUtils;

import java.lang.ref.WeakReference;

/**
 * 隐藏的界面
 *
 * @author wang
 * @time on 2017/7/20.
 */
public class HideActivity extends Activity {
    private boolean isRight = true;
    public static Handler mHandler;

    public final static int KEY_MSG_FINISH = 0;
    public final static int KEY_MSG_FINISH_NOT_CLOSE_PC_TOUCH = 1;

    private boolean isShrink = true;// 判断菜单是否收缩

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);
        setContentView(R.layout.activity_hide);

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
        lp.width = ScreenUtils.dip2px(this, 1);
        lp.height = ScreenUtils.dip2px(this, 1);
        lp.gravity = Gravity.CENTER;//对话框居中显示
        win.setBackgroundDrawableResource(R.drawable.shape_all);
        win.setAttributes(lp);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("HideActivity, onDestroy");

        // 收缩菜单
        if (isShrink){
            ControlMenuLayout controlMenu = isRight ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
            if ((controlMenu != null)){
                controlMenu.shrinkMenu();
            }
        }

        // 移除所有的消息
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<HideActivity> weakReference;

        public UIHandler(HideActivity activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HideActivity activity = weakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            // 开始处理更新UI
            switch (msg.what){
                case KEY_MSG_FINISH:{// 直接退出，并收缩菜单
                    activity.isShrink = false;
                    activity.finish();
                    break;
                }
                case KEY_MSG_FINISH_NOT_CLOSE_PC_TOUCH:{// 退出，不收缩菜单
                    activity.isShrink = false;
                    activity.finish();
                    break;
                }
                default:
                    break;
            }
        }
    }

}
