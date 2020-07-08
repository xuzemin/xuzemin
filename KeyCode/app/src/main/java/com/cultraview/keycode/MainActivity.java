package com.cultraview.keycode;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Toast.makeText(getApplicationContext(),"keycode value"+keyCode,Toast.LENGTH_SHORT).show();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("Touch","event.toString"+event.toString());
        int actionIndex = event.getActionIndex();
        Log.e("Touch","event.getActionIndex"+actionIndex);
        Log.e("Touch","event.getToolType"+event.getToolType(actionIndex));
        Log.e("Touch","event.getTouchMajor"+event.getTouchMajor(actionIndex));
        Log.e("Touch","event.getSize"+event.getSize());
        return super.onTouchEvent(event);
    }
}
