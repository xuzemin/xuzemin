package com.android.jdrd.headcontrol.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.jdrd.headcontrol.util.Contact;

import java.util.Vector;

/**
 * Created by Administrator on 2017/1/18.
 */

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder=null; //控制对象
    public Vector<Float> point_xs=new Vector<Float>();
    public Vector<Float> point_ys=new Vector<Float>();
    public Paint p;
    public int Scale = 100;
    public int myview_width,myview_height;
    public Vector<Float> path_xs=new Vector<Float>();
    public Vector<Float> path_ys=new Vector<Float>();
    public Bitmap bitmap = null;
    public float bitmap_x = 0 , bitmap_y = 0;
    public float center_x = 0 , center_y = 0;
    public int rote = 0;
    public Movie gifMovie;
    public boolean paint = false,Ishave = false;
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
        canvas.drawColor(Color.WHITE);//这里是绘制背景
        p=new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth((float) 2.0);
        for(int i=0;i<point_xs.size();i++) {
            canvas.drawCircle(point_xs.elementAt(i), point_ys.elementAt(i), 5, p);
            canvas.drawPoint(point_xs.elementAt(i),point_ys.elementAt(i),p);
            if (i >= 1) {
                canvas.drawLine(point_xs.elementAt(i-1),point_ys.elementAt(i-1),point_xs.elementAt(i),point_ys.elementAt(i),p);
            }
        }
        center_x = bitmap.getWidth()/2+bitmap_x;
        center_y = bitmap.getHeight()/2+bitmap_y;
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
        p.setColor(Color.BLUE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth((float) 2.0);
        for(int i=0;i<path_xs.size();i++) {
//            canvas.drawCircle(path_xs.elementAt(i), path_ys.elementAt(i), 3, p);
            if (i >= 1) {
                canvas.drawLine(path_xs.elementAt(i-1),path_ys.elementAt(i-1),path_xs.elementAt(i),path_ys.elementAt(i),p);
            }
        }
        drawtable(canvas);
        if(bitmap!=null){
            Matrix matrix = new Matrix();
            matrix.setTranslate(bitmap_x,bitmap_y);
            matrix.postRotate(rote, center_x,center_y);
            canvas.drawBitmap(bitmap,matrix,null);
        }
        showGifImage(canvas);
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
        gifMovie.draw(canvas, bitmap_x, bitmap_y);
        if ((now - mMovieStart) >= duration) {
            mMovieStart = 0;
            return true;
        }
        return false;
    }
    private void drawtable(Canvas canvas){
        p.setColor(Color.GRAY);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth((float) 0.5);

        Contact.debugLog("myview_height/Scale = "+ myview_height/Scale);

        for(int y=0;y <= (myview_height/Scale) ; y++) {
            canvas.drawLine(20,y*Scale+20,620,y*Scale+20,p);
        }

        Contact.debugLog("myview_width/Scale = "+ myview_width/Scale);

        for(int y=0;y <= (myview_width/Scale);y++) {
            canvas.drawLine(y*Scale+20,20,y*Scale+20,1020,p);
        }
    }
}