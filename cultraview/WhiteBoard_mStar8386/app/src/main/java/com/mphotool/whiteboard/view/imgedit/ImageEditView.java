package com.mphotool.whiteboard.view.imgedit;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.activity.WhiteBoardApplication;
import com.mphotool.whiteboard.elements.Image;
import com.mphotool.whiteboard.utils.Constants;
import com.mphotool.whiteboard.view.PanelManager;

public class ImageEditView extends FrameLayout {
    String TAG = "ImageEditView";

    private static final long DEFAULT_TIME_INTERVAL = 500;
    private static final long TIME_INTERVAL_TOLERANT = 50;
    private static long sTimeEndTransform;
    private static long sTimeInterval = DEFAULT_TIME_INTERVAL;
    private static int sTouchState;
    private int[] mCropIndicatorResId = new int[]{R.drawable.image_crop_1_1, R.drawable.image_crop_1_2, R.drawable.image_crop_1_3, R.drawable.image_crop_2_1, R.drawable.image_crop_2_2, R.drawable.image_crop_3_1, R.drawable.image_crop_3_2, R.drawable.image_crop_3_3};
    private Drawable[] mCropIndicators;
    private Paint mCropInsideShadowPaint;
    private Paint mCropLinePaint;
    private LinearLayout mCropMenu;
    private Paint mCropOutsideShadowPaint;
    private EditObject mEditObject;
    private RectF mImageCropBounds;  //用于定位剪裁框
    private RectF mPreRectF;
    private RectF mTrueCropRect;  //剪裁完成之后，将剪裁框还原为真正的bitmap中的区域
    private Image mImage;
    Context mContext;

    private OnClickListener mOnClickListener = new OnClickListener() {
        public void onClick(View pView)
        {
            int id = pView.getId();
            if (id == R.id.lib_image_crop_cancel)
            {
                if (mOnImageEditListener != null)
                {
                    mOnImageEditListener.onCropCancel();
                }
            }
            else if (id == R.id.lib_image_crop_ok)
            {
                if (mOnImageEditListener != null)
                {
                    mOnImageEditListener.onCropOk(mImage, mPreRectF, mImageCropBounds);
                }
            }
        }
    };
    private OnImageEditListener mOnImageEditListener;
    private float mPrevX;
    private float mPrevY;
    private Mode mState = Mode.NONE;
    private RectF mTouchBounds;
    private int mTouchTolerance;

    public enum Mode {
        NONE,
        MOVE
    }

    public ImageEditView(Context context)
    {
        super(context);
        mContext = context;
        init();
    }

    public ImageEditView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ImageEditView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public void setImage(PanelManager manager, Image image)
    {
        mImage = image;
        mEditObject.changeMenu();

        RectF rect = new RectF(mImage.getActualRectF());
        mImageCropBounds.set((float) (rect.left - 1), (float) (rect.top - 1), (float) (rect.right + 1), (float) (rect.bottom + 1));
        mPreRectF = new RectF(mImageCropBounds);

        invalidate();
    }

    private void init()
    {
        int i;
        setWillNotDraw(false);
        LayoutInflater.from(mContext).inflate(R.layout.image_crop_menu_layout, this, true);
        mCropMenu = (LinearLayout) findViewById(R.id.lib_crop_menu_root);
        findViewById(R.id.lib_image_crop_cancel).setOnClickListener(mOnClickListener);
        findViewById(R.id.lib_image_crop_ok).setOnClickListener(mOnClickListener);

        mTouchBounds = new RectF();
        mImageCropBounds = new RectF();
        Resources res = mContext.getResources();
        mCropIndicators = new Drawable[mCropIndicatorResId.length];
        for (i = 0; i < mCropIndicators.length; i++)
        {
            mCropIndicators[i] = res.getDrawable(mCropIndicatorResId[i]);
        }
        mTouchTolerance = res.getDimensionPixelSize(R.dimen.edit_touch_tolerance);
        mEditObject = new EditObject(mContext, mCropMenu,mImageCropBounds);
        mCropInsideShadowPaint = new Paint();
        mCropInsideShadowPaint.setStyle(Style.FILL);
        mCropInsideShadowPaint.setColor(-1);
        mCropInsideShadowPaint.setAlpha(100);
        mCropOutsideShadowPaint = new Paint();
        mCropOutsideShadowPaint.setStyle(Style.FILL);
        mCropOutsideShadowPaint.setColor(ViewCompat.MEASURED_STATE_MASK);
        mCropOutsideShadowPaint.setAlpha(100);
        mCropLinePaint = new Paint();
        mCropLinePaint.setAntiAlias(true);
        mCropLinePaint.setStrokeWidth(1.0f);
        mCropLinePaint.setStyle(Style.STROKE);
        mCropLinePaint.setStrokeJoin(Join.ROUND);
        mCropLinePaint.setStrokeCap(Cap.ROUND);
        mCropLinePaint.setColor(Color.parseColor("#ffbbbbbb"));
    }

