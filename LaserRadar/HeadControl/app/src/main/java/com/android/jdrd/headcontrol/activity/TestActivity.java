package com.android.jdrd.headcontrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Constant;

import java.io.IOException;

public class TestActivity extends Activity implements View.OnClickListener {
    private Button bt_openSearchPeople;
    private Button bt_closeSearchPeople;
    private Button bt_openAR;
    private Button bt_closeAR;
    private Button bt_openPower;
    private Button bt_closePower;
    private Button bt_openWater;
    private Button bt_closeWater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
    }

    private void initView() {
        bt_openSearchPeople = (Button) findViewById(R.id.openSearchPeople);
        bt_closeSearchPeople = (Button) findViewById(R.id.closeSearchPeople);
        bt_openAR = (Button) findViewById(R.id.openAR);
        bt_closeAR = (Button) findViewById(R.id.closeAR);
        bt_openPower = (Button) findViewById(R.id.openBigScreenPower);
        bt_closePower = (Button) findViewById(R.id.closeBigScreenPower);
        bt_openWater = (Button)findViewById(R.id.openWater);
        bt_closeWater = (Button)findViewById(R.id.closeWater);
        bt_openSearchPeople.setOnClickListener(this);
        bt_closeSearchPeople.setOnClickListener(this);
        bt_openAR.setOnClickListener(this);
        bt_closeAR.setOnClickListener(this);
        bt_openPower.setOnClickListener(this);
        bt_closePower.setOnClickListener(this);
        bt_openWater.setOnClickListener(this);
        bt_closeWater.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openSearchPeople:
                Intent intent = new Intent();
                intent.setAction("com.jdrd.CursorSDKExample.TD_CAMERA");
                intent.putExtra("msg", "远");
                sendBroadcast(intent);
                break;
            case R.id.closeSearchPeople:
                Intent intent2 = new Intent();
                intent2.setAction("com.jdrd.CursorSDKExample.TD_CAMERA");
                intent2.putExtra("msg", "关闭");
                sendBroadcast(intent2);
                break;
            case R.id.openAR:
                try {
                    ServerSocketUtil.sendDateToClient("open", Constant.ip_bigScreen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.closeAR:
                try {
                    ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.openBigScreenPower:
                try {
                    ServerSocketUtil.sendDateToClient("openBigScreenPower", Constant.ip_ros);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.closeBigScreenPower:
                try {
                    ServerSocketUtil.sendDateToClient("closeBigScreenPower", Constant.ip_ros);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.openWater:
                try {
                    ServerSocketUtil.sendDateToClient("openWater", Constant.ip_ros);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.closeWater:
                try {
                    ServerSocketUtil.sendDateToClient("closeWater", Constant.ip_ros);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
