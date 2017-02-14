package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.util.Contact;
import com.android.jdrd.headcontrol.view.MyView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by Administrator on 2017/2/7.
 */

public class MapFragment extends BaseFragment implements View.OnClickListener {

    private MyView surfaceview=null;
    private int bitmap_width = 0,bitmap_height= 0;
    //路线选择、找人时间、找人范围、转弯角度、互动时间
    private Spinner planchooce,serchtime,scope,angle,gametime;
    //路线选择、找人时间、找人范围、转弯角度、互动时间；（对应number）
    private int plannumber,serchtimenumber,scopenumber,anglenumber,gametimenumber;
    private Context context;
    private double nLenStart = 0;
    private EditText point_x,point_y;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> map_Plan;
    public static boolean Istouch = false ,Isplan = false;
    //路线模式布局、路线模式详细设置；
    private LinearLayout linear_plan,linear_plan_info,linear_point,linear_roam;

    private HashMap<String,Vector<String>> arrayPlan;
    private HashMap<String,HashMap<String,Vector<String>>> arrayPlanLists;
    public  MapFragment(){
        super();
    }
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Contact.debugLog("btn_sure");

                    break;
                case 2:
                    Contact.debugLog("btn_cancle");

                    break;
            }
            super.handleMessage(msg);
        }
    };

    @SuppressLint("ValidFragment")
    public MapFragment(Context context){
        super(context);
        this.context = context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {//给当前的fragment绘制UI布局，可以使用线程更新UI
        mView=inflater.inflate(R.layout.fragment_map,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        surfaceview=(MyView)findViewById(R.id.surfaceview);
        surfaceview.myview_height = 1000;
        surfaceview.myview_width = 600;
    }

    @Override
    public void initData() {
        surfaceview.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tuzi);

        InputStream is = getResources().openRawResource(R.mipmap.tuzi);
        surfaceview.gifMovie = Movie.decodeStream(is);
        surfaceview.bitmap = Bitmap.createBitmap(surfaceview.bitmap,0,0,100,100);

        bitmap_width = surfaceview.bitmap.getWidth();
        bitmap_height= surfaceview.bitmap.getHeight();

        Contact.debugLog("bitmap_width = " + bitmap_width +" bitmap_height = "+bitmap_height);
        Contact.debugLog("surfaceview.gifMovie.width() = " +surfaceview.gifMovie.width()+" surfaceview.gifMovie.height() = "+surfaceview.gifMovie.height());


        planchooce = (Spinner) findViewById(R.id.spinner_plan);
        serchtime = (Spinner) findViewById(R.id.serchtime);
        scope = (Spinner) findViewById(R.id.scope);
        angle = (Spinner) findViewById(R.id.angle);
        gametime = (Spinner) findViewById(R.id.gametime);
        point_x = (EditText) findViewById(R.id.point_x);
        point_y = (EditText) findViewById(R.id.point_y);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void initEvent() {
        linear_plan = (LinearLayout) findViewById(R.id.relative_plan);
        linear_plan_info = (LinearLayout) findViewById(R.id.relative_plan_info);
        linear_point = (LinearLayout)findViewById(R.id.linearlayout_point);
        linear_roam = (LinearLayout)findViewById(R.id.linearlayout_roam);
        findViewById(R.id.button_plan_back).setOnClickListener(this);
        findViewById(R.id.button_clearlast).setOnClickListener(this);
        findViewById(R.id.button_clearall).setOnClickListener(this);
        findViewById(R.id.button_plan).setOnClickListener(this);
        findViewById(R.id.button_pointchooce).setOnClickListener(this);
        findViewById(R.id.button_pathplan).setOnClickListener(this);
        findViewById(R.id.button_cruise).setOnClickListener(this);
        findViewById(R.id.button_savenext).setOnClickListener(this);
        findViewById(R.id.button_saveall).setOnClickListener(this);
        findViewById(R.id.button_execut).setOnClickListener(this);
        findViewById(R.id.button_next).setOnClickListener(this);
        findViewById(R.id.button_move).setOnClickListener(this);
        findViewById(R.id.button_roam_start).setOnClickListener(this);
        findViewById(R.id.button_roam_stop).setOnClickListener(this);

        surfaceview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int nCnt = event.getPointerCount();
                int n = event.getAction();

                if(n==MotionEvent.ACTION_DOWN){
                    if(Istouch){
//                        if(Isplan){
//                            surfaceview.point_xs.removeAllElements();
//                            surfaceview.point_ys.removeAllElements();
//                            Isplan = false;
//                        }
                        if( event.getX() > 20 && event.getX() < 620 && event.getY() > 20 && event.getY() < 1020 ){
                            float a,b;
                            int x = (int) ((event.getX() - 20) % surfaceview.Scale);
                            int x_int = (int) (event.getX() - 20) / surfaceview.Scale;
                            Contact.debugLog("surfaceview.Scale = " +surfaceview.Scale);
                            Contact.debugLog("x = " +x+" x_int = "+x_int);
                            int y =(int)  (event.getY() - 20) % surfaceview.Scale;
                            int y_int = (int)(event.getY() - 20) / surfaceview.Scale;
                            Contact.debugLog("x = " +x+" x_int = "+x_int);

                            if(x > (surfaceview.Scale/2)){
                                a = surfaceview.Scale * (x_int +1) +20;
                            }else {
                                a = surfaceview.Scale * x_int +20;
                            }
                            if(y > (surfaceview.Scale/2)){
                                b = surfaceview.Scale * (y_int +1) +20;
                            }else {
                                b = surfaceview.Scale * y_int +20;
                            }

                            surfaceview.point_xs.add(a);
                            surfaceview.point_ys.add(b);
//                            surfaceview.point_xs.add(event.getX());
//                            surfaceview.point_ys.add(event.getY());
                        }
                        Contact.debugLog("x = " +event.getX()+" y = "+event.getY());
                        Istouch = false;
                    }
                }

                //放大缩小
                if( (n&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt)//<span style="color:#ff0000;">2表示两个手指</span>
                {
//                    for(int i=0; i< nCnt; i++)
//                    {
//                        float x = event.getX(i);
//                        float y = event.getY(i);
//                        Point pt = new Point((int)x, (int)y);
//                    }
                    int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
                    int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));

                    nLenStart = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
                }else if( (n&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP  && 2 == nCnt)
                {
//                    for(int i=0; i< nCnt; i++)
//                    {
//                        float x = event.getX(i);
//                        float y = event.getY(i);
//                        Point pt = new Point((int)x, (int)y);
//                    }
                    int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
                    int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));
                    double nLenEnd = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
                    if(nLenEnd > nLenStart)//通过两个手指开始距离和结束距离，来判断放大缩小
                    {
                        Contact.debugLog("放大");
                        if(surfaceview.Scale < 200) {
                            surfaceview.Scale = surfaceview.Scale * 2;
                        }
                    }
                    else
                    {
                        Contact.debugLog("缩小");
                        if(surfaceview.Scale > 25){
                            surfaceview.Scale = surfaceview.Scale / 2;
                        }
                    }
                }
                return true;
            }
        });

        map_Plan = new ArrayList<>();
        map_Plan.add("默认路线");
        map_Plan.add("路线一");
        map_Plan.add("路线二");
        map_Plan.add("路线三");

        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,map_Plan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planchooce.setAdapter(adapter);
        planchooce.setOnItemSelectedListener(new SpinnerSelectedListener());

        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.button_left:
