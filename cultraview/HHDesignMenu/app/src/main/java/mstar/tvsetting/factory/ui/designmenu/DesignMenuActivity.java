package mstar.tvsetting.factory.ui.designmenu;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mstar.factorymenu.ui.R;

import mstar.factorymenu.ui.adapter.SubTitleAdapter;
import mstar.factorymenu.ui.adapter.TitleAdapter;
import mstar.factorymenu.ui.fragments.PictrueCurveTypeFragment;
import mstar.factorymenu.ui.fragments.PictrueModeFragment;
import mstar.factorymenu.ui.fragments.PictrueOverScanFragment;
import mstar.factorymenu.ui.fragments.PictrueWBFragment;
import mstar.factorymenu.ui.fragments.PictureADCFragment;
import mstar.factorymenu.ui.fragments.general.GeneralConfigFragment;
import mstar.factorymenu.ui.fragments.general.GeneralDebugFragment;
import mstar.factorymenu.ui.fragments.sound.SoundAdvancesFragment;
import mstar.factorymenu.ui.fragments.sound.SoundModeFragment;
import mstar.factorymenu.ui.fragments.sound.SoundVolOsdFragment;
import mstar.factorymenu.ui.fragments.system.SystemInformationFragment;
import mstar.factorymenu.ui.fragments.system.SystemUpgradeFragment;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.factorymenu.ui.view.CustomListView;
import mstar.factorymenu.ui.view.CustomTitleListView;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.vo.CtvEnumInputSource;
import com.hht.android.sdk.touch.HHTTouchManager;
import com.mstar.android.tvapi.common.TvManager;

import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

