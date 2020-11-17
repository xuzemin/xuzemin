package com.ctv.annotation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.ss);
        gotoservice();
    }

    private void gotoservice(){

        Log.d("hhh", "gotoservice: ");
     Intent   intent = new Intent(this, AnnotationService.class);
        startService(intent);
        finish();

    }
}