package com.ctv.settings.timeanddate.holder;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.Tools;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.adapter.DateFormatAdapter;

public class DateFormatViewHolder extends BaseViewHolder {
    private final static String TAG = DateFormatViewHolder.class.getCanonicalName();
    private ListView mLvTimeFormat;
    private int formatIndex;
    private DateFormatAdapter mDateFormatAdapter;
    private View btnBack;
    private TextView mTvBackTitle;

    public DateFormatViewHolder(Activity Activity){
        super(Activity);
    }


    @Override
    public void initUI(Activity activity) {
        mLvTimeFormat = (ListView)mActivity.findViewById(R.id.lv_dateformat);
        mTvBackTitle = (TextView) mActivity.findViewById(R.id.back_title);
        btnBack = mActivity.findViewById(R.id.back_btn);
        String[] date_format_strings = mActivity.getResources().getStringArray(
                R.array.date_format_values);
        formatIndex = Tools.getDateFormat();
        mDateFormatAdapter = new DateFormatAdapter(mActivity, date_format_strings,
                formatIndex);
        mLvTimeFormat.setAdapter(mDateFormatAdapter);
        Log.i(TAG, "--formatIndex:" + formatIndex);
        mLvTimeFormat.setSelection(formatIndex);
    }

    @Override
    public void initData(Activity activity) {
        mTvBackTitle.setText(mActivity.getResources().getString(R.string.title_date_format));
    }

    @Override
    public void refreshUI(View view) {

    }

    @Override
    public void initListener() {
        mLvTimeFormat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                formatIndex = position;
                Tools.commitDateFormat(formatIndex);
                mDateFormatAdapter.setSelect(formatIndex);
                mDateFormatAdapter.notifyDataSetChanged();
                mActivity.setResult(CommonConsts.REFRESH_DATEFORMAT);
                mActivity.finish();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.finish();
            }
        });
    }
}
