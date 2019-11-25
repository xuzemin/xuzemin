package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.been.AppInfo;
import com.protruly.floatwindowlib.utils.ApkInfoUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;
import com.zhy.http.okhttp.utils.L;

import java.util.List;

/**
 * Created by gaoyixiang on 2019/5/20.
 */

public class SelectAppDialog extends FrameLayout {

    private ApkInfoUtils apkInfoUtils;
    private List<AppInfo> dataList;
    public Callback mCallback;
    private Context mContext;
    private GridView gridView;
    public SelectAppDialog(@NonNull Context context) {
        super(context);
        this.mContext=context;
        init();
    }

    public SelectAppDialog(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        init();
    }

    public SelectAppDialog(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        init();
    }
    interface Callback {
        void selected(AppInfo appInfo);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            L.e("position"+position);
            if(position < dataList.size()){
                AppInfo appInfo = dataList.get(position);
                if (appInfo != null){
                    if(mCallback != null){
                        mCallback.selected(appInfo);
                        SPUtil.saveData(getContext(), CommConst.USERED_PACKAGE_NAME, appInfo.getPackName());
                    }
                }
            }
        }
    };

    private void init(){
        apkInfoUtils = new ApkInfoUtils();
        LayoutInflater.from(mContext).inflate(R.layout.dialog_apps, this);
        initView();
    }

    private void initView() {
        dataList = apkInfoUtils.scanInstallApp(mContext);
        int item_app = R.layout.item_app;
        CommonAdapter<AppInfo> commonAdapter=new CommonAdapter<AppInfo>(mContext,item_app,dataList) {
            @Override
            protected void convert(ViewHolder viewHolder, AppInfo appInfo, int position) {
                viewHolder.setText(R.id.text,appInfo.appName);
                viewHolder.setImageDrawable(R.id.image,appInfo.appIcon);
            }
        };

        gridView = findViewById(R.id.signal_grid_view);
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener((adapterView, view, position, l) -> {
            L.e("position"+position);
            if(position < dataList.size()){
                AppInfo appInfo = dataList.get(position);
                if (appInfo != null){
                    if(mCallback != null){
                        mCallback.selected(appInfo);
                        SPUtil.saveData(getContext(), CommConst.USERED_PACKAGE_NAME, appInfo.getPackName());
                    }
                }
            }
        });
    }

    public void setCallBack(Callback mCallBack){
        this.mCallback=mCallBack;
    }
}
