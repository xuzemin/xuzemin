package com.mphotool.whiteboard.elements;


import com.mphotool.whiteboard.utils.Constants;

public class Ink {
    public int mInkColor;
    public int mPointCount;
    public Point[] mPoints;
    public float mThickness;

    public Ink()
    {
        this.mInkColor = -1;
        this.mThickness = Constants.PEN_WIDTH_LITTLE;
        this.mPointCount = 0;
    }


}
