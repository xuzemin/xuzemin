package com.ctv.newlauncher.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.ctv.ctvlauncher.R;

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
         /* if(isWifiConnected(context)){
              Log.d("NetWorkUtil","iswificonnected");
              if(isNetworkConnected(context)){
                  Log.d("NetWorkUtil","iswificonnected:avaivable:true");
                  return getWifiStrength(context);
              }else{
                  Log.d("NetWorkUtil","iswificonnected:avaivable:false");
                  return R.mipmap.net_no;
              }
          }else if(isEthernetConnected(context)){
              Log.d("NetWorkUtil","isEthernetConnected");
              if(isNetworkConnected(context)){
                  Log.d("NetWorkUtil","isEthernetConnected:avaivable:true");
                  return R.mipmap.net_eth;
              }else{
                  Log.d("NetWorkUtil","isEthernetConnected:avaivable:false");
                  return R.mipmap.net_no;
              }
          }
        return R.mipmap.net_no;*/
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
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = wifiInfo.getRssi();
        if (level <= 0 && level >= -50) {
            Log.d("NetWorkUtil", "getWifiStrength: -50");
            return R.mipmap.net_wifi_level_four;
        } else if (level < -50 && level >= -70) {
            Log.d("NetWorkUtil", "getWifiStrength: -70");

            return R.mipmap.net_wifi_level_three;
        } else if (level < -70 && level >= -80) {
            Log.d("NetWorkUtil", "getWifiStrength: -80");

            return R.mipmap.net_wifi_level_two;
        } else if (level < -80 && level >= -100) {
            Log.d("NetWorkUtil", "getWifiStrength: -100");

            return R.mipmap.net_wifi_level_one;
        } else {
            Log.d("NetWorkUtil", "getWifiStrength: 000");

            return R.mipmap.net_wifi;
        }
    }
}
