package com.ctv.settings.about.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ctv.settings.about.R;
import com.ctv.settings.about.activity.AboutActivity;
import com.ctv.settings.about.activity.BootOptionsActivity;

import com.ctv.settings.about.activity.RestoreFactoryActivity;
import com.ctv.settings.about.activity.SystemUpdateActivity;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.utils.Tools;
import com.mstar.android.pppoe.PPPOE_STA;

public class AboutDeviceViewHolder extends BaseViewHolder implements View.OnClickListener{
        private View rl_update_system;
    private View mBootOptions;    //
    private TextView mTvBootOptions; //
    private TextView aboutitem;
    final int REQUEST_BOOT_OPTIONS=1;
    private View systemrestorefactory;
    private View about;
    private String[] options_vals;
    private int boot_option;

    public AboutDeviceViewHolder(Activity activity){
        super(activity);
        initData(activity);


    }

    @Override
    public void initUI(Activity activity) {
        mBootOptions = mActivity.findViewById(R.id.rl_boot_options);
        systemrestorefactory= mActivity.findViewById(R.id.rl_system_restore_factory);
        mTvBootOptions = (TextView)mActivity.findViewById(R.id.tv_boot_options);
        about = mActivity.findViewById(R.id.rl_about_tv);
       aboutitem= (TextView)mActivity.findViewById(R.id.tv_item_about);
        rl_update_system= mActivity.findViewById(R.id.rl_update_system);
    }

    @Override
    public void initListener() {
        mBootOptions.setOnClickListener(this);
        systemrestorefactory.setOnClickListener(this);
        about.setOnClickListener(this);
        aboutitem.setOnClickListener(this);
        rl_update_system.setOnClickListener(this);
    }


    @Override
    public void refreshUI(View view) {
        int id = view.getId();
            if (id == R.id.tv_boot_options) {
                String optionValue = Tools.getBootLoader(mActivity);//
                if(optionValue.equals("1")){
                    boot_option = 0;
                }else if(optionValue.equals("2")){
                    boot_option = 1;
                }else if(optionValue.equals("3")){
                    boot_option = 2;
                }
                mTvBootOptions.setText(options_vals[boot_option]);
            }
        }


    @Override
    public void initData(Activity activity) {
        boot_option = 0;
        options_vals = mActivity.getResources().getStringArray(R.array.starting_up_option_vals);
        String optionValue = Tools.getBootLoader(mActivity);//拿到点击时的 Tools.setBootLoader(BootOptionsActivity.this,i+1+"")传入的i+1
        if(optionValue.equals("1")){
            boot_option = 0;
        }else if(optionValue.equals("2")){
            boot_option = 1;
        }else if(optionValue.equals("3")){
            boot_option = 2;
        }
        mTvBootOptions.setText(options_vals[boot_option]);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rl_boot_options) {
            mActivity.startActivityForResult(new Intent(mActivity, BootOptionsActivity.class), REQUEST_BOOT_OPTIONS);
        } else if (id == R.id.rl_update_system) {
            mActivity.startActivity(new Intent(mActivity, SystemUpdateActivity.class));
        } else if (id == R.id.rl_system_restore_factory) {
            mActivity.startActivity(new Intent(mActivity, RestoreFactoryActivity.class));
        } else if (id == R.id.rl_about_tv) {
            mActivity.startActivity(new Intent(mActivity, AboutActivity.class));
        }
    }
}
