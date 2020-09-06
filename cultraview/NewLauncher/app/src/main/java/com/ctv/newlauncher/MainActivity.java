package com.ctv.newlauncher;

import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.ctv.ctvlauncher.R;
import com.ctv.newlauncher.fragment.FragmentAppGride;
import com.ctv.newlauncher.fragment.FragmentLauncher;
import com.ctv.newlauncher.service.Theme1TimeService;
import com.ctv.newlauncher.utils.TimeUtil;


public class MainActivity extends FragmentActivity implements View.OnClickListener,View.OnFocusChangeListener{
    private static final String DOCUMENT_PKG = "com.jrm.localmm";
    private static final String TAG = "MainActivity";
    private static final String WHITEBOARD_PKG = "com.mphotool.whiteboard";
    private static final String NETMEETING_PKG = "com.mysher.mtalk";
    private static final String NETWORKSET= "com.ctv.settings";
    private FragmentAppGride fragmentAppGride;
    private FragmentLauncher fragmentLauncher;
    private View viewLauncher;
    private View viewMore;
    private View rootLayout;
    private AnimationSet animationSet;
    private TimeBroadcastReceiver mReceiver = new TimeBroadcastReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.viewLauncher = findViewById(R.id.layout_launcher);
        this.viewMore = findViewById(R.id.layout_grid);
       starttimeservice();
        this.fragmentLauncher = new FragmentLauncher(this.viewLauncher);
        this.fragmentAppGride = new FragmentAppGride(this.viewMore,this);
        this.fragmentLauncher.setOnButtonClickListener(this);
        this.fragmentLauncher.setButtonFooucesListener(this);

        rootLayout = findViewById(R.id.main);

        initamin();
        registerLoginBroadcast();
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        // TODO: 2019-10-23 qkmin 8386适配 打开热点
        mWifiManager.setWifiEnabled(true);
    }
    private void initamin(){
        animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.25f, 1.0f, 1.25f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setDuration(100);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);
    }
    private void starttimeservice(){
        Intent intent = new Intent(MainActivity.this, Theme1TimeService.class);
        Log.d("hhc", "startservice: ");
        startService(intent);
    }
    @Override
    protected void onResume() {
        super.onResume();
        final Drawable drawable = WallpaperManager.getInstance(this).getDrawable();
        rootLayout.setBackground(drawable);
        Log.i("gyx","onResume");
    }

    @Override
    public void onBackPressed() {
        if (viewLauncher.getVisibility() == View.INVISIBLE){
            exitViewMore();
        }
    }

    public void onClick(View view) {
        if (view.getId() == R.id.button_more) {
            enterViewMore();
        } else if (view.getId() == R.id.button_whiteboard) {
            enterWhiteboard();
        } else if (view.getId() == R.id.button_document) {
            enterDocument();
        } else if (view.getId() == R.id.button_sharescreen) {
            enterNetMeeting();
        }else if(view.getId() == R.id.network_state_iv){
            enterNetWork();

        }
    }
    class TimeBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
//            int year = intent.getIntExtra("year", 0);
//            int month = intent.getIntExtra("month", 0);
//            int day = intent.getIntExtra("day", 0);
//            int hour = intent.getIntExtra("hour", 0);
//            int minute = intent.getIntExtra("minute", 0);
            int network = intent.getIntExtra("network", 0);
//            hour = TimeUtil.checkHourStatus(context, hour);
//            String week = intent.getStringExtra("week");
//            String time = TimeUtil.changeTimeFormat(hour) + ":" + TimeUtil.changeTimeFormat(minute);
//            String date = null;
//            int pos = Integer.parseInt(SystemProperties.get("persist.sys.dateformat", "2"));
//            if (pos == 0) {
//                date = TimeUtil.changeTimeFormat(month) + "/" + TimeUtil.changeTimeFormat(day) + "/" + year;
//            } else if (pos == 1) {
//                date = TimeUtil.changeTimeFormat(day) + "/" + TimeUtil.changeTimeFormat(month) + "/" + year;
//            } else if (pos == 2) {
//                date = year + "/" + TimeUtil.changeTimeFormat(month) + "/" + TimeUtil.changeTimeFormat(day);
//            }

            if (fragmentLauncher != null) {
                Log.d("TimeUtil", "onReceive:network ="+network);
                fragmentLauncher.updateTime( network );
            }
           /* if(fragmentother!=null){
                fragmentother.updateTime(time ,date);
            }*/
            //  time_tv.setText(TimeUtil.changeTimeFormat(hour)+":"+TimeUtil.changeTimeFormat(minute));
            //  date_tv.setText(TimeUtil.changeTimeFormat(year)+"-"+TimeUtil.changeTimeFormat(month)+"-"+TimeUtil.changeTimeFormat(day)+" "+ week);

        }
    }
    private void registerLoginBroadcast() {
        IntentFilter intentFilter = new IntentFilter(Theme1TimeService.ACTION_TYPE_TIME);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);

    }
    private void unRegisterLoginBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterLoginBroadcast();
    }

    private void enterDocument() {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(DOCUMENT_PKG);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装文件管理器，请先安装文件管理器应用", Toast.LENGTH_LONG).show();
        } else {
            startActivity(launchIntentForPackage);
        }
    }
    private void enterNetWork(){
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(NETWORKSET);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装白板应用，请先安装设置", Toast.LENGTH_LONG).show();
        } else {
            startActivity(launchIntentForPackage);
        }
    }
    private void enterWhiteboard() {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(WHITEBOARD_PKG);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装白板应用，请先安装白板应用", Toast.LENGTH_LONG).show();
        } else {
            startActivity(launchIntentForPackage);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode != 4 || this.viewLauncher.getVisibility() ==View.VISIBLE) {
            return super.onKeyUp(keyCode, event);
        }
        exitViewMore();
        return true;
    }


    private void exitViewMore() {
        this.viewLauncher.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.fragment_leftin);
        Animation loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.fragment_rightout);
        loadAnimation2.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {

            }

            public void onAnimationStart(Animation animation) {

            }

            public void onAnimationEnd(Animation animation) {
                MainActivity.this.viewMore.setVisibility(View.INVISIBLE);
            }
        });
        this.viewLauncher.startAnimation(loadAnimation);
        this.viewMore.startAnimation(loadAnimation2);
    }

    private void enterViewMore() {
        this.viewMore.setVisibility(View.VISIBLE);
        Animation loadAnimation = AnimationUtils.loadAnimation(this, R.anim.fragment_rightin);
        Animation loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.fragment_leftout);
        loadAnimation2.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                MainActivity.this.viewLauncher.setVisibility(View.INVISIBLE);
            }
        });
        this.viewLauncher.startAnimation(loadAnimation2);
        this.viewMore.startAnimation(loadAnimation);
    }

    private void enterNetMeeting() {
        Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(NETMEETING_PKG);
        if (launchIntentForPackage == null) {
            Toast.makeText(this, "未安装无线投屏，请先安装视频会议后使用", Toast.LENGTH_LONG).show();
            return;
        }
        Log.d(TAG, "enterNetMeeting:::::::::::::::::::::::");
        startActivity(launchIntentForPackage);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if(hasFocus){
            v.startAnimation(animationSet);
        }else{
            v.clearAnimation();
        }
    }
}
