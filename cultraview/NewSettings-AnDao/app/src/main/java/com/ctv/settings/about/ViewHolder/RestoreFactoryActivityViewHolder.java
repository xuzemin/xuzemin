package com.ctv.settings.about.ViewHolder;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.about.activity.RestoreFactoryActivity;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.hht.android.sdk.ops.HHTOpsManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestoreFactoryActivityViewHolder implements View.OnClickListener {
    private static final String TAG = "RestoreFactoryActivityViewHolder";
    private View closeview;
    private AlertDialog.Builder close_builder;
    private Context context;
    private RestoreFactoryActivity activity;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    Button restore_ok;
    Button restore_cancel;
    private boolean isReset;
    private boolean msetting_copy_cultraview_projrct;

    public RestoreFactoryActivityViewHolder(Context context) {
        this.context = context;
        activity = (RestoreFactoryActivity) context;
        initdata();
        initView();
    }

    private void initView() {
        close_builder = new AlertDialog.Builder(context);
        closeview = LayoutInflater.from(context).inflate(R.layout.close_layout, null);
        close_builder.setView(closeview);
    }

    private void initdata() {
        restore_ok = (Button) activity.findViewById(R.id.restore_ok);
        restore_cancel = (Button) activity.findViewById(R.id.restore_cancel);
        restore_ok.setOnClickListener(this);
        restore_cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.restore_ok) {
            if (ActivityManager.isUserAMonkey()) {
                return;
            }


            ProgressDialog    dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.restore_factory_system_reboot));
            dialog.setCancelable(false);
            dialog.setIndeterminate(true);
            dialog.show();
            Thread closeSystemThread = new Thread(closeSystemRunnable);
            closeSystemThread.start();


        } else if (id == R.id.restore_cancel) {
            activity.finish();
        }

    }

    //Start WhiteBoard Patch
    Runnable closeSystemRunnable = new Runnable() {
        @Override
        public void run() {
            try {

                if (HHTOpsManager.getInstance().isOpsOk()) {
                    HHTOpsManager.getInstance().setOpsPowerTurnOff();
                    int count = 0;
                    while (HHTOpsManager.getInstance().isOpsOk()) {

                        Thread.sleep(1000);
                        count++;
                        if (count == 30) {
                            break;
                        }
                    }
                    ;
                }
                resetPassword();
                resetStart();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    };

    private void resetStart() {

        // TODO: 2019-10-24 8386

        Log.d("qkmin", "resetStart");
        try {
            TvManager tvManager = TvManager.getInstance();
            msetting_copy_cultraview_projrct = tvManager.setTvosInterfaceCommand("MSETTING_COPY_CULTRAVIEW_PROJRCT");
            tvManager.setTvosCommonCommand("Closeaudio");
            Log.d(TAG, "-----setTvosCommonCommand copy cultraview_pro: " + msetting_copy_cultraview_projrct);
        } catch (TvCommonException e) {
            e.printStackTrace();
            Log.e("qkmin", "resetStart TvCommonException" + e);
        }
        Settings.System.putInt(context.getContentResolver(), "menuTimeMode", 0);
//        Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
//        intent.putExtra("from", "restorefactory");
//        context.sendBroadcast(intent);

        //qkmkin 8.0和6.0不同

//        if (msetting_copy_cultraview_projrct) {
//            intoRecovery();
//
//        }

        intoRecovery();


    }


    private void intoRecovery() {
        Intent resetIntent;
        if (android.os.Build.VERSION.RELEASE.equals("8.0.0")) {
            resetIntent = new Intent("android.intent.action.FACTORY_RESET");
            resetIntent.setPackage("android");
            resetIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            resetIntent.putExtra(Intent.EXTRA_REASON, "ResetConfirmFragment");
//            if (activity.getIntent().getBooleanExtra("shutdown", false)) {
//                resetIntent.putExtra("shutdown", true);
//            }
        } else {
            resetIntent = new Intent("android.intent.action.MASTER_CLEAR");
            resetIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            resetIntent.putExtra("from", "restorefactory");
        }
        context.sendBroadcast(resetIntent);
    }


    public boolean resetHotelDB() {
        int result = -1;
        File srcFile = new File("/tvdatabase/Database/", "hotel_mode.db");
        File destFile = new File("/tvconfig/Database/", "hotel_mode.db");
        try {
            result = CtvTvManager.getInstance().copyCmDb(srcFile.getPath(), destFile.getPath());
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if (result == -1) {
            Log.d(TAG, "save hotel_mode.db to /tvconfig/Database/ fail!!");
            return false;
        }
        Log.d(TAG, "save ok");
        return true;
    }

    public boolean resetPassword() {
        int result = -1;
        File srcFile = new File("/tvdatabase/Database/", "cultraview_projectinfo.db");
        File destFile = new File("/tvconfig/Database/", "cultraview_projectinfo.db");
        try {
            result = CtvTvManager.getInstance().copyCmDb(srcFile.getPath(), destFile.getPath());
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (result == -1) {
            Log.d(TAG, "save cultraview_password.db to /tvconfig/Database/ fail!!");
            return false;
        }
        Log.d(TAG, "save ok");
        return true;
    }

}




