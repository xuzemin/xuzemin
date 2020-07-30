package com.protruly.floatwindowlib.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.protruly.floatwindowlib.R;
import com.yinghe.whiteboardlib.ui.DownLoading;

import java.lang.ref.WeakReference;

/**
 * Desc: 下载更新过程中
 *
 * @author wang
 * @time 2017/4/13.
 */
public class DownloadingLayout extends FrameLayout {
    // 宽和高
    public static int viewWidth;
    public static int viewHeight;

    private DownLoading downLoading;
    private View rootView;

    public static Handler mHandler;

    public DownloadingLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.popup_downloading, this);

        mHandler = new UIHandler(this);
        downLoading = (DownLoading) findViewById(R.id.downloading);
        rootView = findViewById(R.id.downloading_root_view);
        showDownLoading();
    }

    /**
     * 进度条进度刷新
     */
    private void showDownLoading() {
        downLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 更新进度条
     *
     * @param process
     */
    private void updateProcess(int process){
        downLoading.setProgress(process);
        if (100 == process) {
            downLoading.finishLoad();
        }
        if (-1 == process) {
            downLoading.setStop(true);
        }
    }

    /**
     * UI异步处理
     */
    public static final class UIHandler extends Handler {
        WeakReference<DownloadingLayout> weakReference;

        public UIHandler(DownloadingLayout downloadingLayout) {
            super();
            this.weakReference = new WeakReference<>(downloadingLayout);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            DownloadingLayout activity = weakReference.get();

            if (activity == null) {
                return;
            }

            // 开始处理更新UI
            switch (msg.what){
                case 1:
                    int process = (int)msg.obj;
                    activity.updateProcess(process);
                    break;
            }
        }
    }
}
