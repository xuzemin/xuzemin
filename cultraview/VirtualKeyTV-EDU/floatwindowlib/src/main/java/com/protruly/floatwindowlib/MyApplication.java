package com.protruly.floatwindowlib;

import android.app.Application;
import android.content.Context;

import com.apkfuns.log2file.LogFileEngineFactory;
import com.apkfuns.logutils.LogUtils;
import com.yinghe.whiteboardlib.utils.CrashHandler;
import com.yinghe.whiteboardlib.utils.DrawConsts;

/**
 * Desc:全局上下文
 *
 * @author Administrator
 * @time 2017/4/13.
 */
public class MyApplication extends Application {

    public static volatile Context myApp = null;

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = getApplicationContext();

        initLog();
    }

    /**
     * 初始化日志配置
     */
    private void initLog(){
        LogUtils.getLogConfig()
                .configAllowLog(true)
                .configTagPrefix("Virtualkey")
                .configShowBorders(true)
               .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}");

//        LogUtils.getLog2FileConfig().configLog2FileEnable(true)
//                // targetSdkVersion >= 23 需要确保有写sdcard权限
//                .configLog2FilePath(DrawConsts.SDCARD_DIR + "/logs/Virtualkey/")
//                .configLog2FileNameFormat("%d{yyyyMMdd}.txt")
//                .configLogFileEngine(new LogFileEngineFactory(this));

        new Thread(()->{
            CrashHandler.getInstance().init(myApp);
        }).start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }

}
