package com.mphotool.whiteboard.view.layerviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;

import com.mphotool.whiteboard.utils.Constants;

public class PenWidth extends View {
    private int mColor = -65536;
    private Paint mPaint = new Paint(1);
    private float mPenWidth = Constants.BORDER_WIDTH_MEDIUM;

    public PenWidth(Context context) {
        super(context);
    }

    public PenWidth(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PenWidth(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = 21)
    public PenWidth(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mPaint.setColor(this.mColor);
        this.mPaint.setStrokeWidth(0.01f);
        int width = getWidth();
        int height = getHeight();
        this.mPaint.setStyle(Style.FILL);
        canvas.drawCircle((float) (width / 2), (float) (height / 2), (this.mPenWidth * Constants.BORDER_WIDTH_MEDIUM) / 3.0f, this.mPaint);
    }

    public void setPenColor(int color) {
        this.mColor = color;
    }

    public void setPenWidth(float width) {
        this.mPenWidth = width;
    }
}
