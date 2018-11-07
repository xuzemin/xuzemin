package com.android.example.kotlindemo.util

import android.util.Log

class Constant{
    private val TAG:String = "kotlin"
    private val Debug:Boolean = true
    init {
        Log.e(TAG,"Constant")
    }
    fun debugLog(log:String){
        if(Debug) {
            Log.e(TAG, log)
        }
    }
}