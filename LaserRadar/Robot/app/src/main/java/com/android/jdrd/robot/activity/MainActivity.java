package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.android.jdrd.robot.R;
import com.android.jdrd.robot.adapter.GridViewAdapter;
import com.android.jdrd.robot.dialog.MyDialog;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.service.ServerSocketUtil;
import com.android.jdrd.robot.service.SetStaticIPService;
import com.android.jdrd.robot.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity implements View.OnClickListener, Animation.AnimationListener {

    private MyReceiver receiver;
    private RobotDBHelper robotDBHelper;
    private static List<Map> areaList = new ArrayList<>(),deskList = new ArrayList<>(),robotList = new ArrayList<>();
    private LinearLayout linearlayout_all;
    private RelativeLayout map_right_Ralative;
    private ImageView imgViewmapnRight;
    private GridView deskview,robotgirdview;
    private List<Map<String, Object>> Areadata_list =  new ArrayList<>();
    private List<Map<String, Object>> Deskdata_list =  new ArrayList<>();
    private static List<Map> Robotdata_list =  new ArrayList<>();
    private SimpleAdapter desk_adapter,area_adapter;
    private static boolean DeskIsEdit = false,AreaIsEdit = false;
    private boolean IsRight = false;
    private ListView area;
    private static int CURRENT_AREA_id = 0;
    private final String [] from ={"image","text"};
    private final int [] to = {R.id.image,R.id.text};
    private GridViewAdapter gridViewAdapter;

//    private Button buttonCamera, buttonDelete, buttonWith, buttonPlace, buttonMusic, buttonThought, buttonSleep;
//    private Animation animationTranslate, animationRotate, animationScale;
//    private static int width, height;
//    private LayoutParams params = new LayoutParams(0, 0);
//    private static Boolean isClick = false;
//    private TextView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Intent SetStaticIPService = new Intent(this, SetStaticIPService.class);
        startService(SetStaticIPService);

        //启动后台通讯服务
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        startService(serverSocket);

        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        linearlayout_all = (LinearLayout) findViewById(R.id.linearlayout_all);
        imgViewmapnRight = (ImageView) findViewById(R.id.imgViewmapnRight);
        map_right_Ralative = (RelativeLayout) findViewById(R.id.map_right_Ralative);
        area = (ListView) findViewById(R.id.area);
        imgViewmapnRight.setOnClickListener(this);
        findViewById(R.id.config_redact).setOnClickListener(this);
        robotgirdview  = (GridView) findViewById(R.id.robotgirdview);

        robotgirdview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DeskConfigPathAcitivty.class);
                intent.putExtra("id", (Integer) Robotdata_list.get(position).get("id"));
                startActivity(intent);
            }
        });

        deskview = (GridView) findViewById(R.id.gview);
        //获取数据
        desk_adapter = new SimpleAdapter(this, Deskdata_list, R.layout.item, from, to);
        deskview.setAdapter(desk_adapter);
        deskview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.debugLog("position"+position);
                Constant.debugLog("DeskIsEdit"+DeskIsEdit);
                Constant.debugLog(areaList.toString());
                if(areaList != null && areaList.size() > 0){
                    if(DeskIsEdit){
                        if(position == 0){
                            Intent intent = new Intent(MainActivity.this, DeskConfigPathAcitivty.class);
                            intent.putExtra("area", CURRENT_AREA_id);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(MainActivity.this, DeskConfigPathAcitivty.class);
                            intent.putExtra("area", CURRENT_AREA_id);
                            intent.putExtra("id", (Integer) Deskdata_list.get(position).get("id"));
                            startActivity(intent);
                        }
                        getDeskData();
                    }else {
                        Toast.makeText(getApplicationContext(), Deskdata_list.get(position).get("text").toString(), Toast.LENGTH_SHORT).show();
                        getDeskData();
                    }
                }else{Toast.makeText(getApplicationContext(),"请添加并选择区域",Toast.LENGTH_SHORT).show();
                }
            }
        });

        area_adapter = new SimpleAdapter(this, Areadata_list, R.layout.item, from, to);
        area.setAdapter(area_adapter);
        area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.debugLog("position"+position);
                Constant.debugLog("AreaIsEdit"+AreaIsEdit);
                if(AreaIsEdit){
                    if(position == 0){
                        AreaIsEdit = false;
                    }else if(position == 1){
                        dialog();
                    }else{
                        dialog(Areadata_list.get(position).get("text").toString(),(int)Areadata_list.get(position).get("id"));
                    }
                    getAreaData();
                }else{
                    if(position == 0){
                        AreaIsEdit = true;
                    }else{
                        startAnimationRight();
                        DeskIsEdit = false;
                        CURRENT_AREA_id = (int) Areadata_list.get(position).get("id");
                        getDeskData();
                    }
                    getAreaData();
                }
            }
        });
