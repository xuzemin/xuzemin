package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.common.MyTimePicker;
import com.android.jdrd.headcontrol.common.MyTimerPicker1;
import com.android.jdrd.headcontrol.database.HeadControlBean;
import com.android.jdrd.headcontrol.dialog.SelfDialog;
import com.android.jdrd.headcontrol.entity.Clean4;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Constant;
import com.android.jdrd.headcontrol.util.JsonPackage;
import com.google.gson.Gson;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.android.jdrd.headcontrol.R;
/**
 * Created by Administrator on 2016/10/25 0025.
 */

public class CleanFragment extends BaseFragment implements Animation.AnimationListener,View.OnClickListener{
    Button mButton_Save;
    private  TextView mTextView_Start_Hour1;
    private TextView mTextView_Start_Minute1;
    private TextView mTextView_End_Hour1;
    private TextView mTextView_End_Minute1;
    private ImageView mImageView_bianji1;
    ImageView mImageView_Switch_Open;
    ImageView mImageView_Switch_Close;
    ImageView mImageView_yaguang_no;
    ImageView mImageView_yaguang_per;
    ImageView mImageView_biaozhun_no;
    ImageView mImageView_biaozhun_per;
    ImageView mImageView_liangguang_no;
    ImageView mImageView_liangguang_per;
   // MyClickListener mMyClickListener;


    ContentResolver mContentResolver;
    Uri mUri_Clean;
    Uri mUri_CleanTime;
    int flag;
    float start;
    float end;
    int type;
    int state;
    int warn;
    int garbage;
    int speed;
    int start_hour;
    int start_minute;
    int end_hour;
    int end_minute;
    int flag_modified;
    float start_modified;
    float end_modified;
    int type_modified=0;

    private RelativeLayout rr_right_bar_clean;
    boolean flag_rr_bar;
    private LinearLayout rr_right_bar_clean1;
    private ImageView img_ViewBtnRightClean;

    private Button btn_clean_time_start,btn_clean_time_stop;
    private Spinner select_clean_time;

    private ImageView add_time_imageView;
    private LinearLayout ll_addTimeLin;
    private ImageView iv_Clean_del;
    private FrameLayout fl_FrameLayout;

    private ArrayAdapter<String> adapter;
    private Context context;
    private float gametimenumber=0;
    private TextView textView001;

    private String saveWaterLevetChange = "";

    private  int saveTime;
    private  int saveTime1;
    timeSelectThread    timeSelect;
    private boolean controlThread;
    String[] languages1;
    public CleanFragment(){
        super();
    }

    @SuppressLint("ValidFragment")
    public CleanFragment(Context context){
        super(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_clean,container,false);

      timeSelect = new timeSelectThread();

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        mButton_Save = (Button) findViewById(R.id.btn_Clean_Save);
        mTextView_Start_Hour1 = (TextView) findViewById(R.id.tv_Clean_Start_Hour1);
        mTextView_Start_Minute1 = (TextView) findViewById(R.id.tv_Clean_Start_Minute1);
        mTextView_End_Hour1 = (TextView) findViewById(R.id.tv_Clean_End_Hour1);
        mTextView_End_Minute1 = (TextView) findViewById(R.id.tv_Clean_End_Minute1);
        ll_addTimeLin = (LinearLayout) findViewById(R.id.ll_addTimeLin);
        mImageView_yaguang_no= (ImageView) findViewById(R.id.iv_Clean_yaguang_no);
        mImageView_yaguang_per= (ImageView) findViewById(R.id.iv_Clean_yaguang_per);
        mImageView_biaozhun_no= (ImageView) findViewById(R.id.iv_Clean_biaozhun_no);
        mImageView_biaozhun_per= (ImageView) findViewById(R.id.iv_Clean_biaozhun_per);
        mImageView_liangguang_no= (ImageView) findViewById(R.id.iv_Clean_liangguang_no);
        mImageView_liangguang_per= (ImageView) findViewById(R.id.iv_Clean_liangguang_per);

        rr_right_bar_clean = (RelativeLayout) findViewById(R.id.rr_right_bar_clean_all);
        rr_right_bar_clean1= (LinearLayout) findViewById(R.id.rr_right_bar_clean1);
        img_ViewBtnRightClean = (ImageView) findViewById(R.id.img_btnViewRightClean);
        add_time_imageView = (ImageView) findViewById(R.id.add_time_imageView);

        btn_clean_time_start = (Button) findViewById(R.id.btn_clean_time_start);
        btn_clean_time_stop = (Button) findViewById(R.id.btn_clean_time_stop);

        select_clean_time = (Spinner) findViewById(R.id.select_clean_time);


    }

