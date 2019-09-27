package com.ctv.settings.about.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.AboutDeviceViewHolder;

public class AboutDeviceActivity extends Activity {

    private AboutDeviceViewHolder mAboutDeviceViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_device);
        mAboutDeviceViewHolder = new AboutDeviceViewHolder(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== Activity.RESULT_OK){
            mAboutDeviceViewHolder.refreshUI(findViewById(R.id.tv_boot_options));
        }
    }
}