//        initialButton();
    }

    private void setGridView() {
        int size = Robotdata_list.size();
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        robotgirdview.setLayoutParams(params); // 重点
        robotgirdview.setColumnWidth(itemWidth); // 重点
        robotgirdview.setHorizontalSpacing(5); // 间距
        robotgirdview.setStretchMode(GridView.NO_STRETCH);
        robotgirdview.setNumColumns(size); // 重点
        gridViewAdapter = new GridViewAdapter(getApplicationContext(),
                Robotdata_list);
        robotgirdview.setAdapter(gridViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jdrd.activity.Main");
        if(receiver != null ){
            this.registerReceiver(receiver,filter);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if( receiver !=null) {
            unregisterReceiver(receiver);
        }
        robotDBHelper.execSQL("update robot set outline= '0' ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAreaData();
        if(CURRENT_AREA_id == 0){
            if(areaList !=null && areaList.size() >0){
                CURRENT_AREA_id = (int) Areadata_list.get(1).get("id");
            }
        }
        getDeskData();
//        robotDBHelper.execSQL("delete from robot where id = '5'");
//        robotDBHelper.execSQL("insert into  robot (name,ip,state,outline) values ('新建机器人','192.168.1.120',1,1)");
        getRobotData();
        gridViewAdapter.notifyDataSetInvalidated();
//        gridViewAdapter = new GridViewAdapter(getApplicationContext(),
//                Robotdata_list);
//        robotgirdview.setAdapter(gridViewAdapter);
    }

    @Override
    public void onClick(View v) {
        List<Map> list;
        switch (v.getId()){
            case R.id.imgViewmapnRight:
                startAnimationRight();
                break;
            case R.id.config_redact:
                if(DeskIsEdit){
                    DeskIsEdit = false;
                }else{
                    DeskIsEdit = true;
                }
                getDeskData();
                break;
        }
    }

    public List<Map<String, Object>> getDeskData(){
        Deskdata_list.clear();
        try {
            deskList = robotDBHelper.queryListMap("select * from desk where area = '"+CURRENT_AREA_id+"'" ,null);
            Log.e("Robot",deskList.toString());
            Log.e("Robot","CURRENT_AREA_id"+CURRENT_AREA_id);
        }catch (Exception e){
            e.printStackTrace();
        }
        Map<String, Object> map;
//        map = new HashMap<>();
//        map.put("image", R.mipmap.zuo_xs);
//        map.put("id", 0);
//        map.put("text",getString(R.string.config_redact));
//        Deskdata_list.add(map);
        if(DeskIsEdit){
            map = new HashMap<>();
            map.put("image", R.mipmap.zuo_xs);
            map.put("id", 0);
            map.put("text",getString(R.string.config_add));
            Deskdata_list.add(map);
        }
        if(deskList !=null && deskList.size() >0){
            for(int i=0 ,size = deskList.size();i<size;i++){
                map = new HashMap<>();
                if(DeskIsEdit){
                    map.put("image", R.mipmap.ic_launcher);
                }else{
                    map.put("image", R.mipmap.bg);
                }
                map.put("id", deskList.get(i).get("id"));
                map.put("text", deskList.get(i).get("name"));
                map.put("area", deskList.get(i).get("area"));
                Deskdata_list.add(map);
            }
        }
        desk_adapter.notifyDataSetChanged();
        return Deskdata_list;
    }

    public List<Map> getRobotData(){
        Robotdata_list.clear();
        try {
            robotList = robotDBHelper.queryListMap("select * from robot where outline = 1" ,null);
            Log.e("Robot",robotList.toString());
            for(int i =0 ,size = robotList.size();i< size ; i++){
                Robotdata_list.add(robotList.get(i));
                Constant.debugLog(Robotdata_list.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            robotList.clear();
            robotList = robotDBHelper.queryListMap("select * from robot where outline = 0 " ,null);
            Log.e("Robot",robotList.toString());
            for(int i =0 ,size = robotList.size();i< size ; i++){
                Robotdata_list.add(robotList.get(i));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setGridView();
        return Robotdata_list;
    }

    public List<Map<String, Object>> getAreaData(){
        Areadata_list.clear();
        try {
            areaList = robotDBHelper.queryListMap("select * from area",null);
            Constant.debugLog(areaList.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        Map<String, Object> map ;
        map = new HashMap<>();
        map.put("image", R.mipmap.zuo_xs);
        map.put("text",getString(R.string.config_redact));
        Areadata_list.add(map);
        if(AreaIsEdit){
            map = new HashMap<>();
            map.put("image", R.mipmap.zuo_xs);
            map.put("text",getString(R.string.config_add));
            Areadata_list.add(map);
        }
        if(areaList !=null && areaList.size() >0){
            for(int i=0 ,size = areaList.size();i<size;i++){
                map = new HashMap<>();
                if(AreaIsEdit){
                    map.put("image", R.mipmap.ic_launcher);
                }else{
                    map.put("image", R.mipmap.bg);
                }
                map.put("id", areaList.get(i).get("id"));
                map.put("text", areaList.get(i).get("name"));
                Areadata_list.add(map);
            }
        }
        area_adapter.notifyDataSetChanged();
        return Areadata_list;
    }

    //右边动画
    private void startAnimationRight(){
        if (IsRight){
            linearlayout_all.setVisibility(View.VISIBLE);
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,-linearlayout_all.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0F
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(MainActivity.this);
            map_right_Ralative.startAnimation(translateAnimation);
        }else {
            TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,-linearlayout_all.getWidth(),
                    Animation.ABSOLUTE,0.0f,
                    Animation.ABSOLUTE,0.0f
            );
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(false);
            translateAnimation.setAnimationListener(MainActivity.this);
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
            imgViewmapnRight.setImageResource(R.mipmap.zuo_yc);
        }else {
            IsRight = true;
            linearlayout_all.setVisibility(View.GONE);
            imgViewmapnRight.setImageResource(R.mipmap.zuo_xs);
        }
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    private MyDialog dialog ;
    private EditText editText;
    private TextView title;
    private void dialog() {
        dialog = new MyDialog(this);
        editText = (EditText) dialog.getEditText();
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"区域名称不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    robotDBHelper.insert("area",new String[]{"name"},new Object[]{editText.getText().toString()});
                    getAreaData();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
    private void dialog(String name, final int id) {
        dialog = new MyDialog(this);
        editText = (EditText) dialog.getEditText();
        editText.setText(name);
        title = (TextView) dialog.getTextView();
        title.setText(R.string.change_area);
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"区域名称不能为空",Toast.LENGTH_SHORT).show();
                }else{
                    robotDBHelper.execSQL("update area set name= '"+editText.getText().toString().trim()+"' where id= '"+ id +"'");
                    dialog.dismiss();
                }
            }
        });
        //delete from person where name = 'wuyudong'
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                robotDBHelper.execSQL("delete from area where id= '"+ id +"'");
                List<Map> desklist;
                desklist = robotDBHelper.queryListMap("select * from desk where area = '"+id+"'" ,null);
                for(int i = 0 ,size = desklist.size();i < size ;i++){
                    robotDBHelper.execSQL("delete from command where desk= '"+ desklist.get(i).get("id") +"'");
                }
                robotDBHelper.execSQL("delete from desk where area= '"+ id +"'");
                getAreaData();
                getDeskData();
                dialog.dismiss();
            }
        });
        ((Button)dialog.getNegative()).setText(R.string.btn_delete);
        dialog.show();
    }



//    private void initialButton()
//    {
//        // TODO Auto-generated method stub
//        Display display = getWindowManager().getDefaultDisplay();
//        height = display.getHeight();
//        width = display.getWidth();
//        Log.v("width  & height is:", String.valueOf(width) + ", " + String.valueOf(height));
//
//        params.height = 50;
//        params.width = 50;
//        //设置边距  (int left, int top, int right, int bottom)
//        params.setMargins(10, height - 98, 0, 0);
//
//        buttonSleep = (Button) findViewById(R.id.button_composer_sleep);
//        buttonSleep.setLayoutParams(params);
//
//        buttonThought = (Button) findViewById(R.id.button_composer_thought);
//        buttonThought.setLayoutParams(params);
//
//        buttonMusic = (Button) findViewById(R.id.button_composer_music);
//        buttonMusic.setLayoutParams(params);
//
//        buttonPlace = (Button) findViewById(R.id.button_composer_place);
//        buttonPlace.setLayoutParams(params);
//
//        buttonWith = (Button) findViewById(R.id.button_composer_with);
//        buttonWith.setLayoutParams(params);
//
//        buttonCamera = (Button) findViewById(R.id.button_composer_camera);
//        buttonCamera.setLayoutParams(params);
//
//        buttonDelete = (Button) findViewById(R.id.button_friends_delete);
//        buttonDelete.setLayoutParams(params);
//
//        buttonDelete.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                if(isClick == false)
//                {
//                    isClick = true;
//                    buttonDelete.startAnimation(animRotate(-45.0f, 0.5f, 0.45f));
//                    buttonCamera.startAnimation(animTranslate(0.0f, -180.0f, 10, height - 240, buttonCamera, 80));
//                    buttonWith.startAnimation(animTranslate(30.0f, -150.0f, 60, height - 230, buttonWith, 100));
//                    buttonPlace.startAnimation(animTranslate(70.0f, -120.0f, 110, height - 210, buttonPlace, 120));
//                    buttonMusic.startAnimation(animTranslate(80.0f, -110.0f, 150, height - 180, buttonMusic, 140));
//                    buttonThought.startAnimation(animTranslate(90.0f, -60.0f, 175, height - 135, buttonThought, 160));
//                    buttonSleep.startAnimation(animTranslate(170.0f, -30.0f, 190, height - 90, buttonSleep, 180));
//
//                }
//                else
//                {
//                    isClick = false;
//                    buttonDelete.startAnimation(animRotate(90.0f, 0.5f, 0.45f));
//                    buttonCamera.startAnimation(animTranslate(0.0f, 140.0f, 10, height - 98, buttonCamera, 180));
//                    buttonWith.startAnimation(animTranslate(-50.0f, 130.0f, 10, height - 98, buttonWith, 160));
//                    buttonPlace.startAnimation(animTranslate(-100.0f, 110.0f, 10, height - 98, buttonPlace, 140));
//                    buttonMusic.startAnimation(animTranslate(-140.0f, 80.0f, 10, height - 98, buttonMusic, 120));
//                    buttonThought.startAnimation(animTranslate(-160.0f, 40.0f, 10, height - 98, buttonThought, 80));
//                    buttonSleep.startAnimation(animTranslate(-170.0f, 0.0f, 10, height - 98, buttonSleep, 50));
//
//                }
//
//            }
//        });
//        buttonCamera.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                buttonCamera.startAnimation(setAnimScale(2.5f, 2.5f));
//                buttonWith.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonPlace.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonMusic.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonThought.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonSleep.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
//            }
//        });
//        buttonWith.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                buttonWith.startAnimation(setAnimScale(2.5f, 2.5f));
//                buttonCamera.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonPlace.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonMusic.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonThought.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonSleep.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
//            }
//        });
//        buttonPlace.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                buttonPlace.startAnimation(setAnimScale(2.5f, 2.5f));
//                buttonWith.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonCamera.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonMusic.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonThought.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonSleep.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
//            }
//        });
//        buttonMusic.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                buttonMusic.startAnimation(setAnimScale(2.5f, 2.5f));
//                buttonPlace.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonWith.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonCamera.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonThought.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonSleep.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
//            }
//        });
//        buttonThought.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                buttonThought.startAnimation(setAnimScale(2.5f, 2.5f));
//                buttonPlace.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonWith.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonCamera.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonMusic.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonSleep.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
//            }
//        });
//        buttonSleep.setOnClickListener(new OnClickListener()
//        {
//
//            @Override
//            public void onClick(View v)
//            {
//                // TODO Auto-generated method stub
//                buttonSleep.startAnimation(setAnimScale(2.5f, 2.5f));
//                buttonPlace.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonWith.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonCamera.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonMusic.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonThought.startAnimation(setAnimScale(0.0f, 0.0f));
//                buttonDelete.startAnimation(setAnimScale(0.0f, 0.0f));
//            }
//        });
//
//    }
//
//    protected Animation setAnimScale(float toX, float toY)
//    {
//        // TODO Auto-generated method stub
//        animationScale = new ScaleAnimation(1f, toX, 1f, toY, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.45f);
//        animationScale.setInterpolator(MainActivity.this, R.anim.fade);
//        animationScale.setDuration(500);
//        animationScale.setFillAfter(false);
//        return animationScale;
//
//    }
//
//    protected Animation animRotate(float toDegrees, float pivotXValue, float pivotYValue)
//    {
//        // TODO Auto-generated method stub
//        animationRotate = new RotateAnimation(0, toDegrees, Animation.RELATIVE_TO_SELF, pivotXValue, Animation.RELATIVE_TO_SELF, pivotYValue);
//        animationRotate.setAnimationListener(new Animation.AnimationListener()
//        {
//            @Override
//            public void onAnimationStart(Animation animation)
//            {
//                // TODO Auto-generated method stub
//
//            }
//            @Override
//            public void onAnimationRepeat(Animation animation)
//            {
//                // TODO Auto-generated method stub
//
//            }
//            @Override
//            public void onAnimationEnd(Animation animation)
//            {
//                // TODO Auto-generated method stub
//                animationRotate.setFillAfter(true);
//            }
//        });
//        return animationRotate;
//    }
//    //移动的动画效果
//	/*
//	 * TranslateAnimation(float fromXDelta, float toXDelta, float fromYDelta, float toYDelta)
//	 *
//	 * float fromXDelta:这个参数表示动画开始的点离当前View X坐标上的差值；
//     *
//　　       * float toXDelta, 这个参数表示动画结束的点离当前View X坐标上的差值；
//     *
//　　       * float fromYDelta, 这个参数表示动画开始的点离当前View Y坐标上的差值；
//     *
//　　       * float toYDelta)这个参数表示动画开始的点离当前View Y坐标上的差值；
//	 */
//    protected Animation animTranslate(float toX, float toY, final int lastX, final int lastY,
//                                      final Button button, long durationMillis)
//    {
//        // TODO Auto-generated method stub
//        animationTranslate = new TranslateAnimation(0, toX, 0, toY);
//        animationTranslate.setAnimationListener(new Animation.AnimationListener()
//        {
//            @Override
//            public void onAnimationStart(Animation animation)
//            {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation)
//            {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation)
//            {
//                // TODO Auto-generated method stub
//                params = new LayoutParams(0, 0);
//                params.height = 50;
//                params.width = 50;
//                params.setMargins(lastX, lastY, 0, 0);
//                button.setLayoutParams(params);
//                button.clearAnimation();
//            }
//        });
//        animationTranslate.setDuration(durationMillis);
//        return animationTranslate;
//    }


    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String StringE = intent.getStringExtra("msg");
            Constant.debugLog("msg"+StringE);
            if(StringE !=null && !StringE.equals("")){
                pasreJson(StringE);
            }
        }
    }
    public void pasreJson(String string){
        if(string.equals("robot_connect") || string.equals("robot_unconnect")){
            getRobotData();
        }else if(string.equals("robot_destory")){
            robotDBHelper.execSQL("update robot set outline= '0' ");
        }else {
            JSONObject object;
            try {
                object = new JSONObject(string);
                String type = object.getString(Constant.Type);
                String funtion = object.getString(Constant.Function);
                String data = object.getString(Constant.Data);
                Constant.debugLog("message" + object.toString());
                if (object != null) {
                    if (type.equals(Constant.State)) {
                        if (funtion.equals(Constant.Navigation)) {
                        } else if (funtion.equals(Constant.Camera)) {
                        } else if (funtion.equals(Constant.Peoplesearch)) {
                        } else if (funtion.equals(Constant.Obstacle)) {
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
