package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Spinner;
import android.widget.Toast;

import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.activity.WelcomeActivity;
import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.dialog.MyDialog;
import com.android.jdrd.headcontrol.util.Contact;
import com.android.jdrd.headcontrol.view.MyView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
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

public class MapFragment extends BaseFragment implements View.OnClickListener {

    private MyReceiver receiver = null;
    private MyView surfaceview = null;
    private int bitmap_width = 0,bitmap_height= 0;
    //路线选择、找人时间、找人范围、转弯角度、互动时间
    private Spinner planchooce,serchtime,scope,angle,gametime;
    //路线选择、找人时间、找人范围、转弯角度、互动时间；（对应number）
    private float plannumber,serchtimenumber,scopenumber,anglenumber,gametimenumber;
    private Context context;
    private double nLenStart = 0;
    private EditText point_x,point_y;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> map_Plan;
    private IntentFilter filter;
    private float eventx,eventy;
    public static boolean Istouch = false;
    //路线模式布局、路线模式详细设置；
    private LinearLayout linear_plan,linear_plan_info,linear_point,linear_roam;
    private HashMap<String,Vector<Float>> arrayhash;
    private Vector<Float> xs,ys,arrayserchtime,arrayscope,arrayangle,arraygametime;
    private HashMap<String,HashMap<String,Vector<Float>>> arrayPlanLists = new HashMap<>();
    private Vector<String> strings = new Vector<>();
    public  MapFragment(){
        super();
    }
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(surfaceview !=null&&surfaceview.point_xs.size()>=1){
                        surfaceview.point_xs.removeAllElements();
                        surfaceview.point_ys.removeAllElements();
                        arrayserchtime.removeAllElements();
                        arrayscope.removeAllElements();
                        arrayangle.removeAllElements();
                        arraygametime.removeAllElements();
                    }
                    linear_plan_info.setVisibility(View.GONE);
                    Istouch = false;
                    Contact.debugLog("btn_sure");
                    break;
                case 2:
                    Contact.debugLog("btn_cancle");
                    break;
                case 3:
                    Contact.debugLog("btn_warn_sure");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {//给当前的fragment绘制UI布局，可以使用线程更新UI
        mView=inflater.inflate(R.layout.fragment_map,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        surfaceview=(MyView)findViewById(R.id.surfaceview);
        surfaceview.myview_height = 1000;
        surfaceview.myview_width = 600;
        receiver=new MyReceiver();
        filter=new IntentFilter();
        filter.addAction("com.jdrd.fragment.Map");
        context.registerReceiver(receiver,filter);
        readXML();
    }

    @Override
    public void initData() {
        surfaceview.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.tuzi);

        InputStream is = getResources().openRawResource(R.mipmap.tuzi);
        surfaceview.gifMovie = Movie.decodeStream(is);
        surfaceview.bitmap = Bitmap.createBitmap(surfaceview.bitmap,0,0,100,100);

        bitmap_width = surfaceview.bitmap.getWidth();
        bitmap_height= surfaceview.bitmap.getHeight();

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
        findViewById(R.id.button_clearlast).setOnClickListener(this);
        findViewById(R.id.button_clearall).setOnClickListener(this);
        findViewById(R.id.button_plan).setOnClickListener(this);
        findViewById(R.id.button_plan_stop).setOnClickListener(this);
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
        findViewById(R.id.button_return).setOnClickListener(this);
        findViewById(R.id.button_remove).setOnClickListener(this);

        surfaceview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int nCnt = event.getPointerCount();
                int n = event.getAction();

                if(n==MotionEvent.ACTION_DOWN && 1 == nCnt){
                    eventx = event.getX();
                    eventy = event.getY();
                    if(Istouch){
                        if( event.getX() > 20 && event.getX() < 620 && event.getY() > 20 && event.getY() < 1020 ){
                            float a,b;
                            a = (event.getX() - 20 - surfaceview.translate_x )/ surfaceview.scale;
                            b = ((event.getY() - 20 - surfaceview.translate_y )/surfaceview.scale);
                            int x = (int) a % surfaceview.Scale;
                            int x_int = (int) a / surfaceview.Scale ;
                            int y =(int)  b % surfaceview.Scale ;
                            int y_int = (int) b / surfaceview.Scale;

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
                        }
                        Istouch = false;
                    }else{

                    }
                }else if(n==MotionEvent.ACTION_MOVE&&1 == nCnt){
                    surfaceview.translate_x += (event.getX() - eventx)/5;
                    surfaceview.translate_y += (event.getY() - eventy)/5;
                    eventx = event.getX();
                    eventy = event.getY();
                }

                //放大缩小
                if( (n&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN && 2 == nCnt)//<span style="color:#ff0000;">2表示两个手指</span>
                {
                    int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
                    int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));

                    nLenStart = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
                }else if( (n&MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP  && 2 == nCnt)
                {
                    int xlen = Math.abs((int)event.getX(0) - (int)event.getX(1));
                    int ylen = Math.abs((int)event.getY(0) - (int)event.getY(1));
                    double nLenEnd = Math.sqrt((double)xlen*xlen + (double)ylen * ylen);
                    if(nLenEnd > nLenStart)//通过两个手指开始距离和结束距离，来判断放大缩小
                    {
                        if(surfaceview.scale < 3) {
                            surfaceview.scale = (float) (surfaceview.scale + 0.2);
                        }
                    }
                    else
                    {
                        if(surfaceview.scale > 1){
                            surfaceview.scale = (float) (surfaceview.scale - 0.2);
                        }
                    }
                }
                return true;
            }
        });
        updatekey();
        planchooce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                plannumber = position;
                Contact.debugLog("plannumber = "+plannumber);
                HashMap<String,Vector<Float>> array = arrayPlanLists.get(strings.get((int) plannumber));
                Contact.debugLog("  onItemSelected  arrayPlanLists = "+arrayPlanLists.toString());
                Contact.debugLog(" onItemSelected arrayhash = "+arrayhash.toString());
