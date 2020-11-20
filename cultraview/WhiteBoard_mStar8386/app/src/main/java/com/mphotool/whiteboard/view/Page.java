package com.mphotool.whiteboard.view;

import com.mphotool.whiteboard.elements.Image;
import com.mphotool.whiteboard.elements.LineMaterial;
import com.mphotool.whiteboard.elements.Material;
import com.mphotool.whiteboard.elements.Selector;
import com.mphotool.whiteboard.elements.ShapeMaterial;
import com.mphotool.whiteboard.entity.Storable;
import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.ToofifiLog;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Dong.Daoping on 2018/4/20 0020
 * 说明：
 */
public class Page implements Serializable, Storable {
    public static final String TAG = "Page";
    public final Object SHAPE_LOCK;

    PanelManager panelManager;

    /**对元素进行管理，实现重绘等操作*/
    List<Material> materialList = Collections.synchronizedList(new ArrayList<Material>());
    private float mScaleRatio = 1.0f;
    public int mImageCount = 0; //用于记录图片插入时的下标

    public Page(PanelManager manager)
    {
        SHAPE_LOCK = new Object();
        panelManager = manager;
    }

    public float getScaleRatio()
    {
        return this.mScaleRatio;
    }

    public void setScaleRatio(float ratio)
    {
        this.mScaleRatio = ratio;
    }

    public String toString()
    {
        String str = "" + "Materials:\n";
        for (Material m : materialList)
        {
            str = str + "# " + m + "\n";
        }
        str = str + "Actions:\n";
        return str;
    }

    public boolean writeObject(OutputStream out)
    {
        try
        {
            out.write(PanelManager.NOTE_NCC_FLAG);

            int size = materialList.size();
            BaseUtils.dbg(TAG, "writeObject materialList.size=" + size);
            out.write(BaseUtils.intToByteArray(size));

            int i;
            for (i = 0; i < size; i++) {
                Material m = (Material) materialList.get(i);
                if (m != null && !(m instanceof Selector)) {
                    m.setId(i);
                    BaseUtils.dbg(TAG, "writeObject m.getClass().getName()=" + m.getClass().getName().toString());
                    BaseUtils.writeOutputStreamString(out, m.getClass().getName().toString());
                    try
                    {
                        m.writeObject(out);
                    }catch (IOException e){
                        continue;
                    }
                }
            }

            int bgColor = panelManager.getBoardBackgroundColor();
            out.write(BaseUtils.intToByteArray(bgColor));
            return true;
        }
        catch (IOException e)
        {
            return false;
        }
    }

    public boolean readObject(InputStream in)
    {
        String s;
        byte[] buf = new byte[4];
        try
        {
            byte[] flag = new byte[PanelManager.NOTE_NCC_FLAG.length];
            in.read(flag);
            if (compareBytes(PanelManager.NOTE_NCC_FLAG, flag))
            {
                int size = BaseUtils.readInputStreamInt(in, buf);
                List<Material> allMaterials = new ArrayList();
                int i = 0;
                while (i < size)
                {
                    int length = BaseUtils.readInputStreamInt(in, buf);
                    if (length < 0 || length > 1024)
                    {
                        BaseUtils.dbg(TAG, "wrong length: " + length);
                        i++;
                        continue;
                    }
                    byte[] str = new byte[length];
                    if (in.read(str) != length)
                    {
                        BaseUtils.dbg(TAG, "read class failed");
                        i++;
                        continue;
                    }
                    s = new String(str);
                    try
                    {
                        Material m = (Material) Class.forName(s).newInstance();
                        m.setPanelManager(panelManager);
                        m.readObject(in);
                        allMaterials.add(m);
                        if(m instanceof Image){
                            mImageCount ++;
                            ((Image)m).updateImageVertex(((Image) m).getActualRectF());
                        }
                        i++;
                    }
                    catch (Exception e)
                    {
                        BaseUtils.dbg(TAG, "new instance " + s + ": " + BaseUtils.getStackTrace(e));
                        i++;
                        continue;
                    }
                }
                materialList = allMaterials;
                int bgColor = BaseUtils.readInputStreamInt(in, buf);
                panelManager.setBoardBgBackground(bgColor);
                panelManager.UpdateBoardBackground(true);
                return true;
            }
            BaseUtils.dbg(TAG, "Not a ncc format");
            return false;
        }
        catch (IOException e3)
        {
            ToofifiLog.e(TAG, "read material failed:" + BaseUtils.getStackTrace(e3));
            return false;
        }
    }

    public boolean readPreviewObject(InputStream in)
    {
        String s;
//        BaseUtils.dbg(TAG, "readPreviewObject");
        byte[] buf = new byte[4];
        try
        {
            byte[] flag = new byte[PanelManager.NOTE_NCC_FLAG.length];
            in.read(flag);
            if (compareBytes(PanelManager.NOTE_NCC_FLAG, flag))
            {
                int size = BaseUtils.readInputStreamInt(in, buf);
                List<Material> allMaterials = new ArrayList();
                int i = 0;
                while (i < size)
                {
                    int length = BaseUtils.readInputStreamInt(in, buf);
                    BaseUtils.dbg(TAG, " length: " + length);
                    if (length < 0 || length > 1024)
                    {
                        BaseUtils.dbg(TAG, "wrong length: " + length);
                        i++;
                        continue;
                    }
                    byte[] str = new byte[length];
                    if (in.read(str) != length)
                    {
                        BaseUtils.dbg(TAG, "read class failed");
                        i++;
                        continue;
                    }
                    s = new String(str);
                    try
                    {
                        BaseUtils.dbg(TAG, "readPreviewObject s=" + s);
                        Material m = (Material) Class.forName(s).newInstance();
                        m.setPanelManager(panelManager);
                        m.readObject(in);
                        allMaterials.add(m);
                        if(m instanceof Image){
                            mImageCount ++;
                            ((Image)m).updateImageVertex(((Image) m).getActualRectF());
                        }
                        i++;
                    }
                    catch (Exception e)
                    {
                        BaseUtils.dbg(TAG, "new instance " + s + ": " + BaseUtils.getStackTrace(e));
                        i++;
                        continue;
                    }
                }
                materialList = allMaterials;
                BaseUtils.readInputStreamInt(in, buf);
//                panelManager.setBoardBgBackground(bgColor);
//                panelManager.UpdateBoardBackground(false);
                return true;
            }
            BaseUtils.dbg(TAG, "Not a ncc format");
            return false;
        }
        catch (IOException e3)
        {
            BaseUtils.dbg(TAG, "read material failed:" + BaseUtils.getStackTrace(e3));
            return false;
        }
    }

    private boolean compareBytes(byte[] a, byte[] b)
    {
        if (a.length != b.length)
        {
            return false;
        }
        int length = a.length;
        for (int i = 0; i < length; i++)
        {
            if (a[i] != b[i])
            {
                return false;
            }
        }
        return true;
    }

    public void release(){
        if(materialList != null && materialList.size() > 0){
            for(Material temp : materialList){
                if(temp instanceof Image){
                    ((Image) temp).release();
                }
                temp = null;
            }
        }
        materialList.clear();
        materialList = null;
    }
}
