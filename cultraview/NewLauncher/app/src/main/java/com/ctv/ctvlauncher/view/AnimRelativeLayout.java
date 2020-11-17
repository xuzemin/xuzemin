package com.ctv.ctvlauncher.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.ctv.ctvlauncher.utils.AnimClickUtil;

/***
 * 只要控件外层套上该控件，即可实现点击动画
 */
public class AnimRelativeLayout extends RelativeLayout {

    /**
     * 动画模式【true：华丽效果——缩放加方向】【false：只缩放】
     * 华丽效果：即点击控件的 上、下、左、右、中间时的效果都不一样
     * 普通效果：即点击控件的任意部位，都只是缩放效果，与 华丽效果模式下 点击控件中间时的动画一样
     **/
    private boolean superb = false;

    /**
     * 顶点判断【0：中间】【1：上】【2：右】【3：下】【4：左】
     **/
    private int pivot = 0;
    private AnimClickUtil bamAnim;

    public AnimRelativeLayout(Context context) {
        this(context, null);
        this.setClickable(true);
    }

    public AnimRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.setClickable(true);
    }

    public AnimRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setClickable(true);
        initView();
    }

    private void initView() {
        bamAnim = new AnimClickUtil();
    }

    /**
     * 打开/关闭华丽效果，默认时关闭的
     */
    public void openSuperb(boolean isOpen) {
        superb = isOpen;
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                pivot = bamAnim.startAnimDown(this, superb, event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                bamAnim.startAnimUp(this, pivot);
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);

    }

    public void setDownEndListener(AnimClickUtil.OnAnimEndListener listener) {
        bamAnim.setAnimEndListener(listener);
    }
}