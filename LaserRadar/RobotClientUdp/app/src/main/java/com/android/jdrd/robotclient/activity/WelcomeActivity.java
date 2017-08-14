package com.android.jdrd.robotclient.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.android.jdrd.robotclient.R;
import com.android.jdrd.robotclient.helper.RobotDBHelper;


/**
 * Created by jdrd on 2017/6/28.
 */

public class WelcomeActivity extends Activity {

    MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_start);
        myHandler = new MyHandler();
        wasteTime();    //执行耗时操作
    }

    void wasteTime(){
        try {
            RobotDBHelper.getInstance(getApplicationContext());
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myHandler.sendEmptyMessage(101);//通知Handler
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 101){
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                finish();
                //跳到主界面，并结束欢迎页面
            }
        }
    }
}
