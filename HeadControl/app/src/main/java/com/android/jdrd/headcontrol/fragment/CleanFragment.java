package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.common.MyTimePicker;
import com.android.jdrd.headcontrol.common.MyTimerPicker1;
import com.android.jdrd.headcontrol.dialog.SelfDialog;
import com.android.jdrd.headcontrol.entity.Clean4;
import com.google.gson.Gson;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import com.android.jdrd.headcontrol.R;
/**
 * Created by Administrator on 2016/10/25 0025.
 */

public class CleanFragment extends BaseFragment implements Animation.AnimationListener{
    Button mButton_Save;
    TextView mTextView_Start_Hour1;
    TextView mTextView_Start_Minute1;
    TextView mTextView_End_Hour1;
    TextView mTextView_End_Minute1;
    ImageView mImageView_bianji1;
    ImageView mImageView_Switch_Open;
    ImageView mImageView_Switch_Close;
    ImageView mImageView_yaguang_no;
    ImageView mImageView_yaguang_per;
    ImageView mImageView_biaozhun_no;
    ImageView mImageView_biaozhun_per;
    ImageView mImageView_liangguang_no;
    ImageView mImageView_liangguang_per;
    MyClickListener mMyClickListener;


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
    private RelativeLayout rr_right_bar_clean1;
    private ImageView img_ViewBtnRightClean;


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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        mButton_Save= (Button) findViewById(R.id.btn_Clean_Save);
        mTextView_Start_Hour1= (TextView) findViewById(R.id.tv_Clean_Start_Hour1);
        mTextView_Start_Minute1= (TextView) findViewById(R.id.tv_Clean_Start_Minute1);
        mTextView_End_Hour1= (TextView) findViewById(R.id.tv_Clean_End_Hour1);
        mTextView_End_Minute1= (TextView) findViewById(R.id.tv_Clean_End_Minute1);
        mImageView_bianji1= (ImageView) findViewById(R.id.iv_Clean_bianji1);
        mImageView_Switch_Open= (ImageView) findViewById(R.id.iv_Clean_Switch_Open);
        mImageView_Switch_Close= (ImageView) findViewById(R.id.iv_Clean_Switch_Close);
        mImageView_yaguang_no= (ImageView) findViewById(R.id.iv_Clean_yaguang_no);
        mImageView_yaguang_per= (ImageView) findViewById(R.id.iv_Clean_yaguang_per);
        mImageView_biaozhun_no= (ImageView) findViewById(R.id.iv_Clean_biaozhun_no);
        mImageView_biaozhun_per= (ImageView) findViewById(R.id.iv_Clean_biaozhun_per);
        mImageView_liangguang_no= (ImageView) findViewById(R.id.iv_Clean_liangguang_no);
        mImageView_liangguang_per= (ImageView) findViewById(R.id.iv_Clean_liangguang_per);

