package com.android.jdrd.headcontrol.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.jdrd.headcontrol.R;
import com.android.jdrd.headcontrol.common.BaseFragment;
import com.android.jdrd.headcontrol.dialog.MyDialog;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.android.jdrd.headcontrol.util.Constant;
import com.android.jdrd.headcontrol.view.MyView;
import com.eaibot.bean.GoalPoseStamped;
import com.eaibot.bean.MarkPose;
import com.eaibot.bean.RobotPose;
import com.eaibot.layer.ControlLayer;
import com.eaibot.layer.GoalPathLayer;
import com.eaibot.layer.MapGridLayer;
import com.eaibot.layer.MultiGoalPoseLayer;
import com.eaibot.layer.PathLayer;
import com.eaibot.layer.RobotPoseLayer;
import com.eaibot.listener.OnMapClickListener;
import com.eaibot.publisher.MultiGoalPosePublisher;
import com.eaibot.ros.RosService;
import com.eaibot.shape.MapView;
import com.eaibot.utils.MapUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.ros.node.NodeMain;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

import static com.android.jdrd.headcontrol.fragment.MapFragment.Isplan;

/**
 * Created by jdrd on 2017/5/24.
 *
 */

public class EaiFragment extends BaseFragment implements View.OnClickListener, Animation.AnimationListener {
    private RobotPoseLayer robotPoseLayer;
    private MapUtil mapUtil;
    private SharedPreferences sp;
    private MultiGoalPosePublisher multiGoalPosePublisher;
    private ImageView imgViewmapnRight;
    private static int cirles = 0;
    private boolean IsSuccus = false;
    private static boolean CURRENT_CRILES = false;
    private static int CurrentPoint = 0;
    private ArrayAdapter<String> adapter;
    private static boolean IsRight = false;
    private Timer timer;
    public static boolean IsFind = false,IsAway = false ,Isplan = false;
    private static Thread thread;
    private Vector<Float> arrayserchtime,arrayscope,arraygametime;
    private LinearLayout linearlayout_map,linear_plan,linear_plan_info,linear_point,linear_roam,linearlayout_all,linearlayout_plan_change;
    private float plannumber =0,serchtimenumber =0,scopenumber =0,gametimenumber =0,serchtimenumber_roam =0,scopenumber_roam =0,gametimenumber_roam =0;
    private RelativeLayout map_right_Ralative;
    private MyReceiver receiver;

