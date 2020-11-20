package com.mphotool.whiteboard.view.menuviews;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.mphotool.whiteboard.R;
import com.mphotool.whiteboard.utils.Constants;

public class SlideButton extends View {
    private TriggerEventListener listener = null;
    private Bitmap mBlockBitmap = null;
    private int mBlockHeight = 0;
    private int mBlockWidth = 0;
    private float mBlockX = 0.0f;
    private float mBlockY = 0.0f;
    private Context mContext = null;
    private float mTouchDownX;

    public interface TriggerEventListener {
        void onTrigger(boolean z);
    }

    public SlideButton(Context context)
    {
        super(context);
        init(context);
    }

    public SlideButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context);
    }

    public SlideButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = 21)
    public SlideButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context)
    {
        mContext = context;
    }

    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (mBlockBitmap == null)
        {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.slider_clean);
            mBlockWidth = getHeight() - 2;
            mBlockHeight = getHeight() - 2;
            mBlockBitmap = Bitmap.createScaledBitmap(bitmap, mBlockWidth, mBlockHeight, true);
            bitmap.recycle();

        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(-1);
        canvas.drawBitmap(mBlockBitmap, mBlockX, mBlockY + Constants.BORDER_WIDTH_THIN, paint);
    }

    public static float getFontlength(Paint paint, String str)
    {
        return paint.measureText(str);
    }

    public static float getFontHeight(Paint paint)
    {
        FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public static float getFontLeading(Paint paint)
    {
        FontMetrics fm = paint.getFontMetrics();
        return fm.leading - fm.ascent;
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        boolean on = false;
        switch (event.getAction())
        {
            case 0:
                mTouchDownX = event.getX();
                Log.d("SlideButton", "ACTION_DOWN : mTouchDownX = " + mTouchDownX);
                break;
            case 1:
                ValueAnimator animator = ValueAnimator.ofInt(new int[]{(int) mBlockX, 0});
                animator.setDuration(500);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.addUpdateListener(new AnimatorUpdateListener() {
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        mBlockX = (float) ((Integer) animation.getAnimatedValue()).intValue();
                        postInvalidate();
                    }
                });
                animator.start();
                if (mBlockX >= ((float) (getWidth() - mBlockWidth) - 20))
                {
                    on = true;
                }
                listener.onTrigger(on);
                break;
            case 2:
                mBlockX = event.getX() - mTouchDownX;

                if (mBlockX < 0.0f)
                {
                    mBlockX = 0.0f;
                }
                if (mBlockX + ((float) mBlockWidth) > ((float) getWidth()))
                {
                    mBlockX = (float) (getWidth() - mBlockWidth);
                }
                postInvalidate();
                break;
        }
        return true;
    }

    public void setOnTriggerEventListener(TriggerEventListener triggerListener)
    {
        listener = triggerListener;
    }
}
