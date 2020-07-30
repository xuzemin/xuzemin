
package com.ctv.settings.network.holder;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.net.*;
import android.net.IpConfiguration.IpAssignment;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
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
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.InitDataInfo;
import com.ctv.settings.network.utils.Tools;
import com.ctv.settings.utils.L;

/**
 * 有线模块
 * @author xuzemin
 * @date 2019/09/19
 */
@SuppressLint("NewApi")
public class WireViewHolder implements OnFocusChangeListener, OnClickListener {

    private static final String TAG = "WireViewHolder";

    private final Context ctvContext;

    private FrameLayout wire_open_switch_fl;

    private FrameLayout wire_auto_ip_fl;

    private FrameLayout wire_ip_fl;

    private FrameLayout wire_ipv6_fl;

    private FrameLayout wire_subnet_fl;

    private FrameLayout wire_default_geteway_fl;

    private FrameLayout wire_first_dns_fl;

    private FrameLayout wire_second_dns_fl;

    private LinearLayout wire_open_ll;

    private ImageView wire_open_switch_iv;

    private ImageView wire_auto_ip_iv;

    private ImageView wire_ipv6_iv;

    private EditText wire_ip_edt1;

    private EditText wire_ip_edt2;

    private EditText wire_ip_edt3;

    private EditText wire_ip_edt4;

    private EditText wire_subnet_edt1;

    private EditText wire_subnet_edt2;

    private EditText wire_subnet_edt3;

    private EditText wire_subnet_edt4;

    private EditText wire_default_geteway_edt1;

    private EditText wire_default_geteway_edt2;

    private EditText wire_default_geteway_edt3;

    private EditText wire_default_geteway_edt4;

    private EditText wire_first_dns_edt1;

    private EditText wire_first_dns_edt2;

    private EditText wire_first_dns_edt3;

    private EditText wire_first_dns_edt4;

    private EditText wire_second_dns_edt1;

    private EditText wire_second_dns_edt2;

    private EditText wire_second_dns_edt3;

    private EditText wire_second_dns_edt4;

    private FrameLayout v4_ip_fl;

    private FrameLayout v4_subnet_fl;

    private FrameLayout v4_default_geteway_fl;

    private FrameLayout v4_first_dns_fl;

    private FrameLayout v4_second_dns_fl;

    private EditText v6_ip_edt;

    private EditText v6_subnet_edt;

    private EditText v6_efault_geteway_edt;

    private EditText v6_first_dns_edt;

    private EditText v6_second_dns_edt;

    private Button wire_save_btn;

    private final NetWorkActivity activity;

    private boolean isOpen = false;

    private boolean isAutoip = false;

    private boolean isIpv6 = false;

    private static EthernetManager ethernetManager = null;

    private ConnectivityManager mConnectivityManager = null;

    private final ConnectivityListener mListener;

    private String[] mAutoIps = null;

    private String[] mAutoSubnets = null;

    private String[] mAutoDns1 = null;

    private String[] mAutoDns2 = null;

    private String[] mAutoGateway = null;

    private Map<String, String> mArpMap = new HashMap<String, String>();

    private boolean isErrorTips = false;

    private Thread mARPthread = null;

    private final WifiManager mWifiManager;

    private InitDataInfo mInitData = null;

