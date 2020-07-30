package com.ctv.settings.timeanddate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.timeanddate.holder.TimeZoneViewHolder;

public class TimeZoneActivity extends BaseActivity {
    private static final String TAG = "TimeZoneActivity";
    private TimeZoneViewHolder mTimeZoneViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timezone);
        mTimeZoneViewHolder = new TimeZoneViewHolder(this);
    }



}
