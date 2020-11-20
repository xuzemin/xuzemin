package com.mphotool.whiteboard.view.menuviews;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class SwipeRecyclerView extends RecyclerView {
    private int mLastX;
    private int mLastY;
    private OnItemSwipeListener mOnItemSwipeListener;
    private View mTouchView = null;

    public interface OnItemSwipeListener {
        boolean onItemSwipe(int i);
    }

    public SwipeRecyclerView(Context context) {
        super(context);
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean dispatchTouchEvent(MotionEvent e) {
        int x = (int) e.getX();
        int y = (int) e.getY();
        switch (e.getAction()) {
            case 0:
                this.mTouchView = findChildViewUnder((float) x, (float) y);
                break;
            case 1:
                if (this.mTouchView != null) {
                    boolean handle = false;
                    if (Math.abs(this.mTouchView.getScrollX()) > (getWidth() * 2) / 3 && this.mOnItemSwipeListener != null) {
                        handle = this.mOnItemSwipeListener.onItemSwipe(getChildAdapterPosition(this.mTouchView));
                    }
                    if (!handle) {
                        ValueAnimator animation = ValueAnimator.ofInt(new int[]{this.mTouchView.getScrollX(), 0});
                        animation.setDuration(500);
                        animation.setInterpolator(new DecelerateInterpolator());
                        animation.start();
                        animation.addUpdateListener(new AnimatorUpdateListener() {
                            public void onAnimationUpdate(ValueAnimator animation) {
                                if (SwipeRecyclerView.this.mTouchView != null) {
                                    SwipeRecyclerView.this.mTouchView.scrollTo(((Integer) animation.getAnimatedValue()).intValue(), 0);
                                }
                            }
                        });
                        break;
                    }
                    this.mTouchView.scrollTo(0, 0);
                    break;
                }
                break;
            case 2:
                if (this.mTouchView != null) {
                    this.mTouchView.scrollBy(this.mLastX - x, 0);
                    break;
                }
                break;
        }
        this.mLastX = x;
        this.mLastY = y;
        return super.dispatchTouchEvent(e);
    }

    public SwipeRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnItemSwipeListener(OnItemSwipeListener listener) {
        this.mOnItemSwipeListener = listener;
    }
}
