package com.ctv.settings.timeanddate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.timeanddate.holder.AutoBootupViewHolder;

public class AutoBootupActivity extends BaseActivity {

    private AutoBootupViewHolder mAutoBootupViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bootup);
        mAutoBootupViewHolder = new AutoBootupViewHolder(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mAutoBootupViewHolder.onKeyDown(keyCode);
        return super.onKeyDown(keyCode, event);

    }
}
