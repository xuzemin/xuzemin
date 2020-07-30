package com.ctv.settings.about.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.AboutDeviceViewHolder;
import com.ctv.settings.about.ViewHolder.AboutViewHolder;
import com.ctv.settings.base.BaseActivity;

public class AboutActivity extends BaseActivity implements View.OnClickListener{
    private AboutViewHolder aboutViewHolder;
    private View btnBack;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        aboutViewHolder= new AboutViewHolder(this);
        btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.back_title);
        textView.setText(getResources().getString(R.string.about_tv));

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }

    }
}
