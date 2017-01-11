package com.android.jdrd.map.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.skyworth.splicing.SerialPortUtil;

/**
 * Created by Administrator on 2017/1/9.
 */

public class SerialActivity extends Activity {
    TextView TextView ;
    private String content = "";
    SerialPortUtil mSerialPortUtil;
    EditText editText;

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
                    TextView.setText(msg.obj.toString());
                    break;
                case Constant.COMMUNCITY_REEOR:
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);
        //单模式
        TextView = (TextView) this.findViewById(R.id.textView);
        editText = (EditText) this.findViewById(R.id.editText);
        mSerialPortUtil = SerialPortUtil.getInstance();
        mSerialPortUtil.setOnDataReceiveListener(new SerialPortUtil.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size) {
                // TODO Auto-generated method stub
//                TextView.setText(new String(buffer,0,size));
                content = new String(buffer, 0, size);
                Log.e("","content"+content);
                Message msg =  Message.obtain(handler,GETDATA,content);
                msg.sendToTarget();
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().trim().equals("")){
                    Log.e("SerialActivity", "content"+editText.getText().toString().trim());
                    mSerialPortUtil.sendCmds(editText.getText().toString()+'\r'+'\n');
                    editText.setText("");
                }

//                mSerialPortUtil.sendCmds("q\r\n");
//                byte[] byte_1 = {'q','\r','\n'};
//                byte[] byte_2  =  editText.getText().toString().trim().getBytes();
//                byte[] byte_3 = new byte[byte_2.length+byte_1.length];
//                System.arraycopy(byte_2, 0, byte_3, 0, byte_2.length);
//                System.arraycopy(byte_1, 0, byte_3, byte_2.length, byte_1.length);
//                mSerialPortUtil.sendBuffer(byte_1);
//                mSerialPortUtil.sendBuffer(editText.getText().toString().getBytes());

            }
        });
    }
}
