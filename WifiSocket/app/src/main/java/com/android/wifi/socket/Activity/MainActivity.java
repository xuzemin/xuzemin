package com.android.wifi.socket.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.android.wifi.socket.util.timeUtils;
import com.android.wifi.socket.wifisocket.R;
import com.android.wifi.socket.wifisocket.WifiAdmin;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseActivity implements View.OnClickListener,AdapterView.OnItemClickListener,View.OnTouchListener{
    private String TAG = "MainActivity";
    private int Server_HOST_PORT = 58888;
    private int Client_HOST_PORT = 58000;
    private String IP = "192.168.1.122";
    private WifiAdmin wifiadmin;
    private IntentFilter mWifiFilter;
    private String passwork = "13662282";
    private String wifiname = "";
    private String lastSSID = null;
    private String CurrentSSID = null;
    public Button connect,cancel,up,down,left,right;
    private List<ScanResult> list;
    private DatagramSocket socket_;
    private NetworkInfo networkInfo;
    private List<ScanResult> scanlist;
    private LinearLayout linearLayout_wifilist,linearLayout_control;
    private ScanResult scanResult;
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
    private int STARTCONNECT = 2;
    private int OUTOFTIME = 3;
    private int RECIEVE = 4;
    private Timer timer;
    private boolean isOutoftime = false;
    private TimerTask task ;
    private boolean isdeleted = false;

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
                    resetTimeTask();
                    showProgress();
                    connect();
                    isOutoftime = false;
                    timer = new Timer(true);
                    timer.schedule(task,5*1000);
                    cancel.setText("断开");
                    connect.setEnabled(false);
                    break;
                case 2:
                    resetTimeTask2();
                    timer = new Timer(true);
                    timer.schedule(task,5*1000);
                    break;
                case 3:
                    hideProgress();
                    Log.e(TAG,"连接超时");
                    isOutoftime = true;
                    cancel.setText("取消");
                    connect.setEnabled(true);
                    removeButton();
                    senddata("cancel");
                    Toast.makeText(MainActivity.this,"连接超时",Toast.LENGTH_LONG).show();
                    break;
                case 4:
                    hideProgress();
                    if(!isOutoftime){
                        if(msg.obj.equals("get")|| msg.obj.equals("heartbeat")){
                            showButton();
                            resetTimeTask();
                            timer = new Timer(true);
                            timer.schedule(task,5*1000);
                        }else if (msg.obj.equals("isconnected")){
                            removeButton();
                            Toast.makeText(MainActivity.this,"已有设备连接",Toast.LENGTH_LONG).show();
                        }
                    }
                    break;
            }
        }
    };
    public void resetTimeTask(){
        if (task != null){
            task.cancel();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                senddata("heartbeat");
                handler.sendEmptyMessage(STARTCONNECT);
            }
        };
    }
    public void resetTimeTask2() {
        if (task != null) {
            task.cancel();
        }
        task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(OUTOFTIME);
            }
        };
    }
    private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,""+intent.getAction());
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int message = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch (message) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.e(TAG,"CurrentSSID"+CurrentSSID);
                        hideProgress();
                        if(CurrentSSID != null){
                            isdeleted = true;
                            Log.e(TAG,"CurrentSSID"+CurrentSSID);
                            wifiadmin.openWifi();
                        }
                        Log.d(TAG, "WIFI_STATE_DISABLED");
                        thread_Wait();
                        wifi_state.setChecked(false);
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        hideProgress();
                        deletepass();
                        Log.d(TAG, "WIFI_STATE_ENABLED");
                        wifi_state.setChecked(true);
                        start_Scan();
                        break;
                    default:
                        break;
                }
            }

            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                hideProgress();
                NetworkInfo.State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                networkInfo = mConnectivityManager.getActiveNetworkInfo();
                Log.e(TAG,"isdeleted"+isdeleted);
                if(isdeleted){
                    deletepass();
                    isdeleted = false;
                }
                if(state.toString().equals("CONNECTED")){
                    if(networkInfo !=null && CurrentSSID!=null){
                        if((CurrentSSID).equals(networkInfo.getExtraInfo())){
                            Toast.makeText(MainActivity.this,"指定网络连接成功",Toast.LENGTH_LONG).show();
                            linearLayout_wifilist.setVisibility(View.GONE);
                            linearLayout_control.setVisibility(View.VISIBLE);
                            if(!lastSSID.equals(CurrentSSID)) {
                                deletepasswork(lastSSID);
                                lastSSID = CurrentSSID;
                            }
                            handler.sendEmptyMessage(CONNECT_SUCCES);
                        }
                    }else{
                        deletepass();
                        linearLayout_wifilist.setVisibility(View.VISIBLE);
                        linearLayout_control.setVisibility(View.GONE);
                        Toast.makeText(MainActivity.this,"非指定网络连接",Toast.LENGTH_LONG).show();
                    }
                }else if(state.toString().equals("DISCONNECTED")){
                    deletepass();
                    linearLayout_wifilist.setVisibility(View.VISIBLE);
                    linearLayout_control.setVisibility(View.GONE);
                    networkInfo = null;
                }else{
                    deletepass();
                    Log.e(TAG,"state="+state.toString());
                }
            }
        }
    };
    public void deletepass(){
        if(deletepasswork(CurrentSSID)){
            CurrentSSID = null;
        }
        if(deletepasswork(lastSSID)){
            lastSSID = null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();
        connect.setEnabled(false);
        cancel.setText("断开");
        linearLayout_wifilist.setVisibility(View.VISIBLE);
        linearLayout_control.setVisibility(View.GONE);
        removeButton();
    }

    public void init(){
        wifi_state = (Switch)findViewById(R.id.wifistate);
        listView = (ListView)findViewById(R.id.listview);
        listView.setOnItemClickListener(this);
        registerWIFI();
        linearLayout_wifilist = (LinearLayout) findViewById(R.id.layout_wifilist);
        linearLayout_control = (LinearLayout)findViewById(R.id.layout_control);
        wifiadmin = new WifiAdmin(this);
        wifi_state.setChecked(false);
        wifi_state.setOnClickListener(this);
        mConnectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        connect = (Button) findViewById(R.id.button_connect);
        cancel = (Button) findViewById(R.id.button_cancel);
        connect.setOnClickListener(this);
        cancel.setOnClickListener(this);
        up = (Button) findViewById(R.id.up);
        up.setOnTouchListener(this);
        down = (Button) findViewById(R.id.down);
        down.setOnTouchListener(this);
        left = (Button) findViewById(R.id.left);
        left.setOnTouchListener(this);
        right = (Button) findViewById(R.id.right);
        right.setOnTouchListener(this);
    }

    @Override
    public void onClick(View view) {
        if (timeUtils.isFastDoubleClick()) {
            return ;
        }
        switch (view.getId()){
            case R.id.wifistate:
                senddata("cancel");
                showProgress();
                Log.e(TAG,""+wifi_state.isChecked());
                if(!wifi_state.isChecked()){
                    Toast.makeText(this,"正在关闭Wifi",Toast.LENGTH_LONG).show();
                    deletepass();
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
            case R.id.button_connect:
                resetTimeTask();
                showProgress();
                isOutoftime = false;
                timer = new Timer(true);
                timer.schedule(task,5*1000);

                senddata("connect");
                connect.setEnabled(false);
                cancel.setText("断开");
                break;
            case R.id.button_cancel:
                senddata("cancel");
                deletepass();
                cancel.setText("取消");
                connect.setEnabled(true);
                break;
        }
    }
    public void removeButton(){
        up.setVisibility(View.INVISIBLE);
        down.setVisibility(View.INVISIBLE);
        left.setVisibility(View.INVISIBLE);
        right.setVisibility(View.INVISIBLE);
    }
    public void showButton(){
        up.setVisibility(View.VISIBLE);
        down.setVisibility(View.VISIBLE);
        left.setVisibility(View.VISIBLE);
        right.setVisibility(View.VISIBLE);
    }
    private void registerWIFI() {
        mWifiFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiConnectReceiver, mWifiFilter);
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
        showProgress();
        int isConfiguration = IsConfiguration(scanResult.SSID);
        Log.e(TAG,"点击");
        CurrentSSID = "\""+scanResult.SSID+"\"";
        if(!CurrentSSID.equals(lastSSID)){
            deletepasswork(lastSSID);
            lastSSID = CurrentSSID;
        }
        if(-1 != isConfiguration){
            Log.e(TAG,"已有密码");
            hideProgress();
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
                hideProgress();
                Toast.makeText(this,"",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.up:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    senddata("up");
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    senddata("stop");
                }
                break;
            case R.id.down:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    senddata("down");
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    senddata("stop");
                }
                break;
            case R.id.left:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    senddata("left");
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    senddata("stop");
                }
                break;
            case R.id.right:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    senddata("down");
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    senddata("stop");
                }
                break;
        }
        return true;
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
    public boolean deleteWifiConfig(int networkId){
        boolean flag = wifiadmin.getWifiManager().removeNetwork(networkId);
        return flag;
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
        hideProgress();
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

    public boolean deletepasswork(String SSID){
        String deleteSSID = SSID;
        if(deleteSSID!=null) {
            int IsInWIfiConfig = NetinfoConfiguration(deleteSSID);
            if(-1!=IsInWIfiConfig){
                return deleteWifiConfig(IsInWIfiConfig);
            }
        }
        return false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis()-exitTime) > 2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                deletepass();
                unregisterReceiver(mWifiConnectReceiver);
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        deletepass();
    }

    public void connect(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    socket_ = new DatagramSocket(Client_HOST_PORT);
                    String str = "connect";
                    byte data[] = str.getBytes();
                    DatagramPacket package_udp = new DatagramPacket(data, data.length, InetAddress.getByName(IP),Server_HOST_PORT);
                    socket_.send(package_udp);
                    while(true) {
                        ReceiveServerSocketData();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }

            }
        }.start();
    }
    public void ReceiveServerSocketData() {
        try {
            //实例化的端口号要和发送时的socket一致，否则收不到data
            Log.e(TAG, "接受开始");
            byte data[] = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket_.receive(packet);
            //把接收到的data转换为String字符串
            String result = new String(packet.getData(), packet.getOffset(),
                    packet.getLength());
            Log.e(TAG, "收到的数据"+result.toString());
            hideProgress();
            Message msg = new Message();
            msg.what = RECIEVE;
            msg.obj = result;
            handler.sendMessage(msg);
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

    }
    public void senddata(final String str){
        new Thread() {
            @Override
            public void run() {
                super.run();
                byte data[] = str.getBytes();
                try {
                    if (socket_ != null){
                        DatagramPacket packa = new DatagramPacket(data, data.length, InetAddress.getByName(IP), Server_HOST_PORT);
                        socket_.send(packa);
                    }
                }catch (UnknownHostException e ){
                    Log.e(TAG, e.toString());
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }.start();
    }
}

