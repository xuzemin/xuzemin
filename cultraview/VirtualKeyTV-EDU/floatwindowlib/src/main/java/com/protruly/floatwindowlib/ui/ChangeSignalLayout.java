package com.protruly.floatwindowlib.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.IDHelper;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ViewUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Desc:更新APP弹框
 *
 * @author wang
 * @time 2017/4/13.
 */
public class ChangeSignalLayout extends FrameLayout {
    private Context mContext;

    // 宽和高
    public static int viewWidth;
    public static int viewHeight;
    public static String opsEnable = SystemProperties.get("opsinside.config","1");
	CommonAdapter<SignalInfo> commonAdapter;
	List<SignalInfo> dataList;

    public static Handler mHandler;

    public ChangeSignalLayout(Context context) {
        this(context, null);
    }

    public ChangeSignalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    private void init(){
        mHandler = new UIHandler(this);

        LayoutInflater.from(mContext).inflate(R.layout.popup_signal_change, this);
        initView();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        // 获得数据
	    //dataList = DBHelper.getSourceNames(getContext());
        dataList = new ArrayList<>();
        // 设置Adapter
        int gridMenuID = R.layout.signal_grid_item;
        commonAdapter = new CommonAdapter<SignalInfo>(mContext,
                gridMenuID, dataList) {
            @Override
            protected void convert(ViewHolder holder, SignalInfo signalInfo, int position) {
                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
                if(signalInfo.getSourceId() == 25 &&"0".equals(opsEnable)){
                    holder.setText(R.id.signal_text, "HDMI3");
                }else{
                    holder.setText(R.id.signal_text, signalInfo.getName());
                }
            }
        };

        // 初始化
        GridView gridView = (GridView) findViewById(R.id.signal_grid_view);
        gridView.setAdapter(commonAdapter);
        gridView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * item事件监听
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        if (ViewUtils.isFastDoubleClick()){
            return;
        }

        if (position < dataList.size()){
	        SignalInfo signalInfo = dataList.get(position);
            int sourceIndex = signalInfo.getSourceId();

            AppUtils.changeSource(getContext(), sourceIndex);
            LogUtils.d("position->%s, sourceIndex->%s", position, sourceIndex);
	        if (mContext instanceof Activity){
	            ((Activity)mContext).finish();
	        }
        }
    };

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<ChangeSignalLayout> weakReference;

        public UIHandler(ChangeSignalLayout activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            ChangeSignalLayout signalLayout = weakReference.get();
            if (signalLayout == null){
                return;
            }
        }
    }
}
