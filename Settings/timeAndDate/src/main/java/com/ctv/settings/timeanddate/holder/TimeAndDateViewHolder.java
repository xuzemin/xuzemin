package com.ctv.settings.timeanddate.holder;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.utils.NetUtils;
import com.ctv.settings.utils.Tools;
import com.ctv.settings.timeanddate.activity.AutoBootupActivity;
import com.ctv.settings.timeanddate.activity.AutoShutdownActivity;
import com.ctv.settings.timeanddate.activity.DateFormatActivity;
import com.ctv.settings.timeanddate.activity.TimeZoneActivity;
import com.ctv.settings.timeanddate.bean.MessageEvent;
import com.ctv.settings.timeanddate.bean.TimezoneInfo;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.timeanddate.R;
import android.provider.Settings.SettingNotFoundException;
import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class TimeAndDateViewHolder extends BaseViewHolder implements View.OnClickListener{
    private final static String TAG = TimeAndDateViewHolder.class.getCanonicalName();

    private ImageView mIvIs24HourFormat;
    private ImageView mIvAutoDateTime;

    private View mRlDateFormat;
    private TextView mTvDateFormat;
    private TextView mTvTimeZone;

    private View mRlIs24HourFormat;
    private View mRlAutoDateTime;
    private View mRltimeZone;

    private boolean is24hourFormat;    //是否使用24小时格式
    private boolean isAutoDateTime;   //是否自动获取时区

    private static String mAutoTimezoneId;
    private String zone;

    private TimezoneInfo mTimezoneInfo;
    private String stringDateFormat;
    private View mAutoShutdown;
    private View mAutoBootup;


    public TimeAndDateViewHolder(Activity activity){
        super(activity);
    }

    @Override
    public void refreshUI(View view) {
        int id = view.getId();
        if (id == R.id.iv_is_24_hour_format) {        //更新24小时制状态
            if (is24hourFormat) {
                mIvIs24HourFormat.setImageResource(R.mipmap.on);
            } else {
                mIvIs24HourFormat.setImageResource(R.mipmap.off);
            }
        } else if (id == R.id.iv_auto_date_time) {        //更新自动时区状态
            if (isAutoDateTime) {
                mIvAutoDateTime.setImageResource(R.mipmap.on);
            } else {
                mIvAutoDateTime.setImageResource(R.mipmap.off);
            }
        } else if (id == R.id.tv_timezone) {         //更新系统时区
            Log.i("gyx", "tv_timezone");
            zone = Tools.getTimeZoneText();
            mTvTimeZone.setText(zone);
        } else if (id == R.id.tv_date_format) {         //更新日期格式
            Date now = Calendar.getInstance().getTime();
            int dateFormatIndex = Tools.getDateFormat(mActivity);
            stringDateFormat = DateFormat.format(CommonConsts.DATE_FORMAT_STRINGS[dateFormatIndex], now)
                    .toString();
            mTvDateFormat.setText(stringDateFormat);
        }
    }


    /**
     * @param activity 初始化数据
     */
    @Override
    public void initData(Activity activity) {
        Log.d(TAG, "initData");
        is24hourFormat = DateFormat.is24HourFormat(mActivity);

        //是否使用24小时制
        if(is24hourFormat){
            mIvIs24HourFormat.setImageResource(R.mipmap.on);
        }else{
            mIvIs24HourFormat.setImageResource(R.mipmap.off);
        }

        //是否自动获取日期时间
        try {
            isAutoDateTime = Settings.Global.getInt(mActivity.getContentResolver(),
                    Settings.Global.AUTO_TIME) > 0;
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
            isAutoDateTime = false;
        }
        if(isAutoDateTime){
            mIvAutoDateTime.setImageResource(R.mipmap.on);
        }else{
            mIvAutoDateTime.setImageResource(R.mipmap.off);
        }

        //系统时区
        mTimezoneInfo = new TimezoneInfo(mActivity);
        initAutoTimezoneId();
        zone = Tools.getTimeZoneText();
        mTvTimeZone.setText(zone);

        //日期格式
        Date now = Calendar.getInstance().getTime();
        int dateFormatIndex = Tools.getDateFormat(mActivity);
        stringDateFormat = DateFormat.format(CommonConsts.DATE_FORMAT_STRINGS[dateFormatIndex], now)
                .toString();
        mTvDateFormat.setText(stringDateFormat);
    }


    /**
     * @param activity 初始化UI
     */
    @Override
    public void initUI(Activity activity) {
        Log.d(TAG, "initUI");
        mRlIs24HourFormat = mActivity.findViewById(R.id.rl_is_24_hour_format);
        mIvIs24HourFormat = (ImageView)mActivity.findViewById(R.id.iv_is_24_hour_format);
        mRlAutoDateTime = mActivity.findViewById(R.id.rl_auto_date_time);
        mIvAutoDateTime = (ImageView) mActivity.findViewById(R.id.iv_auto_date_time);
        mRltimeZone = mActivity.findViewById(R.id.rl_timezone);
        mTvTimeZone = (TextView)mActivity.findViewById(R.id.tv_timezone);
        mRlDateFormat = mActivity.findViewById(R.id.rl_date_format);
        mTvDateFormat = (TextView) mActivity.findViewById(R.id.tv_date_format);
        mAutoShutdown = mActivity.findViewById(R.id.rl_turn_off);
        mAutoBootup = mActivity.findViewById(R.id.rl_turn_on);
    }

    /**
     * 初始化点击事件
     */
    @Override
    public void initListener() {
        Log.d(TAG, "initListener");
        mRlIs24HourFormat.setOnClickListener(this);
        mRlAutoDateTime.setOnClickListener(this);
        mRltimeZone.setOnClickListener(this);
        mRlDateFormat.setOnClickListener(this);
        mAutoShutdown.setOnClickListener(this);
        mAutoBootup.setOnClickListener(this);
    }

    /**
     * @param view 点击事件监听
     */
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_is_24_hour_format) {
            is24hourFormat = !is24hourFormat;
            Settings.System.putString(mActivity.getContentResolver(),
                    Settings.System.TIME_12_24, is24hourFormat ? CommonConsts.HOURS_24
                            : CommonConsts.HOURS_12);
            refreshUI(mIvIs24HourFormat);
        } else if (id == R.id.rl_auto_date_time) {
            isAutoDateTime = !isAutoDateTime;
            if (isAutoDateTime && mAutoTimezoneId == null) {
                initAutoTimezoneId();
            } else if (isAutoDateTime && mAutoTimezoneId != null) {
                AlarmManager alarm = (AlarmManager) mActivity
                        .getSystemService(Context.ALARM_SERVICE);
                alarm.setTimeZone(mAutoTimezoneId);
            }
            Tools.saveAutoTimeValue(mActivity, isAutoDateTime);
            refreshUI(mIvAutoDateTime);
        } else if (id == R.id.rl_timezone) {
            mActivity.startActivity(new Intent(mActivity, TimeZoneActivity.class));
        } else if (id == R.id.rl_date_format) {
            mActivity.startActivity(new Intent(mActivity, DateFormatActivity.class));
        } else if (id == R.id.rl_turn_off) {
            mActivity.startActivity(new Intent(mActivity, AutoShutdownActivity.class));
        } else if (id == R.id.rl_turn_on) {
            mActivity.startActivity(new Intent(mActivity, AutoBootupActivity.class));
        }
    }

    /**
     * 初始化自动获取时区ID
     */
    private void initAutoTimezoneId() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    mAutoTimezoneId = mTimezoneInfo.getTimezoneFromWebyield();
                    if (isAutoDateTime && mAutoTimezoneId != null) {
                        AlarmManager alarm = (AlarmManager)mActivity
                                .getSystemService(Context.ALARM_SERVICE);
                        alarm.setTimeZone(mAutoTimezoneId);
                      //  dateTimeHandler.sendEmptyMessageDelayed(Constants.UPDATE_LISTVIEW, 100);
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    mAutoTimezoneId = null;
                    e.printStackTrace();
                    Log.d(TAG, "getAutoTimezoneId Exception:" + e.getMessage());
                    if (isAutoDateTime && mAutoTimezoneId == null) {
                        // 通过 http://ip-api.com/ 获取 time zone
                        String responseStr = NetUtils.get("http://ip-api.com/json");
                        JSONObject jsonObject = null;
                        if (responseStr != null) {
                            try {
                                jsonObject = new JSONObject(responseStr);
                                mAutoTimezoneId = jsonObject.getString("timezone");
                                AlarmManager alarm = (AlarmManager) mActivity
                                        .getSystemService(Context.ALARM_SERVICE);
                                alarm.setTimeZone(mAutoTimezoneId);
                                EventBus.getDefault().post(new MessageEvent("refreshtimezone"));
                                Log.d(TAG, "mAutoTimezoneId=" + mAutoTimezoneId);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }
}
