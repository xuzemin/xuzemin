package com.android.zbrobot.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zbrobot.R;
import com.android.zbrobot.adapter.ZB_AreaAdapter;
import com.android.zbrobot.adapter.ZB_DeskAdapter;
import com.android.zbrobot.adapter.ZB_GridViewAdapter;
import com.android.zbrobot.dialog.ZB_DeleteDialog;
import com.android.zbrobot.dialog.ZB_MyDialog;
import com.android.zbrobot.dialog.ZB_RobotDialog;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.service.Protocol;
import com.android.zbrobot.service.ServerSocketUtil;
import com.android.zbrobot.service.SetStaticIPService;
import com.android.zbrobot.util.Constant;
import com.android.zbrobot.util.PreferencesUtils;
import com.android.zbrobot.util.RobotUtils;
import com.ls.lsros.callback.CallBack;
import com.ls.lsros.data.bean.Position;
import com.ls.lsros.data.provide.bean.MapOperationResult;
import com.ls.lsros.data.provide.bean.NavigationResult;
import com.ls.lsros.helper.ROSConnectHelper;
import com.ls.lsros.helper.RobotInfoHelper;
import com.ls.lsros.helper.RobotMapOperationHelper;
import com.ls.lsros.helper.RobotNavigationHelper;
import com.ls.lsros.helper.RosSDKInitHelper;
import com.ls.lsros.util.CoordinatesUtils;
import com.ls.lsros.websocket.ROSClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.android.zbrobot.util.RobotUtils.fileName;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/7/27
 * 描述: 主页
 */
public class ZB_MainActivity extends Activity implements View.OnClickListener, Animation.AnimationListener {
    // 初始化广播
    private MyReceiver receiver;
    // 初始化数据库帮助类
    private static RobotDBHelper robotDBHelper;

    // 存储数据  以Map键值对的形式存储
    private static List<Map> areaList = new ArrayList<>();// 区域
    private static List<Map> deskList = new ArrayList<>();// 桌面
    private static List<Map> robotList = new ArrayList<>();// 机器人
    private static List<Map> commandList = new ArrayList<>();// 命令
    private static List<Map> robotData_List = new ArrayList<>();// 机器人数据

    // 区域数据列
    private List<Map<String, Object>> areaData_list = new ArrayList<>();
    // 桌面数据列
    private List<Map<String, Object>> deskData_list = new ArrayList<>();

    // 左侧平移出的linearLayout_all
    private LinearLayout linearLayout_all;
    // 机器人LinearLayout
    private LinearLayout linear_robot;
    // 桌面LinearLayout
    private LinearLayout linear_desk;

    private RelativeLayout map_right_Relative;

    // 导航栏左侧按钮
    private ImageView imgViewMapRight;

    // 导航栏右侧注销按钮
    private ImageView img_cancel;
    // 桌子横向展示
    private GridView deskView;
    // 机器人状态横向展示
    private GridView robotGirdView;
    // 区域列表
    private ListView area;
    private boolean isRunning = false;
    // 区域名称
    private TextView area_text;
    // 上 下 左 右 停止 收缩
    private Button up, down, left, right, stop, shrink;

    // 编辑桌子
    private Button config_redact;
    private int moredesk = 0;
    // 当前的下标
    public static int Current_INDEX = 1;
    // 当前桌面id
    public static int CURRENT_AREA_id = 0;
    //初始化平移动画
    private TranslateAnimation translateAnimation;

    public static boolean isGetMap = false;

    // 桌面适配器
    private ZB_DeskAdapter desk_adapter;
    // 区域适配器
    private ZB_AreaAdapter area_adapter;
    // 机器人状态适配器
    private ZB_GridViewAdapter SJXGridViewAdapter;

    // 桌子是否编辑
    public static boolean DeskIsEdit = false;
    // 区域是否编辑
    public static boolean AreaIsEdit = false;

    // 是否向右展开
    private boolean IsRight = true;
    // 是否销毁
    private boolean IsFinish = true;
    // 是否收缩
    private boolean isShrink = false;
    //密度
    private float density;
    // Login
    public static String name = null;

    // 底部确定取消
    private RelativeLayout lay;
    private ArrayList<Boolean> selectItems; //用于存储已选中项目的位置
    private boolean isState;
    private List isList = new ArrayList();
    private boolean ischeck = false;
    @SuppressLint("HandlerLeak")
    public final Handler mhandle = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constant.MSG_REFRESH_CONNECT:
                    Constant.debugLog("RobotUtils.getInstance().STEP"+RobotUtils.STEP);
                    switch (RobotUtils.STEP){
                        case 0:
                        case 3:
                            robotDBHelper.execSQL("update robot set outline = '1' where ip = '192.168.106.1'");
                            if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                                Toast.makeText(getApplicationContext(), "暂无外部存储", Toast.LENGTH_SHORT).show();
                            }else {
                                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ZB_MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }else {
                                    RobotUtils.getInstance().Connect();
                                    getRobotData();
                                    // 刷新
                                    SJXGridViewAdapter.notifyDataSetInvalidated();
                                }
                            }
                            break;
                        case 1:
                            RobotUtils.getInstance().importMap();
                            // 刷新
                            SJXGridViewAdapter.notifyDataSetInvalidated();
                            break;
                        case 2:
                            RobotUtils.getInstance().setImageInfo();
