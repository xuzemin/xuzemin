package com.ctv.annotation.action;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.ctv.annotation.WhiteBoardApplication;
import com.ctv.annotation.element.Material;
import com.ctv.annotation.element.Point;
import com.ctv.annotation.element.Polygon;
import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.utils.PanelUtils;
import com.ctv.annotation.view.PanelManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class FrameBufferEraserState  extends Action{
    static String TAG = "FrameBufferEraserState";

    public static int PolygonId = 0;
    private boolean finish = false;
    public static final int MSG_TOUCH_UP= 1;
    private static final String KEY_PID = "touch_pid";
    private static final String KEY_X = "touch_x";
    private static final String KEY_Y = "touch_y";
    private static final int MESSAGE_ERASER_UP = 1;
    private Bitmap mBackgroundBitmap;
    private Polygon mDstPolygon;
    public static float mEraserHeight = Constants.ERASER_WIDTH;
    public static float mEraserWidth = Constants.ERASER_WIDTH;
    private boolean mEraserNeedUp = false;
    private Paint mErasePaint;

    ArrayList<Material> mAfterMaterials = new ArrayList();
    ArrayList<Material> mBeforeMaterials = new ArrayList();


    private android.os.Handler mHandler = new android.os.Handler() {
        public void handleMessage(Message msg)
        {
            if (msg.what == MSG_TOUCH_UP)
            {
                Bundle bundle = msg.getData();
                Rect polygonRect = mDstPolygon.getRect();
//                BaseUtils.dbg(TAG, "polygonRect = " + polygonRect.left + " / " + polygonRect.top + " / " + polygonRect.right + " / " + polygonRect.bottom);
                eraserUp(bundle.getInt(FrameBufferEraserState.KEY_PID), bundle.getFloat(FrameBufferEraserState.KEY_X), bundle.getFloat(FrameBufferEraserState.KEY_Y));
                clearPolygon();
            }
        }
    };
    private PointF[] mPrevPointF = new PointF[15];
    private PanelManager mManager;
    private int mTouchPointerId = -1;
    private Path cachePath = new Path();
    Rect mPreEraseRect = new Rect();
    Rect mCurrentEraseRect = new Rect();
    Rect mLastRect = new Rect();
    private long mDownTime;
    private double distance;
    private DashPathEffect mDashPathEffect;
    Point mPrevPoint;


    protected BlockingQueue<Polygon> mEraserQueue = new ArrayBlockingQueue<Polygon>(20);
    private List<Polygon> mListPolygon = new ArrayList<Polygon>();
    private void nativeEraserFrameBuffer(int left,int top, int right,int bottom)
    {
//        JniPainterUtil.nativeEraserFrameBuffer(left,top,right,bottom);
    }

    private void nativeEnableFrameBufferPost(int i) {
//        JniPainterUtil.nativeEnableFrameBufferPost(i);
    }

    private void nativeEraserDown(int i, float f, float f2, float f3, float f4, Bitmap bitmap) {
//        JniPainterUtil.nativeEraserDown(i, f, f2, f3, f4, bitmap);
    }

    private void nativeEraserMove(int i, float f, float f2, float f3, float f4) {
//        JniPainterUtil.nativeEraserMove(i, f, f2, f3, f4);
    }

    private void nativeEraserUp(int i, float f, float f2, Bitmap bitmap) {
//        JniPainterUtil.nativeEraserUp(i, f, f2, bitmap);
    }

    public FrameBufferEraserState(PanelManager manager)
    {
        Log.d("hhh", "FrameBufferEraserState: ");
        mManager = manager;
        mErasePaint = new Paint();
        mErasePaint.setColor(Color.BLACK);
        mErasePaint.setAntiAlias(true);
        mErasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mErasePaint.setStyle(Paint.Style.FILL);
        int length = mPrevPointF.length;
        for (int i = 0; i < length; i++)
        {
            mPrevPointF[i] = new PointF(-1.0f, -1.0f);
        }
        mDstPolygon = new Polygon(WhiteBoardApplication.SCREEN_WIDTH, WhiteBoardApplication.SCREEN_HEIGHT);
    }

    private Rect getRubberRect(int x, int y, int w, int h) {

        return new Rect(x - w, y - h, x + w, y + h);

    }
    protected void RubberPath(Canvas mCanvas, Rect mRect, float toX, float toY) {

        this.cachePath.reset();
        float padding = (float) Constants.erase_value - 5.0f;

        this.cachePath.moveTo(toX - padding, toY - padding);
        this.cachePath.lineTo(toX + padding, toY - padding);
        this.cachePath.lineTo(toX + padding, toY + padding);
        this.cachePath.lineTo(toX - padding, toY + padding);

        // 绘制虚线框
        this.cachePath.close();
        Rect dirtyRect = PanelUtils.rectAddWidth(mRect, 300);
        if(System.currentTimeMillis() - mDownTime > 35 || finish || distance > 80) {
            //   mCanvas.drawPath(cachePath, mRectPaint);
            // 处理path
            mManager.drawScreen(dirtyRect);
            mDownTime = System.currentTimeMillis();
        }
/*        mCanvas.drawPath(cachePath, mRectPaint);
        // 处理path
        mManager.drawScreen(dirtyRect);*/
    }

    private Rect drawEraser(PanelManager manager, int x, int y, int w, int h)
    {
        Log.d("hhh", "drawEraser: ");
        mLastRect.set(x - w, y - h, x + w, y + h);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        manager.getCacheCanvas().drawRect(mLastRect, paint);

        return new Rect(mLastRect);

    }
    private Matrix matrix = null;
    public void drawr(PanelManager mManager,Rect rect){
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        if (this.matrix == null) {
            this.matrix = new Matrix();
        }
        mManager.getCacheCanvas().drawRect(rect, paint);

    }
    int pointerId = -1;
    static Rect mLastEraseRect = new Rect();

    public boolean onTouchEvent(MotionEvent pEvent)
    {
        // pEvent.getAction();
        int index = pEvent.getActionIndex();
        int pid = pEvent.getPointerId(index);
        int action = pEvent.getActionMasked();
//      BaseUtils.dbg(TAG, "event.size(0) = " + pEvent.getSize(0) + " , event.size = " + pEvent.getSize() + " , pointerCount = " + pEvent.getPointerCount());
        Log.d("aaa", "onTouchEvent: index="+index);
        Log.d("aaa", "onTouchEvent: pid="+pid);
        Log.d("aaa", "onTouchEvent: action="+action);

        float pX = 0;
        float pY = 0;
        int pointCount = pEvent.getPointerCount();
        Log.d("aaa", "onTouchEvent: pointCount="+pointCount);

//        BaseUtils.dbg(TAG,"onTouch --> pointCount = " + pointCount + " , pointerId = " + pointerId + " , x_0 = " + pEvent.getX(0) + ", y_0 = " + pEvent.getY(0));
        if(pointerId == -1){
            pointerId = pid;
            pX = pEvent.getX(index);
            pY = pEvent.getY(index);
            Log.d("aaa", "onTouchEvent: px="+pX);
            Log.d("aaa", "onTouchEvent: py="+pY);
            Log.d("aaa", "--------------- ");
        }else{
            Log.d("aaa", "onTouchEvent: else  pointerid="+pointerId);
            for(int i=0;i<pointCount;i++){
//               BaseUtils.dbg(TAG,"onTouch --> index = " + i + ", getPointerId = " +  pEvent.getPointerId(i) + ", x = " + pEvent.getX(i) + ", y = " + pEvent.getY(i));
                if(pointerId == pEvent.getPointerId(i)){
                    pX = pEvent.getX(i);
                    pY = pEvent.getY(i);
                    Log.d("aaa", "onTouchEvent: for--pointer="+pointerId);
                    Log.d("aaa", "onTouchEvent: for--px="+pX);
                    Log.d("aaa", "onTouchEvent: for--py="+pY);
                    break;
                }
            }
        }
//        BaseUtils.dbg(TAG,"onTouch -------------------------------------------------> 计算之后： pointerId = " + pointerId + " , pX = " + pX + ", pY = " + pY);
        if(pX == 0 && pY == 0){
            pointerId = pid;
            pX = pEvent.getX(index);
            pY = pEvent.getY(index);
        }
        float xx = pX;
        float yy = pY;

        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                Log.d("ccc", "onTouchEvent: down");
                mDownTime = System.currentTimeMillis();
                pointerId = pid;
                mPreEraseRect = new Rect((int) (xx - ((float) mEraserWidth)), (int) (yy - ((float) mEraserHeight)), (int) (((float) mEraserWidth) + xx), (int) (((float) mEraserHeight) + yy));
                mLastEraseRect = new Rect((int) (pX - ((float) Constants.erase_value)), (int) (pY - ((float) Constants.erase_value)), (int) (((float) Constants.erase_value) + pX), (int) (((float) Constants.erase_value) + pY));
                //  mLastEraseRect = new Rect((int) (xx - ((float) mEraserWidth)), (int) (yy - ((float) mEraserHeight)), (int) (((float) mEraserWidth) + xx), (int) (((float) mEraserHeight) + yy));
                touchDown(pid, pX, pY);

              //  mBeforeMaterials.addAll(mManager.getMaterials());

                finish = false;

/*                if (BuildConfig.is_accelerate)
                {
                    mManager.lockCanvas();
                }*/
                return true;
//                break;
            case MotionEvent.ACTION_UP:
                Log.d("ccc", "onTouchEvent: up");
                mListPolygon.add(mDstPolygon);
                mEraserQueue.offer(mDstPolygon);

//                JniPainterUtil.EnableFrameBufferPost(0);
/*                touchUp(pid, pX, pY);
                mManager.repaintToOffScreenCanvas(null, true);*/
                finish=true;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("ccc", "onTouchEvent: move");
                touchMove(pid, pX, pY);
                break;
        }



        Rect eraseRect = new Rect((int) (xx - ((float) mEraserWidth)), (int) (yy - ((float) mEraserHeight)), (int) (((float) mEraserWidth) + xx), (int) (((float) mEraserHeight) + yy));

        mCurrentEraseRect = eraseRect;
        //   mManager.drawBetweenRect(mManager.getCacheCanvas(), mLastEraseRect, eraseRect, mErasePaint);

        eraseRect = new Rect(eraseRect);
        eraseRect.union(mLastEraseRect);
        eraseRect.union(eraseRect);

        Rect actualRect = new Rect(eraseRect);
        Canvas canvas = mManager.getCacheCanvas();

        if (action != MotionEvent.ACTION_UP)
        {
//            for (Material m2 : mManager.getMaterials())
//            {
//                if (m2 instanceof Image && ((Image) m2).intersect(actualRect))
//                {
//                    m2.draw(canvas);
//                }
//            }
        }
        //刷新临时橡皮擦空间
        Rect dirtyRect = PanelUtils.rectAddWidth(eraseRect, 5);

        //绘制橡皮擦,记录最后一次绘制的rect
        //  mLastEraseRect = getRubberRect((int) pX, (int) pY, (int) Constants.erase_value, (int) Constants.erase_value);
        mLastEraseRect = drawEraser(mManager, (int) xx, (int) yy, (int) mEraserWidth, (int) mEraserHeight);


/*        if (BuildConfig.is_accelerate)
        {
            drawAcc(mManager, eraseRect, (int) xx, (int) yy);
        }
        else*/
        {
            mManager.drawScreen(dirtyRect);
        }

        mPreEraseRect = mCurrentEraseRect;

        if (pEvent.getActionMasked() == MotionEvent.ACTION_UP)
        {
            mLastEraseRect = drawEraser(mManager, (int) xx, (int) yy, (int) mEraserWidth, (int) mEraserHeight);
            Rect upRect = PanelUtils.rectAddWidth(mLastRect, 30);
            mManager.cleanTargetRect(upRect);

//            for (Material m2 : mManager.getMaterials())
//            {
//                if (m2 instanceof Image && ((Image) m2).intersect(upRect))
//                {
//                    m2.draw(canvas);
//                }
//            }

 /*           if (BuildConfig.is_accelerate)
            {
                drawAcc(mManager, upRect, (int) xx, (int) yy);
            }
            else*/
            {
                mManager.drawScreen(upRect);
            }
            mPreEraseRect = mLastRect;
            touchUp(pid, pX, pY);

        }

        return true;
    }

    private void touchDown(int pointId, float x, float y)
    {
        if (mEraserNeedUp)
        {
            mEraserNeedUp = false;
            return;
        }

        eraserDown(pointId, x, y);
    }


    private void touchMove(int pointId, float x, float y)
    {
        if (mTouchPointerId == -1)
        {
            eraserDown(pointId, x, y);
        }
        else
        {
            eraserMove(mTouchPointerId, x, y);
        }
    }

    private void touchUp(int pointId, float x, float y)
    {
        Message msg = Message.obtain();
        msg.what = MSG_TOUCH_UP;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PID, pointId);
        bundle.putFloat(KEY_X, x);
        bundle.putFloat(KEY_Y, y);
        msg.setData(bundle);
        mHandler.sendMessageDelayed(msg, 20);
        mEraserNeedUp = true;
        pointerId = -1;

    }

    private void eraserDown(int pid, float pX, float pY)
    {

        nativeEnableFrameBufferPost(0);
        mBackgroundBitmap = mManager.getCurrentViewBitmap();
        nativeEraserDown(pid, pX, pY, mEraserWidth, mEraserHeight, mBackgroundBitmap);
        mTouchPointerId = pid;
        mPrevPointF[pid].x = pX;
        mPrevPointF[pid].y = pY;

        mDstPolygon.addRect((int) (pX - mEraserWidth), (int) (pY - mEraserHeight), (int) (mEraserWidth + pX), (int) (mEraserHeight + pY));
        clearPolygon();
    }

    private void clearPolygon()
    {
        mDstPolygon.clear();
    }


    private void eraserMove(int pid, float pX, float pY)
    {
        nativeEnableFrameBufferPost(0);
        if (mPrevPointF[pid].x == -1.0f)
        {
            nativeEraserDown(pid, pX, pY, mEraserWidth, mEraserHeight, mBackgroundBitmap);
            mPrevPointF[pid].x = pX;
            mPrevPointF[pid].y = pY;
            return;
        }
        nativeEraserMove(pid, pX, pY, mEraserWidth, mEraserHeight);


        distance = Math.sqrt((double) (((mPrevPointF[pid].x - pX) * (mPrevPointF[pid].x - pX)) + ((mPrevPointF[pid].y - pY) * (mPrevPointF[pid].y - pY))));
//
        if (distance > ((double) mEraserWidth / 2))
        {
            int pointCount = ((int) (distance / ((double) mEraserWidth / 2))) + 1;
            float deltaX = (pX - mPrevPointF[pid].x) / ((float) pointCount);
            float deltaY = (pY - mPrevPointF[pid].y) / ((float) pointCount);
            float x = mPrevPointF[pid].x;
            float y = mPrevPointF[pid].y;
            for (int i = 0; i < pointCount; i++)
            {
                x += deltaX;
                y += deltaY;
                Rect temp = new Rect((int) (x - mEraserWidth - 1), (int) (y - mEraserHeight - 1), (int) (mEraserWidth + x + 1), (int) (mEraserHeight + y + 1));
                mDstPolygon.addRect(temp);
            }
        }
        else
        {
            Rect temp = new Rect((int) (pX - mEraserWidth - 1), (int) (pY - mEraserHeight - 1), (int) (mEraserWidth + pX + 1), (int) (mEraserHeight + pY + 1));
            mDstPolygon.addRect(temp);
        }
        mPrevPointF[pid].x = pX;
        mPrevPointF[pid].y = pY;

    }

    private void eraserUp(int pid, float pX, float pY)
    {
        mTouchPointerId = -1;
        nativeEraserUp(pid, pX, pY, mBackgroundBitmap);

        //注释之后会取消回退,减少计算
        // splitShapeByEraserRect(mDstPolygon);

        finish = true;

        int length = mPrevPointF.length;
        for (int i = 0; i < length; i++)
        {
            mPrevPointF[i].x = -1.0f;
            mPrevPointF[i].y = -1.0f;
        }
        nativeEnableFrameBufferPost(1);
    }


    @Override public void linkMaterial(List<Material> list)
    {

    }

    @Override public boolean onTouch(PanelManager panelManager, MotionEvent motionEvent)
    {
        return false;
    }

    public void undo(PanelManager manager)
    {
        super.undo(manager);
//        manager.getMaterials().clear();
//        manager.getMaterials().addAll(mBeforeMaterials);
    }

    public void redo(PanelManager manager)
    {
        super.redo(manager);
//        manager.getMaterials().clear();
//        manager.getMaterials().addAll(mAfterMaterials);
    }

    public boolean isFinish()
    {
        return finish;
    }

    public static void drawAcc(PanelManager manager, Rect rect, int x, int y)
    {
        if (rect.left < 0)
        {
            rect.left = 0;
        }
        if (rect.top < 0)
        {
            rect.top = 0;
        }
        if (rect.right >= 1920)
        {
            rect.right = 1919;
        }
        if (rect.bottom >= 1080)
        {
            rect.bottom = 1079;
        }

        int width = rect.width();
        int height = rect.height();
        int[] pixelsNew = new int[width * height];
        manager.getCurrentViewBitmap().getPixels(pixelsNew,0,width,rect.left,rect.top,width,height);
        // JniPainterUtil.WriteRect(0, 0, rect.left, rect.top, rect.right, rect.bottom, pixelsNew, 0, 0, null);
    }
}
