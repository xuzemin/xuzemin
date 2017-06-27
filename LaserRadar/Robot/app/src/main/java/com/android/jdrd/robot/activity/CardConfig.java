package com.android.jdrd.robot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.adapter.CardAdapter;
import com.android.jdrd.robot.adapter.MyAdapter;
import com.android.jdrd.robot.helper.RobotDBHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jdrd on 2017/6/27.
 */

public class CardConfig extends Activity implements View.OnClickListener {
    private CardAdapter myAdapter;
    private ListView cardlist;
    private RobotDBHelper robotDBHelper;
    private List<Map> card_list = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_card);
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        findViewById(R.id.setting_back).setOnClickListener(this);
        cardlist = (ListView) findViewById(R.id.cardlist);

        card_list = robotDBHelper.queryListMap("select * from card " ,null);
        myAdapter = new CardAdapter(this,card_list);
        cardlist.setAdapter(myAdapter);
        cardlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CardConfig.this, CommandAcitivty.class);
                intent.putExtra("command_id", card_list.get(position).get("id").toString());
                startActivity(intent);
            }
        });
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
