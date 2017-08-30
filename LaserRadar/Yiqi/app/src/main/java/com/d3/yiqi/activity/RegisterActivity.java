package com.d3.yiqi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;

import com.d3.yiqi.R;
import com.d3.yiqi.service.XmppConnection;
import com.d3.yiqi.service.XmppService;
import com.d3.yiqi.util.DialogFactory;

@SuppressWarnings("all")
public class RegisterActivity extends Activity implements OnClickListener {

	private Button mBtnRegister;
	private Button mRegBack;
	private EditText mEmailEt, mNameEt, mPasswdEt, mPasswdEt2, nameMCH;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.register);
		initView();
		mBtnRegister.setOnClickListener(this);
		mRegBack.setOnClickListener(this);

	}
	
	private void initView(){
		mBtnRegister = (Button) findViewById(R.id.register_btn);
		mRegBack = (Button) findViewById(R.id.reg_back_btn);
		nameMCH = (EditText) findViewById(R.id.reg_nameMCH);
		mEmailEt = (EditText) findViewById(R.id.reg_email);
		mNameEt = (EditText) findViewById(R.id.reg_name);
		mPasswdEt = (EditText) findViewById(R.id.reg_password);
		mPasswdEt2 = (EditText) findViewById(R.id.reg_password2);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_back_btn:
			login();
			break;
		case R.id.register_btn:
			registered();
			break;
		default:
			break;
		}
	}

	private void registered() {
		String accounts = mNameEt.getText().toString();
		String password = mPasswdEt.getText().toString();
		String password2 = mPasswdEt2.getText().toString();
		String email = mEmailEt.getText().toString();
		String nicheng = nameMCH.getText().toString();
		IQ result = null;
		//能否链接
		if(!XmppConnection.isConnected()){
			Toast.makeText(getApplicationContext(), "服务器维护，请稍候",
					Toast.LENGTH_SHORT).show();
		}
		else{
			result =  XmppService.regist(accounts, password, email, nicheng);   //注册
		//可以则判断
			if(accounts.length() == 0 || password.length() == 0) {
				Toast.makeText(getApplicationContext(), "亲！帐号或密码不能为空哦",
					Toast.LENGTH_SHORT).show();
			} 
			else if (!password.equals(password2)) {
				Toast.makeText(getApplicationContext(), "两次密码不相同！",
						Toast.LENGTH_SHORT).show();
			}
			else if (result == null) {
				Toast.makeText(getApplicationContext(), "服务器忙，请稍候",
						Toast.LENGTH_SHORT).show();
			}
			else if (result.getType() == IQ.Type.ERROR) {
				if (result.getError().toString().equalsIgnoreCase("conflict(409)")) {
					Toast.makeText(getApplicationContext(), "这个账号已经存在",
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getApplicationContext(), "注册失败",
							Toast.LENGTH_SHORT).show();
				}
			} 
			else if (result.getType() == IQ.Type.RESULT) {   //注册成功直接登录
					Toast.makeText(getApplicationContext(), "恭喜，注册成功",
							Toast.LENGTH_SHORT).show();
					if(XmppService.login(accounts, password))
					{
					Intent intent = new Intent();
					intent.putExtra("USERID", accounts);
					intent.setClass(RegisterActivity.this, FriendListActivity.class);
					startActivity(intent);
					}
				}
		}
	}

	private void login() {
		Intent intent = new Intent();
		intent.setClass(RegisterActivity.this, LoginActivity.class);
		startActivity(intent);
	}
}