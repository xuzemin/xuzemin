package com.hht.middleware.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hht.middleware.R;
import com.hht.middleware.base.BaseFragment;
import com.hht.middleware.dialog.IntroduceDialog;

/**
 * Author: chenhu
 * Time: 2019/12/19 15:08
 * Description do somethings
 */
public class DeviceFragment extends BaseFragment implements View.OnClickListener {
    private Button mCommonButton, mDeviceButton;

    public static DeviceFragment newInstance() {
        Bundle args = new Bundle();
        DeviceFragment fragment = new DeviceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {
        mCommonButton = findViewById(R.id.fragment_device_common_bt);
        mDeviceButton = findViewById(R.id.fragment_device_device_bt);
        mCommonButton.setOnClickListener(this);
        mDeviceButton.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_device_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog = new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(180);
        introduceDialog.show(getString(R.string.fragment_device_introduce));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_device_common_bt:
                replaceFragment(CommonFragment.newInstance());
                break;
            case R.id.fragment_device_device_bt:
                replaceFragment(DeviceChildFragment.newInstance());
                break;

        }
    }
}
