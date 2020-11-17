package com.ctv.settings.network.holder;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.Listener.LoadDataAsyncTask;
import com.ctv.settings.network.activity.NetWorkActivity;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.utils.L;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static com.ctv.settings.network.utils.NetUtils.HOST_BAND_TYPE;


/**
 * 网络ViewHolder
 *
 * @author xuzemin
 * @date 2019/09/17
 */
public class NetWorkViewHolder extends BaseViewHolder implements View.OnClickListener, ConnectivityListener.Listener {

    private final static String TAG = NetWorkViewHolder.class.getCanonicalName();

    private RelativeLayout rl_wire, rl_wifi, rl_pppoe, rl_host_spot, rl_bluetooth;
//            , rl_screen_hot;

    private TextView tv_wire, tv_wifi, tv_pppoe, tv_host_spot, tv_bluetooth;//, tv_screen_hot;

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
        rl_wire = (RelativeLayout) activity.findViewById(R.id.rl_wire);
        rl_wifi = (RelativeLayout) activity.findViewById(R.id.rl_wifi);
        rl_pppoe = (RelativeLayout) activity.findViewById(R.id.rl_pppoe);
        rl_host_spot = (RelativeLayout) activity.findViewById(R.id.rl_host_wifi);
        rl_bluetooth = (RelativeLayout) activity.findViewById(R.id.rl_bluetooth);
//        rl_screen_hot = (RelativeLayout) activity.findViewById(R.id.rl_screen_hot);

        tv_wire = (TextView) activity.findViewById(R.id.tv_wire);
        tv_wifi = (TextView) activity.findViewById(R.id.tv_wifi);
        tv_pppoe = (TextView) activity.findViewById(R.id.tv_pppoe);
        tv_host_spot = (TextView) activity.findViewById(R.id.tv_host_wifi);
        tv_bluetooth = (TextView) activity.findViewById(R.id.tv_bluetooth);
//        tv_screen_hot = (TextView) activity.findViewById(R.id.tv_screen_hot);
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
//        rl_screen_hot.setOnClickListener(this);
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
        }
