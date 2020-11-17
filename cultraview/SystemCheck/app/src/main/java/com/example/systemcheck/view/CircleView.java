package com.example.systemcheck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


import com.example.systemcheck.R;
import com.example.systemcheck.uitil.TvLanguage;


public class CircleView extends View {

    private Context mContext;
    private Paint mPaint;
    private int mProgress;

    public CircleView(Context context) {
        this(context, null);
    }

    public CircleView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CircleView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
        this.mPaint = new Paint();

        this.mPaint.setStrokeWidth(12.0f);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth() / 2;
       // this.mPaint.setColor(MainActivity.mCircleViewColorOne.getCurrentTextColor());
        this.mPaint.setColor(getResources().getColor(R.color.bg_rc));
        float strokeWidth = this.mPaint.getStrokeWidth() / 2.0f;
        float f = (float) width;
        canvas.drawCircle(f, f, (float) ((int) (f - strokeWidth)), this.mPaint);
       // this.mPaint.setColor(MainActivity.mCircleViewColorTwo.getCurrentTextColor());
        this.mPaint.setColor(getResources().getColor(R.color.bg_load));
        float f2 = ((float) (width * 2)) - strokeWidth;
        canvas.drawArc(new RectF(strokeWidth, strokeWidth, f2, f2), -90.0f, (float) ((getProgress() * TvLanguage.PAPIAMENTO) / 100), false, this.mPaint);
    }

    public void reDraw() {
        invalidate();
    }

    public int getProgress() {
        return this.mProgress;
    }

    public void setProgress(int i) {
        this.mProgress = i;
    }

}
