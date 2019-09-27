package com.ctv.settings.security;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ctv.settings.adapter.PermissionsListAadpter;
import com.ctv.settings.bean.ApkInfo;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionsDialog extends Dialog{

	private static final String TAG = "PermissionsDialog";

	private final Context ctvContext;

	private LinearLayout lockscreen_ly;

	private RelativeLayout relativeLayout;

	private ImageView sys_swich_iv;

	private RelativeLayout pass_change;

	private View view;

	private String mPassWord;

	private LinearLayout permissions_ly;

	private ListView application_permission_lv;

	private PackageManager pm;

	private List<ApkInfo> applist;

	private PermissionsListAadpter adapter;

	public Handler mHandler = null;

	public PermissionsDialog(Context ctvContext, Handler mHandler) {
		super(ctvContext);
		this.ctvContext = ctvContext;
		this.mHandler = mHandler;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.permissions_dialog_layout);
		setWindowStyle();
		findViews();
		initData();
	}

	private void initData() {
		applist = new ArrayList<ApkInfo>();
		adapter = new PermissionsListAadpter(ctvContext, applist);
		adapter.setAppData(applist);
		application_permission_lv.setAdapter(adapter);

		new Thread(()->{
			applist.addAll(scanInstallApp());
			if (mHandler != null) {
				mHandler.post(()->{
					adapter.notifyDataSetChanged();
				});
			}
		}).start();
	}

	private void setWindowStyle() {
		Window w = getWindow();
		Resources res = ctvContext.getResources();
		Drawable drab = res.getDrawable(R.drawable.transparency_bg);
		w.setBackgroundDrawable(drab);
		WindowManager.LayoutParams lp = w.getAttributes();
		final float scale = res.getDisplayMetrics().density;
		// In the mid-point to calculate the offset x and y
		lp.y = (int) (-36 * scale + 0.5f);
		lp.width = (int) (680 * scale + 0.5f);
		lp.height = (int) (408 * scale + 0.5f);
		// Range is from 1.0 for completely opaque to 0.0 for no dim.
		w.setDimAmount(0.0f);
		w.setAttributes(lp);
	}

	/**
	 * findViews(The function of the method)
	 * 
	 * @Title: findViews
	 * @Description: TODO
	 */
	private void findViews() {
		permissions_ly = (LinearLayout) findViewById(R.id.permissions_ly);
		application_permission_lv = (ListView) findViewById(R.id.application_permission_lv);
		application_permission_lv
				.setOnItemClickListener(
						new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int postion, long arg3) {
						dismiss();
						ApkInfo appinfo = applist.get(postion);
						L.d(TAG, "onItemClick = %s, getPackageName=%s", postion, appinfo.getPackageName());
						Tools.startManagePermissionsActivity(ctvContext,
								appinfo.getPackageName());
					}
				}
				);
	}

	public List<ApkInfo> scanInstallApp() {
		List<ApkInfo> appInfos = new ArrayList<ApkInfo>();
		pm = ctvContext.getPackageManager(); // 获得PackageManager对象
		List<ApplicationInfo> listAppcations = pm

		.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		Collections.sort(listAppcations,
				new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
		for (ApplicationInfo app : listAppcations) {
			// appInfos.add(getAppInfo(app, pm));

			if (app.packageName == null) {
				Log.d("appPackName", "appPackName:" + "null");
			} else {
				Log.d("appPackName", "appPackName:" + app.packageName);
			}
			Intent intent = pm.getLaunchIntentForPackage(app.packageName);
			if (intent != null) {
				if (app.packageName
						.equals("com.android.cultraview.launcher.whiteboard")
						|| app.packageName
								.equals("com.android.cultraview.floatbuttonview")
						|| app.packageName
								.equals("com.protruly.floatwindowlib")
						|| app.packageName.equals("com.ctv.imageselect")
						|| app.packageName.equals("com.cultraview.annotate")
						|| app.packageName.equals("com.ctv.whiteboard")
						|| app.packageName.equals("com.jrm.localmm")
						|| app.packageName.equals("com.android.camera2")
						|| app.packageName.equals("com.cultraview.settings")
						|| app.packageName.equals("com.ctv.easytouch")) {
					continue;
				}
				appInfos.add(getAppInfo(app, pm));
			}
		}
		return appInfos;
	}

	private ApkInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
		ApkInfo appInfo = new ApkInfo();
		appInfo.setLabel(pm.getApplicationLabel(app).toString());// 应用名称
		appInfo.setApk_icon(app.loadIcon(pm));// 应用icon
		appInfo.setPackageName(app.packageName);// 应用包名，用来卸载
		return appInfo;
	}

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view,
//							int postion, long arg3) {
//		ApkInfo appinfo = applist.get(postion);
//		L.d(TAG, "onItemClick = %s, getPackageName=%s", postion, appinfo.getPackageName());
//		Tools.startManagePermissionsActivity(ctvContext,
//				appinfo.getPackageName());
//	}
}
