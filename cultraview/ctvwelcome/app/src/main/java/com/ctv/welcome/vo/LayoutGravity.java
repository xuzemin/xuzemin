
package com.ctv.welcome.vo;

import android.util.Log;
import android.view.View;

public class LayoutGravity {
    public static final int ALIGN_ABOVE = 2;

    public static final int ALIGN_BOTTOM = 8;

    public static final int ALIGN_LEFT = 1;

    public static final int ALIGN_RIGHT = 4;

    public static final int CENTER_HORI = 256;

    public static final int CENTER_VERT = 512;

    public static final int TO_ABOVE = 32;

    public static final int TO_BOTTOM = 128;

    public static final int TO_LEFT = 16;

    public static final int TO_RIGHT = 64;

    private final int DEFAULT_EDITOR_HEIGHT = 120;

    private int layoutGravity;

    public LayoutGravity(int gravity) {
        this.layoutGravity = gravity;
    }

    public int getLayoutGravity() {
        return this.layoutGravity;
    }

    public void setLayoutGravity(int gravity) {
        this.layoutGravity = gravity;
    }

    public void setVertGravity(int gravity) {
        this.layoutGravity &= 682;
        this.layoutGravity |= gravity;
    }

    public void setHoriGravity(int gravity) {
        this.layoutGravity &= 341;
        this.layoutGravity |= gravity;
    }

    public boolean isParamFit(int param) {
        return (this.layoutGravity & param) > 0;
    }

    public int getHoriParam() {
        for (int i = 1; i <= 256; i <<= 2) {
            if (isParamFit(i)) {
                return i;
            }
        }
        return 0;
    }

    public int getVertParam() {
        for (int i = 2; i <= 512; i <<= 2) {
            if (isParamFit(i)) {
                return i;
            }
        }
        return 0;
    }

    public int[] getOffset(View anchor, View window, int xOffset, int yOffset) {
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int anchWidth = anchor.getWidth();
        int anchHeight = anchor.getHeight();
        int winWidth = window.getWidth();
        int x = 0;
        int y = 0;
        if (anchHeight == 0) {
            anchHeight = 120;
        }
        Log.d("tag", "getOffset,viewX:" + location[0] + ",viewY:" + location[1] + ",viewWidth:"
                + anchWidth + ",viewHeight:" + anchHeight);
        Log.d("tag", "getOffset,popWidth:" + winWidth);
        int horiParam = getHoriParam();
        Log.d("tag", "getOffset,horiParam:" + horiParam);
        switch (horiParam) {
            case 1:
                x = 0;
                break;
            case 4:
                x = anchWidth - winWidth;
                break;
            case 16:
                x = -winWidth;
                break;
            case 64:
                x = location[0];
                y = (location[1] + anchHeight) + yOffset;
                break;
            case 256:
                x = (anchWidth - winWidth) / 2;
                break;
        }
        return new int[] {
                x, y
        };
    }
}
