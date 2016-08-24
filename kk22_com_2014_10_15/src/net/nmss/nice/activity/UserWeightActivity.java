package net.nmss.nice.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.biz.HttpHelpers;
import net.nmss.nice.widget.wheel_view.ArrayWheelAdapter;
import net.nmss.nice.widget.wheel_view.WheelView;

public class UserWeightActivity extends BaseActivity {

	private TextView title_right_tv;
	private WheelView year;
	private WheelView month;
	private TextView title_left_img;
	private TextView tv_weight;
	private String text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.complete_userinfo_weight);
		text = getIntent().getExtras().getString("weight");
		initViews();
		setListener();
	}

	private void initViews() {
		title_right_tv = (TextView) findViewById(R.id.title_right_tv);
		year = (WheelView) findViewById(R.id.year);
		month = (WheelView) findViewById(R.id.month);
		title_left_img = (TextView) findViewById(R.id.title_left_img);
		tv_weight = (TextView) findViewById(R.id.user_property_text);
		tv_weight.setText(text);
	}

	@Override
	public void onResume() {
		NiceUserInfo info = NiceUserInfo.getInstance();
		if(!TextUtils.isEmpty(info.getWeight()) && text.equals("当前体重(KG)")){
			DoubleAlertDialog(info.getWeight());
		}else if(!TextUtils.isEmpty(info.getTargetWeight()) && text.equals("目标体重(KG)")){
			DoubleAlertDialog(info.getTargetWeight());
		}else{
			DoubleAlertDialog( "60.0");
		}
		super.onResume();
	}
	
	private void setListener() {
		title_left_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	/**
	 * 体重
	 * 
	 * @param id
	 * @param value
	 */
	private void DoubleAlertDialog(String value) {

		ArrayList<String> list1 = new ArrayList<String>();
		for (int i = 0; i <= 60; i++) {
			list1.add(String.valueOf(30 + i));
		}
		String[] weights = list1.toArray(new String[list1.size()]);

		ArrayList<String> list2 = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list2.add("." + i);
		}
		String[] decimals = list2.toArray(new String[list2.size()]);

		int weightIndex = 0;
		int decimalIndex = 0;
		if (!TextUtils.isEmpty(value)) {
			String[] values = value.split("\\.");
			weightIndex = list1.indexOf(values[0]);
			decimalIndex = list2.indexOf("." + values[1]);
		}

		year.setAdapter(new ArrayWheelAdapter<String>(weights));

		month.setAdapter(new ArrayWheelAdapter<String>(decimals));

		year.setCurrentItem(weightIndex);
		month.setCurrentItem(decimalIndex);

		title_right_tv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				int currentWeight = year.getCurrentItem();
				String weightData = year.getTextItem(currentWeight);

				int currentDecimal = month.getCurrentItem();
				String decimalData = month.getTextItem(currentDecimal);
				
				String weight = weightData + decimalData ;
				if(text.equals("当前体重(KG)")){
					NiceUserInfo.getInstance().setWeight(weight);
				}else if(text.equals("目标体重(KG)")){
					NiceUserInfo.getInstance().setTargetWeight(weight);
				}
				HttpHelpers.updateUserInfo();
				finish();
			}
		});
	}

}
