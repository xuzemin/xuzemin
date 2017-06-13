package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.jdrd.robot.R;

/**
 * Created by jdrd on 2017/6/13.
 *
 */

public class DeskConfigPathAcitivty extends Activity{
    TextView content_tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        Intent intent =getIntent();// 收取 email
        content_tv = ((TextView)findViewById(R.id.content_tvad));
        int i = intent.getIntExtra("id",0);
        content_tv.setText(""+ i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
