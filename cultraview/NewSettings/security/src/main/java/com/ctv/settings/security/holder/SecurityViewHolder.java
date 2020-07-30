package com.ctv.settings.security.holder;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ctv.settings.base.BaseViewHolder;
import com.ctv.settings.security.R;
import com.ctv.settings.security.activity.PermissionsActivity;
import com.ctv.settings.security.utils.GreneralUtils;
import com.ctv.settings.utils.L;
import com.ctv.settings.utils.Tools;
import com.ctv.settings.utils.ViewUtils;

/**
 * 安全ViewHolder
 * @author wanghang
 * @date 2019/09/17
 */
public class SecurityViewHolder extends BaseViewHolder implements View.OnClickListener {
    private final static String TAG = SecurityViewHolder.class.getCanonicalName();

    private View mainSecurity; // 安全模块根界面
    private View itemAppPermissions; // 安全模块根界面
    private View itemUnknownStore; // 安全模块根界面
    private View itemCameraPermissions; // 安全模块根界面
    private View itemUdiskPermissions; // 安全模块根界面

    private ImageView imAppPermissions; // app权限
    private ImageView imUnknownStore; // 位置来源开关
    private ImageView imUdiskPermissions; // U盘权限
    private ImageView imCameraPermissions; // 摄像头权限

    private boolean isUnknownStore = false;
    private boolean isCameraPermissions = true;
    private boolean isUdiskPermissions = true;

    public Handler mHandler = null;
    private View item_lock_screen;
    private View item_usb_lock;

    public SecurityViewHolder(Activity activity) {
        super(activity);
    }

    public SecurityViewHolder(Activity activity, Handler handler) {
        super(activity);
        this.mHandler = handler;
    }

    /**
     * 初始化UI
     *
     * @param activity
     */
    @Override
    public void initUI(Activity activity) {
//        EventBus.getDefault().register(this);

        Log.d(TAG, "initUI");
        mainSecurity = activity.findViewById(R.id.main_security);

        itemAppPermissions = activity.findViewById(R.id.item_app_permissions);
        itemUnknownStore = activity.findViewById(R.id.item_unknown_store);
        itemCameraPermissions = activity.findViewById(R.id.item_camera_permissions);
        itemUdiskPermissions = activity.findViewById(R.id.item_udisk_permissions);

        imAppPermissions = (ImageView) activity.findViewById(R.id.im_item_app_permissions);
        imUnknownStore = (ImageView) activity.findViewById(R.id.im_item_unknown_store);
        imUdiskPermissions = (ImageView) activity.findViewById(R.id.im_item_udisk_permissions);
        imCameraPermissions = (ImageView) activity.findViewById(R.id.im_item_camera_permissions);

        item_lock_screen = activity.findViewById(R.id.item_lock_screen);
        item_usb_lock = activity.findViewById(R.id.item_usb_lock);
    }

    /**
     * 初始化数据
     *
     * @param activity
     */
    @Override
    public void initData(Activity activity) {
        Log.d(TAG, "initData");
        isUnknownStore = Tools.isNonMarketAppsAllowed(activity);
        int resId = isUnknownStore ? R.mipmap.on : R.mipmap.off;
        imUnknownStore.setBackgroundResource(resId);

        isCameraPermissions = true;
        resId = isCameraPermissions ? R.mipmap.on : R.mipmap.off;
        imCameraPermissions.setBackgroundResource(resId);

        isUdiskPermissions = true;
        resId = isUdiskPermissions ? R.mipmap.on : R.mipmap.off;
        imUdiskPermissions.setBackgroundResource(resId);


        L.d(TAG, "isUnknownStore:%s, isCameraPermissions:%s, isUdiskPermissions:%s",
                isUnknownStore, isCameraPermissions, isUdiskPermissions);
    }

    /**
     * 初始化监听
     */
    @Override
    public void initListener() {
        Log.d(TAG, "initListener");
        itemAppPermissions.setOnClickListener(this);
        itemUnknownStore.setOnClickListener(this);
        itemCameraPermissions.setOnClickListener(this);
        itemUdiskPermissions.setOnClickListener(this);

        //imAppPermissions.setOnClickListener(this);
//        imUnknownStore.setOnClickListener(this);
        imUdiskPermissions.setOnClickListener(this);
        imCameraPermissions.setOnClickListener(this);

        item_lock_screen.setOnClickListener(this);
        item_usb_lock.setOnClickListener(this);
    }

    /**
     * 刷新指定view
     *
     * @param view
     */
    @Override
    public void refreshUI(View view) {
    }

    /**
     * 显示和隐藏
     *
     * @param isShow
     */
    public void isShow(boolean isShow) {
        int visible = isShow ? View.VISIBLE : View.GONE;
        mainSecurity.setVisibility(visible);
    }

    /**
     * 处理dialog
     *
     * @param dialog
     */
    private void handleDialog(final Dialog dialog) {
        isShow(false);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                isShow(true);
            }
        });
    }

    public void onDestroy(){
//        EventBus.getDefault().unregister(this);
    }

//    /**
//     * 处理事件:更新音量刷新开关
//     */
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void handleVoiceBarEvent(Event.UpdateVoiceBarEvent event ) {
//        if (!mActivity.isFinishing()){
////            updateVoiceBarUI(event.state)
//        }
//    }


    @Override
    public void onClick(View view) {
        if (ViewUtils.isFastDoubleClick()){
            return;
        }

        int id = view.getId();
        L.d(TAG, "mOnClickListener view.id->%s", id);
        if (id == R.id.item_app_permissions) {
            mActivity.startActivity(new Intent(mActivity, PermissionsActivity.class));
        } else if (id == R.id.item_unknown_store){
            isUnknownStore = Tools.isNonMarketAppsAllowed(mActivity);
            isUnknownStore = !isUnknownStore;
            Tools.setNonMarketAppsAllowed(mActivity, isUnknownStore);

            int resId = isUnknownStore ? R.mipmap.on : R.mipmap.off;
            imUnknownStore.setBackgroundResource(resId);
            L.d(TAG, "mOnClickListener view.id->%s isUnknownStore:%s", id, isUnknownStore);
        } else if (id == R.id.item_camera_permissions){
            isCameraPermissions = !isCameraPermissions;
            int resId = isCameraPermissions ? R.mipmap.on : R.mipmap.off;
            imCameraPermissions.setBackgroundResource(resId);
            L.d(TAG, "mOnClickListener view.id->%s imCameraPermissions:%s", id, imCameraPermissions);
        } else if (id == R.id.item_udisk_permissions){
            isUdiskPermissions = !isUdiskPermissions;
            int resId = isUdiskPermissions ? R.mipmap.on : R.mipmap.off;
            imUdiskPermissions.setBackgroundResource(resId);
            L.d(TAG, "mOnClickListener view.id->%s imUdiskPermissions:%s", id, imUdiskPermissions);
        }else if(view.getId() == R.id.item_lock_screen){
            GreneralUtils.getInstance(mActivity).openLockScreenFunction();
        }else if(view.getId() == R.id.item_usb_lock){
            GreneralUtils.getInstance(mActivity).openUsbLockFunction();
        }

    }
}
