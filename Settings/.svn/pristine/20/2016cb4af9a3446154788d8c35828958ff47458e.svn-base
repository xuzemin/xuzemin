package com.ctv.settings.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.ctv.settings.about.ViewHolder.AboutDeviceViewHolder;
import com.ctv.settings.base.IBaseViewHolder;
import com.ctv.settings.device.viewHolder.DeviceViewHolder;
import com.ctv.settings.greneral.GreneralViewHolder;
import com.ctv.settings.language.LanguageInpoutViewHolder;
import com.ctv.settings.network.holder.NetWorkViewHolder;
import com.ctv.settings.security.SecurityViewHolder;
import com.ctv.settings.timeanddate.holder.TimeAndDateViewHolder;


/**
 * @Description: 主界面UI
 * @Author: wanghang
 * @CreateDate: 2019/9/25 11:09
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/9/25 11:09
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MainViewHolder implements IBaseViewHolder {
    private Activity mActivity;
    private Handler mHandler;

    private NetWorkViewHolder netviewHolder;
    private TimeAndDateViewHolder timeAndDateViewHolder;

    private DeviceViewHolder deviceViewHolder;
    private SecurityViewHolder securityViewHolder;
    private LanguageInpoutViewHolder languageInpoutViewHolder;

    private GreneralViewHolder greneralViewHolder;
    private AboutDeviceViewHolder aboutDeviceViewHolder;

    /**
     * 构造方法
     * @param activity
     * @param handler
     */
    public MainViewHolder(Activity activity, Handler handler){
        this.mActivity = activity;
        this.mHandler = handler;

        initUI(activity);
        initData(activity);
        initListener();
    }
    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        deviceViewHolder = new DeviceViewHolder(activity);
        netviewHolder = new NetWorkViewHolder(activity);
        greneralViewHolder = new GreneralViewHolder(activity);

        securityViewHolder = new SecurityViewHolder(activity, mHandler);
        languageInpoutViewHolder = new LanguageInpoutViewHolder(activity, mHandler);
        timeAndDateViewHolder = new TimeAndDateViewHolder(activity);
        aboutDeviceViewHolder = new AboutDeviceViewHolder(activity);
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        deviceViewHolder.initData(activity);
        netviewHolder.initData(activity);
        greneralViewHolder.initData(activity);

        securityViewHolder.initData(activity);
        languageInpoutViewHolder.initData(activity);
        timeAndDateViewHolder.initData(activity);

//        aboutViewHolder.initData(activity);
    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {

    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {

    }

    public void refreshOnResume(Activity activity) {
        if(netviewHolder == null) {
            netviewHolder = new NetWorkViewHolder(activity);
        }
        netviewHolder.initData(activity);
//
        if(timeAndDateViewHolder==null){
            timeAndDateViewHolder = new TimeAndDateViewHolder(activity);
        }
        timeAndDateViewHolder.initData(activity);

    }

    /**
     * 跳转监听回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        deviceViewHolder.onActivityResult(requestCode, resultCode, data);
    }


}
