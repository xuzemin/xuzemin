
package com.ctv.settings.network.activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.Listener.LoadDataAsyncTask;
import com.ctv.settings.network.R;
import com.ctv.settings.network.holder.*;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.utils.L;
import com.mstar.android.wifi.MWifiManager;
import android.net.wifi.WifiManager;

/**
 * 网络模块
 * @author xuzemin
 * @date 2019/09/19
 */
public class NetWorkActivity extends AppCompatActivity implements ConnectivityListener.Listener {
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//        currentTag = 1;

        Intent getIntent = getIntent();
        currentTag = getIntent.getIntExtra("CurrentTag",0);

        if(currentTag != 0) {
            mConnectivityListener = new ConnectivityListener(this, this);
            switch (currentTag) {
                case NetUtils.WIRELESS_CONNECT:  //WIFI
                    setContentView(R.layout.page_wireless);
                    wirelessViewHolder = new WirelessViewHolder(this,mConnectivityListener);
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
                    wifiHotspotViewHolder = new WifiHotspotViewHolder(this,mConnectivityListener);
                    break;
                case NetUtils.SCREEN_HOT:   //投屏热点
                    setContentView(R.layout.page_screen_hot_spot);
                    try {
                        screenHotSponViewHolder = new ScreenHotSponViewHolder(this,mConnectivityListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case NetUtils.PPPOE_CONNECT:
                    setContentView(R.layout.page_pppoe);
                    pppoeViewHolder = new PppoeViewHolder(this,mConnectivityListener);
                    break;
            }
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        if(currentTag == NetUtils.BLUETOOTH){
            mBluetoothHolder.onResume();
        }else {
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
                mConnectivityListener.start();
            });
            mAyncTask.execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mConnectivityListener != null) {
            mConnectivityListener = null;
        }
//        if (netstateViewHolder != null) {
//            netstateViewHolder.onExit();
//        }
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
//        if (netViewHolder != null) {
//            netViewHolder.onExit();
//        }
        onExit();
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.gc();
    }

    public void onExit() {
//        ctvContext = null;
//        pageNumber = -1;
//        netViewHolder = null;
//        netstateViewHolder = null;
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
//        animationHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        switch (currentTag){
            case NetUtils.WIRELESS_CONNECT:  //WIFI
                if(wireViewHolder != null) {
                    return wireViewHolder.onKeyDown(keyCode, event);
                }
            case NetUtils.BLUETOOTH: //蓝牙
                if(wireViewHolder != null) {
                    return mBluetoothHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.WIRE_CONNECT:   //以太网  待网线验证
                if(wireViewHolder != null) {
                    return wireViewHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.WIFI_HOTSPOT:    //热点
                if(wirelessViewHolder != null) {
                    return wirelessViewHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.SCREEN_HOT:   //投屏热点
                if(screenHotSponViewHolder != null) {
                    return screenHotSponViewHolder.onKeyDown(keyCode, event);
                }
                break;
            case NetUtils.PPPOE_CONNECT:
                if(pppoeViewHolder != null) {
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
        L.d( "-------------onEthernetAvailabilityChanged,isAvailable, " + isAvailable);
        if(wireViewHolder != null){
            wireViewHolder.showEthernetInfo(false);
        }
    }



    public WifiHotspotViewHolder getWifiHotspotViewHolder() {
        return wifiHotspotViewHolder;
    }

    public WireViewHolder getWireViewHolder() {
        return wireViewHolder;
    }

    public WirelessViewHolder getWirelessViewHolder() {
        return wirelessViewHolder;
    }


    private void refreshView(Intent intent) throws Exception {
        String action = intent.getAction();
        L.d("--action, " + action);
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            if(wirelessViewHolder !=null){
                wirelessViewHolder.updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN));
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            if ((!mConnectivityListener.getConnectivityStatus().isWifiConnected())
                    && (!mConnectivityListener.isWifiEnabled())) {
//                netstateViewHolder.refreshNetworkStatus();
                //pppoeViewHolder.refreshPPPoeConnectState();
            } else {
//                netstateViewHolder.refreshWifiStatus(false);
            }
            if (wirelessViewHolder != null && wirelessViewHolder.isOpenWifi) {
                wirelessViewHolder.stateChanged(intent);
            }
        } else if (NetUtils.WIFI_AP_STATE_CHANGED_ACTION.equals(action)) {
            if(wifiHotspotViewHolder !=null){
                wifiHotspotViewHolder.upApStateChange(intent);
            }

            if(screenHotSponViewHolder !=null) {
                screenHotSponViewHolder.upApStateChange(intent);
            }
        } else if (MWifiManager.WIFI_DEVICE_ADDED_ACTION.equals(action)) {
//            netstateViewHolder.refreshNetworkStatus();
            //  pppoeViewHolder.refreshPPPoeConnectState();
        } else if (MWifiManager.WIFI_DEVICE_REMOVED_ACTION.equals(action)) {
            if (NetUtils.WIFI_AP_STATE_ENABLED ==
                    NetUtils.getWifiApConfiguration(mConnectivityListener.getWifiManager()).status
//                    mConnectivityListener.getWifiManager()
//                    .getWifiApConfiguration().status
            ) {
                if (
                        !NetUtils.isWifiApEnabled(mConnectivityListener.getWifiManager())
//                        !mConnectivityListener.getWifiManager().isWifiApEnabled()
                ) {
                    if(wifiHotspotViewHolder != null) {
                        wifiHotspotViewHolder.isOpenHotspot = false;
                    }
                    if(screenHotSponViewHolder != null) {
                        screenHotSponViewHolder.isOpenHotspot = false;
                        screenHotSponViewHolder.setOpenHotspot(false);
                    }
                    if(wifiHotspotViewHolder !=null) {
                        wifiHotspotViewHolder.setOpenHotspot(false);
                    }
                }
            }
            if (wirelessViewHolder!=null && !wirelessViewHolder.wifiManager.isWifiEnabled()) {
                wirelessViewHolder.isOpenWifi = false;
                wirelessViewHolder.setAndCheckWifiData(false);
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)
                || NetUtils.INET_CONDITION_ACTION.equals(action)) {

//            netstateViewHolder.refreshNetworkStatus();
            if (wirelessViewHolder!=null)
                wireViewHolder.showEthernetInfo(false);


            //  pppoeViewHolder.updatePppoeData(mConnectivityListener.getPPPoeStatusDescription());
        }
    }

}