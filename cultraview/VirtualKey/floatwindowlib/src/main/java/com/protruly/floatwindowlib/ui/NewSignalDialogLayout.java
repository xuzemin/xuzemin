package com.protruly.floatwindowlib.ui;

import android.app.Activity;
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

import com.apkfuns.logutils.LogUtils;
import com.cultraview.tv.CtvCommonManager;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.NewSignalInfo;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.IDHelper;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import static android.security.KeyStore.getApplicationContext;

/**
 * Created by gaoyixiang on 2019/8/22.
 */

public class NewSignalDialogLayout extends FrameLayout {
    private Context mContext;
    private boolean isRightShow = false;
    private int currentNumber = -1;
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
        initView();

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
        int[] port;
        if(currentSource == 23){
            port = AppUtils.getCommand("GetTIPORT");
            if(port!=null && port.length>0){
                if(port[0] == 2){
                    currentSource = 24;
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
    }

    private void initView() {
        // 获得数据
        refreshData();
        commonAdapter = new CommonAdapter<NewSignalInfo>(mContext,
                gridMenuID, getDataList) {
            @Override
            protected void convert(ViewHolder holder, NewSignalInfo signalInfo, int position) {
//                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
//                holder.setText(R.id.signal_text, signalInfo.getName());
                holder.setVisible(R.id.signal_select,false);
                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
                holder.setText(R.id.signal_text, signalInfo.getName());
                if(position ==  currentNumber && position!=0){
                    holder.setVisible(R.id.signal_select,true);
                }
            }
        };

        // 初始化
        gridView = (GridView) findViewById(R.id.signal_grid_view);
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
//        if (position < dataList.size()){
//            NewSignalInfo signalInfo = dataList.get(position);
//            int sourceIndex = signalInfo.getSourceId();
//
//            if (sourceIndex >= 0){ // 切换到其他信号源
//                AppUtils.changeSignal(mContext, sourceIndex);
//            }
//        }
        if (position < getDataList.size()){
            if(position == 0){
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                mContext.startActivity(intent);
            }else {
                NewSignalInfo signalInfo = dataList.get(position - 1);
                int sourceIndex = signalInfo.getSourceId();

//                if (sourceIndex >= 0) { // 切换到其他信号源
//                    AppUtils.changeSignal(getApplicationContext(), sourceIndex);
//                }
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
//                finish();
            }
        }

        NewSignalDialogLayout.this.setVisibility(View.GONE);


        // 在PC界面时，收起菜单
        // 收缩菜单
        ControlMenuLayout controlMenu = isRightShow ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
        if ((controlMenu != null)) {
            controlMenu.shrinkMenu();
        }
    };

}
