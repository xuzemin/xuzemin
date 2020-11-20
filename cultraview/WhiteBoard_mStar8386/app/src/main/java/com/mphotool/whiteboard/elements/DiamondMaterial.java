package com.mphotool.whiteboard.elements;

import com.mphotool.whiteboard.utils.PathUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.Serializable;

/**
 * @Description: 菱形
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class DiamondMaterial extends ShapeMaterial implements Serializable {
    private static final String TAG = DiamondMaterial.class.getSimpleName();

    public DiamondMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
        super(manager, mBrushColor, mBrushThickness);
    }

    @Override
    protected void initInk(int color, float thickness) {
        mInk = new Ink();
        mInk.mInkColor = color;
        mInk.mThickness = thickness;
        mInk.mPoints = new Point[4];
        mInk.mPointCount = 4;

        for (int i = 0; i < mInk.mPointCount; i ++){
            mInk.mPoints[i] = new Point();
        }
    }

    @Override
    protected void handlePath() {
        // path形状为箭头
        this.mPath.reset();
        mInk.mPoints[0].mX = startX + ((midX - startX) / 2.0f);
        mInk.mPoints[0].mY = startY;

        mInk.mPoints[1].mX = midX;
        mInk.mPoints[1].mY = startY + ((midY - startY) / 2.0f);

        mInk.mPoints[2].mX = startX + ((midX - startX) / 2.0f);
        mInk.mPoints[2].mY = midY;

        mInk.mPoints[3].mX = startX;
        mInk.mPoints[3].mY = startY + ((midY - startY) / 2.0f);

        // path形状为菱形
        this.mPath.reset();
        this.mPath.moveTo(startX + ((midX - startX) / 2.0f), startY);
        this.mPath.lineTo(midX, startY + ((midY - startY) / 2.0f));
        this.mPath.lineTo(startX + ((midX - startX) / 2.0f), midY);
        this.mPath.lineTo(startX, startY + ((midY - startY) / 2.0f));
        this.mPath.close();
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);
    }
}
