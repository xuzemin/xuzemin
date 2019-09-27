package com.ctv.settings.about.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.AboutDeviceViewHolder;
import com.ctv.settings.about.ViewHolder.AboutViewHolder;

public class AboutActivity extends Activity {
    private AboutViewHolder aboutViewHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        aboutViewHolder= new AboutViewHolder(this);

    }
}
