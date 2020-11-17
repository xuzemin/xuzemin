package mstar.factorymenu.ui.fragments.system;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cultraview.tv.CtvCommonManager;
import com.cultraview.tv.CtvTvManager;
import com.cultraview.tv.common.exception.CtvCommonException;
import com.mstar.android.storage.MStorageManager;
import com.mstar.android.tv.TvCommonManager;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mstar.factorymenu.ui.R;
import mstar.factorymenu.ui.adapter.SystemAdapter;
import mstar.factorymenu.ui.bean.SystemBean;
import mstar.factorymenu.ui.utils.LogUtils;
import mstar.factorymenu.ui.utils.Tools;
import mstar.factorymenu.ui.utils.USBUpgradeThread;
import mstar.tvsetting.factory.desk.FactoryDeskImpl;
import mstar.tvsetting.factory.desk.IFactoryDesk;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SystemUpgradeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemUpgradeFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, View.OnFocusChangeListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView imageView_uart_enable;
    private ImageView imageView_rs232_command;
    private IFactoryDesk factoryManager;
    private short watchDogMode;

    private String[] watchdogenable = {
            "Off", "On"
    };
    private ImageView imageViewWatchDog;
    private ArrayList list;
    private ListView listView;
    private String val_shipping = "Please Click!";
    private SystemAdapter soundVolOsdAdapter;
    private StorageManager mStorageManager;
    public List<Map<String, String>> MountedVolumes = new ArrayList<Map<String, String>>();
    public List<File> ScanFiles = new ArrayList<File>();

    public List<File> update_signed_file = new ArrayList<File>();

    private int UpgradeMainTip = 1;
    private final String mstarUpgradeName = "MstarUpgrade.bin";
    private final String updateSignedName = "update_signed.zip";
    private int panelUpgradeFileSize = 0;
    private String panelUpgradeFilePath = "";
    private String panelUpgradeFileName = "";
    private int typeCUpgradeFileSize = 0;
    private String typeUpgradeFileName = "";
    private String typeCUpgradeFilePath = "";

    private String hdmiOutUpgradeFilePath = "";
    private final String hdmiOutName = "hdmiout_h.bin";
    private final String typeCName = "typec_h.bin";


    private String uPath = "";
    private String unZipPath = "/HHT/";
    private static final String TVOS_INTERFACE_CMD_LED_BREATH_OFF = "LedBreathOff";
    private final BroadcastReceiver storageChangeReceiver = new BroadcastReceiver() {
        @SuppressLint("LongLogTag")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("action, " + action);
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                hasStorageDevice();
                scanUpdateFile();
            } else if (Intent.ACTION_MEDIA_EJECT.equals(action)) {
                hasStorageDevice();
                scanUpdateFile();
            }

        }
    };
    private File updateSignedFile;
    private ProgressDialog dialog;



    public SystemUpgradeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictrueModeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SystemUpgradeFragment newInstance(String param1, String param2) {
        SystemUpgradeFragment fragment = new SystemUpgradeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.system_information, container, false);
        factoryManager = FactoryDeskImpl.getInstance(getActivity());
        initViews(view);
        return view;
    }


    private void initViews(View view) {
        listView = view.findViewById(R.id.system_information_layout);
        list = new ArrayList();
        soundVolOsdAdapter = new SystemAdapter(getActivity(), R.layout.item_system, list);
        listView.setAdapter(soundVolOsdAdapter);
        soundVolOsdAdapter.changeSelected(-1);

        listView.setOnItemClickListener(this);
        listView.setOnItemSelectedListener(this);
        mStorageManager = (StorageManager) getActivity().getSystemService(Context.STORAGE_SERVICE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file");
        getActivity().registerReceiver(storageChangeReceiver, intentFilter);


        hasStorageDevice();
        scanUpdateFile();
        listView.setVisibility(View.VISIBLE);


    }


    /**
     * 更新System upgrade
     */
    private void setSystemUpgradeListData() {
        list.clear();
        if (!TextUtils.isEmpty(Tools.FindFileOnUSB(mstarUpgradeName))) {
            UpgradeMainTip = 0;
        } else {
            UpgradeMainTip = 1;
        }

        SystemBean soundVolOsdBean = new SystemBean();
        soundVolOsdBean.setTitle("Upgrade Main");
        soundVolOsdBean.setValue(val_shipping);
        soundVolOsdBean.setId(UpgradeMainTip);

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
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        switch (seekBar.getId()) {

        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFocusChange(View view, boolean b) {
        switch (view.getId()) {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        LogUtils.d("onItemClick--->" + i);
        switch (i) {
            case 0:
                if (ScanFiles.size() > 0) {
                    if (!TextUtils.isEmpty(Tools.FindFileOnUSB(mstarUpgradeName))) {
                        Tools.UpgradeMain();
                    } else {
                        Toast.makeText(getActivity(), "未检测到bin文件", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "未检测到U盘", Toast.LENGTH_SHORT).show();
                }
                break;
            case 1: //Upgrade Panel
                if (Tools.CheckUsbIsExist()) {
                    if (isUpgradeFileExists()) {
                        if (panelUpgradeFileSize <= 1) {
                            try {
                                uPath = USBUpgradeThread.FindUSB();
                                USBUpgradeThread.unzipFile(getActivity(), panelUpgradeFilePath, uPath + unZipPath, handler);
                            } catch (Exception e) {
                                LogUtils.e("Exception-->" + e);
                            }
                        } else {
                            Toast.makeText(getActivity(), "U盘中有多个版控文件", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getActivity(), "未检测到升级文件", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "未检测到U盘", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3: //update_signed
                if (update_signed_file.size() > 0) {
                    if (dialog == null) {
                        dialog = new ProgressDialog(getActivity());
                        dialog.setMessage("请稍后！");
                        dialog.setCancelable(false);
                        dialog.setIndeterminate(true);
                        dialog.show();
                    } else {
                        dialog.show();
                    }
                    new UpdateSystemThread().start();
                } else {
                    Toast.makeText(getActivity(), "未检测到U盘", Toast.LENGTH_SHORT).show();
                }

                break;

            case 6:
                if (Tools.CheckUsbIsExist()) {
                    if (isTypcCUpgradeFileExists()) {
                        if (dialog == null) {
                            dialog = new ProgressDialog(getActivity());
                            dialog.setMessage("Type-C 升级中，请勿操作！请勿断电！请勿拔插盘！");
                            dialog.setCancelable(false);
                            dialog.show();
                        } else {
                            dialog.setMessage("Type-C 升级中，请勿操作！请勿断电！请勿拔插盘！");
                            dialog.show();
                        }
                        USBUpgradeThread.UpgradeTypeC(handler, typeCUpgradeFilePath);
                        setDialogOnKeyListener();

                    } else {
                        Toast.makeText(getActivity(), "未检测到升级文件", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "未检测到U盘", Toast.LENGTH_SHORT).show();
                }
                break;

            case 7:
                if (Tools.CheckUsbIsExist()) {
                    if (isHdmiOutCUpgradeFileExists()) {
                        if (dialog == null) {
                            dialog = new ProgressDialog(getActivity());
                            dialog.setMessage("Hdmi Out 升级中，请勿操作！请勿断电！请勿拔插盘！");
                            dialog.setCancelable(false);
                            dialog.show();
                        } else {
                            dialog.setMessage("Hdmi Out 升级中，请勿操作！请勿断电！请勿拔插盘！");
                            dialog.show();
                        }
                        USBUpgradeThread.UpgradeHdmiOut(handler, hdmiOutUpgradeFilePath);
                        setDialogOnKeyListener();

                    } else {
                        Toast.makeText(getActivity(), "未检测到升级文件", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getActivity(), "未检测到U盘", Toast.LENGTH_SHORT).show();
                }
                break;
            case 8: //mac
                USBUpgradeThread.UpgradeMAC(handler);
                break;

            case 9://HDCP1
                USBUpgradeThread.writeHDCP14Key(getActivity());
                break;
            case 10: //HDCP2
                USBUpgradeThread.writeHDCP22Key(getActivity());
                break;
        }
    }
    private void setDialogOnKeyListener(){
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                LogUtils.d("dialog onKey return true ");
                return true;
            }
        });
    }
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == USBUpgradeThread.UPGRATE_START) {

            } else if (msg.what == USBUpgradeThread.UPGRATE_END_SUCCESS) {
                try {
                    CtvTvManager.getInstance().setEnvironment("db_table", "0");
                } catch (CtvCommonException e) {
                    e.printStackTrace();
                }
            } else if (msg.what == USBUpgradeThread.UPGRATE_END_SUCCESS_MAIN) {
                CtvCommonManager.getInstance().rebootSystem("reboot");
            } else if (msg.what == USBUpgradeThread.UPGRATE_END_FILE_NOT_FOUND) {
                Toast.makeText(getActivity(), "未找到U盘中烧录文件", Toast.LENGTH_SHORT).show();
            } else if (msg.what == USBUpgradeThread.UPGRATE_END_FIFL_ALREADY_UP_TO_DATE) {

            } else if (msg.what == USBUpgradeThread.UPGRATE_END_SUCCESS_MAC) {
                Toast.makeText(getActivity(), "烧录成功", Toast.LENGTH_SHORT).show();
            } else if (msg.what == USBUpgradeThread.UPGRATE_PANELOVER) {
                setEnv();
            } else if (msg.what == USBUpgradeThread.UPGRATE_TYPEC_FAILED) {
                Toast.makeText(getActivity(), "Type-c升级失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (msg.what == USBUpgradeThread.UPGRATE_TYPEC_SUCCESS) {
                Toast.makeText(getActivity(), "Type-c升级成功", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else if (msg.what == USBUpgradeThread.UPGRATE_HDMI_OUT_FAILED) {
                Toast.makeText(getActivity(), "Hdmi-out升级失败", Toast.LENGTH_SHORT).show();
                TvCommonManager.getInstance().setTvosCommonCommand(TVOS_INTERFACE_CMD_LED_BREATH_OFF);
                dialog.dismiss();
            } else if (msg.what == USBUpgradeThread.UPGRATE_HDMI_OUT_SUCCESS) {
                Toast.makeText(getActivity(), "Hdmi-out升级成功", Toast.LENGTH_SHORT).show();
                TvCommonManager.getInstance().setTvosCommonCommand(TVOS_INTERFACE_CMD_LED_BREATH_OFF);
                dialog.dismiss();
                CtvCommonManager.getInstance().rebootSystem("reboot");
            } else {
                Toast.makeText(getActivity(), "烧录失败", Toast.LENGTH_SHORT).show();
            }
        }

        ;
    };

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        soundVolOsdAdapter.changeSelected(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void clearOnFocus() {
        if (soundVolOsdAdapter != null) {
            soundVolOsdAdapter.changeSelected(-1);
        }
    }


    private class UpdateSystemThread extends Thread {

        @Override
        public void run() {
            super.run();
            verifyPackage();
        }
    }

    /**
     * update zip升级
     */
    private void verifyPackage() {
        RecoverySystem.ProgressListener progressListener = new RecoverySystem.ProgressListener() {
            @Override
            public void onProgress(int progress) {

            }
        };
        try {
            RecoverySystem.verifyPackage(updateSignedFile, progressListener, null);
            RecoverySystem.installPackage(getActivity(), updateSignedFile);
        } catch (Exception e) {
            LogUtils.e("verifyPackage exception, " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void scanUpdateFile() {
        ScanFiles.clear();
        update_signed_file.clear();
        //bin
        for (Map<String, String> map : MountedVolumes) {
            File UpdateFile = new File(map.get("volume_path"), mstarUpgradeName);
            if (UpdateFile.exists()) {
                ScanFiles.add(UpdateFile);
            }
        }
        // zip
        for (Map<String, String> map : MountedVolumes) {
            updateSignedFile = new File(map.get("volume_path"), updateSignedName);
            if (updateSignedFile.exists()) {
                update_signed_file.add(updateSignedFile);
            }
        }

        LogUtils.d("scanUpdateFile--->:" + ScanFiles.size());
        if (ScanFiles.size() > 0) {
            UpgradeMainTip = 0;
        } else {
            UpgradeMainTip = 1;
        }
        setSystemUpgradeListData();
        soundVolOsdAdapter.setData(list);
    }

    //查看是否有存储设备
    private boolean hasStorageDevice() {
        MountedVolumes.clear();
        MStorageManager sm = MStorageManager.getInstance(getActivity());
        StorageVolume[] volumeList = getVolumeList();
        for (StorageVolume volume : volumeList) {

            String path = null;//volume.getPath();
            try {
                path = Tools.getPath(volume);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Environment.MEDIA_MOUNTED.equals(sm.getVolumeState(path))) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("volume_path", path);
                String label = sm.getVolumeLabel(path);
                if (TextUtils.isEmpty(label)) {
                    if (Environment.getExternalStorageDirectory().getAbsolutePath().equals(path)) {
                        map.put("volume_lable", getActivity().getString(R.string.sdcard_lable));
                    } else {
                        map.put("volume_lable", getActivity().getString(R.string.mobile_stoarge_device));
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


    //反射getVolumeList()方法
    private StorageVolume[] getVolumeList() {
        StorageVolume[] mStorageVolume = null;
        try {
            Class cls = Class.forName("android.os.storage.StorageManager");
            Method getVolumeList = cls.getDeclaredMethod("getVolumeList");
            mStorageVolume = (StorageVolume[]) getVolumeList.invoke(mStorageManager);
            LogUtils.d("getVolumeList");
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d(e.getMessage());
        }
        return mStorageVolume;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(storageChangeReceiver);
        super.onDestroy();
    }

    /**
     * 判断升级文件是否存在
     *
     * @return
     */
    public boolean isUpgradeFileExists() {
        panelUpgradeFileSize = 0;
        panelUpgradeFileName = "";
        String filepath = USBUpgradeThread.FindFolderOnUSB();
        LogUtils.d("isUpgradeFileExists filepath:" + filepath);
        File[] files = new File(filepath).listFiles();
        for (int sdx = 0; sdx < files.length; sdx++) {
            String name = files[sdx].getName();
            LogUtils.d("Upgrade name:" + name);
            if (Tools.getFormatName(name)) {
                panelUpgradeFilePath = files[sdx].getPath();
                panelUpgradeFileName = files[sdx].getName();
                if (!files[sdx].isHidden()) {
                    ++panelUpgradeFileSize;
                }
                LogUtils.d("Upgrade find zip path is" + panelUpgradeFilePath + "name is :" + panelUpgradeFileName);

            }

        }
        if (panelUpgradeFileSize > 0) {
            return true;
        }

        return false;
    }


    /**
     * 判断升级文件是否存在
     *
     * @return
     */
    public boolean isTypcCUpgradeFileExists() {
        typeCUpgradeFileSize = 0;
        typeUpgradeFileName = "";
        typeCUpgradeFilePath = "";
        String filepath = USBUpgradeThread.FindUSB();
        LogUtils.d("isTypcCUpgradeFileExists filepath:" + filepath);
        File[] files = new File(filepath).listFiles();
        for (int sdx = 0; sdx < files.length; sdx++) {
            String name = files[sdx].getName();
            LogUtils.d("Upgrade name:" + name);
            if (name.equals(typeCName)) {
                typeCUpgradeFilePath = files[sdx].getPath();
                typeUpgradeFileName = files[sdx].getName();
                if (!files[sdx].isHidden()) {
                    return true;
                }
                LogUtils.d("Upgrade find zip path is" + typeCUpgradeFilePath + "name is :" + typeUpgradeFileName);

            }
        }
        return false;
    }

    /**
     * 判断升级文件是否存在
     *
     * @return
     */
    public boolean isHdmiOutCUpgradeFileExists() {
        hdmiOutUpgradeFilePath = "";
        String filepath = USBUpgradeThread.FindUSB();
        LogUtils.d("isTypcCUpgradeFileExists filepath:" + filepath);
        File[] files = new File(filepath).listFiles();
        for (int sdx = 0; sdx < files.length; sdx++) {
            String name = files[sdx].getName();
            LogUtils.d("Upgrade name:" + name);
            if (name.equals(hdmiOutName)) {
                hdmiOutUpgradeFilePath = files[sdx].getPath();
                return true;
            }
        }
        return false;
    }


    /**
     * 更新版控文件后设置环境变量
     */
    private void setEnv() {
        if (!TextUtils.isEmpty(panelUpgradeFileName)) {
            String substring = panelUpgradeFileName.substring(0, panelUpgradeFileName.lastIndexOf("."));
            String[] listName = substring.split("_");
            String panelModel = listName[2];
            LogUtils.d("panelModel is " + panelModel);
            try {
                CtvTvManager.getInstance().setEnvironment("panelModel", panelModel);
            } catch (CtvCommonException e) {
                e.printStackTrace();
            }
        }
        if (!TextUtils.isEmpty(uPath)) {
            File file = new File(uPath + "/UpgradePanel/UpdateProductModel.txt");
            if (file.exists()) {
                LogUtils.d("UpdateProductModel " + panelUpgradeFileName.split("_")[1]);
                SystemProperties.set("persist.product.model", panelUpgradeFileName.split("_")[1]);
                try {
                    CtvTvManager.getInstance().setEnvironment("productModel", panelUpgradeFileName.split("_")[1]);
                } catch (CtvCommonException e) {
                    e.printStackTrace();
                }
            }
        }
        String path = uPath + unZipPath + "singleMode.txt";
        LogUtils.d("singleMode path :" + path);
        File file = new File(path);
        if (file.exists()) {
            String fileContent = Tools.getFileContent(file);
            LogUtils.d("singleMode txt is " + fileContent);
            try {
                CtvTvManager.getInstance().setEnvironment("singleMode", fileContent.trim());
            } catch (CtvCommonException e) {
                e.printStackTrace();
            }
        }

        try {
            CtvTvManager.getInstance().setEnvironment("db_table", "0");
        } catch (CtvCommonException e) {
            e.printStackTrace();
        }
        CtvCommonManager.getInstance().rebootSystem("reboot");
    }
}
