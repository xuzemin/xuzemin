package com.mphotool.whiteboard.action;

import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.view.PanelManager;

import java.util.List;

public class DeleteAction extends Action {
    private static final long serialVersionUID = Constants.serialVersionUID;;

    public void undo(PanelManager manager) {
        super.undo(manager);
        manager.getMaterials().addAll(materialList);
    }

    public void redo(PanelManager manager) {
        super.redo(manager);
        manager.getMaterials().removeAll(materialList);
    }

    public boolean onTouch(PanelManager manager, MotionEvent event) {
        return false;
    }

    public void linkMaterial(List<Material> lists) {
        for (Integer i : this.mMaterialIdList) {
            if (i.intValue() >= 0 && i.intValue() < lists.size()) {
                this.materialList.add(lists.get(i.intValue()));
            }
        }
    }
}
