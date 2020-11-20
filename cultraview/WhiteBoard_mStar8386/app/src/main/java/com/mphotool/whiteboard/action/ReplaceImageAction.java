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

/**
 * 图片剪裁之后，新图片替换原有图片，本类用于记录替换操作
 */
public class ReplaceImageAction extends Action {
    private Image mOldImage = null;
    private Image mNewImage = null;
    private int mIndex = 0;

    public ReplaceImageAction(Image oldImage, Image newImage)
    {
        oldImage.setSelected(false);
        newImage.setSelected(false);
        mOldImage = oldImage;
        mNewImage = newImage;
    }

    public void undo(PanelManager manager)
    {
        super.undo(manager);
        if (mOldImage != null && mNewImage != null)
        {
//            mIndex = manager.getMaterials().indexOf(mNewImage);
//            if (mIndex < 0 || mIndex >= manager.getMaterials().size())
//            {
//                return;
//            }
            manager.removeMaterial(mNewImage);
            manager.addMaterial(mOldImage);
            mOldImage.mNeedRebindTexture = true;
//            manager.getMaterials().add(mIndex, mOldImage);
        }
    }

    public void redo(PanelManager manager)
    {
        super.redo(manager);
        if (mOldImage != null && mNewImage != null)
        {
//            mIndex = manager.getMaterials().indexOf(mOldImage);
//            if (mIndex < 0 || mIndex >= manager.getMaterials().size())
//            {
//                return;
//            }
            manager.removeMaterial(mOldImage);
            manager.addMaterial(mNewImage);
            mNewImage.mNeedRebindTexture = true;
//            manager.getMaterials().add(mIndex, mNewImage);
        }
    }

    public boolean onTouch(PanelManager manager, MotionEvent event)
    {
        return false;
    }

    public List<Material> getAllMaterials()
    {
        List<Material> ms = new ArrayList();
        ms.add(mOldImage);
        ms.add(mNewImage);
        return ms;
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
//        materialList.add(mImage);
        return super.writeObject(out);
    }

    public void linkMaterial(List<Material> lists)
    {
//        if (mMaterialIdList.size() == 1)
//        {
//            try
//            {
//                mImage = (Image) lists.get(((Integer) mMaterialIdList.get(0)).intValue());
//                return;
//            }
//            catch (ClassCastException e)
//            {
//                ToofifiLog.e(Constants.TAG, "InsertImageAction: not Image");
//                return;
//            }
//        }
        ToofifiLog.e(Constants.TAG, "InsertImageAction: list not 1, is " + mMaterialIdList.size());
    }
}
