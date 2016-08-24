package net.nmss.nice.activity;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.biz.HttpHelpers;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserNameActivity extends BaseActivity implements View.OnClickListener{
	
	private TextView title_left_img;
	private EditText user_name;
	private TextView title_right_tv;
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				Toast.makeText(UserNameActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case 2:
				finish();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_name_layout);
		initViews();
	}
	
	private void initViews() {
		title_left_img = (TextView) findViewById(R.id.title_left_img);
		title_right_tv = (TextView) findViewById(R.id.title_right_tv);
		user_name = (EditText) findViewById(R.id.user_name);
		String d = NiceUserInfo.getInstance().getName();
		if(d != null && !d.equals("null") && d.length()>=0) {
			user_name.setText(d);
			user_name.setSelection(d.length());
		}
		
		title_left_img.setOnClickListener(this);
		title_right_tv.setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.title_left_img:
			finish();
			break;
		case R.id.title_right_tv:
			String newDeclaration = user_name.getText().toString().trim();
			if(TextUtils.isEmpty(newDeclaration)){
				Toast.makeText(this, "不能为空哦", Toast.LENGTH_SHORT).show();
				return;
			}
			NiceUserInfo.getInstance().setName(newDeclaration);
			HttpHelpers.updataUserName(handler);
			break;
		default:
			break;
		}
	}
}
