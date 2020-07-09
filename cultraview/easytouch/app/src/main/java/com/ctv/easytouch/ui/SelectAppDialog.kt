package com.ctv.easytouch.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import android.widget.GridView
import com.ctv.easytouch.R
import com.ctv.easytouch.been.AppInfo
import com.ctv.easytouch.utils.ApkInfoUtils
import com.zhy.adapter.abslistview.CommonAdapter
import com.zhy.adapter.abslistview.ViewHolder

class SelectAppDialog @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null) :
    FrameLayout(mContext, attrs) {
    private var apkInfoUtils: ApkInfoUtils? = null
    private var dataList: List<AppInfo>? = null

    private var mCallback: Callback? = null

    /**
     * item监听器
     */
    private val mOnItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
        if (position < dataList!!.size) {
            val appInfo = dataList!![position]
            if (appInfo != null) {
                if (mCallback != null) {
                    mCallback!!.selected(appInfo)
                }
            }
        }
    }

    interface Callback {
        fun selected(appInfo: AppInfo)
    }

    init {

        init()
    }

    private fun init() {
        apkInfoUtils = ApkInfoUtils()

        LayoutInflater.from(mContext).inflate(R.layout.dialog_signal, this)
        initView()
    }

    /**
     * 初始化UI
     */
    private fun initView() {

        // 获得数据
        dataList = apkInfoUtils!!.scanInstallApp(mContext)

        // 设置Adapter
        val gridMenuID = R.layout.item_app
        val commonAdapter = object : CommonAdapter<AppInfo>(
            mContext,
            gridMenuID, dataList
        ) {
            override fun convert(holder: ViewHolder, appInfo: AppInfo, position: Int) {
                holder.setText(R.id.text, appInfo.appName)
                holder.setImageDrawable(R.id.image, appInfo.appIcon)
            }
        }

        // 初始化
        val gridView = findViewById<View>(R.id.signal_grid_view) as GridView
        gridView.adapter = commonAdapter
        gridView.onItemClickListener = mOnItemClickListener
    }

    fun setCallback(mCallback: Callback) {
        this.mCallback = mCallback
    }
}
