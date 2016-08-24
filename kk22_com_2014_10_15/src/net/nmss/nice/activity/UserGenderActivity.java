package net.nmss.nice.activity;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.biz.HttpHelpers;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class UserGenderActivity extends BaseActivity implements
		View.OnClickListener {

	private TextView title_left_img;
	private TextView title_right_tv;
	private RadioGroup gender_group;
	private RadioButton male;
	private RadioButton female;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_gender);
		init();
		setListener();
	}

	private void init() {

		// showInMiddle = (ViewGroup) View.inflate(activity,
		// R.layout.user_gender, null);
		title_left_img = (TextView) findViewById(R.id.title_left_img);
		title_right_tv = (TextView) findViewById(R.id.title_right_tv);
		male = (RadioButton) findViewById(R.id.male);
		female = (RadioButton) findViewById(R.id.female);
		gender_group = (RadioGroup) findViewById(R.id.gender_group);

	}

	private void setListener() {
		// TODO Auto-generated method stub

		title_left_img.setOnClickListener(this);
		title_right_tv.setOnClickListener(this);
		gender_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
				case R.id.male:
					NiceUserInfo.getInstance().setGender("男");
					break;
				case R.id.female:
					NiceUserInfo.getInstance().setGender("女");
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img:
			finish();
			break;
		case R.id.title_right_tv:
			if (male.isChecked()) {
				NiceUserInfo.getInstance().setGender("男");
			} else {
				NiceUserInfo.getInstance().setGender("女");
			}
			HttpHelpers.updateUserInfo();
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {

		String g = NiceUserInfo.getInstance().getGender();
		if ("男".equals(g)) {
			male.setChecked(true);
		} else if ("女".equals(g)){
			female.setChecked(true);
		}
		super.onResume();
	}

}
