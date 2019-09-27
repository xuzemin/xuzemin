package com.ctv.settings.timeanddate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.L;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.bean.MessageEvent;
import com.ctv.settings.timeanddate.holder.TimeAndDateViewHolder;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TimeAndDateActivity extends AppCompatActivity {
    private final static String TAG = "TimeAndDateLib";
    private TimeAndDateViewHolder mTimeAndDateViewHolder;
    final static String REFRESH_TIME_ZONE="refreshtimezone";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        setContentView(R.layout.activity_time_date);
        mTimeAndDateViewHolder = new TimeAndDateViewHolder(this);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        switch (messageEvent.getMessage()){
            case REFRESH_TIME_ZONE:{
                mTimeAndDateViewHolder.refreshUI(findViewById(R.id.tv_timezone));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        L.d(TAG,"onActivityResult");
        if(resultCode== CommonConsts.REFRESH_TIMEZONE){
            mTimeAndDateViewHolder.refreshUI(findViewById(R.id.tv_timezone));
        }else if(resultCode==CommonConsts.REFRESH_DATEFORMAT){
            mTimeAndDateViewHolder.refreshUI(findViewById(R.id.tv_date_format));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
