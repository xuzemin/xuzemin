package com.mphotool.whiteboard.view;


import android.util.Log;

import com.mphotool.whiteboard.utils.BaseUtils;

public abstract class BaseThread extends Thread {
    private static final String TAG = "BaseThread";
    protected volatile Thread mCurrentThread;
    protected volatile boolean mIsRunning;

    public void run() {
        this.mIsRunning = true;
        this.mCurrentThread = Thread.currentThread();
    }

    public void stopThread() {
        if (this.mIsRunning) {
            this.mIsRunning = false;
            if (this.mCurrentThread != null) {
                this.mCurrentThread.interrupt();
                this.mCurrentThread = null;
            }
        }
    }

    public boolean isRunning() {
        return this.mIsRunning && this.mCurrentThread != null && this.mCurrentThread.isAlive();
    }
}
