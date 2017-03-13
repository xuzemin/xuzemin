package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.adapter.ChangeMapAdapter;
import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.dialog.MyDialog;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Constant;
import com.android.jdrd.headcontrol.view.MyView;
import com.jiadu.mapdemo.util.SerialPortUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Administrator on 2017/2/7.
 */

public class MapFragment extends BaseFragment implements View.OnClickListener,Animation.AnimationListener{

    private MyReceiver receiver = new MyReceiver();
    private IntentFilter filter =new IntentFilter();
    private Map map;
    private boolean IsX = false;
    private Double sendDegree;
    private float sendDistance;
    private static MyView surfaceview = null;
    //路线选择、找人时间、找人范围、转弯角度、互动时间
    private Spinner planchooce,serchtime,scope,angle,gametime,spinner_Scale,spinner_set_Scale;
    //路线选择、找人时间、找人范围、转弯角度、互动时间；（对应number）
    private float plannumber =0,serchtimenumber =0,scopenumber =0,anglenumber,gametimenumber =0,spinner_Scale_number= 0;
    private Context context;
    private ImageView imgViewmapnRight;
    private TextView bilici;
    private RelativeLayout map_right_Ralative;
    private double nLenStart = 0;
    private EditText point_x,point_y,go_point_x,go_point_y;
    private ArrayAdapter<String> adapter;
    private int scale = 1;
    private ArrayList<String> map_Plan;
    private float eventx,eventy;
    private ListView plan_change_list;
    private static float return_x = -2,return_y = -3 , a = 0, b = 0;
    public static boolean Istouch = false,Isplan = false,IsFind = false,IsAway = false,IsRight = false,IsSetReturn = false;
    //路线模式布局、路线模式详细设置；
    private LinearLayout linearlayout_map,linear_plan,linear_plan_info,linear_point,linear_roam,linearlayout_all,linearlayout_plan_change;
    private HashMap<String,Vector<Float>> arrayhash;
    private Vector<Float> xs,ys,arrayserchtime,arrayscope,arrayangle,arraygametime,scale_number;
    private Vector<Float> xs_tmp = new Vector<>();
    private Vector<Float> ys_tmp  = new Vector<>();
    private Vector<Float> arrayserchtime_tmp = new Vector<>();
    private Vector<Float> arrayscope_tmp = new Vector<>();
    private Vector<Float> arrayangle_tmp = new Vector<>();
    private Vector<Float> arraygametime_tmp = new Vector<>();
    private Timer timer;
    private ChangeMapAdapter ChangeMapAdapter;
    private int tasknumber = -1;
    private TimerTask task;
    private HashMap<String,HashMap<String,Vector<Float>>> arrayPlanLists = new HashMap<>();
    private Vector<String> strings = new Vector<>();
    public  MapFragment(){
        super();
    }

