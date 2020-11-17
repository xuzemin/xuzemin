package com.ctv.annotation.view.dialog;

import android.content.Context;
import android.view.View;

public class AnimatedView extends View {
    private int target;

    public AnimatedView(Context context) {
        super(context);
    }

    public float getXFactor() {
        return getX() / ((float) this.target);
    }

    public void setXFactor(float xFactor) {
        setX(((float) this.target) * xFactor);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return this.target;
    }
}
