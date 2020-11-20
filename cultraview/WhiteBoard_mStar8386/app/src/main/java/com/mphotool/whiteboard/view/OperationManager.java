package com.mphotool.whiteboard.view;

import com.mphotool.whiteboard.action.Action;
import com.mphotool.whiteboard.utils.Constants;

import java.util.Stack;

/**
 * Created by Dong.Daoping on 2018/4/25
 * 说明：负责对Action的管理
 * Page管理涉及的模块较多，后续加入管理
 */
public class OperationManager {
    private Stack<Action> mRedoCommandsStack = new Stack();
    private Stack<Action> mUndoCommandsStack = new Stack();

    private static OperationManager mInstance;

    PanelManager mPanelManager;

    private OperationManager(PanelManager panelManager)
    {
        mPanelManager = panelManager;
    }

    public static OperationManager getInstance(PanelManager panelManager)
    {
        if (mInstance == null)
        {
            mInstance = new OperationManager(panelManager);
        }
        return mInstance;
    }

    public boolean canUndo()
    {
        return this.mUndoCommandsStack.size() > 0;
    }

    public boolean canRedo()
    {
        return this.mRedoCommandsStack.size() > 0;
    }

    public Action preUndo() {
        if (canUndo()) {
            return (Action) this.mUndoCommandsStack.pop();
        }
        return null;
    }

    public void undo(Action pCommand, PanelManager manager) {
        if (pCommand != null) {
            pCommand.undo(manager);
            this.mRedoCommandsStack.add(pCommand);
        }
    }

    public Action preRedo() {
        if (canRedo()) {
            return (Action) this.mRedoCommandsStack.pop();
        }
        return null;
    }

    public void redo(Action pCommand, PanelManager manager) {
        if (pCommand != null) {
            pCommand.redo(manager);
            this.mUndoCommandsStack.add(pCommand);
        }
    }

    /**
     * 加入新action时，current有可能在redo或undo时指向中间位置，此时加入新action，需要保证新action为最后一个
     */
    public void addAction(Action action)
    {

        mUndoCommandsStack.add(action);
        if (this.mUndoCommandsStack.size() > Constants.MAX_UNDO_COUNT) {
            this.mUndoCommandsStack.remove(0);
        }
        mRedoCommandsStack.clear();
    }

    public void release()
    {
        clearActions();
    }

    public void clearActions()
    {
        this.mUndoCommandsStack.clear();
        this.mRedoCommandsStack.clear();
    }

}
