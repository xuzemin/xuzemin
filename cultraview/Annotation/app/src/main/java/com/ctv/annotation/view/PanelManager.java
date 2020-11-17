package com.ctv.annotation.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.SystemProperties;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import com.ctv.Jni.WbJniCall;
import com.ctv.annotation.R;
import com.ctv.annotation.WhiteBoardApplication;

import com.ctv.annotation.action.EraseMaterialAction;
import com.ctv.annotation.action.FrameBufferEraserState;
import com.ctv.annotation.action.PenAction;
import com.ctv.annotation.action.RubberRaftModule;
import com.ctv.annotation.action.SelectAction;
import com.ctv.annotation.element.Image;
import com.ctv.annotation.element.Material;
import com.ctv.annotation.element.Selector;
import com.ctv.annotation.entity.Storable;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.utils.StatusEnum;
import com.ctv.annotation.view.imagedit.ImageEditView;
import com.itextpdf.xmp.impl.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

public class PanelManager {
    static String TAG = "PanelManager";
    public static final byte[] NOTE_NCC_FLAG = new byte[]{(byte) -52, (byte) 110, (byte) 111, (byte) 116, (byte) 101};
    private int mPenColor;
    public float MAX_SCALE_SIZE = 3.0f;
    public float MIN_SCALE_SIZE = 0.3f;
    private int mBackgroundColor = 0;
    private int mCellColor = 0;
    private Context mContext = null;
    private boolean mDrawCellBackground = true;
    private int mStyle = 1;
    private Canvas mBgCanvas,mCacheCanvas,fbCanvas0,fbCanvas1,fbCanvas2,mRCanvas;

    private EraseMaterialAction mEraseMaterialAction;
    private SurfaceHolder mHolder = null;
    private Bitmap mOldBitmap;
    private OnActionChangedListener mOnActionChangedListener = null;
    private OnEventListener mOnEventListener = null;
    private OnModeChangedListener mOnModeChangedListener = null;
    private OnPageChangedListener mOnPageChangedListener = null;
    private OnScaleChangedListener mOnScaleChangedListener = null;
    private OnTouchListener mOnTouchListener = null;
    private Paint mPaint = new Paint();
    private boolean mReady = false;
    private SelectAction mSelectAction;
    private Selector mSelector = null;
    private boolean mSwitchPageInc = true;
    private Page mCurrentPage;

    private Bitmap mBgBitmap;

    private Bitmap mViewBitmap;
    private int mWidth = 0;
    private int mHeight = 0;

    Canvas surfaceCanvas = null;
    boolean hasLocked = false;

    protected ImageEditView mImageEditView;//图片剪裁控件
    private ArrayList<Page> mPages = new ArrayList();
    private int mCurrentPageIndex = 0;
    private Page mTmpPage = null;
    private int mTmpPageIndex = 0;
    //    private BaseBrushState mBaseBrushState;
    private PenAction mBaseBrushState;
    private FrameBufferEraserState mEraseAction = null;
    private boolean mIsGestureRoam = false; // 判断是否为缩放模式


    public boolean isLocked = false;
    private int actionMode = 0; //操作类型，决定touch事件的最终操作类型
    private int status = StatusEnum.STATUS_NC;
    float penWidth = Constants.PEN_WIDTH_MIDDLE;

    private boolean mHasRepainted = true;
    public static boolean mIsNeedSleep = false;
    private static Bitmap sViewBitmap;

    private static final HashMap<String, String> sBitmapReferenceMap = new HashMap();
    private static final HashMap<String, Integer> sTextureReferenceMap = new HashMap();
    private RubberRaftModule RubberAction;

    public static HashMap<String, String> getBitmapReferenceMap()
    {
        return sBitmapReferenceMap;
    }

    public static HashMap<String, Integer> getTextureReferenceMap()
    {
        return sTextureReferenceMap;
    }

    protected BaseGLSurfaceView mGLSurfaceView;

    public static int S_NONE = 0;
    public static int S_DURING_INSERT_POINT = 0x100;
    public int actionStatus = 0;  //当前状态：画笔补点处理耗时，缩放耗时，初始化耗时等


    /**
     * 将surface刷新放进队列和线程进行
     */
    protected BlockingQueue<Rect> mDrawingQueue = new ArrayBlockingQueue<Rect>(60);
    protected static final Object DRAWING_LOCKER = new Object();
    protected DrawingThread mDrawingThread;
    public Rect mWorkingRect;

