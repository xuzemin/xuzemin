package com.ctv.settings.device;

import android.os.Bundle;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.device.viewHolder.ThemeBgViewHolder;
import com.ctv.settings.security.R;

public class ThemeBgActvity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_bg);
        ThemeBgViewHolder holder = new ThemeBgViewHolder(this);
    }


}
