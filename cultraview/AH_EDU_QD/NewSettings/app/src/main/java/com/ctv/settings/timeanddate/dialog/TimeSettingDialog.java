package com.ctv.settings.timeanddate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import com.ctv.settings.R;
import com.ctv.settings.timeanddate.holder.DateAndTimeSettingViewHolder;

import java.util.Calendar;

public class TimeSettingDialog extends Dialog {
    DateAndTimeSettingViewHolder viewHolder;
    private Context mCpntext;
    public TimeSettingDialog(@NonNull Context context, DateAndTimeSettingViewHolder viewHolder) {
        super(context);
        this.viewHolder=viewHolder;
        this.mCpntext=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_setting_layout);
        findViews();
    }

    private void findViews() {
        Button mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        Button mBtnOk = (Button) findViewById(R.id.btn_ok);
        TimePicker mTimePicker=(TimePicker) findViewById(R.id.timepicker);
        boolean is24hourFormat = DateFormat.is24HourFormat(mCpntext);
        if(is24hourFormat){
            mTimePicker.setIs24HourView(true);
        }else{
            mTimePicker.setIs24HourView(false);
        }
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.hintTimeSettingDialog();
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.setTime(mTimePicker);
            }
        });
    }
}
