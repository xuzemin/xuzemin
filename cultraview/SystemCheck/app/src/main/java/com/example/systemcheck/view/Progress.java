package com.example.systemcheck.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.systemcheck.R;

public class Progress extends View {

    private Paint paint;
    private ObjectAnimator animator;
    private int progress;
    public Progress(Context context) {
        super(context);
        initview();
    }

    public Progress(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initview();
    }

    public Progress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initview();
    }

    public Progress(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    private void initview(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        animator = new ObjectAnimator();
        animator.setPropertyName("progress");
        animator.setTarget(this);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(getResources().getColor(R.color.white));
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);
        int height =getHeight()/2;
        int width=  getWidth()/2;
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectF,0,0,paint);
        paint.setColor(getResources().getColor(R.color.bg_load));
        int precent =getProgress()*(getWidth()/100);
        RectF rectF_n = new RectF(0, 0, precent, getHeight());
        canvas.drawRoundRect(rectF_n,0,0,paint);

        Log.d("hong", "onDraw: getProgress ="+getProgress());
        Log.d("hong", "onDraw: Height ="+getHeight());
        Log.d("hong", "onDraw: Width ="+getWidth());
    }
    public void reDraw() {
        invalidate();
    }
    public void setProgress(int i){
        this.progress = i;
    }
    public int getProgress(){
        return progress;
    }

}
