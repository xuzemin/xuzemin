package com.ctv.annotation.action;

import android.graphics.Matrix;
import android.view.MotionEvent;

import com.ctv.annotation.BuildConfig;
import com.ctv.annotation.element.Material;
import com.ctv.annotation.element.PencilInk;
import com.ctv.annotation.view.PanelManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class MoveAndZoomAction extends Action{
    String TAG = "MoveAndZoomAction";
    PanelManager mManager = null;

    private static final long DEFAULT_TIME_INTERVAL = 250;
    private static long sTimeInterval = DEFAULT_TIME_INTERVAL;
    private static final long TIME_INTERVAL_TOLERANT = 50;
    private static long sTimeUP;

    private static int sTouchState = 3;

    private boolean mIsOpenGLEnabled = BuildConfig.enable_opengl;

    private Matrix mMatrix;

    private int selectedWidth = 0;

    /**
     * 多指移动与缩放处理
     */
//    GlobalRoamResizeTransformer mTransformer;
//    private OnTransformListener mTransformListener = new OnTransformListener() {
//        @Override public boolean onContinueTransform(Matrix matrix)
//        {
//
//            Selector selector = mManager.getSelector();
//            float[] values = new float[9];
//            matrix.getValues(values);
//            if(values[0] > 1 && selector.selectedRect().width() >= 1920){
//                return false;
//            }
//            Rect rect = new Rect();
//            List<Material> ms = mManager.getMaterials();
//            Rect updateRect = new Rect();
//            int iconWidth = (int) (((float) mManager.getContext().getResources().getDimensionPixelSize(R.dimen.select_icon_width)));
//            for (Material m : ms)
//            {
//                if (m instanceof Selector)
//                {
//                    selector = (Selector) m;
//                    updateRect.union(selector.rect());
//                    PanelUtils.rectAddWidth(updateRect, iconWidth);
//                }
//                else if (m.isSelected())
//                {
//                    m.continueTransform(matrix);
//                    rect.union(m.rect());
//                }
//            }
//            if (selector != null)
//            {
//                selector.setSelectRect(rect.left, rect.top, rect.right, rect.bottom);
//                updateRect.union(selector.rect());
//            }
//            updateRect.union(rect);
//            PanelUtils.rectAddWidth(updateRect, iconWidth + 5);
//
//            if (mIsOpenGLEnabled)
//            {
//                mManager.requestOpenGLRender(true);
//                return true;
//            }
//
//            selectedWidth = updateRect.width();
//
//            //  mManager.repaintToOffScreenCanvas(updateRect, true);
//            //    mManager.repaintToSurfaceInQueue(updateRect);
//            return true;
//        }
//
//        @Override public void onEndTransform(Matrix matrix)
//        {
//
//            /**缩放结束后，重置point位置*/
////            List<Material> ms = mManager.getMaterials();
//            for (Material m : materialList)
//            {
//                if (m instanceof PencilInk)
//                {
//                    ((PencilInk) m).endTransform(matrix);
//                }
//            }
//            mMatrix = matrix;
//            mManager.addAction(MoveAndZoomAction.this);
//
//            if (mIsOpenGLEnabled)
//            {
//                /**此处直接进行缓存和显示区的重绘，再清空OpenGL画布，避免闪烁*/
//                // mManager.repaintToOffScreenCanvas(null, false);
//                //  mManager.repaintScreen(null);
//                //  mManager.requestOpenGLRender(false);
//
//            }
//        }
//    };

    public void undo(PanelManager manager)
    {
        super.undo(manager);
        Matrix matrix = new Matrix();
        if (mMatrix != null && this.mMatrix.invert(matrix))
        {
            int size = materialList.size();
            for (int i = 0; i < size; i++)
            {
                if(materialList.get(i) instanceof PencilInk){
                    ((PencilInk)materialList.get(i)).resetWidth = true;
                }
                materialList.get(i).transform(matrix);
            }
        }
    }

    public void redo(PanelManager manager)
    {
        super.redo(manager);
        if (mMatrix != null)
        {
            int size = materialList.size();
            for (int i = 0; i < size; i++)
            {
                if(materialList.get(i) instanceof PencilInk){
                    ((PencilInk)materialList.get(i)).resetWidth = true;
                }
                materialList.get(i).transform(mMatrix);
            }
        }
    }

    public void setSelected(List<Material> mSelectedMaterials)
    {
        materialList.clear();
        materialList.addAll(mSelectedMaterials);
    }


    public void setPanelManager(PanelManager panelManager)
    {
        mManager = panelManager;
    }

    public MoveAndZoomAction(PanelManager panelManager)
    {
        mManager = panelManager;
     //   mTransformer = new GlobalRoamResizeTransformer(mTransformListener);
    }

    private MoveAndZoomAction createAction(PanelManager manager, List<Material> list, Matrix matrix)
    {
        MoveAndZoomAction act = new MoveAndZoomAction(manager);
        act.materialList = list;
        act.mMatrix = matrix;
        return act;
    }

    public void reset()
    {
    }

    public boolean onTouch(PanelManager manager, MotionEvent event)
    {
        if (System.currentTimeMillis() - sTimeUP >= sTimeInterval)
        {
            int action = event.getActionMasked();
            if (mIsOpenGLEnabled && ((action == 0 || action == 5) && sTouchState != 0))
            {

                this.mManager.requestOpenGLRender(true);
                this.mManager.drawBackgroundOnly();
                this.mManager.showOpenGL();
            }
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
            {
                sTouchState = 0;
            }
            if (sTouchState != 0)
            {
                return true;
            }
            if (action == 1 || action == 3)
            {
                sTouchState = 3;

            //    this.mTransformer.onTouch(event);
                sTimeUP = System.currentTimeMillis();
                return true;
            }
        //    mTransformer.onTouch(event);
        }
        return true;
    }

    public boolean readObject(InputStream in) throws IOException
    {
        super.readObject(in);
        return true;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        super.writeObject(out);
        return true;
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
}
