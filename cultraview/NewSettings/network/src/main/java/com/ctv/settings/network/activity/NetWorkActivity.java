
package com.ctv.settings.network.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.Listener.LoadDataAsyncTask;
import com.ctv.settings.network.R;
import com.ctv.settings.network.holder.*;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.utils.L;

import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.Objects;

/**
 * 网络模块
 *
 * @author xuzemin
 * @date 2019/09/19
 */
public class NetWorkActivity extends BaseActivity implements ConnectivityListener.Listener {
    private BluetoothViewHolder mBluetoothHolder;

    private ConnectivityListener mConnectivityListener = null;

    private WireViewHolder wireViewHolder;

    private WifiHotspotViewHolder wifiHotspotViewHolder;

    private WirelessViewHolder wirelessViewHolder;

    private ScreenHotSponViewHolder screenHotSponViewHolder;

    private PppoeViewHolder pppoeViewHolder;

    boolean isFromSetting = false;

    private int currentTag;

    private InitDataInfo mInitDataInfo = null;

    private LoadDataAsyncTask mAyncTask = null;

    private String[] mWifiRegexs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiRegexs = cm.getTetherableWifiRegexs();


        Intent getIntent = getIntent();
        currentTag = getIntent.getIntExtra("CurrentTag", 0);
        mConnectivityListener = new ConnectivityListener(this, this);
        if (currentTag != 0) {
            switch (currentTag) {
                case NetUtils.WIRELESS_CONNECT:  //WIFI
                    setContentView(R.layout.page_wireless);
                    wirelessViewHolder = new WirelessViewHolder(this, mConnectivityListener);
                    break;
                case NetUtils.BLUETOOTH: //蓝牙
                    setContentView(R.layout.page_bluetooth);
                    mBluetoothHolder = new BluetoothViewHolder(this);
                    break;
                case NetUtils.WIRE_CONNECT:   //以太网  待网线验证
                    setContentView(R.layout.page_wire);
                    wireViewHolder = new WireViewHolder(this, mConnectivityListener);
                    break;
                case NetUtils.WIFI_HOTSPOT:    //热点
                    setContentView(R.layout.page_wifi_hotspot);
                    wifiHotspotViewHolder = new WifiHotspotViewHolder(this, mConnectivityListener);
                    break;
                case NetUtils.SCREEN_HOT:   //投屏热点
                    setContentView(R.layout.page_screen_hot_spot);
                    try {
                        screenHotSponViewHolder = new ScreenHotSponViewHolder(this, mConnectivityListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case NetUtils.PPPOE_CONNECT:
                    setContentView(R.layout.page_pppoe);
                    pppoeViewHolder = new PppoeViewHolder(this, mConnectivityListener);
                    break;
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if (currentTag == NetUtils.BLUETOOTH) {
            mBluetoothHolder.onResume();
        } else {
            mAyncTask = new LoadDataAsyncTask(this, mConnectivityListener);
            mAyncTask.setFinishListener(data -> {
                if (data == null) {
                    data = new InitDataInfo(null, null, null, null, 0, false, null, false);
                }
                switch (currentTag) {
                    case NetUtils.WIRELESS_CONNECT: {
                        wirelessViewHolder.initData(data);
                        break;
                    }
                    case NetUtils.WIRE_CONNECT: {
                        wireViewHolder.initData(data);
                        break;
                    }
                    case NetUtils.SCREEN_HOT:
                        screenHotSponViewHolder.refreshWifiApUi(true, data);
                        break;
                    case NetUtils.WIFI_HOTSPOT:
                        wifiHotspotViewHolder.refreshWifiApUi(true, data);
                        break;
                    case NetUtils.PPPOE_CONNECT:
                        pppoeViewHolder.initData(data);
                        break;
                }
                if (mConnectivityListener == null) {
                    mConnectivityListener = new ConnectivityListener(this, this);
                }
                mConnectivityListener.start();
            });
            mAyncTask.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnectivityListener != null) {
            mConnectivityListener.stop();
        }
        if (mConnectivityListener != null) {
            mConnectivityListener = null;
        }
        if (wifiHotspotViewHolder != null) {
            wifiHotspotViewHolder.onExit();
        }
        if (wirelessViewHolder != null) {
            wirelessViewHolder.onExit();
        }
        if (screenHotSponViewHolder != null) {
            screenHotSponViewHolder.onExit();
        }
        if (wireViewHolder != null) {
            wireViewHolder.onExit();
        }
        if (pppoeViewHolder != null) {
            pppoeViewHolder.onExit();
        }
        if (mBluetoothHolder != null) {
            mBluetoothHolder.onExit();
        }
        onExit();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void onExit() {
        wireViewHolder = null;
        wirelessViewHolder = null;
        screenHotSponViewHolder = null;
        wifiHotspotViewHolder = null;
        currentTag = -1;
        isFromSetting = false;
        mConnectivityListener = null;
        mInitDataInfo = null;
        if (mAyncTask != null && !mAyncTask.isCancelled()) {
            mAyncTask.cancel(true);
        }
        mAyncTask = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        switch (currentTag) {
            case NetUtils.WIRELESS_CONNECT:  //WIFI
                if (wifiHotspotViewHolder != null) {
                    return wifiHotspotViewHolder.onKeyDown(keyCode, event);
                }
            case NetUtils.BLUETOOTH: //蓝牙
                if (mBluetoothHolder != null) {
                    return mBluetoothHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.WIRE_CONNECT:   //以太网  待网线验证
                if (wireViewHolder != null) {
                    return wireViewHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.WIFI_HOTSPOT:    //热点
                if (wifiHotspotViewHolder != null) {
                    return wifiHotspotViewHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.SCREEN_HOT:   //投屏热点
                if (screenHotSponViewHolder != null) {
                    return screenHotSponViewHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.PPPOE_CONNECT:
                if (pppoeViewHolder != null) {
                    return pppoeViewHolder.onKeyDown(keyCode, event);
                }
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConnectivityChange(Intent intent) {
        if (intent != null) {
            try {
                refreshView(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPPPoeChanged(String status) {
    }

    @Override
    public void onEthernetAvailabilityChanged(boolean isAvailable) {
        // TODO Auto-generated method stub
        L.d("-------------onEthernetAvailabilityChanged,isAvailable, " + isAvailable);
        if (wireViewHolder != null) {
            wireViewHolder.showEthernetInfo(false);
        }
    }

    private void refreshView(Intent intent) throws Exception {
        String action = intent.getAction();
        L.d("--action, " + action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            if (wirelessViewHolder != null) {
                wirelessViewHolder.updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN));
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            if ((!mConnectivityListener.getConnectivityStatus().isWifiConnected())
                    && (!mConnectivityListener.isWifiEnabled())) {
            }
            if (wirelessViewHolder != null && wirelessViewHolder.isOpenWifi) {
                wirelessViewHolder.stateChanged(intent);
            }
        } else if (NetUtils.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
            if (wifiHotspotViewHolder != null) {
                wifiHotspotViewHolder.upApStateChange(intent);
            }

            if (screenHotSponViewHolder != null) {
                screenHotSponViewHolder.upApStateChange(intent);
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)
                || NetUtils.INET_CONDITION_ACTION.equals(action)) {
            if (wireViewHolder != null)
                wireViewHolder.showEthernetInfo(false);
        } else if (WifiManager.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {

            Log.d("qkmin--->", "WifiManager.WIFI_AP_STATE_CHANGED_ACTION");

//            handleWifiApStateChanged(intent.getIntExtra(
//                    WifiManager.EXTRA_WIFI_AP_STATE, WifiManager.WIFI_AP_STATE_FAILED));
        } else if (ConnectivityManager.ACTION_TETHER_STATE_CHANGED.equals(action)) {
            Log.d("qkmin--->", "ConnectivityManager.ACTION_TETHER_STATE_CHANGED");

            // TODO - this should understand the interface types
            ArrayList<String> available = intent.getStringArrayListExtra(
                    ConnectivityManager.EXTRA_AVAILABLE_TETHER);
            ArrayList<String> active = intent.getStringArrayListExtra(
                    ConnectivityManager.EXTRA_ACTIVE_TETHER);
            ArrayList<String> errored = intent.getStringArrayListExtra(
                    ConnectivityManager.EXTRA_ERRORED_TETHER);
//            updateState(available.toArray(new String[available.size()]),
//                    active.toArray(new String[active.size()]),
//                    errored.toArray(new String[errored.size()]));
            // MStar Android Patch Begin

            WifiManager mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_DISABLED
                    && wifiHotspotViewHolder != null && wifiHotspotViewHolder.mRestartWifiApAfterConfigChange) {
                wifiHotspotViewHolder.mRestartWifiApAfterConfigChange = false;
                Log.d("qkmin", "Restarting WifiAp due to prior config change.");
                // TODO: 2019-12-03 2.4g 和 5G
                wifiHotspotViewHolder.setSoftapEnabled(this, true);
            } else if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_DISABLED
                    && screenHotSponViewHolder != null && screenHotSponViewHolder.mRestartWifiApAfterConfigChange) {
                screenHotSponViewHolder.mRestartWifiApAfterConfigChange = false;
                Log.d("qkmin", "screenHotSponViewHolder   Restarting WifiAp due to prior config change.");
                // TODO: 2019-12-03 2.4g 和 5G
                screenHotSponViewHolder.setSoftapEnabled(this, true);
            }


        } else if (Intent.ACTION_AIRPLANE_MODE_CHANGED.equals(action)) {
//            enableWifiSwitch();
            Log.d("qkmin--->", "Intent.ACTION_AIRPLANE_MODE_CHANGED");

        }
    }

    private void updateTetherState(Object[] available, Object[] tethered, Object[] errored) {
        boolean wifiTethered = false;
        boolean wifiErrored = false;

        for (Object o : tethered) {
            String s = (String) o;
            for (String regex : mWifiRegexs) {
                if (s.matches(regex)) wifiTethered = true;
            }
        }
        for (Object o : errored) {
            String s = (String) o;
            for (String regex : mWifiRegexs) {
                if (s.matches(regex)) wifiErrored = true;
            }
        }

        if (wifiTethered) {
            WifiManager mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration wifiConfig = mWifiManager.getWifiApConfiguration();
            Log.d("qkmin---->", " wifiConfig.SSID:" + wifiConfig.SSID);

            //updateConfigSummary(wifiConfig);
        } else if (wifiErrored) {
            Log.d("qkmin---->", " wifi wifiErrored");

            //mSwitch.setSummary(R.string.wifi_error);
        }
    }

}