package com.hht.middleware.listener;

import android.view.View;

import java.util.Calendar;

/**
 * Author: chenhu
 * Time: 2019/12/4 16:12
 * 延时点击事件监听
 */
public abstract class OnDelayedClickListener implements View.OnClickListener {
    private static final int MIN_DELAYED_TIME = 1000;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long curTime = Calendar.getInstance().getTimeInMillis();
        if (curTime - lastClickTime > MIN_DELAYED_TIME) {
            onDelayedClick(v);
            lastClickTime = curTime;
        }
    }

    protected abstract void onDelayedClick(View view);
}
