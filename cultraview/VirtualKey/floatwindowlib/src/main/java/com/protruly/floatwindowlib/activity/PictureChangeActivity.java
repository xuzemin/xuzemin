package com.protruly.floatwindowlib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvCommonManager;
import com.hht.android.sdk.device.HHTCommonManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.ActivityCollector;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.NewSignalInfo;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import java.util.ArrayList;
import java.util.List;

import static com.protruly.floatwindowlib.utils.MyUtils.getBoard;

public class PictureChangeActivity extends Activity {
    CommonAdapter<NewSignalInfo> commonAdapter;
    List<NewSignalInfo> dataList,getDataList;
    GridView gridView;

    long uptime = 0;
    int Key_Code = -1;
    long time_bak = 0;
    long TIME_CHECK = 1000;
    private int currentNumber = -1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().gravity = Gravity.RIGHT|Gravity.TOP;
        setContentView(R.layout.activity_signal_change);
    }

    /**
     * 初始化UI
     */
    private void initView() {
        getDataList = new ArrayList<>();
        dataList = DBHelper.getSourceNames(getApplicationContext());
        // 设置Adapter
        int gridMenuID = R.layout.signal_grid_dialog_item;
        int currentSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        if (getBoard().equals("CV8386H_MH")) {
            int[] port;
            if (currentSource == 23) {
                port = AppUtils.getCommand("GetTIPORT");
                if (port != null && port.length > 0) {
                    if (port[0] == 2) {
                        currentSource = 9;
                    } else if (port[0] == 3) {
                        currentSource = 26;
                    }
                }
            } else if (currentSource == 0) {
                port = AppUtils.getCommand("GetVGA");
                if (port != null && port.length > 0) {
                    if (port[0] == 1) {
                        currentSource = 16;
                    }
                }
            }
        } else { // AH
            if (currentSource == 23){
                try {
                    // HDMI_SEL state
                    int hdmiSel = TvManager.getInstance().getGpioDeviceStatus(0x64);
                    LogUtils.d("currentSource hdmiSel= " + hdmiSel);
                    if (hdmiSel == 1){
                        currentSource = 26;
                    } else {
                        currentSource = 23;
                    }
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        }

        // 默认选择主页
        if (currentNumber == -1 || currentSource >= 34){
            currentNumber = 0;
        }
        LogUtils.d("currentSource = " + currentSource + ", selected Index=" + currentNumber);
        String sourcelist = SystemProperties.get("ro.build.source.list");
        int[] sourceChannel;
        if(sourcelist != null && !sourcelist.equals("")) {
            String[] list = sourcelist.split(",");
            sourceChannel = new int[list.length];
            for (int m = 0;m < list.length;m++) {
				Log.e("sourcelist","sourcelist = " + list[m]);
                sourceChannel[m] = Integer.parseInt(list[m]);
                if(sourceChannel[m] == currentSource){
                    currentNumber = m+1;
                }
            }
        }

        NewSignalInfo signalInfo = new NewSignalInfo(34, R.mipmap.btn_home, "HOME");
        signalInfo.setXmlIndex(dataList.size());
        getDataList.add(signalInfo);
        getDataList.addAll(dataList);

        commonAdapter = new CommonAdapter<NewSignalInfo>(getApplicationContext(),
                gridMenuID, getDataList) {
            @Override
            protected void convert(ViewHolder holder, NewSignalInfo signalInfo, int position) {
                holder.setVisible(R.id.signal_select,false);
                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
                holder.setText(R.id.signal_text, signalInfo.getName());
                if(position ==  currentNumber && position!=0){
                    holder.setVisible(R.id.signal_select,true);
                }
            }
        };
        // 初始化
        gridView = findViewById(R.id.signal_grid_dilog_view);
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener(mOnItemClickListener);
        LinearLayout linearLayout = findViewById(R.id.more);
        linearLayout.setOnClickListener(v -> {
            if(gridView.getLastVisiblePosition() < getDataList.size() - 1 - 5){
                gridView.setSelection(gridView.getFirstVisiblePosition() + 4);
            }else if(gridView.getLastVisiblePosition() < getDataList.size() - 1
                    && gridView.getLastVisiblePosition() >= getDataList.size() - 1 - 5){
                gridView.setSelection(getDataList.size() - 5);
            }else{
                gridView.setSelection(0);
            }
            gridView.requestFocusFromTouch();
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_TV_INPUT:
                finish();
                return true;
            case KeyEvent.KEYCODE_2:
                time_bak = System.currentTimeMillis();
                Key_Code = keyCode;
                uptime = time_bak;
                return true;
            case KeyEvent.KEYCODE_5:
                time_bak = System.currentTimeMillis();
                if(Key_Code == KeyEvent.KEYCODE_2 && uptime >= time_bak - TIME_CHECK){
                    Key_Code = keyCode;
                    uptime = time_bak;
                }else{
                    uptime = 0;
                }
                return true;
            case KeyEvent.KEYCODE_8:
                time_bak = System.currentTimeMillis();
                if(Key_Code == KeyEvent.KEYCODE_5 && uptime >= time_bak - TIME_CHECK){
                    Key_Code = keyCode;
                    uptime = time_bak;
                }else{
                    uptime = 0;
                }
                return true;
            case KeyEvent.KEYCODE_0:
                time_bak = System.currentTimeMillis();
                if(Key_Code == KeyEvent.KEYCODE_8 && uptime >= time_bak - TIME_CHECK){
                    AppUtils.gotoOtherApp(this,"mstar.factorymenu.ui","mstar.tvsetting.factory.ui.designmenu.DesignMenuActivity");
                    this.finish();
                }
                Key_Code = -1;
                uptime = 0;

                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if(gridView.hasFocus()){
//                    if(gridView.getLastVisiblePosition() == getDataList.size() - 1 ){
//                        gridView.setSelection(0);
//                    }
                    return true;
                }
                break;
            default:
                Key_Code = -1;
                uptime = 0;
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        ActivityCollector.addActivity(this);
        FloatWindowManager.removeSignalDialog(getApplicationContext());
        FloatWindowManager.removeSettingsDialog(getApplicationContext());
        FloatWindowManager.removeThmometerWindow(getApplicationContext());
        FloatWindowManager.removeDownloadWindow(getApplicationContext());
    }

    /**
     * item事件监听
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        if(getBoard().equals("CV8386H_MH")) {
            changeSingalMH(position);
        }else if(getBoard().equals("CV8386H_AH")){
            changeSingalAH(position);
        }
        finish();
    };

    public void changeSingalAH(int position){
        if (position < getDataList.size()) {
            if (position == 0) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                NewSignalInfo signalInfo = dataList.get(position - 1);
                int sourceIndex = signalInfo.getSourceId();
                Log.i("CommonCommandsourceInde", "sourceIndex:" + sourceIndex);
                if (sourceIndex >= 0) { // 切换到其他信号源
                    AppUtils.changeSignal(getApplicationContext(), sourceIndex);
                    // 发送SOURCE广播
                    AppUtils.noticeChangeSignal(getApplicationContext(), sourceIndex);
                }
            }
        }
    }

    public void changeSingalMH(int position){
        if (position < getDataList.size()) {
            if (position == 0) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                NewSignalInfo signalInfo = dataList.get(position - 1);
                int sourceIndex = signalInfo.getSourceId();
                Log.i("CommonCommandsourceInde", "sourceIndex:" + sourceIndex);
                if (sourceIndex >= 0) { // 切换到其他信号源
                    if (sourceIndex == 23) {
                        AppUtils.sendCommand("SetTIPORT0");
                    } else if (sourceIndex == 9) {
                        AppUtils.sendCommand("SetTIPORT2");
                        sourceIndex = 23;
                    } else if (sourceIndex == 26) {
                        AppUtils.sendCommand("SetTIPORT3");
                        sourceIndex = 23;
                        //MyUtils.resetIO();
                    } else if (sourceIndex == 16) {
                        AppUtils.sendCommand("SetVGA1");
                    } else if (sourceIndex == 0) {
                        AppUtils.sendCommand("SetVGA0");
                    }
                    AppUtils.changeSignal(getApplicationContext(), sourceIndex);
                    // 发送SOURCE广播
                    AppUtils.noticeChangeSignal(getApplicationContext(), sourceIndex);
                }
            }
        }
    }
}

