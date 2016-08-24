package net.nmss.nice.activity;

import java.util.ArrayList;
import java.util.List;

import net.nmss.nice.R;
import net.nmss.nice.utils.NiceConstants;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class WelcomeActivity extends BaseActivity implements View.OnClickListener{
	private ViewPager mViewPager;
	private ViewPagerAdapter vpAdapter;
	private List<View> views = new ArrayList<View>();;
	private View[] dotView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_layout);
		initViews();
	}

	private void initViews() {
		/** 初始化viewPager */
		mViewPager = (ViewPager) findViewById(R.id.vp_welcome);
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		int[] picResId = { R.drawable.guide1, R.drawable.guide2,R.drawable.guide3};
		// 初始化引导图片列表
		for (int i = 0; i < picResId.length-1; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setBackgroundResource(picResId[i]);
			views.add(iv);
		}
		View view = View.inflate(this, R.layout.welcome_last_page_layout, null);
		view.setBackgroundResource(picResId[picResId.length-1]);
		view.findViewById(R.id.btn_welcome_last_page).setOnClickListener(this);
		views.add(view);
		vpAdapter = new ViewPagerAdapter(views);
		mViewPager.setAdapter(vpAdapter);
		mViewPager.setOnPageChangeListener(new VPOnPagerChangeListener());
		setCurrentDot(0);
	}

	private void setCurrentDot(int index) {
		if (dotView == null) {
			int[] dotViewId = { R.id.view_dot_one_welcome,
					R.id.view_dot_two_welcome, R.id.view_dot_three_welcome};
			dotView = new View[dotViewId.length];
			for (int i = 0; i < dotViewId.length; i++) {
				dotView[i] = findViewById(dotViewId[i]);
			}
		}

		for (int i = 0; i < dotView.length; i++) {
			if (i != index) {
				dotView[i].setBackgroundResource(R.drawable.welcome_dot_press);
			} else {
				dotView[i].setBackgroundResource(R.drawable.welcome_dot_normal);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_welcome_last_page:
			NiceConstants.LoginType = "游客";
			getLoginInfo();
			Intent login = new Intent(WelcomeActivity.this,HomeActivity.class);
			startActivity(login);
			this.finish();
			break;
		}
	}
	
	class ViewPagerAdapter extends PagerAdapter {
		private List<View> views;

		public ViewPagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public int getCount() {
			if (views != null) {
				return views.size();
			}
			return 0;
		}

		// 初始化position位置的界面
		@Override
		public Object instantiateItem(View container, int position) {
			((ViewPager) container).addView(views.get(position), 0);
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
	}

	class VPOnPagerChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
			setCurrentDot(position);
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}

	}
}
