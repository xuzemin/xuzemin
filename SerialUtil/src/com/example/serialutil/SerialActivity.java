package com.example.serialutil;

import com.skyworth.splicing.SerialPortUtil;
import com.skyworth.splicing.SerialPortUtil.OnDataReceiveListener;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
/*
 * @author guoxiao
 * 
 */
public class SerialActivity extends Activity {

	SerialPortUtil mSerialPortUtil;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_world);
        //单模式
        mSerialPortUtil = SerialPortUtil.getInstance();
        mSerialPortUtil.setOnDataReceiveListener(new OnDataReceiveListener() {
			
			@Override
			public void onDataReceive(byte[] buffer, int size) {
				// TODO Auto-generated method stub
				Log.d("[gx]", " DataReceive:" + new String(buffer,0,size));
			}
		});
    }

    public void onWriteq(View view){
    	mSerialPortUtil.sendCmds("q\r\n");
    	byte[] str = {'q','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    public void onWritew(View view){
    	mSerialPortUtil.sendCmds("w\r\n");
    	byte[] str = {'w','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    public void onWritee(View view){
    	mSerialPortUtil.sendCmds("e\r\n");
    	byte[] str = {'e','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    public void onWritr(View view){
    	mSerialPortUtil.sendCmds("r\r\n");
    	byte[] str = {'r','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    public void onWritt(View view){
    	mSerialPortUtil.sendCmds("t\r\n");
    	byte[] str = {'t','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    public void onWrity(View view){
    	mSerialPortUtil.sendCmds("y\r\n");
    	byte[] str = {'y','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    public void onWritu(View view){
    	mSerialPortUtil.sendCmds("u\r\n");
    	byte[] str = {'u','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    public void onWriti(View view){
    	mSerialPortUtil.sendCmds("i\r\n");
    	byte[] str = {'i','\r','\n'};
    	mSerialPortUtil.sendBuffer(str);
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hello_world, menu);
        return true;
    }
    
}
