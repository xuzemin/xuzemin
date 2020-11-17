
package com.ctv.settings.network.holder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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

import com.ctv.settings.R;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.activity.NetWorkActivity;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.Tools;


/**
 * Wifi模块
 *
 * @author xuzemin
 * @date 2019/09/19
 */
public class WifiHotspotViewHolder implements OnFocusChangeListener, OnClickListener {

    private static final String TAG = "WifiHotspotViewHolder";

    private static final int[] SECURE_TYPE = {
            R.string.wifi_security_open, R.string.wifi_security_wpa2,R.string.wifi_security_wpa
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

    // 热点类型
    private FrameLayout hotspot_type_fl;
    private ImageView hotspot_type_iv;
    private TextView hotspot_type_tv;
    private TextView hotspot_type_sele_tv;

    private TextView hotspot_open_tv;
    private TextView back_title;

    private boolean isShowPwd = false;

    public boolean isOpenHotspot = false;

    private boolean isSecureOpenType = false;

    private int secureType = NetUtils.SECURE_TYPE_WPA2;

    private String secureString;

    private String ss_id1, wifi_secure1;

    public NotificationManager notificationManager;

    private NetWorkActivity activity;

    private final ConnectivityListener mListener;

    private LinearLayout back_hotspot;

    private ProgressDialog mDialog = null;

    private EthernetManager mEthernetManager;

    private WifiManager mWifiManager;

    private ConnectivityManager mCm;
    private OnStartTetheringCallback mStartTetheringCallback;

    //安全性
    public static final int OPEN_INDEX = 0;
    public static final int WPA2_INDEX = 1;
    public static final int WPA_INDEX = 2;
    public static boolean IS_AP_5G = false;// 热点:默认2.4G

    @SuppressLint("HandlerLeak")
    private final Handler wifiHotHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "wifiHotHandler setOpenHotspot");
            isOpenHotspot = true;
            hotspot_open_iv.setBackgroundResource(R.mipmap.on);
            hotspot_ll.setVisibility(View.VISIBLE);
            hotspot_save_btn.setVisibility(View.VISIBLE);
            hotspot_type_fl.setVisibility(View.GONE);
            ShowSaveStatusDialog();
        }
    };
    public boolean mRestartWifiApAfterConfigChange;

    public WifiHotspotViewHolder(Activity ctvContext, ConnectivityListener conListener) {
        super();
        this.ctvContext = ctvContext.getApplicationContext();
        this.mListener = conListener;
        activity = (NetWorkActivity) ctvContext;
        initWifiManager();
        initView();
        initData();
        Log.i(TAG, "----last-isOpenHotspot:" + isOpenHotspot);
    }

    private void initWifiManager() {
        mWifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mCm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

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
        back_hotspot = (LinearLayout) activity.findViewById(R.id.back_hotspot);

        hotspot_type_fl = (FrameLayout) activity.findViewById(R.id.hotspot_type_fl);
        hotspot_type_iv = (ImageView) activity.findViewById(R.id.hotspot_type_iv);
        hotspot_type_tv = (TextView) activity.findViewById(R.id.hotspot_type_tv);
        hotspot_type_sele_tv = (TextView) activity.findViewById(R.id.hotspot_type_sele_tv);

        hotspot_open_tv = (TextView) activity.findViewById(R.id.hotspot_open_tv);

        back_title = (TextView) activity.findViewById(R.id.back_title);

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
        back_hotspot.setOnClickListener(this);

        hotspot_type_fl.setOnFocusChangeListener(this);
        hotspot_type_iv.setOnFocusChangeListener(this);
        hotspot_type_iv.setOnClickListener(this);

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
        // 热点类型
        IS_AP_5G = NetUtils.isHotspot5G();

        // 热点开关
        try {
            isOpenHotspot = NetUtils.isWifiApEnabled(mListener.getWifiManager());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "wifiap isOpenHotspot = " + isOpenHotspot + " ----- IS_AP_5G : " + IS_AP_5G);

        setOpenHotspot(false);
        Log.d(TAG, "wifiap isOpenHotspot 2222222222222222 = " + isOpenHotspot);

        updateHotpotTypeUI();

        if (isOpenHotspot) {
            hotspot_open_iv.setBackgroundResource(R.mipmap.on);
            hotspot_ll.setVisibility(View.VISIBLE);
            hotspot_save_btn.setVisibility(View.VISIBLE);

            hotspot_type_fl.setVisibility(View.GONE);
        } else {
            hotspot_open_iv.setBackgroundResource(R.mipmap.off);
            hotspot_ll.setVisibility(View.INVISIBLE);
            hotspot_save_btn.setVisibility(View.INVISIBLE);

            hotspot_type_fl.setVisibility(View.VISIBLE);
        }
        setShowPwd();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (hotspot_secure_iv.hasFocus()) {
                    if(secureType == NetUtils.SECURE_TYPE_OPEN){
                        secureType = NetUtils.SECURE_TYPE_WPA;
                    }else{
                        secureType = secureType -1;
                    }
                    switchSecure(secureType);
                    return true;
                }else if (hotspot_type_fl.hasFocus()) {
                    IS_AP_5G = !IS_AP_5G;
                    NetUtils.setHotspotType(IS_AP_5G);
                    IS_AP_5G = NetUtils.isHotspot5G();
                    updateHotpotTypeUI();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (hotspot_secure_iv.hasFocus()) {
                    if(secureType == NetUtils.SECURE_TYPE_WPA){
                        secureType = NetUtils.SECURE_TYPE_OPEN;
                    }else{
                        secureType = secureType +1;
                    }
                    switchSecure(secureType);
                    return true;
                }else if (hotspot_type_fl.hasFocus()) {
                    IS_AP_5G = !IS_AP_5G;
                    NetUtils.setHotspotType(IS_AP_5G);
                    IS_AP_5G = NetUtils.isHotspot5G();
                    updateHotpotTypeUI();
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

    /**
     * 更新热点类型UI
     */
    private void updateHotpotTypeUI(){
        int hotpotRes = IS_AP_5G ? R.string.hot_spot_type_two : R.string.hot_spot_type_one;
        back_title.setText(ctvContext.getResources().getText(hotpotRes));
        hotspot_type_sele_tv.setText(ctvContext.getResources().getText(hotpotRes));
        hotspot_open_tv.setText(ctvContext.getResources().getText(hotpotRes));
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
            if(secureType == NetUtils.SECURE_TYPE_WPA){
                secureType = NetUtils.SECURE_TYPE_OPEN;
            }else{
                secureType = secureType +1;
            }
            switchSecure(secureType);
        } else if (i == R.id.hotspot_type_iv) {
            // 切换热点类型
            IS_AP_5G = !IS_AP_5G;
            NetUtils.setHotspotType(IS_AP_5G);

            IS_AP_5G = NetUtils.isHotspot5G();
            updateHotpotTypeUI();
        } else if (i == R.id.hotspot_save_btn) {
            secureString = hotspot_secure_sele_tv.getText().toString();
            if (secureString.equals(ctvContext.getString(R.string.wifi_security_wpa2))) {
                secureType = NetUtils.SECURE_TYPE_WPA2;
            } else if (secureString.equals(ctvContext.getString(R.string.wifi_security_open))) {
                secureType = NetUtils.SECURE_TYPE_OPEN;
            } else {
                secureType = NetUtils.SECURE_TYPE_WPA;
            }
            saveWifiApConfig(secureType);
        } else if (i == R.id.back_hotspot) {
            activity.finish();
        }
    }

    private void saveWifiApConfig(int secureType) {

        WifiConfiguration mWifiConfig = getConfig(secureType);
        if (mWifiConfig != null) {
            /**
             * if soft AP is stopped, bring up
             * else restart with new config
             * TODO: update config on a running access point when framework support is added
             * qkmin 先关闭，收到广播ConnectivityManager.ACTION_TETHER_STATE_CHANGED  调用 setSoftapEnabled后启动
             */
            if (mWifiManager.getWifiApState() == WifiManager.WIFI_AP_STATE_ENABLED) {
                // MStar Android Patch Begin
                Log.d("TetheringSettings",
                        "Wifi AP config changed while enabled, stop and restart");
                mRestartWifiApAfterConfigChange = true;
                setSoftapEnabled(activity, false);
            }
            mWifiManager.setWifiApConfiguration(mWifiConfig);
            // MStar Android Patch End
//            int index = WifiApDialog.getSecurityTypeIndex(mWifiConfig);
//            mCreateNetwork.setSummary(String.format(getActivity().getString(CONFIG_SUBTEXT),
//                    mWifiConfig.SSID,
//                    mSecurityType[index]));
        } else {
            return;
        }
        ShowSaveStatusDialog();
    }

    private void switchSecure(int secureType) {
//        if (isIncrease) {
//            secureType = (secureType + 1) % 2;
//        } else {
//            secureType = (secureType + 2 - 1) % 2;
//        }
        Log.d(TAG, "mSecureType, " + secureType);
        switch (secureType) {
            case NetUtils.SECURE_TYPE_WPA2:
                hotspot_secure_sele_tv.setText(SECURE_TYPE[1]);
                break;
            case NetUtils.SECURE_TYPE_OPEN:
                hotspot_secure_sele_tv.setText(SECURE_TYPE[0]);
                break;
            case NetUtils.SECURE_TYPE_WPA:
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

    public void setOpenHotspot(boolean isClik) {
        Log.i(TAG, "setOpenHotspot  22222222-isOpenHotspot:" + isOpenHotspot);
        try {
            if (isClik) {
                if (isOpenHotspot) {
                    //打开热点

                    setSoftapEnabled(activity, false);
                    setSoftapEnabled(activity, true);
//                    setSoftapEnabled(activity, true);
                } else {
                    //关闭热点
                    setSoftapEnabled(activity, false);
                }
                if (isOpenHotspot) {
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

            hotspot_type_fl.setVisibility(View.GONE);
        } else {
            hotspot_open_iv.setBackgroundResource(R.mipmap.off);
            hotspot_ll.setVisibility(View.INVISIBLE);
            hotspot_save_btn.setVisibility(View.INVISIBLE);

            hotspot_type_fl.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    public void refreshWifiApUi(boolean isInit, InitDataInfo data) {
        mStartTetheringCallback = new OnStartTetheringCallback();

        WifiConfiguration config = null;
        if (isInit) {
            config = data.getConfig();
        } else {
            WifiManager wifiManager = mListener.getWifiManager();
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
            if (!has_focus) {
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
//                String wifiapband = SystemProperties.get("Wifiapband");
//                Log.d(TAG, "wifiap isOpenHotspot = " + isOpenHotspot + " ----- wifiapband : " + wifiapband);
//                if (!isOpenHotspot && !"".equals(wifiapband) && wifiapband != null && wifiapband.equals("Apband5G")) {
//                    setOpenHotspot(false);
//                    refreshWifiApUi(false, null);
//                    break;
//                }

                if (mDialog != null) {
                    mDialog.cancel();
                }
//                showToast(R.string.wifiap_config_success);
                if (!isOpenHotspot) {
                    isOpenHotspot = true;
                    setOpenHotspot(false);
                    refreshWifiApUi(false, null);
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

    public void onExit() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
        wifiHotHandler.removeCallbacksAndMessages(null);
        mStartTetheringCallback = null;

    }


    /**
     * ap 打开和关闭
     *
     * @param mContext
     * @param enable
     */
    public void setSoftapEnabled(Context mContext, boolean enable) {
        final ContentResolver cr = mContext.getContentResolver();
        /**
         * Disable Wifi if enabling tethering
         */
        if(!Tools.isDoubleWifi()) {
            int wifiState = mWifiManager.getWifiState();
            if (enable && ((wifiState == WifiManager.WIFI_STATE_ENABLING) ||
                    (wifiState == WifiManager.WIFI_STATE_ENABLED))) {
                mWifiManager.setWifiEnabled(false);
                Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 1);
            }
        }

        if (enable) {
            // 设置热点类型
            NetUtils.setHotspotType(IS_AP_5G);
            mCm.startTethering(ConnectivityManager.TETHERING_WIFI, true, mStartTetheringCallback, wifiHotHandler);
        } else {
            mCm.stopTethering(ConnectivityManager.TETHERING_WIFI);
        }

        /**
         *  If needed, restore Wifi on tether disable
         */
        if(!Tools.isDoubleWifi()) {
            if (!enable) {
                int wifiSavedState = 0;
                try {
                    wifiSavedState = Settings.Global.getInt(cr, Settings.Global.WIFI_SAVED_STATE);
                } catch (Settings.SettingNotFoundException ignored) {
                    ;
                }
                if (wifiSavedState == 1) {
                    mWifiManager.setWifiEnabled(true);
                    Settings.Global.putInt(cr, Settings.Global.WIFI_SAVED_STATE, 0);
                }
            }
        }
    }


    private static final class OnStartTetheringCallback extends
            ConnectivityManager.OnStartTetheringCallback {

        OnStartTetheringCallback() {
        }

        @Override
        public void onTetheringStarted() {
        }

        @Override
        public void onTetheringFailed() {
        }

    }


    public WifiConfiguration getConfig(int mSecurityTypeIndex) {

        WifiConfiguration config = new WifiConfiguration();

        /**
         * TODO: SSID in WifiConfiguration for soft ap
         * is being stored as a raw string without quotes.
         * This is not the case on the client side. We need to
         * make things consistent and clean it up
         */

        String ssid = hotspot_ssid_edt.getText().toString().trim();
        if (TextUtils.isEmpty(ssid)) {
            hotspot_ssid_edt.setText("");
            hotspot_ssid_edt.setHint(R.string.please_input_ssid);
            showToast(R.string.please_input_ssid);
            return null;
        }
        config.SSID = ssid;
        String passwd = null;
        switch (mSecurityTypeIndex) {
            case OPEN_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                return config;

            case WPA2_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                passwd = hotspot_pwd_edt.getText().toString().trim();
                if (secureType != NetUtils.SECURE_TYPE_OPEN
                        && (TextUtils.isEmpty(passwd) || passwd.length() < 8)) {
                    hotspot_pwd_edt.setText("");
                    hotspot_pwd_edt.requestFocus();
                    showToast(R.string.wifiap_pwd_notice);
                    return null;
                } else {
                    String password = passwd;
                    config.preSharedKey = password;
                }
                return config;
            case WPA_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                passwd = hotspot_pwd_edt.getText().toString().trim();
                if (secureType != NetUtils.SECURE_TYPE_OPEN
                        && (TextUtils.isEmpty(passwd) || passwd.length() < 8)) {
                    hotspot_pwd_edt.setText("");
                    hotspot_pwd_edt.requestFocus();
                    showToast(R.string.wifiap_pwd_notice);
                    return null;
                } else {
                    String password = passwd;
                    config.preSharedKey = password;
                }
                return config;
        }
        return null;
    }


}
