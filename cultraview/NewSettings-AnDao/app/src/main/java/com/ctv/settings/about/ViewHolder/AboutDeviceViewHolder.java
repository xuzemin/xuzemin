package com.ctv.settings.about.ViewHolder;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.about.activity.AboutActivity;
import com.ctv.settings.about.activity.BootOptionsActivity;
import com.ctv.settings.about.activity.RestoreFactoryActivity;
import com.ctv.settings.about.activity.SystemUpdateActivity;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.utils.CommonConsts;
import com.ctv.settings.utils.DataTool;
import com.ctv.settings.utils.Tools;

import java.util.Arrays;

public class AboutDeviceViewHolder extends BaseViewHolder implements View.OnClickListener {
    private final String TAG = "AboutDeviceViewHolder";
    private View rl_update_system;
    private View mBootOptions;    //
    private TextView mTvBootOptions; //
    private TextView aboutitem;
    final int REQUEST_BOOT_OPTIONS = 1;
    private View systemrestorefactory;
    private View about;
    private String[] options_vals;
    private int boot_option;
    public TextView bootoptionitem;
    private static final String[] ah_boot_vals = {"ANDROID", "OPS", "HDMI1", "HDMI2", "HDMI3", "VGA"};
    private static final String[] ah_cx_boot_vals = {"ANDROID", "OPS", "HDMI3", "HDMI2", "VGA"}; //"HDMI1",去掉前置hdmi，hdmi3名称改为hdmi1
    private static final String[] mh_boot_vals = {"ANDROID", "ATV", "DTV", "HDMI1", "HDMI2", "OPS", "HDMI3", "DP", "AV1", "YPBPR", "VGA"};
    public final static boolean IS_AH_CX = TextUtils.equals(SystemProperties.get("ro.build.display.id", ""), "CN8386_AH_CX");
    private boolean ahBoard;

    public AboutDeviceViewHolder(Activity activity) {
        super(activity);
        initData(activity);


    }

    @Override
    public void initUI(Activity activity) {
        mBootOptions = mActivity.findViewById(R.id.rl_boot_options);
        systemrestorefactory = mActivity.findViewById(R.id.rl_system_restore_factory);
        mTvBootOptions = (TextView) mActivity.findViewById(R.id.tv_boot_options);
        about = mActivity.findViewById(R.id.rl_about_tv);

        rl_update_system = mActivity.findViewById(R.id.rl_update_system);
        bootoptionitem = (TextView) mActivity.findViewById(R.id.tv_boot_options);
    }

    @Override
    public void initListener() {
        mBootOptions.setOnClickListener(this);
        systemrestorefactory.setOnClickListener(this);
        about.setOnClickListener(this);

        rl_update_system.setOnClickListener(this);
    }


    @Override
    public void refreshUI(View view) {
        int id = view.getId();
        if (id == R.id.tv_boot_options) {
            mTvBootOptions.setText(options_vals[boot_option]);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == CommonConsts.BOOTOPTION_ITEM1) {
            initData(mActivity);
        }
    }

    @Override
    public void initData(Activity activity) {
        boot_option = 0;
        options_vals = mActivity.getResources().getStringArray(R.array.starting_up_option_vals);
        ahBoard = DataTool.isAHBoard();

        if (ahBoard) {
            if (IS_AH_CX) {
                Log.d("keww", "IS_AH_CX = " + IS_AH_CX);
                options_vals = activity.getResources().getStringArray(R.array.starting_up_ah_cx_option_vals);
                setSelectAH_CX();
            } else {
                Log.d("keww", "ahBoard = " + ahBoard);
                options_vals = activity.getResources().getStringArray(R.array.starting_up_ah_option_vals);
                setSelectAH();
            }
        } else {
            options_vals = activity.getResources().getStringArray(R.array.starting_up_option_vals);
            setSelectMH();
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

    /**
     * AH 选中index
     */
    private void setSelectAH() {
        String bootInputsource = SystemProperties.get("persist.sys.boot.source", "ANDROID").trim();
        Log.d(TAG, "bootInputsource:" + bootInputsource);
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
        String bootInputsource = SystemProperties.get("persist.sys.boot.source", "ANDROID").trim();
        Log.d(TAG, "bootInputsource:" + bootInputsource);
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
                //case "HDMI1":
                //    boot_option = 2;
                //    break;
                case "HDMI3":
                    boot_option = 2;
                    break;
                case "HDMI2":
                    boot_option = 3;
                    break;
                case "VGA":
                    boot_option = 4;
                    break;
            }
        }
        Log.d(TAG, "mInputSource  AH_CX:" + boot_option);
    }

    /**
     * AH 选中index
     */
    private void setSelectMH() {
        String bootInputsource ;
        String board = SystemProperties.get("ro.product.board");
        if (board.equals("CN8386_BH")) {
            bootInputsource = SystemProperties.get("persist.sys.boot.source", "OPS");
        }else {
            bootInputsource = SystemProperties.get("persist.sys.boot.source", "ANDROID");
        }
        if (options_vals != null) {
            boot_option = Arrays.binarySearch(mh_boot_vals, bootInputsource);
        }
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
}
