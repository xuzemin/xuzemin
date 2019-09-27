
package com.ctv.settings.network.adapter;

import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ctv.settings.network.R;
import com.ctv.settings.network.holder.NetstateViewHolder;
import com.ctv.settings.network.utils.NetUtils;
import com.ctv.settings.network.utils.Tools;
import com.ctv.settings.utils.L;
import com.mstar.android.pppoe.PppoeManager;

@SuppressLint("NewApi")
public class NetstateAdapter extends BaseAdapter {

    private static final String TAG = "NetstateAdapter";

    private Context ctvContext;

    private boolean isConnect;

    private final String[] addr_name_strings;

    private int status;

    private PppoeManager pppoeManager;

    private TextView state_value_tv1;

    private TextView state_value_tv2;

    private TextView state_value_tv3;

    private TextView state_value_tv4, state_mac;

    private String[] wifi_ips;

    private String[] wifi_gateways;

    private String[] wifi_masks;

    private String wifi_dns1 = "";

    private String wifi_dns2 = "";

    private String[] ethernet_ips;

    private String[] ethernet_gateways;

    private String[] ethernet_masks;

    private String ethernet_dns1 = "";

    private String ethernet_dns2 = "";

    private final String wire_mac, wireless_mac;
    private Activity mActivity;

    public NetstateAdapter(Context context, String[] addr_name_strings, int status,
            boolean isConnect) {
        ctvContext = context.getApplicationContext();
        this.mActivity = (Activity)context;
        this.addr_name_strings = addr_name_strings;
        this.status = status;
        this.isConnect = isConnect;
        wireless_mac = Tools.getWirelessMacAddress(ctvContext).toUpperCase()
                .replaceAll(":", "  :  ");
        wire_mac = Tools.getWireMacAddress(ctvContext).toUpperCase().replaceAll(":", "  :  ");
    }

    public void setConnect(boolean isConnect) {
        this.isConnect = isConnect;
        if (!isConnect) {
            status = NetUtils.TYPE_NONE;
        }
        notifyDataSetChanged();
    }
    public void onExit(){
        ctvContext = null;
        wifi_ips = null;
        wifi_gateways = null;
        wifi_masks = null;
        ethernet_ips = null;
        ethernet_gateways = null;
        ethernet_masks = null;
    }

    @Override
    public int getCount() {
        if (isConnect) {
            return addr_name_strings.length;
        } else {
            return 2;
        }
    }

    @Override
    public Object getItem(int index) {
        return addr_name_strings[index];
    }

    public void setEthernetDevInfo(LinkProperties linkProperties) {
        status = ConnectivityManager.TYPE_ETHERNET;
        try {
            List<LinkAddress> linkAddressList = NetUtils.getAllLinkAddresses(linkProperties);
            if(linkAddressList != null && linkAddressList.size() > 0){
                for (LinkAddress linkAddress : linkAddressList) {
                    InetAddress address = linkAddress.getAddress();
                    if (address instanceof Inet4Address) {
                        String ip = address.getHostAddress();
                        if (TextUtils.isEmpty(ip)) {
                            return;
                        }
                        ethernet_ips = ip.split("\\.");
                    }
                }
                if (ethernet_ips == null) {
                    return;
                }
                for (RouteInfo route : linkProperties.getRoutes()) {
                    if (route.isDefaultRoute()) {
                        ethernet_gateways = route.getGateway().getHostAddress().trim().split("\\.");
                        break;
                    }
                }
                ethernet_masks = new String[ethernet_ips.length];
                for (int i = 0; i < ethernet_ips.length; i++) {
                    ethernet_masks[i] = ethernet_ips[i].equals(ethernet_gateways[i]) ? "255" : "0";
                }
                Iterator<InetAddress> dnsIterator = linkProperties.getDnsServers().iterator();
                if (dnsIterator.hasNext()) {
                    ethernet_dns1 = dnsIterator.next().getHostAddress();
                }
                if (dnsIterator.hasNext()) {
                    ethernet_dns2 = dnsIterator.next().getHostAddress();
                }
                notifyDataSetChanged();
            }
        } catch (Exception e) {
            L.d(e.toString());
        }
    }

    public void setWifiDevInfo(LinkProperties linkProperties, WifiInfo wifiInfo) {
        int ip = wifiInfo.getIpAddress();
        String ipString = String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff),
                (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        Log.i(TAG, "-wifi_ip- :" + ipString);
        wifi_ips = ipString.split("\\.");
        for (RouteInfo route : linkProperties.getRoutes()) {
            if (route.isDefaultRoute()) {
                wifi_gateways = route.getGateway().getHostAddress().trim().split("\\.");
            }
        }
        if (wifi_ips == null) {
            return;
        }
        wifi_masks = new String[wifi_ips.length];
        for (int i = 0; i < wifi_ips.length; i++) {
            wifi_masks[i] = wifi_ips[i].equals(wifi_gateways[i]) ? "255" : "0";
        }
        Iterator<InetAddress> dnsIterator = linkProperties.getDnsServers().iterator();
        if (dnsIterator.hasNext()) {
            wifi_dns1 = dnsIterator.next().getHostAddress();
        }
        if (dnsIterator.hasNext()) {
            wifi_dns2 = dnsIterator.next().getHostAddress();
        }
        status = ConnectivityManager.TYPE_WIFI;
        notifyDataSetChanged();
    }

