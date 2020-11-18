package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by gaoyixiang on 2019/6/1.
 */

public class DrawView extends RelativeLayout{
    Context mContext;
    float moveY;
    public DrawView(Context context) {
        super(context);
        setTranslationY(294);
        SharedPreferences sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        sp.edit().putFloat("y",294).commit();
        this.mContext=context;
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTranslationY(294);
        SharedPreferences sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        sp.edit().putFloat("y",294).commit();
        this.mContext=context;
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTranslationY(294);
        SharedPreferences sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        sp.edit().putFloat("y",294).commit();
        this.mContext=context;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float y= getY() + (event.getY() - moveY);
                Log.i("gyx","y="+y+"--------");
                if(y<0){
                    y=0;
                }
                if(y>600){
                    y=600;
                }
                SharedPreferences sp = mContext.getSharedPreferences("sp", Context.MODE_PRIVATE);
                sp.edit().putFloat("y",y).commit();
                setTranslationY(y);
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }


}
