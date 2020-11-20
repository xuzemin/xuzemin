package com.mphotool.whiteboard.action;

import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.view.PanelManager;

import java.util.List;

public class CleanAction extends Action {
    public CleanAction(PanelManager manager)
    {
    }

    public void doClean(PanelManager manager, Material object)
    {
        this.materialList.addAll(manager.getMaterials());
        manager.getMaterials().clear();
    }

    @Override
    public void undo(PanelManager manager)
    {
        super.undo(manager);
        manager.resetMaterialList(materialList);
//        Iterator it = this.materialList.iterator();
//        while (it.hasNext())
//        {
//            manager.addMaterial((Material) it.next());
//        }
    }

    public void redo(PanelManager manager)
    {
        super.redo(manager);
        doClean(manager, null);
    }

    public boolean onTouch(PanelManager manager, MotionEvent event)
    {
        return false;
    }

    public void linkMaterial(List<Material> lists)
    {
        for (Integer i : this.mMaterialIdList)
        {
            if (i.intValue() >= 0 && i.intValue() < lists.size())
            {
                this.materialList.add(lists.get(i.intValue()));
            }
        }
    }
}
