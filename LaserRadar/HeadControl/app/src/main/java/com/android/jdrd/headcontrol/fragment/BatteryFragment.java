package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.activity.WelcomeActivity;
import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.database.HeadControlBean;
import com.android.jdrd.headcontrol.database.HeadControlDao;
import com.android.jdrd.headcontrol.dialog.WarningDialog;
import com.android.jdrd.headcontrol.entity.Battery3;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Constant;
import com.android.jdrd.headcontrol.util.IListener;
import com.android.jdrd.headcontrol.util.ListenerManager;
import com.google.gson.Gson;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2016/10/23 0023.
 */

public class BatteryFragment extends BaseFragment implements Animation.AnimationListener,IListener{

    Button mButton_Save;//保存按钮
    ImageView mImageView_Battery_Power;//电池图标
    TextView mTextView_Level;//剩余电量  电量等级


    ImageView mImageView_Switch_Open;
    ImageView mImageView_Switch_Close;

    TextView mTextView_Warn;//警告设置值
    SeekBar mSeekBar;
    MyClickListener mMyClickListener;

    ContentResolver mContentResolver;
    Uri mUri;
    int warn;//电源警告设置值
    //int level = 100;//剩余电量  模拟数据
    int level;//剩余电量
    int state;//
    float temperature;
    float voltage;
    int warn_modified;
    int progres_warn;
    IntentFilter filter;
    private BatteryReceiver receiver = null;

    private RelativeLayout ll_right_bar1;
    boolean flag;
    private View ll_right_bar;
    private ImageView imgViewBtnRight;

    private Context mContext;


//电量值 及运行剩余的时间
    private TextView tv_Battery_Level;
    private TextView tv_Battery_runTime;

    private String power01;

    public BatteryFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public BatteryFragment(Context context) {
        super(context);
    }
    HeadControlDao headControlDaoBF;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {//给当前的fragment绘制UI布局，可以使用线程更新UI
        mView = inflater.inflate(R.layout.fragment_battery, container, false);
        mContext = getActivity();
        receiver = new BatteryReceiver();
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //****
        ListenerManager.getInstance().registerListtener(this);

        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void initView() {
        mButton_Save = (Button) findViewById(R.id.btn_Battery_Save);
        mImageView_Battery_Power = (ImageView) findViewById(R.id.iv_Battery_Power);
        mTextView_Level = (TextView) findViewById(R.id.tv_Battery_Level);
        mImageView_Switch_Open = (ImageView) findViewById(R.id.iv_Battery_Switch_Open);
        mImageView_Switch_Close = (ImageView) findViewById(R.id.iv_Battery_Switch_Close);
        mTextView_Warn = (TextView) findViewById(R.id.tv_Battery_Warn);
        mSeekBar = (SeekBar) findViewById(R.id.sb_SeekBar);

        ll_right_bar1 = (RelativeLayout)findViewById(R.id.ll_right_bar1);
        ll_right_bar = findViewById(R.id.ll_right_bar);
        imgViewBtnRight = (ImageView) findViewById(R.id.imgViewBtnRight);
        getActivity().registerReceiver(receiver, filter);
        mMyClickListener = new MyClickListener();
        headControlDaoBF = new HeadControlDao(mContext);//初始化数据库
        BatteryThread thread = new BatteryThread();//启动线程
        thread.start();

    }
boolean b=true;
    @Override
    public void initData() {

        HeadControlBean beanBF = headControlDaoBF.query("power");

        if (beanBF ==null){
            beanBF = new HeadControlBean();
            beanBF.setFunction("power");
            beanBF.setDataInt(100);
            boolean add = headControlDaoBF.add(beanBF);
            if (add){
                Constant.debugLog("电源数据添加成功");
            }else {
                Constant.debugLog("电源添加失败");
            }

        }

        //查询数据库中的信息并显示在UI界面上
      //  mContentResolver = getActivity().getContentResolver();

//        if (cursor == null) {
//            //table does not exist弹出对话框（不可点击外屏消失），点确定退出,发送信息给小屏
////           Toast.makeText(getActivity(),"系统出现异常",Toast.LENGTH_SHORT).show();
//           //showWarnDDialog();
//           //Intent intent = new Intent("com.jiadu.broadcast.setting.warn");
//          // intent.putExtra("warn", "{\"warninfo\":{\"type\":\"warn\",\"function\":\"18\",data:\"14\"}}");
//           // getActivity().sendBroadcast(intent);
//
//        } else {
//            while (cursor.moveToNext()) {
//                int id = cursor.getInt(cursor.getColumnIndex("id"));
//                warn = cursor.getInt(cursor.getColumnIndex("warn"));
//                level = cursor.getInt(cursor.getColumnIndex("level"));
//                state = cursor.getInt(cursor.getColumnIndex("state"));
//                temperature = cursor.getFloat(cursor.getColumnIndex("temperature"));
//                voltage = cursor.getFloat(cursor.getColumnIndex("voltage"));
//            }
//            if (cursor != null) {
//                cursor.close();
//            }
//            Log.d("tag", "warn:" + warn + "level:" + level + "state:" + state + " temperature:" + temperature + " voltage:" + voltage);


        //模拟数据
//        if (level==100)
//            b=true;
//        else if (level==0)
//            b=false;
//        if (b){
//            level=level-1;
//        }else {
//            level=level+1;
//        }
              level=beanBF.getDataInt();  //获取数据库值
            mTextView_Level.setText(String.valueOf(level)+"%");
//            progres_warn = warn;
//            mTextView_Warn.setText(String.valueOf(progres_warn) + "%");
//            mSeekBar.setProgress(progres_warn);
//            warn_modified = warn;



            int currentPower = level / 10;
            switch (currentPower) {
                case 0:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power0);
                    break;
                case 1:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power1);
                    break;
                case 2:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power2);
                    break;
                case 3:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power3);
                    break;
                case 4:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power4);
                    break;
                case 5:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power5);
                    break;
                case 6:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power6);
                    break;
                case 7:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power7);
                    break;
                case 8:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power8);
                    break;
                case 9:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power9);
                    break;
                case 10:
                    mImageView_Battery_Power.setImageResource(R.mipmap.power9);
                    break;

            }

            mSeekBar.setMax(100);
