package com.ctv.settings.about.ViewHolder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.ctv.settings.about.Adapter.AboutTvAdapter;
import com.ctv.settings.about.Bean.AboutTvInfo;
import com.ctv.settings.about.Bean.AboutTvXml;
import com.ctv.settings.about.Bean.DeviceNameDialog;
import com.ctv.settings.about.R;
import com.ctv.settings.about.activity.AboutActivity;
import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.utils.*;
import com.mstar.android.tvapi.common.vo.Constants;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AboutViewHolder {
    private static final String TAG = "AboutViewHolder";

    private static Activity activity;

    // private static Context  context;

    private AboutTvAdapter aboutTvAdapter;

    private List<AboutTvInfo> aboutTvInfos;

    private ListView about_tv_lv;

    public TextView about_tv_us_fl_tx, about_tv_url_tx;

    private FrameLayout about_tv_fl_head, about_tv_fl_bottom;

    private String[] about_tv_value, ABOUT_TV_STRING;

    private String infoDDR, infoEMMC;

    private File file;

    View fl_about_bg;

    private static View dialog_show_background;

    public static TextView about_tv_tv;
    public static Handler aboutTvHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CommonConsts.DEVICE_NAME_CHANGE:
                    String deviceName = (String) msg.obj;
                    // about_tv_tv.setText(deviceName);
                    SPUtil.saveData(AboutViewHolder.activity, CommonConsts.DEVICE_TITLE,
                            deviceName);
                    break;
                case CommonConsts.DIALOG_DISMISS:
                    dialog_show_background.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }

    };
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            aboutTvAdapter.setAboutTvInfos(aboutTvInfos);
            aboutTvAdapter.notifyDataSetChanged();
        }
    };

    public AboutViewHolder(Activity activity) {
        this.activity = activity;
        activity = (Activity) activity;
        initData(activity);

    }


    public void initUI(Activity activity) {


    }


    public void initData(Activity activity) {
        fl_about_bg = activity.findViewById(R.id.ll_about_bg);
        dialog_show_background = activity.findViewById(R.id.dialog_show_background);
        about_tv_lv = (ListView) activity.findViewById(R.id.lv_about_tv);
        //  about_tv_fl_head = (FrameLayout) LayoutInflater.from(activity).inflate(
        // R.layout.about_tv_head, null);
        about_tv_fl_bottom = (FrameLayout) LayoutInflater.from(activity).inflate(
                R.layout.about_tv_bottom, null);
        about_tv_us_fl_tx = (TextView) about_tv_fl_bottom.findViewById(R.id.about_tv_us_fl_tx);
        about_tv_url_tx = (TextView) about_tv_fl_bottom.findViewById(R.id.about_tv_url_tx);
        // about_tv_lv.addHeaderView(about_tv_fl_head);
        setKeyListener();
        if (DataTool.getCustomer(activity).equals("CYF")) {
            about_tv_lv.addFooterView(about_tv_fl_bottom);
        } else if (SystemPropertiesUtils.get(activity, "CTV").equalsIgnoreCase("macan")
                && (SystemPropertiesUtils.get(activity, "CTV").equalsIgnoreCase("VIANO") || DataTool
                .getCustomer(activity).equalsIgnoreCase("VIANO"))) {
            about_tv_lv.addFooterView(about_tv_fl_bottom);
            about_tv_us_fl_tx.setText("VIANO SUPPORT");
            about_tv_url_tx.setVisibility(View.VISIBLE);
            about_tv_url_tx.setText("https://iviano.com/support");
        }
        // about_tv_tv = (TextView) about_tv_fl_head.findViewById(R.id.about_tv_tv);
        //  about_tv_tv.setText(DataTool.getDeviceName(activity));
        aboutTvInfos = new ArrayList<AboutTvInfo>();
        infoDDR = Tools.getDDRInformation(activity);
        infoEMMC = Tools.getEMMCInformation(activity);
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long utc1 = Long.parseLong(SystemPropertiesUtils.get(activity, "ro.build.date.utc"));
        sdr.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String str_build_date = "";
//        if(SystemPropertiesUtils.get(activity,"client.config").equals("KJMT")){
//            str_build_date =sdr.format(new Date(utc1 * 1000L));
//        }else{
        // TODO: 2019-11-06 8386 ro.build.display.id=CN8386_AH_EDU_V20191106_102456


        String disPlayName = SystemPropertiesUtils.get(activity, "ro.build.display.id", "EDU");// DFQ
        L.i("qkmin---->+clientName"+disPlayName);
        str_build_date = disPlayName + " " + sdr.format(new Date(utc1 * 1000L));
//        }
        if (!isCustomerAbout()) {
            AboutTvInfo aboutTvInfo;
            String clientNameTmp = SystemPropertiesUtils.get(activity, "ro.build.display.id", "EDU");// DFQ

            String[] strArr = clientNameTmp.split("_");
            String clientName = strArr[strArr.length-1];
            L.i("qkmin---->clientName"+clientName);

            // SG
            // EDU
            // MTG

            ABOUT_TV_STRING = activity.getResources().getStringArray(R.array.about_tv_item);
            about_tv_value = new String[]{
                    getCpu(activity),
                    getGPU(activity),
                    clientName,// DataTool.getModelName(ctvContext),// 1. Model
                    Build.VERSION.RELEASE, // 2. Android version
                    str_build_date,// getSystemVersion(ctvContext),// 3. System
                    // version
                    infoDDR,// 4. Memory info
                    infoEMMC,// 5.Emmc info

            };
            if (DataTool.getCustomer(activity).startsWith("CNC")) {
                ABOUT_TV_STRING = activity.getResources().getStringArray(
                        R.array.about_tv_item_cnc);
            }
            if (DataTool.getCustomer(activity).startsWith("CNC")) {
                about_tv_value = new String[]{
                        // 1. Model /Android version
                        Build.VERSION.RELEASE, getSystemVersion(activity),
                        // 2. System version
                        getMemoryInformation(activity), // 3. Memory info
                        getCpu(activity),
                        getGPU(activity)
                };
            }
            for (int i = 0; i < ABOUT_TV_STRING.length; i++) {
                aboutTvInfo = new AboutTvInfo();
                aboutTvInfo.setAboutString(ABOUT_TV_STRING[i]);
                aboutTvInfo.setAboutValue(about_tv_value[i]);
                aboutTvInfos.add(aboutTvInfo);
            }
        }
        aboutTvAdapter = new AboutTvAdapter(activity, aboutTvInfos);
        about_tv_lv.setAdapter(aboutTvAdapter);
        // must set this,or the focus will be get by item button、imagebutton
        about_tv_lv.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        about_tv_lv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//              if (view == about_tv_fl_head) {
//                  setHeadFocus(true);
//                  setBottomFocus(false);
//              } else
                if (i == about_tv_lv.getChildCount() - 1) {
                    //setHeadFocus(false);
                    // setBottomFocus(true);
                } else {
                    //  setHeadFocus(false);
                    // setBottomFocus(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        about_tv_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (view == about_tv_fl_head) {
                    dialog_show_background.setVisibility(View.VISIBLE);
                    DeviceNameDialog deviceNameDialog = new DeviceNameDialog(activity);
                    deviceNameDialog.show();

                    handleDialog(deviceNameDialog);
                } else if (i == about_tv_lv.getChildCount() - 1) {
                    if (DataTool.getCustomer(activity).equals("CYF")) {
                        activity.startActivity(new Intent(
                                "com.cultraview.settings.abouttv.ABOUT_US"));
                    } else if (SystemPropertiesUtils.get(activity, "ro.board.platform", "CTV").equalsIgnoreCase(
                            "macan")
                            && (SystemPropertiesUtils.get(activity, "persist.sys.brand", "CTV").equalsIgnoreCase(
                            "VIANO") || DataTool.getCustomer(activity).equalsIgnoreCase(
                            "VIANO"))) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri content_url = Uri.parse("https://iviano.com/support");
                        intent.setDataAndType(content_url, "text/html");
                        activity.startActivity(intent);
                    }

                }

            }
        });
    }

    //    private void setHeadFocus(boolean flag) {
