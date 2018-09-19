package com.android.zbrobot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import com.android.zbrobot.R;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.util.Constant;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/17
 * 描述: 机器人编辑页面
 */

public class ZB_RobotConfigActivity extends Activity implements View.OnClickListener , CompoundButton.OnCheckedChangeListener {
    // 初始化数据库类
    private RobotDBHelper robotDBHelper;
    // 机器人id
    private static int robotId;
    // 机器人信息
    private Map robotConfig;
    // 机器人名称
    private EditText name;
    // 区域名称
    private TextView area_text;
    // 区域id
    public int areaId;
    // 存储区域数据
    private List<Map> areaList;

    private CheckBox robot_top,robot_botton,roobotside;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.zb_activity_robot_config);

        //初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        // 机器人名称
        name = ((EditText) findViewById(R.id.name));
        // 确定
        findViewById(R.id.sure).setOnClickListener(this);
        // 返回
        findViewById(R.id.setting_back).setOnClickListener(this);
        // 返回
        findViewById(R.id.back).setOnClickListener(this);
        // 机器人编辑左侧  ->区域
        area_text = (TextView) findViewById(R.id.area_text);
        // 机器人编辑右侧  ->必选
        findViewById(R.id.area).setOnClickListener(this);
        // 获取RobotActivity传递过来的Id
        Intent intent = getIntent();
        robotId = intent.getIntExtra("id", 0);
        robot_top = (CheckBox) findViewById(R.id.robot_top);
        robot_botton = (CheckBox) findViewById(R.id.robot_bottom);
        roobotside = (CheckBox) findViewById(R.id.robot_side);

        robot_top.setOnCheckedChangeListener(this);
        robot_botton.setOnCheckedChangeListener(this);
        roobotside.setOnCheckedChangeListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // 查询机器人列表 根据id查询
        List<Map> robotList = robotDBHelper.queryListMap("select * from robot where id = '" + robotId + "'", null);
        robotConfig = robotList.get(0);

        if(robotConfig.get("pathway").equals(0)){

        }else{

        }

        if (robotConfig.get("up_obstacle") != null) {
            robot_top.setChecked(0 == (int)robotConfig.get("up_obstacle") ? true : false);
        }
        // 下边障碍物
        if (robotConfig.get("down_obstacle") != null) {
            robot_botton.setChecked(0 == (int)robotConfig.get("down_obstacle") ? true : false);
        }
        // 侧边障碍物
        if (robotConfig.get("side_obstacle") != null) {
            roobotside.setChecked(0 == (int)robotConfig.get("side_obstacle") ? true : false);
        }

        name.setHint(robotConfig.get("name").toString());
        // 查询区域列表
        areaList = robotDBHelper.queryListMap("select * from area ", null);
        areaId = (int) robotConfig.get("area");
        area_text.setText("未选择区域");
        if (areaList != null && areaList.size() > 0) {
            if (areaId != 0) {
                for (int i = 0, size = areaList.size(); i < size; i++) {
                    if (areaList.get(i).get("id").equals(areaId)) {
                        area_text.setText(areaList.get(i).get("name").toString());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 点击确定按钮
            case R.id.sure:
                Constant.debugLog("区域areaId----->" + areaId);
                if (!name.getText().toString().trim().equals("")) {
                    // 修改机器人名称
                    robotDBHelper.execSQL("update robot set name= '" + name.getText().toString().trim() + "'  where id= '" + robotId + "'");
                } else {
                    // 获取原有机器人名称 然后再修改
                    robotDBHelper.execSQL("update robot set name= '" + robotConfig.get("name").toString() + "'  where id= '" + robotId + "'");
                }
                finish();
                break;
            // 返回
            case R.id.setting_back:
                finish();
                break;
            // 返回
            case R.id.back:
                finish();
                break;
            // 区域
            case R.id.area:
                // 跳转到系统卡列表AreaConfig 并传递id
                Intent intent = new Intent(ZB_RobotConfigActivity.this, ZB_AreaConfig.class);
                intent.putExtra("id", robotId);
                startActivity(intent);
                break;
        }
    }

    /**
     * 是否打开障碍物
     *
     * @param buttonView CheckBox
     * @param isChecked  是否选中
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.robot_top:
                if (robot_top.isChecked()) {
                    // up_obstacle = 0;
                    robotDBHelper.execSQL("update robot set up_obstacle = '" + 0 + "' where id= '" + robotId + "'");
                } else {
                    // up_obstacle = 1;
                    robotDBHelper.execSQL("update robot set up_obstacle = '" + 1 + "' where id= '" + robotId + "'");
                }
                break;
            case R.id.robot_bottom:
                if (robot_botton.isChecked()) {
                    // up_obstacle = 0;
                    robotDBHelper.execSQL("update robot set down_obstacle = '" + 0 + "' where id= '" + robotId + "'");
                } else {
                    // up_obstacle = 1;
                    robotDBHelper.execSQL("update robot set down_obstacle = '" + 1 + "' where id= '" + robotId + "'");
                }
                break;
            case R.id.robot_side:
                if (roobotside.isChecked()) {
                    // up_obstacle = 0;
                    robotDBHelper.execSQL("update robot set side_obstacle = '" + 0 + "' where id= '" + robotId + "'");
                } else {
                    // up_obstacle = 1;
                    robotDBHelper.execSQL("update robot set side_obstacle = '" + 1 + "' where id= '" + robotId + "'");
                }
                break;
        }
    }
}
