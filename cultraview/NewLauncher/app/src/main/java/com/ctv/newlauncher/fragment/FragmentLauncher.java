package com.ctv.newlauncher.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.ctv.ctvlauncher.R;
import com.ctv.newlauncher.utils.ExcutorPool;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.device.HHTDeviceManager;
import com.mstar.android.tv.TvManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Future;

public class FragmentLauncher {
    private static final int HANDLERMSG_UPDATETIME = 1;
    private static final String TAG = "FragmentLauncher";
    private OnClickListener buttonClickListener;
    private View.OnFocusChangeListener buttonFooucesListener;
    private View buttonMore;
    private View buttonWhiteboard;
    private View buttonWirelessShare;
    private View contentView;
    private View documentonMore;
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            if (message.what == 1) {
                FragmentLauncher.this.updateShowTime();
            }

            super.handleMessage(message);
        }
    };
    private Calendar mCalendar;
    private OnButtonClickListener onButtonClickListener = new OnButtonClickListener();
    private OnbuttonFoucesChangeListener onbuttonFoucesChangeListener =new OnbuttonFoucesChangeListener();
    private TextView textviewData;
    private TextView textviewTime;
    private TextView textViewTemp;
    private TextView light_tv;
    private SeekBar temp_seekbar;
    private ImageView img_board_temperature;

    private SimpleDateFormat timeFormat;
    private UpdateTimeRunnable updateTimeRunnable;
    private String[] weekStrings;
    private SimpleDateFormat yearFormat;
    private ImageView network_state_iv;

    public  IntentFilter intentFilter;

    private class OnbuttonFoucesChangeListener implements View.OnFocusChangeListener {
            private OnbuttonFoucesChangeListener(){

            }
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (FragmentLauncher.this.buttonFooucesListener != null) {
                FragmentLauncher.this.buttonFooucesListener.onFocusChange(v,hasFocus);
            }
        }
    }
    private class OnButtonClickListener implements OnClickListener {
        private OnButtonClickListener() {

        }


        public void onClick(View view) {
            if (FragmentLauncher.this.buttonClickListener != null) {
                FragmentLauncher.this.buttonClickListener.onClick(view);
            }
        }
    }

    private class UpdateTimeRunnable implements Runnable {
        private boolean isStarted;
        private Future updateTimeFurture;

        private UpdateTimeRunnable() {
            this.isStarted = false;
        }

        public void start() {
            this.isStarted = true;
            FragmentLauncher.this.timeFormat = new SimpleDateFormat("HH:mm");
            FragmentLauncher.this.  mCalendar = Calendar.getInstance();
            FragmentLauncher.this.yearFormat = new SimpleDateFormat("yyyy年M月d日");
            this.updateTimeFurture = ExcutorPool.submit(this);
        }

        public void stop() {
            this.isStarted = false;
            Future future = this.updateTimeFurture;
            if (future != null) {
                future.cancel(true);
            }
        }

        public void run() {
            while (this.isStarted) {
               FragmentLauncher.this.updateTime();
               FragmentLauncher.this.updateTime();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public FragmentLauncher(View view) {
        this.contentView = view;

        intentFilter = new IntentFilter();

        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");

        init();
    }
        private void inittvtime(){
        contentView.findViewById(R.id.textview_time);
        contentView.findViewById(R.id.textview_date);
        }
    private Context getContext() {
        return this.contentView.getContext();
    }

    private void init() {
        this.network_state_iv = this.contentView.findViewById(R.id.network_state_iv);
        this.buttonWhiteboard = this.contentView.findViewById(R.id.button_whiteboard);
        this.buttonMore = this.contentView.findViewById(R.id.button_more);
        this.documentonMore = this.contentView.findViewById(R.id.button_document);
        this.buttonWirelessShare = this.contentView.findViewById(R.id.button_sharescreen);
        this.textviewTime = (TextView) this.contentView.findViewById(R.id.textview_time);
        this.textviewData = (TextView) this.contentView.findViewById(R.id.textview_date);
        this.textViewTemp = (TextView) this.contentView.findViewById(R.id.temp_tv);
        this.light_tv = (TextView) this.contentView.findViewById(R.id.light_tv);
        this.temp_seekbar = (SeekBar) this.contentView.findViewById(R.id.temp_seekbar);
        this.temp_seekbar.setEnabled(false);
        this.img_board_temperature = (ImageView) this.contentView.findViewById(R.id.img_board_temperature);
        this.buttonWhiteboard.setOnClickListener(this.onButtonClickListener);
        this.buttonMore.setOnClickListener(this.onButtonClickListener);
        this.documentonMore.setOnClickListener(this.onButtonClickListener);
        this.buttonWirelessShare.setOnClickListener(this.onButtonClickListener);
        this.weekStrings = getContext().getResources().getStringArray(R.array.weeks);
        this.network_state_iv.setOnClickListener(this.onButtonClickListener);

        initFocuse();
        start();
    }
    private void initFocuse(){
        this.buttonWhiteboard.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.buttonWirelessShare.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.documentonMore.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.buttonMore.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);


    }

    public void start() {
        UpdateTimeRunnable updateTimeRunnable = this.updateTimeRunnable;
        if (updateTimeRunnable != null) {
            updateTimeRunnable.stop();
            this.updateTimeRunnable = null;
        }
        this.updateTimeRunnable = new UpdateTimeRunnable();
        this.updateTimeRunnable.start();
        this.contentView.clearFocus();
    }

    public void stop() {
        UpdateTimeRunnable updateTimeRunnable = this.updateTimeRunnable;
        if (updateTimeRunnable != null) {
            updateTimeRunnable.stop();
            this.updateTimeRunnable = null;
        }
    }

    public void setOnButtonClickListener(OnClickListener onClickListener) {
        this.buttonClickListener = onClickListener;
    }
    public void setButtonFooucesListener(View.OnFocusChangeListener onFoucesChangeListener){
        this.buttonFooucesListener = onFoucesChangeListener;
    }
    private void updateTime() {
        this.handler.sendEmptyMessage(1);
    }
    public void updateTime(String time, String date,String week, int network){
        Log.d("hhc", "updateTime: network ="+network);
        if(network_state_iv!=null&&network!=0){
            Log.d("hhc", "updateTime: setBackgroundResource(network)");
            network_state_iv.setBackgroundResource(network);
        }
    }
    public void updateTime(int network){
        if(network_state_iv!=null&&network!=0){
            Log.d("hhc", "updateTime: setBackgroundResource(network)");
            network_state_iv.setBackgroundResource(network);
        }
    }


    private void updateShowTime() {
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        this.textviewTime.setText(this.timeFormat.format(this.mCalendar.getTime()));
        String format = this.yearFormat.format(this.mCalendar.getTime());
        String str = this.weekStrings[this.mCalendar.get(7) - 1];
        TextView textView = this.textviewData;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(format);
        stringBuilder.append("   ");
        stringBuilder.append(str);
        textView.setText(stringBuilder.toString());


        int temp_value = HHTDeviceManager.getInstance().getTempSensorValue();
        setSeekBar(temp_value);

        int light_value = HHTDeviceManager.getInstance().getLightSensorValue();
        Log.e("hhc","light_value"+light_value);
        light_tv.setText(light_value+"lx");

    }

    private void setSeekBar(int temp) {
        float temp_value = temp;
        // int vlaue = SystemProperties.getInt("test_tmp", 0);
        // temp_value = temp_value +vlaue;
        if (temp_value < 45) {
            img_board_temperature.setBackgroundResource(R.mipmap.board_temperature_circle_green);
            temp_seekbar.setProgressDrawable(getContext().getResources().getDrawable(
                    R.drawable.layer_board_temperature_green));
        } else if (temp_value < 65) {
            img_board_temperature.setBackgroundResource(R.mipmap.board_temperature_circle_orange);
            temp_seekbar.setProgressDrawable(getContext().getResources().getDrawable(
                    R.drawable.layer_board_temperature_orange));
        } else {
            img_board_temperature.setBackgroundResource(R.mipmap.board_temperature_circle_red);
            temp_seekbar.setProgressDrawable(getContext().getResources().getDrawable(
                    R.drawable.layer_board_temperature_red));
        }
        textViewTemp.setText(temp_value + "℃");
        if (temp_value < 0) {
            temp_value = 0;
        } else if (temp_value > 100) {
            temp_value = 100;
        }
        temp_seekbar.setProgress((int) temp_value);
    }



}
