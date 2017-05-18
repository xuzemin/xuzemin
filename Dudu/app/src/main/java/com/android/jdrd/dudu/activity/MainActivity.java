package com.android.jdrd.dudu.activity;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.jdrd.dudu.R;
import com.android.jdrd.dudu.utils.Constant;


public class MainActivity extends AppCompatActivity {
    private AudioManager mAudioManager;
    private Constant constant;
    private AudioManager.OnAudioFocusChangeListener mAudioFocusChangeListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        constant = new Constant(this);
        constant = Constant.getConstant(this);
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constant.startPlay();
            }
        });
    }
}
