package com.mphotool.whiteboard.elements;

import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.utils.PathUtils;
import com.mphotool.whiteboard.view.PanelManager;

/**
 * @Description: 作用描述
 * @Author: wanghang
 * @CreateDate: 2019/10/9 18:15
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/9 18:15
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class CircleMaterial extends ShapeMaterial {
    private static final String TAG = CircleMaterial.class.getSimpleName();

    private float radius = 0;
    private float centerX = 0;
    private float centerY = 0;

    private float startAngle = 0;
    private float endAngle = 360;

    public CircleMaterial() {
        super();
    }
    public CircleMaterial(PanelManager manager, int mBrushColor, float mBrushThickness) {
        super(manager, mBrushColor, mBrushThickness);
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
        int d360 = 0;
        if( radius < 50){
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
        }
//        BaseUtils.dbg(TAG, "draw radius=" + radius);
        float[] pos = new float[2];
        for (int i = 0; i< d360; i++){
            P_MEASURE.getPosTan(len *  i / d360, pos, null);
//            BaseUtils.dbg(TAG, "draw point x=" + pos[0] + " y=" + pos[1]);
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
    protected void handlePath() {
        super.handlePath();

        // 获得半径和中心点
        float deltaX = Math.abs(startX - midX);
        float deltaY = Math.abs(startY - midY);
        float r = deltaX < deltaY ?  deltaX * 0.5F : deltaY* 0.5F; // 计算直径
        if (r <= 0){
            return ;
        }
        radius = r;

        centerX = (startX + midX) * 0.5F;
        centerY = (startY + midY) * 0.5F;

        // path形状为圆形
        this.mPath.reset();
        this.mPath.addArc(centerX-r, centerY-r, centerX+r, centerY + r, startAngle, endAngle - startAngle);
    }

    @Override
    public boolean isCross(float x1, float y1, float x2, float y2) {
        return PathUtils.isCrossInk(mInk, x1, y1, x2, y2);
    }

}