    @SuppressLint("HandlerLeak")
    private final Handler wireHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.i(TAG, "wireHandler ");
            if (msg.what == 0) {
                isOpen = true;
                setAndTipOpen(false);
                showEthernetInfo(false);
            }
        }
    };

    public WireViewHolder(Context ctvContext, ConnectivityListener conListener) {
        super();
        this.ctvContext = ctvContext.getApplicationContext();
        this.mListener = conListener;
        activity = (NetWorkActivity) ctvContext;
        initView();
        initListener();
        // initData();
        mWifiManager = (WifiManager) ctvContext.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * initView(The function of the method)
     *
     * @Title: initView
     * @Description: TODO
     */
    private void initView() {
        wire_default_geteway_fl =  (FrameLayout) activity.findViewById(R.id.wire_default_geteway_fl);
        wire_open_switch_fl =  (FrameLayout) activity.findViewById(R.id.wire_open_switch_fl);
        wire_second_dns_fl =  (FrameLayout) activity.findViewById(R.id.wire_second_dns_fl);
        wire_first_dns_fl = (FrameLayout)  activity.findViewById(R.id.wire_first_dns_fl);
        wire_auto_ip_fl =  (FrameLayout) activity.findViewById(R.id.wire_auto_ip_fl);
        wire_subnet_fl = (FrameLayout)  activity.findViewById(R.id.wire_subnet_fl);
        wire_ip_fl =  (FrameLayout) activity.findViewById(R.id.wire_ip_fl);
        wire_ipv6_fl =  (FrameLayout) activity.findViewById(R.id.wire_ipv6_fl);

        wire_open_ll =  (LinearLayout) activity.findViewById(R.id.wire_open_ll);

        wire_open_switch_iv =  (ImageView) activity.findViewById(R.id.wire_open_switch_iv);
        wire_auto_ip_iv = (ImageView)  activity.findViewById(R.id.wire_auto_ip_iv);
        wire_ipv6_iv =  (ImageView) activity.findViewById(R.id.wire_ipv6_iv);

        wire_ip_edt1 = (EditText)  activity.findViewById(R.id.wire_ip_edt1);
        wire_ip_edt2 = (EditText)  activity.findViewById(R.id.wire_ip_edt2);
        wire_ip_edt3 = (EditText)  activity.findViewById(R.id.wire_ip_edt3);
        wire_ip_edt4 = (EditText)  activity.findViewById(R.id.wire_ip_edt4);
        wire_subnet_edt1 =  (EditText) activity.findViewById(R.id.wire_subnet_edt1);
        wire_subnet_edt2 = (EditText)  activity.findViewById(R.id.wire_subnet_edt2);
        wire_subnet_edt3 = (EditText)  activity.findViewById(R.id.wire_subnet_edt3);
        wire_subnet_edt4 = (EditText)  activity.findViewById(R.id.wire_subnet_edt4);
        wire_default_geteway_edt1 =  (EditText) activity
                .findViewById(R.id.wire_default_geteway_edt1);
        wire_default_geteway_edt2 =  (EditText) activity
                .findViewById(R.id.wire_default_geteway_edt2);
        wire_default_geteway_edt3 =  (EditText) activity
                .findViewById(R.id.wire_default_geteway_edt3);
        wire_default_geteway_edt4 =  (EditText) activity
                .findViewById(R.id.wire_default_geteway_edt4);

        wire_first_dns_edt1 = (EditText)  activity.findViewById(R.id.wire_first_dns_edt1);
        wire_first_dns_edt2 = (EditText)  activity.findViewById(R.id.wire_first_dns_edt2);
        wire_first_dns_edt3 = (EditText)  activity.findViewById(R.id.wire_first_dns_edt3);
        wire_first_dns_edt4 = (EditText)  activity.findViewById(R.id.wire_first_dns_edt4);
        wire_second_dns_edt1 = (EditText)  activity.findViewById(R.id.wire_second_dns_edt1);
        wire_second_dns_edt2 = (EditText)  activity.findViewById(R.id.wire_second_dns_edt2);
        wire_second_dns_edt3 = (EditText)  activity.findViewById(R.id.wire_second_dns_edt3);
        wire_second_dns_edt4 = (EditText)  activity.findViewById(R.id.wire_second_dns_edt4);
        v6_ip_edt =  (EditText) activity.findViewById(R.id.v6_ip_edt);
        v6_subnet_edt = (EditText)  activity.findViewById(R.id.v6_subnet_edt);
        v6_efault_geteway_edt =  (EditText) activity.findViewById(R.id.v6_efault_geteway_edt);
        v6_first_dns_edt = (EditText)  activity.findViewById(R.id.v6_first_dns_edt);
        v6_second_dns_edt =  (EditText) activity.findViewById(R.id.v6_second_dns_edt);

        v4_ip_fl =  (FrameLayout) activity.findViewById(R.id.v4_ip_fl);
        v4_subnet_fl =  (FrameLayout) activity.findViewById(R.id.v4_subnet_fl);
        v4_default_geteway_fl =  (FrameLayout)activity.findViewById(R.id.v4_efault_geteway_fl);
        v4_first_dns_fl =  (FrameLayout)activity.findViewById(R.id.v4_first_dns_fl);
        v4_second_dns_fl = (FrameLayout) activity.findViewById(R.id.v4_second_dns_fl);

        wire_save_btn = (Button) activity.findViewById(R.id.wire_save_btn);

    }

    private void initListener() {
        wire_save_btn.setOnFocusChangeListener(this);
        wire_auto_ip_fl.setOnFocusChangeListener(this);
        wire_ipv6_fl.setOnFocusChangeListener(this);
        v6_ip_edt.setOnFocusChangeListener(this);
        v6_subnet_edt.setOnFocusChangeListener(this);
        v6_efault_geteway_edt.setOnFocusChangeListener(this);
        v6_first_dns_edt.setOnFocusChangeListener(this);
        v6_second_dns_edt.setOnFocusChangeListener(this);
        wire_ip_edt1.setOnFocusChangeListener(this);
        wire_ip_edt2.setOnFocusChangeListener(this);
        wire_ip_edt3.setOnFocusChangeListener(this);
        wire_ip_edt4.setOnFocusChangeListener(this);
        wire_subnet_edt1.setOnFocusChangeListener(this);
        wire_subnet_edt2.setOnFocusChangeListener(this);
        wire_subnet_edt3.setOnFocusChangeListener(this);
        wire_subnet_edt4.setOnFocusChangeListener(this);
        wire_default_geteway_edt1.setOnFocusChangeListener(this);
        wire_default_geteway_edt2.setOnFocusChangeListener(this);
        wire_default_geteway_edt3.setOnFocusChangeListener(this);
        wire_default_geteway_edt4.setOnFocusChangeListener(this);
        wire_first_dns_edt1.setOnFocusChangeListener(this);
        wire_first_dns_edt2.setOnFocusChangeListener(this);
        wire_first_dns_edt3.setOnFocusChangeListener(this);
        wire_first_dns_edt4.setOnFocusChangeListener(this);
        wire_second_dns_edt1.setOnFocusChangeListener(this);
        wire_second_dns_edt2.setOnFocusChangeListener(this);
        wire_second_dns_edt3.setOnFocusChangeListener(this);
        wire_second_dns_edt4.setOnFocusChangeListener(this);
        wire_open_switch_fl.setOnClickListener(this);
        wire_auto_ip_fl.setOnClickListener(this);
        wire_ipv6_fl.setOnClickListener(this);
        wire_save_btn.setOnClickListener(this);
        activity.findViewById(R.id.back_wire).setOnClickListener(this);
    }

    public void initData(InitDataInfo data) {
        isOpen = mListener.isEthernetEnable();
        Log.i(TAG, "-shtet-isOpen:" + isOpen);
        ethernetManager = mListener.getEthernetManager();
        mConnectivityManager = (ConnectivityManager) ctvContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        mInitData = data;
        isAutoip = isAutoIp();
        isIpv6 = false;// unsupport_v6
        setIpv6(false);
        if (isOpen) {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.on);
            wire_open_ll.setVisibility(View.VISIBLE);
            wire_save_btn.setVisibility(View.VISIBLE);
        } else {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.off);
            wire_save_btn.setVisibility(View.GONE);
            wire_open_ll.setVisibility(View.GONE);
        }
        if (isAutoip) {
            wire_auto_ip_iv.setBackgroundResource(R.mipmap.on);
        } else {
            wire_auto_ip_iv.setBackgroundResource(R.mipmap.off);
        }
