package com.ctv.settings.security.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.security.R;
import com.ctv.settings.security.holder.PermissionsViewHolder;

import java.lang.ref.WeakReference;

/**
 * 权限界面
 * @author wanghang
 * @date 2019/09/18
 */
public class PermissionsActivity extends BaseActivity {

    public static Handler mHandler = null;
    private PermissionsViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        mHandler = new UIHandler(this);
        mViewHolder = new   PermissionsViewHolder(this, mHandler);
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
