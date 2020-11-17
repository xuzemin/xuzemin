package com.ctv.settings.general;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.DataTool;

public class EyePlusStatusActivity extends BaseActivity implements View.OnClickListener {
    private View viewById;
    private TextView textview;
    private int eye_plus;
    private EyePlusApdater eyePlusApdater;
    private String[] OPTIONS_VALS;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye_plus);
        viewById = findViewById(R.id.back_btn);
        textview = (TextView) findViewById(R.id.back_title);
        textview.setText(getResources().getString(R.string.item_eye_plus));
        ListView listView = (ListView) findViewById(R.id.lv_eye_plus);
        viewById.setOnClickListener(this);
        if(DataTool.IS_AH_EDU_QD){
            OPTIONS_VALS = getResources().getStringArray(R.array.eye_plus_mode_Light);
            eye_plus = GreneralUtils.getInstance(getApplicationContext()).getEyePlusIndex_Light();
        }else{
            OPTIONS_VALS = getResources().getStringArray(R.array.eye_plus_mode);
            eye_plus = GreneralUtils.getInstance(getApplicationContext()).getEyePlusIndex();
        }
        eyePlusApdater = new EyePlusApdater(getApplicationContext(), OPTIONS_VALS, eye_plus);
        listView.setAdapter(eyePlusApdater);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            if(DataTool.IS_AH_EDU_QD) {
                GreneralUtils.getInstance(getApplicationContext()).setEyePlusStatus_Light(i);
            }else{
                GreneralUtils.getInstance(getApplicationContext()).setEyePlusStatus(i);
            }
            setResult(CommonConsts.REQUEST_EYE_PLUS_OPTIONS);
            finish();
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            setResult(CommonConsts.REQUEST_EYE_PLUS_OPTIONS);
            finish();
        }
    }
}
