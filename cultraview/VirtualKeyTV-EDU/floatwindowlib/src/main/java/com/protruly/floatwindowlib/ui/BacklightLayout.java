package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cultraview.tv.CtvAudioManager;
import com.protruly.floatwindowlib.R;
import com.protruly.floatwindowlib.control.FloatWindowManager;
import com.protruly.floatwindowlib.service.FloatWindowService;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.CmdUtils;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Desc: 亮度对话框
 *
 * @author wang
 * @time 2017/4/13.
 */
public class BacklightLayout extends FrameLayout {
    private static final String TAG = BacklightLayout.class.getSimpleName();

    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private TextView textView;
    private SeekBar light;

    public static Handler mHandler;

    private Timer timer;

    public HandlerThread mDataThread;
    public static Handler mDataHandler;// 数据处理

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 2:
                    setVisibility(VISIBLE);
                    break;
            }
        }
    };
    public BacklightLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.light_dialog, this);

        mDataThread = new HandlerThread(TAG);
        mDataThread.start();
        mDataHandler = new DataHandler(this, mDataThread.getLooper());

        mHandler = new UIHandler(this);
        textView = (TextView) findViewById(R.id.tv_volume_value);
        light = (SeekBar) findViewById(R.id.skb_volume_value);
        light.setOnSeekBarChangeListener(new LightListener());
        try {
            light.setProgress(AppUtils.getBacklight());
            Log.d(TAG, "获得当前亮度->" + AppUtils.getBacklight());
        } catch (Exception e) {
            e.printStackTrace();
        }
        light.setMax(100);

        int audioSpdifOutMode = CtvAudioManager.getInstance().getAudioSpdifOutMode();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) { // 点击了外部区域
//            Log.d(TAG, "点击了外部区域");
//	        stopTimetask();
//            setVisibility(INVISIBLE);
            if (hideRunnable != null) {
                mDataHandler.removeCallbacks(hideRunnable);
            }
            BacklightLayout.this.setVisibility(View.GONE);
//            mDataHandler.sendEmptyMessageDelayed(2, 200);
        } else {
//            Log.d(TAG, "getAction ->" + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startTime = System.currentTimeMillis();
                    endTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    startTime = System.currentTimeMillis();
                    endTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    startTime = System.currentTimeMillis();
                    endTime = System.currentTimeMillis();
                    break;
            }

            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 更新进度条
     *
     * @param process
     */
    private void updateProcess(int process) {
        textView.setText(process + "");
    }

    /**
     * 更新进度条
     *
     * @param process
     */
    public void refreshBacklight(int process) {
        textView.setText(process + "");
        light.setProgress(process);
    }

    /**
     * 开始任务
     */
//	public void startTimetask(){
//	    // 开启定时器，每隔5秒刷新一次
//	    if (timer == null) {
////		    Log.d(TAG, "startTimetask");
//		    startTime = System.currentTimeMillis();
//		    endTime = System.currentTimeMillis();
//		    timer = new Timer();
//		    timer.scheduleAtFixedRate(new RefreshTask(), 10, 500);
//	    }
//    }

    /**
     * 停止任务
     */
    public void stopTimetask() {
        if (timer != null) {
//			Log.d(TAG, "stopTimetask");
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 亮度监听类
     */
    class LightListener implements SeekBar.OnSeekBarChangeListener {
        private int lastProgress;
        private boolean isStarting = false; // 是否正在滑动

        @Override
        public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            if (hideRunnable != null) {
                mDataHandler.removeCallbacks(hideRunnable);
                //postDelayed(hideRunnable, 5000);
            }
            startTime = System.currentTimeMillis();
            endTime = System.currentTimeMillis();

            if (Math.abs(progress - lastProgress) <= 3) {
                return;
            }

            lastProgress = progress;

            mHandler.postDelayed(() -> {
                if (isStarting) { // 正在滑动时
                    AppUtils.setBacklight(progress);
                    if (seekBar.getProgress() != 50) {
                        Settings.System.putInt(BacklightLayout.this.getContext().getContentResolver(), "lastBlackLight", seekBar.getProgress());
                    }
                }
//                Log.d(TAG, "获得当前亮度->" + mTvPictureManager.getBacklight());
                updateProcess(progress);
            }, 50);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            isStarting = true;
            lastProgress = seekBar.getProgress();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (hideRunnable != null) {
                mDataHandler.removeCallbacks(hideRunnable);
                mDataHandler.postDelayed(hideRunnable, 5000);
            }
            //	removeCallbacks(layout.hideRunnable);
            //	postDelayed(layout.hideRunnable, 5000);
        }
    }

    public static long startTime;
    public static long endTime;
//	class RefreshTask extends TimerTask {
//		@Override
//		public void run() {
//			endTime = System.currentTimeMillis();
////			Log.d(TAG, "Timetask run endTime->" + endTime + " startTime->" + startTime);
//			if (endTime - startTime > 1000 * 3){
////				stopTimetask();
//				mHandler.postDelayed(()->{
//					BacklightLayout.this.setVisibility(View.INVISIBLE);
//				}, 100);
//			}
//		}
//	}

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<BacklightLayout> weakReference;

        public UIHandler(BacklightLayout downloadingLayout) {
            super();
            this.weakReference = new WeakReference<>(downloadingLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BacklightLayout layout = weakReference.get();

            if (layout == null) {
                return;
            }
        }
    }

    /**
     * 隐藏UI
     */
    Runnable hideRunnable = () -> {
        if (mHandler != null) {
            mHandler.post(() -> {
                BacklightLayout.this.setVisibility(View.GONE);
            });
        }
    };

    /**
     * data异步处理
     */
    public static final class DataHandler extends Handler {
        WeakReference<BacklightLayout> weakReference;

        public DataHandler(BacklightLayout layout, Looper looper) {
            super(looper);
            this.weakReference = new WeakReference<>(layout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BacklightLayout layout = weakReference.get();

            if (layout == null) {
                return;
            }

            // 开始处理
            switch (msg.what) {
                case 0: { // 移除隐藏动作
//					removeCallbacks(layout.hideRunnable);
                    break;
                }
                case 1: { // 处理隐藏动作
//					removeCallbacks(layout.hideRunnable);
//					postDelayed(layout.hideRunnable, 5000);
                    break;
                }
                case 2: { // 延时处理隐藏底部动作
                    removeCallbacks(layout.hideRunnable);
                    postDelayed(layout.hideRunnable, FloatWindowService.hideTime);
                    break;
                }
                case 3: { // 立即隐藏底部动作
//					removeCallbacks(service.hideRunnable);
//					postDelayed(service.hideRunnable, 1);
                    break;
                }
                default:
                    break;
            }
        }
    }

    public void setPostVisibility() {
        handler.removeMessages(2);
        handler.sendEmptyMessageDelayed(2, 200);
    }



    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            CmdUtils.changeUSBTouch(getContext(), false);
        } else {
            CmdUtils.changeUSBTouch(getContext(), true);
        }
    }
}
