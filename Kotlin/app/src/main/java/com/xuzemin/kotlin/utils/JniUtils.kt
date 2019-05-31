package com.xuzemin.kotlin.utils

class JniUtils{
    var jniUtils : JniUtils? = null
    fun getInstance():JniUtils{
        if (jniUtils == null){
            jniUtils = JniUtils()
        }
        return jniUtils as JniUtils
    }
    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    external fun stringFromJNI(): String
    external fun CheckValue(floatarray : FloatArray)
}

