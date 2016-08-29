package com.android.wifi.socket.Activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import com.android.wifi.socket.util.LogUtil;
import com.android.wifi.socket.util.PreferenUtil;
import com.android.wifi.socket.widght.MyProgressDialog;
import com.android.wifi.socket.wifisocket.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class BaseActivity extends Activity {
	private String LOG_TAG = "BaseActivity";
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
				progressDialog = new MyProgressDialog(this, R.style.CustomProgressDialog);
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

	public int chkVersion() {
		PreferenUtil preferenUtil = new PreferenUtil(this);
		int oldVersionCode = preferenUtil.getversionCode();
		int newVersionCode = getVersionCode();
		if (newVersionCode > oldVersionCode) {
			preferenUtil.setVersionCode(newVersionCode);
			return SplashActivity.GOTOWELCOME;
		} else {
			return SplashActivity.GOTOFIRST;
		}
	}
	public int getVersionCode() {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			LogUtil.exception(e);
		}

		int versionCode = 1;
		if (packInfo != null) {
			versionCode = packInfo.versionCode;
			// NiceUserInfo.getInstance().setVersionCode(packInfo.packageName);
		}
		LogUtil.i(LOG_TAG, "packInfo.versionCode:" + versionCode);
		return versionCode;
	}

}
