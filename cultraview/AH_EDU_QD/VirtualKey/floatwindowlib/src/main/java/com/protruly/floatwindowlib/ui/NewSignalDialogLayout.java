package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.content.Intent;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvCommonManager;
import com.hht.android.sdk.device.HHTCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.protruly.floatwindowlib.R;
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

/**
 * Created by gaoyixiang on 2019/8/22.
 */

public class NewSignalDialogLayout extends FrameLayout {
    private Context mContext;
    private boolean isRightShow = false;
    private static int currentNumber = -1;
    GridView gridView;
    public NewSignalDialogLayout(@NonNull Context context) {
        super(context);
        this.mContext=context;
        init();
    }

    public NewSignalDialogLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        init();
    }

    public NewSignalDialogLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        init();
    }
    public void setRightShow(boolean rightShow) {
        isRightShow = rightShow;
    }
    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.dialog_signals, this);
        refreshData();

    }
    public CommonAdapter<NewSignalInfo> commonAdapter;
    List<NewSignalInfo> dataList,getDataList;
    int gridMenuID;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_OUTSIDE){
            this.setVisibility(View.GONE);
        }
        return super.onTouchEvent(event);
    }

    public void refreshData(){
        getDataList = new ArrayList<>();
        dataList = DBHelper.getSourceNames(getContext());
        // 设置Adapter
        gridMenuID = R.layout.signal_grid_item;
        int currentSource = CtvCommonManager.getInstance().getCurrentTvInputSource();
        if (getBoard().equals("CV8386H_MH")){
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
        }else { // AH
            if (currentSource == 23){
                try {
                    // HDMI_SEL Status
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
//            int[] list = new int[]{1,28,23,9,26,25,24,2,16,0};
            sourceChannel = new int[list.length];
            for (int m = 0;m < list.length;m++) {
                sourceChannel[m] = Integer.parseInt(list[m]);
                if(sourceChannel[m] == currentSource){
                    currentNumber = m+1;
                }
            }
        }

        // 默认选择主页
        if (currentNumber == -1 || currentSource >= 34){
            currentNumber = 0;
        }
        LogUtils.d("currentSource = " + currentSource + ", selected Index=" + currentNumber);
        NewSignalInfo signalInfo = new NewSignalInfo(34, R.mipmap.btn_home, "HOME");
        signalInfo.setXmlIndex(dataList.size());
        getDataList.add(signalInfo);
        getDataList.addAll(dataList);
        commonAdapter = new CommonAdapter<NewSignalInfo>(getContext().getApplicationContext(),
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
        gridView = findViewById(R.id.signal_grid_view);
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

    /**
     * item事件监听
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        if(getBoard().equals("CV8386H_MH")) {
            changeSingalMH(position);
        }else if(getBoard().equals("CV8386H_AH")){
            changeSingalAH(position);
        }
        NewSignalDialogLayout.this.setVisibility(View.GONE);
        // 在PC界面时，收起菜单
        // 收缩菜单
        ControlMenuLayout controlMenu = isRightShow ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
        if ((controlMenu != null)) {
            controlMenu.shrinkMenu();
        }
    };

    public void changeSingalAH(int position){
        if (position < getDataList.size()) {
            if (position == 0) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                mContext.startActivity(intent);
            } else {
                NewSignalInfo signalInfo = dataList.get(position - 1);
                int sourceIndex = signalInfo.getSourceId();
                Log.i("CommonCommandsourceInde", "sourceIndex:" + sourceIndex);
                if (sourceIndex >= 0) { // 切换到其他信号源
                    AppUtils.changeSignal(getContext().getApplicationContext(), sourceIndex);
                    // 发送SOURCE广播
                    AppUtils.noticeChangeSignal(getContext().getApplicationContext(), sourceIndex);
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
                mContext.startActivity(intent);
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
                    AppUtils.changeSignal(getContext().getApplicationContext(), sourceIndex);
                    // 发送SOURCE广播
                    AppUtils.noticeChangeSignal(getContext().getApplicationContext(), sourceIndex);
                }
            }
        }
    }

}
