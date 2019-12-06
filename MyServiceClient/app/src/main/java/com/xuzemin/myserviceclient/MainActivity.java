package com.xuzemin.myserviceclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.xuzemin.myserviceclient.service.ClientSocketUtil;
import com.xuzemin.myserviceclient.view.DragGridView;
import com.xuzemin.myserviceclient.view.GridViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private DragGridView dragGridView;
    private GridViewAdapter gridViewAdapter;
    private List<String> strList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Intent serverSocket = new Intent(this, ClientSocketUtil.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serverSocket);
        }else{
            startService(serverSocket);
        }

        strList = new ArrayList<>();
        strList.add("1");
        strList.add("2");
        strList.add("3");
        strList.add("4");
        strList.add("5");

        dragGridView = findViewById(R.id.dragview);
        gridViewAdapter = new GridViewAdapter(this,strList);
        dragGridView.setAdapter(gridViewAdapter);
    }
}
