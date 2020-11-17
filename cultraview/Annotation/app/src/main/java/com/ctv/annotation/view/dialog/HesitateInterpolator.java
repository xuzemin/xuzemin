package com.ctv.annotation.view.dialog;

import android.view.animation.Interpolator;

import com.itextpdf.text.pdf.BaseField;

public class HesitateInterpolator implements Interpolator {
    private static double POW = 0.5d;

    HesitateInterpolator() {
    }

    public float getInterpolation(float input) {
        if (((double) input) < 0.5d) {
            return ((float) Math.pow((double) (input * BaseField.BORDER_WIDTH_MEDIUM), POW)) * 0.5f;
        }
        return (((float) Math.pow((double) ((1.0f - input) * BaseField.BORDER_WIDTH_MEDIUM), POW)) * -0.5f) + 1.0f;
    }
}
