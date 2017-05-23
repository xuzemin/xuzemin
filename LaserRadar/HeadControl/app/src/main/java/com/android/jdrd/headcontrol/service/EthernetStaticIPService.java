package com.android.jdrd.headcontrol.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.jdrd.headcontrol.util.EthernetUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.List;

import static com.android.jdrd.headcontrol.util.EthernetUtils.getIPv4Address;


public class EthernetStaticIPService extends Service {
    private IntentFilter mWifiFilter;
    private IntentFilter mEthFilterr;
    private boolean isscaning = true;
    private List<ScanResult> list;
    private List<ScanResult> scanlist;
    private ConnectivityManager mConnectivityManager;
    private List<WifiConfiguration> wifiConfigurationList;
    private NetworkInfo networkInfo;
    private ScanResult scanResult;

    StaticIpConfiguration mStaticIpConfiguration;
    IpConfiguration mIpConfiguration;
    EthernetManager mEthManager;
    //以太网(有线)静态ip参数
    private  static String mEthIpAddress = "192.168.88.106";  //IP
    private  static String mEthNetmask = "255.255.255.0";  //  子网掩码
    private  static String mEthGateway = "192.168.88.1";   //网关
    private  static String mEthdns1 = "192.168.88.1";   // DNS1
    private  static String mEthdns2 = "202.96.134.133";   // DNS2

    @Override
    public void onCreate() {
        super.onCreate();
        registerEthernetWIFI();
        Log.e("zfg", "EthernetStaticIPService onCreate执行了");
        mConnectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void registerEthernetWIFI() {
        mEthFilterr = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mEthFilterr.addAction(EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        registerReceiver(mEthConnectReceiver, mEthFilterr);
    }

    private BroadcastReceiver mEthConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo.State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).getState();
                networkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (state.toString().equals("CONNECTED")) {
                    Toast.makeText(getApplicationContext(), "有线网络连接了", Toast.LENGTH_LONG).show();
                    //设置静态IP
                    setEthernetStaticIP();
                    isscaning = false;
                } else if (state.toString().equals("DISCONNECTED")) {
                    isscaning = true;
                    networkInfo = null;
                }
            }
        }
    };


    public  void setEthernetStaticIP() {
        mEthManager = (EthernetManager)this.getSystemService("ethernet");
        mStaticIpConfiguration = new StaticIpConfiguration();
        /*
		  * get ip address, netmask,dns ,gw etc.
		  */
        Inet4Address inetAddr = getIPv4Address(mEthIpAddress);
        int prefixLength = EthernetUtils.maskStr2InetMask(mEthNetmask);
        InetAddress gatewayAddr = getIPv4Address(mEthGateway);
        InetAddress dnsAddr = getIPv4Address(mEthdns1);
        if (inetAddr.getAddress().toString().isEmpty() || prefixLength ==0 || gatewayAddr.toString().isEmpty()
                || dnsAddr.toString().isEmpty()) {
            return;
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName("android.net.LinkAddress");
        } catch (Exception e) {
            // TODO: handle exception
        }
        Class[] cl = new Class[]{InetAddress.class, int.class};
        Constructor cons = null;
        //取得所有构造函数
        try {
            cons = clazz.getConstructor(cl);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        //给传入参数赋初值
        Object[] x = {inetAddr, prefixLength};
        String dnsStr2 = mEthdns2;
        //mStaticIpConfiguration.ipAddress = new LinkAddress(inetAddr, prefixLength);
        try {
            mStaticIpConfiguration.ipAddress = (LinkAddress) cons.newInstance(x);
            Log.d("232323", "chanson 1111111");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        mStaticIpConfiguration.gateway=gatewayAddr;
        mStaticIpConfiguration.dnsServers.add(dnsAddr);
        if (!dnsStr2.isEmpty()) {
            mStaticIpConfiguration.dnsServers.add(getIPv4Address(dnsStr2));
        }
        Log.d("2312321", "chanson mStaticIpConfiguration  ====" + mStaticIpConfiguration);
        mIpConfiguration = new IpConfiguration(IpConfiguration.IpAssignment.STATIC, IpConfiguration.ProxySettings.NONE, mStaticIpConfiguration, null);
        mEthManager.setConfiguration(mIpConfiguration);
    }



    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
