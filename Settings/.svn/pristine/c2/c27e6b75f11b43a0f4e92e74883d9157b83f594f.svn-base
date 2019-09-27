
package com.ctv.settings.network.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import com.ctv.settings.network.R;
import com.ctv.settings.network.mcast.DisplayViewHolder;

public class McastActivity extends Activity {

    private static final String TAG = "NetworkSettingsActivity";

    private Context mContext;

    private DisplayViewHolder mDisplayViewHolder;

    private boolean isFromSetting;

    @SuppressLint("HandlerLeak")
    private final Handler animationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mDisplayViewHolder.scaleAnimation(true);
                    break;
                case 1:
                    mDisplayViewHolder.scaleAnimation(false);
                    break;
                case 2:
                    mDisplayViewHolder.setOut();
                    if (isFromSetting) {
                        Intent intent = new Intent();
                        intent.setAction("com.cultraview.settings.CTVSETTINGS");
                        intent.putExtra("StartPageNumber", 5);
                        startActivity(intent);
                    }
                    finish();
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_display);
        mContext = McastActivity.this;
        mDisplayViewHolder = new DisplayViewHolder(mContext);
        isFromSetting = getIntent().getBooleanExtra("FromSetting", false);
        animationHandler.sendEmptyMessage(0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mDisplayViewHolder.exitMcast();
            animationHandler.sendEmptyMessage(1);
            animationHandler.sendEmptyMessageDelayed(2, 300);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
        if (mDisplayViewHolder != null) {
            mDisplayViewHolder.exitMcast();
            mDisplayViewHolder.unregisterReceiver();
            mDisplayViewHolder.onExit();
        }
        animationHandler.removeCallbacksAndMessages(null);
        mContext = null;
        mDisplayViewHolder = null;
        /************* start ,don't change the location,avoid Memory leak********** */
        android.os.Process.killProcess(android.os.Process.myPid());
        System.gc();
        /************* end ,don't change the location,avoid Memory leak********** */
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public DisplayViewHolder getDisplayViewHolder() {
        return mDisplayViewHolder;
    }
}
