package com.mphotool.whiteboard.action;

import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;

import com.ctv.Jni.WbJniCall;
import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.activity.MainActivity;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.PencilInk;
import com.mphotool.whiteboard.elements.Point;
import com.mphotool.whiteboard.elements.PointEntity;
import com.mphotool.whiteboard.elements.PointId;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.JniPainterUtil;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.view.BaseThread;
import com.mphotool.whiteboard.view.PanelManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 画笔操作
 * @author wanghang
 */
public class PenAction extends Action {
    private static final String TAG = "PenAction";//PenAction.class.getSimpleName();
    /**
     * 同步锁
     */
    private static final Object SHAPES_LOCK = new Object();
    /**
     * 画笔颜色
     */
    protected int mBrushColor = -1;
    /**
     * 画笔宽度
     */
    protected float mBrushThickness = Constants.PEN_WIDTH_MIDDLE;
    /**
     * 产生的画笔集合
     */
    protected PencilInk[] mBrushes;
    /**
     * 是否有效绘制（触摸点超过10个，则认为是无效操作）
     */
    protected boolean mIsValidDrawing = false;

    /**
     * 单指（触摸点已经UP，则认为是无效操作）
     */
    protected boolean mIsSinglefinger = false;

    /**
     * 画笔模式 0,正常、1，毛笔
     */
    protected int penType = 0;
    /**
     * 备份？？？
     */
    protected List<Material> mLocalBrushShapes;

    /**溢出点集合**/
    private List<PointId> OverflowID ;

    /**
     * 当前的坐标值
     */
    protected float mX;
    protected float mY;


    protected int Refresh_count = 0;
    protected int mPointerId;
    /**
     * Tool type
     */
    //protected int mToolType;
    /**
     * 记录上一次触摸点的坐标
     */
    private float[] mPrevXs = new float[20];

    private float[] mPrevYs = new float[20];

    private boolean finish = false;

    private PanelManager mManager;

    public PenAction(PanelManager manager)
    {
        this.mManager = manager;
        this.mBrushes = new PencilInk[20];
        this.mLocalBrushShapes = new ArrayList();
        OverflowID = new ArrayList<PointId>();
    }

