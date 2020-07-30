package com.protruly.floatwindowlib.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.ui.ChangeSignalLayout;

/**
 * Created by Carson_Ho on 16/7/22.
 */
public class SignalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signal_layout, container, false);
        initView();
        return view;
    }

    private void initView(){

    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
