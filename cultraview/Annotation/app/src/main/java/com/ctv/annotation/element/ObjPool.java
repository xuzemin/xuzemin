package com.ctv.annotation.element;

import android.util.Log;

public class ObjPool<T> {
    private static final String TAG = "ObjPoll";
    private int mCount;
    private int mInitSize;
    private int mMaxSize;
    private Object[] mObjBuffer;
    private int mSize;
    private int mStepSize;

    public ObjPool() {
        this.mInitSize = 128;
        this.mStepSize = 256;
        this.mMaxSize = 10240;
        this.mSize = 0;
        this.mCount = 0;
        this.mObjBuffer = new Object[this.mInitSize];
        this.mSize = this.mInitSize;
        this.mCount = 0;
    }

    public ObjPool(int initSize, int stepSize, int maxSize) {
        this.mInitSize = 128;
        this.mStepSize = 256;
        this.mMaxSize = 10240;
        this.mSize = 0;
        this.mCount = 0;
        this.mInitSize = initSize;
        this.mStepSize = stepSize;
        this.mMaxSize = maxSize;
        this.mObjBuffer = new Object[this.mInitSize];
        this.mSize = this.mInitSize;
        this.mCount = 0;
    }

    private boolean checkAnUpSize() {
        if (this.mCount >= this.mSize) {
            if (this.mSize >= this.mMaxSize) {
                return false;
            }
            Object[] newArray = new Object[(this.mSize + this.mStepSize)];
            System.arraycopy(this.mObjBuffer, 0, newArray, 0, this.mObjBuffer.length);
            this.mSize += this.mStepSize;
            this.mObjBuffer = newArray;
            Log.i(TAG, "Resize to " + this.mSize);
        }
        return true;
    }

    public synchronized int size() {
        return this.mCount;
    }

    public synchronized int currentCapcity() {
        return this.mSize;
    }

    public synchronized boolean contains(T obj) {
        boolean z = false;
        synchronized (this) {
            int count = 0;
            if (this.mCount > 0) {
                for (int i = 0; i < this.mSize && count < this.mCount; i++) {
                    if (this.mObjBuffer[i] != null) {
                        count++;
                        if (obj == this.mObjBuffer[i]) {
                            z = true;
                            break;
                        }
                    }
                }
            }
        }
        return z;
    }

    public synchronized Object take() {
        Object t = null;
        synchronized (this) {
            for (int i = 0; i < this.mSize; i++) {
                if (this.mObjBuffer[i] != null) {
                    t = this.mObjBuffer[i];
                    this.mObjBuffer[i] = null;
                    this.mCount--;
                    break;
                }
            }
        }
        return t;
    }

    public synchronized boolean put(T obj) {
        boolean z = false;
        synchronized (this) {
            if (checkAnUpSize()) {
                for (int i = 0; i < this.mSize; i++) {
                    if (this.mObjBuffer[i] == null) {
                        this.mObjBuffer[i] = obj;
                        this.mCount++;
                        z = true;
                        break;
                    }
                }
            }
        }
        return z;
    }
}
