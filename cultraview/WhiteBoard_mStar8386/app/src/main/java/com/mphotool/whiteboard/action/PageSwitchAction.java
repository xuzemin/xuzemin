package com.mphotool.whiteboard.action;

import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.util.List;

/**
 * Created by Dong.Daoping on 2018/5/7 0007
 * 说明：页面切换事件
 */
public class PageSwitchAction extends Action {

    private int mPreIndex;
    private int mCurIndex;

    public PageSwitchAction(int preIndex, int curIndex)
    {
        mPreIndex = preIndex;
        mCurIndex = curIndex;
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
    public void redo(PanelManager panelManager)
    {
        super.redo(panelManager);
        panelManager.setPage(mCurIndex);
    }

    @Override
    public void undo(PanelManager panelManager)
    {
        super.undo(panelManager);
        panelManager.setPage(mPreIndex);
    }

}
