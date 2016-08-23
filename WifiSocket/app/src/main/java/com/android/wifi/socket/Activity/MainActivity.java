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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wifi.socket.service.WifiConnectService;
import com.android.wifi.socket.wifisocket.R;
import com.android.wifi.socket.wifisocket.WifiAdmin;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,AdapterView.OnItemClickListener{
    private String TAG = "MainActivity";
    private WifiAdmin wifiadmin;
    private IntentFilter mWifiFilter;
    private String passwork = "13662282";
    private String wifiname = "";
    private String lastSSID,CurrentSSID;

    private List<ScanResult> list;
    private TextView connect_wifi;
    private NetworkInfo networkInfo;
    private List<ScanResult> scanlist;
    private RelativeLayout conntect_layout;
    private ScanResult scanResult;
    private Intent intent;
    private List<WifiConfiguration> wifiConfigurationList;
    private ListView listView;
    private WifiAdapter wifiAdapter;
    private StringBuffer stringBuffer;
    private Switch wifi_state;
    private long exitTime = 0;
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
                    wifiAdapter = new WifiAdapter(MainActivity.this,scanlist);
                    listView.setAdapter(wifiAdapter);
                    break;
                case 1:
                    conntect_layout.setVisibility(View.VISIBLE);
                    if(networkInfo!=null) {
                        String str = networkInfo.getExtraInfo();
                        connect_wifi.setText(str.substring(1,str.length()-1));
                    }
                    break;
                case 2:
                    connect_wifi.setText("");
                    conntect_layout.setVisibility(View.GONE);
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
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d(TAG, "WIFI_STATE_ENABLED");
                        wifi_state.setChecked(true);
                        start_Scan();
                        break;
                    default:
                        break;
                }
            }
            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                NetworkInfo.State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                networkInfo = mConnectivityManager.getActiveNetworkInfo();
                if(state.toString().equals("CONNECTED")){
                    if(networkInfo !=null && CurrentSSID!=null){
                        if((CurrentSSID).equals(networkInfo.getExtraInfo())){
                            Toast.makeText(MainActivity.this,"指定网络连接成功",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MainActivity.this,WifControlActivity.class));
                            if(!lastSSID.equals(CurrentSSID)) {
                                deletepasswork(lastSSID);
                                lastSSID = CurrentSSID;
                            }
                        }
                    }else{
                        Toast.makeText(MainActivity.this,"非指定网络连接",Toast.LENGTH_LONG).show();
                    }
                    handler.sendEmptyMessage(CONNECT_SUCCES);
                }else if(state.toString().equals("DISCONNECTED")){
                    Toast.makeText(MainActivity.this,"网络断开",Toast.LENGTH_LONG).show();
                    handler.sendEmptyMessage(CONNECT_FAIL);
                    networkInfo = null;
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
        connect_wifi = (TextView) findViewById(R.id.connected_wifi);
        conntect_layout = (RelativeLayout) findViewById(R.id.conntect_layout);
        conntect_layout.setVisibility(View.INVISIBLE);
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
                    Toast.makeText(this,"正在关闭Wifi",Toast.LENGTH_LONG).show();
                    wifi_state.setClickable(false);
                    listView.setVisibility(View.INVISIBLE);
                    listView.setEnabled(false);
                    wifiadmin.closeWifi();
                    wifi_state.setClickable(true);
                }else{
                    wifi_state.setClickable(false);
                    Toast.makeText(this,"正在打开Wifi",Toast.LENGTH_LONG).show();
                    wifiadmin.openWifi();
                    listView.setEnabled(true);
                    listView.setVisibility(View.VISIBLE);
                    wifi_state.setClickable(true);
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
    }

    public void startservice(){
        if(CurrentSSID!=null){
            int IsInWIfiConfig = NetinfoConfiguration(CurrentSSID);
            Log.e(TAG,"IsInWIfiConfig"+IsInWIfiConfig);
            Bundle bundle = new Bundle();
            bundle.putInt("IsInWIfiConfig",IsInWIfiConfig);
            intent=new Intent(this,WifiConnectService.class);
            intent.putExtras(bundle);
            startService(intent);
        }
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
                    getshowlist();
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
        scanResult=scanlist.get(i);
        int isConfiguration = IsConfiguration(scanResult.SSID);
        Log.e(TAG,"点击");
        CurrentSSID = "\""+scanResult.SSID+"\"";
        if(!CurrentSSID.equals(lastSSID)){
            deletepasswork(lastSSID);
            lastSSID = CurrentSSID;
        }
        if(-1 != isConfiguration){
            Log.e(TAG,"已有密码");
            if(ConnectWifi(isConfiguration)){
                //打开缓冲动画
            }
        }else{
            Log.e(TAG,"设置密码");
            int add_config = AddWifiConfig(list,scanResult.SSID,passwork);
            Log.e(TAG,"add_config增加密码结果"+add_config);
            if(-1!= add_config ){
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        while(-1 == IsConfiguration(scanResult.SSID)){
                            thread(2*1000);
                        }
                        Log.e(TAG,"增加密码成功后id"+IsConfiguration(scanResult.SSID));
                        ConnectWifi(IsConfiguration(scanResult.SSID));
                        //启动缓冲动画
                    }
                }.start();
            }else{
                Log.e(TAG,"设置失败");
            }
        }
    }

    /**
     * wifi listview adapter
     */
    public class WifiAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private List<ScanResult> list;
        public WifiAdapter(Context context,List<ScanResult> list){
            this.mInflater = LayoutInflater.from(context);
            this.list = list;
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

    public int IsConfiguration(String SSID){
        wifiConfigurationList = wifiadmin.getConfiguration();
        if(wifiConfigurationList!=null && wifiConfigurationList.size() > 0) {
            for (int i = 0; i < wifiConfigurationList.size(); i++) {
                if (wifiConfigurationList.get(i).SSID.equals("\"" + SSID + "\"")) {//地址相同
                    return wifiConfigurationList.get(i).networkId;
                }
            }
        }
        return -1;
    }
    public int NetinfoConfiguration(String SSID){
        wifiConfigurationList = wifiadmin.getConfiguration();
        if(wifiConfigurationList!=null && wifiConfigurationList.size() > 0) {
            for (int i = 0; i < wifiConfigurationList.size(); i++) {
                if (wifiConfigurationList.get(i).SSID.equals(SSID)) {//地址相同
                    return wifiConfigurationList.get(i).networkId;
                }
            }
        }
        return -1;
    }

    public int AddWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){
        int wifiId = -1;
        for(int i = 0;i < wifiList.size(); i++){
            ScanResult wifi = wifiList.get(i);
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
    public void deleteWifiConfig(int networkId){
        boolean flag = wifiadmin.getWifiManager().removeNetwork(networkId);
        wifiConfigurationList = wifiadmin.getConfiguration();
    }

    /**
     * connect wifi
     * @param wifiId
     * @return
     */
    public boolean ConnectWifi(int wifiId){
        wifiConfigurationList = wifiadmin.getConfiguration();
        for(int i = 0; i < wifiConfigurationList.size(); i++){
            WifiConfiguration wifi = wifiConfigurationList.get(i);
            if(wifi.networkId == wifiId){
                Log.e(TAG,"wifi.status"+wifi.status);
                if(wifi.status == 0){
                    Log.e(TAG,"正在连接");
//                    Toast.makeText(this,"正在连接",Toast.LENGTH_LONG).show();
                    wifiadmin.getWifiManager().enableNetwork(wifiId, true);
                    return true;
                }else if(wifi.status == 1 ){
                    Log.e(TAG,"无法连接");
                    wifiadmin.getWifiManager().enableNetwork(wifiId, true);
//                    Toast.makeText(this,"无法连接",Toast.LENGTH_LONG).show();
                    return true;
                }else if(wifi.status == 2){
                    Log.e(TAG,"已经连接");
                    wifiadmin.getWifiManager().enableNetwork(wifiId, true);
//                    while(!(wifiadmin.getWifiManager().enableNetwork(wifiId, true))){
//                        //status:0--已经连接，1--不可连接，2--可以连接
//                    }
//                    Toast.makeText(this,"已经连接",Toast.LENGTH_LONG).show();
                    return false;
                }else{
                    wifiadmin.getWifiManager().enableNetwork(wifiId, true);

                }
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

    public void getshowlist(){
        scanlist = new ArrayList<>();
        for(int i = 0 ; i < list.size();i++){
            if(list.get(i).SSID.length() > 4) {
                String SSID = list.get(i).SSID.substring(0, 0);
                if (SSID.equals(wifiname)) {
                    scanlist.add(list.get(i));
                }
            }
        }
    }

    public void deletepasswork(String SSID){
        String deleteSSID = SSID;
        if(deleteSSID!=null) {
            int IsInWIfiConfig = NetinfoConfiguration(deleteSSID);
            if(-1!=IsInWIfiConfig){
                deleteWifiConfig(IsInWIfiConfig);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Log.e(TAG,"lastSSID"+lastSSID);
                Log.e(TAG,"CurrentSSID"+CurrentSSID);
//                deletepasswork(lastSSID);
//                deletepasswork(CurrentSSID);

                startservice();
                unregisterReceiver(mWifiConnectReceiver);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
