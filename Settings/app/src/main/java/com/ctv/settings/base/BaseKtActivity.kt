package com.ctv.settings.base

import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Desc:Base Acitvity
 *
 * @author wanghang
 * *
 * @time 2019/9/10.
 */
open class BaseKtActivity: AppCompatActivity(), CoroutineScope by MainScope() {
    fun startCoroutine(context: CoroutineContext = EmptyCoroutineContext,
                       start: CoroutineStart = CoroutineStart.DEFAULT,
                       block: suspend CoroutineScope.() -> Unit): Job = launch(context, start, block)

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}