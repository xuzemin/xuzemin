package com.android.wifi.socket.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.wifi.socket.Activity.MainActivity;
import com.android.wifi.socket.recieve.WifiBroadcastReciever;
import com.android.wifi.socket.wifisocket.R;
import com.android.wifi.socket.wifisocket.WifiAdmin;

/**
 * Created by xuzemin on 16/8/20.
 */
public class WifiConnectService extends Service {
    private String TAG = "WifiConnectService";
    private WifiAdmin wifiAdmin;
    private ConnectivityManager connectivityManager;
    private int CurrentSSID = 0;
    private Boolean remove_;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo.State state = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                Log.e(TAG,"state.toString() = "+ state.toString());
                if(state.toString().equals("CONNECTED")){
                    Log.e(TAG, "CONNECTED");
                }else if(state.toString().equals("DISCONNECTED")){
                    Log.e(TAG, "Delete CurrentSSID");
                    wifiAdmin = new WifiAdmin(context);
                    if(CurrentSSID!=0 && remove_) {
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
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        Notification noti = new Notification.Builder(this)
                .setContentTitle("Title")
                .setContentText("Message")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(12346, noti);

        flags = Service.START_STICKY;
        Bundle bundle = intent.getExtras();
        CurrentSSID = bundle.getInt("IsInWIfiConfig");
        Log.e(TAG, "CurrentSSID=" + CurrentSSID);

        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);

        Intent intent = new Intent(this, WifiBroadcastReciever.class);
        Bundle bundle = new Bundle();
        bundle.putInt("IsInWIfiConfig",CurrentSSID);
        intent.putExtras(bundle);
        intent.setAction("arui.alarm.action");
        PendingIntent sender = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        long firstime = SystemClock.elapsedRealtime();
        AlarmManager am = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);

        // 10秒一个周期，不停的发送广播
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime,
                10 * 1000, sender);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

        Bundle bundle = new Bundle();
        bundle.putInt("IsInWIfiConfig",CurrentSSID);
        Intent intent=new Intent(this,WifiConnectService.class);
        intent.putExtras(bundle);
        startService(intent);
    }
    public void deleteWifiConfig(int networkId){
        boolean flag = wifiAdmin.getWifiManager().removeNetwork(networkId);
        Log.e(TAG,"flag= " +flag+";networkid = "+ networkId);
        Log.e(TAG,"CurrentSSID="+CurrentSSID);
        if(flag){
            remove_ =false;
        }
    }
    public class MyBinder extends Binder {

        public WifiConnectService getService(){
            return WifiConnectService.this;
        }
    }

    private MyBinder myBinder = new MyBinder();

    public void MyMethod(){
        for(int i = 0; i < 100; i++)
        {
            Log.i(TAG, "BindService-->MyMethod()");
        }
    }
}
