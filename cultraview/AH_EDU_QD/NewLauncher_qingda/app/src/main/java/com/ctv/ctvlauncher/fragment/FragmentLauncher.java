package com.ctv.ctvlauncher.fragment;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.utils.BroadcastUtil;
import com.ctv.ctvlauncher.utils.ExcutorPool;
import com.ctv.ctvlauncher.utils.TimeUtil;

import java.security.cert.CertPathValidatorException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Future;

public class FragmentLauncher {
    private static final int HANDLERMSG_UPDATETIME = 1;
    private static final String TAG = "FragmentLauncher";
    private OnClickListener buttonClickListener;
    private View.OnFocusChangeListener buttonFooucesListener;
    private View.OnLongClickListener buttononlongclicklistener;
    private View.OnTouchListener buttononTouchlistener;
    private String date;
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
    private OnButtonLongclicklistener onButtonLongclicklistener=new OnButtonLongclicklistener();
    private OnButtonOnTouchListener onButtonOnTouchListener= new OnButtonOnTouchListener();
    public TextView textviewData;
    public TextView textviewTime;
    private SimpleDateFormat timeFormat;
    private UpdateTimeRunnable updateTimeRunnable;
    private String[] weekStrings;
    private SimpleDateFormat yearFormat;
    private ImageView network_state_iv;

