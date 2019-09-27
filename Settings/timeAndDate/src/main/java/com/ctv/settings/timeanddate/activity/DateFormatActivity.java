package com.ctv.settings.timeanddate.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.ctv.settings.timeanddate.R;
import com.ctv.settings.timeanddate.holder.DateFormatViewHolder;

public class DateFormatActivity extends AppCompatActivity {

    private DateFormatViewHolder mTimeFormatViewHolder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dateformat);
        mTimeFormatViewHolder = new DateFormatViewHolder(this);
    }
}
