
package com.ctv.settings.network.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.IpConfiguration.IpAssignment;
import android.net.IpConfiguration.ProxySettings;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.network.R;
import com.ctv.settings.network.helper.WifiConfigHelper;
import com.ctv.settings.network.holder.WirelessViewHolder;
import com.ctv.settings.network.utils.NetUtils;

public class WiFiAddDialog extends Dialog implements OnFocusChangeListener, OnClickListener {

    private static final String TAG = "WiFiAddDialog";

    private final Context ctvContext;

    private EditText wifi_add_ssid_edt;

    private EditText wifi_add_pwd_edt;

    private ImageView wifi_add_secure_iv;

    private ImageView wifi_add_show_pwd_iv;

    private TextView wifi_add_secure_sele_tv;

    private TextView wifi_add_ssid_tv;

    private TextView wifi_add_secure_tv;

    private TextView wifi_add_pwd_tv;

    private TextView wifi_add_show_pwd_tv;

    private FrameLayout wifi_add_secure_fl;

    private FrameLayout wifi_add_pwd_fl;

    private FrameLayout wifi_add_show_pwd_fl;

    private Button wifi_add_save_btn;

    private Button wifi_add_cancle_btn;

    // 0: open , 1:WPA/WPA2 PSK , 2:WEP
    private int mSecureType = NetUtils.SECURE_OPEN;

    private boolean isShowPwd;

    private final WifiManager wifiManager;

    private final WirelessViewHolder wirelessViewHolder;
    private final Activity mActivity;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    wirelessViewHolder.wirelessAdapter.updateTheTop(wifi_add_ssid_edt.getText()
                            .toString().trim());
                    wirelessViewHolder.wirelessAdapter.setConnectState(2);
                    break;
            }
        }
    };

    public WiFiAddDialog(Context context, WirelessViewHolder wirelessViewHolder) {
        super(context);
        this.ctvContext = context.getApplicationContext();
        this.mActivity = (Activity)context;
        this.wirelessViewHolder = wirelessViewHolder;
        setWindowStyle();
        wifiManager = (WifiManager) ctvContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
        wifi_add_pwd_edt = null;
        wifi_add_secure_iv = null;
        wifi_add_show_pwd_iv = null;
        wifi_add_secure_sele_tv = null;
        wifi_add_ssid_tv = null;
        wifi_add_secure_tv = null;
        wifi_add_pwd_tv = null;
        wifi_add_show_pwd_tv = null;
        wifi_add_secure_fl = null;
        wifi_add_pwd_fl = null;
        wifi_add_show_pwd_fl = null;
        wifi_add_save_btn = null;
        wifi_add_cancle_btn = null;
        mSecureType = 0;
        isShowPwd = false;
        mHandler.removeCallbacksAndMessages(null);
        textWatcher = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wifi_add);
        findViews();
        setPwdVisibility();
    }

    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = ctvContext.getResources();
        Drawable drab = res.getDrawable(R.drawable.button_save_shape);
        w.setBackgroundDrawable(drab);
        w.setDimAmount(0.0f);
