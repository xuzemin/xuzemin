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
import com.ctv.settings.about.R;

import com.ctv.settings.utils.Tools;
import com.cultraview.tv.CtvCommonManager;
import com.mstar.android.storage.MStorageManager;
import com.mstar.android.tvapi.common.vo.Constants;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalUpdateActivity extends Activity implements View.OnFocusChangeListener , View.OnClickListener {

    private int height;

    private int width;

    private float x_point;

    private float y_point;
    protected static final String TAG = "LocalUpdateActivity";
    private FrameLayout local_update_layout;
    private AlertDialog.Builder close_builder;
    private boolean updateSymbol = true;
    private LocalUpdateActivity context;
    private boolean mIsUpdating = false;
    private int mCurrentProgress;
    private ProgressBar mProgressBar;
    private SelectUpdateDialog selectUpdateDialog;
   private boolean mHasNewVerison = false;
    private LinearLayout percent_progress_ll;
    public List<File> mScanFiles = new ArrayList<File>();
    private TextView current_version_tv;
    private StorageManager mStorageManager;
    private TextView check_device_tv;
    public List<Map<String, String>> mMountedVolumes = new ArrayList<Map<String, String>>();
    private TextView check_version_tv;
    public File mUpdateFile;
    private TextView check_more_tv;
    private TextView check_result_tv;
    private TextView percent_progress_tv;
    private Button local_update_ok;
    private Button local_update_cancel;
    private Button local_update_cancel2;
    private ScrollView local_update_scroll;
    private int mRetryCount;
    private View closeview;
    private String update_str = "";
    private final BroadcastReceiver storageChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + action);
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                update_str += getString(R.string.sdcard_insert) + "\n";
                check_more_tv.setText(update_str);
                local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                hasStorageDevice();
                scanUpdateFile();
            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
                if (selectUpdateDialog != null) {
                    selectUpdateDialog.dismiss();
                }
                update_str += getString(R.string.sdcard_remove) + "\n";
                check_more_tv.setText(update_str);
                local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                hasStorageDevice();
                scanUpdateFile();
                percent_progress_ll.setVisibility(View.GONE);
                local_update_cancel2.setVisibility(View.GONE);
                percent_progress_tv.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                check_result_tv.setVisibility(View.VISIBLE);
                local_update_ok.setVisibility(View.VISIBLE);
                local_update_cancel.setVisibility(View.VISIBLE);
                if (mScanFiles.size() < 1) {
                    check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
                    check_result_tv.setText(R.string.check_file_failure);
                    local_update_ok.setEnabled(false);
                    local_update_ok.setFocusable(false);
                    local_update_ok.setTextColor(context.getResources().getColor(R.color.gray));
                    local_update_ok.setBackgroundResource(R.drawable.shape_bg_accessibilit);
                    local_update_cancel.requestFocus();
                } else {
                    check_result_tv.setBackgroundResource(R.drawable.check_file_success_bg);
                    check_result_tv.setText(R.string.check_file_success);
                    local_update_ok.setEnabled(true);
                    local_update_ok.setFocusable(true);
                    local_update_ok.setBackgroundResource(R.drawable.shape_btn_bg_unselect);
                    local_update_ok.setTextColor(context.getResources().getColor(
                            R.color.white_seven));
                    local_update_ok.requestFocus();
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
                    check_version_tv.setText(getString(R.string.check_new_version) + " ......");
                    local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    scanUpdateFile();
                    break;
                case 2:
                    update_str += getString(R.string.check_failure) + "\n";
                    check_more_tv.setText(update_str);
                    local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    local_update_cancel2.requestFocus();
                    break;
                case 3:
                    update_str += getString(R.string.updated);
                    check_more_tv.setText(update_str);
                    local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    break;
                case 4:
                    percent_progress_tv.setText(mCurrentProgress + "%");
                    mProgressBar.setProgress(mCurrentProgress);
                    if (mCurrentProgress == 100) {
                        update_str += getString(R.string.check_success);
                        check_more_tv.setText(update_str);
                        local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                        mIsUpdating = false;
                    }
                    break;
                case 5:
                    update_str += getString(R.string.check_package) + "\n";
                    check_more_tv.setText(update_str);
                    local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                    percent_progress_tv.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    percent_progress_ll.setVisibility(View.VISIBLE);
                    local_update_cancel2.setVisibility(View.VISIBLE);
                    local_update_ok.setVisibility(View.GONE);
                    local_update_cancel.setVisibility(View.GONE);
                    check_result_tv.setVisibility(View.GONE);
                    local_update_cancel2.requestFocus();
                    mIsUpdating = true;
                    new UpdateSystemThread().start();
                    break;
                default:
                    break;
            }
        }
    };
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
    private void checkStorage() {
        mRetryCount++;
        boolean hasStorage = hasStorageDevice();
        if (hasStorage) {
            myHandler.sendEmptyMessage(1);
        } else {
            if (mRetryCount <10) {
                myHandler.sendEmptyMessageDelayed(0, 200);
            } else {
                update_str += getString(R.string.check_new_device) + " ......\n";
                check_more_tv.setText(update_str);
                local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
                check_result_tv.setText(R.string.check_file_failure);
                local_update_ok.setEnabled(false);
                local_update_ok.setFocusable(false);
                local_update_ok.setTextColor(context.getResources().getColor(R.color.gray));
                local_update_ok.setBackgroundResource(R.drawable.shape_bg_accessibilit);
                local_update_cancel.requestFocus();
            }
        }
    }

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
            RecoverySystem.verifyPackage(mUpdateFile, progressListener, null);
            if (updateSymbol) {
                resetSystem(mUpdateFile);
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "verifyPackage exception, " + e.getMessage());
            e.printStackTrace();

            return false;
        }
    }

    static public String getPath(StorageVolume volume)
            throws Exception
    {
        Method createBondMethod = volume.getClass().getMethod("getPath");
        String returnValue = (String) createBondMethod.invoke(volume);
        return returnValue;
    }
    //反射getVolumeList()方法
    private StorageVolume[] getVolumeList(){
        StorageVolume[] mStorageVolume=null;
        try {
            Class cls = Class.forName("android.os.storage.StorageManager");
            Method getVolumeList = cls.getDeclaredMethod("getVolumeList");
            mStorageVolume=( StorageVolume[]) getVolumeList.invoke(mStorageManager);
            Log.i("hhc","getVolumeList");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("hhc",e.getMessage());
        }
        return mStorageVolume;
    }

    private boolean hasStorageDevice() {
        mMountedVolumes.clear();
        MStorageManager sm = MStorageManager.getInstance(context);
        for (StorageVolume volume : getVolumeList()) {

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
                mMountedVolumes.add(map);
            }
        }
        // do not have any storage
        if (mMountedVolumes.size() <= 0) {
            return false;
        } else {
            return true;
        }
    }
    @SuppressLint("ServiceCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = LocalUpdateActivity.this;
      //  AppCloseApplication.getInstance().addActivity(LocalUpdateActivity.this);
//          Tools.setThemeByTime(context);

        super.onCreate(savedInstanceState);
            setContentView(R.layout.local_update);
        initDialogView();
        percent_progress_ll = (LinearLayout) findViewById(R.id.percent_progress_ll);
        current_version_tv = (TextView) findViewById(R.id.current_version_tv);
        check_device_tv = (TextView) findViewById(R.id.check_device_tv);
        check_version_tv = (TextView) findViewById(R.id.check_version_tv);
        check_more_tv = (TextView) findViewById(R.id.check_more_tv);
        check_result_tv = (TextView) findViewById(R.id.check_result_tv);
        percent_progress_tv = (TextView) findViewById(R.id.percent_progress_tv);


        local_update_ok = (Button) findViewById(R.id.local_update_ok);
        local_update_cancel2 = (Button) findViewById(R.id.local_update_cancel2);
        local_update_cancel = (Button) findViewById(R.id.local_update_cancel);
        local_update_scroll = (ScrollView) findViewById(R.id.local_update_scroll);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        local_update_layout = (FrameLayout) findViewById(R.id.local_update_layout);


        local_update_ok.setOnFocusChangeListener(this);
        local_update_cancel2.setOnFocusChangeListener(this);
        local_update_cancel.setOnFocusChangeListener(this);

        local_update_ok.setOnClickListener(this);
        local_update_cancel2.setOnClickListener(this);
        local_update_cancel.setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        registerReceiver(storageChangeReceiver, intentFilter);
        mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
        current_version_tv.setText(getString(R.string.current_version) + " "
                + Tools.getSystemVersion());
        check_device_tv.setText(getString(R.string.check_new_device) + " ......");

        // default state
        check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
        check_result_tv.setText(R.string.check_file_failure);
        local_update_cancel.requestFocus();

        Message msg = myHandler.obtainMessage();
        msg.what = 0;
        myHandler.sendMessage(msg);







    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            initCountViewSize();
        }
        super.onWindowFocusChanged(hasFocus);
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
        height = local_update_layout.getMeasuredHeight();
        width = local_update_layout.getMeasuredWidth();
        x_point = local_update_layout.getX();
        y_point = local_update_layout.getY();
    }

    private void initDialogView() {
        close_builder = new AlertDialog.Builder(this);
        closeview = LayoutInflater.from(this).inflate(R.layout.close_layout, null);
        TextView content = (TextView) closeview.findViewById(R.id.dialog_content);
        content.setText(getResources().getString(R.string.update_system_tip));
        close_builder.setView(closeview);
    }


    private void scanUpdateFile() {
        mScanFiles.clear();
        for (Map<String, String> map : mMountedVolumes) {
            mUpdateFile = new File(map.get("volume_path"), "update_signed.zip");
            if (mUpdateFile.exists()) {
                mScanFiles.add(mUpdateFile);
            }
        }
        if (mScanFiles.size() > 0) {
            mHasNewVerison = true;
            update_str += getString(R.string.check_updatezip) + "\n";
            check_more_tv.setText(update_str);
            local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            check_result_tv.setBackgroundResource(R.drawable.check_file_success_bg);
            check_result_tv.setText(R.string.check_file_success);
            local_update_ok.setEnabled(true);
            local_update_ok.setFocusable(true);
            local_update_ok.setBackgroundResource(R.drawable.shape_btn_bg_unselect);
            local_update_ok.setTextColor(context.getResources().getColor(R.color.white_seven));
            local_update_ok.requestFocus();
        } else {
            mHasNewVerison = false;
            update_str += getString(R.string.no_update_file) + "\n";
            check_more_tv.setText(update_str);
            local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
            check_result_tv.setBackgroundResource(R.drawable.check_file_failure_bg);
            check_result_tv.setText(R.string.check_file_failure);
            local_update_ok.setEnabled(false);
            local_update_ok.setFocusable(false);
            local_update_ok.setTextColor(context.getResources().getColor(R.color.gray));
            local_update_ok.setBackgroundResource(R.drawable.shape_bg_accessibilit);
            local_update_cancel.requestFocus();
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.local_update_ok) {
            if (mScanFiles.size() == 1) {
                update_str += getString(R.string.check_package) + "\n";
                check_more_tv.setText(update_str);
                local_update_scroll.fullScroll(ScrollView.FOCUS_DOWN);
                mUpdateFile = mScanFiles.get(0);
                percent_progress_tv.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(100);
                percent_progress_ll.setVisibility(View.VISIBLE);
                local_update_cancel2.setVisibility(View.VISIBLE);
                local_update_ok.setVisibility(View.GONE);
                local_update_cancel.setVisibility(View.GONE);
                check_result_tv.setVisibility(View.GONE);
                local_update_cancel2.requestFocus();
                mIsUpdating = true;
                new UpdateSystemThread().start();
            } else if (mScanFiles.size() > 1) {
                selectUpdateDialog = new SelectUpdateDialog(LocalUpdateActivity.this);
                selectUpdateDialog.show();
            }
        } else if (i == R.id.local_update_cancel2 || i == R.id.local_update_cancel) {
            updateSymbol = false;
            finish();
        }


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
}
