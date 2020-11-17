package com.ctv.settings.security.permission;

import android.Manifest;
import android.annotation.NonNull;
import android.annotation.Nullable;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.camera2.utils.ArrayUtils;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctv.settings.R;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.security.adapter.AppInstallPermissionListAadpter;
import com.ctv.settings.security.adapter.PermissionsDetailListAadpter;
import com.ctv.settings.security.adapter.PermissionsListAadpter;
import com.ctv.settings.security.bean.ApkInfo;
import com.ctv.settings.security.bean.PermissionBean;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AppRestrictionsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "AppRestrictionsActivity";
    private ApplicationsState.Session mAppSession;
    protected ApplicationsState mApplicationsState;
    private IPackageManager mIpm;
    private AppOpsManager mAppOpsManager;
    private List list = new ArrayList();
    private List<ApplicationsState.AppEntry> appState = new ArrayList();
    private final ApplicationsState.Callbacks mAppSessionCallbacks =
            new ApplicationsState.Callbacks() {

                @Override
                public void onRunningStateChanged(boolean running) {
                    updateAppList();
                }

                @Override
                public void onPackageListChanged() {
                    updateAppList();
                }


                @Override
                public void onRebuildComplete(ArrayList<ApplicationsState.AppEntry> apps) {
                    updateAppList(apps);
                }

                @Override
                public void onPackageIconChanged() {
                    updateAppList();
                }

                @Override
                public void onPackageSizeChanged(String packageName) {
                    updateAppList();
                }

                @Override
                public void onAllSizesComputed() {
                    updateAppList();
                }

                @Override
                public void onLauncherInfoChanged() {
                    updateAppList();
                }

                @Override
                public void onLoadEntriesCompleted() {
                    updateAppList();
                }
            };
    private AppInstallPermissionListAadpter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_restrictions);
        initView();
        mIpm = ActivityThread.getPackageManager();
        mAppOpsManager = this.getSystemService(AppOpsManager.class);
        mApplicationsState = ApplicationsState.getInstance(
                (Application) this.getApplicationContext());
        mAppSession = mApplicationsState.newSession(mAppSessionCallbacks);
        updateAppList();

    }


    @Override
    public void onResume() {
        super.onResume();
        mAppSession.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAppSession.pause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppSession.release();
    }


    private void initView() {
        findViewById(R.id.back_btn).setOnClickListener(this);
        ((TextView) findViewById(R.id.back_title)).setText(getString(R.string.item_unknown_store));


        ListView lv_install = (ListView) findViewById(R.id.application_install_permission);
        adapter = new AppInstallPermissionListAadpter(this, list);
        lv_install.setAdapter(adapter);
        lv_install.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InstallAppsState state = (InstallAppsState) appState.get(i).extraInfo;
                setCanInstallApps(appState.get(i), state.canInstallApps() ? false : true);
            }
        });
    }

    private void setCanInstallApps(ApplicationsState.AppEntry entry, boolean newState) {
        Log.d(TAG, "setCanInstallApps:" + newState);
        mAppOpsManager.setMode(AppOpsManager.OP_REQUEST_INSTALL_PACKAGES,
                entry.info.uid, entry.info.packageName,
                newState ? AppOpsManager.MODE_ALLOWED : AppOpsManager.MODE_ERRORED);
        updateAppList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }

    protected void updateAppList() {
        ApplicationsState.AppFilter filter = new ApplicationsState.CompoundFilter(getFilter(),
                ApplicationsState.FILTER_NOT_HIDE);
        ArrayList<ApplicationsState.AppEntry> apps = mAppSession.rebuild(filter, getComparator());
        if (apps != null) {
            updateAppList(apps);
        }
    }

    private void updateAppList(ArrayList<ApplicationsState.AppEntry> apps) {
        list.clear();
        appState.clear();
        for (final ApplicationsState.AppEntry entry : apps) {
            loadInstallAppsState(entry);
            PermissionBean bean = new PermissionBean();
            InstallAppsState state = (InstallAppsState) entry.extraInfo;
            bean.setChecked(state.canInstallApps());
            bean.setName(entry.label);
            Log.d(TAG, "entry:" + entry.info.packageName+"caninstallapps:"+state.canInstallApps());
            list.add(bean);
        }

        appState.addAll(apps);
        adapter.setAppData(list);
//        PreferenceGroup group = getPreferenceScreen();
//        // Because we're sorting the app entries, we should remove-all to ensure that sort order
//        // is retained
//        final List<Preference> newList = new ArrayList<>(apps.size() + 1);
//        for (final ApplicationsState.AppEntry entry : apps) {
//            final String packageName = entry.info.packageName;
//            Preference recycle = group.findPreference(packageName);
//            if (recycle == null) {
//                recycle = createAppPreference();
//            }
//            newList.add(bindPreference(recycle, entry));
//        }
//        final Preference header = findPreference(HEADER_KEY);
//        group.removeAll();
//        if (header != null) {
//            group.addPreference(header);
//        }
//        if (newList.size() > 0) {
//            for (Preference prefToAdd : newList) {
//                group.addPreference(prefToAdd);
//            }
//        } else {
//            final Preference empty = new Preference(getPreferenceManager().getContext());
//            empty.setKey("empty");
//            empty.setTitle(R.string.noApplications);
//            empty.setEnabled(false);
//            group.addPreference(empty);
//        }
    }

    public ApplicationsState.AppFilter getFilter() {
        return new ApplicationsState.AppFilter() {

            @Override
            public void init() {
            }

            @Override
            public boolean filterApp(ApplicationsState.AppEntry entry) {
                if (!(entry.extraInfo instanceof InstallAppsState)) {
                    loadInstallAppsState(entry);
                }
                InstallAppsState state = (InstallAppsState) entry.extraInfo;
                return state.isPotentialAppSource();
            }
        };
    }

    private void loadInstallAppsState(ApplicationsState.AppEntry entry) {
        entry.extraInfo = createInstallAppsStateFor(entry.info.packageName, entry.info.uid);
    }

    private boolean hasRequestedAppOpPermission(String permission, String packageName) {
        try {
            String[] packages = mIpm.getAppOpPermissionPackages(permission);
            return ArrayUtils.contains(packages, packageName);
        } catch (RemoteException exc) {
            Log.e(TAG, "PackageManager dead. Cannot get permission info");
            return false;
        }
    }

    private InstallAppsState createInstallAppsStateFor(String packageName, int uid) {
        final InstallAppsState appState = new InstallAppsState();
        appState.permissionRequested = hasRequestedAppOpPermission(
                Manifest.permission.REQUEST_INSTALL_PACKAGES, packageName);
        appState.permissionGranted = hasPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES,
                uid);
        appState.appOpMode = getAppOpMode(AppOpsManager.OP_REQUEST_INSTALL_PACKAGES, uid,
                packageName);
        return appState;
    }

    private boolean hasPermission(String permission, int uid) {
        try {
            int result = mIpm.checkUidPermission(permission, uid);
            return result == PackageManager.PERMISSION_GRANTED;
        } catch (RemoteException e) {
            Log.e(TAG, "PackageManager dead. Cannot get permission info");
            return false;
        }
    }

    private int getAppOpMode(int appOpCode, int uid, String packageName) {
        return mAppOpsManager.checkOpNoThrow(appOpCode, uid, packageName);
    }

    /**
     * Collection of information to be used as {@link ApplicationsState.AppEntry#extraInfo} objects
     */
    private static class InstallAppsState {
        public boolean permissionRequested;
        public boolean permissionGranted;
        public int appOpMode;

        private InstallAppsState() {
            this.appOpMode = AppOpsManager.MODE_DEFAULT;
        }

        public boolean canInstallApps() {
            if (appOpMode == AppOpsManager.MODE_DEFAULT) {
                return permissionGranted;
            } else {
                return appOpMode == AppOpsManager.MODE_ALLOWED;
            }
        }

        public boolean isPotentialAppSource() {
            return appOpMode != AppOpsManager.MODE_DEFAULT || permissionRequested;
        }

        @Override
        public String toString() {
            return "[permissionGranted: " + permissionGranted
                    + ", permissionRequested: " + permissionRequested
                    + ", appOpMode: " + appOpMode
                    + "]";
        }
    }

    public Comparator<ApplicationsState.AppEntry> getComparator() {
        return ApplicationsState.ALPHA_COMPARATOR;
    }


}