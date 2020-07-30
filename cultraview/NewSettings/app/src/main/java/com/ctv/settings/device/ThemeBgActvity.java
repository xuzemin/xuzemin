package com.ctv.settings.device;

import android.os.Bundle;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.device.viewHolder.ThemeBgViewHolder;
import com.ctv.settings.utils.L;

public class ThemeBgActvity extends BaseActivity {
    private final static String TAG = ThemeBgActvity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_bg);
        L.d(TAG, "onCreate");
        ThemeBgViewHolder holder = new ThemeBgViewHolder(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.d(TAG, "onDestroy");
    }
}
