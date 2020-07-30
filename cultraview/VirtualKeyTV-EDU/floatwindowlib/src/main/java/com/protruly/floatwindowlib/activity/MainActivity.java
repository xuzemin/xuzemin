package com.protruly.floatwindowlib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.broadcast.BootReceiver;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.yinghe.whiteboardlib.utils.ACache;

/**
 * 主界面
 * @author wang
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        gotToWindow();
    }

    /**
     * 实现跳转
     */
    private void gotToWindow() {
        // 启动服务
        Intent intent = new Intent(MainActivity.this, FloatWindowService.class);
        startService(intent);
        MainActivity.this.finish();
    }
}
