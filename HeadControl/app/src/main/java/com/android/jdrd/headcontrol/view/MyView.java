package com.android.jdrd.headcontrol.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.jdrd.headcontrol.R;

import java.util.Vector;

/**
 * Created by Administrator on 2017/1/18.
 */

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder=null; //控制对象
    public Vector<Float> point_xs=new Vector<Float>();
    public Vector<Float> point_ys=new Vector<Float>();
    public Paint p;
    public int Scale = 50;
    public float scale = 1,translate_x = 0,translate_y = 0;
    public int myview_width,myview_height;
    public Vector<Double> path_xs=new Vector<Double>();
    public Vector<Double> path_ys=new Vector<Double>();
    public Bitmap bitmap = null,rotbitmap = null;
    public Double bitmap_x = 0.0 , bitmap_y = 0.0;
    public Double center_x = 0.0 , center_y = 0.0;
    public int rote = 45;
    public Movie gifMovie;
    public boolean paint = false,Ishave = false,Isplan = true,Ispath = false;
    public int temp = 0;
    public MyView(Context context, AttributeSet attr) {
        super(context,attr);
        // TODO Auto-generated constructor stub
        holder=getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        new Thread(new MyLoop()).start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }

    public void doDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Matrix matrix = new Matrix();
        matrix.postScale(scale,scale);
        matrix.postTranslate(translate_x,translate_y);
        canvas.concat(matrix);
        canvas.drawColor(getResources().getColor(R.color.darkgray));//这里是绘制背景

        p=new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(getResources().getColor(R.color.path));
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth((float) 5.0);
        for(int i=0;i<point_xs.size();i++) {
            if(Isplan){
                canvas.drawCircle(point_xs.elementAt(i), point_ys.elementAt(i), 9, p);
                canvas.drawPoint(point_xs.elementAt(i),point_ys.elementAt(i),p);
                if (i >= 1) {
                    drawAL(canvas,point_xs.elementAt(i-1),point_ys.elementAt(i-1),point_xs.elementAt(i),point_ys.elementAt(i),p);
                }
            }else{
                canvas.drawCircle(point_xs.elementAt(i), point_ys.elementAt(i), 10, p);
                canvas.drawPoint(point_xs.elementAt(i),point_ys.elementAt(i),p);
            }
        }
        center_x = bitmap.getWidth()/2+bitmap_x;
        center_y = bitmap.getHeight()/2+bitmap_y;
        if(Ispath){
            if(paint){
                Ishave = false;
                if(path_xs!=null) {
                    for (int i = 0; i < path_xs.size(); i++) {
                        if (path_xs.elementAt(i) == center_x && path_ys.elementAt(i) == center_y) {
                            Ishave = true;
                        }
                    }
                }
                if(!Ishave){
                    path_xs.add(bitmap.getWidth()/2+bitmap_x);
                    path_ys.add(bitmap.getHeight()/2+bitmap_y);
                }
                paint = false;
            }
        }
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth((float) 2.0);
        for(int i=0;i<path_xs.size();i++) {
//            canvas.drawCircle(path_xs.elementAt(i), path_ys.elementAt(i), 3, p);
            if (i >= 1) {
                drawAL(canvas,path_xs.elementAt(i-1),path_ys.elementAt(i-1),path_xs.elementAt(i),path_ys.elementAt(i),p);
            }
        }
        drawtable(canvas);
        if(rotbitmap!=null){
            matrix = new Matrix();
            matrix.setTranslate(Float.valueOf(String.valueOf(bitmap_x)),Float.valueOf(String.valueOf(bitmap_y)));
            matrix.postRotate(rote, Float.valueOf(String.valueOf(center_x)),Float.valueOf(String.valueOf(center_y)));
            canvas.drawBitmap(rotbitmap,matrix,null);
        }
        if(bitmap!=null) {
            matrix = new Matrix();
            matrix.setTranslate(Float.valueOf(String.valueOf(bitmap_x)),Float.valueOf(String.valueOf(bitmap_y)));
            matrix.postRotate(0,Float.valueOf(String.valueOf(center_x)),Float.valueOf(String.valueOf(center_y)));
            canvas.drawBitmap(bitmap, matrix, null);
        }

//        showGifImage(canvas);
    }


    class MyLoop implements Runnable{
        //熟悉游戏编程的应该很面熟吧，主循环
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(true){
                try{
                    if(temp == 5){
                        temp = 0;
                        paint = true;
                    }else{
                        temp ++;
                    }

                    Canvas c=holder.lockCanvas();
                    doDraw(c);
                    holder.unlockCanvasAndPost(c);
                    Thread.sleep(50);
                }catch(Exception e){

                }
            }
        }

    }
    private long mMovieStart;
    private boolean showGifImage(Canvas canvas) {
        //得到系统时间
        long now = SystemClock.uptimeMillis();
        if (mMovieStart == 0) {
            // 把开始时间设置为当前时间
            mMovieStart = now;
        }
        int duration = gifMovie.duration();
        if (duration == 0) {
            // 如果没有持续时间就设置为100
            duration = 100;
        }
        // 设置间隔时间
        int relTime = (int) ((now - mMovieStart) % duration);
        gifMovie.setTime(relTime);
        //在指定的位置进行绘制，这里是左上角
        gifMovie.draw(canvas,Float.valueOf(String.valueOf(bitmap_x)), Float.valueOf(String.valueOf(bitmap_y)));
        if ((now - mMovieStart) >= duration) {
            mMovieStart = 0;
            return true;
        }
        return false;
    }
    private void drawtable(Canvas canvas){
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth((float) 0.5);
        for(int y=0;y <= (myview_width/Scale) ; y++) {
            canvas.drawLine(20,y*Scale+20,1020,y*Scale+20,p);
        }
        for(int y=0;y <= (myview_width/Scale);y++) {
            canvas.drawLine(y*Scale+20,20,y*Scale+20,620,p);
        }
    }

    public void drawAL(Canvas canvas,double sx, double sy, double ex, double ey,Paint p)
    {
        double H = 20; // 箭头高度
        double L = 5; // 底边的一半
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        // 画线
        canvas.drawLine((float)sx, (float)sy, (float)ex, (float)ey,p);
        Path triangle = new Path();
        triangle.moveTo((float) ex, (float) ey);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle,p);
    }
    public double[] rotateVec(double px, double py, double ang, boolean isChLen, double newLen)
    {
        double mathstr[] = new double[2];
        // 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度、新长度
        double vx = px * Math.cos(ang) - py * Math.sin(ang);
        double vy = px * Math.sin(ang) + py * Math.cos(ang);
        if (isChLen) {
            double d = Math.sqrt(vx * vx + vy * vy);
            vx = vx / d * newLen;
            vy = vy / d * newLen;
            mathstr[0] = vx;
            mathstr[1] = vy;
        }
        return mathstr;
    }
}