//                            File file = new File(fileName);
//                            try {
//                                MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), "map.JPEG", null);
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            }
                            break;
                        case 4:
                            robotDBHelper.execSQL("update area set pointx = '"+RobotUtils.originX+"',pointy = '"+RobotUtils.originY
                                    +"' where id = '"+CURRENT_AREA_id+"'");
                            robotDBHelper.execSQL("update robot set outline = '1' where ip = '192.168.106.1'");
                            if(robotDBHelper!=null){
                                List<Map> List = robotDBHelper.queryListMap("select * from area where id = '"+CURRENT_AREA_id+"'", null);
                                if(List != null && List.size() >0){
                                    RobotUtils.getInstance().setInitPose(0
                                            ,0,0);
                                }
                            }
                            break;
                        case 5:
                            RobotUtils.getInstance().startGetstat();

                            RobotNavigationHelper.getInstance().sendGoal(0,0,0);
                            RobotNavigationHelper.getInstance().startNav(new CallBack<NavigationResult>() {
                                @Override
                                public void call(NavigationResult data) {
                                    Constant.debugLog("开始导航-->" + data.getCode() +
                                            "isSuccess" + data.isSuccess());
                                }
                            });
                            break;
                        case 6:
                            robotDBHelper.execSQL("update robot set outline = '1' where ip = '192.168.106.1'");
                            getRobotData();
                            Constant.debugLog("aaaa");
//                            robotDBHelper.execSQL("update robot set outline = '1' where ip = '192.168.106.1'");
                            if(RobotUtils.robotStatus !=null) {
                                Constant.debugLog("aaaa"+RobotUtils.robotStatus.getStatus());
                                switch (RobotUtils.robotStatus.getStatus()) {
                                    case 0:
                                        break;
                                    case 1:
                                        isRunning = true;
                                        break;
                                    case 2:
                                        break;
                                    case 3:
                                        if (isRunning) {
                                            isRunning = false;
                                            ServerSocketUtil.LsCurrent++;
                                            ServerSocketUtil.sendLSList(ZB_RobotDialog.idList);
                                        }
                                        break;
                                    case 9:
                                        ServerSocketUtil.sendLSList(ZB_RobotDialog.idList);
                                        break;
                                    case 4:
                                        break;
                                }
                            }
                            break;
                    }
                    mhandle.sendEmptyMessageDelayed(Constant.MSG_REFRESH_CONNECT,1000);
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.zb_activity_main);