    @Override
    public void initData() {

    }
    @Override
    public void initEvent() {
        mButton_Save.setOnClickListener(this);
        add_time_imageView.setOnClickListener(this);
        ll_addTimeLin.setOnClickListener(this);
        mImageView_yaguang_no.setOnClickListener(this);
        mImageView_yaguang_per.setOnClickListener(this);
        mImageView_biaozhun_no.setOnClickListener(this);
        mImageView_biaozhun_per.setOnClickListener(this);
        mImageView_liangguang_no.setOnClickListener(this);
        mImageView_liangguang_per.setOnClickListener(this);
        btn_clean_time_start.setOnClickListener(this);
        btn_clean_time_stop.setOnClickListener(this);
        select_clean_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                String[] languages = getResources().getStringArray(R.array.cleanTimeItem);
               languages1=languages[position].split("分");
                saveTime= Integer.valueOf(languages1[0]);
                 saveTime1=saveTime;
                Constant.debugLog("选择"+saveTime+"分钟");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        // 建立数据源
      //  String[] mItems = getResources().getStringArray(R.array.cleanTimeItem);
        // 建立Adapter并且绑定数据源
       // ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,R.layout.simple_spinner_item, mItems);

//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //绑定 Adapter到控件
//        select_clean_time .setAdapter(adapter);
//        //spinner监听
//        select_clean_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                String[] languages = getResources().getStringArray(R.array.cleanTimeItem);
//              //  Toast.makeText(CleanFragment.this, "你点击的是:"+languages[pos], 2000).show();
//              Constant.debugLog("==点击的行数==="+languages);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        img_ViewBtnRightClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimationRight();
            }
        });
        add_time_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


    }

//
//public  void  aaa(){
//    Timer timer= new Timer(true);
//    timer.schedule(new TimerTask() {
//        @Override
//        public void run() {
//
//        }
//    }, 1000 * 1, 1000 * 60 * 30);
//}