    protected void onDraw(Canvas canvas)
    {
        DrawingUtils.drawCropIndicators(canvas, mCropIndicators, 8, mImageCropBounds);
        float widthInterval = mImageCropBounds.width() / Constants.MAX_SCALE_SIZE;
        float heightInterval = mImageCropBounds.height() / Constants.MAX_SCALE_SIZE;
        canvas.drawLine(mImageCropBounds.left, mImageCropBounds.top + heightInterval, mImageCropBounds.right, mImageCropBounds.top + heightInterval, mCropLinePaint);
        Canvas canvas2 = canvas;
        canvas2.drawLine(mImageCropBounds.left, (heightInterval * 2.0f) + mImageCropBounds.top, mImageCropBounds.right, (heightInterval * 2.0f) + mImageCropBounds.top, mCropLinePaint);
        canvas.drawLine(mImageCropBounds.left + widthInterval, mImageCropBounds.top, mImageCropBounds.left + widthInterval, mImageCropBounds.bottom, mCropLinePaint);
        canvas2 = canvas;
        canvas2.drawLine((widthInterval * 2.0f) + mImageCropBounds.left, mImageCropBounds.top, (widthInterval * 2.0f) + mImageCropBounds.left, mImageCropBounds.bottom, mCropLinePaint);
        if (mImage != null)
        {
            RectF imageBounds = new RectF(mImageCropBounds);
            RectF screenBounds = new RectF(0.0f, 0.0f, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
            DrawingUtils.drawShadows(canvas, mCropInsideShadowPaint, mImageCropBounds, imageBounds);
            DrawingUtils.drawShadows(canvas, mCropOutsideShadowPaint, imageBounds, screenBounds);
            return;
        }
        return;
    }

    private void adjustPosition()
    {
        if (mImage != null)
        {
            mTouchBounds.set(mImageCropBounds.left - ((float) mTouchTolerance), mImageCropBounds.top - ((float) mTouchTolerance), mImageCropBounds.right + ((float) mTouchTolerance), mImageCropBounds.bottom + ((float) mTouchTolerance));
            mEditObject.moveFloatMenu(mImageCropBounds);
        }
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        if (System.currentTimeMillis() - sTimeEndTransform < sTimeInterval)
        {
            return true;
        }
        float x = event.getX();
        float y = event.getY();
        if (mState == Mode.NONE && !mTouchBounds.contains(x, y))
        {
            return false;
        }
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                sTouchState = 0;
                if (mState == Mode.NONE)
                {
                    int selectEdge = mEditObject.calculateSelectedEdge(x, y, mImageCropBounds);
                    if (!mEditObject.selectEdge(x, y, mImageCropBounds))
                    {
                        mEditObject.selectEdge(16);
                    }
                    mEditObject.setPrevDistance(mEditObject.computeLength(mImageCropBounds.left, mImageCropBounds.top, mImageCropBounds.right, mImageCropBounds.bottom));
                    mPrevX = x;
                    mPrevY = y;
                    mState = Mode.MOVE;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (sTouchState == 0)
                {
                    long startTime = System.currentTimeMillis();
                    if (mState == Mode.MOVE)
                    {
                        mEditObject.selectEdge(0);
                        mState = Mode.NONE;
                    }
                    sTimeEndTransform = System.currentTimeMillis();
                    sTimeInterval = (sTimeEndTransform - startTime) + TIME_INTERVAL_TOLERANT;
                    sTouchState = 3;
                    break;
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (sTouchState == 0)
                {
                    if (mState == Mode.MOVE)
                    {
                        float dx = x - mPrevX;
                        float dy = y - mPrevY;
                        mPrevX = x;
                        mPrevY = y;

//                        RectF rect = this.mEditObject.moveCurrentSelection(x, y, dx, dy);

                        mEditObject.moveCurrentCrop(x, y, dx, dy, mPreRectF);

                        break;
                    }
                }
                return true;
        }
        invalidate();
        return true;
    }

    public void setOnImageEditListener(OnImageEditListener pOnImageEditListener)
    {
        mOnImageEditListener = pOnImageEditListener;
    }

    public void show()
    {
        adjustPosition();
        mEditObject.setMenuVisible(true);
        invalidate();
        setVisibility(View.VISIBLE);
    }

    public void hide()
    {
        setVisibility(GONE);
    }

}
