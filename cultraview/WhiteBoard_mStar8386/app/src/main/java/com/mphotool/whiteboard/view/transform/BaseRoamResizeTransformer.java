package com.mphotool.whiteboard.view.transform;

import android.graphics.Matrix;
import android.view.MotionEvent;

public class BaseRoamResizeTransformer {
    protected Matrix mFinalMatrix;
    protected int mFirstPointerId;
    protected OnTransformListener mOnTransformListener;
    protected MotionEvent mPrevMotionEvent;
    protected int mSecondPointerId;

    public BaseRoamResizeTransformer(OnTransformListener listener) {
        this.mOnTransformListener = listener;
    }

    public void onTouch(MotionEvent event) {
        switch (event.getActionMasked()) {
            case 0:
                touchDown(event);
                return;
            case 1:
                touchUp(event);
                return;
            case 2:
                touchMove(event);
                return;
            case 5:
                touchPointerDown(event);
                return;
            case 6:
                touchPointerUp(event);
                return;
            default:
                return;
        }
    }

    protected void touchDown(MotionEvent event) {
        this.mFirstPointerId = event.getPointerId(event.getActionIndex());
        this.mPrevMotionEvent = MotionEvent.obtain(event);
        this.mFinalMatrix = new Matrix();
    }

    protected void touchPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            this.mSecondPointerId = event.getPointerId(event.getActionIndex());
            this.mPrevMotionEvent = MotionEvent.obtain(event);
            int firstPointerIndex = event.findPointerIndex(this.mFirstPointerId);
            if (firstPointerIndex < 0 || this.mFirstPointerId == this.mSecondPointerId) {
                this.mFirstPointerId = event.getPointerId(findNewActiveIndex(event, this.mSecondPointerId, firstPointerIndex));
            }
        }
    }

    protected void touchMove(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            continueScale(event);
        } else {
            continueMove(event);
        }
    }

    protected void touchPointerUp(MotionEvent event) {
        int pointerCount = event.getPointerCount();
        int actionIndex = event.getActionIndex();
        int actionId = event.getPointerId(actionIndex);
        if (pointerCount > 2) {
            int newIndex;
            if (actionId == this.mFirstPointerId) {
                newIndex = findNewActiveIndex(event, this.mSecondPointerId, actionIndex);
                if (newIndex >= 0) {
                    this.mFirstPointerId = event.getPointerId(newIndex);
                }
            } else if (actionId == this.mSecondPointerId) {
                newIndex = findNewActiveIndex(event, this.mFirstPointerId, actionIndex);
                if (newIndex >= 0) {
                    this.mSecondPointerId = event.getPointerId(newIndex);
                }
            }
            this.mPrevMotionEvent = MotionEvent.obtain(event);
        }
        if (actionId == this.mFirstPointerId) {
            this.mFirstPointerId = this.mSecondPointerId;
        }
    }

    protected void touchUp(MotionEvent event) {
        if (this.mOnTransformListener != null) {
            this.mOnTransformListener.onEndTransform(this.mFinalMatrix);
        }
        if (this.mPrevMotionEvent != null) {
            this.mPrevMotionEvent.recycle();
            this.mPrevMotionEvent = null;
        }
        this.mFirstPointerId = -1;
        this.mSecondPointerId = -1;
    }

    protected void continueScale(MotionEvent event) {
        int preFirstIndex = this.mPrevMotionEvent.findPointerIndex(this.mFirstPointerId);
        int preSecondIndex = this.mPrevMotionEvent.findPointerIndex(this.mSecondPointerId);
        float previousFirstX = this.mPrevMotionEvent.getX(preFirstIndex);
        float previousFirstY = this.mPrevMotionEvent.getY(preFirstIndex);
        float previousSecondX = this.mPrevMotionEvent.getX(preSecondIndex);
        float previousSecondY = this.mPrevMotionEvent.getY(preSecondIndex);
        int firstPointerIndex = event.findPointerIndex(this.mFirstPointerId);
        int secondPointerIndex = event.findPointerIndex(this.mSecondPointerId);
        float currentFirstX = event.getX(firstPointerIndex);
        float currentFirstY = event.getY(firstPointerIndex);
        float currentSecondX = event.getX(secondPointerIndex);
        float currentSecondY = event.getY(secondPointerIndex);
        float preDis = (float) Math.sqrt(Math.pow((double) (previousFirstX - previousSecondX), 2.0d) + Math.pow((double) (previousFirstY - previousSecondY), 2.0d));
        float curDis = (float) Math.sqrt(Math.pow((double) (currentFirstX - currentSecondX), 2.0d) + Math.pow((double) (currentFirstY - currentSecondY), 2.0d));
        float scaleRatio = curDis / preDis;
        if (scaleRatio > 1.0f) {
            scaleRatio = 1.0f + (((curDis - preDis) / preDis) / 2.0f);
        } else if (scaleRatio < 1.0f) {
            scaleRatio = 1.0f - (((preDis - curDis) / preDis) / 2.0f);
        }
        float deltaX1 = currentFirstX - previousFirstX;
        float deltaY1 = currentFirstY - previousFirstY;
        float deltaX2 = currentSecondX - previousSecondX;
        float deltaY2 = currentSecondY - previousSecondY;
        Matrix matrix = new Matrix();
        Matrix tempMatrix = new Matrix(this.mFinalMatrix);
        matrix.postTranslate((deltaX1 + deltaX2) / 2.0f, (deltaY1 + deltaY2) / 2.0f);
        tempMatrix.postTranslate((deltaX1 + deltaX2) / 2.0f, (deltaY1 + deltaY2) / 2.0f);
        matrix.postScale(scaleRatio, scaleRatio, (currentFirstX + currentSecondX) / 2.0f, (currentFirstY + currentSecondY) / 2.0f);
        tempMatrix.postScale(scaleRatio, scaleRatio, (currentFirstX + currentSecondX) / 2.0f, (currentFirstY + currentSecondY) / 2.0f);
        if (this.mOnTransformListener != null && this.mOnTransformListener.onContinueTransform(matrix)) {
            this.mPrevMotionEvent = MotionEvent.obtain(event);
            this.mFinalMatrix.set(tempMatrix);
        }
    }

    protected void continueMove(MotionEvent event) {
        int preFirstIndex = this.mPrevMotionEvent.findPointerIndex(this.mFirstPointerId);
        float previousFirstX = this.mPrevMotionEvent.getX(preFirstIndex);
        float previousFirstY = this.mPrevMotionEvent.getY(preFirstIndex);
        int firstPointerIndex = event.findPointerIndex(this.mFirstPointerId);
        float delX = event.getX(firstPointerIndex) - previousFirstX;
        float delY = event.getY(firstPointerIndex) - previousFirstY;
        Matrix matrix = new Matrix();
        Matrix tempMatrix = new Matrix(this.mFinalMatrix);
        matrix.setTranslate(delX, delY);
        tempMatrix.postTranslate(delX, delY);
        if (this.mOnTransformListener != null && this.mOnTransformListener.onContinueTransform(matrix)) {
            this.mPrevMotionEvent = MotionEvent.obtain(event);
            this.mFinalMatrix.set(tempMatrix);
        }
    }

    private int findNewActiveIndex(MotionEvent event, int otherActiveId, int removedPointerIndex) {
        int pointerCount = event.getPointerCount();
        int otherActiveIndex = event.findPointerIndex(otherActiveId);
        int i = 0;
        while (i < pointerCount) {
            if (i != removedPointerIndex && i != otherActiveIndex) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public Matrix getFinalMatrix() {
        return this.mFinalMatrix;
    }
}
