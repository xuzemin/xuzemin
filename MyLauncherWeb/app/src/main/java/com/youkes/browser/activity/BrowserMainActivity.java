package com.youkes.browser.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.youkes.browser.constant.Constants;
import com.youkes.browser.preference.PreferenceManager;
import com.youkes.browser.utils.Constant;
import com.youkes.browser.utils.FileHandle;
import com.youkes.browser.utils.RootCmd;

import static com.youkes.browser.activity.MainActivity.ImageNameList;
import static com.youkes.browser.activity.MainActivity.VideoNameList;
import static com.youkes.browser.utils.Constant.EVENT_GETEVENT;
import static com.youkes.browser.utils.Constant.EVENT_START_VIDEO;
import static com.youkes.browser.utils.Constant.EVENT_TO_MAIN;
import static com.youkes.browser.utils.Constant.EVENT_TO_THREAD;
import static com.youkes.browser.utils.Constant.isImagePlay;
import static com.youkes.browser.utils.Constant.isVideoPlay;

@SuppressWarnings("deprecation")
public class BrowserMainActivity extends BrowserActivity {
	CookieManager mCookieManager;
	@SuppressLint("HandlerLeak")
	private Handler mBhandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case EVENT_GETEVENT:
					FileHandle.readFile(Constant.EventPath);
					break;
				case EVENT_TO_MAIN:
					new Thread(new Runnable() {
						@Override
						public void run() {
							RootCmd.execRootCmdSilent("am start -n com.youkes.browser/.activity.MainActivity");
						}
					}).start();
					break;
				case EVENT_TO_THREAD:
					if((VideoNameList != null && VideoNameList.size() >= 0)
							||(ImageNameList == null && ImageNameList.size() >= 0) ) {

						if (Constant.isResetPlay) {
							Constant.CurrentNumber = 0;
							Constant.isResetPlay = false;
						}
						if (Constant.CurrentNumber >= Constant.OUTTIME) {
							if (!Constant.isPlay(BrowserMainActivity.this)) {
								isVideoPlay = true;
								mBhandler.sendEmptyMessage(EVENT_TO_MAIN);
								Constant.debugLog("am start -n com.youkes.browser/.activity.MainActivity");
								return;
							} else {
								Constant.CurrentNumber = 0;
							}
						} else {
							if (Constant.isPlay(BrowserMainActivity.this)) {
								Constant.CurrentNumber = 0;
							}
							Constant.CurrentNumber++;
						}
						if (!FileHandle.isIsRun()) {
							mBhandler.sendEmptyMessage(EVENT_GETEVENT);
						}
						Constant.debugLog("MyConstant.Constant 111" + Constant.CurrentNumber);
						mBhandler.sendEmptyMessageDelayed(EVENT_TO_THREAD, 2000);
					}
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void updateCookiePreference() {
		mCookieManager = CookieManager.getInstance();
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			CookieSyncManager.createInstance(this);
		}

		mCookieManager.setAcceptCookie(PreferenceManager.getInstance().getCookiesEnabled());
		super.updateCookiePreference();

	}

	@Override
	public synchronized void initializeTabs() {
		restoreOrNewTab();
		closeDrawers();
	}



	@Override
	protected void onNewIntent(Intent intent) {
		handleNewIntent(intent);
		super.onNewIntent(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBhandler.sendEmptyMessage(EVENT_TO_THREAD);
		mBhandler.sendEmptyMessage(EVENT_GETEVENT);
	}


	@Override
	protected void onPause() {
		super.onPause();
		if(!isFinished) {
			saveOpenTabs();
		}
		mBhandler.removeMessages(EVENT_TO_THREAD);
		FileHandle.stopFileHandle();
	}

	@Override
	public void updateHistory(String title, String url) {
		super.updateHistory(title, url);
		addItemToHistory(title, url);
	}

	@Override
	public boolean isIncognito() {
		return false;
	}



	@Override
	public void closeActivity() {
		closeDrawers();
		this.finish();
	}

}