public class DesignMenuActivity extends FragmentActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = DesignMenuActivity.class.getSimpleName();
    private CustomTitleListView titleListView;
    private CustomListView subTitleListView;
    private String[] titleString = {" PICTURE SETTING", " SOUND SETTING", " GENERAL SETTING", " SYSTEM SETTING"};
    private String[] subTitleString = {"ADC", "  Picture Mode", "WB Adjust", "Over Scan", " Picture Curve Type"};
    private String[] subSoundTitleString = {"Sound Mode", "VOL_OSD", "Advanced"};
    private String[] subGeneralTitleString = {"Debug", "Default configuration"};
    private String[] subSystemTitleString = {"System Information", "System Upgrade"};

    private IFactoryDesk factoryDesk;

    protected final static int MSG_START_RESET_ALL = 110;
    protected final static int MSG_START_SHIPPING_MODE = 111;
    protected final static int MSG_REBOOT_SYSTEM = 121;
    protected final static int MSG_INTO_RECOVERY = 131;
    protected final static int MSG_INTO_LOAD_DB = 141;
    protected final static int MSG_INTO_CREATE_DESIGN_VIEW_HOLDER = 151;


    private List<Fragment> listFragments = new ArrayList();
    private Fragment currentFragment = new Fragment();
    private int titleIndex = 0;
    private int subTitleIndex = 0;
    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_INTO_LOAD_DB) {
                factoryDesk = FactoryDeskImpl.getInstance(DesignMenuActivity.this);
                factoryDesk.loadEssentialDataFromDB();
                showFirstFragment();
            }
        }
    };


    private FrameLayout frameLayout;
    private PictrueModeFragment pictrueModeFragment;
    private PictureADCFragment pictureADCFragment;
    private TitleAdapter titleAdapter;
    private SubTitleAdapter subTitleAdapter;
    private PictrueWBFragment pictrueWBFragment;
    private PictrueOverScanFragment pictrueOverScanFragment;
    private PictrueCurveTypeFragment pictrueCurveTypeFragment;
    private SoundModeFragment soundModeFragment;
    private SoundVolOsdFragment soundVolOsdFragment;
    private SoundAdvancesFragment soundAdvancesFragment;
    private GeneralDebugFragment generalDebugFragment;
    private GeneralConfigFragment generalConfigFragment;
    private SystemInformationFragment systemInformationFragment;
    private SystemUpgradeFragment systemUpgradeFragment;
    private LinearLayout frameTitle, frameSbuTitle;

    private String action = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design_menu);
        action = getIntent().getStringExtra("data");
        findView();
        initData();
        handler.sendEmptyMessage(MSG_INTO_LOAD_DB);
        setData();
        setSelect();
        finishSettingAndMenu();
        HHTTouchManager.getInstance().controlPcTouchRect("mstar.factorymenu.ui.hh","hht_win_factory",
                new Rect(0,0,1685,1080),false);
    }


    private void setData() {
        titleAdapter = new TitleAdapter(this, R.layout.item_title, Arrays.asList(titleString));
        if (!TextUtils.isEmpty(action)) { //显示 System Information
            subTitleAdapter = new SubTitleAdapter(this, R.layout.item_title, Arrays.asList(subSystemTitleString));
            titleIndex = 3;
        } else {
            subTitleAdapter = new SubTitleAdapter(this, R.layout.item_title, Arrays.asList(subTitleString));
        }
        titleListView.setAdapter(titleAdapter);
        subTitleListView.setAdapter(subTitleAdapter);
        titleListView.setOnItemClickListener(this);
        titleListView.setOnItemSelectedListener(this);
        subTitleListView.setOnItemSelectedListener(this);
        subTitleListView.setOnItemClickListener(this);
        titleListView.setNextFocusRightId(R.id.list_sub_title);
        subTitleListView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KEYCODE_DPAD_UP) {
                        if (!isADCFragmentShow() && subTitleAdapter.getSelectString().equals(subTitleString[1])) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });

    }

    /**
     * 设置默认listview 焦点
     */
    private void setSelect() {
        if (TextUtils.equals(action, "swVersion")) {
            titleListView.setSelection(3);
            subTitleListView.setSelection(0);
            titleAdapter.changeSelected(3);
            subTitleAdapter.changeSelected(0);
            return;
        }
        if (isADCFragmentShow()) {
            titleListView.setSelection(0);
            subTitleListView.setSelection(0);
            titleAdapter.changeSelected(0);
            subTitleAdapter.changeSelected(0);
        } else {
            titleListView.setSelection(0);
            subTitleListView.setSelection(1);
            titleAdapter.changeSelected(0);
            subTitleAdapter.changeSelected(1);
        }

    }

    /**
     * 显示fragment
     */
    private void showFirstFragment() {
        if (TextUtils.isEmpty(action)) {
            if (isADCFragmentShow()) {
                switchFragment(pictureADCFragment).commit();
            } else {
                switchFragment(pictrueModeFragment).commit();
            }
        } else if (TextUtils.equals(action, "swVersion")) {
            switchFragment(systemInformationFragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void findView() {
        titleListView = findViewById(R.id.list_title);
        subTitleListView = findViewById(R.id.list_sub_title);
        frameLayout = findViewById(R.id.fr_layout);
        frameTitle = findViewById(R.id.ff_title);
        frameSbuTitle = findViewById(R.id.ff_subtitle);
    }

    private void initData() {
        pictureADCFragment = PictureADCFragment.newInstance("", "");
        pictrueModeFragment = PictrueModeFragment.newInstance("", "");
        pictrueWBFragment = PictrueWBFragment.newInstance("", "");
        pictrueOverScanFragment = PictrueOverScanFragment.newInstance("", "");
        pictrueCurveTypeFragment = PictrueCurveTypeFragment.newInstance("", "");

        soundModeFragment = SoundModeFragment.newInstance("", "");
        soundVolOsdFragment = SoundVolOsdFragment.newInstance("", "");
        soundAdvancesFragment = SoundAdvancesFragment.newInstance("", "");

        generalDebugFragment = GeneralDebugFragment.newInstance("", "");
        generalConfigFragment = GeneralConfigFragment.newInstance("", "");
        systemInformationFragment = SystemInformationFragment.newInstance("", "");
        systemUpgradeFragment = SystemUpgradeFragment.newInstance("", "");

        if (TextUtils.isEmpty(action)) {
            listFragments.add(pictrueModeFragment);
            listFragments.add(pictureADCFragment);
            listFragments.add(pictrueWBFragment);
            listFragments.add(pictrueOverScanFragment);
            listFragments.add(pictrueCurveTypeFragment);
        } else if (TextUtils.equals(action, "swVersion")) {
            listFragments.add(systemInformationFragment);
            listFragments.add(systemUpgradeFragment);
        }


    }

    /**
     * 切换Fragment
     *
     * @param targetFragment
     * @return
     */
    private FragmentTransaction switchFragment(Fragment targetFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!targetFragment.isAdded()) {
            //第一次使用switchFragment()时currentFragment为null，所以要判断一下
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.fr_layout, targetFragment, targetFragment.getClass().getName());

        } else {
            transaction
                    .hide(currentFragment)
                    .show(targetFragment);
        }
        currentFragment = targetFragment;

        return transaction;
    }

    /**
     * list item onClick
     *
     * @param adapterView
     * @param view
     * @param position
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (titleAdapter.equals(adapterView.getAdapter())) {
            updateSubTitle(position);
            titleAdapter.changeSelected(position);
        } else {
            updateDetail(position);
            subTitleAdapter.changeSelected(position);
        }
    }

    /**
     * 详细页面
     *
     * @param position
     */
    private void updateDetail(int position) {

        switch (position) {
            case 0:
                if (titleIndex == 0) {
                    switchFragment(pictureADCFragment).commit();
                } else if (titleIndex == 1) {
                    switchFragment(soundModeFragment).commit();
                } else if (titleIndex == 2) {
                    switchFragment(generalDebugFragment).commit();
                } else if (titleIndex == 3) {
                    switchFragment(systemInformationFragment).commit();
                    systemInformationFragment.clearOnFocus();
                }
                break;
            case 1:
                if (titleIndex == 0) {
                    switchFragment(pictrueModeFragment).commit();
                } else if (titleIndex == 1) {
                    switchFragment(soundVolOsdFragment).commit();
                } else if (titleIndex == 2) {
                    switchFragment(generalConfigFragment).commit();
                } else if (titleIndex == 3) {
                    switchFragment(systemUpgradeFragment).commit();
                    systemUpgradeFragment.clearOnFocus();
                }
                break;
            case 2:
                if (titleIndex == 0) {
                    switchFragment(pictrueWBFragment).commit();
                } else if (titleIndex == 1) {
                    switchFragment(soundAdvancesFragment).commit();
                }
                break;

            case 3:
                if (titleIndex == 0) {
                    switchFragment(pictrueOverScanFragment).commit();
                }
                break;

            case 4:
                if (titleIndex == 0) {
                    switchFragment(pictrueCurveTypeFragment).commit();
                }
                break;
        }
        frameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 更新二级菜单
     *
     * @param position
     */
    private void updateSubTitle(int position) {
        frameLayout.setVisibility(View.GONE);
        titleIndex = position;
        switch (position) {
            case 0:
                subTitleAdapter.setData(Arrays.asList(subTitleString));
                break;
            case 1:
                subTitleAdapter.setData(Arrays.asList(subSoundTitleString));
                listFragments.clear();
                listFragments.add(soundModeFragment);
                listFragments.add(soundVolOsdFragment);
                listFragments.add(soundAdvancesFragment);
                break;
            case 2:

                subTitleAdapter.setData(Arrays.asList(subGeneralTitleString));
                listFragments.clear();
                listFragments.add(generalDebugFragment);
                listFragments.add(generalConfigFragment);
                break;
            case 3:
                subTitleAdapter.setData(Arrays.asList(subSystemTitleString));
                listFragments.clear();
                listFragments.add(systemInformationFragment);
                listFragments.add(systemUpgradeFragment);
                break;
        }
    }

    /**
     * 关闭设置
     */
    private void finishSettingAndMenu() {
        try {
            ActivityManager sd = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(sd, "com.hht.settings");
        } catch (Exception e) {
            LogUtils.e("Exception:" + e);
        }
        try {
            ActivityManager sd = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod("forceStopPackage", String.class);
            method.invoke(sd, "com.cultraview.ctvmenu");
        } catch (Exception e) {
            LogUtils.e("Exception:" + e);
        }
    }

    /**
     * 当前通道不在VGA或Yp 就不显示ADC
     *
     * @return
     */
    private boolean isADCFragmentShow() {
        int currentTvInputSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        LogUtils.d("currentTvInputSource-->" + currentTvInputSource);
        if (currentTvInputSource == CtvEnumInputSource.E_INPUT_SOURCE_VGA.ordinal()
                || currentTvInputSource == CtvEnumInputSource.E_INPUT_SOURCE_YPBPR.ordinal()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getAdapter() == titleAdapter) {
            titleAdapter.changeSelected(position);
        } else {
            if (subTitleListView.isFocused()) {
                subTitleAdapter.changeSelected(position);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void RefreshList() {
        if (subTitleAdapter != null) {
            subTitleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HHTTouchManager.getInstance().controlPcTouchRect("mstar.factorymenu.ui.hh","hht_win_factory",
                new Rect(0,0,1685,1080),true);
        finish();
    }
}
