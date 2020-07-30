package com.ctv.settings.timeanddate.holder;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.timeanddate.dialog.BootupSaveDialog;
import com.hht.android.sdk.time.HHTTimeManager;
import com.hht.android.sdk.time.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class AutoBootupViewHolder extends BaseViewHolder implements View.OnClickListener {

    private TimePicker mTimePicker;
    private View mRlAutoBootup;
    private int auto_bootup;
    private ImageView mIvAutoBootup;
    private Time mDateTime;
    private Button mBtnSave;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    String[] REPEAT_VALUES;
    private TextView mTvRepeat;
    private int bootup_repeat;
    private BootupSaveDialog saveDialog;
    private boolean isOnTimerEnable;
    private View btnBack;
    private TextView mTvBackTitle;
    private Handler mHandler = new Handler() {
        @Override
        public String getMessageName(Message message) {
            return super.getMessageName(message);
        }
    };
    private View mRlAutoBootupTime;
    private TextView mTvAutoBootupTime;
    private View mRlRepeat;

    public AutoBootupViewHolder(Activity activity) {
        super(activity);
    }

    @Override
    public void initUI(Activity activity) {
        mTimePicker = (TimePicker) mActivity.findViewById(R.id.timepicker);
        mRlAutoBootup = mActivity.findViewById(R.id.rl_auto_bootup);
        mIvAutoBootup = (ImageView) mActivity.findViewById(R.id.iv_auto_bootup);
        mBtnSave = (Button) mActivity.findViewById(R.id.btn_save);
        mIvLeft = (ImageView) mActivity.findViewById(R.id.iv_left);
        mIvRight = (ImageView) mActivity.findViewById(R.id.iv_right);
        mTvRepeat = (TextView) mActivity.findViewById(R.id.tv_repeat);
        saveDialog = new BootupSaveDialog(mActivity, this);
        mTvBackTitle = (TextView) mActivity.findViewById(R.id.back_title);
        btnBack = mActivity.findViewById(R.id.back_btn);

        mRlAutoBootupTime = mActivity.findViewById(R.id.rl_auto_bootup_time);
        mTvAutoBootupTime = (TextView)mActivity.findViewById(R.id.tv_auto_bootup_time);

        mRlRepeat = mActivity.findViewById(R.id.rl_repeat);
    }

    @Override
    public void initData(Activity activity) {
        //定时关机开关状态
//        isOnTimerEnable = TvTimerManager.getInstance().isOnTimerEnable();
        isOnTimerEnable = HHTTimeManager.getInstance().isScheduleTimeBootEnable();
        if (isOnTimerEnable) {
            mIvAutoBootup.setImageResource(R.mipmap.on);
        } else {
            mIvAutoBootup.setImageResource(R.mipmap.off);
        }

        //开机时间状态
        mDateTime = new Time();

        // HHTApi test start
        TimeUtil onTime = HHTTimeManager.getInstance().getScheduleTimeForBoot();
        mDateTime.minute = onTime.min;
        mDateTime.hour = onTime.hour;
        // HHTApi test start

//        mDateTime = TvTimerManager.getInstance().getTvOnTimer();
//        mDateTime.hour = Settings.System.getInt(mActivity.getContentResolver(), "bootup_hour", 8);
//        mDateTime.minute = Settings.System.getInt(mActivity.getContentResolver(), "bootup_minute", 8);
        if (mDateTime.hour == 0 && mDateTime.minute == 0){
            mDateTime.hour = 8;
            mDateTime.minute = 8;
        }

        mTimePicker.setIs24HourView(true);
        mTimePicker.setHour(mDateTime.hour);
        mTimePicker.setMinute(mDateTime.minute);

        //重复性，默认关闭
        REPEAT_VALUES = mActivity.getResources().getStringArray(R.array.repeat_values);
        bootup_repeat = Settings.System.getInt(mActivity.getContentResolver(), "bootup_repeat", 0);
        mTvRepeat.setText(REPEAT_VALUES[bootup_repeat]);
        mTvBackTitle.setText(mActivity.getResources().getString(R.string.title_auto_bootup));
        if(mDateTime.minute<10&&mDateTime.hour<10){
            mTvAutoBootupTime.setText("0"+mDateTime.hour+" : 0"+mDateTime.minute);
        }else if(mDateTime.minute<10){
            mTvAutoBootupTime.setText(mDateTime.hour+" : 0"+mDateTime.minute);
        }else if(mDateTime.hour<10){
            mTvAutoBootupTime.setText("0"+mDateTime.hour+" : "+mDateTime.minute);
        }else{
            mTvAutoBootupTime.setText(mDateTime.hour+" : "+mDateTime.minute);
        }
    }

    @Override
    public void refreshUI(View view) {
        int id = view.getId();
        if (id == R.id.iv_auto_bootup) {
            if (isOnTimerEnable) {
                mIvAutoBootup.setImageResource(R.mipmap.on);
            } else {
                mIvAutoBootup.setImageResource(R.mipmap.off);
            }
        }
    }


    @Override
    public void initListener() {
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                mDateTime.minute = minute;
                mDateTime.hour = hour;
            }
        });

        mRlAutoBootup.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mIvLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);
        mRlAutoBootupTime.setOnClickListener(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        //定时关机开关
        //保存按钮
        if (id == R.id.rl_auto_bootup) {
            isOnTimerEnable = !isOnTimerEnable;
            refreshUI(mActivity.findViewById(R.id.iv_auto_bootup));
            if (isOnTimerEnable) {
                Log.i("gyx", "isOnTimerEnable=" + isOnTimerEnable);
                Settings.System.putInt(mActivity.getContentResolver(), "auto_bootup", 1);
            } else {
                Settings.System.putInt(mActivity.getContentResolver(), "auto_bootup", 0);
            }

            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable,500);
        } else if (id == R.id.btn_save) {
            saveDialog.show();
        } else if (id == R.id.iv_left||id == R.id.iv_right) {
            if (bootup_repeat == 0) {
                mTvRepeat.setText(REPEAT_VALUES[1]);
                bootup_repeat = 1;
                Settings.System.putInt(mActivity.getContentResolver(), "bootup_repeat", bootup_repeat);
            } else {
                mTvRepeat.setText(REPEAT_VALUES[0]);
                bootup_repeat = 0;
                Settings.System.putInt(mActivity.getContentResolver(), "bootup_repeat", bootup_repeat);
            }
        } else if (id == R.id.rl_auto_bootup_time) {
            saveDialog.show();
        }
    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                    TvTimerManager.getInstance().setOnTimerEnable(isOnTimerEnable);
                    // HHTApi test start
                    HHTTimeManager.getInstance().setScheduleTimeBootEnable(isOnTimerEnable);
                    // HHTApi test start
                }
            }).start();
        }
    };

    public void hintDialog() {
        saveDialog.dismiss();
    }

    public void save(TimePicker timePicker) {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        mDateTime.minute = minute;
        mDateTime.hour = hour;

//        Settings.System.putInt(mActivity.getContentResolver(), "bootup_hour", mDateTime.hour);
//        Settings.System.putInt(mActivity.getContentResolver(), "bootup_minute", mDateTime.minute);


        // HHTApi test start
        TimeUtil onTime = new TimeUtil();
        onTime.min = minute;
        onTime.hour = hour;
        List<String> mList =new ArrayList<>();
//        mList.add(TimeUtil.EnumWeek.SUN.toString());
        mList.add(TimeUtil.EnumWeek.MON.toString());
        mList.add(TimeUtil.EnumWeek.TUE.toString());
        mList.add(TimeUtil.EnumWeek.WED.toString());
        mList.add(TimeUtil.EnumWeek.THU.toString());
        mList.add(TimeUtil.EnumWeek.FRI.toString());
//        mList.add(TimeUtil.EnumWeek.SAT.toString());
        onTime.week = mList;
        Log.d("TimeUtil", "NewSettings onTime:" + onTime);
        HHTTimeManager.getInstance().setScheduleTimeForBoot(onTime);
        // HHTApi test start

//        Time DateTime = TvTimerManager.getInstance().getCurrentTvTime();
//        DateTime.hour = mDateTime.hour;
//        DateTime.minute = mDateTime.minute;
//        TvTimerManager.getInstance().setTvOnTimer(DateTime);
//        mHandler.removeCallbacks(runnable);
//        mHandler.postDelayed(runnable,500);

        saveDialog.dismiss();
        if(mDateTime.minute<10&&mDateTime.hour<10){
            mTvAutoBootupTime.setText("0"+mDateTime.hour+" : 0"+mDateTime.minute);
        }else if(mDateTime.minute<10){
            mTvAutoBootupTime.setText(mDateTime.hour+" : 0"+mDateTime.minute);
        }else if(mDateTime.hour<10){
            mTvAutoBootupTime.setText("0"+mDateTime.hour+" : "+mDateTime.minute);
        }else{
            mTvAutoBootupTime.setText(mDateTime.hour+" : "+mDateTime.minute);
        }

    }

    public void onKeyDown(int keycode){
        if(mRlRepeat.hasFocus()){
            if(keycode== KeyEvent.KEYCODE_DPAD_LEFT||keycode== KeyEvent.KEYCODE_DPAD_RIGHT){
                if (bootup_repeat == 0) {
                    mTvRepeat.setText(REPEAT_VALUES[1]);
                    bootup_repeat = 1;
                    Settings.System.putInt(mActivity.getContentResolver(), "bootup_repeat", bootup_repeat);
                } else {
                    mTvRepeat.setText(REPEAT_VALUES[0]);
                    bootup_repeat = 0;
                    Settings.System.putInt(mActivity.getContentResolver(), "bootup_repeat", bootup_repeat);
                }
            }
        }

    }

}
