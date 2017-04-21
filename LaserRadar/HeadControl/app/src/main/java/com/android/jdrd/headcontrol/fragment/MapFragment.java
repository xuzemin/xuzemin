package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
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
import android.widget.Toast;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.adapter.ChangeMapAdapter;
import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.dialog.FaceDialog;
import com.android.jdrd.headcontrol.dialog.MyDialog;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Constant;
import com.android.jdrd.headcontrol.view.MyView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by xuzemin on 2017/2/7.
 *
 */

public class MapFragment extends BaseFragment implements View.OnClickListener,Animation.AnimationListener{
    //注册广播为接受通讯数据
    private MyReceiver receiver;
    private boolean IsSuccus = false;
    private MyView surfaceview;
    //路线选择、找人时间、找人范围、转弯角度、互动时间
    private Spinner planchooce,serchtime,scope,angle,gametime,plan_cirles,gametime_roam,scope_roam,serchtime_roam;
    //路线选择、找人时间、找人范围、转弯角度、互动时间；（对应number）
    private float plannumber =0,serchtimenumber =0,scopenumber =0,gametimenumber =0,serchtimenumber_roam =0,scopenumber_roam =0,gametimenumber_roam =0;
    private Context context;
    private ImageView imgViewmapnRight;
    private RelativeLayout map_right_Ralative;
    private EditText point_x,point_y,go_point_x,go_point_y;
    private ArrayAdapter<String> adapter;
    private float eventx,eventy;
    private ListView plan_change_list;
    private static float a = 0, b = 0;
    public static boolean Istouch = false,Isplan = false,IsFind = false,IsAway = false,IsRight = false;
    //路线模式布局、路线模式详细设置；
    private LinearLayout linearlayout_map,linear_plan,linear_plan_info,linear_point,linear_roam,linearlayout_all,linearlayout_plan_change;
    private HashMap<String,Vector<Float>> arrayhash;
    private Vector<Float> arrayserchtime,arrayscope,arraygametime;
    private Timer timer;
    private ChangeMapAdapter ChangeMapAdapter;
    private int tasknumber = -1;
    private int cirles = 0;
    private boolean CURRENT_CRILES = false;
    private boolean SETRETURN = false;
    private TimerTask task;
    private HashMap<String,HashMap<String,Vector<Float>>> arrayPlanLists = new HashMap<>();
    private Vector<String> strings = new Vector<>();
    public  MapFragment(){
        super();
    }
    private Thread thread = null ;
    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(surfaceview !=null && surfaceview.point_xs.size()>=1){
                        surfaceview.point_xs.removeAllElements();
                        surfaceview.point_ys.removeAllElements();
                        arrayserchtime.removeAllElements();
                        arrayscope.removeAllElements();
                        arraygametime.removeAllElements();
                    }
                    updatekey();
                    linear_plan_info.setVisibility(View.GONE);
                    linear_plan.setVisibility(View.VISIBLE);
                    Istouch = false;
                    break;
                case 2:
                    Constant.debugLog("btn_cancle");
                    break;
                case 3:
                    if(thread!=null){
                        Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                        Constant.getConstant().sendCamera(3,context);
                        surfaceview.IsHuman = false;
                        if(thread.isAlive()){
                            thread = new Thread();
                            surfaceview.current_plan_number = 0;
                        }
                        try {
                            ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    }
                    break;
                case 4:
                    if(thread!=null){
                        synchronized (thread) {
                            thread.notify();
                        }
                    }
                    break;
                case 5:
                    ChangeMapAdapter.update((Integer) msg.obj,plan_change_list);
                    break;
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
        surfaceview=(MyView)findViewById(R.id.surfaceview);
        surfaceview.myview_height = Constant.MyView_Height;
        surfaceview.myview_width = Constant.MyView_Width;
    }

