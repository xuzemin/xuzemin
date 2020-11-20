package com.mphotool.whiteboard.action;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.view.PanelManager;

import java.util.List;

public class BaseSelectorState extends Action {
    static String TAG = "BaseSelectorState";
    /**两次缩放事件的时间间隔*/
    private static final long DEFAULT_TIME_INTERVAL = 500;
    private static long sTimeInterval = DEFAULT_TIME_INTERVAL;

    /**action_up时间*/
    private static long sTimeUP;

    /**记录状态*/
    private static int sTouchState;

    /**需要重绘的区域*/
    protected Rect mDirtyRect = new Rect();
    public boolean mIsGlobalTransforming = false;
    protected int mPointerId;

    /**画布整体缩放*/
    public GlobalTransformState mGlobalTransformState;


    protected long mTouchDownTime = System.currentTimeMillis();
    PanelManager mPanelManager;

    public BaseSelectorState(PanelManager manager) {
        mPanelManager = manager;
        this.mGlobalTransformState = new GlobalTransformState(manager);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (System.currentTimeMillis() - sTimeUP >= sTimeInterval) {
            if (this.mIsGlobalTransforming) {
                this.mGlobalTransformState.onTouchEvent(event);
            }
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    sTouchState = 0;
                    touchDown(event);
                    this.mTouchDownTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    if (sTouchState == 0) {
                        if (this.mIsGlobalTransforming) {
                            this.mIsGlobalTransforming = false;
                        }
                        sTouchState = 3;
                        break;
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!(sTouchState != 0 || this.mPointerId == -1 || this.mIsGlobalTransforming)) {
                        break;
                    }
                default:
                    break;
            }
        }
        return true;
    }

    protected void touchDown(MotionEvent event) {
        this.mPointerId = event.getPointerId(0);
        float x = event.getX();
        float y = event.getY();
        float pressure = event.getPressure();
        this.mDirtyRect.setEmpty();
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
    }

    @Override public void undo(PanelManager panelManager)
    {
        super.undo(panelManager);
    }
}
