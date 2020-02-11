package com.protruly.floatwindowlib.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvCommonManager;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.ActivityCollector;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.NewSignalInfo;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

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
//        WindowManager mWindowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
//
//        // 初始化布局参数
//        int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
//        getWindow().getAttributes().x = screenWidth - 2000;
        getWindow().getAttributes().gravity = Gravity.RIGHT|Gravity.TOP;
        setContentView(R.layout.activity_signal_change);
        initView();

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
        int[] port;
        if(currentSource == 23){
            port = AppUtils.getCommand("GetTIPORT");
            if(port!=null && port.length>0){
                if(port[0] == 2){
                    currentSource = 9;
                }else if(port[0] == 3){
                    currentSource = 26;
                }
            }
        }else if(currentSource == 0){
            port = AppUtils.getCommand("GetVGA");
            if(port!=null && port.length>0){
                if(port[0] == 1){
                    currentSource = 16;
                }
            }
        }
        String sourcelist = SystemProperties.get("ro.build.source.list");
        int[] sourceChannel = new int[0];
        if(sourcelist != null && !sourcelist.equals("")) {
            String[] list = sourcelist.split(",");
            sourceChannel = new int[list.length];
            for (int m = 0;m < list.length;m++) {
//				Log.e("sourcelist","sourcelist = " + list[m]);
                sourceChannel[m] = Integer.parseInt(list[m]);
                if(sourceChannel[m] == currentSource){
                    currentNumber = m+1;
                    dataList.get(m).setSelected(true);
                }
            }
//			Log.e("sourcelist","sourcelist = " + Arrays.toString(sourceChannel));
        }

        NewSignalInfo signalInfo = new NewSignalInfo(34, R.mipmap.btn_home, "HOME");
        signalInfo.setXmlIndex(dataList.size());
        if(currentSource == 34){
            signalInfo.setSelected(true);
            currentNumber = 0;
        }else{
            signalInfo.setSelected(false);
        }
//        dataList.add(signalInfo);
        getDataList.add(signalInfo);
        getDataList.addAll(dataList);

        LogUtils.e("currentSource"+currentSource);
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
            LogUtils.e("gridView.getDataList()"+getDataList.size() );
            LogUtils.e("gridView.getLastVisiblePosition()"+gridView.getLastVisiblePosition());
            LogUtils.e("gridView.getFirstVisiblePosition()"+gridView.getFirstVisiblePosition());
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
                    if(gridView.getLastVisiblePosition() == getDataList.size() - 1 ){
                        gridView.setSelection(0);
                    }
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
        MyUtils.checkUSB(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyUtils.checkUSB(false);
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
        if (position < getDataList.size()){
            if(position == 0){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }else {
                NewSignalInfo signalInfo = dataList.get(position - 1);
                int sourceIndex = signalInfo.getSourceId();
                Log.i("CommonCommandsourceInde","sourceIndex:"+sourceIndex);
                if (sourceIndex >= 0) { // 切换到其他信号源
                    if(sourceIndex == 23){
                        AppUtils.sendCommand("SetTIPORT0");
                    }else if(sourceIndex == 9){
                        AppUtils.sendCommand("SetTIPORT2");
                        sourceIndex = 23;
                    }else if(sourceIndex == 26){
                        AppUtils.sendCommand("SetTIPORT3");
                        sourceIndex = 23;
                    }else if(sourceIndex == 16){
                        AppUtils.sendCommand("SetVGA0");
                        sourceIndex = 0;
                    }else if(sourceIndex == 0){
                        AppUtils.sendCommand("SetVGA1");
                    }
                    AppUtils.changeSignal(getApplicationContext(), sourceIndex);
                    // 发送SOURCE广播
                    AppUtils.noticeChangeSignal(getApplicationContext(), sourceIndex);
                }

                finish();
            }
        }

    };

}

