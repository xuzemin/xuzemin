package com.android.jdrd.headcontrol.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.fragment.BatteryFragment;
import com.android.jdrd.headcontrol.fragment.CleanFragment;
import com.android.jdrd.headcontrol.fragment.MapFragment;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Contact;

import java.util.ArrayList;
import java.util.List;


/**
 * http://www.cnblogs.com/smyhvae/p/3983234.html
 * Created by Administrator on 2016/10/23 0023.
 */

public class WelcomeActivity extends Activity {

    public boolean IsMap = false;
    public static boolean IsClean = false;
    RelativeLayout mRelativeLayout_Exit;//设置栏
    ImageView mImageView_Exit;//设置栏中的返回键
    TextView mTextView_Exit;//“设置”

    RelativeLayout mRelativeLayout_Battery;//电源栏
    ImageView mImageView_Battery;//电源图标
    ImageView mImageView_Battery_you;//电源栏中的右键

    RelativeLayout mRelativeLayout_Clean;//清洁栏
    ImageView mImageView_Clean;//清洁图标
    ImageView mImageView_Clean_you;//清洁右键

    RelativeLayout mRelativeLayout_Map;//电源栏
    ImageView mImageView_Map;//电源图标
    ImageView mImageView_Map_you;//电源栏中的右键

    MyClickListener mMyClickListener;
    List<BaseFragment> list;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    MapFragment.Istouch = false;
                    MapFragment.Isplan = false;
                    if(IsClean){
                        changeClean();
                    }else{
                        changeBattery();
                        IsMap = false;
                    }
                    break;
                case 2:
                    IsMap = true;
                    //do nothing
                    break;
                case 3:

                    break;
                case 4:
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //启动后台通讯服务
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        startService(serverSocket);


        Intent testActivity = new Intent(this, TestActivity.class);
        startActivity(testActivity);


        list = new ArrayList<>();
        BatteryFragment batteryFragment = new BatteryFragment(WelcomeActivity.this);
        CleanFragment cleanFragment = new CleanFragment(WelcomeActivity.this);
        MapFragment mapFragment = new MapFragment(WelcomeActivity.this);
        list.add(batteryFragment);
        list.add(cleanFragment);
        list.add(mapFragment);
        //步骤一：添加一个FragmentTransaction的实例
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //步骤二：用add()方法加上Fragment的对象rightFragment
//        BatteryFragment batteryFragment = new BatteryFragment(WelcomeActivity.this);
//        transaction.add(R.id.ll_right, batteryFragment, "batteryFragment");
//        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        transaction.addToBackStack(null);//将Fragment添加到返回栈中,添加到Activity管理的回退栈中。


        //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
//        transaction.commit();

