package com.protruly.floatwindowlib.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;

import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.ActivityCollector;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.NewSignalInfo;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

public class PictureChangeActivity extends Activity {
    CommonAdapter<NewSignalInfo> commonAdapter;
    List<NewSignalInfo> dataList;
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
        dataList = DBHelper.getSourceNames(getApplicationContext());
        // 设置Adapter
        int gridMenuID = R.layout.signal_grid_dialog_item;
        commonAdapter = new CommonAdapter<NewSignalInfo>(getApplicationContext(),
                gridMenuID, dataList) {
            @Override
            protected void convert(ViewHolder holder, NewSignalInfo signalInfo, int position) {
                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
                holder.setText(R.id.signal_text, signalInfo.getName());
            }
        };
        // 初始化
        GridView gridView = findViewById(R.id.signal_grid_dilog_view);
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_TV_INPUT:
                finish();
                return true;
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
        if (position < dataList.size()){
            NewSignalInfo signalInfo = dataList.get(position);
            int sourceIndex = signalInfo.getSourceId();

            if (sourceIndex >= 0){ // 切换到其他信号源
                AppUtils.changeSignal(getApplicationContext(), sourceIndex);
            }
            finish();
        }

    };

}

