package com.ctv.settings.about.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.RestoreFactoryActivityViewHolder;
import com.ctv.settings.utils.SystemPropertiesUtils;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.Constants;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreFactoryActivity extends Activity implements View.OnClickListener {

    Button restore_ok;
    Button restore_cancel;
    private boolean isReset;
    private View closeview;
    private AboutDeviceActivity aboutDeviceActivity;
    private AlertDialog.Builder close_builder;
    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private RestoreFactoryActivityViewHolder restoreFactoryActivityViewHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_factory);
        restoreFactoryActivityViewHolder=new RestoreFactoryActivityViewHolder(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {


        }


    }
}
