package com.ctv.settings.about.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.ctv.settings.about.Adapter.UpdateAdapter;
import com.ctv.settings.about.Bean.TipDialog;
import com.ctv.settings.about.R;
import com.ctv.settings.about.activity.LocalUpdateActivity;
import com.ctv.settings.about.activity.SystemUpdateActivity;
import com.ctv.settings.utils.SystemPropertiesUtils;
import com.ctv.settings.utils.Tools;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.vo.Constants;

import java.io.File;
import java.util.logging.LogRecord;

public class UpdateViewHolder {
    private static boolean isUpgradeStart = false;

    private String[] SYSTEM_UPDATE_STRING;
    private ListView system_update_lv;
    private Handler hanlder;
    private Context context;
    private SystemUpdateActivity activity;
    private boolean isMit=false;
    private UpdateAdapter updateAdapter;
        private TipDialog upgrade_dialog;
    public UpdateViewHolder(Context context){
        this.context=context;
        activity=(SystemUpdateActivity)context;
        hanlder = new Handler();
        initData();
        initView();
        initListenner();
    }
    private void initData() {
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
    private void initView() {
        if(upgrade_dialog == null){
            upgrade_dialog = new TipDialog(context, context.getResources().getString(
                    R.string.open_usb_upgrade_tip)) {
                @Override
                public void onDialogClick(boolean isOK) {
                    if (isOK) {
                        if (CheckUsbIsExist() && !isUpgradeStart) {
                            isUpgradeStart = true;
                            UpgradeMain();
                        } else {
                            isUpgradeStart = false;
                            Toast.makeText(context,context.getResources().getString(R.string.please_plug_usb), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        isUpgradeStart = false;
                    }
                }
            };
        }
    }

    private boolean CheckUsbIsExist() {
        boolean ret = false;
        // TODO check USB exist function
        ret = CheckUsbIsExistS();
        return ret;
    }
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
                hanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal()) {
                            Toast.makeText(context, context.getResources().getString(R.string.upgrade_sucessful), Toast.LENGTH_SHORT)
                                    .show();
/*                            try {
                                CtvTvManager.getInstance().setEnvironment("db_table", "0");
                            } catch (CtvCommonException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }*/
                            CtvCommonManager.getInstance().rebootSystem("reboot");
                            isUpgradeStart = false;
                        } else if (upgrate_status == EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal()) {
                            isUpgradeStart = false;
                            Toast.makeText(context, context.getResources().getString(R.string.upgrade_file_not_found), Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            isUpgradeStart = false;
                            Toast.makeText(context, context.getResources().getString(R.string.upgrade_file_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
        return true;
    }
    public int UpgradeMainFun() {
        int ret = 0;
        // TODO UpgradeMain function
        String mainpath;
        mainpath = FindFileOnUSB("CtvUpgrade.bin");
        // should not change this file name

        if ("" == mainpath) {
            ret = EnumUpgradeStatus.E_UPGRADE_FILE_NOT_FOUND.ordinal();
        } else {
            try {
                CtvTvManager.getInstance().setEnvironment("factory_mode", "1");

                if (CtvTvManager.getInstance().setEnvironment("upgrade_mode", "usb")) {
                    CtvTvManager.getInstance().setEnvironment("CtvUpgrade_complete", "0");
                    ret = EnumUpgradeStatus.E_UPGRADE_SUCCESS.ordinal();
                } else {
                    Log.d("UpgradeMainFun:", "setEnvironment Failed!");
                    ret = EnumUpgradeStatus.E_UPGRADE_FAIL.ordinal();
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

    private void initListenner() {
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
                    if (!SystemPropertiesUtils.get(context, "ro.tcl.devmodel", "NONE").equals("NONE")) {
                        Intent intent = new Intent("com.cultraview.update.NETWORK_UPDATE");
                        intent.putExtra("FromSetting", true);
                        try {
                            context.startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(activity, "update can not be done!",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        System.gc();
                        activity.finish();
                    } else {
                        context.startActivity(new Intent("android.intent.action.CTV_OPEN_DOWNLOADER"));
                    }
                } else {
                    Toast.makeText(context, R.string.not_network, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (position == 1) {
            if (!upgrade_dialog.isShowing()) {
                upgrade_dialog.show();
            }
                /*if (CheckUsbIsExist() && !isUpgradeStart) {
                    isUpgradeStart = true;
                    UpgradeMain();
                } else {
                    isUpgradeStart = false;
                    Toast.makeText(ctvContext, "Please Plug USB!", Toast.LENGTH_SHORT).show();
                }*/
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
