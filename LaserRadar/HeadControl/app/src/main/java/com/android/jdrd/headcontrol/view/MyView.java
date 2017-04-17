package com.android.jdrd.headcontrol.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.util.Constant;

import java.util.Vector;

/**
 * Created by Administrator on 2017/1/18.
 *
 */

public class MyView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder=null; //控制对象
    public Vector<Float> point_xs=new Vector<>();
    public Vector<Float> point_ys=new Vector<>();
    public Paint p;
    public float scalepoint = 14;
    public int config = 0 ;
    public float translate_x = 0,translate_y = 0;
    public double obstacle_x,obstacle_y,obstacle_back_x,obstacle_back_y,human_x,human_y;
    public int myview_width,myview_height;
    public Vector<Double> path_xs=new Vector<>();
    public Vector<Double> path_ys=new Vector<>();
    public Bitmap bitmap = null,rotbitmap = null,obstacle = null,human = null;
    public Double bitmap_x = 200.0 , bitmap_y = 200.0;
    public Double center_x = 0.0 , center_y = 0.0;
    public float rote = 45;
    public int current_plan_number = -1;
    public boolean paint = false,Ishave = false,Isplan = true,Ispath = false,IsHuman = false,Isshow = false;
    public int temp = 0,showtemp = 0;
    private Bitmap startpoint = BitmapFactory.decodeResource(getResources(), R.mipmap.qi);
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
        super.draw(canvas);
        Matrix matrix = new Matrix();
        matrix.postTranslate(translate_x,translate_y);
        canvas.concat(matrix);
        canvas.drawColor(getResources().getColor(R.color.darkgray));//这里是绘制背景

        p=new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(getResources().getColor(R.color.path));
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth((float) 5.0);
//        for(int i=0;i<point_xs.size();i++) {
//            if(Isplan){
//                canvas.drawCircle(point_xs.elementAt(i), point_ys.elementAt(i), 9, p);
//                canvas.drawPoint(point_xs.elementAt(i),point_ys.elementAt(i),p);
//                if( i == 0){
//                    canvas.drawBitmap(startpoint,point_xs.elementAt(i)-startpoint.getWidth()/2,point_ys.elementAt(i)-startpoint.getHeight()/2,p);
//                }else if (i >= 1) {
//                    drawAL(canvas,point_xs.elementAt(i-1),point_ys.elementAt(i-1),point_xs.elementAt(i),point_ys.elementAt(i),p);
//                }
//            }else{
//                canvas.drawCircle(point_xs.elementAt(i), point_ys.elementAt(i), 10, p);
//                canvas.drawPoint(point_xs.elementAt(i),point_ys.elementAt(i),p);
//            }
//        }
        center_x = bitmap.getWidth()/2+bitmap_x;
        center_y = bitmap.getHeight()/2+bitmap_y;
        if(Ispath){
            if(paint){
                Ishave = false;
                if(path_xs!=null) {
                    for (int i = 0; i < path_xs.size(); i++) {
                        if (path_xs.elementAt(i).equals(center_x) && path_ys.elementAt(i).equals(center_y)) {
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
        for(int i=0,length = path_xs.size();i<length;i++) {
            if (i >= 1) {
                drawAL(canvas,path_xs.elementAt(i-1),path_ys.elementAt(i-1),path_xs.elementAt(i),path_ys.elementAt(i),p);
            }
        }

        drawtable(canvas);

        p=new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(getResources().getColor(R.color.path));
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth((float) 22.0);
        p.setTextSize(22);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL ));
        for(int i=0 ,length = point_xs.size();i<length;i++) {
            if(Isplan){
                if(i < current_plan_number){
                    p.setColor(getResources().getColor(R.color.origen));
                }else{
                    if( i % 2 ==1){
                        p.setColor(getResources().getColor(R.color.path));
                    }else{
                        p.setColor(getResources().getColor(R.color.path_point));
                    }
                }
//                p.setColor(getResources().getColor(R.color.point));
//                if(i >=9){
//                    canvas.drawText((i + 1) + "", point_xs.elementAt(i) - 14, point_ys.elementAt(i) + 8, p);
//                }else {
//                    canvas.drawText((i + 1) + "", point_xs.elementAt(i) - 8, point_ys.elementAt(i) + 8, p);
//                }
//                p.setColor(getResources().getColor(R.color.path));
                if (i >= 1) {
//                    double x_end_tmp,y_end_tmp,x_start_tmp,y_start_tmp;
//                    double x = point_xs.elementAt(i)-point_xs.elementAt(i-1);
//                    double y = point_ys.elementAt(i)-point_ys.elementAt(i-1);
//                    double Distance = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
//                    if(point_xs.elementAt(i-1) > point_xs.elementAt(i)){
//                        x_end_tmp = point_xs.elementAt(i) + Math.abs(Constant.rote * x /Distance);
//                        x_start_tmp = point_xs.elementAt(i-1) - Math.abs(Constant.rote * x /Distance);
//                    }else if(point_xs.elementAt(i-1) < point_xs.elementAt(i)){
//                        x_end_tmp = point_xs.elementAt(i) - Math.abs(Constant.rote * x /Distance);
//                        x_start_tmp = point_xs.elementAt(i-1) + Math.abs(Constant.rote * x /Distance);
//                    }else{
//                        x_end_tmp = point_xs.elementAt(i);
//                        x_start_tmp = point_xs.elementAt(i-1);
//                    }
//                    if(point_ys.elementAt(i-1) > point_ys.elementAt(i)){
//                        y_end_tmp = point_ys.elementAt(i) + Math.abs(Constant.rote * y / Distance);
//                        y_start_tmp = point_ys.elementAt(i-1) - Math.abs(Constant.rote * y / Distance);
//                    } else if(point_ys.elementAt(i-1) < point_ys.elementAt(i)){
//                        y_end_tmp = point_ys.elementAt(i) - Math.abs(Constant.rote * y / Distance);
//                        y_start_tmp = point_ys.elementAt(i -1) + Math.abs(Constant.rote * y / Distance);
//                    }else{
//                        y_end_tmp = point_ys.elementAt(i);
//                        y_start_tmp = point_ys.elementAt(i -1);
//                    }
//                    drawAL(canvas,x_start_tmp,y_start_tmp,x_end_tmp,y_end_tmp,p);
                    drawAL(canvas,point_xs.elementAt(i-1),point_ys.elementAt(i -1),point_xs.elementAt(i),point_ys.elementAt(i),p);
                }
                if(i == point_xs.size() -1){
                    canvas.drawBitmap(startpoint,point_xs.elementAt(0)-startpoint.getWidth()/2,point_ys.elementAt(0)-startpoint.getHeight()/2,p);
                }
            }else{
                canvas.drawCircle(point_xs.elementAt(i), point_ys.elementAt(i), scalepoint, p);
                canvas.drawPoint(point_xs.elementAt(i),point_ys.elementAt(i),p);
            }
        }
        for(int i=0 ,length = point_xs.size();i<length;i++) {
            if (Isplan) {
                if(i>0){
                    p.setColor(getResources().getColor(R.color.point));
                    canvas.drawCircle(point_xs.elementAt(i), point_ys.elementAt(i), scalepoint, p);
                    p.setColor(getResources().getColor(R.color.title_dudutext_color));
                    if(i >=9){
                        canvas.drawText((i + 1) + "", point_xs.elementAt(i) - 13, point_ys.elementAt(i) + 8, p);
                    }else {
                        canvas.drawText((i + 1) + "", point_xs.elementAt(i) - 7, point_ys.elementAt(i) + 8, p);
                    }
                }
            }
        }

        if(null != rotbitmap){
            matrix = new Matrix();
            matrix.setTranslate(Float.valueOf(String.valueOf(bitmap_x)),Float.valueOf(String.valueOf(bitmap_y)));
            matrix.postRotate(rote-180, Float.valueOf(String.valueOf(center_x)),Float.valueOf(String.valueOf(center_y)));
            canvas.drawBitmap(rotbitmap,matrix,null);
        }
        if(null != bitmap) {
            matrix = new Matrix();
            matrix.setTranslate(Float.valueOf(String.valueOf(bitmap_x)),Float.valueOf(String.valueOf(bitmap_y)));
            canvas.drawBitmap(bitmap, matrix, null);
        }
        if(null != human){
            if(IsHuman){
                matrix = new Matrix();
                matrix.setTranslate((float)human_x,(float)human_y);
                canvas.drawBitmap(human, matrix, null);
            }
        }

        if(null != obstacle){
            if(Isshow){
                switch (config){
                    //无障碍
                    case 0:
                        break;
                    //前方有障碍
                    case 1:
                        matrix = new Matrix();
                        matrix.setTranslate((float) obstacle_x,(float) obstacle_y);
                        canvas.drawBitmap(obstacle, matrix, null);
                        break;
                    //后方有障碍
                    case 2:
                        matrix = new Matrix();
                        matrix.setTranslate((float) obstacle_back_x,(float) obstacle_back_y);
                        canvas.drawBitmap(obstacle, matrix, null);
                        break;
                    //前后方都有障碍
                    case 3:
                        matrix = new Matrix();
                        matrix.setTranslate((float) obstacle_x,(float) obstacle_y);
                        canvas.drawBitmap(obstacle, matrix, null);
                        matrix = new Matrix();
                        matrix.setTranslate((float) obstacle_back_x,(float) obstacle_back_y);
                        canvas.drawBitmap(obstacle, matrix, null);
                        break;
                }
            }
        }
    }


    private class MyLoop implements Runnable{
        //熟悉游戏编程的应该很面熟吧，主循环
        @Override
        public void run() {
            Canvas c = null ;
            while(true){
                try{
                    if(temp == 5){
                        temp = 0;
                        paint = true;
                    }else{
                        temp ++;
                    }
                    if(showtemp == 8){
                        if(Isshow){
                            Isshow = false;
                        }else{
                            Isshow = true;
                        }
                        showtemp = 0;
                    }else{
                        showtemp++;
                    }

                    c=holder.lockCanvas();
                    doDraw(c);
                    holder.unlockCanvasAndPost(c);
                    Thread.sleep(50);
                }catch(Exception e){
                    if(c != null){
                        holder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }

    private void drawtable(Canvas canvas){
        p = new Paint();
        p.setColor(Color.GRAY);
//        p.setColor(getResources().getColor(R.color.line));
        p.setStyle(Paint.Style.FILL);
        p.setTextSize(20);
        p.setStrokeWidth((float) 3.0);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL ));
        for(int y=0,length = myview_height/Constant.SCALE_NUMBER;y <= length ; y++) {
                if(y != 0){
                    canvas.drawText(y+"",15,y*Constant.SCALE_NUMBER+45,p);
                }else{
                    canvas.drawText(y+"",15,y*Constant.SCALE_NUMBER+30,p);
                }
                canvas.drawLine(40,y*Constant.SCALE_NUMBER+40,myview_width+40,y*Constant.SCALE_NUMBER+40,p);
        }
        for(int y=0,length = myview_width/Constant.SCALE_NUMBER;y <= length;y++) {
                canvas.drawLine(y*Constant.SCALE_NUMBER+40,40,y*Constant.SCALE_NUMBER+40,myview_height+40,p);
                if(y != 0){
                    canvas.drawText(y+"",y*Constant.SCALE_NUMBER+35,30,p);
                }
        }
    }

    public void drawAL(Canvas canvas,double sx, double sy, double ex, double ey,Paint p)
    {
        double H = 25; // 箭头高度
        double L = 10; // 底边的一半
        int x3 ;
        int y3 ;
        int x4 ;
        int y4 ;
        double awrad = Math.atan(L / H); // 箭头角度
        double arraow_len = Math.sqrt(L * L + H * H); // 箭头的长度
        double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
        double x_3 = ex - arrXY_1[0]; // (x3,y3)是第一端点
        double y_3 = ey - arrXY_1[1];
        double x_4 = ex - arrXY_2[0]; // (x4,y4)是第二端点
        double y_4 = ey - arrXY_2[1];
        Double X3 = x_3;
        x3 = X3.intValue();
        Double Y3 = y_3;
        y3 = Y3.intValue();
        Double X4 = x_4;
        x4 = X4.intValue();
        Double Y4 = y_4;
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