    private ArrayList<RobotPose> robotList = new ArrayList<>();
    private ArrayList<MarkPose> markList = new ArrayList<>();
    private HashMap<String,HashMap<String,Vector<Float>>> arrayPlanLists = new HashMap<>();
    private Vector<String> strings = new Vector<>();
    private HashMap<String,Vector<Float>> configArray = new HashMap<>();
    public  EaiFragment(){
        super();
    }
    private TimerTask task;
    private Spinner planchooce,serchtime,scope,gametime,plan_cirles,gametime_roam,scope_roam,serchtime_roam;
    private RosService rosService;
    private NodeMain nodeMain;
    private ControlLayer controlLayer;
    private MapView mapview;
    private MapGridLayer mapGridLayer;
    private PathLayer pathLayer;
    private GoalPathLayer goalPathLayer;
//        private MultiGoalPoseLayer goalPathLayer;
    @SuppressLint("ValidFragment")
    public EaiFragment(Context context){
        super(context);
    }
    public final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    if(thread!=null){
                        if(thread.isAlive()){
                            thread = new Thread();
                        }
                        Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    }
                    Constant.getConstant().sendCamera(3,mContext);
                    CURRENT_CRILES = false;
                    if(Constant.CURRENTINDEX_MAP == 2){
                        ((Button)findViewById(R.id.button_plan_stop)).setText("继续执行");
                    }
                    try {
                        ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    break;
                case 4:
                    if(thread!=null){
                        synchronized (thread) {
                            thread.notify();
                        }
                    }
                    break;
                case 5:
                    break;
                case 6:
                    if(thread!=null){
                        if(thread.isAlive()){
                            thread = new Thread();
                        }
                        Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    }
                    Constant.getConstant().sendCamera(3,mContext);
                    Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                    try {
                        ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                        Thread.sleep(500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case 7:
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_eaimap,container,false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void initView() {
        mapview = (MapView) findViewById(R.id.eaimap);
        mapview = mapview.getListMapView(mapview);
        controlLayer = mapview.getControlLayer();
        imgViewmapnRight = (ImageView) findViewById(R.id.imgViewmapnRight_eai);
        linearlayout_all = (LinearLayout) findViewById(R.id.linearlayout_all_eai);
        map_right_Ralative = (RelativeLayout) findViewById(R.id.map_right_Ralative_eai);

        linearlayout_plan_change = (LinearLayout) findViewById(R.id.relative_plan_change_eai);
        linearlayout_map = (LinearLayout) findViewById(R.id.linearlayout_map_eai);
        linear_plan = (LinearLayout) findViewById(R.id.relative_plan_eai);
        linear_plan_info = (LinearLayout) findViewById(R.id.relative_plan_info_eai);
        linear_point = (LinearLayout) findViewById(R.id.linearlayout_point_eai);
        linear_roam = (LinearLayout) findViewById(R.id.linearlayout_roam_eai);

        planchooce = (Spinner) findViewById(R.id.spinner_plan_eai);
        serchtime = (Spinner) findViewById(R.id.serchtime_eai);
        scope = (Spinner) findViewById(R.id.scope_eai);
        gametime_roam = (Spinner) findViewById(R.id.gametime_roam_eai);
        scope_roam = (Spinner) findViewById(R.id.scope_roam_eai);
        serchtime_roam = (Spinner) findViewById(R.id.serchtime_roam_eai);
        plan_cirles = (Spinner) findViewById(R.id.spinner_plan_cirles_eai);
        gametime = (Spinner) findViewById(R.id.gametime_eai);

    }



    @Override
    public void initData() {
        initSetupMap();
        ArrayList<String> map_Plan = new ArrayList<>();
        map_Plan.add("远");
        map_Plan.add("中");
        map_Plan.add("近");
        adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,map_Plan);
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
        adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,map_Plan);
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
        adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,map_Plan);
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
        adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,map_Plan);
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
        adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,map_Plan);
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
        adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,map_Plan);
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
        adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,map_Plan);
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
//        initNavigationMap();
        planchooce.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tasknumber = 0;
                CurrentPoint = 0;
                plannumber = position;
                goalPathLayer.clearAllMarkerGoals();
                HashMap<String, Vector<Float>> array = arrayPlanLists.get(strings.get((int) plannumber));
                Constant.debugLog("array"+array.toString());
                if(array!=null&&array.size()>0){
                    RobotPose robotPose;
                    MarkPose markPose;
                    markList = new ArrayList<>();
                    robotList = new ArrayList<>();
                    Vector<Float> point_xs = array.get("point_xs");
                    Vector<Float> point_ys = array.get("point_ys");
                    Vector<Float> point_ws = array.get("point_ws");
                    Constant.debugLog("point_xs"+point_xs.toString() +"\n"
                    +"point_ys"+point_ys.toString() +"\n"
                            +"point_ys"+point_ys.toString());
                    if(point_xs!=null && point_xs.size()>0){
                        for(int i = 0,size = point_xs.size();i<size;i++){
                            robotPose = new RobotPose();
                            markPose = new MarkPose();
                            robotPose.setPointX(point_xs.get(i));
                            robotPose.setPointY(point_ys.get(i));
                            robotPose.setQuaternionW(point_ws.get(i));
                            Constant.debugLog("robotPose"+robotPose.toString());
                            robotList.add(robotPose);
                            markPose.setName(""+i);
                            markPose.setId(i);
                            markPose.setRobotPose(robotPose);
                            Constant.debugLog("markPose"+markPose.toString());
                            markList.add(markPose);
                        }
                    }
                    if(markList!=null && markList.size()>0){
                        goalPathLayer.setMarkPoses(markList);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    public void updateplan(){
        readXML();
        strings = getKey();
        if(strings !=null && mContext!=null){
            adapter = new ArrayAdapter<>(mContext,R.layout.item_spinselect,strings);
            adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
            planchooce.setAdapter(adapter);
//            planchooce.setSelection(0,true);
        }
    }

    //获取路线key名称HashMap<String, ArrayList<RobotPose>>
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

    @Override
    public void initEvent() {
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.scale_add).setOnClickListener(this);
        findViewById(R.id.scale_subtract).setOnClickListener(this);
        findViewById(R.id.rotate_left).setOnClickListener(this);
        findViewById(R.id.rotate_right).setOnClickListener(this);
        imgViewmapnRight.setOnClickListener(this);

        findViewById(R.id.button_plan_back_eai).setOnClickListener(this);
        findViewById(R.id.button_plan_info_back_eai).setOnClickListener(this);
        findViewById(R.id.button_point_back_eai).setOnClickListener(this);
        findViewById(R.id.button_roam_back_eai).setOnClickListener(this);
        findViewById(R.id.button_pointchooce_eai).setOnClickListener(this);
        findViewById(R.id.button_pathplan_eai).setOnClickListener(this);
        findViewById(R.id.button_cruise_eai).setOnClickListener(this);
        findViewById(R.id.button_returnback_eai).setOnClickListener(this);
        findViewById(R.id.button_setreturn_eai).setOnClickListener(this);
        findViewById(R.id.button_versioncode_eai).setOnClickListener(this);

        findViewById(R.id.button_next_eai).setOnClickListener(this);
        findViewById(R.id.button_point_stop_eai).setOnClickListener(this);

        findViewById(R.id.button_roam_start_eai).setOnClickListener(this);
        findViewById(R.id.button_roam_stop_eai).setOnClickListener(this);

        findViewById(R.id.button_choose_eai).setOnClickListener(this);
        findViewById(R.id.button_clearlast_eai).setOnClickListener(this);
        findViewById(R.id.button_saveall_eai).setOnClickListener(this);
        findViewById(R.id.button_clearall_eai).setOnClickListener(this);

        findViewById(R.id.button_clearall_eai).setOnClickListener(this);
        findViewById(R.id.button_clearall_eai).setOnClickListener(this);
        findViewById(R.id.button_clearall_eai).setOnClickListener(this);
        findViewById(R.id.button_clearall_eai).setOnClickListener(this);
        findViewById(R.id.button_clearall_eai).setOnClickListener(this);

        findViewById(R.id.button_execut_eai).setOnClickListener(this);
        findViewById(R.id.button_plan_stop_eai).setOnClickListener(this);
        findViewById(R.id.button_plan_eai).setOnClickListener(this);
        findViewById(R.id.button_remove_eai).setOnClickListener(this);
        findViewById(R.id.plan_change_eai).setOnClickListener(this);
        findViewById(R.id.button_return_eai).setOnClickListener(this);

    }

    @Override
    public void onStart() {
        super.onStart();
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jdrd.fragment.Map");
        if(mContext!=null && receiver != null ){
            mContext.registerReceiver(receiver,filter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //左移
            case R.id.left:
                controlLayer.autoTranslate(100,0);
                break;
            //右移
            case R.id.right:
                controlLayer.autoTranslate(-100,0);
                break;
            //放大
            case R.id.scale_add:
                controlLayer.zoomInBaseCenter();
                break;
            //缩小
            case R.id.scale_subtract:
                controlLayer.zoomOutBaseCenter();
                break;
            //下移
            case R.id.down:
                controlLayer.autoTranslate(0,-100);
                break;
            //上移
            case R.id.up:
                controlLayer.autoTranslate(0,100);
                break;
            //向左旋转
            case R.id.rotate_left:
                controlLayer.anticlockwiseRotate();
                break;
            //向右旋转
            case R.id.rotate_right:
                controlLayer.clockwiseRotate();
                break;

            //即时操控
            case R.id.button_pointchooce_eai:
                Constant.CURRENTINDEX_MAP = 1;
                goalPathLayer.setGoalPoseMode();
                go_Point();
                Isplan = false;
                break;

            case R.id.button_next_eai:
                Constant.debugLog("robotList"+robotList.toString());
                if(robotList!=null&& robotList.size()>0){
//                    goalPathLayer.navToGoalPose(robotList.get(0));
                    RobotPose robotPose = robotList.get(0);
                    sendNativePoint(robotPose.getPointX(),robotPose.getPointY(),robotPose.getQuaternionW()*360);
                }
                break;

            case R.id.button_point_stop_eai:
                goalPathLayer.cancelNavToGoalPose();
//                goalPathLayer.cancelNavigation();
                break;

            case R.id.button_pathplan_eai:
                Constant.CURRENTINDEX_MAP = 2;
                serchtime.setSelection(0,true);
                scope.setSelection(0,true);
                gametime.setSelection(0,true);
                plan_cirles.setSelection(0,true);
                updateplan();
                go_Plan();
                Isplan = true;
                break;

            case R.id.button_cruise_eai:
                Constant.CURRENTINDEX_MAP = 3;
                go_Roam();
                serchtime_roam.setSelection(0,true);
                scope_roam.setSelection(0,true);
                gametime_roam.setSelection(0,true);
                break;

            case R.id.button_returnback_eai:
                Toast.makeText(mContext,"返回原点",Toast.LENGTH_SHORT).show();
                break;

            case R.id.button_setreturn_eai:
//                goalPathLayer.setInitPoseMode();
                break;

            case R.id.button_versioncode_eai:
                PackageInfo pi = null;//getPackageName()是你当前类的包名，0代表是获取版本信息
                try {
                    PackageManager pm = mContext.getPackageManager();
                    pi = pm.getPackageInfo(mContext.getPackageName(), 0);
                    String name = pi.versionName;
                    int code = pi.versionCode;
                    Toast.makeText(mContext,"当前版本号"+name+",当前代码版本号"+code,Toast.LENGTH_SHORT).show();
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.imgViewmapnRight_eai:
                startAnimationRight();
                break;

            case R.id.button_plan_back_eai:
                Constant.CURRENTINDEX_MAP = 0;
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_plan.setVisibility(View.GONE);
                goalPathLayer.setInitPoseMode();
                break;

            case R.id.button_plan_info_back_eai:
                updateplan();
                Constant.CURRENTINDEX_MAP = 2;
                linear_plan_info.setVisibility(View.GONE);
                linear_plan.setVisibility(View.VISIBLE);
                break;

            case R.id.button_point_back_eai:
                Constant.CURRENTINDEX_MAP = 0;
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_point.setVisibility(View.GONE);
                goalPathLayer.setInitPoseMode();
                break;

            case R.id.button_roam_back_eai:
                Constant.CURRENTINDEX_MAP = 0;
                linearlayout_map.setVisibility(View.VISIBLE);
                linear_roam.setVisibility(View.GONE);
                goalPathLayer.setInitPoseMode();
                break;

            //执行路线
            case R.id.button_execut_eai:
                startPlan();
                break;
            //暂停
            case R.id.button_plan_stop_eai:
                handler.sendEmptyMessage(3);
                break;
            //新路线
            case R.id.button_plan_eai:
                goalPathLayer.setGoalPoseMode();
                Isplan = true;
                serchtime_roam.setSelection(0,true);
                scope_roam.setSelection(0,true);
                gametime_roam.setSelection(0,true);
                go_NewPlan();
                robotList = new ArrayList<>();
                arrayserchtime = new Vector<>();
                arrayscope = new Vector<>();
                arraygametime = new Vector<>();
                goalPathLayer.clearAllMarkerGoals();
                break;

            //删除
            case R.id.button_remove_eai:
                if(arrayPlanLists.size() > 0){
                    Toast.makeText(mContext,"删除路线",Toast.LENGTH_SHORT).show();
                    readXML();
                    arrayPlanLists.remove(strings.get((int) plannumber));
                    writeXML();
                    updateplan();
                }
                goalPathLayer.clearAllMarkerGoals();
                break;
            //调整
            case R.id.plan_change_eai:
                break;
            //返回原点
            case R.id.button_return_eai:
                handler.sendEmptyMessage(4);
                break;
            //开始漫游
            case R.id.button_roam_start_eai:
                startRoam();
                Toast.makeText(mContext,"开始漫游行走",Toast.LENGTH_SHORT).show();
                startAnimationRight();
                break;
            //停止漫游
            case R.id.button_roam_stop_eai:
                break;
            //清除当前
            case R.id.button_clearlast_eai:
                int robotListsize = robotList.size();
                if(robotList!=null&&robotListsize>0){
                    Constant.debugLog("arrayserchtime"+arrayserchtime.toString());
                    robotList.remove(robotListsize-1);
                    arrayserchtime.remove(robotListsize -1);
                    arrayscope.remove(robotListsize -1);
                    arraygametime.remove(robotListsize -1);
                    goalPathLayer.clearLastMarkerGoal();
                }
                break;
            //清除所有
            case R.id.button_clearall_eai:
                robotList = new ArrayList<>();
                arrayserchtime = new Vector<>();
                arrayscope = new Vector<>();
                arraygametime = new Vector<>();
                goalPathLayer.clearAllMarkerGoals();
                break;
            //规划完毕
            case R.id.button_saveall_eai:
                Constant.CURRENTINDEX_MAP = 2;
                if(robotList.size()> 0 ){
                    Vector<Float> point_x = new Vector<>();
                    Vector<Float> point_y = new Vector<>();
                    Vector<Float> point_w = new Vector<>();
                    RobotPose robotPose;
                    readXML();
                    Constant.debugLog("robotList"+robotList.toString());
                    for(int i =0,size = robotList.size();i<size;i++){
                        robotPose =  robotList.get(i);
                        point_x.add((float) robotPose.getPointX());
                        point_y.add((float) robotPose.getPointY());
                        point_w.add((float) robotPose.getQuaternionW());
                    }
                    Constant.debugLog("point_x"+point_x.toString());
                    configArray = new HashMap<>();
                    configArray.put("point_xs",point_x);
                    configArray.put("point_ys",point_y);
                    configArray.put("point_ws",point_w);
                    configArray.put("arrayserchtime",arrayserchtime);
                    configArray.put("arrayscope",arrayscope);
                    configArray.put("arraygametime",arraygametime);
                    dialog();
                }else{
                    Toast.makeText(mContext,"没有规划任何路线",Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
    //漫游模式
    private void go_Roam(){
        setVisible();
        linear_roam.setVisibility(View.VISIBLE);
    }

    //规划新路线
    private void go_NewPlan() {
        arrayserchtime = new Vector<>();
        arrayscope = new Vector<>();
        arraygametime = new Vector<>();
        setVisible();
        linear_plan_info.setVisibility(View.VISIBLE);
    }

    //发往底层
    private void sendNativePoint(double up_x,double up_y ,double angle){
        Map map  = new LinkedHashMap();
        map.put("point_x",up_x);
        map.put("point_y",up_y);
        map.put("angle",angle);
        Constant.getConstant().sendBundle(Constant.Command,Constant.Navigation,map);
        Constant.debugLog("map"+map.toString());
    }

    //路线规划
    private void go_Plan(){
        setVisible();
        linear_plan.setVisibility(View.VISIBLE);
    }
    //点选模式
    private void go_Point() {
        setVisible();
        linear_point.setVisibility(View.VISIBLE);
    }
    private void setVisible(){
        linearlayout_plan_change.setVisibility(View.GONE);
        linear_plan.setVisibility(View.GONE);
        linearlayout_map.setVisibility(View.GONE);
        linear_plan_info.setVisibility(View.GONE);
        linear_point.setVisibility(View.GONE);
        linear_roam.setVisibility(View.GONE);
    }
    public void initSetupMap(){
        rosService = RosService.getRosServiceInstance(mContext);
        mapGridLayer = new MapGridLayer();
        pathLayer = new PathLayer();
        goalPathLayer = new GoalPathLayer();
//        goalPathLayer = new GoalPathLayer();
        robotPoseLayer = new RobotPoseLayer();
        mapview.addLayer(mapGridLayer);
        mapview.addLayer(pathLayer);
        mapview.addLayer(goalPathLayer);
        mapview.addLayer(robotPoseLayer);
        mapview.init(rosService.getNodeMainExecutor());

        rosService.addRosNode(mapview);
        mapUtil = new MapUtil();
        mapUtil.setNodeConfiguration(rosService.getNodeMainExecutor(),rosService.getNodeConfiguration());
//        multiGoalPosePublisher = new MultiGoalPosePublisher();
//        rosService.addRosNode(multiGoalPosePublisher);

        goalPathLayer.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onClicked(boolean b, RobotPose robotPose) {
                Constant.debugLog("MapClick" + b +"RobotPose "+ robotPose);
                if(b){
                    if(!Isplan){
//                        goalPathLayer.clearAllMarkerGoals();
                        robotList = new ArrayList<>();
                        robotList.add(robotPose);
                        goalPathLayer.clearAllMarkerGoals();
                        markList = new ArrayList<>();
                        Constant.debugLog("robotPose"+robotPose.toString());
                        MarkPose markPose = new MarkPose(0,"point",robotPose);
                        robotPose.setQuaternionX(0);
                        robotPose.setQuaternionY(0);
                        robotPose.setQuaternionZ(0);
                        robotPose.setPointZ(0);
                        Constant.debugLog("robotPose"+robotPose.toString());
                        markList.add(markPose);
                        goalPathLayer.setMarkPoses(markList);
                    }else{
                        Constant.debugLog("robotlist"+robotList);
                        robotList.add(robotPose);
                        arrayserchtime.add(serchtimenumber);
                        arrayscope.add(scopenumber);
                        arraygametime.add(gametimenumber);
                    }
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        goalPathLayer.setInitPoseMode();
        updateplan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        IsRight = false;
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
            translateAnimation.setAnimationListener(EaiFragment.this);
            map_right_Ralative.startAnimation(translateAnimation);
        }else {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,linearlayout_all.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(false);
            translateAnimation.setAnimationListener(EaiFragment.this);
            map_right_Ralative.startAnimation(translateAnimation);
        }

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
            Constant.debugLog(""+StringE);
            if(StringE !=null && !StringE.equals("")){
                pasreJson(StringE);
            }
        }
    }
    private static int tasknumber = -1;
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
                            }else if(tasknumber == 30){
                                handler.sendEmptyMessage(7);
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
                            }else if(tasknumber == 30){
                                handler.sendEmptyMessage(7);
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
                            if(task!=null){
                                task.cancel();
                            }
                            try {
                                if(tasknumber == 1){
                                    //摄像头搜索到人
                                    IsFind = true;
                                    tasknumber = 2;
                                    ServerSocketUtil.sendDateToClient(string, Constant.ip_ros);
                                }else if(tasknumber == 10){
                                    IsFind = true;
                                    tasknumber = 11;
                                    Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                                    Map map  = new LinkedHashMap();
                                    map.put("degree",degree);
                                    map.put("distance",distance);
                                    Constant.getConstant().sendCamera(0,getContext());
                                    Constant.getConstant().sendBundle(Constant.Command,Constant.Roamsearch,map);
                                    Constant.debugLog("map"+map);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else if(str.equals("nobody")){
                        }else if(str.equals("away")){
                            if(tasknumber == 3 && IsAway){
                                tasknumber = 4 ;
                                if(task!=null ){
                                    task.cancel();
                                }
                                Constant.getConstant().sendCamera(3,getContext());
                                ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                                handler.sendEmptyMessage(4);
                            }else if(tasknumber == 12 && IsAway){
                                tasknumber = 13 ;
                                if(task!=null ){
                                    task.cancel();
                                }
                                ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                                Constant.getConstant().sendCamera(3,getContext());
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
                        if("obstacle".equals(str)){
                            String direction = jsonObject.getString("direction");
                            if("front".equals(direction)){
                            }else if("back".equals(direction)){
                            }else{
                            }
                        }else{
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
    public void planStart(final HashMap<String, Vector<Float>> array){
        Constant.debugLog("size"+robotList.size());
        Vector<Float> arrayserchtime_tmp;
        Vector<Float> arrayscope_tmp;
        Vector<Float> arraygametime_tmp;
        Vector<Float> xs_tmp;
        Vector<Float> ys_tmp;
        Vector<Float> ws_tmp;
        xs_tmp = array.get("point_xs");
        ys_tmp = array.get("point_ys");
        ws_tmp = array.get("point_ws");
        arrayserchtime_tmp = array.get("arrayserchtime");
        arrayscope_tmp = array.get("arrayscope");
        arraygametime_tmp = array.get("arraygametime");
        Float ser,game;
        CurrentPoint = 0;
        while(CurrentPoint < robotList.size()){
            try {
                //
                tasknumber = 0;
                synchronized (thread) {
                    sendNativePoint(xs_tmp.get(CurrentPoint), ys_tmp.get(CurrentPoint), ws_tmp.get(CurrentPoint));
                    thread.wait();
                    ser = arrayserchtime_tmp.get(CurrentPoint);
                    if (ser != 0 && IsSuccus == true) {
                        resetFindTimer();
                        if (ser == 1) {
                            Constant.debugLog("arrayserchtime = " + 0);
                            timer.schedule(task, 40 * 1000);
                        } else if (ser == 2) {
                            Constant.debugLog("arrayserchtime = " + 1);
                            timer.schedule(task, 2 * 40 * 1000);
                        } else if (ser == 3) {
                            Constant.debugLog("arrayserchtime = " + 2);
                            timer.schedule(task, 3 * 40 * 1000);
                        }
                        IsFind = false;
                        Constant.getConstant().sendCamera(arrayscope_tmp.get(CurrentPoint), mContext);
                        Constant.getConstant().sendBundle(Constant.Command, Constant.Peoplesearch, "");
                        thread.wait();
                        //如果有找到人则到达指定位置
                        if (IsFind) {
                            IsFind = false;
                            Constant.debugLog("互动时间 = " + CurrentPoint);
                            resetGameTimer();
                            IsAway = false;
                            game = arraygametime_tmp.get(CurrentPoint);
                            if (game == 0) {
                                Constant.debugLog("arraygametime_tmp = " + 0);
                                timer.schedule(task, 15 * 1000);
                            } else if (game == 1) {
                                IsAway = true;
                            } else {
                                float a = game - 1;
                                timer.schedule(task, (long) (a * 60 * 1000));
                                Constant.debugLog("tasknumber = " + a);
                            }
                            thread.wait();
                        }
                        sendNativePoint(xs_tmp.get(CurrentPoint), ys_tmp.get(CurrentPoint), ws_tmp.get(CurrentPoint));
                        thread.wait();
                    }
                    CurrentPoint++;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        CurrentPoint = 0;
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
                CURRENT_CRILES = false;
                cirles = 1;
                while (CURRENT_CRILES || cirles > 0) {
                    Constant.debugLog("CURRENT_CRILES" + CURRENT_CRILES);
//                    synchronized (array) {
                    planStart(array);
                    if (!CURRENT_CRILES) {
                        cirles--;
                    }
//                    }
                }
                thread = new Thread();
            }

        });
        thread.start();
    }
    public void resetFindTimer(){
        if (task != null){
            task.cancel();  //将原任务从队列中移除
        }
        timer = new Timer();
        task = new TimerTask() {
            public void run () {
                Constant.getConstant().sendCamera(3,mContext);
                Constant.getConstant().sendBundle(Constant.Command,Constant.StopSearch,"");
                handler.sendEmptyMessage(4);
            }
        };
    }
    public void resetGameTimer(){
        if (task != null){
            task.cancel();  //将原任务从队列中移除
        }
        timer = new Timer();
        task = new TimerTask() {
            public void run () {
                //互动触发
                Constant.getConstant().sendCamera(3,mContext);
                handler.sendEmptyMessage(4);
                try {
                    ServerSocketUtil.sendDateToClient("close", Constant.ip_bigScreen);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }

    public void startRoam(){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
//                    Random random=new Random();
//                    float x = 0 , y = 0;
//                    while(x  < 110 || x > Constant.MyView_Width - 120){
//                        x = random.nextInt(Constant.MyView_Width - 120);
//                    }
//                    while(y  < 250 || y > Constant.MyView_Height - 182){
//                        y = random.nextInt(Constant.MyView_Height - 182);
//                    }
                    synchronized (thread){
                        try {
                            //前往地图标注地点
                            if(serchtimenumber_roam == 1) {
                                Constant.getConstant().sendCamera(scopenumber_roam,mContext);
                            }
                            IsFind = false;
                            thread.wait();
                            if(IsFind){
                                IsFind = false;
                                resetGameTimer();
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
                            Constant.getConstant().sendCamera(3,mContext);
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
    private MyDialog dialog ;
    private EditText editText;
    private void dialog() {
        dialog = new MyDialog(mContext);
        dialog.setCanceledOnTouchOutside(false);
        editText = (EditText) dialog.getEditText();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"".equals(editText.getText().toString().trim())){
                    Constant.debugLog("configArray"+configArray.toString());
                    arrayPlanLists.put(editText.getText().toString().trim(),configArray);
                    Constant.debugLog("arrayPlanLists"+arrayPlanLists.toString());
                    writeXML();
//                    goalPathLayer.clearAllMarkerGoals();
                    arrayserchtime.removeAllElements();
                    arrayscope.removeAllElements();
                    arraygametime.removeAllElements();
                    linear_plan_info.setVisibility(View.GONE);
                    linear_plan.setVisibility(View.VISIBLE);
                    robotList.clear();
                    dialog.dismiss();
                }else {
                    Toast.makeText(mContext,"请输入合法的路线名称",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Toast.makeText(mContext,"请继续规划路线",Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }


    //xml读取
    public synchronized  void readXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(Constant.filePath);
            Document parse;
            if (!file.exists()) {
                parse = builder.parse(mContext.getAssets().open("map.xml"));
            }else{
                parse = builder.parse(file);
            }
            parse.normalize();
            Element root = parse.getDocumentElement();
            NodeList planLists = root.getElementsByTagName("key");
            arrayPlanLists  = new HashMap<>();
            if(planLists.getLength()==0){
                parse = builder.parse(mContext.getAssets().open("map.xml"));
                root = parse.getDocumentElement();
                planLists = root.getElementsByTagName("key");
            }
            for (int i = 0,length = planLists.getLength(); i < length; i++) {
                configArray = new HashMap<>();
                Element item = (Element) planLists.item(i);
                String key = item.getAttribute("key");
                getFloat(item,"item_xs","point_xs");
                getFloat(item,"item_ys","point_ys");
                getFloat(item,"item_ws","point_ws");
                getFloat(item,"item_serchtime","arrayserchtime");
                getFloat(item,"item_scope","arrayscope");
                getFloat(item,"item_gametime","arraygametime");
                arrayPlanLists.put(key,configArray);
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
        configArray.put(key,floats);
    }

    //xml写入
    public synchronized void writeXML() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.newDocument();
            Element planLists = document.createElement("planLists");
            Constant.debugLog("arrayPlanLists"+arrayPlanLists.toString());
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
                Vector<Float> point_ws = planList.get("point_ws");
                Vector<Float> arrayserchtime = planList.get("arrayserchtime");
                Vector<Float> arrayscope = planList.get("arrayscope");
                Vector<Float> arraygametime = planList.get("arraygametime");
                Element point_xs_ = document.createElement("point_xs");
                Element point_ys_ = document.createElement("point_ys");
                Element point_ws_ = document.createElement("point_ws");
                Element arrayserchtime_ = document.createElement("arrayserchtime");
                Element arrayscope_ = document.createElement("arrayscope");
                Element arraygametime_ = document.createElement("arraygametime");

                for(int x = 0;x < point_xs.size();x ++){
                    Element item_xs = document.createElement("item_xs");
                    item_xs.setTextContent(point_xs.elementAt(x)+"");
                    Element item_ys = document.createElement("item_ys");
                    item_ys.setTextContent(point_ys.elementAt(x)+"");
                    Element item_ws = document.createElement("item_ws");
                    item_ws.setTextContent(point_ws.elementAt(x)+"");
                    Element item_serchtime = document.createElement("item_serchtime");
                    item_serchtime.setTextContent(arrayserchtime.elementAt(x)+"");
                    Element item_scope = document.createElement("item_scope");
                    item_scope.setTextContent(arrayscope.elementAt(x)+"");
                    Element item_gametime = document.createElement("item_gametime");
                    item_gametime.setTextContent(arraygametime.elementAt(x)+"");
                    point_xs_.appendChild(item_xs);
                    point_ys_.appendChild(item_ys);
                    point_ws_.appendChild(item_ws);
                    arrayserchtime_.appendChild(item_serchtime);
                    arrayscope_.appendChild(item_scope);
                    arraygametime_.appendChild(item_gametime);
                }
                keysElement.appendChild(point_xs_);
                keysElement.appendChild(point_ys_);
                keysElement.appendChild(point_ws_);
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

}
