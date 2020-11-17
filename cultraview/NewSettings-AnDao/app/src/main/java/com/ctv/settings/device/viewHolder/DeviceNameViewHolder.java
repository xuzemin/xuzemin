package com.ctv.settings.device.viewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.device.data.Constant;
import com.cultraview.tv.utils.CtvCommonUtils;


/**
 * 设置ViewHolder
 *
 * @author wanghang
 * @date 2019/09/17
 */
public class DeviceNameViewHolder extends BaseViewHolder implements View.OnFocusChangeListener, View.OnClickListener {


    private EditText device_name_et;

    private Button device_name_save;

    private Button device_name_cancel;


    private String deviceName;

    public DeviceNameViewHolder(Activity activity) {
        super(activity);
    }


    @Override
    public void initUI(Activity activity) {
        findViews(activity);
        ((TextView)activity.findViewById(R.id.back_title)).setText(mActivity.getString(R.string.item_device_name));
        ((ImageView) activity.findViewById(R.id.back_btn)).setOnClickListener(this);


    }

    /**
     * findViews(The function of the method)
     *
     * @Title: findViews
     * @Description: TODO
     */
    private void findViews(Activity activity) {
        device_name_et = (EditText) activity.findViewById(R.id.device_name_et);
        device_name_save = (Button) activity.findViewById(R.id.device_name_save);
        device_name_cancel = (Button) activity.findViewById(R.id.device_name_cancel);
        device_name_et.setOnFocusChangeListener(this);
        device_name_save.setOnFocusChangeListener(this);
        device_name_cancel.setOnFocusChangeListener(this);
        device_name_save.setOnClickListener(this);
        device_name_cancel.setOnClickListener(this);
        /*String deviceName = (String)SPUtil.getData(ctvContext, Constants.DEVICE_TITLE, "DavinciBoard");
        this.deviceName = deviceName;//DataTool.getDeviceName(ctvContext);*/
        //设备名称
        String deviceName = CtvCommonUtils.getCultraviewProjectInfo(mActivity, "tbl_SoftwareVersion", "DeviceName");
        device_name_et.setText(deviceName);
        device_name_et.selectAll();
    }

    @Override
    public void initListener() {

    }

    @Override
    public void refreshUI(View view) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.device_name_save) {
            saveDate();
        } else if (id == R.id.device_name_cancel || id == R.id.back_btn) {
            mActivity.finish();
        }
    }

    private void saveDate() {

        String name = device_name_et.getText().toString();
        if (TextUtils.isEmpty(name)) {
            device_name_et.setHint(R.string.correct_device_name);
            device_name_et.requestFocus();
        } else {
            setDeviceName(name);
            Intent intent = new Intent();
            intent.putExtra(Constant.deviceName, name);
            mActivity.setResult(Constant.device_name_request_code, intent);
            mActivity.finish();
//            deviceChangeListener.deviceNameChange(name);
//            dismiss();
        }

    }

    @SuppressLint("NewApi")
    private void setDeviceName(String name) {
        CtvCommonUtils.setCultraviewProjectInfo(mActivity, "tbl_SoftwareVersion", "DeviceName",
                device_name_et.getText().toString().trim());
    }

    @Override
    public void onFocusChange(View view, boolean has_focus) {
        int id = view.getId();
        if (id == R.id.device_name_et) {
            if (has_focus) {
                device_name_et.setSelected(true);
                device_name_et.selectAll();
            } else {
                device_name_et.setSelected(false);
            }
        } else if (id == R.id.device_name_save) {
            if (has_focus) {
                device_name_save.setSelected(true);
            } else {
                device_name_save.setSelected(false);
            }
        } else if (id == R.id.device_name_cancel) {
            if (has_focus) {
                device_name_cancel.setSelected(true);
            } else {
                device_name_cancel.setSelected(false);
            }
        }
    }
}
