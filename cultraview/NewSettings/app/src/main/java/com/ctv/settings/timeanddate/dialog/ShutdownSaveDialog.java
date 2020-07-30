package com.ctv.settings.timeanddate.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TimePicker;

import com.ctv.settings.R;
import com.ctv.settings.timeanddate.holder.AutoShutdownViewHolder;
import com.hht.android.sdk.time.HHTTimeManager;
import com.hht.android.sdk.time.util.TimeUtil;

public class ShutdownSaveDialog extends Dialog {
    private Context mContext;
    private AutoShutdownViewHolder mAutoShutdownViewHolder;
    private TimePicker mTimePicker;
    public ShutdownSaveDialog(Context context, AutoShutdownViewHolder holder) {
        super(context);
        this.mContext=context;
        this.mAutoShutdownViewHolder=holder;
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
        lp.height =480;
        // Range is from 1.0 for completely opaque to 0.0 for no dim.
        w.setDimAmount(0.0f);
        w.setAttributes(lp);
    }
    public void findViews(){
        Button mBtnCancel=(Button)findViewById(R.id.btn_cancel);
        Button mBtnOk=(Button)findViewById(R.id.btn_ok);
        mTimePicker = (TimePicker)findViewById(R.id.timepicker);
        mTimePicker.setIs24HourView(true);

        // HHTApi test start
        TimeUtil offTime = HHTTimeManager.getInstance().getScheduleTimeForShutdown();
        int hour = offTime.hour;
        int minute = offTime.min;
        // HHTApi test start

        if (hour == 0 && minute == 0){
            hour = 8;
            minute = 8;
        }

//        int hour = Settings.System.getInt(mContext.getContentResolver(), "shutdown_hour", 8);
//        int minute = Settings.System.getInt(mContext.getContentResolver(), "shutdown_minute", 8);
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
                mAutoShutdownViewHolder.save(mTimePicker);
            }
        });
    }

}