//                if (surfaceview.bitmap != null) {
//                    if (surfaceview.bitmap_x +bitmap_width/2  > 0) {
//                        surfaceview.bitmap_x -= 30;
//                    }
//                }
//                break;
//            case R.id.button_right:
//                if (surfaceview.bitmap != null) {
//                    if (surfaceview.bitmap_x + bitmap_width/2 < 570) {
//                        surfaceview.bitmap_x += 30;
//                    }
//                }
//                break;
//            case R.id.button_up:
//                if (surfaceview.bitmap != null) {
//                    if (surfaceview.bitmap_y + bitmap_height/2 > 0) {
//                        surfaceview.bitmap_y -= 30;
//                    }
//                }
//                break;
//            case R.id.button_down:
//                if (surfaceview.bitmap != null) {
//                    if (surfaceview.bitmap_y + bitmap_height/2 <= 870) {
//                        surfaceview.bitmap_y += 30;
//                    }
//                }
//                break;
//            case R.id.button_rote:
//                if (surfaceview.bitmap != null){
//                    surfaceview.rote+=45;
//                }
//                break;
            //清除上一步
            case R.id.button_clearlast:
                Istouch = true;
                if(surfaceview !=null&&surfaceview.point_xs.size()>=1){
                    surfaceview.point_xs.remove(surfaceview.point_xs.size()-1);
                    surfaceview.point_ys.remove(surfaceview.point_ys.size()-1);
                }
                break;
            //清除所有
            case R.id.button_clearall:
                if(surfaceview !=null){
                    surfaceview.point_xs.removeAllElements();
                    surfaceview.point_ys.removeAllElements();
                }
                break;
            //规划新路线
            case R.id.button_plan:
                Istouch = true;
                linear_plan_info.setVisibility(View.VISIBLE);
                break;
            //下一步
            case R.id.button_savenext:
                Istouch = true;

                break;
            //规划完毕
            case R.id.button_saveall:
                linear_plan_info.setVisibility(View.GONE);
                break;
            //执行路线
            case R.id.button_execut:
                if(Istouch){
                    Contact.showWarntext(context,handler);
                }

                break;
            //漫游模式
            case R.id.button_cruise:
                setVisible();
                linear_roam.setVisibility(View.VISIBLE);
                break;
            //路线规划模式
            case R.id.button_pathplan:
                setVisible();
                linear_plan.setVisibility(View.VISIBLE);
                break;
            //点选模式
            case R.id.button_pointchooce:
                setVisible();
                linear_point.setVisibility(View.VISIBLE);
                break;
            //点选下一步
            case R.id.button_next:

                break;
            //点选移动
            case R.id.button_move:
                if(point_x.getText()!=null&& !point_x.getText().toString().equals("") && point_y.getText()!=null&& !point_y.getText().toString().equals("")){

                }else{
                    Toast.makeText(context,"请输入正确的坐标",Toast.LENGTH_SHORT).show();
                }
                break;
            //开始漫游
            case R.id.button_roam_start:

                break;
            //停止漫游
            case R.id.button_roam_stop:

                break;
        }
    }

    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            Contact.debugLog("OnItemSelectedListener = "+arg2);
            plannumber = arg2;
        }
        public void onNothingSelected(AdapterView<?> arg0) {

        }
    }

    private void init(){
        map_Plan = new ArrayList<>();
        map_Plan.add("远");
        map_Plan.add("中");
        map_Plan.add("近");
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,map_Plan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scope.setAdapter(adapter);
        scope.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contact.debugLog("OnItemSelectedListener = "+position);
                scopenumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        map_Plan = new ArrayList<>();
        map_Plan.add("1分钟");
        map_Plan.add("5分钟");
        map_Plan.add("10分钟");
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,map_Plan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serchtime.setAdapter(adapter);
        serchtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contact.debugLog("OnItemSelectedListener = "+position);
                serchtimenumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        map_Plan = new ArrayList<>();
        map_Plan.add("30度");
        map_Plan.add("60度");
        map_Plan.add("90度");
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,map_Plan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        angle.setAdapter(adapter);
        angle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contact.debugLog("OnItemSelectedListener = "+position);
                anglenumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        map_Plan = new ArrayList<>();
        map_Plan.add("3分钟");
        map_Plan.add("6分钟");
        map_Plan.add("9分钟");
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,map_Plan);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gametime.setAdapter(adapter);
        gametime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Contact.debugLog("OnItemSelectedListener = "+position);
                gametimenumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setVisible(){
        linear_plan.setVisibility(View.GONE);
        linear_plan_info.setVisibility(View.GONE);
        linear_point.setVisibility(View.GONE);
        linear_roam.setVisibility(View.GONE);

    }
}
