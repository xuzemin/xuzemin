
package com.ctv.settings.network.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.network.Listener.ConnectivityListener;
import com.ctv.settings.network.activity.NetWorkActivity;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.Tools;

/**
 * pppoe ViewHolder
 *
 * @author xuzemin
 * @date 2019/09/17
 */
public class PppoeViewHolder implements OnFocusChangeListener, OnClickListener {
    private static final String TAG = "PppoeViewHolder";

    public static final int PPPOE_DIAL = 2;

    private final Context ctvContext;

    private FrameLayout pppoe_show_pwd_fl;

    private FrameLayout pppoe_auto_dialer_fl;

    private EditText pppoe_username_edt;

    private EditText pppoe_pwd_edt;

    private TextView pppoe_pwd_tv;

    private TextView pppoe_username_tv;

    private TextView pppoe_auto_dialer_tv;

    private TextView pppoe_show_pwd_tv;

    private ImageView pppoe_show_pwd_iv;

    private ImageView pppoe_auto_dialer_iv;

    private TextView pppoe_net_state_btn;

    private Button pppoe_dialer_hangup;

    private Button pppoe_dialer_ok;

    private final NetWorkActivity activity;

    private boolean isShowPwd = false;

    private boolean isAutoDialer = false;

    private final ConnectivityListener mListener;

    private EthernetManager mEthernetManager;