        //当第一次打开app的时候去查询应用信息并且查入到数据库
        /*sharedPreferencesUtils = new SharedPreferencesUtils(this);
        if (sharedPreferencesUtils.getPreferenceValue(SharedPreferencesUtils.PREFERENCE_IS_FIRST,false)==false){
            //第一次进入app
            handler.sendEmptyMessage(1);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            initData();
        }
        initEvent();
    }

    private void initView() {

        mRelativeLayout_Exit = (RelativeLayout) findViewById(R.id.rl_Exit);
        mImageView_Exit = (ImageView) findViewById(R.id.iv_Exit);
        mTextView_Exit = (TextView) findViewById(R.id.tv_Exit);
        mRelativeLayout_Battery = (RelativeLayout) findViewById(R.id.rl_Battery);
        mRelativeLayout_Clean = (RelativeLayout) findViewById(R.id.rl_Clean);
        mRelativeLayout_Map = (RelativeLayout) findViewById(R.id.rl_Map);

        mImageView_Exit = (ImageView) findViewById(R.id.iv_Exit);

        mImageView_Battery = (ImageView) findViewById(R.id.iv_Battery);
        mImageView_Battery_you = (ImageView) findViewById(R.id.iv_Battery_you);

        mImageView_Clean = (ImageView) findViewById(R.id.iv_Clean);
        mImageView_Clean_you = (ImageView) findViewById(R.id.iv_Clean_you);

        mImageView_Map = (ImageView) findViewById(R.id.iv_Map);
        mImageView_Map_you = (ImageView) findViewById(R.id.iv_Map_you);
        setBackgroundColor();
        mRelativeLayout_Battery.setBackgroundColor(android.graphics.Color.parseColor("#80313033"));
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void initData() {
        FragmentManager fragmentManager_battery = getFragmentManager();
        FragmentTransaction transaction_battery = fragmentManager_battery.beginTransaction();
        transaction_battery.replace(R.id.ll_right, list.get(0), "batteryFragment");
        transaction_battery.commit();
        mMyClickListener = new MyClickListener();
    }


    private void initEvent() {

        mRelativeLayout_Battery.setOnClickListener(mMyClickListener);
        mRelativeLayout_Clean.setOnClickListener(mMyClickListener);
        mRelativeLayout_Map.setOnClickListener(mMyClickListener);

    }


    /*@Override
    public boolean onTouchEvent(MotionEvent event) {
        //监听屏幕是否处于在点击状态，若被点击则发送广播到小屏'
        Intent intent=new Intent("com.jiadu.broadcast.setting.touch");
        sendBroadcast(intent);
        return super.onTouchEvent(event);
    }*/

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        LogUtils.log("点击发送广播");
        //监听屏幕是否处于在点击状态，若被点击则发送广播到小屏'
        Intent intent = new Intent("com.jiadu.broadcast.setting.touch");
        sendBroadcast(intent);
        return super.dispatchTouchEvent(ev);
    }

    public class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_Exit:
                case R.id.tv_Exit:
                case R.id.iv_Exit:
                    WelcomeActivity.this.finish();
                    break;
                //点击了电源栏
                case R.id.rl_Battery:
                    IsClean = false;
                    if(IsMap){
                        if(MapFragment.Istouch || MapFragment.Isplan){
                            Contact.showWarntext(WelcomeActivity.this,handler);
                        }else{
                            changeBattery();
                            IsMap = false;
                        }
                    }else{
                        changeBattery();
                        IsMap = false;
                    }
                    break;
                case R.id.rl_Clean:
                    IsClean = true;
                    if(IsMap){
                        if(MapFragment.Istouch || MapFragment.Isplan){
                            Contact.showWarntext(WelcomeActivity.this,handler);
                        }else{
                            changeClean();
                            IsMap = false;
                        }
                    }else{
                        changeClean();
                        IsMap = false;
                    }
                    break;
                case R.id.rl_Map:
                    if(!IsMap){
                        IsMap = true;
                        changeMap();
                    }
                    break;
            }
        }
    }

    private void setClickable() {
        mRelativeLayout_Battery.setClickable(true);
        mRelativeLayout_Clean.setClickable(true);
        mRelativeLayout_Map.setClickable(true);
    }

    private void setBackgroundColor() {
        //电源栏
        mRelativeLayout_Battery.setBackgroundColor(android.graphics.Color.parseColor("#B3313033"));
        mImageView_Battery.setImageResource(R.mipmap.dianyuan_no);
        mImageView_Battery_you.setImageResource(R.mipmap.you_no);
        //清扫栏
        mRelativeLayout_Clean.setBackgroundColor(android.graphics.Color.parseColor("#B3313033"));
        mImageView_Clean.setImageResource(R.mipmap.qingjie_no);
        mImageView_Clean_you.setImageResource(R.mipmap.you_no);
        //地图栏
        mRelativeLayout_Map.setBackgroundColor(android.graphics.Color.parseColor("#B3313033"));
        mImageView_Map.setImageResource(R.mipmap.qingjie_no);
        mImageView_Map_you.setImageResource(R.mipmap.you_no);
    }
    private void changeBattery(){
        setClickable();
        setBackgroundColor();
        mRelativeLayout_Battery.setClickable(false);
        //电源栏
        mRelativeLayout_Battery.setBackgroundColor(android.graphics.Color.parseColor("#80313033"));
        mImageView_Battery.setImageResource(R.mipmap.dianyuan_per);
        mImageView_Battery_you.setImageResource(R.mipmap.you_per);
        FragmentManager fragmentManager_battery = getFragmentManager();
        FragmentTransaction transaction_battery = fragmentManager_battery.beginTransaction();
        transaction_battery.replace(R.id.ll_right, list.get(0), "batteryFragment");
        transaction_battery.commit();
    }
    private void changeClean() {
        setClickable();
        setBackgroundColor();
        mRelativeLayout_Clean.setClickable(false);
        mRelativeLayout_Clean.setBackgroundColor(android.graphics.Color.parseColor("#80313033"));
        mImageView_Clean.setImageResource(R.mipmap.qingjie_per);
        mImageView_Clean_you.setImageResource(R.mipmap.you_per);
        FragmentManager fragmentManager_Clean = getFragmentManager();
        FragmentTransaction transaction_clean = fragmentManager_Clean.beginTransaction();
        transaction_clean.replace(R.id.ll_right, list.get(1), "cleanFragment");
        transaction_clean.commit();
    }
    private void changeMap(){
        setClickable();
        setBackgroundColor();
        mRelativeLayout_Map.setClickable(false);
        mRelativeLayout_Map.setBackgroundColor(android.graphics.Color.parseColor("#80313033"));
        mImageView_Map.setImageResource(R.mipmap.qingjie_per);
        mImageView_Map_you.setImageResource(R.mipmap.you_per);
        FragmentManager fragmentManager_Map = getFragmentManager();
        FragmentTransaction transaction_map = fragmentManager_Map.beginTransaction();
        transaction_map.replace(R.id.ll_right, list.get(02), "cleanFragment");
        transaction_map.commit();
    }
}
