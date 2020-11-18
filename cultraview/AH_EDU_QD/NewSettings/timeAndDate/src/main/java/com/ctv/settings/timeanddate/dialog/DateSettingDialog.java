package com.ctv.settings.timeanddate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.holder.DateAndTimeSettingViewHolder;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.Tools;

import java.util.Calendar;
import java.util.Date;

public class DateSettingDialog extends Dialog {
    DateAndTimeSettingViewHolder viewHolder;
    private DatePicker mDatePicker;

    public DateSettingDialog(@NonNull Context context, DateAndTimeSettingViewHolder viewHolder) {
        super(context);
        this.viewHolder = viewHolder;
    }

    @RequiresApi(api = 26)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_date_setting_layout);
        findViews();
    }

    @RequiresApi(api = 26)
    private void findViews() {
        Button mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        Button mBtnOk = (Button) findViewById(R.id.btn_ok);
        mDatePicker = (DatePicker) findViewById(R.id.datepicker);
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.hintDateSettingDialog();
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.setDate(mDatePicker);
            }
        });
    }
}
