package com.ctv.easytouch

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ctv.easytouch.service.FloatWindowService

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gotToWindow()
    }

    /**
     * 实现跳转
     */
    private fun gotToWindow() {
        // 启动服务
        val intent = Intent(this@MainActivity, FloatWindowService::class.java)
        startService(intent)
        this@MainActivity.finish()
    }
}
