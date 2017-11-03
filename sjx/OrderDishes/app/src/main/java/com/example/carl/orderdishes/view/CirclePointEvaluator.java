package com.example.carl.orderdishes.view;

import android.animation.TypeEvaluator;
import android.graphics.Point;

/**
 * Created by Carl on 2017/11/1.
 *
 */

public class CirclePointEvaluator implements TypeEvaluator {

    /**
     * @param t   当前动画进度
     * @param startValue 开始值
     * @param endValue   结束值
     * @return
     */
    private Point mCircleConPoint = new Point(200,200);

    @Override
    public Object evaluate(float t, Object startValue, Object endValue) {

        Point startPoint = (Point) startValue;
        Point endPoint = (Point) endValue;

        int x = (int) (Math.pow((1-t),2)*startPoint.x+2*(1-t)*t*mCircleConPoint.x+Math.pow(t,2)*endPoint.x);
        int y = (int) (Math.pow((1-t),2)*startPoint.y+2*(1-t)*t*mCircleConPoint.y+Math.pow(t,2)*endPoint.y);

        return new Point(x,y);
    }

}

