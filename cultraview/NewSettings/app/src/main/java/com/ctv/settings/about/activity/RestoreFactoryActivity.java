package com.ctv.settings.about.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.about.ViewHolder.RestoreFactoryActivityViewHolder;
import com.ctv.settings.base.BaseActivity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreFactoryActivity extends BaseActivity implements View.OnClickListener {

    Button restore_ok;
    Button restore_cancel;
    private boolean isReset;
    private View closeview;
    private AlertDialog.Builder close_builder;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private RestoreFactoryActivityViewHolder restoreFactoryActivityViewHolder;
    private View back;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_factory);
        restoreFactoryActivityViewHolder=new RestoreFactoryActivityViewHolder(this);
        back = findViewById(R.id.back_btn);
        back.setOnClickListener(this);
        text = (TextView) findViewById(R.id.back_title);
        text.setText(getResources().getString(R.string.system_restore_factory));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back_btn) {
            finish();
        }


    }
}
