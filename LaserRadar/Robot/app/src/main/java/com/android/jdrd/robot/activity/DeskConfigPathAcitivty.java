package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.util.Constant;

import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/13.
 *
 */

public class DeskConfigPathAcitivty extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int deskid,areaid;
    private EditText name;
    private boolean IsADD = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_config);

        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        Intent intent =getIntent();// 收取 email
        int deskid = intent.getIntExtra("id",0);
        int areaid = intent.getIntExtra("area",0);

        if(deskid == 0){
            IsADD = true;
        }else
        {IsADD = false;}

        name = (EditText) findViewById(R.id.name);
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.change_name).setOnClickListener(this);
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
            case R.id.change_name:
                if(name.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"名称不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if(IsADD){
                        robotDBHelper.insert("desk",new String[]{"name","area"},new Object[]{name.getText().toString().trim(),"'"+areaid+"'"});
                        List<Map> desklist = robotDBHelper.queryListMap("select * from desk where area = '"+areaid+"'" ,null);
                        Constant.debugLog("desklist"+desklist.toString());
                        deskid = (int) desklist.get(desklist.size()-1).get("id");
                        IsADD = false;
                        Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                    }else{
                        robotDBHelper.execSQL("update desk set name= '"+name.getText().toString().trim()+"' where id= '"+ deskid +"'");
                        Toast.makeText(getApplicationContext(),"更新成功",Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
    }
}
