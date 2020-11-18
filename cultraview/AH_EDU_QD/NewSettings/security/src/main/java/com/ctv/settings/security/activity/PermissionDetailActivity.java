package com.ctv.settings.security.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.security.R;
import com.ctv.settings.security.adapter.PermissionsDetailListAadpter;
import com.ctv.settings.security.adapter.PermissionsListAadpter;
import com.ctv.settings.security.bean.ApkInfo;
import com.ctv.settings.security.bean.PermissionBean;
import com.ctv.settings.security.permission.AppPermissionGroup;
import com.ctv.settings.security.permission.AppPermissions;
import com.ctv.settings.security.permission.LocationUtils;
import com.ctv.settings.security.permission.Permission;
import com.ctv.settings.security.utils.PermissionUtils;
import com.ctv.settings.utils.L;

import java.util.ArrayList;
import java.util.List;

public class PermissionDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "PermissionDetailActivity";

    private LinearLayout lockscreen_ly;

    private RelativeLayout relativeLayout;

    private ImageView sys_swich_iv;

    private RelativeLayout pass_change;


    private String mPassWord;

    private LinearLayout pwd_ly;

    private static final String PWKEY = "password";

    private SharedPreferences mPWPreferences;

    private LinearLayout old_pwd, first_pwd, second_pwd;

    private EditText et_old_pwd, et_first_pwd, et_sec_pwd;

    private Button pwd_save, pwd_cancel;

    private View old_view;

    private String mOldStr;

    private String mFirstStr;

    private String mSecStr;

    private String mSetPassWord;
    private ImageView back_btn;
    private TextView back_title;
    private LinearLayout general_top_back_layout;
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
        Log.d("qkmin Permission", "packageName" + packageName);
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
}

