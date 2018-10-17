package com.android.zbrobot.view;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.zbrobot.R;
import com.android.zbrobot.activity.ZB_CoordinateConfigActivity;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.util.Constant;
import com.android.zbrobot.util.RobotUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
/**
 *
 * Created by SJX on 2017/9/5.
 */

public class CoordinateView extends SurfaceView implements SurfaceHolder.Callback {
    public static int translate_x = 0,translate_y = 0;
    public Paint p;
    public static boolean isWrite = false;
    public static float scale = 1;
    public int myview_width,myview_height;
    public int point_width,point_height,screenwithPixel;
    private SurfaceHolder holder=null; //控制对象
    private float startX,startY,distance,newdistance;
    private static int TRANSLATE = 1;
    private float derection = 0,density;
    private static int SCALE = 2;
    private static int DEFAULT = 0;
    private int INDEX = DEFAULT;
    private DisplayMetrics outMetrics;
    public static int areaid = 0;
    private Bitmap backbitmap,pointbitmap;
    public static double point_x = 0,point_y = 0;
    private File file;
    private Rect mSrcRect, mDestRect;
    private Map areaconfig;
    public static double initial_x = 0,initial_y = 0;
    private RobotDBHelper robotDBHelper;
    private Context context;
    public CoordinateView(Context context , AttributeSet attr) {
        super(context);
        this.context = context;
        holder=getHolder();
        holder.addCallback(this);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        robotDBHelper = RobotDBHelper.getInstance(context);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        screenwithPixel = dm.widthPixels;         // 屏幕宽度（像素）
        density = dm.density;
        if(areaid !=0){
//            File appDir = new File(Environment.getExternalStorageDirectory(),Constant.DIR_NAME);
//            if(appDir.exists()){
//                String fileName = areaid + ".png";
//                // 查询机器人列表
//                List<Map> robotList = robotDBHelper.queryListMap("select * from area where id = '" + areaid + "'", null);
//                if(robotList!=null && robotList.size() >0){
//                    areaconfig = robotList.get(0);
//                    if(areaconfig !=null){
//                        if(areaconfig.get("pointx").toString() !=null && areaconfig.get("pointy").toString()!=null){
//                            initial_x = Double.valueOf(areaconfig.get("pointx").toString());
//                            initial_y = Double.valueOf(areaconfig.get("pointy").toString());
//                        }else{
//                            Toast.makeText(getContext(),"区域地图坐标未设置初始化",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
            List<Map> List = robotDBHelper.queryListMap("select * from area where id = '"+areaid+"'", null);
            if(List != null && List.size() >0){
                initial_x = Double.parseDouble(List.get(0).get("pointx").toString().trim()) * -1;
                initial_y = Double.parseDouble(List.get(0).get("pointy").toString().trim()) * -1;
            }
            file = new File(RobotUtils.fileName);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            backbitmap = BitmapFactory.decodeStream(fis);
            if(backbitmap !=null){
                Constant.Bitmap_HEIGHT = backbitmap.getHeight();
                Constant.Bitmap_WIDTH = backbitmap.getWidth();
//                myview_height = Constant.Bitmap_HEIGHT;
//                myview_width = Constant.Bitmap_WIDTH;
                Log.e("Logutil",Constant.Bitmap_WIDTH+ " bitmap "+ Constant.Bitmap_HEIGHT);
            }else{
                Toast.makeText(context,"区域未设置导航地图",Toast.LENGTH_SHORT).show();
            }

            InputStream is = getResources().openRawResource(R.mipmap.point1);
            pointbitmap = BitmapFactory.decodeStream(is);
            Log.e("Logutil",pointbitmap.getWidth()+ " point "+ pointbitmap.getHeight());

//            Resources r = this.getContext().getResources();
//            InputStream is = r.openRawResource(R.mipmap.point);
//            BitmapDrawable bmpDraw = new BitmapDrawable(is);
//            pointbitmap = bmpDraw.getBitmap();
            point_width = pointbitmap.getWidth();
            point_height = pointbitmap.getHeight();
        }
//        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!ZB_CoordinateConfigActivity.pointx.getText().toString().equals("") && !CoordinateView.isWrite){
            CoordinateView.point_x = ((Float.valueOf(ZB_CoordinateConfigActivity.pointx.getText().toString())+
                    CoordinateView.initial_x) * 20) * CoordinateView.scale;
        }
        if(!ZB_CoordinateConfigActivity.pointy.getText().toString().equals("") && !CoordinateView.isWrite) {
            CoordinateView.point_y = ((Float.valueOf(ZB_CoordinateConfigActivity.pointy.getText().toString()) / -1 -
                    CoordinateView.initial_y) * 20 + Constant.Bitmap_HEIGHT) * CoordinateView.scale;
        }
        //        backbitmap = null;
//        point_x = 0;
//        point_y = 0;
//        translate_x = 0;
//        translate_y = 0;
//        scale = 1;
        translate_x = (ZB_CoordinateConfigActivity.widthPixels-backbitmap.getWidth())/2;
        translate_y = (int) ((300*density-backbitmap.getWidth())/2);
        new Thread(new MyLoop()).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        backbitmap = null;
//        point_x = 0;
//        point_y = 0;
//        translate_x = 0;
//        translate_y = 0;
//        scale = 1;
    }

    protected void doDraw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(getResources().getColor(R.color.white));
