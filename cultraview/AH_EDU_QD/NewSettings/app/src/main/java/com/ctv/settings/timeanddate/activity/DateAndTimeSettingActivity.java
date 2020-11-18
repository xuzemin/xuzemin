package com.ctv.settings.timeanddate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.timeanddate.holder.DateAndTimeSettingViewHolder;

public class DateAndTimeSettingActivity extends BaseActivity {

    private DateAndTimeSettingViewHolder mDateAndTimeSettingViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_setting);
        mDateAndTimeSettingViewHolder = new DateAndTimeSettingViewHolder(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDateAndTimeSettingViewHolder.onDestory();
    }
}
