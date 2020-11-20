package com.mphotool.whiteboard.elements;

import com.mphotool.whiteboard.utils.BaseUtils;
import com.mphotool.whiteboard.utils.MathUtils;
import com.mphotool.whiteboard.utils.PathUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.Serializable;

/**
 * @Description: 箭头
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ArrowMaterial extends ShapeMaterial implements Serializable {
    private static final String TAG = ArrowMaterial.class.getSimpleName();

    public ArrowMaterial() {
        super();
    }

    public ArrowMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
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
        mVector.mPoints = new Point[7];
        mVector.mPointCount = 7;

        for (int i = 0; i < mVector.mPointCount; i ++){
            mVector.mPoints[i] = new Point();
        }
    }

    @Override
    protected void handleInks() {
        super.handleInks();
        float distance = MathUtils.getDistance(startX, startY, midX, midY);

        if (Float.compare(distance, 2F) < 0){
            return;
        }
        //求取坐标
/*        for (int i = 0; i < mVector.mPointCount; i++)
        {
            int preIndex = i;
            int endIndex = (i + 1) % mVector.mPointCount;
            addPointsBySegment(mVector.mPoints[preIndex], mVector.mPoints[endIndex]);
        }*/
        float distance0 = MathUtils.getDistance(mVector.mPoints[0].mX, mVector.mPoints[0].mY, mVector.mPoints[1].mX, mVector.mPoints[1].mY);
        float distance1 = MathUtils.getDistance(mVector.mPoints[1].mX, mVector.mPoints[1].mY, mVector.mPoints[2].mX, mVector.mPoints[2].mY);
        float distance2 = MathUtils.getDistance(mVector.mPoints[2].mX, mVector.mPoints[2].mY, mVector.mPoints[3].mX, mVector.mPoints[3].mY);
        float distance3 = MathUtils.getDistance(mVector.mPoints[3].mX, mVector.mPoints[3].mY, mVector.mPoints[4].mX, mVector.mPoints[4].mY);
        float distance4 = MathUtils.getDistance(mVector.mPoints[4].mX, mVector.mPoints[4].mY, mVector.mPoints[5].mX, mVector.mPoints[5].mY);
        float distance5 = MathUtils.getDistance(mVector.mPoints[5].mX, mVector.mPoints[5].mY, mVector.mPoints[6].mX, mVector.mPoints[6].mY);
        float distance6 = MathUtils.getDistance(mVector.mPoints[6].mX, mVector.mPoints[6].mY, mVector.mPoints[0].mX, mVector.mPoints[0].mY);

        insertInksPoint(mVector.mPoints[0].mX, mVector.mPoints[0].mY, mVector.mPoints[1].mX, mVector.mPoints[1].mY,distance0,false,true);
        insertInksPoint(mVector.mPoints[1].mX, mVector.mPoints[1].mY, mVector.mPoints[2].mX, mVector.mPoints[2].mY,distance1,false,false);
        insertInksPoint(mVector.mPoints[2].mX, mVector.mPoints[2].mY, mVector.mPoints[3].mX, mVector.mPoints[3].mY,distance2,false,false);
        insertInksPoint(mVector.mPoints[3].mX, mVector.mPoints[3].mY, mVector.mPoints[4].mX, mVector.mPoints[4].mY,distance3,false,false);
        insertInksPoint(mVector.mPoints[4].mX, mVector.mPoints[4].mY, mVector.mPoints[5].mX, mVector.mPoints[5].mY,distance4,false,false);
        insertInksPoint(mVector.mPoints[5].mX, mVector.mPoints[5].mY, mVector.mPoints[6].mX, mVector.mPoints[6].mY,distance5,false,false);
        insertInksPoint(mVector.mPoints[6].mX, mVector.mPoints[6].mY, mVector.mPoints[0].mX, mVector.mPoints[0].mY,distance6,true,false);
