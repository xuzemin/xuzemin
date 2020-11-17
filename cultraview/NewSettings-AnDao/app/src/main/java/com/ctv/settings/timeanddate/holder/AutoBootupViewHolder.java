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
import android.widget.LinearLayout;
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

    private View mRlAutoBootup;
    private ImageView mIvAutoBootup;
    private Time mDateTime;
    String[] REPEAT_VALUES;
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
    private TextView planTitle;
    private LinearLayout item_plan;
    private String TAG = "AutoBootupViewHolder";

    public AutoBootupViewHolder(Activity activity) {
        super(activity);
    }

    @Override
    public void initUI(Activity activity) {
        mRlAutoBootup = mActivity.findViewById(R.id.rl_auto_bootup);
        mIvAutoBootup = (ImageView) mActivity.findViewById(R.id.iv_auto_bootup);
        saveDialog = new BootupSaveDialog(mActivity, this);
        mTvBackTitle = (TextView) mActivity.findViewById(R.id.back_title);
        btnBack = mActivity.findViewById(R.id.back_btn);
        mRlAutoBootupTime = mActivity.findViewById(R.id.rl_auto_bootup_time);
        mTvAutoBootupTime = (TextView) mActivity.findViewById(R.id.tv_auto_bootup_time);
        planTitle = mActivity.findViewById(R.id.tv_title);
        item_plan = mActivity.findViewById(R.id.ll_plan_item);
    }

    @Override
    public void initData(Activity activity) {
        mDateTime = new Time();
        isOnTimerEnable= HHTTimeManager.getInstance().isScheduleTimeBootEnable();
        refreshPlan();
        mTvBackTitle.setText(mActivity.getResources().getString(R.string.title_auto_bootup));
        if (mDateTime.minute < 10 && mDateTime.hour < 10) {
            mTvAutoBootupTime.setText("0" + mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.minute < 10) {
            mTvAutoBootupTime.setText(mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.hour < 10) {
            mTvAutoBootupTime.setText("0" + mDateTime.hour + " : " + mDateTime.minute);
        } else {
            mTvAutoBootupTime.setText(mDateTime.hour + " : " + mDateTime.minute);
        }
    }

    @Override
    public void refreshUI(View view) {
        int id = view.getId();
        if (id == R.id.iv_auto_bootup) {
            refreshPlan();
        }
    }


    @Override
    public void initListener() {

        mRlAutoBootup.setOnClickListener(this);
        mRlAutoBootupTime.setOnClickListener(this);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
    }
    /**
     * 定时关机时间设置是否为空
     * @return
     */
    private boolean isOnTimerEmpty() {
        TimeUtil scheduleTimeForShutdown = HHTTimeManager.getInstance().getScheduleTimeForBoot();
        if (scheduleTimeForShutdown.week.size()==0) {
            Log.d(TAG, "isOffTimerEmpty true");
            return true;
        }
        return false;
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        //定时关机开关
        //保存按钮
        if (id == R.id.rl_auto_bootup) {
            isOnTimerEnable = !isOnTimerEnable;
            if (isOnTimerEmpty()){
                TimeUtil onTime = new TimeUtil();
                onTime.min = mDateTime.minute;
                onTime.hour = mDateTime.hour;
                List<String> mList = new ArrayList<>();
                mList.add(TimeUtil.EnumWeek.MON.toString());
                mList.add(TimeUtil.EnumWeek.TUE.toString());
                mList.add(TimeUtil.EnumWeek.WED.toString());
                mList.add(TimeUtil.EnumWeek.THU.toString());
                mList.add(TimeUtil.EnumWeek.FRI.toString());
                onTime.week = mList;
                Log.d("TimeUtil", "NewSettings onTime:" + onTime);
                HHTTimeManager.getInstance().setScheduleTimeForBoot(onTime);
            }


            refreshUI(mActivity.findViewById(R.id.iv_auto_bootup));
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, 500);
        } else if (id == R.id.btn_save) {
            saveDialog.show();
            saveDialog.setPlan(HHTTimeManager.getInstance().getScheduleTimeForBoot());
        } else if (id == R.id.rl_auto_bootup_time) {
            saveDialog.show();
            saveDialog.setPlan(HHTTimeManager.getInstance().getScheduleTimeForBoot());
        }
    }

    Runnable runnable = new Runnable() {
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


        saveDialog.dismiss();
        if (mDateTime.minute < 10 && mDateTime.hour < 10) {
            mTvAutoBootupTime.setText("0" + mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.minute < 10) {
            mTvAutoBootupTime.setText(mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.hour < 10) {
            mTvAutoBootupTime.setText("0" + mDateTime.hour + " : " + mDateTime.minute);
        } else {
            mTvAutoBootupTime.setText(mDateTime.hour + " : " + mDateTime.minute);
        }
        refreshPlan();
    }

    public void onKeyDown(int keycode) {
    }

    private void refreshPlan() {
        TimeUtil onTime = HHTTimeManager.getInstance().getScheduleTimeForBoot();
        mDateTime.minute = onTime.min;
        mDateTime.hour = onTime.hour;
        if (isOnTimerEnable) {
            mIvAutoBootup.setImageResource(R.mipmap.on);
            planTitle.setText(getPlanTitle(onTime));
            item_plan.setVisibility(View.VISIBLE);
        } else {
            mIvAutoBootup.setImageResource(R.mipmap.off);
            planTitle.setText(getPlanTitle(onTime));
            item_plan.setVisibility(View.GONE);
        }
    }

    /**
     * 获取定时开机计划
     *
     * @param timeUtil
     * @return
     */
    private String getPlanTitle(TimeUtil timeUtil) {
        StringBuilder stringBuilder = new StringBuilder();
        String str = "　";
        stringBuilder.append(mActivity.getString(R.string.text_shutdown));
        stringBuilder.append(str);
        if (mDateTime.minute < 10 && mDateTime.hour < 10) {
            stringBuilder.append("0" + mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.minute < 10) {
            stringBuilder.append(mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.hour < 10) {
            stringBuilder.append("0" + mDateTime.hour + " : " + mDateTime.minute);
        } else {
            stringBuilder.append(mDateTime.hour + " : " + mDateTime.minute);
        }
        stringBuilder.append(str);
        stringBuilder.append(getRepeatString(timeUtil.week));
        Log.d(TAG, "getPlanTitle:" + stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * list转String
     *
     * @param list
     * @return
     */
    private String getRepeatString(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        String str = "、";
        if (list.contains(TimeUtil.EnumWeek.MON.toString())) {
            stringBuilder.append(mActivity.getString(R.string.text_monday));
            stringBuilder.append(str);
        }
        if (list.contains(TimeUtil.EnumWeek.TUE.toString())) {
            stringBuilder.append(mActivity.getString(R.string.text_tuesday));
            stringBuilder.append(str);
        }
        if (list.contains(TimeUtil.EnumWeek.WED.toString())) {
            stringBuilder.append(mActivity.getString(R.string.text_wednesday));
            stringBuilder.append(str);
        }
        if (list.contains(TimeUtil.EnumWeek.THU.toString())) {
            stringBuilder.append(mActivity.getString(R.string.text_thursday));
            stringBuilder.append(str);
        }
        if (list.contains(TimeUtil.EnumWeek.FRI.toString())) {
            stringBuilder.append(mActivity.getString(R.string.text_friday));
            stringBuilder.append(str);
        }
        if (list.contains(TimeUtil.EnumWeek.SAT.toString())) {
            stringBuilder.append(mActivity.getString(R.string.text_saturday));
            stringBuilder.append(str);
        }
        if (list.contains(TimeUtil.EnumWeek.SUN.toString())) {
            stringBuilder.append(mActivity.getString(R.string.text_sunday));
            stringBuilder.append(str);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}