    @Override
    public void initData() {
        surfaceview.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.touxiang);
        surfaceview.rotbitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.jiantou);
        surfaceview.obstacle = BitmapFactory.decodeResource(getResources(), R.mipmap.luzhang);
        surfaceview.human = BitmapFactory.decodeResource(getResources(), R.mipmap.ren);
        planchooce = (Spinner) findViewById(R.id.spinner_plan);
        serchtime = (Spinner) findViewById(R.id.serchtime);
        scope = (Spinner) findViewById(R.id.scope);
        gametime_roam = (Spinner) findViewById(R.id.gametime_roam);
        scope_roam = (Spinner) findViewById(R.id.scope_roam);
        serchtime_roam = (Spinner) findViewById(R.id.serchtime_roam);
        plan_cirles = (Spinner) findViewById(R.id.spinner_plan_cirles);
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
        findViewById(R.id.button_point_stop).setOnClickListener(this);
        findViewById(R.id.plan_change).setOnClickListener(this);
        findViewById(R.id.button_plan_change_save).setOnClickListener(this);
        findViewById(R.id.button_plan_change_back).setOnClickListener(this);
        findViewById(R.id.button_choose).setOnClickListener(this);
        findViewById(R.id.go_button_choose).setOnClickListener(this);
        findViewById(R.id.button_clearlast).setOnClickListener(this);
        findViewById(R.id.button_clearall).setOnClickListener(this);
        findViewById(R.id.button_plan).setOnClickListener(this);
        findViewById(R.id.button_versioncode).setOnClickListener(this);
        findViewById(R.id.button_plan_stop).setOnClickListener(this);
        findViewById(R.id.button_pointchooce).setOnClickListener(this);
        findViewById(R.id.button_pathplan).setOnClickListener(this);
        findViewById(R.id.button_cruise).setOnClickListener(this);
        findViewById(R.id.button_returnback).setOnClickListener(this);
        findViewById(R.id.button_setreturn).setOnClickListener(this);
        findViewById(R.id.button_saveall).setOnClickListener(this);
        findViewById(R.id.button_execut).setOnClickListener(this);
        findViewById(R.id.button_next).setOnClickListener(this);
        findViewById(R.id.button_roam_start).setOnClickListener(this);
        findViewById(R.id.button_roam_stop).setOnClickListener(this);
        findViewById(R.id.button_return).setOnClickListener(this);
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
                            a = (event.getX()  - surfaceview.translate_x -40)  ;
                            b = (event.getY()  - surfaceview.translate_y -40) ;
                            int x = (int) a % Constant.SCALE_NUMBER;
                            int x_int = (int) a / Constant.SCALE_NUMBER;
                            int y = (int) b % Constant.SCALE_NUMBER;
                            int y_int = (int) b / Constant.SCALE_NUMBER;
                            if (x > (Constant.SCALE_NUMBER / 2)) {
                                a = Constant.SCALE_NUMBER * (x_int + 1) + 40 ;
                            } else {
                                a = Constant.SCALE_NUMBER * x_int + 40;
                            }
                            if (y > (Constant.SCALE_NUMBER / 2)) {
                                b = Constant.SCALE_NUMBER * (y_int + 1) + 40;
                            } else {
                                b = Constant.SCALE_NUMBER * y_int + 40;
                            }
                            if(Isplan){
                                if(surfaceview.point_xs.size() > 0 && surfaceview.point_xs.lastElement() == a && surfaceview.point_ys.lastElement() == b){
                                    return true;
                                }
                                surfaceview.point_xs.add(a);
                                surfaceview.point_ys.add(b);
                                arrayserchtime.add(serchtimenumber);
                                Constant.debugLog("serchtimenumber"+serchtimenumber+"scopenumber"+scopenumber+"gametimenumber"+gametimenumber);
                                arrayscope.add(scopenumber);
                                arraygametime.add(gametimenumber);
                                serchtime.setSelection(0,true);
                                scope.setSelection(0,true);
                                gametime.setSelection(0,true);
                                Istouch = true;
                                surfaceview.Isplan = true;
                            }else{
                                surfaceview.point_xs.removeAllElements();
                                surfaceview.point_ys.removeAllElements();
                                surfaceview.point_xs.add(a);
                                surfaceview.point_ys.add(b);
                                surfaceview.Isplan = false;
                                Istouch = true;
                            }
                        }
                    }
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
                    HashMap<String, Vector<Float>> array = arrayPlanLists.get(strings.get((int) plannumber));
                    surfaceview.point_xs = array.get("point_xs");
                    surfaceview.point_ys = array.get("point_ys");
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
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jdrd.fragment.Map");
        if(context!=null && receiver != null ){
            context.registerReceiver(receiver,filter);
        }
        init();
        updatekey();
        if(dialog != null){
            dialog.dismiss();
        }
        switch (Constant.CURRENTINDEX_MAP){
            case 0:
                setVisible();
                linearlayout_map.setVisibility(View.VISIBLE);
                Istouch = false;
                Isplan = false;
                break;
            case 1:
                go_Point();
                break;
            case 2:
                go_Plan();
                break;
            case 3:
                go_Roam();
                break;
            case 4:
                go_NewPlan();
                break;
            case 5:
                break;
            default:
                break;
        }
        if(Constant.DIALOG_SHOW){
            dialogFace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        setObstacle();
//        setBackObstacle();
//        surfaceview.config = 3;
//        surfaceview.human_x = 1000;
//        surfaceview.human_y = 600;
//        surfaceview.IsHuman = true;
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
                if(surfaceview.point_xs!=null){
                    Constant.debugLog(surfaceview.point_xs.size()+"size_xs_delete");
                    Constant.debugLog(surfaceview.point_ys.size()+"size_ys_delete");
                    Constant.debugLog(arrayserchtime.size()+"size_xs_deletearrayserchtime");
                    if(surfaceview !=null&&surfaceview.point_xs.size() > 0){
                        int number = surfaceview.point_xs.size() -1;
                        Constant.debugLog(number+"number");
                        if(number == arrayserchtime.size()-1){
                            arrayserchtime.remove(number);
                            arrayscope.remove(number);
                            arraygametime.remove(number);
                        }
                        surfaceview.point_xs.remove(number);
                        surfaceview.point_ys.remove(number);
                    }
                }
                Istouch = true;
                break;
            //清除所有
            case R.id.button_clearall:
                Toast.makeText(context,"清除所有设置坐标",Toast.LENGTH_SHORT).show();
                if(surfaceview!=null){
                    go_NewPlan();
                }
                break;
            //规划新路线
            case R.id.button_plan:
                Constant.CURRENTINDEX_MAP = 4;
                go_NewPlan();
                break;
            //删除当前路线
            case R.id.button_remove:
                if(Istouch){
                    Constant.getConstant().showWarntext(context,handler);
                }else {
                    if(arrayPlanLists.size() > 0){
                        Toast.makeText(context,"删除路线",Toast.LENGTH_SHORT).show();
                        readXML();
                        arrayPlanLists.remove(strings.get((int) plannumber));
                        writeXML();
                        updatekey();
                    }
                }
                break;
            //停止执行
            case R.id.button_plan_stop:
                CURRENT_CRILES = false;
                handler.sendEmptyMessage(3);
                Toast.makeText(context,"停止执行路线",Toast.LENGTH_SHORT).show();
                break;
            //执行路线
            case R.id.button_execut:
                Toast.makeText(context,"开始执行路线",Toast.LENGTH_SHORT).show();
                startPlan();
                dialogFace();
                startAnimationRight();
                break;
            //规划完毕
            case R.id.button_saveall:
                Constant.CURRENTINDEX_MAP = 2;
                readXML();
                if(surfaceview.point_xs.size()> 0 ){
                    arrayhash = new HashMap<>();
                    arrayhash.put("point_xs",surfaceview.point_xs);
                    arrayhash.put("point_ys",surfaceview.point_ys);
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
                Constant.CURRENTINDEX_MAP = 3;
                go_Roam();
                serchtime_roam.setSelection(0,true);
                scope_roam.setSelection(0,true);
                gametime_roam.setSelection(0,true);
                Istouch = false;
                break;
            //路线规划模式
            case R.id.button_pathplan:
                Constant.CURRENTINDEX_MAP = 2;
                go_Plan();
                Istouch = false;
                break;
            //点选模式
            case R.id.button_pointchooce:
                Constant.CURRENTINDEX_MAP = 1;
                go_Point();
                break;
            case R.id.button_return:
                Toast.makeText(context,"返回原点",Toast.LENGTH_SHORT).show();
                if(thread!=null){
                    Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    if(thread.isAlive()){
                        thread = new Thread();
                        surfaceview.current_plan_number = 0;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendNativePoint();
                break;
            //点选下一步
            case R.id.button_next:
                if(surfaceview.point_xs !=null && surfaceview.point_xs.size()>0){
                    pointStart(surfaceview.point_xs.elementAt(0),surfaceview.point_ys.elementAt(0));
                }
                Toast.makeText(context,"正在向目标前进",Toast.LENGTH_SHORT).show();
                break;
            //开始漫游
            case R.id.button_roam_start:
                startRoam();
                Toast.makeText(context,"开始漫游行走",Toast.LENGTH_SHORT).show();
                dialogFace();
                startAnimationRight();
                break;
            //停止漫游
            case R.id.button_roam_stop:
                handler.sendEmptyMessage(3);
                Toast.makeText(context,"停止漫游行走",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_point_stop:
                handler.sendEmptyMessage(3);
                Toast.makeText(context,"停止行走",Toast.LENGTH_SHORT).show();
                break;
            case R.id.button_plan_back:
                Constant.CURRENTINDEX_MAP = 0;
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_plan.setVisibility(View.GONE);
                surfaceview.point_xs.removeAllElements();
                surfaceview.point_ys.removeAllElements();
                Isplan = false;
                break;
            case R.id.button_plan_info_back:
                Constant.CURRENTINDEX_MAP = 2;
                Constant.getConstant().showWarntext(context, handler);
                break;
            case R.id.button_point_back:
                Constant.CURRENTINDEX_MAP = 0;
                Istouch = false;
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_point.setVisibility(View.GONE);
                surfaceview.point_xs.removeAllElements();
                surfaceview.point_ys.removeAllElements();
                break;
            case R.id.button_roam_back:
                Constant.CURRENTINDEX_MAP = 0;
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_roam.setVisibility(View.GONE);
                Istouch = false;
                break;
            case R.id.imgViewmapnRight:
                startAnimationRight();
                break;
            //返回原点
            case R.id.button_returnback:
                Toast.makeText(context,"返回原点",Toast.LENGTH_SHORT).show();
                if(thread!=null){
                    Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    if(thread.isAlive()){
                        thread = new Thread();
                        surfaceview.current_plan_number = 0;
                    }
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendNativePoint();
                break;
            case R.id.button_setreturn:
                SETRETURN = true;
                break;
            case R.id.button_choose:
                if(!point_x.getText().toString().equals("") && !point_y.getText().toString().equals("") ){
                    Istouch = false;
                    a = Float.valueOf(point_x.getText().toString().trim()) ;
                    b = Float.valueOf(point_y.getText().toString().trim()) ;
                    if(a > 10){
                        a = 10;
                    }else if(a < 0){
                        a = 0;
                    }
                    if(b > 6){
                        b = 6;
                    }else if(b < 0){
                        b = 0;
                    }
                    a = a * Constant.SCALE_NUMBER +40;
                    b = b * Constant.SCALE_NUMBER +40;
                    surfaceview.point_xs.add(a);
                    surfaceview.point_ys.add(b);
                    arrayserchtime.add(serchtimenumber);
                    arrayscope.add(scopenumber);
                    arraygametime.add(gametimenumber);
                    Istouch = true;
                    surfaceview.Isplan = true;
                    point_x.setText("");
                    point_y.setText("");
                    serchtime.setSelection(0,true);
                    scope.setSelection(0,true);
                    gametime.setSelection(0,true);
                }else{
                    Toast.makeText(context,"请输入下一个目标的坐标",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.go_button_choose:
                if(!go_point_x.getText().toString().equals("") && !go_point_x.getText().toString().equals("") ){
                    a = Float.valueOf(go_point_x.getText().toString().trim());
                    b = Float.valueOf(go_point_x.getText().toString().trim());
                    if(a > 10){
                        a = 10;
                    }else if(a < 0){
                        a = 0;
                    }
                    if(b > 6){
                        b = 6;
                    }else if(b < 0){
                        b = 0;
                    }
                    a = a * Constant.SCALE_NUMBER +40;
                    b = b * Constant.SCALE_NUMBER +40;
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
                Constant.CURRENTINDEX_MAP = 5;
                go_PlanChange();
                break;
            case R.id.button_plan_change_back:
                Constant.CURRENTINDEX_MAP = 2;
                updatekey();
                setVisible();
                linear_plan.setVisibility(View.VISIBLE);
                break;
            case R.id.button_plan_change_save:
                Toast.makeText(context,"修改成功",Toast.LENGTH_SHORT).show();
                Constant.CURRENTINDEX_MAP = 2;
                writeXML();
                updatekey();
                setVisible();
                linear_plan.setVisibility(View.VISIBLE);
                break;
            case R.id.button_versioncode:
                PackageInfo pi = null;//getPackageName()是你当前类的包名，0代表是获取版本信息
                try {
                    PackageManager pm = context.getPackageManager();
                    pi = pm.getPackageInfo(context.getPackageName(), 0);
                    String name = pi.versionName;
                    int code = pi.versionCode;
                    Toast.makeText(context,"当前版本号"+name+",当前代码版本号"+code,Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void init(){
        ArrayList<String> map_Plan = new ArrayList<>();
        map_Plan.add("远");
        map_Plan.add("中");
        map_Plan.add("近");
        adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,map_Plan);
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
        map_Plan.add("无限循环");
        int i = 1;
        while(i < 10){
            map_Plan.add("循环"+(i)+"次");
            i++;
        }
        adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        plan_cirles.setAdapter(adapter);
        plan_cirles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                cirles = position;
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
        adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,map_Plan);
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
        map_Plan.add("默认15秒");
        map_Plan.add("与用户互动");
        i = 0;
        while(i < 10){
            map_Plan.add((i+1)+"分钟");
            i++;
        }
        adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,map_Plan);
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

        map_Plan = new ArrayList<>();
        map_Plan.add("不找人");
        map_Plan.add("找人");
        adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        serchtime_roam.setAdapter(adapter);
        serchtime_roam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                serchtimenumber_roam = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        map_Plan = new ArrayList<>();
        map_Plan.add("远");
        map_Plan.add("中");
        map_Plan.add("近");
        adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        scope_roam.setAdapter(adapter);
        scope_roam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                scopenumber_roam = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        map_Plan = new ArrayList<>();
        map_Plan.add("默认15秒");
        map_Plan.add("与用户互动");
        i = 0;
        while(i < 10){
            map_Plan.add((i+1)+"分钟");
            i++;
        }
        adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        gametime_roam.setAdapter(adapter);
        gametime_roam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gametimenumber_roam = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setVisible(){
        linearlayout_plan_change.setVisibility(View.GONE);
        linear_plan.setVisibility(View.GONE);
        linearlayout_map.setVisibility(View.GONE);
        linear_plan_info.setVisibility(View.GONE);
        linear_point.setVisibility(View.GONE);
        linear_roam.setVisibility(View.GONE);
    }

    //底层获取
    private void getUpPoint(double native_x,double native_y){
        if(SETRETURN){
            Constant.return_x = native_x;
            Constant.return_y = native_y;
        }
        if(native_x <= 0 && native_x >= -6 && native_y >= -7.6 && native_y <=2.4){
            surfaceview.bitmap_y = ( native_x * -1 * Constant.SCALE_NUMBER) - surfaceview.bitmap.getHeight() / 2 + 40 ;
            surfaceview.bitmap_x = ( native_y * -1 * Constant.SCALE_NUMBER ) + 2.4 * Constant.SCALE_NUMBER - surfaceview.bitmap.getWidth() / 2 + 40 ;
        }
    }

    //设置方向
    private void getAngle(int angle){
        if(SETRETURN){
            Constant.return_degree = angle;
            SETRETURN = false;
            Toast.makeText(context,"设置原点成功",Toast.LENGTH_SHORT).show();
        }
        if(angle < 360 && angle >= 0){
            surfaceview.rote =  -angle;
        }
    }

    //发往底层
    private void sendNativePoint(float up_x,float up_y ,int angle){
        Map map  = new LinkedHashMap();
        double a = ((up_y - 40) / - 1 / Constant.SCALE_NUMBER);
        map.put("point_x",a);
        a = (up_x - 40 ) / - 1 / Constant.SCALE_NUMBER + 2.4 ;
        map.put("point_y",a);
        map.put("angle",angle);
        Constant.getConstant().sendBundle(Constant.Command,Constant.Navigation,map);
        Constant.debugLog("map"+map.toString());
    }
    //发往底层
    private void sendNativePoint(){
        Map map  = new LinkedHashMap();
        map.put("point_x",Constant.return_x);
        map.put("point_y",Constant.return_y);
        map.put("angle",Constant.return_degree);
        Constant.getConstant().sendBundle(Constant.Command,Constant.Navigation,map);
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
            Constant.debugLog("message"+object.toString());
            if(object !=null){
                if(type.equals(Constant.State)){
                    if(funtion.equals(Constant.Navigation)){
                        JSONObject jsonObject = new JSONObject(data);
                        String flag = jsonObject.getString(Constant.Result);
                        getUpPoint(jsonObject.getDouble("x"),jsonObject.getDouble("y"));
                        getAngle(jsonObject.getInt("angle"));
                        if(flag.equals("success")){
                            IsSuccus = true;
                            if(tasknumber == 0 ){
                                //到达新地点
                                tasknumber = 1;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber ==4){
                                //返回新地点
                                tasknumber = 5;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 20){
                                tasknumber = -1;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 10){
                                tasknumber = 11;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 14){
                                tasknumber = 15;
                                handler.sendEmptyMessage(4);
                            }
                        }else if(flag.equals("fail")){
                            IsSuccus = false;
                            if(tasknumber == 0  ) {
                                //到达新地点
                                tasknumber = 1;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 4){
                                //返回新地点
                                tasknumber = 5;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 10){
                                tasknumber = 11;
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 14){
                                tasknumber = 15;
                                handler.sendEmptyMessage(4);
                            }
                        }else if(flag.equals("navigating")){

                        }
                    }else if(funtion.equals(Constant.Camera)){
                        JSONObject jsonObject = new JSONObject(data);
                        String str =  jsonObject.getString("result");
                        Constant.debugLog("Camera"+jsonObject.toString());
                        if(str.equals("body")){
                            float degree = Float.valueOf(jsonObject.getString("degree"));
                            float distance = Float.valueOf(jsonObject.getString("distance"));
                            Constant.debugLog("distance"+distance);
                            Constant.debugLog("degree"+degree);
                            if(degree > 180){
                                degree = degree - 360;
                            }
                            surfaceview.human_x = surfaceview.bitmap_x + (surfaceview.bitmap.getWidth() - surfaceview.obstacle.getWidth())/2 + distance / 1000 * Constant.SCALE_NUMBER * Math.sin((degree+surfaceview.rote)*Math.PI/180) ;
                            surfaceview.human_y = surfaceview.bitmap_y + (surfaceview.bitmap.getHeight() - surfaceview.obstacle.getHeight())/2 - distance / 1000 * Constant.SCALE_NUMBER * Math.cos((degree+surfaceview.rote)*Math.PI/180) - surfaceview.human.getHeight()/2;
                            if(task!=null){
                                task.cancel();
                            }
                            try {
                                if(tasknumber == 1){
                                    surfaceview.IsHuman = true;
                                    //摄像头搜索到人
                                    IsFind = true;
                                    tasknumber = 2;
                                    ServerSocketUtil.sendDateToClient(string, Constant.ip_ros);
                                }else if(tasknumber == 10){
                                    surfaceview.IsHuman = true;
                                    IsFind = true;
                                    tasknumber = 11;
                                    Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                                    Map map  = new LinkedHashMap();
                                    map.put("degree",degree);
                                    map.put("distance",distance);
                                    Constant.getConstant().sendCamera(0,context);
                                    Constant.getConstant().sendBundle(Constant.Command,Constant.Roamsearch,map);
                                    Constant.debugLog("map"+map);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if(str.equals("nobody")){
                        }else if(str.equals("away")){
                            if(tasknumber == 3 && IsAway){
                                surfaceview.IsHuman = false;
                                tasknumber = 4 ;
                                Toast.makeText(context,"away",Toast.LENGTH_SHORT).show();
                                if(task!=null ){
                                    task.cancel();
                                }
                                Constant.getConstant().sendCamera(3,context);
                                ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 12 && IsAway){
                                surfaceview.IsHuman = false;
                                tasknumber = 13 ;
                                Toast.makeText(context,"away",Toast.LENGTH_SHORT).show();
                                if(task!=null ){
                                    task.cancel();
                                }
                                ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                                Constant.getConstant().sendCamera(3,context);
                                handler.sendEmptyMessage(4);
                            }
                        }
                    }else if(funtion.equals(Constant.Peoplesearch)){
                        if(data.equals("foundpeople")){
                            Constant.debugLog("foundpeople"  +"    tasknumber"+tasknumber);
                            if(tasknumber == 2){
                                //机器人找到人
                                tasknumber = 3;
                                handler.sendEmptyMessage(4);
                                ServerSocketUtil.sendDateToClient("open", Constant.ip_bigScreen);
                            }else if(tasknumber == 11){
                                tasknumber = 12;
                                handler.sendEmptyMessage(4);
                                ServerSocketUtil.sendDateToClient("open", Constant.ip_bigScreen);
                            }
                        }
                    }else if(funtion.equals(Constant.Obstacle)){
                        JSONObject jsonObject = new JSONObject(data);
                        String str =  jsonObject.getString("result");
                        Constant.debugLog("obstacle"+ jsonObject.toString());
                        Toast.makeText(context,Constant.Obstacle+jsonObject.toString(),Toast.LENGTH_SHORT).show();
                        if("obstacle".equals(str)){
                            String direction = jsonObject.getString("direction");
                            setObstacle();
                            setBackObstacle();
                            if("front".equals(direction)){
                                surfaceview.config = 1;
                            }else if("back".equals(direction)){
                                surfaceview.config = 2;
                            }else{
                                surfaceview.config = 3;
                            }
                        }else{
                            surfaceview.config = 0;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setObstacle(){
        surfaceview.obstacle_x = surfaceview.bitmap_x + (surfaceview.bitmap.getWidth()- surfaceview.obstacle.getWidth())/2 + surfaceview.bitmap.getWidth() /3 * 2 * Math.sin(surfaceview.rote*Math.PI/180);
        surfaceview.obstacle_y = surfaceview.bitmap_y + (surfaceview.bitmap.getHeight() - surfaceview.obstacle.getHeight())/2 - surfaceview.bitmap.getHeight()/3 * 2  * Math.cos(surfaceview.rote*Math.PI/180);
    }
    public void setBackObstacle(){
        surfaceview.obstacle_back_x = surfaceview.bitmap_x + (surfaceview.bitmap.getWidth()- surfaceview.obstacle.getWidth())/2 + surfaceview.bitmap.getWidth() /3 * 2 * Math.sin((surfaceview.rote+180)*Math.PI/180);
        surfaceview.obstacle_back_y = surfaceview.bitmap_y + (surfaceview.bitmap.getHeight() - surfaceview.obstacle.getHeight())/2 - surfaceview.bitmap.getHeight() /3 * 2 * Math.cos((surfaceview.rote+180)*Math.PI/180);
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
                HashMap<String,Vector<Float>> planList;
                Map.Entry entry = (Map.Entry) iter.next();

                planList = (HashMap<String, Vector<Float>>) entry.getValue();

                String key = (String) entry.getKey();
                Element keysElement = document.createElement("key");
                keysElement.setAttribute("key",key);

                Vector<Float> point_xs = planList.get("point_xs");
                Vector<Float> point_ys = planList.get("point_ys");
                Vector<Float> arrayserchtime = planList.get("arrayserchtime");
                Vector<Float> arrayscope = planList.get("arrayscope");
                Vector<Float> arraygametime = planList.get("arraygametime");
                Element point_xs_ = document.createElement("point_xs");
                Element point_ys_ = document.createElement("point_ys");
                Element arrayserchtime_ = document.createElement("arrayserchtime");
                Element arrayscope_ = document.createElement("arrayscope");
                Element arraygametime_ = document.createElement("arraygametime");

                for(int x = 0;x < point_xs.size();x ++){
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //xml读取
    public synchronized  void readXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(Constant.filePath);
            Document parse;
            if (!file.exists()) {
                parse = builder.parse(context.getAssets().open("map.xml"));
            }else{
                parse = builder.parse(file);
            }
            parse.normalize();
            Element root = parse.getDocumentElement();
            NodeList planLists = root.getElementsByTagName("key");
            arrayPlanLists  = new HashMap<>();
            if(planLists.getLength()==0){
                parse = builder.parse(context.getAssets().open("map.xml"));
                root = parse.getDocumentElement();
                planLists = root.getElementsByTagName("key");
            }
            for (int i = 0,length = planLists.getLength(); i < length; i++) {
                arrayhash = new HashMap<>();
                Element item = (Element) planLists.item(i);
                String key = item.getAttribute("key");
                getFloat(item,"item_xs","point_xs");
                getFloat(item,"item_ys","point_ys");
                getFloat(item,"item_serchtime","arrayserchtime");
                getFloat(item,"item_scope","arrayscope");
                getFloat(item,"item_gametime","arraygametime");
                arrayPlanLists.put(key,arrayhash);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getFloat(Element item,String keys,String key){
        NodeList nodes = item.getElementsByTagName(keys);
        Vector<Float> floats=new Vector<>();
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
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            strings.add(key);
        }
        return strings;
    }

    public void updatekey(){
        readXML();
        Istouch = false;
        strings = getKey();
        if(strings !=null && context!=null){
            surfaceview.point_xs.removeAllElements();
            surfaceview.point_ys.removeAllElements();
            adapter = new ArrayAdapter<>(context,R.layout.item_spinselect,strings);
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
    private void dialogFace(){
        Constant.DIALOG_SHOW = true;
        FaceDialog.getDialog(context).show();
    }

    public void startPlan(){
        thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap<String,Vector<Float>> array = arrayPlanLists.get(strings.get((int) plannumber));

                if(cirles == 0){
                    CURRENT_CRILES = true;
                }else if(cirles > 0){
                    CURRENT_CRILES = false;
                }
                Constant.debugLog("CURRENT_CRILES"+CURRENT_CRILES);
                while(CURRENT_CRILES || cirles > 0){
                    planStart(array);
                    if(!CURRENT_CRILES){
                        cirles--;
                    }
                }
                thread = new Thread();
                surfaceview.current_plan_number = 0;
            }
        });
        thread.start();
    }

    public void startRoam(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Random random=new Random();
                    float x = 0 , y = 0;
                    while(x  < 150 || x > Constant.MyView_Width - 150){
                        x = random.nextInt(Constant.MyView_Width - 150);
                    }
                    while(y  < 150 || y > Constant.MyView_Height - 150){
                        y = random.nextInt(Constant.MyView_Height - 150);
                    }
                    synchronized (thread){
                        try {
                            tasknumber = 10;
                            //前往地图标注地点
                            sendNativePoint(x,y,0);
                            if(serchtimenumber_roam == 1) {
                                Constant.getConstant().sendCamera(scopenumber_roam,context);
                            }
                            IsFind = false;
                            thread.wait();
                            if(IsFind){
                                IsFind = false;
                                resetTimer2();
                                IsAway = false;
                                if(gametimenumber_roam == 0){
                                    Constant.debugLog("arraygametime_tmp = " + 0);
                                    timer.schedule(task, 15 * 1000);
                                }else if(gametimenumber_roam == 1){
                                    IsAway = true;
                                }else {
                                    float a = gametimenumber_roam - 1;
                                    timer.schedule(task, (long) (a * 60 * 1000));
                                    Constant.debugLog("tasknumber = " + a);
                                }
                                thread.wait();
                            }
                            Constant.getConstant().sendCamera(3,context);
                            Thread.sleep(1000);
                            //返回原点
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
                tasknumber = 4;
                Constant.getConstant().sendCamera(3,context);
                Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                handler.sendEmptyMessage(4);
                surfaceview.IsHuman = false;
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
                //互动触发
                tasknumber = 4;
                Constant.getConstant().sendCamera(3,context);
                handler.sendEmptyMessage(4);
                surfaceview.IsHuman = false;
                try {
                    ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                } catch (IOException e) {
                    e.printStackTrace();
                }

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

    //漫游模式
    private void go_Roam(){
        Isplan = false;
        Istouch = true;
        setVisible();
        linear_roam.setVisibility(View.VISIBLE);
    }
    //路线规划
    private void go_Plan(){
        surfaceview.Isplan = true;
        setVisible();
        linear_plan.setVisibility(View.VISIBLE);
        updatekey();
        Istouch = false;
        Isplan = true;
    }
    //点选模式
    private void go_Point() {
        surfaceview.Isplan = false;
        Istouch = true;
        setVisible();
        linear_point.setVisibility(View.VISIBLE);
        Isplan = false;
    }
    //规划新路线
    private void go_NewPlan() {
        arrayserchtime = new Vector<>();
        arrayscope = new Vector<>();
        arraygametime = new Vector<>();
        surfaceview.point_ys.removeAllElements();
        surfaceview.point_xs.removeAllElements();
        Constant.debugLog(surfaceview.point_xs.size()+"size_xs");
        Istouch = true;
        setVisible();
        linear_plan_info.setVisibility(View.VISIBLE);
    }
    //路线调整
    private void go_PlanChange() {
        if(null!=arrayPlanLists&&plannumber >= 0&&arrayPlanLists.size() > 0){
            setVisible();
            linearlayout_plan_change.setVisibility(View.VISIBLE);
            ChangeMapAdapter = new ChangeMapAdapter(arrayPlanLists,strings.get((int) plannumber),context,handler);
            plan_change_list.setAdapter(ChangeMapAdapter);
        }
    }
    private void planStart(final HashMap<String, Vector<Float>> array){
        Vector<Float> arrayserchtime_tmp;
        Vector<Float> arrayscope_tmp;
        Vector<Float> arraygametime_tmp;
        Vector<Float> xs_tmp;
        Vector<Float> ys_tmp;
        xs_tmp = array.get("point_xs");
        ys_tmp  = array.get("point_ys");
        arrayserchtime_tmp = array.get("arrayserchtime");
        arrayscope_tmp = array.get("arrayscope");
        arraygametime_tmp = array.get("arraygametime");
        int i = 0;
        while(i < xs_tmp.size()){
            sendNativePoint(xs_tmp.get(i),ys_tmp.get(i),0);
            synchronized (thread){
                try {
                    tasknumber = 0;
                    //前往地图标注地点
                    thread.wait();
                    surfaceview.current_plan_number = i+1;
                    //到达对应地点notify
                    if(arrayserchtime_tmp.get(i) != 0 && IsSuccus){
                        //发送 找人时间以及机器人旋转以及找人范围
                        resetTimer();
                        if(arrayserchtime_tmp.get(i) == 1){
                            Constant.debugLog("arrayserchtime_tmp = " + 0);
                            timer.schedule(task, 40 * 1000);
                        } else if (arrayserchtime_tmp.get(i) == 2) {
                            Constant.debugLog("arrayserchtime_tmp = " + 1);
                            timer.schedule(task, 2 * 40 * 1000);
                        } else if (arrayserchtime_tmp.get(i) == 3) {
                            Constant.debugLog("arrayserchtime_tmp = " + 2);
                            timer.schedule(task, 3 * 40 * 1000);
                        }
                        IsFind = false;
                        Constant.getConstant().sendCamera(arrayscope_tmp.get(i),context);
                        Constant.getConstant().sendBundle(Constant.Command,Constant.Peoplesearch,"");
                        //互动中 找人
                        thread.wait();
                        //如果有找到人则到达指定位置
                        if(IsFind){
                            IsFind = false;
                            Constant.debugLog("互动时间 = "+i);
                            resetTimer2();
                            IsAway = false;
                            if(arraygametime_tmp.get(i) == 0){
                                Constant.debugLog("arraygametime_tmp = " + 0);
                                timer.schedule(task, 15 * 1000);
                            }else if(arraygametime_tmp.get(i) == 1){
                                IsAway = true;
                            }else {
                                float a = arraygametime_tmp.get(i) - 1;
                                timer.schedule(task, (long) (a * 60 * 1000));
                                Constant.debugLog("tasknumber = " + a);
                            }
                            thread.wait();
                        }
                        //返回原点
                        sendNativePoint(xs_tmp.get(i),ys_tmp.get(i),0);
                        thread.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //返回后进行下一个地点
            i++;
        }
        sendNativePoint();
        tasknumber = -1;
        surfaceview.current_plan_number = 0;
    }
    private void pointStart(final float point_x,final float point_y){
        thread = null;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (thread){
                    try {
                        tasknumber = 0;
                        //前往地图标注地点
                        sendNativePoint(point_x,point_y,0);
                        thread.wait();
                        //到达对应地点notify
//                        resetTimer();
//                        timer.schedule(task, 40 * 1000);
//                        IsFind = false;
//                        Constant.getConstant().sendCamera(0,context);
//                        Constant.getConstant().sendBundle(Constant.Command,Constant.Peoplesearch,"");
//                        //互动中 找人
//                        thread.wait();
//                        //如果有找到人则到达指定位置
//                        if(IsFind){
//                            IsFind = false;
//                            resetTimer2();
//                            IsAway = true;
//                            thread.wait();
//                        }
//                        Constant.getConstant().sendCamera(3,context);
//                        sendNativePoint(point_x,point_y,0);
//                        thread.wait();
//                        tasknumber = -1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
}
