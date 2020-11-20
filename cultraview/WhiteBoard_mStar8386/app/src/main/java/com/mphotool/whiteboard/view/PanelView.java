package com.mphotool.whiteboard.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;

import com.hht.android.sdk.device.HHTCommonManager;
import com.hht.android.sdk.picture.HHTPictureManager;
import com.mphotool.whiteboard.activity.MainActivity;
import com.mphotool.whiteboard.activity.WhiteBoardApplication;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.StatusEnum;

public class PanelView extends SurfaceView implements Callback, View.OnTouchListener {
    private static final String TAG = "PanelView";

    public PanelManager mPanelManager = null;
    private int backlight_tmp = 0;
    private final String BACKLIGHT = "persist.sys.backlight";
    private final String EYE_MODE = "persist.sys.eye_protection_mode";
    private static final int EVENT_EYE_PROTECT_OPEN = 0;
    private static final int EVENT_EYE_PROTECT_CLOSE = 1;
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

    private void setBacklight(int brightness){
        SystemProperties.set(BACKLIGHT, "" + brightness);
        HHTPictureManager.getInstance().setBackLight(brightness);
    }

    @Override
    public boolean isInTouchMode() {
        return super.isInTouchMode();
    }

    private PanelTouchStatusListener mPanelTouchStatusListener = null;

    public void setOpenGLView(BaseGLSurfaceView pOpenGLView)
    {
        mPanelManager.setOpenGLView(pOpenGLView);
    }

    /**
     * 从外部设置的画布touch状态监听，用于改变外部UI状态（例如隐藏各类选择菜单）
     */
    public interface PanelTouchStatusListener {

        void onTouchDown();

        void onTouch(MotionEvent motionEvent);

        void onTouchUp();
    }

    public static void dbg(String msg)
    {
        BaseUtils.dbg(TAG, msg);
    }

    public void setPanelTouchStatusListener(PanelTouchStatusListener listener)
    {
        mPanelTouchStatusListener = listener;
    }

    private void init(Context context)
    {
        /** 获得SurfaceHolder对象并为SurfaceView添加状态监听 **/
        getHolder().addCallback(this);

        /**surfaceview置顶*/
        setZOrderOnTop(false);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setOnTouchListener(this);
        /**getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件***/
//        Constants.touchSlop = (float) ViewConfiguration.get(getContext()).getScaledTouchSlop();
        mPanelManager = new PanelManager(context);
    }

    public PanelView(Context context)
    {
        super(context);
        init(context);
    }

    public PanelView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public PanelView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context);
    }

    public int addPage()
    {
        return mPanelManager.addPage();
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        if (!mPanelManager.isReady())
        {
            mPanelManager.init(holder, getWidth(), getHeight());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        dbg(String.format("surfaceChanged, %d, %d", new Object[]{Integer.valueOf(width), Integer.valueOf(height)}));
        mPanelManager.drawScreenDelay(new Long((long) 200));
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        mPanelManager.setReady(false);
    }

    public boolean onTouch(View v, MotionEvent event)
    {

        if (!WhiteBoardApplication.isWhiteBoardEnable)
        {
            return true;
        }

        if (mPanelManager.isCroping())
        {
            dbg("during crop , do nothing in onTouchEvent");
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

        if (mPanelTouchStatusListener != null)
        {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN)
            {
                mPanelTouchStatusListener.onTouchDown();
                MainActivity.isTouching = 1;
            }
            else if (event.getActionMasked() == MotionEvent.ACTION_UP)
            {
                mPanelTouchStatusListener.onTouchUp();

                MainActivity.isTouching = 0;
            }
            else
            {
                /**事件传给外部回调对象，进行隐藏各类菜单的操作*/
                mPanelTouchStatusListener.onTouch(event);
            }

        }

        mPanelManager.isLocked = true;

        /**传递至PanelManager，处理擦除、选择等操作*/
        mPanelManager.onTouch(event);

        if (event.getActionMasked() == MotionEvent.ACTION_UP || event.getActionMasked() == MotionEvent.ACTION_CANCEL)
        {
            /**一次Touch事件结束，4s之后重置到默认状态*/
            mPanelManager.setStatus(StatusEnum.STATUS_NC);
            mPanelManager.isLocked = false;
        }

        return true;
    }

    public int setPenColor(int color)
    {
        mPanelManager.setPenColor(color);
        return 0;
    }

    public int cleanBoardAction(boolean trigger)
    {
        mPanelManager.cleanBoardAction(trigger);
        return 0;
    }

    public int updateSurface()
    {
        reDraw();
        return 0;
    }

    public int setPenWidth(float width)
    {
        if (mPanelManager != null)
        {
            mPanelManager.setPenWidth(width);
        }
        return 0;
    }

    public Bitmap getSuitBitmap(int id)
    {
        return mPanelManager.getSuitBitmap(id, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT,false);
    }

    private void reDraw()
    {
        if (mPanelManager != null)
        {
            mPanelManager.repaintToOffScreenCanvas(null, false);
            mPanelManager.repaintToSurfaceInQueue();
        }
    }

    public int undo(boolean trigger)
    {
        mPanelManager.undo(trigger);
        mPanelManager.repaintToOffScreenCanvas(null, false);
        mPanelManager.repaintToSurfaceInQueue();
        return 0;
    }

    public int redo(boolean trigger)
    {
        mPanelManager.redo(trigger);
        mPanelManager.repaintToOffScreenCanvas(null, false);
        mPanelManager.repaintToSurfaceInQueue();
        return 0;
    }

    public void destory()
    {
        mPanelManager.destory();
    }
}
