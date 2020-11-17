package com.ctv.settings.general;

import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.L;
import com.cultraview.tv.CtvTvManager;

public class HdmiOutActivity extends BaseActivity implements View.OnClickListener {
    private final String TAG = "HdmiOutActivity";
    private View viewById;
    private TextView textview;
    private int hdmiIndex = 0;
    private String[] hdmiString = {"AUTO", "720P", "2K", "4K"};

    private String hdmiOutResolute = "AUTO";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_usb);
        viewById = findViewById(R.id.back_btn);
        textview = (TextView) findViewById(R.id.back_title);
        textview.setText(getResources().getString(R.string.item_hdmi));
        ListView listView = (ListView) findViewById(R.id.lv_share_usb);
        viewById.setOnClickListener(this);

        try {
            hdmiOutResolute = CtvTvManager.getInstance().getEnvironment("HdmiOutResolute");
        } catch (Exception e) {

        }
        if (!TextUtils.isEmpty(hdmiOutResolute)) {
            if (hdmiOutResolute.equals("AUTO") || hdmiOutResolute.equals("auto")) {
                hdmiIndex = 0;
            } else if (hdmiOutResolute.equals("4k") || hdmiOutResolute.equals("4K")) {
                hdmiIndex = 3;
            } else if (hdmiOutResolute.equals("2k") || hdmiOutResolute.equals("2K")) {
                hdmiIndex = 2;
            } else {
                hdmiIndex = 1;
            }
        } else {
            hdmiIndex = 0;
        }

        ShareUsbApdater shareUsbApdater = new ShareUsbApdater(this, hdmiString, hdmiIndex);
        listView.setAdapter(shareUsbApdater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                setHdmi(position);
                setResult(CommonConsts.REQUEST_HDMI_OUT_OPTIONS);
                finish();
            }
        });
    }

    public void setHdmi(int index) {
        Log.d(TAG, "setHdmi-->" + index);
        try {
            CtvTvManager.getInstance().setTvosCommonCommand("SetNovaTekHtxMode#" + index);
            CtvTvManager.getInstance().setEnvironment("HdmiOutResolute", hdmiString[index]);
        } catch (Exception e) {
            Log.e(TAG, "setHdmi CtvTvManager Exception:" + e);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }

    }
}
