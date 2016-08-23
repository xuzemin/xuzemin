package com.android.wifi.socket.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.wifi.socket.wifisocket.WifiAdmin;

/**
 * Created by xuzemin on 16/8/20.
 */
public class WifiConnectService extends Service {
    private String TAG = "WifiConnectService";
    private WifiAdmin wifiAdmin;
    private ConnectivityManager connectivityManager;
    private int CurrentSSID = 0;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                Log.e(TAG,"state.toString() = "+ state.toString());
                if(state.toString().equals("CONNECTED")){
                    Toast.makeText(context,"CONNECTED",Toast.LENGTH_LONG).show();
                    Log.e(TAG, "CONNECTED");
                }else if(state.toString().equals("DISCONNECTED")){
                    Log.e(TAG, "Delete CurrentSSID");
                    Toast.makeText(context,"DISCONNECTED",Toast.LENGTH_LONG).show();
                    wifiAdmin = new WifiAdmin(context);
                    if(CurrentSSID!=0) {
                        deleteWifiConfig(CurrentSSID);
                    }
                }else{
                    Log.e(TAG,"state="+state.toString());
                }
            }
        }
    };
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_REDELIVER_INTENT;
        Bundle bundle = intent.getExtras();
        CurrentSSID = bundle.getInt("IsInWIfiConfig");
        Log.e(TAG, "CurrentSSID=" + CurrentSSID);
        if(CurrentSSID==0){
            this.stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    public void deleteWifiConfig(int networkId){
        boolean flag = wifiAdmin.getWifiManager().removeNetwork(networkId);
        Log.e(TAG,"flag= " +flag+";networkid = "+ networkId);
        Log.e(TAG,"CurrentSSID="+CurrentSSID);
//        this.stopService(new Intent(this,WifiConnectService.class));
        this.stopSelf();
    }
}
