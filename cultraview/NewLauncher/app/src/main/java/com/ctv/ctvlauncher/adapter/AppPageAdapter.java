package com.ctv.ctvlauncher.adapter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.bean.AppPageBean;
import com.ctv.ctvlauncher.compat.LauncherActivityInfoCompat;
import com.ctv.ctvlauncher.view.AppPage;

import java.util.LinkedList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.viewpager.widget.PagerAdapter;

public class AppPageAdapter extends PagerAdapter implements AppPage.OnPageItemClick{
    private static final String TAG = "AppPageAdapter";
    private List<AppPageBean> appPageList;
    private LinkedList<AppPage> appPages = new LinkedList();
    private int columCount;
    private static AlertDialog dialog;
    public static boolean isDelete = false;
    private Context context;
    private View.OnLongClickListener onLongClickListener;
    private View.OnFocusChangeListener onFocusChangeListener;
    private int rowCount;
    private String[] SystemApp = new String[]{"com.android.toofifi","com.ctv.ctvlauncher","com.android.calculator2","com.ctv.insttouduce_mettingyitiji"
            ,"com.ctv.welcome","com.cultraview.cleaner","com.dazzle.timer","com.dazzlewisdom.screenrec","com.example.newmagnifier","com.example.spotlight","com.example.systemcheck"
            ,"com.jxw.launcher","com.xingen.camera","mstar.factorymenu.ui.hh","","","","",""};

    @Override
    public int getCount() {
        List list = this.appPageList;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    public AppPageAdapter(Context context, int i, int i2, View.OnFocusChangeListener onFocusChangeListener, View.OnLongClickListener onLongClickListener
    ) {
        this.context = context;
        this.rowCount = i;
        this.columCount = i2;
        this.onFocusChangeListener = onFocusChangeListener;
        this.onLongClickListener = onLongClickListener;
    }

    public List<AppPageBean> getPageBeanList() {
        return this.appPageList;
    }

    public Object instantiateItem(ViewGroup viewGroup, int i) {
        Log.d("APPpage", "---instantiateItem --  i = "+i  );
        AppPage appPage;
        if (this.appPages.size() == 0) {
            appPage = new AppPage(this.context,onFocusChangeListener,onLongClickListener);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-1, -1);
            appPage.setColumnCount(this.columCount);

            appPage.setRowCount(this.rowCount);
            appPage.setLayoutParams(layoutParams);
        } else {
            appPage = (AppPage) this.appPages.removeFirst();
        }
        appPage.setTag(this.appPageList.get(i));
        appPage.setAppBeanList(this.appPageList.get(i));
        appPage.setOnPageItemClick(this);
        viewGroup.addView(appPage);
        return appPage;
    }
    public void setAppPageList(List<AppPageBean> list) {
        this.appPageList = list;
        this.appPages = new LinkedList();
        notifyDataSetChanged();
    }
    public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
        AppPage appPage = (AppPage) obj;
        viewGroup.removeView(appPage);
        this.appPages.add(appPage);
    }
    @Override
    public void onPageItemClicked(LauncherActivityInfoCompat launcherActivityInfoCompat) {
        if(isDelete) {
            uninstallPackage(launcherActivityInfoCompat);
        }else{
            startActivity(launcherActivityInfoCompat.getComponentName().getPackageName());
        }
    }

    private void startActivity(String str) {
        this.context.startActivity(this.context.getPackageManager().getLaunchIntentForPackage(str));
    }

    private void uninstallPackage( LauncherActivityInfoCompat launcherActivityInfoCompat) {
        if((launcherActivityInfoCompat.getApplicationInfo().flags & ApplicationInfo.FLAG_SYSTEM)!= 0){
            Toast.makeText(context,context.getText(R.string.delete_system),Toast.LENGTH_SHORT).show();
        }else{
            PackageManager packageManager = context.getPackageManager();
            showUninstallDialog(launcherActivityInfoCompat.getComponentName().getPackageName(),
                    String.valueOf(launcherActivityInfoCompat.getApplicationInfo().loadLabel(packageManager)));
        }
    }

    private void showUninstallDialog(final String packageName,final String packageLable){
        if(dialog == null || !dialog.isShowing()) {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_uninstall, null, false);
            dialog = new AlertDialog.Builder(context, R.style.CustomDialog).setView(view).create();
            TextView title = view.findViewById(R.id.title);
            title.setText(R.string.message_uninstall);
            TextView message = view.findViewById(R.id.message);
            message.setText(packageLable);
            Button negtive = view.findViewById(R.id.negtive);
            negtive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            Button positive = view.findViewById(R.id.positive);
            positive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    quietUnInstallApk(packageName);
                    dialog.dismiss();
                    Log.e("uninstall", "nouninstall");
                }
            });
            dialog.show();
        }
    }

    private boolean isSystemApp(String str){
        for(String app : SystemApp){
            if(app.equals(str)){
                return true;
            }
        }
        return false;
    }

    private void quietUnInstallApk(String packageName) {
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent sender = PendingIntent.getActivity(context, 0, intent, 0);
        PackageInstaller mPackageInstaller = context.getPackageManager().getPackageInstaller();
        mPackageInstaller.uninstall(packageName, sender.getIntentSender());// 卸载APK
    }

}
