package com.mphotool.whiteboard.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.ctv.Jni.WbJniCall;
import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.action.Action;
import com.mphotool.whiteboard.action.BaseSelectorState;
import com.mphotool.whiteboard.action.CleanAction;
import com.mphotool.whiteboard.action.DeleteAction;
import com.mphotool.whiteboard.action.EraseMaterialAction;
import com.mphotool.whiteboard.action.EraseMaterialAction.EventData;
import com.mphotool.whiteboard.action.InsertImageAction;
import com.mphotool.whiteboard.action.PageSwitchAction;
import com.mphotool.whiteboard.action.PasteAction;
import com.mphotool.whiteboard.action.PenAction;
import com.mphotool.whiteboard.action.ReplaceImageAction;
import com.mphotool.whiteboard.action.RubberRaftModule;
import com.mphotool.whiteboard.action.SelectAction;
import com.mphotool.whiteboard.action.ShapeAction;
import com.mphotool.whiteboard.activity.MainActivity;
import com.mphotool.whiteboard.activity.WhiteBoardApplication;
import com.mphotool.whiteboard.elements.EraseMaterial;
import com.mphotool.whiteboard.elements.Image;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.PencilInk;
import com.mphotool.whiteboard.elements.Selector;
import com.mphotool.whiteboard.entity.Storable;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.utils.SharedPreferencesUtils;
import com.mphotool.whiteboard.utils.StatusEnum;
import com.mphotool.whiteboard.utils.ToofifiLog;
import com.mphotool.whiteboard.utils.Utils;
import com.mphotool.whiteboard.view.imgedit.ImageEditView;
import com.mphotool.whiteboard.view.imgedit.OnImageEditListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;

public class PanelManager {
    static String TAG = "PanelManager";
    public static final byte[] NOTE_NCC_FLAG = new byte[]{(byte) -52, (byte) 110, (byte) 111, (byte) 116, (byte) 101};

    private int mPenColor = Color.WHITE;
    public float MAX_SCALE_SIZE = 3.0f;
    public float MIN_SCALE_SIZE = 0.3f;
    private int mBackgroundColor = Constants.BG_COLOLR_DEFAULT;
    private int mCellColor = 0;
    public Context mContext = null;
    private boolean mDrawCellBackground = true;
    private int mStyle = Constants.BG_STYLE_DEFAULT;

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

    private Bitmap mBgBitmap,mRBitmap;
    private Canvas mBgCanvas,mCacheCanvas,fbCanvas0,fbCanvas1,fbCanvas2,mRCanvas;

    private Bitmap mViewBitmap;
    private int mWidth = 0;
    private int mHeight = 0;
    protected ImageEditView mImageEditView;//图片剪裁控件
    private ArrayList<Page> mPages = new ArrayList();
    private int mCurrentPageIndex = 0;
    private Page mTmpPage = null;
    private int mTmpPageIndex = 0;
    //    private BaseBrushState mBaseBrushState;
    private PenAction mBaseBrushState;
    private RubberRaftModule RubberAction = null;
    private boolean mIsGestureRoam = false; // 判断是否为缩放模式
    private boolean mIsEraser = false; // 判断是否为自动板擦模式
    protected BaseSelectorState mSelectorState;
    public boolean isLocked = false;
    private int actionMode = 0; //操作类型，决定touch事件的最终操作类型
    private int shapeType = StatusEnum.STATUS_SHAPE_RECTTANGLE; //形状类型
    private int status = StatusEnum.STATUS_NC;
    float penWidth = Constants.PEN_WIDTH_MIDDLE;

    private ShapeAction mShapeAction;

    private boolean mHasRepainted = true;

    private static Bitmap sViewBitmap;

    private static final HashMap<String, String> sBitmapReferenceMap = new HashMap();
    private static final HashMap<String, Integer> sTextureReferenceMap = new HashMap();

    public static HashMap<String, String> getBitmapReferenceMap() {
        return sBitmapReferenceMap;
    }

    public static HashMap<String, Integer> getTextureReferenceMap() {
        return sTextureReferenceMap;
    }

    protected BaseGLSurfaceView mGLSurfaceView;

    public static int S_NONE = 0;
    public static int S_DURING_INSERT_POINT = 0x100;
    public int actionStatus = 0;  //当前状态：画笔补点处理耗时，缩放耗时，初始化耗时等

    public void requestOpenGLRender(boolean isDrawShape) {
        if (mGLSurfaceView != null) {
            this.mGLSurfaceView.setDrawShape(isDrawShape);
            this.mGLSurfaceView.requestRender();
        }
    }