    private PenAction createAction(PanelManager manager, List<Material> list)
    {
        PenAction temp = new PenAction(manager);
        temp.materialList.addAll(list);
        return temp;
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        int action = event.getActionMasked();
        int count = event.getPointerCount();
//        BaseUtils.dbg(TAG, "action=" + action + " count=" + count);
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                this.mIsValidDrawing = true;
                this.mIsSinglefinger = false;
                if (BuildConfig.is_accelerate) {
                    Refresh_count = 0;
                    ScanHandler.removeMessages(SCAN_SURFACE_START);
                    ScanHandler.removeMessages(SCAN_SURFACE_END);
                    WbJniCall.fbLock(1);
                    mManager.repaintScreen(null);
                }
                touchDown(event);
                finish = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                int Index = event.getActionIndex();
                int mId = event.getPointerId(Index);
                BaseUtils.dbg(TAG, "ACTION_POINTER_DOWN mPointerId=" + mId);
                if (!PanelUtils.isMultiPen()) {
                    break;
                }
                if (this.mIsValidDrawing)
                {
                    touchPointerDown(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (this.mIsValidDrawing)
                {
                    touchMove(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                OverflowID.clear();
                if (this.mIsValidDrawing && !mIsSinglefinger)
                {
                    touchUp(event);
                }
                finish = true;
                this.mIsValidDrawing = false;
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
            default:
                break;
        }
        return true;
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

    protected void touchDown(MotionEvent event)
    {
        OverflowID.clear();
        int actionIndex = event.getActionIndex();
        int mToolType = event.getToolType(actionIndex);
        this.mX = event.getX(actionIndex);
        this.mY = event.getY(actionIndex);
        this.mPointerId = event.getPointerId(actionIndex);
   //     BaseUtils.dbg(TAG, "touchDown actionIndex=" + actionIndex);
        BaseUtils.dbg(TAG, " pointId=" + mPointerId + " mType=" + mToolType);
        PointId mTemp = new PointId();
        mTemp.setPointID(mPointerId);
//        mTemp.setIndex(actionIndex);
        OverflowID.add(mTemp);

        startBrushStroke(this.mPointerId, this.mX, this.mY, mToolType);
    }

    protected void touchPointerDown(MotionEvent event)
    {
        int actionIndex = event.getActionIndex();
        int pointerId = event.getPointerId(actionIndex);
        int mToolType = event.getToolType(actionIndex);
  //      BaseUtils.dbg(TAG, "touchPointerDown actionIndex=" + actionIndex);
        BaseUtils.dbg(TAG, " pointId=" + pointerId + " mType=" + mToolType);
//        BaseUtils.dbg(TAG, "AndroidRuntime actionIndex=" + actionIndex + " pointer=" + event.getPointerCount());

        // 判断是否支持多触控绘制
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
//            mtemp.setIndex(actionIndex);
            OverflowID.add(mtemp);
            startBrushStroke(pointerId, event.getX(actionIndex), event.getY(actionIndex), mToolType);
        }else{
            if(OverflowID.size() < Constants.MULTI_FINGER_NUMBER){
                PointId mTemp = checkPointID(pointerId,OverflowID);
                if(mTemp != null)
                    OverflowID.remove(mTemp);
                PointId mtemp = new PointId();
                mtemp.setPointID(pointerId);
//                mtemp.setIndex(actionIndex);
                OverflowID.add(mtemp);
                startBrushStroke(pointerId, event.getX(actionIndex), event.getY(actionIndex), mToolType);
            }
        }
    }

    protected void touchMove(MotionEvent event)
    {
        if (enableKalmanFilter())
        {
            handleTestHistoryMove(event);
        }
        else
        {
            touchMove4Normal(event);
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
                if (!(this.mBrushes[pointerId] == null || this.mBrushes[pointerId].isFinished()))
                {
                    filter(pointerId, this.mX, this.mY,0);
                }
                continue;
            }
            if (!(this.mBrushes[pointerId] == null
                    || this.mBrushes[pointerId].isFinished())) {
                for (int h = 0; h < historySize; h++) {
                    float x = event.getHistoricalX(p, h);
                    float y = event.getHistoricalY(p, h);
                    filter(pointerId, x, y, 0);
                }
            }
        }
    }
    /**
     * move事件正常记录，不计算位移量，直接绘制
     */
    private void touchMove4Normal(MotionEvent event)
    {
        BaseUtils.dbg(TAG, "touchMove4Normal");
        final int pointerCount = event.getPointerCount();
        if (pointerCount > 10)
        {
            this.mIsValidDrawing = false;
            return;
        }

        for (int i = 0; i < pointerCount; i++)
        {
            this.mX = event.getX(i);
            this.mY = event.getY(i);
            this.mPointerId = event.getPointerId(i);
            if (!PanelUtils.isMultiPen()){ // 单指触控, 只处理pointerId为0的数据
                if (mPointerId != 0){
                    continue;
                }
            }

            if (!(this.mBrushes[this.mPointerId] == null || this.mBrushes[this.mPointerId].isFinished()))
            {
                continueBrushStroke(this.mPointerId, this.mX, this.mY,0);
            }
        }
    }

    private void filter(int pointerId, float x, float y, int mType)
    {
        this.mPrevXs[pointerId] = x;
        this.mPrevYs[pointerId] = y;
        continueBrushStroke(pointerId, x, y,mType);
    }

    protected void touchPointerUp(MotionEvent event)
    {
	    final int actionIndex = event.getActionIndex();
	    final int pointerId = event.getPointerId(actionIndex);
    //    BaseUtils.dbg(TAG, "pointerId=" + pointerId + " ,actionIndex=" + actionIndex);
        // 判断是否支持多触控绘制
        if (!PanelUtils.isMultiPen()){ // 单指触控
            if (pointerId != 0){
                return;
            }

	        // 抬起处理
	        finish = true;
	        this.mIsValidDrawing = false;
        }

	    final float x = event.getX(actionIndex);
	    final float y = event.getY(actionIndex);
      //  BaseUtils.dbg(TAG, "touchPointerUp mPointerId=" + mPointerId);
     //   BaseUtils.dbg(TAG, "touchPointerUp actionIndex=" + actionIndex + " mPointerId=" + mPointerId);
      //  BaseUtils.dbg(TAG, "touchPointerUp mToolType=" + mToolType);
	    if (this.mBrushes[pointerId] != null && !this.mBrushes[pointerId].isFinished())
	    {
            endBrushStroke(pointerId, x, y,0);
	    }
    }

    protected void touchUp(MotionEvent event)
    {
        int actionIndex = event.getActionIndex();
        this.mX = event.getX(actionIndex);
        this.mY = event.getY(actionIndex);
        this.mPointerId = event.getPointerId(actionIndex);
      //  BaseUtils.dbg(TAG, "touchUp mPointerId=" + mPointerId);
        if (this.mBrushes[this.mPointerId] != null && !this.mBrushes[this.mPointerId].isFinished())
        {
            endBrushStroke(this.mPointerId, this.mX, this.mY,0);
            if (BuildConfig.is_accelerate)
                WbJniCall.fbLock(0);
            requestRedraw();
        }
    }

    protected void startBrushStroke(int pointerId, float x, float y, int mTType)
    {
        initBrush(pointerId,mTType);
        mBrushes[pointerId].startStroke(x, y);

        Point p = new Point(x, y, false);
        PointEntity pointEntity = new PointEntity(mBrushes[pointerId], pointerId, p, 0);
        mPointQueue.offer(pointEntity);

        synchronized (SHAPES_LOCK)
        {
            this.mLocalBrushShapes.add(this.mBrushes[pointerId]);
        }
        this.mPrevXs[pointerId] = x;
        this.mPrevYs[pointerId] = y;
    }

    protected Rect continueBrushStroke(int pointId, float x, float y,int mType)
    {
        Rect rect = this.mBrushes[pointId].continueStroke(x, y);

        this.mBrushes[pointId].drawCurrentPath(mManager);
//        BaseUtils.dbg(TAG, "left=" + rect.left + " top=" + rect.top + " right=" + rect.right + " bottom=" + rect.bottom);
        Point p = new Point(x, y, false);
        PointEntity pointEntity = new PointEntity(mBrushes[pointId], pointId, p, 1);
        mPointQueue.offer(pointEntity);
        return rect;
    }

    protected Rect endBrushStroke(int pointerId, float x, float y,int mType)
    {
       // BaseUtils.dbg(TAG, "endBrushStroke pointerId=" + pointerId );
        Rect rect = this.mBrushes[pointerId].endStroke(x, y);
//        this.mBrushes[pointerId].drawCurrentPath(mManager);

        Point p = new Point(x, y, false);
        PointEntity pointEntity = new PointEntity(mBrushes[pointerId], pointerId, p, 2);
        mPointQueue.offer(pointEntity);

        return rect;
    }

    /**
     * 绘制
     */
    protected void requestRedraw()
    {
        Rect dirtyRect = new Rect();
        synchronized (SHAPES_LOCK)
        {
            if (this.mLocalBrushShapes.isEmpty())
            {
                mManager.repaintToOffScreenCanvas(null, true);
                return;
            }

            Iterator i$ = this.mLocalBrushShapes.iterator();

            while (i$.hasNext())
            {
                Rect m = ((PencilInk) i$.next()).getBounds();
                dirtyRect.union(m);
            }
//            BaseUtils.dbg(TAG, "left=" + dirtyRect.left + " top=" + dirtyRect.top + " right=" + dirtyRect.right + " bottom=" + dirtyRect.bottom);
            i$ = this.mLocalBrushShapes.iterator();
            while (i$.hasNext())
            {
                mManager.addMaterial((Material) i$.next());
            }
            PenAction act = createAction(mManager, mLocalBrushShapes);
            mManager.addAction(act);
            mLocalBrushShapes.clear();
            if (BuildConfig.is_accelerate)
            {
                PanelManager.mIsNeedSleep = true;
                int penWidth = (int)mBrushThickness + 10;
//                Log.v(TAG,"jean penWidth=" +  penWidth);
                Rect tempRect = PanelUtils.rectAddWidth(dirtyRect, penWidth);
                mManager.repaintScreen(tempRect);
                ScanHandler.sendEmptyMessageDelayed(SCAN_SURFACE_START,50);
            }
        }
    }

    private void initBrush(int pointerId,int mTType)
    {
        mBrushes[pointerId] = new PencilInk(mManager, this.mBrushColor, this.mBrushThickness,penType,mTType);
    }


    public void abortState()
    {
        synchronized (SHAPES_LOCK)
        {
            mManager.removeMaterialList(mLocalBrushShapes);
            this.mLocalBrushShapes.clear();
            if (BuildConfig.is_accelerate)
                WbJniCall.fbLock(0);
        }
    }

    public int getCurrentBrushColor()
    {
        return this.mBrushColor;
    }

    public void setCurrentBrushColor(int color)
    {
        this.mBrushColor = color;
    }

    public float getCurrentBrushThickness()
    {
        return this.mBrushThickness;
    }

    public void setCurrentBrushThickness(float thickness)
    {
        this.mBrushThickness = thickness;
    }

    public void setPenMode(int mType){
        this.penType = mType;
    }

    protected boolean enableKalmanFilter()
    {
        return true;
    }

    @Override public void linkMaterial(List<Material> list)
    {

    }

    @Override public boolean onTouch(PanelManager panelManager, MotionEvent motionEvent)
    {
        return false;
    }

    @Override public void redo(PanelManager panelManager)
    {
        super.redo(panelManager);
        panelManager.getMaterials().addAll(materialList);
    }

    @Override public void undo(PanelManager panelManager)
    {
        super.undo(panelManager);
        panelManager.getMaterials().removeAll(materialList);
    }

    public void startPointThread()
    {
        if (this.mPointThread == null || !this.mPointThread.isAlive())
        {
            this.mPointThread = new InsertPointThread();
            this.mPointThread.start();
        }
    }

    public void stopPointThread()
    {
        if (this.mPointThread != null)
        {
            this.mPointThread.stopThread();
            this.mPointThread = null;
        }
    }

    InsertPointThread mPointThread;
    protected BlockingQueue<PointEntity> mPointQueue = new ArrayBlockingQueue<PointEntity>(100);

    class InsertPointThread extends BaseThread {

        public InsertPointThread()
        {

        }

        @Override public void run()
        {
            super.run();
            PointEntity pointEntity = null;
//            BaseUtils.dbg(TAG, "InsertPointThread mIsRunning=" + mIsRunning);
            while (mIsRunning)
            {
                try
                {
                    pointEntity = mPointQueue.take();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if (pointEntity != null)
                {
                    insertPoint(pointEntity);
                }

                if(finish && mPointQueue.size() == 0){
                    mManager.actionStatus = PanelManager.S_NONE;
                }else{
                    mManager.actionStatus = PanelManager.S_DURING_INSERT_POINT;
                }
            }
        }

        private void insertPoint(PointEntity pointEntity)
        {
            PencilInk pencil = pointEntity.pencil;
            Point p = pointEntity.point;
//            BaseUtils.dbg(TAG, "pointEntity.pointType=" + pointEntity.pointType);
            if (pointEntity.pointType == 0)
            {
                pencil.startOnPoint(p);
            }
            else if (pointEntity.pointType == 1)
            {
                pencil.continueOnPoint(p);
            }
            else if (pointEntity.pointType == 2)
            {
                pencil.endOnPoint(p);
            }
        }
    }

    private final static int SCAN_SURFACE_START = 0x01;
    private final static int SCAN_SURFACE_END = 0x02;
    private final static int SCAN_SURFACE_SYNC = 0x03;

    private Handler ScanHandler = new Handler() {
        public void handleMessage(Message msg) {
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
                case SCAN_SURFACE_SYNC:
                    WbJniCall.fbLock(0);
                    mManager.repaintScreen(null);
                    break;
            }
        }
    };
}
