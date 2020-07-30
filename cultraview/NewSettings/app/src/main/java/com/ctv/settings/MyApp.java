package com.ctv.settings;

import android.app.Application;
import android.content.Context;

import com.ctv.settings.utils.CrashHandler;
import com.ctv.settings.utils.Tools;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2020/1/7 15:00
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/1/7 15:00
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MyApp extends Application {
    public int themeResID = 0;

    private final static boolean IS_ALLOW_LOG = false; // 是否开启日志
    private final static String APP_NAME = "SettingsNew";
    private final static String FORMAT_NAME = "%d{yyyyMMdd}.txt";

    @Override
    public void onCreate() {
//        Tools.changeTheme(this);
        super.onCreate();
        init();
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this); // 分包处理
    }

    /**
     * 初始化
     */
    private void init() {
        themeResID = Tools.getThemeID(this);

        // 初始化日志记录
//        LogUtils.getLogConfig()
//            .configAllowLog(true)
//            .configTagPrefix(APP_NAME)
//            .configShowBorders(true)
//            .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}");

//        if (IS_ALLOW_LOG){
//            // 初始化日志记录
//            LogUtils.getLog2FileConfig().configLog2FileEnable(true)
//                // targetSdkVersion >= 23 需要确保有写sdcard权限
//                .configLog2FilePath(CommonConsts.SDCARD_DIR + "/logs/$APP_NAME/")
//                .configLog2FileNameFormat(FORMAT_NAME)
//                .configLogFileEngine(LogFileEngineFactory(this));
//        }

        // 初始化cash日志
        CrashHandler.getInstance().init(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}
