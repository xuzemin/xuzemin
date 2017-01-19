package com.android.jdrd.headcontrol.activity;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.view.MyGLSurfaceView;

public class MainActivity extends AppCompatActivity {
    private GLSurfaceView mGLView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGLView = (GLSurfaceView)this.findViewById(R.id.surface);
        setContentView(mGLView);
    }
}
