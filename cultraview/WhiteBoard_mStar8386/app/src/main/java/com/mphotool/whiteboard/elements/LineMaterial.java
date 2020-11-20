package com.mphotool.whiteboard.elements;

import android.graphics.Color;

import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.MathUtils;
import com.mphotool.whiteboard.utils.PathUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.Serializable;

import javax.microedition.khronos.opengles.GL10;

/**
 * @Description: 直线
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class LineMaterial extends ShapeMaterial {
    private static final String TAG = LineMaterial.class.getSimpleName();

    public LineMaterial() {
        super();
    }

    public LineMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
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
        mVector.mPoints = new Point[2];
        mVector.mPointCount = 2;

        for (int i = 0; i < mVector.mPointCount; i ++){
            mVector.mPoints[i] = new Point();
        }
    }

    @Override
    protected void handleInks() {
        super.handleInks();
        float distance = MathUtils.getDistance(startX, startY, midX, midY);
        if (Float.compare(distance, 4F) < 0){
            savePointsToArray(new Point(startX, startY));
            savePointsToArray(new Point(midX, midY));
            return;
        }

        //求取坐标
        insertInksPoint(startX, startY, midX, midY,distance,true,true);
    }

    @Override
    protected void handleVector() {
        super.handleVector();
        mVector.mPoints[0] = new Point(startX, startY);
        mVector.mPoints[1] = new Point(midX, midY);
    }

    @Override
    protected void handlePath() {
        super.handlePath();
        BaseUtils.dbg(TAG, "handlePath");
        // path形状为直线
        this.mPath.reset();
        this.mPath.moveTo(startX, startY);
        this.mPath.lineTo(midX, midY);
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);
    }
}
