package com.android.lottery.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;

import com.android.lottery.R;
import com.android.lottery.adapter.NiceViewPageAdapter;

import java.util.ArrayList;
import java.util.List;

public class KernelFragments extends Fragment {
	private final static String LOG_TAG = "KernelFragments";
	private View mRootView;
	private Context mContext;
	private ViewPager mViewPager;
	private MyPagerAdapter mViewPageAdapter;
	private GridView gridview;
	private List<View> listViews;
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
		gridview = (GridView) mRootView.findViewById(R.id.gview);



		mViewPager = (ViewPager) mRootView.findViewById(R.id.kernel_viewpager);
		listViews = new ArrayList<>();
		ImageView imageView = new ImageView(mContext);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setLayoutParams(new Gallery.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		imageView.setBackgroundResource(R.mipmap.aaa);
		listViews.add(imageView);
		imageView.setBackgroundResource(R.mipmap.first_activity);
		listViews.add(imageView);
		mViewPageAdapter = new MyPagerAdapter(listViews);
		mViewPager.setCurrentItem(0);
		mViewPager.setAdapter(mViewPageAdapter);
		mViewPager.setOffscreenPageLimit(5);
		return mRootView;
	}


	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {

		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
}
