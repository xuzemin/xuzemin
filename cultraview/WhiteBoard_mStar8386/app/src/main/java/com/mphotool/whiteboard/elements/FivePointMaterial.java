package com.mphotool.whiteboard.elements;

import com.mphotool.whiteboard.utils.MathUtils;
import com.mphotool.whiteboard.utils.PathUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.Serializable;

/**
 * @Description: 五角星
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FivePointMaterial extends ShapeMaterial implements Serializable {
    private static final String TAG = FivePointMaterial.class.getSimpleName();

    public FivePointMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
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
        mVector.mPoints = new Point[10];
        mVector.mPointCount = 10;

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
        for (int i = 0; i < 5; i++)
        {
            addPointsBySegment(mVector.mPoints[2 * i], mVector.mPoints[2 * i+ 1]);
        }
    }

    @Override
    protected void handleVector() {
        super.handleVector();

        // path形状为五角星

        float deltaX = Math.abs(startX - midX);
        float deltaY = Math.abs(startY - midY);

        // 防止长轴为0
        if (deltaX == 0 || deltaY == 0){
            return;
        }

        float rLong = deltaX < deltaY ?  deltaX * 0.5F : deltaY * 0.5F; // 计算长轴半径
        float r =  (float) (rLong * Math.sin(Math.toRadians(18)) / Math.cos(Math.toRadians(36)));    //五角星短轴的长度
        // 中心坐标
        float cX = (startX + midX) * 0.5F;
        float cY = (startY + midY) * 0.5F;

        float yDegree = 0;// 长轴与y轴的夹角

        //求取坐标
        for (int i = 0; i < 5; i++)
        {
            // 外顶点坐标
            float outsideX = (float) (cX - (rLong * Math.cos(Math.toRadians(90 + i * 72 + yDegree))));
            float outsideY = (float) (cY - (rLong * Math.sin(Math.toRadians(90 + i * 72 + yDegree))));

            // 添加外点
            mVector.mPoints[2 * i].mX = outsideX;
            mVector.mPoints[2 * i].mY = outsideY;

            // 内顶点坐标
            float insideX = (float) (cX - (r * Math.cos(Math.toRadians(90 + 36 + i * 72 + yDegree))));
            float insideY = (float)(cY - (r * Math.sin(Math.toRadians(90 + 36 + i * 72 + yDegree))));

            // 添加内点
            mVector.mPoints[2 * i + 1].mX = insideX;
            mVector.mPoints[2 * i+ 1].mY = insideY;
        }
    }

    @Override
    protected void handlePath() {
        super.handlePath();

        // path形状为五角星

        float deltaX = Math.abs(startX - midX);
        float deltaY = Math.abs(startY - midY);

        // 防止长轴为0
        if (deltaX == 0 || deltaY == 0){
            return;
        }

        this.mPath.reset();

        //求取坐标
        for (int i = 0; i < 5; i++)
        {
            // 添加外点
            float outsideX = mVector.mPoints[2 * i].mX;
            float outsideY = mVector.mPoints[2 * i].mY;
            if (i == 0) {
                this.mPath.moveTo(outsideX, outsideY);
            } else {
                this.mPath.lineTo(outsideX, outsideY);
            }
            // 添加内点
            float insideX = mVector.mPoints[2 * i + 1].mX;
            float insideY = mVector.mPoints[2 * i+ 1].mY;

            this.mPath.lineTo(insideX, insideY);
        }
        this.mPath.close();
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);
    }
}
