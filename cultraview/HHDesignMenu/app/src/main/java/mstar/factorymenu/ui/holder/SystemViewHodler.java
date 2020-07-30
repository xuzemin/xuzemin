package mstar.factorymenu.ui.holder;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvFactoryManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.cultraview.tv.utils.CtvCommonUtils;
import com.hht.android.sdk.boardInfo.HHTBoardInfoManager;
import com.hht.android.sdk.device.HHTCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.adapter.SystemAdapter;
import mstar.factorymenu.ui.bean.SystemBean;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.factorymenu.ui.utils.Tools;
import mstar.tvsetting.factory.desk.IFactoryDesk;
import mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity;

public class SystemViewHodler implements View.OnFocusChangeListener, View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    private final String DEFAULT_MAC_ADDRESS = "00:30:1B:BA:02:DB";

    private final String TAG = SystemViewHodler.class.getSimpleName();

    private Context mainActivity = null;
    private LinearLayout systemItem, systemInformation, systemUpgrade, systemUpgradeLayout;
    private ListView listView;
    private List list;
    private SystemAdapter systemAdapter;
    private String val_board_type = "HH_C", val_panel_type = "xxxxxxxx", val_sw_version = "xxxxxxxx", val_sw_upgrade_name = "MstarUpgrade.bin",
            val_touch_panel_firmware = "xxxxxxxx", val_touch_driver_firmware = "V1.0", val_type_c = "V1.0", val_hdmi_out = "xxxxxxxx", val_tuner_type = "MXL661", val_hdcpkey1 = "***", val_hdcpkey2 = "***", val_mac = "00:00:00:00:00:00", val_shipping = "Please Click!";
    private String val_sn, val_panel_version;
    public static String FACTORY_MODE_NORMAL = "0";
    public static String FACTORY_MODE_AUTOTEST = "1";

    protected final static int FLAG_SET_VOLUME_WITHOUT_UI = 1 << 7;


    protected final static int MSG_START_RESET_ALL = 110;
    protected final static int MSG_START_SHIPPING_MODE = 111;
    protected final static int MSG_REBOOT_SYSTEM = 121;
    protected final static int MSG_INTO_RECOVERY = 131;
    protected final static int MSG_INTO_LOAD_DB = 141;
    protected final static int MSG_INTO_CREATE_DESIGN_VIEW_HOLDER = 151;

    private String mac_ethAddr = "macaddr";
    private int itemIndex = 0; // 0 system information  1 system upgrade


    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 2000;// 规定有效时间
    long[] mHits = new long[COUNTS];
    @SuppressLint("HandlerLeak")
    protected final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_START_RESET_ALL) {
                resetall();
            } else if (msg.what == MSG_START_SHIPPING_MODE) {
                shippingmode();
            } else if (msg.what == MSG_REBOOT_SYSTEM) {
                CtvCommonManager.getInstance().rebootSystem("reboot");
            } else if (msg.what == MSG_INTO_RECOVERY) {
                intoRecovery();
            } else if (msg.what == MSG_INTO_CREATE_DESIGN_VIEW_HOLDER) {
            }
        }

        ;
    };


    public SystemViewHodler(View view, Context mainActivity, IFactoryDesk factoryDesk) {
        this.mainActivity = mainActivity;
        findView(view);
        setListener();
    }


    private void findView(View view) {
        listView = view.findViewById(R.id.system_information_layout);
        list = new ArrayList();
        systemAdapter = new SystemAdapter(mainActivity, R.layout.item_system, list);
        listView.setAdapter(systemAdapter);
        listView.setOnItemSelectedListener(this);
        initData();
    }

    private void setListener() {
        listView.setOnItemClickListener(this);
    }

    private void initData() {
        itemIndex = 0;
        list.clear();
        SystemBean soundVolOsdBean = new SystemBean();
        val_board_type = HHTBoardInfoManager.getInstance().getBoardModel();
        LogUtils.d("val_board_type:" + val_board_type);
        soundVolOsdBean.setTitle("Board Type");
        soundVolOsdBean.setValue(val_board_type);
        list.add(soundVolOsdBean);

        val_panel_type = getPanelTypeName();
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Panel Type");
        soundVolOsdBean.setValue(val_panel_type);
        list.add(soundVolOsdBean);

        val_panel_version = getPanelTypeVersion();
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Panel Version");
        soundVolOsdBean.setValue(val_panel_version);
        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("SW Version");
        timeStamp2Date(SystemProperties.get("ro.build.date.utc"));
        soundVolOsdBean.setValue(val_sw_version);
        list.add(soundVolOsdBean);
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("SW Upgrade Name");
        soundVolOsdBean.setValue(val_sw_upgrade_name);
        list.add(soundVolOsdBean);

        val_sn = HHTBoardInfoManager.getInstance().getBoardSerial();
        if (TextUtils.isEmpty(val_sn)) {
            val_sn = mainActivity.getResources().getString(R.string.str_burn_ng);
        }
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("SN");
        soundVolOsdBean.setValue(val_sn);
        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Touch Panel Firmware Version");
        getTouchPanelFirVersion();
        soundVolOsdBean.setValue(val_touch_panel_firmware);
        list.add(soundVolOsdBean);

        val_touch_driver_firmware = getTouchDriverVersion();
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Touch Panel Driver Version");
        soundVolOsdBean.setValue(val_touch_driver_firmware);
        if (!val_touch_driver_firmware.equals("V1.0")) {
            list.add(soundVolOsdBean);
        }

        val_type_c = getTypeC();
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Type-C FirmWare Version");
        soundVolOsdBean.setValue(val_type_c);
        list.add(soundVolOsdBean);

        val_hdmi_out = getHDMIOutVersion();
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("HDMIOut FirmWare Version");
        soundVolOsdBean.setValue(val_hdmi_out);
        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Tuner1 Type");
        soundVolOsdBean.setValue(val_tuner_type);
        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("HDCPKEY1");
        val_hdcpkey1 = checkHDCP1xkey();
        soundVolOsdBean.setValue(val_hdcpkey1);
        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("HDCPKEY2");
        val_hdcpkey2 = checkHDCP2key();
        soundVolOsdBean.setValue(val_hdcpkey2);
        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Mac Address");
        val_mac = getwireMacAddress();
        refreshwireMacAddress();
        soundVolOsdBean.setValue(val_mac);
        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Shipping INIT");
        soundVolOsdBean.setValue(val_shipping);
        list.add(soundVolOsdBean);
        systemAdapter.setData(list);
    }

    private String getTypeC() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            short[] getTypeCVersions = TvManager.getInstance().setTvosCommonCommand("GetTypeCVersion");
            LogUtils.d("getTypeC length---->" + getTypeCVersions.length);
            if (getTypeCVersions != null) {
                for (int i = 0; i < getTypeCVersions.length; i++) {
                    LogUtils.d("getTypeC--re->val:" + getTypeCVersions[i]);
                    //String val = Tools.hex2Str(Integer.toHexString(getTypeCVersions[i]));
                    //LogUtils.d("getTypeC--->val:" + val);
                    stringBuffer.append(getTypeCVersions[i] + ".");
                }
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
            LogUtils.e("getTypeC error---->" + e);
        }
        String tm = "V" + stringBuffer.toString().trim();
        String substring = tm.substring(0, tm.length() - 1);
        return substring;
    }

    private String getTouchDriverVersion() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            short[] oGetTouchDevVers = CtvTvManager.getInstance().setTvosCommonCommand("oGetTouchDDriveVer");
            LogUtils.d("getTouchDriverVersion size-->：" + oGetTouchDevVers.length);
            if (oGetTouchDevVers == null || oGetTouchDevVers.length == 0) {
                LogUtils.d("getTouchDriverVersion null return V1.0-->：");
                return "V1.0";
            }
            for (int i = 0; i < oGetTouchDevVers.length; i++) {
                LogUtils.d("getTouchDriverVersion i-->：" + i + "val:" + oGetTouchDevVers[i]);
                String val = Tools.hex2Str(Integer.toHexString(oGetTouchDevVers[i]));
                LogUtils.d("val_touch_panel_firmware--->val:" + val);
                stringBuffer.append(val);
            }
            LogUtils.d("getTouchDriverVersion stringBuffer：" + stringBuffer.toString());
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("getTouchDriverVersion--->" + e);
        }


        return "V1.0";
    }

    private String getPanelTypeName() {
        try {
            String CUSTOMER_INI_PATH = TvManager.getInstance().getStringFromIni(IFactoryDesk.SYS_INI_PATH,
                    IFactoryDesk.CUSTOMER_INI_KEY);
            String name = TvManager.getInstance().getStringFromIni(CUSTOMER_INI_PATH,
                    IFactoryDesk.PANEL_INI_KEY);
            String m_pPanelName = TvManager.getInstance().getStringFromIni(name,
                    IFactoryDesk.PANEL_INI_KEY);
            if (!TextUtils.isEmpty(m_pPanelName)) {
                return m_pPanelName;
            }
        } catch (Exception e) {
            LogUtils.e("getPanelTypeName error" + e);
        }

        return "";
    }

    private String getHDMIOutVersion() {
        try {
            short[] hdmioutVersion = CtvTvManager.getInstance().setTvosCommonCommand("GetNovaTekVersion");
            LogUtils.d("getHDMIOutVersion");
            if (hdmioutVersion != null) {
                for (int i = 0; i < hdmioutVersion.length; i++) {

                    int i1 = hdmioutVersion[i] / 16;
                    int i2 = hdmioutVersion[i] % 16;
                    LogUtils.d("HDMIout-i->" + hdmioutVersion[i] + "i1:" + i1 + "i2:" + i2);
                    return "V" + i1 + "." + i2;
                }
            }
        } catch (Exception e) {
            LogUtils.e("GetNovaTekVersion error:" + e);
        }
        return "";
    }

    /**
     * 获取版控
     *
     * @return
     */
    private String getPanelTypeVersion() {
        if (!TextUtils.isEmpty(SystemProperties.get("persist.product.model"))) {
            try {
                String panelModel = CtvTvManager.getInstance().getEnvironment("panelModel");
                if (!TextUtils.isEmpty(panelModel)) {
                    return SystemProperties.get("persist.product.model") + "_" + panelModel;
                }
            } catch (CtvCommonException e) {
                e.printStackTrace();
            }


            return SystemProperties.get("persist.product.model");
        }
        return "";

    }

    /**
     * 获取 Touch Panel Firmware Version
     */
    private void getTouchPanelFirVersion() {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            short[] oGetTouchDevVers = CtvTvManager.getInstance().setTvosCommonCommand("oGetTouchDevVer");
            LogUtils.d("oGetTouchDevVers size-->：" + oGetTouchDevVers.length);
            for (int i = 0; i < oGetTouchDevVers.length; i++) {
                String val = Tools.hex2Str(Integer.toHexString(oGetTouchDevVers[i]));
                LogUtils.d("val_touch_panel_firmware--->val:" + val);
                stringBuffer.append(val);
            }
            val_touch_panel_firmware = stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("val_touch_panel_firmware--->" + val_touch_panel_firmware);
        }
    }


    /**
     * 时间戳转换成日期格式字符串
     *
     * @param seconds 精确到秒的字符串
     * @param
     * @return
     */
    public void timeStamp2Date(String seconds) {
        String version = SystemProperties.get("ro.build.version.incremental");
        if (version.startsWith("TVOS")) {
            version = "TVOS-04.24";
        }
        LogUtils.d("sw version:" + seconds);
        if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
            return;
        }
        String format = "yyyyMMddHHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String format1 = sdf.format(new Date(Long.valueOf(seconds + "000")));
        StringBuilder sb = new StringBuilder(format1);
        sb.insert(8, ".");
        val_sw_version = version + "_" + sb.toString() + "_" + val_board_type;
    }


    @Override
    public void onFocusChange(View view, boolean b) {

    }

    /**
     * 更新System information
     */
    private void setSystemInforListData() {
        listView.setVisibility(View.VISIBLE);
        initData();
    }

    /**
     * 更新System upgrade
     */
    private void setSystemUpgradeListData() {
        itemIndex = 1;
        listView.setVisibility(View.VISIBLE);
        list.clear();
        SystemBean soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade Main");
        soundVolOsdBean.setValue(val_shipping);
        soundVolOsdBean.setId(1);
        list.add(soundVolOsdBean);
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade Panel");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade MCU");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);
        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade update_signed");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade Touch Panel");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade Touch Driver");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade Type-C");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade HDMIout");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Burn MAC");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Burn HDCP1");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);

        soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Burn HDCP2");
        soundVolOsdBean.setValue(val_shipping);

        list.add(soundVolOsdBean);
        systemAdapter.setData(list);
    }

    private void removeAllView() {
        listView.setVisibility(View.GONE);
        systemUpgradeLayout.setVisibility(View.GONE);

    }

    /**
     * PICTURE SETTING ITEM
     *
     * @param itemIndex
     * @param onFocus
     */
    public void showLayout(int itemIndex, boolean onFocus) {
        systemItem.setVisibility(View.VISIBLE);
    }

    public void setHideView() {
        systemItem.setVisibility(View.GONE);
    }

    /**
     * 设置二级菜单显示和隐藏
     *
     * @param visibility
     */
    public void setVisibility(int visibility) {
        systemItem.setVisibility(visibility);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

        }
    }


    private String getwireMacAddress() {
        String ret = null;

        String ethMac = getDeviceMacAddress("eth0");
        LogUtils.d(TAG, "ethMac = " + ethMac);
        if (ethMac.equals(DEFAULT_MAC_ADDRESS)) {
            return mainActivity.getResources().getString(R.string.str_burn_ng);
        }

        String envMac = "";
        try {
            envMac = CtvTvManager.getInstance().getEnvironment(mac_ethAddr);
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtils.d(TAG, "envMac1 = " + envMac);
        if (envMac == null || envMac.equals("")) {
            return mainActivity.getResources().getString(R.string.str_burn_ng);
        }
        envMac = envMac.toUpperCase(Locale.ENGLISH);
        LogUtils.d(TAG, "envMac2 = " + envMac);
        if (ethMac.equals(envMac)) {
            ret = ethMac;
        } else {
            ret = mainActivity.getResources().getString(R.string.str_burn_error);
        }
        return ret;
    }


    public void refreshwireMacAddress() {
        try {
            val_mac = CtvTvManager.getInstance().getEnvironment(mac_ethAddr);
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private String getDeviceMacAddress(String ifname) {
        if (!ifname.equals("eth0") && !ifname.equals("eth1")) {
            return null;
        }
        try {
            return loadFileAsString("/sys/class/net/" + ifname + "/address").toUpperCase(
                    Locale.ENGLISH).substring(0, 17);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String loadFileAsString(String filePath) throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }


    /**
     * 恢复出厂设置
     */
    private void resetSystem() {
        // TODO: 2020/3/21 maybe  shutdown ops
        handler.sendEmptyMessage(MSG_START_SHIPPING_MODE);
    }

    /**
     *
     */
    public void shippingmode() {
        Toast toastShipping;
        setFactoryMode(FACTORY_MODE_NORMAL);
        if (enableDtv()) {
            clearAtvCmdbBin();
            clearDtvCmdbBin();
        } else {
            clearDtvCmdbBin();
            clearAtvCmdbBin();
        }
        resetUserDB();
        CtvFactoryManager.getInstance().setUartOnOff(false);

        CtvCommonUtils.setCultraviewProjectInfo(mainActivity, "tbl_Configuration", "GuideMode", "1");

        if (getCustomer().equals("CNC_SANYO")) {
            toastShipping = Toast.makeText(mainActivity, R.string.fac_reset_ok,
                    Toast.LENGTH_LONG);
        } else {
            toastShipping = Toast.makeText(mainActivity, "SHIPPING MODE OK!",
                    Toast.LENGTH_LONG);
        }
        toastShipping.show();
        if (enableSMRecovery() == true) {
            handler.sendEmptyMessageDelayed(MSG_INTO_RECOVERY, 2000);
        } else {
            handler.sendEmptyMessageDelayed(MSG_REBOOT_SYSTEM, 2000);
        }
    }

    private boolean enableSMRecovery() {
        boolean ret = false;
        String enableSMRecovery = CtvCommonUtils.getCultraviewProjectInfo(mainActivity,
                "tbl_Configuration", "EnableSMRecovery");
        LogUtils.d(TAG, "EnableSMRecovery = " + enableSMRecovery);
        if (enableSMRecovery.equals("1")) {
            ret = true;
        }
        return ret;
    }


    // for customer ask
    public String getCustomer() {
        String customer = "";
        String split = "-";
        String systemVersion = CtvCommonUtils.getCultraviewProjectInfo(mainActivity, "tbl_SoftwareVersion",
                "MainVersion");
        StringTokenizer versioninfo = new StringTokenizer(systemVersion, split);
        if (versioninfo.hasMoreElements()) {
            customer = versioninfo.nextToken();
        } else {
            customer = "";
        }
        return customer;
    }

    void resetUserDB() {
        try {
            CtvTvManager.getInstance().setTvosInterfaceCommand("SHIPPING_COPY_DB");
            CtvTvManager.getInstance().setEnvironment("ATVBLUESCREENFLAG", "1");
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void setFactoryMode(String factoryMode) {
        LogUtils.d(TAG, "factoryMode==" + factoryMode);
        try {
            if (factoryMode.equals(FACTORY_MODE_AUTOTEST)) {
                CtvTvManager.getInstance().setEnvironment("factory_poweron_mode", "direct");
            } else {

                //CtvTvManager.getInstance().setEnvironment("factory_poweron_mode",
                //        SystemProperties.get("ro.product.powermode", "secondary"));
                LogUtils.d(TAG, "secondary");
                CtvTvManager.getInstance().setEnvironment("factory_poweron_mode", "secondary");

            }
            CtvTvManager.getInstance().setEnvironment("factory_burningmode", "0");
            CtvTvManager.getInstance().setEnvironment("factory_mode", factoryMode);
        } catch (CtvCommonException e) {
            // TODO auto build catch
            e.printStackTrace();
        }
    }


    public boolean enableDtv() {
        boolean ret = false;
        String enabledtv = CtvCommonUtils.getCultraviewProjectInfo(mainActivity, "tbl_Configuration",
                "EnableDTV");
        LogUtils.d(TAG, "enabledtv = " + enabledtv);
        if (enabledtv.equals("1")) {
            ret = true;
        }
        return ret;
    }

    public void clearAtvCmdbBin() {
        File atvsrcFile = new File("/tvcustomer/Customer/", "atv_cmdb_clear.bin");
        File atvdestFile = new File("/tvdatabase/Database/", "atv_cmdb.bin");
        copyFile(atvsrcFile, atvdestFile);
        LogUtils.d(TAG, "clear atv_cmdb_0.bin");
        if (CtvCommonManager.getInstance().getCurrentTvSystem() == CtvCommonManager.TV_SYSTEM_ISDB) {
            atvsrcFile = new File("/tvcustomer/Customer/", "atv_cmdb_cable_clear.bin");
            if (atvsrcFile != null && atvsrcFile.exists()) {
                atvdestFile = new File("/tvcustomer/Customer/", "atv_cmdb_cable.bin");
                copyFile(atvsrcFile, atvdestFile);
                LogUtils.d(TAG, "clear atv_cmdb_cable.bin");
            }
        }
    }

    public void clearDtvCmdbBin() {
        File dtvsrcFile = new File("/tvcustomer/Customer/", "dtv_cmdb_clear.bin");
        File dtvdestFile = new File("/tvdatabase/Database/", "dtv_cmdb_0.bin");
        copyFile(dtvsrcFile, dtvdestFile);
        LogUtils.d(TAG, "clear dtv_cmdb_0.bin");
        dtvsrcFile = new File("/tvcustomer/Customer/", "dtv_dvbc_cmdb_clear.bin");
        if (dtvsrcFile != null && dtvsrcFile.exists()) {
            dtvdestFile = new File("/tvdatabase/Database/", "dtv_cmdb_1.bin");
            copyFile(dtvsrcFile, dtvdestFile);
            LogUtils.d(TAG, "clear dtv_cmdb_1.bin");
        }
        dtvsrcFile = new File("/tvcustomer/Customer/", "dtv_sat_cmdb_clear.bin");
        if (dtvsrcFile != null && dtvsrcFile.exists()) {
            dtvdestFile = new File("/tvdatabase/Database/", "dtv_cmdb_2.bin");
            copyFile(dtvsrcFile, dtvdestFile);
            LogUtils.d(TAG, "clear dtv_cmdb_2.bin");
        }
    }

    private boolean copyFile(File srcFile, File destFile) {
        boolean result = false;

        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally {
                in.close();
            }

        } catch (IOException e) {
            result = false;
        }
        chmodFile(destFile);
        return result;
    }

    private void chmodFile(File destFile) {
        try {
            String command = "chmod 666 " + destFile.getAbsolutePath();
            LogUtils.d(TAG, "command = " + command);

            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec(command);

        } catch (IOException e) {
            LogUtils.d(TAG, "chmod fail!!!!");
            e.printStackTrace();
        }
    }

    private boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    LogUtils.d(TAG, "out.write(buffer, 0, bytesRead);");
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            LogUtils.e(TAG, e.getMessage());
            return false;
        }
    }

    public boolean setUartOnOff(boolean isEnable) {
        CtvFactoryManager.getInstance().setUartOnOff(isEnable);
        return true;
    }

    public void setCustomerAtvCmdbBin() {
        File atvsrcFile = new File("/tvcustomer/Customer/", "atv_cmdb_customer.bin");
        File atvdestFile = new File("/tvdatabase/Database/", "atv_cmdb.bin");
        LogUtils.d(TAG, "set factory atv_cmdb_ctv.bin");
        copyFile(atvsrcFile, atvdestFile);
        if (CtvCommonManager.getInstance().getCurrentTvSystem() == CtvCommonManager.TV_SYSTEM_ISDB) {
            atvsrcFile = new File("/tvcustomer/Customer/", "atv_cmdb_cable_customer.bin");
            if (atvsrcFile != null && atvsrcFile.exists()) {
                atvdestFile = new File("/tvcustomer/Customer/", "atv_cmdb_cable.bin");
                copyFile(atvsrcFile, atvdestFile);
                LogUtils.d(TAG, "set factory atv_cmdb_cable.bin");
            }
        }
    }

    public void setCustomerDtvCmdbBin() {
        File dtvsrcFile = new File("/tvcustomer/Customer/", "dtv_cmdb_customer.bin");
        File dtvdestFile = new File("/tvdatabase/Database/", "dtv_cmdb_0.bin");
        copyFile(dtvsrcFile, dtvdestFile);
        LogUtils.d(TAG, "set factory dtv_cmdb_ctv.bin");
        dtvsrcFile = new File("/tvcustomer/Customer/", "dtv_dvbc_cmdb_customer.bin");
        if (dtvsrcFile != null && dtvsrcFile.exists()) {
            dtvdestFile = new File("/tvdatabase/Database/", "dtv_cmdb_1.bin");
            copyFile(dtvsrcFile, dtvdestFile);
            LogUtils.d(TAG, "set factory dtv_cmdb_1.bin");
        }
        dtvsrcFile = new File("/tvcustomer/Customer/", "dtv_sat_cmdb_customer.bin");
        if (dtvsrcFile != null && dtvsrcFile.exists()) {
            dtvdestFile = new File("/tvdatabase/Database/", "dtv_cmdb_2.bin");
            copyFile(dtvsrcFile, dtvdestFile);
            LogUtils.d(TAG, "set factory dtv_cmdb_2.bin");
        }
    }


    public void resetall() {
        setDutFactorySelect();
        setFactoryMode(FACTORY_MODE_AUTOTEST);
        restoreToDefault();
        File file = new File("/data/data/mstar.factorymenu.ui/shared_prefs/EqNonlinear.xml");
        if (file.isFile() && file.exists()) {
            file.delete();
        }
        File file1 = new File("/data/data/mstar.factorymenu.ui/shared_prefs/OsdValue.xml");
        if (file1.isFile() && file1.exists()) {
            file1.delete();
        }
        File file2 = new File("/data/data/com.cultraview.ctvmenu/shared_prefs/OsdValue.xml");
        if (file2.isFile() && file2.exists()) {
            file2.delete();
        }
        if (enableDtv()) {
            setCustomerAtvCmdbBin();
            setCustomerDtvCmdbBin();
        } else {
            setCustomerDtvCmdbBin();
            setCustomerAtvCmdbBin();
        }
        setUartOnOff(true);


        AudioManager audioManager = (AudioManager) mainActivity.getSystemService(Context.AUDIO_SERVICE);
//        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,
//                SystemProperties.getInt("ro.sys.def.volume", 35), FLAG_SET_VOLUME_WITHOUT_UI);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 35, FLAG_SET_VOLUME_WITHOUT_UI);
        CtvCommonUtils.setCultraviewProjectInfo(mainActivity, "tbl_Configuration", "GuideMode", "1");
        Toast ctvreset = Toast
                .makeText(mainActivity, "RESET ALL OK!", Toast.LENGTH_LONG);
        ctvreset.setGravity(Gravity.TOP, 0, 0);
        ctvreset.show();
        handler.sendEmptyMessageDelayed(MSG_REBOOT_SYSTEM, 2000);
    }


    private void intoRecovery() {
        Intent resetIntent;
        if (android.os.Build.VERSION.RELEASE.equals("8.0.0")) {
            resetIntent = new Intent("android.intent.action.FACTORY_RESET");
            resetIntent.setPackage("android");
            resetIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            resetIntent.putExtra("android.intent.extra.REASON", "ResetConfirmFragment");
//            if (mainActivity.getIntent().getBooleanExtra("shutdown", false)) {
            //resetIntent.putExtra("shutdown", true);
//            }
        } else {
            resetIntent = new Intent("android.intent.action.MASTER_CLEAR");
            resetIntent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            resetIntent.putExtra("from", "restorefactory");
        }
        mainActivity.sendBroadcast(resetIntent);
    }


    public void setDutFactorySelect() {
        int pos = Integer.valueOf(CtvCommonUtils.getCultraviewProjectInfo(mainActivity,
                "tbl_Configuration", "OEMFactory"));
        switch (pos) {
            case 0:
                ctvProgramPreset();
                break;
            case 1:
                zjtlProgramPreset();
                break;
            case 2:
                gzycProgramPreset();
                break;
            case 3:
                tpvProgramPreset();
                break;
            case 4:
                tclProgramPreset();
                break;
            default:
                break;
        }
    }

    public void ctvProgramPreset() {
        /*
         * try { setCTVAtvCmdbBin(); setCTVDtvCmdbBin(); } catch
         * (RemoteException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         */
        SystemProperties.set("persist.sys.dutproduct", "0");
    }

    public void zjtlProgramPreset() {
        /*
         * try { setZJTLAtvCmdbBin(); setZJTLDtvCmdbBin(); } catch
         * (RemoteException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         */
        SystemProperties.set("persist.sys.dutproduct", "1");
    }

    public void gzycProgramPreset() {
        /*
         * try { setGZYCAtvCmdbBin(); setGZYCDtvCmdbBin(); } catch
         * (RemoteException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         */
        SystemProperties.set("persist.sys.dutproduct", "2");
    }

    public void tpvProgramPreset() {
        /*
         * try { setTPVAtvCmdbBin(); setTPVDtvCmdbBin(); } catch
         * (RemoteException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         */
        SystemProperties.set("persist.sys.dutproduct", "3");
    }

    public boolean restoreToDefault() {
        boolean result = false;
        File srcFile = new File("/tvconfig/TvBackup/Database/", "user_setting.db");
        File destFile = new File("/tvdatabase/Database/", "user_setting.db");

        result = copyFile(srcFile, destFile);
        if (result == false) {
            return false;
        }
        srcFile = new File("/tvconfig/TvBackup/Database/", "factory.db");
        destFile = new File("/tvdatabase/Database/", "factory.db");
        result = copyFile(srcFile, destFile);
        if (result == false) {
            return false;
        }
        return true;
    }

    public void tclProgramPreset() {
        /*
         * try { setTCLAtvCmdbBin(); setTCLDtvCmdbBin(); } catch
         * (RemoteException e) { // TODO Auto-generated catch block
         * e.printStackTrace(); }
         */
        SystemProperties.set("persist.sys.dutproduct", "4");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0: //Board Type
                continuousClick(COUNTS, DURATION);
                break;

            case 1:
                break;
            case 13:
            case 14:
                SystemBean systemBean = (SystemBean) list.get(position);
                if (systemBean.getTitle().equals("Shipping INIT")) {
                    resetSystem();
                }
                break;
        }
    }

    /**
     * 5次点击跳转到老工厂菜单
     *
     * @param count
     * @param time
     */
    private void continuousClick(int count, long time) {
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                intent.setComponent(new ComponentName("mstar.factorymenu.ui",
                        "mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity"));
                mainActivity.startActivity(intent);
                LogUtils.d("mainActivity fin");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((DesignMenuActivity) mainActivity).finish();
                    }
                }, 300);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mainActivity, "There is not the APP of " + "mstar.factorymenu.ui.hh",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * HDCP1
     *
     * @return
     */
    private String checkHDCP1xkey() {
        // TODO Auto-generated method stub
        String ret = null;
        File mHDCPKeyMstarFile = new File("/certificate/hdcp_key.bin");
        File mHDCPKeyCtvFile = new File("/factory/hdcp_key.bin");
        String iniFilePathFromEnv = "";
        String mHDCPKeyBuildIniFromEnvFile = null;
        try {
            iniFilePathFromEnv = CtvCommonManager.getInstance().getEnvironment("customer_ini_path");
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (iniFilePathFromEnv != null && !iniFilePathFromEnv.isEmpty()) {
            mHDCPKeyBuildIniFromEnvFile = getProfileString(iniFilePathFromEnv, "StorageHDCP",
                    "bEFUSEHdcpEnable", null);
        }

        String projectId = "";
        String mHDCPKeyBuildInIC = null;
        try {
            projectId = CtvCommonManager.getInstance().getEnvironment("project_id");
        } catch (CtvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (projectId != null && !projectId.isEmpty()) {
            String iniFilePath = "/tvconfig/config/model/Customer_"
                    + projectId.substring(projectId.length() - 1) + ".ini";
            mHDCPKeyBuildInIC = getProfileString(iniFilePath, "StorageHDCP", "bEFUSEHdcpEnable",
                    null);
        }
        if (mHDCPKeyBuildIniFromEnvFile != null && mHDCPKeyBuildIniFromEnvFile.equals("1")) {
            ret = mainActivity.getResources().getString(R.string.str_burn_ok) + "+2";

        } else if (mHDCPKeyBuildInIC != null && mHDCPKeyBuildInIC.equals("1")) {
            ret = mainActivity.getResources().getString(R.string.str_burn_ok) + "+2";

        } else if (mHDCPKeyCtvFile != null && mHDCPKeyCtvFile.exists()) {
            if ((getFileSize(mHDCPKeyCtvFile) > 100 && getFileSize(mHDCPKeyCtvFile) < 500)) {
                ret = mainActivity.getResources().getString(R.string.str_burn_ok) + "+1";

            } else {
                try {
                    if (CtvTvManager.getInstance().setTvosInterfaceCommand("CheckHdcp14Validity")) {
                        ret = mainActivity.getResources().getString(R.string.str_burn_ok)
                                + "+1";

                        return ret;
                    }
                } catch (CtvCommonException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                ret = mainActivity.getResources().getString(R.string.str_burn_error) + "+1";

            }
        } else if (mHDCPKeyMstarFile != null && mHDCPKeyMstarFile.exists()) {
            if ((getFileSize(mHDCPKeyMstarFile) > 100 && getFileSize(mHDCPKeyMstarFile) < 500)) {
                ret = mainActivity.getResources().getString(R.string.str_burn_ok);

            } else {
                ret = mainActivity.getResources().getString(R.string.str_burn_error);

            }
        } else {
            ret = mainActivity.getResources().getString(R.string.str_burn_ng);

        }

        return ret;
    }

    private long getFileSize(File file) {
        long size = 0;
        if (file.exists()) {
            try {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
                fis.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            LogUtils.e("UpdatHdcpKey", "file not exsit!");
        }
        LogUtils.d("UpdatHdcpKey", "file size = " + size);
        return size;
    }

    public String getProfileString(String file, String section, String variable, String defaultValue) {
        String strLine, value = "";
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            while ((strLine = bufferedReader.readLine()) != null) {
                strLine = removeIniComments(strLine);
                if (strLine.contains(section)) {
                    while ((strLine = bufferedReader.readLine()) != null) {
                        strLine = removeIniComments(strLine);
                        if (strLine.contains("[")) {
                            return defaultValue;
                        }
                        if (strLine.contains(variable)) {
                            strLine = strLine.trim();
                            String[] strArray = strLine.split("=");
                            if (strArray.length == 1) {
                                value = strArray[0].trim();
                                if (value.equalsIgnoreCase(variable)) {
                                    value = "";
                                    return value;
                                }
                            } else if (strArray.length == 2) {
                                value = strArray[0].trim();
                                if (value.equalsIgnoreCase(variable)) {
                                    value = strArray[1].trim();
                                    return value;
                                }
                            } else if (strArray.length > 2) {
                                value = strArray[0].trim();
                                if (value.equalsIgnoreCase(variable)) {
                                    value = strLine.substring(strLine.indexOf("=") + 1).trim();
                                    return value;
                                }
                            }
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // TODO: handle exception
        } catch (IOException e) {
            // TODO: handle exception
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return defaultValue;
    }

    private String removeIniComments(String source) {
        String result = source;
        if (result.contains(";")) {
            result = result.substring(0, result.indexOf(";"));
        }
        if (result.contains("#")) {
            result = result.substring(0, result.indexOf("#"));
        }
        return result.trim();
    }


    private String checkHDCP2key() {
        // TODO Auto-generated method stub
        String ret = null;
        File mHDCPKeyFile = new File("/factory/CtvHDCPKey.bin");
        if (mHDCPKeyFile != null && mHDCPKeyFile.exists()) {
            if ((getFileSize(mHDCPKeyFile) > 500 && getFileSize(mHDCPKeyFile) < 1500)) {
                ret = mainActivity.getResources().getString(R.string.str_burn_ok);
            } else {
                try {
                    if (CtvTvManager.getInstance().setTvosInterfaceCommand("CheckHdcpValidity")) {
                        ret = mainActivity.getResources().getString(R.string.str_burn_ok);
                        return ret;
                    }
                } catch (CtvCommonException e) {
                    e.printStackTrace();
                }
                ret = mainActivity.getResources().getString(R.string.str_burn_error);

            }
        } else {
            ret = mainActivity.getResources().getString(R.string.str_burn_ng);

        }

        return ret;
    }

    public void onDestroy() {
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        systemAdapter.changeSelected(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void clearOnFocus() {
        if (systemAdapter != null) {
            systemAdapter.changeSelected(-1);
        }
    }

    public void onHiddenChanged() {
        initData();
    }
}
