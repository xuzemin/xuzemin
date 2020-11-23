package com.mphotool.whiteboard.action;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import com.mphotool.whiteboard.elements.Image;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.PencilInk;
import com.mphotool.whiteboard.elements.Point;
import com.mphotool.whiteboard.elements.Polygon;
import com.mphotool.whiteboard.elements.ShapeMaterial;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.SharedPreferencesUtils;
import com.mphotool.whiteboard.view.BaseThread;
import com.mphotool.whiteboard.view.FloatBallManager;
import com.mphotool.whiteboard.view.PanelManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RubberRaftModule extends Action{
    private static String TAG = "RubberRaftModule";
    private List<Polygon> mListPolygon = new ArrayList<Polygon>();
    private PanelManager mManager;
    private Polygon mDstPolygon;
    private Path cachePath = new Path();
    private static Rect mLastEraseRect = new Rect();
    private long mDownTime;
    private boolean finish = false;
    private boolean mEraserNeedUp = false;
    public Boolean RubberQueueIsOk = true;
    private int mTouchPointerId = -1;
    private double distance = 0.0f;

//    private FloatBallManager mFBManager;
    Point mPrevPoint;
    DashPathEffect mDashPathEffect;
    Paint mRectPaint = new Paint();
    ArrayList<Material> mAfterMaterials = new ArrayList();
    ArrayList<Material> mBeforeMaterials = new ArrayList();

    protected BlockingQueue<Polygon> mEraserQueue = new ArrayBlockingQueue<Polygon>(20);
    RubberRaftModule.RubberThread mRubberThread;

    public RubberRaftModule(PanelManager manager){
        mManager = manager;
        /**初始画ErasePaint**/
        mRectPaint = new Paint();

        mRectPaint.setAntiAlias(true);
        mRectPaint.setStrokeWidth(4.0f);
//        mRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        int mColor = (int)SharedPreferencesUtils.getParam(Constants.BG_COLOLR, Constants.BG_COLOLR_DEFAULT);
        mRectPaint.setColor(mColor ^ Constants.INK_SELECTED_COLOR_MASK);
        mRectPaint.setStyle(Paint.Style.STROKE);
        mRectPaint.setStrokeJoin(Paint.Join.ROUND);
        mRectPaint.setStrokeCap(Paint.Cap.SQUARE);
        /**指定虚线样式*/
        float margin = mRectPaint.getStrokeWidth();
        mDashPathEffect = new DashPathEffect(new float[]{margin, margin * 1.5f, margin, margin * 1.5f}, 1.0f);
        mRectPaint.setPathEffect(mDashPathEffect);
        mRectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mListPolygon.clear();
        mPrevPoint = new Point(-1.0f, -1.0f);
        mDstPolygon = new Polygon(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
//        mFBManager = FloatBallManager.getInstance(mManager.mContext);
    }

    public void InitRubbrPolygon(){
        int mColor = (int)SharedPreferencesUtils.getParam(Constants.BG_COLOLR, Constants.BG_COLOLR_DEFAULT);
        mRectPaint.setColor(mColor ^ Constants.INK_SELECTED_COLOR_MASK);
        mDstPolygon = new Polygon(Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
    }

    class RubberThread extends BaseThread {
        Polygon mRubberPolygon = null;

        public RubberThread() {

        }

        @Override
        public void run() {
            super.run();
            BaseUtils.dbg(TAG, "EraserThread");
            while (true) {
                try {
                    mRubberPolygon = mEraserQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                splitShapeByEraserRect(mRubberPolygon);
                if(mEraserQueue.size() == 0){
                    if(mEraserNeedUp) {
                        mManager.repaintToOffScreenCanvas(mManager.mWorkingRect, true);
                    }
                    if(mListPolygon != null && mListPolygon.size() > 0){
                        for(int i=0;i<mListPolygon.size();i++) {
                            mListPolygon.get(i).clear();
                        }
                        mListPolygon.clear();
                    }
                    RubberQueueIsOk = true;
                }else{
                    RubberQueueIsOk = false;
                }
                BaseUtils.dbg(TAG, "mEraserQueue=" + mEraserQueue.size());
            }
        }
    }


    public void startRubberThread()
    {
        if (this.mRubberThread == null || !this.mRubberThread.isAlive())
        {
            this.mRubberThread = new RubberRaftModule.RubberThread();
            this.mRubberThread.start();
        }
    }

    public void stopRubberThread()
    {
        if (this.mRubberThread != null)
        {
            this.mRubberThread.stopThread();
            this.mRubberThread = null;
        }
    }

    private int mPointId = -1;
    public boolean onTouchEvent(MotionEvent mEvent) {
        int action = mEvent.getActionMasked();
        int index = mEvent.getActionIndex();
        float pX = mEvent.getX(index);
        float pY = mEvent.getY(index);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mPointId = mEvent.getPointerId(index);
                mDownTime = System.currentTimeMillis();
                touchDown(mPointId,pX,pY);
//                mFBManager.addBallView((int)pX, (int)pY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPointId = mEvent.getPointerId(index);
                touchMove(mPointId, pX, pY);
                break;
            case MotionEvent.ACTION_UP:
//                mFBManager.removeBallView();
                mPointId = mEvent.getPointerId(index);
                touchUp(mPointId,pX,pY);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPointId = mEvent.getPointerId(index);
                touchUp(mPointId,pX,pY);
                break;
            default:
                break;
        }
        return true;
    }

//    private Bitmap mBackgroundBitmap;
    private void touchDown(int pid,float pX, float pY) {
        if (pid != 0){
            return;
        }

        if (mEraserNeedUp) {
            mEraserNeedUp = false;
            return;
        }
//        BaseUtils.dbg(TAG, "touchDown pid=" + pid);
        mTouchPointerId = pid;
        mPrevPoint.mX = pX;
        mPrevPoint.mY = pY;
//        mBackgroundBitmap = mManager.getCurrentViewBitmap();
//        JniPainterUtil.nativeEraserDown(pid,pX,pY,Constants.erase_value,mBackgroundBitmap);
        mLastEraseRect = new Rect((int) (pX - ((float) Constants.erase_value)), (int) (pY - ((float) Constants.erase_value)), (int) (((float) Constants.erase_value) + pX), (int) (((float) Constants.erase_value) + pY));
        mDstPolygon.clear();
        mDstPolygon.addRect((int) (pX - Constants.erase_value), (int) (pY - Constants.erase_value), (int) (Constants.erase_value + pX), (int) (Constants.erase_value + pY));
        finish = false;
    }

    private void touchMove(int pointId, float x, float y) {
        if (pointId != 0){
            return;
        }

        if (mTouchPointerId == -1) {
            touchDown(pointId, x, y);
        } else {
            eraserMove(pointId, x, y);
        }
    }

    private void eraserMove(int pid, float pX, float pY) {
        float x = 0.0f;
        float y = 0.0f;
        if (mPrevPoint.mX == -1.0f) {
            mPrevPoint.mX = pX;
            mPrevPoint.mY = pY;
            return;
        }
        x = mPrevPoint.mX;
        y = mPrevPoint.mY;
        distance = Math.sqrt((double) (((x - pX) * (x - pX)) + ((y - pY) * (y - pY))));
        BaseUtils.dbg(TAG, "distance=" + distance);
        if (Math.abs((int)distance) < 2){
            return;
        }

        Boolean MoveEffective = false;
        Rect mTemp = new Rect();
        Canvas mCanvas = mManager.getCacheCanvas();

        Rect eraseRect = new Rect((int) (pX - ((float) Constants.erase_value)), (int) (pY - ((float) Constants.erase_value)), (int) (((float) Constants.erase_value) + pX), (int) (((float) Constants.erase_value) + pY));

        mManager.drawBetweenRect(mCanvas, mLastEraseRect, eraseRect);

        mManager.cleanTargetRect(mLastEraseRect);
        RubberPath(mManager,eraseRect,pX,pY);
//        JniPainterUtil.nativeEraserMove(pid,pX,pY);
        eraseRect = new Rect(eraseRect);
        eraseRect.union(mLastEraseRect);
        eraseRect.union(eraseRect);

        Rect actualRect = new Rect(eraseRect);
        for(int i=0;i<mManager.getMaterials().size();i++) {
            Material m2 = mManager.getMaterials().get(i);
            if (m2 instanceof Image && ((Image) m2).intersect(actualRect)) {
                m2.draw(mManager.getCacheCanvas());
            }
        }

        mLastEraseRect = getRubberRect((int) pX, (int) pY, (int) Constants.erase_value, (int) Constants.erase_value);
        if (distance > Constants.MIN_DISTANCE_BETWEEN_POINT) {
            int pointCount = ((int) (distance / Constants.MIN_DISTANCE_BETWEEN_POINT)) + 1;
            float deltaX = (pX - x) / ((float) pointCount);
            float deltaY = (pY - y) / ((float) pointCount);
            for (int i = 0; i < pointCount; i++)
            {
                x += deltaX;
                y += deltaY;
                Rect temp = new Rect((int) (x - Constants.erase_value - 1), (int) (y - Constants.erase_value - 1), (int) (Constants.erase_value + x + 1), (int) (Constants.erase_value + y + 1));
                MoveEffective = mDstPolygon.QuadrilateralTrajectory(x, y, pX, pY, Constants.MIN_DISTANCE_BETWEEN_POINT);
                if (MoveEffective) {
                    mDstPolygon.addRect(temp);
                }
            }
        } else {
            mTemp = new Rect((int) (pX - Constants.erase_value - 1), (int) (pY - Constants.erase_value - 1), (int) (Constants.erase_value + pX + 1), (int) (Constants.erase_value + pY + 1));
            MoveEffective = mDstPolygon.QuadrilateralTrajectory(x,y,pX,pY,Constants.MIN_DISTANCE_BETWEEN_POINT);
            if(MoveEffective) {
                mDstPolygon.addRect(mTemp);
            }
//            RubberPath(mCanvas,mTemp,pX,pY);
        }
        mPrevPoint.mX = pX;
        mPrevPoint.mY = pY;
    }

    private void touchUp(int pid, float pX, float pY) {
        if (pid != 0){
            return;
        }

        mLastEraseRect = getRubberRect((int) pX, (int) pY, (int) Constants.erase_value, (int) Constants.erase_value);
        Rect upRect = PanelUtils.rectAddWidth(mLastEraseRect, 8);
//        JniPainterUtil.nativeEraserUp(pid,pX,pY,mBackgroundBitmap);
        mManager.cleanTargetRect(upRect);
        for(int i=0;i<mManager.getMaterials().size();i++) {
            Material m2 = mManager.getMaterials().get(i);
            if (m2 instanceof Image && ((Image) m2).intersect(upRect)) {
                m2.draw(mManager.getCacheCanvas());
            }
        }
        mManager.drawScreen(upRect);

        mEraserNeedUp = true;
        mTouchPointerId = -1;
        mListPolygon.add(mDstPolygon);

        mEraserQueue.offer(mDstPolygon);

        mPrevPoint.mX = -1.0f;
        mPrevPoint.mY = -1.0f;
        mPointId = -1;
        finish = true;
    }

    private Rect getRubberRect(int x, int y, int w, int h) {
        return new Rect(x - w, y - h, x + w, y + h);
    }

    protected void RubberPath(PanelManager mMger,Rect mRect, float toX, float toY) {

        this.cachePath.reset();

        float padding = (float) Constants.erase_value - 5.0f;
        this.cachePath.moveTo(toX - padding, toY - padding);
        this.cachePath.lineTo(toX + padding, toY - padding);
        this.cachePath.lineTo(toX + padding, toY + padding);
        this.cachePath.lineTo(toX - padding, toY + padding);

        // 绘制虚线框
        this.cachePath.close();
        Rect dirtyRect = PanelUtils.rectAddWidth(mRect, 500);

        mMger.getCacheCanvas().drawPath(cachePath, mRectPaint);

        // 加速
//        mMger.getfbCanvas0().drawPath(cachePath, mRectPaint);
//        mMger.getfbCanvas1().drawPath(cachePath, mRectPaint);
//        mMger.getfbCanvas2().drawPath(cachePath, mRectPaint);

            // 处理path
//            mManager.drawScreen(dirtyRect);

        mManager.repaintScreen(dirtyRect);
    }

    private void splitShapeByEraserRect(Polygon polygon) {
        List<Material> removeShapes = new ArrayList();
        List<Material> addShapes = new ArrayList();
        List<Material> materials = mManager.getMaterials();
        int size = materials.size();
//        BaseUtils.dbg(TAG, "000000 +　materials.size()=" + size);
        long start = System.currentTimeMillis();
        for (int mindex = size - 1; mindex >= 0; mindex--) {
            Material tempShape = (Material) materials.get(mindex);
            if (tempShape instanceof PencilInk) {
                PencilInk ink = (PencilInk) tempShape;
                if (ink != null) {
                    if (ink.isInPolygon(polygon)) {
                        removeShapes.add(ink);
                    } else {
                        ArrayList<PencilInk> shapes = ink.splitPencil(polygon);
                        if (shapes != null) {
                            removeShapes.add(ink);
                            Iterator i$ = shapes.iterator();
                            while (i$.hasNext()) {
                                addShapes.add((PencilInk) i$.next());
                            }
                        }
                    }
                }
            }else if (tempShape instanceof ShapeMaterial){
                ShapeMaterial shape = (ShapeMaterial) tempShape;
                if (shape == null){
                    continue;
                }

                if (shape.isInPolygon(polygon)) // 在橡皮檫的矩形list记录中
                {
                    removeShapes.add(shape);
                    Constants.mIsEraser = false;
                } else { // 判断图形
                    List<ShapeMaterial> shapes = shape.splitShape(polygon);
                    if (shapes == null || shapes.size() == 0)
                    {
                        if(Constants.mIsEraser) {
                            removeShapes.add(shape);
                            Constants.mIsEraser = false;
                        }
                        continue;
                    }
                    removeShapes.add(shape);
                    Constants.mIsEraser = false;
                    Iterator i$ = shapes.iterator();
                    while (i$.hasNext())
                    {
                        addShapes.add((ShapeMaterial) i$.next());
                    }
                }
            }
        }

        if (!removeShapes.isEmpty()) {
            mManager.removeMaterialList(removeShapes);
        }
        if (!addShapes.isEmpty()) {
            mManager.addMaterialList(addShapes);
        }
        if (!removeShapes.isEmpty() || !addShapes.isEmpty()) {
            mAfterMaterials.addAll(mManager.getMaterials());
        }
//        BaseUtils.dbg(TAG, "calculating time=" + (System.currentTimeMillis()-start) + "ms");
    }

    public void undo(PanelManager manager) {
        super.undo(manager);
        manager.getMaterials().clear();
        manager.getMaterials().addAll(mBeforeMaterials);
    }

    public void redo(PanelManager manager) {
        super.redo(manager);
        manager.getMaterials().clear();
        manager.getMaterials().addAll(mAfterMaterials);
    }

    public boolean isFinish() {
        return finish;
    }

    @Override
    public void linkMaterial(List<Material> list) {

    }

    @Override
    public boolean onTouch(PanelManager panelManager, MotionEvent motionEvent) {
        return false;
    }

}