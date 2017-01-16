package com.android.jdrd.map.myapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by Administrator on 2016/11/24.
 */

public class UdpCommunicateTool {
    private final static String TAG = "UdpCommunicateTool";

    public static void ReceiveServerSocketData(DatagramSocket socket, Handler handler, int from_where) {
        try {
            // 实例化的端口号要和发送时的socket一致，否则收不到data
            byte data[] = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            socket.receive(packet);
            // 把接收到的data转换为String字符串
            //DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.getData(),packet.getOffset(),packet.getLength()));
            String result = new String(packet.getData(), packet.getOffset(),
                    packet.getLength(),"GBK");
            Message msg =  Message.obtain(handler,from_where,result);
            msg.sendToTarget();
        } catch (SocketException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }

    public static void sendData(String str, final DatagramSocket socket, final Handler handler) {
        try {
            byte[] data = str.getBytes("GBK");

            // 创建一个DatagramPacket 对象，并指定要讲这个数据包发送到网络当中的哪个地址，以及端口号
            final DatagramPacket packa = new DatagramPacket(data, data.length,
                    InetAddress.getByName(Constant.Server_IP), Constant.Server_HOST_PORT);
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        if (socket == null) {
                            handler.sendEmptyMessage(Constant.COMMUNCITY_REEOR);
                        } else {
                            socket.send(packa);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } catch (UnknownHostException e) {
            Log.e(TAG, e.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}


