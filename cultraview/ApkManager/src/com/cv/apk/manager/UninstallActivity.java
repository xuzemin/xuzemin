
package com.cv.apk.manager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import android.content.Context;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cv.apk.manager.utils.AllPagesAdapter;
import com.cv.apk.manager.utils.Constant;
import com.cv.apk.manager.utils.PageControl;
import com.cv.apk.manager.view.LoadingPageLayout;
import com.cv.apk.manager.view.NotFindUserApkLayout;
import com.cv.apk.manager.view.UninstallSysPageLayout;
import com.cv.apk.manager.view.UninstallUserPageLayout;
import com.cv.apk.manager.view.VerticalViewPager;
import com.cv.apk.manager.view.VerticalViewPager.OnPageChangeListener;
import android.content.SharedPreferences;
import android.os.SystemProperties;
import android.widget.FrameLayout;


/**
 * @Company: com.cultraview
 * @date: 2016
 * @author Write Macro.Song(songhong@cultraview.com)
 * @since 2.0.0
 */
public class UninstallActivity extends Activity implements OnClickListener, OnPageChangeListener {
    FrameLayout rootView; //顶部的view

    private static final String TAG = "UninstallActivity";

    public static final int UPDATA_SYSALL = 0x01;

    public static final int UPATE_PAGE = 0x02;

    public static boolean sysall_reading_to_over = false;

    private UninstallUserPageLayout uninstallUserPage;

    private UninstallSysPageLayout uninstallSysPage;

    private VerticalViewPager user_centerPager;

    private VerticalViewPager sys_centerPager;

    private LinearLayout ll_uninstall_sys_select;

    private LinearLayout ll_uninstall_user_select;

    private ImageView iv_uninstall_top_user;

    private ImageView iv_uninstall_top_sys;

    private TextView tv_uninstall_top_user;

    private TextView tv_uninstall_top_sys;

    private ArrayList<View> pages_user;

    private ArrayList<View> pages_sys;

    private PageControl pageControl;

    /** Used to mark the above 0: the user;1: the system */
    private int top_uninstall_flag;

    private int user_installed_apk;

    boolean thread_flag = true;

    public static boolean updata_sys_all = false;

    public static boolean unInstallReturn = false;

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<UninstallActivity> weakReference;

