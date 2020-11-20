package com.mphotool.whiteboard.elements;

import android.graphics.Path;

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
public class CylinderMaterial extends ShapeMaterial implements Serializable {
    private static final String TAG = CylinderMaterial.class.getSimpleName();
    private Path OvalPath = new Path();
    private Path SemiellipsePath = new Path();
    private Point PointUp = new Point(0.0f,0.0f);
    private Point PointDown = new Point(0.0f,0.0f);


    public CylinderMaterial() {
        super();
    }

    public CylinderMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
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
        mVector.mPoints = new Point[4];
        mVector.mPointCount = 4;

        for (int i = 0; i < mVector.mPointCount; i ++){
            mVector.mPoints[i] = new Point();
        }
    }

    @Override
    protected void handleInks() {
        super.handleInks();
        int d360 = 360;
        float R = Math.abs(midX-startX);
        BaseUtils.dbg(TAG, "draw point R=" + R);
        if(R > 0 && R < 100)
            d360 = 90;
        if(R >= 100 && R < 300)
            d360 = 210;
        else if(R >= 300 && R <= 600)
            d360 = 300;
        else if (R > 600 && R <= 900)
            d360 = 360;
        else
            d360 = 490;
        Point mPoint = new Point(0.0f,0.0f);

        float distance0 = MathUtils.getDistance(mVector.mPoints[0].mX, mVector.mPoints[0].mY, mVector.mPoints[1].mX, mVector.mPoints[1].mY);
        float distance1 = MathUtils.getDistance(mVector.mPoints[2].mX, mVector.mPoints[2].mY, mVector.mPoints[3].mX, mVector.mPoints[3].mY);
        if (Float.compare(distance0, 4F) < 0){
            savePointsToArray(new Point(mVector.mPoints[0].mX, mVector.mPoints[0].mY));
            savePointsToArray(new Point(mVector.mPoints[1].mX, mVector.mPoints[1].mY));
            return;
        }
        if (Float.compare(distance1, 4F) < 0){
            savePointsToArray(new Point(mVector.mPoints[2].mX, mVector.mPoints[2].mY));
            savePointsToArray(new Point(mVector.mPoints[3].mX, mVector.mPoints[3].mY));
            return;
        }

        P_MEASURE.setPath(OvalPath, false);
        float nlen = P_MEASURE.getLength();
        if (nlen == 0){
            return;
        }
        float[] npos = new float[2];
        for (int i = 0; i< d360; i++){
            P_MEASURE.getPosTan(nlen *  i / d360, npos, null);
//            BaseUtils.dbg(TAG, "draw point x=" + npos[0] + " y=" + npos[1]);
            if(i == 0) {
                mPoint = new Point(npos[0], npos[1]);
                insertInksPoint(mPoint,false,true);
            } else if(i == d360 -1) {
                insertInksPoint(mPoint, true, false);
            }else {
                insertInksPoint(new Point(npos[0], npos[1]), false, false);
            }
        }

        insertInksPoint(mVector.mPoints[0].mX, mVector.mPoints[0].mY, mVector.mPoints[1].mX, mVector.mPoints[1].mY,distance0,true,true);

        P_MEASURE.setPath(SemiellipsePath, true);
        float len = P_MEASURE.getLength();
        if (len == 0){
            return;
        }
        float[] pos = new float[2];
        float[] tan = new float[2];
        for (int i = 0;i< d360;i++){
            P_MEASURE.getPosTan(len *  i / d360, pos, tan);
            float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
            if(degrees >= 90F || degrees <= -90F ) {
                if (i == 0) {
                    mPoint = new Point(pos[0], pos[1]);
                    insertInksPoint(mPoint, false, true);
                } else if (i == d360 - 1) {
                    insertInksPoint(new Point(pos[0], pos[1]), true, false);
                } else {
                    insertInksPoint(new Point(pos[0], pos[1]), false, false);
                }
            }
        }
        insertInksPoint(mVector.mPoints[2].mX , mVector.mPoints[2].mY, mVector.mPoints[3].mX, mVector.mPoints[3].mY,distance1,true,true);
    }

    @Override
    protected void handleVector() {
        super.handleVector();
        PathUtils.addPoint(mVector.mPoints[0], startX, startY+(midY-startY)/10);
        PathUtils.addPoint(mVector.mPoints[1], startX, midY - (midY-startY)/10);
        PathUtils.addPoint(mVector.mPoints[2], midX, midY-(midY-startY)/10);
        PathUtils.addPoint(mVector.mPoints[3], midX, startY+(midY-startY)/10);
    }

    @Override
    protected void handlePath() {
        super.handlePath();
        // 获得半径和中心点
        float upX = 0.00f,upY=0.00f,downX = 0.00f,downY=0.00f;
        Point m0,m1,m2,m3;
        float R = Math.abs(midX-startX);
        float r = Math.abs((midY-startY)/5);
        if (r <= 0F){
            return ;
        }
        if(midY >= startY && midX >= startX) {
            upX = Math.abs(startX + R/2);
            upY = Math.abs(startY + r/2);
            downX = Math.abs(startX + R/2);
            downY = Math.abs(midY - r/2);

            m0 = new Point(startX, startY+ r/2);
            m1 = new Point(startX, midY - r/2);
            m2 = new Point(midX, midY- r/2);
            m3 = new Point(midX, startY+ r/2);
        }else if(midY < startY && midX >= startX){
            upX = Math.abs(startX + R/2);
            upY = Math.abs(midY + r/2);
            downX = Math.abs(startX + R/2);
            downY = Math.abs(startY - r/2);

            m0 = new Point(startX, startY - r/2);
            m1 = new Point(startX, midY + r/2);
            m2 = new Point(midX, midY + r/2);
            m3 = new Point(midX, startY - r/2);
        }else if(midY >= startY && midX < startX){
            upX = Math.abs(startX - R/2);
            upY = Math.abs(startY + r/2);
            downX = Math.abs(startX - R/2);
            downY = Math.abs(midY - r/2);

            m0 = new Point(startX, startY + r/2);
            m1 = new Point(startX, midY - r/2);
            m2 = new Point(midX, midY - r/2);
            m3 = new Point(midX, startY + r/2);
        }else {
            upX = Math.abs(midX + R/2);
            upY = Math.abs(midY + r/2);
            downX = Math.abs(startX - R/2);
            downY = Math.abs(startY - r/2);

            m0 = new Point(startX, startY - r/2);
            m1 = new Point(startX, midY + r/2);
            m2 = new Point(midX, midY + r/2);
            m3 = new Point(midX, startY - r/2);
        }

        PointUp.mX = upX;
        PointUp.mY = upY;
        PointDown.mX = downX;
        PointDown.mY = downY;


        // path形状为椭圆
        this.mPath.reset();
        this.OvalPath.reset();
        this.SemiellipsePath.reset();

        this.mPath.addArc(getOvalRectF(upX,upY,R,r), 0.0f, 360.0f);
        this.OvalPath.addArc(getOvalRectF(upX,upY,R,r), 0.0f, 360.0f);
        this.mPath.moveTo(m0.mX, m0.mY);
        this.mPath.lineTo(m1.mX, m1.mY);
        this.mPath.addArc(getOvalRectF(downX,downY,R,r), 0.0f, 180.0f);
        this.SemiellipsePath.addArc(getOvalRectF(downX,downY,R,r), 0.0f, 360.0f);
        this.mPath.moveTo(m2.mX, m2.mY);
        this.mPath.lineTo(m3.mX,m3.mY);
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);
    }
}
