package com.mphotool.whiteboard.action;

import android.graphics.Matrix;
import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.PencilInk;
import com.mphotool.whiteboard.view.PanelManager;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Dong.Daoping on 2018/5/7 0007
 * 说明：页面缩放事件
 */
public class PanelTransformAction extends Action {

    private Matrix mMatrix;

    public PanelTransformAction(Matrix matrix)
    {
        mMatrix = matrix;
    }

    @Override
    public void linkMaterial(List<Material> list)
    {

    }

    @Override
    public boolean onTouch(PanelManager panelManager, MotionEvent motionEvent)
    {
        return false;
    }

    @Override
    public void undo(PanelManager panelManager)
    {
        super.redo(panelManager);

        Matrix matrix = new Matrix();
        if (this.mMatrix.invert(matrix)) {
            List<Material> list = panelManager.getMaterials();
            Iterator i$ = list.iterator();
            while (i$.hasNext()) {
                Material shape = (Material) i$.next();
                if (shape != null) {
                    int index = list.indexOf(shape);
                    if (index != -1) {
                        if(shape instanceof PencilInk){
                            ((PencilInk)shape).resetWidth = true;
                        }
                        list.get(index).transform(matrix);
                    }
                }
            }

            float[] matrixValue = new float[9];
            this.mMatrix.getValues(matrixValue);
            panelManager.setScaleRatio(1.0f / matrixValue[0]);

        }
    }

    @Override
    public void redo(PanelManager panelManager)
    {
        super.undo(panelManager);

        List<Material> list = panelManager.getMaterials();
        Iterator i$ = list.iterator();
        while (i$.hasNext()) {
            Material shape = (Material) i$.next();
            if (shape != null) {
                int index = list.indexOf(shape);
                if (index != -1) {
                    if(shape instanceof PencilInk){
                        ((PencilInk)shape).resetWidth = true;
                    }
                    list.get(index).transform(mMatrix);
                }
            }
        }

        float[] matrixValue = new float[9];
        this.mMatrix.getValues(matrixValue);
        panelManager.setScaleRatio(matrixValue[0]);

//        panelManager.resetMaterialList(mMaterials);
//        Log.d("PanelTransformAction", "after undo -- mMaterials.size = " + mMaterials.size() + "  ,  manager.size = " + panelManager.getMaterials().size());
    }
}
