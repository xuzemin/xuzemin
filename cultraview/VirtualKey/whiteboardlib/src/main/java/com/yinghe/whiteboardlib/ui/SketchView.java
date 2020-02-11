/*
 * Copyright (C) 2015 Federico Iosue (federico.iosue@gmail.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.yinghe.whiteboardlib.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.apkfuns.logutils.LogUtils;
import com.yinghe.whiteboardlib.bean.SketchData;
import com.yinghe.whiteboardlib.bean.StrokeRecord;
import com.yinghe.whiteboardlib.utils.AppUtils;
import com.yinghe.whiteboardlib.utils.DrawConsts;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.yinghe.whiteboardlib.utils.DrawConsts.EDIT_STROKE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_DRAW;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_ERASER;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_ERASER_CIRCLE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_ERASER_RECT;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_LINE;
import static com.yinghe.whiteboardlib.utils.DrawConsts.STROKE_TYPE_TEXT;


/**
 * 画板界面UI
 * @author wang
 */
public class SketchView extends View implements OnTouchListener {
    final String TAG = getClass().getSimpleName();

    public interface TextWindowCallback {
        void onText(View view, StrokeRecord record);
    }

    public void setTextWindowCallback(TextWindowCallback textWindowCallback) {
        this.textWindowCallback = textWindowCallback;
    }

    private TextWindowCallback textWindowCallback;

    public static final int DEFAULT_STROKE_SIZE = 7;
    public static final int DEFAULT_STROKE_ALPHA = 100;
    public static final int DEFAULT_ERASER_SIZE = 50;

    private float strokeSize = DEFAULT_STROKE_SIZE;
    private int strokeRealColor = Color.RED;//画笔实际颜色
    private int strokeColor = Color.RED;//画笔颜色
    private int strokeAlpha = 255;//画笔透明度
    private float eraserSize = DEFAULT_ERASER_SIZE;

    private Bitmap mBitmap;// 临时图片
    private Canvas mCanvas;// 临时画布
    // 增量画布，用来绘制移动过程中的操作步骤
    private Bitmap dBitmap;// 增量图片
    private Canvas dCanvas;// 增量画布

    private Paint mBitmapPaint = null;
    private Paint strokePaint;// 画笔
    private Paint eraserPaint;// 橡皮擦
    private Paint eraserIconPaint;// 橡皮擦跟随的图标
    private Paint selectedPaint;// 用来选中的的路径画笔
    private Paint clearPaint;// 画布清空的画笔

    private PointF[] downPointFs, prePointFs, curPointFs;// 记录点击的点坐标
    private PointF downPointF, prePointF, curPointF;// 记录点击的点坐标数组
    private List<Integer> curPointerIdArr;// 记录多点触控的手指索引
    private int mWidth, mHeight;

    private boolean isUndoOrRedo = false;// 进行撤销或者重做操作
    public boolean isTouch = false;

    SketchData curSketchData;
    private Context mContext;

    StrokeRecord curStrokeRecord;

    private PaintFlagsDrawFilter pfd;

    private int editMode = DrawConsts.EDIT_STROKE;
    private static final int MAX_TOUCH_POINTS = 3;// 多点触控的最大值

    private HandlerThread mDataThread;
    private Handler mDataHandler;// 数据处理

    public void setStrokeType(int strokeType) {
        lastStrokeType = strokeType;
        this.strokeType = strokeType;
    }

    public int getStrokeType() {
        return strokeType;
    }

    private int strokeType = DrawConsts.STROKE_TYPE_DRAW;
    private int lastStrokeType = DrawConsts.STROKE_TYPE_DRAW;

    private OnDrawChangedListener onDrawChangedListener;


    private final static Lock LOCK = new ReentrantLock();

    public void setSketchData(SketchData sketchData) {
        this.curSketchData = sketchData;
    }

    public SketchView(Context context, AttributeSet attr) {
        super(context, attr);
        //        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速

        this.mContext = context;
        initParams();
        this.setFocusableInTouchMode(true);
        this.setFocusable(true);
        this.setKeepScreenOn(true);
        if (isFocusable()) {
            this.setOnTouchListener(this);
        }

        mDataThread = new HandlerThread("mDataThread-message");
        mDataThread.start();
        mDataHandler = new DataHandler(this, mDataThread.getLooper());
    }


