package com.d3.yiqi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.d3.yiqi.R;
import com.d3.yiqi.service.XmppConnection;
/**
 * 欢迎界面
 */
public class WelcomeActivity extends Activity {
	private Handler mHandler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		initView();
		
	}

	public void initView() {
			mHandler = new Handler();
//			new ConnThread().start();
			mHandler.postDelayed(new Runnable() {
				public void run() {
					goLoginActivity();
				}
			}, 3000);
	}

	/**
	 * 进入登陆界面
	 */
	public void goLoginActivity() {
		Intent intent = new Intent();
		intent.setClass(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

}

///**
// * 连接openfire线程
//*/
//class ConnThread extends Thread{
//	@Override
//	public void run() {
//		XmppConnection.getConnection();
//		super.run();
//	}
//}