        rr_right_bar_clean = (RelativeLayout) findViewById(R.id.rr_right_bar_clean);
        rr_right_bar_clean1= (RelativeLayout) findViewById(R.id.rr_right_bar_clean1);
        img_ViewBtnRightClean = (ImageView) findViewById(R.id.img_ViewBtnRightClean);

    }

    @Override
    public void initData() {
       /* mMyClickListener=new MyClickListener();
        //查询数据库中的信息并显示在UI界面上
        mContentResolver = getActivity().getContentResolver();
        mUri_Clean = Uri.parse("content://com.jiadu.provider/clean");
        Cursor cursor_clean = mContentResolver.query(mUri_Clean, null, null, null, null);
        while (cursor_clean.moveToNext()) {
            int id = cursor_clean.getInt(cursor_clean.getColumnIndex("id"));
            flag=cursor_clean.getInt(cursor_clean.getColumnIndex("flag"));
            start = cursor_clean.getFloat(cursor_clean.getColumnIndex("start"));
            end = cursor_clean.getFloat(cursor_clean.getColumnIndex("end"));
            type = cursor_clean.getInt(cursor_clean.getColumnIndex("type"));
            state = cursor_clean.getInt(cursor_clean.getColumnIndex("state"));
            warn = cursor_clean.getInt(cursor_clean.getColumnIndex("warn"));
            garbage = cursor_clean.getInt(cursor_clean.getColumnIndex("garbage"));
            speed = cursor_clean.getInt(cursor_clean.getColumnIndex("speed"));
        }
        if(cursor_clean!=null){
            cursor_clean.close();
        }
        Log.d("tag","flag:"+flag+"start:"+start+" end:"+end+" type:"+type+" state:"+state+" garbage:"+garbage+" speed:"+speed);
        flag_modified=flag;
        if(flag_modified==0){
            mImageView_Switch_Open.setVisibility(View.GONE);
            mImageView_Switch_Close.setVisibility(View.VISIBLE);
        }
        DecimalFormat df=new DecimalFormat("##0");
        start_hour=(int)start;//得到开始小时
        float result1= (start-start_hour)*100;
        start_minute= Integer.parseInt(df.format(result1));//得到开始分钟
        end_hour=(int)end;//得到结束小时
        float result2=(end-end_hour)*100;
        end_minute= Integer.parseInt(df.format(result2));//得到结束分钟
        if(String.valueOf(start_hour).length()>1){
            mTextView_Start_Hour1.setText(String.valueOf(start_hour));
        }else{
            mTextView_Start_Hour1.setText("0"+ String.valueOf(start_hour));
        }
        if(String.valueOf(start_minute).length()>1){
            mTextView_Start_Minute1.setText(String.valueOf(start_minute));
        }else {
            mTextView_Start_Minute1.setText("0"+ String.valueOf(start_minute));
        }
        if(String.valueOf(end_hour).length()>1){
            mTextView_End_Hour1.setText(String.valueOf(end_hour));
        }else{
            mTextView_End_Hour1.setText("0"+ String.valueOf(end_hour));
        }
        if(String.valueOf(end_minute).length()>1){
            mTextView_End_Minute1.setText(String.valueOf(end_minute));
        }else{
            mTextView_End_Minute1.setText("0"+ String.valueOf(end_minute));
        }


        if(type==1){
            mImageView_yaguang_per.setVisibility(View.VISIBLE);
            mImageView_yaguang_no.setVisibility(View.GONE);
        }else  if(type==2){
            mImageView_biaozhun_per.setVisibility(View.VISIBLE);
            mImageView_biaozhun_no.setVisibility(View.GONE);
        }else {
            mImageView_liangguang_per.setVisibility(View.VISIBLE);
            mImageView_liangguang_no.setVisibility(View.GONE);
        }*/
    }
    @Override
    public void initEvent() {
        mButton_Save.setOnClickListener(mMyClickListener);
        mImageView_Switch_Open.setOnClickListener(mMyClickListener);
        mImageView_Switch_Close.setOnClickListener(mMyClickListener);
        mImageView_bianji1.setOnClickListener(mMyClickListener);
        mImageView_yaguang_no.setOnClickListener(mMyClickListener);
        mImageView_yaguang_per.setOnClickListener(mMyClickListener);
        mImageView_biaozhun_no.setOnClickListener(mMyClickListener);
        mImageView_biaozhun_per.setOnClickListener(mMyClickListener);
        mImageView_liangguang_no.setOnClickListener(mMyClickListener);
        mImageView_liangguang_per.setOnClickListener(mMyClickListener);
        rr_right_bar_clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimationRight();
            }
        });
    }

    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
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

    public  class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_Clean_Save:
                    if(type_modified==0){
                        Toast.makeText(getActivity(),"至少选择一种类型", Toast.LENGTH_SHORT).show();
                    }else {
                        save();
                    }
                    break;
                case R.id.iv_Clean_Switch_Open:
                    mImageView_Switch_Open.setVisibility(View.GONE);
                    mImageView_Switch_Close.setVisibility(View.VISIBLE);
                    flag_modified=0;
                    break;
                case R.id.iv_Clean_Switch_Close:
                    mImageView_Switch_Open.setVisibility(View.VISIBLE);
                    mImageView_Switch_Close.setVisibility(View.GONE);
                    break;
                case R.id.iv_Clean_bianji1:
                    mImageView_bianji1.setImageResource(R.mipmap.bianji_pre);
