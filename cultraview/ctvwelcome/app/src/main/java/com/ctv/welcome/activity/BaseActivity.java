
package com.ctv.welcome.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.ctv.welcome.util.PreferencesUtil;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity extends Activity {
    private final String TAG = getClass().getSimpleName();

    private DisplayMetrics mDisplayMetrics;

    private int mWindowHeight;

    private int mWindowWidth;

    private List<Toast> toastList;

    protected abstract void initContent();

    protected abstract void initListener();

    protected abstract void initVariable();

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        init();
    }

    public void setContentView(View view) {
        super.setContentView(view);
        init();
    }

    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        init();
    }

    private void init() {
        initVariable();
        initListener();
        initContent();
    }

    protected void toast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toast.show();
        this.toastList.add(toast);
    }

    protected void toast(int resId) {
        toast(getString(resId));
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.toastList = new ArrayList();
        PreferencesUtil.init(this);
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        for (Toast toast : this.toastList) {
            toast.cancel();
        }
        this.toastList.clear();
        super.onDestroy();
    }

    protected void LOG(String msg) {
        Log.d(this.TAG, "" + msg);
    }

    protected <T> View bindView(int resId) {
        return findViewById(resId);
    }

    protected void initWindowSize() {
        View child = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
        params.width = (int) (((float) PreferencesUtil.getScreenWidth()) * PreferencesUtil
                .getScreenOffset());
        params.height = (int) (((float) PreferencesUtil.getScreenHeight()) * PreferencesUtil
                .getScreenOffset());
        params.gravity = 17;
        child.setLayoutParams(params);
        LOG("init window:" + params);
    }

    protected void initScreen() {
        this.mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.mDisplayMetrics);
        PreferencesUtil.putScreenWidth(this.mDisplayMetrics.widthPixels);
        PreferencesUtil.putScreenHeight(this.mDisplayMetrics.heightPixels);
    }

    protected void initScreenSize() {
        this.mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(this.mDisplayMetrics);
        this.mWindowWidth = this.mDisplayMetrics.widthPixels;
        this.mWindowHeight = this.mDisplayMetrics.heightPixels;
    }

    protected void changeWindowSize(boolean isWindowMode) {
        View child = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) child.getLayoutParams();
        if (isWindowMode) {
            params.width = (int) (((float) this.mWindowWidth) * 0.8f);
            params.height = (int) (((float) this.mWindowHeight) * 0.8f);
        } else {
            params.width = this.mWindowWidth;
            params.height = this.mWindowHeight;
        }
        params.gravity = 17;
        child.setLayoutParams(params);
    }
}
