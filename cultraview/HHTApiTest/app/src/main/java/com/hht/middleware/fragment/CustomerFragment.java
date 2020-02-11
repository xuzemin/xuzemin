package com.hht.middleware.fragment;

import android.os.Bundle;

import com.hht.middleware.R;
import com.hht.middleware.base.BaseFragment;
import com.hht.middleware.base.FragmentUtils;
import com.hht.middleware.dialog.IntroduceDialog;

/**
 * Author: chenhu
 * Time: 2019/12/13 9:57
 * Description do somethings
 */
public class CustomerFragment extends BaseFragment {

    public static CustomerFragment newInstance() {
        Bundle args = new Bundle();
        CustomerFragment fragment = new CustomerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_customer_layout;
    }

    @Override
    public void OnBackListener() {
        replaceFragment(MainFragment.newInstance());
    }

    @Override
    public void OnIntroduceListener() {
        IntroduceDialog introduceDialog =new IntroduceDialog(mActivity);
        introduceDialog.setIntroduceDialogHeight(220);
        introduceDialog.show(getString(R.string.fragment_customer_introduce));

    }
}
