package com.ctv.settings.device;

import android.os.Bundle;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.device.viewHolder.DeviceNameViewHolder;
import com.ctv.settings.security.R;

public class DeviceNameActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_name);
        new DeviceNameViewHolder(this);
    }

}
