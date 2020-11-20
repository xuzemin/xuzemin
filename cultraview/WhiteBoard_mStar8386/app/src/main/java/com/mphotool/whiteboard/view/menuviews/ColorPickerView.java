package com.mphotool.whiteboard.view.menuviews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.ToofifiLog;

public class ColorPickerView extends View {
    private static final float BORDER_WIDTH_PX = 1.0f;
    private float HUE_PANEL_WIDTH;
    private float PALETTE_CIRCLE_TRACKER_RADIUS;
    private float PANEL_SPACING;
    private float RECTANGLE_TRACKER_OFFSET;
    private int mBorderColor;
    private Paint mBorderPaint;
    private float mDensity;
    private float mDrawingOffset;
    private RectF mDrawingRect;
    private float mHue;
    private Paint mHuePaint;
    private RectF mHueRect;
    private Shader mHueShader;
    private Paint mHueTrackerPaint;
    private OnColorChangedListener mListener;
    private float mSat;
    private Shader mSatShader;
    private Paint mSatValPaint;
    private RectF mSatValRect;
    private Paint mSatValTrackerPaint;
    private int mSliderTrackerColor;
    private Point mStartTouchPoint;
    private float mVal;
    private Shader mValShader;

    public interface OnColorChangedListener {
        void onColorChanged(int i);
    }

    public ColorPickerView(Context context) {
        this(context, null);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.HUE_PANEL_WIDTH = 22.0f;
        this.PANEL_SPACING = 6.0f;
        this.PALETTE_CIRCLE_TRACKER_RADIUS = 8.0f;
        this.RECTANGLE_TRACKER_OFFSET = Constants.BORDER_WIDTH_MEDIUM;
        this.mDensity = 1.0f;
        this.mHue = 360.0f;
        this.mSat = 0.0f;
        this.mVal = 0.0f;
        this.mSliderTrackerColor = -14935012;
        this.mBorderColor = -9539986;
        this.mStartTouchPoint = null;
        init();
    }

