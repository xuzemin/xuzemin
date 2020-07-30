package com.ctv.settings.security.activity;

import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.security.adapter.PermissionsDetailListAadpter;
import com.ctv.settings.security.bean.PermissionBean;
import com.ctv.settings.security.permission.AppPermissionGroup;
import com.ctv.settings.security.permission.AppPermissions;
import com.ctv.settings.security.utils.PermissionUtils;
import com.ctv.settings.utils.L;

import java.util.ArrayList;
import java.util.List;

public class PermissionDetailActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "PermissionDetailActivity";

    private ListView listView;
    private PermissionsDetailListAadpter adapter;
    private List<PermissionBean> list = new ArrayList();
    private AppPermissions mAppPermissions;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions_detail);
        String packageName = getIntent().getStringExtra("data");
        String label = getIntent().getStringExtra("label");
        L.d(TAG, "qkmin Permission", "packageName" + packageName);
        initViews(label, packageName);
        getPermissions(packageName);
    }

    /**
     * 更新权限
     *
     * @param packageName
     */
    private void getPermissions(String packageName) {
        PackageInfo packageInfo = PermissionUtils.getPackageInfo(this, packageName);
        if (packageInfo == null) {
            Toast.makeText(this, "App not found", Toast.LENGTH_LONG).show();
        }
        mAppPermissions = new AppPermissions(this, packageInfo, null, true, new Runnable() {
            @Override
            public void run() {
                L.e("app permissions error");
            }
        });
        loadPreferences();
    }

    private void initViews(String label, String packageName) {
        btnBack = (ImageView) findViewById(R.id.back_btn);
        TextView titleTV = (TextView) findViewById(R.id.back_title);
        titleTV.setText(label + "-" + getString(R.string.item_permissions) + "");
        btnBack.setOnClickListener(this);

        listView = (ListView) findViewById(R.id.lv_permission);
        adapter = new PermissionsDetailListAadpter(this, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("qkmin adapter", "onItemClick--->view" + i);
                String groupName = list.get(i).getGroupName();
                boolean check = list.get(i).isChecked();
                final AppPermissionGroup group = mAppPermissions.getPermissionGroup(groupName);

                if (group == null) {
                    return;
                }
                Log.d("qkmin adapter", "onItemClick--->group.permission" + check);
                if (check) {
                    group.revokeRuntimePermissions(false);
                } else {
                    group.grantRuntimePermissions(false);
                }
                refresh(i, check);
            }
        });
    }

    /**
     * 刷新选项
     *
     * @param position
     * @param check    false 打开，true 关闭
     */
    private void refresh(int position, boolean check) {
        loadPreferences();
    }

    private void loadPreferences() {
        list.clear();
        for (AppPermissionGroup group : mAppPermissions.getPermissionGroups()) {
            if (!PermissionUtils.shouldShowPermission(group, mAppPermissions.getPackageInfo().packageName)) {
                continue;
            }
//            preference.setEnabled(!group.isPolicyFixed());
//            preference.setChecked(group.areRuntimePermissionsGranted());
            boolean isPlatform = group.getDeclaringPackage().equals(PermissionUtils.OS_PKG);
            Log.d("qkmin", "loadPreferences--->" + group.getApp().packageName + "=====" + group.getName() + "lable" + group.getLabel());
            PermissionBean bean = new PermissionBean(group.getLabel().toString(), group.getName().toString(), !group.isPolicyFixed(), group.areRuntimePermissionsGranted());
            list.add(bean);

        }

        adapter.setAppData(list);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        L.d(TAG, "onDestroy");
        list.clear();
        if (mAppPermissions != null) {
            mAppPermissions.onDestroy();
        }

        super.onDestroy();
    }
}

