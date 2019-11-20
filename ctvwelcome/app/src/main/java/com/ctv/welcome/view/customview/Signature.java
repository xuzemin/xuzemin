
package com.ctv.welcome.view.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;

import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import com.ctv.welcome.R;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Signature extends View {
    private static final float TOUCH_TOLERANCE = 2.0f;

    private float JUDGE_AREA;

    private int cacheMode;

    private Set<Style> drawPath;

    private int eraser;

    private Bitmap eraserBitmap;

    private Matrix eraserMatrix;

    private int eraserPointerId;

    private boolean isEraserVisible;

    private Bitmap mBitmap;

    private Canvas mCanvas;

    private Paint mEraserPaint;

    private int mHeight;

    private Paint mPaint;

    private PaintFlagsDrawFilter mSetfil;

    private int mWidth;

    private int mix_eraser;

    private int normal;

    private OnSigningListener onSigningListener;

    private Map<Integer, Style> pathMap;

    private LinearLayout penCon;

    private Style style;

    private float touchSize;

    public interface OnSigningListener {
        void onSigning(boolean z);
    }

    public Signature(Context context) {
        this(context, null);
    }

    public Signature(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Signature(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.pathMap = new HashMap();
        this.drawPath = new HashSet();
        this.JUDGE_AREA = 9.0f;
        this.normal = 0;
        this.eraser = 1;
        this.mix_eraser = 2;
        this.cacheMode = 0;
        this.isEraserVisible = false;
        this.eraserPointerId = -1;
        init();
    }

    private void init() {
        if (this.style == null) {
            this.style = new Style(2, -1);
        }
        setFocusableInTouchMode(true);
        this.mPaint = new Paint();
        this.mPaint.setColor(this.style.getColor());
        this.mPaint.setStyle(android.graphics.Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setFilterBitmap(true);
        this.mPaint.setStrokeWidth((float) this.style.getStrokeWidth());
        this.mPaint.setStrokeJoin(Join.ROUND);
        this.mPaint.setStrokeCap(Cap.ROUND);
        this.mSetfil = new PaintFlagsDrawFilter(0, 2);
        this.mEraserPaint = new Paint();
        this.mEraserPaint.setAlpha(0);
        this.mEraserPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        this.mEraserPaint.setAntiAlias(true);
        this.mEraserPaint.setDither(true);
        this.mEraserPaint.setStyle(android.graphics.Paint.Style.STROKE);
        this.mEraserPaint.setStrokeJoin(Join.ROUND);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        this.mWidth = dm.widthPixels;
        this.mHeight = dm.heightPixels;
        Log.d("Signature", "init,mWidth:" + this.mWidth + ",mHeight:" + this.mHeight);
        this.eraserBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eraser_hand);
        this.eraserMatrix = new Matrix();
        this.mEraserPaint.setStrokeWidth((float) this.eraserBitmap.getWidth());
    }

    public void setMode(int mode) {
        this.cacheMode = mode;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        Log.d("Signature", "onMeasure,width:" + width + ",height:" + height);
        this.mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        this.mCanvas = new Canvas(this.mBitmap);
        this.mCanvas.setDrawFilter(this.mSetfil);
    }

    protected void onDraw(Canvas canvas) {
        Set<Style> drawPath = new HashSet();
        Paint bmpPaint = new Paint();
        bmpPaint.setAntiAlias(true);
        bmpPaint.setDither(true);
        bmpPaint.setFilterBitmap(true);
        canvas.setDrawFilter(this.mSetfil);
        canvas.drawBitmap(this.mBitmap, 0.0f, 0.0f, bmpPaint);
        for (Entry<Integer, Style> entry : this.pathMap.entrySet()) {
            drawPath.add(entry.getValue());
        }
        for (Style style0 : drawPath) {
            this.mPaint.setColor(style0.getColor());
            this.mPaint.setStrokeWidth((float) style0.getStrokeWidth());
            if (!(style0.getMode() == this.eraser || style0.getMode() == this.mix_eraser)) {
                canvas.drawPath(style0.getPath(), this.mPaint);
            }
        }
        if (this.cacheMode == this.eraser) {
            if (this.isEraserVisible) {
                canvas.drawBitmap(this.eraserBitmap, this.eraserMatrix, null);
            }
        } else if (this.cacheMode == this.mix_eraser) {
            canvas.drawBitmap(this.eraserBitmap, this.eraserMatrix, null);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        int size = event.getPointerCount();
        this.touchSize = event.getTouchMajor();
        if (this.cacheMode == this.eraser) {
            this.eraserMatrix.reset();
            this.eraserMatrix.postTranslate(event.getX()
                    - ((float) (this.eraserBitmap.getWidth() / 2)), event.getY()
                    - ((float) (this.eraserBitmap.getHeight() / 2)));
        } else if (this.cacheMode == this.normal) {
            if (this.touchSize >= this.JUDGE_AREA) {
                this.cacheMode = this.mix_eraser;
                this.eraserMatrix.reset();
                this.eraserMatrix.postTranslate(
                        event.getX() - ((float) (this.eraserBitmap.getWidth() / 2)), event.getY()
                                - ((float) (this.eraserBitmap.getHeight() / 2)));
            }
        } else if (this.cacheMode == this.mix_eraser) {
            this.eraserMatrix.reset();
            this.eraserMatrix.postTranslate(event.getX()
                    - ((float) (this.eraserBitmap.getWidth() / 2)), event.getY()
                    - ((float) (this.eraserBitmap.getHeight() / 2)));
        }
        Log.d("Signature", "onTouchEvent,point count:" + size);
        Log.d("Signature", "onTouchEvent,action:" + event.getActionMasked() + ",actionIndex:"
                + event.getActionIndex());
        int pointerId;
        switch (event.getActionMasked()) {
            case 0:
                pointerId = event.getPointerId(event.getActionIndex());
                if (this.penCon != null) {
                    this.penCon.setVisibility(GONE);
                }
                if (this.cacheMode != this.eraser) {
                    addPath(pointerId, event.getX(event.getActionIndex()),
                            event.getY(event.getActionIndex()), this.cacheMode);
                    break;
                }
                this.isEraserVisible = true;
                addPath(pointerId, event.getX(event.getActionIndex()),
                        event.getY(event.getActionIndex()), this.cacheMode);
                break;
            case 1:
                pointerId = event.getPointerId(event.getActionIndex());
                if (this.cacheMode != this.mix_eraser) {
                    if (this.cacheMode != this.eraser) {
                        drawPenLine(pointerId);
                        this.pathMap.remove(Integer.valueOf(pointerId));
                        break;
                    }
                    this.isEraserVisible = false;
                    this.pathMap.remove(Integer.valueOf(pointerId));
                    break;
                }
                this.cacheMode = this.normal;
                this.pathMap.remove(Integer.valueOf(pointerId));
                break;
            case 2:
                if (this.onSigningListener != null) {
                    this.onSigningListener.onSigning(true);
                }
                if (this.cacheMode != this.eraser) {
                    for (int i = 0; i < size; i++) {
                        drawPenPath(event.getPointerId(i), event.getX(i), event.getY(i));
                    }
                    break;
                }
                drawPenPath(event.getPointerId(0), event.getX(0), event.getY(0));
                break;
            case 3:
            case 6:
                pointerId = event.getPointerId(event.getActionIndex());
                if (this.cacheMode != this.eraser) {
                    drawPenLine(pointerId);
                    this.pathMap.remove(Integer.valueOf(pointerId));
                    break;
                }
                this.pathMap.remove(Integer.valueOf(pointerId));
                break;
            case 5:
                pointerId = event.getPointerId(event.getActionIndex());
                if (this.penCon != null) {
                    this.penCon.setVisibility(GONE);
                }
                if (this.cacheMode != this.eraser) {
                    addPath(pointerId, event.getX(event.getActionIndex()),
                            event.getY(event.getActionIndex()), this.cacheMode);
                    break;
                }
                break;
        }
        invalidate();
        return true;
    }

    private void drawPenPath(int pointerId, float x, float y) {
        Style style = (Style) this.pathMap.get(Integer.valueOf(pointerId));
        if (style != null) {
            style.setMode(this.cacheMode);
            Path path = style.getPath();
            PointF pointF = style.getPointF();
            this.mPaint.setColor(style.getColor());
            this.mPaint.setStrokeWidth((float) style.getStrokeWidth());
            float dx = Math.abs(x - pointF.x);
            float dy = Math.abs(y - pointF.y);
            double s = Math.sqrt(Math.pow((double) (x - pointF.x), 2.0d)
                    + Math.pow((double) (y - pointF.y), 2.0d));
            if (path != null && s >= 2.0d) {
                path.quadTo(pointF.x, pointF.y, (pointF.x + x) / TOUCH_TOLERANCE, (pointF.y + y)
                        / TOUCH_TOLERANCE);
                pointF.x = x;
                pointF.y = y;
                if (style.getMode() == this.eraser || style.getMode() == this.mix_eraser) {
                    this.mCanvas.drawPath(path, this.mEraserPaint);
                }
            }
        } else if (this.cacheMode == this.eraser) {
            addPath(pointerId, x, y, this.cacheMode);
        }
    }

    private void drawPenLine(int pointerId) {
        Style style = (Style) this.pathMap.get(Integer.valueOf(pointerId));
        style.setMode(this.cacheMode);
        Path path = style.getPath();
        this.mPaint.setColor(style.getColor());
        this.mPaint.setStrokeWidth((float) style.getStrokeWidth());
        if (style.getMode() != this.eraser && style.getMode() != this.mix_eraser) {
            this.mCanvas.drawPath(path, this.mPaint);
        }
    }

    public void addPath(int pointerId, float x, float y, int mode) {
        Path path = new Path();
        Style style0 = new Style(this.style.getStrokeWidth(), this.style.getColor());
        style0.setPointF(new PointF(x, y));
        style0.setPath(path);
        style0.setMode(mode);
        path.moveTo(x, y);
        this.pathMap.put(Integer.valueOf(pointerId), style0);
    }

    public void clearPath() {
        this.pathMap.clear();
        this.drawPath.clear();
        if (!(this.mBitmap == null || this.mBitmap.isRecycled())) {
            this.mBitmap.recycle();
        }
        this.mBitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, Config.ARGB_8888);
        this.mCanvas = new Canvas(this.mBitmap);
        invalidate();
    }

    public void setColor(int color) {
        this.style.setColor(color);
        this.mPaint.setColor(color);
        invalidate();
    }

    public void setColor(String color) {
        this.style.setColor(Color.parseColor(color));
        this.mPaint.setColor(Color.parseColor(color));
        invalidate();
    }

    public void setStrokeWidth(int width) {
        this.style.setStrokeWidth(width);
        this.mPaint.setStrokeWidth((float) width);
        invalidate();
    }

    public void setPenConGone(LinearLayout penCon) {
        this.penCon = penCon;
    }

    public void setOnSigningListener(OnSigningListener onSigningListener) {
        this.onSigningListener = onSigningListener;
    }

    public void setTouchAreaJudge(float mTouchAreaJudge) {
        this.JUDGE_AREA = mTouchAreaJudge;
    }
}
