package com.mphotool.whiteboard.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.mphotool.whiteboard.R;

public class DrawBoard extends RelativeLayout {
    private Context mContext;
//    private View mEraser;
//    private DrawView mEraser;
    public PanelView mPanelView;

    private BoardTouchedListener mTouchedListener;

    public void setTouchedListener(BoardTouchedListener listener)
    {
        this.mTouchedListener = listener;
    }

    public interface BoardTouchedListener {
        void touched();
    }

    public DrawBoard(Context context)
    {
        super(context);
        init();
    }

    public DrawBoard(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public DrawBoard(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = 21)
    public DrawBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init()
    {
        this.mContext = getContext();
        PanelView panelView = new PanelView(this.mContext);
        this.mPanelView = panelView;
        mPanelView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(panelView, params);
/*        this.mEraser = new View(this.mContext);
        this.mEraser.setBackgroundColor(Color.parseColor("#ffffffff"));
        hideErase();
        addView(this.mEraser, params);*/

        this.mPanelView.setPanelTouchStatusListener(new PanelView.PanelTouchStatusListener() {

            @Override public void onTouchDown()
            {
                if (mTouchedListener != null)
                {
                    mTouchedListener.touched();
                }
                lockMenu();
            }

            @Override
            public void onTouch(MotionEvent motionEvent)
            {

            }

            @Override public void onTouchUp()
            {
                unlockMenu();
            }

        });
    }

    private void lockMenu()
    {
        findViewById(R.id.more_function).setClickable(false);
        findViewById(R.id.change_bg).setClickable(false);
        findViewById(R.id.math_icon).setClickable(false);
        findViewById(R.id.pen_selector).setClickable(false);
        findViewById(R.id.clean_board).setClickable(false);
        findViewById(R.id.Point_control).setClickable(false);
        findViewById(R.id.selection).setClickable(false);
        findViewById(R.id.undo_board).setClickable(false);
        findViewById(R.id.redo_board).setClickable(false);
        findViewById(R.id.add_panel).setClickable(false);
        findViewById(R.id.prev_panel).setClickable(false);
        findViewById(R.id.panel_index).setClickable(false);
        findViewById(R.id.next_panel).setClickable(false);
    }

    private void unlockMenu()
    {
        findViewById(R.id.more_function).setClickable(true);
        findViewById(R.id.change_bg).setClickable(true);
        findViewById(R.id.math_icon).setClickable(true);
        findViewById(R.id.pen_selector).setClickable(true);
        findViewById(R.id.clean_board).setClickable(true);
        findViewById(R.id.Point_control).setClickable(true);
        findViewById(R.id.selection).setClickable(true);
        findViewById(R.id.undo_board).setClickable(true);
        findViewById(R.id.redo_board).setClickable(true);
        findViewById(R.id.add_panel).setClickable(true);
        findViewById(R.id.prev_panel).setClickable(true);
        findViewById(R.id.panel_index).setClickable(true);
        findViewById(R.id.next_panel).setClickable(true);
    }

    public void cleanBoard()
    {
        this.mPanelView.cleanBoardAction(true);
    }

    public boolean undoBoard()
    {
        return this.mPanelView.undo(false) == 0;
    }

    public boolean redoBoard()
    {
        return this.mPanelView.redo(false) == 0;
    }

    public void setPenColor(int color)
    {
        this.mPanelView.setPenColor(color);
    }

    public void setPenWidth(float width)
    {
        this.mPanelView.setPenWidth(width);
    }

    /*private void hideErase()
    {
        this.mEraser.setVisibility(View.GONE);
    }

    private void moveErase(float x, float y, int width, int height)
    {
        this.mEraser.setLayoutParams(new LayoutParams(width, height));
        this.mEraser.setX(x);
        this.mEraser.setY(y);
    }*/

}
