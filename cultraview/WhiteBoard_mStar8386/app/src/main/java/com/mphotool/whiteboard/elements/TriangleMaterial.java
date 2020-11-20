package com.mphotool.whiteboard.elements;

import android.util.Log;

import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.MathUtils;
import com.mphotool.whiteboard.utils.PathUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.Serializable;

/**
 * @Description: 三角形
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class TriangleMaterial extends ShapeMaterial implements Serializable {
    private static final String TAG = TriangleMaterial.class.getSimpleName();

    public TriangleMaterial() {
        super();
    }

    public TriangleMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
        super(manager, mBrushColor, mBrushThickness);
    }

    /**
     * 初始化顶点坐标
     * @param color
     * @param thickness
     */
    @Override
    protected void initVector(int color, float thickness) {
        mVector = new Ink();
        mVector.mInkColor = color;
        mVector.mThickness = thickness;
        mVector.mPoints = new Point[3];
        mVector.mPointCount = 3;

        for (int i = 0; i < mVector.mPointCount; i ++){
            mVector.mPoints[i] = new Point();
        }
    }

    @Override
    protected void handleInks() {
        super.handleInks();

        float distance = MathUtils.getDistance(startX, startY, midX, midY);

        if (Float.compare(distance, 4F) < 0){
            return;
        }

        float distance0 = MathUtils.getDistance(startX + ((midX - startX) / 2.0f), startY, midX, midY);
        float distance1 = MathUtils.getDistance(midX, midY,startX, midY);
        float distance2 = MathUtils.getDistance(startX, midY, startX + ((midX - startX) / 2.0f), startY);
        insertInksPoint(startX + ((midX - startX) / 2.0f), startY, midX, midY,distance0,false,true);
        insertInksPoint(midX, midY,startX, midY,distance1,false,false);
        insertInksPoint(startX, midY, startX + ((midX - startX) / 2.0f), startY,distance2,true,false);
    }

    @Override
    protected void handleVector() {
        super.handleVector();
        PathUtils.addPoint(mVector.mPoints[0],startX + ((midX - startX) / 2.0f), startY);
        PathUtils.addPoint(mVector.mPoints[1], midX, midY);
        PathUtils.addPoint(mVector.mPoints[2], startX, midY);
    }

    @Override
    protected void handlePath() {
        super.handlePath();
        // path形状为三角形

        this.mPath.reset();
        this.mPath.moveTo(startX + ((midX - startX) / 2.0f), startY);
        this.mPath.quadTo(startX + ((midX - startX) / 2.0f),startY,midX,midY);
        this.mPath.quadTo(midX,midY,startX,midY);
        this.mPath.quadTo(startX,midY,startX + ((midX - startX) / 2.0f), startY);
/*        this.mPath.lineTo(midX, midY);
        this.mPath.lineTo(startX, midY);*/
        this.mPath.close();
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);
    }
}