//                    showDialogxinjia();
                    SelfDialog selfDialog=new SelfDialog(getActivity(),handler,start_hour,start_minute,end_hour,end_minute);
                    selfDialog.show();

                    break;
                case R.id.iv_Clean_yaguang_no:
                    type_modified=1;
                    mImageView_yaguang_per.setVisibility(View.VISIBLE);
                    mImageView_yaguang_no.setVisibility(View.GONE);
                    mImageView_biaozhun_no.setVisibility(View.VISIBLE);
                    mImageView_biaozhun_per.setVisibility(View.GONE);
                    mImageView_liangguang_no.setVisibility(View.VISIBLE);
                    mImageView_liangguang_per.setVisibility(View.GONE);
                    break;
                case R.id.iv_Clean_yaguang_per:
                    type_modified=0;
                    mImageView_yaguang_no.setVisibility(View.VISIBLE);
                    mImageView_yaguang_per.setVisibility(View.GONE);
                    break;
                case R.id.iv_Clean_biaozhun_no:
                    type_modified=2;
                    mImageView_biaozhun_per.setVisibility(View.VISIBLE);
                    mImageView_biaozhun_no.setVisibility(View.GONE);
                    mImageView_yaguang_per.setVisibility(View.GONE);
                    mImageView_yaguang_no.setVisibility(View.VISIBLE);
                    mImageView_liangguang_no.setVisibility(View.VISIBLE);
                    mImageView_liangguang_per.setVisibility(View.GONE);
                    break;
                case R.id.iv_Clean_biaozhun_per:
                    type_modified=0;
                    mImageView_biaozhun_no.setVisibility(View.VISIBLE);
                    mImageView_biaozhun_per.setVisibility(View.GONE);
                    break;
                case R.id.iv_Clean_liangguang_no:
                    type_modified=3;
                    mImageView_liangguang_per.setVisibility(View.VISIBLE);
                    mImageView_liangguang_no.setVisibility(View.GONE);
                    mImageView_biaozhun_per.setVisibility(View.GONE);
                    mImageView_biaozhun_no.setVisibility(View.VISIBLE);
                    mImageView_yaguang_per.setVisibility(View.GONE);
                    mImageView_yaguang_no.setVisibility(View.VISIBLE);
                    break;
                case R.id.iv_Clean_liangguang_per:
                    type_modified=0;
                    mImageView_liangguang_no.setVisibility(View.VISIBLE);
                    mImageView_liangguang_per.setVisibility(View.GONE);
                    break;

            }
        }
    }

    /**
     *  弹出时间编辑对话框
     */
    private void showDialogxinjia(){
        AlertDialog.Builder timeDialog = new AlertDialog.Builder(getActivity());
        View view= this.getActivity().getLayoutInflater().inflate(R.layout.custom_timepicker1,null);
        timeDialog.setView(view);  //添加view
        final AlertDialog dialog = timeDialog.create();
        final ImageView imageView_queding_no= (ImageView) view.findViewById(R.id.iv_ImageView_queding_no);
        final ImageView imageView_queding_per= (ImageView) view.findViewById(R.id.iv_ImageView_queding_per);
        final ImageView imageView_quxiao_no= (ImageView) view.findViewById(R.id.iv_ImageView_quxiao_no);
        final ImageView imageView_quxiao_per= (ImageView) view.findViewById(R.id.iv_ImageView_quxiao_per);
        //开始
        final MyTimerPicker1 mMyTimePicker_Start_Hour = (MyTimerPicker1) view.findViewById(R.id.mtp_Start_Hour1);
        final MyTimerPicker1 mMyTimePicker_Start_Minute = (MyTimerPicker1) view.findViewById(R.id.mtp_Start_Minute1);
        //结束
        final MyTimePicker mMyTimePicker_End_Hour = (MyTimePicker) view.findViewById(R.id.mtp_End_Hour1);
        final MyTimePicker mMyTimePicker_End_Minute = (MyTimePicker) view.findViewById(R.id.mtp_End_Minute1);
        imageView_queding_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_queding_no.setVisibility(View.GONE);
                imageView_queding_per.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        imageView_quxiao_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView_quxiao_no.setVisibility(View.GONE);
                imageView_quxiao_per.setVisibility(View.VISIBLE);
                dialog.dismiss();
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
                //获取开始小时
                start_hour= Integer.parseInt(text);
                mTextView_Start_Hour1.setText(text);
            }
        });
        mMyTimePicker_Start_Minute.setData(list_minute_start1);
        mMyTimePicker_Start_Minute.setSelected(start_minute);//设置默认开始分钟
        mMyTimePicker_Start_Minute.setOnSelectListener(new MyTimerPicker1.onSelectListener() {
            @Override
            public void onSelect(String text) {
                //获取开始分钟
                start_minute= Integer.parseInt(text);
                mTextView_Start_Minute1.setText(String.valueOf(text));
            }
        });

        mMyTimePicker_End_Hour.setData(list_hour_end1);
        mMyTimePicker_End_Hour.setSelected(end_hour);//设置结束默认小时
        mMyTimePicker_End_Hour.setOnSelectListener(new MyTimePicker.onSelectListener() {
            @Override
            public void onSelect(String text) {
                //获取结束小时
                end_hour= Integer.parseInt(text);
                mTextView_End_Hour1.setText(String.valueOf(text));
            }
        });
        mMyTimePicker_End_Minute.setData(list_minute_end1);
        mMyTimePicker_End_Minute.setSelected(end_minute);//设置结束默认分钟
        mMyTimePicker_End_Minute.setOnSelectListener(new MyTimePicker.onSelectListener() {
            @Override
            public void onSelect(String text) {
                //获取结束分钟
                end_minute= Integer.parseInt(text);
                mTextView_End_Minute1.setText(String.valueOf(text));
            }
        });


