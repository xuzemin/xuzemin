package com.ctv.settings.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import com.ctv.settings.about.ViewHolder.AboutDeviceViewHolder;
import com.ctv.settings.base.IBaseViewHolder;
import com.ctv.settings.device.viewHolder.DeviceViewHolder;
import com.ctv.settings.general.GreneralViewHolder;
import com.ctv.settings.language.holder.LanguageInpoutViewHolder;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.Listener.LoadDataAsyncTask;
import com.ctv.settings.network.holder.NetWorkViewHolder;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.security.holder.SecurityViewHolder;
import com.ctv.settings.timeanddate.holder.TimeAndDateViewHolder;
import com.ctv.settings.utils.L;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
    private ConnectivityListener connectivityListener;
    private GreneralViewHolder greneralViewHolder;
    private AboutDeviceViewHolder aboutDeviceViewHolder;
    private ConnectivityListener.Listener listener = new ConnectivityListener.Listener() {
        @Override
        public void onConnectivityChange(Intent intent) {
            L.d("intent" + intent.getAction());
            try {
                netviewHolder.checkStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPPPoeChanged(String status) {
            netviewHolder.updatePppoeInfo(status);
            L.e("status" + status);
        }

        @Override
        public void onEthernetAvailabilityChanged(boolean isAvailable) {
            L.e("isAvailable" + isAvailable);
            netviewHolder.updateEthernetInfo(isAvailable);
        }
    };

    public ExecutorService singleThreadExecutor = Executors.newScheduledThreadPool(10);

    /**
     * 构造方法
     *
     * @param activity
     * @param handler
     */
    public MainViewHolder(Activity activity, Handler handler) {
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
        greneralViewHolder = new GreneralViewHolder(activity, mHandler, singleThreadExecutor);
        securityViewHolder = new SecurityViewHolder(activity, mHandler);
        languageInpoutViewHolder = new LanguageInpoutViewHolder(activity, mHandler);
        timeAndDateViewHolder = new TimeAndDateViewHolder(activity, mHandler, singleThreadExecutor);
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

        aboutDeviceViewHolder.initData(activity);
    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        connectivityListener = new ConnectivityListener(mActivity, listener);
        LoadDataAsyncTask mAyncTask = new LoadDataAsyncTask(mActivity, connectivityListener);
        mAyncTask.setFinishListener(data -> {
            if (data == null) {
                data = new InitDataInfo(null, null, null, null, 0, false, null, false);
            }
            if (connectivityListener == null) {
                connectivityListener = new ConnectivityListener(mActivity, listener);
            }
            connectivityListener.start();
        });
        mAyncTask.execute();

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
        deviceViewHolder.onResume(activity);
    }

    /**
     * 跳转监听回调
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        deviceViewHolder.onActivityResult(requestCode, resultCode, data);
        timeAndDateViewHolder.onActivityResult(requestCode, resultCode, data);
        aboutDeviceViewHolder.onActivityResult(requestCode, resultCode, data);
        netviewHolder.onActivityResult(requestCode, resultCode, data);
        greneralViewHolder.onActivityResult(requestCode, resultCode, data);
//        greneralViewHolder.refreshFourScreenItemEnable();
    }

    public void onDestroy() {
        if (timeAndDateViewHolder != null) {
            timeAndDateViewHolder.unregisterReceiver();
        }
        if (singleThreadExecutor != null) {
            try {
                singleThreadExecutor.shutdown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (connectivityListener != null) {
            connectivityListener.stop();
        }
    }

}
