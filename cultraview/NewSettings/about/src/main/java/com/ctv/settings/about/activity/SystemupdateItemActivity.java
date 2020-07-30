package com.ctv.settings.about.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import com.ctv.settings.about.Bean.SelectUpdateDialog;
import com.ctv.settings.about.Bean.SystemSelectUpdateDialog;
import com.ctv.settings.about.Bean.TipDialog;
import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.UpdateViewHolder;
import com.ctv.settings.base.BaseActivity;
import com.ctv.settings.utils.Tools;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.mstar.android.storage.MStorageManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemupdateItemActivity extends BaseActivity implements View.OnClickListener, View.OnFocusChangeListener {
    private boolean mHasNewVerison = false;
    private int height;
    private int width;
    private float x_point;
    private float y_point;
    public File UpdateFile;
    private boolean mIsUpdating = false;
    private View closeview;
    private int mCurrentProgress;
    private AlertDialog.Builder close_builder;
    private boolean updateSymbol = true;
    private SystemSelectUpdateDialog systemSelectUpdateDialog;
    public List<File> ScanFiles = new ArrayList<File>();
    protected static final String TAG = "SystemupdateItemActivity";
    public List<Map<String, String>> MountedVolumes = new ArrayList<Map<String, String>>();
    private SystemupdateItemActivity context;
    private StorageManager mStorageManager;
    private TextView system_current_version_tv, system_check_device_tv, system_check_version_tv, system_check_result_tv, system_check_more_tv;
    private ScrollView system_update_scroll;
    private TextView system_percent_progress_tv;
    private LinearLayout system_percent_progress_ll;
    private FrameLayout system_update_layout;
    // private SystemSelectUpdateDialog systemSelectUpdateDialog;
    private Button system_update_ok, system_update_cancel2, system_update_cancel;
    private ProgressBar progressBar;
    private String update_str = "";
    private int mRetryCount;

    private final BroadcastReceiver storageChangeReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + action);
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                update_str += getString(R.string.sdcard_insert) + "\n";
                system_check_more_tv.setText(update_str);
                system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                hasStorageDevice();
                scanUpdateFile();
            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
                if (systemSelectUpdateDialog != null) {
                    systemSelectUpdateDialog.dismiss();
                }
                update_str += getString(R.string.sdcard_remove) + "\n";
                system_check_more_tv.setText(update_str);
                system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                hasStorageDevice();
                scanUpdateFile();
                system_percent_progress_ll.setVisibility(View.GONE);
                system_update_cancel2.setVisibility(View.GONE);
                system_percent_progress_tv.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                system_check_result_tv.setVisibility(View.VISIBLE);
                system_update_ok.setVisibility(View.VISIBLE);
                system_update_cancel.setVisibility(View.VISIBLE);
                if (ScanFiles.size() < 1) {
                    system_check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
                    system_check_result_tv.setText(R.string.check_file_failure);
//                    system_update_ok.setEnabled(false);
//                    system_update_ok.setFocusable(false);
                    system_update_ok.setVisibility(View.GONE);
//                    system_update_ok.setTextColor(context.getResources().getColor(R.color.gray));
//                    system_update_ok.setBackgroundResource(R.drawable.shape_bg_accessibilit);
//                    system_update_cancel.requestFocus();
                } else {
                    system_check_result_tv.setBackgroundResource(R.drawable.check_file_success_bg);
                    system_check_result_tv.setText(R.string.check_file_success);
//                    system_update_ok.setEnabled(true);
//
//                    system_update_ok.setFocusable(true);
                    system_update_ok.setVisibility(View.VISIBLE);
//                    system_update_ok.setTextColor(context.getResources().getColor(
//                            R.color.white_seven));
//                    system_update_ok.requestFocus();
                }
            }

        }
    };
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    checkStorage();
                    break;
                case 1:
                    system_check_version_tv.setText(getString(R.string.check_new_version) + " ......");
                    system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    scanUpdateFile();
                    break;
                case 2:
                    update_str += getString(R.string.check_failure) + "\n";
                    system_check_more_tv.setText(update_str);
                    system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    system_update_cancel2.requestFocus();
                    break;
                case 3:
                    update_str += getString(R.string.updated);
                    system_check_more_tv.setText(update_str);
                    system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                case 4:
                    system_percent_progress_tv.setText(mCurrentProgress + "%");
                    progressBar.setProgress(mCurrentProgress);
                    if (mCurrentProgress == 100) {
                        update_str += getString(R.string.check_success);
                        system_check_more_tv.setText(update_str);
                        system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                        mIsUpdating = false;
                    }
                    break;

                case 5:
                    update_str += getString(R.string.check_package) + "\n";
                    system_check_more_tv.setText(update_str);
                    system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    system_percent_progress_tv.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    system_percent_progress_ll.setVisibility(View.VISIBLE);
                    system_update_cancel2.setVisibility(View.VISIBLE);
                    system_update_ok.setVisibility(View.GONE);
                    system_update_cancel.setVisibility(View.GONE);
                    system_check_result_tv.setVisibility(View.GONE);
                    system_update_cancel2.requestFocus();
                    mIsUpdating = true;
                    new UpdateSystemThread().start();
                    break;
                default:
                    break;
            }

        }

    };
    private View back;
    private TextView text;

    private class UpdateSystemThread extends Thread {

        @Override
        public void run() {
            super.run();
            updateSystem();
        }
    }

    private void updateSystem() {
        mHasNewVerison = false;
        if (verifyPackage()) {
            myHandler.sendEmptyMessage(3);
        } else {
            myHandler.sendEmptyMessage(2);
        }
    }

    @SuppressLint("LongLogTag")
    private boolean verifyPackage() {
        // recovery listener
        RecoverySystem.ProgressListener progressListener = new RecoverySystem.ProgressListener() {
            @Override
            public void onProgress(int progress) {
                mCurrentProgress = progress;
                myHandler.sendEmptyMessage(4);
            }
        };
        // call system interface to update system
        try {
            RecoverySystem.verifyPackage(UpdateFile, progressListener, null);
            if (updateSymbol) {
                resetSystem(UpdateFile);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "verifyPackage exception, " + e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = SystemupdateItemActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_systemupdate_item);
        initDialogView();
        system_update_layout = (FrameLayout) findViewById(R.id.system_update_layout);
        system_update_scroll = (ScrollView) findViewById(R.id.system_update_scroll);
        back = findViewById(R.id.back_btn);
        back.setOnClickListener(this);
        text = (TextView) findViewById(R.id.back_title);
        text.setText(getResources().getString(R.string.system_update));
        system_current_version_tv = (TextView) findViewById(R.id.system_current_version_tv);
        system_check_device_tv = (TextView) findViewById(R.id.system_check_device_tv);
        system_check_version_tv = (TextView) findViewById(R.id.system_check_version_tv);
        system_check_more_tv = (TextView) findViewById(R.id.system_check_more_tv);

        system_check_result_tv = (TextView) findViewById(R.id.system_check_result_tv);
        system_percent_progress_tv = (TextView) findViewById(R.id.system_percent_progress_tv);
        system_percent_progress_ll = (LinearLayout) findViewById(R.id.system_percent_progress_ll);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        system_update_ok = (Button) findViewById(R.id.system_update_ok);
        system_update_cancel = (Button) findViewById(R.id.system_update_cancel);
        system_update_cancel2 = (Button) findViewById(R.id.system_update_cancel2);


        system_update_ok.setOnFocusChangeListener(this);
        system_update_cancel.setOnFocusChangeListener(this);
        system_update_cancel2.setOnFocusChangeListener(this);

        system_update_cancel2.setOnClickListener(this);
        system_update_ok.setOnClickListener(this);
        system_update_cancel.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        registerReceiver(storageChangeReceiver, intentFilter);
        mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
        system_current_version_tv.setText(getString(R.string.current_version) + " "
                + Tools.getSystemVersion());
        system_check_device_tv.setText(getString(R.string.check_new_device) + " ......");

        // default state
        system_check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
        system_check_result_tv.setText(R.string.check_file_failure);
        system_update_cancel.requestFocus();

        Message msg = myHandler.obtainMessage();
        msg.what = 0;
        myHandler.sendMessage(msg);


        initView();

    }

    static public String getPath(StorageVolume volume)
            throws Exception {
        Method createBondMethod = volume.getClass().getMethod("getPath");
        String returnValue = (String) createBondMethod.invoke(volume);
        return returnValue;
    }

    //反射getVolumeList()方法
    private StorageVolume[] getVolumeList() {
        StorageVolume[] mStorageVolume = null;
        try {
            Class cls = Class.forName("android.os.storage.StorageManager");
            Method getVolumeList = cls.getDeclaredMethod("getVolumeList");
            mStorageVolume = (StorageVolume[]) getVolumeList.invoke(mStorageManager);
            Log.i("hhc", "getVolumeList");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("hhc", e.getMessage());
        }
        return mStorageVolume;
    }

    //查看是否有存储设备
    private boolean hasStorageDevice() {
        MountedVolumes.clear();
        MStorageManager sm = MStorageManager.getInstance(context);
        StorageVolume[] volumeList = getVolumeList();
        for (StorageVolume volume : volumeList) {

            String path = null;//volume.getPath();
            try {
                path = getPath(volume);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Environment.MEDIA_MOUNTED.equals(sm.getVolumeState(path))) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("volume_path", path);
                String label = sm.getVolumeLabel(path);
                if (TextUtils.isEmpty(label)) {
                    if (Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) {
                        map.put("volume_lable", getString(R.string.sdcard_lable));
                    } else {
                        map.put("volume_lable", getString(R.string.mobile_stoarge_device));
                    }
                } else {
                    map.put("volume_lable", label);
                }
                MountedVolumes.add(map);
            }
        }
        // do not have any storage
        if (MountedVolumes.size() <= 0) {
            return false;
        } else {
            return true;
        }
    }

    private void initDialogView() {
        close_builder = new AlertDialog.Builder(this);
        closeview = LayoutInflater.from(this).inflate(R.layout.close_layout, null);
        TextView content = (TextView) closeview.findViewById(R.id.dialog_content);
        content.setText(getResources().getString(R.string.update_system_tip));
        close_builder.setView(closeview);
    }

    private void scanUpdateFile() {
        ScanFiles.clear();
        for (Map<String, String> map : MountedVolumes) {
            UpdateFile = new File(map.get("volume_path"), "MstarUpgrade.bin");
            if (UpdateFile.exists()) {
                ScanFiles.add(UpdateFile);
            }
        }
        if (ScanFiles.size() > 0) {
            mHasNewVerison = true;
            update_str += getString(R.string.check_updatezip) + "\n";
            system_check_more_tv.setText(update_str);
            system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            system_check_result_tv.setBackgroundResource(R.drawable.check_file_success_bg);
            system_check_result_tv.setText(R.string.check_file_success);
//            system_update_ok.setEnabled(true);
//            system_update_ok.setFocusable(true);
            system_update_ok.setVisibility(View.VISIBLE);
//            system_update_ok.setBackgroundResource(R.drawable.shape_btn_bg);
//            system_update_ok.setTextColor(context.getResources().getColor(R.color.white_seven));
            system_update_ok.requestFocus();
        } else {
            mHasNewVerison = false;
            update_str += getString(R.string.no_update_file) + "\n";
            system_check_more_tv.setText(update_str);
            system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            system_check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
            system_check_result_tv.setText(R.string.check_file_failure);
//            system_update_ok.setEnabled(false);
//            system_update_ok.setFocusable(false);
            system_update_ok.setVisibility(View.GONE);
//            system_update_ok.setTextColor(context.getResources().getColor(R.color.gray));
//            system_update_ok.setBackgroundResource(R.drawable.shape_btn_bg);
//            system_update_cancel.requestFocus();
        }
    }

    private void checkStorage() {
        mRetryCount++;
        boolean hasStorage = hasStorageDevice();
        if (hasStorage) {
            myHandler.sendEmptyMessage(1);
        } else {
            if (mRetryCount < 10) {
                myHandler.sendEmptyMessageDelayed(0, 200);
            } else {
                update_str += getString(R.string.check_new_device) + " ......\n";
                system_check_more_tv.setText(update_str);
                system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                system_check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
                system_check_result_tv.setText(R.string.check_file_failure);
//                system_update_ok.setEnabled(false);
//                system_update_ok.setFocusable(false);
//                system_update_ok.setTextColor(context.getResources().getColor(R.color.gray));
//                system_update_ok.setBackgroundResource(R.drawable.shape_btn_bg);
//                system_update_cancel.requestFocus();
            }
        }
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if (i == R.id.system_update_ok) {


            if (CheckUsbIsExist() && !isUpgradeStart) {
                isUpgradeStart = true;
                UpgradeMain();
            } else {
                isUpgradeStart = false;
                //Toast.makeText(ctvContext,ctvContext.getResources().getString(R.string.please_plug_usb), Toast.LENGTH_SHORT).show();
            }



//            if (ScanFiles.size() == 1) {
//                update_str += getString(R.string.check_package) + "\n";
//                system_check_more_tv.setText(update_str);
//                system_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
//                UpdateFile = ScanFiles.get(0);
//                system_percent_progress_tv.setVisibility(View.VISIBLE);
//                progressBar.setVisibility(View.VISIBLE);
//                progressBar.setProgress(100);
//                system_percent_progress_ll.setVisibility(View.VISIBLE);
//                system_update_cancel2.setVisibility(View.VISIBLE);
//                system_update_ok.setVisibility(View.GONE);
//                system_update_cancel.setVisibility(View.GONE);
//                system_check_result_tv.setVisibility(View.GONE);
//                system_update_cancel2.requestFocus();
//                mIsUpdating = true;
//                new UpdateSystemThread().start();
//            } else if (ScanFiles.size() > 1) {
//            systemSelectUpdateDialog= new SystemSelectUpdateDialog(SystemupdateItemActivity.this);
//                systemSelectUpdateDialog.show();
//            }
        } else if (i == R.id.system_update_cancel2 || i == R.id.system_update_cancel) {
            updateSymbol = false;
            finish();
        } else if (i == R.id.back_btn) {
            finish();
        }


    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    private void resetSystem(File mUpdateFile) {
        try {
            int[] GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                    "GetOPSDEVICESTATUS");
            int[] GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                    "GetOPSPOWERSTATUS");
            Log.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
            Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
            if (GetOPSDEVICESTATUS[0] == 0 || GetOPSPOWERSTATUS[0] == 0) {// 0,表示有OPS设备接入；1，表示没有OPS设备接入。
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Dialog close_dialog = close_builder.create();
                        close_dialog.getWindow().setBackgroundDrawableResource(
                                android.R.color.transparent);
                        close_dialog.getWindow().setType(
                                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        close_dialog.setCanceledOnTouchOutside(false);
                        close_dialog.show();
                        WindowManager.LayoutParams lp = close_dialog.getWindow().getAttributes();
                        lp.width = 640;
                        close_dialog.getWindow().setAttributes(lp);
                    }
                });
                Log.d("chen_powerdown", "start");
                Log.d("chen_powerdown", "close ops");
                CtvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                Thread.sleep(200);
                CtvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                Log.d("chen_powerdown", "ops:state");
                Thread.sleep(2000);
                GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSDEVICESTATUS");
                GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                        "GetOPSPOWERSTATUS");
                Log.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
                Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
                int count = 0;
                while (GetOPSPOWERSTATUS[0] == 0) {
                    Log.d("chen_powerdown", "checkops state start ");
                    Log.d("chen_powerdown", "checkops time count : " + count);
                    Thread.sleep(1000);
                    count++;
                    if (count == 30) {
                        Log.d("chen_powerdown", "ops force now :count: " + count);
                        CtvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWER");
                        Thread.sleep(5 * 1000);
                        CtvCommonManager.getInstance().setTvosCommonCommand("SetOPSPOWERON");
                        break;
                    }
                    Log.d("chen_powerdown", "change ops state start");
                    GetOPSDEVICESTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSDEVICESTATUS");
                    GetOPSPOWERSTATUS = CtvCommonManager.getInstance().setTvosCommonCommand(
                            "GetOPSPOWERSTATUS");
                    Log.d("chen_powerdown", "change ops state resutl:");
                    Log.d("chen_powerdown", "GetOPSDEVICESTATUS:" + GetOPSDEVICESTATUS[0]);
                    Log.d("chen_powerdown", "GetOPSPOWERSTATUS:" + GetOPSPOWERSTATUS[0]);
                }
                ;
                RecoverySystem.installPackage(this, mUpdateFile);
            } else {
                RecoverySystem.installPackage(this, mUpdateFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (touchOutSide(event)) {
                updateSymbol = false;
                finish();
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean touchOutSide(MotionEvent event) {
        float down_point_x = event.getRawX();
        float down_point_y = event.getRawY();
        if (height == 0) {
            initCountViewSize();
        }
        if (down_point_x < x_point || down_point_y < y_point || (down_point_y > (height + y_point))
                || down_point_x > x_point + width) {
            return true;
        }
        return false;
    }

    private void initCountViewSize() {
        height = system_update_layout.getMeasuredHeight();
        width = system_update_layout.getMeasuredWidth();
        x_point = system_update_layout.getX();
        y_point = system_update_layout.getY();
    }

    private TipDialog upgrade_dialog;
    private static boolean isUpgradeStart = false;

    private void initView() {
        if (upgrade_dialog == null) {
            upgrade_dialog = new TipDialog(context, context.getResources().getString(
                    R.string.open_usb_upgrade_tip)) {
                @Override
                public void onDialogClick(boolean isOK) {
                    Log.d("qkmin--->", "isOK" + isOK + "!isUpgradeStart" + !isUpgradeStart + "CheckUsbIsExist():" + CheckUsbIsExist());
                    if (isOK) {
                        if (CheckUsbIsExist() && !isUpgradeStart) {
                            isUpgradeStart = true;
                            UpgradeMain();
                        } else {
                            isUpgradeStart = false;
                            //Toast.makeText(ctvContext,ctvContext.getResources().getString(R.string.please_plug_usb), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        isUpgradeStart = false;
                    }
                }
            };
        }
    }

    public boolean UpgradeMain() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final int upgrate_status = UpgradeMainFun();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
//                new Handler().post(new Runnable() {
//                    @Override
//                    public void run() {
                // TODO Auto-generated method stub
                if (upgrate_status == UpdateViewHolder.EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal()) {

                    CtvCommonManager.getInstance().rebootSystem("reboot");
                    isUpgradeStart = false;
                } else if (upgrate_status == UpdateViewHolder.EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal()) {
                    isUpgradeStart = false;
//                            Toast.makeText(ctvContext, ctvContext.getResources().getString(R.string.upgrade_file_not_found), Toast.LENGTH_SHORT)
//                                    .show();
                } else {
                    isUpgradeStart = false;
//                            Toast.makeText(ctvContext, ctvContext.getResources().getString(R.string.upgrade_file_error), Toast.LENGTH_SHORT).show();
                }
//                    }
//                }
//                );
            }
        }).start();
        return true;
    }


    public int UpgradeMainFun() {
        int ret = 0;
        // TODO UpgradeMain function
        String mainpath;
        mainpath = FindFileOnUSB("MstarUpgrade.bin");
        // should not change this file name

        if ("" == mainpath) {
            ret = UpdateViewHolder.EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        } else {
            try {
                CtvTvManager.getInstance().setEnvironment("factory_mode", "1");

                if (CtvTvManager.getInstance().setEnvironment("upgrade_mode", "usb")) {
                    CtvTvManager.getInstance().setEnvironment("CtvUpgrade_complete", "0");
                    ret = UpdateViewHolder.EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                } else {
                    Log.d("UpgradeMainFun:", "setEnvironment Failed!");
                    ret = UpdateViewHolder.EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
                }
            } catch (CtvCommonException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return ret;
    }


    private static String FindFileOnUSB(String filename) {
        // TODO Find File On USB function
        String filepath = "";
        File usbroot = new File("/mnt/usb/");
        File targetfile;
        if (usbroot != null && usbroot.exists()) {
            File[] usbitems = usbroot.listFiles();

            for (int sdx = 0; sdx < usbitems.length; sdx++) {
                if (usbitems[sdx].isDirectory()) {
                    targetfile = new File(usbitems[sdx].getPath() + "/" + filename);
                    if (targetfile != null && targetfile.exists()) {
                        filepath = usbitems[sdx].getPath() + "/" + filename;
                        break;
                    }
                }
            }
        }
        return filepath;
    }

    private boolean CheckUsbIsExist() {
        boolean ret = false;
        // TODO check USB exist function
        ret = CheckUsbIsExistS();
        return ret;
    }

    @SuppressLint("NewApi")
    public boolean CheckUsbIsExistS() {
        boolean ret = false;
        File usbfile = new File("/mnt/usb/");
        if (getUsbCout(usbfile) > 0) {
            ret = true;
        }
        return ret;
    }


    private int getUsbCout(File file) {
        int cout = 0;
        File[] flist = file.listFiles();

        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory() && flist[i].canRead()) {
                // android 6.0 external directory is not contain "sd"
                cout++;
                /*
                 * String sdname = flist[i].getName(); Pattern mPatern =
                 * Pattern.compile("sd+[a-z]{1}+[1-9]{1}"); Matcher mMatcher =
                 * mPatern.matcher(sdname); boolean issd = mMatcher.matches();
                 * if (issd) { cout++; }
                 */
            }
        }
        return cout;
    }
}
