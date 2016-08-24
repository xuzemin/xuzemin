package net.nmss.nice.activity;

import java.io.File;
import java.util.Random;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.nmss.nice.utils.NiceConstants;
import net.nmss.nice.utils.UrlHelper;
import net.nmss.nice.AppManager;
import net.nmss.nice.R;
import net.nmss.nice.bean.NiceUserInfo;
import net.nmss.nice.utils.AsyncHttpRequestUtil;
import net.nmss.nice.utils.ImageLoaderUtils;
import net.nmss.nice.utils.LogUtil;
import net.nmss.nice.utils.NetWorkHelper;
import net.nmss.nice.utils.PreferenUtil;
import net.nmss.nice.widget.MyProgressDialog;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class BaseActivity extends FragmentActivity {
	private final static String LOG_TAG = "BaseActivity";
	// private ExitDialog exitDialog;
	// private ExitDialog contactCustomerServiceDialog;
	private MyProgressDialog progressDialog;
	protected SlidingMenu sm;
	int count = 0;
	long lastTime;

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
		LogUtil.i("displayBriefMemory", "onDestroy()");
		displayBriefMemory();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		chkLocation();
		NiceUserInfo info = NiceUserInfo.getInstance();
		String newUid = info.getUId();
		if (newUid == null || newUid.length() == 0) {
			PreferenUtil pUtil = new PreferenUtil(this);
			String oldUid = pUtil.getUID();
			info.setUId(oldUid);
		}
		LogUtil.i("displayBriefMemory", "onResume()");
		displayBriefMemory();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		PreferenUtil pUtil = new PreferenUtil(this);
		String newUid = NiceUserInfo.getInstance().getUId();
		String oldUid = pUtil.getUID();
		if (newUid != null && newUid.length() != 0 && !newUid.equals(oldUid)) {
			pUtil.setUid(newUid);
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		ImageLoaderUtils.getInstance().clearMemoryCache();
		LogUtil.i("displayBriefMemory", "onStop()");
		displayBriefMemory();
		super.onStop();
	}

	public void displayBriefMemory() {
		final ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
		activityManager.getMemoryInfo(info);

		LogUtil.i("displayBriefMemory", "系统剩余内存:" + (info.availMem >> 10) + "k");
		LogUtil.i("displayBriefMemory", "系统是否处于低内存运行：" + info.lowMemory);
		LogUtil.i("displayBriefMemory", "当系统剩余内存低于" + info.threshold
				+ "时就看成低内存运行");
	}

	public void initSlidingMenu() {
//		// customize the SlidingMenu
//		sm = new SlidingMenu(this);
//		sm.setMode(SlidingMenu.TOUCHMODE_NONE);
//		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//		sm.setBehindOffset(150);
//		sm.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
//		// sm.setMenu(R.layout.sliding_menu_left_layout);
//		sm.setSecondaryMenu(R.layout.sliding_menu_left_layout);
//		// initSlidingMenuLeft();

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

	// private void initSlidingMenuLeft() {
	// // ListView lvSlidingMenu = (ListView)
	// // sm.findViewById(R.id.lv_sliding_menu_left);
	// // lvSlidingMenu.setAdapter(new SlidingMenuLeftAdater(this));
	// // lvSlidingMenu.setSelection(0);
	// View.OnClickListener listener = new View.OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// switch (v.getId()) {
	// case R.id.btn_buy_service_sliding_menu:// 购买服务
	// // Toast.makeText(getApplicationContext(), "购买服务",
	// // Toast.LENGTH_SHORT).show();
	// startActivity(new Intent(BaseActivity.this,
	// MyYesActivity.class));
	// break;
	// case R.id.rl_customer_service_sliding_menu:// 联系客服
	// // Toast.makeText(getApplicationContext(), "联系客服",
	// // Toast.LENGTH_SHORT).show();
	// if (contactCustomerServiceDialog == null)
	// new ContactCustomerServiceDialog(BaseActivity.this)
	// .show();
	// break;
	// case R.id.rl_frequently_asked_questions_sliding_menu:// 常见问题
	// // Toast.makeText(getApplicationContext(), "常见问题",
	// // Toast.LENGTH_SHORT).show();
	// startActivity(new Intent(BaseActivity.this,
	// CommonProblemsActivity.class));
	// break;
	// case R.id.rl_about_yes_sliding_menu:// 关于YES
	// // Toast.makeText(getApplicationContext(), "关于YES",
	// // Toast.LENGTH_SHORT).show();
	// startActivity(new Intent(BaseActivity.this,
	// AboutActivity.class));
	// break;
	// case R.id.rl_chk_update_sliding_menu:// 检查更新
	// NiceConstants.isChecked = false;
	// NiceConstants.isChkingAppVersion = true;
	// Intent startService = new Intent(BaseActivity.this,
	// UpdateAppService.class);
	// startService.putExtra(UpdateAppService.COMMAND,
	// UpdateAppService.START_COMM);
	// startService.putExtra(UpdateAppService.LOCAL_VERSION_FLAG,
	// getVersionStr());
	// startService.putExtra(UpdateAppService.SHOULD_TOAST, true);
	// startService(startService);
	// break;
	// case R.id.rl_exit_load_sliding_menu:// 退出登陆
	// // Toast.makeText(getApplicationContext(), "退出登陆",
	// // Toast.LENGTH_SHORT).show();
	// // Intent load = new
	// // Intent(BaseActivity.this,LoginActivity.class);
	// // load.putExtra("canBack", false);
	// // startActivity(load);
	// AppManager.getInstance().exit(BaseActivity.this);
	// break;
	// default:
	// break;
	// }
	// }
	// };
	//
	// ((Button) findViewById(R.id.btn_buy_service_sliding_menu))
	// .setOnClickListener(listener);
	// ((RelativeLayout) findViewById(R.id.rl_customer_service_sliding_menu))
	// .setOnClickListener(listener);
	// ((RelativeLayout)
	// findViewById(R.id.rl_frequently_asked_questions_sliding_menu))
	// .setOnClickListener(listener);
	// ((RelativeLayout) findViewById(R.id.rl_about_yes_sliding_menu))
	// .setOnClickListener(listener);
	// ((RelativeLayout) findViewById(R.id.rl_chk_update_sliding_menu))
	// .setOnClickListener(listener);
	// ((RelativeLayout) findViewById(R.id.rl_exit_load_sliding_menu))
	// .setOnClickListener(listener);
	//
	// sm.setOnOpenedListener(new OnOpenedListener() {
	// @Override
	// public void onOpened() {
	// NiceUserInfo info = NiceUserInfo.getInstance();
	// ((TextView) sm.findViewById(R.id.tv_user_name_sliding_menu))
	// .setText(info.getName());
	// RoundImageView headPic = (RoundImageView) sm
	// .findViewById(R.id.img_header_pic_sliding_menu_left);
	// ImageLoaderUtils.getInstance().displayUserHeadPic(
	// info.getHead_pic(), headPic);
	// }
	// });
	// }

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

	public void showProgress() {

		try {
			if (progressDialog == null) {
				progressDialog = new MyProgressDialog(this);
			}
			// if(progressDialog.isShowing()) {
			// return;
			// }
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

	@Override
	public void onBackPressed() {
		AppManager am = AppManager.getInstance();

		if (am.getTopActivity().equals(this) && am.getActivities().size() > 1) {
			super.onBackPressed();
			return;
		}
		if (sm != null) {
			if (sm.isSecondaryMenuShowing()) {
				sm.toggle();
				return;
			}
		}
		if (count == 0) {
			Toast.makeText(this, "再按一次退出YES瘦客户端", Toast.LENGTH_SHORT).show();
			count++;
			lastTime = System.currentTimeMillis();
			return;
		} else {
			long nowTime = System.currentTimeMillis();
			if (nowTime - lastTime <= 2500) {
				saveUserHeadPicUrl();
				parseLogin();
				ImageLoaderUtils.getInstance().clearMemoryCache();
				ImageLoaderUtils.getInstance().clearDiscCache();
				ImageLoaderUtils.getInstance().stop();
				AsyncHttpRequestUtil.dispose(this);
				AppManager.getInstance().exit(this);
			} else {
				count = 0;
			}
		}
	}

	protected void saveUserHeadPicUrl() {
		NiceUserInfo info = NiceUserInfo.getInstance();
		String headUrl = info.getHead_pic();
		PreferenUtil util = new PreferenUtil(this);
		if (headUrl != null) {
			util.setUserHeadPic(headUrl);
		}
	}

	private void parseLogin() {
		RequestParams params = new RequestParams();
		params.put(NiceConstants.UID, NiceUserInfo.getInstance().getUId());
		String url = UrlHelper.getAbsoluteUrl("newsApp/loginOut");
		AsyncHttpRequestUtil.get(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				// TODO 自动生成的方法存根
				LogUtil.i("parseLogin", "onStart");
				super.onStart();
			}

			@Override
			public void onSuccess(int statusCode, String content) {
				// TODO 自动生成的方法存根
				LogUtil.i("parseLogin", "onSuccess");
			}

			@Override
			public void onFinish() {
				// TODO 自动生成的方法存根
				LogUtil.i("parseLogin", "onFinish");
				super.onFinish();
			}
		});
	}

	public int getVersionCode() {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
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

	public String getVersionStr() {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			LogUtil.exception(e);
		}

		String versionCode = "1.0.0";
		if (packInfo != null) {
			versionCode = packInfo.versionName;
			// NiceUserInfo.getInstance().setVersionCode(packInfo.packageName);
		}
		LogUtil.i(LOG_TAG, "packInfo.packageName:" + versionCode);
		return versionCode;
	}

	private void chkLocation() {
		NiceUserInfo info = NiceUserInfo.getInstance();
		String latitude = info.getLatitude();
		String longitude = info.getLongitude();
		LogUtil.i(LOG_TAG, "latitude:" + latitude);
		LogUtil.i(LOG_TAG, "longitude:" + longitude);
		if ("0.0".equals(latitude) || "0.0".equals(longitude)) {
			((NiceApplication) getApplication()).requestLocation();
			LogUtil.i(LOG_TAG, "requestLocation");
		}
	}

	/**
	 * 获取手机IMEI
	 */
	public String imei;

	public String getIMEIinfo() {
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		return imei;
	}

	public String getUAinfo() {
		WebView webview;
		webview = new WebView(this);
		webview.layout(0, 0, 0, 0);
		WebSettings settings = webview.getSettings();
		String ua = settings.getUserAgentString();
		ua = ua.replaceAll(" ", "");
		LogUtil.i(LOG_TAG, ua);
		return ua;
	}

	public boolean chkNetConnect() {
		boolean isConn = NetWorkHelper.checkNetState(this);
		if (!isConn) {
			Toast.makeText(this, getString(R.string.net_not_conn),
					Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	public String getETText(TextView et) {
		String str = et.getText().toString();
		if (str == null || "".equals(str.trim())) {
			return "";
		}
		return str.trim();
	}

	public boolean chkEditText(TextView... ets) {
		for (TextView et : ets) {
			String str = et.getText().toString();
			if (str == null || "".equals(str.trim())) {
				Toast.makeText(this, (et.getHint() + "不能为空"),
						Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}

	public String getMetaData(String key) {
		String result = null;
		try {
			result = this.getPackageManager().getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA).metaData
					.getString(key);
		} catch (NameNotFoundException e) {
			LogUtil.exception(e);
		}
		return result;
	}

	public String getString4Res(int resId) {
		return this.getResources().getString(resId);
	}

	public void hideKeyBoard() {
		if (this.getCurrentFocus() != null) {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(this.getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);

		}
	}

	protected void installApk(File file) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("application/vnd.android.package-archive");
		intent.setData(Uri.fromFile(file));
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);

	}

	public void getResolution() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		NiceConstants.W = mDisplayMetrics.widthPixels;
		NiceConstants.H = mDisplayMetrics.heightPixels;
		LogUtil.e(LOG_TAG, "Width = " + NiceConstants.W);
		LogUtil.e(LOG_TAG, "Height = " + NiceConstants.H);
/*		if ((W == 480 && H == 640) || (W == 240 && H == 320)
				|| (W == 320 && H == 480) || (W == 600 && H == 800)) {
			NiceConstants.rate = "small";
		} else if ((W == 480 && H == 800) || (W == 480 && H == 854)
				|| (W == 540 && H == 960)) {
			NiceConstants.rate = "middle";
		} else if ((W == 1080 && H == 1920) || (W == 720 && H == 1280)
				|| (W == 800 && H == 1280)|| (W == 1080 && H == 1776)) {
			NiceConstants.rate = "high";
		}else{
			NiceConstants.rate = "other";
		}
		LogUtil.e(LOG_TAG, "分辨率为:" + NiceConstants.rate);*/
	}
	
	public void getLoginInfo(){
		//获取SharedPreferences对象
		NiceConstants.LoginType = "游客";
		Random random = new Random();
		NiceUserInfo userInfo = NiceUserInfo.getInstance();
		userInfo.setName("游客" + random.nextInt(1000));
		userInfo.setPwd("");
		userInfo.setUId("0");
		userInfo.setDeclaration("到此一游");
		PreferenUtil preferenUtil = new PreferenUtil(
				BaseActivity.this);
		preferenUtil.setVisitName(userInfo.getName());
		preferenUtil.setVisitPwd(userInfo.getPwd());
		preferenUtil.setVisitUid(userInfo.getUId());
		preferenUtil.setVisitDel(userInfo.getDeclaration());
	}

}
