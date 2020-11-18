package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;

import java.lang.ref.WeakReference;

/**
 * Desc: 温度计布局
 *
 * @author wang
 * @time 2017/4/13.
 */
public class ThemometerLayout extends FrameLayout {
    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private TextView textView;

    public static Handler mHandler;

    public ThemometerLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.popup_tmp, this);

        mHandler = new UIHandler(this);
        textView = (TextView) findViewById(R.id.textTT);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){ // 点击了外部区域
            LogUtils.d("点击了外部区域");
            setVisibility(INVISIBLE);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 更新进度条
     *
     * @param process
     */
    private void updateProcess(float process){
        textView.setText(process + "℃");
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<ThemometerLayout> weakReference;

        public UIHandler(ThemometerLayout downloadingLayout) {
            super();
            this.weakReference = new WeakReference<>(downloadingLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ThemometerLayout layout = weakReference.get();

            if (layout == null) {
                return;
            }

            // 开始处理更新UI
            switch (msg.what){
                case 1:
                    float process = (float)msg.obj;
                    layout.updateProcess(process);
                    break;
            }
        }
    }
}
