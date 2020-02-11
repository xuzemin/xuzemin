package com.hht.middleware;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.hht.middleware.base.BaseActivity;
import com.hht.middleware.base.FragmentUtils;
import com.hht.middleware.fragment.MainFragment;

import java.util.List;

public class MainActivity extends BaseActivity {
    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // todo 根据业务需求申请相应的权限
        // requestRuntimePermission(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {

        if (FragmentUtils.getInstance().findFragment(MainFragment.class) == null) {
            FragmentUtils.getInstance().loadRootFragment(R.id.main_content, MainFragment.newInstance());
        }
    }

    @Override
    public void onDenied(List<String> deniedPermissions) {
        if (deniedPermissions != null && deniedPermissions.size() > 0) {
            for (int i = 0; i < deniedPermissions.size(); i++) {
                Log.d(TAG, deniedPermissions.get(i) + " 拒绝给予权限");
            }
        }
    }
}
