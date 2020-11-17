
package com.ctv.welcome.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import com.ctv.welcome.R;

public class SubsectionProgress extends ProgressBar {
    private static final int CU = 10;

    private static final int TE_CU = 20;

    private static final int XI = 2;

    private static final int ZHONG = 6;

    private Bitmap indicatorBitmap;

    private int indicatorHeight;

    private int mHeight;

    private int mWidth;

    private OnChangeListener onChangeListener;

    private float progress;

    private int[] progresses;

    private Bitmap seekBarBitmap;

    private Paint seekBarPaint;

    private int seekBarWidth;

    private Paint textPaint;

    private String[] titles;

    public interface OnChangeListener {
        void onChang(int i);
    }

    public SubsectionProgress(Context context) {
        this(context, null);
    }

    public SubsectionProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubsectionProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.titles = new String[] {
                getResources().getString(R.string.thin), getResources().getString(R.string.medium),
                getResources().getString(R.string.thick),
                getResources().getString(R.string.super_thick)
        };
        init();
    }

    public void setTitles(String... titles) {
        this.titles = titles;
        invalidate();
    }

    private void init() {
        this.indicatorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.indicator);
        this.seekBarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seekbar);
        this.indicatorHeight = this.indicatorBitmap.getHeight();
        this.seekBarWidth = this.seekBarBitmap.getWidth();
        Log.d("chen","seekBarWidth :"+seekBarWidth);
        this.seekBarPaint = new Paint();
        this.textPaint = new Paint();
        this.textPaint.setColor(-1);
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(20.0f);
        this.textPaint.setTextAlign(Align.CENTER);
        this.progresses = new int[] {
                12, 71, 120, 170
        };
        this.progress = (float) this.progresses[0];
    }

    protected synchronized void onDraw(Canvas canvas) {
        drawSeekBar(canvas);
        drawText(canvas);
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < this.titles.length; i++) {
            canvas.drawText(this.titles[i], (float) this.progresses[i], 20.0f, this.textPaint);
        }
    }

    private void drawSeekBar(Canvas canvas) {
        canvas.drawBitmap(this.seekBarBitmap, 0.0f, 41.0f, this.seekBarPaint);
        if (this.progress - ((float) (this.indicatorBitmap.getWidth() / 2)) < 0.0f) {
            canvas.drawBitmap(this.indicatorBitmap, 0.0f, 30.0f, this.seekBarPaint);
        } else {
            canvas.drawBitmap(this.indicatorBitmap,
                    this.progress - ((float) (this.indicatorBitmap.getWidth() / 2)), 30.0f,
                    this.seekBarPaint);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case 0:
                this.progress = event.getX();
                invalidate();
                return true;
            case 1:
                float eventX2 = event.getX();
                if (eventX2 < ((float) (this.seekBarWidth / 4))) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(2);
                    }
                    this.progress = (float) this.progresses[0];
                    invalidate();
                    return true;
                } else if (eventX2 >= ((float) (this.seekBarWidth / 4))
                        && eventX2 < ((float) (this.seekBarWidth / 2))) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(6);
                    }
                    this.progress = (float) this.progresses[1];
                    invalidate();
                    return true;
                } else if (eventX2 >= ((float) (this.seekBarWidth / 2))
                        && eventX2 < ((float) ((this.seekBarWidth / 4) * 3))) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(10);
                    }
                    this.progress = (float) this.progresses[2];
                    invalidate();
                    return true;
                } else if (eventX2 < ((float) ((this.seekBarWidth / 4) * 3))) {
                    return true;
                } else {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(20);
                    }
                    this.progress = (float) this.progresses[3];
                    invalidate();
                    return true;
                }
            case 2:
                float eventX = event.getX();
                if (eventX >= ((float) this.seekBarWidth) || eventX <= 0.0f) {
                    return true;
                }
                this.progress = eventX;
                invalidate();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        this.mWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public int dp2px(float dp) {
        return (int) ((dp * getResources().getDisplayMetrics().density) + 0.5f);
    }

    public int px2dp(float px) {
        return (int) ((px / getResources().getDisplayMetrics().density) + 0.5f);
    }

    public int sp2px(float sp) {
        return (int) ((sp * getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }
}
