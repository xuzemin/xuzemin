package com.ctv.settings.about.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.UpdateViewHolder;
import com.ctv.settings.base.BaseActivity;
import com.mstar.android.tvapi.common.vo.VideoWindowInfo;

public class SystemUpdateActivity extends BaseActivity implements View.OnClickListener {

    private UpdateViewHolder updateViewHolder;
    private View back;
    private TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_system_update);
      updateViewHolder = new UpdateViewHolder(this);
        back = findViewById(R.id.back_btn);
        back.setOnClickListener(this);
        text = (TextView) findViewById(R.id.back_title);
        text.setText(getResources().getString(R.string.system_update_main));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }

    }
}
