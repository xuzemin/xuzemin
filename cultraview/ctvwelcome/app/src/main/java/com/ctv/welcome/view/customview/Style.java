
package com.ctv.welcome.view.customview;

import android.graphics.Path;
import android.graphics.PointF;

public class Style {
    private int color;

    private int mode;

    private Path path;

    private PointF pointF;

    private int strokeWidth;

    public PointF getPointF() {
        return this.pointF;
    }

    public void setPointF(PointF pointF) {
        this.pointF = pointF;
    }

    public Path getPath() {
        return this.path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Style(int strokeWidth, int color, Path path) {
        this.strokeWidth = strokeWidth;
        this.color = color;
        this.path = path;
    }

    public Style(int strokeWidth, int color) {
        this.strokeWidth = strokeWidth;
        this.color = color;
    }

    public int getStrokeWidth() {
        return this.strokeWidth;
    }

    public int getColor() {
        return this.color;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
