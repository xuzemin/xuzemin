package com.ctv.settings.general;

import android.os.Bundle;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.L;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import static com.ctv.settings.utils.DataTool.isAHBoard;

public class ShareUsbActivity extends BaseActivity implements View.OnClickListener {
    private View viewById;
    private TextView textview;
    private int share_usb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_usb);
        viewById = findViewById(R.id.back_btn);
        textview = (TextView) findViewById(R.id.back_title);
        textview.setText(getResources().getString(R.string.item_public_usb));
        ListView listView = (ListView) findViewById(R.id.lv_share_usb);
        viewById.setOnClickListener(this);

        String[] OPTIONS_VALS = getResources().getStringArray(R.array.share_usb_mode);
        String isShareUsb = GreneralUtils.getInstance(this).getUsbShareStatus();
//        String optionValue = Tools.getBootLoader(this);//
        if (isShareUsb.equals("0")) {
            share_usb = 0;
        } else if (isShareUsb.equals("1")) {
            share_usb = 1;
        } else if (isShareUsb.equals("2")) {
            share_usb = 2;
        } else {
            share_usb = 0;
        }
        ShareUsbApdater shareUsbApdater = new ShareUsbApdater(this, OPTIONS_VALS, share_usb);
        listView.setAdapter(shareUsbApdater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = i+"";
                SystemProperties.set("persist.sys.share_usb_mode",value);
                setResult(CommonConsts.REQUEST_SHARE_USB_OPTIONS);
                if(isAHBoard()) {
                    try {
                        switch (i) {
                            case 0:
                            case 1:
                                TvManager.getInstance().setTvosCommonCommand("ConfigTca9539#10#0");
                                break;
                            case 2:
                                TvManager.getInstance().setTvosCommonCommand("ConfigTca9539#10#1");
                                break;
                        }
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                        L.e("e" + e.toString());
                    }
                }
                finish();
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }

    }
}
