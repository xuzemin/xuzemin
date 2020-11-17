package com.ctv.sourcemenu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.mstar.android.tv.TvLanguage;


public class RectView extends View {

    private Context mContext;
    private Paint mPaint;
    private int mProgress;


    public RectView(Context context) {
        super(context);
        init(context);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

      init(context);
    }
    private void init(Context context){
        this.mContext = context;
        this.mPaint = new Paint();
        float width = context.getResources().getDimension(R.dimen.dp_12);
        this.mPaint.setStrokeWidth(width);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
    }


    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth()/2;
     this.mPaint.setColor(getResources().getColor(R.color.bg_rc));


//       mPaint.setColor(Color.BLACK);
//        this.mPaint.setColor(getResources().getColor(R.color.bg_load));
        float strokeWidth = this.mPaint.getStrokeWidth()/ 2.0f;
     //   float f = (float) width;
     //   Log.d("hhh", "onDraw: f  = "+f+"strokeWidth ="+strokeWidth);
//        canvas.drawCircle(f, f, 120, this.mPaint);
        this.mPaint.setColor(getResources().getColor(R.color.bg_load));

        float f2 = ((float) (width * 2)) - strokeWidth;
        canvas.drawArc(new RectF(strokeWidth, strokeWidth, f2, f2), -90.0f, (float) ((getProgress() * TvLanguage.PAPIAMENTO) / 50), false, this.mPaint);

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
