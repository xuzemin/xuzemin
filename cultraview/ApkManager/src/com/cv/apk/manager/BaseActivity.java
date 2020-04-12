package com.cv.apk.manager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.view.View;



/**
 *
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        int bgIndex = sharedPreferences.getInt("bgIndex", -1);
        int resTheme[] = {R.style.MyBg, R.style.MyBg2, R.style.MyBg3, R.style.MyBg4, R.style.MyBg5};
        int gwTheme[] = {R.style.MyBg, R.style.MyBg2, R.style.MyBg3, R.style.MyBg4, R.style.MyBg5, R.style.MyBg6};
        boolean gw = SystemProperties.get("client.config").equals("GW");
        if (gw) {
            if (bgIndex == -1) {
                setTheme(gwTheme[5]);
            } else {
                setTheme(gwTheme[bgIndex]);
            }
        } else {
            if (bgIndex == -1) {
                setTheme(resTheme[0]);
            } else {
                setTheme(resTheme[bgIndex]);
            }
        }
    }




}