    public void setPppoeManager(PppoeManager pppoeManager) {
        this.pppoeManager = pppoeManager;
        status = NetstateViewHolder.TYPE_PPPOE;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View contentview, ViewGroup parent) {
        if (contentview == null) {
            contentview = LayoutInflater.from(mActivity).inflate(R.layout.page_net_state_item,
                    parent, false);
        }
        TextView state_item_tv = contentview.findViewById(R.id.state_item_tv);
        state_item_tv.setText(addr_name_strings[position]);
        state_value_tv1 = contentview.findViewById(R.id.state_value_tv1);
        state_value_tv2 = contentview.findViewById(R.id.state_value_tv2);
        state_value_tv3 = contentview.findViewById(R.id.state_value_tv3);
        state_value_tv4 = contentview.findViewById(R.id.state_value_tv4);
        state_mac = contentview.findViewById(R.id.state_mac);
        if (position == 0 || position == 1) {
            contentview.findViewById(R.id.state_point_1).setVisibility(View.INVISIBLE);
            contentview.findViewById(R.id.state_point_2).setVisibility(View.INVISIBLE);
            contentview.findViewById(R.id.state_point_3).setVisibility(View.INVISIBLE);
            if (position == 0) {
                state_mac.setText(wireless_mac);
            } else {
                state_mac.setText(wire_mac);
            }
            return contentview;
        }
        if (status == ConnectivityManager.TYPE_ETHERNET) {
            switch (position) {
                case 2:
                    setIpText(ethernet_ips);
                    break;
                case 3:
                    setIpText(ethernet_masks);
                    break;
                case 4:
                    setIpText(ethernet_gateways);
                    break;
                case 5:
                    if (!TextUtils.isEmpty(ethernet_dns1)) {
                        setIpText(ethernet_dns1);
                    }
                    break;
                case 6:
                    if (!TextUtils.isEmpty(ethernet_dns2)) {
                        setIpText(ethernet_dns2);
                    }
                    break;
                default:
                    break;
            }
        } else if (status == ConnectivityManager.TYPE_WIFI) {
            switch (position) {
                case 2:
                    setIpText(wifi_ips);
                    break;
                case 3:
                    setIpText(wifi_masks);
                    break;
                case 4:
                    setIpText(wifi_gateways);
                    break;
                case 5:
                    if (!TextUtils.isEmpty(wifi_dns1)) {
                        setIpText(wifi_dns1);
                    }
                    break;
                case 6:
                    if (!TextUtils.isEmpty(wifi_dns2)) {
                        setIpText(wifi_dns2);
                    }
                    break;
                default:
                    break;
            }
        } else if (status == NetstateViewHolder.TYPE_PPPOE) {
            switch (position) {
                case 2:
                    setIpText(pppoeManager.getIpaddr());
                    break;
                case 3:
                    setIpText(pppoeManager.getMask());
                    break;
                case 4:
                    setIpText(pppoeManager.getRoute());
                    break;
                case 5:
                    if (pppoeManager.getDns1() != null) {
                        setIpText(pppoeManager.getDns1());
                    }
                    break;
                case 6:
                    if (pppoeManager.getDns2() != null) {
                        setIpText(pppoeManager.getDns2());
                    }
                    break;
                default:
                    break;
            }
        }
        return contentview;
    }

    private void clearText() {
        state_value_tv1.setText("");
        state_value_tv2.setText("");
        state_value_tv3.setText("");
        state_value_tv4.setText("");
    }

    private void setIpText(String ip) {
        if (TextUtils.isEmpty(ip)) {
            clearText();
            return;
        }

        String[] ipArray = ip.split("\\.");

        if (ipArray == null || ipArray.length != 4) {
            clearText();
            return;
        }
        state_value_tv1.setText(ipArray[0]);
        state_value_tv2.setText(ipArray[1]);
        state_value_tv3.setText(ipArray[2]);
        state_value_tv4.setText(ipArray[3]);
    }

    private void setIpText(String[] ips) {
        if (ips == null || ips.length != 4) {
            clearText();
            return;
        }
        state_value_tv1.setText(ips[0]);
        state_value_tv2.setText(ips[1]);
        state_value_tv3.setText(ips[2]);
        state_value_tv4.setText(ips[3]);
    }
}
