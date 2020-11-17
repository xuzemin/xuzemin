package com.ctv.settings.language.activity;

import android.os.Bundle;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.language.holder.LanguageSelectViewHolder;

/**
 * 语言选择设置
 * @author wanghang
 * @date 2019/09/23
 */
public class LanguageSelectActivity extends BaseActivity {
    private LanguageSelectViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);

        mViewHolder = new LanguageSelectViewHolder(this);
    }
}
