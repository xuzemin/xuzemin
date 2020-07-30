package com.protruly.floatwindowlib.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.activity.SettingsNewActivity;
import com.protruly.floatwindowlib.constant.CommConsts;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.protruly.floatwindowlib.ui.SignalDialogLayout;
import com.protruly.floatwindowlib.ui.ThemometerLayout;
import com.protruly.floatwindowlib.utils.MyUtils;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;
import com.yinghe.whiteboardlib.utils.CommConst;
import com.yinghe.whiteboardlib.utils.SPUtil;
import com.yinghe.whiteboardlib.utils.ScreenUtils;
import com.yinghe.whiteboardlib.utils.ViewUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Carson_Ho on 16/7/22.
 */
public class MoreFragment extends Fragment {
    private static final String TAG = MoreFragment.class.getSimpleName();

    LinearLayout screenshot;
    LinearLayout eyecareLL;
    ImageView eyecareImage;
    LinearLayout temperatureLL;
    LinearLayout lightSenseLL;
    ImageView lightSenseImage;
    LinearLayout userDefinedLL;


    private Context mContext;
    public static Handler mHandler = null;

    // 对话框UI
    Dialog dialog;
    TextView mTitle;
    TextView mMessage;
    EditText mEditText;
    Button mConfirm; //确定按钮
    Button mCancel; //取消按钮

