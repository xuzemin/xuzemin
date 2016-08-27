package com.android.wifi.socket.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.android.wifi.socket.widght.MyProgressDialog;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class BaseActivity extends FragmentActivity {
	private MyProgressDialog progressDialog;
	protected SlidingMenu sm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		AppManager.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppManager.getInstance().killActivity(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (sm != null) {
				if (sm.isSecondaryMenuShowing()) {
					sm.toggle();
				} else {
					sm.showSecondaryMenu();
				}
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showProgress() {

		try {
			if (progressDialog == null) {
				progressDialog = new MyProgressDialog(this);
			}
			if (!progressDialog.isShowing() && !this.isFinishing())
				progressDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void hideProgress() {
		try {
			if (progressDialog != null)
				progressDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
