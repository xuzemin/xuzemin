package com.ctv.settings.about.Bean;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ctv.settings.about.R;
import com.ctv.settings.about.ViewHolder.UpdateViewHolder;
import com.ctv.settings.about.activity.LocalUpdateActivity;
import com.ctv.settings.about.activity.SystemupdateItemActivity;
import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.mstar.android.tvapi.common.TvManager;

import java.io.File;

public class SystemSelectUpdateDialog extends Dialog implements View.OnFocusChangeListener, View.OnClickListener {
    private static final String TAG = "SelectUpdateDialog";
    private final SystemupdateItemActivity systemupdateItemActivity;
    private Button system_update_sele_btn;
    private ListView system_update_sele_lv;
    private TipDialog upgrade_dialog;


    public static final String LOCAL_UPDATE_ACTION = "com.cultraview.settings.update.LOCAL_UPDATE";

    private static boolean isUpgradeStart = false;

    public SystemSelectUpdateDialog(SystemupdateItemActivity systemupdateItemActivity) {
        super(systemupdateItemActivity);
        this.systemupdateItemActivity = systemupdateItemActivity;
        initView();

    }
    private void initView() {
        if(upgrade_dialog == null){
            upgrade_dialog = new TipDialog(systemupdateItemActivity, systemupdateItemActivity.getResources().getString(
                    R.string.open_usb_upgrade_tip)) {
                @Override
                public void onDialogClick(boolean isOK) {
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
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.system_select_update);
    }

    private void setWindowStyle() {
        Window w = getWindow();
        Resources res = systemupdateItemActivity.getResources();
        Drawable drab = res.getDrawable(R.drawable.transparency_bg);
        w.setBackgroundDrawable(drab);
        WindowManager.LayoutParams lp = w.getAttributes();
        final float scale = res.getDisplayMetrics().density;
        // In the mid-point to calculate the offset x and y
        lp.y = (int) (-36 * scale + 0.5f);
        lp.width = (int) (680 * scale + 0.5f);
        lp.height = (int) (408 * scale + 0.5f);
        w.setAttributes(lp);
    }

    private void findViews() {
        system_update_sele_btn = (Button) findViewById(R.id.system_update_sele_btn);
        system_update_sele_btn.setOnFocusChangeListener(this);
        system_update_sele_btn.setOnClickListener(this);
        system_update_sele_lv = (ListView) findViewById(R.id.system_update_sele_lv);
        SimpleAdapter simpleAdapter = new SimpleAdapter(systemupdateItemActivity,

                systemupdateItemActivity.MountedVolumes, R.layout.system_update_select_item,
                new String[]{
                        "volume_lable"
                }, new int[]{
                R.id.system_updatezip_list_item
        });
        system_update_sele_lv.setAdapter(simpleAdapter);
        Log.i(TAG, "-----mMountedVolumes:" + systemupdateItemActivity.MountedVolumes.toString());
        system_update_sele_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (position >= systemupdateItemActivity.ScanFiles.size()) {
//                    systemupdateItemActivity.UpdateFile = systemupdateItemActivity.ScanFiles
//                            .get(systemupdateItemActivity.ScanFiles.size() - 1);
//                } else if (position > 0) {
//                    systemupdateItemActivity.UpdateFile = systemupdateItemActivity.ScanFiles
//                            .get(position - 1);
//                } else {
//                    systemupdateItemActivity.UpdateFile = systemupdateItemActivity.ScanFiles.get(position);
//                }
//                dismiss();
//                systemupdateItemActivity.myHandler.sendEmptyMessage(5);


                switch (position) {
                    case 0:
//                        if (isMit) {
                        // LocalUpdateActivity.class
                        systemupdateItemActivity.startActivity(new Intent(LOCAL_UPDATE_ACTION));
                        try {
                            TvManager.getInstance().setTvosCommonCommand("SetUSBONOFF_High");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
//                        } else {
//                            // Determine the network status, if ok upgrade function
//                            if (Tools.isNetConnected(ctvContext)) {
//                                // com.cultraview.update.NetworkUpdate.java
//                                if (!SystemProperties.get("ro.tcl.devmodel", "NONE").equals("NONE")) {
//                                    Intent intent = new Intent("com.cultraview.update.NETWORK_UPDATE");
//                                    intent.putExtra("FromSetting", true);
//                                    try {
//                                        ctvContext.startActivity(intent);
//                                    } catch (Exception e) {
//                                        Toast.makeText(activity, "update can not be done!",
//                                                Toast.LENGTH_SHORT).show();
//                                        return;
//                                    }
//                                    System.gc();
//                                    activity.finish();
//                                } else {
//                                    ctvContext.startActivity(new Intent(Constants.NET_UPDATE_ACTION));
//                                }
//                            } else {
//                                Toast.makeText(ctvContext, R.string.not_network, Toast.LENGTH_SHORT).show();
//                            }
//                        }
                        break;
                    case 1:
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
                        break;
                    default:
                        break;
                }


            }
        });
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
