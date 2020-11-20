package com.mphotool.whiteboard.view.imgedit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.BaseUtils;


public class EditObject {
    private static final String TAG = "EditObject";
    public static final int BOTTOM_LEFT = 9;
    public static final int BOTTOM_RIGHT = 12;
    public static final int MOVE_BLOCK = 16;
    public static final int MOVE_BOTTOM = 8;
    public static final int MOVE_LEFT = 1;
    public static final int MOVE_NONE = 0;
    public static final int MOVE_RIGHT = 4;
    public static final int MOVE_TOP = 2;
    public static final int TOP_LEFT = 3;
    public static final int TOP_RIGHT = 6;
    private RectF mCropBounds;
    private LinearLayout mCropMenu;
    private int mMenuMargin;
    private float mMenuWidth;
    private int mMinSideSize;
    private int mMovingEdges = 0;
    private double mPrevDistance;
    private int mTouchTolerance;

    public EditObject(Context context, LinearLayout pCropMenu, RectF cropBounds)
    {
        Resources res = context.getResources();
        mCropMenu = pCropMenu;
        mMenuWidth = (float) context.getResources().getDimensionPixelSize(R.dimen.crop_menu_width);
        mCropBounds = cropBounds;
        mTouchTolerance = res.getDimensionPixelSize(R.dimen.edit_touch_tolerance);
        mMinSideSize = res.getDimensionPixelSize(R.dimen.edit_min_side);
        mMenuMargin = res.getDimensionPixelSize(R.dimen.crop_menu_margin);
        mCropMenu.setVisibility(View.GONE);
    }

    public boolean selectEdge(float x, float y, RectF bounds)
    {
        int edgeSelected = calculateSelectedEdge(x, y, bounds);
        Log.w(TAG, "calculateSelectedEdge = " + edgeSelected);
        if (edgeSelected == 0)
        {
            return false;
        }
        return selectEdge(edgeSelected);
    }

    public boolean selectEdge(int edge)
    {
        if (checkValid(edge))
        {
            mMovingEdges = edge;
            return true;
        }
        Log.w(TAG, "Bad edge selected");
        return false;
    }

    private boolean checkValid(int selected)
    {
        return selected == 0 || checkBlock(selected) || checkEdge(selected) || checkCorner(selected);
    }

    private boolean checkBlock(int selected)
    {
        return selected == MOVE_BLOCK;
    }

    private boolean checkEdge(int selected)
    {
        return selected == 1 || selected == 2 || selected == 4 || selected == 8;
    }

    private boolean checkCorner(int selected)
    {
        return selected == 3 || selected == 6 || selected == 12 || selected == 9;
    }

    public int calculateSelectedEdge(float x, float y, RectF bounds)
    {
        float left = Math.abs(x - bounds.left);
        float right = Math.abs(x - bounds.right);
        float top = Math.abs(y - bounds.top);
        float bottom = Math.abs(y - bounds.bottom);
        float centerX = Math.abs(x - (bounds.left + (bounds.width() / 2.0f)));
        float centerY = Math.abs(y - (bounds.top + (bounds.height() / 2.0f)));
        int edgeSelected = 0;
        if (left > ((float) mTouchTolerance) || ((float) mTouchTolerance) + y < bounds.top || y - ((float) mTouchTolerance) > bounds.bottom || left >= right)
        {
            if (right <= ((float) mTouchTolerance) && ((float) mTouchTolerance) + y >= bounds.top && y - ((float) mTouchTolerance) <= bounds.bottom && (top <= ((float) mTouchTolerance) || centerY <= ((float) mTouchTolerance) || bottom <= ((float) mTouchTolerance)))
            {
                edgeSelected = 0 | 4;
            }
        }
        else if (top <= ((float) mTouchTolerance) || centerY <= ((float) mTouchTolerance) || bottom <= ((float) mTouchTolerance))
        {
            edgeSelected = 0 | 1;
        }
        if (top > ((float) mTouchTolerance) || ((float) mTouchTolerance) + x < bounds.left || x - ((float) mTouchTolerance) > bounds.right || top >= bottom)
        {
            if (bottom > ((float) mTouchTolerance) || ((float) mTouchTolerance) + x < bounds.left || x - ((float) mTouchTolerance) > bounds.right)
            {
                return edgeSelected;
            }
            if (left <= ((float) mTouchTolerance) || centerX <= ((float) mTouchTolerance) || right <= ((float) mTouchTolerance))
            {
                return edgeSelected | 8;
            }
            return edgeSelected;
        }
        else if (left <= ((float) mTouchTolerance) || centerX <= ((float) mTouchTolerance) || right <= ((float) mTouchTolerance))
        {
            return edgeSelected | 2;
        }
        else
        {
            return edgeSelected;
        }
    }

