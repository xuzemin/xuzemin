package com.mphotool.whiteboard.utils;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import com.mphotool.whiteboard.entity.MVector;

/**
 * @Description: 数学工具
 * @Author: wanghang
 * @CreateDate: 2019/10/14 10:47
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/14 10:47
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MathUtils {
    /**
     * As meaning of method name.
     * 获得两点之间的距离
     * @param p0
     * @param p1
     * @return
     */
    public static float getDistance(PointF p0, PointF p1) {
        float distance = (float) Math.sqrt(Math.pow(p0.y - p1.y, 2) + Math.pow(p0.x - p1.x, 2));
        return distance;
    }

    /**
     * 获得两点之间的距离
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @return
     */
    public static float getDistance(float x0, float y0, float x1, float y1) {
        float distance = (float) Math.sqrt(Math.pow(y0 - y1, 2) + Math.pow(x0 - x1, 2));
        return distance;
    }

    /**
     * 判断点在三角形内
     * @param A
     * @param B
     * @param C
     * @param P
     * @return
     */
    public static boolean isInTriangle(PointF A, PointF B, PointF C, PointF P) {
        double ABC = triAngleArea(A, B, C);
        double ABp = triAngleArea(A, B, P);
        double ACp = triAngleArea(A, C, P);
        double BCp = triAngleArea(B, C, P);
        if ((int) ABC == (int) (ABp + ACp + BCp)) {// 若面积之和等于原三角形面积，证明点在三角形内,这里做了一个约等于小数点之后没有算（25714.25390625、25714.255859375）
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获得三角形面积
     * @param A
     * @param B
     * @param C
     * @return
     */
    public static double triAngleArea(PointF A, PointF B, PointF C) {// 由三个点计算这三个点组成三角形面积
        double result = Math.abs((A.x * B.y + B.x * C.y
                + C.x * A.y - B.x * A.y - C.x
                * B.y - A.x * C.y) / 2.0D);
        return result;
    }

    /**
     * 判断两个线段是否有交叉点
     * @param line0sx
     * @param line0sy
     * @param line0ex
     * @param line0ey
     * @param line1sx
     * @param line1sy
     * @param line1ex
     * @param line1ey
     * @return
     */
    public static boolean isIntersection(int line0sx, int line0sy, int line0ex, int line0ey, int line1sx, int line1sy, int line1ex, int line1ey)
    {
        long localLength2 = (long) (((line0sx - line0ex) * (line1sy - line1ey)) - ((line0sy - line0ey) * (line1sx - line1ex)));
        if (localLength2 == 0)
        {
            return false;
        }
        long localLength3 = (long) ((line0sx * line0ey) - (line0sy * line0ex));
        long localLength1 = (long) ((line1sx * line1ey) - (line1sy * line1ex));
        int lIndexJ = (int) (((((long) (line1sx - line1ex)) * localLength3) - (((long) (line0sx - line0ex)) * localLength1)) / localLength2);
        if (lIndexJ + 1 < Math.min(line0sx, line0ex) || lIndexJ - 1 > Math.max(line0sx, line0ex) || lIndexJ + 1 < Math.min(line1sx, line1ex) || lIndexJ - 1 > Math.max(line1sx, line1ex))
        {
            return false;
        }
        int lIndexI = (int) (((((long) (line1sy - line1ey)) * localLength3) - (((long) (line0sy - line0ey)) * localLength1)) / localLength2);
        if (lIndexI + 1 < Math.min(line0sy, line0ey) || lIndexI - 1 > Math.max(line0sy, line0ey) || lIndexI + 1 < Math.min(line1sy, line1ey) || lIndexI - 1 > Math.max(line1sy, line1ey))
        {
            return false;
        }
        return true;
    }

    /**
     * 判断点是否在椭圆内
     * @param rectF
     * @param x1
     * @param y1
     * @return
     */
    public static boolean isInOval(RectF rectF, float x1, float y1){
        float centerX = (rectF.left + rectF.right) * 0.5F;
        float centerY = (rectF.top + rectF.bottom) * 0.5F;

        float detalW = Math.abs(rectF.left - rectF.right);
        float detalH = Math.abs(rectF.top - rectF.bottom);

        if (detalW == 0 || detalH == 0){
            return false;
        }

        float a = detalW * 0.5F;
        float b = detalH * 0.5F;

        if (detalW >= detalH){ // 长轴在x轴上
            a = detalW * 0.5F;
            b = detalH * 0.5F;
        }
        else { // 长轴在Y轴上
            // 交互X Y轴坐标
            float tmp = centerX;
            centerX = centerY;
            centerY = tmp;

            tmp = x1;
            x1 = y1;
            y1 = tmp;

            a = detalH * 0.5F;
            b = detalW * 0.5F;
        }

        double v = Math.pow(centerX- x1, 2) / Math.pow(a, 2) + Math.pow(centerY- y1, 2) / Math.pow(b, 2);
        return v < 1;
    }

    /**
     * 判断点在圆内
     * @param rectF
     * @param x1
     * @param y1
     * @return
     */
    public static boolean isInCircle(RectF rectF, float x1, float y1){
        boolean flag = false;

        // 圆中心点
        float centerX = (rectF.left + rectF.right) * 0.5F;
        float centerY = (rectF.top + rectF.bottom) * 0.5F;

        // 计算半径
        float deltaW = Math.abs(rectF.left - rectF.right);
        float deltaH = Math.abs(rectF.top - rectF.bottom);
        float r = deltaW < deltaH ?  deltaW * 0.5F : deltaH * 0.5F; // 计算半径
        if (r <= 0){
            return flag;
        }
        flag = getDistance(x1, y1, centerX, centerY) <= r;
        return flag;
    }

    public static boolean crossCircleAndRectangle(Rect circleRectF, Rect rectF){
        float circleX = (circleRectF.left + circleRectF.right) * 0.5F;
        float circleY = (circleRectF.top + circleRectF.bottom) * 0.5F;

        float rcircleX = (rectF.left + rectF.right) * 0.5F;
        float rcircleY = (rectF.top + rectF.bottom) * 0.5F;

        float r = Math.abs(circleRectF.left - circleRectF.right) * 0.5F;
        MVector v = new MVector(circleX - rcircleX,
                circleY - rcircleY);

        MVector h = new MVector(Math.abs(rectF.left - rectF.right) * 0.5F,
                Math.abs(rectF.top - rectF.bottom) * 0.5F);

        MVector u = v.sub(h);

        u.x = Math.max(u.x, 0);
        u.y = Math.max(u.y, 0);
        return Double.compare(u.dot(u), Math.pow(r, 2)) <= 0;
    }

//    public static float computeA(float[] tan){
////        float startDegrees = (float)Math.toDegrees(Math.atan2(tan[1], tan[0]));
//    }
}
