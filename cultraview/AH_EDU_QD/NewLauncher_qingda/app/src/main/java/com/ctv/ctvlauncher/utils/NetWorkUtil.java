package com.ctv.ctvlauncher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ctv.ctvlauncher.R;

import java.util.List;

public class NetWorkUtil {




    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public static boolean isEthernetConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mEthernetNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
            if (mEthernetNetworkInfo != null) {
                return mEthernetNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int checkNetWorkType(Context context){
        Log.d("NetWorkUtil","checkNetWorkState");
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if(activeNetwork == null){
            Log.d("NetWorkUtil","checkNetWorkState:type:"+0);
            return 0;
        }else{
            Log.d("NetWorkUtil","checkNetWorkState:type:"+activeNetwork.getType());
            return activeNetwork.getType();
        }
    }
    public static int  checkNetWorkState(Context context){
        int pic = 0;
        Log.d("NetWorkUtil","checkNetWorkState");
        switch (checkNetWorkType(context)){
            case 0:
                pic = R.mipmap.net_no;
                break;
            case ConnectivityManager.TYPE_ETHERNET:
                if(isNetworkConnected(context)){
                    Log.d("NetWorkUtil","isEthernetConnected:avaivable:true");
                    pic = R.mipmap.net_eth;
                }else{
                    Log.d("NetWorkUtil","isEthernetConnected:avaivable:false");
                    pic = R.mipmap.net_no;
                }
                break;
            case ConnectivityManager.TYPE_WIFI:
                if(isNetworkConnected(context)){
                    Log.d("NetWorkUtil","iswificonnected:avaivable:true");
                    pic = getWifiStrength(context);
                }else{
                    Log.d("NetWorkUtil","iswificonnected:avaivable:false");
                    pic = R.mipmap.net_no;
                }
                break;
            default:
                if(isNetworkConnected(context)){
                    Log.d("NetWorkUtil","isEthernetConnected:avaivable:true");
                    pic = R.mipmap.net_eth;
                }else{
                    Log.d("NetWorkUtil","isEthernetConnected:avaivable:false");
                    pic = R.mipmap.net_no;
                }
                break;
        }
        return pic;

    }

    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getWifiStrength(Context context) {
         WifiInfo wifiInfo;
         String ssid1;
        int hhclevel;
         ScanResult Currentscanresout = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();

        ssid1 = wifiInfo.getSSID();
    //    int level = wifiInfo.getRssi();
      //  WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<ScanResult> scanResults = wifiManager.getScanResults();
        for (ScanResult results:scanResults){
            if (ssid1.contains(results.SSID)){
                if (!results.SSID.isEmpty()){
                    Currentscanresout = results;
                    Log.d("NetWorkUtil", "CurrentWifi:  Currentscanresout="+results.SSID);

                }
                Log.d("NetWorkUtil", "CurrentWifi:  results.SSID = kong");

            }


        }


        // scanResults.clear();
   //     wifiManager.getScanResults().clear();
    //    List<ScanResult> scanResults = wifiManager.getScanResults();
      //  String scanResult2 = scanResults.get(1).SSID;

    //    int i = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
      //  int hhclevel = WifiManager.calculateSignalLevel(scanResults.get(0).level, 4);
        if (Currentscanresout == null){
            hhclevel = 0;
        }else {
            hhclevel = WifiManager.calculateSignalLevel(Currentscanresout.level, 4);

        }
//        Log.d("NetWorkUtil", "getWifiStrength: level ="+level + "  ssid1= "+ ssid1);
//
//        Log.d("NetWorkUtil", "getWifiStrength: i ="+i+ " " +
//                " scanResults.size ="+scanResults.size() + "   Currentscanresout ="+Currentscanresout.SSID +"   hhclevel ="+hhclevel
//       );
        int lel =hhclevel+1;
        if (lel==4) {
            Log.d("NetWorkUtil", "getWifiStrength: 4");
            return R.mipmap.net_wifi_level_four;
        } else if (lel == 3) {
            Log.d("NetWorkUtil", "getWifiStrength: 3");

            return R.mipmap.net_wifi_level_three;
        } else if (lel == 2) {
            Log.d("NetWorkUtil", "getWifiStrength: 2");

            return R.mipmap.net_wifi_level_two;
        } else if (lel == 1) {
            Log.d("NetWorkUtil", "getWifiStrength: 1");

            return R.mipmap.net_wifi_level_one;
        } else {
            Log.d("NetWorkUtil", "getWifiStrength: 000");

            return R.mipmap.net_wifi;

        }
    }



}
