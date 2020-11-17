
package com.ctv.settings.network.holder;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.activity.NetWorkActivity;
import com.ctv.settings.network.adapter.WirelessAdapter;
import com.ctv.settings.network.dialog.WiFiAddDialog;
import com.ctv.settings.network.dialog.WiFiConnectDialog;
import com.ctv.settings.network.helper.WifiConfigHelper;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wifi模块
 * @author xuzemin
 * @date 2019/09/19
 */
public class WirelessViewHolder implements OnFocusChangeListener, OnClickListener,
        ConnectivityListener.WifiNetworkListener {

    private static final String TAG = "WirelessViewHolder";

    private List<ScanResult> scanResults = new ArrayList<>();

    private AtomicBoolean mConnected = new AtomicBoolean(false);

    private final NetWorkActivity activity;

    public WirelessAdapter wirelessAdapter = null;

    private FrameLayout wireless_switch_fl = null;

    private ImageView wireless_switch_iv = null;

    private WifiManager wifiManager;

    private final Context ctvContext;

    private ListView wifi_lv = null;

    private ProgressBar wifi_pb = null;

    private boolean isOpen_top = false;

    public boolean isOpenWifi = false;

    public boolean isDialog = false;

    public String forgetingSsid = "";

    private String clickSsid = "";

    private boolean mWasAssociating = false;

    private boolean mWasAssociated = false;

    private boolean mWasHandshaking = false;

    private WifiConfiguration mConnectingWifiConfiguration = null;

    private final ConnectivityListener mListener;

    private boolean isRegisted = false;

    private boolean isWifiOpenning = false;

    private InitDataInfo mInitData = null;

    @SuppressLint("HandlerLeak")
    private final Handler wifiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NetUtils.OPEN_WIFI:
//                    Log.i(TAG, "wifiHandler setAndCheckWifiData");
//                    openWifi();
                    wifiManager.setWifiEnabled(true);
                    break;
                case NetUtils.START_SCAN_WIFI:
                    wifiManager.startScan();
                    break;
                case NetUtils.REFRESH_SIGNAL_WIFI:
                    refreshWifiSignal();
                    wifiHandler.sendEmptyMessageDelayed(NetUtils.REFRESH_SIGNAL_WIFI,1000);
                    break;
                case NetUtils.CHECKOUT_TIME_WIFI:
                    if (wirelessAdapter.getmConnectState() == 2 && !mWasAssociating) {
                        try {
                            timeout_remove_network();
                        } catch (Exception e) {
                            L.d(e.toString());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private final BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!isOpenWifi || isDialog) {
                return;
            }
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                Log.d(TAG, "----------------Got supplicant state: " + state.name());
                switch (state) {
                    case ASSOCIATING:
                        mWasAssociating = true;
                        break;
                    case ASSOCIATED:
                        mWasAssociated = true;
                        break;
                    case COMPLETED:
                        // this just means the supplicant has connected, now
                        // we wait for the rest of the framework to catch up
                        mWasAssociating = false;
                        mWasAssociated = false;
                        mWasHandshaking = false;
                        mConnectingWifiConfiguration = null;
                        break;
                    case DISCONNECTED:
                    case DORMANT:
                        // Becase the Shared Key Authentication for wep has not
                        // 4-ways handshaking certification process. It will
                        // return
                        // an ASSOC-REJECT, and make supplicant return a
                        // disconnected status,
                        // when it is in Associating stage. So when
                        // mWasAssociating is true and
                        // mWasAssociated is false, users input a wrong
                        // password.

                        /*
                         * if (mWasAssociating && !mWasAssociated) { Log.d(TAG,
                         * "password error!! RESULT_BAD_AUTHENTICATION");
                         * WifiConfigHelper.forgetConfiguration(context,
                         * mConnectingWifiConfiguration, null);
                         * wirelessAdapter.setConnectState(3); mWasAssociating =
                         * false; mConnectingWifiConfiguration = null; } else
                         */if (mWasAssociated || mWasHandshaking) {
                        if (mWasHandshaking) {
                            Log.d(TAG, "RESULT_BAD_AUTHENTICATION");
                        } else {
                            Log.d(TAG, "RESULT_UNKNOWN_ERROR");
                        }
                        mWasAssociated = false;
                        mWasHandshaking = false;
                        try {
                            WifiConfigHelper.forgetConfiguration(context,
                                    mConnectingWifiConfiguration);
                            if(wirelessAdapter !=null){
                                wirelessAdapter.setmSsid("");
                                wirelessAdapter.setConnectState(0);
                            }
                        } catch (Exception e) {
                            L.d(e.toString());
                        }
                        wirelessAdapter.setConnectState(3);
                        mConnectingWifiConfiguration = null;
                    }
                        break;
                    case INTERFACE_DISABLED:
                    case UNINITIALIZED:
                        Log.d(TAG, "RESULT_UNKNOWN_ERROR");
                        // wirelessAdapter.setConnectState(0);
                        break;
                    case FOUR_WAY_HANDSHAKE:
                    case GROUP_HANDSHAKE:
                        mWasHandshaking = true;
                        break;
                    case INACTIVE:
                        if (mWasAssociating && !mWasAssociated) {
                            // If we go inactive after 'associating' without
                            // ever having
                            // been 'associated', the AP(s) must have rejected
                            // us.
                            Log.d(TAG, "RESULT_REJECTED_BY_AP");
                            // wirelessAdapter.setConnectState(0);
                            break;
                        }
                    case SCANNING:
                        if (wirelessAdapter.getmConnectState() == 2) {
                            wirelessAdapter.setConnectState(2);
                            /*
                             * hide ssid,input incorrect secureType ,time out
                             * error
                             */
                            if (mConnectingWifiConfiguration != null
                                    && mConnectingWifiConfiguration.hiddenSSID) {
                                wifiHandler.sendEmptyMessageDelayed(NetUtils.CHECKOUT_TIME_WIFI, 8000);
                            }
                        }
                        break;
                    case INVALID:
                    default:
                        return;
                }
            }
        }
    };

    public WirelessViewHolder(Context ctvContext, ConnectivityListener conListener) {
        super();
        this.ctvContext = ctvContext.getApplicationContext();
        this.mListener = conListener;
        wifiManager = mListener.getWifiManager();
        activity = (NetWorkActivity) ctvContext;
        initViewAndReceiver();
    }

    private void initViewAndReceiver() {
        wireless_switch_fl = (FrameLayout) activity.findViewById(R.id.wireless_switch_fl);
        wireless_switch_iv = (ImageView) activity.findViewById(R.id.wireless_switch_iv);
        wifi_lv = (ListView) activity.findViewById(R.id.wifi_lv);
        wifi_pb = (ProgressBar) activity.findViewById(R.id.wifi_pb);
        wifi_pb.setVisibility(View.GONE);
        LayoutInflater factory = LayoutInflater.from(activity);
        LinearLayout mFooterView = (LinearLayout) factory.inflate(R.layout.add_wifi_ssid_btn, null);
        wifi_lv.addFooterView(mFooterView, null, true);
        scanResults.clear();
        wirelessAdapter = new WirelessAdapter(activity, scanResults);
        wifi_lv.setAdapter(wirelessAdapter);
        wireless_switch_fl.setOnFocusChangeListener(this);
        wireless_switch_fl.setOnClickListener(this);
        wireless_switch_iv.setOnFocusChangeListener(this);
        wireless_switch_iv.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        ctvContext.registerReceiver(mWifiReceiver, filter);
        activity.findViewById(R.id.back_wireless).setOnClickListener(this);
        isRegisted = true;
        wifi_lv.setOnItemClickListener((parent, view, position, id) -> {
            Log.i(TAG, "-----setOnItemClickListener--position:" + position);
            isDialog = true;
            if (position == (scanResults.size())) {
                // show dialog to add hidden SSID.
                clickSsid = "";
                Log.d(TAG, "clicked position:" + position);
                WiFiAddDialog addtDialog = new WiFiAddDialog(activity, WirelessViewHolder.this);
                addtDialog.show();
            } else {
                ScanResult scanResult = scanResults.get(position);
                clickSsid = scanResult.SSID;
                WifiConfiguration config = null;
                try {
                    config = WifiConfigHelper.getConfigurationForNetwork(
                            ctvContext, scanResult);
                } catch (Exception e) {
                    L.d(e.toString());
                }
                Log.d(TAG, "----------------config =" + config);
                if (config != null) {
                    //  int configType = config.getAuthType();
                    Log.i(TAG, "onItemClick config: " + config.toString() + "===");
                    //   Log.i(TAG, "onItemClick configType: " + configType);
                    if (wifiManager.getConnectionInfo().getSSID().equals(config.SSID)) {
                        WiFiConnectDialog connectDialog = new WiFiConnectDialog(activity,
                                WirelessViewHolder.this, scanResult, mListener);
                        connectDialog.show();
                        return;
                    } else {
                        if (WifiConfigHelper.isNetworkSaved(config)) {
                            try {
                                NetUtils.connect(wifiManager,config);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
//                                wifiManager.connect(config, mConnectListener);
                            wirelessAdapter.setmSsid(clickSsid);
                            wirelessAdapter.setConnectState(2);
                            setAndCheckWifiData(false);
                            isDialog = false;
                            initAssociatState(config);
                        } else {
                            WiFiConnectDialog connectDialog = new WiFiConnectDialog(activity,
                                    WirelessViewHolder.this, scanResult, mListener);
                            connectDialog.show();
                            return;
                        }
                    }

                } else {
                    WiFiConnectDialog connectDialog = new WiFiConnectDialog(activity,
                            WirelessViewHolder.this, scanResult, mListener);
                    connectDialog.show();
                }
            }
        });
    }

    /**
     * initData(The function of the method)
     *
     * @Title: initData
     * @Description: TODO
     */
    public void initData(InitDataInfo data) {
        isOpenWifi = false;
        mInitData = data;
        if (mListener.isWifiEnabled()) {
            isOpenWifi = true;
        }
        Log.i(TAG, "-shtet-isOpenWifi:" + isOpenWifi);
        initWifiData(true);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isWifiOpenning) {
            return true;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.wireless_switch_fl || view.getId() == R.id.wireless_switch_iv) {
            isOpenWifi = !isOpenWifi;
            setAndCheckWifiData(true);
        }else if(view.getId() == R.id.back_wireless){
            activity.finish();
        }
    }




    public void stateChanged(Intent intent) {
        NetworkInfo networkInfo = intent
                .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
        mConnected.set(networkInfo.isConnected());
        if (networkInfo.getState() == NetworkInfo.State.CONNECTING) {
            wirelessAdapter.setmSsid(wifiManager.getConnectionInfo().getSSID());
            wirelessAdapter.setConnectState(2);
            mConnectingWifiConfiguration = WifiConfigHelper.getWifiConfiguration(wifiManager,
                    wifiManager.getConnectionInfo().getNetworkId());
        } else if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            wirelessAdapter.updateTheTop(wifiManager.getConnectionInfo().getSSID());
            wirelessAdapter.setConnectState(1);
            wifi_lv.setSelection(0);
        }else{
            wirelessAdapter.setConnectState(0);
        }
    }

    public void setAndCheckWifiData(boolean isClick) {
        Log.d(TAG, "setAndCheckWifiData --  isOpenWifi  " + isOpenWifi);
        if (isOpenWifi) {
            // wifi_dongle is ready

            // TODO: 2019-10-24 8386 wifi
//            if (!MWifiManager.getInstance().isWifiDeviceExist()) {
//                if (isClick) {
//                    showToast(R.string.please_insert_dongle);
//                }
//                isOpenWifi = false;
//                Log.d(TAG, "setAndCheckWifiData --  wifi_dongle?");
//                return;
//            }
            // binary mark, Each Bit is a flag for example :0B111
            int flag = 0;

            try {
                if (NetUtils.isWifiApEnabled(wifiManager)) {
                    flag = flag + 1;// wifi_ap is active
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!mListener.getPPPoeStatusDescription().equals(
                    ctvContext.getString(R.string.pppoe_disconnected))) {
                flag = flag + 2;// wifi_pppoe is active
            }
            Log.d(TAG, "--flag: " + flag);
            if (isClick) {
                openWifi();
            }
        } else {
            if (isClick) {
                isOpen_top = false;
                wifiHandler.removeMessages(NetUtils.OPEN_WIFI);
                wifiManager.setWifiEnabled(false);
                mConnectingWifiConfiguration = null;
//                scanResults.clear();
                wirelessAdapter.setmSsid("");
                wirelessAdapter.setConnectState(0);
                wirelessAdapter.setScanResults(scanResults);
                mWasAssociating = false;
                mWasAssociated = false;
                mWasHandshaking = false;
            }
            wireless_switch_iv.setBackgroundResource(R.mipmap.off);
            setWirelessStatus(false);
            wifi_lv.setVisibility(View.INVISIBLE);
        }
    }

    private void openWifi() {
        wireless_switch_iv.setBackgroundResource(R.mipmap.on);
        wifi_pb.setVisibility(View.VISIBLE);
        wireless_switch_iv.setClickable(false);
        isWifiOpenning = true;
        wifiHandler.sendEmptyMessageDelayed(NetUtils.OPEN_WIFI,1000);
    }

    private void wifiIsOpened() {
        isWifiOpenning = false;
        wireless_switch_iv.setClickable(true);
        isOpen_top = true;
        wifi_pb.setVisibility(View.GONE);
        mListener.scanWifiAccessPoints(this);
    }

    private void refreshWifiSignal() {

        List<ScanResult> scanRst = mListener.getAvailableNetworks();
        if (scanRst != null) {
            // Log.i(TAG, "--refreshWifiSignal--scanRst.size:" +
            // scanRst.size());
            scanResults.clear();
            for (ScanResult result : scanRst) {
                if (result.capabilities.contains("[IBSS]") || TextUtils.isEmpty(result.SSID.trim())) {
                    continue;
                } else if (scanResults.contains(result)) {
                    continue;
                } else {
                    boolean isNew = true;
                    for (int i = 0; i < scanResults.size(); i++) {
                        if (scanResults.get(i).SSID.equals(result.SSID)) {
                            scanResults.set(i, result);
                            isNew = false;
                            break;
                        }
                    }
                    if (isNew) {
                        scanResults.add(result);
                    }
                }
            }
        }
        wirelessAdapter.setScanResults(scanResults);
        wirelessAdapter.notifyDataSetChanged();
        if (isOpen_top) {
            wifi_lv.setSelection(0);
            isOpen_top = false;
        }
    }

    private void initWifiData(boolean isInit) {
        scanResults.clear();
        if (isOpenWifi) {
            wireless_switch_iv.setBackgroundResource(R.mipmap.on);
            setWirelessStatus(true);
            wifi_lv.setVisibility(View.VISIBLE);
            List<ScanResult> scanRst;
            if (isInit) {
                scanRst = mInitData.getScanRst();
            } else {
                scanRst = mListener.getAvailableNetworks();
            }
            if (scanRst != null) {
                Log.i(TAG, "==scanRst.size:" + scanRst.size());
//                scanResults.clear();
                for (ScanResult result : scanRst) {
                    String ssid = result.SSID;
                    Log.i("WirelessAdapter1", "00SSID:-" + ssid + "-");
                    if (result.capabilities.contains("[IBSS]")
                            || TextUtils.isEmpty(result.SSID.trim())) {
                        continue;
                    } else if (scanResults.contains(result)) {
                        continue;
                    } else {
                        boolean isNew = true;
                        for (int i = 0; i < scanResults.size(); i++) {
                            if (scanResults.get(i).SSID.equals(result.SSID)) {
                                scanResults.set(i, result);
                                isNew = false;
                                break;
                            }
                        }
                        if (isNew) {
                            Log.i("WirelessAdapter1", "==add SSID:-" + result.SSID + "-");
                            scanResults.add(result);
                        }
                    }
                }
            }
            Log.i("WirelessAdapter", "=======scanResults:" + scanResults);
            wirelessAdapter.setScanResults(scanResults);
            wirelessAdapter.updateTheTop(wifiManager.getConnectionInfo().getSSID());
//            wirelessAdapter.setConnectState(1);
            wirelessAdapter.notifyDataSetChanged();
        } else {
            wireless_switch_iv.setBackgroundResource(R.mipmap.off);
            setWirelessStatus(false);
            wifi_lv.setVisibility(View.INVISIBLE);
        }
    }

    public void updateWifiState(int state) {
        Log.e(TAG, "--------------updataWifiState state=" + state);
        switch (state) {
            case WifiManager.WIFI_STATE_UNKNOWN:
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                setOpenWifi(true);
                wifiIsOpened();
                wifiHandler.sendEmptyMessage(NetUtils.REFRESH_SIGNAL_WIFI);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            default:
            case WifiManager.WIFI_STATE_DISABLING:
            case WifiManager.WIFI_STATE_DISABLED:
                // wifiIsOpened();
                wifi_pb.setVisibility(View.GONE);
                isWifiOpenning = false;
                wireless_switch_iv.setClickable(true);
                isOpen_top = false;
                wifiHandler.removeMessages(NetUtils.REFRESH_SIGNAL_WIFI);
                break;
        }
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        Log.i(TAG, "--onFocusChange" + view.getId());
        if (view.getId() == R.id.wireless_switch_iv) {
            if (has_focus) {
                wireless_switch_fl.setSelected(true);
            } else {
                wireless_switch_fl.setSelected(false);
            }
        }
    }

    private void showToast(int id) {
        if (id <= 0) {
            Log.d(TAG, "id < 0");
            return;
        }
        Toast.makeText(ctvContext, id, Toast.LENGTH_SHORT).show();
    }

    private void setOpenWifi(boolean isOpenWifi) {
        this.isOpenWifi = isOpenWifi;
        initWifiData(false);
    }

    public void onExit() {
        if (isRegisted) {
            ctvContext.unregisterReceiver(mWifiReceiver);
        }
        if (wirelessAdapter != null) {
            wirelessAdapter.onExit();
        }
        wirelessAdapter = null;
        if (scanResults != null) {
            scanResults.clear();
        }
        wireless_switch_fl = null;
        wireless_switch_iv = null;
        wifi_lv = null;
        wifi_pb = null;
        isOpen_top = false;
        isOpenWifi = false;
        isDialog = false;
        forgetingSsid = "";
        clickSsid = "";
        mWasAssociating = false;
        mWasAssociated = false;
        mWasHandshaking = false;
        mConnectingWifiConfiguration = null;
        isRegisted = false;
        isWifiOpenning = false;
        mInitData = null;
        wifiHandler.removeCallbacksAndMessages(null);
    }

    public void initAssociatState(WifiConfiguration config) {
        mWasAssociating = false;
        mWasAssociated = false;
        mWasHandshaking = false;
        mConnectingWifiConfiguration = config;
    }

    private void setWirelessStatus(boolean status) {
        SharedPreferences sp = ctvContext.getSharedPreferences("wifi_status", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean("wifi_on", status);
        editor.commit();
    }

    // Becase wep has two encrytion, Open System Authentication and Shared Key
    // Authentication.
    // But the Open System Authentication will not return an error password
    // authentication
    // information, it returns success directly, lead to the wrong password for
    // preserved.
    // So we shall be delete the wrong password, to ensure that the next input
    // the correct
    // password, it will be connect.
    private void timeout_remove_network() throws Exception {
        if (mConnectingWifiConfiguration != null) {
            Log.d(TAG, "----------------timeout_remove_network,mConnectingWifiConfiguration:"
                    + mConnectingWifiConfiguration.SSID + ",networkId:"
                    + mConnectingWifiConfiguration.networkId);
            if (mConnectingWifiConfiguration.networkId == NetUtils.INVALID_NETWORK_ID) {
                WifiConfigHelper.forgetConfiguration(ctvContext, mConnectingWifiConfiguration);

            } else {
                WifiConfigHelper.forgetWifiNetwork(ctvContext);
            }

            if (wirelessAdapter != null) {
                wirelessAdapter.setConnectState(3);
            }
        }
    }

    @Override
    public void onWifiListChanged() {
        // TODO Auto-generated method stub
    }
}
