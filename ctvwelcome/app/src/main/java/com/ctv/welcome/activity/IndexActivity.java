
package com.ctv.welcome.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ctv.welcome.R;
import com.bumptech.glide.Glide;
import com.ctv.welcome.adapter.CategoryAdapter;
import com.ctv.welcome.adapter.CategoryAdapter.OnCategoryClickListener;
import com.ctv.welcome.adapter.ContentAdapter;
import com.ctv.welcome.adapter.CustomAdapter;
import com.ctv.welcome.adapter.CustomContentAdapter;
import com.ctv.welcome.adapter.CustomThemeAdapter;
import com.ctv.welcome.adapter.CustomThemeAdapter.onItemClickListener;
import com.ctv.welcome.constant.Ad;
import com.ctv.welcome.constant.Building;
import com.ctv.welcome.constant.Car;
import com.ctv.welcome.constant.Clothing;
import com.ctv.welcome.constant.Company;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.constant.Constants;
import com.ctv.welcome.constant.Customer;
import com.ctv.welcome.constant.Education;
import com.ctv.welcome.constant.Government;
import com.ctv.welcome.constant.Manufacture;
import com.ctv.welcome.constant.Medical;
import com.ctv.welcome.constant.Tobacco;
import com.ctv.welcome.constant.Traffic;
import com.ctv.welcome.constant.WelcomeModule;
import com.ctv.welcome.task.SystemPropertiesProxy;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.util.DBUtil;
import com.ctv.welcome.util.FileUtil;
import com.ctv.welcome.util.FileUtils;
import com.ctv.welcome.util.LogUtils;
import com.ctv.welcome.util.PreferencesUtil;
import com.ctv.welcome.util.Utils;
import com.ctv.welcome.view.ObservableScrollView;
import com.ctv.welcome.view.ObservableScrollView.ScrollViewListener;
import com.ctv.welcome.vo.CategoryContentData;
import com.ctv.welcome.vo.CategoryIndexData;
import com.ctv.welcome.vo.CustomContentData;
import com.ctv.welcome.vo.IndexData;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class IndexActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "IndexActivity";

    public static boolean mIsWindowMode;

    private ImageView blurView;

    private List<CategoryContentData> categoryContentList;

    private ImageView imgSignature;

    private ImageView imgWelcome;

    private LinkedHashMap<Integer, List<CategoryContentData>> listLinkedHashMap;

    private ImageView mAboutIv;

    private TextView mCancel;

    private CategoryAdapter mCategoryAdapter;

    private RecyclerView mCategoryContentRV;

    private RecyclerView mCategoryRV;

    private TextView mCategoryTitleTV;

    private int mChildVersion;

    private ContentAdapter mContentAdapter;

    private RelativeLayout mContentRecyc;

    private int mCurId;

    private CustomAdapter mCustomAdapter;

    private CustomContentAdapter mCustomContentAdaper;

    private ArrayList<IndexData> mCustomList;

    private CustomThemeAdapter mCustomThemeAdaper;

    private ImageView mExit;

    private RelativeLayout mParentLlt;

    private int mParentVersion;

    public PopupWindow mPopupWindow;

    private ImageView mZoomBtn;

    private ObservableScrollView scrollCategoryContent;

    private LinearLayout txtEndHint;

    private TextView txtSignatureBg;

    private class findUserAddModule implements Runnable {
        private String categoryName;

        private boolean isAddTheme;

        private boolean isScroll;

        public findUserAddModule(String categoryName, boolean isAddTheme, boolean isScroll) {
            this.categoryName = categoryName;
            this.isAddTheme = isAddTheme;
            this.isScroll = isScroll;
        }

        public void run() {
            String addModulePath;
            String customPath;
            if (isAddTheme) {
                addModulePath = Config.CUSTOM_CATEGORY_CONTENT_PATH + Config.CUSTOME + "/"
                        + categoryName;
            } else {
                addModulePath = Config.CUSTOM_CATEGORY_CONTENT_PATH + categoryName;
            }
            LogUtils.d(IndexActivity.TAG, "findUserAddModule,categoryName:" + categoryName);
            LogUtils.d(IndexActivity.TAG, "findUserAddModule,addModulePath:" + addModulePath);
            List<CategoryContentData> localData = FileUtils.getCategoryFiles(addModulePath);
            if (localData != null && localData.size() > 0) {
                LOG("add module size:" + localData.size());
                categoryContentList.addAll(localData);
            } else if (localData == null) {
                LOG("add module == null");
            } else if (localData.size() == 0) {
                LOG("add module size:0");
            }
            if (isAddTheme) {
                customPath = FileUtil.getBaseStorageDir() + "/" + Config.THEME_PICTURE + "/"
                        + Config.CUSTOME + "/" + categoryName;
            } else {
                customPath = FileUtil.getBaseStorageDir() + "/" + Config.THEME_PICTURE + "/"
                        + categoryName;
            }
            LogUtils.d(IndexActivity.TAG, "findUserAddModule,customPath:" + customPath);
            List<CategoryContentData> customContent = FileUtils.getCategoryFiles(customPath);
            if (customContent != null && customContent.size() > 0) {
                LOG("custom content size:" + customContent.size());
                categoryContentList.addAll(customContent);
            } else if (customContent == null) {
                LOG("custom content == null");
            } else if (customContent.size() == 0) {
                LOG("custom content size:0");
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    updateCategoryContentList(
                            findUserAddModule.this.categoryName, findUserAddModule.this.isAddTheme,
                            findUserAddModule.this.isScroll);
                }
            });
        }
    }

    private static LayoutManager getLayoutManager(Context context, int coloumNum) {
        return new GridLayoutManager(context, coloumNum);
    }

    private static List<CategoryContentData> initCategoryContent(Context context, int[] titles,
            int[] icons, int[] texts, int[] images) {
        List<CategoryContentData> list = new ArrayList();
        int len = titles.length;
        for (int in = 0; in < len; in++) {
            CategoryContentData data = new CategoryContentData();
            data.setName(context.getString(titles[in]));
            data.setIcon(Integer.valueOf(icons[in]));
            data.setImageRes(Integer.valueOf(images[in]));
            data.setText(context.getString(texts[in]));
            list.add(data);
        }
        return list;
    }

    private static List<CategoryContentData> initCategoryContent(Context context, int[] titles,
            int[] icons, int[] texts, int[] images, String[] mainTitle, String[] subTitle,
            float[] mainTitleX, float[] mainTitleY, float[] subTitleX, float[] subTitleY) {
        List<CategoryContentData> list = new ArrayList();
        int len = titles.length;
        for (int in = 0; in < len; in++) {
            CategoryContentData data = new CategoryContentData();
            data.setName(context.getString(titles[in]));
            data.setIcon(Integer.valueOf(icons[in]));
            data.setImageRes(Integer.valueOf(images[in]));
            data.setText(context.getString(texts[in]));
            data.setMainTitleHtml(mainTitle[in]);
            data.setSubTitleHtml(subTitle[in]);
            data.setMainTitleX(mainTitleX[in]);
            data.setMainTitleY(mainTitleY[in]);
            data.setSubTitleX(subTitleX[in]);
            data.setSubTitleY(subTitleY[in]);
            list.add(data);
        }
        return list;
    }

    private static List<CategoryContentData> initWelcomeContent(Context context, int[] titles,
            int[] icons, int[] texts, int[] images, String[] mainTitle, String[] subTitle,
            float[] mainTitleX, float[] mainTitleY, float[] subTitleX, float[] subTitleY) {
        List<CategoryContentData> list = new ArrayList();
        int len = titles.length;
        for (int in = 0; in < len; in++) {
            CategoryContentData data = new CategoryContentData();
            data.setName(context.getString(titles[in]));
            data.setIcon(Integer.valueOf(icons[in]));
            data.setImageRes(Integer.valueOf(images[in]));
            data.setText(context.getString(texts[in]));
            data.setMainTitleHtml(mainTitle[in]);
            data.setSubTitleHtml(subTitle[in]);
            data.setMainTitleX(mainTitleX[in]);
            data.setMainTitleY(mainTitleY[in]);
            data.setSubTitleX(subTitleX[in]);
            data.setSubTitleY(subTitleY[in]);
            list.add(data);
        }
        for (Entry<Integer, String> entry : WelcomeModule.SUBTITLE2_HTMLTEXT.entrySet()) {
            Integer key = (Integer) entry.getKey();
            CategoryContentData data = (CategoryContentData) list.get(key.intValue());
            data.setSubTitle2Html((String) entry.getValue());
            data.setSubTitle2X(((Float) WelcomeModule.SUBTITLE2_X.get(key)).floatValue());
            data.setSubTitle2Y(((Float) WelcomeModule.SUBTITLE2_Y.get(key)).floatValue());
        }
        return list;
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LogUtils.i(TAG, "-----onCreate-----");
        mIsWindowMode = DBUtil.isWindowMode();
        initData();
        if (mIsWindowMode) {
            setContentView( R.layout.activity_index_window);
        } else {
            setContentView(R.layout.activity_index);
        }
        initScreenSize();
        changeWindowSize(mIsWindowMode);
        EventBus.getDefault().register(this);
    }

    protected void onResume() {
        super.onResume();
        LogUtils.i(TAG, "-----onResume-----");
        boolean isWindowMode = DBUtil.isWindowMode();
        if (mIsWindowMode != isWindowMode) {
            mIsWindowMode = isWindowMode;
            if (mIsWindowMode) {
                setContentView((int) R.layout.activity_index_window);
            } else {
                setContentView((int) R.layout.activity_index);
            }
            changeWindowSize(mIsWindowMode);
        }

      //  layoutManager.findViewByPosition

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus){
            int position = mCategoryAdapter.getSelectId();
            if(position == -1){
                position = 0;
                mCategoryAdapter.setSelectId(position);
                View view = mCategoryRV.getLayoutManager().findViewByPosition(position);
                // RecyclerView.ViewHolder viewHolder = mCategoryRV.findViewHolderForAdapterPosition(position);
                //  mCategoryAdapter.
                // View view = viewHolder.itemView;
                mCategoryAdapter.setmCurrentSelected(view,position);
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(String tag) {
        if (!TextUtils.isEmpty(tag) && tag.contains("refresh")) {
            String[] name = tag.split(":");
            if (name[1].equals("signature")) {
                addSignaturePicture();
                return;
            }
            int id = Utils.getIdByCategoryName(name[1]);
            if (id != -1) {
                if (mCategoryAdapter == null) {
                    return;
                }
                if (id == R.string.welcome_module) {
                    addWelcomeModule();
                    return;
                }
                mCategoryAdapter.setSelected(false);
                addCategoryContent(id);
            } else if (mCategoryAdapter != null) {
                mCategoryAdapter.setSelected(false);
                updateCustomCategoryContent(name[1], true);
            }
        }
    }

    private void addCategoryContent(int id) {
        categoryContentList = new ArrayList();
        categoryContentList.addAll((List) listLinkedHashMap.get(Integer.valueOf(id)));
        ThreadManager.getThreadPoolProxy().excute(
                new findUserAddModule(Utils.getCategoryNameById(id), false, false));
    }

    private void addWelcomeModule() {
        mCategoryAdapter.setSelected(true);
        mCategoryTitleTV.setText(getString(R.string.welcome_module));
        categoryContentList = new ArrayList();
        categoryContentList.addAll((List) listLinkedHashMap.get(Integer
                .valueOf(R.string.welcome_module)));
        ThreadManager.getThreadPoolProxy().excute(
                new findUserAddModule(getString(R.string.welcome_module_zh), false, false));
        if (mContentRecyc.getVisibility() == View.INVISIBLE || mContentRecyc.getVisibility() == View.GONE) {
            mContentRecyc.setVisibility(View.VISIBLE);
        }
    }

    private void addSignaturePicture() {
        mCategoryAdapter.setSelected(true);
        findImageFromCutsomOrSignature("/" + Config.SIGNATURE_PICTURE, R.string.signature, false,
                false);
        if (mContentRecyc.getVisibility() == View.INVISIBLE || mContentRecyc.getVisibility() == View.GONE) {
            mContentRecyc.setVisibility(View.VISIBLE);
        }
    }

    private void updateCustomCategoryContent(String industryName, boolean isAddTheme) {
        categoryContentList = new ArrayList();
        ThreadManager.getThreadPoolProxy().excute(
                new findUserAddModule(industryName, isAddTheme, false));
    }

    protected void initVariable() {
        mCategoryRV = (RecyclerView) bindView(R.id.index_category_rv);
        mCategoryContentRV = (RecyclerView) bindView(R.id.index_category_content_rv);
        imgSignature = (ImageView) bindView(R.id.img_signature_picture);
        imgWelcome = (ImageView) bindView(R.id.img_welcome_module);
        txtSignatureBg = (TextView) bindView(R.id.txt_signature_picture_bg);
        mCategoryTitleTV = (TextView) bindView(R.id.index_category_title_tv);
        mZoomBtn = (ImageView) bindView(R.id.index_zoom_iv);
        mCancel = (TextView) bindView(R.id.index_cancel_tv);
        mExit = (ImageView) bindView(R.id.index_exit_iv);
        mContentRecyc = (RelativeLayout) bindView(R.id.index_content_recycler_container_rlt);
        mAboutIv = (ImageView) bindView(R.id.index_about_iv);
        mParentLlt = (RelativeLayout) bindView(R.id.index_parent_llt);
        scrollCategoryContent = (ObservableScrollView) bindView(R.id.scroll_category_content);
        txtEndHint = (LinearLayout) bindView(R.id.linear_scroll_end_hint);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.index_zoom_iv:
                handleWindowChange();
                return;
            case R.id.index_exit_iv:
                if (mExit != null) {
                    finish();
                    return;
                }
                return;
            case R.id.index_about_iv:
                showAbout();
                return;
            case R.id.index_cancel_tv:
                hideCancel();
                return;
            case R.id.img_welcome_module:
                initWelcomeModule();
                return;
            case R.id.img_signature_picture:
                showSignaturePicture();
                return;
            default:
                return;
        }
    }

    private void showSignaturePicture() {
        mCategoryAdapter.setSelected(true);
        findImageFromCutsomOrSignature("/" + Config.SIGNATURE_PICTURE, R.string.signature, false,
                true);
        if (mContentRecyc.getVisibility() == View.INVISIBLE || mContentRecyc.getVisibility() == View.GONE) {
            mContentRecyc.setVisibility(View.VISIBLE);
        }
    }

    private void showAbout() {
        mParentVersion = SystemPropertiesProxy.getInt(this, "ro.product.customer.no", 0)
                .intValue();
        mChildVersion = SystemPropertiesProxy.getInt(this, "ro.product.customer.child.no", 0)
                .intValue();
        System.out.println("mParentVersion" + mParentVersion);
        System.out.println("mChildVersion" + mChildVersion);
        View about = LayoutInflater.from(this).inflate(R.layout.layout_about, null);
        blurView = new ImageView(this);
        LayoutParams layoutParams = new LayoutParams(-1, -1);
        WeakReference<Bitmap> bitmapWeakReference = FileUtil.takeIndexActivity(this,
                mParentLlt.getLeft(), mParentLlt.getTop());
        blurView.setScaleType(ScaleType.FIT_XY);
        blurView.setBackgroundDrawable(new BitmapDrawable(
                blurBitmap((Bitmap) bitmapWeakReference.get())));
        mParentLlt.addView(blurView, layoutParams);
        ImageView logo = (ImageView) about.findViewById(R.id.lyt_about_logo_iv);
        TextView company = (TextView) about.findViewById(R.id.lyt_about_company_tv);
        TextView officialWebsite = (TextView) about
                .findViewById(R.id.lyt_about_official_website_tv);
        TextView number = (TextView) about.findViewById(R.id.lyt_about_number_tv);
        TextView webText = (TextView) about.findViewById(R.id.lyt_about_website_tv);
        TextView txtConfirm = (TextView) about.findViewById(R.id.txt_about_confirm);
        if (mParentVersion == 1 && mChildVersion == 1) {
            Glide.with((Activity) this).load(Integer.valueOf(R.drawable.seelink_logo)).into(logo);
            company.setText(R.string.company_name_seelink);
            officialWebsite.setText(" www.seelink.vip");
            number.setText(R.string.contact_number_seelink);
        }
        if (mParentVersion == 1 && mChildVersion == 0) {
            logo.setVisibility(View.GONE);
            company.setVisibility(View.GONE);
            webText.setVisibility(View.GONE);
            officialWebsite.setVisibility(View.GONE);
            number.setVisibility(View.GONE);
        }
        TextView version = (TextView) about.findViewById(R.id.lyt_about_version_tv);
        try {
            TextView textView = version;
            textView.setText(getResources().getString(R.string.version)
                    + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        final PopupWindow popupWindow = new PopupWindow(about, -2, -2, true);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAtLocation(mParentLlt, 17, 0, 0);
        popupWindow.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                if (blurView != null) {
                    mParentLlt.removeView(blurView);
                }
            }
        });
        txtConfirm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void hideCancel() {
        if (mCustomContentAdaper != null) {
            mCustomContentAdaper.setDeleteVisible(false);
        }
        if (mCancel != null) {
            mCancel.setVisibility(View.GONE);
        }
    }

    protected void initListener() {
        mCancel.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mZoomBtn.setOnClickListener(this);
        mAboutIv.setOnClickListener(this);
        imgSignature.setOnClickListener(this);
        imgWelcome.setOnClickListener(this);
        scrollCategoryContent.setScrollViewListener(new ScrollViewListener() {
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx,
                    int oldy) {
                int scrollX = scrollView.getScrollX();
                int scrollY = scrollView.getScrollY();
                if (scrollY + scrollView.getHeight() >= mCategoryContentRV
                        .getMeasuredHeight()) {
                  //  txtEndHint.setVisibility(View.VISIBLE);
                } else {
                 //   txtEndHint.setVisibility(View.GONE);f
                }
            }
        });
    }

    protected void initData() {
        listLinkedHashMap = new LinkedHashMap();
        listLinkedHashMap.put(
                R.string.welcome_module,
                initWelcomeContent(this, WelcomeModule.TITLES, WelcomeModule.ICON,
                        WelcomeModule.TEXTS, WelcomeModule.IMAGE, WelcomeModule.MAINTITLE_HTMLTEXT,
                        WelcomeModule.SUBTITLE_HTMLTEXT, WelcomeModule.MAINTITLE_X,
                        WelcomeModule.MAINTITLE_Y, WelcomeModule.SUBTITLE_X,
                        WelcomeModule.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.company,
                initCategoryContent(this, Company.TITLES, Company.ICON, Company.TEXTS,
                        Company.IMAGE, Company.MAINTITLE_HTMLTEXT, Company.SUBTITLE_HTMLTEXT,
                        Company.MAINTITLE_X, Company.MAINTITLE_Y, Company.SUBTITLE_X,
                        Company.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.customer,
                initCategoryContent(this, Customer.TITLES, Customer.IMAGE, Customer.TEXTS,
                        Customer.IMAGE));
        listLinkedHashMap.put(
                R.string.ad,
                initCategoryContent(this, Ad.TITLES, Ad.ICON, Ad.TEXTS, Ad.IMAGE,
                        Ad.MAINTITLE_HTMLTEXT, Ad.SUBTITLE_HTMLTEXT, Ad.MAINTITLE_X,
                        Ad.MAINTITLE_Y, Ad.SUBTITLE_X, Ad.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.education,
                initCategoryContent(this, Education.TITLES, Education.ICON, Education.TEXTS,
                        Education.IMAGE, Education.MAINTITLE_HTMLTEXT, Education.SUBTITLE_HTMLTEXT,
                        Education.MAINTITLE_X, Education.MAINTITLE_Y, Education.SUBTITLE_X,
                        Education.SUBTITLE_Y));
        listLinkedHashMap.put(
                Integer.valueOf(R.string.government),
                initCategoryContent(this, Government.TITLES, Government.ICON, Government.TEXTS,
                        Government.IMAGE, Government.MAINTITLE_HTMLTEXT,
                        Government.SUBTITLE_HTMLTEXT, Government.MAINTITLE_X,
                        Government.MAINTITLE_Y, Government.SUBTITLE_X, Government.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.medical,
                initCategoryContent(this, Medical.TITLES, Medical.ICON, Medical.TEXTS,
                        Medical.IMAGE, Medical.MAINTITLE_HTMLTEXT, Medical.SUBTITLE_HTMLTEXT,
                        Medical.MAINTITLE_X, Medical.MAINTITLE_Y, Medical.SUBTITLE_X,
                        Medical.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.traffic,
                initCategoryContent(this, Traffic.TITLES, Traffic.ICON, Traffic.TEXTS,
                        Traffic.IMAGE, Traffic.MAINTITLE_HTMLTEXT, Traffic.SUBTITLE_HTMLTEXT,
                        Traffic.MAINTITLE_X, Traffic.MAINTITLE_Y, Traffic.SUBTITLE_X,
                        Traffic.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.car,
                initCategoryContent(this, Car.TITLES, Car.ICON, Car.TEXTS, Car.IMAGE,
                        Car.MAINTITLE_HTMLTEXT, Car.SUBTITLE_HTMLTEXT, Car.MAINTITLE_X,
                        Car.MAINTITLE_Y, Car.SUBTITLE_X, Car.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.manufacture,
                initCategoryContent(this, Manufacture.TITLES, Manufacture.ICON, Manufacture.TEXTS,
                        Manufacture.IMAGE, Manufacture.MAINTITLE_HTMLTEXT,
                        Manufacture.SUBTITLE_HTMLTEXT, Manufacture.MAINTITLE_X,
                        Manufacture.MAINTITLE_Y, Manufacture.SUBTITLE_X, Manufacture.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.clothing,
                initCategoryContent(this, Clothing.TITLES, Clothing.ICON, Clothing.TEXTS,
                        Clothing.IMAGE, Clothing.MAINTITLE_HTMLTEXT, Clothing.SUBTITLE_HTMLTEXT,
                        Clothing.MAINTITLE_X, Clothing.MAINTITLE_Y, Clothing.SUBTITLE_X,
                        Clothing.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.building,
                initCategoryContent(this, Building.TITLES, Building.ICON, Building.TEXTS,
                        Building.IMAGE, Building.MAINTITLE_HTMLTEXT, Building.SUBTITLE_HTMLTEXT,
                        Building.MAINTITLE_X, Building.MAINTITLE_Y, Building.SUBTITLE_X,
                        Building.SUBTITLE_Y));
        listLinkedHashMap.put(
                R.string.tobacco,
                initCategoryContent(this, Tobacco.TITLES, Tobacco.ICON, Tobacco.TEXTS,
                        Tobacco.IMAGE, Tobacco.MAINTITLE_HTMLTEXT, Tobacco.SUBTITLE_HTMLTEXT,
                        Tobacco.MAINTITLE_X, Tobacco.MAINTITLE_Y, Tobacco.SUBTITLE_X,
                        Tobacco.SUBTITLE_Y));
    }

    protected void initContent() {
        initCategory();
        initWelcomeModule();
    }

    private void initCustom() {
        mCustomList = new ArrayList();
        mCustomList.clear();
        IndexData data = new IndexData();
        data.setIcon(Integer.valueOf(Constants.mCustomIcons[0]));
        data.setName(getString(Constants.mCustomTitles[0]));
        mCustomList.add(data);
        if (new File(Config.SIGNATURE_PICTURE).exists()) {
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    IndexData data = new IndexData();
                    data.setIcon(Integer.valueOf(Constants.mCustomIcons[1]));
                    data.setName(getString(Constants.mCustomTitles[1]));
                    if (mCustomList != null
                            && mCustomList.size() < 2) {
                        mCustomList.add(data);
                    }
                }
            });
        }
        mCustomAdapter = new CustomAdapter(mCustomList, this);
        mCustomAdapter.setSelected(true);
        mCustomAdapter.setOnCategoryClickListener(new OnCategoryClickListener() {
            public void onCategoryClick(View view, int position, boolean isSelected) {
                if (position == 0) {
                    if (isSelected) {
                        mCategoryAdapter.setSelected(true);
                        if (mContentRecyc.getVisibility() == View.GONE
                                || mContentRecyc.getVisibility() == View.INVISIBLE) {
                            mContentRecyc.setVisibility(View.VISIBLE);
                        }
                        findImageFromCutsomOrSignature(Config.THEME_PICTURE,
                                R.string.custom_theme, true, false);
                    }
                } else if (isSelected) {
                    mCategoryAdapter.setSelected(true);
                    findImageFromCutsomOrSignature(Config.SIGNATURE_PICTURE,
                            R.string.signature, false, false);
                }
            }
        });
    }

    private void findImageFromCutsomOrSignature(final String picType, final int titleRes, final boolean isAdd,
            final boolean isScroll) {
        //final String str = picType;
        //final int i = titleRes;
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final List<CustomContentData> signatureData = FileUtils.getFiles(
                        FileUtil.getBaseStorageDir() + picType, isAdd);
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (isAdd) {
                            CustomContentData data = new CustomContentData();
                            data.setFilename(getResources().getString(
                                    R.string.add_theme));
                            data.setFilepath(Integer.valueOf(R.drawable.ic_custom_add));
                            if (signatureData == null || signatureData.size() <= 0) {
                                List<CustomContentData> datas = new ArrayList();
                                datas.add(data);
                                mCategoryTitleTV.setText(IndexActivity.this
                                        .getString(titleRes));
                                mCustomContentAdaper = new CustomContentAdapter(
                                        datas, mContentRecyc,
                                        mCancel, IndexActivity.this, isAdd);
                            } else {
                                signatureData.add(0, data);
                                mCategoryTitleTV.setText(IndexActivity.this
                                        .getString(titleRes));
                                mCustomContentAdaper = new CustomContentAdapter(
                                        signatureData, mContentRecyc,
                                        mCancel, IndexActivity.this, isAdd);
                            }
                        } else {
                            mCategoryTitleTV.setText(IndexActivity.this
                                    .getString(titleRes));
                            mCustomContentAdaper = new CustomContentAdapter(
                                    signatureData, mContentRecyc,
                                    mCancel, IndexActivity.this, isAdd);
                        }
                        mCategoryContentRV.setLayoutManager(IndexActivity
                                .getLayoutManager(IndexActivity.this, 3));
                        mCategoryContentRV.setAdapter(mCustomContentAdaper);
                        if (isScroll) {
                            scrollCategoryContent.scrollTo(0,
                                    scrollCategoryContent.getScrollY());
                        }
                    }
                });
            }
        });
    }

    private void initCategory() {
        ArrayList<CategoryIndexData> dataList = new ArrayList();
        int len = Constants.mCategoryTitles.length;
        for (int in = 0; in < len; in++) {
            CategoryIndexData data = new CategoryIndexData();
            data.setName(getString(Constants.mCategoryTitles[in]));
            data.setIcon(Integer.valueOf(Constants.mCategoryIcons[in]));
            data.setSelIcon(Integer.valueOf(Constants.mCategorySelIcons[in]));
            data.setId(Constants.mCategoryTitles[in]);
            dataList.add(data);
        }
        mCategoryAdapter = new CategoryAdapter(dataList, this);
        mCategoryRV.setAdapter(mCategoryAdapter);
        mCategoryRV.setLayoutManager(getLayoutManager(this, 2));
        mCategoryAdapter.setOnCategoryClickListener(new OnCategoryClickListener() {
            public void onCategoryClick(View view, int position, boolean isSelected) {

                if(view == null || view.getTag() == null){
                   return;
                }
                LOG("click=" + view.getTag());
                initCategoryContent(((CategoryIndexData) view.getTag()).getId());
                if (mContentRecyc.getVisibility() == View.INVISIBLE
                        || mContentRecyc.getVisibility() == View.GONE) {
                    mContentRecyc.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initWelcomeModule() {
        mCategoryAdapter.setSelected(true);
        mCategoryTitleTV.setText(getString(R.string.welcome_module));
        categoryContentList = new ArrayList();
        categoryContentList.addAll((List) listLinkedHashMap.get(Integer
                .valueOf(R.string.welcome_module)));
        ThreadManager.getThreadPoolProxy().excute(
                new findUserAddModule(getString(R.string.welcome_module_zh), false, true));
        if (mContentRecyc.getVisibility() == View.INVISIBLE || mContentRecyc.getVisibility() == View.GONE) {
            mContentRecyc.setVisibility(View.VISIBLE);
        }
    }

    private void initCategoryContent(int id) {
        mCategoryTitleTV.setText(getString(id));
        mCurId = id;
        switch (id) {
            case R.string.custom:
                findCustomTheme(Config.CUSTOM_THEME_MODULE_PATH, R.string.custom);
                return;
            default:
                categoryContentList = new ArrayList();
                categoryContentList.addAll((List) listLinkedHashMap.get(Integer
                        .valueOf(id)));
                ThreadManager.getThreadPoolProxy().excute(
                        new findUserAddModule(Utils.getCategoryNameById(id), false, true));
                return;
        }
    }

    private void findCustomTheme(final String customThemeModulePath, final int titleRes) {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final List<String> signatureData = FileUtils.getDirNames(customThemeModulePath);
                runOnUiThread(new Runnable() {
                    public void run() {
                        String data = getString(R.string.add_theme);
                        if (signatureData == null || signatureData.size() <= 0) {
                            List<String> datas = new ArrayList();
                            datas.add(data);
                            mCategoryTitleTV.setText(IndexActivity.this
                                    .getString(titleRes));
                            mCustomThemeAdaper = new CustomThemeAdapter(
                                    IndexActivity.this, datas);
                        } else {
                            signatureData.add(0, data);
                            mCategoryTitleTV.setText(IndexActivity.this
                                    .getString(titleRes));
                            mCustomThemeAdaper = new CustomThemeAdapter(
                                    IndexActivity.this, signatureData);
                        }
                        mCategoryContentRV.setLayoutManager(IndexActivity
                                .getLayoutManager(IndexActivity.this, 3));
                        mCategoryContentRV
                                .setAdapter(mCustomThemeAdaper);
                        mCustomThemeAdaper
                                .setOnItemClickListener(new onItemClickListener() {
                                    public void onItemClick(View view, int position) {
                                        String categoryName = (String) mCustomThemeAdaper
                                                .getDatas().get(position);
                                        mCategoryTitleTV.setText(categoryName);
                                        categoryContentList = new ArrayList();
                                        ThreadManager.getThreadPoolProxy().excute(
                                                new findUserAddModule(categoryName, true, true));
                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateCategoryContentList(String category_name, boolean isAddTheme,
            boolean isScroll) {
        if (categoryContentList == null || categoryContentList.size() <= 0) {
            categoryContentList = new ArrayList();
            mCategoryContentRV.setLayoutManager(getLayoutManager(this, 3));
            mContentAdapter = new ContentAdapter(categoryContentList, this,
                    category_name, isAddTheme);
            mCategoryContentRV.setAdapter(mContentAdapter);
        } else {
            mCategoryContentRV.setLayoutManager(getLayoutManager(this, 3));
            mContentAdapter = new ContentAdapter(categoryContentList, this,
                    category_name, isAddTheme);
            mCategoryContentRV.setAdapter(mContentAdapter);
        }
        if (isScroll) {
            scrollCategoryContent.scrollTo(0, scrollCategoryContent.getScrollY());
        }
    }

    private void handleWindowChange() {
        if (PreferencesUtil.isBigScreen()) {
            PreferencesUtil.smallScreen();
        } else {
            PreferencesUtil.bigScreen();
        }
        initWindowSize();
        handleWindowBtnBg();
    }

    private void handleWindowBtnBg() {
        int resId = R.drawable.zoommax;
        if (PreferencesUtil.isBigScreen()) {
            resId = R.drawable.zoommin;
        }
        mZoomBtn.setBackgroundResource(resId);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (!(mContentAdapter == null || mContentAdapter.mPopupWindow == null || !mContentAdapter.mPopupWindow
                .isShowing())) {
            mContentAdapter.mPopupWindow.dismiss();
            mContentAdapter.mPopupWindow = null;
        }
        if (!(mCustomContentAdaper == null || mCustomContentAdaper.mPopupWindow == null || !mCustomContentAdaper.mPopupWindow
                .isShowing())) {
            mCustomContentAdaper.mPopupWindow.dismiss();
            mCustomContentAdaper.mPopupWindow = null;
        }
        if (!(mPopupWindow == null || blurView == null || mParentLlt == null)) {
            mParentLlt.removeView(blurView);
        }
        return super.onTouchEvent(event);
    }

    public Bitmap blurBitmap(Bitmap bitmap) {
        Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(getApplicationContext());
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(25.0f);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        bitmap.recycle();
        rs.destroy();
        return outBitmap;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == 1 && data != null) {
            String file_path = data.getStringExtra("add_module_file");
            String industryName = data.getStringExtra("industry_name");
            boolean isAddTheme = data.getBooleanExtra("isAddTheme", false);
            LogUtils.i(TAG, "onActivityResult,filePath:" + file_path + ",industryName:"
                    + industryName + ",isAddTheme:" + isAddTheme);
            if (file_path != null) {
                mContentAdapter.addCategoryContent(file_path, industryName, isAddTheme);
            }
        }
    }
}
