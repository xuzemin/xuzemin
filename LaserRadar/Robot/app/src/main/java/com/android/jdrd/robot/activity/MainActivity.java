package com.android.jdrd.robot.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.service.ServerSocketUtil;
import com.android.jdrd.robot.service.SetStaticIPService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Animation.AnimationListener {

    private RobotDBHelper robotDBHelper;
    private Button selectBtn;
    private Button insertBtn;
    private Button updateBtn;
    private Button deleteBtn;
    private TextView contentTv;
    private List<Map> tableList;
    private LinearLayout linearlayout_all;
    private RelativeLayout map_right_Ralative;
    private ImageView imgViewmapnRight;
    private GridView gview;
    private List<Map<String, Object>> data_list =  new ArrayList<>();
    private SimpleAdapter sim_adapter;
    private static boolean IsEdit = false;
    private boolean IsRight = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent SetStaticIPService = new Intent(this, SetStaticIPService.class);
        startService(SetStaticIPService);

        //启动后台通讯服务
        Intent serverSocket = new Intent(this, ServerSocketUtil.class);
        startService(serverSocket);

        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());

        linearlayout_all = (LinearLayout) findViewById(R.id.linearlayout_all);
        imgViewmapnRight = (ImageView) findViewById(R.id.imgViewmapnRight);
        map_right_Ralative = (RelativeLayout) findViewById(R.id.map_right_Ralative);
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
        imgViewmapnRight.setOnClickListener(this);
        gview = (GridView) findViewById(R.id.gview);
        //获取数据
        String [] from ={"image","text"};
        int [] to = {R.id.image,R.id.text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from, to);
        //配置适配器
        gview.setAdapter(sim_adapter);
        gview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!IsEdit){
                    if(position == gview.getCount()-1){
                        Intent intent = new Intent(MainActivity.this, DeskConfigPathAcitivty.class);
                        startActivity(intent);// 启动新的 Activity
                    }else {
                        Intent intent = new Intent(MainActivity.this, DeskConfigPathAcitivty.class);
                        intent.putExtra("id", (Integer) data_list.get(position).get("id"));
                        startActivity(intent);// 启动新的 Activity
                    }
                }else {
                    Toast.makeText(getApplicationContext(),data_list.get(position).get("text").toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
        sim_adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        List<Map> list;
        switch (v.getId()){
            case R.id.imgViewmapnRight:
                startAnimationRight();
                break;
            case R.id.select_btn:
                getData();
                break;
            case R.id.insert_btn:
                robotDBHelper.insert("area",new String[]{"name"},new Object[]{"qiangyu"});
                getData();
                break;
            case R.id.update_btn:
                robotDBHelper.update("area",new String[]{"name"},new Object[]{"yangqiangyu"},
                        new String[]{"name"},new String[]{"qiangyu"});
                getData();
                break;
            case R.id.delete_btn:
                robotDBHelper.delete("area","name",
                        "qiangyu");
                getData();
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
        data_list.clear();
        try {
            tableList = robotDBHelper.queryListMap("select * from area",null);
            Log.e("Robot",tableList.toString());
        }catch (Exception e){
            e.printStackTrace();
            return getData();
        }
        if(tableList !=null && tableList.size() >0){
            for(int i=0 ,size = tableList.size();i<size;i++){
                Map<String, Object> map = new HashMap<>();
                map.put("image", R.mipmap.ic_launcher);
                map.put("id", tableList.get(i).get("id"));
                map.put("text", tableList.get(i).get("name"));
                data_list.add(map);
            }
        }
        if(!IsEdit){
            Map<String, Object> map = new HashMap<>();
            map.put("image", R.mipmap.bg);
            map.put("id", 0);
            map.put("text",getString(R.string.config_add));
            data_list.add(map);
        }

        sim_adapter.notifyDataSetChanged();
        return data_list;
    }

    //右边动画
    private void startAnimationRight(){
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
}