    public  IntentFilter intentFilter;
    private RelativeLayout bottom_ll;
    private TextView textviweapm;
    public LinearLayout date_apm;
    private View buttonbrowser;

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
    private class  OnButtonLongclicklistener implements View.OnLongClickListener{
            private OnButtonLongclicklistener(){

            }
        @Override
        public boolean onLongClick(View v) {
            if (FragmentLauncher.this.buttononlongclicklistener !=null){
                FragmentLauncher.this.buttononlongclicklistener.onLongClick(v);
            }
            return true;
        }
    }
    private class OnButtonOnTouchListener implements View.OnTouchListener{
        private OnButtonOnTouchListener(){


        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (FragmentLauncher.this.buttononTouchlistener!=null){
                FragmentLauncher.this.buttononTouchlistener.onTouch(v,event);
            }
            return true;
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
            FragmentLauncher.this.yearFormat = new SimpleDateFormat("yyyy/M/d");
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
                try {
                    Thread.sleep(5000);
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
        buttonbrowser = this.contentView.findViewById(R.id.button_browser);
        this.network_state_iv = this.contentView.findViewById(R.id.network_state_iv);
        this.buttonWhiteboard = this.contentView.findViewById(R.id.button_whiteboard);
        this.buttonMore = this.contentView.findViewById(R.id.button_more);
        this.documentonMore = this.contentView.findViewById(R.id.button_document);
        this.bottom_ll = this.contentView.findViewById(R.id.bottom_ll);
        this.buttonWirelessShare = this.contentView.findViewById(R.id.button_sharescreen);
        this.textviewTime =  this.contentView.findViewById(R.id.textview_time);
        this.date_apm = this.contentView.findViewById(R.id.date_apm);
        this.textviweapm = this.contentView.findViewById(R.id.apm);
        this.textviewData =  this.contentView.findViewById(R.id.textview_date);
        this.bottom_ll.setOnClickListener(this.onButtonClickListener);
        this.buttonWhiteboard.setOnClickListener(this.onButtonClickListener);
        buttonbrowser.setOnClickListener(this.onButtonClickListener);
        this.buttonMore.setOnClickListener(this.onButtonClickListener);
        this.documentonMore.setOnClickListener(this.onButtonClickListener);
        this.buttonWirelessShare.setOnClickListener(this.onButtonClickListener);
        this.weekStrings = getContext().getResources().getStringArray(R.array.weeks);
        this.network_state_iv.setOnClickListener(this.onButtonClickListener);

        //initOntouch();
        initFocuse();
        initlongclick();
        start();
    }
    private void initOntouch(){
        this.buttonWhiteboard.setOnTouchListener(onButtonOnTouchListener);
        this.buttonMore.setOnTouchListener(onButtonOnTouchListener);
        this.documentonMore.setOnTouchListener(onButtonOnTouchListener);
        this.buttonWirelessShare.setOnTouchListener(onButtonOnTouchListener);
    }

    private void initlongclick(){
        this.buttonWhiteboard.setOnLongClickListener(onButtonLongclicklistener);
        this.buttonMore.setOnLongClickListener(onButtonLongclicklistener);
        this.documentonMore.setOnLongClickListener(onButtonLongclicklistener);
        this.buttonWirelessShare.setOnLongClickListener(onButtonLongclicklistener);
    }
    private void initFocuse(){
        this.buttonbrowser.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.buttonWhiteboard.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.buttonWirelessShare.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.documentonMore.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.buttonMore.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
        this.bottom_ll.setOnFocusChangeListener(this.onbuttonFoucesChangeListener);
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
    public void setonlongbuttonclicklistener(View.OnLongClickListener onLongClickListener){
        this.buttononlongclicklistener =onLongClickListener;
    }
    public void setonbuttontouchlistener(View.OnTouchListener onTouchListener){
        this.buttononTouchlistener =onTouchListener;
    }



    public void updateTime() {
        BroadcastUtil.sendnetbroad(getContext());
        this.handler.sendEmptyMessage(1);
    }

    public void updateTime(int network){
        if(network_state_iv!=null&&network!=0){
            Log.d("hhc", "updateTime: setBackgroundResource(network)");
            network_state_iv.setBackgroundResource(network);
        }
    }
    private void diateee(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh-mm", Locale.getDefault());
        String format = simpleDateFormat.format(new Date());
        Log.d(TAG, "diateee: format ="+format);
    }
    private void updateShowTime() {
      //  diateee();
        String flag = Settings.System.getString(getContext().getContentResolver(),Settings.System.TIME_12_24);
        Log.d(TAG, "updateShowTime:   flag  = "+flag);
        int dateFormatIndex = TimeUtil.getDateFormat();
        Calendar now = Calendar.getInstance();
        this.mCalendar.setTimeInMillis(System.currentTimeMillis());
        date = android.text.format.DateFormat.format(TimeUtil.DATE_FORMAT_STRINGS[dateFormatIndex], now).toString();

        String time = DateFormat.getTimeFormat(getContext()).format(Calendar.getInstance().getTime());

        StringBuilder str_time = new StringBuilder();
        if(flag == null){
            flag = "24";
        }
        Log.d(TAG, "updateShowTime:   flag  = "+flag);
        if(flag.equals("12")) {
            String appm  = "";
            String s_hour="";
            String s_min = "";
            int hours = now.get(Calendar.HOUR);
            if (hours <10 || hours >0){
               s_hour = String.format("%02d", hours);
            }
            int min = now.get(Calendar.MINUTE);
            if (min <10 || min>=0){
                s_min = String.format("%02d", min);
            }
            int apm = now.get(Calendar.AM_PM);
            if (apm == 0) {
                appm = getContext().getResources().getString(R.string.am);
            } else if (apm == 1) {
                appm = getContext().getResources().getString(R.string.pm);
            } else {
                appm = "";
            }
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(appm);
            textviweapm.setVisibility(View.VISIBLE);
            textviweapm.setText(stringBuilder2);

            str_time.append(s_hour);
            str_time.append(":");
            str_time.append(s_min);
            Log.d(TAG, "updateShowTime:appm =  "+appm);
        }else{
            textviweapm.setVisibility(View.GONE);
            str_time.append(time);
        }
        //this.textviewTime.setText(this.timeFormat.format(this.mCalendar.getTime()));
        textviewTime.setText(str_time.toString());
        String format = this.yearFormat.format(this.mCalendar.getTime());
        String str = this.weekStrings[this.mCalendar.get(7) - 1];
        TextView textView = this.textviewData;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(date);
        stringBuilder.append("   ");
        stringBuilder.append(str);
        textView.setText(stringBuilder.toString());

    }



}
