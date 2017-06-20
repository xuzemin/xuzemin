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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
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
import com.android.jdrd.robot.dialog.RobotDialog;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.service.ServerSocketUtil;
import com.android.jdrd.robot.service.SetStaticIPService;
import com.android.jdrd.robot.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketImpl;
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
    public static int CURRENT_AREA_id = 0;
    private final String [] from ={"image","text"};
    private final int [] to = {R.id.image,R.id.text};
    private GridViewAdapter gridViewAdapter;
    private boolean isShrink = false;
    private Button button1,button2,button3,button0;

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
        button1 = (Button) findViewById(R.id.btn1);
        button2 = (Button) findViewById(R.id.btn2);
        button3 = (Button) findViewById(R.id.btn3);
        button0 = (Button) findViewById(R.id.btn0);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button0.setOnClickListener(this);

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
                Intent intent = new Intent(MainActivity.this, RobotActivity.class);
                intent.putExtra("id", (Integer) Robotdata_list.get(position).get("id"));
                startActivity(intent);
            }
        });

        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.shrink).setOnClickListener(this);

        deskview = (GridView) findViewById(R.id.gview);
        //获取数据
        desk_adapter = new SimpleAdapter(this, Deskdata_list, R.layout.item, from, to);
        deskview.setAdapter(desk_adapter);
        deskview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getAreaData();
                Constant.debugLog("position"+CURRENT_AREA_id);
                Constant.debugLog("DeskIsEdit"+DeskIsEdit);
                Constant.debugLog(areaList.toString());
                if(areaList != null && areaList.size() > 0 && CURRENT_AREA_id !=0){
                    if(DeskIsEdit ){
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
                        Constant.debugLog("position"+CURRENT_AREA_id);
                        robotDialog("*s#");
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"请添加并选择区域",Toast.LENGTH_SHORT).show();
                }
            }
        });

        area_adapter = new SimpleAdapter(this, Areadata_list, R.layout.item, from, to);
        area.setAdapter(area_adapter);
        area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getAreaData();
                Constant.debugLog("position"+CURRENT_AREA_id);
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
                        startAnimationLeft();
                        DeskIsEdit = false;
                        CURRENT_AREA_id = (int) Areadata_list.get(position).get("id");
                        getDeskData();
                    }
                    getAreaData();
                }
            }
        });
        robotDBHelper.execSQL("update robot set outline= '0' ");
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
    protected void onPause() {
        super.onPause();
        if( receiver !=null) {
            this.unregisterReceiver(receiver);
        }
        robotDBHelper.execSQL("update robot set outline= '0' ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAreaData();
        if(CURRENT_AREA_id == 0){
            if(areaList !=null && areaList.size() >0){
                CURRENT_AREA_id = (int) areaList.get(0).get("id");
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
        switch (v.getId()){
            case R.id.imgViewmapnRight:
                startAnimationLeft();
                break;
            case R.id.config_redact:
                if(DeskIsEdit){
                    DeskIsEdit = false;
                }else{
                    DeskIsEdit = true;
                }
                getDeskData();
                break;
            case R.id.up:
                robotDialog("*u#");
                break;
            case R.id.down:
                robotDialog("*d#");
                break;
            case R.id.left:
                robotDialog("*l#");
                break;
            case R.id.right:
                robotDialog("*r#");
                break;
            case R.id.stop:
                robotDialog("*s#");
                break;
            case R.id.shrink:
                Constant.debugLog("startAnimationShrink"+isShrink);
                startAnimationShrink();
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
            robotList = robotDBHelper.queryListMap("select * from robot" ,null);
            Constant.debugLog("robotList"+robotList.toString());
            List<Map> Robotdata_listcache =  new ArrayList<>();
            int j;
            Constant.debugLog("robotList.size()" + robotList.toString());
            for(int i =0 ,size = robotList.size();i < size ; i++){
                Constant.debugLog("size" + size +" ip"+robotList.get(i).get("ip").toString());
                String ip = robotList.get(i).get("ip").toString();
                j = 0;
                int h = ServerSocketUtil.socketlist.size();
                while( j < h){
                    if(ip.equals(ServerSocketUtil.socketlist.get(j).get("ip"))){
                        robotDBHelper.execSQL("update robot set outline= '1' where ip = '"+robotList.get(i).get("ip")+"'");
                        robotList.get(i).put("outline",1);
                        Robotdata_listcache.add(robotList.get(i));
                        robotList.remove(i);
                    }
                    h = ServerSocketUtil.socketlist.size();
                    j++;
                }
                size = robotList.size();
            }
            Constant.debugLog("robotList"+Robotdata_listcache.toString());
            Constant.debugLog("robotList"+robotList.toString());
            Robotdata_list.addAll(Robotdata_listcache);
            Robotdata_list.addAll(robotList);
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

    private void startAnimationLeft(){
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


    private void startAnimationShrink(){
        if (isShrink){
            Animation translate= AnimationUtils.loadAnimation(this,R.animator.translate_in);
            translate.setFillAfter(true);
            translate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    isShrink = false;
                    button0.setVisibility(View.GONE);
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            button0.startAnimation(translate);
        }else {
            button0.setVisibility(View.VISIBLE);
            Animation translate= AnimationUtils.loadAnimation(this,R.animator.translate_out);
            translate.setFillAfter(true);
            translate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isShrink = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            button0.startAnimation(translate);
        }
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
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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
                if(areaList !=null && areaList.size() >0){
                    CURRENT_AREA_id = (int) areaList.get(0).get("id");
                }
                getDeskData();
                dialog.dismiss();
            }
        });
        ((Button)dialog.getNegative()).setText(R.string.btn_delete);
        dialog.show();
    }

    private RobotDialog robotDialog ;
    private void robotDialog(String str) {
        robotDialog = new RobotDialog(this,str);
        robotDialog.show();
    }



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
        }
    }
}
