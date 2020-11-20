package com.mphotool.whiteboard.elements;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class PencilInk extends Material {

    static String TAG = "PencilInk";
    private static final Object INK_VERTEX_LOCK = new Object();
    private static RectF sBoundRectF = new RectF();
    private Rect mBoundRect;

    protected boolean mIsSelected;

    private static RectF sPathBounds = new RectF();
    private static Rect sPathRect = new Rect();
    private static RectF sPathRectf = new RectF();
    protected transient Ink mInk;
    protected boolean mIsFinished = false;
    protected transient Paint mPaint;
    protected transient Paint mFinemPaint;
    protected transient Paint mSpeedPaint;
    public boolean resetWidth = false;
    /*画笔类型*/
    protected transient int ToolType = 0;
    protected transient int PenType = 0;
    protected transient Path mCurrentPath;
    protected transient Path mPath;
    protected transient float mLastEndX;
    protected transient float mLastEndY;
    protected transient float mPreviousX;
    protected transient float mPreviousY;



    private int mGLPointCount;
    private float[] mGLPoints;
    private FloatBuffer mGLVertexPointsBuffer;

    private boolean mIsOpenGLEnabled = BuildConfig.enable_opengl;
    private float mGreen;
    private float mBlue;
    private float mRed;
    private int Green;
    private int Blue;
    private int Red;

    private boolean isSameWidth = (BuildConfig.transform_type == 0);
    private Matrix matrix;

    public PencilInk()
    {
        mBoundRect = new Rect();
        initInk(Color.WHITE, Constants.PEN_WIDTH_LITTLE);
        matrix=new Matrix();
        matrix.setSkew(2,2);
        initPaint();
        initOpenGL();
    }

    public PencilInk(PanelManager manager, int color, float thickness,int mType,int mTType)
    {
        mManager = manager;
        PenType = mType;
        ToolType = mTType;
        mBoundRect = new Rect();
        matrix=new Matrix();
        matrix.setSkew(2,2);
        initInk(color, thickness);
        initPaint();
        initOpenGL();
    }

    private void initInk(int color, float thickness)
    {
        mInk = new Ink();
        mInk.mPoints = new Point[100];
        mInk.mInkColor = color;
        mInk.mThickness = thickness;

        this.mRed = ((float) Color.red(this.mInk.mInkColor)) / 255.0f;
        this.mGreen = ((float) Color.green(this.mInk.mInkColor)) / 255.0f;
        this.mBlue = ((float) Color.blue(this.mInk.mInkColor)) / 255.0f;

        this.Red = Color.red(this.mInk.mInkColor);
        this.Green = Color.green(this.mInk.mInkColor);
        this.Blue = Color.blue(this.mInk.mInkColor);
    }

    protected void initPaint()
    {
        float mWidth = 0;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        if(ToolType == 2)
            mWidth = mInk.mThickness/2;
        else
            mWidth = mInk.mThickness;
        mPaint.setStrokeWidth(mWidth);
        mPaint.setColor(mInk.mInkColor);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeJoin(Join.ROUND);
        mPaint.setStrokeCap(Cap.ROUND);
//        mPaint.setMaskFilter(new BlurMaskFilter(1, BlurMaskFilter.Blur.SOLID));
        mPaint.setDither(true);

 /*       mFinemPaint = new Paint(mPaint);
        mFinemPaint.setStrokeWidth(mInk.mThickness/3);*/

        float pWidth = mWidth/2;
        if(PenType == 1) {
            Path p = new Path();
            p.addRect(new RectF(0, 0, pWidth, pWidth), Path.Direction.CCW);
            p.transform(matrix);
            mPaint.setPathEffect(new PathDashPathEffect(p, 1, 0, PathDashPathEffect.Style.TRANSLATE));
        }

        mSpeedPaint = new Paint(mPaint);
        mSpeedPaint.setARGB(255,Blue,Green,Red);
    }

    private void initOpenGL()
    {
        if (this.mIsOpenGLEnabled)
        {
            this.mGLPoints = new float[100];
        }
    }

    @Override
    public void draw(Canvas canvas)
    {
        if (isSelected())
        {
            Paint pe = new Paint();
            pe.setAntiAlias(true);
            pe.setStrokeWidth(mPaint.getStrokeWidth() + 4.0f);
            pe.setColor(mPaint.getColor() ^ Constants.INK_SELECTED_COLOR_MASK);
            pe.setStyle(Style.STROKE);
            pe.setStrokeJoin(Join.ROUND);
            pe.setStrokeCap(Cap.ROUND);
            pe.setDither(true);
            canvas.drawPath(mPath, pe);
        }
        canvas.drawPath(mPath, mPaint);
    }


    private void enlargeGlPointIfNecessary()
    {
        if (this.mGLPointCount * 2 >= this.mGLPoints.length)
        {
            float[] values = new float[(this.mGLPoints.length + 200)];
            System.arraycopy(this.mGLPoints, 0, values, 0, this.mGLPointCount * 2);
            this.mGLPoints = values;
        }
    }

    private void transformGLPoints(Matrix pMatrix)
    {
        BaseUtils.dbg(TAG, " transformGLPoints");
        float[] point = new float[2];
        int length = this.mGLPointCount * 2;
        for (int i = 0; i < length; i += 2)
        {
            point[0] = this.mGLPoints[i];
            point[1] = this.mGLPoints[i + 1];
            pMatrix.mapPoints(point);
            this.mGLPoints[i] = point[0];
            this.mGLPoints[i + 1] = point[1];
        }
        synchronized (INK_VERTEX_LOCK)
        {
            if (this.mGLVertexPointsBuffer != null)
            {
                this.mGLVertexPointsBuffer.clear();
                this.mGLVertexPointsBuffer.put(this.mGLPoints, 0, this.mGLPointCount * 2);
            }
        }
    }

    public void drawGLShape(GL10 gl)
    {
        synchronized (INK_VERTEX_LOCK)
        {
            if (this.mGLVertexPointsBuffer != null)
            {

                // 启用顶点开关
                gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                if (isSelected())
                {
                    gl.glLineWidth(mInk.mThickness+2.0f);
                    int color = mPaint.getColor() ^ Constants.INK_SELECTED_COLOR_MASK;
                    gl.glColor4f(((float) Color.red(color)) / 255.0f, ((float) Color.green(color)) / 255.0f, ((float) Color.blue(color)) / 255.0f, 1.0f);
                    //设置浮点缓冲区的初始位置
                    this.mGLVertexPointsBuffer.position(0);
                    gl.glVertexPointer(2, 5126, 0, this.mGLVertexPointsBuffer);
                    gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, this.mGLPointCount);
                }

                //指明可走样或反走样线的宽度
                if (isSameWidth)
                {
                    gl.glLineWidth(2.0f);
                }
                else
                {
                    gl.glLineWidth(mInk.mThickness);
                }

          /*      {
                    gl.glColor4f(this.mRed, this.mGreen, this.mBlue, 1.0f);
                    this.mTempGLVertexPointsBuffer.position(0);
                    gl.glVertexPointer(2, GL10.GL_FLOAT, 0, this.mTempGLVertexPointsBuffer);
                    gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, this.mGLPointCount);
                }*/

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


    @Override public Rect rect()
    {
        return getBounds();
    }

    public boolean intersect(Rect dst)
    {
        return PanelUtils.isRectImpact(dst, rect());
    }

    @Override public boolean isCross(float x1, float y1, float x2, float y2)
    {
        if (mInk.mPointCount < 2)
        {
            return false;
        }
        Point point = mInk.mPoints[0];
        float lx = point.mX;
        float ly = point.mY;
        for (int i = 1; i < mInk.mPointCount; i++)
        {
            point = mInk.mPoints[i];
            if (isIntersection((int) lx, (int) ly, (int) point.mX, (int) point.mY, (int) x1, (int) y1, (int) x2, (int) y2))
            {
                return true;
            }
            lx = point.mX;
            ly = point.mY;
        }
        return false;
    }

    public boolean isInPolygon(Polygon pPolygon)
    {
        RectF pathBounds = sPathBounds;
        mPath.computeBounds(pathBounds, false);
        if (pPolygon.contains((int) pathBounds.left, (int) pathBounds.top) && pPolygon.contains((int) pathBounds.left, (int) pathBounds.bottom) && pPolygon.contains((int) pathBounds.right, (int) pathBounds.top) && pPolygon.contains((int) pathBounds.right, (int) pathBounds.bottom))
        {
            return true;
        }
        return false;
    }

    public ArrayList<PencilInk> splitPencil(Polygon polygon)
    {
        ArrayList<PencilInk> inks = null;
        RectF pathRectf = sPathRectf;
        mPath.computeBounds(pathRectf, false);
        Rect pathRect = sPathRect;
        pathRect.set((int) pathRectf.left, (int) pathRectf.top, (int) pathRectf.right, (int) pathRectf.bottom);
        Rect polygonRect = polygon.getRect();
        if (Rect.intersects(pathRect, polygonRect))
        {
            inks = new ArrayList();
            Side side = null;
            byte mark = (byte) 0;
            for (int i = 0; i < mInk.mPointCount; i++)
            {
                Point point = mInk.mPoints[i];
                if (point != null)
                {
                    int rectIndex = -1;
                    if(polygon.containsInmTempRect((int)point.mX,(int)point.mY)) {
                        rectIndex = polygon.containsInRect((int) point.mX, (int) point.mY);
                    }
                    Point p;
                    if (rectIndex != -1)
                    {
                        p = point;
                        if (i == 0)
                        {
                            side = new Inside(inks);
                        }
                        else if (side instanceof Outside)
                        {
                            side = side.next(computeInterSectPoint(polygon, point, mInk.mPoints[i - 1], rectIndex), true, false);
                        }
                        mark = (byte) (mark | 1);
                    }
                    else
                    {
                        if (i == 0)
                        {
                            PencilInk ink = getInkWithColorAndThickness();
                            ink.startStroke(point.mX, point.mY);
                            ink.startOnPoint(new Point(point.mX, point.mY, false));
                            side = new Outside(ink, inks);
                        }
                        else
                        {
                            p = point;
                            if (side instanceof Inside)
                            {
                                p = computeInterSectPoint(polygon, mInk.mPoints[i - 1], point, rectIndex);
                            }
                            side = side.next(p, false, p.mIsFalsePoint);
                        }
                        mark = (byte) (mark | 2);
                    }
                    if (i == mInk.mPointCount - 1)
                    {
                        if (mark == (byte) 3)
                        {
                            side.end(point);
                        }
                        else if (mark == (byte) 2)
                        {
                            inks = null;
                        }
                    }
                }
            }
        }
        return inks;
    }

    private Point computeInterSectPoint(Polygon polygon, Point inside, Point outside, int rectIndex)
    {
        Point tempPoint = new Point();
        tempPoint.mX = outside.mX;
        tempPoint.mY = outside.mY;
        float deltaX = (inside.mX - outside.mX) / Constants.MIN_DISTANCE_BETWEEN_POINT;
        float deltaY = (inside.mY - outside.mY) / Constants.MIN_DISTANCE_BETWEEN_POINT;
        int count = 0;
        do
        {
            count++;
            tempPoint.mX += deltaX;
            tempPoint.mY += deltaY;
        }
        while (!polygon.contains((int) tempPoint.mX, (int) tempPoint.mY, rectIndex) /*&& count < 20*/);
        return tempPoint;
    }


    public static boolean isIntersection(int line0sx, int line0sy, int line0ex, int line0ey, int line1sx, int line1sy, int line1ex, int line1ey)
    {
        long localLength2 = (long) (((line0sx - line0ex) * (line1sy - line1ey)) - ((line0sy - line0ey) * (line1sx - line1ex)));
        if (localLength2 == 0)
        {
            return false;
        }
        long localLength3 = (long) ((line0sx * line0ey) - (line0sy * line0ex));
        long localLength1 = (long) ((line1sx * line1ey) - (line1sy * line1ex));
        int lIndexJ = (int) (((((long) (line1sx - line1ex)) * localLength3) - (((long) (line0sx - line0ex)) * localLength1)) / localLength2);
        if (lIndexJ + 1 < Math.min(line0sx, line0ex) || lIndexJ - 1 > Math.max(line0sx, line0ex) || lIndexJ + 1 < Math.min(line1sx, line1ex) || lIndexJ - 1 > Math.max(line1sx, line1ex))
        {
            return false;
        }
        int lIndexI = (int) (((((long) (line1sy - line1ey)) * localLength3) - (((long) (line0sy - line0ey)) * localLength1)) / localLength2);
        if (lIndexI + 1 < Math.min(line0sy, line0ey) || lIndexI - 1 > Math.max(line0sy, line0ey) || lIndexI + 1 < Math.min(line1sy, line1ey) || lIndexI - 1 > Math.max(line1sy, line1ey))
        {
            return false;
        }
        return true;
    }

    public Rect getBounds()
    {
        RectF localBoundRectF = sBoundRectF;
        mPath.computeBounds(localBoundRectF, false);
        int temp = (int) (1.0f + mInk.mThickness);
        if (mIsSelected)
        {
            temp += 4;
        }
        mBoundRect.set((int) (localBoundRectF.left - ((float) temp)), (int) (localBoundRectF.top - ((float) temp)), (int) (localBoundRectF.right + ((float) temp)), (int) (localBoundRectF.bottom + ((float) temp)));
        return new Rect(mBoundRect);
    }

    protected Rect getCurrentPathBounds()
    {
        RectF localBoundRectF = sBoundRectF;
        mCurrentPath.computeBounds(localBoundRectF, false);
        int temp = (int) (1.0f + mInk.mThickness);
        mBoundRect.set((int) (localBoundRectF.left - ((float) temp)), (int) (localBoundRectF.top - ((float) temp)), (int) (localBoundRectF.right + ((float) temp)), (int) (localBoundRectF.bottom + ((float) temp)));
        return new Rect(mBoundRect);
    }

    protected PencilInk getInkWithColorAndThickness()
    {
        return new PencilInk(mManager, mInk.mInkColor, mInk.mThickness,PenType,ToolType);
    }

    public void continueTransform(Matrix pMatrix)
    {
//        BaseUtils.dbg(TAG, "continueTransform");
        if (this.mIsOpenGLEnabled)
        {
            transformGLPoints(pMatrix);
        }

        mPath.transform(pMatrix);
        float tempRadius = pMatrix.mapRadius(mInk.mThickness);
        mInk.mThickness = tempRadius;
        mPaint.setStrokeWidth(mInk.mThickness);
    }

    public void endTransform(Matrix matrix)
    {
        transformPoints(matrix);
        if (resetWidth)
        {
            float tempRadius = matrix.mapRadius(mInk.mThickness);
            mInk.mThickness = tempRadius;
            mPaint.setStrokeWidth(mInk.mThickness);
            resetWidth = false;
        }
    }

    @Override
    public void transform(Matrix matrix)
    {
        endTransform(matrix);
        mPath.transform(matrix);
        if (this.mIsOpenGLEnabled)
        {
            transformGLPoints(matrix);
        }
    }

    private void transformPoints(Matrix matrix)
    {
//        BaseUtils.dbg(TAG, "transformPoints");
        float[] point = new float[2];
        for (int i = 0; i < mInk.mPointCount; i++)
        {
            point[0] = mInk.mPoints[i].mX;
            point[1] = mInk.mPoints[i].mY;
            matrix.mapPoints(point);
            mInk.mPoints[i].mX = point[0];
            mInk.mPoints[i].mY = point[1];
        }
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

            for (int i = 1; i < mInk.mPointCount - 1; i++)
            {
                if (!mInk.mPoints[i].mIsFalsePoint)
                {
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
            }
            mPath.moveTo(mLastEndX, mLastEndY);
            mPath.lineTo(mInk.mPoints[mInk.mPointCount - 1].mX, mInk.mPoints[mInk.mPointCount - 1].mY);

            if (this.mIsOpenGLEnabled)
            {
                this.mGLVertexPointsBuffer = ByteBuffer.allocateDirect(((this.mGLPointCount * 2) * 32) / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
                this.mGLVertexPointsBuffer.position(0);
                this.mGLVertexPointsBuffer.put(this.mGLPoints, 0, this.mGLPointCount * 2);
            }
        }
    }

    public void startStroke(float pX, float pY)
    {
        mPreviousX = pX;
        mPreviousY = pY;
        mLastEndX = pX;
        mLastEndY = pY;
        mCurrentPath = new Path();
        mPath = new Path();
        mPath.moveTo(pX, pY);
    }

    public Rect continueStroke(float pX, float pY)
    {
        return continueStroke(pX, pY, false);
    }

    private Rect continueStroke(float pX, float pY, boolean pIsFalsePoint)
    {
        if (!pIsFalsePoint)
        {
            strokeCubicTo(pX, pY);
        }
        return getCurrentPathBounds();
    }

    public Rect endStroke(float pX, float pY)
    {
        if (mPreviousX == pX && mPreviousY == pY)
        {
            pX += 0.1f;
        }
        mCurrentPath.reset();
        mCurrentPath.moveTo(mLastEndX, mLastEndY);
        mCurrentPath.lineTo(pX, pY);
//        mCurrentPath.rewind(); // 添加 0813 1021
        mPath.moveTo(mLastEndX, mLastEndY);
        mPath.lineTo(pX, pY);
//        mPath.rewind(); // 添加 0813 1021

        setInkFinished();
        return getBounds();
    }

    private void enlargePointArrayIfNecessary()
    {
        if (mInk.mPointCount >= mInk.mPoints.length)
        {
            Point[] points = new Point[(mInk.mPoints.length + 100)];
            System.arraycopy(mInk.mPoints, 0, points, 0, mInk.mPointCount);
            mInk.mPoints = points;
        }
    }

    protected void strokeCubicTo(float pX, float pY)
    {
        float localMidFloatX = (mPreviousX + pX) / 2.0f;
        float localMidFloatY = (mPreviousY + pY) / 2.0f;
        if (mCurrentPath == null)
        {
            mCurrentPath = new Path();
        }
        mCurrentPath.reset();
        mCurrentPath.moveTo(mLastEndX, mLastEndY);
        mCurrentPath.cubicTo(mLastEndX, mLastEndY, mPreviousX, mPreviousY, localMidFloatX, localMidFloatY);
        mPath.moveTo(mLastEndX, mLastEndY);
        mPath.cubicTo(mLastEndX, mLastEndY, mPreviousX, mPreviousY, localMidFloatX, localMidFloatY);
        mPreviousX = pX;
        mPreviousY = pY;
        mLastEndX = localMidFloatX;
        mLastEndY = localMidFloatY;
    }

    public void drawCurrentPath(PanelManager mPanelManager)
    {
        if (mCurrentPath != null)
        {
//            PathMeasure measure = new PathMeasure(mCurrentPath, false);
//            if(measure.getLength() > 0.1) {
                if (BuildConfig.is_accelerate) {
                    mPanelManager.getfbCanvas0().drawPath(mCurrentPath, mSpeedPaint);
                    mPanelManager.getfbCanvas1().drawPath(mCurrentPath, mSpeedPaint);
                    mPanelManager.getfbCanvas2().drawPath(mCurrentPath, mSpeedPaint);
                }
                mPanelManager.getCacheCanvas().drawPath(mCurrentPath, mPaint);
//            }
        }
    }
	
    public Point lastPoint;

    public void startOnPoint(Point point)
    {
        Point[] pointArr = mInk.mPoints;
        Ink ink = mInk;
        int i = ink.mPointCount;
        ink.mPointCount = i + 1;
        pointArr[i] = point;

        lastPoint = point;

        if (this.mIsOpenGLEnabled)
        {
            this.mGLPoints[0] = point.mX;
            this.mGLPoints[1] = point.mY;

            this.mGLPointCount++;
        }

    }

    public void continueOnPoint(Point point)
    {
        if(lastPoint == null){
            lastPoint = point;
            return;
        }
        insertFalsePoints(lastPoint, point);
        savePointsToArray(point);
        lastPoint = point;
    }

    public void endOnPoint(Point point)
    {
//        BaseUtils.dbg(TAG, "endOnPoint");
        if(lastPoint == null){
            lastPoint = point;
            return;
        }

        insertFalsePoints(lastPoint, point);
        savePointsToArray(point);
        lastPoint = point;

        if (this.mIsOpenGLEnabled)
        {
            this.mGLVertexPointsBuffer = ByteBuffer.allocateDirect(((this.mGLPointCount * 2) * 32) / 8).order(ByteOrder.nativeOrder()).asFloatBuffer();
            this.mGLVertexPointsBuffer.position(0);
            this.mGLVertexPointsBuffer.put(this.mGLPoints, 0, this.mGLPointCount * 2);
        }
    }


    private void insertFalsePoints(Point prePoint, Point curPoint)
    {
        float dist = (float) Math.sqrt((double) (((prePoint.mX - curPoint.mX) * (prePoint.mX - curPoint.mX)) + ((prePoint.mY - curPoint.mY) * (prePoint.mY - curPoint.mY))));
//        BaseUtils.dbg(TAG, " .dist=" + dist + " curPoint.mX=" + curPoint.mX + " curPoint.mY=" + curPoint.mY);
//        BaseUtils.dbg(TAG, " .dist=" + dist + " prePoint.mX=" + prePoint.mX + " prePoint.mY=" + prePoint.mY);
        if (dist > Constants.MIN_DISTANCE_BETWEEN_POINT)
        {
            int pointCount = ((int) (dist / Constants.MIN_DISTANCE_BETWEEN_POINT));
            float deltaX = (curPoint.mX - prePoint.mX) / ((float) pointCount);
            float deltaY = (curPoint.mY - prePoint.mY) / ((float) pointCount);
            float tempX = prePoint.mX;
            float tempY = prePoint.mY;
            for (int i = 0; i < pointCount; i++)
            {
                tempX += deltaX;
                tempY += deltaY;
                savePointsToArray(new Point(tempX, tempY, true));
            }
        }
    }

    Point PreviousPoint =new Point(0.0f,0.0f);
    protected void savePointsToArray(Point point)
    {
        enlargePointArrayIfNecessary();
        Point[] pointArr = mInk.mPoints;
        Ink ink = mInk;
        int i = ink.mPointCount;
        ink.mPointCount = i + 1;
        pointArr[i] = point;

        if (this.mIsOpenGLEnabled && !point.mIsFalsePoint)
        {
            enlargeGlPointIfNecessary();
            this.mGLPoints[this.mGLPointCount * 2] = point.mX;
            this.mGLPoints[(this.mGLPointCount * 2) + 1] = point.mY;

            this.mGLPointCount++;

            PreviousPoint.mX = point.mX;
            PreviousPoint.mY = point.mY;
        }
    }


    private abstract class Side {
        ArrayList<PencilInk> pencils;

        abstract Side next(Point point, boolean z, boolean z2);

        Side(ArrayList<PencilInk> list)
        {
            pencils = list;
        }

        void end(Point point)
        {
        }
    }

    /**
     * OutSide代表被擦除的部分
     */
    private class Inside extends Side {
        Inside(ArrayList<PencilInk> pencils)
        {
            super(pencils);
        }

        Side next(Point point, boolean inside, boolean pointFalse)
        {
            /**仍然处于被擦除，返回自身*/
            if (inside)
            {
                return this;
            }

            /**未被擦除，创建新的笔迹（outside）*/
            PencilInk ink = getInkWithColorAndThickness();
            ink.startStroke(point.mX, point.mY);
            ink.startOnPoint(new Point(point.mX, point.mY, false));
            return new Outside(ink, pencils);
        }
    }

    /**
     * OutSide代表未被擦除的部分
     */
    private class Outside extends Side {
        PencilInk pencil;

        Outside(PencilInk ink, ArrayList<PencilInk> pencils)
        {
            super(pencils);
            pencil = ink;
        }

        Side next(Point point, boolean inside, boolean pointFalse)
        {

            /**下一段是被擦除的，则结束本线段*/
            if (inside)
            {
                pencil.endStroke(point.mX, point.mY);
                pencil.endOnPoint(new Point(point.mX, point.mY, false));
                pencils.add(pencil);
                return new Inside(pencils);
            }

            /**下一段未被擦除，则继续加入point*/
            pencil.continueStroke(point.mX, point.mY, point.mIsFalsePoint);
            if(!point.mIsFalsePoint){
                pencil.continueOnPoint(point);
            }
            return this;
        }

        void end(Point point)
        {
            pencil.endStroke(point.mX, point.mY);
            pencil.endOnPoint(new Point(point.mX, point.mY, false));
            pencils.add(pencil);
        }
    }

    public boolean isFinished()
    {
        return mIsFinished;
    }

    public void setInkFinished()
    {
        mIsFinished = true;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        out.write(BaseUtils.intToByteArray(mPaint.getColor()));
        out.write(BaseUtils.floatToByte(mPaint.getStrokeWidth()));
        int size = mInk.mPointCount;
        BaseUtils.dbg(TAG, " writeObject buf size=" + size);
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
//        BaseUtils.dbg(TAG, "readObject buf size=" + size);
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

    public float TwoAngleDifference(Point mPoint,Point mPreviousPoint){
        if(Math.abs(mPoint.mX - mPreviousPoint.mX) < 1e-7)
            return 0.0f;

        float k = Math.abs((mPoint.mY - mPreviousPoint.mY)/(mPoint.mX - mPreviousPoint.mX));
        return k;
    }
}
