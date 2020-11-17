package com.ctv.annotation.action;

import android.view.MotionEvent;

import com.ctv.annotation.element.Material;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.view.PanelManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Action implements Serializable {
    private static final long serialVersionUID = Constants.serialVersionUID;
    private int mId = -1;

    protected List<Integer> mMaterialIdList = new ArrayList();
    public List<Material> materialList = new ArrayList();
    public long time = 0;

    public abstract void linkMaterial(List<Material> list);

    public abstract boolean onTouch(PanelManager panelManager, MotionEvent motionEvent);

    public void redo(PanelManager panelManager){
    }

    public void undo(PanelManager panelManager){
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }

    public void setTime(long t) {
        this.time = t;
    }

    public long getTime() {
        return this.time;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        out.write(BaseUtils.longToByteArray(serialVersionUID));
        out.write(BaseUtils.intToByteArray(getId()));
        int size = this.materialList.size();
        out.write(BaseUtils.intToByteArray(size));
        for (int i = 0; i < size; i++) {
            Material material = (Material) this.materialList.get(i);
            if (material != null) {
                out.write(BaseUtils.intToByteArray(material.getId()));
            }
        }
        out.write(BaseUtils.longToByteArray(this.time));
        return true;
    }

    public boolean readObject(InputStream in) throws IOException
    {
        byte[] buf = new byte[4];
        int read = in.read(buf);
        if (in.read(buf) != 4) {
            return false;
        }
        setId(BaseUtils.byteArrayToInt(buf));
        if (in.read(buf) != 4) {
            return false;
        }
        int size = BaseUtils.byteArrayToInt(buf);
        for (int i = 0; i < size; i++) {
            if (in.read(buf) != 4) {
                return false;
            }
            this.mMaterialIdList.add(Integer.valueOf(BaseUtils.byteArrayToInt(buf)));
        }
        this.time = BaseUtils.readInputStreamLong(in);
        return true;
    }

    public List<Material> getAllMaterials() {
        return this.materialList;
    }

    public String toString() {
        return super.toString() + ",id=" + getId();
    }

    public static boolean onTouch(Action action, PanelManager manager, MotionEvent event) {
        return action.onTouch(manager, event);
    }
}
