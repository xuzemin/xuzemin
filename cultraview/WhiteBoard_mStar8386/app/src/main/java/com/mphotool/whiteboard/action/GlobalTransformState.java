package com.mphotool.whiteboard.action;

import android.graphics.Matrix;
import android.view.MotionEvent;

import com.mphotool.whiteboard.BuildConfig;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.PencilInk;
import com.mphotool.whiteboard.elements.ShapeMaterial;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.view.PanelManager;
import com.mphotool.whiteboard.view.transform.GlobalRoamResizeTransformer;
import com.mphotool.whiteboard.view.transform.OnTransformListener;

import java.util.List;

public class GlobalTransformState {
    String TAG = "GlobalTransformState";
    private static final long DEFAULT_TIME_INTERVAL = 250;
    private static long sTimeInterval = DEFAULT_TIME_INTERVAL;
    private static final long TIME_INTERVAL_TOLERANT = 50;
    private static long sTimeUP;
    private static int sTouchState = 3;
    protected GlobalRoamResizeTransformer mGlobalRoamResizeTransformer;
    PanelManager mPanelManager;
    boolean mIsOpenGLEnabled = BuildConfig.enable_opengl;
    List<Material> materialList;

    private OnTransformListener mOnGlobalTransformListener = new OnTransformListener() {
        @Override public boolean onContinueTransform(Matrix matrix)
        {
            float[] values = new float[9];
            matrix.getValues(values);
            float finalSize = mPanelManager.getScaleRatio() * values[0];
            if (finalSize < Constants.MIN_SCALE_SIZE)
            {
                return false;
            }
            else if (finalSize > Constants.MAX_SCALE_SIZE)
            {
                return false;
            }

            mPanelManager.zoomPanelView(finalSize);

            List<Material> list = mPanelManager.getMaterials();
            int size = list.size();
            for (int i = 0; i < size; i++)
            {
                list.get(i).continueTransform(matrix);
            }

            if (mIsOpenGLEnabled)
            {
                mPanelManager.requestOpenGLRender(true);
                return true;
            }

            mPanelManager.repaintToOffScreenCanvas(null, true);

            return true;
        }

        @Override public void onEndTransform(Matrix matrix)
        {
            BaseUtils.dbg(TAG, "onEndTransform ");
            float[] values = new float[9];
            matrix.getValues(values);

            List<Material> list = mPanelManager.getMaterials();
            int size = list.size();
//            int size = materialList.size();
//            BaseUtils.dbg(TAG, "size = " + size + " mIsOpenGLEnabled=" + (mIsOpenGLEnabled?"true":"false"));
            for (int i = 0; i < size; i++)
            {
                Material m = list.get(i);
//                Material m = materialList.get(i);
                if (m instanceof PencilInk)
                {
                    ((PencilInk) m).endTransform(matrix);
                }
                if (m instanceof ShapeMaterial)
                {
                    ((ShapeMaterial) m).endTransform(matrix);
                }
            }

            if (mIsOpenGLEnabled)
            {
                /**此处直接进行缓存和显示区的重绘，再清空OpenGL画布，避免闪烁*/
                mPanelManager.repaintToOffScreenCanvas(null, false);
                mPanelManager.repaintScreen(null);

                mPanelManager.requestOpenGLRender(false);
            }

            mPanelManager.addAction(new PanelTransformAction(matrix));
        }
    };

    public GlobalTransformState(PanelManager manager)
    {
        mPanelManager = manager;
        mIsOpenGLEnabled = BuildConfig.enable_opengl;
        this.mGlobalRoamResizeTransformer = new GlobalRoamResizeTransformer(this.mOnGlobalTransformListener);
    }


    public void onTouchEvent(MotionEvent event)
    {
        if (System.currentTimeMillis() - sTimeUP >= sTimeInterval)
        {
            int action = event.getActionMasked();
//            BaseUtils.dbg(TAG, "sTouchState = " + sTouchState + " action=" + action + " mIsOpenGLEnabled=" + mIsOpenGLEnabled);
            if (mIsOpenGLEnabled && ((action == 0 || action == 5) && sTouchState != 0))
            {
                this.mPanelManager.requestOpenGLRender(true);
                this.mPanelManager.drawBackgroundOnly();
                this.mPanelManager.showOpenGL();
            }
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
            {
                sTouchState = 0;
                if (materialList == null || materialList.size() == 0)
                {
                    materialList = mPanelManager.getMaterials();
                    BaseUtils.dbg(TAG, "初始化list: list.zize = " + materialList.size());
                }
            }
            if (sTouchState != 0)
            {
                return;
            }
            if (action == 1 || action == 3)
            {
                sTouchState = 3;
//                long startTime = System.currentTimeMillis();

                /**希沃中选中缩放和全页面缩放都会传入BaseSelectorState，此处用于处理选中框*/
//                this.mStateCallback.setImageEditState(Mode.NONE);

                this.mGlobalRoamResizeTransformer.onTouch(event);
                sTimeUP = System.currentTimeMillis();
                return;
            }
            this.mGlobalRoamResizeTransformer.onTouch(event);
        }
    }

    private boolean isCanRoam(Matrix matrix)
    {
        if (!isValidMatrix(matrix))
        {
            return false;
        }
        float[] values = new float[9];
        matrix.getValues(values);
        Matrix matrix1 = new Matrix(this.mGlobalRoamResizeTransformer.getFinalMatrix());
        matrix1.postScale(values[0], values[4]);
        matrix1.getValues(values);
        float newScaleRatio = mPanelManager.getScaleRatio() * values[0];
        if (((double) newScaleRatio) >= 0.995d && ((double) newScaleRatio) <= 1.005d)
        {
            return true;
        }
        else if (newScaleRatio > Constants.MAX_SCALE_SIZE || newScaleRatio < Constants.MIN_SCALE_SIZE)
        {
            return false;
        }
        else
        {

            return true;
        }
    }

    public boolean isValidMatrix(Matrix matrix)
    {
        float[] values = new float[9];
        matrix.getValues(values);
        for (float value : values)
        {
            if (Float.isInfinite(value) || Float.isNaN(value))
            {
                return false;
            }
        }
        return true;
    }
}
