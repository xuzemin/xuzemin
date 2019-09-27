package com.ctv.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.apkfuns.logutils.LogUtils
import com.ctv.settings.base.BaseKtActivity
import com.ctv.settings.event.Event
import com.ctv.settings.extensions.executeRequest
import com.ctv.settings.view.MainViewHolder

import kotlinx.coroutines.Job
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Desc:主界面
 *
 * @author wanghang
 * *
 * @time 2019/9/10.
 */
class MainActivity : BaseKtActivity() {

    lateinit var mainViewHolder:MainViewHolder

    val handler:Handler = Handler()

    companion object {
        val TAG = MainActivity::class.java.simpleName // 是否开启日志
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 注册事件
//        EventBus.getDefault().register(this)

        // 初始化UI
        initView()

        // 初始化数据
        initData()

        LogUtils.d("MainActivity is OK!")
    }

    /**
     * 初始化UI
     */
    private fun initView(){
        mainViewHolder = MainViewHolder(this,handler)
    }

    /**
     * 初始化数据
     */
    private fun initData(){}

    /**
     * 处理事件:更新音量刷新开关
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun handleVoiceBarEvent(event: Event.UpdateVoiceBarEvent) {
        if (!isFinishing){
//            updateVoiceBarUI(event.state)
        }
    }

    private fun updateUI(resStr: String) {
//        test_result.text = resStr
    }
    
    private fun loadData(username: String): Job {
        return executeRequest(
            //  请求调用
            request = {
                getUserInfo(username)
            },
            //  成功回调
            onSuccess = {
                updateUI(it)
            },
            //  失败回调
            onFail = {
                it.printStackTrace()
            })
    }

    private fun getUserInfo(useName:String): String {
        return useName + "Hello"
    }

    override fun onResume() {
        super.onResume()
//        mainViewHolder.refreshOnResume(this)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mainViewHolder.onActivityResult(requestCode, resultCode, data)
    }
}
