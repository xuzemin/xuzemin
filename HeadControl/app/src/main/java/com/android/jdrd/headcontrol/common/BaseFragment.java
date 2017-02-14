package com.android.jdrd.headcontrol.common;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/10/23 0023.
 */

public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected View mView;
    public BaseFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public BaseFragment(Context context) {
        mContext=context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initView();
        initData();
        initEvent();
        return mView;
    }

    public abstract void initView();
    public abstract void initData();
    public abstract void initEvent();

    public View findViewById(int id) {
        return mView.findViewById(id);
    }

}
