package com.ctv.settings.timeanddate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.timeanddate.holder.DateFormatViewHolder;

public class DateFormatActivity extends BaseActivity {

    private DateFormatViewHolder mTimeFormatViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dateformat);
        mTimeFormatViewHolder = new DateFormatViewHolder(this);

    }
}
