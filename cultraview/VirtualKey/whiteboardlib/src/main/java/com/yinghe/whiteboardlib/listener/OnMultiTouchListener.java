package com.yinghe.whiteboardlib.listener;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import com.apkfuns.logutils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 多点触控监听
 * @author wang
 * @date 2018/5/23
 */
public abstract class OnMultiTouchListener implements View.OnTouchListener {
    private final static String TAG = OnMultiTouchListener.class.getSimpleName();

    private boolean isMultiTouch = false; // 是否多点触控
    private boolean isTouching = false;
    private static final int MAX_TOUCH_POINTS = 5;// 多点触控的最大值

    private PointF[] downPointFs, prePointFs, curPointFs;// 记录点击的点坐标
    private PointF downPointF, prePointF, curPointF;// 记录点击的点坐标数组
    private List<Integer> curPointerIdArr;// 记录多点触控的手指索引

    public OnMultiTouchListener(boolean isMultiTouch){
        this.isMultiTouch = isMultiTouch;
        initPoint();
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

    /**
     * 对移动点的处理
     *
     * @param event
     */
    private void beforeHandleMovePoints(MotionEvent event, PointF[]  pointFs){
        curPointF.x = event.getX();
        curPointF.y = event.getY();

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

    private void beforeTouchDown(MotionEvent event){
        // 首次按下时
        if (event.getPointerCount() == 1){
            isTouching = true;
            curPointerIdArr.clear();

            // 创建新的画笔记录
            touchDownForFirst(event);
        }

        // 当前触控个数
        int actionIndex = event.getActionIndex();
        // 获得当前触控索引
        int pointerId = event.getPointerId(actionIndex);
        if (pointerId < 0 || pointerId >= MAX_TOUCH_POINTS){
            return;
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
    }

    private void afterTouchDown(int pointerId){
        // 设置prePointFs值
        prePointFs[pointerId].x = curPointFs[pointerId].x;
        prePointFs[pointerId].y = curPointFs[pointerId].y;

        prePointF.x = curPointF.x;
        prePointF.y = curPointF.y;
    }

    /**
     * 首次按下
     * @param event
     */
    public abstract void touchDownForFirst(MotionEvent event);

    /**
     * 普通按下时
     * @param pointerId
     */
    public abstract void touchDown(int pointerId);

    /**
     * 移动过程中
     */
    public abstract void touchMove();

    /**
     * 抬起操作
     */
    private void touchUp(MotionEvent event){}

    /**
     * 抬起了所有触控点
     *
     * @param event
     */
    public abstract void touchUpAll(MotionEvent event);

    /**
     * 触摸移动之后
     */
    private void afterTouchMove(){
        prePointF.x = curPointF.x;
        prePointF.y = curPointF.y;

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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 触控事件
        // 统一单点和多点
        int action = (event.getAction() & MotionEvent.ACTION_MASK) % 5;
        if (action == MotionEvent.ACTION_CANCEL) {
            return false;
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {// 按下
                synchronized (curPointerIdArr){
                    // 当前触控个数
                    int actionIndex = event.getActionIndex();
                    // 获得当前触控索引
                    int pointerId = event.getPointerId(actionIndex);
                    if (pointerId < 0 || pointerId >= MAX_TOUCH_POINTS){
                        break;
                    }

                    beforeTouchDown(event);

                    // 处理按下的逻辑
                    touchDown(pointerId);

                    afterTouchDown(pointerId);
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {// 移动
                synchronized (curPointerIdArr){
                    // 对touch事件预处理:获得当前点
                    beforeHandleMovePoints(event, curPointFs);

                    // 处理移动事件
                    touchMove();

                    // 移动之后
                    afterTouchMove();
                }

                break;
            }
            case MotionEvent.ACTION_UP: {// 抬起
                // 删除抬起的触控ID
                int actionIndex = event.getActionIndex();
                Integer pointerId = event.getPointerId(actionIndex);
                curPointerIdArr.remove(pointerId);

                // 最后一个手指抬起的时候
                if (event.getPointerCount() <= 1){
                    isTouching = false;
                    curPointerIdArr.clear();

                    // 抬起了所有触控点
                    touchUpAll(event);
                }
                break;
            }
        }

        return false;
    }
}
