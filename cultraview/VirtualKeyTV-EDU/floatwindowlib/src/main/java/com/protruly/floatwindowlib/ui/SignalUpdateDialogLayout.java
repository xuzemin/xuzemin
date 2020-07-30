package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.entity.SignalInfo;
import com.protruly.floatwindowlib.helper.DBHelper;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * Desc:信号源弹框
 *
 * @author wang
 * @time 2017/4/13.
 */
public class SignalUpdateDialogLayout extends FrameLayout {
    private Context mContext;

    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private int[] sourceIdList;// 信号源切换的list值

    private Callback mCallback;

    private View sureBtn;
    private View cancelBtn;

    GridView mGridView;

    public interface Callback{
        void selected(int sourceInput);
    }

    public SignalUpdateDialogLayout(Context context) {
        this(context, null);
    }

    public SignalUpdateDialogLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        init();
    }

    private void init(){
        LayoutInflater.from(mContext).inflate(R.layout.dialog_signal_update, this);
        initView();
    }

    /**
     * 初始化UI
     */
    private void initView() {
        sureBtn = findViewById(R.id.positiveButton);
        cancelBtn = findViewById(R.id.negativeButton);
        sureBtn.setOnClickListener(mOnClickListener);
        cancelBtn.setOnClickListener(mOnClickListener);
        if("JPE".equals(AppUtils.client)){
            if("CV648H_L".equals(AppUtils.clientBoard)){
                sourceIdList = getResources().getIntArray(R.array.input_source_id_list_jpe_l);
            }else if(("CN648_K55".equals(AppUtils.clientBoard))){
                sourceIdList = getResources().getIntArray(R.array.input_source_id_list_jpe_kn);
            }else{
                sourceIdList = getResources().getIntArray(R.array.input_source_id_list_jpe);
            }

        }else if("ZX".equals(AppUtils.client)){
            sourceIdList = getResources().getIntArray(R.array.input_source_id_list_zx);
        }else if("CM".equals(AppUtils.client)){
            sourceIdList = getResources().getIntArray(R.array.input_source_id_list_cm);
        }else if("HLC".equals(AppUtils.client)){
            sourceIdList = getResources().getIntArray(R.array.input_source_id_list_hlc);
        }else{
            if("CV648H_L".equals(AppUtils.clientBoard)){
                sourceIdList = getResources().getIntArray(R.array.input_source_id_list_edu_l);
            }else{
                sourceIdList = getResources().getIntArray(R.array.input_source_id_list);
            }
        }
        // 获得信号源参数值


        // 获得数据
        List<SignalInfo> dataList = DBHelper.getSourceInfoFilter(getContext(), "34");

        // 设置Adapter
        int gridMenuID = R.layout.signal_update_grid_item;
        CommonAdapter<SignalInfo> commonAdapter = new CommonAdapter<SignalInfo>(mContext,
                gridMenuID, dataList) {
            @Override
            protected void convert(ViewHolder holder, SignalInfo signalInfo, int position) {
                EditText signalEdit = holder.getView(R.id.signal_edit);
                signalEdit.setText(signalInfo.getName() + "");
            }
        };

        // 初始化
        mGridView = (GridView) findViewById(R.id.signal_grid_view);
        mGridView.setAdapter(commonAdapter);
        mGridView.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE){ // 点击了外部区域
            LogUtils.d("点击了外部区域");
            setVisibility(INVISIBLE);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setCallback(Callback mCallback) {
        this.mCallback = mCallback;
    }

    /**
     * item事件监听
     */
    private AdapterView.OnItemClickListener mOnItemClickListener = (parent, view, position, id) -> {
        RelativeLayout relativeLayout=(RelativeLayout) mGridView.getAdapter().getView(position,view,null);
        EditText editText=(EditText) relativeLayout.getChildAt(0);
        if (editText != null) {
//                    signalEdit.setInputType(InputType.TYPE_CLASS_NUMBER);//输入类型
            editText.setFocusableInTouchMode(true);
            editText.setFocusable(true);
            editText.requestFocus();
        }
        if (position < sourceIdList.length){
            int source = sourceIdList[position];
//            if (mCallback != null){
//                SPUtil.saveData(getContext(), CommConst.SHORT_SOURCE_INDEX, position);
//                mCallback.selected(source);
//            }
        }

//        SignalUpdateDialogLayout.this.setVisibility(View.GONE);
    };

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.positiveButton:
                    setVisibility(View.GONE);
                    break;
                case R.id.negativeButton:
                    setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };
}
