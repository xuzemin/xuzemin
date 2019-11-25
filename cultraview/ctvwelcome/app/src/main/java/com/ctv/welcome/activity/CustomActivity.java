
package com.ctv.welcome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter.OnRecyclerViewItemClickListener;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;
import com.ctv.welcome.adapter.FileBrowserAdapter;
import com.ctv.welcome.adapter.FileBrowserListAdapter;
import com.ctv.welcome.adapter.LocalFileAdapter;
import com.ctv.welcome.adapter.TabAdapter;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.constant.DishType;
import com.ctv.welcome.listener.OnItemClickListener;
import com.ctv.welcome.listener.OnItemDoubleClickListener;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.task.VIPApplication;
import com.ctv.welcome.util.FileUtil;
import com.ctv.welcome.util.FileUtils;
import com.ctv.welcome.util.LogUtils;
import com.ctv.welcome.util.ProgressDialogUtil;
import com.ctv.welcome.vo.CustomContentData;
import com.ctv.welcome.vo.LocalFile;
import com.ctv.welcome.vo.XZDish;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomActivity extends BaseActivity implements OnClickListener {
    private static final String TAG = "CustomActivity";

    private ArrayList<LocalFile> datas;

    private TextView filePathTv;

    private String industryName;

    private boolean isAddModule = false;

    private boolean isAddTheme;

    private LocalFile localFile;

    private FileBrowserAdapter localFileAdapter;

    private FileBrowserListAdapter localFileListAdapter;

    private int mCurSortType = 0;

    private Map<String, XZDish> mLeftContentMap;

    private LinearLayout mLeftParent;

    private RecyclerView mListRecyclerView;

    private String mRealStorePath;

    private RecyclerView mRecyclerView;

    private PopupWindow mSortPopup;

    private String mStorePathText;

    private RecyclerView mUsbImageRecyclerView;

    private UsbReceiver mUsbReceiver;

    private TextView txtSortType;

    private TextView txtTitle;

    private class ListPictureRunnable implements Runnable {
        private String newPath;

        public ListPictureRunnable(String newPath) {
            this.newPath = newPath;
        }

        public void run() {
            final ArrayList<LocalFile> datas = FileUtil.listPictures(new File(this.newPath));
            CustomActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (datas == null || datas.size() <= 0) {
                        CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                        return;
                    }
                    CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                    CustomActivity.this.updateFileBrowser(datas);
                }
            });
        }
    }

    class UsbReceiver extends BroadcastReceiver {
        private String path;

        UsbReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri data = intent.getData();
            if (data != null) {
                this.path = data.getPath();
                if (!action.equals("android.intent.action.MEDIA_MOUNTED")
                        && !intent.getAction().equals("android.intent.action.MEDIA_CHECKING")) {
                    String[] split = this.path.split("/");
                    if (!split[split.length - 1].equals(Config.SDCARD)
                            && !TextUtils.isEmpty(this.path) && this.path.contains(Config.UDISK)) {
                        CustomActivity.this.initUSB();
                    }
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pop_window_filebrower);
        initReceiver();
    }

    private void init() {
        for (String path : FileUtils.getUsbDevicePath()) {
            addLeftContent(path);
        }
    }

    private void initLocal() {
        View locale = getLayoutInflater().inflate(R.layout.layout_left_content, null);
        this.mLeftContentMap.put(XZDish.getLocalPath(), XZDish.getLocalDish(locale));
        handleDishSelected(this, locale, true);
    }

    private void initReceiver() {
        this.mUsbReceiver = new UsbReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
        intentFilter.addAction("android.intent.action.MEDIA_CHECKING");
        intentFilter.addAction("android.intent.action.MEDIA_EJECT");
        intentFilter.addAction("android.intent.action.MEDIA_REMOVED");
        intentFilter.addDataScheme("file");
        registerReceiver(this.mUsbReceiver, intentFilter);
    }

    public void onClick(View v) {
    }

    private void initUSB() {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final ArrayList<LocalFile> datas = FileUtil
                        .listPictures(new File(Config.UDISK_PATH));
                CustomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (datas == null || datas.size() <= 0) {
                            CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        CustomActivity.this.localFileAdapter.replaceData(datas);
                        CustomActivity.this.localFileAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mUsbReceiver);
    }

    protected void initVariable() {
        this.isAddModule = getIntent().getBooleanExtra("add_module", false);
        this.industryName = getIntent().getStringExtra("industry_name");
        this.isAddTheme = getIntent().getBooleanExtra("isAddTheme", false);
        this.txtTitle = (TextView) bindView(R.id.pop_fb_title);
        this.txtSortType = (TextView) bindView(R.id.txt_sort_type);
        ((TextView) bindView(R.id.pop_fb_time)).setText(new SimpleDateFormat().format(new Date()));
        this.mRecyclerView = (RecyclerView) bindView(R.id.pop_fb_recyclerView);
        this.mListRecyclerView = (RecyclerView) bindView(R.id.pop_list_recyclerView);
        ListView tabLv = (ListView) bindView(R.id.pop_fb_tab);
        this.filePathTv = (TextView) bindView(R.id.pop_fb_filePath);
        ImageView back = (ImageView) bindView(R.id.pop_fb_back);
        if (this.isAddModule) {
            this.txtTitle.setText(R.string.add_module);
        }
        LogUtils.i(TAG, "inner storage path:"
                + Environment.getExternalStorageDirectory().getAbsolutePath());
        this.mRealStorePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + Config.SAVE_FOLDER_NAME + "/" + Config.THEME_PICTURE;
        this.mStorePathText = getString(R.string.inner_storage) + "/" + Config.SAVE_FOLDER_NAME
                + "/" + Config.THEME_PICTURE;
        this.filePathTv.setText(this.mStorePathText);
        Button confirm = (Button) bindView(R.id.pop_fb_confirm);
        Button cancel = (Button) bindView(R.id.pop_fb_cancel);
        ArrayList<Integer> tabs = new ArrayList();
        tabs.add(Integer.valueOf(R.drawable.selector_tab_localstroage));
        tabs.add(Integer.valueOf(R.drawable.selector_tab_usb));
        tabLv.setAdapter(new TabAdapter(tabs));
        this.localFileAdapter = new FileBrowserAdapter(VIPApplication.getContext(), true);
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        this.mRecyclerView.setAdapter(this.localFileAdapter);
        initSortPop();
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final ArrayList<LocalFile> datas = FileUtil.listPictures(new File(
                        CustomActivity.this.mRealStorePath));
                CustomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (datas == null || datas.size() <= 0) {
                            CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        CustomActivity.this.localFileAdapter.replaceData(datas);
                        CustomActivity.this.localFileAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        this.localFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(LocalFile file) {
                CustomActivity.this.localFile = file;
            }
        });
        this.localFileAdapter.setOnItemDoubleClickListener(new OnItemDoubleClickListener() {
            public void OnItemDoubleClick(LocalFile file) {
                CustomActivity.this.iconSortDoubleClick(file);
            }
        });
        tabLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, final int position,
                    long id) {
                ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                    public void run() {
                        if (position == 0) {
                            CustomActivity.this.mRealStorePath = Environment
                                    .getExternalStorageDirectory().toString();
                            CustomActivity.this.mStorePathText = CustomActivity.this
                                    .getString(R.string.inner_storage);
                            Log.i(CustomActivity.TAG, "innerPath:"
                                    + CustomActivity.this.mRealStorePath);
                            CustomActivity.this.datas = FileUtil.listPictures(new File(
                                    CustomActivity.this.mRealStorePath));
                        } else {
                            CustomActivity.this.mRealStorePath = Config.UDISK_PATH;
                            CustomActivity.this.mStorePathText = CustomActivity.this.mRealStorePath;
                            CustomActivity.this.datas = FileUtil.listPictures(new File(
                                    CustomActivity.this.mRealStorePath));
                        }
                        CustomActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                CustomActivity.this.filePathTv
                                        .setText(CustomActivity.this.mStorePathText);
                                if (CustomActivity.this.datas == null
                                        || CustomActivity.this.datas.size() <= 0) {
                                    CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                                    return;
                                }
                                CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                                CustomActivity.this.updateFileBrowser(CustomActivity.this.datas);
                            }
                        });
                    }
                });
            }
        });
        this.txtSortType.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (CustomActivity.this.mSortPopup == null
                        || !CustomActivity.this.mSortPopup.isShowing()) {
                    CustomActivity.this.txtSortType
                            .setBackgroundResource(R.drawable.sort_type_selected);
                    CustomActivity.this.showSelectSortTypePop(view);
                    return;
                }
                CustomActivity.this.mSortPopup.dismiss();
            }
        });
        this.mSortPopup.setOnDismissListener(new OnDismissListener() {
            public void onDismiss() {
                CustomActivity.this.txtSortType.setBackgroundResource(R.drawable.sort_type_normal);
            }
        });
        back.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String string = CustomActivity.this.filePathTv.getText().toString();
                if (string.endsWith("0") || string.endsWith(Config.UDISK)
                        || string.endsWith(CustomActivity.this.getString(R.string.inner_storage))) {
                    System.out.println("return");
                    return;
                }
                CustomActivity.this.mStorePathText = string.substring(0, string.lastIndexOf("/"));
                CustomActivity.this.mRealStorePath = CustomActivity.this.mStorePathText;
                CustomActivity.this.filePathTv.setText(CustomActivity.this.mStorePathText);
                if (string.contains(CustomActivity.this.getString(R.string.inner_storage))) {
                    CustomActivity.this.mRealStorePath = CustomActivity.this.mStorePathText
                            .replace(CustomActivity.this.getString(R.string.inner_storage),
                                    Environment.getExternalStorageDirectory().getAbsolutePath());
                }
                ThreadManager.getThreadPoolProxy().excute(
                        new ListPictureRunnable(CustomActivity.this.mRealStorePath));
            }
        });
        confirm.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent;
                if (CustomActivity.this.localFile == null) {
                    if (CustomActivity.this.isAddModule) {
                        CustomActivity.this.setResult(0, new Intent());
                    }
                    CustomActivity.this.finish();
                } else if (CustomActivity.this.isAddModule) {
                    intent = new Intent();
                    intent.putExtra("add_module_file", CustomActivity.this.localFile.getFilePath());
                    intent.putExtra("industry_name", CustomActivity.this.industryName);
                    intent.putExtra("isAddTheme", CustomActivity.this.isAddTheme);
                    CustomActivity.this.setResult(1, intent);
                    CustomActivity.this.finish();
                } else {
                    intent = new Intent(CustomActivity.this, EditActivity.class);
                    intent.putExtra("IMAGE_PATH", CustomActivity.this.localFile.getFilePath());
                    intent.putExtra("SHOW_QROCDE", false);
                    CustomActivity.this.startActivity(intent);
                    CustomActivity.this.finish();
                }
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (CustomActivity.this.isAddModule) {
                    CustomActivity.this.setResult(0, new Intent());
                }
                CustomActivity.this.finish();
            }
        });
    }

    private void iconSortDoubleClick(LocalFile file) {
        this.mRealStorePath = file.getFilePath();
        this.mStorePathText = file.getFilePathText();
        this.filePathTv.setText(this.mStorePathText);
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final ArrayList<LocalFile> datas = FileUtil.listPictures(new File(
                        CustomActivity.this.mRealStorePath));
                CustomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (datas == null || datas.size() <= 0) {
                            CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        CustomActivity.this.updateFileBrowser(datas);
                    }
                });
            }
        });
    }

    private void initIconAdapter(ArrayList<LocalFile> datas, boolean isBigIcon, int columNum) {
        this.mRecyclerView.setLayoutManager(new GridLayoutManager(this, columNum));
        this.localFileAdapter = new FileBrowserAdapter(this, isBigIcon);
        this.localFileAdapter.replaceData(datas);
        this.mRecyclerView.setAdapter(this.localFileAdapter);
        this.localFileAdapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(LocalFile file) {
                CustomActivity.this.localFile = file;
            }
        });
        this.localFileAdapter.setOnItemDoubleClickListener(new OnItemDoubleClickListener() {
            public void OnItemDoubleClick(LocalFile file) {
                CustomActivity.this.iconSortDoubleClick(file);
            }
        });
    }

    private void initListAdapter(ArrayList<LocalFile> datas) {
        LogUtils.d(TAG, "initListAdapter,datas size:" + datas.size());
        this.localFileListAdapter = new FileBrowserListAdapter(this, datas);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.mRecyclerView.setAdapter(this.localFileListAdapter);
        this.localFileListAdapter.setOnItemDoubleClickListener(new OnItemDoubleClickListener() {
            public void OnItemDoubleClick(LocalFile file) {
                CustomActivity.this.listSortDoubleClick(file);
            }
        });
        this.localFileListAdapter.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(LocalFile file) {
                CustomActivity.this.localFile = file;
            }
        });
    }

    private void updateFileBrowser(ArrayList<LocalFile> datas) {
        if (this.mCurSortType == 0) {
            initIconAdapter(datas, true, 5);
        } else if (this.mCurSortType == 1) {
            initIconAdapter(datas, false, 3);
        } else {
            initListAdapter(datas);
        }
    }

    private void initSortPop() {
        View root = getLayoutInflater().inflate(R.layout.layout_sort, null);
        if (this.mSortPopup == null) {
            this.mSortPopup = new PopupWindow(90, 110);
            this.mSortPopup.setContentView(root);
            this.mSortPopup.setOutsideTouchable(true);
            this.mSortPopup.setBackgroundDrawable(new BitmapDrawable());
            ViewGroup group = (ViewGroup) root;
            int count = group.getChildCount();
            for (int in = 0; in < count; in++) {
                group.getChildAt(in).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        CustomActivity.this.handleSortTypeClick(v);
                    }
                });
            }
        }
    }

    private void showSelectSortTypePop(View view) {
        this.mSortPopup.showAsDropDown(view, -39, 0);
    }

    private void handleSortTypeClick(View v) {
        String path;
        String txtCurPath = this.filePathTv.getText().toString();
        if (txtCurPath.startsWith(getString(R.string.inner_storage))) {
            path = txtCurPath.replace(getString(R.string.inner_storage), Environment
                    .getExternalStorageDirectory().getAbsolutePath());
        } else {
            path = txtCurPath;
        }
        switch (v.getId()) {
            case R.id.txt_big_icon:
                this.mCurSortType = 0;
                this.localFile = null;
                sortByIcon(path);
                this.mSortPopup.dismiss();
                return;
            case R.id.txt_small_icon:
                this.mCurSortType = 1;
                this.localFile = null;
                sortByIcon(path);
                this.mSortPopup.dismiss();
                return;
            case R.id.txt_list:
                this.mCurSortType = 2;
                this.localFile = null;
                sortByList(path);
                this.mSortPopup.dismiss();
                return;
            default:
                return;
        }
    }

    private void sortByList(final String path) {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final ArrayList<LocalFile> datas = FileUtil.listFileDetail(new File(path));
                LogUtils.d(CustomActivity.TAG, "sortByList,datas size:" + datas.size());
                CustomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (datas == null || datas.size() <= 0) {
                            CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        CustomActivity.this.updateFileBrowser(datas);
                    }
                });
            }
        });
    }

    private void listSortDoubleClick(LocalFile file) {
        this.mRealStorePath = file.getFilePath();
        this.mStorePathText = file.getFilePathText();
        this.filePathTv.setText(this.mStorePathText);
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final ArrayList<LocalFile> datas = FileUtil.listFileDetail(new File(
                        CustomActivity.this.mRealStorePath));
                CustomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (datas == null || datas.size() <= 0) {
                            CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        CustomActivity.this.updateFileBrowser(datas);
                    }
                });
            }
        });
    }

    private void sortByIcon(final String path) {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final ArrayList<LocalFile> datas = FileUtil.listPictures(new File(path));
                CustomActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (datas == null || datas.size() <= 0) {
                            CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        CustomActivity.this.updateFileBrowser(datas);
                    }
                });
            }
        });
    }

    private void handleLeftContentMap(boolean isAdd, final XZDish dish) {
        if (dish != null && dish.getLayout() != null) {
            View layout = dish.getLayout();
            if (isAdd) {
                initDishShow(dish, layout);
                if (dish.getType() == DishType.USB) {
                    handleUsb(dish);
                }
                this.mLeftParent.addView(layout, getContentParams(this));
                layout.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        CustomActivity.this.handleDishSelected(CustomActivity.this, v, true);
                        CustomActivity.this.initUsb(dish.getPath());
                    }
                });
                return;
            }
            this.mLeftParent.removeView(layout);
            handleDishSelected(this,
                    ((XZDish) this.mLeftContentMap.get(XZDish.getLocalPath())).getLayout(), true);
        }
    }

    private void handleDishSelected(Context context, View view, boolean isSelected) {
        ViewGroup parent = (ViewGroup) view.getParent();
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            child.setSelected(child == view);
        }
    }

    private void handleUsb(XZDish dish) {
        TextView usbName = (TextView) dish.getLayout().findViewById(R.id.left_name_tv);
        String name = dish.getName();
        if (!TextUtils.isEmpty(name)) {
            usbName.setText(name);
        }
    }

    private void initDishShow(XZDish dish, View layout) {
        int resId = R.drawable.dish_local_selector;
        int nameId = R.string.SD_Card;
        switch (dish.getType()) {
            case LOCALE:
                resId = R.drawable.dish_local_selector;
                break;
            case USB:
                resId = R.drawable.dish_usb_selector;
                nameId = R.string.dish_usb;
                break;
        }
        ((ImageView) layout.findViewById(R.id.left_icon_tv)).setImageResource(resId);
        ((TextView) layout.findViewById(R.id.left_name_tv)).setText(nameId);
    }

    private LayoutParams getContentParams(Context context) {
        int height = Math.round(context.getResources().getDimension(R.dimen.left_content_width));
        LayoutParams params = new LayoutParams(height, height);
        params.topMargin = 10;
        return params;
    }

    protected void initListener() {
    }

    protected void initContent() {
    }

    private void initUsb() {
        if (new File("/mnt/usb").exists()) {
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    final List<CustomContentData> usbFiles = FileUtils.getUSBFiles();
                    if (usbFiles != null && usbFiles.size() > 0) {
                        CustomActivity.this.runOnUiThread(new Runnable() {
                            private LocalFileAdapter localFileAdapter;

                            public void run() {
                                CustomActivity.this.mUsbImageRecyclerView
                                        .setLayoutManager(new GridLayoutManager(
                                                CustomActivity.this, 4));
                                if (usbFiles.size() > 0) {
                                    this.localFileAdapter = new LocalFileAdapter(
                                            R.layout.adp_local_item, usbFiles, CustomActivity.this);
                                    this.localFileAdapter
                                            .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                                                public void onItemClick(View view, int position) {
                                                    Intent intent = new Intent(CustomActivity.this,
                                                            EditActivity.class);
                                                    Object filepath = ((CustomContentData) usbFiles
                                                            .get(position)).getFilepath();
                                                    if (filepath instanceof String) {
                                                        intent.putExtra("IMAGE_PATH", (String) filepath);
                                                    }
                                                    intent.putExtra("SHOW_QROCDE", false);
                                                    CustomActivity.this.startActivity(intent);
                                                    CustomActivity.this.finish();
                                                }
                                            });
                                    CustomActivity.this.mUsbImageRecyclerView
                                            .setAdapter(this.localFileAdapter);
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    private void initUsb(final String path) {
        if (new File(path).exists()) {
            ProgressDialogUtil.show((Context) this, (int) R.string.loading);
            ProgressDialogUtil.setCanelable(true);
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    final List<CustomContentData> usbFiles = FileUtils.getUSBFiles(path);
                    CustomActivity.this.runOnUiThread(new Runnable() {
                        private LocalFileAdapter localFileAdapter;

                        public void run() {
                            if (usbFiles == null || usbFiles.size() <= 0) {
                                CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                                ProgressDialogUtil.dimissProgressDialog();
                                return;
                            }
                            CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                            CustomActivity.this.mRecyclerView
                                    .setLayoutManager(new GridLayoutManager(CustomActivity.this, 4));
                            if (usbFiles.size() > 0) {
                                this.localFileAdapter = new LocalFileAdapter(
                                        R.layout.adp_local_item, usbFiles, CustomActivity.this);
                                this.localFileAdapter
                                        .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                                            public void onItemClick(View view, int position) {
                                                if (CustomActivity.this.isAddModule) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra(
                                                            "add_module_file",
                                                            (String) ((CustomContentData) localFileAdapter
                                                                    .getData().get(position))
                                                                    .getFilepath());
                                                    CustomActivity.this.setResult(1, intent);
                                                    CustomActivity.this.finish();
                                                    return;
                                                }
                                              Intent  intent = new Intent(CustomActivity.this,
                                                        EditActivity.class);
                                                Object filepath = ((CustomContentData) usbFiles
                                                        .get(position)).getFilepath();
                                                if (filepath instanceof String) {
                                                    intent.putExtra("IMAGE_PATH", (String)filepath);
                                                }
                                                intent.putExtra("SHOW_QRCODE", false);
                                                CustomActivity.this.startActivity(intent);
                                                CustomActivity.this.finish();
                                            }
                                        });
                                CustomActivity.this.mRecyclerView.setAdapter(this.localFileAdapter);
                            }
                            ProgressDialogUtil.dimissProgressDialog();
                        }
                    });
                }
            });
        }
    }

    private void addLeftContent(String path) {
        XZDish dish = XZDish.getDish(path, DishType.USB,
                getLayoutInflater().inflate(R.layout.layout_left_content, null));
        String[] split = path.split("/");
        String name = split[split.length - 1];
        dish.setName(name);
        if (!name.equals(Config.SDCARD) && !this.mLeftContentMap.containsKey(path)) {
            this.mLeftContentMap.put(path, dish);
        }
    }

    private void initSDCard() {
        ThreadManager.getThreadPoolProxy().excute(new Runnable() {
            public void run() {
                final List<CustomContentData> datas = FileUtils.getFiles(
                        Environment.getExternalStorageDirectory() + "/", false);
                CustomActivity.this.runOnUiThread(new Runnable() {
                    private LocalFileAdapter localFileAdapter;

                    public void run() {
                        CustomActivity.this.mRecyclerView.setLayoutManager(new GridLayoutManager(
                                CustomActivity.this, 4));
                        if (datas == null || datas.size() <= 0) {
                            CustomActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        CustomActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        this.localFileAdapter = new LocalFileAdapter(R.layout.adp_local_item,
                                datas, CustomActivity.this);
                        this.localFileAdapter
                                .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                                    public void onItemClick(View view, int position) {
                                        if (CustomActivity.this.isAddModule) {
                                            Intent intent = new Intent();
                                            intent.putExtra(
                                                    "add_module_file",
                                                    (String) ((CustomContentData)localFileAdapter
                                                            .getData().get(position)).getFilepath());
                                            CustomActivity.this.setResult(1, intent);
                                            CustomActivity.this.finish();
                                            return;
                                        }
                                       Intent intent = new Intent(CustomActivity.this, EditActivity.class);
                                        Object filepath = ((CustomContentData) datas.get(position))
                                                .getFilepath();
                                        if (filepath instanceof String) {
                                            intent.putExtra("IMAGE_PATH", (String) filepath);
                                        }
                                        intent.putExtra("SHOW_QROCDE", false);
                                        CustomActivity.this.startActivity(intent);
                                        CustomActivity.this.finish();
                                    }
                                });
                        CustomActivity.this.mRecyclerView.setAdapter(this.localFileAdapter);
                    }
                });
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
