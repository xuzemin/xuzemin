package com.mphotool.whiteboard.view.imgedit;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.mphotool.whiteboard.utils.Constants;

public class DrawingUtils {
    private static void drawIndicator(Canvas canvas, Drawable indicator, int indicatorSize, float centerX, float centerY) {
        int left = ((int) centerX) - (indicatorSize / 2);
        int top = ((int) centerY) - (indicatorSize / 2);
        indicator.setBounds(left, top, left + indicatorSize, top + indicatorSize);
        indicator.draw(canvas);
    }

    public static void drawEditIndicators(Canvas canvas, Drawable[] editIndicators, int indicatorSize, RectF bounds) {
        if (editIndicators.length >= 8) {
            drawIndicator(canvas, editIndicators[0], indicatorSize, bounds.left, bounds.top);
            drawIndicator(canvas, editIndicators[1], indicatorSize, bounds.centerX(), bounds.top);
            drawIndicator(canvas, editIndicators[2], indicatorSize, bounds.right, bounds.top);
            drawIndicator(canvas, editIndicators[3], indicatorSize, bounds.left - 2.0f, bounds.centerY());
            drawIndicator(canvas, editIndicators[4], indicatorSize, bounds.right + 2.0f, bounds.centerY());
            drawIndicator(canvas, editIndicators[5], indicatorSize, bounds.left, bounds.bottom);
            drawIndicator(canvas, editIndicators[6], indicatorSize, bounds.centerX(), bounds.bottom + Constants.MAX_SCALE_SIZE);
            drawIndicator(canvas, editIndicators[7], indicatorSize, bounds.right, bounds.bottom);
        }
    }

    public static void drawCropIndicators(Canvas canvas, Drawable[] cropIndicators, int indicatorSize, RectF bounds) {
        if (cropIndicators.length >= 8) {
            drawIndicator(canvas, cropIndicators[0], indicatorSize, (bounds.left + ((float) (indicatorSize / 2))) - Constants.MIN_WIDTH_ACTIVATE_ERASER, (bounds.top + ((float) (indicatorSize / 2))) - Constants.MIN_WIDTH_ACTIVATE_ERASER);
            drawIndicator(canvas, cropIndicators[1], indicatorSize, bounds.centerX(), (bounds.top + ((float) (indicatorSize / 2))) - Constants.MIN_WIDTH_ACTIVATE_ERASER);
            drawIndicator(canvas, cropIndicators[2], indicatorSize, (bounds.right - ((float) (indicatorSize / 2))) + Constants.MIN_WIDTH_ACTIVATE_ERASER, (bounds.top + ((float) (indicatorSize / 2))) - Constants.MIN_WIDTH_ACTIVATE_ERASER);
            drawIndicator(canvas, cropIndicators[3], indicatorSize, (bounds.left + ((float) (indicatorSize / 2))) - Constants.MIN_WIDTH_ACTIVATE_ERASER, bounds.centerY());
            drawIndicator(canvas, cropIndicators[4], indicatorSize, (bounds.right - ((float) (indicatorSize / 2))) + Constants.MIN_WIDTH_ACTIVATE_ERASER, bounds.centerY());
            drawIndicator(canvas, cropIndicators[5], indicatorSize, (bounds.left + ((float) (indicatorSize / 2))) - Constants.MIN_WIDTH_ACTIVATE_ERASER, (bounds.bottom - ((float) (indicatorSize / 2))) + Constants.MIN_WIDTH_ACTIVATE_ERASER);
            drawIndicator(canvas, cropIndicators[6], indicatorSize, bounds.centerX(), (bounds.bottom - ((float) (indicatorSize / 2))) + Constants.MIN_WIDTH_ACTIVATE_ERASER);
            drawIndicator(canvas, cropIndicators[7], indicatorSize, (bounds.right - ((float) (indicatorSize / 2))) + Constants.MIN_WIDTH_ACTIVATE_ERASER, (bounds.bottom - ((float) (indicatorSize / 2))) + Constants.MIN_WIDTH_ACTIVATE_ERASER);
        }
    }

    public static void drawShadows(Canvas canvas, Paint p, RectF innerBounds, RectF outerBounds) {
        canvas.drawRect(outerBounds.left, outerBounds.top, innerBounds.right, innerBounds.top, p);
        canvas.drawRect(innerBounds.right, outerBounds.top, outerBounds.right, innerBounds.bottom, p);
        canvas.drawRect(innerBounds.left, innerBounds.bottom, outerBounds.right, outerBounds.bottom, p);
        canvas.drawRect(outerBounds.left, innerBounds.top, innerBounds.left, outerBounds.bottom, p);
    }
}
