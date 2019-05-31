package com.xuzemin.kotlin

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import com.xuzemin.kotlin.utils.JniUtils
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity() {

    lateinit var jniUtils : JniUtils
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Example of a call to a native method

        jniUtils = JniUtils().getInstance()
        sample_text.text = jniUtils.stringFromJNI()

        val flattery : FloatArray  = floatArrayOf(0.3f,0.2f,0.5f,0.1f,0.4f,0.0f)

        jniUtils.CheckValue(flattery)

        var str = ""
        for (index in 1..5){
            print(flattery[index])
            str = str + flattery[index]+";"
        }
        jnivalue.text = str





    }
}