    private void init() {
        this.mDensity = getContext().getResources().getDisplayMetrics().density;
        this.PALETTE_CIRCLE_TRACKER_RADIUS *= this.mDensity;
        this.RECTANGLE_TRACKER_OFFSET *= this.mDensity;
        this.HUE_PANEL_WIDTH *= this.mDensity;
        this.PANEL_SPACING *= this.mDensity;
        this.mDrawingOffset = calculateRequiredOffset();
        initPaintTools();
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void initPaintTools() {
        this.mSatValPaint = new Paint();
        this.mSatValTrackerPaint = new Paint();
        this.mHuePaint = new Paint();
        this.mHueTrackerPaint = new Paint();
        this.mBorderPaint = new Paint();
        this.mSatValTrackerPaint.setStyle(Style.STROKE);
        this.mSatValTrackerPaint.setStrokeWidth(this.mDensity * Constants.BORDER_WIDTH_MEDIUM);
        this.mSatValTrackerPaint.setAntiAlias(true);
        this.mHueTrackerPaint.setColor(this.mSliderTrackerColor);
        this.mHueTrackerPaint.setStyle(Style.STROKE);
        this.mHueTrackerPaint.setStrokeWidth(this.mDensity * Constants.BORDER_WIDTH_MEDIUM);
        this.mHueTrackerPaint.setAntiAlias(true);
    }

    private float calculateRequiredOffset() {
        return 1.5f * Math.max(Math.max(this.PALETTE_CIRCLE_TRACKER_RADIUS, this.RECTANGLE_TRACKER_OFFSET), 1.0f * this.mDensity);
    }

    private int[] buildHueColorArray() {
        int[] hue = new int[Constants.CODE_SIP_USER_REJECTED];
        int count = 0;
        int i = hue.length - 1;
        while (i >= 0) {
            hue[count] = Color.HSVToColor(new float[]{(float) i, 1.0f, 1.0f});
            i--;
            count++;
        }
        return hue;
    }

    protected void onDraw(Canvas canvas) {
        if (this.mDrawingRect.width() > 0.0f && this.mDrawingRect.height() > 0.0f) {
            drawSatValPanel(canvas);
            drawHuePanel(canvas);
        }
    }

    private void drawSatValPanel(Canvas canvas) {
        RectF rect = this.mSatValRect;
        this.mBorderPaint.setColor(this.mBorderColor);
        canvas.drawRect(this.mDrawingRect.left, this.mDrawingRect.top, 1.0f + rect.right, 1.0f + rect.bottom, this.mBorderPaint);
        if (this.mValShader == null) {
            this.mValShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, -1, -16777216, TileMode.CLAMP);
        }
        this.mSatShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, -1, Color.HSVToColor(new float[]{this.mHue, 1.0f, 1.0f}), TileMode.CLAMP);
        this.mSatValPaint.setShader(new ComposeShader(this.mValShader, this.mSatShader, Mode.MULTIPLY));
        canvas.drawRect(rect, this.mSatValPaint);
        Point p = satValToPoint(this.mSat, this.mVal);
        this.mSatValTrackerPaint.setColor(-16777216);
        canvas.drawCircle((float) p.x, (float) p.y, this.PALETTE_CIRCLE_TRACKER_RADIUS - (1.0f * this.mDensity), this.mSatValTrackerPaint);
        this.mSatValTrackerPaint.setColor(-2236963);
        canvas.drawCircle((float) p.x, (float) p.y, this.PALETTE_CIRCLE_TRACKER_RADIUS, this.mSatValTrackerPaint);
    }

    private void drawHuePanel(Canvas canvas) {
        RectF rect = this.mHueRect;
        this.mBorderPaint.setColor(this.mBorderColor);
        canvas.drawRect(rect.left - 1.0f, rect.top - 1.0f, rect.right + 1.0f, 1.0f + rect.bottom, this.mBorderPaint);
        if (this.mHueShader == null) {
            this.mHueShader = new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, buildHueColorArray(), null, TileMode.CLAMP);
            this.mHuePaint.setShader(this.mHueShader);
        }
        canvas.drawRect(rect, this.mHuePaint);
        float rectHeight = (4.0f * this.mDensity) / Constants.BORDER_WIDTH_MEDIUM;
        Point p = hueToPoint(this.mHue);
        RectF r = new RectF();
        r.left = rect.left - this.RECTANGLE_TRACKER_OFFSET;
        r.right = rect.right + this.RECTANGLE_TRACKER_OFFSET;
        r.top = ((float) p.y) - rectHeight;
        r.bottom = ((float) p.y) + rectHeight;
        canvas.drawRoundRect(r, Constants.BORDER_WIDTH_MEDIUM, Constants.BORDER_WIDTH_MEDIUM, this.mHueTrackerPaint);
    }

    private Point hueToPoint(float hue) {
        RectF rect = this.mHueRect;
        float height = rect.height();
        Point p = new Point();
        p.y = (int) ((height - ((hue * height) / 360.0f)) + rect.top);
        p.x = (int) rect.left;
        return p;
    }

    private Point satValToPoint(float sat, float val) {
        RectF rect = this.mSatValRect;
        float height = rect.height();
        float width = rect.width();
        Point p = new Point();
        p.x = (int) ((sat * width) + rect.left);
        p.y = (int) (((1.0f - val) * height) + rect.top);
        return p;
    }

    private float[] pointToSatVal(float x, float y) {
        RectF rect = this.mSatValRect;
        float[] result = new float[2];
        float width = rect.width();
        float height = rect.height();
        if (x < rect.left) {
            x = 0.0f;
        } else if (x > rect.right) {
            x = width;
        } else {
            x -= rect.left;
        }
        if (y < rect.top) {
            y = 0.0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y -= rect.top;
        }
        result[0] = (1.0f / width) * x;
        result[1] = 1.0f - ((1.0f / height) * y);
        return result;
    }

    private float pointToHue(float y) {
        RectF rect = this.mHueRect;
        float height = rect.height();
        if (y < rect.top) {
            y = 0.0f;
        } else if (y > rect.bottom) {
            y = height;
        } else {
            y -= rect.top;
        }
        return 360.0f - ((y * 360.0f) / height);
    }

    public boolean onTouchEvent(MotionEvent event) {
        boolean update = false;
        switch (event.getAction()) {
            case 0:
                this.mStartTouchPoint = new Point((int) event.getX(), (int) event.getY());
                update = moveTrackersIfNeeded(event);
                break;
            case 1:
                this.mStartTouchPoint = null;
                update = moveTrackersIfNeeded(event);
                break;
            case 2:
                update = moveTrackersIfNeeded(event);
                break;
        }
        if (!update) {
            return super.onTouchEvent(event);
        }
        if (this.mListener != null) {
            this.mListener.onColorChanged(Color.HSVToColor(255, new float[]{this.mHue, this.mSat, this.mVal}));
        }
        invalidate();
        return true;
    }

    private boolean moveTrackersIfNeeded(MotionEvent event) {
        if (this.mStartTouchPoint == null) {
            return false;
        }
        int startX = this.mStartTouchPoint.x;
        int startY = this.mStartTouchPoint.y;
        if (this.mHueRect.contains((float) startX, (float) startY)) {
            this.mHue = pointToHue(event.getY());
            return true;
        } else if (!this.mSatValRect.contains((float) startX, (float) startY)) {
            return false;
        } else {
            float[] result = pointToSatVal(event.getX(), event.getY());
            this.mSat = result[0];
            this.mVal = result[1];
            return true;
        }
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 0;
        if (widthMode == 1073741824 && heightMode == 1073741824) {
            height = (int) (((float) width) - this.HUE_PANEL_WIDTH);
        }
        setMeasuredDimension(width, height);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mDrawingRect = new RectF();
        this.mDrawingRect.left = this.mDrawingOffset + ((float) getPaddingLeft());
        this.mDrawingRect.right = (((float) w) - this.mDrawingOffset) - ((float) getPaddingRight());
        this.mDrawingRect.top = this.mDrawingOffset + ((float) getPaddingTop());
        this.mDrawingRect.bottom = (((float) h) - this.mDrawingOffset) - ((float) getPaddingBottom());
        setUpSatValRect();
        setUpHueRect();
    }

    private void setUpSatValRect() {
        RectF dRect = this.mDrawingRect;
        float panelSide = (dRect.height() - Constants.BORDER_WIDTH_MEDIUM) - this.PANEL_SPACING;
        float left = dRect.left + 1.0f;
        float top = dRect.top + 1.0f;
        this.mSatValRect = new RectF(left, top, left + panelSide, top + panelSide);
    }

    private void setUpHueRect() {
        RectF dRect = this.mDrawingRect;
        this.mHueRect = new RectF((dRect.right - this.HUE_PANEL_WIDTH) + 1.0f, dRect.top + 1.0f, dRect.right - 1.0f, (dRect.bottom - 1.0f) - this.PANEL_SPACING);
    }

    public void setOnColorChangedListener(OnColorChangedListener listener) {
        this.mListener = listener;
    }

    public int getColor() {
        return Color.HSVToColor(255, new float[]{this.mHue, this.mSat, this.mVal});
    }

    public void setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        this.mHue = hsv[0];
        this.mSat = hsv[1];
        this.mVal = hsv[2];
        if (this.mListener != null) {
            this.mListener.onColorChanged(Color.HSVToColor(255, new float[]{this.mHue, this.mSat, this.mVal}));
        }
        invalidate();
    }
}
