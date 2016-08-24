package net.nmss.nice.activity;

import java.util.ArrayList;
import java.util.List;

import net.nmss.nice.R;
import net.nmss.nice.adapters.NiceViewPageAdapter;
import net.nmss.nice.fragments.BookFragments;
import net.nmss.nice.fragments.KernelFragments;
import net.nmss.nice.fragments.MeFragments;
import net.nmss.nice.fragments.RoomFragment;
import net.nmss.nice.share.Conf;
import net.nmss.nice.utils.LogUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

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
	}

	private void changeToKernelState() {
		if (currentTab == KERNEL)
			return;
		if (kernelFragments == null) {
			kernelFragments = new ArrayList<Fragment>();
			kernelFragments.add(new KernelFragments());
		}
		mViewPager.removeAllViews();
		mViewPageAdapter.addFragment(kernelFragments);
		mViewPager.setCurrentItem(0);
		currentTab = KERNEL;
	}

	private void changeToRoomState() {
		if (currentTab == ROOM)
			return;
		if (roomFragments == null) {
			roomFragments = new ArrayList<Fragment>();
			roomFragments.add(new RoomFragment());
		}
		mViewPager.removeAllViews();
		mViewPageAdapter.addFragment(roomFragments);
		mViewPager.setCurrentItem(0);
		currentTab = ROOM;
	}

	private void changeToBookState() {
		if (currentTab == BOOK)
			return;
		if (bookFragments == null) {
			bookFragments = new ArrayList<Fragment>();
			bookFragments.add(new BookFragments());
		}
		mViewPager.removeAllViews();
		mViewPageAdapter.addFragment(bookFragments);
		mViewPager.setCurrentItem(0);
		currentTab = KERNEL;
	}

	private void changeToMeState() {
		if (currentTab == ME)
			return;
		if (meFragments == null) {
			meFragments = new ArrayList<Fragment>();
			meFragments.add(new MeFragments());
		}
		mViewPager.removeAllViews();
		mViewPageAdapter.addFragment(meFragments);
		mViewPager.setCurrentItem(0);
		currentTab = KERNEL;
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
			Intent intent = new Intent();
			intent.setClass(HomeActivity.this,MessageActivity.class);
			startActivity(intent);
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
