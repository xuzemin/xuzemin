package com.android.example.kotlindemo.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.example.kotlindemo.R
import com.android.example.kotlindemo.util.Constant
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val constant = Constant()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        constant.debugLog("")
        KotlinApplication.registerActivity(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        KotlinApplication.exitActivity()

    }

    override fun onResume() {
        super.onResume()
        initData()
//        Toast.makeText(applicationContext,"adawda",Toast.LENGTH_SHORT).show()
    }

    fun initData(){
        text.text = "111"
    }
}