//        Matrix matrix = new Matrix();
//        matrix.setScale(scale, scale);
//        matrix.postTranslate(translate_x/2,translate_y/2);
//        canvas.concat(matrix);
        p = new Paint();
        myview_width = (int) (Constant.Bitmap_WIDTH*scale);
        myview_height = (int) (Constant.Bitmap_HEIGHT*scale);
        if(backbitmap !=null){
            mSrcRect = new Rect(translate_x, translate_y, myview_width+translate_x, myview_height+translate_y);
            mDestRect = new Rect(0, 0, ZB_CoordinateConfigActivity.widthPixels, (int) myview_height);
            canvas.drawBitmap(backbitmap, null, mSrcRect, p);
//            canvas.drawBitmap(backbitmap, matrix, null);
        }
        Matrix matrix = new Matrix();
//        matrix.setScale( 1, 1);
        if(!ZB_CoordinateConfigActivity.derection.getText().toString().equals("")) {
            derection = Integer.valueOf(ZB_CoordinateConfigActivity.derection.getText().toString().trim()) * -1;

        }else{
            derection = 0;
        }
        matrix.postRotate((derection) % 360,
                point_width / 2, point_height / 2);
//        matrix.postRotate(90);
        matrix.postTranslate((float) (point_x+translate_x-point_width/2), (float) (point_y+translate_y-point_height/2));
//        matrix.postRotate(90);
        canvas.drawBitmap(pointbitmap, matrix, null);