    private Thread thread = null ;

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(surfaceview !=null && surfaceview.point_xs.size()>=1){
                        surfaceview.point_xs.removeAllElements();
                        surfaceview.point_ys.removeAllElements();
                        arrayserchtime.removeAllElements();
                        arrayscope.removeAllElements();
//                        arrayangle.removeAllElements();
                        arraygametime.removeAllElements();
                    }
                    updatekey();
                    linear_plan_info.setVisibility(View.GONE);
                    linear_plan.setVisibility(View.VISIBLE);
                    Istouch = false;
                    break;
                case 2:
                    Constant.getConstant().debugLog("btn_cancle");
                    break;
                case 3:
                    if(thread!=null){
                        Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                        if(thread.isAlive()){
                            thread = new Thread();
                        }
                        Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    }
//                    sendNativePoint();
                    findViewById(R.id.button_execut).setClickable(true);
                    findViewById(R.id.button_plan_stop).setClickable(false);
                    break;
                case 4:
                    Constant.debugLog("hander"+4);
                    if(thread!=null){
                        synchronized (thread) {
                            try {
                                thread.sleep(1000);
                                thread.notify();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case 5:
                    ChangeMapAdapter.update((Integer) msg.obj,plan_change_list);
                default:
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //给当前的fragment绘制UI布局，可以使用线程更新UI
        mView=inflater.inflate(R.layout.fragment_map,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        try {
            Constant.spu.openSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        surfaceview=(MyView)findViewById(R.id.surfaceview);
        surfaceview.myview_height = 900;
        surfaceview.myview_width = 1800;
        Constant.Current_x = 0.0;
        Constant.Current_y = 0.0;
    }

    @Override
    public void initData() {
        surfaceview.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.touxiang);
        surfaceview.rotbitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.jiantou);
        planchooce = (Spinner) findViewById(R.id.spinner_plan);
        serchtime = (Spinner) findViewById(R.id.serchtime);
        spinner_Scale = (Spinner) findViewById(R.id.spinner_Scale);
        scope = (Spinner) findViewById(R.id.scope);
        gametime = (Spinner) findViewById(R.id.gametime);
        point_x = (EditText) findViewById(R.id.point_x);
        point_y = (EditText) findViewById(R.id.point_y);
        go_point_x = (EditText) findViewById(R.id.go_point_x);
        go_point_y = (EditText) findViewById(R.id.go_point_y);
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void initEvent() {
        plan_change_list = (ListView) findViewById(R.id.plan_change_list);
        bilici = (TextView) findViewById(R.id.bilici);
        imgViewmapnRight = (ImageView) findViewById(R.id.imgViewmapnRight);
        map_right_Ralative = (RelativeLayout) findViewById(R.id.map_right_Ralative);
        linearlayout_all = (LinearLayout) findViewById(R.id.linearlayout_all);
        linearlayout_plan_change = (LinearLayout) findViewById(R.id.relative_plan_change);
        linearlayout_map = (LinearLayout) findViewById(R.id.linearlayout_map);
        linear_plan = (LinearLayout) findViewById(R.id.relative_plan);
        linear_plan_info = (LinearLayout) findViewById(R.id.relative_plan_info);
        linear_point = (LinearLayout)findViewById(R.id.linearlayout_point);
        linear_roam = (LinearLayout)findViewById(R.id.linearlayout_roam);
        imgViewmapnRight.setOnClickListener(this);
        findViewById(R.id.plan_change).setOnClickListener(this);
        findViewById(R.id.button_plan_change_save).setOnClickListener(this);
        findViewById(R.id.button_plan_change_back).setOnClickListener(this);
        findViewById(R.id.button_choose).setOnClickListener(this);
        findViewById(R.id.go_button_choose).setOnClickListener(this);
        findViewById(R.id.button_clearlast).setOnClickListener(this);
        findViewById(R.id.button_clearall).setOnClickListener(this);
        findViewById(R.id.button_plan).setOnClickListener(this);
        findViewById(R.id.button_plan_stop).setOnClickListener(this);
        findViewById(R.id.button_pointchooce).setOnClickListener(this);
        findViewById(R.id.button_pathplan).setOnClickListener(this);
        findViewById(R.id.button_cruise).setOnClickListener(this);
        findViewById(R.id.button_saveall).setOnClickListener(this);
        findViewById(R.id.button_execut).setOnClickListener(this);
        findViewById(R.id.button_next).setOnClickListener(this);
        findViewById(R.id.button_roam_start).setOnClickListener(this);
        findViewById(R.id.button_roam_stop).setOnClickListener(this);
        findViewById(R.id.button_setreturn).setOnClickListener(this);
        findViewById(R.id.button_remove).setOnClickListener(this);
        findViewById(R.id.button_plan_back).setOnClickListener(this);
        findViewById(R.id.button_plan_info_back).setOnClickListener(this);
        findViewById(R.id.button_point_back).setOnClickListener(this);
        findViewById(R.id.button_roam_back).setOnClickListener(this);
        surfaceview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int nCnt = event.getPointerCount();
                int n = event.getAction();

                if(n==MotionEvent.ACTION_DOWN && 1 == nCnt){
                    eventx = event.getX();
                    eventy = event.getY();

                    if (Istouch) {
                        if (eventx > 70 && eventx < surfaceview.myview_width - 80 && eventy > 70 && eventy < surfaceview.myview_width - 80) {
                            a = (event.getX()  - surfaceview.translate_x)  ;
                            b = (event.getY()  - surfaceview.translate_y)  ;
                            int x = (int) a % surfaceview.Scale;
                            int x_int = (int) a / surfaceview.Scale;
                            int y = (int) b % surfaceview.Scale;
                            int y_int = (int) b / surfaceview.Scale;
                            if (x > (surfaceview.Scale / 2)) {
                                a = surfaceview.Scale * (x_int + 1) ;
                            } else {
                                a = surfaceview.Scale * x_int ;
                            }
                            if (y > (surfaceview.Scale / 2)) {
                                b = surfaceview.Scale * (y_int + 1) ;
                            } else {
                                b = surfaceview.Scale * y_int ;
                            }
                            Constant.debugLog(a+"    "+b);
                            if(IsSetReturn){
                                return_x = a;
                                return_y = b;
                                IsSetReturn = false;
                                Isplan = false;
                                Istouch = false;
                            }else if(!IsSetReturn&&Isplan){
                                if(surfaceview.point_xs.size() >0
                                        && a == surfaceview.point_xs.get(surfaceview.point_xs.size()-1)
                                        && b == surfaceview.point_ys.get(surfaceview.point_ys.size()-1)){

                                }else {
                                    surfaceview.point_xs.add(a);
                                    surfaceview.point_ys.add(b);
                                    arrayserchtime.add(serchtimenumber);
                                    arrayscope.add(scopenumber);
                                    arraygametime.add(gametimenumber);
                                    xs.add(a * Constant.Scale);
                                    ys.add(b * Constant.Scale);
                                    surfaceview.Isplan = true;
                                }
                            }else{
//                                sendNativePoint(a,b,0);
                                //发送坐标
                                surfaceview.point_xs.removeAllElements();
                                surfaceview.point_ys.removeAllElements();
                                surfaceview.point_xs.add(a);
                                surfaceview.point_ys.add(b);
                                surfaceview.Isplan = false;
                                Istouch = false;
                            }
                        }
                    } else {

                    }
                }else if(n==MotionEvent.ACTION_MOVE&&1 == nCnt){
                }

                //放大缩小
                if( (n&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt)//<span style="color:#ff0000;">2表示两个手指</span>
                {
                }else if( (n&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP  && 2 == nCnt)
                {
                    int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
                    int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));
                    double nLenEnd = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
                }
                return true;
            }
        });

        planchooce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(Istouch){
                    Constant.getConstant().showWarntext(context,handler);
                }else {
                    plannumber = position;
                    Constant.Scale = 1 ;
                    readXML();
                    spinner_Scale.setSelection(0,true);
                    xs_tmp.removeAllElements();
                    ys_tmp.removeAllElements();
                    HashMap<String, Vector<Float>> array = arrayPlanLists.get(strings.get((int) plannumber));
                    xs_tmp = array.get("point_xs");
                    ys_tmp = array.get("point_ys");
                    scale_number = array.get("scale_number");
                    Constant.debugLog("scale_number = " + scale_number);
                    surfaceview.point_xs.removeAllElements();
                    surfaceview.point_ys.removeAllElements();

                    if(scale_number.elementAt(0) == 100){
                        if(spinner_Scale_number !=3){
                            spinner_Scale.setSelection(3,true);
                        }else{
                            reset_surface();
                            Constant.Scale=100;
                            set_surface();
                            bilici.setText("1:100米");
                            surfaceview.point_xs = xs_tmp;
                            surfaceview.point_ys = ys_tmp;
                        }
                    }else if(scale_number.elementAt(0) == 10){
                        if(spinner_Scale_number !=2){
                            spinner_Scale.setSelection(2,true);
                        }else{
                            reset_surface();
                            Constant.Scale=10;
                            set_surface();
                            bilici.setText("1:10米");
                            surfaceview.point_xs = xs_tmp;
                            surfaceview.point_ys = ys_tmp;
                        }
                    }else if(scale_number.elementAt(0) == 2){
                        if(spinner_Scale_number !=1){
                            spinner_Scale.setSelection(1,true);
                        }else{
                            reset_surface();
                            Constant.Scale=2;
                            set_surface();
                            bilici.setText("1:2米");
                            surfaceview.point_xs = xs_tmp;
                            surfaceview.point_ys = ys_tmp;
                        }

                    }else if(scale_number.elementAt(0) == 1){
                        if(spinner_Scale_number !=0){
                            spinner_Scale.setSelection(0,true);
                        }else{
                            reset_surface();
                            Constant.Scale=1;
                            set_surface();
                            bilici.setText("1:1米");
                            surfaceview.point_xs = xs_tmp;
                            surfaceview.point_ys = ys_tmp;
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        filter.addAction("com.jdrd.fragment.Map");
        if(context!=null && receiver != null && filter != null){
            context.registerReceiver(receiver,filter);
        }
        init();
        updatekey();
        if(dialog != null){
            dialog.dismiss();
        }
        linearlayout_map.setVisibility(View.VISIBLE);
        linear_roam.setVisibility(View.GONE);
        linear_plan.setVisibility(View.GONE);
        linear_plan_info.setVisibility(View.GONE);
        linear_point.setVisibility(View.GONE);
        Istouch = false;
        Isplan = false;

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(context!=null && receiver !=null) {
            context.unregisterReceiver(receiver);
        }
        Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //清除上一步
            case R.id.button_clearlast:
                if(surfaceview !=null&&surfaceview.point_xs.size()>=1){
                    if(surfaceview.point_xs.size() == arrayserchtime.size()){
                        surfaceview.point_xs.remove(surfaceview.point_xs.size()-1);
                        surfaceview.point_ys.remove(surfaceview.point_ys.size()-1);
                        xs.remove(xs.size()-1);
                        ys.remove(ys.size()-1);
                        arrayserchtime.remove(arrayserchtime.size()-1);
                        arrayscope.remove(arrayscope.size()-1);
                        arraygametime.remove(arraygametime.size()-1);
                    }else{
                        surfaceview.point_xs.remove(surfaceview.point_xs.size()-1);
                        surfaceview.point_ys.remove(surfaceview.point_ys.size()-1);
                    }
                }
                Istouch = true;
                break;
            //清除所有
            case R.id.button_clearall:
                if(surfaceview!=null){
                    surfaceview.point_xs = new Vector<>();
                    surfaceview.point_ys = new Vector<>();
                    xs = new Vector<>();
                    ys = new Vector<>();
                    arrayserchtime = new Vector<>();
                    arrayscope = new Vector<>();
                    arraygametime = new Vector<>();
                    scale_number = new Vector<Float>();
                }
                break;
            //规划新路线
            case R.id.button_plan:
                arrayserchtime = new Vector<Float>();
                arrayscope = new Vector<Float>();
                arraygametime = new Vector<Float>();
                xs = new Vector<Float>();
                ys = new Vector<Float>();
                surfaceview.point_xs = new Vector<Float>();
                surfaceview.point_ys = new Vector<Float>();
                Istouch = true;
                linear_plan_info.setVisibility(View.VISIBLE);
                linear_plan.setVisibility(View.GONE);
                break;
            //删除当前路线
            case R.id.button_remove:
                if(Istouch){
                    Constant.getConstant().showWarntext(context,handler);
                }else {
                    if(arrayPlanLists.size() > 0){
                        readXML();
                        arrayPlanLists.remove(strings.get((int) plannumber));
                        writeXML();
                        updatekey();
                    }
                }
                break;
            //停止执行
            case R.id.button_plan_stop:
                handler.sendEmptyMessage(3);
                findViewById(R.id.button_execut).setClickable(true);
                findViewById(R.id.button_plan_stop).setClickable(false);
                break;
            //执行路线
            case R.id.button_execut:
                startPlan();
                findViewById(R.id.button_execut).setClickable(false);
                findViewById(R.id.button_plan_stop).setClickable(true);
                Constant.getConstant().showWarn(context,handler);
                break;
            //规划完毕
            case R.id.button_saveall:
                readXML();
                arrayhash = new HashMap<>();
                scale_number = new Vector<>();
                if(scale_number.size()<=0){
                    scale_number.add(Constant.Scale);
                }else {
                    scale_number.setElementAt(Constant.Scale,0);
                }
                arrayhash.put("scale_number",scale_number);
                if(surfaceview.point_xs.size()> 0 ){
                    arrayhash.put("point_xs",xs);
                    arrayhash.put("point_ys",ys);
                    arrayhash.put("arrayserchtime",arrayserchtime);
                    arrayhash.put("arrayscope",arrayscope);
                    arrayhash.put("arraygametime",arraygametime);
                    dialog();
                }else{
                    Toast.makeText(context,"没有规划任何路线",Toast.LENGTH_SHORT).show();
                }
                break;
            //漫游模式
            case R.id.button_cruise:
                Isplan = false;
                Istouch = true;
                setVisible();
                linear_roam.setVisibility(View.VISIBLE);
                linearlayout_map.setVisibility(View.GONE);
                break;
            //路线规划模式
            case R.id.button_pathplan:
                surfaceview.Isplan = true;
                setVisible();
                linear_plan.setVisibility(View.VISIBLE);
                linearlayout_map.setVisibility(View.GONE);
                updatekey();
                Constant.Current_x = 0.0;
                Constant.Current_y = 0.0;
                Istouch = false;
                Isplan = true;
                break;
            //点选模式
            case R.id.button_pointchooce:
                surfaceview.Isplan = false;
                Istouch = true;
                setVisible();
                linear_point.setVisibility(View.VISIBLE);
                linearlayout_map.setVisibility(View.GONE);
                Isplan = false;
                break;
            case R.id.button_setreturn:
                Istouch = true;
                IsSetReturn = true;
                break;
            //点选下一步
            case R.id.button_next:
                Istouch = true;
                break;
            //开始漫游
            case R.id.button_roam_start:
                findViewById(R.id.button_roam_stop).setClickable(true);
                findViewById(R.id.button_roam_start).setClickable(false);
                startRoam();
                break;
            //停止漫游
            case R.id.button_roam_stop:
                findViewById(R.id.button_roam_stop).setClickable(false);
                findViewById(R.id.button_roam_start).setClickable(true);
                handler.sendEmptyMessage(3);
                break;
            case R.id.button_plan_back:
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_plan.setVisibility(View.GONE);
                surfaceview.point_xs.removeAllElements();
                surfaceview.point_ys.removeAllElements();
                Isplan = false;
                break;
            case R.id.button_plan_info_back:
                Constant.getConstant().showWarntext(context, handler);
                break;
            case R.id.button_point_back:
                Istouch = false;
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_point.setVisibility(View.GONE);
                surfaceview.point_xs.removeAllElements();
                surfaceview.point_ys.removeAllElements();
                break;
            case R.id.button_roam_back:
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_roam.setVisibility(View.GONE);

                break;
            case R.id.imgViewmapnRight:
                startAnimationRight();
                break;
            case R.id.button_choose:
                if(!point_x.getText().toString().equals("") && !point_y.getText().toString().equals("") ){
                    Istouch = false;
                    a = Float.valueOf(point_x.getText().toString().trim());
                    b = Float.valueOf(point_y.getText().toString().trim());
                    a = a * surfaceview.Scale  ;
                    b = b * surfaceview.Scale  ;
                    surfaceview.point_xs.add(a);
                    surfaceview.point_ys.add(b);
                    arrayserchtime.add(serchtimenumber);
                    arrayscope.add(scopenumber);
                    arraygametime.add(gametimenumber);
                    xs.add(a * Constant.Scale);
                    ys.add(b * Constant.Scale);
                    Istouch = true;
                    surfaceview.Isplan = true;
                    point_x.setText("");
                    point_y.setText("");
                }else{
                    Toast.makeText(context,"请输入下一个目标的坐标",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.go_button_choose:
                if(!go_point_x.getText().toString().equals("") && !go_point_x.getText().toString().equals("") ){
                    a = Float.valueOf(go_point_x.getText().toString().trim());
                    b = Float.valueOf(go_point_x.getText().toString().trim());
                    a = a * surfaceview.Scale ;
                    b = b * surfaceview.Scale ;
                    Constant.debugLog("a" +a + "b"+b);
//                    sendNativePoint(a,b,0);
//                    发送坐标
                    surfaceview.point_xs.removeAllElements();
                    surfaceview.point_ys.removeAllElements();
                    surfaceview.point_xs.add(a);
                    surfaceview.point_ys.add(b);
                    surfaceview.Isplan = false;
                    go_point_x.setText("");
                    go_point_y.setText("");
                }else{
                    Toast.makeText(context,"请输入坐标",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.plan_change:
                if(null!=arrayPlanLists&&plannumber >= 0&&arrayPlanLists.size() > 0){
                    linear_plan.setVisibility(View.GONE);
                    linearlayout_plan_change.setVisibility(View.VISIBLE);
                    ChangeMapAdapter = new ChangeMapAdapter(arrayPlanLists,strings.get((int) plannumber),context,handler);
                    plan_change_list.setAdapter(ChangeMapAdapter);
                }
                break;
            case R.id.button_plan_change_back:
                updatekey();
                linear_plan.setVisibility(View.VISIBLE);
                linearlayout_plan_change.setVisibility(View.GONE);
                break;

            case R.id.button_plan_change_save:
                xs_tmp = new Vector<>();
                ys_tmp = new Vector<>();
                xs_tmp = arrayPlanLists.get(strings.get((int)plannumber)).get("point_xs");
                ys_tmp = arrayPlanLists.get(strings.get((int)plannumber)).get("point_ys");
                reset_surface();
                arrayPlanLists.get(strings.get((int)plannumber)).put("point_xs",xs_tmp);
                arrayPlanLists.get(strings.get((int)plannumber)).put("point_ys",ys_tmp);
                writeXML();
                updatekey();
                linear_plan.setVisibility(View.VISIBLE);
                linearlayout_plan_change.setVisibility(View.GONE);
                break;
        }
    }


    private void init(){

        map_Plan = new ArrayList<>();
        map_Plan.add("远");
        map_Plan.add("中");
        map_Plan.add("近");
        adapter = new ArrayAdapter<String>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        scope.setAdapter(adapter);
        scope.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scopenumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        map_Plan = new ArrayList<>();
        map_Plan.add("1：1米");
        map_Plan.add("1：2米");
        map_Plan.add("1：10米");
        map_Plan.add("1：100米");
        adapter = new ArrayAdapter<String>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        spinner_Scale.setAdapter(adapter);
        spinner_Scale.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinner_Scale_number = position;
                Constant.debugLog(position+"");
                switch (position){
                    case 0:
                        reset_surface();
                        Constant.Scale=1;
                        set_surface();
                        bilici.setText("1:1米");
                        surfaceview.point_xs = xs_tmp;
                        surfaceview.point_ys = ys_tmp;
                        break;
                    case 1:
                        reset_surface();
                        Constant.Scale = 2;
                        set_surface();
                        bilici.setText("1:2米");
                        surfaceview.point_xs = xs_tmp;
                        surfaceview.point_ys = ys_tmp;
                        break;
                    case 2:
                        reset_surface();
                        Constant.Scale = 10;
                        bilici.setText("1:10米");
                        set_surface();
                        surfaceview.point_xs = xs_tmp;
                        surfaceview.point_ys = ys_tmp;
                        break;
                    case 3:
                        reset_surface();
                        Constant.Scale = 100;
                        bilici.setText("1:100米");
                        set_surface();
                        surfaceview.point_xs = xs_tmp;
                        surfaceview.point_ys = ys_tmp;
                        break;
                }
                Constant.debugLog(surfaceview.point_xs.size()+"surfaceview.point_xs.size()");
                Constant.debugLog(surfaceview.point_ys.size()+"surfaceview.point_ys.size()");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        map_Plan = new ArrayList<>();
        map_Plan.add("不找人");
        map_Plan.add("找人1圈");
        map_Plan.add("找人2圈");
        map_Plan.add("找人3圈");
        adapter = new ArrayAdapter<String>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        serchtime.setAdapter(adapter);
        serchtime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serchtimenumber = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        map_Plan = new ArrayList<>();
        map_Plan.add("默认5秒");
        map_Plan.add("与用户互动");
        int i = 0;
        while(i < 10){
            map_Plan.add((i+1)+"分钟");
            i++;
        }
        adapter = new ArrayAdapter<String>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        gametime.setAdapter(adapter);
        gametime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

    //底层获取
    private void getUpPoint(double native_x,double native_y){
//        Constant.debugLog("point"+"native_x"+native_x +"native_y"+native_y);
        Constant.Current_x = -native_y/Constant.Scale;
        Constant.Current_y = -native_x/Constant.Scale;
    }

    public void send_data_distance(double distance){
//        sendDistance = distance;
        map  = new LinkedHashMap();
        map.put("distance",distance);
        Constant.getConstant().sendBundle(Constant.Command,Constant.Walk,map);
    }

    public void send_data_degree(double degree){
        sendDegree = degree;
        Constant.debugLog("当前角度"+ Constant.Current_degree);
        Constant.debugLog("需要到达角度"+ degree);
        degree = Constant.Current_degree - degree;
        Constant.debugLog("发送角度"+ degree);
        if(degree < -180){
            degree += 360;
        }else if (degree > 180){
            degree -= 360;
        }
        Constant.debugLog("发送角度"+ degree);
        map  = new LinkedHashMap();
        map.put("degree", degree);
        Constant.getConstant().sendBundle(Constant.Command,Constant.Turn,map);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        map_right_Ralative.clearAnimation();
        if (IsRight){
            IsRight = false;
            imgViewmapnRight.setImageResource(R.mipmap.you_yc);
        }else {
            IsRight = true;
            linearlayout_all.setVisibility(View.GONE);
            imgViewmapnRight.setImageResource(R.mipmap.you_xs);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String StringE = intent.getStringExtra("msg");
            if(StringE !=null && !StringE.equals("")){
                pasreJson(StringE);
            }
        }
    }

    public void pasreJson(String string){
        JSONObject object ;
        try {
            object = new JSONObject(string);
            String type = object.getString(Constant.Type);
            String funtion = object.getString(Constant.Function);
            String data = object.getString(Constant.Data);
            if(object !=null){
                if(type.equals(Constant.State)){
                    if(funtion.equals(Constant.Camera)){
                        JSONObject jsonObject = new JSONObject(data);
                        String str =  jsonObject.getString("result");
                        if(str.equals("body")){
                            Constant.debugLog("camera data ="+data);
                            try {
                                if(tasknumber == 4){
                                    Toast.makeText(context,"找人成功",Toast.LENGTH_SHORT).show();
//                                    Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                                    //摄像头搜索到人
                                    IsFind = true;
                                    tasknumber = 5;
                                    ServerSocketUtil.sendDateToClient(string);
                                    if(task!=null){
                                        task.cancel();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if(str.equals("nobody")){
                        }else if(str.equals("away")){
                            if(tasknumber == 5 && IsAway){
                                tasknumber = 6 ;
                                Toast.makeText(context,"人物离开",Toast.LENGTH_SHORT).show();
                                if(task!=null ){
                                    task.cancel();
                                }
                                Constant.getConstant().sendCamera(Float.valueOf(3),context);
                                handler.sendEmptyMessage(4);
                            }
                        }
                    }else if(funtion.equals(Constant.Peoplesearch)){
                        if(data.equals("foundpeople")){
                            Toast.makeText(context,"已到达用户面前",Toast.LENGTH_SHORT).show();
                            Constant.debugLog("继续转向"+string);
                            if(tasknumber == 5){
                                //机器人找到人
                                tasknumber = 5;
                                handler.sendEmptyMessage(4);
                            }
                        }
                    }else if(funtion.equals(Constant.Turn)){
                        JSONObject jsonObject = new JSONObject(data);
                        String flag = jsonObject.getString(Constant.Result);
                        if(flag.equals("success")){
                            startWait();
                        }else{
                            startWait();
                        }

                    }else if(funtion.equals(Constant.Walk)){
                        JSONObject jsonObject = new JSONObject(data);
                        String flag = jsonObject.getString(Constant.Result);
                        if(flag.equals("success")){
                            Constant.debugLog("继续转向"+string);
                            // 返回成功走的距离
                            if(tasknumber ==1){
                                Toast.makeText(context,"到达目标x点",Toast.LENGTH_SHORT).show();
                                tasknumber = 2;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber ==3){
                                Toast.makeText(context,"到达目标y点",Toast.LENGTH_SHORT).show();
                                tasknumber = 4;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber ==9){
                                Toast.makeText(context,"返回目标y点",Toast.LENGTH_SHORT).show();
                                tasknumber = 10;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber ==7){
                                Toast.makeText(context,"返回目标x点",Toast.LENGTH_SHORT).show();
                                tasknumber = 8;
                                handler.sendEmptyMessage(4);
                            }
                        }else if(flag.equals("fail")){
                            Constant.debugLog("继续转向"+string);
                            // 返回失败走的距离
                            if(tasknumber ==1){
                                Toast.makeText(context,"向目标x轴前进",Toast.LENGTH_SHORT).show();
                                tasknumber = 2;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber ==3){
                                Toast.makeText(context,"向目标y轴前进",Toast.LENGTH_SHORT).show();
                                tasknumber = 4;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber ==9){
                                Toast.makeText(context,"向目标y轴返回",Toast.LENGTH_SHORT).show();
                                tasknumber = 10;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber ==7){
                                Toast.makeText(context,"向目标x轴返回",Toast.LENGTH_SHORT).show();
                                tasknumber = 8;
                                handler.sendEmptyMessage(4);
                            }
                        }else if(flag.equals("walking")){
                            getUpPoint(jsonObject.getDouble("x"),jsonObject.getDouble("y"));
                            String reason = jsonObject.getString(Constant.Reason);
                            if(reason.equals("obstacle")){

                            }else if(reason.equals("obstaclemove")){
                                if(IsX){
                                    if(sendDistance - Constant.Current_x > 0.1){
                                        send_data_distance(sendDistance - Constant.Current_x);
                                    }else if(sendDistance - Constant.Current_x < -0.1){
                                        send_data_distance(Constant.Current_x - sendDistance);
                                    }
                                }else {
                                    if(sendDistance - Constant.Current_y > 0.1){
                                        send_data_distance(sendDistance - Constant.Current_y);
                                    }else if(sendDistance - Constant.Current_y < -0.1){
                                        send_data_distance(Constant.Current_y - sendDistance);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //xml写入
    public synchronized void writeXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element planLists = document.createElement("planLists");

            Iterator<Map.Entry<String, HashMap<String,Vector<Float>>>> iter = arrayPlanLists.entrySet().iterator();
            while(iter.hasNext()){
                HashMap<String,Vector<Float>> planList = new HashMap<>();
                Map.Entry entry = (Map.Entry) iter.next();

                planList = (HashMap<String, Vector<Float>>) entry.getValue();

                String key = (String) entry.getKey();
                Element keysElement = document.createElement("key");
                keysElement.setAttribute("key",key);

                Vector<Float> point_xs = planList.get("point_xs");
                Vector<Float> point_ys = planList.get("point_ys");
                Vector<Float> arrayserchtime = planList.get("arrayserchtime");
                Vector<Float> arrayscope = planList.get("arrayscope");
//                Vector<Float> arrayangle = planList.get("arrayangle");
                Vector<Float> arraygametime = planList.get("arraygametime");
                Vector<Float> scale_vector = planList.get("scale_number");

                Element point_xs_ = document.createElement("point_xs");
                Element scale = document.createElement("scale_number");
                Element point_ys_ = document.createElement("point_ys");
                Element arrayserchtime_ = document.createElement("arrayserchtime");
                Element arrayscope_ = document.createElement("arrayscope");
                Element arraygametime_ = document.createElement("arraygametime");

                Element item_scale = document.createElement("item_scale");
                item_scale.setTextContent(scale_vector.elementAt(0)+"");
                scale.appendChild(item_scale);
                for(int x = 0,length = point_xs.size();x < length;x ++){
                    Element item_xs = document.createElement("item_xs");
                    item_xs.setTextContent(point_xs.elementAt(x)+"");
                    Element item_ys = document.createElement("item_ys");
                    item_ys.setTextContent(point_ys.elementAt(x)+"");
                    Element item_serchtime = document.createElement("item_serchtime");
                    item_serchtime.setTextContent(arrayserchtime.elementAt(x)+"");
                    Element item_scope = document.createElement("item_scope");
                    item_scope.setTextContent(arrayscope.elementAt(x)+"");
                    Element item_gametime = document.createElement("item_gametime");
                    item_gametime.setTextContent(arraygametime.elementAt(x)+"");
                    point_xs_.appendChild(item_xs);
                    point_ys_.appendChild(item_ys);
                    arrayserchtime_.appendChild(item_serchtime);
                    arrayscope_.appendChild(item_scope);
                    arraygametime_.appendChild(item_gametime);
                }
                keysElement.appendChild(point_xs_);
                keysElement.appendChild(point_ys_);
                keysElement.appendChild(arrayserchtime_);
                keysElement.appendChild(arrayscope_);
                keysElement.appendChild(arraygametime_);
                keysElement.appendChild(scale);

                planLists.appendChild(keysElement);
            }

            document.appendChild(planLists);

            TransformerFactory transfactory = TransformerFactory.newInstance();
            Transformer transformer = transfactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");// 设置输出采用的编码方式
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");// 是否自动添加额外的空白
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");// 是否忽略XML声明
            FileOutputStream fos = new FileOutputStream(Constant.filePath);
            Source source = new DOMSource(document);
            Result result = new StreamResult(fos);
            transformer.transform(source, result);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    //xml读取
    public synchronized  void readXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(Constant.filePath);
            Document parse = null;
            if (!file.exists()) {
                parse = builder.parse(context.getAssets().open("map.xml"));
            }else{
                parse = builder.parse(file);
            }
            parse.normalize();
            Element root = parse.getDocumentElement();
            NodeList planLists = root.getElementsByTagName("key");

            arrayPlanLists  = new HashMap<>();
            for (int i = 0,length = planLists.getLength(); i < length; i++) {
                arrayhash = new HashMap<>();
                Element item = (Element) planLists.item(i);
                String key = item.getAttribute("key");
                getFloat(item,"item_xs","point_xs");
                getFloat(item,"item_ys","point_ys");
                getFloat(item,"item_serchtime","arrayserchtime");
                getFloat(item,"item_scope","arrayscope");
                getFloat(item,"item_gametime","arraygametime");
                getFloat(item,"item_scale","scale_number");
                arrayPlanLists.put(key,arrayhash);
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    public void getFloat(Element item,String keys,String key){
        NodeList nodes = item.getElementsByTagName(keys);
        Vector<Float> floats=new Vector<Float>();
        for (int j = 0 ,length = nodes.getLength(); j < length; j++) {
            Node node =  nodes.item(j);
            String string = node.getFirstChild().getNodeValue();
            floats.add(Float.valueOf(string));
        }
        arrayhash.put(key,floats);
    }

    //获取路线key名称
    public Vector<String> getKey(){
        Iterator<Map.Entry<String, HashMap<String,Vector<Float>>>> iter = arrayPlanLists.entrySet().iterator();
        Vector<String> strings = new Vector<>();
        while(iter.hasNext()) {
            HashMap<String, Vector<Float>> planList = new HashMap<>();
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            strings.add(key);
        }
        return strings;
    }

    public void updatekey(){
        readXML();
        Constant.Scale = 1;
        Istouch = false;
        strings = getKey();
        if(strings !=null && context!=null){
            surfaceview.point_xs.removeAllElements();
            surfaceview.point_ys.removeAllElements();
            adapter = new ArrayAdapter<String>(context,R.layout.item_spinselect,strings);
            adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
            planchooce.setAdapter(adapter);
        }
    }

    private MyDialog dialog ;
    private EditText editText;
    private void dialog() {
        dialog = new MyDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        editText = (EditText) dialog.getEditText();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayPlanLists.put(editText.getText().toString().trim(),arrayhash);
                writeXML();
                surfaceview.point_xs.removeAllElements();
                surfaceview.point_ys.removeAllElements();
                xs.removeAllElements();
                ys.removeAllElements();
                arrayserchtime.removeAllElements();
                arrayscope.removeAllElements();
                arraygametime.removeAllElements();
                linear_plan_info.setVisibility(View.GONE);
                linear_plan.setVisibility(View.VISIBLE);
                updatekey();
                dialog.dismiss();
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(context,"请继续规划路线",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void startPlan(){
        thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String,Vector<Float>> array = arrayPlanLists.get(strings.get((int) plannumber));
                Constant.debugLog("array = " + array.toString());
                xs_tmp = array.get("point_xs");
                ys_tmp  = array.get("point_ys");
                arrayserchtime_tmp = array.get("arrayserchtime");
                arrayscope_tmp = array.get("arrayscope");
//                    arrayangle_tmp = array.get("arrayangle");
                arraygametime_tmp = array.get("arraygametime");
                int i = 0;
                surfaceview.current_plan_number = 0;
                while(i < xs_tmp.size()){

                    Constant.debugLog("thread = " + i);
                    synchronized (thread){
                        try {
                            //转向需要增加调整
//                            send_data_degree(270);
                            tasknumber = 0;
                            Constant.debugLog("第几个"+i+"左转右转"+(xs_tmp.get(i) - Constant.Current_x > 0));
                            Constant.debugLog(""+ (xs_tmp.get(i) - Constant.Current_x));
                            if(xs_tmp.get(i)/90 - Constant.Current_x > 0.1){
                                send_data_degree(10);
                                thread.wait();
                                IsX = true;
                                sendDistance = xs_tmp.get(i)/90;
                                send_data_distance(xs_tmp.get(i)/90 - Constant.Current_x);
                                Constant.debugLog("发送x坐标 = " + (xs_tmp.get(i)/90 - Constant.Current_x));
                                thread.wait();
//                                }
                            }else if(xs_tmp.get(i)/90 - Constant.Current_x < -0.1){
                                send_data_degree(190);
                                thread.wait();
                                IsX = true;
                                sendDistance = xs_tmp.get(i)/90;
                                send_data_distance(Constant.Current_x - xs_tmp.get(i)/90 );
                                Constant.debugLog("发送x坐标 = " + (xs_tmp.get(i)/90 - Constant.Current_x));
                                thread.wait();
//                                }
                            }
                            tasknumber = 2;
                            Constant.debugLog("第几个"+i+"左转右转"+(ys_tmp.get(i)-Constant.Current_y > 0));
                            if(ys_tmp.get(i)/90-Constant.Current_y > 0.1){
                                send_data_degree(100);
                                thread.wait();
                                IsX = false;
                                sendDistance = ys_tmp.get(i)/90;
                                send_data_distance(ys_tmp.get(i)/90 - Constant.Current_y);
                                thread.wait();
                            }else if(ys_tmp.get(i)/90-Constant.Current_y < -0.1){
                                send_data_degree(280);
                                thread.wait();
                                IsX = false;
                                sendDistance = ys_tmp.get(i)/90;
                                send_data_distance(Constant.Current_y - ys_tmp.get(i)/90 );
                                thread.wait();
//                                }
                            }
                            tasknumber = 4;
                            ///4444
//                            Constant.Current_y += ys_tmp.get(i)/90-Constant.Current_y;
                            surfaceview.current_plan_number = i+1 ;
                            //到达对应地点notify
                            if(arrayserchtime_tmp.get(i) != 0) {
                                //发送 找人时间以及机器人旋转以及找人范围
                                resetTimer();
                                if (arrayserchtime_tmp.get(i) == 1) {
                                    Constant.debugLog("arrayserchtime_tmp = " + 0);
                                    timer.schedule(task, 1 * 40 * 1000);
                                } else if (arrayserchtime_tmp.get(i) == 2) {
                                    Constant.debugLog("arrayserchtime_tmp = " + 1);
                                    timer.schedule(task, 2 * 40 * 1000);
                                } else if (arrayserchtime_tmp.get(i) == 3) {
                                    Constant.debugLog("arrayserchtime_tmp = " + 2);
                                    timer.schedule(task, 3 * 40 * 1000);
                                }
                                IsFind = false;
                                Constant.getConstant().sendCamera(arrayscope_tmp.get(i), context);
                                Constant.getConstant().sendBundle(Constant.Command, Constant.Peoplesearch, "");
                                //互动中 找人
                                thread.wait();
                                //5555
                                if (IsFind) {
                                    IsFind = false;
                                    Constant.debugLog("互动时间 = " + i);
                                    resetTimer2();
                                    IsAway = false;
                                    if (arraygametime_tmp.get(i) == 0) {
                                        Constant.debugLog("arraygametime_tmp = " + 0);
                                        timer.schedule(task, 1 * 5 * 1000);
                                    } else if (arraygametime_tmp.get(i) == 1) {
                                        IsAway = true;
                                    } else {
                                        float a = arraygametime_tmp.get(i) - 1;
                                        timer.schedule(task, (long) (a * 60 * 1000));
                                        Constant.debugLog("tasknumber = " + a);
                                    }
                                    thread.wait();

                                    if (xs_tmp.get(i) / 90 - Constant.Current_x > 0.1) {
                                        send_data_degree(10);
                                        thread.wait();
                                        IsX = true;
                                        sendDistance = xs_tmp.get(i) / 90;
                                        send_data_distance(xs_tmp.get(i) / 90 - Constant.Current_x);
                                        Constant.debugLog("发送x坐标 = " + (xs_tmp.get(i) / 90 - Constant.Current_x));
                                        thread.wait();
                                    } else if (xs_tmp.get(i) / 90 - Constant.Current_x < -0.1) {
                                        send_data_degree(190);
                                        thread.wait();
                                        IsX = true;
                                        sendDistance = xs_tmp.get(i) / 90;
                                        send_data_distance(Constant.Current_x - xs_tmp.get(i) / 90);
                                        Constant.debugLog("发送x坐标 = " + (xs_tmp.get(i) / 90 - Constant.Current_x));
                                        thread.wait();
                                    }
                                    tasknumber = 8;
                                    Constant.debugLog("第几个" + i + "左转右转" + (ys_tmp.get(i) - Constant.Current_y > 0));
                                    if (ys_tmp.get(i) / 90 - Constant.Current_y > 0.1) {
                                        send_data_degree(100);
                                        thread.wait();
                                        IsX = false;
                                        sendDistance = ys_tmp.get(i) / 90;
                                        send_data_distance(ys_tmp.get(i) / 90 - Constant.Current_y);
                                        thread.wait();
                                    } else if (ys_tmp.get(i) / 90 - Constant.Current_y < -0.1) {
                                        send_data_degree(280);
                                        thread.wait();
                                        IsX = false;
                                        sendDistance = ys_tmp.get(i) / 90;
                                        send_data_distance(Constant.Current_y - ys_tmp.get(i) / 90);
                                        thread.wait();
                                    }
                                    tasknumber = 10;
                                    //9999
                                }

                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //返回后进行下一个地点
                    i++;
                }
                synchronized (thread) {
                    try {
                        Constant.debugLog("返回原点");
                        for(int j = 0; j < 2;j++) {
                            tasknumber = 0;
                            if (0 - Constant.Current_x > 0.1) {
                                send_data_degree(10);
                                thread.wait();
                                tasknumber = 1;
                                IsX = true;
                                sendDistance = 0;
                                send_data_distance(0 - Constant.Current_x);
                                Constant.debugLog("发送x坐标 = " + (0 - Constant.Current_x));
                                thread.wait();
                            } else if (0 - Constant.Current_x < -0.1) {
                                send_data_degree(190);
                                thread.wait();
                                tasknumber = 1;
                                IsX = true;
                                sendDistance = 0;
                                send_data_distance(Constant.Current_x - 0);
                                Constant.debugLog("发送x坐标 = " + (0 - Constant.Current_x));
                                thread.wait();
                            }
                            tasknumber = 2;
                            if (0 - Constant.Current_y > 0.1) {
                                send_data_degree(100);
                                thread.wait();
                                tasknumber = 1;
                                IsX = false;
                                sendDistance = 0;
                                send_data_distance(0 - Constant.Current_y);
                                thread.wait();
                            } else if (0 - Constant.Current_y < -0.1) {
                                send_data_degree(280);
                                thread.wait();
                                tasknumber = 1;
                                IsX = false;
                                sendDistance = 0;
                                send_data_distance(Constant.Current_y - 0);
                                thread.wait();
                            }
                        }
                        send_data_degree(280);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                findViewById(R.id.button_execut).setClickable(true);
                findViewById(R.id.button_plan_stop).setClickable(false);
//                sendNativePoint();
                tasknumber = -1;
                thread = new Thread();
            }
        });
        thread.start();
    }

    public void startRoam(){
        thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    tasknumber = -1;
                    Random random=new Random();
                    float x = 0 , y = 0;
                    while(x  < 90 || x >810){
                        x = random.nextInt(810);
                    }
                    while(y  < 80 || y >450){
                        y = random.nextInt(450);
                    }
                    tasknumber = 20;
                    synchronized (thread) {
                        try {
                            thread.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    public void resetTimer(){
        if (task != null){
            task.cancel();  //将原任务从队列中移除
        }
        timer = new Timer();
        task = new TimerTask() {
            public void run () {
                Constant.debugLog("task");
                //跳过找人，互动。准备返回远点
                tasknumber = 10;
                IsFind = false;
                Constant.getConstant().sendCamera(Float.valueOf(3),context);
                Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                handler.sendEmptyMessage(4);
            }
        };
    }

    public void resetTimer2(){
        if (task != null){
            task.cancel();  //将原任务从队列中移除
        }
        timer = new Timer();
        task = new TimerTask() {
            public void run () {
                tasknumber = 6;
                Constant.getConstant().sendCamera(Float.valueOf(3),context);
                handler.sendEmptyMessage(4);
            }
        };
    }
    //右边动画
    private void startAnimationRight(){
        if (IsRight){
            linearlayout_all.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,linearlayout_all.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0F
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(MapFragment.this);
            map_right_Ralative.startAnimation(translateAnimation);

        }else {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,linearlayout_all.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(false);
            translateAnimation.setAnimationListener(MapFragment.this);
            map_right_Ralative.startAnimation(translateAnimation);
        }
    }
    public void reset_surface(){
        Constant.debugLog("set_surface"+Constant.Scale);
        Constant.debugLog("set_surface"+xs_tmp.size());
        for(int i = 0 ; i < xs_tmp.size();i++){
            xs_tmp.setElementAt((xs_tmp.elementAt(i) ) * Constant.Scale ,i);
            ys_tmp.setElementAt((ys_tmp.elementAt(i) ) * Constant.Scale ,i);
        }
    }
    public void set_surface(){
        Constant.debugLog("reset_surface"+Constant.Scale);
        Constant.debugLog("set_surface"+xs_tmp.size());
        for(int i = 0 ; i < xs_tmp.size();i++){
            xs_tmp.setElementAt((xs_tmp.elementAt(i) ) / Constant.Scale ,i);
            ys_tmp.setElementAt((ys_tmp.elementAt(i) ) / Constant.Scale ,i);
        }
    }
    public void startWait(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                if(Constant.Current_degree - sendDegree < 3 && sendDegree - Constant.Current_degree < 3){
                    Constant.debugLog("转向正确");
                    if(tasknumber ==0){
                        tasknumber = 1;
                        handler.sendEmptyMessage(4);
                    }else if (tasknumber == 2){
                        tasknumber = 3;
                        handler.sendEmptyMessage(4);
                    }else if (tasknumber == 6){
                        tasknumber = 7;
                        handler.sendEmptyMessage(4);
                    }else if (tasknumber == 8){
                        tasknumber = 9;
                        handler.sendEmptyMessage(4);
                    }
//                }else{
//                    Constant.debugLog("继续转向");
//                    send_data_degree(sendDegree);
//                }
            }
        }).start();
    }

}
