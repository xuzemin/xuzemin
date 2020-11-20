package com.mphotool.whiteboard.elements;

import com.mphotool.whiteboard.utils.PathUtils;
import com.mphotool.whiteboard.view.PanelManager;

import java.io.Serializable;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OvalMaterial extends ShapeMaterial implements Serializable {
    private static final String TAG = OvalMaterial.class.getSimpleName();
//    private Matrix mMatrix = new Matrix();
    private float radius = 0;
    public OvalMaterial() {
        super();
    }
    public OvalMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
        super(manager, mBrushColor, mBrushThickness);
    }

    @Override
    protected void handlePath() {
        super.handlePath();
        // 获得半径和中心点
        float deltaX = Math.abs(startX - midX);
        float deltaY = Math.abs(startY - midY);
        float r = deltaX < deltaY ?  deltaY * 0.5F : deltaX* 0.5F; // 计算直径
        if (r <= 0){
            return ;
        }
        radius = r;
        // path形状为椭圆
        this.mPath.reset();
        this.mPath.addArc(getShapeRectF(0), 0.0f, 360.0f);
    }

    @Override
    protected void handleInks() {
        super.handleInks();
        Point mPoint = new Point(0.0f,0.0f);
        P_MEASURE.setPath(mPath, false);
        float len = P_MEASURE.getLength();
        if (len == 0){
            return;
        }

        int d360 = 360;
       /* if( radius < 50){
            d360 = 60;
        }else if( radius >= 50){
            d360 = 90;
        }else if(radius >= 100) {
            d360 = 120;
        }else if(radius >= 150) {
            d360 = 180;
        }else if(radius >= 200){
            d360 = 240;
        }else {
            d360 = 360;
        }*/

        float[] pos = new float[2];
        for (int i = 0;i< d360;i++){
            P_MEASURE.getPosTan(len *  i / d360, pos, null);
//            savePointsToArray(new Point(pos[0], pos[1]));
            if(i == 0) {
                mPoint = new Point(pos[0], pos[1]);
                insertInksPoint(mPoint,false,true);
            } else if(i == d360 -1) {
                insertInksPoint(mPoint, true, false);
            }else {
                insertInksPoint(new Point(pos[0], pos[1]), false, false);
            }

        }
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);

//        return MathUtils.isInOval(mShapeRectF, x1, y1) || MathUtils.isInOval(mShapeRectF, x2, y2);
    }
}
