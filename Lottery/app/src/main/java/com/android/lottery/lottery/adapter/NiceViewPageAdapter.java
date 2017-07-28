package com.android.lottery.lottery.adapter;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;


public class NiceViewPageAdapter extends FragmentStatePagerAdapter {

	public List<Fragment> mFragments = new ArrayList<Fragment>();//ViewPager里的Fragments

	public NiceViewPageAdapter(FragmentActivity activity) {
		super(activity.getSupportFragmentManager());
	}
	
	public void addFragment(List<Fragment> fragments) {
		if(fragments == null)
			return;
		if(fragments.equals(this.mFragments)) {
			return;
		}
		this.mFragments = fragments;
		this.notifyDataSetChanged();
	}
	
	public void clear() {
		mFragments.clear();
	}
	
	@Override
	public Fragment getItem(int position) {
		return mFragments.get(position);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	//

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		super.destroyItem(container, position, object);
	}	
}
