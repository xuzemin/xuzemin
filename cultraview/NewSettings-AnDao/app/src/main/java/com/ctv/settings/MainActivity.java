package com.ctv.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.ScrollView;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.utils.L;
import com.ctv.settings.view.MainViewHolder;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2020/1/7 15:25
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/1/7 15:25
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MainActivity extends BaseActivity {
    private final static String TAG = MainActivity.class.getSimpleName(); // 是否开启日志

    private MainViewHolder mainViewHolder;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        L.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // 注册事件
//        EventBus.getDefault().register(this)

        // 初始化UI
        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.device.THEME_BG_ACTION");
        registerReceiver(recevier, filter);
    }

    private BroadcastReceiver recevier  = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            L.d("qkmin", "收到广播换壁纸");
            recreate();
        }
    };

    //取消注册
    private void unRegisterLoginBroadcast() {
        unregisterReceiver(recevier);
    }

    /**
     * 初始化UI
     */
    private void initView(){
        mainViewHolder = new MainViewHolder(this, handler);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mainViewHolder.refreshOnResume(this);
        Intent intent = getIntent();
        if (intent != null ){
            String stringExtra = intent.getStringExtra("unknow_source");
            ScrollView scrollview = (ScrollView)findViewById(R.id.scrollview);
            if(!TextUtils.isEmpty(stringExtra)){
                if(stringExtra.equals("android.settings.APPLICATION")){
                    scrollview.post(new Runnable() {
                        @Override
                        public void run() {
                            L.i("gyx","action unknow_source");
                            scrollview.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mainViewHolder.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

//        finish();
//        ExitAppUtil.getInstance().delActivity(this);
//        ExitAppUtil.getInstance().exit();
    }

    @Override
    protected void onDestroy() {
        L.d(TAG, "onDestroy");
        super.onDestroy();
        unRegisterLoginBroadcast();
        mainViewHolder.onDestroy();

//        ExitAppUtil.getInstance().exit();
//        System.exit(0);
//        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