//        if (flag) {
//            ((TextView) about_tv_fl_head.getChildAt(0)).setTextColor(activity.getResources()
//                    .getColor(R.color.white));
//            ((TextView) about_tv_fl_head.getChildAt(1)).setTextColor(activity.getResources()
//                    .getColor(R.color.white));
//            ((ImageView) about_tv_fl_head.getChildAt(2))
//                    .setBackgroundResource(R.drawable.item_rigth_focus);
//        } else {
//            ((TextView) about_tv_fl_head.getChildAt(0)).setTextColor(activity.getResources()
//                    .getColor(R.color.half_white));
//            ((TextView) about_tv_fl_head.getChildAt(1)).setTextColor(activity.getResources()
//                    .getColor(R.color.half_white));
//            ((ImageView) about_tv_fl_head.getChildAt(2))
//                    .setBackgroundResource(R.drawable.item_rigth_unfocus);
//        }
//    }
    private void setBottomFocus(boolean flag) {
        if (flag) {
            ((TextView) about_tv_fl_bottom.getChildAt(0)).setTextColor(activity.getResources()
                    .getColor(R.color.white));
            ((TextView) about_tv_fl_bottom.getChildAt(1)).setTextColor(activity.getResources()
                    .getColor(R.color.white));
            ((ImageView) about_tv_fl_bottom.getChildAt(2))
                    .setBackgroundResource(R.drawable.item_rigth_focus);
        } else {
            ((TextView) about_tv_fl_bottom.getChildAt(0)).setTextColor(activity.getResources()
                    .getColor(R.color.half_white));
            ((TextView) about_tv_fl_bottom.getChildAt(1)).setTextColor(activity.getResources()
                    .getColor(R.color.half_white));
            ((ImageView) about_tv_fl_bottom.getChildAt(2))
                    .setBackgroundResource(R.drawable.item_rigth_unfocus);
        }

    }

    private String getGPU(Context context) {
        return context.getResources().getString(R.string.gpu_info_8386);
    }

