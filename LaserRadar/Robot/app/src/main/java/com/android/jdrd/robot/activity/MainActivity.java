package com.android.jdrd.robot.activity;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private RobotDBHelper robotDBHelper;
    private Button selectBtn;
    private Button insertBtn;
    private Button updateBtn;
    private Button deleteBtn;
    private TextView contentTv;
    private List<Map> tableList;

    private GridView gview;
    private List<Map<String, Object>> data_list;
    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = { R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,R.mipmap.ic_launcher,};
    private String[] iconName = { "通讯录", "日历", "照相机", "时钟", "游戏", "短信", "铃声",
            "设置", "语音", "天气", "浏览器", "视频" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        selectBtn = (Button) findViewById(R.id.select_btn);
        insertBtn = (Button) findViewById(R.id.insert_btn);
        updateBtn = (Button) findViewById(R.id.update_btn);
        deleteBtn = (Button) findViewById(R.id.delete_btn);
        contentTv = (TextView) findViewById(R.id.content_tv);
        findViewById(R.id.insert_table_btn).setOnClickListener(this);
        findViewById(R.id.select_table_btn).setOnClickListener(this);
        selectBtn.setOnClickListener(this);
        insertBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        gview = (GridView) findViewById(R.id.gview);
        //新建List
        data_list = new ArrayList<>();
        //获取数据
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getData();
//        //新建适配器
//        String [] from ={"image","text"};
//        int [] to = {R.id.image,R.id.text};
//        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from, to);
//        //配置适配器
//        gview.setAdapter(sim_adapter);
    }

    @Override
    public void onClick(View v) {
        List<Map> list;
        switch (v.getId()){
            case R.id.select_btn:
                data_list.clear();
//                list = robotDBHelper.queryListMap("select * from area",null);
//                contentTv.setText(String.valueOf(list));
                getData();
                //新建适配器
                String [] from ={"image","text"};
                int [] to = {R.id.image,R.id.text};
                sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from, to);
                //配置适配器
                gview.setAdapter(sim_adapter);
                break;
            case R.id.insert_btn:
                robotDBHelper.insert("area",new String[]{"name"},new Object[]{"qiangyu"});
                break;
            case R.id.update_btn:
                robotDBHelper.update("area",new String[]{"name"},new Object[]{"yangqiangyu"},
                        new String[]{"name"},new String[]{"qiangyu"});
                break;
            case R.id.delete_btn:
                robotDBHelper.delete("area",
                        new String[]{"name"},new String[]{"qiangyu"});
                break;
            case R.id.select_table_btn:
                list = robotDBHelper.queryListMap("select * from desk",null);
                contentTv.setText(String.valueOf(list));
                break;
            case R.id.insert_table_btn:
                robotDBHelper.insert("desk",new String[]{"name","area"},new Object[]{"qiangyu",1});
                break;
        }
    }

    public List<Map<String, Object>> getData(){
        try {
            tableList = robotDBHelper.queryListMap("select * from area",null);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"数据库未初始化",Toast.LENGTH_SHORT).show();
        }
        if(tableList !=null && tableList.size() >0){
            for(int i=0 ,size = tableList.size();i<size;i++){
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", icon[i]);
                map.put("text", tableList.get(i));
                data_list.add(map);
            }
        }else{
            robotDBHelper.insert("area",new String[]{"name"},new Object[]{"qiangyu"});
            robotDBHelper.insert("area",new String[]{"name"},new Object[]{"qiangyu"});
            robotDBHelper.insert("area",new String[]{"name"},new Object[]{"qiangyu"});
            robotDBHelper.insert("area",new String[]{"name"},new Object[]{"qiangyu"});
            robotDBHelper.insert("area",new String[]{"name"},new Object[]{"qiangyu"});

        }

        return data_list;
    }
}
