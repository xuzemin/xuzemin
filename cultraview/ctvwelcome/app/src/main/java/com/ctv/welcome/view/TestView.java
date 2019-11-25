package com.ctv.welcome.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class TestView extends View {
    public TestView(Context context) {
        super(context);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //设置画笔的基本属性
        Paint paint = new Paint();
        paint.setColor(Color.BLUE);//设置画笔颜色为蓝色
        paint.setStyle(Paint.Style.STROKE);//设置画笔的填充样式
        paint.setStrokeWidth(40);//设置画笔的宽度

         //绘制圆形
         //cx：圆心的x坐标。
         //cy：圆心的y坐标。
         //radius：圆的半径。
         //paint：绘制时所使用的画笔。
        canvas.drawCircle(190,200,150,paint);
        paint.setStrokeWidth(2);
      ///  canvas.drawLine(0,0,150,150,paint);


    }
}
