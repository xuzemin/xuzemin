package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/16.
 *
 */

public class RobotConfigActivity extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int robotid;
    private Map robotconfig;
    private EditText name;
    private ArrayAdapter adapter;
    private Spinner spinner;
    public int areaid;
    private List<Map> arealist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_robot_config);
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        Intent intent =getIntent();
        robotid = intent.getIntExtra("id",0);


        List<Map> robotlist = robotDBHelper.queryListMap("select * from robot where id = '"+ robotid +"'" ,null);
        robotconfig = robotlist.get(0);
        Constant.debugLog("robot"+robotlist.toString());
        name = ((EditText)findViewById(R.id.name));
        name.setHint(robotconfig.get("name").toString());

        arealist = robotDBHelper.queryListMap("select * from area " ,null);

        findViewById(R.id.sure).setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.area);
        ArrayList<String> map_Plan = new ArrayList<>();
        if(arealist!=null && arealist.size()>0){
            for(int i=0,size = arealist.size();i<size;i++){
                map_Plan.add(arealist.get(i).get("name").toString());
            }
        }
        adapter = new ArrayAdapter<>(this,R.layout.item_spinselect,map_Plan);
        adapter.setDropDownViewResource(R.layout.item_dialogspinselect);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                areaid = (int) arealist.get(position).get("id");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        areaid = (int) robotconfig.get("area");
        if(arealist !=null && arealist.size()>0){
            if(areaid !=0){
                for(int i = 0,size = arealist.size();i<size;i++){
                    if(arealist.get(i).get("id").equals(areaid)){
                        spinner.setSelection(i,true);
                        break;
                    }
                }
            }else{
                Constant.debugLog("id"+arealist.get(0).get("id"));
                areaid = (int) arealist.get(0).get("id");
                spinner.setSelection(0,true);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sure:
                Constant.debugLog("areaid"+areaid);
                    if(areaid != 0){
                        if(!name.getText().toString().trim().equals("")){
                            robotDBHelper.execSQL("update robot set name= '"+name.getText().toString().trim()+"' ,area= '"+areaid+"' where id= '"+ robotid +"'");
                        }else{
                            robotDBHelper.execSQL("update robot set name= '"+robotconfig.get("name").toString()+"' ,area= '"+areaid+"' where id= '"+ robotid +"'");
                        }
                    }else {
                        if(!name.getText().toString().trim().equals("")){
                            robotDBHelper.execSQL("update robot set name= '"+name.getText().toString().trim()+"' ,area= '"+areaid+"' where id= '"+ robotid +"'");
                        }else{
                            robotDBHelper.execSQL("update robot set name= '"+robotconfig.get("name").toString()+"' ,area= '"+areaid+"' where id= '"+ robotid +"'");
                        }
                    }
                finish();
                break;
            case R.id.setting_back:
                finish();
                break;
        }
    }
}
