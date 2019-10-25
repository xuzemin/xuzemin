package com.youkes.browser.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by john on 2017/7/13.
 */

public class LauncherVideoView extends VideoView {
    public LauncherVideoView(Context context) {
        super(context);
    }

    public LauncherVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LauncherVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public LauncherVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
//        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
//        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        width = widthSpecSize;
        height = heightSpecSize;
        setMeasuredDimension(width, height);
    }
}