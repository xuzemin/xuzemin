package com.ys.setstaticip;

import android.app.Activity;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.LinkAddress;
import android.net.StaticIpConfiguration;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.Inet4Address;
import java.net.InetAddress;

import static com.ys.setstaticip.NetUtils.getIPv4Address;

public class MainActivity extends Activity {


    StaticIpConfiguration mStaticIpConfiguration;

    IpConfiguration mIpConfiguration;
    EthernetManager mEthManager;

    private  static String mEthIpAddress = "192.168.88.154";  //IP
    private  static String mEthNetmask = "255.255.255.0";  //  子网掩码
    private  static String mEthGateway = "192.168.88.1";   //网关
    private  static String mEthdns1 = "8.8.8.8";   // DNS1
    private  static String mEthdns2 = "8.8.4.4";   // DNS2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setStaticIP();
    }

    private void setStaticIP() {
        mEthManager = (EthernetManager) getSystemService("ethernet");
        mStaticIpConfiguration = new StaticIpConfiguration();
        /*
		  * get ip address, netmask,dns ,gw etc.
		  */
        Inet4Address inetAddr = getIPv4Address(mEthIpAddress);
        int prefixLength = NetUtils.maskStr2InetMask(mEthNetmask);
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
}
