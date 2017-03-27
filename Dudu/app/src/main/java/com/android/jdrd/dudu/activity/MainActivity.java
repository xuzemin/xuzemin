package com.android.jdrd.dudu.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.android.jdrd.dudu.R;
import com.android.jdrd.dudu.utils.Constant;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constant.getConstant(this);
    }

}