    public void moveFloatMenu(RectF editBounds)
    {
        mCropMenu.setTranslationX(((editBounds.left + editBounds.right) - mMenuWidth) / 2.0f);
        mCropMenu.setTranslationY(editBounds.bottom + ((float) mMenuMargin));
    }

    public void setMenuVisible(boolean flag)
    {
        if (flag)
        {
            mCropMenu.setVisibility(View.VISIBLE);
        }
        else
        {
            mCropMenu.setVisibility(View.GONE);
        }
    }

    public void changeMenu()
    {
        setMenuVisible(false);
        moveFloatMenu(mCropBounds);
        setMenuVisible(true);
    }

    public void setPrevDistance(double pDistance)
    {
        mPrevDistance = pDistance;
    }

    public boolean moveCurrentCrop(float pointX, float pointY, float dX, float dY, RectF boundsRect)
    {
//        BaseUtils.dbg(TAG, "---------- moveCurrentCrop : mMovingEdges = " + mMovingEdges + " , 初始值： dx = " + dX + ", dy = " + dY);
//
//        BaseUtils.dbg(TAG, "moveCurrentCrop : 图片的 boundsRect = " + boundsRect.left + " / " + boundsRect.top + "/ " + boundsRect.right + " / " + boundsRect.bottom);
//
//        BaseUtils.dbg(TAG, "moveCurrentCrop : mCropBounds = " + mCropBounds.left + " / " + mCropBounds.top + "/ " + mCropBounds.right + " / " + mCropBounds.bottom);
        if (mMovingEdges == 0)
        {
            return false;
        }
        RectF crop = mCropBounds;
        if (mMovingEdges != MOVE_BLOCK)
        {
            float dx = 0.0f;
            float dy = 0.0f;
            if ((mMovingEdges & 1) != 0)
            {
                dx = Math.min(crop.left + dX, crop.right - ((float) mMinSideSize)) - crop.left;
//                Log.d(TAG, "MOVE_LEFT --- 换算后： dx = " + dx);
            }
            else if ((mMovingEdges & 4) != 0)
            {
                dx = Math.max(crop.right + dX, crop.left + ((float) mMinSideSize)) - crop.right;
            }
            if ((mMovingEdges & 2) != 0)
            {
                dy = Math.min(crop.top + dY, crop.bottom - ((float) mMinSideSize)) - crop.top;
            }
            else if ((mMovingEdges & 8) != 0)
            {
                dy = Math.max(crop.bottom + dY, crop.top + ((float) mMinSideSize)) - crop.bottom;
            }
            double currentDistance;
            float scale;
            switch (mMovingEdges)
            {
                case MOVE_LEFT:
//                    Log.d(TAG, "MOVE_LEFT start --- crop = " + crop.left + " / " + crop.top + "/ " + crop.right + " / " + crop.bottom);
                    if (crop.left + dx < boundsRect.left)
                    {
                        dx = boundsRect.left - crop.left;
                    }
                    crop.left += dx;
//                    Log.d(TAG, "MOVE_LEFT end --- crop = " + crop.left + " / " + crop.top + "/ " + crop.right + " / " + crop.bottom);

                    break;
                case MOVE_TOP:
                    if (crop.top + dy < boundsRect.top)
                    {
                        dy = boundsRect.top - crop.top;
                    }
                    crop.top += dy;
                    break;
                case TOP_LEFT:
                    currentDistance = computeLength(pointX, pointY, crop.right, crop.bottom);
                    scale = (float) (currentDistance / mPrevDistance);
//                    Log.d(TAG, "TOP_LEFT --- scale = " + scale);
                    if (crop.width() * scale <= ((float) mMinSideSize) || crop.height() * scale <= ((float) mMinSideSize))
                    {
                        scale = Math.max(((float) mMinSideSize) / crop.width(), ((float) mMinSideSize) / crop.height());
                    }
                    else if (crop.left + (crop.width() * (1.0f - scale)) <= boundsRect.left || crop.top + (crop.height() * (1.0f - scale)) <= boundsRect.top)
                    {
                        scale = Math.min((crop.right - boundsRect.left) / crop.width(), (crop.bottom - boundsRect.top) / crop.height());
                        mPrevDistance = currentDistance;
                    }
                    else
                    {
                        mPrevDistance = currentDistance;
                    }
                    crop.left += crop.width() * (1.0f - scale);
                    crop.top += crop.height() * (1.0f - scale);
                    crop.left = Math.max(crop.left, boundsRect.left);
                    crop.top = Math.max(crop.top, boundsRect.top);
                    break;
                case MOVE_RIGHT:
                    if (crop.right + dx > boundsRect.right)
                    {
                        dx = boundsRect.right - crop.right;
                    }
                    crop.right += dx;
                    break;
                case TOP_RIGHT:
                    currentDistance = computeLength(pointX, pointY, crop.left, crop.bottom);
                    scale = (float) (currentDistance / mPrevDistance);
//                    Log.d(TAG, "TOP_RIGHT --- scale = " + scale);
                    if (crop.width() * scale <= ((float) mMinSideSize) || crop.height() * scale <= ((float) mMinSideSize))
                    {
                        scale = Math.max(((float) mMinSideSize) / crop.width(), ((float) mMinSideSize) / crop.height());
                    }
                    else if (crop.right - (crop.width() * (1.0f - scale)) >= boundsRect.right || crop.top + (crop.height() * (1.0f - scale)) <= boundsRect.top)
                    {
                        scale = Math.min((crop.right - boundsRect.left) / crop.width(), (crop.bottom - boundsRect.top) / crop.height());
                        mPrevDistance = currentDistance;
                    }
                    else
                    {
                        mPrevDistance = currentDistance;
                    }
                    crop.right -= crop.width() * (1.0f - scale);
                    crop.top += crop.height() * (1.0f - scale);
                    crop.right = Math.min(crop.right, boundsRect.right);
                    crop.top = Math.max(crop.top, boundsRect.top);
                    break;
                case MOVE_BOTTOM:
                    if (crop.bottom + dy > boundsRect.bottom)
                    {
                        dy = boundsRect.bottom - crop.bottom;
                    }
                    crop.bottom += dy;
                    break;
                case BOTTOM_LEFT:
                    currentDistance = computeLength(pointX, pointY, crop.right, crop.top);
                    scale = (float) (currentDistance / mPrevDistance);
//                    Log.d(TAG, "BOTTOM_LEFT --- scale = " + scale);
                    if (crop.width() * scale <= ((float) mMinSideSize) || crop.height() * scale <= ((float) mMinSideSize))
                    {
                        scale = Math.max(((float) mMinSideSize) / crop.width(), ((float) mMinSideSize) / crop.height());
                    }
                    else if (crop.left + (crop.width() * (1.0f - scale)) <= boundsRect.left || crop.bottom - (crop.height() * (1.0f - scale)) >= boundsRect.bottom)
                    {
                        scale = Math.min((crop.right - boundsRect.left) / crop.width(), (crop.bottom - boundsRect.top) / crop.height());
                        mPrevDistance = currentDistance;
                    }
                    else
                    {
                        mPrevDistance = currentDistance;
                    }
                    crop.left += crop.width() * (1.0f - scale);
                    crop.bottom -= crop.height() * (1.0f - scale);
                    crop.left = Math.max(crop.left, boundsRect.left);
                    crop.bottom = Math.min(crop.bottom, boundsRect.bottom);
                    break;
                case BOTTOM_RIGHT:
                    currentDistance = computeLength(pointX, pointY, crop.left, crop.top);
                    scale = (float) (currentDistance / mPrevDistance);
//                    Log.d(TAG, "BOTTOM_RIGHT --- scale = " + scale);
                    if (crop.width() * scale <= ((float) mMinSideSize) || crop.height() * scale <= ((float) mMinSideSize))
                    {
                        scale = Math.max(((float) mMinSideSize) / crop.width(), ((float) mMinSideSize) / crop.height());
                    }
                    else if (crop.right - (crop.width() * (1.0f - scale)) >= boundsRect.right || crop.bottom - (crop.height() * (1.0f - scale)) >= boundsRect.bottom)
                    {
                        scale = Math.min((crop.right - boundsRect.left) / crop.width(), (crop.bottom - boundsRect.top) / crop.height());
                        mPrevDistance = currentDistance;
                    }
                    else
                    {
                        mPrevDistance = currentDistance;
                    }
                    crop.right -= crop.width() * (1.0f - scale);
                    crop.bottom -= crop.height() * (1.0f - scale);
                    crop.right = Math.min(crop.right, boundsRect.right);
                    crop.bottom = Math.min(crop.bottom, boundsRect.bottom);
                    break;
                default:
                    break;
            }
        }else{
            if (crop.left + dX < boundsRect.left)
            {
                dX = boundsRect.left - crop.left;
            }
            else if (crop.right + dX > boundsRect.right)
            {
                dX = boundsRect.right - crop.right;
            }
            if (crop.top + dY < boundsRect.top)
            {
                dY = boundsRect.top - crop.top;
            }
            else if (crop.bottom + dY > boundsRect.bottom)
            {
                dY = boundsRect.bottom - crop.bottom;
            }

            crop.offset(dX, dY);
        }



//        BaseUtils.dbg(TAG, "moveCurrentCrop : 临时 crop = " + crop.left + " / " + crop.top + "/ " + crop.right + " / " + crop.bottom);
//        BaseUtils.dbg(TAG, "moveCurrentCrop : 最终mCropBounds = " + mCropBounds.left + " / " + mCropBounds.top + "/ " + mCropBounds.right + " / " + mCropBounds.bottom);
        moveFloatMenu(mCropBounds);
        return true;
    }