public class timeSelectThread extends Thread{
    @Override
    public void run() {
        super.run();
        while (controlThread)
        {
            if (saveTime>0 ){
                saveTime = saveTime-1;
                Constant.debugLog("=================倒计时==============="+saveTime);
            }else if (saveTime == 0){
                String closeWater = JsonPackage.stringToJson("command", "closeWater", "");
                Constant.debugLog("=============停止清洁==================");
                try {
                    ServerSocketUtil.sendDateToClient(closeWater, Constant.ip_ros);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                controlThread = false;
                saveTime=saveTime1;
            }
            try {
                sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Constant.debugLog("*********start_hour值是：*********"+start_hour);
                    Bundle bundle= msg.getData();
                    if((bundle.getString("start_hour_handler"))==null){
                        if(String.valueOf(start_hour).length()>1){
                            mTextView_Start_Hour1.setText(String.valueOf(start_hour));
                        }else{
                            mTextView_Start_Hour1.setText("0"+ String.valueOf(start_hour));
                        }
                    }else {
                        mTextView_Start_Hour1.setText(bundle.getString("start_hour_handler"));
                    }

                    if((bundle.getString("start_minute_handler"))==null){
                        if(String.valueOf(start_minute).length()>1){
                            mTextView_Start_Minute1.setText(String.valueOf(start_minute));
                        }else {
                            mTextView_Start_Minute1.setText("0"+ String.valueOf(start_minute));
                        }
                    }else {
                        mTextView_Start_Minute1.setText(bundle.getString("start_minute_handler"));
                    }
                    if((bundle.getString("end_hour_handler"))==null){
                        if(String.valueOf(end_hour).length()>1){
                            mTextView_End_Hour1.setText(String.valueOf(end_hour));
                        }else{
                            mTextView_End_Hour1.setText("0"+ String.valueOf(end_hour));
                        }
                    }else {
                        mTextView_End_Hour1.setText(bundle.getString("end_hour_handler"));
                    }
                    if ((bundle.getString("end_minute_handler"))==null){
                        if(String.valueOf(end_minute).length()>1){
                            mTextView_End_Minute1.setText(String.valueOf(end_minute));
                        }else{
                            mTextView_End_Minute1.setText("0"+ String.valueOf(end_minute));
                        }
                    }else{
                        mTextView_End_Minute1.setText(bundle.getString("end_minute_handler"));
                    }
                    break;

            }

        }
    };

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        rr_right_bar_clean.clearAnimation();
        if (flag_rr_bar){
            flag_rr_bar = false;
            img_ViewBtnRightClean.setImageResource(R.mipmap.you_yc);
        }else {
            flag_rr_bar = true;
            rr_right_bar_clean1.setVisibility(View.GONE);
            img_ViewBtnRightClean.setImageResource(R.mipmap.you_xs);
        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
//清洁水量类型
//    @Override
    public void onClick(View v) {
        switch (v.getId()){

//            case R.id.iv_Clean_yaguang_no:
//                setSelect(0);
//                break;
//            case R.id.iv_Clean_biaozhun_per:
//                setSelect(1);
//                break;
//            case R.id.iv_Clean_liangguang_no:
//                setSelect(2);
//                break;


            case R.id.btn_Clean_Save:
                if(type_modified==0){
                    Toast.makeText(getActivity(),"至少选择一种类型", Toast.LENGTH_SHORT).show();
                }else {
//                    save();
                }
                break;
            case R.id.iv_Clean_yaguang_no:
                type_modified=1;
                mImageView_yaguang_per.setVisibility(View.VISIBLE);
                mImageView_yaguang_no.setVisibility(View.GONE);
                mImageView_biaozhun_no.setVisibility(View.VISIBLE);
                mImageView_biaozhun_per.setVisibility(View.GONE);
                mImageView_liangguang_no.setVisibility(View.VISIBLE);
                mImageView_liangguang_per.setVisibility(View.GONE);
                saveWaterLevetChange = "low";
//                String openWaterLow = JsonPackage.stringToJson("command", "openWater", "low");
//                try {
//                    ServerSocketUtil.sendDateToClient(openWaterLow, Constant.ip_ros);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case R.id.iv_Clean_yaguang_per:
                type_modified=0;
                mImageView_yaguang_no.setVisibility(View.VISIBLE);
                mImageView_yaguang_per.setVisibility(View.GONE);
                saveWaterLevetChange = "";
//                String closeWater1 = JsonPackage.stringToJson("command", "closeWater", "");
//                try {
//                    ServerSocketUtil.sendDateToClient(closeWater1, Constant.ip_ros);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case R.id.iv_Clean_biaozhun_no:
                type_modified=2;
                mImageView_biaozhun_per.setVisibility(View.VISIBLE);
                mImageView_biaozhun_no.setVisibility(View.GONE);
                mImageView_yaguang_per.setVisibility(View.GONE);
                mImageView_yaguang_no.setVisibility(View.VISIBLE);
                mImageView_liangguang_no.setVisibility(View.VISIBLE);
                mImageView_liangguang_per.setVisibility(View.GONE);
                saveWaterLevetChange = "middle";
//                String openWaterMiddle = JsonPackage.stringToJson("command", "openWater", "middle");
//                try {
//                    ServerSocketUtil.sendDateToClient(openWaterMiddle, Constant.ip_ros);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case R.id.iv_Clean_biaozhun_per:
                type_modified=0;
                mImageView_biaozhun_no.setVisibility(View.VISIBLE);
                mImageView_biaozhun_per.setVisibility(View.GONE);
                saveWaterLevetChange = "";
//                String closeWater2 = JsonPackage.stringToJson("command", "closeWater", "");
//                try {
//                    ServerSocketUtil.sendDateToClient(closeWater2, Constant.ip_ros);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case R.id.iv_Clean_liangguang_no:
                type_modified=3;
                mImageView_liangguang_per.setVisibility(View.VISIBLE);
                mImageView_liangguang_no.setVisibility(View.GONE);
                mImageView_biaozhun_per.setVisibility(View.GONE);
                mImageView_biaozhun_no.setVisibility(View.VISIBLE);
                mImageView_yaguang_per.setVisibility(View.GONE);
                mImageView_yaguang_no.setVisibility(View.VISIBLE);
                saveWaterLevetChange = "high";
//                String openWaterHigh = JsonPackage.stringToJson("command", "openWater", "high");
//                try {
//                    ServerSocketUtil.sendDateToClient(openWaterHigh, Constant.ip_ros);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;
            case R.id.iv_Clean_liangguang_per:
                type_modified=0;
                mImageView_liangguang_no.setVisibility(View.VISIBLE);
                mImageView_liangguang_per.setVisibility(View.GONE);
                saveWaterLevetChange = "";
//                String closeWater3 = JsonPackage.stringToJson("command", "closeWater", "");
//                try {
//                    ServerSocketUtil.sendDateToClient(closeWater3, Constant.ip_ros);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                break;

            //开始清洁
            case R.id.btn_clean_time_start:
                if (!TextUtils.isEmpty(saveWaterLevetChange)){//当点击开始没选择水量时
                if (!controlThread){
                    controlThread = true;
                        Constant.debugLog("============启动清洁时间==============");
                        String StartWater = JsonPackage.stringToJson("command", "openWater", saveWaterLevetChange);
                        try {
                            ServerSocketUtil.sendDateToClient(StartWater, Constant.ip_ros);
                            Constant.debugLog("=============时间是否下发================");
                            timeSelect=null;
                            timeSelect=new timeSelectThread();
                            timeSelect.start();

                            mImageView_yaguang_no.setEnabled(false);
                            mImageView_yaguang_per.setEnabled(false);
                            mImageView_biaozhun_no.setEnabled(false);
                            mImageView_biaozhun_per.setEnabled(false);
                            mImageView_liangguang_no.setEnabled(false);
                            mImageView_liangguang_per.setEnabled(false);
                            select_clean_time.setEnabled(false);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        Toast.makeText(getActivity(),"清洁已启动",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getActivity(),"请选择水量",Toast.LENGTH_SHORT).show();

                }
                break;
            //结束清洁
            case R.id.btn_clean_time_stop:
                if (controlThread){
                    controlThread=false;
                    saveWaterLevetChange = "";
                    String closeWater = JsonPackage.stringToJson("command", "closeWater", "");
                    try {
                        ServerSocketUtil.sendDateToClient(closeWater, Constant.ip_ros);
                        Constant.debugLog("==========结束清洁时间==============");

                        mImageView_yaguang_no.setEnabled(true);
                        mImageView_yaguang_per.setEnabled(true);
                        mImageView_biaozhun_no.setEnabled(true);
                        mImageView_biaozhun_per.setEnabled(true);
                        mImageView_liangguang_no.setEnabled(true);
                        mImageView_liangguang_per.setEnabled(true);
                        select_clean_time.setEnabled(true);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(getActivity(),"清洁功能已关闭",Toast.LENGTH_SHORT).show();



                    saveClean();
                }
                break;
        }

    }

    public void saveClean(){
//        mImageView_liangguang_per.setVisibility(View.GONE);
        mImageView_liangguang_no.setVisibility(View.VISIBLE);
//        mImageView_biaozhun_per.setVisibility(View.GONE);
        mImageView_biaozhun_no.setVisibility(View.VISIBLE);
//        mImageView_yaguang_per.setVisibility(View.GONE);
        mImageView_yaguang_no.setVisibility(View.VISIBLE);
    }



//    private void addItemTimeLin() {
//
//        final View viewAddItem = this.getActivity().getLayoutInflater().inflate(R.layout.add_item_cleantime,null);
//        ll_addTimeLin.addView(viewAddItem);
//        final TextView mTextView_Start_Hour1= (TextView) viewAddItem.findViewById(R.id.tv_Clean_Start_Hour1);
//        final TextView mTextView_Start_Minute1= (TextView) viewAddItem.findViewById(R.id.tv_Clean_Start_Minute1);
//        final TextView mTextView_End_Hour1= (TextView) viewAddItem.findViewById(R.id.tv_Clean_End_Hour1);
//        final TextView mTextView_End_Minute1= (TextView) viewAddItem.findViewById(R.id.tv_Clean_End_Minute1);
//        final ImageView  mImageView_bianji1= (ImageView) viewAddItem.findViewById(R.id.iv_Clean_bianji1);
//        final ImageView iv_Clean_del = (ImageView) viewAddItem.findViewById(R.id.iv_Clean_del);
//        final ImageView mImageView_Switch_Open= (ImageView) viewAddItem.findViewById(R.id.iv_Clean_Switch_Open);
//        final ImageView  mImageView_Switch_Close= (ImageView) viewAddItem.findViewById(R.id.iv_Clean_Switch_Close);
//        FrameLayout  fl_FrameLayout = (FrameLayout) viewAddItem.findViewById(R.id.fl_FrameLayout);
//
//
//        mImageView_bianji1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mImageView_bianji1.setImageResource(R.mipmap.bianji_pre);
//                showDialogxinjia(mTextView_Start_Hour1, mTextView_Start_Minute1,
//                        mTextView_End_Hour1, mTextView_End_Minute1
//                        , mImageView_bianji1
//                );
//            }
//        });
//        iv_Clean_del.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//            }
//        });
//        fl_FrameLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mImageView_Switch_Open.getVisibility() == View.GONE) {
//
//                } else {
//
//                }
//            }
//        });
//
//
//
//    }


//    /**
//     * 弹出时间编辑对话框
//     */
//                                 String timeState1,timeState2,timeEnd1,timeEnd2;
//    private void showDialogxinjia(final TextView mTextView_Start_Hour1, final TextView mTextView_Start_Minute1,
//                                  final TextView mTextView_End_Hour1, final TextView mTextView_End_Minute1
//            , ImageView mImageView_bianji1) {
//
//        timeState1=mTextView_Start_Hour1.getText().toString();
//        timeState2=mTextView_Start_Minute1.getText().toString();
//        timeEnd1=mTextView_End_Hour1.getText().toString();
//        timeEnd2=mTextView_End_Minute1.getText().toString();
//        AlertDialog.Builder timeDialog = new AlertDialog.Builder(getActivity());
//        View view = this.getActivity().getLayoutInflater().inflate(R.layout.custom_timepicker1, null);
//        timeDialog.setView(view);  //添加view
//        final AlertDialog dialog = timeDialog.create();
//        final ImageView imageView_queding_no = (ImageView) view.findViewById(R.id.iv_ImageView_queding_no);
//        final ImageView imageView_queding_per = (ImageView) view.findViewById(R.id.iv_ImageView_queding_per);
//        final ImageView imageView_quxiao_no = (ImageView) view.findViewById(R.id.iv_ImageView_quxiao_no);
//        final ImageView imageView_quxiao_per = (ImageView) view.findViewById(R.id.iv_ImageView_quxiao_per);
//        //开始
//        final MyTimerPicker1 mMyTimePicker_Start_Hour = (MyTimerPicker1) view.findViewById(R.id.mtp_Start_Hour1);
//        final MyTimerPicker1 mMyTimePicker_Start_Minute = (MyTimerPicker1) view.findViewById(R.id.mtp_Start_Minute1);
//        //结束
//        final MyTimePicker mMyTimePicker_End_Hour = (MyTimePicker) view.findViewById(R.id.mtp_End_Hour1);
//        final MyTimePicker mMyTimePicker_End_Minute = (MyTimePicker) view.findViewById(R.id.mtp_End_Minute1);
//        imageView_queding_no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView_queding_no.setVisibility(View.GONE);
//                imageView_queding_per.setVisibility(View.VISIBLE);
//
//                dialog.dismiss();
//            }
//        });
//        imageView_quxiao_no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageView_quxiao_no.setVisibility(View.GONE);
//                imageView_quxiao_per.setVisibility(View.VISIBLE);
//                dialog.dismiss();
//            }
//        });
//
//        //创建两个集合分别用来装长度 我们的MyPickerView继承view 没有void onTimeChanged(TimePicker view, int hourOfDay, int minute);
//        List<String> list_hour_start1 = new ArrayList<String>();
//        List<String> list_minute_start1 = new ArrayList<String>();
//        List<String> list_hour_end1 = new ArrayList<String>();
//        List<String> list_minute_end1 = new ArrayList<String>();
//        for (int i = 0; i < 24; i++) {
//            list_hour_start1.add(i < 10 ? "0" + i : "" + i);//添加0
//            list_hour_end1.add(i < 10 ? "0" + i : "" + i);//添加0
//        }
//        for (int i = 0; i < 60; i++) {
//            list_minute_start1.add(i < 10 ? "0" + i : "" + i);
//            list_minute_end1.add(i < 10 ? "0" + i : "" + i);
//        }
//
//        mMyTimePicker_Start_Hour.setData(list_hour_start1);
//        mMyTimePicker_Start_Hour.setSelected( Integer.parseInt(timeState1));//设置默认开始小时
//        mMyTimePicker_Start_Hour.setOnSelectListener(new MyTimerPicker1.onSelectListener() {
//            @Override
//            public void onSelect(String text) {
//                //获取开始小时
//                start_hour = Integer.parseInt(text);
//                timeState1=text;
//
//            }
//        });
//        mMyTimePicker_Start_Minute.setData(list_minute_start1);
////        mMyTimePicker_Start_Minute.setSelected(start_minute);//设置默认开始分钟
//        mMyTimePicker_Start_Minute.setSelected(Integer.parseInt(timeState2));//设置默认开始分钟
//        mMyTimePicker_Start_Minute.setOnSelectListener(new MyTimerPicker1.onSelectListener() {
//            @Override
//            public void onSelect(String text) {
//                //获取开始分钟
//                start_minute = Integer.parseInt(text);
////                mTextView_Start_Minute1.setText(String.valueOf(text));
//                timeState2=String.valueOf(text);
//            }
//        });
//
//        mMyTimePicker_End_Hour.setData(list_hour_end1);
//        mMyTimePicker_End_Hour.setSelected(Integer.valueOf(timeEnd1));//设置结束默认小时
//        mMyTimePicker_End_Hour.setOnSelectListener(new MyTimePicker.onSelectListener() {
//            @Override
//            public void onSelect(String text) {
//                //获取结束小时
//                end_hour = Integer.parseInt(text);
//                timeEnd1=String.valueOf(text);
//            }
//        });
//        mMyTimePicker_End_Minute.setData(list_minute_end1);
//        mMyTimePicker_End_Minute.setSelected(Integer.parseInt(timeEnd2));//设置结束默认分钟
//        mMyTimePicker_End_Minute.setOnSelectListener(new MyTimePicker.onSelectListener() {
//            @Override
//            public void onSelect(String text) {
//                //获取结束分钟
//                end_minute = Integer.parseInt(text);
//                timeEnd2=String.valueOf(text);
//            }
//        });
//
//        dialog.show();
//    }

    /**
     * 保存清洁配置修改后的信息
     */
//    private void save(){
//        start_hour= Integer.parseInt(mTextView_Start_Hour1.getText().toString().trim());
//        start_minute= Integer.parseInt(mTextView_Start_Minute1.getText().toString().trim());
//        end_hour= Integer.parseInt(mTextView_End_Hour1.getText().toString().trim());
//        end_minute= Integer.parseInt(mTextView_End_Minute1.getText().toString().trim());
//        start_modified=start_hour+start_minute/100f;
//        end_modified=end_hour+end_minute/100f;
//        //发送广播信息通知小屏上报Ros系统
//        Intent intent=new Intent("com.jiadu.broadcast.setting.clean");
//        Gson gson=new Gson() ;
//        Clean4 clean4=new Clean4();
//        clean4.setType("param");
//        clean4.setFunction("clean");
//        Clean4.DataBean dataBean=new Clean4.DataBean();
//        dataBean.setSurface(type_modified);
//        List list=new ArrayList();
//        Clean4.DataBean.TimerBean timerBean=new Clean4.DataBean.TimerBean();
//        timerBean.setFlag(flag_modified);
//        timerBean.setStarttime(start_modified);
//        timerBean.setEndtime(end_modified);
//        list.add(timerBean);
//        dataBean.setTimer(list);
//        clean4.setData(dataBean);
//        String str = gson.toJson(clean4);
//        Log.d("tag","str:"+str);
//        intent.putExtra("clean",str);
//        getActivity().sendBroadcast(intent);
//        // 注册一个针对ContentProvider的ContentObserver用来观察内容提供者的数据变化
//        final Uri uri = Uri.parse("content://com.jiadu.provider/clean");
//        getActivity().getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler()));
//    }

    public class  MyContentObserver extends ContentObserver {
        Handler mHandler;
        Runnable mRunnable;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
            mHandler=new Handler();
            mRunnable=new Runnable() {
                @Override
                public void run() {
                    Log.d("tag","清洁数据内容变化了...");
                }
            };
        }

        @Override
        public void onChange(boolean selfChange) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable,100);
            super.onChange(selfChange);


        }
    }


    private void startAnimationRight(){
        if (flag_rr_bar){
            rr_right_bar_clean1.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,rr_right_bar_clean1.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0F
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(CleanFragment.this);
            rr_right_bar_clean.startAnimation(translateAnimation);

        }else {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,rr_right_bar_clean1.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(false);
            translateAnimation.setAnimationListener(CleanFragment.this);
            rr_right_bar_clean.startAnimation(translateAnimation);
        }
    }
}
