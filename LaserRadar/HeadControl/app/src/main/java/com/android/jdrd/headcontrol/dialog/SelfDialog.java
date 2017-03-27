package com.android.jdrd.headcontrol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.android.jdrd.headcontrol.common.MyTimePicker;
import com.android.jdrd.headcontrol.common.MyTimerPicker1;

import java.util.ArrayList;
import java.util.List;
import com.android.jdrd.headcontrol.R;
/**
 * Created by Administrator on 2016/11/3 0003.
 */

public class SelfDialog extends Dialog {

    ImageView imageView_queding_no;
    ImageView imageView_queding_per;
    ImageView imageView_quxiao_no;
    ImageView imageView_quxiao_per;
    MyTimerPicker1 mMyTimePicker_Start_Hour;
    MyTimerPicker1 mMyTimePicker_Start_Minute;
    MyTimePicker mMyTimePicker_End_Hour;
    MyTimePicker mMyTimePicker_End_Minute;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器

    String yesStr;
    String noStr;

    Handler handler;
    int start_hour;
    int start_minute;
    int end_hour;
    int end_minute;
    String start_hour_handler;
    String start_minute_handler;
    String end_hour_handler;
    String end_minute_handler;

    /**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public SelfDialog(Context context, Handler handler, int start_hour, int start_minute, int end_hour, int end_minute) {
        super(context, R.style.MyDialog);
        Log.d("tag","start_hour:"+start_hour);
        Log.d("tag","start_minute:"+start_minute);
        Log.d("tag","end_hour:"+end_hour);
        Log.d("tag","end_minute:"+end_minute);
        this.handler=handler;
        this.start_hour=start_hour;
        this.start_minute=start_minute;
        this.end_hour=end_hour;
        this.end_minute=end_minute;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_timepicker1);

        imageView_queding_no= (ImageView)findViewById(R.id.iv_ImageView_queding_no);
        imageView_queding_per= (ImageView) findViewById(R.id.iv_ImageView_queding_per);
        imageView_quxiao_no= (ImageView) findViewById(R.id.iv_ImageView_quxiao_no);
        imageView_quxiao_per= (ImageView) findViewById(R.id.iv_ImageView_quxiao_per);
        //开始
        mMyTimePicker_Start_Hour = (MyTimerPicker1) findViewById(R.id.mtp_Start_Hour1);
        mMyTimePicker_Start_Minute = (MyTimerPicker1) findViewById(R.id.mtp_Start_Minute1);
        //结束
        mMyTimePicker_End_Hour = (MyTimePicker) findViewById(R.id.mtp_End_Hour1);
        mMyTimePicker_End_Minute = (MyTimePicker) findViewById(R.id.mtp_End_Minute1);

        /*if (start_hour<10){
            start_hour_handler = "0"+start_hour;
        }else {
            start_hour_handler = start_hour+"";
        }

        if (start_minute<10){
            start_minute_handler = "0"+start_minute;
        }else {
            start_minute_handler = start_minute+"";
        }

        if (end_hour<10){
            end_hour_handler = "0"+end_hour;
        }else {
            end_hour_handler = end_hour+"";
        }

        if (end_minute<10){
            end_minute_handler = "0"+end_minute;
        }else {
            end_minute_handler = end_minute+"";
        }*/

        imageView_queding_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_queding_no.setVisibility(View.GONE);
                imageView_queding_per.setVisibility(View.VISIBLE);
                Message message=new Message();
                Bundle bundle=new Bundle();
                bundle.putString("start_hour_handler",start_hour_handler);
                bundle.putString("start_minute_handler",start_minute_handler);
                bundle.putString("end_hour_handler",end_hour_handler);
                bundle.putString("end_minute_handler",end_minute_handler);
                message.what=1;
                message.setData(bundle);
                handler.sendMessage(message);
                SelfDialog.this.dismiss();
            }
        });
        imageView_quxiao_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_quxiao_no.setVisibility(View.GONE);
                imageView_quxiao_per.setVisibility(View.VISIBLE);
                SelfDialog.this.dismiss();
            }
        });

        //创建两个集合分别用来装长度 我们的MyPickerView继承view 没有void onTimeChanged(TimePicker view, int hourOfDay, int minute);
        List<String> list_hour_start1 = new ArrayList<String>();
        List<String> list_minute_start1 = new ArrayList<String>();
        List<String> list_hour_end1 = new ArrayList<String>();
        List<String> list_minute_end1 = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            list_hour_start1.add(i<10?"0" + i:""+i);//添加0
            list_hour_end1.add(i<10?"0" + i:""+i);//添加0
        }
        for (int i = 0; i < 60; i++) {
            list_minute_start1.add(i < 10 ? "0" + i : "" + i);
            list_minute_end1.add(i < 10 ? "0" + i : "" + i);
        }

        mMyTimePicker_Start_Hour.setData(list_hour_start1);
        mMyTimePicker_Start_Hour.setSelected(start_hour);//设置默认开始小时
        mMyTimePicker_Start_Hour.setOnSelectListener(new MyTimerPicker1.onSelectListener() {
            @Override
            public void onSelect(String text) {
                start_hour_handler=text;
            }
        });
        mMyTimePicker_Start_Minute.setData(list_minute_start1);
        mMyTimePicker_Start_Minute.setSelected(start_minute);//设置默认开始分钟
        mMyTimePicker_Start_Minute.setOnSelectListener(new MyTimerPicker1.onSelectListener() {
            @Override
            public void onSelect(String text) {
                start_minute_handler=text;
            }
        });

        mMyTimePicker_End_Hour.setData(list_hour_end1);
        mMyTimePicker_End_Hour.setSelected(end_hour);//设置结束默认小时
        mMyTimePicker_End_Hour.setOnSelectListener(new MyTimePicker.onSelectListener() {
            @Override
            public void onSelect(String text) {
                end_hour_handler=text;
            }
        });
        mMyTimePicker_End_Minute.setData(list_minute_end1);
        mMyTimePicker_End_Minute.setSelected(end_minute);//设置结束默认分钟
        mMyTimePicker_End_Minute.setOnSelectListener(new MyTimePicker.onSelectListener() {
            @Override
            public void onSelect(String text) {
                end_minute_handler=text;
            }
        });

    }

    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {

    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {

    }

    /**
     * 初始化界面控件
     */
    private void initView() {

    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
//        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
//        messageStr = message;
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }

}