    public RectF moveCurrentSelection(float pointX, float pointY, float dX, float dY) {
        BaseUtils.dbg(TAG, "moveCurrentSelection  mMovingEdges = " + mMovingEdges);
        if (mMovingEdges == 0) {
            return new RectF();
        }
        RectF finalRect = new RectF(mCropBounds);
////        mScaleMatrix.reset();
        if (mMovingEdges == MOVE_BLOCK) {
            return finalRect;
        }
        float dx = 0.0f;
        float dy = 0.0f;
        if ((mMovingEdges & 1) != 0) {
            dx = Math.min(finalRect.left + dX, finalRect.right - ((float) mMinSideSize)) - finalRect.left;
        } else if ((mMovingEdges & 4) != 0) {
            dx = Math.max(finalRect.right + dX, finalRect.left + ((float) mMinSideSize)) - finalRect.right;
        }
        if ((mMovingEdges & 2) != 0) {
            dy = Math.min(finalRect.top + dY, finalRect.bottom - ((float) mMinSideSize)) - finalRect.top;
        } else if ((mMovingEdges & 8) != 0) {
            dy = Math.max(finalRect.bottom + dY, finalRect.top + ((float) mMinSideSize)) - finalRect.bottom;
        }
        double currentDistance;
        float scale;
        switch (mMovingEdges) {
            case 1:
                finalRect.left += dx;
                return finalRect;
            case 2:
                finalRect.top += dy;
                return finalRect;
            case 3:
                currentDistance = computeLength(pointX, pointY, finalRect.right, finalRect.bottom);
                scale = (float) (currentDistance / mPrevDistance);
                if (finalRect.width() * scale <= ((float) mMinSideSize) || finalRect.height() * scale <= ((float) mMinSideSize)) {
                    scale = Math.max(((float) mMinSideSize) / finalRect.width(), ((float) mMinSideSize) / finalRect.height());
                } else {
                    mPrevDistance = currentDistance;
                }
                finalRect.left += finalRect.width() * (1.0f - scale);
                finalRect.top += finalRect.height() * (1.0f - scale);
                return finalRect;
            case 4:
                finalRect.right += dx;
                return finalRect;
            case 6:
                currentDistance = computeLength(pointX, pointY, finalRect.left, finalRect.bottom);
                scale = (float) (currentDistance / mPrevDistance);
                if (finalRect.width() * scale <= ((float) mMinSideSize) || finalRect.height() * scale <= ((float) mMinSideSize)) {
                    scale = Math.max(((float) mMinSideSize) / finalRect.width(), ((float) mMinSideSize) / finalRect.height());
                } else {
                    mPrevDistance = currentDistance;
                }
                finalRect.right -= finalRect.width() * (1.0f - scale);
                finalRect.top += finalRect.height() * (1.0f - scale);
                return finalRect;
            case 8:
                finalRect.bottom += dy;
                return finalRect;
            case 9:
                currentDistance = computeLength(pointX, pointY, finalRect.right, finalRect.top);
                scale = (float) (currentDistance / mPrevDistance);
                if (finalRect.width() * scale <= ((float) mMinSideSize) || finalRect.height() * scale <= ((float) mMinSideSize)) {
                    scale = Math.max(((float) mMinSideSize) / finalRect.width(), ((float) mMinSideSize) / finalRect.height());
                } else {
                    mPrevDistance = currentDistance;
                }
                finalRect.left += finalRect.width() * (1.0f - scale);
                finalRect.bottom -= finalRect.height() * (1.0f - scale);
                return finalRect;
            case 12:
                currentDistance = computeLength(pointX, pointY, finalRect.left, finalRect.top);
                scale = (float) (currentDistance / mPrevDistance);
                if (finalRect.width() * scale <= ((float) mMinSideSize) || finalRect.height() * scale <= ((float) mMinSideSize)) {
                    scale = Math.max(((float) mMinSideSize) / finalRect.width(), ((float) mMinSideSize) / finalRect.height());
                } else {
                    mPrevDistance = currentDistance;
                }
                finalRect.right -= finalRect.width() * (1.0f - scale);
                finalRect.bottom -= finalRect.height() * (1.0f - scale);
                return finalRect;
            default:
                return finalRect;
        }
    }

    public double computeLength(float x1, float y1, float x2, float y2)
    {
        return Math.sqrt((double) (((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))));
    }
}
