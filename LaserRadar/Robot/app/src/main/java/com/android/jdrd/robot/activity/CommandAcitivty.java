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
    private List<Map> goallist;
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

        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);

        ArrayList<String> map_Plan = new ArrayList<>();
        map_Plan.add("直行");
        map_Plan.add("左岔道");
        map_Plan.add("右岔道");
        diretionAdapter = new ArrayAdapter<>(this,R.layout.item_spinselect,map_Plan);
        diretionAdapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        direction.setAdapter(diretionAdapter);

        map_Plan = new ArrayList<>();
        goallist = robotDBHelper.queryListMap("select * from card " ,null);
        if(goallist!=null&&goallist.size()>0){
            for(int i = 0,size = goallist.size();i< size;i++){
                map_Plan.add(goallist.get(i).get("name").toString());
            }
        }
        goalAdapter = new ArrayAdapter<>(this,R.layout.item_spinselect,map_Plan);
        goalAdapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        goal.setAdapter(goalAdapter);

        List<Map> commandlist = robotDBHelper.queryListMap("select * from command where id = '"+ command_id +"'" ,null);
        Log.e("commandlist" ,commandlist.toString());
        if(commandlist !=null && commandlist.size() >0){
            commandconfig = commandlist.get(0);
            Constant.debugLog("commandconfig type"+(int)commandconfig.get("type"));
            if(commandconfig.get("goal") !=null){
                if(goallist!=null&&goallist.size()>0){
                    boolean flag = false;
                    for(int i = 0,size = goallist.size();i< size;i++){
                        if(goallist.get(i).get("id").equals(commandconfig.get("goal"))){
                            goal.setSelection(i,true);
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        goal.setSelection(0,true);
                    }
                }
            }else{
                goal.setSelection(0,true);
            }
            if(commandconfig.get("direction") !=null){
                direction.setSelection((int)commandconfig.get("direction"),true);
            }else{
                direction.setSelection(0,true);
            }

            if(commandconfig.get("speed") !=null){
                speed.setText(commandconfig.get("speed").toString().trim());
            }
            if(commandconfig.get("music") !=null){
                mp3.setText(commandconfig.get("music").toString());
            }
            if(commandconfig.get("outime") !=null){
                outime.setText(commandconfig.get("outime").toString());
            }
            if(commandconfig.get("shownumber") !=null){
                shownum.setText(commandconfig.get("shownumber").toString());
            }
            if(commandconfig.get("showcolor") !=null){
                showcolor.setText(commandconfig.get("showcolor").toString());
            }
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

        direction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                directionnum = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        goal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                goalnum = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        findViewById(R.id.btn_sure).setOnClickListener(this);

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
            case R.id.btn_delete:
                robotDBHelper.execSQL("delete from command where id = '"+command_id+"'");
                finish();
                break;
            case R.id.btn_sure:
                switch ((int)commandconfig.get("type")){
                    case 0:
                        if(!speed.getText().toString().equals("") && !mp3.getText().toString().equals("") && !outime.getText().toString().equals("")
                                &&!shownum.getText().toString().equals("") && !showcolor.getText().toString().equals("")){
                            robotDBHelper.execSQL("update command set goal= '"+goallist.get(goalnum).get("id")+"' ," +
                                    "direction = '"+directionnum+"' ,speed = '"+speed.getText().toString().trim()+"'," +
                                    "music = '"+mp3.getText().toString().trim()+"' ,outime = '"+outime.getText().toString().trim()+"' ," +
                                    "shownumber = '"+shownum.getText().toString().trim()+"' ,showcolor = '"+showcolor.getText().toString().trim()+"' where id= '"+ command_id +"'");
                        }
                        break;
                    case 1:
                        if(!speed.getText().toString().equals("") && !mp3.getText().toString().equals("") && !outime.getText().toString().equals("")
                                &&!shownum.getText().toString().equals("") && !showcolor.getText().toString().equals("")){
                            robotDBHelper.execSQL("update command set goal= '"+0+"' ," +
                                    "direction = '"+0+"' ,speed = '"+speed.getText().toString().trim()+"'," +
                                    "music = '"+mp3.getText().toString().trim()+"' ,outime = '"+outime.getText().toString().trim()+"' ," +
                                    "shownumber = '"+shownum.getText().toString().trim()+"' ,showcolor = '"+showcolor.getText().toString().trim()+"' where id= '"+ command_id +"'");
                        }
                        break;
                    case 2:
                        if(!speed.getText().toString().equals("") && !mp3.getText().toString().equals("") && !outime.getText().toString().equals("")
                                &&!shownum.getText().toString().equals("") && !showcolor.getText().toString().equals("")){
                            robotDBHelper.execSQL("update command set goal= '"+0+"' ," +
                                    "direction = '"+0+"' ,speed = '"+speed.getText().toString().trim()+"'," +
                                    "music = '"+mp3.getText().toString().trim()+"' ,outime = '"+outime.getText().toString().trim()+"' ," +
                                    "shownumber = '"+shownum.getText().toString().trim()+"' ,showcolor = '"+showcolor.getText().toString().trim()+"' where id= '"+ command_id +"'");
                        }
                        break;
                    case 3:
                        if(!mp3.getText().toString().equals("") && !outime.getText().toString().equals("")
                                &&!shownum.getText().toString().equals("") && !showcolor.getText().toString().equals("")){
                            robotDBHelper.execSQL("update command set goal= '"+0+"' ," +
                                    "direction = '"+0+"' ,speed = '"+0+"'," +
                                    "music = '"+mp3.getText().toString().trim()+"' ,outime = '"+outime.getText().toString().trim()+"' ," +
                                    "shownumber = '"+shownum.getText().toString().trim()+"' ,showcolor = '"+showcolor.getText().toString().trim()+"' where id= '"+ command_id +"'");
                        }
                        break;
                    default:
                        break;
                }
                finish();
                break;
        }
    }


}
