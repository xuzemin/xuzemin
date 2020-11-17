package com.ctv.annotation.view;

import android.util.Log;

public class BaseThread extends Thread {
    private static final String TAG = "BaseThread";
    protected volatile Thread mCurrentThread;
    protected volatile boolean mIsRunning;

    public void run() {
        this.mIsRunning = true;
        this.mCurrentThread = Thread.currentThread();
    }

    public void stopThread() {
        Log.d(TAG, "stopThread");
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
