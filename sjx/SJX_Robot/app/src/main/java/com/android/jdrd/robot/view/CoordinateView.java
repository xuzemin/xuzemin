package com.android.jdrd.robot.view;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.jdrd.robot.R;
import com.android.jdrd.robot.activity.SJX_CoordinateConfigActivity;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
/**
 *
 * Created by SJX on 2017/9/5.
 */

public class CoordinateView extends SurfaceView implements SurfaceHolder.Callback {
    public float translate_x = 0,translate_y = 0;
    public Paint p;
    public static boolean isWrite = false;
    public static float scale = 1;
    public float myview_width,myview_height;
    private SurfaceHolder holder=null; //控制对象
    private float startX,startY,distance,newdistance;
    private static int TRANSLATE = 1;
    private static int SCALE = 2;
    private static int DEFAULT = 0;
    private int INDEX = DEFAULT;
    private DisplayMetrics outMetrics;
    public static int areaid = 0;
    private Bitmap backbitmap;
    public static double point_x = 0,point_y = 0;
    private File file;
    private Map areaconfig;
    public static double initial_x = 14.4,initial_y = 13.8;
    private RobotDBHelper robotDBHelper;
    private Context context;
    public CoordinateView(Context context , AttributeSet attr) {
        super(context);
        this.context = context;
        holder=getHolder();
        holder.addCallback(this);
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        myview_height = (300*outMetrics.density);
        myview_width = outMetrics.widthPixels;
        robotDBHelper = RobotDBHelper.getInstance(context);

        if(areaid !=0){
            File appDir = new File(Environment.getExternalStorageDirectory(),Constant.DIR_NAME);
            if(appDir.exists()){
                String fileName = areaid + ".png";
                // 查询机器人列表
                List<Map> robotList = robotDBHelper.queryListMap("select * from area where id = '" + areaid + "'", null);
                if(robotList!=null && robotList.size() >0){
                    areaconfig = robotList.get(0);
                    if(areaconfig !=null){
                        if(areaconfig.get("pointx").toString() !=null && areaconfig.get("pointy").toString()!=null){
                            initial_x = Double.valueOf(areaconfig.get("pointx").toString());
                            initial_y = Double.valueOf(areaconfig.get("pointy").toString());
                        }else{
                            Toast.makeText(getContext(),"区域地图坐标未设置初始化",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                file = new File(appDir, fileName);
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
                    wm.getDefaultDisplay().getMetrics(outMetrics);
                    point_x = point_x / scale;
                    point_y = point_y / scale;
                    scale = (float) (Math.sqrt(myview_height/backbitmap.getHeight()));
                    point_x = point_x * scale;
                    point_y = point_y * scale;
                }else{
                    Toast.makeText(context,"区域未设置导航地图",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if(!SJX_CoordinateConfigActivity.pointx.getText().toString().equals("") && !CoordinateView.isWrite){
            CoordinateView.point_x = ((Float.valueOf(SJX_CoordinateConfigActivity.pointx.getText().toString())+
                    CoordinateView.initial_x) * 20) * CoordinateView.scale;
        }
        if(!SJX_CoordinateConfigActivity.pointy.getText().toString().equals("") && !CoordinateView.isWrite) {
            CoordinateView.point_y = ((Float.valueOf(SJX_CoordinateConfigActivity.pointy.getText().toString()) / -1 -
                    CoordinateView.initial_y) * 20 + Constant.Bitmap_HEIGHT) * CoordinateView.scale;
        }
        new Thread(new MyLoop()).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        backbitmap = null;
        point_x = 0;
        point_y = 0;
        translate_x = 0;
        translate_y = 0;
        scale = 1;
    }

    protected void doDraw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(getResources().getColor(R.color.white));
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(translate_x,translate_y);
        canvas.concat(matrix);
        if(backbitmap !=null){
            canvas.drawBitmap(backbitmap, matrix, null);
        }
        p = new Paint(); //笔触
        p.setAntiAlias(true); //反锯齿
        p.setColor(getResources().getColor(R.color.path));
        p.setStyle(Paint.Style.FILL);
        p.setStrokeWidth((float) 22.0);
        p.setTextSize(22);
        p.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL ));
        if(point_x !=0 || point_y != 0){
            canvas.drawCircle( (float) (point_x + translate_x) , (float) (point_y + translate_y), 14, p);
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
                if(isWrite){
                    double x = ((startX )/scale/scale - translate_x)/Constant.SCALE - initial_x ;
                    double y = (((startY )/scale/scale -translate_y)/-1 + Constant.Bitmap_HEIGHT )/Constant.SCALE - initial_y;

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

                    SJX_CoordinateConfigActivity.pointx.setText(x_text);
                    SJX_CoordinateConfigActivity.pointy.setText(y_text);
//                    isWrite = false;
//                    SJX_CoordinateConfigActivity.map.setChecked(false);
                    point_y = ((y / -1 - initial_y) * 20 + Constant.Bitmap_HEIGHT) * scale ;
                    point_x = ((x + initial_x) * 20 ) * scale ;
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
                    double x = ((event.getX() )/scale/scale - translate_x)/Constant.SCALE - initial_x ;
                    double y = (((event.getY() )/scale/scale -translate_y)/-1 + Constant.Bitmap_HEIGHT )/Constant.SCALE - initial_y;

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

                    SJX_CoordinateConfigActivity.pointx.setText(x_text);
                    SJX_CoordinateConfigActivity.pointy.setText(y_text);
//                    isWrite = false;
//                    SJX_CoordinateConfigActivity.map.setChecked(false);
                    point_y = ((y / -1 - initial_y) * 20 + Constant.Bitmap_HEIGHT) * scale ;
                    point_x = ((x + initial_x) * 20 ) * scale ;
                }else {
                    if (nCnt == 1 && INDEX == TRANSLATE) {
                        translate_x += event.getX() - startX;
                        translate_y += event.getY() - startY;
                        startX = event.getX();
                        startY = event.getY();
                    } else if (nCnt == 2 && INDEX == SCALE) {
                        newdistance = (float) Math.sqrt(Math.pow(event.getX(1) - event.getX(0), 2) + Math.pow(event.getY(1) - event.getY(0), 2));
                        if (newdistance - distance > 40 && scale < 2) {
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
                        if (scale > 2) {
                            scale = 2;
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
        if(translate_x > 0){
            translate_x = 0;
        }
//        if(translate_y < myview_height - 1500 * scale){
//            translate_y = myview_height - 1500* scale;
//        }
        if(translate_y > 0){
            translate_y = 0;
        }
        return true;
    }
}
