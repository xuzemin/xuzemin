
package com.ctv.settings.network.holder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.R;
import com.ctv.settings.network.activity.NetWorkActivity;
import com.ctv.settings.network.dialog.TipDialog;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.network.utils.Tools;
import com.mstar.android.wifi.MWifiManager;

public class WifiHotspotViewHolder implements OnFocusChangeListener, OnClickListener {

    private static final String TAG = "WifiHotspotViewHolder";

    private static final int[] SECURE_TYPE = {
            R.string.wifi_security_wpa, R.string.wifi_security_wpa2, R.string.wifi_security_open
    };

    private final Context ctvContext;

    private LinearLayout hotspot_ll;

    private FrameLayout hotspot_open_fl;

    private FrameLayout hotspot_show_pwd_fl;

    private FrameLayout hotspot_pwd_fl;

    private FrameLayout hotspot_secure_fl;

    private EditText hotspot_ssid_edt;

    private EditText hotspot_pwd_edt;

    private ImageView hotspot_open_iv;

    private ImageView hotspot_secure_iv;

    private ImageView hotspot_show_pwd_iv;

    private TextView hotspot_ssid_tv;

    private TextView hotspot_secure_tv;

    private TextView hotspot_secure_sele_tv;

    private TextView hotspot_pwd_tv;

    private TextView hotspot_ssid_value_tv;

    private TextView hotspot_secure_value_tv;

    private TextView hotspot_show_pwd_tv;

    private Button hotspot_save_btn;

    private boolean isShowPwd = false;

    public boolean isOpenHotspot = false;

    private boolean isSecureOpenType = false;

    private int secureType = NetUtils.SECURE_TYPE_WPA;

    private String secureString;

    private String ss_id1, wifi_secure1;

    public NotificationManager notificationManager;

    private NetWorkActivity activity;

    private final ConnectivityListener mListener;

    private ProgressDialog mDialog = null;

