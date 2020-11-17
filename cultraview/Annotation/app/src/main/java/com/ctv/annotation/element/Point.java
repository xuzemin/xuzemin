package com.ctv.annotation.element;

public class Point {
    public boolean mIsFalsePoint;
    public float mX;
    public float mY;

    public Point() {}

    public Point(float x, float y)
    {
        mX = x;
        mY = y;
        mIsFalsePoint = false;
    }

    public Point(float x, float y, boolean isFalsePoint)
    {
        mX = x;
        mY = y;
        mIsFalsePoint = isFalsePoint;
    }

    @Override
    public String toString()
    {
        return "location:  " + mX + " / " + mY + " , IsFalsePoint = " + mIsFalsePoint;
    }

}
