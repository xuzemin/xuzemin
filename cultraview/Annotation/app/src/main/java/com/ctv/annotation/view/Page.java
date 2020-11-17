package com.ctv.annotation.view;

import com.ctv.annotation.element.Image;
import com.ctv.annotation.element.Material;
import com.ctv.annotation.entity.Storable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Page implements Serializable, Storable {
    public final Object SHAPE_LOCK;
    private float mScaleRatio = 1.0f;
    PanelManager panelManager;
    /**对元素进行管理，实现重绘等操作*/
    List<Material> materialList = Collections.synchronizedList(new ArrayList<Material>());

    public Page(PanelManager manager)
    {
        SHAPE_LOCK = new Object();
        panelManager = manager;
    }


    public float getScaleRatio()
    {
        return this.mScaleRatio;
    }
    @Override
    public boolean readObject(InputStream inputStream) throws IOException, ClassNotFoundException {
        return false;
    }

    @Override
    public boolean writeObject(OutputStream outputStream) throws IOException {
        return false;
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