    /**
     * 初始化点
     */
    private void initPoint() {
        // 初始化点
        initPointArr();

        // 初始化触控索引
        initPointerIndexArr();
    }

    /**
     * 初始化触控索引
     */
    private void initPointerIndexArr(){
        if(curPointerIdArr == null){
            curPointerIdArr = new ArrayList<>();
        }

        curPointerIdArr.clear();
    }

    /**
     * 初始化点
     */
    private void initPointArr(){
        downPointF = new PointF();
        prePointF = new PointF();
        curPointF = new PointF();

        downPointFs = new PointF[MAX_TOUCH_POINTS];
        curPointFs = new PointF[MAX_TOUCH_POINTS];
        prePointFs = new PointF[MAX_TOUCH_POINTS];

        for(int i = 0; i < MAX_TOUCH_POINTS; i++){
            curPointFs[i] = new PointF();
            downPointFs[i] = new PointF();
            prePointFs[i] = new PointF();
        }
    }

    private void initParams() {
        initPoint();
        setBackgroundColor(Color.TRANSPARENT);

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setStyle(Paint.Style.FILL);
        mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        mBitmapPaint.setStrokeCap(Paint.Cap.SQUARE);

        // 画笔
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        strokePaint.setAntiAlias(true);
        strokePaint.setDither(true);
        strokePaint.setColor(strokeRealColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStrokeWidth(strokeSize);

        // 普通绘制画笔设置
        eraserIconPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        eraserIconPaint.setAntiAlias(true);
        eraserIconPaint.setDither(true);
        eraserIconPaint.setColor(Color.WHITE);
        eraserIconPaint.setStyle(Paint.Style.FILL);
        eraserIconPaint.setStrokeJoin(Paint.Join.ROUND);
        eraserIconPaint.setStrokeCap(Paint.Cap.ROUND);
        eraserIconPaint.setStrokeWidth(eraserSize);

        //圈选橡皮擦的画笔
        selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        selectedPaint.setAntiAlias(true);
        selectedPaint.setDither(true);
        selectedPaint.setColor(Color.BLACK);
        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setStrokeJoin(Paint.Join.ROUND);
        selectedPaint.setStrokeCap(Paint.Cap.ROUND);
        selectedPaint.setStrokeWidth(2);
        PathEffect effects = new DashPathEffect(new float[] { 20.0F, 10.0F, 5.0F, 10.0F}, 1F);
        selectedPaint.setPathEffect(effects);

        //橡皮擦
        eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        eraserPaint.setAntiAlias(true);
        eraserPaint.setDither(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeCap(Paint.Cap.ROUND);
        eraserPaint.setStrokeWidth(eraserSize);

        // 画布画笔
        clearPaint = new Paint();
        PorterDuffXfermode pdfClear = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        clearPaint.setXfermode(pdfClear);

        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG |
                Paint.FILTER_BITMAP_FLAG);
    }

    /**
     * 初始化画布
     */
    private void initCanvas(){
        // 回收图片
        if (mBitmap!= null && !mBitmap.isRecycled()){
            mBitmap.isRecycled();
            mBitmap = null;
            mCanvas = null;
        }

        // 创建临时画布
//        LogUtils.d("w * h -> %s * %s",mWidth ,  mHeight);
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        mCanvas = new Canvas(mBitmap);
        mCanvas.setDrawFilter(pfd);
    }

    /**
     * 初始化增量画布
     */
    private void initDCanvas() {
        // 回收图片
        if (dBitmap != null && !dBitmap.isRecycled()) {
            dBitmap.isRecycled();
            dBitmap = null;
            dCanvas = null;
        }

        // 创建临时画布
        dBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_4444);
        dCanvas = new Canvas(dBitmap);
    }

    /**
     * 设置画笔透明度
     *
     * @param mAlpha
     */
    public void setStrokeAlpha(int mAlpha) {
        this.strokeAlpha = mAlpha;
        calculateColor();
        strokePaint.setStrokeWidth(strokeSize);
    }