    private final WifiManager mWifiManager;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PPPOE_DIAL:
                    dial();
                    break;
                default:
                    break;
            }
        }
    };

    public PppoeViewHolder(Context ctvContext, ConnectivityListener conListener) {
        this.ctvContext = ctvContext.getApplicationContext();
        this.mListener = conListener;
        activity = (NetWorkActivity) ctvContext;
        initView();
        mWifiManager = (WifiManager) ctvContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private void initView() {
        pppoe_show_pwd_fl = (FrameLayout) activity.findViewById(R.id.pppoe_show_pwd_fl);
        pppoe_auto_dialer_fl = (FrameLayout)activity.findViewById(R.id.pppoe_auto_dialer_fl);
        pppoe_username_edt = (EditText) activity.findViewById(R.id.pppoe_username_edt);
        pppoe_pwd_edt = (EditText) activity.findViewById(R.id.pppoe_pwd_edt);
        pppoe_username_tv = (TextView) activity.findViewById(R.id.pppoe_username_tv);
        pppoe_pwd_tv = (TextView) activity.findViewById(R.id.pppoe_pwd_tv);
        pppoe_auto_dialer_tv = (TextView)activity.findViewById(R.id.pppoe_auto_dialer_tv);
        pppoe_show_pwd_tv = (TextView)activity.findViewById(R.id.pppoe_show_pwd_tv);
        pppoe_show_pwd_iv = (ImageView) activity.findViewById(R.id.pppoe_show_pwd_iv);
        pppoe_auto_dialer_iv = (ImageView) activity.findViewById(R.id.pppoe_auto_dialer_iv);
        pppoe_net_state_btn = (TextView) activity.findViewById(R.id.pppoe_net_state_btn);
        pppoe_dialer_hangup =(Button) activity.findViewById(R.id.pppoe_dialer_hangup);
        pppoe_dialer_ok = (Button) activity.findViewById(R.id.pppoe_dialer_ok);
        pppoe_show_pwd_fl.setOnFocusChangeListener(this);
        pppoe_auto_dialer_fl.setOnFocusChangeListener(this);
        pppoe_pwd_edt.setOnFocusChangeListener(this);
        pppoe_username_edt.setOnFocusChangeListener(this);
        pppoe_dialer_hangup.setOnFocusChangeListener(this);
        pppoe_dialer_ok.setOnFocusChangeListener(this);
        pppoe_show_pwd_fl.setOnClickListener(this);
        pppoe_auto_dialer_fl.setOnClickListener(this);
        pppoe_dialer_hangup.setOnClickListener(this);
        pppoe_dialer_ok.setOnClickListener(this);
        activity.findViewById(R.id.back_pppoe).setOnClickListener(this);
        isShowPwd = false;
    }

    /**
     * initData(The function of the method)
     * 
     * @Title: initData
     */
    public void initData(InitDataInfo data) {
        isAutoDialer = data.isAutoDialer();
        String user = mListener.getPPPoEDialer().getUser();
        String passwd = mListener.getPPPoEDialer().getPasswd();
        Log.d(TAG, "=============PPPOE status:" + mListener.getPPPoeStatusDescription());
        if (TextUtils.isEmpty(user) || TextUtils.isEmpty(passwd)) {
            pppoe_username_edt.setHint(R.string.input_username);
            pppoe_pwd_edt.setHint(R.string.input_password);
        } else {
            pppoe_username_edt.setText(user);
            pppoe_pwd_edt.setText(passwd);
        }
        updatePppoeData(mListener.getPPPoeStatusDescription());
        setShowPwd();
        setAutoDialer(isAutoDialer);
    }

    public void updatePppoeData(String status) {
        if(status == null){
            return;
        }
        if (status.equals(ctvContext.getString(R.string.pppoe_connected))) {
            Log.d(TAG, "@pppoe_connect");
            pppoe_net_state_btn.setText(R.string.pppoe_connected);
//            pppoe_net_state_btn.setBackgroundResource(R.drawable.shape_net_state_on);

            pppoe_dialer_ok.setVisibility(View.INVISIBLE);
            pppoe_username_edt.setEnabled(false);
            pppoe_pwd_edt.setEnabled(false);

        } else if (status.equals(ctvContext.getString(R.string.pppoe_disconnected))) {
            Log.d(TAG, "@pppoe_disconnect");
            pppoe_net_state_btn.setText(R.string.pppoe_disconnected);
//            pppoe_net_state_btn.setBackgroundResource(R.drawable.shape_net_state_off);

            pppoe_dialer_ok.setVisibility(View.VISIBLE);
            pppoe_username_edt.setEnabled(true);
            pppoe_pwd_edt.setEnabled(true);

        } else if (status.equals(ctvContext.getString(R.string.pppoe_dialing))) {
            Log.d(TAG, "@pppoe_dialing");
            pppoe_net_state_btn.setText(R.string.pppoe_dialing);
//            pppoe_net_state_btn.setBackgroundResource(R.drawable.shape_net_state_off);

        } else if (status.equals(ctvContext.getString(R.string.pppoe_authfailed))) {
            pppoe_net_state_btn.setText(R.string.pppoe_authfailed);
//            pppoe_net_state_btn.setBackgroundResource(R.drawable.shape_net_state_off);
            pppoe_dialer_ok.setVisibility(View.VISIBLE);
            pppoe_username_edt.setEnabled(true);
            pppoe_pwd_edt.setEnabled(true);
        } else if (status.equals(ctvContext.getString(R.string.pppoe_failed))) {
            pppoe_net_state_btn.setText(R.string.pppoe_failed);
//            pppoe_net_state_btn.setBackgroundResource(R.drawable.shape_net_state_off);
            pppoe_dialer_ok.setVisibility(View.VISIBLE);
            pppoe_username_edt.setEnabled(true);
            pppoe_pwd_edt.setEnabled(true);

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.pppoe_show_pwd_fl) {
            isShowPwd = !isShowPwd;
            setShowPwd();
        } else if (i == R.id.pppoe_auto_dialer_fl) {
            isAutoDialer = !isAutoDialer;
            setAutoDialer(isAutoDialer);
        } else if (i == R.id.pppoe_dialer_hangup) {
            hangupPppoe();
        } else if (i == R.id.pppoe_dialer_ok) {
            dial();
        }else if (i == R.id.back_pppoe) {
            activity.finish();
        }
    }

    public void hangupPppoe() {
        mListener.getPPPoEDialer().hangup();
    }

    private void setAutoDialer(boolean isClick) {
        // TODO Auto-generated method stub
        if (isAutoDialer) {
            pppoe_auto_dialer_iv.setBackgroundResource(R.mipmap.on);
        } else {
            pppoe_auto_dialer_iv.setBackgroundResource(R.mipmap.off);
        }
        //if (isClick) {
        Tools.setBooleanPref(ctvContext, NetUtils.PPPOE_IS_AUTO_DIALER, false);
        //}
    }

    private void setShowPwd() {
        if (isShowPwd) {
            pppoe_show_pwd_iv.setBackgroundResource(R.mipmap.on);
            pppoe_pwd_edt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            pppoe_show_pwd_iv.setBackgroundResource(R.mipmap.off);
            pppoe_pwd_edt.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }

    private void dial() {
        if (mListener.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
        if (!mListener.isEthernetAvailable()) {
            getEthernetManager().setEnabled(true);
        }
        if (mListener.getPPPoeStatusDescription()
                .equals(ctvContext.getString(R.string.pppoe_dialing))) {
            Log.d(TAG, "CONNECTING.....");
            return;
        }
        // check username and password
        String user = pppoe_username_edt.getText().toString().trim();
        String passwd = pppoe_pwd_edt.getText().toString().trim();
        if (TextUtils.isEmpty(user)) {
            Toast.makeText(ctvContext, R.string.input_username, Toast.LENGTH_LONG).show();
            return;
        } else if (TextUtils.isEmpty(passwd)) {
            Toast.makeText(ctvContext, R.string.input_password, Toast.LENGTH_LONG).show();
            return;
        } else {
            String ifName = null;
            Log.d(TAG, "---------->userName:" + user + ";pwd:" + passwd + ";ifName:" + ifName);
            mListener.getPPPoEDialer().dial(user, passwd);
            pppoe_net_state_btn.setText(R.string.is_dialing);
//            pppoe_net_state_btn.setBackgroundResource(R.drawable.shape_net_state_off);

        }
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int i = view.getId();
        if (i == R.id.pppoe_show_pwd_fl) {
            if (has_focus) {
                pppoe_show_pwd_fl.setSelected(true);
                pppoe_show_pwd_tv.setSelected(true);
            } else {
                pppoe_show_pwd_fl.setSelected(false);
                pppoe_show_pwd_tv.setSelected(false);
            }
        } else if (i == R.id.pppoe_auto_dialer_fl) {
            if (has_focus) {
                pppoe_auto_dialer_fl.setSelected(true);
                pppoe_auto_dialer_tv.setSelected(true);
            } else {
                pppoe_auto_dialer_fl.setSelected(false);
                pppoe_auto_dialer_tv.setSelected(false);
            }
        } else if (i == R.id.pppoe_pwd_edt) {
            if (has_focus) {
                pppoe_pwd_tv.setSelected(true);
                pppoe_pwd_edt.setSelected(true);
                pppoe_pwd_edt.selectAll();
            } else {
                pppoe_pwd_tv.setSelected(false);
                pppoe_pwd_edt.setSelected(false);
            }
        } else if (i == R.id.pppoe_username_edt) {
            if (has_focus) {
                pppoe_username_tv.setSelected(true);
                pppoe_username_edt.setSelected(true);
                pppoe_username_edt.selectAll();
            } else {
                pppoe_username_tv.setSelected(false);
                pppoe_username_edt.setSelected(false);
            }
        } else if (i == R.id.pppoe_dialer_hangup) {
            if (has_focus) {
                pppoe_dialer_hangup.setSelected(true);
            } else {
                pppoe_dialer_hangup.setSelected(false);
            }
        } else if (i == R.id.pppoe_dialer_ok) {
            if (has_focus) {
                pppoe_dialer_ok.setSelected(true);
            } else {
                pppoe_dialer_ok.setSelected(false);
            }
        }
    }

    public void onExit() {
        pppoe_show_pwd_fl = null;
        pppoe_auto_dialer_fl = null;
        pppoe_username_edt= null;
        pppoe_pwd_edt= null;
        pppoe_pwd_tv= null;
        pppoe_username_tv= null;
        pppoe_auto_dialer_tv= null;
        pppoe_show_pwd_tv= null;
        pppoe_show_pwd_iv= null;
        pppoe_auto_dialer_iv= null;
        pppoe_net_state_btn= null;
        pppoe_dialer_hangup= null;
        pppoe_dialer_ok= null;
        isShowPwd = false;
        isAutoDialer = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("WrongConstant")
    public EthernetManager getEthernetManager() {
        if (mEthernetManager == null) {
            mEthernetManager = (EthernetManager) ctvContext
                    .getSystemService(NetUtils.ETHERNET_SERVICE);
        }
        return mEthernetManager;
    }
}
