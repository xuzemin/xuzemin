package com.mphotool.whiteboard.view.imgedit;

import android.graphics.RectF;

import com.mphotool.whiteboard.elements.Image;

public interface OnImageEditListener {
    void onCropOk(Image imageShape, RectF preRectF, RectF cropRectF);

    void onCropCancel();

}