//        // 1、 (midX, midY + ((startY - midY) / 2.0f)) -> (midX + ((startX - midX) / 4.0f), midY)
//        // 添加起点
//        Point startP = new Point(midX, midY + ((startY - midY) / 2.0f));
//        Point endP = new Point(midX + ((startX - midX) / 4.0f), midY);
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
//
//        // 2、 (midX + ((startX - midX) / 4.0f), midY) -> (midX + ((startX - midX) / 4.0f),midY + ((startY - midY) / 4.0f))
//        // 添加起点
//        startP = new Point(midX + ((startX - midX) / 4.0f), midY);
//        endP = new Point(midX + ((startX - midX) / 4.0f), midY + ((startY - midY) / 4.0f));
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
//
//        // 3、 (midX + ((startX - midX) / 4.0f),midY + ((startY - midY) / 4.0f)) -> (startX, midY + ((startY - midY) / 4.0f))
//        startP = new Point(midX + ((startX - midX) / 4.0f),midY + ((startY - midY) / 4.0f));
//        endP = new Point(startX, midY + ((startY - midY) / 4.0f));
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
//
//        // 4、 (startX, midY + ((startY - midY) / 4.0f)) -> (startX, midY + (((startY - midY) / 4.0f) * 3.0f))
//        startP = new Point(startX, midY + ((startY - midY) / 4.0f));
//        endP = new Point(startX, midY + (((startY - midY) / 4.0f) * 3.0f));
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
//
//        // 5、(startX, midY + (((startY - midY) / 4.0f) * 3.0f)) -> (startX, midY + (((startY - midY) / 4.0f) * 3.0f))
//        startP = new Point(startX, midY + (((startY - midY) / 4.0f) * 3.0f));
//        endP = new Point(startX, midY + (((startY - midY) / 4.0f) * 3.0f));
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
//
//        // 6、(startX, midY + (((startY - midY) / 4.0f) * 3.0f)) -> (midX + ((startX - midX) / 4.0f),midY + (((startY - midY) / 4.0f) * 3.0f))
//        startP = new Point(startX, midY + (((startY - midY) / 4.0f) * 3.0f));
//        endP = new Point(midX + ((startX - midX) / 4.0f),midY + (((startY - midY) / 4.0f) * 3.0f));
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
//
//        // 7、(midX + ((startX - midX) / 4.0f),midY + (((startY - midY) / 4.0f) * 3.0f)) -> (midX + ((startX - midX) / 4.0f), startY)
//        startP = new Point(midX + ((startX - midX) / 4.0f),midY + (((startY - midY) / 4.0f) * 3.0f));
//        endP = new Point(midX + ((startX - midX) / 4.0f), startY);
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
//
//        // 8、 (midX + ((startX - midX) / 4.0f), startY) -> (midX, midY + ((startY - midY) / 2.0f))
//        startP = new Point(midX + ((startX - midX) / 4.0f), startY);
//        endP = new Point(midX, midY + ((startY - midY) / 2.0f));
//        // 添加起点
//        savePointsToArray(startP);
//        insertFalsePoints(startP, endP);
//        savePointsToArray(endP);
    }

    @Override
    protected void handleVector() {
        super.handleVector();
        PathUtils.addPoint(mVector.mPoints[0], midX, midY + ((startY - midY) / 2.0f));
        PathUtils.addPoint(mVector.mPoints[1], midX + ((startX - midX) / 4.0f), midY);
        PathUtils.addPoint(mVector.mPoints[2], midX + ((startX - midX) / 4.0f),midY + ((startY - midY) / 4.0f));
        PathUtils.addPoint(mVector.mPoints[3], startX, midY + ((startY - midY) / 4.0f));
        PathUtils.addPoint(mVector.mPoints[4], startX, midY + (((startY - midY) / 4.0f) * 3.0f));
        PathUtils.addPoint(mVector.mPoints[5], midX + ((startX - midX) / 4.0f),midY + (((startY - midY) / 4.0f) * 3.0f));
        PathUtils.addPoint(mVector.mPoints[6], midX + ((startX - midX) / 4.0f), startY);
    }

    @Override
    protected void handlePath() {
        super.handlePath();

        // path形状为箭头
        this.mPath.reset();

        this.mPath.moveTo(midX, midY + ((startY - midY) / 2.0f));
        this.mPath.lineTo(midX + ((startX - midX) / 4.0f), midY);
        this.mPath.lineTo(midX + ((startX - midX) / 4.0f),midY + ((startY - midY) / 4.0f));
        this.mPath.lineTo(startX, midY + ((startY - midY) / 4.0f));
        this.mPath.lineTo(startX, midY + (((startY - midY) / 4.0f) * 3.0f));

        this.mPath.lineTo(midX + ((startX - midX) / 4.0f),midY + (((startY - midY) / 4.0f) * 3.0f));
        this.mPath.lineTo(midX + ((startX - midX) / 4.0f), startY);
        this.mPath.close();
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);
    }
}