//        // 静态IP
//        Intent SetStaticIPService = new Intent(this, SetStaticIPService.class);
//        startService(SetStaticIPService);
        // 启动后台通讯服务
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        startService(serverSocket);

        RobotUtils.STEP = 0;
        // 初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        //初始化控件
        // 左侧平移出来的LinearLayout
        linearLayout_all =  findViewById(R.id.linearlayout_all);
        // TODO
        lay =  findViewById(R.id.lay);

        // 导航栏左侧ImageView菜单
        imgViewMapRight =  findViewById(R.id.imgViewmapnRight);
        imgViewMapRight.setOnClickListener(this);
        // 导航栏右侧ImageView注销
        img_cancel =  findViewById(R.id.img_cancel);
        img_cancel.setOnClickListener(this);

        // 左侧平移出来的RelativeLayout
        map_right_Relative =  findViewById(R.id.map_right_Ralative);

        // 区域列表
        area =  findViewById(R.id.area);
        // 区域右侧编辑桌子按钮
        config_redact =  findViewById(R.id.config_redact);
        config_redact.setOnClickListener(this);

        // 初始化区域名称
        area_text = (TextView) findViewById(R.id.area_text);

        // 机器人整体LinearLayout
        linear_robot = (LinearLayout) findViewById(R.id.linear_robot);
        linear_robot.setOnClickListener(this);

        // 桌面整体LinearLayout
        linear_desk = (LinearLayout) findViewById(R.id.linear_desk);
        linear_desk.setOnClickListener(this);

        // ActionBar
        findViewById(R.id.main).setOnClickListener(this);

        // 系统卡编辑
        findViewById(R.id.card).setOnClickListener(this);
        findViewById(R.id.config_redact_re).setOnClickListener(this);
        findViewById(R.id.img_cancel_re).setOnClickListener(this);
        // 头RelativeLayout
        findViewById(R.id.activity_main).setOnClickListener(this);

        // 获取传递过来的数据
        name = getIntent().getStringExtra("name");
        if(name !=null) {
            if (name.equals(Constant.ADMIN)) {

            } else if (name.equals(Constant.SINGLE)) {
                imgViewMapRight.setVisibility(View.GONE);
                config_redact.setVisibility(View.GONE);
                DeskIsEdit = false;
            } else if (name.equals(Constant.MANY)) {
                config_redact.setVisibility(View.GONE);
                DeskIsEdit = false;
            }
        }

        // 初始化机器人列表
        robotGirdView = (GridView) findViewById(R.id.robotgirdview);
        robotGirdView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 关闭左侧区域
                if (!IsRight) {
                    startAnimationLeft();
                } else {
                    // 跳转到RobotActivity 并传递数据
                    Intent intent = new Intent(ZB_MainActivity.this, ZB_RobotActivity.class);
                    intent.putExtra("id", (Integer) robotData_List.get(position).get("id"));
                    intent.putExtra("us_name", name);
                    startActivity(intent);
                }
            }
        });

        // 初始化上按钮
        up = (Button) findViewById(R.id.up);
        up.setOnClickListener(this);
        // 初始化下按钮
        down = (Button) findViewById(R.id.down);
        down.setOnClickListener(this);
        // 初始化左按钮
        left = (Button) findViewById(R.id.left);
        left.setOnClickListener(this);
        // 初始化右按钮
        right = (Button) findViewById(R.id.right);
        right.setOnClickListener(this);
        // 初始化停止按钮
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);
        // 初始化收缩按钮
        shrink = (Button) findViewById(R.id.shrink);
        shrink.setOnClickListener(this);

        //获取数据
        desk_adapter = new ZB_DeskAdapter(this, deskData_list);
        selectItems = new ArrayList<>();
        // 初始化桌面列表
        deskView = (GridView) findViewById(R.id.gview);
        deskView.setAdapter(desk_adapter);
        // 桌面子列表点击事件
        deskView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 关闭左侧区域
                if (!IsRight) {
                    startAnimationLeft();
                } else {
                    // 获取区域数据
                    getAreaData();
                    if (areaList != null && areaList.size() > 0 && CURRENT_AREA_id != 0) {
                        // TODO 复选框
                        if (isState) {
                            CheckBox checkBox =  view.findViewById(R.id.ck_select);
                            if (checkBox.isChecked() && !ischeck) {
                                checkBox.setChecked(false);
                                selectItems.set(position, false);
                            } else {
                                checkBox.setChecked(true);
                                selectItems.set(position, true);
                            }
                            ischeck = false;
                            desk_adapter.notifyDataSetChanged();
                        } else {
                            if (DeskIsEdit) {
                                if (position == 0) {
                                    // 跳转到DeskConfigPathActivity 并传递area
                                    Intent intent = new Intent(ZB_MainActivity.this, ZB_DeskConfigPathActivity.class);
                                    intent.putExtra("area", CURRENT_AREA_id);
                                    startActivity(intent);
                                } else {
                                    // 跳转到DeskConfigPathActivity 并传递area
                                    Intent intent = new Intent(ZB_MainActivity.this, ZB_DeskConfigPathActivity.class);
                                    intent.putExtra("area", CURRENT_AREA_id);
                                    intent.putExtra("id", (Integer) deskData_list.get(position).get("id"));
                                    startActivity(intent);
                                }
                                // 获取桌面数据
                                getDeskData();

                            } else {
                                // 打印Log
                                Constant.debugLog("position----->" + CURRENT_AREA_id);
                                //commandList = robotDBHelper.queryListMap("select * from command where desk = '" + deskData_list.get(position).get("id") + "'", null);
                                //ZB_RobotDialog.deskid = (int) deskData_list.get(position).get("id");
                                ZB_RobotDialog.CurrentIndex = 0;
                                isList = new ArrayList();
                                isList.add(deskData_list.get(position).get("id"));
                                robotDialog(isList);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "请添加并选择区域", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        deskView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                List<Map> list = robotDBHelper.queryListMap("select * from area where id = '" + CURRENT_AREA_id + "'", null);
                moredesk = (int) list.get(0).get("moredesk");
                if(moredesk == 1) {
                    getDeskData();
                    if (!isState) {
                        selectItems = new ArrayList<>();
                        for (int i = 0; i < deskList.size(); i++) {
                            selectItems.add(false);
                        }
                        CheckBox box = (CheckBox) view.findViewById(R.id.ck_select);
                        box.setChecked(true);
                        selectItems.set(position, true);
                        setState(true);
                        desk_adapter.setIsState(true);
                        ischeck = true;
                        showOpervate();
                    }
                }
                return false;
            }
        });


        // 初始化区域适配器
        area_adapter = new ZB_AreaAdapter(this, areaData_list);
        area.setAdapter(area_adapter);
        // 区域子列表点击事件
        area.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取区域数据
                getAreaData();
                if (AreaIsEdit) {
                    if (position == 0) {
                        AreaIsEdit = false;
                    } else if (position == 1) {
                        Intent intent = new Intent(ZB_MainActivity.this, ZB_AreaRedact.class);
                        intent.putExtra("area", 0);
                        startActivity(intent);
                        //                        dialog();
                    } else {
                        Intent intent = new Intent(ZB_MainActivity.this, ZB_AreaRedact.class);
                        intent.putExtra("area", (int) areaData_list.get(position).get("id"));
                        startActivity(intent);
                        //                        dialog(areaData_list.get(position).get("name").toString(), (int) areaData_list.get(position).get("id"));
                    }
                    getAreaData();
                } else {
                    if (name.equals(Constant.ADMIN)) {
                        if (position == 0) {
                            AreaIsEdit = true;
                        } else {
                            if (!IsRight) {
                                startAnimationLeft();
                            }
                            DeskIsEdit = false;
                            CURRENT_AREA_id = (int) areaData_list.get(position).get("id");
                            Current_INDEX = position;
                            area_text.setText(areaData_list.get(position).get("name").toString());
                            // 获取桌面数据
                            getDeskData();
                        }
                    } else {
                        if (!IsRight) {
                            startAnimationLeft();
                        }
                        DeskIsEdit = false;
                        CURRENT_AREA_id = (int) areaData_list.get(position).get("id");
                        Current_INDEX = position;
                        area_text.setText(areaData_list.get(position).get("name").toString());
                    }
                    // 获取区域数据
                    getAreaData();
                }
            }
        });

        findViewById(R.id.setting_back).setOnClickListener(this);

        robotDBHelper.execSQL("update robot set outline = '0' ");
        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jdrd.activity.Main");
        // 注册广播
        if (receiver != null) {
            this.registerReceiver(receiver, filter);
        }
        mhandle.sendEmptyMessageDelayed(Constant.MSG_REFRESH_CONNECT,1000);

        List<Map> listrobot = robotDBHelper.queryListMap("select * from robot ", null);
        boolean ishaverobot = false;
        for(Map map : listrobot){
            if(map.get("ip").equals("192.168.106.1")){
                ishaverobot = true;
                break;
            }
        }
        if(!ishaverobot){
            robotDBHelper.execSQL("insert into robot (name,ip,state,outline,electric,robotstate,obstacle," +
                    "commandnum,excute,excutetime,commandstate,lastcommandstate,lastlocation,area,pathway,outtime" +
                    ",turnback,goal,up_obstacle,down_obstacle ,side_obstacle , loop_number) values " +
                    "('雷达','" + "192.168.106.1" + "',0,0,100,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0)");
        }
    }

    // 获取选中的Items
    public ArrayList<Boolean> getSelectItems() {
        return selectItems;
    }

    //设置当前状态 是否在多选模式
    private void setState(boolean b) {
        isState = b;
        if (b) {
            showOpervate();
        } else {
            dismissOperate();
        }
    }

    /**
     * 显示操作界面
     */
    private void showOpervate() {
        lay.setVisibility(View.VISIBLE);
        config_redact.setVisibility(View.GONE);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.operate_in);
        lay.setAnimation(anim);
        // 返回、删除、全选和反选按钮初始化及点击监听
        TextView tvBack = (TextView) findViewById(R.id.operate_back);
        TextView tvOk = (TextView) findViewById(R.id.operate_ok);

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isList = new ArrayList();
                for (int i = 0; i < deskList.size(); i++) {
                    if(selectItems != null && selectItems.size() >0 ) {
                        if (selectItems.get(i)) {
                            Constant.debugLog("" + deskList.toString());
                            isList.add(deskList.get(i).get("id"));
                        }
                    }
                }
                ZB_RobotDialog.CurrentIndex = 0;
                robotDialog(isList);
                selectItems = new ArrayList<>();
                for (int i = 0; i < deskList.size(); i++) {
                    selectItems.add(false);
                }
                getDeskData();
                if (isState) {
                    selectItems.clear();
                    desk_adapter.setIsState(false);
                    setState(false);
                }
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isState) {
                    selectItems.clear();
                    desk_adapter.setIsState(false);
                    setState(false);
                }
            }
        });

    }



    /**
     * 隐藏操作界面
     */
    private void dismissOperate() {
        Animation anim = AnimationUtils.loadAnimation(ZB_MainActivity.this, R.anim.operate_out);
        lay.setVisibility(View.GONE);
        config_redact.setVisibility(View.VISIBLE);
        lay.setAnimation(anim);
    }

    // 返回键取消多选模式
    @Override
    public void onBackPressed() {
        if (isState) {
            selectItems.clear();
            desk_adapter.setIsState(false);
            setState(false);

        } else {
            super.onBackPressed();
        }
    }

    // 设置机器人列表属性
    private void setGridView() {
        int size = robotData_List.size();
        int length = 76;
        int height = 106;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        int gridViewWidth = (int) (dm.widthPixels - 20 * density);
        int itemWidth = (int) (length * density);
        int itemHeight = (int) (height * density);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridViewWidth, itemHeight);
        Constant.linearWidth = (int) (76 * density);
        robotGirdView.setLayoutParams(params); // 重点
        robotGirdView.setColumnWidth(itemWidth); // 重点
        robotGirdView.setHorizontalSpacing((int) (8 * density)); // 间距
        robotGirdView.setStretchMode(GridView.NO_STRETCH);
        robotGirdView.setNumColumns(size); // 重点
        SJXGridViewAdapter = new ZB_GridViewAdapter(getApplicationContext(),
                robotData_List);
        robotGirdView.setAdapter(SJXGridViewAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消广播注册
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
        mhandle.removeMessages(Constant.MSG_REFRESH_CONNECT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAreaData();
        if (CURRENT_AREA_id == 0) {
            if (areaList != null && areaList.size() > 0) {
                CURRENT_AREA_id = (int) areaList.get(0).get("id");
                Current_INDEX = 1;
                area_text.setText(areaList.get(0).get("name").toString());
            } else {
                area_text.setText("请选择左侧区域");
            }
        } else {
            for (int i = 0, size = areaList.size(); i < size; i++) {
                if (((int) areaList.get(i).get("id")) == CURRENT_AREA_id) {
                    area_text.setText(areaList.get(i).get("name").toString());
                    CURRENT_AREA_id = (int) areaList.get(i).get("id");
                    Current_INDEX = i + 1;
                }
            }
        }
        getDeskData();
        getRobotData();
        SJXGridViewAdapter.notifyDataSetInvalidated();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 编辑系统卡
            case R.id.card:
                // 跳转到系统卡页面CardConfig
                startActivity(new Intent(ZB_MainActivity.this, ZB_CardConfig.class));
                break;
            // 机器人列表整体
            case R.id.linear_robot:
                // 关闭左侧区域
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            // ActionBar
            case R.id.main:
                // 关闭左侧区域
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            // 导航栏左侧菜单按钮
            case R.id.setting_back:
                startAnimationLeft();
                break;
            case R.id.imgViewmapnRight:
                startAnimationLeft();
                break;
            // 导航栏右侧注销按钮
            case R.id.img_cancel_re:
            case R.id.img_cancel:
                // 修改存储的账号
                PreferencesUtils.putBoolean(this, "save_name", true);
                PreferencesUtils.putString(this, "pass", null);
                Intent intent = new Intent(this, ZB_LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            // 区域右侧编辑按钮
            case R.id.config_redact_re:
            case R.id.config_redact:
                if (DeskIsEdit) {
                    DeskIsEdit = false;
                    findViewById(R.id.config_redact).setBackgroundResource(R.animator.btn_direct_selector);
                } else {
                    DeskIsEdit = true;
                    findViewById(R.id.config_redact).setBackgroundResource(R.animator.btn_exit_selector);
                }
                getDeskData();
                break;
            // 机器人列表整体
            case R.id.robotgirdview:
                // 关闭左侧区域
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            // 桌面列表整体
            case R.id.linear_desk:
                // 关闭左侧区域
                if (!IsRight) {
                    startAnimationLeft();
                }
                break;
            // 前进命令
            case R.id.up:
                //robotDialog("*u+6+#");// 字符串
                // 发送命令
                //robotDialog(Protocol.getSendData(Protocol.UP, Protocol.getCommandData(Protocol.ROBOT_UP)));
                // 测试前进
                robotDialog(Protocol.getSendData(Protocol.UP_SPEED, Protocol.getCommandData(Protocol.UP_SPEED)));

                // 测试清除所有命令
                //robotDialog(Protocol.getSendData(Protocol.CONTROL_CLEAR, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR)));
                // 发送遥控器命令
                // robotDialog(Protocol.getSendData(Protocol.CONTROL_REMOTE, Protocol.getCommandData(Protocol.ROBOT_CONTROL_REMOTE)));
                // 遥控速度
                // robotDialog(Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_SPEND)));
                // 旋转左右
                //robotDialog(Protocol.getSendData(Protocol.CONTROL_ROTATE, Protocol.getCommandData(Protocol.ROBOT_CONTROL_ROTATE_LEFT)));
                //robotDialog(Protocol.getSendData(Protocol.CONTROL_ROTATE, Protocol.getCommandData(Protocol.ROBOT_CONTROL_ROTATE_RIGHT)));
                // 停止遥控
                //robotDialog(Protocol.getSendData(Protocol.CONTROL_STOP, Protocol.getCommandData(Protocol.ROBOT_CONTROL_STOP)));
                // 速度清0
                //robotDialog(Protocol.getSendData(Protocol.CONTROL_SPEND, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR_SPEND)));

                // 测试
                // robotDialog(Protocol.getSendData(Protocol.COORDINATE_CONFIG, Protocol.getCommandDataByte(Protocol.ROBOT_UP)));
                // 清除电机故障
                // robotDialog(Protocol.getSendData(Protocol.CLEAR_FAULT, Protocol.getCommandDataByte(Protocol.ROBOT_CONTROL_FAULT)));
                // 清除命令集合
                // robotDialog(Protocol.getSendData(Protocol.CONTROL_CLEAR, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR)));
                // END
                // robotDialog(Protocol.getSendData(Protocol.END, Protocol.getCommandData(Protocol.ROBOT_END)));
                break;
            // 后退命令
            case R.id.down:
                //robotDialog("*d+6+#");// 字符串
                // robotDialog(Protocol.getSendData(Protocol.DOWN, Protocol.getCommandData(Protocol.ROBOT_DOWN)));
                // 开始
                robotDialog(Protocol.getSendData(Protocol.START, Protocol.getCommandDataByte(Protocol.ROBOT_START)));
                break;
            // 左转命令
            case R.id.left:
                //robotDialog("*l+6+#"); // 字符串
                // robotDialog(Protocol.getSendData(Protocol.LEFT, Protocol.getCommandData(Protocol.ROBOT_LEFT)));

                // 结束
                robotDialog(Protocol.getSendData(Protocol.END, Protocol.getCommandData(Protocol.ROBOT_END)));
                break;
            // 右转命令
            case R.id.right:
                //robotDialog("*r+6+#");// 字符串
                // robotDialog(Protocol.getSendData(Protocol.RIGHT, Protocol.getCommandData(Protocol.ROBOT_RIGHT)));

                robotDialog(Protocol.getSendData(Protocol.CLEAR_FAULT, Protocol.getCommandDataByte(Protocol.ROBOT_CONTROL_FAULT)));
                break;
            // 停止命令
            case R.id.stop:
                //robotDialog("*s+6+#");// 字符串
                //robotDialog(Protocol.getSendData(Protocol.STOP, Protocol.getCommandData(Protocol.ROBOT_STOP)));

                // 测试停止
                robotDialog(Protocol.getSendData(Protocol.UP_SPEED, Protocol.getCommandData(Protocol.UP_STOP)));
                break;
            // 右下角收缩FloatButton按钮
            case R.id.shrink:
                // 关闭左侧区域
                if (!IsRight) {
                    startAnimationLeft();
                }
                // 点击展开 or 收缩
                startAnimationShrink();
                break;
        }
    }

    // 获取桌面数据
    public List<Map<String, Object>> getDeskData() {
        // 先清除一次
        deskData_list.clear();
        try {
            // 查询桌面列表
            deskList = robotDBHelper.queryListMap("select * from desk where area = '" + CURRENT_AREA_id + "'", null);
            // 打印Log
            Constant.debugLog("Robot----->" + deskList.toString());
            Constant.debugLog("Robot----->" + "CURRENT_AREA_id" + CURRENT_AREA_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Object> map;
        if (DeskIsEdit) {
            map = new HashMap<>();
            map.put("image", R.animator.btn_add_desk_selector);
            map.put("id", 0);
            //            map.put("name",getString(R.string.config_add));
            deskData_list.add(map);
        }
        if (deskList != null && deskList.size() > 0) {
            for (int i = 0, size = deskList.size(); i < size; i++) {
                map = new HashMap<>();
                if (DeskIsEdit) {
                    //                    map.put("image", R.mipmap.ic_launcher);
                } else {
                    //                    map.put("image", R.mipmap.bg);
                }
                map.put("id", deskList.get(i).get("id"));
                map.put("name", deskList.get(i).get("name"));
                map.put("area", deskList.get(i).get("area"));
                deskData_list.add(map);
            }
        }
        desk_adapter.notifyDataSetChanged();
        return deskData_list;
    }

    //获取机器人数据
    public List<Map> getRobotData() {
        // 先清除一次
        robotData_List.clear();
        try {
            // 查询机器人列表
            robotList = robotDBHelper.queryListMap("select * from robot", null);
            // 打印Log
            Constant.debugLog("robotList----->" + robotList.toString());

            List<Map> robotData_ListCache = new ArrayList<>();
            int j;
            boolean flag;
            for (int i = 0, size = robotList.size(); i < size; i++) {
                // 打印log
                Constant.debugLog("size----->" + size + " ip----->" + robotList.get(i).get("ip").toString());
                String ip = robotList.get(i).get("ip").toString();
                j = 0;
                int h = ServerSocketUtil.socketList.size();
                flag = false;
                while (j < h) {
                    if (ip.equals(ServerSocketUtil.socketList.get(j).get("ip"))) {
                        // 打印Log
                        Constant.debugLog("<-----对比----->");
                        // 修改运行轨迹
                        robotDBHelper.execSQL("update robot set outline= '1' where ip = '" + robotList.get(i).get("ip") + "'");
                        robotList.get(i).put("outline", 1);
                        robotData_ListCache.add(robotList.get(i));
                        robotList.remove(i);
                        flag = true;
                        break;
                    }
                    j++;
                    h = ServerSocketUtil.socketList.size();
                }
                size = robotList.size();
                if (flag) {
                    i--;
                }
            }
            robotData_List.addAll(robotData_ListCache);
            robotData_List.addAll(robotList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGridView();
        return robotData_List;
    }

    // 获取区域数据
    public List<Map<String, Object>> getAreaData() {
        // 先清除一次
        areaData_list.clear();
        try {
            // 查询区域列表
            areaList = robotDBHelper.queryListMap("select * from area", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> map;
        if (name.equals(Constant.ADMIN)) {
            map = new HashMap<>();
            map.put("image", R.mipmap.qybianji_no);
            //        map.put("name",getString(R.string.config_redact));
            areaData_list.add(map);
            if (AreaIsEdit) {
                map = new HashMap<>();
                map.put("image", "2654");
                //            map.put("name",getString(R.string.config_add));
                areaData_list.add(map);
            }
        }
        if (areaList != null && areaList.size() > 0) {
            for (int i = 0, size = areaList.size(); i < size; i++) {
                map = new HashMap<>();
                if (AreaIsEdit) {
                    //                    map.put("image", R.mipmap.ic_launcher);
                } else {
                    //                    map.put("image", R.mipmap.bg);
                }
                map.put("id", areaList.get(i).get("id"));
                map.put("name", areaList.get(i).get("name"));
                areaData_list.add(map);
            }
        }
        area_adapter.notifyDataSetChanged();
        return areaData_list;
    }

    // 左侧区域平移动画
    private void startAnimationLeft() {
        if (IsFinish) {
            IsFinish = false;
            if (IsRight) {
                linearLayout_all.setVisibility(View.VISIBLE);
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, -Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0F
                );
                // 设置一个动画的持续时间
                translateAnimation.setDuration(500);
                // 设置动画是否停留在最后一帧，为true则是停留在最后一帧
                translateAnimation.setFillAfter(true);
                // 给一个动画设置监听，设置类似侦听动画的开始或动画重复的通知
                translateAnimation.setAnimationListener(ZB_MainActivity.this);
                // 左侧区域平移出来
                map_right_Relative.startAnimation(translateAnimation);

                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0F
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_robot.startAnimation(translateAnimation);
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0F
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_desk.startAnimation(translateAnimation);
            } else {
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, -Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                translateAnimation.setAnimationListener(ZB_MainActivity.this);
                map_right_Relative.startAnimation(translateAnimation);

                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_robot.startAnimation(translateAnimation);
                translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, Constant.linearWidth,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f,
                        Animation.ABSOLUTE, 0.0f
                );
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                linear_desk.startAnimation(translateAnimation);
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        map_right_Relative.clearAnimation();
        if (IsRight) {
            IsRight = false;
        } else {
            IsRight = true;
            linearLayout_all.setVisibility(View.GONE);
        }
        IsFinish = true;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    // FloatButton按钮展开 和 收缩
    @SuppressWarnings("deprecation")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startAnimationShrink() {
        Animation translate;
        if (isShrink) {
            //  默认收缩状态的图标
            findViewById(R.id.shrink).setBackground(getResources().getDrawable(R.animator.btn_shrink_selector));
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_left);
            translate.setAnimationListener(animationListener);
            // 向左运行
            left.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_right);
            translate.setAnimationListener(animationListener);
            // 向右运行
            right.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_up);
            translate.setAnimationListener(animationListener);
            // 前进运行
            up.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_down);
            translate.setAnimationListener(animationListener);
            // 后退运行
            down.startAnimation(translate);
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_in_stop);
            translate.setAnimationListener(animationListener);
            // 停止运行
            stop.startAnimation(translate);
        } else {
            // 点击展开的图标
            findViewById(R.id.shrink).setBackground(getResources().getDrawable(R.animator.btn_shrink_out_selector));
            // 上 下 左 右 停止 的按钮显示出来
            left.setVisibility(View.VISIBLE);
            down.setVisibility(View.VISIBLE);
            up.setVisibility(View.VISIBLE);
            right.setVisibility(View.VISIBLE);
            stop.setVisibility(View.VISIBLE);
            // 左运行
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_left);
            translate.setAnimationListener(animationListener);
            left.startAnimation(translate);
            // 右运行
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_right);
            translate.setAnimationListener(animationListener);
            right.startAnimation(translate);
            // 前进运行
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_up);
            translate.setAnimationListener(animationListener);
            up.startAnimation(translate);
            // 后退运行
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_down);
            translate.setAnimationListener(animationListener);
            down.startAnimation(translate);
            // 停止运行
            translate = AnimationUtils.loadAnimation(this, R.animator.translate_out_stop);
            translate.setAnimationListener(animationListener);
            stop.startAnimation(translate);
        }
    }

    private Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (isShrink) {
                // FloatButton收缩
                isShrink = false;
                up.setVisibility(View.GONE);
                down.setVisibility(View.GONE);
                left.setVisibility(View.GONE);
                right.setVisibility(View.GONE);
                stop.setVisibility(View.GONE);
            } else {
                // FloatButton展开
                isShrink = true;
                left.setVisibility(View.VISIBLE);
                down.setVisibility(View.VISIBLE);
                up.setVisibility(View.VISIBLE);
                right.setVisibility(View.VISIBLE);
                stop.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    };

    // 初始化区域Dialog
    private ZB_MyDialog dialog;
    private EditText editText;
    private TextView title;

    // 区域Dialog
    private void dialog() {
        dialog = new ZB_MyDialog(this);
        editText = (EditText) dialog.getEditText();
        // 确定Dialog
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "区域名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // 添加新区域
                    robotDBHelper.insert("area", new String[]{"name"}, new Object[]{editText.getText().toString()});
                    // 获取区域数据
                    getAreaData();
                    // 销毁当前Dialog
                    dialog.dismiss();
                }
            }
        });
        // 取消Dialog
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁当前Dialog
                dialog.dismiss();
            }
        });
        // 显示Dialog
        dialog.show();
    }

    // 修改区域Dialog
    private void dialog(String name, final int id) {
        dialog = new ZB_MyDialog(this);
        editText = (EditText) dialog.getEditText();
        editText.setText(name);
        title = (TextView) dialog.getTitle();
        // 确定Dialog
        dialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "区域名称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // 修改区域名称
                    robotDBHelper.execSQL("update area set name= '" + editText.getText().toString().trim() + "' where id= '" + id + "'");
                    dialog.dismiss();
                }
            }
        });

        // 删除Dialog
        dialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除当前区域
                deleteDialog(id);
            }
        });
        // 设置Dialog 左侧取消按钮Text 为 删除
        ((Button) dialog.getNegative()).setText(R.string.btn_delete);
        //显示Dialog
        dialog.show();
    }

    // 机器人运行Dialog
    private ZB_RobotDialog SJXRobotDialog;

    // 删除Dialog
    private ZB_DeleteDialog SJXDeleteDialog;

    // 字符串
    private void robotDialog(String str) {
        SJXRobotDialog = new ZB_RobotDialog(this, str);
        SJXRobotDialog.show();
    }

    // byte数组
    private void robotDialog(byte[] data) {
        SJXRobotDialog = new ZB_RobotDialog(this, data);
    }


    // List集合
    private void robotDialog(List idList) {
        SJXRobotDialog = new ZB_RobotDialog(this, idList);
    }

    // 根据id删除区域
    private void deleteDialog(final int id) {
        SJXDeleteDialog = new ZB_DeleteDialog(this);
        SJXDeleteDialog.getTemplate().setText("确定删除区域吗？");
        // 确定Dialog
        SJXDeleteDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除区域
                robotDBHelper.execSQL("delete from area where id= '" + id + "'");
                List<Map> deskList;
                // 删除之后再次查询桌面
                deskList = robotDBHelper.queryListMap("select * from desk where area = '" + id + "'", null);
                for (int i = 0, size = deskList.size(); i < size; i++) {
                    // 删除命令
                    robotDBHelper.execSQL("delete from command where desk= '" + deskList.get(i).get("id") + "'");
                }
                // 删除桌面
                robotDBHelper.execSQL("delete from desk where area= '" + id + "'");
                // 获取区域数据
                getAreaData();
                if (areaList != null && areaList.size() > 0) {
                    CURRENT_AREA_id = (int) areaList.get(0).get("id");
                    Current_INDEX = 1;
                    area_text.setText(areaList.get(0).get("name").toString());
                } else {
                    area_text.setText("请选择左侧区域");
                }
                // 获取桌面数据
                getDeskData();
                // 销毁当前Dialog
                SJXDeleteDialog.dismiss();
                dialog.dismiss();
            }
        });
        // 取消Dialog
        SJXDeleteDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁当前Dialog
                SJXDeleteDialog.dismiss();
            }
        });
        // 显示Dialog
        SJXDeleteDialog.show();
    }


    // 注册广播
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String StringE = intent.getStringExtra("msg");
            // 打印log
            Constant.debugLog("msg----->" + StringE);
            if (StringE != null && !StringE.equals("")) {
                // 解析命令
                parseJson(StringE);
            }
        }
    }

    // 解析命令
    public void parseJson(String string) {
        if (string.equals("robot_connect") || string.equals("robot_unconnect")) {
            // 获取机器人数据
            getRobotData();
            // 刷新
            SJXGridViewAdapter.notifyDataSetInvalidated();
            Constant.debugLog("=====连接成功=====");
        } else if (string.equals("robot_receive_succus")) {
            Constant.debugLog("=====收到指令成功=====");
            synchronized (ZB_RobotDialog.thread) {
                if (!SJXRobotDialog.IsCoordinate) {
                    if (ZB_RobotDialog.CurrentIndex == -1) {
                        ZB_RobotDialog.CurrentIndex = 0;
                    }
                    ZB_RobotDialog.thread.notify();
                } else {
                    ZB_RobotDialog.thread.notify();
                }
            }
        } else if (string.equals("robot_receive_fail")) {
            Constant.debugLog("=====收到指令失败=====");
            if (!SJXRobotDialog.IsCoordinate) {
                if (ZB_RobotDialog.flag) {
                    ZB_RobotDialog.sendCommandList(isList);
                } else {
                    ZB_RobotDialog.sendCommand();
                }
            } else {
                //                SJX_RobotDialog.sendCommandCoordinate();
            }
        } else if (string.equals("robot_destory")) {
            Constant.debugLog("=====销毁机器人=====");
            robotDBHelper.execSQL("update robot set outline= '0' ");
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Constant.debugLog("requestCode"+requestCode+"permissions"+permissions+"grantResults"+grantResults);
        if(1 == requestCode){

        }else{
            Toast.makeText(getApplicationContext(),"已取消获取存储权限",Toast.LENGTH_SHORT).show();
        }
    }
}
