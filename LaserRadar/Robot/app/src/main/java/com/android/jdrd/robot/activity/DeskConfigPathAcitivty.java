package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;

/**
 * Created by jdrd on 2017/6/13.
 *
 */

public class DeskConfigPathAcitivty extends Activity{
    private RobotDBHelper robotDBHelper;
    private int deskid,areaid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_config);

        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        Intent intent =getIntent();// 收取 email
        int deskid = intent.getIntExtra("id",0);
        int areaid = intent.getIntExtra("area",0);

        if(deskid == 0){
            robotDBHelper.execSQL("insert into desk (name,area) values ('A03','"+areaid+"')");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
