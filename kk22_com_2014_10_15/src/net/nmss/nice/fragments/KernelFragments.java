package net.nmss.nice.fragments;

import net.nmss.nice.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class KernelFragments extends Fragment {
	private final static String LOG_TAG = "KernelFragments";
	private View mRootView;
	private Context mContext;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO 自动生成的方法存根
		this.mContext = activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		mRootView = inflater.inflate(R.layout.kernel_fragment_layout,
				null);
		return mRootView;
	}

}
