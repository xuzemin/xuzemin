
package com.ctv.welcome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ctv.welcome.R;
import com.chad.library.adapter.base.BaseQuickAdapter.OnRecyclerViewItemClickListener;
import com.ctv.welcome.adapter.LocalFileAdapter;
import com.ctv.welcome.constant.Config;
import com.ctv.welcome.constant.DishType;
import com.ctv.welcome.task.ThreadManager;
import com.ctv.welcome.util.FileUtils;
import com.ctv.welcome.util.ProgressDialogUtil;
import com.ctv.welcome.vo.CustomContentData;
import com.ctv.welcome.vo.XZDish;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SelectFileActivity extends BaseActivity implements OnClickListener {
    private boolean isAddModule = false;

    private Map<String, XZDish> mLeftContentMap;

    private LinearLayout mLeftParent;

    private RecyclerView mRecyclerView;

    private RecyclerView mUsbImageRecyclerView;

    private UsbReceiver mUsbReceiver;

    class UsbReceiver extends BroadcastReceiver {
        private String path;

        UsbReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Uri data = intent.getData();
            if (data != null) {
                this.path = data.getPath();
                if (action.equals("android.intent.action.MEDIA_MOUNTED")
                        || intent.getAction().equals("android.intent.action.MEDIA_CHECKING")) {
                    SelectFileActivity.this.addLeftContent(this.path);
                    return;
                }
                String[] split = this.path.split("/");
                if (!split[split.length - 1].equals(Config.SDCARD) && !TextUtils.isEmpty(this.path)) {
                    SelectFileActivity.this.mLeftContentMap.remove(this.path);
                    SelectFileActivity.this.initSDCard();
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_select_file);
        this.isAddModule = getIntent().getBooleanExtra("add_module", false);
        initReceiver();
        initSDCard();
        initLocal();
        init();
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

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.mUsbReceiver);
    }

    protected void initVariable() {
        this.mRecyclerView = (RecyclerView) bindView(R.id.act_sf_image_rv);
        this.mUsbImageRecyclerView = (RecyclerView) bindView(R.id.act_sf_usb_image_rv);
        this.mLeftParent = (LinearLayout) bindView(R.id.act_sf_left_linear);
        this.mLeftContentMap = new LinkedHashMap<String, XZDish>() {
            public XZDish put(String key, XZDish value) {
                SelectFileActivity.this.handleLeftContentMap(true, value);
                return (XZDish) super.put(key, value);
            }

            public XZDish remove(Object key) {
                XZDish value = (XZDish) super.remove(key);
                SelectFileActivity.this.handleLeftContentMap(false, value);
                return value;
            }
        };
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
                        SelectFileActivity.this
                                .handleDishSelected(SelectFileActivity.this, v, true);
                        SelectFileActivity.this.initUsb(dish.getPath());
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
        findViewById(R.id.act_sf_back_tv).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SelectFileActivity.this.finish();
            }
        });
    }

    protected void initContent() {
    }

    private void initUsb() {
        if (new File("/mnt/usb").exists()) {
            ThreadManager.getThreadPoolProxy().excute(new Runnable() {
                public void run() {
                    final List<CustomContentData> usbFiles = FileUtils.getUSBFiles();
                    if (usbFiles != null && usbFiles.size() > 0) {
                        SelectFileActivity.this.runOnUiThread(new Runnable() {
                            private LocalFileAdapter localFileAdapter;

                            public void run() {
                                SelectFileActivity.this.mUsbImageRecyclerView
                                        .setLayoutManager(new GridLayoutManager(
                                                SelectFileActivity.this, 4));
                                if (usbFiles.size() > 0) {
                                    this.localFileAdapter = new LocalFileAdapter(
                                            R.layout.adp_local_item, usbFiles,
                                            SelectFileActivity.this);
                                    this.localFileAdapter
                                            .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                                                public void onItemClick(View view, int position) {
                                                    Intent intent = new Intent(
                                                            SelectFileActivity.this,
                                                            EditActivity.class);
                                                    Object filepath = ((CustomContentData) usbFiles
                                                            .get(position)).getFilepath();
                                                    if (filepath instanceof String) {
                                                        intent.putExtra("IMAGE_PATH",(String) filepath);
                                                    }
                                                    intent.putExtra("SHOW_QROCDE", false);
                                                    SelectFileActivity.this.startActivity(intent);
                                                    SelectFileActivity.this.finish();
                                                }
                                            });
                                    SelectFileActivity.this.mUsbImageRecyclerView
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
                    SelectFileActivity.this.runOnUiThread(new Runnable() {
                        private LocalFileAdapter localFileAdapter;

                        public void run() {
                            if (usbFiles == null || usbFiles.size() <= 0) {
                                SelectFileActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                                ProgressDialogUtil.dimissProgressDialog();
                                return;
                            }
                            SelectFileActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                            SelectFileActivity.this.mRecyclerView
                                    .setLayoutManager(new GridLayoutManager(
                                            SelectFileActivity.this, 4));
                            if (usbFiles.size() > 0) {
                                this.localFileAdapter = new LocalFileAdapter(
                                        R.layout.adp_local_item, usbFiles, SelectFileActivity.this);
                                this.localFileAdapter
                                        .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                                            public void onItemClick(View view, int position) {
                                                if (SelectFileActivity.this.isAddModule) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra(
                                                            "add_module_file",
                                                            (String) ((CustomContentData) localFileAdapter
                                                                    .getData().get(position))
                                                                    .getFilepath());
                                                    SelectFileActivity.this.setResult(1, intent);
                                                    SelectFileActivity.this.finish();
                                                    return;
                                                }
                                                Intent intent = new Intent(SelectFileActivity.this,
                                                        EditActivity.class);
                                                Object filepath = ((CustomContentData) usbFiles
                                                        .get(position)).getFilepath();
                                                if (filepath instanceof String) {
                                                    intent.putExtra("IMAGE_PATH", (String)filepath);
                                                }
                                                intent.putExtra("SHOW_QRCODE", false);
                                                SelectFileActivity.this.startActivity(intent);
                                                SelectFileActivity.this.finish();
                                            }
                                        });
                                SelectFileActivity.this.mRecyclerView
                                        .setAdapter(this.localFileAdapter);
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
                SelectFileActivity.this.runOnUiThread(new Runnable() {
                    private LocalFileAdapter localFileAdapter;

                    public void run() {
                        SelectFileActivity.this.mRecyclerView
                                .setLayoutManager(new GridLayoutManager(SelectFileActivity.this, 4));
                        if (datas == null || datas.size() <= 0) {
                            SelectFileActivity.this.mRecyclerView.setVisibility(View.INVISIBLE);
                            return;
                        }
                        SelectFileActivity.this.mRecyclerView.setVisibility(View.VISIBLE);
                        this.localFileAdapter = new LocalFileAdapter(R.layout.adp_local_item,
                                datas, SelectFileActivity.this);
                        this.localFileAdapter
                                .setOnRecyclerViewItemClickListener(new OnRecyclerViewItemClickListener() {
                                    public void onItemClick(View view, int position) {
                                        if (SelectFileActivity.this.isAddModule) {
                                            Intent intent = new Intent();
                                            intent.putExtra(
                                                    "add_module_file",
                                                    (String) ((CustomContentData) localFileAdapter
                                                            .getData().get(position)).getFilepath());
                                            SelectFileActivity.this.setResult(1, intent);
                                            SelectFileActivity.this.finish();
                                            return;
                                        }
                                        Intent intent = new Intent(SelectFileActivity.this,
                                                EditActivity.class);
                                        Object filepath = ((CustomContentData) datas.get(position))
                                                .getFilepath();
                                        if (filepath instanceof String) {
                                            intent.putExtra("IMAGE_PATH", (String)filepath);
                                        }
                                        intent.putExtra("SHOW_QROCDE", false);
                                        SelectFileActivity.this.startActivity(intent);
                                        SelectFileActivity.this.finish();
                                    }
                                });
                        SelectFileActivity.this.mRecyclerView.setAdapter(this.localFileAdapter);
                    }
                });
            }
        });
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
