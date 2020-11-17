package com.ctv.annotation.view;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ctv.annotation.R;
import com.ctv.annotation.mananger.FloatWindowManager;
import com.ctv.annotation.utils.BaseUtils;

import java.util.Timer;
import java.util.TimerTask;

public class FounctionView extends FrameLayout {
    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager.LayoutParams mParams;// 浮窗的参数


    public FounctionView(@NonNull Context context) {
        super(context);
    }

    public FounctionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FounctionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
