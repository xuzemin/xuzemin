package com.android.lottery.lottery.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.lottery.lottery.R;
import com.android.lottery.lottery.adapter.NiceViewPageAdapter;
import com.android.lottery.lottery.fragment.BookFragments;
import com.android.lottery.lottery.fragment.KernelFragments;
import com.android.lottery.lottery.fragment.MeFragments;
import com.android.lottery.lottery.fragment.RoomFragment;
import com.android.lottery.lottery.share.Conf;
import com.android.lottery.lottery.util.LogUtil;
import com.baidu.frontia.Frontia;

public class HomeActivity extends BaseActivity {
	private final static String TAG_LOG = "HomeActivity";
	private ViewPager mViewPager;
	private NiceViewPageAdapter mViewPageAdapter;

	private List<Fragment> kernelFragments;// 柠檬中心
	private List<Fragment> roomFragments;// 圈子
	private List<Fragment> bookFragments;// 柠檬宝典
	private List<Fragment> meFragments;// 我

	private final int KERNEL = 0;
	private final int ROOM = 1;
	private final int BOOK = 2;
	private final int ME = 3;
	private int currentTab = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_activity_layout);
		Frontia.init(this.getApplicationContext(), Conf.APIKEY);
		initViewPage();
		changeToKernelState();
		LogUtil.i(TAG_LOG, "onCreate");
	}

	private void initViewPage() {
		mViewPager = (ViewPager) findViewById(R.id.zoom_viewpager);
		mViewPageAdapter = new NiceViewPageAdapter(this);
		mViewPager.setAdapter(mViewPageAdapter);
		mViewPager.setOffscreenPageLimit(5);
		kernelFragments = new ArrayList<>();
		kernelFragments.add(new KernelFragments());
		kernelFragments.add(new RoomFragment());
		kernelFragments.add(new BookFragments());
		kernelFragments.add(new MeFragments());
		mViewPageAdapter.addFragment(kernelFragments);
		mViewPager.setCurrentItem(0);
		currentTab = KERNEL;
		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				switch (position){
					case KERNEL:
						mViewPager.setCurrentItem(KERNEL);
						((RadioGroup)findViewById(R.id.radioGroup_home_bottom_nav)).check(R.id.radio_btn_1);
						currentTab = KERNEL;
						break;
					case BOOK:
						mViewPager.setCurrentItem(BOOK);
						((RadioGroup)findViewById(R.id.radioGroup_home_bottom_nav)).check(R.id.radio_btn_3);
						currentTab = BOOK;
						break;
					case ROOM:
						mViewPager.setCurrentItem(ROOM);
						((RadioGroup)findViewById(R.id.radioGroup_home_bottom_nav)).check(R.id.radio_btn_2);
						currentTab = ROOM;
						break;
					case ME:
						mViewPager.setCurrentItem(ME);
						((RadioGroup)findViewById(R.id.radioGroup_home_bottom_nav)).check(R.id.radio_btn_4);
						currentTab = ME;
						break;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}

	private void changeToKernelState() {
		if (currentTab == KERNEL)
			return;
		mViewPager.setCurrentItem(0);
		currentTab = KERNEL;
	}

	private void changeToRoomState() {
		if (currentTab == ROOM)
			return;
		mViewPager.setCurrentItem(1);
		currentTab = ROOM;
	}

	private void changeToBookState() {
		if (currentTab == BOOK)
			return;
		mViewPager.setCurrentItem(2);
		currentTab = BOOK;
	}

	private void changeToMeState() {
		if (currentTab == ME)
			return;
		mViewPager.setCurrentItem(3);
		currentTab = ME;
	}

	public void doClick(View view) {
		switch (view.getId()) {
		case R.id.radio_btn_1:
			changeToKernelState();
			break;
		case R.id.radio_btn_2:
			changeToRoomState();
			break;
		case R.id.radio_btn_3:
			changeToBookState();
			break;
		case R.id.radio_btn_4:
			changeToMeState();
			break;
		case R.id.title_message:
//			Intent intent = new Intent();
//			intent.setClass(HomeActivity.this,MessageActivity.class);
//			startActivity(intent);
			break;
		default:
			break;
		}
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
