package com.android.wifi.socket.Activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.wifi.socket.wifisocket.R;
import com.android.wifi.socket.wifisocket.WifiAdmin;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private String TAG = "MainActivity";
    private WifiAdmin wifiadmin;
    private IntentFilter mWifiFilter;
    private List<ScanResult> list;
    private ScanResult scanResult;
    private List<WifiConfiguration> wifiConfigurationList;
    private ListView listView;
    private WifiAdapter wifiAdapter;
    private StringBuffer stringBuffer;
    private Switch wifi_state;
    private boolean isscaning = false;
    private int SCAN_OK = 0;
    private ConnectivityManager mConnectivityManager;
    private int CONNECT_SUCCES = 1;
    private int CONNECT_FAIL = 2;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0 :
                    wifiAdapter = new WifiAdapter(MainActivity.this);
                    listView.setAdapter(wifiAdapter);
                    break;
                case 1:

                    break;
                case 2:

                    break;
            }
        }
    };
    private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,""+intent.getAction());
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int message = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch (message) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.d(TAG, "WIFI_STATE_DISABLED");
                        wifi_state.setChecked(false);
                        thread_Wait();
                        break;
                    case WifiManager.WIFI_STATE_DISABLING:
                        Log.d(TAG, "WIFI_STATE_DISABLING");
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d(TAG, "WIFI_STATE_ENABLED");
                        wifi_state.setChecked(true);
                        start_Scan();
                        break;
                    case WifiManager.WIFI_STATE_ENABLING:
                        Log.d(TAG, "WIFI_STATE_ENABLING");
                        break;
                    case WifiManager.WIFI_STATE_UNKNOWN:
                        Log.d(TAG, "WIFI_STATE_UNKNOWN");
                        break;
                    default:
                        break;
                }
            }
            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                NetworkInfo.State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                Log.e(TAG,"state"+state);
                String str = "CONNECTED";
                Log.e(TAG,"state="+state+";str = "+str);
                if(state.toString().equals("CONNECTED")){
                    Log.e(TAG,"state="+true);
                    handler.sendEmptyMessage(CONNECT_SUCCES);
                }else if(state.toString().equals("DISCONNECTED")){
                    Log.e(TAG,"state="+false);
                    handler.sendEmptyMessage(CONNECT_FAIL);
                }else{
                    Log.e(TAG,"state="+state.toString());
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.wifi).setOnClickListener(this);
        wifi_state = (Switch)findViewById(R.id.wifistate);
        listView = (ListView)findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        registerWIFI();
        wifiadmin = new WifiAdmin(this);
        wifi_state.setChecked(false);
        wifi_state.setOnClickListener(this);
        mConnectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.wifistate:
                Log.e(TAG,""+wifi_state.isChecked());
                if(!wifi_state.isChecked()){
                    wifiadmin.closeWifi();
                    Log.e(TAG,"正在关闭Wifi");
                    Toast.makeText(this,"正在关闭Wifi",Toast.LENGTH_LONG).show();
                }else{
                    wifiadmin.openWifi();
                    Toast.makeText(this,"正在打开Wifi",Toast.LENGTH_LONG).show();
                    Log.e(TAG,"正在打开Wifi");
                }
                break;
            case R.id.wifi:
                startActivity(new Intent(MainActivity.this,WifControlActivity.class));
                break;
        }
    }
    private void registerWIFI() {
        mWifiFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiConnectReceiver, mWifiFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiConnectReceiver);
    }

    public void start_Scan() {
        isscaning = true;
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (isscaning) {
                    if (stringBuffer != null) {
                        stringBuffer = new StringBuffer();
                    }
                    //开始扫描网络
                    try {
                        Thread.sleep(3*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    wifiadmin.startScan();
                    list = wifiadmin.getWifiList();
                    if (list != null) {
                        handler.sendEmptyMessage(SCAN_OK);
                    }
                }
            }
        }.start();
    }

    public void thread_Wait(){
        isscaning = false;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        wifiConfigurationList = wifiadmin.getConfiguration();
        scanResult=list.get(i);
        int networkid = IsConfiguration(scanResult.SSID);
        if(-1 != networkid){
            if(ConnectWifi(networkid)){
                //打开缓冲动画
            }
        }else{
            inputTitleDialog(networkid);
        }
    }

    /**
     * wifi listview adapter
     */
    public class WifiAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public WifiAdapter(Context context){
            this.mInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();
                convertView = mInflater.inflate(R.layout.layout_wifilist, null);
                holder.title = (TextView)convertView.findViewById(R.id.textview);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }
            ScanResult scanResult = list.get(position);
            holder.title.setText(scanResult.SSID);
            return convertView;
        }
        public  class ViewHolder{
            public TextView title;
        }
    }

    /**
     * passwork is config ?
     * @param SSID
     * @return
     */
    public int IsConfiguration(String SSID){
        wifiConfigurationList = wifiadmin.getConfiguration();
        for(int i = 0; i < wifiConfigurationList.size(); i++){
            if(wifiConfigurationList.get(i).SSID.equals("\""+SSID+"\"")){//地址相同
                return wifiConfigurationList.get(i).networkId;
            }
        }
        return -1;
    }

    /**
     * config wifi passwork
     * @param ssid
     * @param pwd
     * @return
     */
    public int AddWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){
        int wifiId = -1;
        for(int i = 0;i < wifiList.size(); i++){
            ScanResult wifi = wifiList.get(i);
            Log.e(TAG,"wifi.SSID ="+wifi.SSID + "ssid="+ssid);
            if(wifi.SSID.equals(ssid)){
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.SSID = "\""+wifi.SSID+"\"";//\"转义字符，代表"
                wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK密码
                wifiCong.hiddenSSID = false;
                wifiCong.status = WifiConfiguration.Status.ENABLED;
                wifiId = wifiadmin.getWifiManager().addNetwork(wifiCong);
                if(wifiId != -1){
                    return wifiId;
                }
                wifiConfigurationList = wifiadmin.getConfiguration();
            }
        }
        return wifiId;
    }

    /**
     * input passwork
     */
    private void inputTitleDialog(int networkid) {
        Toast.makeText(this,"请输入密码",Toast.LENGTH_LONG).show();
        final EditText inputServer = new EditText(this);
        inputServer.setFocusable(true);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.pass_edit_Title)).setView(inputServer).setNegativeButton(
                getString(R.string.cancel), null);
        builder.setPositiveButton(getString(R.string.connect),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String inputName = inputServer.getText().toString();
                        Log.e(TAG,"inputTitleDialog"+inputName);
                        int add_config = AddWifiConfig(list,scanResult.SSID,inputName);
                        if(-1!= add_config ){
                            new Thread(){
                                @Override
                                public void run() {
                                    super.run();
                                    int networkid = IsConfiguration(scanResult.SSID);
                                    while(-1 == networkid){
                                        thread(3000);
                                        networkid = IsConfiguration(scanResult.SSID);
                                    };
                                    ConnectWifi(networkid);
                                    //启动缓冲动画
                                }
                            }.start();
                            Log.e(TAG,"正在连接");
                        }else{
                            Log.e(TAG,"设置失败");
                        }
                    }
                });
        builder.show();
    }

    /**
     * connect wifi
     * @param wifiId
     * @return
     */
    public boolean ConnectWifi(int wifiId){
        wifiConfigurationList = wifiadmin.getConfiguration();
        Toast.makeText(this,"正在连接",Toast.LENGTH_LONG).show();
        for(int i = 0; i < wifiConfigurationList.size(); i++){
            WifiConfiguration wifi = wifiConfigurationList.get(i);
            Log.e(TAG,"wifi.networkId "+wifi.SSID+":"+wifi.networkId);
            Log.i("ConnectWifi",String.valueOf(wifi.status));
            if(wifi.networkId == wifiId){
                while(!(wifiadmin.getWifiManager().enableNetwork(wifiId, true))){
                    Log.i("ConnectWifi",String.valueOf(wifiConfigurationList.get(wifiId).status));
                }
                return true;
            }
        }
        return false;
    }
    private void thread(int t ){
        try {
            Thread.sleep(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
