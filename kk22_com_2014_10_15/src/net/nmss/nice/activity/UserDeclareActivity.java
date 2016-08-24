package net.nmss.nice.activity;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.biz.HttpHelpers;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserDeclareActivity extends BaseActivity implements View.OnClickListener{
	
	private TextView title_left_img;
	private EditText user_declare;
	private TextView title_right_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_declare);
		initViews();
	}
	
	private void initViews() {
		title_left_img = (TextView) findViewById(R.id.title_left_img);
		title_right_tv = (TextView) findViewById(R.id.title_right_tv);
		user_declare = (EditText) findViewById(R.id.user_declare);
		String d = NiceUserInfo.getInstance().getDeclaration();
		if(d != null && !d.equals("null") && d.length()>=0) {
			user_declare.setText(d);
			user_declare.setSelection(d.length());
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
			String newDeclaration = user_declare.getText().toString().trim();
			if(TextUtils.isEmpty(newDeclaration)){
				Toast.makeText(this, "不能为空哦", Toast.LENGTH_SHORT).show();
				return;
			}
			NiceUserInfo.getInstance().setDeclaration(newDeclaration);
			HttpHelpers.updateUserInfo();
			finish();
			break;
		default:
			break;
		}
	}
}