//        WindowManager.LayoutParams lp = w.getAttributes();
//        final float scale = res.getDisplayMetrics().density;
//        // In the mid-point to calculate the offset x and y
//        lp.x = (int) (82.7 * scale + 0.5f);
//        lp.y = (int) (-36 * scale + 0.5f);
//        lp.width = (int) (473.3 * scale + 0.5f);
//        lp.height = (int) (309.3 * scale + 0.5f);
//        w.setAttributes(lp);
    }

    /**
     * init compontent.
     */
    private void findViews() {
        wifi_add_ssid_edt = (EditText) findViewById(R.id.wifi_add_ssid_edt);
        wifi_add_pwd_edt = (EditText)findViewById(R.id.wifi_add_pwd_edt);
        wifi_add_secure_sele_tv = (TextView) findViewById(R.id.wifi_add_secure_sele_tv);
        wifi_add_show_pwd_tv = (TextView) findViewById(R.id.wifi_add_show_pwd_tv);
        wifi_add_secure_tv = (TextView) findViewById(R.id.wifi_add_secure_tv);
        wifi_add_ssid_tv = (TextView) findViewById(R.id.wifi_add_ssid_tv);
        wifi_add_pwd_tv = (TextView) findViewById(R.id.wifi_add_pwd_tv);
        wifi_add_show_pwd_iv = (ImageView) findViewById(R.id.wifi_add_show_pwd_iv);
        wifi_add_secure_iv = (ImageView) findViewById(R.id.wifi_add_secure_iv);
        wifi_add_show_pwd_fl = (FrameLayout) findViewById(R.id.wifi_add_show_pwd_fl);
        wifi_add_secure_fl = (FrameLayout) findViewById(R.id.wifi_add_secure_fl);
        wifi_add_pwd_fl = (FrameLayout) findViewById(R.id.wifi_add_pwd_fl);
        wifi_add_cancle_btn = (Button) findViewById(R.id.wifi_add_cancle_btn);
        wifi_add_save_btn = (Button) findViewById(R.id.wifi_add_save_btn);

        wifi_add_pwd_edt.setOnFocusChangeListener(this);
        wifi_add_save_btn.setOnFocusChangeListener(this);
        wifi_add_ssid_edt.setOnFocusChangeListener(this);
        wifi_add_secure_iv.setOnFocusChangeListener(this);
        wifi_add_cancle_btn.setOnFocusChangeListener(this);
        wifi_add_show_pwd_fl.setOnFocusChangeListener(this);

        wifi_add_save_btn.setOnClickListener(this);
        wifi_add_secure_iv.setOnClickListener(this);
        wifi_add_cancle_btn.setOnClickListener(this);
        wifi_add_show_pwd_fl.setOnClickListener(this);
        wifi_add_ssid_edt.selectAll();
        mSecureType = NetUtils.SECURE_OPEN;
        isShowPwd = false;
        setShowPwd();
        wifi_add_pwd_edt.addTextChangedListener(textWatcher);
        wifi_add_ssid_edt.requestFocus();
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.wifi_add_show_pwd_fl) {
            isShowPwd = !isShowPwd;
            setShowPwd();
        } else if (i == R.id.wifi_add_save_btn) {
            saveDate();
            wirelessViewHolder.isDialog = false;
            dismiss();
        } else if (i == R.id.wifi_add_cancle_btn) {
            wirelessViewHolder.isDialog = false;
            dismiss();
        } else if (i == R.id.wifi_add_secure_iv) {
            switchSecure(true);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (wifi_add_secure_iv.hasFocus()) {
                    switchSecure(false);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (wifi_add_secure_iv.hasFocus()) {
                    switchSecure(true);
                    return true;
                }
                break;
            default:
                break;
            case KeyEvent.KEYCODE_BACK:
                wirelessViewHolder.isDialog = false;
                dismiss();
                return true;

        }
        return false;
    }

    private void saveDate() {
        String ssid = wifi_add_ssid_edt.getText().toString().trim();
        if (TextUtils.isEmpty(ssid)) {
            wifi_add_ssid_edt.requestFocus();
            showToast(R.string.ssid_password_error);
            return;
        }
        WifiConfiguration config = getAddConfig(ssid);
        Log.i(TAG, "---config-" + config);
        // LinkProperties linkProperties = new LinkProperties();
        try {
            NetUtils.setProxySettings(config,ProxySettings.NONE);
            NetUtils.setIpAssignment(config,IpAssignment.DHCP);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        config.setProxySettings(ProxySettings.NONE);
//        config.setIpAssignment(IpAssignment.DHCP);
        WifiConfigHelper.saveConfiguration(ctvContext, config);
        // init linkProperties
        // config.linkProperties = new LinkProperties(linkProperties);
//        wifiManager.connect(config, mConnectListener);
        try {
            NetUtils.connect(wifiManager,config);
        } catch (Exception e) {
            e.printStackTrace();
        }
        wirelessViewHolder.initAssociatState(config);
    }

    private WifiConfiguration getAddConfig(String ssid) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        config.hiddenSSID = true;
        String passwd = wifi_add_pwd_edt.getText().toString().trim();
        Log.i(TAG, "--getAddConfig-mSecureType-" + mSecureType);
        switch (mSecureType) {
            case NetUtils.SECURE_OPEN:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            case NetUtils.SECURE_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (passwd.length() != 0) {
                    int length = passwd.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58)
                            && passwd.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = passwd;
                    } else {
                        config.wepKeys[0] = '"' + passwd + '"';
                    }
                }
                break;
            case NetUtils.SECURE_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (passwd.length() != 0) {
                    if (passwd.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = passwd;
                    } else {
                        config.preSharedKey = '"' + passwd + '"';
                    }
                }
                break;
            default:
                break;
        }
        return config;
    }

    private void switchSecure(boolean isRight) {
        if (isRight) {
            mSecureType = (mSecureType + 1) % 3;
        } else {
            mSecureType = (mSecureType + 3 - 1) % 3;
        }
        Log.i(TAG, "---mSecureType-" + mSecureType);
        setPwdVisibility();
    }

    private void setPwdVisibility() {
        if (mSecureType == NetUtils.SECURE_OPEN) {
            wifi_add_save_btn.setVisibility(View.VISIBLE);
            wifi_add_show_pwd_fl.setVisibility(View.GONE);
            wifi_add_pwd_fl.setVisibility(View.GONE);
            wifi_add_secure_sele_tv.setText(R.string.wifi_security_open);
        } else {
            if (mSecureType == NetUtils.SECURE_WEP) {
                wifi_add_secure_sele_tv.setText(R.string.wifi_security_wep);
            } else {
                wifi_add_secure_sele_tv.setText(R.string.wifi_security_wp);
            }
            wifi_add_save_btn.setVisibility(View.INVISIBLE);
            wifi_add_show_pwd_fl.setVisibility(View.VISIBLE);
            wifi_add_pwd_fl.setVisibility(View.VISIBLE);
        }
    }

    private void setShowPwd() {
        if (isShowPwd) {
            wifi_add_show_pwd_iv.setBackgroundResource(R.mipmap.on);
            wifi_add_pwd_edt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            wifi_add_show_pwd_iv.setBackgroundResource(R.mipmap.off);
            wifi_add_pwd_edt.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int i = view.getId();
        if (i == R.id.wifi_add_ssid_edt) {
            if (has_focus) {
                wifi_add_ssid_tv.setSelected(true);
                wifi_add_ssid_edt.setSelected(true);
                wifi_add_ssid_edt.selectAll();
            } else {
                wifi_add_ssid_edt.setSelected(false);
                wifi_add_ssid_tv.setSelected(false);
            }
        } else if (i == R.id.wifi_add_pwd_edt) {
            if (has_focus) {
                wifi_add_pwd_tv.setSelected(true);
                wifi_add_pwd_edt.setSelected(true);
                wifi_add_pwd_edt.selectAll();
            } else {
                wifi_add_pwd_tv.setSelected(false);
                wifi_add_pwd_edt.setSelected(false);
            }
        } else if (i == R.id.wifi_add_secure_iv) {
            if (has_focus) {
                wifi_add_secure_tv.setSelected(true);
                wifi_add_secure_fl.setSelected(true);
                wifi_add_secure_sele_tv.setSelected(true);
            } else {
                wifi_add_secure_tv.setSelected(false);
                wifi_add_secure_fl.setSelected(false);
                wifi_add_secure_sele_tv.setSelected(false);
            }
        } else if (i == R.id.wifi_add_show_pwd_fl) {
            if (has_focus) {
                wifi_add_show_pwd_tv.setSelected(true);
                wifi_add_show_pwd_fl.setSelected(true);
            } else {
                wifi_add_show_pwd_tv.setSelected(false);
                wifi_add_show_pwd_fl.setSelected(false);
            }
        } else if (i == R.id.wifi_add_save_btn) {
        } else if (i == R.id.wifi_add_cancle_btn) {
            if (has_focus) {
                wifi_add_cancle_btn.setSelected(true);
            } else {
                wifi_add_cancle_btn.setSelected(false);
            }
        }
    }

    private void showToast(int id) {
        if (id <= 0) {
            return;
        }
        Toast.makeText(ctvContext, id, Toast.LENGTH_SHORT).show();
    }

    public static boolean saveConfiguration(Context context, WifiConfiguration config) {
        if (config == null) {
            return false;
        }

        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int networkId = wifiMan.addNetwork(config);
        if (networkId == -1) {
            Log.e(TAG, "failed to add network: " + config.toString());
            return false;
        }

        if (!wifiMan.enableNetwork(networkId, true)) {
            Log.e(TAG, "enable network failed: " + networkId + "; " + config.toString());
            return false;
        }

        if (!wifiMan.saveConfiguration()) {
            Log.e(TAG, "failed to save: " + config.toString());
            return false;
        }
        Log.d(TAG, "saved network: " + config.toString());
        return true;
    }

//    private final WifiManager.ActionListener mConnectListener = new WifiManager.ActionListener() {
//        @Override
//        public void onSuccess() {
//            Log.i(TAG, "--mConnectListener-onSuccess-");
//            mHandler.sendEmptyMessage(1);
//        }
//
//        @Override
//        public void onFailure(int reason) {
//            showToast(R.string.wifi_failed_connect_message);
//        }
//    };

    TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (mSecureType != NetUtils.SECURE_OPEN) {
                if (wifi_add_pwd_edt.getText().length() >= 8) {
                    wifi_add_save_btn.setVisibility(View.VISIBLE);
                } else {
                    wifi_add_save_btn.setVisibility(View.INVISIBLE);
                }
            }
        }
    };
}
