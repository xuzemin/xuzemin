package com.hht.middleware.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.hht.middleware.application.ApplicationManager;
import com.hht.middleware.listener.OnPermissionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenhu
 * Time: 2019/12/12 15:30
 * Description do somethings
 */
public abstract class BaseActivity extends AppCompatActivity
        implements OnPermissionListener {
    protected Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (getLayoutId() <= 0) {
            throw new NullPointerException("layout must no null");
        }
        setContentView(getLayoutId());
        mContext = this;
        ApplicationManager.getInstance().addActivity(this);
        FragmentUtils.getInstance().config(this, getSupportFragmentManager());
        initView();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ApplicationManager.getInstance().finishActivity(this);
    }

    /**
     * 申请运行时权限
     */
    protected void requestRuntimePermission(String[] permissions) {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionList.toArray(new String[permissionList.size()]), MiddleWareConstant.PERMISSION_CODE);
        } else {
            //同意权限，不作处理
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MiddleWareConstant.PERMISSION_CODE:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        //同意权限，不作处理
                    } else {
                        this.onDenied(deniedPermissions);
                    }
                }
                break;
        }
    }
}
