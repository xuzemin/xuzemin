package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;

/**
 * Created by jdrd on 2017/6/16.
 *
 */

public class RobotConfigActivity extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int robotid;
    private EditText name;
    private boolean IsADD = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_robot_config);
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        Intent intent =getIntent();// 收取 email
        robotid = intent.getIntExtra("id",0);

    }

    @Override
    public void onClick(View v) {

    }
}
