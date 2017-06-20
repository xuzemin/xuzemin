package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.adapter.MyAdapter;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/13.
 *
 */

public class CommandAcitivty extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int command_id;
    private Map commandconfig;
    private EditText speed,mp3,outime,shownum,showcolor;
    private Spinner goal,direction;
    private int goalnum,directionnum;
    private ArrayAdapter diretionAdapter,goalAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_command_config);

        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        Intent intent =getIntent();
        command_id = intent.getIntExtra("id",0);
        Log.e("commandlist" ,command_id+"");
        goal = (Spinner) findViewById(R.id.goal);
        direction = (Spinner) findViewById(R.id.direction);
        speed = (EditText) findViewById(R.id.speed);
        mp3 = (EditText) findViewById(R.id.mp3);
        outime = (EditText) findViewById(R.id.outime);
        shownum = (EditText) findViewById(R.id.shownum);
        showcolor = (EditText) findViewById(R.id.showcolor);

        findViewById(R.id.setting_back).setOnClickListener(this);

        List<Map> commandlist = robotDBHelper.queryListMap("select * from command where id = '"+ command_id +"'" ,null);
        Log.e("commandlist" ,commandlist.toString());
        if(commandlist !=null && commandlist.size() >0){
            commandconfig = commandlist.get(0);
            Constant.debugLog("commandconfig type"+(int)commandconfig.get("type"));
            switch ((int)commandconfig.get("type")){
                case 0:
                    break;
                case 1:
                    findViewById(R.id.linear_goal).setVisibility(View.GONE);
                    findViewById(R.id.linear_direction).setVisibility(View.GONE);
                    break;
                case 2:
                    findViewById(R.id.linear_goal).setVisibility(View.GONE);
                    findViewById(R.id.linear_direction).setVisibility(View.GONE);
                    ((TextView)findViewById(R.id.speed_text)).setText("旋转速度");
                    break;
                case 3:
                    findViewById(R.id.linear_goal).setVisibility(View.GONE);
                    findViewById(R.id.linear_direction).setVisibility(View.GONE);
                    findViewById(R.id.linear_speed).setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }


        ArrayList<String> map_Plan = new ArrayList<>();
        map_Plan.add("直行");
        map_Plan.add("左岔道");
        map_Plan.add("右岔道");
        diretionAdapter = new ArrayAdapter<>(this,R.layout.item_spinselect,map_Plan);
        diretionAdapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        direction.setAdapter(diretionAdapter);




    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting_back:
                finish();
                break;
        }
    }


}
