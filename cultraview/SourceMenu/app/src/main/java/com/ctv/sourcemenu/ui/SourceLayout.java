package com.ctv.sourcemenu.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ctv.sourcemenu.R;

public class SourceLayout extends FrameLayout {
    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private View rootView;


    public SourceLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.activity_main, this);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){ // 点击了外部区域
//            Log.d(TAG, "点击了外部区域");
//	        stopTimetask();
//            setVisibility(INVISIBLE);

        } else {

        }

        return super.onTouchEvent(event);
    }
}
