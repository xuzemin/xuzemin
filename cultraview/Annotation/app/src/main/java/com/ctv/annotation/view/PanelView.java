package com.ctv.annotation.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;

import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.utils.StatusEnum;
import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.picture.HHTPictureManager;

public class PanelView extends SurfaceView implements SurfaceHolder.Callback , View.OnTouchListener {
    private SurfaceHolder holder = null;
    public Surface mSurface;
    private int backlight_tmp = 0;
    public PanelManager mPanelManager = null;
    private final String BACKLIGHT = "persist.sys.backlight";
    private final String EYE_MODE = "persist.sys.eye_protection_mode";
    private PanelTouchStatusListener mPanelTouchStatusListener = null;
    private static final int EVENT_EYE_PROTECT_OPEN = 0;
    private static final int EVENT_EYE_PROTECT_CLOSE = 1;
    SurfaceHolder mHolder;
    private Handler mhandle = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case EVENT_EYE_PROTECT_OPEN:
                    int backlight = Integer.parseInt(SystemProperties.get(BACKLIGHT));
                    if(50 < backlight){
                        backlight_tmp = backlight;
                        setBacklight(50);
                    }else{
                        backlight_tmp = 0;
                    }
                    break;
                case EVENT_EYE_PROTECT_CLOSE:
                    if(backlight_tmp > 50){
                        setBacklight(backlight_tmp);
                        backlight_tmp = 0;
                    }
                    break;

            }
        }
    };

    public PanelView(Context context) {
        super(context);
        init(context);
    }

    public PanelView(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(context);

    }

    public PanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        holder = getHolder();
        holder.addCallback(this);

        /**surfaceview置顶*/
        setZOrderOnTop(false);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setOnTouchListener(this);

        Constants.touchSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mPanelManager = new PanelManager(context);

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        mSurface = getHolder().getSurface();
        mHolder = holder;

        if (!mPanelManager.isReady())
        {
            mPanelManager.init(holder, getWidth(), getHeight());
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        mPanelManager.drawScreenDelay(new Long((long) 200));
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

        mPanelManager.setReady(false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mPanelManager.isCroping())
        {
            return true;
        }

        if(HHTCommonManager.EnumEyeProtectionMode.EYE_WRITE_PROTECT.ordinal()
                == HHTCommonManager.getInstance().getEyeProtectionMode()) {
            if (event.getActionMasked() == 1) {
                mhandle.removeMessages(EVENT_EYE_PROTECT_OPEN);
                mhandle.sendEmptyMessage(EVENT_EYE_PROTECT_CLOSE);
            } else if (event.getActionMasked() == 0) {
                mhandle.removeMessages(EVENT_EYE_PROTECT_CLOSE);
                mhandle.sendEmptyMessage(EVENT_EYE_PROTECT_OPEN);
            }
        }

        mPanelManager.isLocked = true;
      //  Log.d(TAG, "PanelView   onTouch:  isLocked = "+ mPanelManager.isLocked );
        /**传递至PanelManager，处理擦除、选择等操作*/
        mPanelManager.onTouch(event);


        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL)
        {
            mPanelManager.setStatus(StatusEnum.STATUS_NC);
            mPanelManager.isLocked = false;
            /**一次Touch事件结束，4s之后重置到默认状态*/
            Log.d("hggh", "PanelView   ACTION_UP:  isLocked = "+ mPanelManager.isLocked );


        }
        return true;
    }

    public int setPenColor(int color) {
//        dbg("setPenColor color=" + color);
        mPanelManager.setPenColor(color);
        return 0;

    }

    /**
     * 从外部设置的画布touch状态监听，用于改变外部UI状态（例如隐藏各类选择菜单）
     */
    public interface PanelTouchStatusListener {

        void onTouchDown();

        void onTouch(MotionEvent motionEvent);

        void onTouchUp();
    }
    public void setPanelTouchStatusListener(PanelTouchStatusListener listener)
    {
        mPanelTouchStatusListener = listener;
    }

    public void destory()
    {
        mPanelManager.destory();
    }

    private void setBacklight(int brightness){
        SystemProperties.set(BACKLIGHT, "" + brightness);
        HHTPictureManager.getInstance().setBackLight(brightness);
    }
}
