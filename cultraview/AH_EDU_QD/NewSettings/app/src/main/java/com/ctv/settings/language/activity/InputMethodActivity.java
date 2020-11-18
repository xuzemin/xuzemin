package com.ctv.settings.language.activity;

import android.os.Bundle;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.language.holder.InputMethodViewHolder;

/**
 * 输入法设置
 * @author wanghang
 * @date 2019/09/23
 */
public class InputMethodActivity extends BaseActivity {
    private static final String TAG = "InputMethodActivity";

    private InputMethodViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_method);

        viewHolder = new InputMethodViewHolder(this);
    }
}
