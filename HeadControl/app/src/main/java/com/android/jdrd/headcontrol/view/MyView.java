package com.android.jdrd.headcontrol.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.Vector;

/**
 * Created by Administrator on 2017/1/18.
 */

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private static String TAG = "MyView";
    private SurfaceHolder holder=null; //控制对象
    public Vector<Float> xs=new Vector<Float>();
    public Vector<Float> ys=new Vector<Float>();
    public Bitmap bitmap = null;
    public int bitmap_x = 0 , bitmap_y = 0;
    public int rote = 0;
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
        Paint p=new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(Color.BLACK);
        p.setStyle(Paint.Style.STROKE);
        for(int i=0;i<xs.size();i++) {
            canvas.drawCircle(xs.elementAt(i), ys.elementAt(i), 5, p);
            if (i >= 1) {
                canvas.drawLine(xs.elementAt(i-1),ys.elementAt(i-1),xs.elementAt(i),ys.elementAt(i),p);
            }
        }
        if(bitmap!=null){
            Log.e(TAG,"bitmap_x = "+bitmap_x+" bitmap_y = "+bitmap_y+" bitmap.getWidth() = "+bitmap.getWidth()+" bitmap.getHeight() = "+bitmap.getHeight()+""+" rote = "+rote);
//            canvas.drawBitmap(bitmap,bitmap_x,bitmap_y,null);
            Matrix matrix = new Matrix();
            matrix.setTranslate(bitmap_x,bitmap_y);
            matrix.postRotate(rote, bitmap.getWidth()/2+bitmap_x,bitmap.getHeight()/2+bitmap_y);
            canvas.drawBitmap(bitmap,matrix,null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction()==MotionEvent.ACTION_DOWN){
            if( event.getX() > 100 && event.getX() < 500 && event.getY() > 100 && event.getY() < 800 ){
                xs.add(event.getX());
                ys.add(event.getY());
            }
            Log.e(TAG,"x = " +event.getX()+" y = "+event.getY());
        }
        return true;
    }

    class MyLoop implements Runnable{
        //熟悉游戏编程的应该很面熟吧，主循环
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(true){
                try{
                    Canvas c=holder.lockCanvas();
                    doDraw(c);
                    holder.unlockCanvasAndPost(c);
                    Thread.sleep(20);
                }catch(Exception e){

                }
            }
        }

    }

}