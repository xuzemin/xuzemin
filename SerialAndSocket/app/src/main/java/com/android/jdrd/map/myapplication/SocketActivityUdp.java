package com.android.jdrd.map.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * Created by Administrator on 2017/1/9.
 */

public class SocketActivityUdp extends Activity{
    private String TAG = "SocketActivity";
    private TextView textView ,ip ,port ;
    private EditText editText;
    private final static int CONNECT_SUCCES = 1;
    private final static int GETDATA = 2;
    private DatagramSocket socket;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT_SUCCES:
                    break;
                case GETDATA:
                    textView.setText(msg.obj.toString());
                    break;
                case Constant.COMMUNCITY_REEOR:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);
        editText = (EditText)this.findViewById(R.id.editTextSocket);
        textView = (TextView)this.findViewById(R.id.textViewSocket);
//        ip = (TextView)this.findViewById(R.id.ip);
//        ip.setText("服务器地址"+Constant.Server_IP);
//        port = (TextView)this.findViewById(R.id.port);
//        port.setText("端口"+Constant.Server_HOST_PORT);
        findViewById(R.id.buttonSocket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().trim().equals("")){
                    UdpCommunicateTool.sendData(editText.getText().toString().trim(), socket, handler);
                    editText.setText("");
                }

            }
        });
        //向目标地址发送连接字段，并确定连接
        connect();
    }


    public void connect() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (socket == null) {
                        socket = new DatagramSocket(null);
                        socket.setReuseAddress(true);
                        //绑定当前客户端端口
                        socket.bind(new InetSocketAddress(Constant.Client_HOST_PORT));
                    }
                    Log.e("发送到大屏的连接数据","connect");
                    //发送连接字段
                    UdpCommunicateTool.sendData("connect", socket, handler);
                    //开启循环接受服务器字段
                    while (true) {
                        UdpCommunicateTool.ReceiveServerSocketData(socket, handler, GETDATA);
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e(TAG, e.toString());
                }
            }
        }.start();
    }
}
