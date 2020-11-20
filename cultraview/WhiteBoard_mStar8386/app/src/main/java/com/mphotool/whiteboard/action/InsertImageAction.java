package com.mphotool.whiteboard.action;

import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.Image;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.ToofifiLog;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class InsertImageAction extends Action {
    private Image mImage = null;
    private int mIndex = 0;

    public InsertImageAction(Image image) {
        this.mImage = image;
    }

    public void undo(PanelManager manager) {
        super.undo(manager);
        if (this.mImage != null) {
            manager.removeMaterial(this.mImage);
        }
    }

    public void redo(PanelManager manager) {
        super.redo(manager);
        if (this.mImage != null) {
            manager.addMaterial(this.mImage);
        }
    }

    public boolean onTouch(PanelManager manager, MotionEvent event) {
        return false;
    }

    public List<Material> getAllMaterials() {
        List<Material> ms = new ArrayList();
        ms.add(this.mImage);
        return ms;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        this.materialList.add(this.mImage);
        return super.writeObject(out);
    }

    public void linkMaterial(List<Material> lists) {
        if (this.mMaterialIdList.size() == 1) {
            try {
                this.mImage = (Image) lists.get(((Integer) this.mMaterialIdList.get(0)).intValue());
                return;
            } catch (ClassCastException e) {
                ToofifiLog.e(Constants.TAG, "InsertImageAction: not Image");
                return;
            }
        }
        ToofifiLog.e(Constants.TAG,"InsertImageAction: list not 1, is " + this.mMaterialIdList.size());
    }
}