//        if(!ZB_CoordinateConfigActivity.derection.getText().toString().equals("")){
//            mSrcRect = new Rect((int)(point_x + translate_x), (int)(point_y + translate_y), 15+(int)(point_x + translate_x), 15+(int)(point_y + translate_y));
//            mDestRect = new Rect(0, 0, 15, 15);
//            canvas.drawBitmap(pointbitmap, mDestRect, mSrcRect, p);
//        }

        p = new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(getResources().getColor(R.color.path));
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth((float) 22.0);
        p.setTextSize(22);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL ));
        if(point_x !=0 || point_y != 0){
            canvas.drawCircle( (float) (point_x + translate_x) , (float) (point_y + translate_y), 6, p);
        }
    }

    private class MyLoop implements Runnable{
        //熟悉游戏编程的应该很面熟吧，主循环
        @Override
        public void run() {
            Canvas c = null;
            while(true){
                try{
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int nCnt = event.getPointerCount();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                Log.e("logutil",startX+" st "+startY);
                Log.e("logutil",translate_x+" tr "+translate_y);
                Log.e("logutil"," scale "+scale);
                if(isWrite){
                    double x = ((startX - translate_x)/scale )/Constant.SCALE - initial_x ;
                    double y = (((startY - translate_y)/scale )/-1 + Constant.Bitmap_HEIGHT )/Constant.SCALE - initial_y;
                    Log.e("logutil",x+" xy "+y);
                    String x_text = String.valueOf(x);
                    String y_text = String.valueOf(y);
                    if (x_text.contains(".")) {
                        if (x_text.length() - 1 - x_text.indexOf(".") > 3) {
                            x_text = (String) x_text.subSequence(0,
                                    x_text.indexOf(".") + 4);
                        }
                    }
                    if (y_text.contains(".")) {
                        if (y_text.length() - 1 - y_text.indexOf(".") > 3) {
                            y_text = (String) y_text.subSequence(0,
                                    y_text.indexOf(".") + 4);
                        }
                    }

                    ZB_CoordinateConfigActivity.pointx.setText(x_text);
                    ZB_CoordinateConfigActivity.pointy.setText(y_text);
                }else{
                    INDEX = TRANSLATE;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if(event.getPointerCount() == 2){
                    distance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2) + Math.pow(event.getY(1) - event.getY(0), 2));
                }
                INDEX = SCALE;
                break;
            case MotionEvent.ACTION_MOVE:
                if(isWrite){
                    double x = (((event.getX()- translate_x)/scale) )/Constant.SCALE - initial_x ;
                    double y = (((event.getY()- translate_y)/scale )/-1 + Constant.Bitmap_HEIGHT )/Constant.SCALE - initial_y;

                    String x_text = String.valueOf(x);
                    String y_text = String.valueOf(y);
                    if (x_text.contains(".")) {
                        if (x_text.length() - 1 - x_text.indexOf(".") > 3) {
                            x_text = (String) x_text.subSequence(0,
                                    x_text.indexOf(".") + 4);
                        }
                    }
                    if (y_text.contains(".")) {
                        if (y_text.length() - 1 - y_text.indexOf(".") > 3) {
                            y_text = (String) y_text.subSequence(0,
                                    y_text.indexOf(".") + 4);
                        }
                    }

                    ZB_CoordinateConfigActivity.pointx.setText(x_text);
                    ZB_CoordinateConfigActivity.pointy.setText(y_text);
                }else {
                    if (nCnt == 1 && INDEX == TRANSLATE) {
                        Log.e("logutil",event.getX()+" event " + event.getY());
                        Log.e("logutil",translate_x+" tran " + translate_y);
                        Log.e("logutil",startX+" str " + startY);
                        translate_x += (event.getX() - startX);
                        translate_y += (event.getY() - startY);
                        startX = event.getX();
                        startY = event.getY();
                    } else if (nCnt == 2 && INDEX == SCALE) {
                        newdistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2) + Math.pow(event.getY(1) - event.getY(0), 2));
                        if (newdistance - distance > 40 && scale < 4) {
                            point_x = point_x / scale;
                            point_y = point_y / scale;
                            scale = (float) (scale + 0.1);
                            distance = newdistance;
                            point_x = point_x * scale;
                            point_y = point_y * scale;
                        } else if (newdistance - distance < -40 && scale > 1) {
                            point_x = point_x / scale;
                            point_y = point_y / scale;
                            scale = (float) (scale - 0.1);
                            distance = newdistance;
                            point_x = point_x * scale;
                            point_y = point_y * scale;
                        }
                        if (scale > 4) {
                            scale = 4;
                        } else if (scale < 1) {
                            scale = 1;
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                INDEX = DEFAULT;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                INDEX = DEFAULT;
                break;
            default:
                break;
        }
//        if(translate_x < myview_width - 2000 * scale){
//            translate_x = myview_width - 2000 * scale;
//        }
//        if(translate_x > 0){
//            translate_x = 0;
//        }
//        if(translate_y < myview_height - 1500 * scale){
//            translate_y = myview_height - 1500* scale;
//        }
//        if(translate_y > 0){
//            translate_y = 0;
//        }
        return true;
    }

    public void drawAL(Canvas canvas,Paint p,int fx, int fy, int sx, int sy) {
        double H = 8;
        double L = 3.5;
        int x3 = 0;
        int y3 = 0;
        int x4 = 0;
        int y4 = 0;
        double awrad = Math.atan(L / H);
        double arraow_len = Math.sqrt(L * L + H * H);
        double[] arrXY_1 = rotateVec(sx - fx, sy - fy, awrad, true, arraow_len);
        double[] arrXY_2 = rotateVec(sx - fx, sy - fy, -awrad, true, arraow_len);
        double x_3 = sx - arrXY_1[0];
        double y_3 = sy - arrXY_1[1];
        double x_4 = sx - arrXY_2[0];
        double y_4 = sy - arrXY_2[1];
        Double X3 = new Double(x_3);
        x3 = X3.intValue();
        Double Y3 = new Double(y_3);
        y3 = Y3.intValue();
        Double X4 = new Double(x_4);
        x4 = X4.intValue();
        Double Y4 = new Double(y_4);
        y4 = Y4.intValue();
        canvas.drawLine(fx, fy, sx, sy, p);
        Path triangle = new Path();
        triangle.moveTo(sx, sy);
        triangle.lineTo(x3, y3);
        triangle.lineTo(x4, y4);
        triangle.close();
        canvas.drawPath(triangle, p);

    }
    public double[] rotateVec(int px, int py, double ang, boolean isChLen, double newLen) {
        double mathstr[] = new double[2];
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
