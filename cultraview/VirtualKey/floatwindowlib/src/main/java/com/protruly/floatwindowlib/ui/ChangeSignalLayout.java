package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;
import com.apkfuns.logutils.LogUtils;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.activity.SettingActivity;
import com.protruly.floatwindowlib.activity.SettingNewActivity;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.IDHelper;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

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

    private int[] inputSourceIntList;// 信号源切换的list值

    public ChangeSignalLayout(Context context) {
        this(context, null);
    }

    public ChangeSignalLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.popup_signal_change, this);
        initView();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        // 获得信号源参数值
        inputSourceIntList = getResources().getIntArray(R.array.input_source_int_list);

        // 获得数据
        List<SignalInfo> dataList = new ArrayList<>();
        String[] inputSourceStrList = getResources().getStringArray(R.array.input_source_str_list);
        String[] inputSourceNameList = getResources().getStringArray(R.array.input_source_name_list);
        int len = inputSourceStrList.length;

        SignalInfo signalInfo = null;
        int resId = 0;
        String suffix = "_normal";
        for (int i =0; i < len; i++){
            resId = IDHelper.getDrawable(mContext, inputSourceStrList[i] + suffix);

            signalInfo = new SignalInfo(resId, inputSourceNameList[i]);
            dataList.add(signalInfo);
        }

        // 设置Adapter
        int gridMenuID = R.layout.signal_grid_item;
        CommonAdapter<SignalInfo> commonAdapter = new CommonAdapter<SignalInfo>(mContext,
                gridMenuID, dataList) {
            @Override
            protected void convert(ViewHolder holder, SignalInfo signalInfo, int position) {
                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
                holder.setText(R.id.signal_text, signalInfo.getName());
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
        if(position == 0){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addCategory(Intent.CATEGORY_HOME);
            mContext.startActivity(intent);
        }else {
            if (position < inputSourceIntList.length) {
                int source = inputSourceIntList[position-1];
                Log.i("CommonCommandsourceInde", "source:" + source);
                if (source > 0) { // 切换到其他信号源
                    if (source == 23) {
                        AppUtils.sendCommand("SetTIPORT0");
                    } else if (source == 24) {
                        AppUtils.sendCommand("SetTIPORT2");
                        source = 23;
                    } else if (source == 26) {
                        AppUtils.sendCommand("SetTIPORT3");
                        source = 23;
                    } else if (source == 16) {
                        AppUtils.sendCommand("SetVGA0");
                        source = 0;
                    } else if (source == 0) {
                        AppUtils.sendCommand("SetVGA1");
                    }
                    AppUtils.changeSignal(mContext, source);
                }

                // 发送SOURCE广播
                AppUtils.noticeChangeSignal(getContext(), source);
            }
        }

        // 退出设置界面
        if ((mContext != null)
                && (mContext instanceof SettingNewActivity)) {
            boolean isRight = ((SettingNewActivity) mContext).isRight();

            // 在PC界面时，收起菜单
            // 收缩菜单
            ControlMenuLayout controlMenu = isRight ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
            if ((controlMenu != null)){
                controlMenu.shrinkMenu();
            }

            ((SettingNewActivity) mContext).finish();
        }
    };
}
