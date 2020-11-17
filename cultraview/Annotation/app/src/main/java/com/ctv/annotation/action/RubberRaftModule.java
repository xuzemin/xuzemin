package com.ctv.annotation.action;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;

import com.ctv.annotation.element.Material;
import com.ctv.annotation.element.Point;
import com.ctv.annotation.element.Polygon;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.utils.PanelUtils;
import com.ctv.annotation.utils.SharedPreferencesUtils;
import com.ctv.annotation.view.BaseThread;
import com.ctv.annotation.view.PanelManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RubberRaftModule extends Action{
    private static String TAG = "RubberRaftModule";
    private static Rect mLastEraseRect = new Rect();
    public Boolean RubberQueueIsOk = Boolean.valueOf(true);
    private int action;
    private Path cachePath = new Path();
    private double distance = 0.0d;
    private boolean finish = false;
    ArrayList<Material> mAfterMaterials = new ArrayList();
    ArrayList<Material> mBeforeMaterials = new ArrayList();
    DashPathEffect mDashPathEffect;
    private long mDownTime;
    private Polygon mDstPolygon;
    private boolean mEraserNeedUp = false;
    protected BlockingQueue<Polygon> mEraserQueue = new ArrayBlockingQueue(20);
    private List<Polygon> mListPolygon = new ArrayList();
    private PanelManager mManager;
    Point mPrevPoint;
    Paint mRectPaint = new Paint();
    RubberThread mRubberThread;
    private int mTouchPointerId = -1;
    private int pointerCount;

    class RubberThread extends BaseThread {
        Polygon mRubberPolygon = null;

        public void run() {
            super.run();
            BaseUtils.dbg(RubberRaftModule.TAG, "EraserThread");
            while (true) {
                try {
                    this.mRubberPolygon = (Polygon)mEraserQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mEraserNeedUp) {
                    Log.d(TAG, "run: repaintToOffScreenCanvas    mListPolygon.size ="+mListPolygon.size());
                    mManager.repaintToOffScreenCanvas(mManager.mWorkingRect, true);
                }
                if (mListPolygon != null && mListPolygon.size() > 0) {
                    for (int i = 0; i < mListPolygon.size(); i++) {
                        ((Polygon) mListPolygon.get(i)).clear();
                    }
                    mListPolygon.clear();
                }
                RubberQueueIsOk = Boolean.valueOf(true);

                BaseUtils.dbg(RubberRaftModule.TAG, "mEraserQueue=" + RubberRaftModule.this.mEraserQueue.size());
            }
        }
    }

    public RubberRaftModule(PanelManager manager) {
        this.mManager = manager;
        this.mRectPaint = new Paint();
        this.mRectPaint.setAntiAlias(true);
        this.mRectPaint.setStrokeWidth(4.0f);
        this.mRectPaint.setColor(Constants.INK_SELECTED_COLOR_MASK ^ ((Integer) SharedPreferencesUtils.getParam(Constants.BG_COLOLR, Integer.valueOf(Constants.BG_COLOLR_DEFAULT))).intValue());
        this.mRectPaint.setStyle(Paint.Style.STROKE);
        this.mRectPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mRectPaint.setStrokeCap(Paint.Cap.SQUARE);
        float margin = this.mRectPaint.getStrokeWidth();
        this.mDashPathEffect = new DashPathEffect(new float[]{margin, margin * 1.5f, margin, margin * 1.5f}, 1.0f);
        this.mRectPaint.setPathEffect(this.mDashPathEffect);
        this.mRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        this.mListPolygon.clear();
        this.mPrevPoint = new Point(-1.0f, -1.0f);
        this.mDstPolygon = new Polygon(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        // Constants.erase_value = 60.0f;
    }

    public void InitRubbrPolygon() {
        this.mRectPaint.setColor(Constants.INK_SELECTED_COLOR_MASK ^ ((Integer) SharedPreferencesUtils.getParam(Constants.BG_COLOLR, Integer.valueOf(Constants.BG_COLOLR_DEFAULT))).intValue());
        this.mDstPolygon = new Polygon(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        //   Constants.erase_value = 60.0f;
    }

    public void startRubberThread() {
        if (this.mRubberThread == null || !this.mRubberThread.isAlive()) {
            this.mRubberThread = new RubberThread();
            this.mRubberThread.start();
        }
    }

    public void stopRubberThread() {
        if (this.mRubberThread != null) {
            this.mRubberThread.stopThread();
            this.mRubberThread = null;
        }
    }

    public boolean onTouchEvent(MotionEvent mEvent) {

        pointerCount = mEvent.getPointerCount();

        this.action = mEvent.getActionMasked();
        int index = mEvent.getActionIndex();
        float pX = mEvent.getX(index);
        float pY = mEvent.getY(index);
        Log.d(TAG, "onTouchEvent: pointerCount" + this.action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//              float pX = mEvent.getX(0);
//              float pY = mEvent.getY(0);
                this.mDownTime = System.currentTimeMillis();
                touchDown(0, pX, pY);

                break;
            case MotionEvent.ACTION_UP:
                touchUp(0, mEvent.getX(0), mEvent.getY(0));
                // touchUp(0,pX,pY);

                break;
            case MotionEvent.ACTION_MOVE:
                // touchMove(0, mEvent.getX(0), mEvent.getY(0));
                touchMove(0,pX,pY);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // touchUp(0, mEvent.getX(0), mEvent.getY(0));
                touchMove(0,pX,pY);
                touchUp(0,pX,pY);
                break;
        }
        return true;
    }

    private void touchDown(int pid, float pX, float pY) {
        if (this.mEraserNeedUp) {
            this.mEraserNeedUp = false;
            return;
        }
        this.mTouchPointerId = pid;
        this.mPrevPoint.mX = pX;
        this.mPrevPoint.mY = pY;
        mLastEraseRect = new Rect((int) (pX - Constants.ERASER_WIDTH), (int) (pY - Constants.ERASER_WIDTH), (int) (Constants.ERASER_WIDTH + pX), (int) (Constants.ERASER_WIDTH + pY));
        this.mDstPolygon.clear();
        this.mDstPolygon.addRect((int) (pX - Constants.ERASER_WIDTH), (int) (pY - Constants.ERASER_WIDTH), (int) (Constants.ERASER_WIDTH + pX), (int) (Constants.ERASER_WIDTH + pY));
        this.finish = false;
        BaseUtils.dbg("hhc","ERASER_WIDTH ="+ Constants.ERASER_WIDTH);
    }

    private void touchMove(int pointId, float x, float y) {
        if (this.mTouchPointerId == -1) {
            touchDown(pointId, x, y);
        } else {
            eraserMove(pointId, x, y);
        }
    }

    private void eraserMove(int pid, float pX, float pY) {
        Boolean MoveEffective = Boolean.valueOf(false);
        Rect mTemp = new Rect();
        Canvas mCanvas = this.mManager.getCacheCanvas();
        if (this.mPrevPoint.mX == -1.0f) {
            this.mPrevPoint.mX = pX;
            this.mPrevPoint.mY = pY;
            return;
        }
        float x = this.mPrevPoint.mX;
        float y = this.mPrevPoint.mY;
        this.distance = Math.sqrt((double) (((x - pX) * (x - pX)) + ((y - pY) * (y - pY))));
        Rect eraseRect = new Rect((int) (pX - Constants.ERASER_WIDTH), (int) (pY - Constants.ERASER_WIDTH), (int) (Constants.ERASER_WIDTH + pX), (int) (Constants.ERASER_WIDTH + pY));
        this.mManager.drawBetweenRect(mCanvas, mLastEraseRect, eraseRect);
        mManager.cleanTargetRect(mLastEraseRect);
        RubberPath(mCanvas,eraseRect,pX,pY);

        eraseRect = new Rect(eraseRect);
        eraseRect.union(mLastEraseRect);
        eraseRect.union(eraseRect);

//        Rect actualRect = new Rect(eraseRect);
//        for(int i=0;i<mManager.getMaterials().size();i++) {
//            Material m2 = mManager.getMaterials().get(i);
//            if (m2 instanceof Image && ((Image) m2).intersect(actualRect)) {
//                m2.draw(mManager.getCacheCanvas());
//            }
//        }

        mLastEraseRect = getRubberRect((int) pX, (int) pY, (int) Constants.ERASER_WIDTH, (int) Constants.ERASER_WIDTH);


        if (this.distance > 20.0d) {
            int pointCount = ((int) (this.distance / 20.0d)) + 1;
            float deltaX = (pX - x) / ((float) pointCount);
            float deltaY = (pY - y) / ((float) pointCount);
            for (int i = 0; i < pointCount; i++) {
                x += deltaX;
                y += deltaY;
                this.mDstPolygon.addRect(new Rect((int) ((x - Constants.ERASER_WIDTH) - 1.0f), (int) ((y - Constants.ERASER_WIDTH) - 1.0f), (int) ((Constants.ERASER_WIDTH + x) + 1.0f), (int) ((Constants.ERASER_WIDTH + y) + 1.0f)));
            }
        } else {
            this.mDstPolygon.addRect(new Rect((int) ((pX - Constants.ERASER_WIDTH) - 1.0f), (int) ((pY - Constants.ERASER_WIDTH) - 1.0f), (int) ((Constants.ERASER_WIDTH + pX) + 1.0f), (int) ((Constants.ERASER_WIDTH + pY) + 1.0f)));
        }
        this.mPrevPoint.mX = pX;
        this.mPrevPoint.mY = pY;
    }

    private void touchUp(int pointId, float pX, float pY) {

        mLastEraseRect = getRubberRect((int) pX, (int) pY, (int) Constants.ERASER_WIDTH, (int) Constants.ERASER_WIDTH);
        Rect upRect = PanelUtils.rectAddWidth(mLastEraseRect, 0);
        Log.i("gyx", "touchUp");
        this.mManager.cleanTargetRect(upRect);
//        for (int i = 0; i < this.mManager.getMaterials().size(); i++) {
//            Material m2 = (Material) this.mManager.getMaterials().get(i);
//            if ((m2 instanceof Image) && ((Image) m2).intersect(upRect)) {
//                m2.draw(this.mManager.getCacheCanvas());
//            }
//        }
        this.mManager.drawScreen(upRect);
        this.mEraserNeedUp = true;
        this.mTouchPointerId = -1;
        this.mListPolygon.add(this.mDstPolygon);
        this.mEraserQueue.offer(this.mDstPolygon);
        this.mPrevPoint.mX = -1.0f;
        this.mPrevPoint.mY = -1.0f;
        finish = true;
    }

    private Rect getRubberRect(int x, int y, int w, int h) {
        return new Rect(x - w, y - h, x + w, y + h);
    }

    protected void RubberPath(Canvas mCanvas, Rect mRect, float toX, float toY) {
        this.cachePath.reset();
        float padding = Constants.ERASER_WIDTH - 5.0f;
        this.cachePath.moveTo(toX - padding, toY - padding);
        this.cachePath.lineTo(toX + padding, toY - padding);
        this.cachePath.lineTo(toX + padding, toY + padding);
        this.cachePath.lineTo(toX - padding, toY + padding);
        this.cachePath.close();
        Rect dirtyRect = PanelUtils.rectAddWidth(mRect, 500);

        mCanvas.drawPath(this.cachePath, this.mRectPaint);
        this.mManager.drawScreen(dirtyRect);
    }


    public void undo(PanelManager manager) {
        super.undo(manager);
//        manager.getMaterials().clear();
//        manager.getMaterials().addAll(this.mBeforeMaterials);
    }

    public void redo(PanelManager manager) {
        super.redo(manager);
//        manager.getMaterials().clear();
//        manager.getMaterials().addAll(this.mAfterMaterials);
    }

    public boolean isFinish() {
        return finish;
    }

    public void linkMaterial(List<Material> list) {
    }

    public boolean onTouch(PanelManager panelManager, MotionEvent motionEvent) {
        return false;
    }

}
