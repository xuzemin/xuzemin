package net.nmss.nice.activity;

import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserTelephoneActivity extends BaseActivity implements View.OnClickListener{
	
	private TextView title_left_img;
	private EditText user_telephone;
	private TextView title_right_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_telephone_layout);
		initViews();
	}
	
	private void initViews() {
		title_left_img = (TextView) findViewById(R.id.title_left_img);
		title_right_tv = (TextView) findViewById(R.id.title_right_tv);
		user_telephone = (EditText) findViewById(R.id.user_telephone);
		String d = NiceUserInfo.getInstance().getTelephone();
		if(d != null && !d.equals("null") && d.length()>=0) {
			user_telephone.setText(d);
			user_telephone.setSelection(d.length());
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
			String newDeclaration = user_telephone.getText().toString().trim();
			if(TextUtils.isEmpty(newDeclaration)){
				Toast.makeText(this, "不能为空哦", Toast.LENGTH_SHORT).show();
				return;
			}
			NiceUserInfo.getInstance().setTelephone(newDeclaration);
			finish();
			break;
		default:
			break;
		}
	}
}
