package com.ctv.annotation.element;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;

import com.ctv.annotation.entity.Storable;
import com.ctv.annotation.utils.BaseUtils;
import com.ctv.annotation.utils.Constants;
import com.ctv.annotation.view.PanelManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public abstract class Material implements Serializable, Storable {
    private static final long serialVersionUID = Constants.serialVersionUID;

    public static int id = 0;
    public int mId = -1;
    transient PanelManager mManager = null;
    private boolean mSelected = false;

    public abstract void draw(Canvas canvas);
    public abstract Rect rect();

    public void setId(int id) {
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }

    public void setPanelManager(PanelManager panelManager) {
        this.mManager = panelManager;
    }

    public boolean intersect(Rect dst) {
        return false;
    }

    public boolean canDrawForScreenShot() {
        return true;
    }

    public boolean isCross(float x1, float y1, float x2, float y2) {
        return false;
    }

    public boolean isInRegion(PanelManager manager, Region region) {
        return false;
    }

    public void setSelected(boolean s) {
        this.mSelected = s;
    }

    public boolean isSelected() {
        return this.mSelected;
    }

    /**使用希沃算法之后，直接传入Matrix进行重绘*/
    public abstract void continueTransform(Matrix matrix);

    public abstract void endTransform(Matrix matrix);

    public abstract void transform(Matrix matrix);

    /**
     * 验证有效性
     * @return
     */
    public boolean isValid() {
        return true;
    }

    public boolean writeObject(OutputStream out) throws IOException {
        out.write(BaseUtils.intToByteArray(20171226));
        out.write(BaseUtils.intToByteArray(getId()));
        return true;
    }

    public boolean readObject(InputStream in) throws IOException, ClassNotFoundException
    {
        byte[] buf = new byte[4];
        int ret = in.read(buf);
        setId(BaseUtils.readInputStreamInt(in, buf));
        return true;
    }

    public String toString() {
        return super.toString() + ",id=" + getId();
    }
}
