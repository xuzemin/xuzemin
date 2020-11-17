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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.timeanddate.dialog.ShutdownSaveDialog;
import com.hht.android.sdk.time.HHTTimeManager;
import com.hht.android.sdk.time.util.TimeUtil;
import com.mstar.android.tv.TvTimerManager;

import java.util.ArrayList;
import java.util.List;

public class AutoShutdownViewHolder extends BaseViewHolder implements View.OnClickListener {
    private final String TAG = "AutoShutdownViewHolder";
    private View mRlAutoShutdown;
    private ImageView mIvAutoShutdown;
    private Time mDateTime;
    private ShutdownSaveDialog saveDialog;
    private View btnBack;
    private TextView mTvBackTitle;
    private Handler mHandler = new Handler() {
        @Override
        public String getMessageName(Message message) {
            return super.getMessageName(message);
        }
    };
    private boolean isOffTimerEnable;
    private View mRlAutoShutdownTime;
    private TextView mTvAutoShutdownTime;
    private TextView planTitle;
    private LinearLayout item_plan;
    private TimeUtil offTime;


    public AutoShutdownViewHolder(Activity activity) {
        super(activity);
    }

    @Override
    public void initUI(Activity activity) {
        mRlAutoShutdown = mActivity.findViewById(R.id.rl_auto_shutdown);
        mIvAutoShutdown = (ImageView) mActivity.findViewById(R.id.iv_auto_shutdown);
        saveDialog = new ShutdownSaveDialog(mActivity, this);
        mTvBackTitle = (TextView) mActivity.findViewById(R.id.back_title);
        btnBack = mActivity.findViewById(R.id.back_btn);
        mRlAutoShutdownTime = mActivity.findViewById(R.id.rl_auto_shutdown_time);
        mTvAutoShutdownTime = (TextView) mActivity.findViewById(R.id.tv_auto_shutdown_time);
        planTitle = mActivity.findViewById(R.id.tv_title);


        item_plan = mActivity.findViewById(R.id.ll_plan_item);

    }

    @Override
    public void initData(Activity activity) {
        //定时关机开关状态
        isOffTimerEnable = HHTTimeManager.getInstance().isScheduleTimeShutdownEnable();
        //关机时间状态
        mDateTime = TvTimerManager.getInstance().getCurrentTvTime();
        offTime = HHTTimeManager.getInstance().getScheduleTimeForShutdown();
        mDateTime.minute = offTime.min;
        mDateTime.hour = offTime.hour;
        if (isOffTimerEnable) {
            mIvAutoShutdown.setImageResource(R.mipmap.on);
            planTitle.setText(getPlanTitle(isOffTimerEnable, offTime));
            item_plan.setVisibility(View.VISIBLE);
        } else {
            mIvAutoShutdown.setImageResource(R.mipmap.off);
            planTitle.setText(getPlanTitle(isOffTimerEnable, offTime));
            item_plan.setVisibility(View.GONE);
        }
        mTvBackTitle.setText(mActivity.getResources().getString(R.string.title_auto_shutdown));

        if (mDateTime.minute < 10 && mDateTime.hour < 10) {
            mTvAutoShutdownTime.setText("0" + mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.minute < 10) {
            mTvAutoShutdownTime.setText(mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.hour < 10) {
            mTvAutoShutdownTime.setText("0" + mDateTime.hour + " : " + mDateTime.minute);
        } else {
            mTvAutoShutdownTime.setText(mDateTime.hour + " : " + mDateTime.minute);
        }
    }

    @Override
    public void refreshUI(View view) {
        int id = view.getId();
        if (id == R.id.iv_auto_shutdown) {
            refreshPlan();
        }
    }

    private void refreshPlan() {
        TimeUtil offTime = HHTTimeManager.getInstance().getScheduleTimeForShutdown();
        mDateTime.minute = offTime.min;
        mDateTime.hour = offTime.hour;
        if (isOffTimerEnable) {
            mIvAutoShutdown.setImageResource(R.mipmap.on);
            planTitle.setText(getPlanTitle(isOffTimerEnable, offTime));
            item_plan.setVisibility(View.VISIBLE);
        } else {
            mIvAutoShutdown.setImageResource(R.mipmap.off);
            planTitle.setText(getPlanTitle(isOffTimerEnable, offTime));
            item_plan.setVisibility(View.GONE);
        }
    }

    @Override
    public void initListener() {

        mRlAutoShutdown.setOnClickListener(this);
        mRlAutoShutdownTime.setOnClickListener(this);
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
        if (id == R.id.rl_auto_shutdown) {
            isOffTimerEnable = !isOffTimerEnable;
            if (isOffTimerEmpty()) {
                TimeUtil offTime = new TimeUtil();
                offTime.min = mDateTime.minute;
                offTime.hour = mDateTime.hour;
                List<String> mList = new ArrayList<>();
                mList.add(TimeUtil.EnumWeek.MON.toString());
                mList.add(TimeUtil.EnumWeek.TUE.toString());
                mList.add(TimeUtil.EnumWeek.WED.toString());
                mList.add(TimeUtil.EnumWeek.THU.toString());
                mList.add(TimeUtil.EnumWeek.FRI.toString());
                offTime.week = mList;
                Log.d("TimeUtil", "NewSettings offTime:" + offTime);
                HHTTimeManager.getInstance().setScheduleTimeForShutdown(offTime);
            }
            refreshUI(mActivity.findViewById(R.id.iv_auto_shutdown));
            mHandler.removeCallbacks(runnable);
            mHandler.postDelayed(runnable, 500);
        } else if (id == R.id.btn_save) {
            saveDialog.show();
            saveDialog.setPlan(HHTTimeManager.getInstance().getScheduleTimeForShutdown());
        } else if (id == R.id.rl_auto_shutdown_time) {
            saveDialog.show();
            saveDialog.setPlan(HHTTimeManager.getInstance().getScheduleTimeForShutdown());
        }
    }

    /**
     * 定时关机时间设置是否为空
     * @return
     */
    private boolean isOffTimerEmpty() {
        TimeUtil scheduleTimeForShutdown = HHTTimeManager.getInstance().getScheduleTimeForShutdown();
        if (scheduleTimeForShutdown.hour == 0 && scheduleTimeForShutdown.min == 0) {
            Log.d(TAG, "isOffTimerEmpty true");
            return true;
        }
        return false;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HHTTimeManager.getInstance().setScheduleTimeShutdownEnable(isOffTimerEnable);
                }
            }).start();
        }
    };

    public void hintDialog() {
        saveDialog.dismiss();
    }

    public void save(TimePicker timePicker) {
        mDateTime.minute = timePicker.getMinute();
        mDateTime.hour = timePicker.getHour();
        saveDialog.dismiss();
        if (mDateTime.minute < 10 && mDateTime.hour < 10) {
            mTvAutoShutdownTime.setText("0" + mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.minute < 10) {
            mTvAutoShutdownTime.setText(mDateTime.hour + " : 0" + mDateTime.minute);
        } else if (mDateTime.hour < 10) {
            mTvAutoShutdownTime.setText("0" + mDateTime.hour + " : " + mDateTime.minute);
        } else {
            mTvAutoShutdownTime.setText(mDateTime.hour + " : " + mDateTime.minute);
        }
        refreshPlan();
    }

    public void onKeydown(int keyCode) {

    }

    /**
     * 获取定时关机计划
     *
     * @param isOnTimerEnable
     * @param timeUtil
     * @return
     */
    private String getPlanTitle(boolean isOnTimerEnable, TimeUtil timeUtil) {
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
