package com.protruly.floatwindowlib.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.broadcast.BootReceiver;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.yinghe.whiteboardlib.utils.ACache;

import java.util.List;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 * 主界面
 * @author wang
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 检测是否有meeting文件夹
//        if (!BuildConfig.DEBUG){
//            //AppUtils.checkPermission();
//        }


        // 初始化触屏设置
//        try {
//            TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_OE_High");
//            TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_SEL_Low");
//        } catch (Exception e){
//            e.printStackTrace();
//        }

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
