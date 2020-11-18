package com.ctv.settings.security.activity;

import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.security.R;
import com.ctv.settings.security.utils.GreneralUtils;

public class USBLockActivity extends BaseActivity implements View.OnClickListener {
    private View usb_lock_switch;
    private View usb_lock_switch_iv;
    private static long startclick = 0;
    private View back_btn;
    private LinearLayout general_top_back_layout;
    private TextView back_title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greneral_usblock_main_layout);
        initView();
        initListener();
        refreshUI();

    }

    private void initListener() {
        usb_lock_switch.setOnClickListener(this);
        general_top_back_layout.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initView() {
        usb_lock_switch = (View) findViewById(R.id.usb_lock_switch);
        usb_lock_switch_iv = (View) findViewById(R.id.usb_lock_switch_iv);
        back_btn = (View) findViewById(R.id.back_btn);
        general_top_back_layout = (LinearLayout) findViewById(R.id.general_top_back_layout);
        usb_lock_switch.requestFocus();
        back_title = (TextView) findViewById(R.id.back_title);
        back_title.setText(getResources().getString(R.string.greneral_usb_lock_item));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            usb_lock_switch.requestFocus();
            if (!doubleclick()) {
                GreneralUtils.getInstance(this).setUsbLockEnable(
                        GreneralUtils.getInstance(this).getUsbLockStatus() ? false : true
                );
                refreshUI();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.usb_lock_switch) {
            view.requestFocus();
            if (doubleclick()) {
                return;
            }
            GreneralUtils.getInstance(this).setUsbLockEnable(
                    GreneralUtils.getInstance(this).getUsbLockStatus() ? false : true);
            refreshUI();
            Log.d("qkmin","persist.sys.touch_enable set");
        } else if (view.getId() == R.id.general_top_back_layout) {
            finish();
        }
    }

    public boolean doubleclick() {
        long endclick = System.currentTimeMillis();
        long time = endclick - startclick;
        startclick = endclick;
        if (0 < time && time < 1000) {
            return true;
        }
        return false;
    }

    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    public void refreshUI() {
        usb_lock_switch_iv.setBackgroundResource(
                GreneralUtils.getInstance(this).getUsbLockStatus() ? R.mipmap.on : R.mipmap.off);
    }
}
