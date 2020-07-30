package com.ctv.settings.timeanddate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.holder.AutoShutdownViewHolder;

public class AutoShutdownActivity extends BaseActivity {

    private AutoShutdownViewHolder mAutoShutdownViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shutdown);
        mAutoShutdownViewHolder = new AutoShutdownViewHolder(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mAutoShutdownViewHolder.onKeydown(keyCode);
        return super.onKeyDown(keyCode, event);
    }
}