//        setAutoIp();
//        showEthernetInfo(true);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (activity.getCurrentFocus() == wire_save_btn) {
                    if (isAutoip) {
                        wire_auto_ip_fl.requestFocus();
                    } else {
                        v4_second_dns_fl.requestFocus();
                    }
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (activity.getCurrentFocus() == wire_auto_ip_fl) {
                    if (isAutoip) {
                        wire_save_btn.requestFocus();
                    } else {
                        v4_ip_fl.requestFocus();
                    }
                    return true;
                } else if (activity.getCurrentFocus() == wire_save_btn) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
//                if (activity.getNetworkSettingsViewHolder().isFocuseWireTextView()) {
//                    wire_open_switch_fl.requestFocus();
//                    return true;
//                }
            default:
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.wire_open_switch_fl) {
            isOpen = !isOpen;
            // setAndTipOpen(true);
            setEthernetOpenOrClose();
            showEthernetInfo(false);
        } else if (i == R.id.wire_auto_ip_fl) {
            isAutoip = !isAutoip;
            if (isAutoip == false) {
                Toast.makeText(ctvContext, R.string.ethernet_tip_2, Toast.LENGTH_LONG).show();
                // mARPthread = new ArpThread();
                // mARPthread.start();//check ip conflict,scan arp.
            }
            setAutoIp();
            unfocusIPconfig();
            showEthernetInfo(false);
        } else if (i == R.id.wire_ipv6_fl) {
            isIpv6 = !isIpv6;
            setIpv6(false);
        } else if (i == R.id.wire_save_btn) {// save and Toast
            isErrorTips = false;
            saveIpAddress();
        }else if (i == R.id.back_wire) {// save and Toast
            activity.finish();
        }

    }

    private void setEthernetOpenOrClose() {
        if (isOpen) {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.on);
            wire_open_ll.setVisibility(View.VISIBLE);
            wire_save_btn.setVisibility(View.VISIBLE);
            ethernetManager.setEnabled(true);
        } else {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.off);
            wire_save_btn.setVisibility(View.GONE);
            wire_open_ll.setVisibility(View.GONE);
            ethernetManager.setEnabled(false);
        }
    }

    private void unfocusIPconfig() {
        wire_ip_fl.getChildAt(0).setSelected(false);
        v4_ip_fl.setSelected(false);
        wire_subnet_fl.getChildAt(0).setSelected(false);
        v4_subnet_fl.setSelected(false);
        wire_default_geteway_fl.getChildAt(0).setSelected(false);
        v4_default_geteway_fl.setSelected(false);
        wire_first_dns_fl.getChildAt(0).setSelected(false);
        v4_first_dns_fl.setSelected(false);
        wire_second_dns_fl.getChildAt(0).setSelected(false);
        v4_second_dns_fl.setSelected(false);
    }

    private void setAndTipOpen(boolean isClick) {
        if (isClick) {
            if (mListener.isWifiEnabled()) {
                if (mWifiManager.isWifiEnabled()) {
                    mWifiManager.setWifiEnabled(false);
//                activity.getWirelessViewHolder().setOpenWifi(false);
                }
                wireHandler.sendEmptyMessage(0);
//                TipDialog tipDialog = new TipDialog(activity, NetUtils.WIRE_CONNECT);
//                tipDialog.show();
//                isOpen = false;
//                Log.d(TAG, "setAndTipOpen --  WifiEnabled?");
                return;
            }
        }
        if (isOpen) {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.on);
            wire_open_ll.setVisibility(View.VISIBLE);
            wire_save_btn.setVisibility(View.VISIBLE);
            ethernetManager.setEnabled(true);
        } else {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.off);
            wire_save_btn.setVisibility(View.GONE);
            wire_open_ll.setVisibility(View.GONE);
            ethernetManager.setEnabled(false);
        }
    }

    private void setIpv6(boolean isSupportV6) {
        if (isSupportV6) {
            wire_ipv6_fl.setVisibility(View.VISIBLE);
        } else {
            wire_ipv6_fl.setVisibility(View.GONE);
        }
        if (isIpv6) {
            wire_ipv6_iv.setBackgroundResource(R.mipmap.on);

            v6_ip_edt.setVisibility(View.VISIBLE);
            v6_subnet_edt.setVisibility(View.VISIBLE);
            v6_efault_geteway_edt.setVisibility(View.VISIBLE);
            v6_first_dns_edt.setVisibility(View.VISIBLE);
            v6_second_dns_edt.setVisibility(View.VISIBLE);
            v4_ip_fl.setVisibility(View.GONE);
            v4_subnet_fl.setVisibility(View.GONE);
            v4_default_geteway_fl.setVisibility(View.GONE);
            v4_first_dns_fl.setVisibility(View.GONE);
            v4_second_dns_fl.setVisibility(View.GONE);
        } else {
            wire_ipv6_iv.setBackgroundResource(R.mipmap.off);
            v6_ip_edt.setVisibility(View.GONE);
            v6_subnet_edt.setVisibility(View.GONE);
            v6_efault_geteway_edt.setVisibility(View.GONE);
            v6_first_dns_edt.setVisibility(View.GONE);
            v6_second_dns_edt.setVisibility(View.GONE);
            v4_ip_fl.setVisibility(View.VISIBLE);
            v4_subnet_fl.setVisibility(View.VISIBLE);
            v4_default_geteway_fl.setVisibility(View.VISIBLE);
            v4_first_dns_fl.setVisibility(View.VISIBLE);
            v4_second_dns_fl.setVisibility(View.VISIBLE);
        }
    }

    public void showEthernetInfo(boolean isInit) {
        LinkProperties linkProperties = null;
        if (isInit) {
            linkProperties = mInitData.getErthernetLinkProperties();
        } else {
            try {
                linkProperties = NetUtils.getLinkProperties(mConnectivityManager,ConnectivityManager.TYPE_ETHERNET);
            } catch (Exception e) {
                L.d(e.toString());
            }
//                    mConnectivityManager
//                    .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);

        }
        if (isOpen) {
            List<LinkAddress> listLink = null;
            try {
                listLink = NetUtils.getAllLinkAddresses(linkProperties);
            } catch (Exception e) {
                L.d(e.toString());
            }
            if (mListener.getConnectivityStatus().isEthernetConnected() && linkProperties != null
                    && listLink != null
                    && !listLink.equals("")
                    && !listLink.isEmpty()) {
                String[] ethernet_ips = null;
                String[] ethernet_gateways = null;
                String[] ethernet_masks = null;
                String[] ethernet_dns1 = null;
                String[] ethernet_dns2 = null;
                for (LinkAddress linkAddress : listLink) {
                    InetAddress address = linkAddress.getAddress();
                    if (address instanceof Inet4Address) {
                        String ip = address.getHostAddress();
                        if (TextUtils.isEmpty(ip)) {
                            return;
                        }
                        ethernet_ips = ip.split("\\.");
                    }
                }
                L.d(ethernet_ips.toString());
                if (ethernet_ips != null && ethernet_ips.length > 3) {
                    wire_ip_edt1.setText(ethernet_ips[0]);
                    wire_ip_edt2.setText(ethernet_ips[1]);
                    wire_ip_edt3.setText(ethernet_ips[2]);
                    wire_ip_edt4.setText(ethernet_ips[3]);
                    mAutoIps = new String[4];
                    System.arraycopy(ethernet_ips, 0, mAutoIps, 0, ethernet_ips.length);
                }

                for (RouteInfo route : linkProperties.getRoutes()) {
                    if (route.isDefaultRoute()) {
                        ethernet_gateways = route.getGateway().getHostAddress().trim().split("\\.");
                        break;
                    }
                }
                L.d(ethernet_gateways.toString());
                if (ethernet_gateways != null && ethernet_gateways.length > 3) {
                    wire_default_geteway_edt1.setText(ethernet_gateways[0]);
                    wire_default_geteway_edt2.setText(ethernet_gateways[1]);
                    wire_default_geteway_edt3.setText(ethernet_gateways[2]);
                    wire_default_geteway_edt4.setText(ethernet_gateways[3]);
                    mAutoGateway = new String[4];
                    System.arraycopy(ethernet_gateways, 0, mAutoGateway, 0,
                            ethernet_gateways.length);

                }
                ethernet_masks = new String[ethernet_ips.length];
                for (int i = 0; i < ethernet_ips.length; i++) {
                    ethernet_masks[i] = ethernet_ips[i].equals(ethernet_gateways[i]) ? "255" : "0";
                }
                L.d(ethernet_masks.toString());
                if (ethernet_masks != null && ethernet_masks.length > 3) {
                    wire_subnet_edt1.setText(ethernet_masks[0]);
                    wire_subnet_edt2.setText(ethernet_masks[1]);
                    wire_subnet_edt3.setText(ethernet_masks[2]);
                    wire_subnet_edt4.setText(ethernet_masks[3]);
                    mAutoSubnets = new String[4];
                    System.arraycopy(ethernet_masks, 0, mAutoSubnets, 0, ethernet_masks.length);
                }
                Iterator<InetAddress> dnsIterator = linkProperties.getDnsServers().iterator();
                if (dnsIterator.hasNext()) {
                    ethernet_dns1 = dnsIterator.next().getHostAddress().trim().split("\\.");
                }
                if (dnsIterator.hasNext()) {
                    ethernet_dns2 = dnsIterator.next().getHostAddress().trim().split("\\.");
                }
                L.d(ethernet_dns1.toString());
                if (ethernet_dns1 != null && ethernet_dns1.length > 3) {
                    wire_first_dns_edt1.setText(ethernet_dns1[0]);
                    wire_first_dns_edt2.setText(ethernet_dns1[1]);
                    wire_first_dns_edt3.setText(ethernet_dns1[2]);
                    wire_first_dns_edt4.setText(ethernet_dns1[3]);
                    mAutoDns1 = new String[4];
                    System.arraycopy(ethernet_dns1, 0, mAutoDns1, 0, ethernet_dns1.length);
                }
                if (ethernet_dns2 != null && ethernet_dns2.length > 3) {
                    wire_second_dns_edt1.setText(ethernet_dns2[0]);
                    wire_second_dns_edt2.setText(ethernet_dns2[1]);
                    wire_second_dns_edt3.setText(ethernet_dns2[2]);
                    wire_second_dns_edt4.setText(ethernet_dns2[3]);
                    mAutoDns2 = new String[4];
                    System.arraycopy(ethernet_dns2, 0, mAutoDns2, 0, ethernet_dns2.length);
                }
            } else {
                wire_ip_edt1.setText("");
                wire_ip_edt2.setText("");
                wire_ip_edt3.setText("");
                wire_ip_edt4.setText("");
                wire_subnet_edt1.setText("");
                wire_subnet_edt2.setText("");
                wire_subnet_edt3.setText("");
                wire_subnet_edt4.setText("");
                wire_default_geteway_edt1.setText("");
                wire_default_geteway_edt2.setText("");
                wire_default_geteway_edt3.setText("");
                wire_default_geteway_edt4.setText("");
                wire_first_dns_edt1.setText("");
                wire_first_dns_edt2.setText("");
                wire_first_dns_edt3.setText("");
                wire_first_dns_edt4.setText("");
                wire_second_dns_edt1.setText("");
                wire_second_dns_edt2.setText("");
                wire_second_dns_edt3.setText("");
                wire_second_dns_edt4.setText("");
            }
        }
    }

    private void setAutoIp() {
        IpConfiguration ipConfiguration = ethernetManager.getConfiguration();
        setIpColor(v4_ip_fl, isAutoip);
        setIpColor(v4_subnet_fl, isAutoip);
        setIpColor(v4_default_geteway_fl, isAutoip);
        setIpColor(v4_first_dns_fl, isAutoip);
        setIpColor(v4_second_dns_fl, isAutoip);

        if (isAutoip) {
        	int hightColor = ctvContext.getResources()
                    .getColor(R.color.transparent);
        	wire_ip_edt1.setHighlightColor(hightColor);
            wire_ip_edt1.setEnabled(false);
            wire_ip_edt2.setEnabled(false);
            wire_ip_edt3.setEnabled(false);
            wire_ip_edt4.setEnabled(false);
            wire_subnet_edt1.setEnabled(false);
            wire_subnet_edt2.setEnabled(false);
            wire_subnet_edt3.setEnabled(false);
            wire_subnet_edt4.setEnabled(false);
            wire_default_geteway_edt1.setEnabled(false);
            wire_default_geteway_edt2.setEnabled(false);
            wire_default_geteway_edt3.setEnabled(false);
            wire_default_geteway_edt4.setEnabled(false);
            wire_first_dns_edt1.setEnabled(false);
            wire_first_dns_edt2.setEnabled(false);
            wire_first_dns_edt3.setEnabled(false);
            wire_first_dns_edt4.setEnabled(false);
            wire_second_dns_edt1.setEnabled(false);
            wire_second_dns_edt2.setEnabled(false);
            wire_second_dns_edt3.setEnabled(false);
            wire_second_dns_edt4.setEnabled(false);
            wire_auto_ip_iv.setBackgroundResource(R.mipmap.on);

            int color = ctvContext.getResources()
                    .getColor(R.color.half_white);
            ((TextView) wire_ip_fl.getChildAt(0)).setTextColor(color);
            ((TextView) wire_subnet_fl.getChildAt(0)).setTextColor(color);
            ((TextView) wire_default_geteway_fl.getChildAt(0)).setTextColor(color);
            ((TextView) wire_first_dns_fl.getChildAt(0)).setTextColor(color);
            ((TextView) wire_second_dns_fl.getChildAt(0)).setTextColor(color);
            ipConfiguration.setIpAssignment(IpAssignment.DHCP);
            ethernetManager.setConfiguration(ipConfiguration);
        } else {
        	int hightColor = ctvContext.getResources()
                    .getColor(R.color.edit_select);
        	wire_ip_edt1.setHighlightColor(hightColor);
            wire_ip_edt1.setEnabled(true);
            wire_ip_edt2.setEnabled(true);
            wire_ip_edt3.setEnabled(true);
            wire_ip_edt4.setEnabled(true);
            wire_subnet_edt1.setEnabled(true);
            wire_subnet_edt2.setEnabled(true);
            wire_subnet_edt3.setEnabled(true);
            wire_subnet_edt4.setEnabled(true);
            wire_default_geteway_edt1.setEnabled(true);
            wire_default_geteway_edt2.setEnabled(true);
            wire_default_geteway_edt3.setEnabled(true);
            wire_default_geteway_edt4.setEnabled(true);
            wire_first_dns_edt1.setEnabled(true);
            wire_first_dns_edt2.setEnabled(true);
            wire_first_dns_edt3.setEnabled(true);
            wire_first_dns_edt4.setEnabled(true);
            wire_second_dns_edt1.setEnabled(true);
            wire_second_dns_edt2.setEnabled(true);
            wire_second_dns_edt3.setEnabled(true);
            wire_second_dns_edt4.setEnabled(true);
            wire_auto_ip_iv.setBackgroundResource(R.mipmap.off);

            @SuppressLint("ResourceType") ColorStateList colorStateList = ctvContext.getResources()
                    .getColorStateList(R.drawable.item_text_color);
            ((TextView) wire_ip_fl.getChildAt(0)).setTextColor(colorStateList);
            ((TextView) wire_subnet_fl.getChildAt(0)).setTextColor(colorStateList);
            ((TextView) wire_default_geteway_fl.getChildAt(0)).setTextColor(colorStateList);
            ((TextView) wire_first_dns_fl.getChildAt(0)).setTextColor(colorStateList);
            ((TextView) wire_second_dns_fl.getChildAt(0)).setTextColor(colorStateList);
        }

        if (mListener.getConnectivityStatus().isWifiConnected()) {
            return;
        }
        ipConfiguration.setIpAssignment(IpAssignment.STATIC);
    }

    private void setIpColor(FrameLayout v, boolean isAutoip) {
        if (isAutoip) {
            v.setBackgroundResource(R.drawable.shape_ip_disabled);
            ((TextView) v.getChildAt(1)).setTextColor(0x91000000);
            ((TextView) v.getChildAt(3)).setTextColor(0x91000000);
            ((TextView) v.getChildAt(5)).setTextColor(0x91000000);
        } else {
            v.setBackgroundResource(R.drawable.selector_btn_bg);
            ((TextView) v.getChildAt(1)).setTextColor(0x7FFFFFFF);
            ((TextView) v.getChildAt(3)).setTextColor(0x7FFFFFFF);
            ((TextView) v.getChildAt(5)).setTextColor(0x7FFFFFFF);
        }
    }

    private void saveIpAddress() {
        IpConfiguration ipConfiguration = ethernetManager.getConfiguration();
        if (isAutoip) {
            wire_auto_ip_iv.setBackgroundResource(R.mipmap.on);
            ipConfiguration.setIpAssignment(IpAssignment.DHCP);
            ethernetManager.setConfiguration(ipConfiguration);
        } else {
            wire_auto_ip_iv.setBackgroundResource(R.mipmap.off);
            ipConfiguration.setIpAssignment(IpAssignment.STATIC);
            StaticIpConfiguration staticConfig = new StaticIpConfiguration();
            String ip = getV4IpAddress(wire_ip_edt1, wire_ip_edt2, wire_ip_edt3, wire_ip_edt4);
            String subnet = getV4Address(wire_subnet_edt1, wire_subnet_edt2, wire_subnet_edt3,
                    wire_subnet_edt4, mAutoSubnets);
            String gateway = getV4Address(wire_default_geteway_edt1, wire_default_geteway_edt2,
                    wire_default_geteway_edt3, wire_default_geteway_edt4, mAutoGateway);
            String first_dns = getV4Address(wire_first_dns_edt1, wire_first_dns_edt2,
                    wire_first_dns_edt3, wire_first_dns_edt4, mAutoDns1);
            String second_dns = getV4Address(wire_second_dns_edt1, wire_second_dns_edt2,
                    wire_second_dns_edt3, wire_second_dns_edt4, mAutoDns2);
            if (TextUtils.isEmpty(ip)) {
                Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
                return;
            }
            if (Tools.matchIP(ip)) {
                Inet4Address inetAddr = null;
                inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ip);
                int networkPrefixLength = 0;
                if (subnet != null && Tools.matchIP(subnet)) {
                    networkPrefixLength = Tools.calcPrefixLengthByMask(subnet);
                } else {
                    isErrorTips = true;
                    Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    if (networkPrefixLength >= 0 && networkPrefixLength <= 32) {
                        staticConfig.ipAddress = NetUtils.getLinkAddress(inetAddr, networkPrefixLength);
//                                new LinkAddress(inetAddr, networkPrefixLength);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    isErrorTips = true;
                    Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                isErrorTips = true;
                Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
                return;
            }
            Log.i(TAG, "gateway==" + gateway);
            if (!TextUtils.isEmpty(gateway)) {
                if (Tools.matchIP(gateway)) {
                    staticConfig.gateway = (Inet4Address) NetworkUtils
                            .numericToInetAddress(gateway);
                } else {
                    isErrorTips = true;
                    Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
                    return;
                }
            } else {
                isErrorTips = true;
                Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
                return;

            }
            if (!TextUtils.isEmpty(first_dns) && Tools.matchIP(first_dns)) {
                staticConfig.dnsServers.add((Inet4Address) NetworkUtils
                        .numericToInetAddress(first_dns));
            }
            if (!TextUtils.isEmpty(second_dns) && Tools.matchIP(second_dns)) {
                staticConfig.dnsServers.add((Inet4Address) NetworkUtils
                        .numericToInetAddress(second_dns));

            }
            if (TextUtils.isEmpty(second_dns) && TextUtils.isEmpty(first_dns)) {
                isErrorTips = true;
                Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
                return;
            }

            String currentIP = getAutoIp();
            if (Tools.isIPConflict(ip, currentIP)) {
                isErrorTips = true;
                Toast.makeText(ctvContext, R.string.ethernet_tip_ip_same, Toast.LENGTH_LONG).show();
                return;
            }
            // if (mArpMap != null) {
            // Log.d(TAG,"---------ARP map:"+mArpMap.toString());
            // if(mArpMap.containsKey(ip)){
            // isErrorTips = true;
            // Toast.makeText(ctvContext, R.string.ethernet_tip_ip_same,
            // Toast.LENGTH_LONG).show();
            // return;
            // }
            // }
            ipConfiguration.setStaticIpConfiguration(staticConfig);
            ethernetManager.setConfiguration(ipConfiguration);
            if (mListener.getConnectivityStatus().isEthernetConnected()) {
                Toast.makeText(ctvContext, R.string.save_tip, Toast.LENGTH_LONG).show();
            } else {
                isErrorTips = true;
                Toast.makeText(ctvContext, R.string.ethernet_tip, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int i = view.getId();
        if (i == R.id.wire_auto_ip_fl) {
            if (has_focus) {
                wire_auto_ip_fl.getChildAt(0).setSelected(true);
                if (isAutoip == false) {
                    Toast.makeText(ctvContext, R.string.ethernet_tip_2, Toast.LENGTH_LONG)
                            .show();
                }

            } else {
                wire_auto_ip_fl.getChildAt(0).setSelected(false);
            }
        } else if (i == R.id.wire_ipv6_fl) {
            if (has_focus) {
                wire_ipv6_fl.getChildAt(0).setSelected(true);
            } else {
                wire_ipv6_fl.getChildAt(0).setSelected(false);
            }
        } else if (i == R.id.v6_ip_edt) {
            if (has_focus) {
                wire_ip_fl.getChildAt(0).setSelected(true);
                v6_ip_edt.selectAll();
                wire_ip_fl.setSelected(true);
            } else {
                wire_ip_fl.getChildAt(0).setSelected(false);
                wire_ip_fl.setSelected(false);
            }
        } else if (i == R.id.v6_subnet_edt) {
            if (has_focus) {
                wire_subnet_fl.getChildAt(0).setSelected(true);
                v6_subnet_edt.selectAll();
                v6_subnet_edt.setSelected(true);
            } else {
                wire_subnet_fl.getChildAt(0).setSelected(false);
                v6_subnet_edt.setSelected(false);
            }
        } else if (i == R.id.v6_efault_geteway_edt) {
            if (has_focus) {
                wire_default_geteway_fl.getChildAt(0).setSelected(true);
                v6_efault_geteway_edt.selectAll();
                v6_efault_geteway_edt.setSelected(false);

            } else {
                wire_default_geteway_fl.getChildAt(0).setSelected(false);
                v6_efault_geteway_edt.setSelected(false);
            }
        } else if (i == R.id.v6_first_dns_edt) {
            if (has_focus) {
                wire_first_dns_fl.getChildAt(0).setSelected(true);
                v6_first_dns_edt.selectAll();
                v6_first_dns_edt.setSelected(true);
            } else {
                wire_first_dns_fl.getChildAt(0).setSelected(false);
                v6_first_dns_edt.setSelected(false);
            }
        } else if (i == R.id.v6_second_dns_edt) {
            if (has_focus) {
                wire_second_dns_fl.getChildAt(0).setSelected(true);
                v6_second_dns_edt.setSelected(true);
                v6_second_dns_edt.selectAll();
            } else {
                wire_second_dns_fl.getChildAt(0).setSelected(false);
                v6_second_dns_edt.setSelected(false);
            }
        } else if (i == R.id.wire_ip_edt1) {
            if (has_focus) {
                wire_ip_fl.getChildAt(0).setSelected(true);
                v4_ip_fl.setSelected(true);
                wire_ip_edt1.selectAll();
            } else {
                wire_ip_fl.getChildAt(0).setSelected(false);
                v4_ip_fl.setSelected(false);
            }
        } else if (i == R.id.wire_ip_edt2) {
            if (has_focus) {
                wire_ip_fl.getChildAt(0).setSelected(true);
                v4_ip_fl.setSelected(true);
                wire_ip_edt2.selectAll();
            } else {
                wire_ip_fl.getChildAt(0).setSelected(false);
                v4_ip_fl.setSelected(false);
            }
        } else if (i == R.id.wire_ip_edt3) {
            if (has_focus) {
                wire_ip_fl.getChildAt(0).setSelected(true);
                v4_ip_fl.setSelected(true);
                wire_ip_edt3.selectAll();
            } else {
                wire_ip_fl.getChildAt(0).setSelected(false);
                v4_ip_fl.setSelected(false);
            }
        } else if (i == R.id.wire_ip_edt4) {
            if (has_focus) {
                wire_ip_fl.getChildAt(0).setSelected(true);
                v4_ip_fl.setSelected(true);
                wire_ip_edt4.selectAll();
            } else {
                wire_ip_fl.getChildAt(0).setSelected(false);
                v4_ip_fl.setSelected(false);
            }
        } else if (i == R.id.wire_subnet_edt1) {
            if (has_focus) {
                wire_subnet_fl.getChildAt(0).setSelected(true);
                v4_subnet_fl.setSelected(true);
                wire_subnet_edt1.selectAll();
            } else {
                wire_subnet_fl.getChildAt(0).setSelected(false);
                v4_subnet_fl.setSelected(false);
            }
        } else if (i == R.id.wire_subnet_edt2) {
            if (has_focus) {
                wire_subnet_fl.getChildAt(0).setSelected(true);
                v4_subnet_fl.setSelected(true);
                wire_subnet_edt2.selectAll();
            } else {
                wire_subnet_fl.getChildAt(0).setSelected(false);
                v4_subnet_fl.setSelected(false);
            }
        } else if (i == R.id.wire_subnet_edt3) {
            if (has_focus) {
                wire_subnet_fl.getChildAt(0).setSelected(true);
                v4_subnet_fl.setSelected(true);
                wire_subnet_edt3.selectAll();
            } else {
                wire_subnet_fl.getChildAt(0).setSelected(false);
                v4_subnet_fl.setSelected(false);
            }
        } else if (i == R.id.wire_subnet_edt4) {
            if (has_focus) {
                wire_subnet_fl.getChildAt(0).setSelected(true);
                v4_subnet_fl.setSelected(true);
                wire_subnet_edt4.selectAll();
            } else {
                wire_subnet_fl.getChildAt(0).setSelected(false);
                v4_subnet_fl.setSelected(false);
            }
        } else if (i == R.id.wire_default_geteway_edt1) {
            if (has_focus) {
                wire_default_geteway_fl.getChildAt(0).setSelected(true);
                v4_default_geteway_fl.setSelected(true);
                wire_default_geteway_edt1.selectAll();
            } else {
                wire_default_geteway_fl.getChildAt(0).setSelected(false);
                v4_default_geteway_fl.setSelected(false);
            }
        } else if (i == R.id.wire_default_geteway_edt2) {
            if (has_focus) {
                wire_default_geteway_fl.getChildAt(0).setSelected(true);
                v4_default_geteway_fl.setSelected(true);
                wire_default_geteway_edt2.selectAll();
            } else {
                wire_default_geteway_fl.getChildAt(0).setSelected(false);
                v4_default_geteway_fl.setSelected(false);
            }
        } else if (i == R.id.wire_default_geteway_edt3) {
            if (has_focus) {
                wire_default_geteway_fl.getChildAt(0).setSelected(true);
                v4_default_geteway_fl.setSelected(true);
                wire_default_geteway_edt3.selectAll();
            } else {
                wire_default_geteway_fl.getChildAt(0).setSelected(false);
                v4_default_geteway_fl.setSelected(false);
            }
        } else if (i == R.id.wire_default_geteway_edt4) {
            if (has_focus) {
                wire_default_geteway_fl.getChildAt(0).setSelected(true);
                v4_default_geteway_fl.setSelected(true);
                wire_default_geteway_edt4.selectAll();
            } else {
                wire_default_geteway_fl.getChildAt(0).setSelected(false);
                v4_default_geteway_fl.setSelected(false);
            }
        } else if (i == R.id.wire_first_dns_edt1) {
            if (has_focus) {
                wire_first_dns_fl.getChildAt(0).setSelected(true);
                v4_first_dns_fl.setSelected(true);
                wire_first_dns_edt1.selectAll();
            } else {
                wire_first_dns_fl.getChildAt(0).setSelected(false);
                v4_first_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_first_dns_edt2) {
            if (has_focus) {
                wire_first_dns_fl.getChildAt(0).setSelected(true);
                v4_first_dns_fl.setSelected(true);
                wire_first_dns_edt2.selectAll();
            } else {
                wire_first_dns_fl.getChildAt(0).setSelected(false);
                v4_first_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_first_dns_edt3) {
            if (has_focus) {
                wire_first_dns_fl.getChildAt(0).setSelected(true);
                v4_first_dns_fl.setSelected(true);
                wire_first_dns_edt3.selectAll();
            } else {
                wire_first_dns_fl.getChildAt(0).setSelected(false);
                v4_first_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_first_dns_edt4) {
            if (has_focus) {
                wire_first_dns_fl.getChildAt(0).setSelected(true);
                v4_first_dns_fl.setSelected(true);
                wire_first_dns_edt4.selectAll();
            } else {
                wire_first_dns_fl.getChildAt(0).setSelected(false);
                v4_first_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_second_dns_edt1) {
            if (has_focus) {
                wire_second_dns_fl.getChildAt(0).setSelected(true);
                v4_second_dns_fl.setSelected(true);
                wire_second_dns_edt1.selectAll();
            } else {
                wire_second_dns_fl.getChildAt(0).setSelected(false);
                v4_second_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_second_dns_edt2) {
            if (has_focus) {
                wire_second_dns_fl.getChildAt(0).setSelected(true);
                v4_second_dns_fl.setSelected(true);
                wire_second_dns_edt2.selectAll();
            } else {
                wire_second_dns_fl.getChildAt(0).setSelected(false);
                v4_second_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_second_dns_edt3) {
            if (has_focus) {
                wire_second_dns_fl.getChildAt(0).setSelected(true);
                v4_second_dns_fl.setSelected(true);
                wire_second_dns_edt3.selectAll();
            } else {
                wire_second_dns_fl.getChildAt(0).setSelected(false);
                v4_second_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_second_dns_edt4) {
            if (has_focus) {
                wire_second_dns_fl.getChildAt(0).setSelected(true);
                v4_second_dns_fl.setSelected(true);
                wire_second_dns_edt4.selectAll();
            } else {
                wire_second_dns_fl.getChildAt(0).setSelected(false);
                v4_second_dns_fl.setSelected(false);
            }
        } else if (i == R.id.wire_save_btn) {
            if (has_focus) {
                wire_save_btn.setSelected(true);
            } else {
                wire_save_btn.setSelected(false);
            }
        }
    }

    private String getV4Address(EditText edit_1, EditText edit_2, EditText edit_3, EditText edit_4,
            String[] auto_param) {
        StringBuffer address = new StringBuffer();
        String str1 = edit_1.getText().toString().trim();
        String str2 = edit_2.getText().toString().trim();
        String str3 = edit_3.getText().toString().trim();
        String str4 = edit_4.getText().toString().trim();
        address.append(str1);
        address.append(".");
        address.append(str2);
        address.append(".");
        address.append(str3);
        address.append(".");
        address.append(str4);
        if (auto_param != null && auto_param.length == 4) {
            if (!str1.equals(auto_param[0]) || !str2.equals(auto_param[1])
                    || !str3.equals(auto_param[2]) || !str4.equals(auto_param[3])) {
                return "";
            }
        }
        return address.toString();
    }

    private String getV4IpAddress(EditText edit_1, EditText edit_2, EditText edit_3, EditText edit_4) {
        StringBuffer address = new StringBuffer();
        String str1 = edit_1.getText().toString().trim();
        if (isIpNum(edit_1, str1)) {
            address.append(str1);
        } else {
            return "";
        }
        address.append(".");
        String str2 = edit_2.getText().toString().trim();
        if (isIpNum(edit_2, str2)) {
            address.append(str2);
        } else {
            return "";
        }
        address.append(".");
        String str3 = edit_3.getText().toString().trim();
        if (isIpNum(edit_3, str3)) {
            address.append(str3);
        } else {
            return "";
        }
        address.append(".");
        String str4 = edit_4.getText().toString().trim();
        if (isIpNum(edit_4, str4)) {
            address.append(str4);
        } else {
            return "";
        }
        if (mAutoIps != null && mAutoIps.length == 4) {
            if (!str1.equals(mAutoIps[0]) || !str2.equals(mAutoIps[1]) || !str3.equals(mAutoIps[2])) {
                return "";
            }
        }
        return address.toString();
    }

    private boolean isIpNum(EditText edit_1, String str1) {
        if (TextUtils.isEmpty(str1)) {
            edit_1.requestFocus();
            return false;
        } else {
            int ip1 = Integer.parseInt(str1);
            if (ip1 > 255 || ip1 < 0) {
                edit_1.requestFocus();
                return false;
            }
        }
        return true;
    }

    public void setOpen(boolean isOpen) {
        Log.i(TAG, "Ethernet_sh----------setOpen " + isOpen);
        this.isOpen = isOpen;
        if (isOpen) {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.on);
            wire_open_ll.setVisibility(View.VISIBLE);
            wire_save_btn.setVisibility(View.VISIBLE);
        } else {
            wire_open_switch_iv.setBackgroundResource(R.mipmap.off);
            wire_save_btn.setVisibility(View.GONE);
            wire_open_ll.setVisibility(View.GONE);
        }
    }

    /**
     * @return true: is auto ip. false: is not auto ip
     */
    private boolean isAutoIp() {
        return mInitData.isAutoip();
    }

    private String getAutoIp() {
        if (mAutoIps != null && mAutoIps.length == 4) {

            StringBuffer address = new StringBuffer();
            String str1 = mAutoIps[0];
            address.append(str1);
            address.append(".");
            String str2 = mAutoIps[1];
            address.append(str2);
            address.append(".");
            String str3 = mAutoIps[2];
            address.append(str3);
            address.append(".");
            String str4 = mAutoIps[3];
            address.append(str4);
            return address.toString();
        }
        return "";
    }

    public void onExit() {
        if (mARPthread != null && mARPthread.isAlive()) {
            mARPthread.interrupt();
            mARPthread = null;
        }
		isOpen = false;
        isAutoip = false;
        isIpv6 = false;
        ethernetManager = null;
        mConnectivityManager = null;
        mAutoIps = null;
        mAutoSubnets = null;
        mAutoDns1 = null;
        mAutoDns2 = null;
        mAutoGateway = null;
        isErrorTips = false;
        mInitData = null;
        wireHandler.removeCallbacksAndMessages(null);


    }

    public class ArpThread extends Thread {
        public void run() {
            while (!isErrorTips) {
                try {
                    if (mArpMap != null && !mArpMap.isEmpty()) {
                        mArpMap.clear();
                    }
                    mArpMap = Tools.createArpMap();
                    isErrorTips = true;
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

}
