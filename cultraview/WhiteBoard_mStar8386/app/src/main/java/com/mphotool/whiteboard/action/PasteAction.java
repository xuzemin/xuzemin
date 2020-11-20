package com.mphotool.whiteboard.action;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.MotionEvent;

import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.Selector;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.ToofifiLog;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 复制粘贴
 * 参考宝沃的复制粘贴，只对选中的内容进行一次复制和粘贴，并未粘贴后自动被选中
 */
public class PasteAction extends Action {

    String TAG = "PasteAction";

    private List<Integer> mAfterMaterialIdList = null;
    ArrayList<Material> mAfterMaterials = new ArrayList();
    private List<Integer> mBeforeMaterialIdList = null;
    ArrayList<Material> mBeforeMaterials = new ArrayList();

    public PasteAction() {

    }

    public List<Material> doPaste(PanelManager panelManager, List<Material> materials){
        Exception e;
        List<Material> newList = new ArrayList<Material>();
        Matrix pasteMatrix = new Matrix();
        pasteMatrix.postTranslate(150.0f, 150.0f);
        pasteMatrix.postScale(1.0f,1.0f);

        this.mBeforeMaterials.addAll(panelManager.getMaterials());
        for (Material oldMaterial : materials) {
            if(oldMaterial instanceof Selector){
                continue;
            }
            try {
                ByteArrayOutputStream out2 = new ByteArrayOutputStream();
                try {
                    oldMaterial.writeObject(out2);
                    Material newMaterial = oldMaterial.getClass().newInstance();
                    newMaterial.setPanelManager(panelManager);
                    ByteArrayInputStream in = new ByteArrayInputStream(out2.toByteArray());
                    newMaterial.readObject(in);
                    oldMaterial.setSelected(false);

                    Rect rect = newMaterial.rect();
                    if (!rect.isEmpty()) {
                        newMaterial.transform(pasteMatrix);
                        panelManager.addMaterial(newMaterial);
                        newMaterial.setPanelManager(panelManager);
                        newMaterial.setSelected(true);
                        newList.add(newMaterial);
                        out2.close();
                        in.close();
                    }
                } catch (Exception e2) {
                    e = e2;
                    ToofifiLog.e(TAG,"PasteAction writeObject: " + BaseUtils.getStackTrace(e));
                }
            } catch (Exception e3) {
                e = e3;
                ToofifiLog.e(TAG,"PasteAction writeObject: " + BaseUtils.getStackTrace(e));
            }
        }
        this.mAfterMaterials.addAll(panelManager.getMaterials());
        return newList;
    }

    public void undo(PanelManager manager) {
        super.undo(manager);
        manager.getMaterials().clear();
        manager.getMaterials().addAll(this.mBeforeMaterials);
    }

    public void redo(PanelManager manager) {
        super.redo(manager);
        manager.getMaterials().clear();
        manager.getMaterials().addAll(this.mAfterMaterials);
    }

    public boolean onTouch(PanelManager manager, MotionEvent event) {
        return false;
    }

    public boolean readObject(InputStream in) throws IOException
    {
        super.readObject(in);
        byte[] buf = new byte[4];
        this.mBeforeMaterialIdList = new ArrayList();
        this.mAfterMaterialIdList = new ArrayList();
        if (in.read(buf) != 4) {
            return false;
        }
        int i;
        int size = BaseUtils.byteArrayToInt(buf);
        for (i = 0; i < size; i++) {
            if (in.read(buf) != 4) {
                return false;
            }
            this.mBeforeMaterialIdList.add(Integer.valueOf(BaseUtils.byteArrayToInt(buf)));
        }
        if (in.read(buf) != 4) {
            return false;
        }
        size = BaseUtils.byteArrayToInt(buf);
        for (i = 0; i < size; i++) {
            if (in.read(buf) != 4) {
                return false;
            }
            this.mAfterMaterialIdList.add(Integer.valueOf(BaseUtils.byteArrayToInt(buf)));
        }
        return true;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        int i;
        super.writeObject(out);
        int size = this.mBeforeMaterials.size();
        out.write(BaseUtils.intToByteArray(size));
        for (i = 0; i < size; i++) {
            out.write(BaseUtils.intToByteArray(((Material) this.mBeforeMaterials.get(i)).getId()));
        }
        size = this.mAfterMaterials.size();
        out.write(BaseUtils.intToByteArray(size));
        for (i = 0; i < size; i++) {
            out.write(BaseUtils.intToByteArray(((Material) this.mAfterMaterials.get(i)).getId()));
        }
        return true;
    }

    public List<Material> getAllMaterials() {
        List<Material> ms = new ArrayList();
        ms.addAll(this.mAfterMaterials);
        Iterator it = this.mBeforeMaterials.iterator();
        while (it.hasNext()) {
            Material m = (Material) it.next();
            if (!ms.contains(m)) {
                ms.add(m);
            }
        }
        return ms;
    }

    public void linkMaterial(List<Material> lists) {
        for (Integer i : this.mBeforeMaterialIdList) {
            if (i.intValue() >= 0 && i.intValue() < lists.size()) {
                this.mBeforeMaterials.add(lists.get(i.intValue()));
            }
        }
        for (Integer i2 : this.mAfterMaterialIdList) {
            if (i2.intValue() >= 0 && i2.intValue() < lists.size()) {
                this.mAfterMaterials.add(lists.get(i2.intValue()));
            }
        }
    }
}
