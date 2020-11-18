package com.ctv.settings.greneral;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ctv.settings.base.BaseActivity;

public class GreneralActivity extends BaseActivity {
    private final static String TAG = "GreneralLib";
    private GreneralViewHolder holder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_layout);
        holder = new GreneralViewHolder(this);
    }
}
