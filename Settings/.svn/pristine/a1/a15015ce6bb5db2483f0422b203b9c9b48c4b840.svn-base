package com.ctv.settings.greneral;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.ctv.settings.utils.ViewUtils;

public class USBLockActivity extends AppCompatActivity implements View.OnClickListener{
    private View usb_lock_switch;
    private View usb_lock_switch_iv;
    private static long startclick = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.greneral_usblock_dialog_layout);
        initView();
        initListener();
        refreshUI();

    }

    private void initListener() {
        usb_lock_switch.setOnClickListener(this);
    }

    private void initData() {

    }

    private void initView() {
        usb_lock_switch = (View)findViewById(R.id.usb_lock_switch);
        usb_lock_switch_iv = (View)findViewById(R.id.usb_lock_switch_iv);
        usb_lock_switch.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            usb_lock_switch.requestFocus();
            if(!doubleclick()){
                GreneralUtils.getInstance(this).setUsbLockEnable(
                        GreneralUtils.getInstance(this).getUsbLockStatus()?false:true
                );
                refreshUI();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.usb_lock_switch){
            view.requestFocus();
            if(doubleclick()){
                return;
            }
            GreneralUtils.getInstance(this).setUsbLockEnable(
                    GreneralUtils.getInstance(this).getUsbLockStatus()?false:true
            );
            refreshUI();
        }
    }
    public boolean doubleclick(){
        long endclick = System.currentTimeMillis();
        long time = endclick - startclick;
        startclick = endclick;
        if(0 < time && time <1000){
            return true;
        }
        return false;
    }
    @Override
    protected void onStop() {
        finish();
        super.onStop();
    }

    public void refreshUI(){
        usb_lock_switch_iv.setBackgroundResource(
                GreneralUtils.getInstance(this).getUsbLockStatus()?R.mipmap.on:R.mipmap.off);
    }
}
