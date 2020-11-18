package com.ctv.settings.timeanddate.holder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.dialog.DateSettingDialog;
import com.ctv.settings.timeanddate.dialog.TimeSettingDialog;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.Tools;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.security.PublicKey;
import java.util.Calendar;
import java.util.Date;

public class DateAndTimeSettingViewHolder extends BaseViewHolder implements View.OnClickListener {


    private View mBtnBack;
    private TextView mBackTitle;
    private View mRlDate;
    private View mRlTime;
    private DateSettingDialog mDateSettingDialog;
    private TimeSettingDialog mTimeSettingDialog;
    private TextView mTvDate;
    private TextView mTvTime;
    private String date;
    private String time;

    public DateAndTimeSettingViewHolder(Activity activity) {
        super(activity);
        registserFilter();
    }

    @Override
    public void initUI(Activity activity) {
        mBtnBack = mActivity.findViewById(R.id.back_btn);
        mBackTitle = (TextView) mActivity.findViewById(R.id.back_title);
        mRlDate = mActivity.findViewById(R.id.rl_date);
        mRlTime = mActivity.findViewById(R.id.rl_time);
        mTvDate = (TextView)mActivity.findViewById(R.id.tv_date);
        mTvTime = (TextView)mActivity.findViewById(R.id.tv_time);
    }
    public void onDestory(){
        mActivity.unregisterReceiver(mTimeChangeReceiver);
    }

    @Override
    public void initListener() {
        mBtnBack.setOnClickListener(this);
        mRlDate.setOnClickListener(this);
        mRlTime.setOnClickListener(this);
    }

    @Override
    public void refreshUI(View view) {

    }

    @Override
    public void initData(Activity activity) {
        mBackTitle.setText(mActivity.getResources().getString(R.string.header_time_date));
        mDateSettingDialog = new DateSettingDialog(mActivity, this);
        mTimeSettingDialog = new TimeSettingDialog(mActivity, this);
        Date now = Calendar.getInstance().getTime();
        int dateFormatIndex = Tools.getDateFormat();
        date = DateFormat.format(CommonConsts.DATE_FORMAT_STRINGS[dateFormatIndex], now).toString();
        time = DateFormat.getTimeFormat(mActivity).format(now);
        mTvDate.setText(date);
        mTvTime.setText(time);

    }
    private void registserFilter() {
        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        mActivity.registerReceiver(mTimeChangeReceiver, timeFilter, null, null);
    }
    private final BroadcastReceiver mTimeChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData();
        }
    };
    public void updateData() {
        Date now = Calendar.getInstance().getTime();
        int dateFormatIndex = Tools.getDateFormat();
        date = DateFormat.format(CommonConsts.DATE_FORMAT_STRINGS[dateFormatIndex], now).toString();
        time = DateFormat.getTimeFormat(mActivity).format(now);
        mTvDate.setText(date);
        mTvTime.setText(time);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back_btn) {
            mActivity.finish();
            mActivity.setResult(CommonConsts.REFRESH_DATEF_AND_TIME);
        } else if (id == R.id.rl_date) {
            mDateSettingDialog.show();
        } else if (id == R.id.rl_time) {
            mTimeSettingDialog.show();
        }
    }

    public void hintDateSettingDialog(){
        mDateSettingDialog.dismiss();
    }
    public void hintTimeSettingDialog(){
        mTimeSettingDialog.dismiss();
    }
    public void setDate(DatePicker datePicker){
        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();
        onDateSet(year,month,dayOfMonth);
        mDateSettingDialog.dismiss();
    }
    public void setTime(TimePicker timePicker){
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        onTimeSet(hour,minute);
        mTimeSettingDialog.dismiss();
    }

    private void onTimeSet(int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        setTVTime(when);
        initTime(mActivity);
        Date now = c.getTime();
        time = DateFormat.getTimeFormat(mActivity).format(now);
        mTvTime.setText(time);
    }

    private void onDateSet(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        long when = c.getTimeInMillis();
        if (when / 1000 < Integer.MAX_VALUE) {
            SystemClock.setCurrentTimeMillis(when);
        }
        setTVTime(when);
        initTime(mActivity);
        Date now = c.getTime();
        java.text.DateFormat shortDateFormat = DateFormat.getDateFormat(mActivity);
        date = shortDateFormat.format(now);
        int dateFormatIndex = Tools.getDateFormat();
        date = DateFormat.format(CommonConsts.DATE_FORMAT_STRINGS[dateFormatIndex], now).toString();
        mTvDate.setText(date);
    }
    private void initTime(Context context) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int week_year = calendar.get(Calendar.WEEK_OF_YEAR);
        StringBuffer sb = new StringBuffer();
        sb.append("SetRTCTime");
        sb.append(changeTimeFormat(year));
        sb.append(changeTimeFormat(month));
        sb.append(changeTimeFormat(day));
        sb.append(changeTimeFormat(week_year));
        sb.append(changeTimeFormat(hour));
        sb.append(changeTimeFormat(minute));
        sb.append(changeTimeFormat(second));
        try {
            TvManager.getInstance().setTvosCommonCommand(sb.toString());
            Log.d("TimeUtil", "setting settime:" + sb.toString());
        } catch (TvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static String changeTimeFormat(int data) {
        if (data > 9) {
            return "" + data;
        } else {
            return "0" + data;
        }
    }
    private final TimerManager timerMgr = TvManager.getInstance().getTimerManager();
    private void setTVTime(long millitime) {
        Time time = new Time();
        time.set(millitime);
        time.set(millitime);
        time.month += 1;
        try {
            timerMgr.setClkTime(time, true);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }
}