    public void setStrokeColor(int color) {
        strokeColor = color;
        calculateColor();
        strokePaint.setColor(strokeRealColor);
    }

    private void calculateColor() {
        strokeRealColor = Color.argb(strokeAlpha, Color.red(strokeColor), Color.green(strokeColor), Color.blue(strokeColor));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        LogUtils.d("onSizeChanged");
        mWidth = w;
        mHeight = h;

        initCanvas();
        initDCanvas();

        drawStrokeRecords(mCanvas, false);

        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 对移动点的处理
     *
     * @param event
     */
    private void handleMovePoints(MotionEvent event, PointF[]  pointFs){
        // 设置当前点和触控索引数组
        int pointerId = -1;
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++){
            // 获得当前触控索引
            pointerId = event.getPointerId(i);
            //            LogUtils.d("多点触控，i-->%s, pointerId-->%s" ,i , pointerId);
            if (pointerId < 0 || pointerId >= MAX_TOUCH_POINTS){
                continue;
            }

            // 记录当前触控索引
            if (curPointerIdArr.contains(pointerId)){
                // 获得当前点的坐标
                pointFs[pointerId].x = event.getX(i);
                pointFs[pointerId].y = event.getY(i);
            }
        }
    }

    /**
     * 创建新的画笔记录
     */
    private void createNewPathRecord(MotionEvent event){
        if (!curSketchData.strokeRedoList.isEmpty()){
            curSketchData.strokeRedoList.clear();
        }

        // 判断按下的面积
        float size = event.getSize() * 1000;
        int areaEraserSize = Math.round(size);
        if (areaEraserSize >= 30){
            strokeType = STROKE_TYPE_ERASER_RECT;
            LogUtils.d("strokeType->%s, areaEraserSize->%s",strokeType, areaEraserSize);

            curStrokeRecord = new StrokeRecord(STROKE_TYPE_ERASER_RECT);
            curStrokeRecord.areaEraserSize = areaEraserSize;
            curStrokeRecord.pathList = new SparseArray<>();
            curStrokeRecord.pathList.put(0, new Path());

            eraserPaint.setStrokeWidth(1f);
            eraserPaint.setStyle(Paint.Style.FILL);
            curStrokeRecord.paint = new Paint(eraserPaint);

            // 获得橡皮擦跟随的图片区域
            float x = event.getX();
            float y = event.getY();
            curStrokeRecord.rect = new RectF();
            curStrokeRecord.rect.left = x - size * 0.5f;
            curStrokeRecord.rect.top = y - size * 0.75f;
            curStrokeRecord.rect.right = x + size * 0.5f;
            curStrokeRecord.rect.bottom = y + size * 0.75f;
        } else {
            // 若是面积擦除画笔类型，则使用上一次画笔类型
            if (strokeType == STROKE_TYPE_ERASER_RECT){
                strokeType = lastStrokeType;
            }
            curStrokeRecord = new StrokeRecord(strokeType);
            curStrokeRecord.pathList = new SparseArray<>();

            // 保存当前画笔类型
            lastStrokeType = strokeType;
            LogUtils.d("创建了其他画笔类型,strokeType->%s", strokeType);
        }

        // 当画笔类型不是文字时，添加记录
        if (strokeType != STROKE_TYPE_TEXT){
            curSketchData.strokeRecordList.add(curStrokeRecord);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        // 触控事件
        // 统一单点和多点
        int action = (event.getAction() & MotionEvent.ACTION_MASK) % 5;
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {// 按下
                // 取消恢复背光
                if (AppUtils.isEyeCare(mContext)){
                    mDataHandler.removeCallbacks(resetLight);
                }

                synchronized (curPointerIdArr){
                    // 首次按下时
                    if (event.getPointerCount() == 1){
                        isTouch = true;
                        curPointerIdArr.clear();

                        // 创建新的画笔记录
                        createNewPathRecord(event);

                        // 执行护眼操作
                        if (AppUtils.isEyeCare(mContext)){
                            AppUtils.eyeCareHandle(mContext, true);
                        }
                    }

                    // 当前触控个数
                    int actionIndex = event.getActionIndex();
                    // 获得当前触控索引
                    int pointerId = event.getPointerId(actionIndex);
                    if (pointerId < 0 || pointerId >= MAX_TOUCH_POINTS){
                        break;
                    }

                    LogUtils.d("多点触控，i-->%s, pointerId-->%s" ,actionIndex , pointerId);

                    // 保存当前触控索引
                    if (!curPointerIdArr.contains(pointerId)){
                        curPointerIdArr.add(pointerId);
                    }

                    // 获得当前点的坐标
                    curPointFs[pointerId].x = event.getX(actionIndex);
                    curPointFs[pointerId].y = event.getY(actionIndex);

                    curPointF.x = event.getX();
                    curPointF.y = event.getY();
                    downPointF.x = curPointF.x;
                    downPointF.y = curPointF.y;

                    // 点击按下操作
                    downPointFs[pointerId].x = curPointFs[pointerId].x;
                    downPointFs[pointerId].y = curPointFs[pointerId].y;

                    touchDownEditStroke(pointerId);

                    // 设置prePointFs值
                    prePointFs[pointerId].x = curPointFs[pointerId].x;
                    prePointFs[pointerId].y = curPointFs[pointerId].y;

                    prePointF.x = curPointF.x;
                    prePointF.y = curPointF.y;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {// 移动
                synchronized (curPointerIdArr){
                    // 对touch事件预处理:获得当前点
                    handleMovePoints(event, curPointFs);
                    curPointF.x = event.getX();
                    curPointF.y = event.getY();

                    touchMoveEditStroke();

                    prePointF.x = curPointF.x;
                    prePointF.y = curPointF.y;
                    setPrePointsByCurPoints();

                    // 绘制数据
                    invalidate();
                }

                break;
            }
            case MotionEvent.ACTION_UP: {// 抬起
                try {
                    LOCK.lock();

                    touchUp(event);
                    // 绘制数据
                    invalidate();
                } catch (Exception e){
                    e.printStackTrace();
                    isTouch = false;
                } finally {
                    LOCK.unlock();
                }
                break;
            }
        }

        return true;
    }

    /**
     * 抬起操作
     */
    private void touchUp(MotionEvent event) {
        // 删除抬起的触控ID
        int actionIndex = event.getActionIndex();
        Integer pointerId = event.getPointerId(actionIndex);
        curPointerIdArr.remove(pointerId);

        // 最后一个手指抬起的时候
        if (event.getPointerCount() <= 1){
            isTouch = false;
            curPointerIdArr.clear();

            // 恢复背光
            if (AppUtils.isEyeCare(mContext)){
                mDataHandler.postDelayed(resetLight, 3000);
            }

            // 将多点触控的多条路径全部合成为一条路径
            if (editMode == EDIT_STROKE){
                if (strokeType == STROKE_TYPE_ERASER_CIRCLE) {// 橡皮擦圈选
                    float midX = (curPointFs[0].x + prePointFs[0].x) / 2;
                    float midY = (curPointFs[0].y + prePointFs[0].y) / 2;
                    curStrokeRecord.pathList.get(0).quadTo(prePointFs[0].x, prePointFs[0].y, midX, midY);
                    curStrokeRecord.pathList.get(0).close();

                    // 触控抬起时，切换为擦除画笔
                    eraserPaint.setStyle(Paint.Style.FILL);
                    curStrokeRecord.paint = new Paint(eraserPaint);
                } else if (strokeType == STROKE_TYPE_DRAW){ // 自由曲线
                    // 遍历record.pathList，获得绘制数据
                    if (curStrokeRecord.path == null){
                        curStrokeRecord.path = new Path();
                    }

                    Path tmpPath = null;
                    int len = curStrokeRecord.pathList.size();
                    for (int i = 0; i < len; i++){
                        tmpPath = curStrokeRecord.pathList.get(i);
                        if (tmpPath == null){
                            continue;
                        }

                        curStrokeRecord.path.addPath(tmpPath);
                    }
                }
            }

            drawOneStrokeRecord(curStrokeRecord, mCanvas, true);
            curStrokeRecord.hasDraw = true;
        }
    }

    /**
     * 将curPointFs的值赋给prePointFs
     */
    private void setPrePointsByCurPoints(){
        // 将curPointFs的值赋给prePointFs
        int pointerId = -1;
        int size = curPointerIdArr.size();
        for (int i = 0; i < size; i++){
            pointerId = curPointerIdArr.get(i);
            if (pointerId < 0 || pointerId >= MAX_TOUCH_POINTS){
                continue;
            }

            prePointFs[pointerId].x = curPointFs[pointerId].x;
            prePointFs[pointerId].y = curPointFs[pointerId].y;
        }
    }

    /**
     * 按下时，操作模式为EDIT_STROKE
     * @param pointerId
     */
    private void touchDownEditStroke(int pointerId) {
        if (strokeType == STROKE_TYPE_ERASER_RECT){ // 接触面积橡皮擦
            if (curStrokeRecord.pathList == null || curStrokeRecord.pathList.get(0) == null){
                return;
            }

            curStrokeRecord.pathList.get(0).moveTo(curPointF.x - curStrokeRecord.areaEraserSize * 0.5f,
                    curPointF.y - curStrokeRecord.areaEraserSize * 0.75f);
//            curStrokeRecord.pathList.get(0).moveTo(downPointF.x, downPointF.y);
        } else if (strokeType == STROKE_TYPE_ERASER) { // 橡皮擦
            curStrokeRecord.pathList.put(0, new Path());
            curStrokeRecord.pathList.get(0).moveTo(downPointFs[0].x, downPointFs[0].y);

            eraserPaint.setStrokeWidth(eraserSize);
            curStrokeRecord.paint = new Paint(eraserPaint); // Clones the mPaint object
        } else if (strokeType == STROKE_TYPE_ERASER_CIRCLE) { // 橡皮擦圈选
            if(pointerId != 0){
                return;
            }

            curStrokeRecord.pathList.put(0, new Path());

            curStrokeRecord.pathList.get(0).moveTo(downPointFs[0].x, downPointFs[0].y);
            curStrokeRecord.paint = new Paint(selectedPaint); // Clones the mPaint object
        } else if (strokeType == DrawConsts.STROKE_TYPE_DRAW ) { // 光滑曲线
            // 初始化路径
            if (curStrokeRecord.pathList.get(pointerId) == null){
                curStrokeRecord.pathList.put(pointerId, new Path());
            }

            PointF downPointF = curPointFs[pointerId];
            curStrokeRecord.pathList.get(pointerId).moveTo(downPointF.x, downPointF.y);

            strokePaint.setColor(strokeRealColor);
            strokePaint.setStrokeWidth(strokeSize);
            curStrokeRecord.paint = new Paint(strokePaint); // Clones the mPaint object
        }  else if (strokeType == STROKE_TYPE_LINE) { // 直线
            curStrokeRecord.pathList.put(0, new Path());
            curStrokeRecord.pathList.get(0).moveTo(downPointFs[0].x, downPointFs[0].y);

            strokePaint.setColor(strokeRealColor);
            strokePaint.setStrokeWidth(strokeSize);
            curStrokeRecord.paint = new Paint(strokePaint); // Clones the mPaint object
        } else if (strokeType == DrawConsts.STROKE_TYPE_CIRCLE
                || strokeType == DrawConsts.STROKE_TYPE_RECTANGLE) { // 圆形或者矩形
            RectF rect = new RectF(downPointFs[0].x, downPointFs[0].y, downPointFs[0].x, downPointFs[0].y);
            curStrokeRecord.rect = rect;
            strokePaint.setColor(strokeRealColor);
            strokePaint.setStrokeWidth(strokeSize);
            curStrokeRecord.paint = new Paint(strokePaint); // Clones the mPaint object
        } else if (strokeType == STROKE_TYPE_TEXT) { // 文字
            curStrokeRecord.textOffX = (int) downPointFs[0].x;
            curStrokeRecord.textOffY = (int) downPointFs[0].y;
            TextPaint tp = new TextPaint();
            tp.setColor(strokeRealColor);
            curStrokeRecord.textPaint = tp; // Clones the mPaint object
            textWindowCallback.onText(this, curStrokeRecord);
            return;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isUndoOrRedo){
            drawStrokeRecords(mCanvas, false);
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            isUndoOrRedo = false;
        } else {
            try {
                LOCK.lock();
                // 绘制所有的画笔记录
                drawAllStroke(canvas);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                LOCK.unlock();
            }
        }
    }

    /**
     * 清理画布canvas
     *
     * @param temptCanvas
     */
    public void clearCanvas(Canvas temptCanvas) {
        temptCanvas.drawPaint(clearPaint);
    }

    /**
     * 绘制所有的画笔记录
     * @param canvas
     */
    private void drawAllStroke(Canvas canvas){
        canvas.setDrawFilter(pfd);

        if (strokeType == STROKE_TYPE_ERASER_RECT){// 面积橡皮擦
            clearCanvas(dCanvas);

            // 在增量画布上，绘制当前记录图片
            dCanvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            dCanvas.drawPath(curStrokeRecord.pathList.get(0), eraserPaint);
            canvas.drawBitmap(dBitmap, 0, 0, mBitmapPaint);

            if (!isTouch){
                canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
            }
            // 绘制跟随的方形橡皮擦
            if (curStrokeRecord != null && !curStrokeRecord.hasDraw){
                canvas.save();
                if (curStrokeRecord.rect != null){
                    canvas.drawRoundRect(curStrokeRecord.rect, 2, 2, eraserIconPaint);
                }
                canvas.restore();
            }
        } else {  // 其他画笔记录
            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            // 直接在画布上绘制当前的路径
            if (curStrokeRecord != null && !curStrokeRecord.hasDraw){
                drawOneStrokeRecord(curStrokeRecord, canvas, false);
            }
        }
    }

    /**
     * 绘制画笔记录
     *
     * @param tmpCanvas 临时画布
     * @param isCompositionPath 是否绘制合成路径
     */
    private void drawStrokeRecords(Canvas tmpCanvas, boolean isCompositionPath) {
        if(curSketchData.strokeRecordList == null || curSketchData.strokeRecordList.isEmpty()){
            return;
        }

        // 绘制所有画笔路径
        for (StrokeRecord record : curSketchData.strokeRecordList) {
            drawOneStrokeRecord(record, tmpCanvas, isCompositionPath);
        }
    }

    /**
     * 绘制一条画笔路径记录
     * @param record
     * @param canvas
     * @param isCompositionPath
     */
    private void drawOneStrokeRecord(StrokeRecord record, Canvas canvas, boolean isCompositionPath){
        if (record == null){
            return;
        }

        int type = record.type;
        if (type == STROKE_TYPE_ERASER // 绘制线性橡皮擦
                || type == STROKE_TYPE_ERASER_RECT  // 绘制面积橡皮擦
                || type == STROKE_TYPE_ERASER_CIRCLE // 绘制圈选橡皮擦
                ||type == STROKE_TYPE_LINE) { // 绘制直线
            if (record.pathList.size() == 0 || record.pathList.get(0) == null){
                return;
            }

            canvas.drawPath(record.pathList.get(0), record.paint);
        } else if (type == STROKE_TYPE_DRAW) { // 绘制光滑曲线
            if (isCompositionPath){// 绘制合成路径
//                LogUtils.d("绘制合成路径");
                if (record.path != null){
                    canvas.drawPath(record.path, record.paint);
                }
            } else {// 绘制分路径
//                LogUtils.d("绘制分路径");
                if (record.pathList == null || record.pathList.size()==0){
                    return;
                }

                // 遍历record.pathList，获得绘制数据
                Path tmpPath = null;
                int len = record.pathList.size();
                for (int i = 0; i < len; i++){
                    tmpPath = record.pathList.get(i);
                    if (tmpPath == null){
                        continue;
                    }

                    canvas.drawPath(tmpPath, record.paint);
                }
            }
        } else if (type == DrawConsts.STROKE_TYPE_CIRCLE) { // 绘制圆形
            canvas.drawOval(record.rect, record.paint);
        } else if (type == DrawConsts.STROKE_TYPE_RECTANGLE) { // 绘制矩形
            canvas.drawRect(record.rect, record.paint);
        } else if (type == STROKE_TYPE_TEXT) { // 绘制文字
            if (record.text != null) {
                StaticLayout layout = new StaticLayout(record.text, record.textPaint, record.textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
                canvas.translate(record.textOffX, record.textOffY);
                layout.draw(canvas);
                canvas.translate(-record.textOffX, -record.textOffY);
            }
        }
    }

    public void addTextStrokeRecord(StrokeRecord record) {
        curSketchData.strokeRecordList.add(record);
        drawOneStrokeRecord(record, mCanvas, true);
        invalidate();
    }

    /**
     * 移动时，操作模式为EDIT_STROKE
     */
    private void touchMoveEditStroke(){
        if (strokeType == STROKE_TYPE_ERASER_RECT) {// 橡皮擦面积擦除
            if (curStrokeRecord.pathList.get(0) == null){
                return;
            }

//            // 连接画笔
//            float midX = (curPointF.x + prePointF.x) / 2;
//            float midY = (curPointF.y + prePointF.y) / 2;
//            curStrokeRecord.pathList.get(0).quadTo(prePointF.x, prePointF.y, midX, midY);

            if (curStrokeRecord.rect == null){
                curStrokeRecord.rect = new RectF();
            }
            curStrokeRecord.rect.left = curPointF.x - curStrokeRecord.areaEraserSize * 0.5f;
            curStrokeRecord.rect.top = curPointF.y - curStrokeRecord.areaEraserSize * 0.75f;
            curStrokeRecord.rect.right = curPointF.x + curStrokeRecord.areaEraserSize * 0.5f;
            curStrokeRecord.rect.bottom = curPointF.y + curStrokeRecord.areaEraserSize * 0.75f;

            // 添加矩形路径
            curStrokeRecord.pathList.get(0).moveTo(curPointF.x - curStrokeRecord.areaEraserSize * 0.5f,
                    curPointF.y - curStrokeRecord.areaEraserSize * 0.75f);
            curStrokeRecord.pathList.get(0).addRect(curPointF.x - curStrokeRecord.areaEraserSize * 0.5f,
                    curPointF.y - curStrokeRecord.areaEraserSize * 0.75f,
                    curPointF.x + curStrokeRecord.areaEraserSize * 0.5f,
                    curPointF.y + curStrokeRecord.areaEraserSize * 0.75f,
                    Path.Direction.CCW);
        } else if (strokeType == STROKE_TYPE_ERASER) {// 橡皮擦
            float midX = (curPointFs[0].x + prePointFs[0].x) / 2;
            float midY = (curPointFs[0].y + prePointFs[0].y) / 2;
            curStrokeRecord.pathList.get(0).quadTo(prePointFs[0].x, prePointFs[0].y, midX, midY);
        } else if (strokeType == STROKE_TYPE_ERASER_CIRCLE) {// 橡皮擦圈选
            if (curStrokeRecord.pathList.get(0) == null){
                return;
            }

            float midX = (curPointFs[0].x + prePointFs[0].x) / 2;
            float midY = (curPointFs[0].y + prePointFs[0].y) / 2;
            curStrokeRecord.pathList.get(0).quadTo(prePointFs[0].x, prePointFs[0].y, midX, midY);
        } else if (strokeType == STROKE_TYPE_DRAW) {// 光滑曲线
            float preX = 0f;
            float preY = 0f;
            float curX = 0f;
            float curY = 0f;

            float dx = 0;
            float dy = 0;

            float cX = 0;
            float cY = 0;
            int pointerId = -1;
            int size = curPointerIdArr.size();
            for (int i = 0; i < size; i++){
                pointerId = curPointerIdArr.get(i);
                if ((pointerId < 0) || (curStrokeRecord.pathList.get(pointerId) == null)){
                    continue;
                }

                preX = prePointFs[pointerId].x;
                preY = prePointFs[pointerId].y;
                curX = curPointFs[pointerId].x;
                curY = curPointFs[pointerId].y ;

                dx = Math.abs(curX - preX);
                dy = Math.abs(curY - preY);
                //两点之间的距离大于等于1时，生成贝塞尔绘制曲线
                if (dx >= 1 || dy >= 1){
                    //设置贝塞尔曲线的操作点为起点和终点的一半
                    cX = (preX + curX) / 2;
                    cY = (preY + curY) / 2;
                    curStrokeRecord.pathList.get(pointerId).quadTo(preX, preY, cX, cY);
                }
            }
        } else if (strokeType == STROKE_TYPE_LINE) {// 直线
            curStrokeRecord.pathList.get(0).reset();
            curStrokeRecord.pathList.get(0).moveTo(downPointFs[0].x, downPointFs[0].y);
            curStrokeRecord.pathList.get(0).lineTo(curPointFs[0].x, curPointFs[0].y);
        } else if ((strokeType == DrawConsts.STROKE_TYPE_CIRCLE)
                || (strokeType == DrawConsts.STROKE_TYPE_RECTANGLE)) {// 方形和圆形
            float left = (downPointFs[0].x < curPointFs[0].x) ? downPointFs[0].x : curPointFs[0].x;
            float top = (downPointFs[0].y < curPointFs[0].y) ? downPointFs[0].y : curPointFs[0].y;
            float right = (downPointFs[0].x > curPointFs[0].x) ? downPointFs[0].x : curPointFs[0].x;
            float bottom = (downPointFs[0].y > curPointFs[0].y) ? downPointFs[0].y : curPointFs[0].y;
            curStrokeRecord.rect.set(left, top, right, bottom);
        }
    }

    /*
     * 删除一笔
     */
    public void undo() {
        if (curSketchData.strokeRecordList.size() > 0) {
            curSketchData.strokeRedoList.add(curSketchData.strokeRecordList.get(curSketchData.strokeRecordList.size() - 1));
            curSketchData.strokeRecordList.remove(curSketchData.strokeRecordList.size() - 1);

            isUndoOrRedo = true;
            clearCanvas(dCanvas);
            clearCanvas(mCanvas);
            invalidate();
        }
    }

    /*
     * 撤销
     */
    public void redo() {
        if (curSketchData.strokeRedoList.size() > 0) {
            curSketchData.strokeRecordList.add(curSketchData.strokeRedoList.get(curSketchData.strokeRedoList.size() - 1));
            curSketchData.strokeRedoList.remove(curSketchData.strokeRedoList.size() - 1);
        }

        isUndoOrRedo = true;
        clearCanvas(dCanvas);
        clearCanvas(mCanvas);
        invalidate();
    }

    public int getRedoCount() {
        return curSketchData.strokeRedoList != null ? curSketchData.strokeRedoList.size() : 0;
    }

    public int getRecordCount() {
        return (curSketchData.strokeRecordList != null && curSketchData.photoRecordList != null) ? curSketchData.strokeRecordList.size() + curSketchData.photoRecordList.size() : 0;
    }

    public int getStrokeRecordCount() {
        return curSketchData.strokeRecordList != null ? curSketchData.strokeRecordList.size() : 0;
    }

    public void setSize(int size, int eraserOrStroke) {
        switch (eraserOrStroke) {
            case STROKE_TYPE_DRAW:
                strokeSize = size;
                break;
            case STROKE_TYPE_ERASER:
                eraserSize = size;
                break;
        }
    }

    /**
     * 清空画板
     */
    public void clearAll() {
        curSketchData.strokeRecordList.clear();
        curSketchData.photoRecordList.clear();
        curSketchData.strokeRedoList.clear();

        isUndoOrRedo = true;

        // 初始化画布
        clearCanvas(dCanvas);
        clearCanvas(mCanvas);

        System.gc();
        invalidate();
    }

    public interface OnDrawChangedListener {
        void onDrawChanged();
    }

    public void setEditMode(int editMode) {
        this.editMode = editMode;
        invalidate();
    }

    public int getEditMode() {
        return editMode;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    /**
     * data异步处理
     */
    public static final class DataHandler extends Handler {
        WeakReference<SketchView> weakReference;

        public DataHandler(SketchView sketchView, Looper looper) {
            super(looper);
            this.weakReference = new WeakReference<>(sketchView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SketchView sketchView = weakReference.get();

            if (sketchView == null) {
                return;
            }

            // 开始处理
            switch (msg.what){
                case 0:{//
                    break;
                }
                default:
                    break;
            }
        }
    }

    /**
     * 恢复背光
     */
    Runnable resetLight =  new Runnable() {
        @Override
        public void run() {
            AppUtils.eyeCareHandle(mContext, false);
        }
    };

}