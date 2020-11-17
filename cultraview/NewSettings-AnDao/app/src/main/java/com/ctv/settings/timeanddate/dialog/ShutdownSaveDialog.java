package com.ctv.settings.timeanddate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TimePicker;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.timeanddate.holder.AutoShutdownViewHolder;
import com.hht.android.sdk.time.HHTTimeManager;
import com.hht.android.sdk.time.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class ShutdownSaveDialog extends Dialog {
    private Context mContext;
    private AutoShutdownViewHolder mAutoShutdownViewHolder;
    private TimePicker mTimePicker;

    private CheckBox cb_sunday;
    private CheckBox cb_monday;
    private CheckBox cb_tuesday;
    private CheckBox cb_wednesday;
    private CheckBox cb_thursday;
    private CheckBox cb_friday;
    private CheckBox cb_saturday;

    public ShutdownSaveDialog(Context context, AutoShutdownViewHolder holder) {
        super(context);
        this.mContext = context;
        this.mAutoShutdownViewHolder = holder;
        setWindowStyle();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_shutdown_layout);
        findViews();
    }

    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = mContext.getResources();
        Drawable drab = res.getDrawable(R.drawable.button_save_shape);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.width = 680;
        lp.height = 480;
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        w.setDimAmount(0.0f);
        w.setAttributes(lp);
    }

    public void findViews() {
        Button mBtnCancel = (Button) findViewById(R.id.btn_cancel);
        Button mBtnOk = (Button) findViewById(R.id.btn_ok);
        mTimePicker = (TimePicker) findViewById(R.id.timepicker);
        mTimePicker.setIs24HourView(true);

        cb_sunday = findViewById(R.id.cb_off_sunday);
        cb_monday = findViewById(R.id.cb_off_monday);
        cb_tuesday = findViewById(R.id.cb_off_tuesday);
        cb_wednesday = findViewById(R.id.cb_off_wednesday);
        cb_thursday = findViewById(R.id.cb_off_thursday);
        cb_friday = findViewById(R.id.cb_off_friday);
        cb_saturday = findViewById(R.id.cb_off_saturday);
        // HHTApi test start
        TimeUtil offTime = HHTTimeManager.getInstance().getScheduleTimeForShutdown();
        int hour = offTime.hour;
        int minute = offTime.min;

        mTimePicker.setHour(hour);
        mTimePicker.setMinute(minute);
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAutoShutdownViewHolder.hintDialog();
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setShutDownTime();
            }
        });
    }

    private void setShutDownTime() {
        Log.d("ShutdownSaveDialog", "setShutDownTime:cb_sunday.isChecked() " + cb_sunday.isChecked()
                + "cb_monday.isChecked()：" + cb_monday.isChecked() + cb_tuesday.isChecked() + cb_wednesday.isChecked() + cb_thursday.isChecked()
                + cb_friday.isChecked() + cb_saturday.isChecked());
        if (!cb_sunday.isChecked() && !cb_monday.isChecked() && !cb_tuesday.isChecked() && !cb_wednesday.isChecked()
                && !cb_thursday.isChecked() && !cb_friday.isChecked() && !cb_saturday.isChecked()) {
            Toast.makeText(getContext(), mContext.getString(R.string.please_set_repeatability), Toast.LENGTH_SHORT).show();
            return;
        }

        int hour = mTimePicker.getHour();
        int minute = mTimePicker.getMinute();
        TimeUtil offTime = new TimeUtil();
        offTime.min = minute;
        offTime.hour = hour;
        List<String> mList = new ArrayList<>();
        if (cb_sunday.isChecked()) {
            mList.add(TimeUtil.EnumWeek.SUN.toString());
        }
        if (cb_monday.isChecked()) {
            mList.add(TimeUtil.EnumWeek.MON.toString());
        }
        if (cb_tuesday.isChecked()) {
            mList.add(TimeUtil.EnumWeek.TUE.toString());

        }
        if (cb_wednesday.isChecked()) {
            mList.add(TimeUtil.EnumWeek.WED.toString());
        }

        if (cb_thursday.isChecked()) {
            mList.add(TimeUtil.EnumWeek.THU.toString());
        }
        if (cb_friday.isChecked()) {
            mList.add(TimeUtil.EnumWeek.FRI.toString());
        }
        if (cb_saturday.isChecked()) {
            mList.add(TimeUtil.EnumWeek.SAT.toString());
        }
        offTime.week = mList;
        Log.d("TimeUtil", "NewSettings offTime:" + offTime);
        HHTTimeManager.getInstance().setScheduleTimeForShutdown(offTime);
        mAutoShutdownViewHolder.save(mTimePicker);
    }

    private void refreshUI() {

    }

    public void setPlan(TimeUtil mDateTime) {
        if (mDateTime.week == null || mDateTime.week.size() == 0) {
            cb_saturday.setChecked(false);
            cb_sunday.setChecked(false);
            cb_monday.setChecked(true);
            cb_tuesday.setChecked(true);
            cb_wednesday.setChecked(true);
            cb_thursday.setChecked(true);
            cb_friday.setChecked(true);
        } else {
            setRepeat(mDateTime.week);
        }
    }


    private void setRepeat(List<String> list) {
        cb_saturday.setChecked(false);
        cb_sunday.setChecked(false);
        cb_monday.setChecked(false);
        cb_tuesday.setChecked(false);
        cb_wednesday.setChecked(false);
        cb_thursday.setChecked(false);
        cb_friday.setChecked(false);
        if (list.contains(TimeUtil.EnumWeek.MON.toString())) {
            cb_monday.setChecked(true);
        }
        if (list.contains(TimeUtil.EnumWeek.TUE.toString())) {
            cb_tuesday.setChecked(true);
        }
        if (list.contains(TimeUtil.EnumWeek.WED.toString())) {
            cb_wednesday.setChecked(true);
        }
        if (list.contains(TimeUtil.EnumWeek.THU.toString())) {
            cb_thursday.setChecked(true);
        }
        if (list.contains(TimeUtil.EnumWeek.FRI.toString())) {
            cb_friday.setChecked(true);
        }
        if (list.contains(TimeUtil.EnumWeek.SAT.toString())) {
            cb_saturday.setChecked(true);

        }
        if (list.contains(TimeUtil.EnumWeek.SUN.toString())) {
            cb_sunday.setChecked(true);
        }

    }

}
