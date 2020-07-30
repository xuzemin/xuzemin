package com.ctv.settings.device;

import android.os.Bundle;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.device.viewHolder.ThemeSytleViewHolder;

public class ThemeStyleActvity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_style);
        ThemeSytleViewHolder holder = new ThemeSytleViewHolder(this);
    }
}
