package com.android.jdrd.headcontrol.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Constant;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestActivity extends Activity {

    private String TAG = "SocketActivity";
    private TextView textView;
    private EditText editText;

    private MyReceiver receiver;
    IntentFilter filter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_socket);

        editText = (EditText)this.findViewById(R.id.editTextSocket);
        textView = (TextView)this.findViewById(R.id.textViewSocket);

        Intent intent = new Intent(this, ServerSocketUtil.class);
        startService(intent);


        receiver = new MyReceiver();
        filter=new IntentFilter();
        filter.addAction("com.jdrd.fragment.Map");
        registerReceiver(receiver, filter);


        findViewById(R.id.startSearchPeople).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("com.jdrd.CursorSDKExample.TD_CAMERA");
                sendBroadcast(intent);
            }
        });

        findViewById(R.id.buttonSocket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Gson gson = new Gson();
                    Map map  = new LinkedHashMap();
                    map.put("type", "command");
                    map.put("function", "peoplesearch");
                    map.put("data", "");
                    String s = gson.toJson(map);
                    ServerSocketUtil.sendDateToClient(s);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*if(!editText.getText().toString().trim().equals("")){
                    try {
                        ServerSocketUtil.sendDateToClient(editText.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }*/

            }
        });

    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String msg = intent.getStringExtra("msg");
            Constant.debugLog("收到了距离角度： " + msg);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(msg);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

}
