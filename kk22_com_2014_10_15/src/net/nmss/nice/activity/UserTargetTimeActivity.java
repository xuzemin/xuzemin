package net.nmss.nice.activity;

import java.util.ArrayList;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.biz.HttpHelpers;
import net.nmss.nice.utils.DateUtils;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.widget.wheel_view.ArrayWheelAdapter;
import net.nmss.nice.widget.wheel_view.OnWheelScrollListener;
import net.nmss.nice.widget.wheel_view.WheelView;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class UserTargetTimeActivity extends BaseActivity {

	private WheelView monthView;
	private WheelView yearView;
	private TextView title_right_tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_target_time_layout);
		initViews();
	}

	@Override
	public void onResume() {

		NiceUserInfo info = NiceUserInfo.getInstance();
		getYearAndMonth();
		if(!TextUtils.isEmpty(info.getTargetTime())&&!"null".equals(info.getTargetTime())){
			String[] timeArr = info.getTargetTime().split("-");
			getDayNum(Integer.parseInt(timeArr[1]));
			TripleAlertDialog(info.getTargetTime(), true);
		}else{
			getDayNum(1);
			LogUtil.e("目标时间", DateUtils.getCurrentYearMouth());
			TripleAlertDialog(DateUtils.getCurrentYearMouth(), true);
		}
		
		super.onResume();
	}
	
	private void initViews() {
		yearView = (WheelView) findViewById(R.id.user_target_time_year);
		monthView = (WheelView) findViewById(R.id.user_target_time_month);
		title_right_tv = (TextView) findViewById(R.id.title_right_tv);
	}
	
	public void doClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img://返回
			finish();
			break;
		default:
			break;
		}
	}
	
	ArrayList<String> year = null;
	String[] years = null;

	ArrayList<String> month = null;
	String[] months = null;

	private void getYearAndMonth() {
		year = new ArrayList<String>();
		for (int i = 0; i <= 121; i++) {
			year.add(String.valueOf(1930 + i));
		}
		 years = year.toArray(new String[year.size()]);

		month = new ArrayList<String>();
		for (int i = 1; i <= 12; i++) {
			if(i <= 9){
				month.add("0"+i+"");
			}else{
				month.add(i+"");
			}
			
		}
		months = month.toArray(new String[month.size()]);
	}

	String[] days = null;
	ArrayList<String> dayList = null;

	/**
	 * 根据从服务器解析出的月份生成对应的天数
	 * 
	 * @param monthNUm
	 */
	private void getDayNum(int monthNUm) {

		dayList = new ArrayList<String>();
		if (monthNUm == 2) {
			for (int i = 1; i <= 28; i++) {
				
				dayList.add(String.valueOf(i));
			}
		} else if (monthNUm == 4 || monthNUm == 6 || monthNUm == 9
				|| monthNUm == 11) {
			for (int i = 1; i <= 30; i++) {
				
				dayList.add(String.valueOf(i));
			}
		} else {
			for (int i = 1; i <= 31; i++) {
				
				dayList.add(String.valueOf(i));
			}

		}
		days = dayList.toArray(new String[dayList.size()]);
	}

	/**
	 * 生日
	 * 
	 * @param id
	 * @param value
	 */
	private void TripleAlertDialog(String value,final boolean isscroll) {

		int yearIndex = 0;
		int monthIndex = 0;
		if (!TextUtils.isEmpty(value)) {
			String[] values = value.split("-");
			yearIndex = year.indexOf(values[0]);
			monthIndex = month.indexOf(values[1]);
			
		}
		
		yearView.setAdapter(new ArrayWheelAdapter<String>(years));
		monthView.setAdapter(new ArrayWheelAdapter<String>(months));
		yearView.setCurrentItem(yearIndex);
		monthView.setCurrentItem(monthIndex);
		
		if(isscroll){
			monthView.addScrollingListener(new OnWheelScrollListener() {
				
				@Override
				public void onScrollingStarted(WheelView wheel) {}
				
				@Override
				public void onScrollingFinished(WheelView wheel) {
					
					int scrollMonthIndex = monthView.getCurrentItem();
					if(dayList!=null)
						dayList.clear();
					getDayNum(scrollMonthIndex+1);
					
				}
			});
		}

		title_right_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				int currentYear = yearView.getCurrentItem();
				String yearData = yearView.getTextItem(currentYear);

				int currentMonth = monthView.getCurrentItem();
				String monthData = monthView.getTextItem(currentMonth);//数字后面带"月"
				
				NiceUserInfo.getInstance().setTargetTime(yearData+"-"+monthData);
				HttpHelpers.updateUserInfo();
				finish();
		}
		});
	}
}
