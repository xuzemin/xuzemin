package com.android.jdrd.headcontrol.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.jdrd.headcontrol.R;


/**
 * Created by Administrator on 2016/10/23 0023.
 */

public class TitleListFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_title01,null);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
