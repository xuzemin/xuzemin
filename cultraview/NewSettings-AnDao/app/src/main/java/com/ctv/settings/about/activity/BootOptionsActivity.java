package com.ctv.settings.about.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.about.Adapter.BootoptionsApdater;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.DataTool;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.Tools;
import com.cultraview.tv.CtvCommonManager;
import com.mstar.android.tv.TvCommonManager;

import java.util.Arrays;

public class BootOptionsActivity extends BaseActivity implements View.OnClickListener {
    private View viewById;
    private TextView textview;
    private ImageView boot_option_item_ivs;
    private int boot_option;
    private final String TAG = BootOptionsActivity.class.getSimpleName();
    private String[] options_vals;
    private String[] ah_boot_vals = {"ANDROID", "OPS", "HDMI1", "HDMI2", "HDMI3", "VGA"};
    private String[] ah_cx_boot_vals = {"ANDROID", "OPS", "HDMI3", "HDMI2", "VGA"}; //"HDMI1",去掉前置hdmi，hdmi3名称改为hdmi1
    private String[] mh_boot_vals = {"ANDROID", "ATV", "DTV", "HDMI1", "HDMI2", "OPS", "HDMI3", "DP", "AV1", "YPBPR", "VGA"};
    private boolean ahBoard;
    public final static boolean IS_AH_CX = TextUtils.equals(SystemProperties.get("ro.build.display.id", ""), "CN8386_AH_CX");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_options);
        viewById = findViewById(R.id.back_btn);
        textview = (TextView) findViewById(R.id.back_title);
        textview.setText(getResources().getString(R.string.boot_options));

        boot_option_item_ivs = (ImageView) findViewById(R.id.boot_option_item_ivs);

        ListView listView = (ListView) findViewById(R.id.lv_boot_options);
        viewById.setOnClickListener(this);
        ahBoard = DataTool.isAHBoard();
        if (ahBoard) {
            if (IS_AH_CX) {
                Log.d("keww1", "IS_AH_CX = " + IS_AH_CX);
                options_vals = getResources().getStringArray(R.array.starting_up_ah_cx_option_vals);
                setSelectAH_CX();
            } else {
                Log.d("keww1", "ahBoard 1= " + ahBoard);
                options_vals = getResources().getStringArray(R.array.starting_up_ah_option_vals);
                setSelectAH();
            }
        } else {
            options_vals = getResources().getStringArray(R.array.starting_up_option_vals);
            setSelectMH();
        }


        BootoptionsApdater bootoptionsApdater = new BootoptionsApdater(this, options_vals, boot_option);
        listView.setAdapter(bootoptionsApdater);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ahBoard) {
                    if (IS_AH_CX) {
                        Log.d(TAG, "AH_CX_onItemClick:" + ah_cx_boot_vals[i]);
                        SystemProperties.set("persist.sys.boot.source", ah_cx_boot_vals[i]);
                    } else {
                        Log.d(TAG, "onItemClick:" + ah_boot_vals[i]);
                        SystemProperties.set("persist.sys.boot.source", ah_boot_vals[i]);
                    }
                } else {
                    SystemProperties.set("persist.sys.boot.source", mh_boot_vals[i]);
                }
                setResult(CommonConsts.BOOTOPTION_ITEM1);//向AboutDeviceActivity返回RESULT_OK.
                finish();
            }
        });
    }

    /**
     * AH 选中index
     */
    private void setSelectAH() {
        String bootInputsource = SystemProperties.get("persist.sys.boot.source", "ANDROID");
        if (options_vals != null) {
            boot_option = Arrays.binarySearch(ah_boot_vals, bootInputsource);
        }
        if (boot_option < 0) {
            switch (bootInputsource) {
                case "ANDROID":
                    boot_option = 0;
                    break;
                case "OPS":
                    boot_option = 1;
                    break;
                case "HDMI1":
                    boot_option = 2;
                    break;
                case "HDMI2":
                    boot_option = 3;
                    break;
                case "HDMI3":
                    boot_option = 4;
                    break;
                case "VGA":
                    boot_option = 5;
                    break;
            }
        }
        Log.d(TAG, "mInputSource  AH:" + boot_option);
    }

    /**
     * AH_CX 选中index
     */
    private void setSelectAH_CX() {
        String bootInputsource = SystemProperties.get("persist.sys.boot.source", "ANDROID");
        if (options_vals != null) {
            boot_option = Arrays.binarySearch(ah_cx_boot_vals, bootInputsource);
        }
        Log.d("keww", "bootInputsource = " + bootInputsource);
        if (boot_option < 0) {
            switch (bootInputsource) {
                case "ANDROID":
                    boot_option = 0;
                    break;
                case "OPS":
                    boot_option = 1;
                    break;
                case "HDMI2":
                    boot_option = 3;
                    break;
                case "HDMI3":
                    boot_option = 2;
                    break;
                case "VGA":
                    boot_option = 4;
                    break;
            }
        }
        Log.d(TAG, "mInputSource  AH:" + boot_option);
    }

    /**
     * AH 选中index
     */
    private void setSelectMH() {
        String bootInputsource ;
        String board = SystemProperties.get("ro.product.board");
        if (board.equals("CN8386_BH")) {
            bootInputsource = SystemProperties.get("persist.sys.boot.source", "OPS");
        } else {
            bootInputsource = SystemProperties.get("persist.sys.boot.source", "ANDROID");
        }
        if (options_vals != null) {
            boot_option = Arrays.binarySearch(mh_boot_vals, bootInputsource);
        }
//        {"ANDROID", "ATV", "DTV", "HDMI1", "HDMI2", "OPS", "HDMI3", "DP", "AV1", "YPBPR", "VGA"};
        if (boot_option < 0) {
            switch (bootInputsource) {
                case "ANDROID":
                    boot_option = 0;
                    break;
                case "ATV":
                    boot_option = 1;
                    break;
                case "DTV":
                    boot_option = 2;
                    break;
                case "HDMI1":
                    boot_option = 3;
                    break;
                case "HDMI2":
                    boot_option = 4;
                    break;
                case "OPS":
                    boot_option = 5;
                    break;
                case "HDMI3":
                    boot_option = 6;
                    break;
                case "DP":
                    boot_option = 7;
                    break;
                case "AV1":
                    boot_option = 8;
                    break;
                case "YPBPR":
                    boot_option = 9;
                    break;
                case "VGA":
                    boot_option = 10;
                    break;
            }
        }
        Log.d(TAG, "mInputSource  MH:" + boot_option);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }
    }


}