//        else if (i == R.id.rl_screen_hot) {
//            goNetWorkActivity(NetUtils.SCREEN_HOT);
//        }
    }

    private void goNetWorkActivity(int id) {
        Intent intent = new Intent(mActivity, NetWorkActivity.class);
        intent.putExtra("CurrentTag", id);
        mActivity.startActivityForResult(intent, NetUtils.NET_STATE);
    }

    private void updateUI(TextView view, String isOpen) {
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
            updateUI(tv_pppoe, status);
        }
    }

    @Override
    public void onEthernetAvailabilityChanged(boolean isAvailable) {
        if (isAvailable) {
            updateUI(tv_wire, mActivity.getString(R.string.bluetooth_connected));
        } else {
            if (mConnectivityListener.isEthernetEnable()) {
                updateUI(tv_wire, mActivity.getString(R.string.status_on));
            } else {
                updateUI(tv_wire, mActivity.getString(R.string.status_off));
            }
        }
    }

    /**
     * UI刷新检测方法
     *
     * @throws Exception
     */
    public void checkStatus() {
        //蓝牙开关判断
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        L.d("mBluetoothAdapter" + mBluetoothAdapter.isEnabled());
        if (mBluetoothAdapter.isEnabled()) {
            if (getDevice(mBluetoothAdapter)!=null){
                updateUI(tv_bluetooth, getDevice(mBluetoothAdapter));
            }else {
                updateUI(tv_bluetooth, mActivity.getString(R.string.status_on));
            }
        } else {
            updateUI(tv_bluetooth, mActivity.getString(R.string.status_off));
        }

        mConnectivityListener = new ConnectivityListener(mActivity, this);
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

    String getDevice(BluetoothAdapter _bluetoothAdapter) {
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        try {//得到蓝牙状态的方法
            Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            int state = (int) method.invoke(_bluetoothAdapter, (Object[]) null);
            if (state == BluetoothAdapter.STATE_CONNECTED) {
                Set<BluetoothDevice> devices = _bluetoothAdapter.getBondedDevices();
                for (BluetoothDevice device : devices) {
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    method.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                    if (isConnected) {
                        return device.getName();
                    }
                }
            }
        } catch (
                Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * UI刷新检测方法
     *
     * @throws Exception
     */
    private void refreshView() throws Exception {
        //有线开关判断
        L.d("isEthernetEnable" + mConnectivityListener.isEthernetEnable());
        if (mConnectivityListener.isEthernetEnable()) {
            updateUI(tv_wire, mActivity.getString(R.string.status_on));
            if (checkEthernetConnect()) {
                updateUI(tv_wire, mActivity.getString(R.string.bluetooth_connected));
            }
        } else {
            updateUI(tv_wire, mActivity.getString(R.string.status_off));
        }
        //WIFI开关判断
        L.d("isWifiEnabled" + mConnectivityListener.isWifiEnabled());
        if (mConnectivityListener.isWifiEnabled()) {
            updateUI(tv_wifi, mActivity.getString(R.string.status_on));
            String ssid = getWIFIName(mActivity);
            if (ssid != null && !ssid.equals("") && !ssid.equals("0x") && !ssid.contains("<")) {
                updateUI(tv_wifi, ssid);
            }
        } else {
            updateUI(tv_wifi, mActivity.getString(R.string.status_off));
        }

        //热点开关判断
        String wifiapband = android.os.SystemProperties.get(HOST_BAND_TYPE);
        int hotpotRes = NetUtils.isHotspot5G()? R.string.hot_spot_type_two : R.string.hot_spot_type_one;
        String hotpotName = mActivity.getString(hotpotRes);
        updateUI(tv_host_spot, hotpotName + " " + mActivity.getString(R.string.status_off));
//        updateUI(tv_screen_hot, mActivity.getString(R.string.status_off));
        L.d("wifiapband" + wifiapband);
        if (NetUtils.isWifiApEnabled(mConnectivityListener.getWifiManager())) {
//            if (wifiapband.equals(NetUtils.WIFI_HOST_BAND)) {
                updateUI(tv_host_spot, hotpotName + " " + mActivity.getString(R.string.status_on));
//            }
//            if (wifiapband.equals(NetUtils.SCREEN_HOST_BAND)) {
//                updateUI(tv_screen_hot, mActivity.getString(R.string.status_on));
//            }
        }
        //PPPOE开关判断
        L.d("getPPPoeStatusDescription" + mConnectivityListener.getPPPoeStatusDescription());
        updateUI(tv_pppoe, mConnectivityListener.getPPPoeStatusDescription());

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NetUtils.NET_STATE) {
            checkStatus();
        }
    }


    public void updateEthernetInfo(boolean isAvailable) {
        if (mConnectivityListener.isEthernetEnable()) {
            updateUI(tv_wire, mActivity.getString(R.string.status_on));
            if (checkEthernetConnect()) {
                updateUI(tv_wire, mActivity.getString(R.string.bluetooth_connected));
            }
        } else {
            updateUI(tv_wire, mActivity.getString(R.string.status_off));
        }
    }

    public void updatePppoeInfo(String status) {
        updateUI(tv_pppoe, status);
    }


    private boolean checkEthernetConnect() {
        LinkProperties linkProperties = null;
        ConnectivityManager mConnectivityManager = (ConnectivityManager) mActivity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        List<LinkAddress> listLink = null;
        try {
            linkProperties = NetUtils.getLinkProperties(mConnectivityManager, ConnectivityManager.TYPE_ETHERNET);
            L.d(linkProperties.toString());
            listLink = NetUtils.getAllLinkAddresses(linkProperties);
            L.d(listLink.toString());
        } catch (Exception e) {
            L.d(e.toString());
        }
        if (linkProperties != null
                && listLink != null
                && !listLink.isEmpty()) {
            L.d("checkEthernetConnect true");
            return true;
        } else {
            L.d("checkEthernetConnect false");
            return false;
        }
    }

    public static String getWIFIName(Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifiId = null;
        if(wifi == NetworkInfo.State.CONNECTED) {
            WifiInfo info = wifiMgr.getConnectionInfo();
            wifiId = info != null ? info.getSSID().replace("\"", "") : null;
        }
        return wifiId;
    }
}
