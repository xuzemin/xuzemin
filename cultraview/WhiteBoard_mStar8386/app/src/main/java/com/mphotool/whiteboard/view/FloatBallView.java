package com.mphotool.whiteboard.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;


public class FloatBallView extends LinearLayout {

    private static final String TAG = "FloatBallView";
    private FloatBallManager mFBManager = null;

    private View mFB_button = null;
    private ImageView mFB_ImgBall = null;
    private ImageView mFB_ImgBigBall = null;
    private ImageView mFB_ImgBg = null;


    private int mDownX;
    private int mDownY;

    private int mLastX;
    private int mLastY;

    public FloatBallView(Context context) {
        this(context, null);
    }

    public FloatBallView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatBallView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        mFBManager = FloatBallManager.getInstance(context);
        inflate(getContext(), R.layout.float_ball_layout, this);
        mFB_button = findViewById(R.id.fl_button);
        mFB_ImgBall = (ImageView) findViewById(R.id.img_ball);
        mFB_ImgBigBall = (ImageView) findViewById(R.id.img_big_ball);
        mFB_ImgBg = (ImageView) findViewById(R.id.img_bg);

        mFB_button.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, final MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mFB_ImgBigBall.setVisibility(View.VISIBLE);
                        mFB_ImgBall.setVisibility(View.INVISIBLE);
                        BaseUtils.dbg(TAG,"float button ACTION_DOWN X = "+event.getRawX()+" Y = "+event.getRawY());
                        touchDown(x,y);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        BaseUtils.dbg(TAG,"ACTION_MOVE");
                        touchMove(x,y);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        BaseUtils.dbg(TAG,"float button ACTION_UP X = "+event.getRawX()+" Y = "+event.getRawY());
                        touchUp(x,y);
                        break;
                }
                return true;
            }
        });
    }

    private void touchDown(int x, int y) {
        mDownX = x;
        mDownY = y;
        mLastX = mDownX;
        mLastY = mDownY;
    }

    private void touchMove(int x, int y) {
        int eachDeltaX = x - mLastX;
        int eachDeltaY = y - mLastY;
        mLastX = x;
        mLastY = y;
        mFBManager.updateBallView(eachDeltaX, eachDeltaY);
    }

    private void touchUp(int x,int y) {
        if (Math.abs(x-mDownX)<this.getWidth()/10 && Math.abs(y-mDownY)<this.getHeight()/10){

        }
        mFB_ImgBigBall.setVisibility(View.INVISIBLE);
        mFB_ImgBall.setVisibility(View.VISIBLE);
//        mFBManager.foldFloatball();
    }
}
