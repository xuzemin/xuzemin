package com.ctv.settings.about.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.about.ViewHolder.UpdateViewHolder;
import com.ctv.settings.base.BaseActivity;

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
