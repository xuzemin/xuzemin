package com.android.wifi.socket.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.android.wifi.socket.util.timeUtils;
import com.android.wifi.socket.wifisocket.R;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by xuzemin on 16/8/15.
 */
public class WifControlActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {
    private static String TAG = "WifControlActivity";
    private int Server_HOST_PORT = 58888;
    private int Client_HOST_PORT = 58000;
    private String IP = "192.168.18.111";
    private int CONNECT_OK = 0;
    private IntentFilter mWifiFilter;
    private DatagramSocket socket_;
    public Button connect,cancel,up,down,left,right;
    private ConnectivityManager mConnectivityManager;
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:

                    break;
                case 1:
                    connect.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (timeUtils.isFastDoubleClick()) {
            return ;
        }
        switch (view.getId()) {
            case R.id.button_connect:
                send_connect();
                connect.setEnabled(false);
                cancel.setText("断开");
                break;
            case R.id.button_cancel:
                cancel.setText("取消");
                connect.setEnabled(true);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.up:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    send_up();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    send_stop();
                }
                break;
            case R.id.down:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    send_down();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    send_stop();
                }
                break;
            case R.id.left:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    send_left();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    send_stop();
                }
                break;
            case R.id.right:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    send_right();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    send_stop();
                }
                break;
        }
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_layout);
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
        registerWIFI();
        connect.setEnabled(false);
        cancel.setText("断开");
        mConnectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public void toastText(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiConnectReceiver);
    }
    private void connect(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    socket_ = new DatagramSocket(Client_HOST_PORT);
                    String str = "connect";
                    byte data[] = str.getBytes();
                    DatagramPacket package_udp = new DatagramPacket(data, data.length,InetAddress.getByName(IP),Server_HOST_PORT);
                    socket_.send(package_udp);
                    handle.sendEmptyMessage(CONNECT_OK);
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
    private void send_connect() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "connect";
                senddata(str);
            }
        }.start();
    }
    private void send_up() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "up";
                senddata(str);
            }
        }.start();
    }
    private void send_down() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "down";
                senddata(str);
            }
        }.start();
    }
    private void send_left() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "left";
                senddata(str);
            }
        }.start();
    }
    private void send_right() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "right";
                senddata(str);
            }
        }.start();
    }
    private void send_stop() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "stop";
                senddata(str);
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
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }
    private void senddata(String str){
        byte data[] = str.getBytes();
        try {
            DatagramPacket packa = new DatagramPacket(data, data.length, InetAddress.getByName(IP), Server_HOST_PORT);
            socket_.send(packa);
        }catch (UnknownHostException e ){
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }
    private BroadcastReceiver mWifiConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG,""+intent.getAction());
            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int message = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1);
                switch (message) {
                    case WifiManager.WIFI_STATE_DISABLED:
                        Log.d(TAG, "WIFI_STATE_DISABLED");
                        finish();
                        break;
                    case WifiManager.WIFI_STATE_ENABLED:
                        Log.d(TAG, "WIFI_STATE_ENABLED");
                        break;
                    default:
                        break;
                }
            }
            if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                NetworkInfo.State state = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
                if(state.toString().equals("CONNECTED")){

                }else if(state.toString().equals("DISCONNECTED")){
                    finish();
                }else{
                    Log.e(TAG,"state="+state.toString());
                }
            }
        }
    };
    private void registerWIFI() {
        mWifiFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mWifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mWifiConnectReceiver, mWifiFilter);
    }
}
