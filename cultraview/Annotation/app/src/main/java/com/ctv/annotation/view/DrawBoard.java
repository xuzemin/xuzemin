package com.ctv.annotation.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ctv.annotation.R;
import com.ctv.annotation.utils.BaseUtils;

public class DrawBoard extends RelativeLayout {
    private Context mContext;
    private View mEraser;
    public PanelView mPanelView;

    private BoardTouchedListener mTouchedListener;
    public DrawBoard(Context context) {
        super(context);
        init();
    }

    public DrawBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public interface BoardTouchedListener {
        void touched();
    }

    private void init(){
        this.mContext = getContext();
        BaseUtils.dbg("hong", "init: drawboard 11111");
        PanelView panelView = new PanelView(this.mContext);
        this.mPanelView = panelView;

        BaseUtils.dbg("hong", "init: drawboard 222222");
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(panelView, params);
//      this.mEraser = new View(this.mContext);
//      this.mEraser.setBackgroundColor(Color.parseColor("#00ffffff"));


//      hideErase();
//      addView(this.mEraser, params);
        mPanelView.setPanelTouchStatusListener(new PanelView.PanelTouchStatusListener() {
            @Override
            public void onTouchDown() {
                if (mTouchedListener != null)
                {
                    mTouchedListener.touched();
                }
                lockMenu();
            }

            @Override
            public void onTouch(MotionEvent motionEvent) {


            }

            @Override
            public void onTouchUp() {
               unlockMenu();
            }
        });

    }


    private void lockMenu()
    {


        findViewById(R.id.pen_selector).setClickable(false);
        findViewById(R.id.clean_board).setClickable(false);




    }

    private void unlockMenu()
    {
        findViewById(R.id.pen_selector).setClickable(true);
        findViewById(R.id.clean_board).setClickable(true);
    }
    private void hideErase()
    {
        this.mEraser.setVisibility(View.GONE);
    }
    public void setPenColor(int color)
    {
        this.mPanelView.setPenColor(color);
    }

}
