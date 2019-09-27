package com.ctv.settings.timeanddate.holder;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.dialog.ShutdownSaveDialog;
import com.mstar.android.tv.TvTimerManager;

public class AutoShutdownViewHolder extends BaseViewHolder implements View.OnClickListener{

    private TimePicker mTimePicker;
    private View mRlAutoShutdown;
    private ImageView mIvAutoShutdown;
    private Time mDateTime;
    private Button mBtnSave;
    private ImageView mIvLeft;
    private ImageView mIvRight;
    String[] REPEAT_VALUES;
    private TextView mTvRepeat;
    private int shutdown_repeat;
    private ShutdownSaveDialog saveDialog;
    private Handler mHandler=new Handler() {
        @Override
        public String getMessageName(Message message) {
            return super.getMessageName(message);
        }
    };
    private boolean isOffTimerEnable;

    public AutoShutdownViewHolder(Activity activity){
        super(activity);
    }

    @Override
    public void initUI(Activity activity) {
        mTimePicker = (TimePicker)mActivity.findViewById(R.id.timepicker);
        mRlAutoShutdown = mActivity.findViewById(R.id.rl_auto_shutdown);
        mIvAutoShutdown = (ImageView) mActivity.findViewById(R.id.iv_auto_shutdown);
        mBtnSave = (Button) mActivity.findViewById(R.id.btn_save);
        mIvLeft = (ImageView) mActivity.findViewById(R.id.iv_left);
        mIvRight = (ImageView) mActivity.findViewById(R.id.iv_right);
        mTvRepeat = (TextView) mActivity.findViewById(R.id.tv_repeat);
        saveDialog = new ShutdownSaveDialog(mActivity,this);
    }

    @Override
    public void initData(Activity activity) {
        //定时关机开关状态
        isOffTimerEnable = TvTimerManager.getInstance().isOffTimerEnable();
        if(isOffTimerEnable){
            mIvAutoShutdown.setImageResource(R.mipmap.on);
        }else{
            mIvAutoShutdown.setImageResource(R.mipmap.off);
        }

        //关机时间状态
        mDateTime = TvTimerManager.getInstance().getCurrentTvTime();
        mDateTime.hour = Settings.System.getInt(mActivity.getContentResolver(), "shutdown_hour", 8);
        mDateTime.minute = Settings.System.getInt(mActivity.getContentResolver(), "shutdown_minute", 8);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setHour(mDateTime.hour);
        mTimePicker.setMinute(mDateTime.minute);

        //重复性，默认关闭
        REPEAT_VALUES = mActivity.getResources().getStringArray(R.array.repeat_values);
        shutdown_repeat = Settings.System.getInt(mActivity.getContentResolver(), "shutdown_repeat", 0);
        mTvRepeat.setText(REPEAT_VALUES[shutdown_repeat]);
    }

    @Override
    public void refreshUI(View view) {
        int id = view.getId();
        if (id == R.id.iv_auto_shutdown) {
            if (isOffTimerEnable) {
                mIvAutoShutdown.setImageResource(R.mipmap.on);
            } else {
                mIvAutoShutdown.setImageResource(R.mipmap.off);
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
                mDateTime.minute=minute;
                mDateTime.hour=hour;
            }
        });

        mRlAutoShutdown.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mIvLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_auto_shutdown) {
            isOffTimerEnable=!isOffTimerEnable;
            refreshUI(mActivity.findViewById(R.id.iv_auto_shutdown));
        } else if (id == R.id.btn_save) {
            saveDialog.show();
        } else if (id == R.id.iv_left) {
            if (shutdown_repeat == 0) {
                mTvRepeat.setText(REPEAT_VALUES[1]);
                shutdown_repeat = 1;
            } else {
                mTvRepeat.setText(REPEAT_VALUES[0]);
                shutdown_repeat = 0;
            }
        } else if (id == R.id.iv_right) {
            if (shutdown_repeat == 0) {
                mTvRepeat.setText(REPEAT_VALUES[1]);
                shutdown_repeat = 1;
            } else {
                mTvRepeat.setText(REPEAT_VALUES[0]);
                shutdown_repeat = 0;
            }
        }
    }
    public void hintDialog(){
        saveDialog.dismiss();
    }

    public void save(){
        Settings.System.putInt(mActivity.getContentResolver(),"shutdown_hour",mDateTime.hour);
        Settings.System.putInt(mActivity.getContentResolver(),"shutdown_minute",mDateTime.minute);
        Settings.System.putInt(mActivity.getContentResolver(),"shutdown_repeat",shutdown_repeat);
        if(isOffTimerEnable){
            Time DateTime = TvTimerManager.getInstance().getCurrentTvTime();
            DateTime.hour=mDateTime.hour;
            DateTime.minute=mDateTime.minute;
            TvTimerManager.getInstance().setTvOffTimer(DateTime);
            Log.i("gyx","isOffTimerEnable="+isOffTimerEnable);
            Settings.System.putInt(mActivity.getContentResolver(),"auto_shutdown",1);
        }else{
            Settings.System.putInt(mActivity.getContentResolver(),"auto_shutdown",0);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TvTimerManager.getInstance().setOffTimerEnable(isOffTimerEnable);
            }
        },500);


        saveDialog.dismiss();
    }

}
