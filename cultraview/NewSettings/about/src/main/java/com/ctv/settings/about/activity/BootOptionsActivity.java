package com.ctv.settings.about.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.*;


import com.ctv.settings.about.Adapter.BootoptionsApdater;
import com.ctv.settings.about.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class BootOptionsActivity extends BaseActivity implements View.OnClickListener {
    private View viewById;
    private TextView textview;
    private ImageView boot_option_item_ivs;
    private int boot_option;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_options);
        viewById = findViewById(R.id.back_btn);
        textview = (TextView) findViewById(R.id.back_title);
        textview.setText(getResources().getString(R.string.boot_options));

        boot_option_item_ivs = (ImageView) findViewById(R.id.boot_option_item_ivs);

        ListView listView = (ListView) findViewById(R.id.lv_boot_options);
        viewById.setOnClickListener(this);
        String[] OPTIONS_VALS = getResources().getStringArray(R.array.starting_up_option_vals);
        String optionValue = Tools.getBootLoader(this);//
        if (optionValue.equals("1")) {
            boot_option = 0;
        } else if (optionValue.equals("2")) {
            boot_option = 1;
        } else if (optionValue.equals("3")) {
            boot_option = 2;
        } else if (optionValue.equals("4")) {
            boot_option = 3;
        } else if (optionValue.equals("5")) {
            boot_option = 4;
        } else if (optionValue.equals("6")) {
            boot_option = 5;
        } else if (optionValue.equals("7")) {
            boot_option = 6;
        }
        BootoptionsApdater bootoptionsApdater = new BootoptionsApdater(this, OPTIONS_VALS, boot_option);
        listView.setAdapter(bootoptionsApdater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Tools.setBootLoader(BootOptionsActivity.this, i + 1 + "");//保存点击时的id
                setResult(CommonConsts.BOOTOPTION_ITEM1);//向AboutDeviceActivity返回RESULT_OK.

                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }

    }
}