    public final Handler wifiHotHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "wifiHotHandler setOpenHotspot");
            isOpenHotspot = true;
            try {
                if (NetUtils.setWifiApEnabled(mListener.getWifiManager(),null, isOpenHotspot)
    //                    mListener.getWifiManager().setWifiApEnabled(null, isOpenHotspot)
            ) {
                    Log.i(TAG, "setOpenHotspot-setWifiApEnabled:" + isOpenHotspot);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            hotspot_open_iv.setBackgroundResource(R.mipmap.on);
            hotspot_ll.setVisibility(View.VISIBLE);
            hotspot_save_btn.setVisibility(View.VISIBLE);
            ShowSaveStatusDialog();
        }
    };

    public WifiHotspotViewHolder(Activity ctvContext, ConnectivityListener conListener) {
        super();
        this.ctvContext = ctvContext.getApplicationContext();
        this.mListener = conListener;
        activity = (NetWorkActivity) ctvContext;
        initView();
        initData();
        Log.i(TAG, "----last-isOpenHotspot:" + isOpenHotspot);
    }

    private void initView() {
        hotspot_ll = (LinearLayout) activity.findViewById(R.id.hotspot_ll);
        hotspot_open_fl = (FrameLayout) activity.findViewById(R.id.hotspot_open_fl);
        hotspot_secure_fl = (FrameLayout) activity.findViewById(R.id.hotspot_secure_fl);
        hotspot_show_pwd_fl = (FrameLayout) activity.findViewById(R.id.hotspot_show_pwd_fl);
        hotspot_pwd_fl = (FrameLayout) activity.findViewById(R.id.hotspot_pwd_fl);
        hotspot_ssid_edt = (EditText) activity.findViewById(R.id.hotspot_ssid_edt);
        hotspot_pwd_edt = (EditText) activity.findViewById(R.id.hotspot_pwd_edt);
        hotspot_open_iv = (ImageView) activity.findViewById(R.id.hotspot_open_iv);
        hotspot_secure_iv = (ImageView) activity.findViewById(R.id.hotspot_secure_iv);
        hotspot_show_pwd_iv = (ImageView) activity.findViewById(R.id.hotspot_show_pwd_iv);
        hotspot_ssid_tv = (TextView) activity.findViewById(R.id.hotspot_ssid_tv);
        hotspot_secure_tv = (TextView) activity.findViewById(R.id.hotspot_secure_tv);
        hotspot_secure_sele_tv = (TextView) activity.findViewById(R.id.hotspot_secure_sele_tv);
        hotspot_pwd_tv = (TextView) activity.findViewById(R.id.hotspot_pwd_tv);
        hotspot_show_pwd_tv = (TextView) activity.findViewById(R.id.hotspot_show_pwd_tv);
        hotspot_ssid_value_tv = (TextView) activity.findViewById(R.id.hotspot_ssid_value_tv);
        hotspot_secure_value_tv = (TextView) activity.findViewById(R.id.hotspot_secure_value_tv);
        hotspot_save_btn = (Button) activity.findViewById(R.id.hotspot_save_btn);
        hotspot_open_fl.setOnFocusChangeListener(this);
        hotspot_ssid_edt.setOnFocusChangeListener(this);
        hotspot_secure_iv.setOnFocusChangeListener(this);
        hotspot_pwd_edt.setOnFocusChangeListener(this);
        hotspot_show_pwd_fl.setOnFocusChangeListener(this);
        hotspot_save_btn.setOnFocusChangeListener(this);
        hotspot_open_fl.setOnClickListener(this);
        hotspot_secure_iv.setOnClickListener(this);
        hotspot_show_pwd_fl.setOnClickListener(this);
        hotspot_save_btn.setOnClickListener(this);
        ss_id1 = ctvContext.getString(R.string.ss_id1) + "  ";
        wifi_secure1 = ctvContext.getString(R.string.wifi_secure1) + "  ";
    }

    /**
     * initData(The function of the method)
     * 
     * @Title: initData
     * @Description: TODO
     */
    void initData() {
//        isOpenHotspot = mListener.getWifiManager().isWifiApEnabled();
        try {
            isOpenHotspot = NetUtils.isWifiApEnabled(mListener.getWifiManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String wifiapband = SystemProperties.get("Wifiapband");
        Log.d(TAG, "wifiap isOpenHotspot = " + isOpenHotspot + " ----- wifiapband : " + wifiapband);
        if(isOpenHotspot && !"".equals(wifiapband) && wifiapband != null && wifiapband.equals("Apband5G")) {
            isOpenHotspot = false;
        }
        setOpenHotspot(false);
        Log.d(TAG, "wifiap isOpenHotspot 2222222222222222 = " + isOpenHotspot);
        if (isOpenHotspot) {
            hotspot_open_iv.setBackgroundResource(R.mipmap.on);
            hotspot_ll.setVisibility(View.VISIBLE);
            hotspot_save_btn.setVisibility(View.VISIBLE);
        } else {
            hotspot_open_iv.setBackgroundResource(R.mipmap.off);
            hotspot_ll.setVisibility(View.INVISIBLE);
            hotspot_save_btn.setVisibility(View.INVISIBLE);
        }
        setShowPwd();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (hotspot_secure_iv.hasFocus()) {
                    switchSecure(false);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
//                if (activity.getNetworkSettingsViewHolder().isFocuseWifiHotspotTextView()) {
//                    hotspot_open_fl.requestFocus();
//                    return true;
//                } else
                    if (hotspot_secure_iv.hasFocus()) {
                    switchSecure(true);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (isSecureOpenType && hotspot_secure_iv.hasFocus()) {
                    hotspot_save_btn.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (isSecureOpenType && hotspot_save_btn.hasFocus()) {
                    hotspot_secure_iv.requestFocus();
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.hotspot_show_pwd_fl) {
            isShowPwd = !isShowPwd;
            setShowPwd();
        } else if (i == R.id.hotspot_open_fl) {
            isOpenHotspot = !isOpenHotspot;
            setOpenHotspot(true);
        } else if (i == R.id.hotspot_secure_iv) {
            switchSecure(true);
        } else if (i == R.id.hotspot_save_btn) {
            secureString = hotspot_secure_sele_tv.getText().toString();
            if (secureString.equals(ctvContext.getString(R.string.wifi_security_wpa2))) {
                secureType = NetUtils.SECURE_TYPE_WPA2;
            } else if (secureString.equals(ctvContext.getString(R.string.wifi_security_open))) {
                secureType = NetUtils.SECURE_TYPE_OPEN;
            } else {
                secureType = NetUtils.SECURE_TYPE_WPA;
            }
            saveWifiApConfig();
        }
    }

    private void saveWifiApConfig() {
        String passwd = hotspot_pwd_edt.getText().toString().trim();
        if (secureType != NetUtils.SECURE_TYPE_OPEN
                && (TextUtils.isEmpty(passwd) || passwd.length() < 8)) {
            hotspot_pwd_edt.setText("");
            // hotspot_ssid_edt.setHint(R.string.wifiap_pwd_notice);
            hotspot_pwd_edt.requestFocus();
            showToast(R.string.wifiap_pwd_notice);
            return;
        }
        String ssid = hotspot_ssid_edt.getText().toString().trim();
        if (TextUtils.isEmpty(ssid)) {
            hotspot_ssid_edt.setText("");
            hotspot_ssid_edt.setHint(R.string.please_input_ssid);
            showToast(R.string.please_input_ssid);
            return;
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        /* hotspot_ssid_value_tv.setText(ss_id1 + config.SSID); */
        // set secure type
        switch (secureType) {
            case NetUtils.SECURE_TYPE_WPA:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                /*
                 * hotspot_secure_value_tv.setText(wifi_secure1 +
                 * ctvContext.getString(R.string.wifi_security_wpa));
                 */
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.preSharedKey = passwd;
                break;
            case NetUtils.SECURE_TYPE_WPA2:
                /*
                 * hotspot_secure_value_tv.setText(wifi_secure1 +
                 * ctvContext.getString(R.string.wifi_security_wpa2));
                 */
                config.allowedKeyManagement.set(NetUtils.WPA2_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.preSharedKey = passwd;
                break;
            case NetUtils.SECURE_TYPE_OPEN:
                /*
                 * hotspot_secure_value_tv.setText(wifi_secure1 +
                 * ctvContext.getString(R.string.wifi_security_open));
                 */
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            default:
                return;

        }
        WifiManager wifiManager = mListener.getWifiManager();
//        Log.d(TAG, "state=" + wifiManager.getWifiApState());
        try {
            Log.d(TAG, "state=" +NetUtils.getWifiApState(wifiManager));
            if (NetUtils.WIFI_AP_STATE_ENABLED == NetUtils.getWifiApState(wifiManager)){// wifiManager.getWifiApState()) {
                // restart wifiap
                NetUtils.setWifiApEnabled(wifiManager,null, false);
//                wifiManager.setWifiApEnabled(null, false);
                NetUtils.setWifiApEnabled(wifiManager,config, true);
//                wifiManager.setWifiApEnabled(config, true);
            } else {
                NetUtils.setWifiApConfiguration(wifiManager,config);
//                wifiManager.setWifiApConfiguration(config);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // configure successful
        ShowSaveStatusDialog();
    }

    private void switchSecure(boolean isIncrease) {
        if (isIncrease) {
            secureType = (secureType + 1) % 3;
        } else {
            secureType = (secureType + 3 - 1) % 3;
        }
        Log.d(TAG, "mSecureType, " + secureType);
        switch (secureType) {
            case NetUtils.SECURE_TYPE_WPA:
                hotspot_secure_sele_tv.setText(SECURE_TYPE[0]);
                break;
            case NetUtils.SECURE_TYPE_WPA2:
                hotspot_secure_sele_tv.setText(SECURE_TYPE[1]);
                break;
            case NetUtils.SECURE_TYPE_OPEN:
                hotspot_secure_sele_tv.setText(SECURE_TYPE[2]);
                break;
            default:
                break;
        }
        // hide passwd layout
        if (secureType == NetUtils.SECURE_TYPE_OPEN) {
            isSecureOpenType = true;
            hotspot_pwd_fl.setVisibility(View.INVISIBLE);
            hotspot_show_pwd_fl.setVisibility(View.INVISIBLE);
        } else {
            isSecureOpenType = false;
            hotspot_pwd_fl.setVisibility(View.VISIBLE);
            hotspot_show_pwd_fl.setVisibility(View.VISIBLE);
        }
    }

    private boolean hasReady(boolean isClick) {
        MWifiManager mWifiManager = MWifiManager.getInstance();
        // check wifiDevice
        if (!mWifiManager.isWifiDeviceExist()) {
            if (isClick) {
                showToast(R.string.please_insert_dongle);
            }
            Log.d(TAG, "hasReady -- wifiDevice?");
            return false;
        }
        // check is wifiDevice support wifi hotspot.
        if (!mWifiManager.isWifiDeviceSupportSoftap()) {
            if (isClick) {
                showToast(R.string.device_do_not_support);
            }
            Log.d(TAG, "hasReady -- support wifi hotspot?");
            return false;
        }
        // WifiManager wifiManager = mListener.getWifiManager();
        int flag = 0;// binary mark
        if (mListener.isWifiEnabled() || getWifiStatus()) {
            flag = flag + 1;// check whether wifi is open
        }
//        if (!mListener.getPPPoeStatusDescription().equals(
//                activity.getString(R.string.pppoe_disconnected))) {
//            flag = flag + 2; // check WiFi pppoe
//        }
        if (!mListener.isEthernetAvailable()) {
            flag = flag + 4; // check ethernet_connection
        }
        if (flag != 0) {
            Log.d(TAG, "to tipDialog....flag: " + flag);
            if (flag % 2 == 1 && Tools.is638CIBN()) {
                mListener.setWifiEnabled(false);
                return true;
            } else {
                if (isClick) {
                    TipDialog tipDialog = new TipDialog(activity, NetUtils.WIFI_HOTSPOT, flag);
                    tipDialog.show();
                }
                return false;
            }
        }
        return true;
    }

    public void setOpenHotspotUi(boolean isOpenHotspot) {
        Log.d(TAG, "setOpenHotspotUi   222  -setOpenHotspotUi:" + isOpenHotspot);
        this.isOpenHotspot = isOpenHotspot;
        String wifiapband = SystemProperties.get("Wifiapband");
        Log.d(TAG, "wifiap isOpenHotspot = " + isOpenHotspot + " ----- wifiapband : " + wifiapband);
        if(isOpenHotspot && !"".equals(wifiapband) && wifiapband != null && wifiapband.equals("Apband5G")) {
            isOpenHotspot = false;
        }
        if (isOpenHotspot) {
            hotspot_open_iv.setBackgroundResource(R.mipmap.on);
            hotspot_ll.setVisibility(View.VISIBLE);
            hotspot_save_btn.setVisibility(View.VISIBLE);
        } else {
            hotspot_open_iv.setBackgroundResource(R.mipmap.off);
            hotspot_ll.setVisibility(View.INVISIBLE);
            hotspot_save_btn.setVisibility(View.INVISIBLE);
        }
    }

    public void setOpenHotspot(boolean isClik) {
//        int apState = mListener.getWifiManager().getWifiApState();
        try {
            int apState = NetUtils.getWifiApState(mListener.getWifiManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
//                mListener.getWifiManager().getWifiApState();
        Log.i(TAG, "setOpenHotspot  22222222-isOpenHotspot:" + isOpenHotspot);
        /*if (isOpenHotspot) {
        if (!hasReady(isClik)) {
            Log.d(TAG, "wifiap have not ready.");
            isOpenHotspot = false;
            return;
        }
        if (apState == WifiManager.WIFI_AP_STATE_ENABLING
                || apState == WifiManager.WIFI_AP_STATE_ENABLED) {
            isOpenHotspot = true;
            screen_hotspot_open_iv.setBackgroundResource(R.drawable.on);
            screen_hotspot_ll.setVisibility(View.VISIBLE);
            screen_hotspot_save_btn.setVisibility(View.VISIBLE);
            return;
        }
    } else {*/
        /*if (apState == WifiManager.WIFI_AP_STATE_DISABLING
                || apState == WifiManager.WIFI_AP_STATE_DISABLED) {
            isOpenHotspot = false;
            return;
        }*/
    //}
        try {
        if (isClik) {
            if(isOpenHotspot){
                    if(NetUtils.setWifiApEnabled(mListener.getWifiManager(),null, false)){
                        Log.i(TAG, "setOpenHotspot-setWifiApEnabled: false , close Apband5G");
                    }

                    if(NetUtils.setWifiApEnabled(mListener.getWifiManager(),null, false)){
                        Log.i(TAG, "setOpenHotspot-setWifiApEnabled: false , close Apband5G");
                    }

//                if (mListener.getWifiManager().setWifiApEnabled(null, false)) {
//                    Log.i(TAG, "setOpenHotspot-setWifiApEnabled: false , close Apband5G");
//                }
//                if (mListener.getWifiManager().setWifiApEnabled(null, false)) {
//                    Log.i(TAG, "setOpenHotspot-setWifiApEnabled: false , close Apband5G");
//                }
                SystemProperties.set("Wifiapband", "Apband2G");
            }

            if(NetUtils.setWifiApEnabled(mListener.getWifiManager(),null, isOpenHotspot)){
                Log.i(TAG, "setOpenHotspot-setWifiApEnabled: false , close Apband5G");
            }

//            if (mListener.getWifiManager().setWifiApEnabled(null, isOpenHotspot)) {
//                Log.i(TAG, "setOpenHotspot-setWifiApEnabled:" + isOpenHotspot);
//            }
            if(isOpenHotspot){
                ShowSaveStatusDialog();
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isOpenHotspot) {
            hotspot_open_iv.setBackgroundResource(R.mipmap.on);
            hotspot_ll.setVisibility(View.VISIBLE);
            hotspot_save_btn.setVisibility(View.VISIBLE);
        } else {
            hotspot_open_iv.setBackgroundResource(R.mipmap.off);
            hotspot_ll.setVisibility(View.INVISIBLE);
            hotspot_save_btn.setVisibility(View.INVISIBLE);
        }
    }

    public void refreshWifiApUi(boolean isInit, InitDataInfo data) {
        WifiConfiguration config = null;
        if (isInit) {
            config = data.getConfig();
        } else {
            WifiManager wifiManager = mListener.getWifiManager();
//            config = wifiManager.getWifiApConfiguration();
            config = NetUtils.getWifiApConfiguration(wifiManager);
        }
        if (config == null) {
            return;
        } else {
            // show Wi-Fi Ap info
            hotspot_ssid_value_tv.setText(ss_id1 + config.SSID);
            hotspot_ssid_edt.setText(config.SSID);
            if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
                hotspot_secure_value_tv.setText(wifi_secure1
                        + ctvContext.getString(R.string.wifi_security_wpa));
                hotspot_secure_sele_tv.setText(R.string.wifi_security_wpa);
                hotspot_pwd_edt.setText(config.preSharedKey);
                secureType = NetUtils.SECURE_TYPE_WPA;

            } else if (config.allowedKeyManagement.get(NetUtils.WPA2_PSK)) {
                hotspot_secure_value_tv.setText(wifi_secure1
                        + ctvContext.getString(R.string.wifi_security_wpa2));
                hotspot_secure_sele_tv.setText(R.string.wifi_security_wpa2);
                hotspot_pwd_edt.setText(config.preSharedKey);
                secureType = NetUtils.SECURE_TYPE_WPA2;
            } else {
                hotspot_secure_value_tv.setText(wifi_secure1
                        + ctvContext.getString(R.string.wifi_security_open));
                hotspot_secure_sele_tv.setText(R.string.wifi_security_open);
                secureType = NetUtils.SECURE_TYPE_OPEN;
                hotspot_pwd_edt.setText("");
            }
            if (secureType == NetUtils.SECURE_TYPE_OPEN) {
                hotspot_pwd_fl.setVisibility(View.INVISIBLE);
                hotspot_show_pwd_fl.setVisibility(View.INVISIBLE);
                isSecureOpenType = true;
            } else {
                isSecureOpenType = false;
                hotspot_pwd_fl.setVisibility(View.VISIBLE);
                hotspot_show_pwd_fl.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setShowPwd() {
        if (isShowPwd) {
            hotspot_show_pwd_iv.setBackgroundResource(R.mipmap.on);
            hotspot_pwd_edt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            hotspot_show_pwd_iv.setBackgroundResource(R.mipmap.off);
            hotspot_pwd_edt.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int i = view.getId();
        if (i == R.id.hotspot_open_fl) {
            if (has_focus) {
                hotspot_open_fl.setSelected(true);
            } else {
                hotspot_open_fl.setSelected(false);
            }
        } else if (i == R.id.hotspot_ssid_edt) {
            if (has_focus) {
                hotspot_ssid_edt.setSelected(true);
                hotspot_ssid_tv.setSelected(true);
                hotspot_ssid_edt.selectAll();
            } else {
                hotspot_ssid_edt.setSelected(false);
                hotspot_ssid_tv.setSelected(false);
            }
        } else if (i == R.id.hotspot_secure_iv) {
            if (has_focus) {
                hotspot_secure_fl.setSelected(true);
                hotspot_secure_tv.setSelected(true);
                hotspot_secure_sele_tv.setSelected(true);
            } else {
                hotspot_secure_tv.setSelected(false);
                hotspot_secure_fl.setSelected(false);
                hotspot_secure_sele_tv.setSelected(false);
            }
        } else if (i == R.id.hotspot_pwd_edt) {
            if (has_focus) {
                hotspot_pwd_tv.setSelected(true);
                hotspot_pwd_edt.setSelected(true);
                hotspot_pwd_edt.selectAll();
            } else {
                hotspot_pwd_tv.setSelected(false);
                hotspot_pwd_edt.setSelected(false);
            }
        } else if (i == R.id.hotspot_show_pwd_fl) {
            if (has_focus) {
                hotspot_show_pwd_tv.setSelected(true);
                hotspot_show_pwd_fl.setSelected(true);
            } else {
                hotspot_show_pwd_tv.setSelected(false);
                hotspot_show_pwd_fl.setSelected(false);
            }
        } else if (i == R.id.hotspot_save_btn) {
            if (has_focus) {
                // hotspot_save_btn.setSelected(true);
            } else {
                hotspot_show_pwd_tv.setSelected(false);
            }
        }
    }

    private void showToast(int id) {
        Toast.makeText(ctvContext, id, Toast.LENGTH_SHORT).show();
    }

    /**
     * wifi ap state change receiver
     */
    public void upApStateChange(Intent intent) {
        int state = intent.getIntExtra(NetUtils.EXTRA_WIFI_AP_STATE,
                NetUtils.WIFI_AP_STATE_FAILED);
        switch (state) {
            default:
            case NetUtils.WIFI_AP_STATE_ENABLING:
                Log.d(TAG, "WIFI_AP_STATE_ENABLING");
                break;
            case NetUtils.WIFI_AP_STATE_ENABLED:
                Log.d(TAG, "WIFI_AP_STATE_ENABLED --------- 222 isOpenHotspot : " + isOpenHotspot);
                String wifiapband = SystemProperties.get("Wifiapband");
                Log.d(TAG, "wifiap isOpenHotspot = " + isOpenHotspot + " ----- wifiapband : " + wifiapband);
                if(!isOpenHotspot && !"".equals(wifiapband) && wifiapband != null && wifiapband.equals("Apband5G")) {
                    setOpenHotspot(false);
                    refreshWifiApUi(false,null);
                    break;
                }
                if (mDialog != null) {
                    mDialog.cancel();
                }
                showToast(R.string.wifiap_config_success);
                if (!isOpenHotspot) {
                    isOpenHotspot = true;
                    setOpenHotspot(false);
                    refreshWifiApUi(false,null);
                }
                break;
            case NetUtils.WIFI_AP_STATE_DISABLING:
                Log.d(TAG, "WIFI_AP_STATE_DISABLING");
                break;
            case NetUtils.WIFI_AP_STATE_DISABLED:
                Log.d(TAG, "WIFI_AP_STATE_DISABLED");
                if (isOpenHotspot) {
                    isOpenHotspot = false;
                    /*
                     * setOpenHotspot(false); refreshWifiApUi();
                     */
                }
                break;
        }
    }

    private boolean getWifiStatus() {
        SharedPreferences sp = ctvContext.getSharedPreferences("wifi_status", Context.MODE_PRIVATE);
        boolean status = sp.getBoolean("wifi_on", false);
        return status;
    }

    private void ShowSaveStatusDialog() {
        if (mDialog != null) {
            mDialog.cancel();
        }
        mDialog = new ProgressDialog(activity);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setTitle(ctvContext.getString(R.string.tip));
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });
        mDialog.setMessage(ctvContext.getString(R.string.wifi_ap_submitting));
        mDialog.show();
    }
    public void onExit(){
        if(mDialog != null){
            mDialog.cancel();
            mDialog = null;
        }
        wifiHotHandler.removeCallbacksAndMessages(null);
    }
}
