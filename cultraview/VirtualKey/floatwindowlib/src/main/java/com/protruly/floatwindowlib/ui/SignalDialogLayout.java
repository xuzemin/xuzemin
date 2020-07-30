package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.apkfuns.logutils.LogUtils;
import com.mstar.android.tvapi.common.TvManager;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.activity.SettingActivity;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.IDHelper;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Desc:信号源弹框
 *
 * @author wang
 * @time 2017/4/13.
 */
public class SignalDialogLayout extends FrameLayout {
    private Context mContext;

    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private int[] inputSourceIntList;// 信号源切换的list值

    private boolean isRightShow = false; // 是否在右边弹出信号源

    public SignalDialogLayout(Context context) {
        this(context, null);
    }

    public SignalDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.dialog_signal, this);
        initView();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_OUTSIDE){
            this.setVisibility(View.GONE);
        }
        return super.onTouchEvent(event);
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
        String[] inputSourceNameList;
        if(MyUtils.getBoard().equals("CV8386H_MH")){
            inputSourceNameList = getResources().getStringArray(R.array.input_source_name_list_mh);
        }else{
            inputSourceNameList = getResources().getStringArray(R.array.input_source_name_list_ah);
        }

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
        int gridMenuID = R.layout.new_signal_grid_item;
        CommonAdapter<SignalInfo> commonAdapter = new CommonAdapter<SignalInfo>(mContext,
                gridMenuID, dataList) {
            @Override
            protected void convert(ViewHolder holder, SignalInfo signalInfo, int position) {
//                holder.setImageResource(R.id.signal_image, signalInfo.getImageId());
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

    public void setRightShow(boolean rightShow) {
        isRightShow = rightShow;
    }

    /**
     * item事件监听
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        if (position < inputSourceIntList.length){
            int source = inputSourceIntList[position];

            if (source >= 0){ // 切换到其他信号源
                AppUtils.changeSignal(mContext, source);
                if(position==1){
                    Intent intent =new Intent();
                    intent.setAction("android.intent.action.OPS_BOOT");
                    mContext.sendBroadcast(intent);
                }
            }

            // 发送SOURCE广播
            AppUtils.noticeChangeSignal(getContext(), source);
        }

        SignalDialogLayout.this.setVisibility(View.GONE);


        // 在PC界面时，收起菜单
        // 收缩菜单
        ControlMenuLayout controlMenu = isRightShow ? FloatWindowManager.getMenuWindow() : FloatWindowManager.getMenuWindowLeft();
        if ((controlMenu != null)) {
            controlMenu.shrinkMenu();
        }
    };
}
