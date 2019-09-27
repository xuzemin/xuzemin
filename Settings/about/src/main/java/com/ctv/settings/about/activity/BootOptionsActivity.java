package com.ctv.settings.about.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import com.ctv.settings.about.R;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class BootOptionsActivity extends Activity implements View.OnClickListener {
    private ListView listView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_options);
        ListView listView= (ListView) findViewById(R.id.lv_boot_options);
        String[] OPTIONS_VALS = getResources().getStringArray(R.array.starting_up_option_vals);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.boot_options_item,R.id.tv_item_boot_options, OPTIONS_VALS);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Tools.setBootLoader(BootOptionsActivity.this,i+1+"");//保存点击时的id
                setResult(Activity.RESULT_OK);//向AboutDeviceActivity返回RESULT_OK.
                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {

    }
}
