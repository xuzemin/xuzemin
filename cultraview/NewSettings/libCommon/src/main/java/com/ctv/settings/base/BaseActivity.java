package com.ctv.settings.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ctv.settings.utils.IDHelper;
import com.ctv.settings.utils.L;


/**
 *
 */
public abstract class BaseActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        int bgIndex = sharedPreferences.getInt("bgIndex", 0);
        L.d("set default theme bgIndex:" + bgIndex);
//        if (bgIndex == 0) {
//            setTheme(IDHelper.getStyle(this, "MyBg"));
//            return;
//        }
        if (bgIndex < 6 && bgIndex > 0) {
            setTheme(IDHelper.getStyle(this, "MyBg" + bgIndex));
        } else {
            L.e("set default theme");
            setTheme(IDHelper.getStyle(this, "MyBg"));
        }

    }
}