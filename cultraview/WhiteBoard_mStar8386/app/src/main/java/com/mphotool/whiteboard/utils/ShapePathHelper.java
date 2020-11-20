package com.mphotool.whiteboard.utils;

import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import com.mphotool.whiteboard.entity.Storable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * 形状路径帮助类
 * @author wanghang
 * @date 2019/10/11
 */
public class ShapePathHelper implements Serializable, Storable {
    private static final float WIDTH_SCALE = 3.3f;
    private static final long serialVersionUID = Constants.serialVersionUID;
    private transient Path mPath = new Path(); //总路径
    //    private transient Path mCurrentPath = new Path(); //临时路径，用于每次处理touch时的局部绘制
    private float width = 2.0f;

    private static final float MIX_DISTANCE = 2.0f;

    // 起始点X、Y
    private float startX;
    private float startY;

    private float cx;
    private float cy;
    private float sx;
    private float sy;

    private float midX;
    private float mLastX = -1.0f;
    private float mLastY = -1.0f;
    private float midY;
    private float mLastEndX, mLastEndY;

    private int shapeType = StatusEnum.STATUS_SHAPE_RECTTANGLE;

    public ShapePathHelper(float width, int shapeType)
    {
        width = width / WIDTH_SCALE;
        this.shapeType = shapeType;
    }

    public ShapePathHelper(float width)
    {
        width = width / WIDTH_SCALE;
    }

    public void onTouch(float toX, float toY, int action)
    {
        boolean up = true;

        if (action == MotionEvent.ACTION_DOWN)
        {
            startX = toX;
            startY = toY;

            mLastX = toX;
            sx = toX;
            mLastY = toY;
            sy = toY;
            mLastEndX = toX;
            mLastEndY = toY;

//            mPath.rewind();

            /**起点*/
//            mPath.moveTo(toX, toY);
            return;
        }
        if (action != MotionEvent.ACTION_UP)
        {
            up = false;
        }

        cx = mLastX;
        cy = mLastY;
        sx = mLastEndX;
        sy = mLastEndY;

        if (up)
        {
            cx = (mLastX + toX) / 2.0f;
            cy = (mLastY + toY) / 2.0f;
            midX = toX;
            midY = toY;
        }
        else
        {
            midX = (cx + toX) / 2.0f;
            midY = (cy + toY) / 2.0f;
        }

        mLastEndX = midX;
        mLastEndY = midY;
        mLastX = toX;
        mLastY = toY;

        // path形状为矩形
        this.mPath.reset();
        this.mPath.moveTo(startX, startY);
        this.mPath.lineTo(startX, midY);
        this.mPath.lineTo(midX, midY);
        this.mPath.lineTo(midX, startY);
        this.mPath.close();

        /**总path*/
//        mPath.moveTo(sx, sy);
//        mPath.cubicTo(sx, sy, cx, cy, midX, midY);

//        mPath.moveTo(cx, cy);
//        mPath.cubicTo(cx, cy, midX, midY, mLastX, mLastY);


    }

    public boolean addToPath(Path path)
    {
//        path.moveTo(cx, cy);
//        path.cubicTo(cx, cy, midX, midY, mLastX, mLastY);

        path.moveTo(sx, sy);
        path.cubicTo(sx, sy, cx, cy, midX, midY);
//        path.moveTo(sx, sy);
//        if ((Math.abs(sx - cx) >= width || Math.abs(sy - cy) >= width) && (Math.abs(cx - midX) >= width || Math.abs(cy - midX) >= width)) {
//            path.quadTo(cx, cy, midX, midY);
//        } else {
//            path.lineTo(midX, midX);
//        }
        return true;
    }


    public Path getPath()
    {
        return mPath;
    }

    /**
     * 绘制框
     * @param rect
     * @param padding
     */
    public void shapeToDrawRect(Rect rect, int padding)
    {
        pointsToRect((int) sx, (int) sy, (int) cx, (int) cy, (int) midX, (int) midY, rect, padding);
    }

    /**
     * 形状的边缘框
     * @param rect
     * @param padding
     */
    public void shapeBoundRect(RectF rect, int padding)
    {
        rect.left = Math.min(startX, midX);
        rect.right = Math.max(startX, midX);
        rect.top = Math.min(startY, midY);
        rect.bottom = Math.max(startY, midY);
    }

    public void pointsToRect(Rect rect, int padding)
    {
        pointsToRect((int) sx, (int) sy, (int) cx, (int) cy, (int) midX, (int) midY, rect, padding);
    }

    private void pointsToRect(int x1, int y1, int x2, int y2, int x3, int y3, Rect rect, int padding)
    {
        if (x1 <= x2)
        {
            rect.left = x1;
            rect.right = x2;
        }
        else
        {
            rect.left = x2;
            rect.right = x1;
        }
        if (x3 <= rect.left)
        {
            rect.left = x3;
        }
        if (x3 >= rect.right)
        {
            rect.right = x3;
        }
        if (y1 <= y2)
        {
            rect.top = y1;
            rect.bottom = y2;
        }
        else
        {
            rect.top = y2;
            rect.bottom = y1;
        }
        if (y3 <= rect.top)
        {
            rect.top = y3;
        }
        if (y3 >= rect.bottom)
        {
            rect.bottom = y3;
        }
        float mStrokeWidth = (float) padding;
        rect.left = (int) (((float) rect.left) - mStrokeWidth);
        rect.right = (int) (((float) rect.right) + mStrokeWidth);
        rect.top = (int) (((float) rect.top) - mStrokeWidth);
        rect.bottom = (int) (((float) rect.bottom) + mStrokeWidth);
    }

    public boolean writeObject(OutputStream out) throws IOException
    {
        out.write(BaseUtils.intToByteArray(20171228));
        out.write(BaseUtils.floatToByte(mLastX));
        out.write(BaseUtils.floatToByte(mLastY));
        out.write(BaseUtils.floatToByte(cx));
        out.write(BaseUtils.floatToByte(cy));
        out.write(BaseUtils.floatToByte(midX));
        out.write(BaseUtils.floatToByte(midY));
        out.write(BaseUtils.floatToByte(width));
        return true;
    }

    public boolean readObject(InputStream in) throws IOException
    {
        byte[] bytes = new byte[4];
        int id = BaseUtils.readInputStreamInt(in, bytes);
        mLastX = BaseUtils.readInputStreamFloat(in, bytes);
        mLastY = BaseUtils.readInputStreamFloat(in, bytes);
        cx = BaseUtils.readInputStreamFloat(in, bytes);
        cy = BaseUtils.readInputStreamFloat(in, bytes);
        midX = BaseUtils.readInputStreamFloat(in, bytes);
        midY = BaseUtils.readInputStreamFloat(in, bytes);
        width = BaseUtils.readInputStreamFloat(in, bytes);
        return true;
    }
}