//                    xs = array.get("point_xs");
//                    ys = array.get("point_ys");
//                    arrayserchtime = array.get("arrayserchtime");
//                    arrayscope = array.get("arrayscope");
//                    arrayangle = array.get("arrayangle");
//                    arraygametime = array.get("arraygametime");
                surfaceview.point_xs = array.get("point_xs");
                surfaceview.point_ys = array.get("point_ys");
                Contact.debugLog("arrayPlanLists = "+arrayPlanLists.toString());
                Contact.debugLog("arrayhash = "+arrayhash.toString());
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        init();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //清除上一步
            case R.id.button_clearlast:
                if(surfaceview !=null&&surfaceview.point_xs.size()>=1){
                    if(Istouch){
                        surfaceview.point_xs.remove(surfaceview.point_xs.size()-1);
                        surfaceview.point_ys.remove(surfaceview.point_ys.size()-1);
                        xs.remove(surfaceview.point_ys.size()-1);
                        ys.remove(surfaceview.point_ys.size()-1);
                        arrayserchtime.remove(surfaceview.point_ys.size()-1);
                        arrayscope.remove(surfaceview.point_ys.size()-1);
                        arrayangle.remove(surfaceview.point_ys.size()-1);
                        arraygametime.remove(surfaceview.point_ys.size()-1);
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
                    arrayangle = new Vector<>();
                    arraygametime = new Vector<>();
                }
                Istouch = true;
                break;
            //规划新路线
            case R.id.button_plan:
                Contact.debugLog("规划新路线 = "+arrayPlanLists.toString());
                arrayserchtime = new Vector<Float>();
                arrayscope = new Vector<Float>();
                arrayangle = new Vector<Float>();
                arraygametime = new Vector<Float>();
                xs = new Vector<Float>();
                ys = new Vector<Float>();
                Contact.debugLog("规划新路线 = surfaceview.point_xs"+surfaceview.point_xs.toString());

                surfaceview.point_xs = new Vector<Float>();
                surfaceview.point_ys = new Vector<Float>();
                Contact.debugLog("规划新路线 = surfaceview.point_xs"+surfaceview.point_xs.toString());

                Contact.debugLog("规划新路线 = "+arrayPlanLists.toString());
                Istouch = true;
                linear_plan_info.setVisibility(View.VISIBLE);
                break;
            //删除当前路线
            case R.id.button_remove:
                arrayPlanLists.remove(strings.get((int) plannumber));
                writeXML();
                updatekey();
                break;
            //停止执行
            case R.id.button_plan_stop:

                break;
            //执行路线
            case R.id.button_execut:
                if(Istouch){
                    Contact.showWarntext(context,handler);
                }else{
                    Contact.showWarn(context,handler);
                }
                break;
            //下一步
            case R.id.button_savenext:
                Contact.debugLog("下一步 = "+arrayPlanLists.toString());
                if(!Istouch){
                    arrayserchtime.add(serchtimenumber);
                    arrayscope.add(scopenumber);
                    arrayangle.add(anglenumber);
                    arraygametime.add(gametimenumber);
                    xs.add(surfaceview.point_xs.elementAt(arrayserchtime.size()-1));
                    ys.add(surfaceview.point_ys.elementAt(arrayserchtime.size()-1));
                    Istouch = true;
                    Toast.makeText(context,"请选取一个新的目标点",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context,"没有选取一个新的目标点",Toast.LENGTH_SHORT).show();
                }

                break;
            //规划完毕
            case R.id.button_saveall:
                if(!Istouch){
                    arrayserchtime.add(serchtimenumber);
                    arrayscope.add(scopenumber);
                    arrayangle.add(anglenumber);
                    arraygametime.add(gametimenumber);
                    xs.add(surfaceview.point_xs.elementAt(arrayserchtime.size()-1));
                    ys.add(surfaceview.point_ys.elementAt(arrayserchtime.size()-1));
                }

                arrayhash = new HashMap<>();
                arrayhash.put("point_xs",xs);
                arrayhash.put("point_ys",ys);
                arrayhash.put("arrayserchtime",arrayserchtime);
                arrayhash.put("arrayscope",arrayscope);
                arrayhash.put("arrayangle",arrayangle);
                arrayhash.put("arraygametime",arraygametime);

                dialog();

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
            case R.id.button_return:
                surfaceview.scale = 1;
                surfaceview.translate_x = 0;
                surfaceview.translate_y = 0;
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
    private void getUpPoint(float native_x,float native_y){
        if(native_x <= 0 && native_x >= -600 && native_y >= -760 && native_y <=240){
            surfaceview.bitmap_x = native_x * -100;
            surfaceview.bitmap_y = native_y *100 + 760;
        }
    }
    //发往底层
    private void setNativePoint(float up_x,float up_y){
        if(up_x >= 0 && up_x <=600 && up_y >= 0 && up_y <= 1000){
            float native_x = up_x / -100 ;
            float native_y = (float) ((up_y / 100) - 7.6);
        }
    }
    //设置方向
    private void getAngle(int angle){
        surfaceview.rote = -angle-45;
    }

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            float a = 0,b = 0;
//            Bundle bundle=intent.getExtras();
//            int count=bundle.getInt("count");
//            Contact.debugLog("OnItemSelectedListener = "+bundle.toString());
            getUpPoint(a,b);
        }
    }

    private void sendBundle(){
        float a = 0,b = 0;
        setNativePoint(a,b);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
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
                String key = (String) entry.getKey();

                planList = (HashMap<String, Vector<Float>>) entry.getValue();

                Element keysElement = document.createElement("key");
                keysElement.setAttribute("key",key);

                Vector<Float> point_xs = planList.get("point_xs");
                Vector<Float> point_ys = planList.get("point_ys");
                Vector<Float> arrayserchtime = planList.get("arrayserchtime");
                Vector<Float> arrayscope = planList.get("arrayscope");
                Vector<Float> arrayangle = planList.get("arrayangle");
                Vector<Float> arraygametime = planList.get("arraygametime");
                Element point_xs_ = document.createElement("point_xs");
                Element point_ys_ = document.createElement("point_ys");
                Element arrayserchtime_ = document.createElement("arrayserchtime");
                Element arrayscope_ = document.createElement("arrayscope");
                Element arrayangle_ = document.createElement("arrayangle");
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
                    Element item_angle = document.createElement("item_angle");
                    item_angle.setTextContent(arrayangle.elementAt(x)+"");
                    Element item_gametime = document.createElement("item_gametime");
                    item_gametime.setTextContent(arraygametime.elementAt(x)+"");
                    point_xs_.appendChild(item_xs);
                    point_ys_.appendChild(item_ys);
                    arrayserchtime_.appendChild(item_serchtime);
                    arrayscope_.appendChild(item_scope);
                    arrayangle_.appendChild(item_angle);
                    arraygametime_.appendChild(item_gametime);
                }
                keysElement.appendChild(point_xs_);
                keysElement.appendChild(point_ys_);
                keysElement.appendChild(arrayserchtime_);
                keysElement.appendChild(arrayscope_);
                keysElement.appendChild(arrayangle_);
                keysElement.appendChild(arraygametime_);

                planLists.appendChild(keysElement);
            }

            document.appendChild(planLists);

            TransformerFactory transfactory = TransformerFactory.newInstance();
            Transformer transformer = transfactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");// 设置输出采用的编码方式
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");// 是否自动添加额外的空白
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");// 是否忽略XML声明
            FileOutputStream fos = new FileOutputStream(Contact.filePath);
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
            File file = new File(Contact.filePath);
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
            for (int i = 0; i < planLists.getLength(); i++) {
                arrayhash = new HashMap<>();
                Element item = (Element) planLists.item(i);
                String key = item.getAttribute("key");
                NodeList nodes = item.getElementsByTagName("item_xs");
                getFloat(item,"item_xs","point_xs");
                getFloat(item,"item_ys","point_ys");
                getFloat(item,"item_serchtime","arrayserchtime");
                getFloat(item,"item_scope","arrayscope");
                getFloat(item,"item_angle","arrayangle");
                getFloat(item,"item_gametime","arraygametime");
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
        for (int j = 0; j < nodes.getLength(); j++) {
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
        strings = getKey();
        adapter = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item,strings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planchooce.setAdapter(adapter);
    }
    private MyDialog dialog ;
    private EditText editText;
    private void dialog() {
        dialog = new MyDialog(context);
        editText = (EditText) dialog.getEditText();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dosomething youself
                Contact.debugLog("arrayhash 22222222= "+arrayhash.toString());
                arrayPlanLists.put(editText.getText().toString().trim(),arrayhash);
                Contact.debugLog("arrayPlanLists 22222222= "+arrayPlanLists.toString());
                writeXML();

                surfaceview.point_xs.removeAllElements();
                surfaceview.point_ys.removeAllElements();
                arrayserchtime.removeAllElements();
                arrayscope.removeAllElements();
                arrayangle.removeAllElements();
                arraygametime.removeAllElements();
                linear_plan_info.setVisibility(View.GONE);

                readXML();
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
}
