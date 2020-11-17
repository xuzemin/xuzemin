package com.ctv.ctvlauncher.fragment;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ctv.ctvlauncher.MainActivity;
import com.ctv.ctvlauncher.R;
import com.ctv.ctvlauncher.adapter.AppPageAdapter;
import com.ctv.ctvlauncher.bean.AppInfo;
import com.ctv.ctvlauncher.bean.AppPageBean;
import com.ctv.ctvlauncher.compat.LauncherActivityInfoCompat;
import com.ctv.ctvlauncher.manage.AppListManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.viewpager.widget.ViewPager;

public class FragmentAppGride implements ViewPager.OnPageChangeListener {
  //  private static OthersGridviewAadpter mAdapter;
    private GridView gridview;
    private View contentView;
    private static List<AppInfo> listapp;
    private Context mContext;
    private PackageManager pm;
    private Thread listAppThread;
    private static long timebak = -1;
    private AnimationSet manimationSet;
    private MyInstalledReceiver installedReceiver;
    private AnimationSet animationSet;
    private View temp_view;
    private View.OnClickListener buttonClickListener;
    private GD_OnButtonClickListener gd_onButtonClickListener =new GD_OnButtonClickListener();
    private MainActivity mActivity;
    public static int GRID_ITEM_HEIGHT = 100;
    public static int GRID_ITEM_WIDTH = 100;
    public static int columCount = 6;
    public static int rowCount = 3;
    private int currentPageIndex = -1;
    private int totalPageCount;
    private static ViewPager viewPagerAppPage;
    public static AppPageAdapter pageAdapter;
    private ViewGroup pageCountGroup;
    private View.OnFocusChangeListener onFocusChangeListener;
    private static List<LauncherActivityInfoCompat> totalLaunchActivityCompat;
    private int lastPageIndex = -1;
    private View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            pageAdapter.isDelete = !pageAdapter.isDelete;
            updateapp();
            return false;
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            String str = "pkgname";
            if (message.what == 1) {
                List launchAppList = new AppListManager(FragmentAppGride.this.mContext,bd_pk,bm_pk,bw_pk,bs_pk).getLaunchAppList(message.getData().getString(str));
                Log.d("hhh", "handleMessage: 1111 --- packagename = "+message.getData().get(str));
                if (launchAppList != null) {
                    if (launchAppList.size() > 0) {
                        Log.d("hhh", "handleMessage:  1111 ==add app");
                        FragmentAppGride.this.addCompat((LauncherActivityInfoCompat) launchAppList.get(0));
                    }
                }
                return;
            } else if (message.what == 2) {
                FragmentAppGride.this.removeCompat(message.getData().getString(str));

                Log.d("hhh", "handleMessage: 2  == remove app  packname  ="+message.getData().getString(str));
            }
            super.handleMessage(message);
        }
    };

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                 // mAdapter.setAppData(listapp);
                    listAppThread = null;
                    break;
            }
        }
    };
    public  IntentFilter filter;
    private RelativeLayout gd_bottom_ll;
    private List<AppPageBean> appPageBeans = new ArrayList();
    public static String bm_pk;
    public static String bw_pk;
    public static String bs_pk;
    public static String bd_pk;

    public FragmentAppGride(View view, Context context, MainActivity activity, View.OnFocusChangeListener onFocusChangeListener) {
        this.contentView = view;
        this.mContext = context;
        this.mActivity=activity;
        this.onFocusChangeListener = onFocusChangeListener;
        initview();
    }
    public void addCompat(LauncherActivityInfoCompat launcherActivityInfoCompat) {
        List pageBeanList = this.pageAdapter.getPageBeanList();
        if (pageBeanList.size() > 0) {
            Log.d("hggh", "addCompat: pageBeanList.size  ="+pageBeanList.size());
            AppPageBean appPageBean = (AppPageBean) pageBeanList.get(pageBeanList.size() - 1);
            if (appPageBean.isFull()) {
                List list = this.totalLaunchActivityCompat;
                pageBeanList.add(new AppPageBean(list, list.size(), this.totalLaunchActivityCompat.size() + 1, rowCount, columCount));
                this.totalLaunchActivityCompat.add(launcherActivityInfoCompat);
                Log.d("hhh", "addCompat: isFull");
                updatePageCount();
            } else {
                Log.d("hhh", "addCompat: ");
                appPageBean.addCompat(launcherActivityInfoCompat);
                this.totalLaunchActivityCompat.add(launcherActivityInfoCompat);
            }
            if (null == pageAdapter){
                Log.d("APPpage", "pageAdapter==null ");
            }
            pageAdapter.setAppPageList(appPageBeans);
            viewPagerAppPage.setAdapter(pageAdapter);
        }
    }

    private void removeCompat(String str) {
        List pageBeanList = this.pageAdapter.getPageBeanList();
        LauncherActivityInfoCompat launcherActivityInfoCompat = null;
        for (int i = 0; i < pageBeanList.size(); i++) {
            AppPageBean appPageBean = (AppPageBean) pageBeanList.get(i);
            if (launcherActivityInfoCompat == null) {
                for (int i2 = 0; i2 < appPageBean.getCount(); i2++) {
                    LauncherActivityInfoCompat launcherActivityInfoCompat2 = appPageBean.get(i2);
                    if (launcherActivityInfoCompat2.getComponentName().getPackageName().equals(str)) {
                        totalLaunchActivityCompat.remove(launcherActivityInfoCompat2);
                        launcherActivityInfoCompat = launcherActivityInfoCompat2;
                        Log.d("hhh", "removeCompat: remove app");
                        break;
                    }
                }
            }
            if (launcherActivityInfoCompat != null) {

                Log.d("hhh", "removeCompat: dirty");
                appPageBean.dirty();
            }
        }
        if (pageBeanList.size() > 0) {
            AppPageBean appPageBean2 = (AppPageBean) pageBeanList.get(pageBeanList.size() - 1);
            if (appPageBean2.getCount() == 1) {
                pageBeanList.remove(appPageBean2);

                Log.d("hhh", "removeCompat: (pageBeanList.size() > 0)");

                updatePageCount();
            } else {
                Log.d("hhh", "removeCompat:  (pageBeanList.size() < 0)");
                appPageBean2.removeCompat(launcherActivityInfoCompat);

            }
        }

        this.pageAdapter.setAppPageList(appPageBeans);
        viewPagerAppPage.setAdapter(pageAdapter);
    }
    AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (view == null) {
                return;
            }
        //    mAdapter.setSelectedId(i);
            setScaleAnimation(view, i, false);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {


        }
    };


    @SuppressLint("WrongConstant")
    public void initview() {
        Log.d("ctvtheme", "initview: ");
  //      if (pageAdapter==null){
          this.pageAdapter = new AppPageAdapter(mContext, rowCount, columCount,onFocusChangeListener,onLongClickListener);
//        }

        Point point = new Point();
        mContext.getResources().getDimensionPixelOffset(R.dimen.launhcerappgride_viewpager_paddingleftright);
        ((WindowManager) mContext.getSystemService("window")).getDefaultDisplay().getSize(point);
        int i = point.x;
        int i2 = point.y;
        i2 = mContext.getResources().getDimensionPixelOffset(R.dimen.launcherappgride_ctv_viewpager_width);
        i = mContext.getResources().getDimensionPixelOffset(R.dimen.launhcerappgride_viewpager_height);
        GRID_ITEM_WIDTH = i2 / columCount;
        GRID_ITEM_HEIGHT = i / rowCount;
 //       if (viewPagerAppPage ==null ){
            this.viewPagerAppPage = (ViewPager) this.contentView.findViewById(R.id.viewpager_apps);
  //      }
 //       if (pageCountGroup == null ){
            this.pageCountGroup = (ViewGroup) this.contentView.findViewById(R.id.viewgroup_pagecount);
 //       }
          initApps();
        this.viewPagerAppPage.addOnPageChangeListener(this);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(AppPageAdapter.isDelete){
                    AppPageAdapter.isDelete = false;
                    updateapp();
                }
                return false;
            }
        });
            start();
        gd_bottom_ll = contentView.findViewById(R.id.gd_bottom_ll);
        gd_bottom_ll.setOnFocusChangeListener(onFocusChangeListener);
        gd_bottom_ll.setOnClickListener(this.gd_onButtonClickListener);

    }
    public void start(){
        installedReceiver = new MyInstalledReceiver();
        filter = new IntentFilter();
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");
        filter.addDataScheme("package");
    }

    public void updateapp(){

        Log.d("ctvtheme", "-------updateapp----");
        if (pageAdapter == null){
            Log.d("APPpage", "pageAdapter =null ");
            this.pageAdapter = new AppPageAdapter(mContext, rowCount, columCount,onFocusChangeListener,onLongClickListener);
        }
        Log.d("ctvtheme", "-------updateapp----");
        appPageBeans.clear();
       // pageAdapter.notify();
        viewPagerAppPage.removeAllViews();

        bm_pk = mActivity.bm_packName;
        bw_pk = mActivity.bw_packName;
        bs_pk = mActivity.bs_packName;
        bd_pk = mActivity.bd_packName;
        Log.d("ctvtheme", "updateapp: bm_pk ="+bm_pk+" /n  bw_pk ="+bw_pk+" /n bs_pk ="+bs_pk+" /n  bd_pk ="+bd_pk);

        int size;
        this.totalLaunchActivityCompat = new AppListManager(mContext,bd_pk,bm_pk,bw_pk,bs_pk).getLaunchAppList(null);
        Log.d("ctvtheme", "updateapp: totalLaunchActivityCompat.size  ="+totalLaunchActivityCompat.size());

        if (this.totalLaunchActivityCompat.size() % (rowCount * columCount) == 0) {
            size = this.totalLaunchActivityCompat.size() / (rowCount * columCount);
        } else {
            size = (this.totalLaunchActivityCompat.size() / (rowCount * columCount)) + 1;
        }
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            //size2 代表每页的item数量
            int size2;
            if ((columCount * rowCount) + i >= this.totalLaunchActivityCompat.size()) {
                size2 = this.totalLaunchActivityCompat.size();
            } else {
                size2 = (columCount * rowCount) + i;
            }
            int i3 = i;
            this.appPageBeans.add(new AppPageBean(this.totalLaunchActivityCompat, i3, size2, rowCount, columCount));
            i += rowCount * columCount;
        }
        this.totalPageCount = size;
        size = this.currentPageIndex;
        if (size < 0) {
            this.currentPageIndex = 0;
        } else {
            int i4 = this.totalPageCount;
            if (size >= i4) {
                this.currentPageIndex = i4 - 1;
            }
        }
        updatePageCount();
        Log.d("ctvtheme", "updateapp: totalLaunchActivityCompat.size ="+totalLaunchActivityCompat.size());
        Log.d("ctvtheme", "updateapp:  appPageBeans.size = "+appPageBeans.size());


        this.pageAdapter.setAppPageList(this.appPageBeans);

        this.viewPagerAppPage.setAdapter(this.pageAdapter);




    }



    public void initApps() {
        appPageBeans.clear();
        // pageAdapter.notify();
        viewPagerAppPage.removeAllViews();
        //   android:layout_width="@dimen/others_gridview_width"
        bm_pk = mActivity.bm_packName;
        bw_pk = mActivity.bw_packName;
        bs_pk = mActivity.bs_packName;
        bd_pk = mActivity.bd_packName;
        Log.d("ctvtheme", "initApps: grid");
        // size 代表分页数
        int size;
        if (totalLaunchActivityCompat!=null){
            totalLaunchActivityCompat.clear();
        }
  //    if (totalLaunchActivityCompat == null ){
            this.totalLaunchActivityCompat = new AppListManager(mContext,bd_pk,bm_pk,bw_pk,bs_pk).getLaunchAppList(null);
            Log.d("ctvtheme", "initApps:   totalLaunchActivityCompat.size  ="+totalLaunchActivityCompat.size());
 //    }
        Log.d("ctvtheme", "initApps:   totalLaunchActivityCompat.size  ="+totalLaunchActivityCompat.size());

        if (this.totalLaunchActivityCompat.size() % (rowCount * columCount) == 0) {
            size = this.totalLaunchActivityCompat.size() / (rowCount * columCount);
        } else {
            size = (this.totalLaunchActivityCompat.size() / (rowCount * columCount)) + 1;
        }
        Log.d("ctvtheme", "initApps: size ="+size);
        int i = 0;
        for (int i2 = 0; i2 < size; i2++) {
            //size2 代表每页的item数量
            int size2;
            if ((columCount * rowCount) + i >= this.totalLaunchActivityCompat.size()) {
                size2 = this.totalLaunchActivityCompat.size();
            } else {
                size2 = (columCount * rowCount) + i;
            }
            int i3 = i;
            Log.d("ctvtheme", "initApps: size2 = "+size2);
            this.appPageBeans.add(new AppPageBean(this.totalLaunchActivityCompat, i3, size2, rowCount, columCount));
            Log.d("ctvtheme", "initApps:  appPageBeans.size ="+appPageBeans.size());
            i += rowCount * columCount;
        }
        this.totalPageCount = size;
        size = this.currentPageIndex;
        if (size < 0) {
            this.currentPageIndex = 0;
        } else {
            int i4 = this.totalPageCount;
            if (size >= i4) {
                this.currentPageIndex = i4 - 1;
            }
        }
        updatePageCount();
        Log.d("ctvtheme", "initApps:   appPageBeans.size  ="+appPageBeans.size());

        this.pageAdapter.setAppPageList(this.appPageBeans);
        this.viewPagerAppPage.setAdapter(this.pageAdapter);
    }
    private void updatePageCount() {
        Log.d("ctvtheme", "---------updatePageCount-------");
        int size;
        int i;
        View view;
        List list = this.totalLaunchActivityCompat;
        if (list == null) {
            this.totalPageCount = 0;
        } else {
            size = list.size();
            i = rowCount;
            int i2 = columCount;
            this.totalPageCount = ((size + (i * i2)) - 1) / (i * i2);
            Log.d("ctvtheme", "grid -----  updatePageCount-----totalPageCount ="+totalPageCount);
        }
        size = this.pageCountGroup.getChildCount();
        Log.d("ctvtheme", "grid -------  updatePageCount-----pageCountGroup.getChildCount() ="+size);
        if (this.totalPageCount < size) {
            while (size > this.totalPageCount){
                this.pageCountGroup.removeViewAt(size - 1);
                size--;
            }
        } else {
            while (size < this.totalPageCount) {
                view = new View(mContext);
                view.setBackgroundResource(R.drawable.pageno_normal);
                int dimensionPixelOffset = mContext.getResources().getDimensionPixelOffset(R.dimen.launhcerappgride_viewpager_pageno_icon_size);
                int dimensionPixelOffset2 = mContext.getResources().getDimensionPixelOffset(R.dimen.launhcerappgride_viewpager_pageno_icon_marginleft);
                ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(dimensionPixelOffset, dimensionPixelOffset);
                ((LinearLayout.LayoutParams) layoutParams).leftMargin = dimensionPixelOffset2;
                this.pageCountGroup.addView(view, layoutParams);
                size++;
            }
        }
        View view2 = null;
        i = this.lastPageIndex;
        if (i < this.totalPageCount) {
            view2 = this.pageCountGroup.getChildAt(i);
        }
        view = this.pageCountGroup.getChildAt(this.currentPageIndex);
        if (view2 != null) {
            view2.setBackgroundResource(R.drawable.pageno_normal);
        }
        if (view != null) {
            view.setBackgroundResource(R.drawable.pageno_selected);
        }
        this.lastPageIndex = this.currentPageIndex;
        if (this.totalPageCount <= 1) {
            this.pageCountGroup.setVisibility(View.INVISIBLE);
        } else {
            this.pageCountGroup.setVisibility(View.VISIBLE);
        }
    }


    public void registerLoginBroadcast() {

        mContext.registerReceiver(installedReceiver, filter);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int i) {
        this.currentPageIndex = i;
        updatePageCount();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class MyInstalledReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String str = "pkgname";
            if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
             // install
                Bundle bundle = new Bundle();
                bundle.putString(str, intent.getData().getSchemeSpecificPart());
                Message message = new Message();
                message.setData(bundle);
                message.what = 1;
                FragmentAppGride.this.handler.sendMessage(message);

                update();
            }
            if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) { // uninstall
                Bundle  bundle = new Bundle();
                bundle.putString(str, intent.getData().getSchemeSpecificPart());
                Message message2 = new Message();
                message2.setData(bundle);
                message2.what = 2;
                FragmentAppGride.this.handler.sendMessage(message2);
                update_remove();
            }
        }
    }
    private void update_remove(){
        if (timebak != -1 && System.currentTimeMillis() - timebak < 2 * 1000) {
            return;
        }
        timebak = System.currentTimeMillis();

        if (listAppThread == null) {
            listAppThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listapp = scanInstallApp();
                    // DialogSeletc.dialgapp =dialog_scanInstallApp();
                    mHandler.sendEmptyMessage(0);

                    Log.d("hhh", "update-----remove ");
                }
            });
            listAppThread.start();
        }
    }

    public void update() {
        Log.d("hhh ", "FragmentGridView-----update ");

        if (timebak != -1 && System.currentTimeMillis() - timebak < 2 * 1000) {
            return;
        }
        timebak = System.currentTimeMillis();

        if (listAppThread == null) {
            listAppThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    listapp = scanInstallApp();
                  // DialogSeletc.dialgapp =dialog_scanInstallApp();
                    mHandler.sendEmptyMessage(0);

                    Log.d("hhh", "update-----run ");
                }
            });
            listAppThread.start();
        }
    }

    public void setScaleAnimation(View view, int position, boolean isonclick) {
        if (view == null) {
            return;
        }
        if (temp_view != null) {
            //temp_view.setAlpha(0.5f);
            Log.d("hhh", "setScaleAnimation: temp_view.setScaleX(1f) ");
            temp_view.setScaleX(1f);
            temp_view.setScaleY(1f);
            //測試
            //temp_view.clearAnimation();
        }
        animationSet = new AnimationSet(true);
        if (manimationSet != null && manimationSet != animationSet) {
            manimationSet.setFillAfter(false);
            manimationSet.cancel();
        }
        if (isonclick) {
            return;
        }
        float x = view.getScaleX();
        if (x == 1.25f) {

            Log.d("hhh", "setScaleAnimation:temp_view = view; ");
            temp_view = view;
    //        mAdapter.setSelectedId(position);
            return;
        }
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.25f, 1.0f, 1.25f, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setDuration(100);
        animationSet.addAnimation(scaleAnimation);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);
        manimationSet = animationSet;
        temp_view = view;
   //     mAdapter.setSelectedId(position);
    }

    public List<AppInfo> scanInstallApp() {
        List<AppInfo> appInfos = new ArrayList<>();
        // 获得PackageManager对象
        pm = mContext.getPackageManager();
        List<ApplicationInfo> listAppcations = pm

                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listAppcations,
                new ApplicationInfo.DisplayNameComparator(pm));// 字典排序
        for (ApplicationInfo app : listAppcations) {
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            if (intent != null) {
                if (app.packageName.equals("com.android.cultraview.launcher.whiteboard")
                        || app.packageName.equals("com.android.cultraview.floatbuttonview")
                        || app.packageName.equals("com.protruly.floatwindowlib")
                        || app.packageName.equals("com.ctv.easytouch")
                        || app.packageName.equals("com.ctv.imageselect")
                        || app.packageName.equals("com.cultraview.annotate")
                        || app.packageName.equals("com.example.launch_2")
                        || app.packageName.equals("com.ctv.newlauncher")
                        || app.packageName.equals("com.ctv.ctvlauncher")

                        ||app.packageName.equals("mstar.factorymenu.ui.hh")
             //           || app.packageName.equals("com.android.toofifi")
                        || app.packageName.equals("com.inpor.fastmeetingcloud")
                        || app.packageName.equals("com.bozee.meetingmark")
                        || app.packageName.equals("com.example.newmagnifier")
                        || app.packageName.equals("com.bozee.remoteserver")
                        || app.packageName.equals("com.bozee.registerclient")
                        || app.packageName.equals("com.bozee.dlna.server")
                        || app.packageName.equals("com.mstar.tv.tvplayer.ui")
                        || app.packageName.equals("com.mstar.netplayer")
                        || app.packageName.equals("mstar.factorymenu.ui")
                        || app.packageName.equals("com.ctv.annotation")
                        || app.packageName.equals("com.dazzlewisdom.screenrec")//录像
                        || app.packageName.equals("com.dazzle.timer")// 计时器
                   //   || app.packageName.equals("com.android.camera2")// 原生相机
                        || app.packageName.equals(mActivity.bw_packName)
                        ||app.packageName.equals(mActivity.bs_packName)
                        ||app.packageName.equals(mActivity.bd_packName)
                        ||app.packageName.equals(mActivity.bm_packName)
                      //  ||app.packageName.equals("com.android.toofifi" )
                        ||app.packageName.equals("com.protruly.floatwindowlib.virtualkey")
                        || app.packageName.equals("com.android.tv.settings")
                        ||app.packageName.equals("com.ctv.sourcemenu")

                                    )
                                     {
                    continue;
                }
                Log.d("apphong", "-----scanInstallApp--- newlauncher- ");
                appInfos.add(getAppInfo(app, pm));
            }
        }
        return appInfos;
    }
    private class GD_OnButtonClickListener implements View.OnClickListener {
        private GD_OnButtonClickListener() {

        }


        public void onClick(View view) {
            if (FragmentAppGride.this.buttonClickListener != null) {
                FragmentAppGride.this.buttonClickListener.onClick(view);
            }
        }
    }

    public void set_gdonButtonCLickListener(View.OnClickListener onClickListener){
        this.buttonClickListener=onClickListener;

    }

    private AppInfo getAppInfo(ApplicationInfo app, PackageManager pm) {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(pm.getApplicationLabel(app).toString());//应用名称
        if (app.packageName .equals("com.mphotool.whiteboard")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_whiteboard_focus));
        }else if (app.packageName.equals( "com.jrm.localmm")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_media_icon_normal));
        }else if (app.packageName.equals("com.mysher.mtalk")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_video_normal));
        } else if (app.packageName.equals("com.ctv.settings")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.bt_settings_icon_normal));
        }else if (app.packageName.equals("com.tencent.androidqqmail")){
            appInfo.setAppIcon(mContext.getResources().getDrawable(R.mipmap.qqyouxiang));
        }
        else {
            appInfo.setAppIcon(app.loadIcon(pm));
        }
        appInfo.setPackName(app.packageName);//应用包名，用来卸载
        return appInfo;
    }

}