//        timeDialog.setTitle("2016");
//        timeDialog.setIcon(null);
//        timeDialog.setCancelable(false);

//        timeDialog.setPositiveButton("确定",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // 取出MyTimePicker设定的时间就行了
//                    }
//                }).create();
        dialog.show();
    }

    /**
     * 保存清洁配置修改后的信息
     */
    private void save(){
        start_hour= Integer.parseInt(mTextView_Start_Hour1.getText().toString().trim());
        start_minute= Integer.parseInt(mTextView_Start_Minute1.getText().toString().trim());
        end_hour= Integer.parseInt(mTextView_End_Hour1.getText().toString().trim());
        end_minute= Integer.parseInt(mTextView_End_Minute1.getText().toString().trim());
        start_modified=start_hour+start_minute/100f;
        end_modified=end_hour+end_minute/100f;
        //发送广播信息通知小屏上报Ros系统
        Intent intent=new Intent("com.jiadu.broadcast.setting.clean");
        Gson gson=new Gson() ;
        Clean4 clean4=new Clean4();
        clean4.setType("param");
        clean4.setFunction("clean");
        Clean4.DataBean dataBean=new Clean4.DataBean();
        dataBean.setSurface(type_modified);
        List list=new ArrayList();
        Clean4.DataBean.TimerBean timerBean=new Clean4.DataBean.TimerBean();
        timerBean.setFlag(flag_modified);
        timerBean.setStarttime(start_modified);
        timerBean.setEndtime(end_modified);
        list.add(timerBean);
        dataBean.setTimer(list);
        clean4.setData(dataBean);
        String str = gson.toJson(clean4);
        Log.d("tag","str:"+str);
        intent.putExtra("clean",str);
        getActivity().sendBroadcast(intent);
        // 注册一个针对ContentProvider的ContentObserver用来观察内容提供者的数据变化
        final Uri uri = Uri.parse("content://com.jiadu.provider/clean");
        getActivity().getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler()));
    }

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
