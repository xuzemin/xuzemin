
package com.cv.apk.manager;

import java.util.ArrayList;
import android.content.Context;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cv.apk.manager.utils.AllPagesAdapter;
import com.cv.apk.manager.view.AppCleanPageLayout;
import com.cv.apk.manager.view.AppStopPageLayout;
import com.cv.apk.manager.view.LoadingPageLayout;
import android.content.SharedPreferences;
import android.widget.FrameLayout;
import android.os.SystemProperties;


public class CleanOrStopAppActivity extends Activity implements OnClickListener {

    private LinearLayout ll_app_stop_select;

    private LinearLayout ll_app_clean_select;

    private ImageView iv_app_top_stop;

    private ImageView iv_app_top_clean;

    private TextView tv_app_top_stop;

    private TextView tv_app_top_clean;

    private AppStopPageLayout appStopPage;

    private AppCleanPageLayout appCleanPage;

    private ViewPager centerPager;

    private ArrayList<View> pages;

    public static boolean sysall_reading_to_over = false;

    public static int top_app_flag; // 显示
    FrameLayout rootView; //顶部的view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_stop);
        initView();
        if (!AppManager.sys_read_over) {
            sysall_reading_to_over = true;
            initLoadingPage();
        } else {
            initPages();
        }

        centerPager.setOnPageChangeListener(pageChangeListener);
    }

    @SuppressLint("CutPasteId")
    private void initView() {
        ll_app_stop_select = (LinearLayout) findViewById(R.id.ll_app_stop_select);
        ll_app_clean_select = (LinearLayout) findViewById(R.id.ll_app_clean_select);
        iv_app_top_stop = (ImageView) findViewById(R.id.iv_app_top_back);
        iv_app_top_clean = (ImageView) findViewById(R.id.iv_app_top_clean);
        tv_app_top_stop = (TextView) findViewById(R.id.tv_app_top_back);
        tv_app_top_clean = (TextView) findViewById(R.id.tv_app_top_clean);
        top_app_flag = 0;
        centerPager = (ViewPager) findViewById(R.id.clean_apk_content);
        ll_app_stop_select.setOnClickListener(this);
        ll_app_clean_select.setOnClickListener(this);
        rootView = (FrameLayout)findViewById(R.id.root_view);

    }

    /**
     * 系统读取完毕，加载处理界面
     */
    public void initPages() {
        pages = new ArrayList<View>();
        appStopPage = new AppStopPageLayout(CleanOrStopAppActivity.this);
        appCleanPage = new AppCleanPageLayout(CleanOrStopAppActivity.this);
        pages.add(appStopPage);
        pages.add(appCleanPage);
        AllPagesAdapter pagesAdapter = new AllPagesAdapter(pages);
        centerPager.setAdapter(pagesAdapter);
        centerPager.setCurrentItem(0);
        appStopPage.getButtonView().requestFocus();
    }

    /**
     * 判断读取是否完成，没完成则加载 Loading页面
     */
    public void initLoadingPage() {
        ArrayList<View> pages = new ArrayList<View>();
        centerPager = (ViewPager) findViewById(R.id.clean_apk_content);
        LoadingPageLayout loadingPageLayout = new LoadingPageLayout(this);
        pages.add(loadingPageLayout);
        AllPagesAdapter pagesAdapter = new AllPagesAdapter(pages);
        centerPager.setAdapter(pagesAdapter);
        appStopPage.getButtonView().requestFocus();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (ll_app_stop_select.isFocused()) {
                return true;
            }
            if (top_app_flag == 1) {
                if (appCleanPage.selector_none_button.isFocused()) {
                    appCleanPage.selector_all_button.requestFocus();
                } else if (appCleanPage.clean.isFocused()) {
                    appCleanPage.selector_none_button.requestFocus();
                } else {
                    ll_app_clean_select.requestFocus();
                }
                return true;
            } else {
                if (appStopPage.stop_seletor_none_btn.isFocused()) {
                    appStopPage.stop_seletor_all_btn.requestFocus();
                } else if (appStopPage.stop_btn.isFocused()) {
                    appStopPage.stop_seletor_none_btn.requestFocus();
                } else {
                    ll_app_stop_select.requestFocus();
                }
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (ll_app_clean_select.isFocused()) {
                jumpCleantoStop();
                return true;
            }
            return super.onKeyDown(keyCode, event);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (ll_app_stop_select.isFocused()) {
                jumpStoptoClean();
                return true;
            } else if (ll_app_clean_select.isFocused()) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onClick(View view) {
        if (ll_app_stop_select == view) {
            jumpCleantoStop();
        } else if (ll_app_clean_select == view) {
            jumpStoptoClean();
        }
    }

    private void jumpCleantoStop() {
        top_app_flag = 0;
        iv_app_top_clean.setBackgroundResource(R.drawable.cleancache);
        tv_app_top_clean.setTextColor(0x50ffffff);
        iv_app_top_stop.setBackgroundResource(R.drawable.backstage_focus);
        tv_app_top_stop.setTextColor(0xffffffff);
        centerPager.setCurrentItem(0);
        appStopPage.getButtonView().requestFocus();
    }

    private void jumpStoptoClean() {
        // stop->clean
        top_app_flag = 1;
        iv_app_top_stop.setBackgroundResource(R.drawable.backstage);
        tv_app_top_stop.setTextColor(0x50ffffff);
        iv_app_top_clean.setBackgroundResource(R.drawable.cleancache_focus);
        tv_app_top_clean.setTextColor(0xffffffff);
        centerPager.setCurrentItem(1);
        appCleanPage.getButtonView().requestFocus();
    }

    public void selected(int index) {
        Log.d("selected", "index->" + index);
        if (index == 0) {
            top_app_flag = 0;
            iv_app_top_clean.setBackgroundResource(R.drawable.cleancache);
            tv_app_top_clean.setTextColor(0x50ffffff);
            iv_app_top_stop.setBackgroundResource(R.drawable.backstage_focus);
            tv_app_top_stop.setTextColor(0xffffffff);
            appStopPage.getButtonView().requestFocus();
        } else {
            top_app_flag = 1;
            iv_app_top_stop.setBackgroundResource(R.drawable.backstage);
            tv_app_top_stop.setTextColor(0x50ffffff);
            iv_app_top_clean.setBackgroundResource(R.drawable.cleancache_focus);
            tv_app_top_clean.setTextColor(0xffffffff);
            appCleanPage.getButtonView().requestFocus();
        }
    }

    OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            selected(position);

        }

        @Override
        public void onPageScrolled(int position, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
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
