
package com.ctv.settings.network.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.EthernetManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.ctv.settings.network.R;
import com.ctv.settings.network.activity.NetWorkActivity;
import com.ctv.settings.network.utils.NetUtils;


public class TipDialog extends Dialog implements OnFocusChangeListener, OnClickListener {

    private static final String TAG = "net.TipDialog";

    private final Context ctvContext;

    private final NetWorkActivity activity;

    private TextView tip_tv;

    private Button tip_ok_btn;

    private Button tip_cancle_btn;

    private final int index;

    /** Each Bit is a flag ,for example :0B111 **/
    private int flag = 0;

    private String tip_str = "";

    private final WifiManager mWifiManager;

    private EthernetManager mEthernetManager;

    public TipDialog(NetWorkActivity activity, int pageIndexr) {
        super(activity);
        this.activity = activity;
        this.ctvContext = activity.getApplicationContext();
        this.index = pageIndexr;
        this.flag = 0;
        mWifiManager = (WifiManager) ctvContext.getSystemService(Context.WIFI_SERVICE);
    }

    public TipDialog(NetWorkActivity activity, int pageIndexr, int flag) {
        super(activity);
        this.activity = activity;
        this.ctvContext = activity.getApplicationContext();
        this.index = pageIndexr;
        this.flag = flag;
        mWifiManager = (WifiManager) ctvContext.getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tip_dialog);
        setWindowStyle();
        findViews();
    }

    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = ctvContext.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparency_bg);
        w.setBackgroundDrawable(drab);
//        WindowManager.LayoutParams lp = w.getAttributes();
//        final float scale = res.getDisplayMetrics().density;
//        // In the mid-point to calculate the offset x and y
//        lp.x = (int) (82.7 * scale + 0.5f);
//        lp.y = (int) (-36 * scale + 0.5f);
//        lp.width = (int) (473.3 * scale + 0.5f);
//        lp.height = (int) (309.3 * scale + 0.5f);
//        w.setAttributes(lp);
    }
    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        super.dismiss();
        tip_tv = null;
        tip_ok_btn= null;
        tip_cancle_btn= null;
        tip_str = "";
        mEthernetManager= null;
    }

    /**
     * init compontent.
     */
    private void findViews() {
        Log.i(TAG, "findViews index:" + index);
        tip_tv = (TextView) findViewById(R.id.tip_tv);
        tip_cancle_btn = (Button) findViewById(R.id.tip_cancle_btn);
        tip_ok_btn = (Button) findViewById(R.id.tip_ok_btn);

        tip_ok_btn.setOnFocusChangeListener(this);
        tip_cancle_btn.setOnFocusChangeListener(this);
        tip_ok_btn.setOnClickListener(this);
        tip_cancle_btn.setOnClickListener(this);
        if (index == NetUtils.WIFI_HOTSPOT) {
            tip_str = ctvContext.getString(R.string.tip_close) + "\n";
            if ((flag & 1) == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.close_wifi_txt) + "\n";
            }
            if ((flag & 3) >> 1 == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.please_hangup_pppoe)
                        + "\n";
            }
            if ((flag & 7) >> 2 == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.open_wire) + "\n";
            }
        } else if (index == NetUtils.WIRELESS_CONNECT) {
            tip_str = ctvContext.getString(R.string.tip_close_wifi) + "\n";
            if ((flag & 1) == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.close_wifiap_txt) + "\n";
            }
            if ((flag & 3) >> 1 == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.please_hangup_pppoe)
                        + "\n";
            }
            if ((flag & 7) >> 2 == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.disconnect_ethernet)
                        + "\n";
            }
        } else if (index == NetUtils.WIRE_CONNECT) {
            tip_str = ctvContext.getString(R.string.tip_open_wire) + "\n";
            tip_str = tip_str + "* " + ctvContext.getString(R.string.close_wifi_txt) + "\n";
        } else if (index == NetUtils.PPPOE_CONNECT) {
            tip_str = ctvContext.getString(R.string.tip_open_pppoe) + "\n";
            if ((flag & 1) == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.close_wifi_txt) + "\n";
            }
            if ((flag & 3) >> 1 == 1) {
                tip_str = tip_str + "* " + ctvContext.getString(R.string.open_wire) + "\n";
            }
        }
        tip_tv.setText(tip_str);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.tip_ok_btn) {// Close or Open
            setCloseOrOpen();
            dismiss();
        } else if (i == R.id.tip_cancle_btn) {
            dismiss();
        }
    }

    private void setCloseOrOpen() {
        if (index == NetUtils.WIFI_HOTSPOT) {
            if ((flag & 1) == 1) { // 1.Close WiFi
                mWifiManager.setWifiEnabled(false);
                activity.getWirelessViewHolder().setOpenWifi(false);
            }
            if ((flag & 3) >> 1 == 1) { // 2.Close PPPoE
               // activity.getPppoeViewHolder().hangupPppoe();
            }
            if ((flag & 7) >> 2 == 1) { // 3.Open wire
                getEthernetManager().setEnabled(true);
                activity.getWireViewHolder().setOpen(true);
            }
            activity.getWifiHotspotViewHolder().wifiHotHandler.sendEmptyMessage(0);
        } else if (index == NetUtils.WIRELESS_CONNECT) {
            if ((flag & 1) == 1) { // 1.Close WiFi AP
                try {
                    NetUtils.setWifiApEnabled(mWifiManager,null, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                mWifiManager.setWifiApEnabled(null, false);
//                activity.getWifiHotspotViewHolder().setOpenHotspotUi(false);
            }
            if ((flag & 3) >> 1 == 1) { // 2.Close PPPoE
               // activity.getPppoeViewHolder().hangupPppoe();
            }
            if ((flag & 7) >> 2 == 1) { // 3.Close Ethernet
                getEthernetManager().setEnabled(false);
//                activity.getWireViewHolder().setOpen(false);
            }
            activity.getWirelessViewHolder().wifiHandler.sendEmptyMessage(0);
        } else if (index == NetUtils.WIRE_CONNECT) {
            // 1.Close WiFi
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
//                activity.getWirelessViewHolder().setOpenWifi(false);
            }
            activity.getWireViewHolder().wireHandler.sendEmptyMessage(0);
        } else if (index == NetUtils.PPPOE_CONNECT) {
            if ((flag & 1) == 1) {// 1.Close WiFi
                mWifiManager.setWifiEnabled(false);
//                activity.getWirelessViewHolder().setOpenWifi(false);
            }
            if ((flag & 3) >> 1 == 1) {// 2.Open wire
                getEthernetManager().setEnabled(true);
//                activity.getWireViewHolder().setOpen(true);
            }
/*            activity.getPppoeViewHolder().mHandler.sendEmptyMessageDelayed(
                    PppoeViewHolder.PPPOE_DIAL, 1200);*/
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            default:
                break;
            case KeyEvent.KEYCODE_BACK:
                dismiss();
                return true;
        }
        return false;
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int i = view.getId();
        if (i == R.id.tip_ok_btn) {
            if (has_focus) {
                tip_ok_btn.setSelected(true);
            } else {
                tip_ok_btn.setSelected(false);
            }
        } else if (i == R.id.tip_cancle_btn) {
            if (has_focus) {
                tip_cancle_btn.setSelected(true);
            } else {
                tip_cancle_btn.setSelected(false);
            }
        }
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
