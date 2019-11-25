package com.protruly.floatwindowlib.ui;

import android.app.Activity;
import android.content.Context;
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

import com.apkfuns.logutils.LogUtils;
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

/**
 * Created by gaoyixiang on 2019/8/22.
 */

public class NewSignalDialogLayout extends FrameLayout {
    private Context mContext;
    private boolean isRightShow = false;
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
    CommonAdapter<NewSignalInfo> commonAdapter;
    List<NewSignalInfo> dataList;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_OUTSIDE){
            this.setVisibility(View.GONE);
        }
        return super.onTouchEvent(event);
    }

    private void initView() {
        // 获得数据
        dataList = DBHelper.getSourceNames(getContext());

        // 设置Adapter
        int gridMenuID = R.layout.signal_grid_item;
        commonAdapter = new CommonAdapter<NewSignalInfo>(mContext,
                gridMenuID, dataList) {
            @Override
            protected void convert(ViewHolder holder, NewSignalInfo signalInfo, int position) {
                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
                holder.setText(R.id.signal_text, signalInfo.getName());
            }
        };

        // 初始化
        GridView gridView = (GridView) findViewById(R.id.signal_grid_view);
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener(mOnItemClickListener);
    }

    /**
     * item事件监听
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        if (position < dataList.size()){
            NewSignalInfo signalInfo = dataList.get(position);
            int sourceIndex = signalInfo.getSourceId();

            if (sourceIndex >= 0){ // 切换到其他信号源
                AppUtils.changeSignal(mContext, sourceIndex);
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
