package com.ctv.settings.base;

import android.app.Activity;
import android.view.View;

/**
 * 抽象ViewHolder
 * @author wanghang
 * @date 2019/09/17
 */
public interface IBaseViewHolder {
    /**
     * 初始化UI
     * @param activity
     */
    void initUI(Activity activity);

    /**
     * 初始化数据
     * @param activity
     */
    void initData(Activity activity);

    /**
     * 初始化监听
     */
    void initListener();

    /**
     * 刷新指定view
     * @param view
     */
    void refreshUI(View view);
}
