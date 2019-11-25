
package com.ctv.welcome.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import com.ctv.welcome.R;

public class ColorProgress extends ProgressBar {
    private float colorBitmap;

    private Bitmap colorLineBitemap;

    private Paint colorPaint;

    private String[] colors;

    private Bitmap indicatorBitmap;

    private int indicatorHeight;

    private float indicatorwidth;

    private OnChangeListener onChangeListener;

    private float progress;

    private Bitmap seekBarBitmap;

    private Paint seekBarPaint;

    private int seekBarWidth;

    public interface OnChangeListener {
        void onChang(String str);
    }

    public ColorProgress(Context context) {
        this(context, null);
    }

    public ColorProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.colors = new String[] {
                "#FFFFFF", "#FF0000", "#FF7800", "#FFFC00", "#00FF0C", "#00FFFC", "#003CFF",
                "#D200FF", "#888688", "#000000"
        };
        init();
    }

    private void init() {
        this.indicatorBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.indicator);
        this.seekBarBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.seekbar);
        this.colorLineBitemap = BitmapFactory.decodeResource(getResources(), R.drawable.color_line);
        this.indicatorHeight = this.indicatorBitmap.getHeight();
        this.seekBarWidth = this.seekBarBitmap.getWidth();
        this.colorBitmap = (float) this.colorLineBitemap.getWidth();
        this.seekBarPaint = new Paint();
        this.colorPaint = new Paint();
        this.colorPaint.setAntiAlias(true);
        this.colorPaint.setStyle(Style.FILL);
        this.progress = (float) (this.seekBarWidth / 20);
    }

    protected synchronized void onDraw(Canvas canvas) {
        drawSeekBar(canvas);
        drawColor(canvas);
    }

    private void drawColor(Canvas canvas) {
        canvas.drawBitmap(this.colorLineBitemap, 0.0f, 0.0f, this.seekBarPaint);
    }

    private void drawSeekBar(Canvas canvas) {
        canvas.drawBitmap(this.seekBarBitmap, 0.0f, 36.0f, this.seekBarPaint);
        if (this.progress - ((float) (this.indicatorBitmap.getWidth() / 2)) < 0.0f) {
            canvas.drawBitmap(this.indicatorBitmap, 0.0f,
                    (float) ((36 - (this.indicatorHeight / 2)) + 10), this.seekBarPaint);
        } else {
            canvas.drawBitmap(this.indicatorBitmap,
                    this.progress - ((float) (this.indicatorBitmap.getWidth() / 2)),
                    (float) ((36 - (this.indicatorHeight / 2)) + 5), this.seekBarPaint);
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
                if (eventX2 < this.colorBitmap / 10.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[0]);
                    }
                    this.progress = (this.colorBitmap / 10.0f) / 2.0f;
                    invalidate();
                } else if (eventX2 >= this.colorBitmap / 10.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 2.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[1]);
                    }
                    this.progress = ((this.colorBitmap / 10.0f) + ((this.colorBitmap / 10.0f) * 2.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 2.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 3.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[2]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 2.0f) + ((this.colorBitmap / 10.0f) * 3.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 3.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 4.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[3]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 3.0f) + ((this.colorBitmap / 10.0f) * 4.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 4.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 5.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[4]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 4.0f) + ((this.colorBitmap / 10.0f) * 5.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 5.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 6.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[5]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 5.0f) + ((this.colorBitmap / 10.0f) * 6.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 6.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 7.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[6]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 6.0f) + ((this.colorBitmap / 10.0f) * 7.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 7.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 8.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[7]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 7.0f) + ((this.colorBitmap / 10.0f) * 8.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 8.0f
                        && eventX2 < (this.colorBitmap / 10.0f) * 9.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[8]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 8.0f) + ((this.colorBitmap / 10.0f) * 9.0f)) / 2.0f;
                    invalidate();
                } else if (eventX2 >= (this.colorBitmap / 10.0f) * 9.0f) {
                    if (this.onChangeListener != null) {
                        this.onChangeListener.onChang(this.colors[9]);
                    }
                    this.progress = (((this.colorBitmap / 10.0f) * 9.0f) + this.colorBitmap) / 2.0f;
                    invalidate();
                }
                return true;
            case 2:
                float eventX = event.getX();
                if (eventX < ((float) this.seekBarWidth) && eventX > 0.0f) {
                    this.progress = eventX;
                    invalidate();
                }
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public int dp2px(float dp) {
        return (int) ((dp * getResources().getDisplayMetrics().density) + 0.5f);
    }

    public int px2dp(float px) {
        return (int) ((px / getResources().getDisplayMetrics().density) + 0.5f);
    }

    public void setOnChangeListener(OnChangeListener onChangeListener) {
        this.onChangeListener = onChangeListener;
    }
}
