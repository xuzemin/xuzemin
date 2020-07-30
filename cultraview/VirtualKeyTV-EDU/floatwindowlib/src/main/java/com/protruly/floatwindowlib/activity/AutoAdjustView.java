package com.protruly.floatwindowlib.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cultraview.tv.CtvPictureManager;
import com.protruly.floatwindowlib.R;

public class AutoAdjustView extends Activity {

    protected static final String TAG = "AutoAdjustView";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_adjust_view);
        new Thread(new AutoAdjustThread()).start();
    }

    private class AutoAdjustThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
                CtvPictureManager.getInstance().execAutoPc();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finish();
        }
    }

    ;
}

