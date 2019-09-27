package com.ctv.settings

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.apkfuns.log2file.LogFileEngineFactory
import com.apkfuns.logutils.LogUtils
import com.ctv.settings.extensions.DelegatesExt
import com.ctv.settings.utils.CommonConsts
import com.ctv.settings.utils.CrashHandler


/**
 * Desc:全局上下文

 * @author wang
 * *
 * @time 2019/9/10.
 */
class MyApp():Application() {
    companion object {
        var INSTANCE: MyApp by DelegatesExt.notNullSingleValue()
        const val IS_ALLOW_LOG = false // 是否开启日志
        const val APP_NAME = "SettingsNew"
        const val FORMAT_NAME = "%d{yyyyMMdd}.txt"
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        // 初始化
        Thread { init() }.start()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this) // 分包处理
    }

    /**
     * 初始化
     */
    private fun init() {
        // 初始化日志记录
        LogUtils.getLogConfig()
            .configAllowLog(true)
            .configTagPrefix(APP_NAME)
            .configShowBorders(true)
            .configFormatTag("%d{HH:mm:ss:SSS} %t %c{-5}")

        if (IS_ALLOW_LOG){
            // 初始化日志记录
            LogUtils.getLog2FileConfig().configLog2FileEnable(true)
                // targetSdkVersion >= 23 需要确保有写sdcard权限
                .configLog2FilePath(CommonConsts.SDCARD_DIR + "/logs/$APP_NAME/")
                .configLog2FileNameFormat(FORMAT_NAME)
                .configLogFileEngine(LogFileEngineFactory(this))
        }

        // 初始化cash日志
        CrashHandler.getInstance().init(INSTANCE)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }
}