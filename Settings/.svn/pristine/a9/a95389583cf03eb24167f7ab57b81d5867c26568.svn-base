package com.ctv.settings.security;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * 安全模块
 * @author wanghang
 * @date 2019/09/17
 */
public class SecurityActivity extends AppCompatActivity {
    private final static String TAG = "SecurityLib";
    private SecurityViewHolder mSecurityViewHolder;

    public static Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);

        Log.d(TAG, "SecurityActivity onCreate");
        mHandler = new UIHandler(this);
        // 初始化viewHolder
        mSecurityViewHolder = new SecurityViewHolder(this, mHandler);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSecurityViewHolder.onDestroy();
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<Activity> weakReference;

        public UIHandler(Activity activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Activity activity = weakReference.get();
            if (activity == null || activity.isFinishing()){
                return;
            }

            switch (msg.what){
                default:
                    break;
            }
        }
    }
}
