package com.ctv.settings.network.holder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.Listener.LoadDataAsyncTask;
import com.ctv.settings.network.R;
import com.ctv.settings.network.activity.NetWorkActivity;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.utils.L;


/**
 * 设置ViewHolder
 *
 * @author wanghang
 * @date 2019/09/17
 */
public class NetWorkViewHolder extends BaseViewHolder implements View.OnClickListener, ConnectivityListener.Listener{

    private final static String TAG = NetWorkViewHolder.class.getCanonicalName();

    private RelativeLayout rl_wire,rl_wifi,rl_pppoe,rl_host_spot,rl_bluetooth,rl_screen_hot;

    private TextView tv_wire,tv_wifi,tv_pppoe,tv_host_spot,tv_bluetooth,tv_screen_hot;

    private ConnectivityListener mConnectivityListener;

    public NetWorkViewHolder(Activity activity) {
        super(activity);
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        rl_wire = activity.findViewById(R.id.rl_wire);
        rl_wifi = activity.findViewById(R.id.rl_wifi);
        rl_pppoe = activity.findViewById(R.id.rl_pppoe);
        rl_host_spot = activity.findViewById(R.id.rl_host_wifi);
        rl_bluetooth = activity.findViewById(R.id.rl_bluetooth);
        rl_screen_hot = activity.findViewById(R.id.rl_screen_hot);

        tv_wire = activity.findViewById(R.id.tv_wire);
        tv_wifi = activity.findViewById(R.id.tv_wifi);
        tv_pppoe = activity.findViewById(R.id.tv_pppoe);
        tv_host_spot = activity.findViewById(R.id.tv_host_wifi);
        tv_bluetooth = activity.findViewById(R.id.tv_bluetooth);
        tv_screen_hot = activity.findViewById(R.id.tv_screen_hot);
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        checkStatus();
    }


    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        rl_wire.setOnClickListener(this);
        rl_wifi.setOnClickListener(this);
        rl_pppoe.setOnClickListener(this);
        rl_host_spot.setOnClickListener(this);
        rl_bluetooth.setOnClickListener(this);
        rl_screen_hot.setOnClickListener(this);
    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rl_wire) {
            goNetWorkActivity(NetUtils.WIRE_CONNECT);
        } else if (i == R.id.rl_pppoe) {
            goNetWorkActivity(NetUtils.PPPOE_CONNECT);
        } else if (i == R.id.rl_wifi) {
            goNetWorkActivity(NetUtils.WIRELESS_CONNECT);
        } else if (i == R.id.rl_host_wifi) {
            goNetWorkActivity(NetUtils.WIFI_HOTSPOT);
        } else if (i == R.id.rl_bluetooth) {
            goNetWorkActivity(NetUtils.BLUETOOTH);
        } else if (i == R.id.rl_screen_hot) {
            goNetWorkActivity(NetUtils.SCREEN_HOT);
        }
    }

    private void goNetWorkActivity(int id){
        Intent intent= new Intent(mActivity, NetWorkActivity.class);
        intent.putExtra("CurrentTag",id);
        mActivity.startActivity(intent);
    }

    private void updateUI(TextView view,String isOpen) {
        view.setText(isOpen);
    }


    @Override
    public void onConnectivityChange(Intent intent) {
        if (intent != null) {
            checkStatus();
        }
    }

    @Override
    public void onPPPoeChanged(String status) {
        if (status != null) {
            updateUI(tv_pppoe,status);
        }
    }

    @Override
    public void onEthernetAvailabilityChanged(boolean isAvailable) {
        if(isAvailable) {
            updateUI(tv_wire, mActivity.getString(R.string.bluetooth_connected));
        }else{
            if (mConnectivityListener.isEthernetEnable()) {
                updateUI(tv_wire, mActivity.getString(R.string.status_on));
            } else {
                updateUI(tv_wire, mActivity.getString(R.string.status_off));
            }
        }
    }

    private void checkStatus(){
        //蓝牙开关判断

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        L.d(TAG,"mBluetoothAdapter"+mBluetoothAdapter.isEnabled());
        if (mBluetoothAdapter.isEnabled()) {
            updateUI(tv_bluetooth,mActivity.getString(R.string.status_on));
        }else{
            updateUI(tv_bluetooth,mActivity.getString(R.string.status_off));
        }

        mConnectivityListener = new ConnectivityListener(mActivity,this);
        LoadDataAsyncTask mAyncTask = new LoadDataAsyncTask(mActivity, mConnectivityListener);

        mAyncTask.setFinishListener(data -> {
            try {
                refreshView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        mAyncTask.execute();
    }

    private void refreshView() throws Exception {
        //有线开关判断
        L.d(TAG,"isEthernetEnable"+mConnectivityListener.isEthernetEnable());
        if(mConnectivityListener.isEthernetEnable()){
            updateUI(tv_wire,mActivity.getString(R.string.status_on));
            if(mConnectivityListener.isEthernetAvailable()){
                updateUI(tv_wire,mActivity.getString(R.string.bluetooth_connected));
            }
        }else{
            updateUI(tv_wire,mActivity.getString(R.string.status_off));
        }
        //WIFI开关判断
        L.d(TAG,"isWifiEnabled"+mConnectivityListener.isWifiEnabled());
        if(mConnectivityListener.isWifiEnabled()){
            updateUI(tv_wifi,mActivity.getString(R.string.status_on));
        }else{
            updateUI(tv_wifi,mActivity.getString(R.string.status_off));
        }

        //热点开关判断
        String wifiapband = android.os.SystemProperties.get("Wifiapband");
        updateUI(tv_host_spot,mActivity.getString(R.string.status_off));
        updateUI(tv_screen_hot,mActivity.getString(R.string.status_off));
        L.d(TAG,"wifiapband"+wifiapband);
        if(NetUtils.isWifiApEnabled(mConnectivityListener.getWifiManager())){
            if (wifiapband.equals(NetUtils.WIFI_HOST_BAND)) {
                updateUI(tv_host_spot, mActivity.getString(R.string.status_on));
            }
            if (wifiapband.equals(NetUtils.SCREEN_HOST_BAND)) {
                updateUI(tv_screen_hot, mActivity.getString(R.string.status_on));
            }
        }
        //PPPOE开关判断
        L.d(TAG,"getPPPoeStatusDescription"+mConnectivityListener.getPPPoeStatusDescription());
        updateUI(tv_pppoe,mConnectivityListener.getPPPoeStatusDescription());

    }
}
