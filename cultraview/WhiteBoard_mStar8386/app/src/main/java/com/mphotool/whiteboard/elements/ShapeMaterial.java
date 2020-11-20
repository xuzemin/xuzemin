package com.mphotool.whiteboard.elements;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.EmbossMaskFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.SharedPreferencesUtils;
import com.mphotool.whiteboard.utils.StatusEnum;
import com.mphotool.whiteboard.utils.ToofifiLog;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ShapeMaterial extends Material {
    private static final String TAG = ShapeMaterial.class.getSimpleName();

    protected boolean mShowRect = false;

    protected DashPathEffect mDashPathEffect;
    //    private transient Paint bgPaint;
    protected transient Paint mPaint = new Paint();
    protected transient Paint mRectPaint = new Paint(mPaint);
    protected transient Path mPath = new Path();

    protected boolean mIsFinished = false;

    protected transient Path cachePath = new Path();

    /**
     * 产生的画笔集合
     */
    protected ShapeMaterial mBrushes;

    // 起始点X、Y
    protected float startX;
    protected float startY;
    protected float cx;
    protected float cy;

    protected float midX;
    protected float mLastX = -1.0f;
    protected float mLastY = -1.0f;
    protected float midY;
    protected float mLastEndX, mLastEndY;
    protected transient float mPreviousX;
    protected transient float mPreviousY;


    protected boolean mIsSelected;

    // 绘图的边框
    protected static RectF sBoundRectF = new RectF();
    protected static Rect sBoundRect = new Rect();
    protected static RectF sShapeRectF = new RectF();
    protected Rect mBoundRect = new Rect();
    protected RectF mShapeRectF = new RectF();

    protected Ink mInk;// 记录数据点:形状的顶点数据
    protected Ink mVector = new Ink();

    // opengl绘制
    protected static final Object INK_VERTEX_LOCK = new Object();
    protected int mGLPointCount;
    protected float[] mGLPoints;
    protected FloatBuffer mGLVertexPointsBuffer;
    protected boolean mIsOpenGLEnabled = BuildConfig.enable_opengl;
    protected float mGreen;
    protected float mBlue;
    protected float mRed;
    protected boolean isSameWidth = (BuildConfig.transform_type == 0);

    protected static final PathMeasure P_MEASURE = new PathMeasure();

    public ShapeMaterial()
    {
        initInk(Color.WHITE, Constants.PEN_WIDTH_LITTLE);
        initVector(Color.WHITE, Constants.PEN_WIDTH_LITTLE);
        initPaint();
        initOpenGL();
    }

    public ShapeMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
        this.mManager = manager;
        initInk(mBrushColor, mBrushThickness);
        initVector(mBrushColor, mBrushThickness);
        initPaint();
        initOpenGL();
    }

    /**
     * 初始化数据信息
     *
     * @param color
     * @param thickness
     */
    protected void initInk(int color, float thickness) {
        mInk = new Ink();
        mInk.mPoints = new Point[100];
        mInk.mInkColor = color;
        mInk.mThickness = thickness;

        this.mRed = ((float) Color.red(this.mInk.mInkColor)) / 255.0f;
        this.mGreen = ((float) Color.green(this.mInk.mInkColor)) / 255.0f;
        this.mBlue = ((float) Color.blue(this.mInk.mInkColor)) / 255.0f;
    }

    /**
     * 初始化定点坐标
     *
     * @param color
     * @param thickness
     */
    protected void initVector(int color, float thickness) {
    }

    private void initOpenGL() {
        if (this.mIsOpenGLEnabled) {
//            this.mGLPoints = new float[100];
        }
    }

    /**
     * 处理Inks
     */
    protected void handleInks() {
    }

    /**
     * 处理定点数据
     */
    protected void handleVector() {
    }

    /**
     * 数据拆分
     *
     * @param polygon
     * @return
     */
    public List<ShapeMaterial> splitShape(Polygon polygon) {
        List<ShapeMaterial> inks = null;
        RectF pathRectf = sBoundRectF;
        mPath.computeBounds(pathRectf, false);
        Rect pathRect = sBoundRect;
        pathRect.set((int) pathRectf.left, (int) pathRectf.top, (int) pathRectf.right, (int) pathRectf.bottom);
        Rect polygonRect = polygon.getRect();
        if (Rect.intersects(pathRect, polygonRect)) {
            inks = new ArrayList();
            Side side = null;
            byte mark = (byte) 0;
//            BaseUtils.dbg(TAG, " mInk.mPointCount=" + mInk.mPointCount);
            int MaterialPointCount = 0;
            for (int i = 0; i < mInk.mPointCount; i++) {
                Point point = mInk.mPoints[i];
                if (point != null) {
                    int rectIndex = -1;
                    if (polygon.containsInmTempRect((int) point.mX, (int) point.mY)) {
                        rectIndex = polygon.containsInRect((int) point.mX, (int) point.mY);
                    }
//                    BaseUtils.dbg(TAG, " rectIndex=" + rectIndex);
                    Point p;
                    if (rectIndex != -1) {
                        MaterialPointCount++;
                        if (MaterialPointCount == mInk.mPointCount)
                            Constants.mIsEraser = true;
                        p = point;
                        if (i == 0) {
                            side = new Inside(inks);
                        } else if (side instanceof Outside) {
                            int distance = (int)Math.sqrt((double) (((mInk.mPoints[i - 1].mX - point.mX) * (mInk.mPoints[i - 1].mX - point.mX)) + ((mInk.mPoints[i - 1].mY - point.mY) * (mInk.mPoints[i - 1].mY - point.mY))));
//                            BaseUtils.dbg(TAG, "00000 distance=" + distance);
                            if(distance > Constants.MAX_DISTANCE_BETWEEN_POINT){
                                side.end(mInk.mPoints[i - 1]);
                                side = new Inside(inks);
                            }else {
                                side = side.next(computeInterSectPoint(polygon, point, mInk.mPoints[i - 1], rectIndex), true, false);
                            }
                        }
                        mark = (byte) (mark | 1);
                    } else {
                        if (i == 0) {
                            ShapeMaterial ink = getInkWithColorAndThickness();
                            ink.startStroke(point.mX, point.mY);
                            ink.startOnPoint(new Point(point.mX, point.mY, false));
                            side = new Outside(ink, inks);
                        } else {
                            p = point;
                            if (side instanceof Inside) {
                                p = computeInterSectPoint(polygon, mInk.mPoints[i - 1], point, rectIndex);
                            }
                            int distance = (int)Math.sqrt((double) (((mInk.mPoints[i - 1].mX - point.mX) * (mInk.mPoints[i - 1].mX - point.mX)) + ((mInk.mPoints[i - 1].mY - point.mY) * (mInk.mPoints[i - 1].mY - point.mY))));
                            if(distance > Constants.MAX_DISTANCE_BETWEEN_POINT){
                                side.end(mInk.mPoints[i - 1]);
                                ShapeMaterial ink = getInkWithColorAndThickness();
                                ink.startStroke(point.mX, point.mY);
                                ink.startOnPoint(new Point(point.mX, point.mY, false));
                                side = new Outside(ink, inks);

                            }else {
                                side = side.next(p, false, p.mIsFalsePoint);
                            }
                        }
                        mark = (byte) (mark | 2);
                    }
                    if (i == mInk.mPointCount - 1) {
                        if (mark == (byte) 3) {
                            side.end(point);
                        } else if (mark == (byte) 2) {
                            inks = null;
                        }
                    }
                }
            }
        }
        return inks;
    }

    protected ShapeMaterial getInkWithColorAndThickness() {
        return new ShapeMaterial(mManager, mInk.mInkColor, mInk.mThickness);
    }

    /**
     * 添加线段
     *
     * @param startP
     * @param endP
     */
    protected void addPointsBySegment(Point startP, Point endP) {
        // 添加起点
        savePointsToArray(startP);
        insertFalsePoints(startP, endP);
        savePointsToArray(endP);
    }

    /**
     * 保存点
     *
     * @param point
     */
    protected void savePointsToArray(Point point) {
        enlargePointArrayIfNecessary();
        Point[] pointArr = mInk.mPoints;
        Ink ink = mInk;
        int i = ink.mPointCount;
        ink.mPointCount = i + 1;
        pointArr[i] = point;

        if (this.mIsOpenGLEnabled && !point.mIsFalsePoint) {
            enlargeGlPointIfNecessary();
            this.mGLPoints[this.mGLPointCount * 2] = point.mX;
            this.mGLPoints[(this.mGLPointCount * 2) + 1] = point.mY;
            this.mGLPointCount++;
        }
    }

    private void enlargePointArrayIfNecessary(int len) {
        if (mInk.mPointCount >= mInk.mPoints.length) {
            Point[] points = new Point[(mInk.mPoints.length + len + 10)];
            System.arraycopy(mInk.mPoints, 0, points, 0, mInk.mPointCount);
            mInk.mPoints = points;
        }
    }

    private void enlargePointArrayIfNecessary() {
        if (mInk.mPointCount >= mInk.mPoints.length) {
            Point[] points = new Point[(mInk.mPoints.length + 100)];
            System.arraycopy(mInk.mPoints, 0, points, 0, mInk.mPointCount);
            mInk.mPoints = points;
        }
    }

    private void enlargeGlPointIfNecessary() {
        if (this.mGLPointCount * 2 >= this.mGLPoints.length) {
            float[] values = new float[(this.mGLPoints.length + 200)];
            System.arraycopy(this.mGLPoints, 0, values, 0, this.mGLPointCount * 2);
            this.mGLPoints = values;
        }
    }

    /**
     * 初始化画笔
     */
    protected void initPaint() {
        // 绘制path 画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mInk.mThickness);
        mPaint.setColor(mInk.mInkColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
//        mPaint.setMaskFilter(new BlurMaskFilter(0.5f, BlurMaskFilter.Blur.SOLID));
//        mPaint.setMaskFilter(new EmbossMaskFilter(new float[]{30, 30, 30}, 0.2f, 60, 80));
        //mPaint.setDither(true);
        // 绘制方框 画笔
        mRectPaint = new Paint();
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setColor(Color.argb(112, 64, 196, 255));
        float margin = mPaint.getStrokeWidth();

        /**指定虚线样式*/
        mDashPathEffect = new DashPathEffect(new float[]{margin, margin * 1.5f, margin, margin * 1.5f}, 1.0f);
        mRectPaint.setPathEffect(mDashPathEffect);
    }

    /**
     * 显示方框
     *
     * @param s
     */
    public void showRect(boolean s) {
        mShowRect = s;
    }

    public boolean selected() {
        return mShowRect;
    }

    @Override
    public void draw(Canvas canvas) {
//        BaseUtils.dbg(TAG, "draw");
        int mColor = (int) SharedPreferencesUtils.getParam(Constants.BG_COLOLR, Constants.BG_COLOLR_DEFAULT);
        Point p1 = new Point(0.0f,0.0f),p2 = new Point(0.0f,0.0f),p3 = new Point(0.0f,0.0f);
        float radian = 0.0f,radian1 = 0.0f,radian2 = 0.0f,radius = 30.0f;
        String rccen = "";

        int shapeType = mManager.getShapeType();
        DecimalFormat decimalFormat=new DecimalFormat("0.0");
        if(shapeType == StatusEnum.STATUS_SHAPE_TRIANGLE) {
            p1 = new Point(startX + ((midX - startX) / 2.0f), startY);
            p2 = new Point(midX, midY);
            p3 = new Point(startX, midY);
            radian1 = ShapeMaterial.calAngle(p1, p2);
            radian2 = ShapeMaterial.calAngle(p1, p3);
            radian = Math.abs(radian2 - radian1);
            rccen = decimalFormat.format(radian);
            radius = (float) (Math.sqrt((p1.mX - p2.mX)*(p1.mX - p2.mX) + (p1.mY - p2.mY)*(p1.mY - p2.mY))/6);
        }else if(shapeType == StatusEnum.STATUS_SHAPE_RIGHT_TRIANGLE) {
            p1 = new Point(startX, startY);
            p2 = new Point(midX, midY);
            p3 = new Point(startX, midY);
            radian1 = ShapeMaterial.calAngle(p1, p2);
            radian2 = ShapeMaterial.calAngle(p1, p3);
            radian = Math.abs(radian2 - radian1);
            rccen = decimalFormat.format(radian);
            radius = (float) (Math.sqrt((p1.mX - p3.mX)*(p1.mX - p3.mX) + (p1.mY - p3.mY)*(p1.mY - p3.mY))/6);
        }else if(shapeType == StatusEnum.STATUS_SHAPE_OVAL) {
            float x = Math.abs(startX - midX);
            float y = Math.abs(startY - midY);
            float rccentricity = 0.0f;
            p1 = new Point(startX, startY);
            p2 = new Point(midX, midY);
            if(x > y) {
                if(x != 0)
                    rccentricity = (y/2)/(x/2);
                else
                    rccentricity = 0;
            }else{
                if(y != 0)
                    rccentricity = (x/2)/(y/2);
                else
                    rccentricity = 0;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("T=");
            stringBuilder.append(decimalFormat.format(rccentricity));
            rccen = stringBuilder.toString();
            if(p1.mX < p2.mX) {
                if(p1.mY < p2.mY)
                    p3 = new Point(startX + x/2, startY + y/2);
                else
                    p3 = new Point(startX + x/2, startY - y/2);
            }else{
                if(p1.mY < p2.mY)
                    p3 = new Point(startX - x/2, startY + y/2);
                else
                    p3 = new Point(startX - x/2, startY - y/2);
            }
        }/*else if(shapeType == StatusEnum.STATUS_SHAPE_CIRCLE) {
            float x = Math.abs(startX - midX);
            float y = Math.abs(startY - midY);
            float rccentricity = 0.0f;
            p1 = new Point(startX, startY);
            p2 = new Point(midX, midY);
            if(x > y){
                rccentricity = y/2.0f;
            }else{
                rccentricity = x/2.0f;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("R=");
            stringBuilder.append(decimalFormat.format(rccentricity));
            rccen = stringBuilder.toString();
            if(p1.mX < p2.mX) {
                if(p1.mY < p2.mY)
                    p3 = new Point(startX + x/2, startY + y/2);
                else
                    p3 = new Point(startX + x/2, startY - y/2);
            }else{
                if(p1.mY < p2.mY)
                    p3 = new Point(startX - x/2, startY + y/2);
                else
                    p3 = new Point(startX - x/2, startY - y/2);
            }
        }*/
        // 选中时
        if (isSelected()) {
            Paint pe = new Paint();
            pe.setAntiAlias(true);
            pe.setStrokeWidth(mPaint.getStrokeWidth() + 4.0f);
            pe.setColor(mPaint.getColor() ^ Constants.INK_SELECTED_COLOR_MASK);
            pe.setStyle(Paint.Style.STROKE);
            pe.setStrokeJoin(Paint.Join.ROUND);
            pe.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawPath(mPath, pe);
        }
        // path形状为矩形
        canvas.drawPath(this.mPath, mPaint);

        if (mShowRect) {
            canvas.drawPath(cachePath, mRectPaint);

            Paint paint=new Paint();
            paint.setColor(mColor ^ Constants.INK_SELECTED_COLOR_MASK);  //设置画笔颜色

            paint.setStrokeWidth (5);//设置画笔宽度
            paint.setAntiAlias(true); //指定是否使用抗锯齿功能，如果使用，会使绘图速度变慢
            paint.setTextSize(14);//设置文字大小

            //绘图样式，设置为填充
            paint.setStyle(Paint.Style.FILL);

            //圆弧画笔
            Paint pai=new Paint();
            pai.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            pai.setColor(mRectPaint.getColor() ^ Constants.INK_SELECTED_COLOR_MASK);
            pai.setStrokeWidth(3.0f);
            if(shapeType == StatusEnum.STATUS_SHAPE_TRIANGLE && radian >= 25.0f) {
                RectF mRectf = new RectF();
                mRectf.set(p1.mX - radius,p1.mY - radius,p1.mX + radius,p1.mY + radius);
                if (p1.mY < p2.mY) {
                    canvas.drawText(rccen, p1.mX - 12.0f, p1.mY + (4.0f*radius/5.0f), paint);
                    if(p1.mX < p2.mX)
                        canvas.drawArc(mRectf,radian1,radian,false,pai);
                    else
                        canvas.drawArc(mRectf,radian1,-radian,false,pai);
                }else {
                    canvas.drawText(rccen, p1.mX - 12.0f, p1.mY - (4.0f*radius/5.0f), paint);
                    if(p1.mX < p2.mX)
                        canvas.drawArc(mRectf,radian2,radian,false,pai);
                    else
                        canvas.drawArc(mRectf,radian2,-radian,false,pai);
                }
            }else if(shapeType == StatusEnum.STATUS_SHAPE_RIGHT_TRIANGLE && radian >= 25.0f) {
                RectF mRectf = new RectF();
                mRectf.set(p1.mX - radius,p1.mY - radius,p1.mX + radius,p1.mY + radius);
                if (p1.mY < p2.mY)
                    if (p1.mX < p2.mX) {
                        canvas.drawText(rccen, p1.mX + radius/5.0f, p1.mY + radius, paint);
                        canvas.drawArc(mRectf,radian1,radian,false,pai);
                    }else {
                        canvas.drawText(rccen, p1.mX - radius, p1.mY + radius, paint);
                        canvas.drawArc(mRectf,radian1,-radian,false,pai);
                    }
                else
                    if (p1.mX < p2.mX) {
                        canvas.drawText(rccen, p1.mX + radius/5.0f, p1.mY - radius, paint);
                        canvas.drawArc(mRectf,radian2,radian,false,pai);
                    }else {
                        canvas.drawText(rccen, p1.mX - radius, p1.mY - radius, paint);
                        canvas.drawArc(mRectf,radian2,-radian,false,pai);
                    }
            }else if(shapeType == StatusEnum.STATUS_SHAPE_OVAL
            /*|| shapeType == StatusEnum.STATUS_SHAPE_CIRCLE*/) {
                canvas.drawText(rccen, p3.mX, p3.mY, paint);
            }
        }

    }

    /**
     * 处理touch事件
     *
     * @param toX
     * @param toY
     * @param action
     */
    public void handleTouch(float toX, float toY, int action) {
        // 处理点
        handlePoint(toX, toY, action);
        // 处理path
        handleCachePath();
        handlePath();

        // 获得轨迹点
        if (action == MotionEvent.ACTION_UP) {
            mBrushes = new ShapeMaterial(mManager, mVector.mInkColor, mVector.mThickness);
            handleInks();
        }
    }

    /**
     * 处理点
     *
     * @param toX
     * @param toY
     * @param action
     */
    protected void handlePoint(float toX, float toY, int action) {
        boolean up = true;
        if (action == MotionEvent.ACTION_DOWN) {
            startX = toX;
            startY = toY;

            mLastX = toX;
            mLastY = toY;

            mLastEndX = toX;
            mLastEndY = toY;

            this.mPath.close();
            this.cachePath.close();
            return;
        }
        if (action != MotionEvent.ACTION_UP) {
            up = false;
        }

        cx = mLastX;
        cy = mLastY;
        if (up) {
            cx = (mLastX + toX) / 2.0f;
            cy = (mLastY + toY) / 2.0f;
            midX = toX;
            midY = toY;
        } else {
            midX = (cx + toX) / 2.0f;
            midY = (cy + toY) / 2.0f;
        }

        mLastEndX = midX;
        mLastEndY = midY;
        mLastX = toX;
        mLastY = toY;
    }

    protected void handlePath() {
        handleVector(); // 处理定点数据
    }

    /**
     * 处理临时路径：矩形框路径
     */
    protected void handleCachePath() {
        this.cachePath.reset();
        int padding = (int) (mInk.mThickness + 1.0f);
        if (midX > startX) {
            if (midY > startY) {
                this.cachePath.moveTo(startX - padding, startY - padding);
                this.cachePath.lineTo(startX - padding,
                        midY + padding);
                this.cachePath.lineTo(midX + padding,
                        midY + padding);
                this.cachePath.lineTo(midX + padding,
                        startY - padding);
            } else {
                this.cachePath.moveTo(startX - padding, startY + padding);
                this.cachePath.lineTo(startX - padding,
                        midY - padding);
                this.cachePath.lineTo(midX + padding,
                        midY - padding);
                this.cachePath.lineTo(midX + padding,
                        startY + padding);
            }
        } else if (midY > startY) {
            this.cachePath.moveTo(startX + padding, startY - padding);
            this.cachePath.lineTo(startX + padding, midY + padding);
            this.cachePath.lineTo(midX - padding, midY + padding);
            this.cachePath.lineTo(midX - padding, startY - padding);
        } else {
            this.cachePath.moveTo(startX + padding, startY + padding);
            this.cachePath.lineTo(startX + padding, midY - padding);
            this.cachePath.lineTo(midX - padding, midY - padding);
            this.cachePath.lineTo(midX - padding, startY + padding);
        }

        // 绘制虚线框
        this.cachePath.close();
    }

    public Rect getBounds() {
        RectF localBoundRectF = sBoundRectF;
        mPath.computeBounds(localBoundRectF, false);
        int temp = (int) (1.0f + mInk.mThickness);
        if (mIsSelected) {
            temp += 4;
        }
        mBoundRect.set((int) (localBoundRectF.left - ((float) temp)), (int) (localBoundRectF.top - ((float) temp)), (int) (localBoundRectF.right + ((float) temp)), (int) (localBoundRectF.bottom + ((float) temp)));
        return new Rect(mBoundRect);
    }

    protected Rect getCurrentPathBounds() {
        RectF localBoundRectF = sBoundRectF;

        cachePath.computeBounds(localBoundRectF, false);
        int temp = (int) (1.0f + mInk.mThickness);

        mBoundRect.set((int) (localBoundRectF.left - ((float) temp)), (int) (localBoundRectF.top - ((float) temp)), (int) (localBoundRectF.right + ((float) temp)), (int) (localBoundRectF.bottom + ((float) temp)));
        return new Rect(mBoundRect);
    }

    /**
     * 形状的边缘框
     *
     * @param padding
     */
    public RectF getShapeRectF(int padding) {
        mShapeRectF = sShapeRectF;
        mShapeRectF.left = Math.min(startX, midX);
        mShapeRectF.right = Math.max(startX, midX);
        mShapeRectF.top = Math.min(startY, midY);
        mShapeRectF.bottom = Math.max(startY, midY);

        mShapeRectF.left = mShapeRectF.left - padding;
        mShapeRectF.right = mShapeRectF.right + padding;
        mShapeRectF.top = mShapeRectF.top - padding;
        mShapeRectF.bottom = mShapeRectF.bottom + padding;

        return new RectF(mShapeRectF);
    }

    public RectF getOvalRectF(float x,float y,float R,float r) {
        mShapeRectF = sShapeRectF;

        mShapeRectF.left = x - R/2;
        mShapeRectF.right = x + R/2;
        mShapeRectF.top = y - r/2;
        mShapeRectF.bottom = y + r/2;

        return new RectF(mShapeRectF);
    }

    @Override
    public Rect rect() {
        return getBounds();
    }

    @Override
    public boolean intersect(Rect dst) {
        return PanelUtils.isRectImpact(dst, rect());
    }

    /**
     * 使用希沃算法之后，直接传入Matrix进行重绘
     *
     * @param pMatrix
     */
    @Override
    public void continueTransform(Matrix pMatrix) {
//        BaseUtils.dbg(TAG, "continueTransform");
//        if (this.mIsOpenGLEnabled)
//        {
//            transformGLPoints(pMatrix);
//        }

        mPath.transform(pMatrix);
        float tempRadius = pMatrix.mapRadius(mInk.mThickness);
        mInk.mThickness = tempRadius;
        mPaint.setStrokeWidth(mInk.mThickness);
    }

    @Override
    public void endTransform(Matrix matrix) {
        transformPoints(matrix);
//        BaseUtils.dbg(TAG, "mInk.mThickness=" + mInk.mThickness);
        float tempRadius = matrix.mapRadius(mInk.mThickness);
//        BaseUtils.dbg(TAG, "tempRadius=" + tempRadius);
/*        mInk.mThickness = tempRadius;
        mPaint.setStrokeWidth(mInk.mThickness);*/
    }

    /**
     * 移动到点
     *
     * @param matrix
     */
    private void transformPoints(Matrix matrix) {
//        BaseUtils.dbg(TAG, "transformPoints");
        if (mInk == null) {
            return;
        }

        float[] point = new float[2];
        for (int i = 0; i < mInk.mPointCount; i++) {
            point[0] = mInk.mPoints[i].mX;
            point[1] = mInk.mPoints[i].mY;
            matrix.mapPoints(point);
            mInk.mPoints[i].mX = point[0];
            mInk.mPoints[i].mY = point[1];
        }

        startX = matrix.mapRadius(startX);
        startY = matrix.mapRadius(startY);
        midX = matrix.mapRadius(midX);
        midY = matrix.mapRadius(midY);
        matrix.mapRect(mShapeRectF);
    }

    @Override
    public void transform(Matrix matrix) {
        endTransform(matrix);
        mPath.transform(matrix);
    }

    public void drawGLShape(GL10 gl) {
        synchronized (INK_VERTEX_LOCK) {
            if (this.mGLVertexPointsBuffer != null) {

                // 启用顶点开关
                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                if (isSelected()) {
                    gl.glLineWidth(6.0f);
                    int color = mPaint.getColor() ^ Constants.INK_SELECTED_COLOR_MASK;
                    gl.glColor4f(((float) Color.red(color)) / 255.0f, ((float) Color.green(color)) / 255.0f, ((float) Color.blue(color)) / 255.0f, 1.0f);
                    //设置浮点缓冲区的初始位置
                    this.mGLVertexPointsBuffer.position(0);
                    gl.glVertexPointer(2, 5126, 0, this.mGLVertexPointsBuffer);
                    gl.glDrawArrays(3, 0, this.mGLPointCount);
                }

                //指明可走样或反走样线的宽度
                if (isSameWidth) {
                    gl.glLineWidth(2.0f);
                } else {
                    gl.glLineWidth(mInk.mThickness);
                }

                gl.glColor4f(this.mRed, this.mGreen, this.mBlue, 1.0f);

                //设置浮点缓冲区的初始位置
                this.mGLVertexPointsBuffer.position(0);

                // 指定顶点集合： 二维空间，顶点的坐标值为浮点数，且顶点是连续的集合
                gl.glVertexPointer(2, GL10.GL_FLOAT, 0, this.mGLVertexPointsBuffer);


                // 绘制顶点集合之间的线
                // GL10.GL_LINE_STRIP : 前后两个顶点用线段连接，但不闭合（最后一个点与第一个点不连接）
                gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, this.mGLPointCount);

                // 禁用顶点开关
                gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            }
        }
    }

    public boolean isInPolygontest(Polygon pPolygon) {
        RectF pathBounds = sBoundRectF;
        mPath.computeBounds(pathBounds, false);
        if (pPolygon.contains((int) pathBounds.left, (int) pathBounds.top) && pPolygon.contains((int) pathBounds.left, (int) pathBounds.bottom) && pPolygon.contains((int) pathBounds.right, (int) pathBounds.top) && pPolygon.contains((int) pathBounds.right, (int) pathBounds.bottom)) {
            return true;
        }
        return false;
    }

    public boolean isInPolygon(Polygon pPolygon) {
        if (pPolygon.containsInRect(getBounds()) >= 0) {
            return true;
        }
        return false;
    }

    public void startStroke(float pX, float pY) {
        mPreviousX = pX;
        mPreviousY = pY;
        mLastEndX = pX;
        mLastEndY = pY;

        cachePath = new Path();
        mPath = new Path();
        mPath.moveTo(pX, pY);
    }

    public Point lastPoint;

    public void startOnPoint(Point point) {
        enlargePointArrayIfNecessary();
        Point[] pointArr = mInk.mPoints;
        Ink ink = mInk;
        int i = ink.mPointCount;
        ink.mPointCount = i + 1;
        pointArr[i] = point;

        lastPoint = point;

        if (this.mIsOpenGLEnabled) {
            this.mGLPoints[0] = point.mX;
            this.mGLPoints[1] = point.mY;
            this.mGLPointCount++;
        }
    }


    public Rect endStroke(float pX, float pY) {
        if (mPreviousX == pX && mPreviousY == pY) {
            pX += 0.1f;
        }

        cachePath.reset();
        cachePath.moveTo(mLastEndX, mLastEndY);
        cachePath.lineTo(pX, pY);
//        mCurrentPath.rewind(); // 添加 0813 1021
        mPath.moveTo(mLastEndX, mLastEndY);
        mPath.lineTo(pX, pY);
//        mPath.rewind(); // 添加 0813 1021

        setInkFinished();
        return getBounds();
    }

    public void continueOnPoint(Point point) {
        if (lastPoint == null) {
            lastPoint = point;
            return;
        }
        insertFalsePoints(lastPoint, point);
        savePointsToArray(point);
        lastPoint = point;
    }

    //jean
    public void insertInksPoint(float x,float y,float x1,float y1,float R,boolean end,boolean start){
        if(R <= 0.0f)
            return;
        int k = (int)(R/10.0f);
//        BaseUtils.dbg(TAG, "setInksPoint k=" + k);
        for(int i=0;i<k;i++) {
            if(i == 0) {
                if(start) {
                    startOnPoint(new Point(x, y));
                    mBrushes.startStroke(x,y);
                }else {
                    continueOnPoint(new Point(x, y));
                    mBrushes.continueStroke(x,y);
                }
            }else if (i == k -1) {
                if(end) {
                    endOnPoint(new Point(x1, y1));
                    mBrushes.endStroke(x1,y1);
                }else {
                    continueOnPoint(new Point(x1, y1));
                    mBrushes.continueStroke(x,y);
                }
            }else{
                float d = 10.0f*i;
                float mX = ((d*(x1 - x))/R) + x;
                float mY = ((d*(y1 - y))/R) + y;
                continueOnPoint(new Point(mX, mY));
                mBrushes.continueStroke(x,y);
            }
        }
    }

    public void insertInksPoint(Point mP,boolean end,boolean start){
        if(start) {
            startOnPoint(mP);
            mBrushes.startStroke(mP.mX,mP.mY);
        }else if (end) {
            endOnPoint(mP);
            mBrushes.endStroke(mP.mX,mP.mY);
        }else {
            continueOnPoint(mP);
            mBrushes.continueStroke(mP.mX,mP.mY);
        }
    }

    public ShapeMaterial getCurrentMaterial(){
        return mBrushes;
    }

    public Rect continueStroke(float pX, float pY)
    {
        return continueStroke(pX, pY, false);
    }

    private Rect continueStroke(float pX, float pY, boolean pIsFalsePoint) {
        if (!pIsFalsePoint) {
            strokeCubicTo(pX, pY);
        }
        return getCurrentPathBounds();
    }


    protected void strokeCubicTo(float pX, float pY) {
        float localMidFloatX = (mPreviousX + pX) / 2.0f;
        float localMidFloatY = (mPreviousY + pY) / 2.0f;
        if (cachePath == null) {
            cachePath = new Path();
        }
        cachePath.reset();
        cachePath.moveTo(mLastEndX, mLastEndY);
        cachePath.cubicTo(mLastEndX, mLastEndY, mPreviousX, mPreviousY, localMidFloatX, localMidFloatY);
        mPath.moveTo(mLastEndX, mLastEndY);
        mPath.cubicTo(mLastEndX, mLastEndY, mPreviousX, mPreviousY, localMidFloatX, localMidFloatY);
        mPreviousX = pX;
        mPreviousY = pY;
        mLastEndX = localMidFloatX;
        mLastEndY = localMidFloatY;
    }


    public void endOnPoint(Point point) {
        if (lastPoint == null) {
            lastPoint = point;
            return;
        }

        insertFalsePoints(lastPoint, point);
        savePointsToArray(point);
        lastPoint = point;

        if (this.mIsOpenGLEnabled) {
            this.mGLVertexPointsBuffer = ByteBuffer.allocateDirect(((this.mGLPointCount * 2) * 32) / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mGLVertexPointsBuffer.position(0);
            this.mGLVertexPointsBuffer.put(this.mGLPoints, 0, this.mGLPointCount * 2);
        }
    }

    /**
     * 插入点数据
     *
     * @param prePoint
     * @param curPoint
     */
    protected void insertFalsePoints(Point prePoint, Point curPoint) {
//        Log.d(TAG, "insertFalsePoints --- prePoint = " + prePoint.toString() + ", curPoint = " + curPoint.toString());
        float dist = (float) Math.sqrt((double) (((prePoint.mX - curPoint.mX) * (prePoint.mX - curPoint.mX)) + ((prePoint.mY - curPoint.mY) * (prePoint.mY - curPoint.mY))));
        if (dist > Constants.MIN_DISTANCE_BETWEEN_POINT) {
            int pointCount = ((int) (dist / Constants.MIN_DISTANCE_BETWEEN_POINT));
            float deltaX = (curPoint.mX - prePoint.mX) / ((float) pointCount);
            float deltaY = (curPoint.mY - prePoint.mY) / ((float) pointCount);
            float tempX = prePoint.mX;
            float tempY = prePoint.mY;

            boolean flagX = (Float.compare(deltaX, 0) < 0);
            boolean flagY = (Float.compare(deltaY, 0) < 0);
            for (int i = 0; i < pointCount; i++) {
                tempX += deltaX;
                tempY += deltaY;
                savePointsToArray(new Point(tempX, tempY, true));
            }
        }
    }

    public void setInkFinished() {
        mIsFinished = true;
    }

    protected abstract class Side {
        List<ShapeMaterial> pencils;

        abstract Side next(Point point, boolean z, boolean z2);

        Side(List<ShapeMaterial> list) {
            pencils = list;
        }

        void end(Point point) {
        }
    }

    /**
     * OutSide代表被擦除的部分
     */
    protected class Inside extends Side {
        Inside(List<ShapeMaterial> pencils) {
            super(pencils);
        }

        Side next(Point point, boolean inside, boolean pointFalse) {
            /**仍然处于被擦除，返回自身*/
            if (inside) {
                return this;
            }
            BaseUtils.dbg(TAG, "Inside point=" + point.mX + " " + point.mY);
            /**未被擦除，创建新的笔迹（outside）*/
            ShapeMaterial ink = getInkWithColorAndThickness();
            ink.startStroke(point.mX, point.mY);
            ink.startOnPoint(new Point(point.mX, point.mY, false));
            return new Outside(ink, pencils);
        }
    }

    /**
     * OutSide代表未被擦除的部分
     */
    protected class Outside extends Side {
        ShapeMaterial pencil;

        Outside(ShapeMaterial ink, List<ShapeMaterial> pencils) {
            super(pencils);
            pencil = ink;
        }

        Side next(Point point, boolean inside, boolean pointFalse) {

            /**下一段是被擦除的，则结束本线段*/
            if (inside) {
                BaseUtils.dbg(TAG, "inside point=" + point.mX + " " + point.mY);
                pencil.endStroke(point.mX, point.mY);
                pencil.endOnPoint(new Point(point.mX, point.mY, false));
                pencils.add(pencil);
//                BaseUtils.dbg(TAG, "inside pencils.size=" + pencils.size());
                return new Inside(pencils);
            }

            /**下一段未被擦除，则继续加入point*/
            pencil.continueStroke(point.mX, point.mY, point.mIsFalsePoint);
            if (!point.mIsFalsePoint) {
//                BaseUtils.dbg(TAG, "continueStroke point=" + point.mX + " " + point.mY);
                pencil.continueOnPoint(point);
            }
            return this;
        }

        void end(Point point) {
            pencil.endStroke(point.mX, point.mY);
            pencil.endOnPoint(new Point(point.mX, point.mY, false));
            pencils.add(pencil);
            BaseUtils.dbg(TAG, "end point=" + point.mX + " " + point.mY);
//            BaseUtils.dbg(TAG, "end pencils.size=" + pencils.size());
        }
    }

    protected Point computeInterSectPoint(Polygon polygon, Point inside, Point outside, int rectIndex) {
        Point tempPoint = new Point();
        tempPoint.mX = outside.mX;
        tempPoint.mY = outside.mY;
        float deltaX = (inside.mX - outside.mX) / Constants.MIN_DISTANCE_BETWEEN_POINT;
        float deltaY = (inside.mY - outside.mY) / Constants.MIN_DISTANCE_BETWEEN_POINT;
        int count = 0;
        do {
            count++;
            tempPoint.mX += deltaX;
            tempPoint.mY += deltaY;
        }
        while (!polygon.contains((int) tempPoint.mX, (int) tempPoint.mY, rectIndex) /*&& count < 20*/);
        return tempPoint;
    }

    public static float calAngle(Point p1, Point p2) {
        double radian = Math.atan2((p2.mY - p1.mY), (p2.mX - p1.mX));
        double angle = radian * (180 / Math.PI);

        //角度
        return (float) angle;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        out.write(BaseUtils.intToByteArray(mPaint.getColor()));
        out.write(BaseUtils.floatToByte(mPaint.getStrokeWidth()));
        int size = mInk.mPointCount;
        out.write(BaseUtils.intToByteArray(size));
        for (int i = 0; i < size; i++)
        {
            Point pd = (Point) mInk.mPoints[i];
            out.write(BaseUtils.floatToByte(pd.mX));
            out.write(BaseUtils.floatToByte(pd.mY));
            out.write(BaseUtils.intToByteArray(pd.mIsFalsePoint ? 0 : 1));
        }
        return true;
    }

    public boolean readObject(InputStream in) throws IOException
    {
        byte[] buf = new byte[4];
        int read = in.read(buf);
        mInk.mInkColor = BaseUtils.byteArrayToInt(buf);
        read = in.read(buf);
        mInk.mThickness = BaseUtils.byteToFloat(buf);
        read = in.read(buf);
        int size = BaseUtils.byteArrayToInt(buf);
//        BaseUtils.dbg(TAG, " buf size=" + size);
        if (read != 4)
        {
            BaseUtils.dbg(TAG, "Pen: base datas");
            return false;
        }
        setId(id);
        mPaint.setColor(mInk.mInkColor);
        mPaint.setStrokeWidth(mInk.mThickness);
        for (int i = 0; i < size; i++)
        {
            Point pd = new Point();
            pd.mX = BaseUtils.readInputStreamFloat(in, buf);
            pd.mY = BaseUtils.readInputStreamFloat(in, buf);
            pd.mIsFalsePoint = BaseUtils.readInputStreamInt(in, buf) == 0;
            enlargePointArrayIfNecessary();
            mInk.mPoints[i] = pd;
            mInk.mPointCount++;
        }
        rebuildPath();
        return true;
    }

    protected void rebuildPath()
    {
//        BaseUtils.dbg(TAG, "rebuildPath");
        if (mPath == null)
        {
            mPath = new Path();
        }
        mPath.reset();
        if (mInk.mPoints.length > 0)
        {

            Point point = mInk.mPoints[0];

            mPreviousX = point.mX;
            mPreviousY = point.mY;
            mPath.moveTo(mPreviousX, mPreviousY);
            mLastEndX = mPreviousX;
            mLastEndY = mPreviousY;

            if (this.mIsOpenGLEnabled)
            {
                this.mGLPoints[0] = this.mPreviousX;
                this.mGLPoints[1] = this.mPreviousY;
                this.mGLPointCount++;
            }
//            BaseUtils.dbg(TAG, "mInk.mPointCount=" + mInk.mPointCount);
            for (int i = 1; i < mInk.mPointCount - 1; i++)
            {
                    //jean test
//                    BaseUtils.dbg(TAG, "mLastEndX=" + mLastEndX + " mLastEndY=" + mLastEndY);
//                BaseUtils.dbg(TAG, "mInk.mPoints[" + i + "].mX=" + mInk.mPoints[i].mX + " mInk.mPoints[" +  i + "].mY=" + mInk.mPoints[i].mY);
                int distance = (int)Math.sqrt((double) (((mLastEndX - mInk.mPoints[i].mX) * (mLastEndX - mInk.mPoints[i].mX)) + ((mLastEndY -  mInk.mPoints[i].mY) * (mLastEndY -  mInk.mPoints[i].mY))));
//                    BaseUtils.dbg(TAG, "distance=" + distance);
                if(distance > Constants.MAX_DISTANCE_BETWEEN_POINT){
                    mPreviousX = mInk.mPoints[i].mX;
                    mPreviousY = mInk.mPoints[i].mY;
                    mPath.moveTo(mPreviousX, mPreviousY);
                    mLastEndX = mPreviousX;
                    mLastEndY = mPreviousY;
                }
                mPath.moveTo(mLastEndX, mLastEndY);
                strokeCubicTo(mInk.mPoints[i].mX, mInk.mPoints[i].mY);
                if (this.mIsOpenGLEnabled)
                {
                    enlargeGlPointIfNecessary();
                    this.mGLPoints[this.mGLPointCount * 2] = this.mInk.mPoints[i].mX;
                    this.mGLPoints[(this.mGLPointCount * 2) + 1] = this.mInk.mPoints[i].mY;
                    this.mGLPointCount++;
                }

            }
            mPath.moveTo(mLastEndX, mLastEndY);
            mPath.lineTo(mInk.mPoints[mInk.mPointCount - 1].mX, mInk.mPoints[mInk.mPointCount - 1].mY);
//            BaseUtils.dbg(TAG, "mInk.mPoints.mX=" + mInk.mPoints[mInk.mPointCount - 1].mX + " mInk.mPoints.mY=" + mInk.mPoints[mInk.mPointCount - 1].mY);

            if (this.mIsOpenGLEnabled)
            {
                this.mGLVertexPointsBuffer = ByteBuffer.allocateDirect(((this.mGLPointCount * 2) * 32) / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
                this.mGLVertexPointsBuffer.position(0);
                this.mGLVertexPointsBuffer.put(this.mGLPoints, 0, this.mGLPointCount * 2);
            }
        }
    }
}
