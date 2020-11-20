package com.mphotool.whiteboard.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;


public class FloatBallManager {
    private static final String TAG = "FloatBallManager";
    private FloatBallView mFloatBallView;
    private WindowManager mWindowManager;
    private LayoutParams mFBParams = null;
    private int mScreenWidth = 0;
    private int mScreenHeight = 0;
    private static Context mCtx = null;
    private View mFBWindow = null;
    private volatile static FloatBallManager instance = null;

    private FloatBallManager(Context ctx){
        mCtx = ctx;
        mWindowManager = (WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE);
    }

    public static  FloatBallManager getInstance(Context ctx){
        if(instance == null){
            synchronized(FloatBallManager.class){
                if(instance == null){
                    instance = new FloatBallManager(ctx);
                }
            }
        }
        return instance;
    }

    public void addBallView(int x,int y) {
        if (mFloatBallView == null) {
            DisplayMetrics dm = new DisplayMetrics();

            mWindowManager.getDefaultDisplay().getMetrics(dm);
            mScreenWidth = dm.widthPixels;
            mScreenHeight = dm.heightPixels;
            mFBWindow = LayoutInflater.from(mCtx).inflate(R.layout.float_ball, null);
            mFloatBallView = mFBWindow.findViewById(R.id.float_ball);
            mFBParams = new LayoutParams();
            mFBParams.x = x;//mScreenWidth-mFloatBallView.getWidth();
            mFBParams.y = y;//mScreenHeight / 2;
            mFBParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mFBParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mFBParams.gravity = Gravity.LEFT | Gravity.TOP;
            mFBParams.type = LayoutParams.TYPE_SYSTEM_ALERT | LayoutParams.TYPE_SYSTEM_OVERLAY;
            mFBParams.format = PixelFormat.TRANSLUCENT;
            mFBParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
            mWindowManager.addView(mFBWindow, mFBParams);
        }
    }

    public void removeBallView() {
        if (mFloatBallView != null) {
            mWindowManager.removeView(mFBWindow);
            mFloatBallView = null;
        }
    }

    public void updateBallView(int deltaX,int deltaY) {
        mFBParams.x += deltaX;
        mFBParams.y += deltaY;
        if (mWindowManager != null) {
//            BaseUtils.dbg(TAG,"updateBallView mParams.x = "+mFBParams.x+" mParams.y = "+mFBParams.y);
            mWindowManager.updateViewLayout(mFBWindow, mFBParams);
        }
    }

    public void foldFloatball() {
        int fb_width = mFloatBallView.getWidth();
        int fb_height = mFloatBallView.getHeight();
        int centerX = (mScreenWidth-fb_width)/2;
        int destX = (mFBParams.x<centerX)? 0:mScreenWidth-fb_width;
//        BaseUtils.dbg(TAG,"fb_width = "+fb_width+" fb_height = "+fb_height+" destX = "+destX);
        mFBParams.x = destX;
        updateBallView(0,0);
    }
}
