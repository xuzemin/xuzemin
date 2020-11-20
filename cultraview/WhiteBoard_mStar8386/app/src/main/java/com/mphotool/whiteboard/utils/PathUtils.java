package com.mphotool.whiteboard.utils;

import android.graphics.Rect;

import com.mphotool.whiteboard.elements.Ink;
import com.mphotool.whiteboard.elements.Point;

/**
 * @Description: path工具类
 * @Author: wanghang
 * @CreateDate: 2019/10/14 15:02
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/14 15:02
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class PathUtils {
    /**
     * @param xA 起始点位置A的x轴绝对位置
     * @param yA 起始点位置A的y轴绝对位置
     * @param rFive 五角星边的边长
     */
    public static float[] fivePoints(float xA, float yA, float rFive) {
        float xB = 0;
        float xC = 0;
        float xD = 0;
        float xE = 0;
        float yB = 0;
        float yC = 0;
        float yD = 0;
        float yE = 0;
        xD = (float) (xA - rFive * Math.sin(Math.toRadians(18)));
        xC = (float) (xA + rFive * Math.sin(Math.toRadians(18)));
        yD = yC = (float) (yA + Math.cos(Math.toRadians(18)) * rFive);
        yB = yE = (float) (yA + Math.sqrt(Math.pow((xC - xD), 2) - Math.pow((rFive / 2), 2)));
        xB = xA + (rFive / 2);
        xE = xA - (rFive / 2);
        float[] floats = new float[]{xA, yA,  xD, yD,xB, yB, xE, yE, xC, yC,xA, yA};
        return floats;
    }

    /**
     * @param xA 起始点位置A的x轴绝对位置
     * @param yA 起始点位置A的y轴绝对位置
     * @param rFive 五角星边的边长
     */
    /**
     *
     *
     * @param cX
     * @param cY
     * @param rLong
     * @param yDegree
     * @param outside
     * @param inside
     */
    public static void fivePoints(float cX, float cY, float rLong, float yDegree,
                                           float[] outside, float[] inside) {
        float r =  (float) (rLong * Math.sin(Math.toRadians(18)) / Math.cos(Math.toRadians(36)));    //五角星短轴的长度

        //求取坐标
        for (int k = 0; k < 5; k++)
        {
            float outsideX = (float) (cX - (rLong * Math.cos(Math.toRadians(90 + k * 72 + yDegree))));
            float outsideY = (float) (cY - (rLong * Math.sin(Math.toRadians(90 + k * 72 + yDegree))));
            outside[2 * k] = outsideX;
            outside[2 * k + 1] = outsideY;

            float insideX = (float) (cX - (r * Math.cos(Math.toRadians(90 + 36 + k * 72 + yDegree))));
            float insideY = (float)(cY - (r * Math.sin(Math.toRadians(90 + 36 + k * 72 + yDegree))));
            inside[2 * k] = insideX;
            inside[2 * k + 1] = insideY;
        }
    }

    /**
     * 判断点是否在Ink中
     *
     * @param mInk
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static boolean isCrossInk(Ink mInk, float x1, float y1, float x2, float y2)
    {
        if (mInk == null || mInk.mPointCount < 2)
        {
            return false;
        }
        Point point = mInk.mPoints[0];
        float lx = point.mX;
        float ly = point.mY;
        for (int i = 1; i < mInk.mPointCount; i++)
        {
            point = mInk.mPoints[i];
            if (MathUtils.isIntersection((int) lx, (int) ly, (int) point.mX, (int) point.mY, (int) x1, (int) y1, (int) x2, (int) y2))
            {
                return true;
            }
            lx = point.mX;
            ly = point.mY;
        }
        return false;
    }

    /**
     * 添加点
     * @param point
     * @param x
     * @param y
     */
    public static void addPoint(Point point, float x, float y){
        point.mX = x;
        point.mY = y;
    }

    public static Point[] segmentCrossRect(Rect rect, Point start, Point end){
        Point[] points = null;

        return points;
    }
}
