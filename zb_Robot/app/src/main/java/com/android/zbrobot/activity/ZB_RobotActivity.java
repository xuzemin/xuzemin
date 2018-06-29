package com.android.zbrobot.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zbrobot.R;
import com.android.zbrobot.adapter.ZB_SpinnerAdapter;
import com.android.zbrobot.dialog.ZB_MyDialog;
import com.android.zbrobot.dialog.ZB_SpinnerDialog;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/16
 * 描述: 机器人状态
 */

public class ZB_RobotActivity extends Activity implements View.OnClickListener {
    // 数据库帮助类
    private RobotDBHelper robotDBHelper;
    // 机器人id
    private int robotId;
    // 初始化广播
    private MyReceiver receiver;
    // 机器人信息
    private Map robotConfig;

    public static int goalNum = -1, isturnback = -1;

    private List<Map> list;

    // 设置按钮
    private Button setting_redact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.zb_activity_robot);
        //初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        // 获取MainActivity传递的Id
        Intent intent = getIntent();
        robotId = intent.getIntExtra("id", 0);

        // 设置
        setting_redact = (Button) findViewById(R.id.setting_redact);
        setting_redact.setOnClickListener(this);
        // 返回
        findViewById(R.id.setting_back).setOnClickListener(this);
        // 返回
        findViewById(R.id.back).setOnClickListener(this);
        // 机器人遥控器
        findViewById(R.id.remote_control).setOnClickListener(this);

        String name = getIntent().getStringExtra("us_name");
        if (name.equals(Constant.ADMIN)) {

        } else if (name.equals(Constant.SINGLE)) {
            setting_redact.setVisibility(View.GONE);
        } else if (name.equals(Constant.MANY)) {
            setting_redact.setVisibility(View.GONE);
        }

        receiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.jdrd.activity.Robot");
        // 注册广播
        if (receiver != null) {
            this.registerReceiver(receiver, filter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 初始化数据
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册广播
        if (receiver != null) {
            this.unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 初始化数据
     */
    private void init() {
        // 查询机器人列表 根据id查询
        List<Map> robotList = robotDBHelper.queryListMap("select * from robot where id = '" + robotId + "'", null);
        robotConfig = robotList.get(0);
        Constant.debugLog("robot----->" + robotList.toString());
        Constant.debugLog("robotConfig----->" + robotConfig.toString());


        goalList = robotDBHelper.queryListMap("select * from card ", null);

        //查询区域列表 根据id查询
        List<Map> areaList = robotDBHelper.queryListMap("select * from area where id = '" + robotConfig.get("area") + "'", null);
        // 打印Log
        Constant.debugLog(areaList.toString());
        // 服务区域
        if (areaList != null && areaList.size() > 0) {
            ((TextView) findViewById(R.id.area)).setText(areaList.get(0).get("name").toString());
        }
        // 机器人在线状态   1->在线     0->离线
        if ((int) robotConfig.get("outline") == 1) {
            ((TextView) findViewById(R.id.outline)).setText("在线");
            findViewById(R.id.linear_control).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.outline)).setText("离线");
            findViewById(R.id.linear_control).setVisibility(View.GONE);
        }
        //机器人状态  0->空闲   1->送餐   2->故障
        if ((int) robotConfig.get("robotstate") == 0) {
            ((TextView) findViewById(R.id.robot_state)).setText("空闲");
        } else if ((int) robotConfig.get("robotstate") == 1) {
            ((TextView) findViewById(R.id.robot_state)).setText("送餐");
        } else if ((int) robotConfig.get("robotstate") == 2) {
            ((TextView) findViewById(R.id.robot_state)).setText("故障");
        }
        // 机器人运行状态  0->直行前进    1->左转    2->右转    3->旋转
        if ((int) robotConfig.get("state") == 0) {
            ((TextView) findViewById(R.id.state)).setText("直行前进");
        } else if ((int) robotConfig.get("state") == 1) {
            ((TextView) findViewById(R.id.state)).setText("左转");
        } else if ((int) robotConfig.get("state") == 2) {
            ((TextView) findViewById(R.id.state)).setText("右转");
        } else if ((int) robotConfig.get("state") == 3) {
            ((TextView) findViewById(R.id.state)).setText("旋转");
        }

        // 获取信息
        ((TextView) findViewById(R.id.name)).setText(robotConfig.get("name").toString());// 名称
        ((TextView) findViewById(R.id.ip)).setText(robotConfig.get("ip").toString());// ip地址
        ((TextView) findViewById(R.id.electric)).setText(robotConfig.get("electric").toString());// 电压
        ((TextView) findViewById(R.id.command_num)).setText(robotConfig.get("commandnum").toString());// 指令总数
        ((TextView) findViewById(R.id.last_location)).setText(robotConfig.get("lastlocation").toString());// 最后坐标
        ((TextView) findViewById(R.id.outtime)).setText(robotConfig.get("outtime").toString());
        if((int)robotConfig.get("turnback") == 1){
            ((TextView)findViewById(R.id.turnback)).setText("是");
            //findViewById(R.id.outime).setVisibility(View.VISIBLE);
            isturnback = 1;
        }else{
            ((TextView)findViewById(R.id.turnback)).setText("否");
            isturnback = 0;
            //findViewById(R.id.outime).setVisibility(View.GONE);
        }
        findViewById(R.id.outtime).setOnClickListener(this);
        findViewById(R.id.turnback).setOnClickListener(this);
        findViewById(R.id.goal).setOnClickListener(this);
        if((int)robotConfig.get("goal") == 0) {
            ((TextView) findViewById(R.id.goal)).setText("请选择站点");
        }else{
            if (goalList != null && goalList.size() > 0) {
                boolean flag = false;
                for (int i = 0, size = goalList.size(); i < size; i++) {
                    if (goalList.get(i).get("id").equals(robotConfig.get("goal"))) {
                        ((TextView) findViewById(R.id.goal)).setText(goalList.get(i).get("name").toString());
                        flag = true;
                        goalNum = i;
                        break;
                    }
                }
                if (!flag) {
                    ((TextView) findViewById(R.id.goal)).setText("请选择站点");
                }
            }
        }

        list = new ArrayList<>();
        // 以键值对来传递值
        Map<String, Object> map;
        map = new HashMap<>();
        map.put("name", "否");
        list.add(map);
        map = new HashMap<>();
        map.put("name", "是");
        list.add(map);

        // 是否有障碍物 0->无  1->有
        if ((int) robotConfig.get("obstacle") == 0) {
            ((TextView) findViewById(R.id.obstacle)).setText("无");
        } else {
            ((TextView) findViewById(R.id.obstacle)).setText("有");
        }
        if ((int) robotConfig.get("pathway") == 0) {
            ((TextView) findViewById(R.id.pathway)).setText("有");
        } else {
            ((TextView) findViewById(R.id.pathway)).setText("无");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 机器人状态设置
            case R.id.setting_redact:
                // 跳转到 机器人编辑页面 并传递id
                Intent intent = new Intent(ZB_RobotActivity.this, ZB_RobotConfigActivity.class);
                intent.putExtra("id", robotId);
                startActivity(intent);
                break;
            // 遥控器
            case R.id.remote_control:
                // 跳转到遥控器页面 并传递id
                Intent intent1 = new Intent(ZB_RobotActivity.this, ZB_RoundActivity.class);
                intent1.putExtra("id", robotId);
                startActivity(intent1);
                break;
            // 返回
            case R.id.setting_back:
                finish();
                break;
            // 返回
            case R.id.back:
                finish();
                break;
            case R.id.outtime:
                dialog_Text();
                break;
            case R.id.goal:
                dialog_spinner(true);
                break;
            case R.id.turnback:
                dialog_spinner(false);
                break;
        }
    }

    // 广播监听
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String StringE = intent.getStringExtra("msg");
            // 打印log
            Constant.debugLog("msg----->" + StringE);
            if (StringE != null && !StringE.equals("")) {
                parseJson(StringE);
            }
        }
    }

    //解析数据
    public void parseJson(String string) {
        if (string.equals("robot")) {
            init();
        } else {
        }
    }

    private ZB_MyDialog textDialog;
    private EditText editText;
    private void dialog_Text() {
        textDialog = new ZB_MyDialog(this);
        // type=  0->速度  1->MP3通道  2->超时时间  3->显示编号  4->显示颜色
        textDialog.getTitle().setText("旋转超时时间修改");
        textDialog.getTitleTemp().setText("请输入超时时间");
        editText = (EditText) textDialog.getEditText();
        // 输入类型为数字文本
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        // 确定Dialog
        textDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().trim().equals("")) {
                    robotDBHelper.execSQL("update robot set outtime = '" + editText.getText().toString().trim() + "' where id= '" + robotId + "'");
                    textDialog.dismiss();
                    ((TextView) findViewById(R.id.outtime)).setText(editText.getText().toString().trim());
                } else {
                    Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 取消Dialog
        textDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁窗体
                textDialog.dismiss();
            }
        });
        // 显示Dialog
        textDialog.show();
    }
    private List<Map> goalList;
    private ZB_SpinnerDialog spinnerDialog;
    private ZB_SpinnerAdapter ZBSpinnerAdapter;

    // 自定义运动到站点对话框
    private void dialog_spinner(final boolean gl) {
        spinnerDialog = new ZB_SpinnerDialog(this);

        if (gl) {
            ZBSpinnerAdapter = new ZB_SpinnerAdapter(this, goalList, gl);
        } else {
            ZBSpinnerAdapter = new ZB_SpinnerAdapter(this, list, gl);
        }
        // 加载适配器
        spinnerDialog.getListView().setAdapter(ZBSpinnerAdapter);
        // 子列表点击事件
        spinnerDialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (gl) {
                    goalNum = position;
                } else {
                    isturnback = position;
                }
                ZBSpinnerAdapter.notifyDataSetChanged();
            }
        });
        // 确定Dialog
        spinnerDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gl) {
                    if (goalList != null && goalList.size() > 0 && goalNum != -1) {
                        robotDBHelper.execSQL("update robot set goal= '" + goalList.get(goalNum).get("id") + "' where id= '" + robotId + "'");
                        ((TextView)findViewById(R.id.goal)).setText(goalList.get(goalNum).get("name").toString());
                        spinnerDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "请选择站点系统卡", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    robotDBHelper.execSQL("update robot set turnback = '" + isturnback + "' where id= '" + robotId + "'");
                    switch (isturnback) {
                        case 0:
                            ((TextView)findViewById(R.id.turnback)).setText("否");
                            break;
                        case 1:
                            ((TextView)findViewById(R.id.turnback)).setText("是");
                            break;
                    }
                    spinnerDialog.dismiss();
                }
            }
        });
        // 取消Dialog
        spinnerDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁窗体
                spinnerDialog.dismiss();
            }
        });
        // 显示Dialog
        spinnerDialog.show();
    }
}
