package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class DeskConfigPathAcitivty extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int deskid,areaid;
    private EditText name;
    private Map deskconfiglist;
    private ListView commandlistview;
    private List<Map> command_list = new ArrayList<>();
    private boolean IsADD = false;
    private MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_config);

        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        name = (EditText) findViewById(R.id.deskname);
        findViewById(R.id.change_name).setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.straight).setOnClickListener(this);
        findViewById(R.id.derail).setOnClickListener(this);
        findViewById(R.id.rotato).setOnClickListener(this);
        findViewById(R.id.wait).setOnClickListener(this);
        findViewById(R.id.puthook).setOnClickListener(this);
        findViewById(R.id.lockhook).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.card).setOnClickListener(this);

        commandlistview = (ListView) findViewById(R.id.added_command);

        Intent intent =getIntent();// 收取 email
        deskid = intent.getIntExtra("id",0);
        areaid = intent.getIntExtra("area",0);

        if(deskid == 0){
            findViewById(R.id.btn_delete).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.title)).setText(R.string.desk_add);
            IsADD = true;
        }else{
            ((TextView)findViewById(R.id.title)).setText(R.string.desk_settings);
            IsADD = false;
            List<Map> desklist = robotDBHelper.queryListMap("select * from desk where id = '"+ deskid +"'" ,null);
            deskconfiglist = desklist.get(0);
            name.setHint(deskconfiglist.get("name").toString());
            ((TextView)findViewById(R.id.title)).setText(R.string.desk_deract);
        }

        myAdapter = new MyAdapter(this,command_list);
        commandlistview.setAdapter(myAdapter);
        commandlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DeskConfigPathAcitivty.this, CommandAcitivty.class);
                intent.putExtra("command_id", command_list.get(position).get("id").toString());
                startActivity(intent);
            }
        });

    }

    public void refreshCommand(){
        command_list.clear();
        List<Map> list = robotDBHelper.queryListMap("select * from command where desk = '"+ deskid +"'" ,null);
        command_list.addAll(list);
        setListViewHeightBasedOnChildren(commandlistview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCommand();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_delete:
                robotDBHelper.execSQL("delete from desk where id= '"+ deskid +"'");
                finish();
                break;
            case R.id.card:
                startActivity(new Intent(DeskConfigPathAcitivty.this,CardConfig.class));
            break;
            case R.id.setting_back:
                finish();
                break;
            case R.id.change_name:
                if(name.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"名称不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    if(IsADD){
                        robotDBHelper.execSQL("insert into desk (name,area) values ('"+name.getText().toString()+"','"+areaid+"')");
                        List<Map> desklist = robotDBHelper.queryListMap("select * from desk where area = '"+areaid+"'" ,null);
                        deskid = (int) desklist.get(desklist.size()-1).get("id");
                        desklist = robotDBHelper.queryListMap("select * from desk where id = '"+ deskid +"'" ,null);
                        deskconfiglist = desklist.get(0);
                        ((TextView)findViewById(R.id.title)).setText(R.string.desk_deract);
                        IsADD = false;
                        Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_SHORT).show();
                    }else{
                        robotDBHelper.execSQL("update desk set name= '"+name.getText().toString().trim()+"' where id= '"+ deskid +"'");
                        Toast.makeText(getApplicationContext(),"更新成功",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.straight:
                if(!IsADD){
                    robotDBHelper.execSQL("insert into command (type,desk) values ('0','"+deskid+"')");
                }
                refreshCommand();
                break;
            case R.id.derail:
                if(!IsADD){
                    robotDBHelper.execSQL("insert into command (type,desk) values ('1','"+deskid+"')");
                }
                refreshCommand();
                break;
            case R.id.rotato:
                if(!IsADD){
                    robotDBHelper.execSQL("insert into command (type,desk) values ('2','"+deskid+"')");
                }
                refreshCommand();
                break;
            case R.id.wait:
                if(!IsADD){
                    robotDBHelper.execSQL("insert into command (type,desk) values ('3','"+deskid+"')");
                }
                refreshCommand();
                break;
            case R.id.puthook:
                if(!IsADD){
                    robotDBHelper.execSQL("insert into command (type,desk) values ('4','"+deskid+"')");
                }
                refreshCommand();
                break;
            case R.id.lockhook:
                if(!IsADD){
                    robotDBHelper.execSQL("insert into command (type,desk) values ('5','"+deskid+"')");
                }
                refreshCommand();
                break;
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        MyAdapter listAdapter = (MyAdapter) commandlistview.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
