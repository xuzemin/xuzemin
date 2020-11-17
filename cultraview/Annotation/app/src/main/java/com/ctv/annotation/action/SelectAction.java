package com.ctv.annotation.action;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.ctv.annotation.element.Material;
import com.ctv.annotation.element.Selector;
import com.ctv.annotation.utils.PanelUtils;
import com.ctv.annotation.utils.StatusEnum;
import com.ctv.annotation.view.BaseThread;
import com.ctv.annotation.view.PanelManager;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class SelectAction extends Action{
    private int mClickEvent = -1;
    private Rect mDownRect = new Rect();
    private boolean mHasSelected = false;
    private boolean mInAction = false;
    private float mLastX;
    private float mLastY;
    private MoveAndZoomAction mMoveAction = null;
    private OnButtonClickListener mOnButtonClickListener = null;
    private Selector mSelector = null;
    boolean hasImageSelected = false;

    PanelManager mManager = null;

    public interface OnButtonClickListener {
        boolean onButtonClickLisener(int i);
    }

    public SelectAction(PanelManager manager)
    {
        mManager = manager;
        mSelector = manager.getSelector();
        //   startDrawingThread();
    }

    public void undo(PanelManager manager)
    {
    }

    public void redo(PanelManager manager)
    {
    }

    public boolean hasSelected()
    {
        return mHasSelected;
    }

    public List<Material> getSelections()
    {
        return materialList;
    }

    public void setOnButtonClickListener(OnButtonClickListener listener)
    {
        mOnButtonClickListener = listener;
    }

    public void reset()
    {
        materialList.clear();
        mHasSelected = false;
        if (mMoveAction != null)
        {
            mMoveAction.reset();
        }
    }

    public void setSelect(List<Material> materials)
    {
        mSelector.clearSelections();
        mSelector.setSelected(true);
        mSelector.addSelections(materials);
        materialList.clear();
        materialList.addAll(materials);
        mSelector.calculateSelectRect();
        mHasSelected = true;
    }

    public int getClickEvent()
    {
        return mClickEvent;
    }

    public boolean onTouch(PanelManager manager, MotionEvent event)
    {
        Material m;
    //    List<Material> existMaterials = manager.getMaterials();
        long downTime = event.getDownTime();
        long eventTime = event.getEventTime();
        int action = event.getActionMasked();
        float x = event.getX(0);
        float y = event.getY(0);
        float dx = x;
        float dy = y;

        if (action == MotionEvent.ACTION_DOWN)
        {

            hasImageSelected = false;
            mClickEvent = -1;

            /**已有被选中时，对touch点的位置进行判断,根据返回的mClickEvent值判断位置点*/
            if (mSelector.selected())
            {
                mClickEvent = mSelector.clickButton(dx, dy);
                if (mClickEvent >= 99 || mClickEvent == -1 || mOnButtonClickListener == null)
                {
                    mDownRect.set(mSelector.selectedRect());
                }
                else
                {
                    mOnButtonClickListener.onButtonClickLisener(mClickEvent);
                    return true;
                }
            }
            else
            {
                mSelector.addPoint(x, y, event);
            }
        }


        if (mSelector != null && mSelector.selected() && (mInAction || mSelector.selectedRect().contains((int) dx, (int) dy)))
        {
            /**touch点在选中框的范围内，则处理移动和缩放事件**/
            mInAction = true;
            manager.setStatus(StatusEnum.STATUS_MOVE_ZOOM_SELECTION);
            /**每次action_down会新建MoveAndZoomAction*/
            if (mMoveAction == null || action == MotionEvent.ACTION_DOWN)
            {
                mMoveAction = new MoveAndZoomAction(manager);
                mMoveAction.setSelected(materialList);
            }
            mMoveAction.onTouch(manager, event);
            if (action == MotionEvent.ACTION_UP)
            {
                mInAction = false;
            }
            return true;
        }
        else if (mClickEvent != -1)
        {
            mLastX = dx;
            mLastY = dy;
            if (action == MotionEvent.ACTION_UP)
            {
                mClickEvent = -1;
            }
            return true;
        }
        else
        {

            /**没有已被选中时，进行路径绘制并加入已选中material*/
            if (action == MotionEvent.ACTION_DOWN)
            {
//                materialList.clear();
//                if (mSelector != null && existMaterials.contains(mSelector))
//                {
//                    manager.unselectAll();
//                    manager.removeMaterial(mSelector);
//                    manager.repaintToOffScreenCanvas(null, true);
////                    manager.reDraw();
//                }
//                mSelector.reset();
//                mHasSelected = false;
         //       manager.unselectAll();
            }
            mSelector.addPoint(x, y, event);
            if (action == MotionEvent.ACTION_DOWN)
            {
                mLastX = dx;
                mLastY = dy;
           //     manager.addMaterial(mSelector);
                return true;
            }
            mSelector.drawPoint(manager, x, y);
 //           int materialSize = existMaterials.size();
            Rect rect3 = new Rect();
            Rect actionRect = PanelUtils.buildRect((int) mLastX, (int) mLastY, (int) dx, (int) dy);

            /**判断路径是否相交，是否被选中*/
//            for (int mindex = materialSize - 1; mindex >= 0; mindex--)
//            {
//                Material material = (Material) existMaterials.get(mindex);
//                boolean isSelected = false;
//                if (!material.isSelected())
//                {
//
//                    if (material.intersect(actionRect))
//                    {
//                        if (material.isCross(mLastX, mLastY, dx, dy) || ((material instanceof Image) && !hasImageSelected))
//                        {
//                            isSelected = true;
//                            if (material instanceof Image)
//                            {
//                                manager.moveImage2Top((Image) material);
//                                hasImageSelected = true;
//                            }
//                        }
//                    }
//
//                    if (isSelected)
//                    {
//
//                        material.setSelected(true);
//                        mSelector.addRect(material.rect());
//                        materialList.add(material);
//
////                        mDrawingQueue.offer(material);
//
//
//                        rect3 = material.rect();
//                        mSelector.addRect(rect3);
//                        material.draw(manager.getCacheCanvas());
//                        manager.drawScreen(rect3);
//                        mHasSelected = true;
//                    }
//                }
//            }
//            if (action == MotionEvent.ACTION_UP)
//            {
//                /**判断路径是否相交，是否被选中*/
//                for (int mindex = materialSize - 1; mindex >= 0; mindex--)
//                {
//                    Material material = (Material) existMaterials.get(mindex);
//                    boolean isSelected = false;
//                    if (!material.isSelected())
//                    {
//                        /**完全包含*/
//                        if (mSelector.getPathRect().contains(material.rect()))
//                        {
//                            isSelected = true;
//                            if (material instanceof Image)
//                            {
//                                manager.moveImage2Top((Image) material);
//                                hasImageSelected = true;
//                            }
//                        }
//
//                        if (isSelected)
//                        {
//                            material.setSelected(true);
//                            materialList.add(material);
//
//                            rect3 = material.rect();
//                            mSelector.addRect(rect3);
//                            material.draw(manager.getCacheCanvas());
//                            manager.drawScreen(rect3);
//                            mHasSelected = true;
////                        manager.repaintToOffScreenCanvas(rect3,true);
//                        }
//                    }
//                }
//
//                if (!mHasSelected)
//                {
//                    manager.removeMaterial(mSelector);
//                }
//                if (materialList.size() <= 10 && !hasImageSelected)
//                {
//                    mSelector.setShowPaste(true);
//                }
//                else
//                {
//                    mSelector.setShowPaste(false);
//                }
//
//                if (materialList.size() == 1 && materialList.get(0) instanceof Image)
//                {
//                    mSelector.setShowCrop(true);
//                    mSelector.setShowPaste(true);
//                }
//                else
//                {
//                    mSelector.setShowCrop(false);
//                }
//
//                mSelector.showRect(true);
//                manager.repaintToOffScreenCanvas(null, true);
////                stopDrawingThread();
//            }
            mLastX = dx;
            mLastY = dy;
            return true;
        }
    }

    public void linkMaterial(List<Material> lists)
    {
        for (Integer i : mMaterialIdList)
        {
            if (i.intValue() >= 0 && i.intValue() < lists.size())
            {
                materialList.add(lists.get(i.intValue()));
            }
        }
    }

    /**
     * 将surface刷新放进队列和线程进行
     */
    protected BlockingQueue<Material> mDrawingQueue = new ArrayBlockingQueue<Material>(60);
    private RefresfThread mRefresfThread;

    private class RefresfThread extends BaseThread {

        private RefresfThread()
        {

        }

        @Override public void run()
        {
            super.run();
            Rect dirtyRect = new Rect();
            while (this.mIsRunning)
            {
                Material m = null;
                try
                {
                    m = (Material) mDrawingQueue.take();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                if (m != null)
                {
                    m.draw(mManager.getCacheCanvas());
//                    mSelector.addRect(m.rect());
                    dirtyRect.set(m.rect());
                    int queueSize = mDrawingQueue.size();
                    if (queueSize > 0)
                    {
                        for (int i = 0; i < queueSize; i++)
                        {
                            try
                            {
                                m = (Material) mDrawingQueue.take();
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                            if (m != null)
                            {
                                m.draw(mManager.getCacheCanvas());
//                                mSelector.addRect(m.rect());
                                dirtyRect.union(m.rect());
                            }
                        }
                    }
                    mManager.drawScreen(dirtyRect);
                }
                else
                {
                    return;
                }
            }
        }
    }

    public void release(){
        mDrawingQueue.clear();
        stopDrawingThread();
    }

    private void startDrawingThread()
    {
        if (this.mRefresfThread == null || !this.mRefresfThread.isAlive())
        {
            this.mRefresfThread = new RefresfThread();
            this.mRefresfThread.start();
        }
    }

    private void stopDrawingThread()
    {
//        if (this.mRefresfThread != null)
//        {
//            this.mRefresfThread.stopThread();
//            this.mRefresfThread = null;
//        }
    }
}
