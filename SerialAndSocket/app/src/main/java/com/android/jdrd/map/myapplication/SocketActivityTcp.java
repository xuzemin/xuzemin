package com.android.jdrd.map.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by Administrator on 2017/1/9.
 */

public class SocketActivityTcp extends Activity implements Runnable{
    private String TAG = "SocketActivity";
    private TextView textView,ip ,port ;
    private EditText editText;
    private String content = "";
    private BufferedReader in = null;
    private PrintWriter out = null;
    private Socket socket = null;
    private final static int CONNECT_SUCCES = 1;
    private final static int GETDATA = 2;
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
//        port.setText("端口"+Constant.HOST_PORT);


        findViewById(R.id.buttonSocket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().trim().equals("")) {
                    String msg = editText.getText().toString();
                    if (socket!=null) {
                        if (socket.isConnected()) {
                            if (!socket.isOutputShutdown()) {
                                out.println(msg);
                            }
                        }
                        editText.setText("");
                    }else{
                        Toast.makeText(SocketActivityTcp.this,"连接未成功",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //启动通讯连接
        new Thread(SocketActivityTcp.this).start();
    }
    //新线程 连接以及获取流
    public void run() {
        try {
            //socket 连接
            socket = new Socket(Constant.Server_IP, Constant.HOST_PORT);
            //获得输入流
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream(),"UTF-8"));
            //获得输出流
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream(),"UTF-8")), true);
            getdata();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //启动新线程循环接受数据
    public void getdata(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream inputStream = socket.getInputStream();
                    byte buffer[] = new byte[1024 * 4];
                    int temp = 0;
                    // 从InputStream当中读取客户端所发送的数据
                    while ((temp = inputStream.read(buffer)) != -1) {
                        System.out.println(new String(buffer, 0, temp));
                        Log.e(TAG, "content"+new String(buffer, 0, temp));
                        content = new String(buffer, 0, temp);
                        Log.e(TAG, "content"+content);
                        Message msg =  Message.obtain(handler,GETDATA,content);
                        msg.sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