        public UIHandler(UninstallActivity activity) {
            super();
            this.weakReference = new WeakReference<UninstallActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UninstallActivity activity = weakReference.get();

            if (activity == null || activity.isFinishing()) {
                return;
            }

            // 开始处理更新UI
            switch (msg.what) {
                case UPDATA_SYSALL:
                    activity.initUserPages();
                    activity.user_centerPager.setCurrentItem(0);
                    break;
                case UPATE_PAGE: // 刷新pages
                    activity.initUserPages();
                    activity.user_centerPager.setCurrentItem(0);
                    break;
                default:
                    break;
            }
        }
    }

    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uninstall);

        mHandler = new UIHandler(this);
        initView();
        // Is being read
        if (!AppManager.sys_read_over) {
            sysall_reading_to_over = true;
            // Check to see if state for loading in the refresh
            new Thread(new UpdateUnnstallThread()).start();
            ArrayList<View> pages = new ArrayList<View>();
            VerticalViewPager centerPager = (VerticalViewPager) findViewById(R.id.uninstall_apk_content);
            LoadingPageLayout loadingPageLayout = new LoadingPageLayout(this);
            pages.add(loadingPageLayout);
            centerPager.setAdapter(new AllPagesAdapter(pages));
        } else {
            initUserPages();
        }
    }


    /**
     * The initial control
     */
    public void initView() {
        ll_uninstall_user_select = (LinearLayout) findViewById(R.id.ll_uninstall_user_select);
        ll_uninstall_sys_select = (LinearLayout) findViewById(R.id.ll_uninstall_sys_select);
        iv_uninstall_top_user = (ImageView) findViewById(R.id.iv_uninstall_top_user);
        iv_uninstall_top_sys = (ImageView) findViewById(R.id.iv_uninstall_top_sys);
        tv_uninstall_top_user = (TextView) findViewById(R.id.tv_uninstall_top_user);
        tv_uninstall_top_sys = (TextView) findViewById(R.id.tv_uninstall_top_sys);
        tv_uninstall_top_sys.setTextColor(0x50ffffff);
        top_uninstall_flag = 0;
        user_centerPager = (VerticalViewPager) findViewById(R.id.uninstall_apk_content);
        ll_uninstall_user_select.setOnClickListener(this);
        ll_uninstall_sys_select.setOnClickListener(this);
        rootView = (FrameLayout)findViewById(R.id.root_view);

    }

    public void initUserPages() {
        user_installed_apk = AppManager.userPackageInfos.size();
        Log.i("ApkManager", "==user_installed_apk.size=" + user_installed_apk);
        if (user_installed_apk == 0) {
            ArrayList<View> pages = new ArrayList<View>();
            VerticalViewPager centerPager = (VerticalViewPager) findViewById(R.id.uninstall_apk_content);
            NotFindUserApkLayout notFindUserApkLayout = new NotFindUserApkLayout(this);
            pages.add(notFindUserApkLayout);
            centerPager.setAdapter(new AllPagesAdapter(pages));
        } else {
            pages_user = new ArrayList<View>();
            int userPageCount = (int) Math.ceil(user_installed_apk / Constant.PAGE_SIZE);
            for (int i = 0; i < userPageCount; i++) {
                uninstallUserPage = new UninstallUserPageLayout(UninstallActivity.this, i);
                pages_user.add(uninstallUserPage);
                user_centerPager.addView(uninstallUserPage, i);
            }
            ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
            pageControl = new PageControl(this, (LinearLayout) group, userPageCount);
            user_centerPager.setAdapter(new AllPagesAdapter(pages_user));
            user_centerPager.setOnPageChangeListener(this);
            user_centerPager.setCurrentItem(0);
            user_centerPager.requestFocus();
        }
    }

    /**
     * Initialize the system page
     */
    public void initSysPages() {
        pages_sys = new ArrayList<View>();
        int sysPageCount = (int) Math.ceil(AppManager.sysPackageInfos.size() / Constant.PAGE_SIZE);
        for (int i = 0; i < sysPageCount; i++) {
            uninstallSysPage = new UninstallSysPageLayout(this, i);
            pages_sys.add(uninstallSysPage);
        }
        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        pageControl = new PageControl(this, (LinearLayout) group, sysPageCount);
        sys_centerPager = (VerticalViewPager) findViewById(R.id.uninstall_apk_content);
        sys_centerPager.setAdapter(new AllPagesAdapter(pages_sys));
        sys_centerPager.setOnPageChangeListener(this);
        sys_centerPager.setCurrentItem(0);
        sys_centerPager.requestFocus();
    }

    @Override
    public void onClick(View view) {
        if (ll_uninstall_user_select == view) {
            jumpSysToUser();
        } else if (ll_uninstall_sys_select == view) {
            jumpUserToSys();
        }
    }

    private void jumpUserToSys() {
        // user-->system
        top_uninstall_flag = 1;
        iv_uninstall_top_user.setBackgroundResource(R.drawable.uninstall_title_user);
        tv_uninstall_top_user.setTextColor(0x50ffffff);
        iv_uninstall_top_sys.setBackgroundResource(R.drawable.uninstall_title_system_focus);
        tv_uninstall_top_sys.setTextColor(Color.WHITE);
        initSysPages();
        ll_uninstall_sys_select.requestFocus();
    }

    private void jumpSysToUser() {
        // system-->user
        top_uninstall_flag = 0;
        initUserPages();
        iv_uninstall_top_sys.setBackgroundResource(R.drawable.uninstall_title_system);
        tv_uninstall_top_sys.setTextColor(0x50ffffff);
        iv_uninstall_top_user.setBackgroundResource(R.drawable.uninstall_title_user_focus);
        tv_uninstall_top_user.setTextColor(Color.WHITE);
        ll_uninstall_user_select.requestFocus();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown--keyCode: " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if ((top_uninstall_flag == 1)) {
                ll_uninstall_sys_select.requestFocus();
                return true;
            } else {
                ll_uninstall_user_select.requestFocus();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (ll_uninstall_user_select.isFocused() && user_installed_apk == 0) {
                // User applications, and useless application block key button
                return true;
            }
            if (top_uninstall_flag == 0) {
                user_centerPager.requestFocus();
            } else if (top_uninstall_flag == 1) {
                sys_centerPager.requestFocus();
            }
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (ll_uninstall_sys_select.isFocused()) {
                jumpSysToUser();
                return true;
            }
            if (ll_uninstall_user_select.isFocused()) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (ll_uninstall_user_select.isFocused()) {
                jumpUserToSys();
                return true;
            }
            if (ll_uninstall_sys_select.isFocused()) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            if (ll_uninstall_user_select.isFocused() && user_installed_apk == 0) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * TODO : Asynchronous update the UI thread
     */
    private class UpdateUnnstallThread extends Thread {
        @Override
        public void run() {
            try {
                while (thread_flag) {
                    if (updata_sys_all) {
                        Message msg = new Message();
                        msg.what = UPDATA_SYSALL;
                        mHandler.sendMessage(msg);
                        updata_sys_all = false;
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    public void changedPage(int page, boolean isUP) {
        if (top_uninstall_flag == 1) {
            if (isUP) {// page up
                sys_centerPager.setCurrentItem(page - 1);
            } else {// page down
                sys_centerPager.setCurrentItem(page + 1);
            }
        } else if (top_uninstall_flag == 0) {
            if (isUP) {
                user_centerPager.setCurrentItem(page - 1);
            } else {
                user_centerPager.setCurrentItem(page + 1);
            }
        }
    }

    @Override
    public void onPageSelected(int page) {
        pageControl.selectPage(page);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    protected void onDestroy() {
        thread_flag = false;
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (unInstallReturn) {
            Log.i(TAG, "========unInstallReturn==");
            initUserPages();
            unInstallReturn = false;
        }
        SharedPreferences sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE);
        int bgIndex = sharedPreferences.getInt("bgIndex", -1);
        int resTheme[] = {R.drawable.bg_theme_default, R.drawable.bg_theme_qipao, R.drawable.bg_theme_jingshu, R.drawable.bg_theme_yemo,R.drawable.bg_theme_jianjie
                ,R.drawable.bg_theme_xingkong};
        int gwTheme[] = {R.drawable.bg_theme_default, R.drawable.bg_theme_qipao, R.drawable.bg_theme_jingshu, R.drawable.bg_theme_yemo,R.drawable.bg_theme_jianjie
                ,R.drawable.bg_theme_xingkong,R.drawable.bg_theme_gw};
        boolean gw = SystemProperties.get("client.config").equals("GW");

        if (bgIndex ==-1){
            if (gw)
                setRootBackground(rootView,5);
            else
                setRootBackground(rootView,0);
        }else {
            setRootBackground(rootView,bgIndex);
        }
    }


    private void setRootBackground(View rootLayout,int data) {
        switch (data) {
            case 2:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_jingshu);
                break;
            case 0:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_default);
                break;
            case 1:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_qipao);
                break;
            case 3:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_yemo);
                break;
            case 4:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_jianjie);
                break;
            case 5:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_xingkong);
				break;
            case 6:
                rootLayout.setBackgroundResource(R.drawable.bg_theme_gw);
                break;
        }
    }
}
