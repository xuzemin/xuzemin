package com.ctv.settings.about.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemProperties;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.ctv.settings.R;
import com.ctv.settings.about.Adapter.UpdateAdapter;
import com.ctv.settings.about.Bean.TipDialog;
import com.ctv.settings.about.activity.SystemUpdateActivity;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.Tools;
import com.mstar.android.tvapi.common.TvManager;

public class UpdateViewHolder implements View.OnClickListener {
    private static boolean isUpgradeStart = false;
    private View mainback;

    private String[] SYSTEM_UPDATE_STRING;
    private ListView system_update_lv;
    private Handler hanlder;
    private Context context;
    private SystemUpdateActivity activity;
    private boolean isMit=false;
    private UpdateAdapter updateAdapter;


    private TipDialog upgrade_dialog;


    public static final String LOCAL_UPDATE_ACTION = "com.cultraview.settings.update.LOCAL_UPDATE";



    public UpdateViewHolder(Context context){
        this.context=context;
        activity=(SystemUpdateActivity)context;
        hanlder = new Handler();
        initData();
        initListenner();
    }



    private void initData() {
        mainback= activity.findViewById(R.id.back_btn);
        system_update_lv = (ListView) activity.findViewById(R.id.system_update_lv);
        if (true/** SystemProperties.get("ro.product.brand").equals("MITASHI") **/
        ) {
            isMit = true;
            SYSTEM_UPDATE_STRING = context.getResources().getStringArray(
                    R.array.system_update_item_mit);
        }
        /**
         * else { isMit = false; SYSTEM_UPDATE_STRING =
         * ctvContext.getResources().getStringArray(
         * R.array.system_update_item); }
         **/
        updateAdapter = new UpdateAdapter(context, SYSTEM_UPDATE_STRING);
        system_update_lv.setAdapter(updateAdapter);
    }






    private void initListenner() {
        mainback.setOnClickListener(this);
        system_update_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                doClickOrLeft(position);
            }
        });
    }
    private void doClickOrLeft(int position) {
        if (position == 0) {
            if (isMit) {
                // LocalUpdateActivity.class          "com.cultraview.settings.update.LOCAL_UPDATE"
                context.startActivity(new Intent("com.cultraview.settings.update.LOCAL_UPDATE"));
                try {
                    TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_High");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // Determine the network status, if ok upgrade function
                if (Tools.isNetConnected(context)) {
                    // com.cultraview.update.NetworkUpdate.java
                    if (!SystemProperties.get("ro.tcl.devmodel", "NONE").equals("NONE")) {
                        Intent intent = new Intent("com.cultraview.update.NETWORK_UPDATE");
                        intent.putExtra("FromSetting", true);
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {

                            return;
                        }
                        System.gc();
                        activity.finish();
                    } else {
                        context.startActivity(new Intent("android.intent.action.CTV_OPEN_DOWNLOADER"));
                    }
                } else {

                }
            }
        } else if (position == 1) {
//            if (!upgrade_dialog.isShowing()) {
//                upgrade_dialog.show();
//            }

            if (isMit){

                context.startActivity(new Intent("com.cultraview.settings.update.SYSTEM_UPDATE"));
            }    try {
                TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_High");
            } catch (Exception e) {
                e.printStackTrace();
            }
//            if (!upgrade_dialog.isShowing()) {
//                upgrade_dialog.show();
//            }
                /*if (CheckUsbIsExist() && !isUpgradeStart) {
                    isUpgradeStart = true;
                    UpgradeMain();
                } else {
                    isUpgradeStart = false;
                    Toast.makeText(ctvContext, "Please Plug USB!", Toast.LENGTH_SHORT).show();
                }*/
        }else if (position ==2){ //OTA
            L.d("qkmin---OTA click");
//            Intent intent = new Intent();
//            ComponentName cName = new ComponentName(
//                    "com.cultraview.osupdatey","com.cultraview.osupdate.OnlineUpdateActivity"); //其中两个参数的含义:第一个是要跳转到的app的包名，第二个参数是该包中的要跳转到app的页面的class
//            intent.setComponent(cName);
//            context.startActivity(intent);
        }
        else {
            if (Tools.isNetConnected(context)){
                if (!SystemProperties.get("ro.tcl.devmodel", "NONE").equals("NONE")){
                    Intent intent = new Intent("com.cultraview.update.NETWORK_UPDATE");
                    intent.putExtra("FromSetting", true);
                    try {
                        context.startActivity(intent);
                    }catch (Exception e){
                        Toast.makeText(activity, "update can not be done!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    System.gc();
                    activity.finish();
                } else {
                    context.startActivity(new Intent("android.intent.action.CTV_OPEN_DOWNLOADER"));
                }
            }else {

            }

        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_btn) {
            activity.finish();
        }
    }

    public enum EnumUpgradeStatus {
        // status fail
        E_UPGRADE_FAIL,
        // status success
        E_UPGRADE_SUCCESS,
        // file not found
        E_UPGRADE_FILE_NOT_FOUND,
        // File is already up to date
        E_UPGRADE_FILE_ALREADY_UP_TO_DATE,
    }
}