    public void showOpenGL() {
        mGLSurfaceView.setZOrderOnTop(true);
        mGLSurfaceView.setZOrderMediaOverlay(true);
        //android 8.0  需要重新画下mGLSurfaceView以便显示在上层
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            mGLSurfaceView.setVisibility(View.INVISIBLE);
            mGLSurfaceView.setVisibility(View.VISIBLE);
        }
    }

    public void drawBackgroundOnly() {
        mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
        repaintToSurfaceInQueue();
    }

    public void setOpenGLView(BaseGLSurfaceView pOpenGLView) {
        this.mGLSurfaceView = pOpenGLView;
        this.mGLSurfaceView.setBoardContent(this);
    }


    /**
     * 将surface刷新放进队列和线程进行
     */
    protected BlockingQueue<Rect> mDrawingQueue = new ArrayBlockingQueue<Rect>(60);
    protected static final Object DRAWING_LOCKER = new Object();
    protected DrawingThread mDrawingThread;
    public Rect mWorkingRect;
    public static boolean mIsNeedSleep = false;

    /**
     * 引入线程刷新以及同步锁控制
     * 在刷新过程中，如果上次绘制未完成，则本次需要刷新的rect会判断是否有未执行刷新的rect，两次或多次汇总为一次刷新
     */
    private class DrawingThread extends BaseThread {
        private DrawingThread() {
        }

        public void run() {
            super.run();
            Rect dirtyRect = new Rect();
            while (this.mIsRunning) {
                Rect rect = null;
                try {
                    rect = (Rect) mDrawingQueue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (rect != null) {
                    dirtyRect.set(rect);
                    int queueSize = mDrawingQueue.size();
                    if (queueSize > 0) {
                        for (int i = 0; i < queueSize; i++) {
                            try {
                                rect = (Rect) mDrawingQueue.take();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (rect != null) {
                                dirtyRect.union(rect);
                            }
                        }
                    }
                    redrawDirtyArea(dirtyRect);
                } else {
                    return;
                }
            }
        }

        private void redrawDirtyArea(Rect rect) {
            if (mViewBitmap != null && !mViewBitmap.isRecycled() && rect != null) {
                long start = System.currentTimeMillis();
                synchronized (DRAWING_LOCKER) {
                    /** 获取Canvas对象，并锁定之**/
                    Canvas canvas = mHolder.lockCanvas(rect);
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
                    /**解除锁定，并提交改动内容**/
                    mHolder.unlockCanvasAndPost(canvas);
                    mIsNeedSleep = false;
                }
            }
        }
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

    public void setOnScaleChangedListener(OnScaleChangedListener listener) {
        mOnScaleChangedListener = listener;
    }

    public float getMinScale() {
        return MIN_SCALE_SIZE;
    }

    public float getMaxScale() {
        return MAX_SCALE_SIZE;
    }

    public void setMinScale(float scale) {
        MIN_SCALE_SIZE = scale;
    }

    public void setMaxScale(float scale) {
        MAX_SCALE_SIZE = scale;
    }

    public int zoomPanelView(float scale) {
        if (scale < getMinScale() || scale > getMaxScale()) {
            return -1;
        }
        setScaleRatio(scale);
        if (mOnScaleChangedListener != null) {
            mOnScaleChangedListener.onScaleChangedListener(scale);
        }
        return 0;
    }

    public float getScaleRatio() {
        return mCurrentPage.getScaleRatio();
    }

    public void setScaleRatio(float ratio) {
        mCurrentPage.setScaleRatio(ratio);
        setPenWidth(penWidth);
    }

    public boolean setStatus(int m) {
        if (m == status) {
            return false;
        }
        if (status == 7 && mEraseMaterialAction != null) {
            removeMaterial(mEraseMaterialAction.getErase());
        }
        status = m;
        return true;
    }

    public void addAction(Action action) {
        OperationManager.getInstance(this).addAction(action);
        triggerActionChanged();
    }

    public void cleanAction() {
        OperationManager.getInstance(this).clearActions();
    }

    private void triggerActionChanged() {
        if (mOnActionChangedListener != null) {
            mOnActionChangedListener.onActionChanged();
        }
    }

    public void setOnActionChangedListener(OnActionChangedListener listener) {
        mOnActionChangedListener = listener;
    }

    public void setOnModeChangedListener(OnModeChangedListener listener) {
        mOnModeChangedListener = listener;
    }

    public void setOnPageChangedListener(OnPageChangedListener listener) {
        mOnPageChangedListener = listener;
    }

    public List<Material> getMaterials() {
        Page page = getCurrentPage();
        if (page == null) {
            return new ArrayList();
        }
        return page.materialList;
    }

    public void addMaterial(Material material) {
        if (material instanceof Image) {
            if (getCurrentPage().mImageCount < 0) {
                getCurrentPage().mImageCount = 0;
            }
            if (getCurrentPage().mImageCount > getMaterials().size()) {
                getCurrentPage().mImageCount = getMaterials().size();
            }
            getCurrentPage().mImageCount++;
            getMaterials().add(getCurrentPage().mImageCount - 1, material);
        } else {
            getMaterials().add(material);
        }
    }

    public void resetMaterialList(List<Material> list) {
        getMaterials().clear();
        getCurrentPage().mImageCount = 0;
        for (Material temp : list) {
            addMaterial(temp);
        }
    }

    public void moveImage2Top(Image img) {
        removeMaterial(img);
        getCurrentPage().mImageCount++;
        getMaterials().add(getCurrentPage().mImageCount - 1, img);
    }

    public void removeMaterial(Material material) {
        synchronized (mCurrentPage.SHAPE_LOCK) {
            getMaterials().remove(material);
            if (material instanceof Image && getCurrentPage().mImageCount > 0) {
                getCurrentPage().mImageCount--;
            }
        }
    }

    public void removeMaterialList(List<Material> list) {
        for (Material temp : list) {
            removeMaterial(temp);
        }
    }

    public void addMaterialList(List<Material> list) {
        for (Material temp : list) {
            addMaterial(temp);
        }
    }

    public void unselectAllAndMaterial() {
        List<Material> materials = getMaterials();
        for (int mindex = materials.size() - 1; mindex >= 0; mindex--) {
            Material ma = (Material) materials.get(mindex);
            if (ma instanceof Selector) {
                materials.remove(ma);
                ((Selector) ma).reset();
            }
            ma.setSelected(false);
        }
        getSelectAction().reset();
    }

    public void unselectAll() {
        for (Material ma : getMaterials()) {
            ma.setSelected(false);
        }
    }

    public boolean setMode(int m) {
        return setMode(m, true, false);
    }

    public int getMode() {
        return actionMode;
    }

    public void setShapeType(int m) {
        shapeType = m;
    }

    public int getShapeType() {
        return shapeType;
    }

    private boolean setMode(int mode, boolean trigger, boolean event) {
        if (mode < 0 || mode > 5) {
            return false;
        }
        if (actionMode == 3 && mode != 3) {
            unselectAllAndMaterial();
            repaintToOffScreenCanvas(null, true);
        }
        actionMode = mode;
        if (trigger) {
            handleEventListener(14, null, Integer.valueOf(actionMode));
        }
        if (event && mOnModeChangedListener != null) {
            mOnModeChangedListener.onModeChanged(actionMode);
        }
        return true;
    }

    public boolean isReady() {
        return mReady;
    }

    public void setReady(boolean isReady) {
        mReady = isReady;
    }

    public void init(SurfaceHolder holder, int width, int height) {
        /**引入线程执行重绘刷新*/
        mWorkingRect = new Rect(0, 0, width, height);
        startDrawingThread();

        mReady = true;

        String sensitivity = Utils.getSystemProperties(Constants.ERASE_WIDTH_ID, Constants.MIN_WIDTH_ACTIVATE_ERASER_DEFAULT);
        String CTV4K = Utils.getSystemProperties("persist.sys.ctv.4k2k","false");
        Constants.MIN_WIDTH_ACTIVATE_ERASER = Float.parseFloat(sensitivity);

        mBgCanvas = new Canvas();
        mBgBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mBgCanvas.setBitmap(mBgBitmap);

/*
        mRCanvas = new Canvas();
        mRBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mRCanvas.setBitmap(mRBitmap);
*/

        mHolder = holder;
        mCacheCanvas = new Canvas();
        mCacheCanvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
        mWidth = width;
        mHeight = height;

        mViewBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        mCacheCanvas.setBitmap(mViewBitmap);
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            canvas.setBitmap(mViewBitmap);
            holder.unlockCanvasAndPost(canvas);
        }

        mBaseBrushState.setCurrentBrushColor(mPenColor);
        mBaseBrushState.setCurrentBrushThickness(penWidth);

        mSelectorState = new BaseSelectorState(this);

        UpdateBoardBackground(true);

        fbCanvas0 = new Canvas();
        fbCanvas1 = new Canvas();
        fbCanvas2 = new Canvas();

        if(CTV4K.equals("true"))
            WbJniCall.fbStart(1);
        else
            WbJniCall.fbStart(0);
        WbJniCall.fbSetCanvas(fbCanvas0,fbCanvas1,fbCanvas2);
    }

    private void startDrawingThread() {
        if (this.mDrawingThread == null || !this.mDrawingThread.isAlive()) {
            this.mDrawingThread = new DrawingThread();
            this.mDrawingThread.start();
        }
    }

    private void stopDrawingThread() {
        if (this.mDrawingThread != null) {
            this.mDrawingThread.stopThread();
            this.mDrawingThread = null;
        }
    }

    public void setOnEventListener(OnEventListener l) {
        mOnEventListener = l;
    }

    public void handleEventListener(int action, ArrayList<? extends Storable> materials, Object object) {
        if (mOnEventListener != null) {
            mOnEventListener.onEvent(action, materials, object);
        }
    }

    public PanelManager(Context context) {
        mContext = context;
        mBackgroundColor = (int) SharedPreferencesUtils.getParam(Constants.BG_COLOLR, Constants.BG_COLOLR_DEFAULT);
        mStyle = (int) SharedPreferencesUtils.getParam(Constants.BG_STYLE, Constants.BG_STYLE_DEFAULT);
        mPages.add(new Page(this));
        mCurrentPage = mPages.get(mCurrentPageIndex);
        mSelector = new Selector(this);
        mSelectAction = new SelectAction(this);

        mShapeAction = new ShapeAction(this);
        mBaseBrushState = new PenAction(this);
        mBaseBrushState.startPointThread();

        RubberAction = new RubberRaftModule(this);
        RubberAction.startRubberThread();
        RubberAction.RubberQueueIsOk = true;
        mSelectAction.setOnButtonClickListener(new SelectAction.OnButtonClickListener() {
            public boolean onButtonClickLisener(int button) {
                List<Material> ms;
                BaseUtils.dbg(TAG, "OpenNote button= " + button);
                switch (button) {
                    case 2:
                        PasteAction paste = new PasteAction();
                        List<Material> newList = paste.doPaste(PanelManager.this, new ArrayList(mSelectAction.getSelections()));
                        mSelectAction.reset();
                        unselectAllAndMaterial();
                        for (Material temp : newList) {
                            temp.setSelected(true);
                        }
                        mSelectAction.reset();
                        mSelectAction.setSelect(newList);
                        mSelector.showRect(true);
                        addMaterial(mSelector);
                        addAction(paste);
                        repaintToOffScreenCanvas(null, true);
                        break;
                    case 1:
                        DeleteAction action = new DeleteAction();

                        action.materialList.addAll(mSelectAction.getSelections());
                        removeMaterial(mSelector);
                        mSelector.reset();
                        Iterator it = action.materialList.iterator();
                        while (it.hasNext()) {
                            ((Material) it.next()).setSelected(false);
                        }
                        removeMaterialList(mSelectAction.getSelections());
                        mSelectAction.reset();
                        addAction(action);
                        repaintToOffScreenCanvas(null, true);
                        break;
                    case 3:
                        if (mSelectAction.materialList.size() == 1 && mSelectAction.materialList.get(0) instanceof Image) {
                            showImageEditView((Image) mSelectAction.materialList.get(0));
                        }
                        break;
                }
                handleEventListener(21, null, Integer.valueOf(button));
                return false;
            }
        });
    }

    public Context getContext() {
        return mContext;
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
    public Canvas getCacheCanvas() {
        return mCacheCanvas;
    }

    public Canvas getRCanvas(){
        return mRCanvas;
    }

    public Selector getSelector() {
        return mSelector;
    }

    public SelectAction getSelectAction() {
        return mSelectAction;
    }

    public int getStatus() {
        return status;
    }

    public void setEraseSize(float[][] sizes) {
    }

    public void setStdEraseSize(float[][] sizes) {
    }

    public void setOnTouchListener(OnTouchListener listener) {
        mOnTouchListener = listener;
    }

    private void handleTouchCallBack(MotionEvent event) {
        if (mOnTouchListener != null) {
            mOnTouchListener.onTouchListener(event);
        }
    }

    public void dbg(String msg) {
        BaseUtils.dbg(getClass().getSimpleName() + "_onTouch", msg);
    }


    float mPointer0LastX, mPointer0CurX;
    float mPointer0LastY, mPointer0CurY;
    private long mDownTime;
    Object m;

    /**
     * 触控逻辑处理
     *
     * @param event
     * @return
     */
    public boolean onTouch(MotionEvent event) {
        final int action = event.getActionMasked();

        if(!RubberAction.RubberQueueIsOk)
            return true;
        // 事件回调
        handleTouchCallBack(event);
        int count = event.getPointerCount();

//        BaseUtils.dbg(TAG, "action=" + action + " count=" + count);
        // 处理事件类型
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                onGestureEraserDown(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsEraser = false;
                if (!this.mIsGestureRoam) {
                    this.mSelectorState.mIsGlobalTransforming = false;
                    break;
                }
                mSelectorState.onTouchEvent(event);
                mIsGestureRoam = false;
                return true;
            case MotionEvent.ACTION_POINTER_DOWN: {
                break; /*暂时屏蔽缩放功能*/
                // 判断是否为多触控绘制
     /*           if (PanelUtils.isMultiPen()) {
                    break;
                }*/
                /*暂时屏蔽缩放功能*/

                // 判断是否为缩放模式
                /*if (!this.mIsGestureRoam) {
                    if (status != StatusEnum.STATUS_ERASE_BY_SIZE
                            && status != StatusEnum.STATUS_MOVE_ZOOM_SELECTION
                            && status != StatusEnum.STATUS_ERASE_BY_SELECTION
                            && System.currentTimeMillis() - mDownTime < 80) {
                        mBaseBrushState.abortState();
                        this.mSelectorState.mIsGlobalTransforming = true;
                        this.mIsGestureRoam = true;
                        break;
                    } else {
                        return true;
                    }
                }
                break;*/
            }
            default:
                break;
        }

        // 处理缩放
        if (this.mIsGestureRoam) {
            if (count >= 2) {
                if (mSelectAction.hasSelected()) {
                    unselectAllAndMaterial();
                }
                return this.mSelectorState.onTouchEvent(event);
            } else {
                return true;
            }
        }
//jean test
//        BaseUtils.dbg(TAG, "mIsGestureRoam=" + (mIsGestureRoam?"true":"false") + " actionMode=" + actionMode);
//        BaseUtils.dbg(TAG, "000 status=" + status);
        /**status为非移动和缩放情况下，根据mode设置status*/
        if (!mIsGestureRoam) {
            switch (actionMode) {
                case 0:
                    //默认情况下，即为普通划线操作，do nothing
                    if(status == 2)
                        break;
                        status = StatusEnum.STATUS_PAINT;
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
                case 4:
                    //从外部菜单栏选择了数学形状
                    status = StatusEnum.STATUS_SHAPE;
                    break;
                case 5:
                    status = StatusEnum.STATUS_PAINT;
                    break;
            }

        }
//        BaseUtils.dbg(TAG, "status=" + status);
        // 处理各种状态
        switch (status) {
            case StatusEnum.STATUS_NC:
                mBaseBrushState.onTouchEvent(event);
                if (action == MotionEvent.ACTION_MOVE) {
                    float spanx = event.getX() - mPointer0LastX;
                    float spany = event.getY() - mPointer0LastY;
                    /**位移大于5，则进入画笔模式*/
                    if (Math.abs(Math.sqrt((double) ((spanx * spanx) + (spany * spany)))) > 5.0d) {
                        Observable.timer(new Long((long) 50), TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
                            public void call(Long aLong) {
                                    status = StatusEnum.STATUS_PAINT;
                            }
                        });
                    }
                }
                break;

            case StatusEnum.STATUS_PAINT: // 画笔
                mBaseBrushState.onTouchEvent(event);
                break;
            case StatusEnum.STATUS_ERASE_BY_SIZE:
                /**创建块消除事件*/
                if (RubberAction.isFinish()) {
                    RubberAction.InitRubbrPolygon();
                }
                RubberAction.onTouchEvent(event);
                break;
            case StatusEnum.STATUS_ERASE_BY_SELECTION:
                if (mEraseMaterialAction == null) {
                    mEraseMaterialAction = new EraseMaterialAction();
                }
                mEraseMaterialAction.onTouch(this, event);
                handleEventListener(6, null, new EventData(action, event.getX(), event.getY()));
                if (action == MotionEvent.ACTION_UP) {
                    mEraseMaterialAction = null;
                    break;
                }
                removeMaterial(mEraseMaterialAction.getErase());
                break;
            case StatusEnum.STATUS_SELECTION:
            case StatusEnum.STATUS_MOVE_ZOOM_SELECTION:
                if (action == MotionEvent.ACTION_DOWN || mSelectAction.getClickEvent() == -1 || mSelectAction.getClickEvent() > 99) {
                    mSelectAction.onTouch(this, event);
                }
                break;
            case StatusEnum.STATUS_SHAPE:

                if (mShapeAction == null) {
                    mShapeAction = new ShapeAction(this);
                }
                mShapeAction.onTouch(this, event);

                break;
            case StatusEnum.STATUS_WRITE:
                break;
            default:
                break;
        }

        return true;
    }


    public boolean insertImage(String path, boolean selected, boolean suit) {
        float width = (float) mWidth;
        float height = (float) mHeight;
        float screenWidth = (float) mWidth;
        float screenHeight = (float) mHeight;
        if (width <= 0.0f) {
            Display display = ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            width = (float) point.x;
            screenWidth = width;
            height = (float) point.y;
            screenHeight = height;
        }
        Image image = new Image(this, path, 0.0f, 0.0f, screenWidth / 2.0f, screenHeight / 2.0f);
        if (!image.isBitmapReady()) {
            return false;
        }
        if (suit) {
            float iw = (float) image.getWidth();
            float ih = (float) image.getHeight();
            float maxWidth = (2.0f * width) / MAX_SCALE_SIZE;
            float maxHeight = (2.0f * height) / MAX_SCALE_SIZE;
            float scaleX = 1.0f;
            float scaleY = 1.0f;
            if (iw > maxWidth || ih > maxHeight) {
                scaleX = maxWidth / iw;
                scaleY = maxHeight / ih;
                if (scaleX > scaleY) {
                    scaleY = scaleX;
                } else {
                    scaleX = scaleY;
                }
                iw *= scaleX;
                ih *= scaleY;
            }
            if (iw > 0.0f && ih > 0.0f) {
                float offsetX = (screenWidth - iw) / 2.0f;
                float offsetY = (screenHeight - ih) / 2.0f;
                Matrix matrix = new Matrix();
                matrix.setTranslate(offsetX, offsetY);
                image.transform(matrix);
            }
        }
        addMaterial(image);
        addAction(new InsertImageAction(image));
        if (selected) {
            mSelectAction.reset();
            unselectAllAndMaterial();
            List<Material> ml = new ArrayList();
            image.setSelected(true);
            ml.add(image);
            mSelectAction.setSelect(ml);
            mSelector.showRect(true);
            addMaterial(mSelector);
        }
        repaintToOffScreenCanvas(null, true);
        repaintToSurfaceInQueue();
        sendInsertImageEvent(image);
        return true;
    }

    public void setImageEditView(ImageEditView pImageEditView) {
        this.mImageEditView = pImageEditView;
        this.mImageEditView.setOnImageEditListener(new OnImageEditListener() {

            @Override
            public void onCropOk(Image imageShape, RectF preRectF, RectF cropBounds) {
                hideImageEditView();

                if (isTheSameRect(preRectF, cropBounds)) {
                    addMaterial(mSelector);
                    return;
                }

                Image newImage = imageShape.cropImage(preRectF, cropBounds);
                if (newImage == null) {
                    return;
                }

                ReplaceImageAction replace = new ReplaceImageAction(imageShape, newImage);
                addAction(replace);

                removeMaterial(imageShape);
                addMaterial(newImage);
                unselectAllAndMaterial();
                mSelectAction.reset();

                List<Material> temp = new ArrayList<>();
                temp.add(newImage);
                mSelectAction.setSelect(temp);
                mSelector.showRect(true);
                mSelector.setShowPaste(true);
                addMaterial(mSelector);

                repaintToOffScreenCanvas(null, true);
                repaintToSurfaceInQueue();
            }

            @Override
            public void onCropCancel() {
                hideImageEditView();
                addMaterial(mSelector);
//                reDraw();
            }
        });
    }

    private boolean isTheSameRect(RectF rect1, RectF rect2) {
        return rect1.left == rect2.left && rect1.top == rect2.top && rect1.right == rect2.right && rect1.bottom == rect2.bottom;
    }

    public void showImageEditView(Image pShape) {
        if (this.mImageEditView == null) {
            return;
        }
        if (pShape != null) {
            this.mImageEditView.setImage(this, pShape);
            this.mImageEditView.show();
            isLocked = true;
            removeMaterial(mSelector);
//            reDraw();
            return;
        }
    }

    public void hideImageEditView() {
        if (this.mImageEditView == null) {
            return;
        }
        isLocked = false;
        this.mImageEditView.hide();
    }

    private void sendInsertImageEvent(Image image) {
        handleEventListener(8, null, image);
    }

    public boolean canUndo() {
        return OperationManager.getInstance(this).canUndo();
    }

    public boolean canRedo() {
        return OperationManager.getInstance(this).canRedo();
    }

    public boolean undo(boolean trigger) {
        unselectAllAndMaterial();
        Action action = OperationManager.getInstance(this).preUndo();
        if (action != null) {
            OperationManager.getInstance(this).undo(action, this);
        }

        triggerActionChanged();
        if (trigger) {
            handleEventListener(4, null, null);
        }
        return true;
    }

    public void redo(boolean trigger) {
        unselectAllAndMaterial();

        Action action = OperationManager.getInstance(this).preRedo();
        if (action != null) {
            OperationManager.getInstance(this).redo(action, this);
        }

        triggerActionChanged();
        if (trigger) {
            handleEventListener(5, null, null);
        }
    }

    public static void exchangeRect(Rect r1, Rect r2) {
        Rect r = new Rect(r2);
        r2.set(r1);
        r1.set(r);
    }

    public void drawBetweenRect(Canvas canvas, Rect r1, Rect r2) {
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Style.FILL);
        paint.setColor(mBackgroundColor);
        paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));

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
        bgPaint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
        canvas.drawBitmap(mBgBitmap, totalRect, totalRect, bgPaint);
    }

    public void drawScreenDelay(long time) {
        Observable.timer(time, TimeUnit.MILLISECONDS).subscribe(new Action1<Long>() {
            public void call(Long aLong) {
                repaintToOffScreenCanvas(null, true);
                repaintToSurfaceInQueue();
            }
        });
    }

    public void repaintToOffScreenCanvas(boolean directly) {
        repaintToOffScreenCanvas(null, directly);
    }

    public void repaintPencilToOffScreenCanvas(Rect dirtyRect, boolean directly) {
        if (mViewBitmap != null && !mViewBitmap.isRecycled()) {
            if (dirtyRect != null) {
                if (dirtyRect.left < dirtyRect.right && dirtyRect.top < dirtyRect.bottom) {
                    mCacheCanvas.save();
                    mCacheCanvas.clipRect(dirtyRect);
                } else if (directly) {
                    repaintToSurfaceInQueue();
                    return;
                } else {
                    return;
                }
            }
            synchronized (DRAWING_LOCKER) {
                if (dirtyRect != null) {
                    mCacheCanvas.drawBitmap(mBgBitmap, dirtyRect, dirtyRect, null);
                } else {
                    mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
                }
                Selector seletor = null;
                List<Material> list = new ArrayList(getMaterials());
                for (Material material : list) {
                    if (material instanceof PencilInk) {
                        material.draw(mCacheCanvas);
                    }
                }
            }
            if (dirtyRect != null) {
                mCacheCanvas.restore();
            }
            if (directly) {
                repaintToSurfaceInQueue();
            }
//            this.mHasRepainted = true;
        }
    }

    public void repaintToOffScreenCanvas(Rect dirtyRect, boolean directly) {
        if (mViewBitmap != null && !mViewBitmap.isRecycled()) {
            if (dirtyRect != null) {
                if (dirtyRect.left < dirtyRect.right && dirtyRect.top < dirtyRect.bottom) {
                    mCacheCanvas.save();
                    mCacheCanvas.clipRect(dirtyRect);
                } else if (directly) {
                    repaintToSurfaceInQueue();
                    return;
                } else {
                    return;
                }
            }
            synchronized (DRAWING_LOCKER) {
                if (dirtyRect != null) {
                    mCacheCanvas.drawBitmap(mBgBitmap, dirtyRect, dirtyRect, null);
                } else {
                    mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
                }
                Selector seletor = null;
                List<Material> list = new ArrayList(getMaterials());
//                BaseUtils.dbg(TAG, "material list= " + list.size());
                for (Material material : list) {
                    if (material instanceof Selector) {
                        seletor = (Selector) material;
                    } else if (material.isValid()) {
                        material.draw(mCacheCanvas);
                    }
                }
                if (seletor != null && seletor.selected()) {
                    seletor.draw(mCacheCanvas);
                }
            }
            if (dirtyRect != null) {
                mCacheCanvas.restore();
            }
            if (directly) {
                repaintToSurfaceInQueue();
            }
//            this.mHasRepainted = true;
        }
    }

    public void repaintToSurfaceInQueue() {
        this.mDrawingQueue.offer(this.mWorkingRect);
    }

    public void repaintToSurfaceInQueue(Rect rect) {
        this.mDrawingQueue.offer(rect);
    }

    public void drawScreen(Rect rect) {
        if (rect == null) {
            repaintToSurfaceInQueue();
        } else {
            repaintToSurfaceInQueue(rect);
        }
    }

    /**
     * 直接重绘指定区域
     */
    public void repaintScreen(Rect rect) {
        if (mHolder != null) {
            synchronized (mHolder) {
                Canvas canvas = mHolder.lockCanvas(rect);
                if (canvas != null && mHolder.getSurface().isValid()) {
                    if (rect == null) {
                        canvas.drawBitmap(mViewBitmap, 0.0f, 0.0f, mPaint);
                    } else {
                        /**重绘指定区域（局部刷新）*/
                        canvas.drawBitmap(mViewBitmap, rect, rect, mPaint);
                    }
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    /**
     * 直接重绘指定区域
     */
    public void repaintScreen(Rect rect, Material material) {
        if (mHolder != null) {
            synchronized (mHolder) {
                Canvas canvas = mHolder.lockCanvas(rect);
                if (canvas != null && mHolder.getSurface().isValid()) {
                    if (rect == null) {
                        canvas.drawBitmap(mViewBitmap, 0.0f, 0.0f, mPaint);
                    } else {
                        /**重绘指定区域（局部刷新）*/
                        canvas.drawBitmap(mViewBitmap, rect, rect, mPaint);
                    }

                    if (material != null) {
                        material.draw(canvas);
                    }
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    /**
     * 清空指定区域(让橡皮擦消失)
     */
    public void cleanTargetRect(Rect rect) {
        Paint paint = new Paint();
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Style.FILL);
        paint.setColor(mBackgroundColor);

        mCacheCanvas.drawRect(rect, paint);
        mCacheCanvas.drawBitmap(mBgBitmap, rect, rect, null);
    }

    private void onGestureEraserDown(MotionEvent event) {

        int count = event.getPointerCount();
        int index = event.getActionIndex();
        int id = event.getPointerId(index);
        float area = event.getSize();
        int mToolType = event.getToolType(0);
        float mPenSize = area* Constants.ERASE_VALUE_DEFAULT;
        BaseUtils.dbg(TAG, " onGestureEraserDown -- mToolType = " + mToolType);
 //       BaseUtils.dbg(TAG, " onGestureEraserDown -- area=" + area);
//        BaseUtils.dbg(TAG, " onGestureEraserDown -- status = " + status + " actionMode=" + actionMode);
        if (!WhiteBoardApplication.isAutoEnterEraseMode) {
            return;
        }

        if (status != StatusEnum.STATUS_ERASE_BY_SELECTION && actionMode != 1) {
            if((mToolType == MotionEvent.TOOL_TYPE_ERASER) || mIsEraser) {
                mIsEraser = true;
                computeEraserSize(mPenSize);
                Utils.setSystemProperties(Constants.HAVE_ERASE,"true");
            }else{
                if(mPenSize - Constants.MIN_WIDTH_ACTIVATE_ERASER > 0) {
                    computeEraserSize(mPenSize);
                }
            }
        } else {
            if (Constants.SYSTEM_CLIENT_CONFIG_NAME != null && (Constants.SYSTEM_CLIENT_CONFIG_NAME.equals("LT") ||
                    Constants.SYSTEM_CLIENT_CONFIG_NAME.equals("EDU")))
                Constants.erase_value = Constants.NO_ERASE_VALUE_DEFAULT;
            else
                Constants.erase_value = Constants.ERASE_VALUE_DEFAULT;
        }
    }

    private void computeEraserSize(float pWidth) {
        Constants.erase_value = (int) pWidth + Constants.ERASE_VALUE_DEFAULT;
        if(Constants.erase_value > 140)
            Constants.erase_value = 140;
        mBaseBrushState.abortState();
        status = StatusEnum.STATUS_ERASE_BY_SIZE;
    }

    public void destory() {
        if (mSelectAction != null) {
            mSelectAction.release();
        }
        if (mBgBitmap != null) {
            mBgCanvas = null;
            mBgBitmap.recycle();
            mBgBitmap = null;
        }
        if (mViewBitmap != null) {
            mCacheCanvas = null;
            mViewBitmap.recycle();
            mViewBitmap = null;
        }

        mDrawingQueue.clear();
        Iterator it = mPages.iterator();
        while (it.hasNext()) {
            ((Page) it.next()).release();
        }
        mPages.clear();
        mPages = null;

        stopDrawingThread();
        mBaseBrushState.stopPointThread();
        RubberAction.stopRubberThread();
        OperationManager.getInstance(this).release();
    }

    public static void setEraseLineColor(int color) {
        EraseMaterial.setSelectorLineColor(color);
    }

    public static void setSelectorLineColor(int color) {
        Selector.setSelectorLineColor(color);
    }

    public static void setSelectorRectColor(int color) {
        Selector.setSelectorRectColor(color);
    }

    public void setSelectedEdgeColor(int color) {
        Image.setSelectedEdgeColor(color);
    }

    public void setDrawCellBackground(boolean draw) {
        mDrawCellBackground = draw;
    }

    public boolean isDrawCellBackground() {
        return mDrawCellBackground;
    }

    public void setStyleBackground(int style) {
        this.mStyle = style;
    }

    public int getStyleBackground() {
        return this.mStyle;
    }

    public void setBoardBgBackground(int color) {
        this.mBackgroundColor = color;
    }

    public int getBoardBackgroundColor() {
        return mBackgroundColor;
    }

    public Bitmap getCurrentBgBitmap() {
        return mBgBitmap;
    }

    public int UpdateBoardBackground(boolean mInit) {
        if (mBgCanvas != null) {
//            int mColor = Color.rgb(100, 100, 100);
            int toColor = BaseUtils.getComparisonColor(mBackgroundColor);
            mCellColor = toColor;

            mBgCanvas.drawColor(mBackgroundColor);
            if (mStyle == 0) {
                Paint paint = new Paint();
                paint.setColor(mCellColor);
                paint.setStrokeWidth(1.0f);
                int widthStep = mWidth / 47;
                int heightStep = widthStep;
                for (int sx = 0; sx <= mWidth; sx += widthStep) {
                    mBgCanvas.drawLine((float) sx, 0.0f, (float) sx, (float) mHeight, paint);
                }
                for (int sy = 0; sy <= mHeight; sy += heightStep) {
                    mBgCanvas.drawLine(0.0f, (float) sy, (float) mWidth, (float) sy, paint);
                }
            } else if (mStyle == 1) {
                Paint paint = new Paint();
                paint.setColor(mCellColor);
                paint.setStrokeWidth(1.0f);
                int widthStep = mWidth / 47;
                int heightStep = widthStep;
                for (int sx = 0; sx <= mWidth; sx += widthStep) {
                    mBgCanvas.drawLine((float) sx, 0.0f, (float) sx, (float) mHeight, paint);
                }
            } else if (mStyle == 2) {
                Paint paint = new Paint();
                paint.setColor(mCellColor);
                paint.setStrokeWidth(1.0f);
                int widthStep = mWidth / 47;
                int heightStep = widthStep;
                for (int sy = 0; sy <= mHeight; sy += heightStep) {
                    mBgCanvas.drawLine(0.0f, (float) sy, (float) mWidth, (float) sy, paint);
                }
            } else if (mStyle == 3) {
                Paint paint = new Paint();
                paint.setColor(mCellColor);
                paint.setStrokeWidth(1.0f);
                int widthStep = mWidth / 47;
                int heightStep = widthStep;
                float nx = 0;
                int n = mWidth / widthStep;

                for (int sy = 0; sy <= mHeight; sy += heightStep) {
                    mBgCanvas.drawLine(0.0f, (float) sy, (float) sy, (float) mHeight, paint);
                }

                for (int sy = 0; sy <= mHeight; sy += heightStep) {
                    nx = n * widthStep - sy;
                    if (nx >= 0)
                        mBgCanvas.drawLine((float) nx, (float) mHeight, (float) mWidth, (float) sy, paint);
                }
            }
            mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
            if (mInit)
                repaintToSurfaceInQueue();
        }
        return 0;
    }

    public void setPenColor(int color) {
        mPenColor = color;
        mBaseBrushState.setCurrentBrushColor(color);
        mShapeAction.setCurrentBrushColor(color);
    }

    public void setPenType(int mType) {
        mBaseBrushState.setPenMode(mType);
        Utils.setSystemProperties(Constants.PEN_TYPE,String.valueOf(mType));
    }

    public void setPenWidth(float width) {
        penWidth = width;
        mBaseBrushState.setCurrentBrushThickness(penWidth * getScaleRatio());
        mShapeAction.setCurrentBrushThickness(penWidth * getScaleRatio());
    }

    public void cleanBoard() {
        if (mHolder != null) {
            Canvas canvas = mHolder.lockCanvas();
            if (canvas != null) {
//                mCacheCanvas.drawColor(mBackgroundColor, Mode.CLEAR);
                mCacheCanvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, null);
                canvas.drawBitmap(mViewBitmap, 0.0f, 0.0f, mPaint);
                mHolder.unlockCanvasAndPost(canvas);

                getCurrentPage().mImageCount = 0;
            }
        }
    }

    public void cleanLocalBoard(Rect rect) {
        if (mHolder != null) {
            Canvas canvas = mHolder.lockCanvas();
            if (canvas != null) {
                mCacheCanvas.drawBitmap(mBgBitmap, rect, rect, null);
                canvas.drawBitmap(mViewBitmap, rect, rect, mPaint);
                mHolder.unlockCanvasAndPost(canvas);

                getCurrentPage().mImageCount = 0;
            }
        }
    }

    public void resetBoard() {
        resetBoard(false);
    }

    public void resetBoard(boolean trigger) {
        zoomPanelView(1.0f);
        repaintToSurfaceInQueue();
        status = StatusEnum.STATUS_NC;
    }

    public void cleanBoardAction(boolean trigger) {
        cleanBoard();
        CleanAction action = new CleanAction(this);
        action.doClean(this, null);
        addAction(action);
        triggerActionChanged();
        if (trigger) {
            handleEventListener(3, null, null);
        }
    }

    public int getCurrentPageIndex() {
        return mCurrentPageIndex;
    }

    public Page getCurrentPage() {
        if (mPages == null || mPages.size() <= mCurrentPageIndex) {
            return null;
        }
        return mPages.get(mCurrentPageIndex);
    }

    public boolean canAddPage() {
        return getPageCount() < Constants.MAX_PAGE_COUNT;
    }

    public int addPage() {
        return addPage(true);
    }

    private int addPage(boolean trigger) {
        mPages.add(new Page(this));
        if (trigger) {
            handleEventListener(18, null, null);
        }
        freeTmpPage();
        return mPages.size() - 1;
    }

    public int getPageCount() {
        return mPages.size();
    }

    public int getTotalMaterialsCount() {
        int count = 0;
        if (mPages.size() > 0) {
            for (Page temp : mPages) {
                count += temp.materialList.size();
            }
        }
        return count;
    }

    public int getLastPagesMaterialsCount() {
        int count = 0;
        if (mPages.size() > 0) {
            count = mPages.get(mPages.size() - 1).materialList.size();
        }
        return count;
    }

    public void switchPage() {
        final Bitmap bitmap = getCurrentViewBitmap().copy(Config.ARGB_8888, true);
        final int width = bitmap.getWidth();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(new int[]{width, 0});
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, mWidth, mHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((Integer) animation.getAnimatedValue()).intValue();
                int left = ((Integer) animation.getAnimatedValue()).intValue();
                Canvas canvas = mHolder.lockCanvas();
                if (canvas == null) {
                    Log.e(TAG, "switchPage error canvas is null");
                    return;
                }
                canvas.clipRect(rect);
//                canvas.drawBitmap(mBgBitmap, 0.0f, 0.0f, mPaint);
                canvas.drawColor(mBackgroundColor);
                if (mSwitchPageInc) {
                    if (!mOldBitmap.isRecycled()) {
                        canvas.drawBitmap(mOldBitmap, (float) (left - width), 0.0f, paint);
                    }
                    canvas.drawBitmap(bitmap, (float) left, 0.0f, paint);
                } else {
                    if (!mOldBitmap.isRecycled()) {
                        canvas.drawBitmap(mOldBitmap, (float) (width - left), 0.0f, paint);
                    }
                    canvas.drawBitmap(bitmap, (float) (-left), 0.0f, paint);
                }
                mHolder.unlockCanvasAndPost(canvas);
                if (value == 0) {
                    mOldBitmap.recycle();
                }
            }
        });
        valueAnimator.start();
    }

    public int setPage(int index) {
        return setPage(index, true);
    }

    public void addPageSwitchAction(int index) {
        if (index != mCurrentPageIndex) {
            addAction(new PageSwitchAction(mCurrentPageIndex, index));
        }
    }

    public int setPage(int index, boolean anima) {
        if (index < 0 || index >= getPageCount()) {
            return -1;
        }
        if (index == mCurrentPageIndex) {
            return 0;
        }
        unselectAllAndMaterial();
        if (anima) {
            mSwitchPageInc = index > mCurrentPageIndex;
            if (!(mOldBitmap == null || mOldBitmap.isRecycled())) {
                mOldBitmap.recycle();
            }
        }
        mOldBitmap = getCurrentViewBitmap().copy(Config.ARGB_8888, true);
        if (index != mCurrentPageIndex) {
            mCurrentPageIndex = index;
            unselectAllAndMaterial();
            triggerActionChanged();
            mCurrentPage = mPages.get(mCurrentPageIndex);
            setPenWidth(penWidth);
            mOnPageChangedListener.onPageChanged(mCurrentPageIndex);
        }

        if (anima) {
            repaintToOffScreenCanvas(null, true);
            switchPage();
        } else {
            repaintToOffScreenCanvas(null, true);
        }
        return 0;
    }

    public Page getPage(int index) {
        return mPages.get(index);
    }

    public Page deletePageAndSet(int del, int set) {
        unselectAllAndMaterial();
        int count = getPageCount();
        if (del >= count) {
            return null;
        }
        int cur = getCurrentPageIndex();
        if (set < 0) {
            if (del > cur) {
                set = cur;
            } else if (del == cur) {
                if (cur < count) {
                    set = cur + 1;
                } else {
                    set = cur - 1;
                }
            } else if (del < cur) {
                set = cur;
            }
        }
        if (set < 0 || set > count) {
            set = 0;
        } else if (set == count) {
            set = del - 1;
        }
        setPage(set);
        Page page = getPage(del);
        deletePage(del);
        return page;
    }

    public int deletePage(int index) {
        return deletePage(index, true);
    }

    public int deletePage(int index, boolean trigger) {
        unselectAllAndMaterial();
        if (index < 0 || index >= mPages.size()) {
            return -1;
        }
        Page page = getPage(mCurrentPageIndex);
        mTmpPageIndex = index;
        mTmpPage = (Page) mPages.remove(index);
        mCurrentPageIndex = mPages.indexOf(page);

        if (trigger) {
            handleEventListener(19, null, Integer.valueOf(index));
        }
        return 0;
    }

    public int restorePage(boolean anim) {
        if (mTmpPage == null) {
            return -1;
        }
        Page old = getCurrentPage();
        mPages.add(mTmpPageIndex, mTmpPage);
        mCurrentPageIndex = mPages.indexOf(old);
        setPage(mTmpPageIndex, anim);
        return mTmpPageIndex;
    }

    public int freeTmpPage() {
        mTmpPage = null;
        return 0;
    }

    public Bitmap getCurrentViewBitmap() {
        return mViewBitmap;
    }


    public Bitmap getSuitBitmap(int id, int toWidth, int toHeight, boolean showScreen) {
//        BaseUtils.dbg(TAG, "id=" + id + " toWidth=" + toWidth + " toHeight=" + toHeight);
        Rect rect = new Rect(0, 0, mWidth, mHeight);
        List<Material> materials = getPage(id).materialList;
        for (Material material : materials) {
            if (material.isValid()) {
                rect.union(material.rect());
            }
        }
        float width = (float) rect.width();
        float height = (float) rect.height();


        float scale = ((float) toWidth) / width;
        float ts = ((float) toHeight) / height;
        if (ts < scale) {
            scale = ts;
        }
        float translateY;
        float translateX = rect.left < 0 ? (float) (-rect.left) : (float) rect.left;
        if (rect.top < 0) {
            translateY = (float) (-rect.top);
        } else {
            translateY = (float) rect.top;
        }

        width = (float) toWidth;
        height = (float) toHeight;
        try {
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Config.ARGB_8888);
//            Bitmap bitmap = mBgBitmap;
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);

            Matrix matrix = new Matrix();
            matrix.postTranslate(translateX, translateY);
            matrix.postScale(scale, scale);
            canvas.setMatrix(matrix);

            for (Material material2 : materials) {
                if (material2.isValid() && material2.canDrawForScreenShot()) {
                    boolean isSelected = material2.isSelected();
                    material2.setSelected(false);
                    material2.draw(canvas);
                    material2.setSelected(isSelected);
                }
            }
            Paint paint = new Paint();
            if (id == getCurrentPageIndex() && showScreen) {
                rect = new Rect(0, 0, mWidth, mHeight);
                paint.setColor(Color.argb(255, 64, 196, 255));
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(1.0f);
                RectF rf = new RectF();
                PanelUtils.rectToRectF(rect, rf, 0.0f);
                PanelUtils.rectFtoRect(rf, rect, 0);
                canvas.drawRect(rect, paint);
            }
            //jean test
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_OVER));
            canvas.drawColor(mBackgroundColor, Mode.DST_OVER);
            return bitmap;
        } catch (OutOfMemoryError e) {
            ToofifiLog.e(TAG, "Get board bitmapp failed: " + BaseUtils.getStackTrace(e));
            return null;
        }
    }

    public Bitmap getPreviewSuitBitmap(List<Material> mater, int toWidth, int toHeight, boolean full, boolean showScreen) {
        Rect rect = new Rect(0, 0, mWidth, mHeight);
        List<Material> materials = mater;
        for (Material material : materials) {
            if (material.isValid()) {
                rect.union(material.rect());
            }
        }
        float width = (float) rect.width();
        float height = (float) rect.height();


        float scale = ((float) toWidth) / width;
        float ts = ((float) toHeight) / height;
        if (ts < scale) {
            scale = ts;
        }
        float translateY;
        float translateX = rect.left < 0 ? (float) (-rect.left) : (float) rect.left;
        if (rect.top < 0) {
            translateY = (float) (-rect.top);
        } else {
            translateY = (float) rect.top;
        }

        width = (float) toWidth;
        height = (float) toHeight;
        try {
            Bitmap bitmap = Bitmap.createBitmap((int) width, (int) height, Config.ARGB_8888);
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);

            Matrix matrix = new Matrix();
            matrix.postTranslate(translateX, translateY);
            matrix.postScale(scale, scale);
            canvas.setMatrix(matrix);

            for (Material material2 : materials) {
                if (material2.isValid() && material2.canDrawForScreenShot()) {
                    boolean isSelected = material2.isSelected();
                    material2.setSelected(false);
                    material2.draw(canvas);
                    material2.setSelected(isSelected);
                }
            }
            Paint paint = new Paint();
            if (showScreen) {

                rect = new Rect(0, 0, mWidth, mHeight);
                paint.setColor(Color.argb(255, 64, 196, 255));
                paint.setStyle(Style.STROKE);
                paint.setStrokeWidth(2.0f);
                RectF rf = new RectF();
                PanelUtils.rectToRectF(rect, rf, 0.0f);
                PanelUtils.rectFtoRect(rf, rect, 0);
                canvas.drawRect(rect, paint);
            }
            paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
            canvas.drawColor(mBackgroundColor, Mode.DST_IN);
            return bitmap;
        } catch (OutOfMemoryError e) {
            BaseUtils.dbg(TAG, "Get board bitmapp failed: " + BaseUtils.getStackTrace(e));
            return null;
        }
    }

    public boolean saveNote(OutputStream out) throws IOException {
        if (mPages != null) {
            out.write(BaseUtils.intToByteArray(getCurrentPageIndex()));
            int size = mPages.size();
            out.write(BaseUtils.intToByteArray(size));
            int i = 0;
            while (i < size) {
                if (((Page) mPages.get(i)).writeObject(out)) {
                    i++;
                } else {
                    BaseUtils.dbg(TAG, "write object failed");
                    return false;
                }
            }
            out.write(BaseUtils.intToByteArray(55));
            out.close();
        }
        out.close();
        return true;
    }

    public boolean saveNote(String path) throws IOException {
        File file = new File(path);
        if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
            return saveNote(new FileOutputStream(file));
        }
        return false;
    }

    public boolean restoreNote(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            return restoreNote(new FileInputStream(file));
        }
        BaseUtils.dbg(TAG, "note file not exists");
        return false;
    }

    public Bitmap PreviewNote(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            return PreviewNote(new FileInputStream(file));
        }
        BaseUtils.dbg(TAG, "note file not exists");
        return null;
    }

    public boolean restoreNote(InputStream in) throws IOException {
        byte[] buf = new byte[4];
        if (in.read(buf) != 4) {
            BaseUtils.dbg(TAG, "read index failed");
            return false;
        }
        int index = BaseUtils.byteArrayToInt(buf);
        if (in.read(buf) != 4) {
            BaseUtils.dbg(TAG, "read size failed");
            return false;
        }
        int size = BaseUtils.byteArrayToInt(buf);

        ArrayList<Page> pages = new ArrayList();
        int i = 0;
        while (i < size) {
            Page page = new Page(this);
            page.panelManager = this;
            if (page.readObject(in)) {
                pages.add(page);
                i++;
            } else {
                BaseUtils.dbg(TAG, "read object failed");
                return false;
            }
        }
        mPages = pages;
        setPage(index, false);
        repaintToOffScreenCanvas(null, true);
        triggerActionChanged();
        in.close();
        return true;
    }

    public Bitmap PreviewNote(InputStream in) throws IOException {
        byte[] buf = new byte[4];
        if (in.read(buf) != 4) {
            BaseUtils.dbg(TAG, "read index failed");
            return null;
        }
        if (in.read(buf) != 4) {
            BaseUtils.dbg(TAG, "read size failed");
            return null;
        }

        int size = BaseUtils.byteArrayToInt(buf);
        if (size < 1)
            return null;
        ArrayList<Page> pages = new ArrayList();
        int i = 0;
        while (i < size) {
            BaseUtils.dbg(TAG, "read Page  --- " + i);
            Page page = new Page(this);
            if (page.readPreviewObject(in)) {
                pages.add(page);
                i++;
            } else {
                BaseUtils.dbg(TAG, "read object failed");
                return null;
            }
        }
//        BaseUtils.dbg(TAG, "pages =" + pages.size());
        List<Material> mMaterial = pages.get(size - 1).materialList;
        if (mMaterial != null) {
            return getPreviewSuitBitmap(mMaterial, mContext.getResources().getDimensionPixelSize(R.dimen.preview_image_width), mContext.getResources().getDimensionPixelSize(R.dimen.preview_image_height), true, true);
        }
        return null;
    }

    public boolean hasSelected() {
        boolean isSelected = mSelectAction != null && mSelectAction.hasSelected();
        boolean isCroping = mImageEditView != null && mImageEditView.isShown();
        return isSelected || isCroping;
    }

    public boolean isCroping() {
        return mImageEditView != null && mImageEditView.isShown();
    }

    public boolean isWorking() {
        return status == StatusEnum.STATUS_PAINT || status == StatusEnum.STATUS_ERASE_BY_SIZE || status == StatusEnum.STATUS_MOVE_ZOOM_SELECTION;
    }
}