    // 对话框UI
    SignalDialogLayout dialogView;
    Dialog signalDialog;
    private int shortSource = 25;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.more_layout, container, false);
        mHandler = new UIHandler(this);
        initView(view);
        setListen();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onResume() {
        super.onResume();

        // 初始化光感UI
        initLightSenseUI();
    }

    /**
     * 初始化光感UI
     */
    private void initLightSenseUI(){
        boolean isClick = false;
        float alpha = 0.4F;
        boolean isLightSenseEnable = MyUtils.isSupportLightSense();
        int resID = R.mipmap.light_sense_default;
        if (isLightSenseEnable){
            isClick = true;
            alpha = 1F;

            int lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
            resID = (lightSense == 0) ? R.mipmap.light_sense_default: R.mipmap.light_sense_focus;
        }
        lightSenseImage.setImageResource(resID);
        lightSenseLL.setClickable(isClick);
        lightSenseLL.setAlpha(alpha);
    }


    /**
     * 初始化对话框UI
     */
    private void initDialogView() {
        View dialogView = LayoutInflater.from(this.getContext()).inflate(R.layout.update_dialog_layout, null);
        mTitle = (TextView) dialogView.findViewById(R.id.title);
        mEditText = (EditText) dialogView.findViewById(R.id.message_edit);
        mMessage = (TextView) dialogView.findViewById(R.id.message);
        mConfirm = (Button) dialogView.findViewById(R.id.positiveButton);
        mCancel = (Button) dialogView.findViewById(R.id.negativeButton);

        dialog = new Dialog(this.getContext(), R.style.DialogStyle);
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.setCanceledOnTouchOutside(true);

        // 设置对话框的大小
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = ScreenUtils.dip2px(this.getContext(), 400); // 宽度
        lp.height = ScreenUtils.dip2px(this.getContext(), 300); // 高度

        dialogWindow.setAttributes(lp);
    }

    /**
     * 初始化对话框UI
     */
    private void initSignalDialog() {
        dialogView = new SignalDialogLayout(getContext());

        signalDialog = new Dialog(this.getContext(), R.style.dialog);
        signalDialog.setContentView(dialogView);
        signalDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        signalDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        signalDialog.setCanceledOnTouchOutside(true);

        // 设置对话框的大小
        Window dialogWindow = signalDialog.getWindow();

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = ScreenUtils.dip2px(this.getContext(), 180); // 宽度
        lp.height = ScreenUtils.dip2px(this.getContext(), 300); // 高度

        dialogWindow.setAttributes(lp);

        dialogView.setCallback((source)->{
            if (ViewUtils.isFastDoubleClick()){
                return;
            }

            LogUtils.d("shortSource->" + shortSource);
            shortSource = source;
            SPUtil.saveData(getContext(),  CommConst.SHORT_SOURCE_INDEX, source);
            signalDialog.dismiss();

            AppUtils.changeSource(getContext(), shortSource);
        });
    }

    /**
     * 初始化控件
     */
    private void initView(View view) {
        eyecareLL = (LinearLayout) view.findViewById(R.id.pup_eyecare);
        eyecareImage = (ImageView) view.findViewById(R.id.eyecare_iv);
        int eyeCare = Settings.System.getInt(getActivity().getContentResolver(), CommConsts.IS_EYECARE, 0);
        int resID = (eyeCare == 0) ? R.mipmap.eye_care_default: R.mipmap.eye_care_focus;
        eyecareImage.setImageResource(resID);

        screenshot = (LinearLayout) view.findViewById(R.id.pup_screenshot);
        temperatureLL = (LinearLayout) view.findViewById(R.id.pup_temperature);

        lightSenseLL = (LinearLayout) view.findViewById(R.id.pup_light_sense);
        lightSenseImage = (ImageView) view.findViewById(R.id.light_iv);

        userDefinedLL = (LinearLayout) view.findViewById(R.id.pup_user_defined);

        shortSource = (Integer) SPUtil.getData(getContext(), CommConst.SHORT_SOURCE_INDEX, 25);

//        initDialogView();
        initSignalDialog();
    }

    /**
     * 注册监听
     */
    private void setListen() {
        screenshot.setOnClickListener(mOnClickListener);
        eyecareLL.setOnClickListener(mOnClickListener);
        temperatureLL.setOnClickListener(mOnClickListener);

        lightSenseLL.setOnClickListener(mOnClickListener);
        userDefinedLL.setOnClickListener(mOnClickListener);
    }

    /**
     * 设置护眼模式
     */
    private void setEyecareMode(boolean isEyeCare){
        int eyeCare = isEyeCare ? 1 : 0;
        Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_EYECARE, eyeCare);
        int resID = (eyeCare == 0) ? R.mipmap.eye_care_default: R.mipmap.eye_care_focus;
        eyecareImage.setImageResource(resID);
        Log.d(TAG, "eyeCare->" + eyeCare );
    }

    /**
     * 设置自动感光
     */
    private void setLightSenseMode(boolean isOpen){
        if (!MyUtils.isSupportLightSense()){
            return;
        }

        int lightSense = isOpen ? 1 : 0;
        Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, lightSense);

        int resID = isOpen ? R.mipmap.light_sense_focus : R.mipmap.light_sense_default;
        lightSenseImage.setImageResource(resID);
        Log.d(TAG, "isLightSense->" + lightSense);

        // 更新光感定时任务
        updateLightSense();
    }

    /**
     * 更新光感定时任务
     */
    private void updateLightSense(){
        LogUtils.d("更新光感定时任务.....");
        Intent tempIntent = new Intent(getContext(), FloatWindowService.class);
        tempIntent.setAction("com.ctv.FloatWindowService.LIGHT_SENSE_ACTION");
        getContext().startService(tempIntent);
    }


    /**
     * 显示温度
     */
    private void showTemperature(){
        float tmpValue = CmdUtils.getTmpValue();

        boolean isFirst = false;
        ThemometerLayout thmometerWindow = FloatWindowManager.getThmometerWindow();
        if (thmometerWindow == null) {
            thmometerWindow = FloatWindowManager.createThemometerWindow(getContext().getApplicationContext());
            isFirst = true;
        }

        int visibility = thmometerWindow.getVisibility();
        if (!isFirst && visibility == View.VISIBLE){ // 若当前是显示，则隐藏
            thmometerWindow.setVisibility(View.INVISIBLE);
        } else { // 若当前是隐藏，则显示
            thmometerWindow.setVisibility(View.VISIBLE);
            // 更新进度
            if (ThemometerLayout.mHandler != null){
                Message message = ThemometerLayout.mHandler.obtainMessage(1, tmpValue);
                ThemometerLayout.mHandler.sendMessage(message);

                // 延迟消失
                ThemometerLayout.mHandler.postDelayed(()->{
                    ThemometerLayout thmometer = FloatWindowManager.getThmometerWindow();
                    if (thmometer != null){
                        thmometer.setVisibility(View.INVISIBLE);
                    }
                }, 3000);
            }
        }
    }

    /**
     * 改变护眼模式
     */
    private void changeEyecare(){
        int eyeCare = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_EYECARE, 0);
        boolean isEyeCare = (eyeCare == 0);
        setEyecareMode(isEyeCare);

        // 打开护眼模式时,关闭自动光感
        if (isEyeCare){
            mHandler.removeMessages(KEY_RESET_BACK_LIGHT);

            // 保存背光值
            int curBacklight = AppUtils.getBacklight();
            if (curBacklight != 50){
                Settings.System.putInt(getActivity().getContentResolver(), "lastBlackLight", curBacklight);
                LogUtils.d("护眼模式 降低light setBacklight 50, curBacklight:" + curBacklight);
            }

            if (curBacklight > 50){
                AppUtils.setBacklight(50);
                // 更新背光
                updateBlackLightSeekbar();

                // 恢复背光值
                mHandler.sendEmptyMessageDelayed(KEY_RESET_BACK_LIGHT, 5000);
            }

            int lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
            if (lightSense == 1){
                // 切换光感
                Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
                if (!MyUtils.isSupportLightSense()){
                    return;
                }

                lightSense = 0;
                Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, lightSense);

                int resID =  R.mipmap.light_sense_default;
                lightSenseImage.setImageResource(resID);
                Log.d(TAG, "isLightSense->" + lightSense);
            }
        } else { // 关闭背光时
            // 关闭背光值
            int lastBlackLight = Settings.System.getInt(mContext.getContentResolver(),
                    "lastBlackLight", 50);
            if (lastBlackLight > 50){
                Settings.System.putInt(getActivity().getContentResolver(), "lastBlackLight", lastBlackLight);
            }

            resetBlackLight();
        }
    }

    /**
     * 恢复背光值
     */
    private void resetBlackLight(){
        mHandler.removeMessages(KEY_RESET_BACK_LIGHT);

        // 恢复背光值
        int lastBlackLight = Settings.System.getInt(mContext.getContentResolver(),
                "lastBlackLight", 50);
        if (lastBlackLight > 50){
            AppUtils.setBacklight(lastBlackLight);
            // 更新背光
            updateBlackLightSeekbar();
            LogUtils.d("护眼模式 恢复light setBacklight lastBlackLight:" + lastBlackLight);
        }
    }

    /**
     * 更新背光值
     */
    private void updateBlackLightSeekbar(){
        LogUtils.d("updateBlackLightSeekbar .....");
        if (SettingsNewActivity.mHandler != null){ // 更新背光
            SettingsNewActivity.mHandler.sendEmptyMessage(SettingsNewActivity.MSG_UPDATE_LIGHT);
        }
    }

    /**
     * 改变光感
     */
    private void changeLightSense(){
        // 切换光感
        int lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
        boolean isOpen = (lightSense == 0);
        setLightSenseMode(isOpen);

        // 自动光感开启时,关闭护眼模式
        lightSense = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_LIGHTSENSE, 0);
        if (lightSense == 1){ // 自动光感时,关闭护眼模式
            int eyeCare = Settings.System.getInt(getContext().getContentResolver(), CommConsts.IS_EYECARE, 0);
            if (eyeCare == 1){ // 开启护眼时， 关闭护眼
                Settings.System.putInt(getContext().getContentResolver(), CommConsts.IS_EYECARE, 0);
                setEyecareMode(false);

                // 恢复背光值
                resetBlackLight();
            }
        }
    }

    /**
     * 显示对话框
     * @param dialog
     */
    private void showDialog(Dialog dialog){
        dialog.setOnDismissListener((d)->{
        });
        dialog.show();
    }

    /**
     * 显示用户自定义对话框
     */
    private void showSignalDialog() {
        initSignalDialog();
        showDialog(signalDialog);
    }

    /**
     * 点击事件
     */
    private View.OnClickListener mOnClickListener = v -> {
        if (ViewUtils.isFastDoubleClick()){
            return;
        }

        int id = v.getId();
        boolean isExit = false;
        switch (id) {
            //截图
            case R.id.pup_screenshot: {
                Log.i(TAG, "screenshot start");
                // 截图或者批注显示时
                Settings.System.putInt(getContext().getContentResolver(), "annotate.start", 1);

                // 在OPS\HDMI\VAG下时，关闭USB触控
//                if (CmdUtils.isInOpsHDMIVgaTopShow(getContext().getApplicationContext())){
//                }
                CmdUtils.changeUSBTouch(getContext(),false);

                Log.i(TAG, "screenshot start");
                String mPackageName = "com.example.cutcapture";
                String mActivityName = "com.example.cutcapture.MainActivity";
                AppUtils.gotoOtherApp(getContext(), mPackageName, mActivityName);

                isExit = true;
                break;
            }
            // 设置护眼模式
            case R.id.pup_eyecare: {
                // 允许背光进度条滑动
                mHandler.post(() ->{
                    if (SettingsNewActivity.mHandler != null){
                        Message msg = SettingsNewActivity.mHandler.obtainMessage(SettingsNewActivity.MSG_UPDATE_LIGHT,
                                true);
                        SettingsNewActivity.mHandler.sendMessage(msg); // 更新亮度进度条
                    }
                });
                changeEyecare();
                break;
            }
            // 自动感光
            case R.id.pup_light_sense: {
                // 允许背光进度条滑动
                mHandler.post(() ->{
                    if (SettingsNewActivity.mHandler != null){
                        Message msg = SettingsNewActivity.mHandler.obtainMessage(SettingsNewActivity.MSG_UPDATE_LIGHT,
                                true);
                        SettingsNewActivity.mHandler.sendMessage(msg); // 更新亮度进度条
                    }
                });
                // 切换光感
                changeLightSense();
                break;
            }
            // 温度显示
            case R.id.pup_temperature: {
                showTemperature();
                break;
            }
            // 用户自定义显示
            case R.id.pup_user_defined: {
//                showUserDefined();
                showSignalDialog();
                break;
            }
            default:{
                break;
            }
        }

        // 退出设置界面
        if (isExit){
            if (mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
        }
    };

    public static final int KEY_CHANGE_LIGHT_SENSE = 1; // 切换光感
    public static final int KEY_CHANGE_EYE_CARE = 2; // 切换护眼
    public static final int KEY_RESET_BACK_LIGHT = 4; // 恢复背光

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<MoreFragment> weakReference;

        public UIHandler(MoreFragment activity) {
            super();
            this.weakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            MoreFragment fragment = weakReference.get();
            if (fragment == null || !fragment.getUserVisibleHint()){
                return;
            }

            switch (msg.what){
                case KEY_CHANGE_LIGHT_SENSE:{ // 打开和关闭光感
                    if (msg.obj instanceof Boolean){
                        boolean isOpen = (boolean)msg.obj;
                        fragment.setLightSenseMode(isOpen);
                    }
                    break;
                }
                case KEY_CHANGE_EYE_CARE:{ // 打开和关闭光感
                    if (msg.obj instanceof Boolean){
                        boolean isOpen = (boolean)msg.obj;
                        if (!isOpen) {
                            mHandler.removeMessages(KEY_RESET_BACK_LIGHT);
                        }
                        fragment.setEyecareMode(isOpen);
                    }
                    break;
                }
                case KEY_RESET_BACK_LIGHT:{ // 恢复背光值
                    fragment.resetBlackLight();
                    break;
                }
                default:
                    break;
            }
        }
    }

}
