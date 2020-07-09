package com.cultraview.cleaner.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import android.content.pm.IPackageDataObserver;
import com.cultraview.cleaner.R;
import com.cultraview.cleaner.object.ApkInfo;
import com.cultraview.cleaner.utils.Tools;
import com.cultraview.cleaner.utils.CacheDataManager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CleanerActivity extends Activity implements OnClickListener{

	private static final String TAG = "CleanerActivity";

	private int killSize = 0;

	private String clearMem = "";

	private ActivityManager mActivityManager;

	private PackageManager mPackageManager;

	public static ArrayList<ApkInfo> my_stop_list;

	private ClearCacheObserver mClearCacheObserver;

	public static List<ApkInfo> my_clean_List;

	int list_size;

	int cleanPageCount;

	int iFirst;

	public List<PackageInfo> allPackageInfos;

	private static final int EVENT_CLEAN_FINISH = 0;

	private static final int EVENT_CLEAR_PROCESS = 1;

	private static final int EVENT_CLEAR_CACHE = 2;

	private static final int EVENT_CLEAN_START = 3;

	private static boolean isRunning = false;

	private static Thread thread;

	private String before;

	private String after;

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case EVENT_CLEAN_FINISH:
//				initThread();
					finish();
					break;
				case EVENT_CLEAR_PROCESS:
					Toast.makeText(getApplicationContext(), getCleanResult(), Toast.LENGTH_SHORT).show();
					break;
				case EVENT_CLEAR_CACHE:
					String string = "";
					try {
						after=CacheDataManager.getTotalCacheSize(CleanerActivity.this);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (before.startsWith("0")) {
						Toast.makeText(getApplicationContext(),
								getString(R.string.cache_cleaner_ok),
								Toast.LENGTH_LONG).show();
					}else {
//						Toast.makeText(getApplicationContext(),
//								string,
//								Toast.LENGTH_LONG).show();
//						Toast.makeText(getApplicationContext(),
//								before,
//								Toast.LENGTH_LONG).show();
//						Toast.makeText(getApplicationContext(),
//								after,
//								Toast.LENGTH_LONG).show();

						Toast.makeText(getApplicationContext(),
								getString(R.string.cleancache)+":"+before,
								Toast.LENGTH_LONG).show();
					}
					break;
				case EVENT_CLEAN_START:
					if(!isRunning){
						thread.start();
					}else{
						Toast.makeText(getApplicationContext(),"进程正在运行",Toast.LENGTH_SHORT).show();
					}
					break;

				default:
					break;
			}
		};
	};

	private void initThread(){
		if(thread!= null && thread.isAlive()){
			thread.interrupt();
			thread = null;
		}
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				isRunning = true;
				stopApp();
				try {
					before=CacheDataManager.getTotalCacheSize(CleanerActivity.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
				CacheDataManager.clearAllCache(CleanerActivity.this);
				cacheCleaner();
				isRunning = false;
//				handler.sendEmptyMessage(EVENT_CLEAN_FINISH);
				handler.sendEmptyMessageDelayed(EVENT_CLEAN_FINISH,3000);
			}
		});
		handler.sendEmptyMessage(EVENT_CLEAN_START);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cleaner);
		mPackageManager = getPackageManager();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initThread();
	}

	private void getStopApps() {
		my_stop_list = new ArrayList<ApkInfo>();
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infoList = mActivityManager.getRunningAppProcesses();
		if (infoList != null) {
			for (RunningAppProcessInfo appProcessInfo : infoList) {
				if (appProcessInfo.importance > RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					String[] pkgList = appProcessInfo.pkgList;
					for (int i = 0; i < pkgList.length; ++i) {
						Log.i(TAG, "==: " + pkgList[i]);
						if (!Tools.isNeedClean(pkgList[i])) {
							my_stop_list.add(getAppInfo(pkgList[i]));
						}
					}
				}
			}
		}
	}

	private ApkInfo getAppInfo(String processName) {
		PackageInfo app = null;
		try {
			app = mPackageManager.getPackageInfo(processName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		ApkInfo appInfo = new ApkInfo();
		ApplicationInfo applicationInfo = app.applicationInfo;
		appInfo.setLabel(applicationInfo.loadLabel(mPackageManager) + "");
		appInfo.setApk_icon(applicationInfo.loadIcon(mPackageManager));
		appInfo.setProcessName(processName);
		return appInfo;
	}

	private void stopApp() {
		getStopApps();
		killSize = 0;
		MemoryInfo bmi = new MemoryInfo();
		mActivityManager = (ActivityManager)
				getSystemService(Context.ACTIVITY_SERVICE);
		mActivityManager.getMemoryInfo(bmi);
		double beforeMem = bmi.availMem / (1024 * 1024.0);
		Log.i(TAG, "beforeMem: " + beforeMem + "M");
		for (int i = 0; i < my_stop_list.size(); i++) {
			killSize++;
			mActivityManager.killBackgroundProcesses(my_stop_list.get(i).getProcessName());
		}
		MemoryInfo ami = new MemoryInfo();
		mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		mActivityManager.getMemoryInfo(ami);
		double releaseMem = (ami.availMem / (1024 * 1024.0)) - beforeMem;
		if (releaseMem > 1.0) {
			DecimalFormat df = new DecimalFormat("#.00");
			clearMem = df.format(releaseMem) + getString(R.string.memory_unit_m);
		} else if (releaseMem == 0.0) {
			return;
		} else {
			releaseMem = (long) (releaseMem * 1024);
			clearMem = "" + releaseMem + getString(R.string.memory_unit_k);
		}
		handler.sendEmptyMessage(EVENT_CLEAR_PROCESS);
		Log.i(TAG, "clearMem: " + clearMem);
		Log.i(TAG, "killSize: " + killSize + " ge");
	}

	private String getCleanResult() {
		return getString(R.string.clean_bg_process) + killSize
				+ getString(R.string.unit) + "\n"
				+ getString(R.string.release_memory) + clearMem;
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()){
		}
	}


	class ClearCacheObserver extends IPackageDataObserver.Stub {
		public void onRemoveCompleted(final String packageName, final boolean succeeded) {
//			final Message msg = handler.obtainMessage(EVENT_CLEAR_CACHE);
//			if (handler.hasMessages(EVENT_CLEAR_CACHE)) {
//				handler.removeMessages(EVENT_CLEAR_CACHE);
//			}
//			handler.sendMessageDelayed(msg, 100);
		}
	}

	private void clearCache(String pName) {
		if (mClearCacheObserver == null) {
			mClearCacheObserver = new ClearCacheObserver();
		}
		mPackageManager.deleteApplicationCacheFiles(pName, mClearCacheObserver);
	}

	private void cacheCleaner() {
		getAllApps();
		ApkInfo apkInfo;
		String pName;
		for (int i = 0; i < my_clean_List.size(); i++) {
			apkInfo = my_clean_List.get(i);
			pName = apkInfo.getPackageName();
			clearCache(pName);
		}
		handler.sendEmptyMessage(EVENT_CLEAR_CACHE);
	}

	public void getAllApps() {
		my_clean_List = new ArrayList<ApkInfo>();
		// 主activity读取的 系统所有应用
		allPackageInfos = getPackageManager().getInstalledPackages(
				PackageManager.GET_UNINSTALLED_PACKAGES);
		PackageManager pm = getPackageManager();
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<ResolveInfo> mApps = mPackageManager.queryIntentActivities(mainIntent, 0);
		ApkInfo apkInfo;
		ApplicationInfo applicationInfo;
		for (int i = 0; i < allPackageInfos.size(); i++) {
			PackageInfo temp = allPackageInfos.get(i);
			applicationInfo = temp.applicationInfo;
			apkInfo = new ApkInfo();
			apkInfo.setApk_icon(applicationInfo.loadIcon(pm));
			apkInfo.setLabel(applicationInfo.loadLabel(pm) + "");
			apkInfo.setPackageName(temp.packageName);
			apkInfo.setSelect(false);
			for (ResolveInfo resolveInfo : mApps) {
				if ((resolveInfo.loadLabel(pm) + "").equals(pm
						.getApplicationLabel(temp.applicationInfo) + "")) {
					if (!Tools.isShuldFiled(temp.packageName + "")) {
						my_clean_List.add(apkInfo);
					}
				}
			}
		}

	}

}
