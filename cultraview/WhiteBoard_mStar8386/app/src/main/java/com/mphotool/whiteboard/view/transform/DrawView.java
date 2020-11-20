package com.mphotool.whiteboard.view.transform;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 自定义圆形小球view：手指在屏幕上滑动，红色的小球始终跟随手指移动。
 */
public class DrawView extends View {
    //初始化圆的位置
    public float currentX=40;
    public float currentY=50;
    public DrawView(Context context) {
        this(context,null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        this(context,null,0);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //创建画笔
        Paint paint=new Paint();
        //填充颜色
        paint.setColor(Color.BLUE);
        //开始画圆，float cx开始x的位置, float cy开始Y的位置, float radius圆的半径, @NonNull Paint paint画笔
        canvas.drawCircle(currentX,currentY,15,paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //getX获得点击x位置，getY获得点击Y的位置
        currentX= event.getX();
        currentY=event.getY();
        //重绘自身
        invalidate();
        //返回true自身消费
        return true;
    }
}


