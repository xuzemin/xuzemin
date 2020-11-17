package com.ctv.annotation.action;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;

import com.ctv.Jni.WbJniCall;
import com.ctv.annotation.BuildConfig;
import com.ctv.annotation.element.Material;
import com.ctv.annotation.element.PencilInk;
import com.ctv.annotation.element.Point;
import com.ctv.annotation.element.PointEntity;
import com.ctv.annotation.element.PointId;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.utils.PanelUtils;
import com.ctv.annotation.view.BaseThread;
import com.ctv.annotation.view.PanelManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import java.util.logging.LogRecord;

public class PenAction extends Action {
    private static final Object SHAPES_LOCK = new Object();
    private static final String TAG = "PenAction";
    public static int pointCount = 0;
    private boolean finish = false;
    protected int mBrushColor = -1;
    protected float mBrushThickness = 4.5f;
    protected PencilInk[] mBrushes;
    private boolean[] mINITs = new boolean[20];
    protected boolean mIsValidDrawing = false;

    /**
     * 单指（触摸点已经UP，则认为是无效操作）
     */
    protected boolean mIsSinglefinger = false;
    protected List<Material> mLocalBrushShapes;
    private PanelManager mManager;
    protected BlockingQueue<PointEntity> mPointQueue = new ArrayBlockingQueue(100);
    InsertPointThread mPointThread;
    protected int mPointerId;
    protected float mPressure;
    private float[] mPrevXs = new float[20];
    private float[] mPrevYs = new float[20];
    protected float mX;
    protected float mY;
    protected int penType = 0;
    private final static int SCAN_SURFACE_START = 0x01;
    private final static int SCAN_SURFACE_END = 0x02;
    protected int Refresh_count = 0;
    /**溢出点集合**/
    private List<PointId> OverflowID ;
    private Handler ScanHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case SCAN_SURFACE_START:
                    if (Refresh_count < 3){
                        WbJniCall.fbLock(1);
                        mManager.repaintScreen(null);
                        ScanHandler.sendEmptyMessageDelayed(SCAN_SURFACE_END, 50);
                        Refresh_count ++;
                    }
                    break;
                case SCAN_SURFACE_END:
                    WbJniCall.fbLock(0);
                    mManager.repaintScreen(null);
                    ScanHandler.sendEmptyMessageDelayed(SCAN_SURFACE_START,50);
                    break;
            }
        }
    };

    public PenAction(PanelManager manager) {
        this.mManager = manager;
        this.mBrushes = new PencilInk[10];
        this.mLocalBrushShapes = new ArrayList();
        OverflowID = new ArrayList<PointId>();
    }

    class InsertPointThread extends BaseThread {
        public void run() {
            super.run();
            PointEntity pointEntity = null;
            while (this.mIsRunning) {
                try {
                    pointEntity = (PointEntity) PenAction.this.mPointQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (pointEntity != null) {
                    insertPoint(pointEntity);
                }
                if (PenAction.this.finish && PenAction.this.mPointQueue.size() == 0) {
                    PenAction.this.mManager.actionStatus = PanelManager.S_NONE;
                } else {
                    PenAction.this.mManager.actionStatus = PanelManager.S_DURING_INSERT_POINT;
                }
            }
        }

        private void insertPoint(PointEntity pointEntity) {
            PencilInk pencil = pointEntity.pencil;
            Point p = pointEntity.point;
            if (pointEntity.pointType == 0) {
                pencil.startOnPoint(p);
            } else if (pointEntity.pointType == 1) {
                pencil.continueOnPoint(p);
            } else if (pointEntity.pointType == 2) {
                pencil.endOnPoint(p);
            }
        }
    }

    public void startPointThread() {
        InsertPointThread insertPointThread = this.mPointThread;
        if (insertPointThread == null || !insertPointThread.isAlive()) {
            this.mPointThread = new InsertPointThread();
            this.mPointThread.start();
        }
    }
    @Override
    public void linkMaterial(List<Material> list) {

    }

    public void setCurrentBrushColor(int color) {
        this.mBrushColor = color;
    }
    public void stopPointThread() {
        InsertPointThread insertPointThread = this.mPointThread;
        if (insertPointThread != null) {
            insertPointThread.stopThread();
            this.mPointThread = null;
        }
    }
    public void setCurrentBrushThickness(float thickness) {
        this.mBrushThickness = thickness;
    }

    protected boolean enableKalmanFilter() {
        return true;
    }

    @Override
    public boolean onTouch(PanelManager panelManager, MotionEvent motionEvent) {

        return false;
    }
    public boolean  onTouchEvent(MotionEvent event) {
        String str = "hggh";
        Log.d(str, "PenAction -------------------------onTouchEvent: ");
        int action = event.getActionMasked();
 //       int count = event.getPointerCount();
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("action=");
//        stringBuilder.append(action);
//        stringBuilder.append(" count=");
//        stringBuilder.append(count);
//        stringBuilder.append("mIsValidDrawing = ");
//        stringBuilder.append(this.mIsValidDrawing);
//        BaseUtils.dbg(str, stringBuilder.toString());
        switch (action){
            case MotionEvent.ACTION_DOWN:
            {
                this.mIsValidDrawing = true;
                this.mIsSinglefinger = false;
                Log.d(str, "PenAction-----onTouchEvent:  ACTION_DOWN");
                WbJniCall.fbStart(1);
                this.mManager.repaintScreen(null);
                touchDown(event);
                this.finish = false;
            }
            break;

            case MotionEvent.ACTION_UP:
            {
                OverflowID.clear();
                Log.d(str, "PenAction  onTouchEvent:  ACTION_UP");
                if (this.mIsValidDrawing && !mIsSinglefinger) {
                    touchUp(event);
                }
                this.finish = true;
                this.mIsValidDrawing = false;
            }
            break;
            case MotionEvent.ACTION_POINTER_UP:
                int actionIndex = event.getActionIndex();
                int pointerId = event.getPointerId(actionIndex);
                if (this.mIsValidDrawing)
                {
                   PointId mTemp = checkPointID(pointerId,OverflowID);
                    if(mTemp != null) {
                        if (!PanelUtils.isMultiPen()) {
                            this.mIsSinglefinger = true;
                            touchUp(event);
                            break;
                        }else {
                            OverflowID.remove(mTemp);
                            touchPointerUp(event);
                        }
                    }
                    break;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (!PanelUtils.isMultiPen()) {
                    break;
                }
                if (this.mIsValidDrawing)
                {
                    touchPointerDown(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
            {
                Log.d(str, "PenAction  onTouchEvent:  ACTION_MOVE");
                if (this.mIsValidDrawing) {
                    touchMove(event);
                }
            }
            break;
        default:
            break;

        }

        return true;
    }
    protected void touchPointerDown(MotionEvent event)
    {
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);

        BaseUtils.dbg(TAG, "AndroidRuntime actionIndex=" + actionIndex + " pointer=" + event.getPointerCount());
        if (!PanelUtils.isMultiPen()){
            return;
        }
        if (BuildConfig.is_accelerate)
            WbJniCall.fbLock(1);
        if (event.getPointerCount() <= Constants.MULTI_FINGER_NUMBER) {
            PointId mTemp = checkPointID(pointerId,OverflowID);
            if(mTemp != null)
                OverflowID.remove(mTemp);
            PointId mtemp = new PointId();
            mtemp.setPointID(pointerId);
            OverflowID.add(mtemp);
            startBrushStroke(pointerId, event.getX(actionIndex), event.getY(actionIndex), event.getPressure(actionIndex));
        }else{
            if(OverflowID.size() < Constants.MULTI_FINGER_NUMBER){
                PointId mTemp = checkPointID(pointerId,OverflowID);
                if(mTemp != null)
                    OverflowID.remove(mTemp);
                PointId mtemp = new PointId();
                mtemp.setPointID(pointerId);
                OverflowID.add(mtemp);
                startBrushStroke(pointerId, event.getX(actionIndex), event.getY(actionIndex), event.getPressure(actionIndex));
            }
        }
    }
    protected void touchPointerUp(MotionEvent event) {
        final int actionIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(actionIndex);
//        BaseUtils.dbg(TAG, "pointerId=" + pointerId + " ,actionIndex=" + actionIndex);

        final float x = event.getX(actionIndex);
        final float y = event.getY(actionIndex);
        if (this.mBrushes[pointerId] != null && !this.mBrushes[pointerId].isFinished())
        {
            endBrushStroke(pointerId, x, y);
        }
    }
    protected Rect endBrushStroke(int pointerId, float x, float y)
    {
        Rect rect = this.mBrushes[pointerId].endStroke(x, y);
        this.mBrushes[pointerId].drawCurrentPath(mManager);

        Point p = new Point(x, y, false);
        PointEntity pointEntity = new PointEntity(mBrushes[pointerId], pointerId, p, 2);
        mPointQueue.offer(pointEntity);

        return rect;
    }
    public PointId checkPointID(int id, List<PointId> list){
        Iterator<PointId> it = list.iterator();
        while (it.hasNext()){
            PointId mTemp = it.next();
            if(mTemp.getPointID() == id)
                return mTemp;
        }
        return null;
    }
    protected void touchDown(MotionEvent event) {
        WbJniCall.fbLock(1);
        int actionIndex = event.getActionIndex();
        this.mX = event.getX(actionIndex);
        this.mY = event.getY(actionIndex);
        this.mPressure = event.getPressure(actionIndex);
        this.mPointerId = event.getPointerId(actionIndex);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("touchDown: mPointerId = ");
        stringBuilder.append(this.mPointerId);
        Log.d("hggh", stringBuilder.toString());
        startBrushStroke(this.mPointerId, this.mX, this.mY, this.mPressure);


        PointId mTemp = new PointId();
        mTemp.setPointID(mPointerId);
//      mTemp.setIndex(actionIndex);
        OverflowID.add(mTemp);
    }
    protected void startBrushStroke(int pointerId, float x, float y, float pressure) {
        initBrush(pointerId);
        this.mBrushes[pointerId].startStroke(x, y);
        synchronized (SHAPES_LOCK) {
            this.mLocalBrushShapes.add(this.mBrushes[pointerId]);
//          StringBuilder stringBuilder = new StringBuilder();
//          stringBuilder.append("   startBrushStroke: mLocalBrushShapes.size =   ");
//          stringBuilder.append(this.mLocalBrushShapes.size());
//          Log.d("mLocalBrushShapes", stringBuilder.toString());
        }
        this.mPrevXs[pointerId] = x;
        this.mPrevYs[pointerId] = y;


    }
    private void initBrush(int pointerId) {
        this.mBrushes[pointerId] = new PencilInk(this.mManager, this.mBrushColor, this.mBrushThickness, this.penType);
    }

    protected void touchMove(MotionEvent event) {
        Log.d("hong", "touchMove");
        if (enableKalmanFilter()) {
          //touchMove4Filter(event);
            handleTestHistoryMove(event);
        } else {
            touchMove4Normal(event);
        }
    }
    private void touchMove4Normal(MotionEvent event) {
        BaseUtils.dbg(TAG, "touchMove4Normal");
        int pointerCount = event.getPointerCount();
        if (pointerCount > 1) {
            this.mIsValidDrawing = false;
            return;
        }
        for (int i = 0; i < pointerCount; i++) {
            this.mX = event.getX(i);
            this.mY = event.getY(i);
            this.mPressure = event.getPressure(i);
            this.mPointerId = event.getPointerId(i);
            if (PanelUtils.isMultiPen() || this.mPointerId == 0) {
                PencilInk[] pencilInkArr = this.mBrushes;
                int i2 = this.mPointerId;
                if (!(pencilInkArr[i2] == null || pencilInkArr[i2].isFinished())) {
                    continueBrushStroke(this.mPointerId, this.mX, this.mY);
                }
            }
        }
    }

    private void touchMove4Filter(MotionEvent event) {
        if (event.getPointerCount() > 1) {
            this.mIsValidDrawing = false;
        } else {
            handleHistoryMove(event);
        }
    }
    /**
     * 处理移动过程中历史数据
     * move事件过滤，当移动位移超过固定阈值之后再进行绘制
     * @param event
     */

    private void handleTestHistoryMove(MotionEvent event){
        final int historySize = event.getHistorySize();
        final int pointerCount = event.getPointerCount();
//        BaseUtils.dbg(TAG, "historySize=" + historySize + " pointerCount=" + pointerCount);
        for (int p = 0; p < pointerCount; p++) {
            final int pointerId = event.getPointerId(p);
            if (!PanelUtils.isMultiPen()) { // 单指触控, 只处理pointerId为0的数据
                if (pointerId != 0) {
                    continue;
                }
            }
            PointId mTemp = checkPointID(pointerId,OverflowID);
            if (mTemp == null)
                continue;
            if(historySize == 0) {
                this.mX = event.getX(p);
                this.mY = event.getY(p);
                this.mPressure = event.getPressure(p);
                if (!(this.mBrushes[pointerId] == null || this.mBrushes[pointerId].isFinished()))
                {
                    filter(pointerId, this.mX, this.mY,this.mPressure);
                }
                continue;
            }
            if (!(this.mBrushes[pointerId] == null
                    || this.mBrushes[pointerId].isFinished())) {
                for (int h = 0; h < historySize; h++) {
                    float x = event.getHistoricalX(p, h);
                    float y = event.getHistoricalY(p, h);
                    float pressure = event.getHistoricalPressure(p, h);
                    filter(pointerId, x, y, pressure);
                }
            }
        }
    }
    private void handleHistoryMove(MotionEvent event) {
        int historySize = event.getHistorySize();
        int pointerCount = event.getPointerCount();
        for (int p = 0; p < pointerCount; p++) {
            int pointerId = event.getPointerId(p);
            PencilInk[] pencilInkArr;
            if (historySize == 0) {
                this.mX = event.getX(p);
                this.mY = event.getY(p);
                pencilInkArr = this.mBrushes;
                if (!(pencilInkArr[pointerId] == null || pencilInkArr[pointerId].isFinished())) {
                    filter(pointerId, this.mX, this.mY, 0.0f);
                }
            } else {
                pencilInkArr = this.mBrushes;
                if (!(pencilInkArr[pointerId] == null || pencilInkArr[pointerId].isFinished())) {
                    for (int h = 0; h < historySize; h++) {
                        filter(pointerId, event.getHistoricalX(p, h), event.getHistoricalY(p, h), 0.0f);
                    }
                }
            }
        }
    }
    private void filter(int pointerId, float x, float y, float pressure) {
        BaseUtils.dbg(TAG, "-----filter----");
//        float[] fArr = this.mPrevXs;
//        float diffX = x - fArr[pointerId];
//        float[] fArr2 = this.mPrevYs;
//        float diffY = y - fArr2[pointerId];
//        fArr[pointerId] = x;
//        fArr2[pointerId] = y;
//        float length = (float) Math.sqrt((double) ((diffX * diffX) + (diffY * diffY)));
        this.mPrevXs[pointerId] = x;
        this.mPrevYs[pointerId] = y;
        continueBrushStroke(pointerId, x, y);
    }
    protected Rect continueBrushStroke(int pointId, float x, float y) {
        Rect rect = this.mBrushes[pointId].continueStroke(x, y);
        this.mBrushes[pointId].drawCurrentPath(this.mManager);
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("left=");
//        stringBuilder.append(rect.left);
//        stringBuilder.append(" top=");
//        stringBuilder.append(rect.top);
//        stringBuilder.append(" right=");
//        stringBuilder.append(rect.right);
//        stringBuilder.append(" bottom=");
//        stringBuilder.append(rect.bottom);
//        BaseUtils.dbg(TAG, stringBuilder.toString());

        Point p = new Point(x, y, false);
        PointEntity pointEntity = new PointEntity(mBrushes[pointId], pointId, p, 1);
        mPointQueue.offer(pointEntity);
      //  this.mPointQueue.offer(new PointEntity(this.mBrushes[pointId], pointId, new Point(x, y, false), 1));
        return rect;
    }

    protected void touchUp(MotionEvent event) {
        Log.d("hong", "touchUp");
        int actionIndex = event.getActionIndex();
        this.mX = event.getX(actionIndex);
        this.mY = event.getY(actionIndex);
        this.mPointerId = event.getPointerId(actionIndex);
        if (this.mBrushes[this.mPointerId] != null && !this.mBrushes[this.mPointerId].isFinished())
        {
            endBrushStroke(this.mPointerId, this.mX, this.mY);
            if (BuildConfig.is_accelerate)
                WbJniCall.fbLock(0);
            requestRedraw();
        }
//        PencilInk[] pencilInkArr = this.mBrushes;
//        int i = this.mPointerId;
//        if (pencilInkArr[i] != null && !pencilInkArr[i].isFinished()) {
//            Log.d("hggh", "touchUp:  is_needDlgRefresh =false");
//            WbJniCall.fbLock(0);
//            requestRedraw();
//        }
    }
    protected void requestRedraw() {
        Log.i("gyx", "requestRedraw");
        Rect dirtyRect = new Rect();
        Rect temp = new Rect();
        synchronized (SHAPES_LOCK) {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("requestRedraw-------mLocalBrushShapes = ");
//            stringBuilder.append(this.mLocalBrushShapes.size());
//            Log.d("hggh", stringBuilder.toString());
            if (this.mLocalBrushShapes.isEmpty()) {
//            StringBuilder stringBuilder2 = new StringBuilder();
//            stringBuilder2.append("requestRedraw-------mLocalBrushShapes = ");
//            stringBuilder2.append(this.mLocalBrushShapes.size());
//            Log.d("hggh", stringBuilder2.toString());
              this.mManager.repaintToOffScreenCanvas(null, true);
              return;
            }
            this.mLocalBrushShapes.clear();
            this.mManager.repaintToOffScreenCanvas(dirtyRect, true);
        }
    }
    public void abortState() {
        synchronized (SHAPES_LOCK) {
           // this.mManager.removeMaterialList(this.mLocalBrushShapes);
            this.mLocalBrushShapes.clear();
        }
    }
}
