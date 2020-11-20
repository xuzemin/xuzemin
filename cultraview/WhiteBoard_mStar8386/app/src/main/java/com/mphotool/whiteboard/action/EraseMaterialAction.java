package com.mphotool.whiteboard.action;

import android.graphics.Rect;
import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.EraseMaterial;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.entity.Storable;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.PanelUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class EraseMaterialAction extends Action {
    private EraseMaterial mErase = null;
    private boolean mHasErase = false;
    private float mLastX;
    private float mLastY;
//
//    ArrayList<Material> mAfterMaterials = new ArrayList();
//    ArrayList<Material> mBeforeMaterials = new ArrayList();

    private Rect mPathRect = new Rect();

    public static class EventData implements Storable {
        public int action;
        public int id;
        public float pressure;
        public float size;
        public long time;
        public float touchMajor;
        public float touchMinor;
        public float x;
        public float y;

        public EventData() {}

        public EventData(int action, float x, float y)
        {
            this.action = action;
            this.x = x;
            this.y = y;
        }

        public String toString()
        {
            return "time=" + this.time + ",id=" + this.id + ",action=" + this.action + ",x" + this.x + ",y=" + this.y + ",pressure=" + this.pressure + ",size=" + this.size;
        }

        public boolean writeObject(OutputStream out) throws IOException
        {
            out.write(BaseUtils.intToByteArray(0));
            out.write(BaseUtils.intToByteArray(this.id));
            out.write(BaseUtils.intToByteArray(this.action));
            out.write(BaseUtils.longToByteArray(this.time));
            out.write(BaseUtils.floatToByte(this.x));
            out.write(BaseUtils.floatToByte(this.y));
            out.write(BaseUtils.floatToByte(this.pressure));
            out.write(BaseUtils.floatToByte(this.size));
            out.write(BaseUtils.floatToByte(this.touchMajor));
            out.write(BaseUtils.floatToByte(this.touchMinor));
            return true;
        }

        public boolean readObject(InputStream in) throws IOException
        {
            byte[] buf = new byte[4];
            in.read(buf);
            this.id = BaseUtils.readInputStreamInt(in, buf);
            this.action = BaseUtils.readInputStreamInt(in, buf);
            this.time = BaseUtils.readInputStreamLong(in);
            this.x = BaseUtils.readInputStreamFloat(in, buf);
            this.y = BaseUtils.readInputStreamFloat(in, buf);
            this.pressure = BaseUtils.readInputStreamFloat(in, buf);
            this.size = BaseUtils.readInputStreamFloat(in, buf);
            this.touchMajor = BaseUtils.readInputStreamFloat(in, buf);
            this.touchMinor = BaseUtils.readInputStreamFloat(in, buf);
            return false;
        }
    }

    public void undo(PanelManager manager)
    {
        super.undo(manager);
        manager.getMaterials().addAll(materialList);
//        manager.getMaterials().addAll(this.mBeforeMaterials);
    }

    public void redo(PanelManager manager)
    {
        super.redo(manager);
//        manager.getMaterials().clear();
        manager.getMaterials().removeAll(materialList);
    }

    public EraseMaterial getErase()
    {
        return this.mErase;
    }

    @Override public void linkMaterial(List<Material> list)
    {

    }

    public boolean onTouch(PanelManager manager, MotionEvent event)
    {
        List<Material> materials = manager.getMaterials();
        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();

        float dx = x;
        float dy = y;
//        float dx = manager.toImagePosX(x);
//        float dy = manager.toImagePosY(y);
        if (this.mErase == null)
        {
            this.mErase = new EraseMaterial(manager);
        }
        if (action == 0)
        {
            this.mLastX = dx;
            this.mLastY = dy;
//            this.mBeforeMaterials.addAll(manager.getMaterials());
            manager.addMaterial(this.mErase);
            this.mErase.addPoint(x, y, event.getActionMasked());
            mPathRect.setEmpty();
            mPathRect.set((int) x, (int) y, (int) x + 1, (int) y - 1);
            mPathRect.union((int) x, (int) y);
            return true;
        }
        this.mErase.addPoint(x, y, event.getActionMasked());
        this.mErase.drawPoint(manager, x, y);
        mPathRect.union((int) x, (int) y);
        int materialSize = materials.size();
        Rect actionRect = PanelUtils.buildRect((int) this.mLastX, (int) this.mLastY, (int) dx, (int) dy);
        for (int mindex = materialSize - 1; mindex >= 0; mindex--)
        {
            Material material = (Material) materials.get(mindex);
//            if (material.isValid())
//            {
                if ((material.intersect(actionRect) && material.isCross(this.mLastX, this.mLastY, dx, dy)) || mPathRect.contains(material.rect()))
                {
                    this.materialList.add(material);
                    material.setSelected(true);

                    material.draw(manager.getCacheCanvas());
                    manager.drawScreen(material.rect());
                    this.mHasErase = true;
                }
//            }
        }
        if (action == MotionEvent.ACTION_UP)
        {
            Iterator it = this.materialList.iterator();
            while (it.hasNext())
            {
                Material m = (Material) it.next();
                manager.removeMaterial(m);
                m.setSelected(false);
            }
            if (this.mErase.eraseMaterials(manager) > 0)
            {
                this.mHasErase = true;
            }
            manager.removeMaterial(this.mErase);
            manager.repaintToOffScreenCanvas(null, true);
            if (this.mHasErase)
            {
                manager.addAction(this);
//                this.mAfterMaterials.addAll(manager.getMaterials());
            }
        }
        this.mLastX = dx;
        this.mLastY = dy;
        return true;
    }
}
