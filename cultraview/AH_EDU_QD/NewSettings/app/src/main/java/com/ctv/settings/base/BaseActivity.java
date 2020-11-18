package com.ctv.settings.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ctv.settings.MyApp;
import com.ctv.settings.utils.ExitAppUtil;
import com.ctv.settings.utils.L;


/**
 *
 */
public abstract class BaseActivity extends Activity {
    private final static String TAG = BaseActivity.class.getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        L.d(TAG,"BaseActivity onCreate");
        super.onCreate(savedInstanceState);
        ExitAppUtil.getInstance().addActivity(this);

        changeTheme();
    }

    /**
     * 切换主题
     */
    private void changeTheme(){
        MyApp app = (MyApp) BaseActivity.this.getApplication();
        setTheme(app.themeResID);
    }

//
//    /**
//     * 切换主题
//     */
//    private void changeTheme(){
//        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
//        int bgIndex = sharedPreferences.getInt("bgIndex", 0);
//        L.d(TAG,"set Theme set theme bgIndex:" + bgIndex);
//        if (bgIndex < 6 && bgIndex > 0) {
//            setTheme(IDHelper.getStyle(this, "MyBg" + bgIndex));
//        } else {
//            L.d(TAG,"set default theme");
//            setTheme(IDHelper.getStyle(this, "MyBg"));
//        }
//    }

    @Override
    public void finish() {
        L.d(TAG,"BaseActivity finish");
        super.finish();
        ExitAppUtil.getInstance().delActivity(this);
    }

    @Override
    protected void onDestroy() {
        L.d(TAG,"BaseActivity onDestroy");
        super.onDestroy();
    }
}