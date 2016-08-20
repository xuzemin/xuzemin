package com.android.wifi.socket.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.wifi.socket.util.timeUtils;
import com.android.wifi.socket.wifisocket.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by xuzemin on 16/8/15.
 */
public class WifControlActivity extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener {
    private static String TAG = "WifControlActivity";
    private int Server_HOST_PORT = 58888;
    private int Client_HOST_PORT = 58000;
    private EditText ip, message;
    private TextView messageText;
    private Socket socket =null;
    private PrintWriter out = null;
    private BufferedReader read;
    private int CONNECT_OK = 0;
    private DatagramSocket socket_;
    public Button connect,cancel,send;
    private Handler handle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    message.setVisibility(View.VISIBLE);
                    send.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    message.setText("");
                    connect.setEnabled(true);
                    send.setEnabled(false);
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
//                connect();
                send_udp();
                connect.setEnabled(false);
                cancel.setText("断开");
                break;
            case R.id.button_cancel:
                cancel.setText("取消");
                connect.setEnabled(true);
                message.setVisibility(View.INVISIBLE);
                send.setVisibility(View.INVISIBLE);
                messageText.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.send:
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    send.setBackgroundResource(R.color.colorAccent);
//                    send("up"+message.getText().toString().trim());
                    send_up();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    send.setBackgroundResource(R.color.colorPrimaryDark);
//                    send("stop"+message.getText().toString().trim());
                    send_down();
                }
                break;
        }
        return true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_layout);
        ip = (EditText) findViewById(R.id.connect_ip);
        message = (EditText) findViewById(R.id.message);
        connect = (Button) findViewById(R.id.button_connect);
        cancel = (Button) findViewById(R.id.button_cancel);
        send = (Button) findViewById(R.id.send);
        messageText = (TextView)findViewById(R.id.messageText);
        connect.setOnClickListener(this);
        cancel.setOnClickListener(this);
        send.setOnTouchListener(this);

        message.setVisibility(View.INVISIBLE);
        send.setVisibility(View.INVISIBLE);
        messageText.setVisibility(View.INVISIBLE);
    }

    public void toastText(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    //    public void connect() {
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                try
//                {
//                    Log.e(TAG, "连接" + ip.getText().toString().trim());
//                    socket = new Socket(ip.getText().toString().trim(), Server_HOST_PORT);
//                    Log.e(TAG, "连接成功");
//                    Log.e(TAG, "连接成功"+socket.isConnected());
//                    handle.sendEmptyMessage(CONNECT_OK);
//                    recive_udp();
//                    read = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    String content;
//                    while ((content = read.readLine()) != null) {
//                        Log.e(TAG, "接受成功");
//                        Log.e(TAG, read.readLine().toString());
//                        Log.e(TAG,"recieve ok"+content);
//                    }
//                }
//                catch(IOException e)
//                {
//                    Log.e(TAG, e.toString());
//                    cancel.setText("取消");
//                    connect.setEnabled(true);
//                }
//            }
//        }.start();
//
//    }
    private void send_udp(){
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    socket_ = new DatagramSocket(Client_HOST_PORT);
                    String str = "connect";
                    byte data[] = str.getBytes();
                    DatagramPacket package_udp = new DatagramPacket(data, data.length,InetAddress.getByName("192.168.1.32"),Server_HOST_PORT);
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
    private void send_up() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "up";
                byte data[] = str.getBytes();
                try {
                    DatagramPacket packa = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.18.111"), Server_HOST_PORT);
                    socket_.send(packa);
                }catch (UnknownHostException e ){
                    Log.e(TAG, e.toString());
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }.start();
    }
    private void send_down() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String str = "stop";
                byte data[] = str.getBytes();
                try {
                    DatagramPacket packa = new DatagramPacket(data, data.length, InetAddress.getByName("192.168.18.111"), Server_HOST_PORT);
                    socket_.send(packa);
                }catch (UnknownHostException e ){
                    Log.e(TAG, e.toString());
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }.start();
    }
    //    private void send(String str ) {
//        try {
//            Log.e(TAG, "连接成功"+socket.isConnected());
//            out = new PrintWriter(new BufferedWriter(
//                    new OutputStreamWriter(socket.getOutputStream())), true);
//            out.println(str+"\r\n");
//            Log.e(TAG,"发送成功");
//        }catch (IOException e){
//            Log.e(TAG, e.toString());
//        }
//    }
    public void ReceiveServerSocketData() {
        DatagramSocket socket_recieve;
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
}
