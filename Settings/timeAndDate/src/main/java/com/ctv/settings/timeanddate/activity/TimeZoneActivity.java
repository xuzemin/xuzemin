package com.ctv.settings.timeanddate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.bean.MessageEvent;
import com.ctv.settings.timeanddate.holder.TimeZoneViewHolder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TimeZoneActivity extends AppCompatActivity {
    private static final String TAG = "TimeZoneActivity";
    private TimeZoneViewHolder mTimeZoneViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timezone);
        mTimeZoneViewHolder = new TimeZoneViewHolder(this);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()){

        }
    }


}
