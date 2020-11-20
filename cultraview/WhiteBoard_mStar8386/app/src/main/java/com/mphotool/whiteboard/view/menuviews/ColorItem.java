package com.mphotool.whiteboard.view.menuviews;

public class ColorItem {
    public static final int COLOR_TYPE_CUSTOM = 1;
    public static final int COLOR_TYPE_DEFAULTS = 0;
    private int color;
    private int type;

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String toString() {
        return "color=" + this.color + ", type=" + this.type;
    }
}