    public boolean isCroping()
    {
        return mImageEditView != null && mImageEditView.isShown();
    }

    public boolean onTouch(MotionEvent event) {
        final int action = event.getActionMasked();

        switch (action){
            case MotionEvent.ACTION_DOWN:
                onGestureEraserDown(event);
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                return true;
//            case MotionEvent.ACTION_POINTER_UP:
//                break;
//            case MotionEvent.ACTION_MOVE:
                break;
            default:
                break;

        }

        switch (actionMode){
            case 0:
                if(status == 2){
                    break;
                }else {
                    Log.d("hggh", "onTouch: actionMode = "+actionMode);
                    //默认情况下，即为普通划线操作，do nothing
                    status=StatusEnum.STATUS_PAINT;
                }

                break;
            case 1:
                //从外部菜单栏选择了橡皮擦消除模式
                status = StatusEnum.STATUS_ERASE_BY_SIZE;
                break;
            case 2:
                //从外部菜单栏选择了消除选中模式
                status = StatusEnum.STATUS_ERASE_BY_SELECTION;
                break;
            case 3:
                //从外部菜单栏选择了选中模式
                status = StatusEnum.STATUS_SELECTION;
                break;

        }
        if (mEraseAction != null && !mEraseAction.isFinish())
        {
            Log.d("hggh", "onTouch: mEraseAction");
            /**擦除事件进行中*/
            status = StatusEnum.STATUS_ERASE_BY_SIZE;
        }

        BaseUtils.dbg("hggh", "status   =" + status);
        switch (status){
            case StatusEnum.STATUS_NC:
                mBaseBrushState.onTouchEvent(event);
                if (action == MotionEvent.ACTION_MOVE)
                {
                    float spanx = event.getX() ;
                    float spany = event.getY() ;
                    /**位移大于5，则进入画笔模式*/
                    if (Math.abs(Math.sqrt((double) ((spanx * spanx) + (spany * spany)))) > 5.0d)
                    {
                        double abs = Math.abs(Math.sqrt((double) ((spanx * spanx) + (spany * spany))));
                        Log.d("hggh", "onTouch:  abs  ="+abs);
                        Observable.timer(new Long((long) 50), TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                            public void call(Long aLong)
                            {
                                status = StatusEnum.STATUS_PAINT;
                            }
                        });
                    }
                }
                break;
            case StatusEnum.STATUS_PAINT: // 画笔
                BaseUtils.dbg("hggh","onTouch: paint");

                mBaseBrushState.onTouchEvent(event);
                break;
            case StatusEnum.STATUS_ERASE_BY_SIZE:
                /**创建块消除事件*/
                BaseUtils.dbg("hggh","onTouch: erase11111");
                if (RubberAction.isFinish()) {
                    BaseUtils.dbg("hggh","kakakakakakak------------onTouch: finsh ="+RubberAction.isFinish());
                    RubberAction.InitRubbrPolygon();
                }
                BaseUtils.dbg("hggh","onTouch: ontuch");

                RubberAction.onTouchEvent(event);

                break;

            default:
                break;

        }
        return true;
    }
    /**
     * 直接重绘指定区域
     */
    public void repaintScreen(Rect rect)
    {
        BaseUtils.dbg("gyx","repaintScreen");

        if (mHolder != null)
        {
            synchronized (mHolder)
            {
                Canvas canvas = mHolder.lockCanvas(rect);
                if (canvas != null && mHolder.getSurface().isValid())
                {
                    if (rect == null)
                    {
                        BaseUtils.dbg("gyx","repaintScreen rect == null");

                        canvas.drawBitmap(mViewBitmap, 0.0f, 0.0f, mPaint);
                    }
                    else
                    {

                        BaseUtils.dbg("gyx","repaintScreen rect != null");

                        /**重绘指定区域（局部刷新）*/
                        canvas.drawBitmap(mViewBitmap, rect, rect, mPaint);
                        canvas.drawColor(0);
                    }
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void onGestureEraserDown(MotionEvent event)
    {

        int count = event.getPointerCount();
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        float area = event.getTouchMajor(index);
        float area1 = event.getSize(index);
        //  BaseUtils.dbg(TAG, "onGestureEraserDown -- area = " + area + " area1=" + area1);
        //  BaseUtils.dbg(TAG, "onGestureEraserDown -- count = " + count + " index=" + index);
        if  (!WhiteBoardApplication.isAutoEnterEraseMode)
        {
            return;
        }
        //  新增
        if  (status != StatusEnum.STATUS_ERASE_BY_SELECTION && actionMode != 1) {
            computeEraserSize(area1 * Constants.ERASE_VALUE_DEFAULT);
        }   else {
            if (Constants.SYSTEM_CLIENT_CONFIG_NAME != null && (Constants.SYSTEM_CLIENT_CONFIG_NAME.equals("LT") ||
                Constants.SYSTEM_CLIENT_CONFIG_NAME.equals("EDU")))
                Constants.erase_value = Constants.NO_ERASE_VALUE_DEFAULT;
            else
                Constants.erase_value = Constants.ERASE_VALUE_DEFAULT;
        }

    }
    private void computeEraserSize(float pWidth)
    {
        //anIn为调节触发橡皮的灵敏度
        int anInt = SystemProperties.getInt("persist.sys.wb.sensitivity", 7);
        if (pWidth - anInt > 0) {

            Constants.erase_value = (int) pWidth + Constants.ERASE_VALUE_DEFAULT;
            if(Constants.erase_value > 80)
               Constants.erase_value = 80;
            BaseUtils.dbg("hggh","computeEraserSize: erase_value ="+ Constants.erase_value);

            mBaseBrushState.abortState();
    //      mWriteAction.abortState();
            status = StatusEnum.STATUS_ERASE_BY_SIZE;
            BaseUtils.dbg("hggh","computeEraserSize:  status = StatusEnum.STATUS_ERASE_BY_SIZE;");


        }
    }

    public void requestOpenGLRender(boolean isDrawShape)
    {
        if (mGLSurfaceView != null)
        {
            this.mGLSurfaceView.setDrawShape(isDrawShape);
            this.mGLSurfaceView.requestRender();
        }
    }

    public void showOpenGL(){
        mGLSurfaceView.setZOrderOnTop(true);
        mGLSurfaceView.setZOrderMediaOverlay(true);
        //android 8.0  需要重新画下mGLSurfaceView以便显示在上层
        if(android.os.Build.VERSION.SDK_INT>=26){
            mGLSurfaceView.setVisibility(View.INVISIBLE);
            mGLSurfaceView.setVisibility(View.VISIBLE);
        }
    }
    public void drawBackgroundOnly()
    {
        mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
        repaintToSurfaceInQueue();
    }
    public boolean hasSelected()
    {
        boolean isSelected = mSelectAction != null && mSelectAction.hasSelected();
        boolean isCroping = mImageEditView != null && mImageEditView.isShown();
        return isSelected || isCroping;
    }
    public boolean setStatus(int m) {
        BaseUtils.dbg(Constants.TAG_TOUCH, "----- PanelManager setStatus = " + m + " status=" + status);
        if (m == status)
        {
            return false;
        }
        if (status == 7 && mEraseMaterialAction != null)
        {
          //  removeMaterial(mEraseMaterialAction.getErase());
        }
        status = m;
        return true;
    }
    public Bitmap getCurrentViewBitmap()
    {
        return mViewBitmap;
    }

    public Context getContext()
    {
        return mContext;
    }

    public Canvas getCacheCanvas()
    {
        return mCacheCanvas;
    }

    public Selector getSelector()
    {
        return mSelector;
    }

    public SelectAction getSelectAction()
    {
        return mSelectAction;
    }

    public void setPenColor(int color)
    {
        mPenColor = color;
        mBaseBrushState.setCurrentBrushColor(color);
    }

    public void removeMaterial(Material material)
    {
//        synchronized (mCurrentPage.SHAPE_LOCK)
//        {
//            getMaterials().remove(material);
//            if (material instanceof Image && getCurrentPage().mImageCount > 0)
//            {
//                getCurrentPage().mImageCount--;
//            }
//        }
    }
    public void drawScreen(Rect rect)
    {
        if (rect == null)
        {
            repaintToSurfaceInQueue();
        }
        else
        {
            repaintToSurfaceInQueue(rect);
        }


    }

    public boolean setMode(int m)
    {
        return setMode(m, true, false);
    }

    private boolean setMode(int mode, boolean trigger, boolean event)
    {
        if (mode < 0 || mode >= 4)
        {
            Log.d("modehong", "setMode: 0000");
            return false;
        }
        if (actionMode == 3 && mode != 3)
        {
            Log.d("modehong", "setMode: 111");
          //  unselectAllAndMaterial();
            repaintToOffScreenCanvas(null, true);
        }
        actionMode = mode;
        if (trigger)
        {
            //  Log.d("hggh", "setMode: Mode  ="+mode);
            handleEventListener(14, null, Integer.valueOf(actionMode));
        }
        if (event && mOnModeChangedListener != null)
        {
            //     Log.d("hggh", "setMode: Mode  ="+mode);
            mOnModeChangedListener.onModeChanged(actionMode);
        }
        return true;
    }

    public void handleEventListener(int action, ArrayList<? extends Storable> materials, Object object)
    {
        Log.d("modehong", "handleEventListener: null");
        if (mOnEventListener != null)
        {
            Log.d("modehong", "handleEventListener: not null");
            mOnEventListener.onEvent(action, materials, object);
        }
    }

    public void setPenWidth(float width) {
        penWidth = width;
        mBaseBrushState.setCurrentBrushThickness(penWidth * getScaleRatio());
    }
    public float getScaleRatio()
    {
        return mCurrentPage.getScaleRatio();
    }

    /**
     * 引入线程刷新以及同步锁控制
     * 在刷新过程中，如果上次绘制未完成，则本次需要刷新的rect会判断是否有未执行刷新的rect，两次或多次汇总为一次刷新
     */
    private class DrawingThread extends BaseThread {
        private DrawingThread()
        {
        }

        public void run()
        {
            super.run();
            Rect dirtyRect = new Rect();
            while (this.mIsRunning)
            {
                Rect rect = null;
                try
                {
                    rect = (Rect) mDrawingQueue.take();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if (rect != null)
                {
                    dirtyRect.set(rect);
                    int queueSize = mDrawingQueue.size();
                    if (queueSize > 0)
                    {
                        for (int i = 0; i < queueSize; i++)
                        {
                            try
                            {
                                rect = (Rect) mDrawingQueue.take();
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            if (rect != null)
                            {
                                dirtyRect.union(rect);
                            }
                        }
                    }
                    redrawDirtyArea(dirtyRect);
                }
                else
                {
                    return;
                }
            }
        }
        private void redrawDirtyArea(Rect rect) {
            if (mViewBitmap != null && !mViewBitmap.isRecycled() && rect != null) {
                long start = System.currentTimeMillis();
                synchronized (DRAWING_LOCKER) {
                    Canvas canvas = mHolder.lockCanvas(rect);
//                    BaseUtils.dbg(TAG, " start=" + start);
                    if (canvas == null) {
                        mDrawingQueue.offer(mWorkingRect);
                        return;
                    }
                    canvas.drawBitmap(mViewBitmap, rect, rect, mPaint);
                    if (mIsNeedSleep) {
                        long end = System.currentTimeMillis();
                        try {
                            if (end - start < 0) {
                                Thread.sleep(30 - (end - start));
                            }
                        } catch (Exception e) {
                        }
                    }
                    mHolder.unlockCanvasAndPost(canvas);
                    mIsNeedSleep = false;
                }
            }
        }
        //        private void redrawDirtyArea(Rect rect)
//        {
//            if (mViewBitmap != null && !mViewBitmap.isRecycled() && rect != null)
//            {
//                Canvas canvas = mHolder.lockCanvas(rect);
//
//                if (canvas == null)
//                {
//                 mDrawingQueue.offer(mWorkingRect);
//                    return;
//                }
//                synchronized (DRAWING_LOCKER)
//                {
//                 canvas.drawBitmap(mViewBitmap, rect, rect, mPaint);
//                 mHolder.unlockCanvasAndPost(canvas);
//                }
//
//            }
//        }

    }



    public void drawScreenDelay(long time)
    {
        Observable.timer(time, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
            public void call(Long aLong)
            {
                repaintToOffScreenCanvas(null, true);
                repaintToSurfaceInQueue();
//                drawScreen();
            }
        });
    }
    public void drawBetweenRect(Canvas canvas, Rect r1, Rect r2)
    {
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        BaseUtils.dbg("hong","drawBetweenRect");

        if (r1.left > r2.left)
        {
            exchangeRect(r1, r2);
        }

        if (r1.left == r2.left)
        {
            if (r1.top > r2.top)
            {
                exchangeRect(r1, r2);
            }
            canvas.drawRect(new Rect(r1.left, r1.top, r1.right, r2.bottom), paint);
        }
        else if (r1.top == r2.top)
        {
            canvas.drawRect(new Rect(r1.left, r1.top, r2.right, r2.bottom), paint);
        }
        else if (r1.top > r2.top)
        {
            Path path = new Path();
            path.moveTo((float) r1.left, (float) r1.bottom);
            path.lineTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r2.left, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r1.right, (float) r1.bottom);
            path.close();
            canvas.drawPath(path, paint);
        }
        else
        {
            Path path = new Path();
            path.moveTo((float) r1.left, (float) r1.bottom);
            path.lineTo((float) r1.left, (float) r1.top);
            path.lineTo((float) r1.right, (float) r1.top);
            path.lineTo((float) r2.right, (float) r2.top);
            path.lineTo((float) r2.right, (float) r2.bottom);
            path.lineTo((float) r2.left, (float) r2.bottom);
            path.close();
            canvas.drawPath(path, paint);
        }

        Rect totalRect = new Rect(r1);
        totalRect.union(r2);
        Paint bgPaint = new Paint();
        bgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

        //  canvas.drawColor(Color.TRANSPARENT,Mode.CLEAR);
        canvas.drawBitmap(mBgBitmap, totalRect, totalRect, bgPaint);

    }
    public static void exchangeRect(Rect r1, Rect r2)
    {

        Rect r = new Rect(r2);
        r2.set(r1);
        r1.set(r);
    }




    public PanelManager(Context context) {
        mContext = context;

        mPages.add(new Page(this));
        mCurrentPage = mPages.get(mCurrentPageIndex);
        mSelector = new Selector(this);
        mSelectAction = new SelectAction(this);

        mBaseBrushState = new PenAction(this);
        mBaseBrushState.startPointThread();
        RubberAction = new RubberRaftModule(this);
       // RubberAction.startRubberThread();
        RubberAction.RubberQueueIsOk = true;
        FrameBufferEraserState.mEraserWidth = context.getResources().getDimension(R.dimen.eraser_width);
        FrameBufferEraserState.mEraserHeight = context.getResources().getDimension(R.dimen.eraser_width);

    }
    public Canvas getfbCanvas0() {
        return fbCanvas0;
    }

    public Canvas getfbCanvas1() {
        return fbCanvas1;
    }

    public Canvas getfbCanvas2() {
        return fbCanvas2;
    }
    public boolean isReady() {
        return mReady;
    }
    public void setReady(boolean isReady)
    {
        mReady = isReady;
    }
    public interface OnActionChangedListener {
        void onActionChanged();
    }
    public interface OnEventListener {
        void onEvent(int i, ArrayList<? extends Storable> arrayList, Object obj);
    }

    public interface OnModeChangedListener {
        void onModeChanged(int i);
    }

    public interface OnPageChangedListener {
        void onPageChanged(int i);
    }

    public interface OnScaleChangedListener {
        void onScaleChangedListener(float f);
    }
    public interface OnTouchListener {
        void onTouchListener(MotionEvent motionEvent);
    }


    public void moveImage2Top(Image img)
    {
        removeMaterial(img);
      //  getCurrentPage().mImageCount++;
    //    getMaterials().add(getCurrentPage().mImageCount - 1, img);
    }

    public void init(SurfaceHolder holder, int width, int height) {

        mWorkingRect = new Rect(0, 0, width, height);
        startDrawingThread();
        mReady = true;


        mBgCanvas = new Canvas();
        mBgBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mBgCanvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mBgCanvas.setBitmap(mBgBitmap);

        mHolder = holder;
        mCacheCanvas = new Canvas();
        mCacheCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        //   mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
        mCacheCanvas.drawPaint(mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        mWidth = width;
        mHeight = height;

        mViewBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        mCacheCanvas.setBitmap(mViewBitmap);
        Canvas canvas = holder.lockCanvas();
        if (canvas != null)
        {
            canvas.setBitmap(mViewBitmap);
            holder.unlockCanvasAndPost(canvas);
        }

        if(Constants.IS_AH_EDU_QD){
            mPenColor = Constants.PEN_COLOLR_AH_EDU_QD;
            mBaseBrushState.setCurrentBrushColor(mPenColor);
        }else{
            mPenColor = Constants.PEN_COLOLR_DEFAULT;
            mBaseBrushState.setCurrentBrushColor(mPenColor);
        }

        mBaseBrushState.setCurrentBrushThickness(penWidth);



        UpdateBoardBackground();
        fbCanvas0 = new Canvas();
        fbCanvas1 = new Canvas();
        fbCanvas2 = new Canvas();
        WbJniCall.fbStart(1);
        WbJniCall.fbSetCanvas(fbCanvas0,fbCanvas1,fbCanvas2);

    }
    public int UpdateBoardBackground()
    {
        if (mBgCanvas != null)
        {
            int mColor = Color.rgb(205 ,201,201);
            int toColor = BaseUtils.getComparisonColor(mColor);
            mCellColor = toColor;
            BaseUtils.dbg("gyx","UpdateBoardBackground");

            mBgCanvas.drawColor(Color.TRANSPARENT);

            mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
            repaintToSurfaceInQueue();
        }
        return 0;
    }

    private void startDrawingThread()
    {
        if (this.mDrawingThread == null || !this.mDrawingThread.isAlive())
        {
            this.mDrawingThread = new DrawingThread();
            this.mDrawingThread.start();
        }
    }
    public void drawScreenDelay(Long time) {
        Observable.timer(time, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
            public void call(Long aLong)
            {
                repaintToOffScreenCanvas(null, true);
                repaintToSurfaceInQueue();
            }
        });
    }
    public void repaintToOffScreenCanvas(Rect dirtyRect, boolean directly)
    {
        BaseUtils.dbg("gyx","--------------repaintToOffScreenCanvas-=--------");


        if (mViewBitmap != null && !mViewBitmap.isRecycled())
        {
            if (dirtyRect != null)
            {
                BaseUtils.dbg("gyx","Rect  !=null");

                if (dirtyRect.left < dirtyRect.right && dirtyRect.top < dirtyRect.bottom)
                {
                    mCacheCanvas.save();
                    mCacheCanvas.clipRect(dirtyRect);
                }
                else if (directly)
                {
                    repaintToSurfaceInQueue();
                    return;
                }
                else
                {
                    return;
                }
            }


            synchronized (DRAWING_LOCKER) {
                if (dirtyRect != null)
                {
                    BaseUtils.dbg("gyx","repaintToOffScreenCanvas ----dirtyRect != null");

                    mCacheCanvas.drawBitmap(mBgBitmap, dirtyRect, dirtyRect, null);
                }
                else
                {
                    BaseUtils.dbg("gyx","repaintToOffScreenCanvas ----dirtyRect == null");

                    mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
                }

            }
            if (dirtyRect != null)
            {
                mCacheCanvas.restore();
            }
            if (directly)
            {
                BaseUtils.dbg(TAG, "material 3333 ");
                repaintToSurfaceInQueue();
            }
//            this.mHasRepainted = true;
        }
    }


    public void repaintToSurfaceInQueue()
    {
        BaseUtils.dbg("gyx","------repaintToSurfaceInQueue---- ");

        this.mDrawingQueue.offer(this.mWorkingRect);
    }

    public void repaintToSurfaceInQueue(Rect rect)
    {
        BaseUtils.dbg("gyx","epaintToSurfaceInQueue--rect ");


        this.mDrawingQueue.offer(rect);
    }

    /**
     * 清空指定区域(让橡皮擦消失)
     */
    public void cleanTargetRect(Rect rect)
    {

        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(mBackgroundColor);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        BaseUtils.dbg("hhhh","cleanTargetRect: drawrect");
        mCacheCanvas.drawRect(rect, paint);

    }
    public void destory() {
//        if(BuildConfig.is_accelerate){
//            //  JniPainterUtil.nativeUnSetBackgroundBitmap(mViewBitmap);
//        }
        if (mSelectAction != null)
        {
            mSelectAction.release();
        }
        if (mBgBitmap != null)
        {
            mBgCanvas = null;
            mBgBitmap.recycle();
            mBgBitmap = null;
        }
        if (mViewBitmap != null)
        {
            mCacheCanvas = null;
            mViewBitmap.recycle();
            mViewBitmap = null;
        }

        mDrawingQueue.clear();
        Iterator it = mPages.iterator();
        while (it.hasNext())
        {
            ((Page) it.next()).release();
        }
        mPages.clear();
        mPages = null;
        RubberAction.stopRubberThread();
        mBaseBrushState.stopPointThread();
        stopDrawingThread();
      //  OperationManager.getInstance(this).release();
    }
    private void stopDrawingThread()
    {
        if (this.mDrawingThread != null)
        {
            this.mDrawingThread.stopThread();
            this.mDrawingThread = null;
        }
    }
}