//            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {// 当进度发生改变时执行
//                    progres_warn = progress;
//                }
//
//                @Override
//                public void onStartTrackingTouch(SeekBar seekBar) {// 在进度开始改变时执行
//
//                }
//
//                @Override
//                public void onStopTrackingTouch(SeekBar seekBar) {// 停止拖动时执行
//                    warn_modified = progres_warn;
//                    mSeekBar.setProgress(progres_warn);
//                    mTextView_Warn.setText(String.valueOf(progres_warn) + "%");
//                }
//            });
        }

    class BatteryThread extends Thread{
        @Override
        public void run() {
            super.run();
            while (true){

                try {
                    handler.sendEmptyMessage(1);
                    //initData();
                    sleep(5000);
                    Constant.debugLog("===============更新=================");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    initData();
//                    if (headControlDaoBF ==null){
//                        Constant.debugLog("------电源值为空------"+headControlDaoBF);
//                    }else {
//                        Constant.debugLog("--------电源=值不为空------------"+headControlDaoBF);
//                        Constant.debugLog("==============更新=================");
//
//                    }
//                    HeadControlBean beanBF = headControlDaoBF.query("power");
//                   level = beanBF.getDataInt();
//                    if (0<=level && level<=10){
//
//                    }
//                    Constant.debugLog("*****power电源值02*****"+beanBF.toString());
                    break;
            }
        }
    };


    @Override
    public void initEvent() {
        mButton_Save.setOnClickListener(mMyClickListener);
        mImageView_Switch_Open.setOnClickListener(mMyClickListener);
        mImageView_Switch_Close.setOnClickListener(mMyClickListener);
        imgViewBtnRight.setOnClickListener(mMyClickListener);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        ll_right_bar1.clearAnimation();
        if (flag){
            flag = false;
            imgViewBtnRight.setImageResource(R.mipmap.you_yc);
        }else {
            flag = true;
            ll_right_bar.setVisibility(View.GONE);
            imgViewBtnRight.setImageResource(R.mipmap.you_xs);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void notifyAllActivity(String str) {

    }

    public class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_Battery_Save:

                    break;
                case R.id.iv_Battery_Switch_Open:
                    mImageView_Switch_Open.setVisibility(View.GONE);
                    mImageView_Switch_Close.setVisibility(View.VISIBLE);
                    mSeekBar.setEnabled(false);
                    break;
                case R.id.iv_Battery_Switch_Close:
                    mImageView_Switch_Open.setVisibility(View.VISIBLE);
                    mImageView_Switch_Close.setVisibility(View.GONE);
                    mSeekBar.setEnabled(true);
                    break;

                case R.id.imgViewBtnRight:
                    startAnimationRight();
                    break;
            }
        }
    }


    public class MyContentObserver extends ContentObserver {
        Handler mHandler;
        Runnable mRunnable;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
            mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    Log.d("tag", "电源数据内容变化了...");
                }
            };
        }

        @Override
        public void onChange(boolean selfChange) {
            mHandler.removeCallbacks(mRunnable);
            mHandler.postDelayed(mRunnable, 100);
            super.onChange(selfChange);
        }
    }

//电量警告弹窗
    private void showWarnDDialog() {
        final WarningDialog dialog = new WarningDialog(getActivity());
        dialog.setYesOnclickListener(new WarningDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                dialog.dismiss();
                getActivity().finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {


        }


    }

    //右边动画
    private void startAnimationRight(){
        if (flag){
            ll_right_bar.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,ll_right_bar.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0F
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(BatteryFragment.this);
            ll_right_bar1.startAnimation(translateAnimation);

        }else {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,ll_right_bar.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(false);
            translateAnimation.setAnimationListener(BatteryFragment.this);
            ll_right_bar1.startAnimation(translateAnimation);
        }

    }

}
