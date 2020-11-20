package com.mphotool.whiteboard.view.transform;

import android.graphics.Matrix;
import android.view.MotionEvent;

import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;


public class GlobalRoamResizeTransformer extends BaseRoamResizeTransformer {
    private static String TAG = "GlobalRoamResizeTransformer";
    private float mCurrSpan;
    private float mFocusX;
    private float mFocusY;
    public boolean mInProgress = false;
    private float mPrevSpan;

    public GlobalRoamResizeTransformer(OnTransformListener pListener) {
        super(pListener);
    }

    public void onTouch(MotionEvent event) {
        int action = event.getActionMasked();

        boolean streamComplete = (action == MotionEvent.ACTION_UP) || (action == MotionEvent.ACTION_CANCEL);
//        BaseUtils.dbg(TAG, "streamComplete = " + (streamComplete?"true":"false"));
        if (streamComplete) {
            this.mInProgress = false;
            if (this.mOnTransformListener != null) {
                this.mOnTransformListener.onEndTransform(this.mFinalMatrix);
            }
            return;
        }
        int div;
        int index;
        boolean configChanged = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP;
        boolean pointerUp = action == MotionEvent.ACTION_POINTER_UP;
        int skipIndex = pointerUp ? event.getActionIndex() : -1;
        float sumX = 0.0f;
        float sumY = 0.0f;
        int count = event.getPointerCount();

        if (pointerUp) {
            div = count - 1;
        } else {
            div = count;
        }
        for (index = 0; index < count; index++) {
            if (skipIndex != index) {
                sumX += event.getX(index);
                sumY += event.getY(index);
            }
        }
        float focusX = sumX / ((float) div);
        float focusY = sumY / ((float) div);

        float devSumX = 0.0f;
        float devSumY = 0.0f;
        for (index = 0; index < count; index++) {
            if (skipIndex != index) {
                devSumX += Math.abs(event.getX(index) - focusX);
                devSumY += Math.abs(event.getY(index) - focusY);
            }
        }
        float spanX = (devSumX / ((float) div)) * 2.0f;
        float spanY = (devSumY / ((float) div)) * 2.0f;
        float span = (float) Math.sqrt((double) ((spanX * spanX) + (spanY * spanY)));
        float transX = focusX - this.mFocusX;
        float transY = focusY - this.mFocusY;
        this.mFocusX = focusX;
        this.mFocusY = focusY;
        if (configChanged) {
            if (!this.mInProgress) {
                this.mFinalMatrix = new Matrix();
            }
            this.mCurrSpan = span;
            this.mPrevSpan = span;
        }
        if (action == MotionEvent.ACTION_MOVE) {
            this.mInProgress = true;
            this.mCurrSpan = span;
            float scaleFactor = getScaleFactor();
//            BaseUtils.dbg(TAG, "scaleFactor = " + scaleFactor);
            Matrix matrix = new Matrix();
            matrix.reset();
//            BaseUtils.dbg(TAG, "Math.abs(transX) = " + Math.abs(transX) +  " Math.abs(transY)=" + Math.abs(transY));
            if (Math.abs(transX) > 1.0f || Math.abs(transY) > 1.0f) {
                matrix.postTranslate(transX, transY);
            }
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

            if (this.mOnTransformListener != null && this.mOnTransformListener.onContinueTransform(matrix)) {
                this.mFinalMatrix.postConcat(matrix);
                this.mPrevSpan = this.mCurrSpan;
            }
        }
    }

    public float getScaleFactor() {
        boolean scaleUp = this.mCurrSpan > this.mPrevSpan;
        float spanDiff = 0.0f;
        if (this.mPrevSpan != 0.0f) {
            spanDiff = Math.abs(this.mCurrSpan - this.mPrevSpan) / 1200.0f;
        }
        if (spanDiff == Float.NaN || this.mPrevSpan <= 0.0f) {
            return 1.0f;
        }
        return scaleUp ? 1.0f + spanDiff : 1.0f - spanDiff;
    }
}
