package com.mphotool.whiteboard.view.transform;

import android.graphics.Matrix;

public interface OnTransformListener {
    boolean onContinueTransform(Matrix matrix);

    void onEndTransform(Matrix matrix);
}
