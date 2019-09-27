package com.ctv.settings.language;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * 语言和输入法
 * @author wanghang
 * @date 2019/09/18
 */
public class LanguageActivity extends AppCompatActivity {
    private LanguageInpoutViewHolder languageInpoutViewHolder;

    public static Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        mHandler = new UIHandler(this);
        languageInpoutViewHolder = new LanguageInpoutViewHolder(this, mHandler);
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