//    private String getGpu(Context ctvContext) {
//        // [ro.product.device]: [cv6a338_base]
//        if (SystemPropertiesUtils.get(context,"ro.product.device").contains("338")) {
//            return ctvContext.getResources().getString(R.string.gpu_infor_338);
//        } else if (SystemPropertiesUtils.get(context,"ro.board.platform", "CTV").equalsIgnoreCase("macan")) {
//            // 538
//            return ctvContext.getResources().getString(R.string.gpu_infor_538);
//        } else {
//            return ctvContext.getResources().getString(R.string.gpu_infor_new);
//        }
//    }

    private String getMemoryInformation(Context ctvContext) {
        if (SystemPropertiesUtils.get(activity, "ro.product.device").contains("338")) {
            if (SystemPropertiesUtils.get(activity, "ro.product.manufacturer").equals("JIEAIWEI_MODEL")
                    || SystemPropertiesUtils.get(activity, "ro.product.manufacturer").equals("DASHI_MODEL")) {
                return "4GB/128GB";
            } else {
                return Tools.getDDRInformation(ctvContext);
            }
        } else {
            if (SystemPropertiesUtils.get(activity, "ro.product.brand").equals("MITASHI")
                    || "MXY".equals("")) {
                return Tools.getDDRInformation(ctvContext);
            } else {
                return Tools.getEMMCInformation(ctvContext);
            }
        }
    }

    private String getSystemVersion(Context ctvContext) {
        if (SystemPropertiesUtils.get(activity, "ro.product.manufacturer").equals("JIEAIWEI_MODEL")
                || SystemPropertiesUtils.get(activity, "ro.product.manufacturer").equals("DASHI_MODEL")) {
            Log.i(TAG, "subCustomerModel");
            return DataTool.getMainVersion(ctvContext);
        } else {
            return DataTool.getCustomVersion(ctvContext); // keh
        }
    }

    private String getCpu(Context ctvContext) {
        // [ro.product.device]: [cv6a338_base]
//        if (SystemPropertiesUtils.get(activity,"ro.product.device").contains("338")) {
//            if (SystemPropertiesUtils.get(activity,"ro.product.manufacturer").equals("JIEAIWEI_MODEL")) {
//                return ctvContext.getResources().getString(R.string.cpu_infor_338_jav);
//            } else {
//                return ctvContext.getResources().getString(R.string.cpu_infor_338);
//            }
//        } else if (SystemPropertiesUtils.get(activity,"ro.board.platform", "CTV").equalsIgnoreCase("macan")) {
//            // 538
//            return ctvContext.getResources().getString(R.string.cpu_infor_538);
//        } else {
//            return ctvContext.getResources().getString(R.string.cpu_infor_cv6a648); // cpu_infor_new
//        }

        return ctvContext.getResources().getString(R.string.cpu_info_8386);
    }

    private boolean isCustomerAbout() {
        Configuration conf = activity.getResources().getConfiguration();
        String language = conf.locale.getLanguage();
        file = new File("/tvcustomer/Customer/about_" + language + ".xml");
        if (!file.exists()) {
            file = new File("/tvcustomer/Customer/about.xml");
            if (!file.exists()) {
                Log.i(TAG, "-->  aboute turn false");
                return false;
            }
        }
        new Thread(new CustomerAboutThread()).start();
        return true;
    }

    private class CustomerAboutThread extends Thread {
        @Override
        public void run() {
            try {
                aboutTvInfos = AboutTvXml.parseFile(file);
                int size = aboutTvInfos.size();
                // set
                if (aboutTvInfos.get(size - 1).getVisibility().equals("gone")) {
                    aboutTvInfos.remove(size - 1);
                } else {
                    aboutTvInfos.get(size - 1).setAboutValue(infoEMMC);
                }
                if (aboutTvInfos.get(size - 2).getVisibility().equals("gone")) {
                    aboutTvInfos.remove(size - 2);
                } else {
                    aboutTvInfos.get(size - 2).setAboutValue(infoDDR);
                }
                handler.sendEmptyMessage(3);
            } catch (Exception e) {
                Log.i("getAttributeValue", "Exception:" + e);
                e.printStackTrace();
            }
        }
    }

    ;

    public void initListener() {

    }


    public void refreshUI(View view) {

    }

    private void setKeyListener() {
        View.OnKeyListener onKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                        if (about_tv_lv.getSelectedItemPosition() == 0) {
                            //  about_tv_fl_head.setBackgroundResource(R.drawable.transparency_bg);
                            about_tv_lv.setSelection(about_tv_lv.getChildCount() - 1);
                        } else {
                            //  about_tv_fl_head.setBackgroundResource(R.drawable.select_item_bg);
                            about_tv_lv.setSelection(0);
                        }
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                        if (about_tv_lv.getSelectedItemPosition() == 0) {
                            // about_tv_fl_head.setBackgroundResource(R.drawable.transparency_bg);
                            about_tv_lv.setSelection(about_tv_lv.getChildCount() - 1);
                        } else {
                            //  about_tv_fl_head.setBackgroundResource(R.drawable.select_item_bg);
                            about_tv_lv.setSelection(0);
                        }
                        return true;
                    }
                }
                return false;
            }
        };
    }

    public void isShow(boolean isShow) {
        int visible = isShow ? View.VISIBLE : View.INVISIBLE;
        fl_about_bg.setVisibility(visible);
    }

    private void handleDialog(final Dialog dialog) {
        isShow(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                isShow(true);
            }
        });
    }
}
