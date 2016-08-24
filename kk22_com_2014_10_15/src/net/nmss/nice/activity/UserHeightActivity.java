package net.nmss.nice.activity;

import java.util.ArrayList;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.biz.HttpHelpers;
import net.nmss.nice.widget.wheel_view.ArrayWheelAdapter;
import net.nmss.nice.widget.wheel_view.WheelView;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

public class UserHeightActivity extends BaseActivity {

	private WheelView height;
	private ArrayList<String> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_userinfo_height);
		initViews();
	}

	@Override
	public void onResume() {
		String height = NiceUserInfo.getInstance().getHeight();
		if (!TextUtils.isEmpty(height)) {
			initWheelView(height);
		} else {
			initWheelView("160");
		}
		super.onResume();
	}

	public void doClick(View v) {
		switch (v.getId()) {
		case R.id.title_right_tv://确定
			int cur = height.getCurrentItem();
			String data = height.getTextItem(cur);
			
			NiceUserInfo.getInstance().setHeight(data);
			HttpHelpers.updateUserInfo();
			finish();
			break;
		case R.id.title_left_img://返回
			finish();
			break;
		default:
			break;
		}
	}
	
	private void initViews() {
		height = (WheelView) findViewById(R.id.height);
	}
	
	private void initWheelView(String value) {

		list = new ArrayList<String>();
		for (int i = 0; i <= 60; i++) {
			list.add(String.valueOf(140 + i));
		}

		String[] arrs = list.toArray(new String[list.size()]);

		height.setAdapter(new ArrayWheelAdapter<String>(arrs));

		int index = 0;
		if (!TextUtils.isEmpty(value)) {

			index = list.indexOf(value);
		}
		height.setCurrentItem(index);
	}
}
