package com.yinghe.whiteboardlib.listener;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;

/**
 * 多点触控监听
 * @author wang
 * @date 2018/5/23
 */
public abstract class FiveOnTouchListener {
    private final static String TAG = FiveOnTouchListener.class.getSimpleName();

    private volatile int countPoint = 0; // 记录触控个数

    private volatile boolean isBlackMode = false; // 记录背光状态：false 亮屏， true 暗屏
    private volatile boolean lastBlackMode = false; // 记录背光状态：false 亮屏， true 暗屏
    private boolean isFirst = true; // 第一次操作

    private final static Object MY_LOCK = new Object();

    public static Handler mHandler;

    public FiveOnTouchListener (){
        mHandler = new UIHandler(this);
    }

    /**
     * 五指触控的回调事件
     */
    public abstract void onFiveTouch();

    public boolean onTouch(MotionEvent event) {
        // 触控事件
        // 统一单点和多点
        int action = (event.getAction() & MotionEvent.ACTION_MASK) % 5;
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {// 按下
                synchronized (MY_LOCK){
                    mHandler.removeMessages(0);

                    // 首次按下时
                    if (event.getPointerCount() == 1){
                        countPoint = 0;
                    }

                    countPoint = countPoint + 1;
                    if (countPoint == 5){
                        Log.d(TAG, "五指触控 isBlackMode" + isBlackMode);
                        int delay = 3000;
                        if (isFirst) { // 第一次五指触摸
                            isFirst = false;
                            delay = 0;
                        }

                        mHandler.sendEmptyMessageDelayed(0, delay);
                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {// 移动
                break;
            }
            case MotionEvent.ACTION_UP: {// 抬起
                synchronized (MY_LOCK){
                    countPoint = countPoint - 1;

                    if (countPoint < 0){
                        countPoint = 0;
                    }

                    // 最后一个手指抬起的时候
                    if (event.getPointerCount() <= 1){
                        Log.d(TAG, "最后一个手指抬起的时候");
                        countPoint = 0;
                    }
                }
                break;
            }
        }

        return false;
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<FiveOnTouchListener> weakReference;

        public UIHandler(FiveOnTouchListener listener) {
            super();
            this.weakReference = new WeakReference<>(listener);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            FiveOnTouchListener listener = weakReference.get();

            if (listener == null) {
                return;
            }

            // 开始处理更新UI
            switch (msg.what){
                case 0:// 更改
                    listener.lastBlackMode = listener.isBlackMode;
                    listener.isBlackMode = !listener.isBlackMode;
                    listener.onFiveTouch();
                    break;
                default:
                    break;
            }
        }
    }
}
