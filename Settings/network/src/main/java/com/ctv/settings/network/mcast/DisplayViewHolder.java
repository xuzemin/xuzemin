
package com.ctv.settings.network.mcast;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.hardware.display.WifiDisplayStatus;
import android.net.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.network.R;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.utils.DataTool;
import com.cultraview.tv.utils.CtvCommonUtils;
import com.mstar.android.pppoe.PPPOE_STA;
import com.mstar.android.pppoe.PppoeManager;
import com.mstar.android.wifi.MWifiManager;

public class DisplayViewHolder {

    private static final String TAG = "DisplayViewHolder";

    private Context ctvContext = null;

    private FrameLayout display_setting_fl, mcast_main_fl;

    private TextView display_name_tv;

    private ImageView display_swich_iv;

    private ImageView display_wifi_state_iv;

    private String display_device_name;

    private DisplayManager displayManager = null;

    private WifiDisplayStatus wifiDisplayStatus = null;

    private boolean isOpenDisplay = false;

    private WifiP2p WifiP2p = null;

    private Activity mActivity = null;

    private boolean isRegisted = false;

    private IntentFilter wifiFilter = null;

    private Intent mIntent = null;
    private boolean isWifiOpened = false;

    public final Handler displayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isWifiOpened = (Boolean) msg.obj;
                    Log.i(TAG, "displayHandler setOpenHotspot");
                    if (isWifiOpened) {
                        ctvContext.sendBroadcast(mIntent);
                        SystemProperties.set("net.wfd.enable", "1");
                        try {
                            WifiP2p = new WifiP2p(ctvContext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        WifiP2p.enter();
                    }
                    // display_device_name = WifiP2p.getMircDeviceName();
                    display_device_name = DataTool.getDeviceName(ctvContext);
                    display_swich_iv.setImageResource(R.mipmap.open);
                    display_wifi_state_iv.setImageResource(R.mipmap.display_wifi_open);
                    display_name_tv.setText(ctvContext.getResources().getString(
                            R.string.device_name)
                            + display_device_name);
                    isOpenDisplay = true;
                    break;
                case 1:
                    if (isWifiOpened) {
                        ctvContext.sendBroadcast(mIntent);
                        SystemProperties.set("net.wfd.enable", "1");
                        try {
                            WifiP2p = new WifiP2p(ctvContext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        WifiP2p.enter();
                    }
                    Toast.makeText(ctvContext, ctvContext.getText(R.string.wifi_display_options_done), Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    public DisplayViewHolder(Context ctvContext) {
        this.ctvContext = ctvContext.getApplicationContext();
        this.mActivity = (Activity)ctvContext;
        initData();
        setData();
    }

    /**
     * initData(The function of the method)
     *
     * @Title: initData
     * @Description: TODO
     */
    @SuppressLint("InlinedApi")
    private void initData() {
        display_setting_fl = (FrameLayout) mActivity.findViewById(R.id.display_setting_fl);
        display_name_tv = (TextView) mActivity.findViewById(R.id.display_name_tv);
        display_swich_iv = (ImageView) mActivity.findViewById(R.id.display_swich_iv);
        display_wifi_state_iv = (ImageView) mActivity.findViewById(R.id.display_wifi_state_iv);
        mcast_main_fl = (FrameLayout) mActivity.findViewById(R.id.mcast_main_fl);

        wifiFilter = new IntentFilter();
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_ADDED_ACTION);
        wifiFilter.addAction(NetUtils.DEVICE_NAME_CHANGE_ACTION);
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_REMOVED_ACTION);
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        ctvContext.registerReceiver(wifiHWReceiver, wifiFilter);
        isRegisted = true;

        displayManager = (DisplayManager) ctvContext.getSystemService(Context.DISPLAY_SERVICE);
        isOpenDisplay = SystemProperties.get("net.wfd.enable").equals("1") ? true : false;

        Log.i(TAG, "----isOpenDisplay:" + isOpenDisplay);
        display_setting_fl.setOnClickListener(dispalyClickListener);
    }
    private OnClickListener dispalyClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // Open wifi, change the switch status
            if(!DataTool.isEnableMcast()){
                Toast.makeText(ctvContext, ctvContext.getString(R.string.display_toast_fail), Toast.LENGTH_SHORT).show();
                return;
            }else{
            isOpenDisplay = !isOpenDisplay;
            if (isOpenDisplay) {
                if (setData()) {
                    DisplayTipDialog dialog = new DisplayTipDialog(mActivity);//Mcast
                    dialog.show();
                    return;
                }
                SystemProperties.set("net.wfd.enable", "1");
            } else {
                setData();
            }
            }
        }
    };
    /**
     * setData(The function of the method)
     *
     * @Title: setData
     * @Description: TODO
     */
    public boolean setData() {
        boolean isFeatureOff = true;
//        wifiDisplayStatus = displayManager.getWifiDisplayStatus();
        try {
            wifiDisplayStatus = NetUtils.getWifiDisplayStatus(displayManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mIntent = new Intent();
        mIntent.setAction("android.intent.action.MSTAR_MCAST");
        int featureState = wifiDisplayStatus.getFeatureState();
        Log.i(TAG, "----featureState:" + featureState);
        try {
            if (isOpenDisplay && featureState == WifiDisplayStatus.FEATURE_STATE_ON && isNetReady()) {
                ctvContext.sendBroadcast(mIntent);
                try {
                    WifiP2p = new WifiP2p(ctvContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                WifiP2p.enter();
                // display_device_name = WifiP2p.getMircDeviceName();
                display_device_name = DataTool.getDeviceName(ctvContext);
                display_swich_iv.setImageResource(R.mipmap.open);
                display_wifi_state_iv.setImageResource(R.mipmap.display_wifi_open);
                isFeatureOff = false;
            } else {
                display_device_name = DataTool.getDeviceName(ctvContext);
                display_swich_iv.setImageResource(R.mipmap.close);
                display_wifi_state_iv.setImageResource(R.mipmap.display_wifi_close);
                if (WifiP2p != null) {
                    WifiP2p.exit();
                    WifiP2p = null;
                }
                isOpenDisplay = false;
                SystemProperties.set("net.wfd.enable", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        display_name_tv.setText(ctvContext.getResources().getString(R.string.device_name)
                + display_device_name);
        return isFeatureOff;
    }

    private boolean isNetReady() throws Exception {
        int flag = 0;
        PppoeManager mPPPoEManager = PppoeManager.getInstance(ctvContext);
        WifiManager mWifiManager = (WifiManager) ctvContext.getSystemService(Context.WIFI_SERVICE);
        @SuppressLint("WrongConstant") EthernetManager mEthernetManager = (EthernetManager) ctvContext.getSystemService(NetUtils.ETHERNET_SERVICE);
        // 1. PPPoE
        if (PPPOE_STA.DISCONNECTED != mPPPoEManager.PppoeGetStatus()) {
            flag += 1;
        }
        // 2. WiFi AP
        if(NetUtils.isWifiApEnabled(mWifiManager)){
            flag += 2;
        }
//        if (mWifiManager.isWifiApEnabled()) {
//            flag += 2;
//        }
        // 3. Ethernet
        if (mEthernetManager.isEnabled()) {
            flag += 4;
        }
        // 4. WiFi
        if (!mWifiManager.isWifiEnabled()) {
            flag += 6;
        }
        if (flag != 0) {
            return false;
        } else {
            return true;
        }

    }

    public void unregisterReceiver() {
        if (isRegisted) {
            ctvContext.unregisterReceiver(wifiHWReceiver);
            isRegisted = false;
        }
        if (WifiP2p != null) {
            WifiP2p.unregisterWifiHWReceiver();
            WifiP2p = null;
        }
    }

    public void exitMcast(){
        if (WifiP2p != null) {
            WifiP2p.exit();
        }
        if (mIntent != null) {
            mIntent = null;
        }
    }

    private BroadcastReceiver wifiHWReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN));
            } else if (NetUtils.DEVICE_NAME_CHANGE_ACTION.equals(intent.getAction())) {
                display_device_name = DataTool.getDeviceName(ctvContext);
                display_name_tv.setText(ctvContext.getResources().getString(R.string.device_name)
                        + display_device_name);
            } else {
                setData();
            }
        }
    };
    private void updateWifiState(int state) {
        Log.e(TAG, "--------------updataWifiState state=" + state);
        switch (state) {
            case WifiManager.WIFI_STATE_UNKNOWN:
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                isWifiOpened = true;
                displayHandler.sendEmptyMessage(1);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            default:
            case WifiManager.WIFI_STATE_DISABLING:
            case WifiManager.WIFI_STATE_DISABLED:
                break;
        }
    }
    public void scaleAnimation(boolean isIn) {
        if (isIn) {
            mcast_main_fl.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.scale));
            mcast_main_fl.setVisibility(View.VISIBLE);
        } else {
            mcast_main_fl.clearAnimation();
            mcast_main_fl.startAnimation(AnimationUtils.loadAnimation(mActivity, R.anim.scale2));
        }
    }

    public void setOut() {
        mcast_main_fl.clearAnimation();
        mcast_main_fl.setVisibility(View.GONE);
    }
    public void onExit(){
        wifiHWReceiver = null;
        displayHandler.removeCallbacksAndMessages(null);
        ctvContext = null;
        wifiFilter = null;
        dispalyClickListener = null;
        mActivity = null;
    }
}
