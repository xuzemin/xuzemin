package com.ctv.settings.security.holder;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ctv.settings.R;
import com.ctv.settings.base.IBaseViewHolder;
import com.ctv.settings.security.activity.PermissionDetailActivity;
import com.ctv.settings.security.adapter.PermissionsListAadpter;
import com.ctv.settings.security.bean.ApkInfo;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.Tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description: 权限管理界面
 * @Author: wanghang
 * @CreateDate: 2019/9/28 14:21
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/9/28 14:21
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PermissionsViewHolder implements IBaseViewHolder {
    private static final String TAG = "PermissionsViewHolder";

    private Activity ctvContext;
    private Handler mHandler;

    private RecyclerView application_permission_lv;

    private PackageManager pm;

    private List<ApkInfo> applist;
    private PermissionsListAadpter adapter;

    public PermissionsViewHolder(Activity activity) {
        this.ctvContext = activity;

        init();
    }

    public PermissionsViewHolder(Activity activity, Handler handler) {
        this.ctvContext = activity;
        this.mHandler = handler;

        init();
    }

    private void init() {
        initUI(ctvContext);
        initData(ctvContext);
        initListener();
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
        initBackTopLayout();
        findViews();
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        new Thread(() -> {
            applist.addAll(scanInstallApp());
            if (mHandler != null) {
                mHandler.post(() -> {
                    adapter.notifyDataSetChanged();
                });
            }
        }).start();
    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ApkInfo appinfo = applist.get(position);
                L.d(TAG, "onItemClick = %s, getPackageName=%s", position, appinfo.getPackageName());
                Tools.startActivityUtil(ctvContext, PermissionDetailActivity.class,appinfo.getLabel(), appinfo.getPackageName());
            }
        });
    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {

    }

    /**
     * 返回布局设置
     */
    private void initBackTopLayout() {
        // 返回按钮
        ImageView backBtn = (ImageView) ctvContext.findViewById(R.id.back_btn);
        backBtn.setOnClickListener((view) -> {
            ctvContext.finish();
        });

        // 设置title
        TextView backTitle = (TextView) ctvContext.findViewById(R.id.back_title);
        backTitle.setText(R.string.item_app_permissions);
    }

    /**
     * findViews(The function of the method)
     *
     * @Title: findViews
     * @Description: TODO
     */
    private void findViews() {
        application_permission_lv = (RecyclerView) ctvContext.findViewById(R.id.application_permission_lv);
        applist = new ArrayList<ApkInfo>();
        adapter = new PermissionsListAadpter(R.layout.permission_item_adapter, applist);
        application_permission_lv.setAdapter(adapter);
    }

    /**
     * 获得应用列表
     *
     * @return
     */
    public List<ApkInfo> scanInstallApp() {
        L.d("qkmin---scanInstallApp>");
        List<ApkInfo> appInfos = new ArrayList<ApkInfo>();
        pm = ctvContext.getPackageManager(); // 获得PackageManager对象
        List<ApplicationInfo> listAppcations = pm

                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
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
                        || app.packageName.equals("com.ctv.easytouch")
                        || app.packageName.equals("com.bozee.meetingmark")) {
                    continue;
                }
                PackageInfo packageInfo = getPackageInfo(ctvContext, app.packageName);
                if (packageInfo == null) {
                    Toast.makeText(ctvContext, "App not found", Toast.LENGTH_LONG).show();
                    continue;
                }

                appInfos.add(getAppInfo(app, pm));
            }
        }

        return appInfos;
    }

    private static PackageInfo getPackageInfo(Activity activity, String packageName) {
        try {
            return activity.getPackageManager().getPackageInfo(
                    packageName, PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private ApkInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        ApkInfo appInfo = new ApkInfo();
        appInfo.setLabel(pm.getApplicationLabel(app).toString());// 应用名称
        appInfo.setApk_icon(app.loadIcon(pm));// 应用icon
        appInfo.setPackageName(app.packageName);// 应用包名，用来卸载
        return appInfo;
    }

    public void onDestroy(){
        applist.clear();
    }
}
