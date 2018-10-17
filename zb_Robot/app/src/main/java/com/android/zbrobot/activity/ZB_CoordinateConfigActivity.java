package com.android.zbrobot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.zbrobot.R;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.util.Constant;
import com.android.zbrobot.view.CoordinateView;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by SJX on 2017/9/5.
 */

public class ZB_CoordinateConfigActivity extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    public static EditText pointx,pointy,derection,waittime;
    private int deskId,areaId;
    public static ToggleButton map;
    public static int heightPixels,widthPixels;
    private Map deskinfo,areainfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        //初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        Intent intent = getIntent();
        deskId = intent.getIntExtra("desk", 0);
        areaId = intent.getIntExtra("area", 0);
        CoordinateView.areaid = areaId;

        DisplayMetrics metrics = new DisplayMetrics();
        // 获取Manager对象后获取Display对象，然后调用getMetrics()方法
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // 从DisplayMetrics对象中获取宽、高
        heightPixels = metrics.heightPixels;
        widthPixels = metrics.widthPixels;

        setContentView(R.layout.zb_activity_coordinate);
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.btn_sure).setOnClickListener(this);
        pointx = (EditText) findViewById(R.id.pointx);
        pointy = (EditText) findViewById(R.id.pointy);
        derection = (EditText) findViewById(R.id.derection);
        waittime = (EditText) findViewById(R.id.waittime);
        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.left).setOnClickListener(this);
        findViewById(R.id.right).setOnClickListener(this);
        findViewById(R.id.map).setOnClickListener(this);
        findViewById(R.id.text_toast).setOnClickListener(this);
        map = (ToggleButton) findViewById(R.id.map);
        map.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    CoordinateView.isWrite = true;
                }else{
                    CoordinateView.isWrite = false;
                }
            }
        });
        setTextWatcher();
        CoordinateView.isWrite = false;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CoordinateView.isWrite = false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        List<Map> deskList = robotDBHelper.queryListMap("select * from desk where id = '" + deskId + "'", null);
        if(deskList !=null && deskList.size() > 0){
            deskinfo = deskList.get(0);
        }
        if(deskinfo.get("pointx") != null){
            pointx.setText(deskinfo.get("pointx").toString());
        }
        if(deskinfo.get("pointy") != null){
            pointy.setText(deskinfo.get("pointy").toString());
        }
        if(deskinfo.get("derection") != null){
            derection.setText(deskinfo.get("derection").toString());
        }
        if(deskinfo.get("waittime") != null){
            waittime.setText(deskinfo.get("waittime").toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.text_toast:
                if(map.isChecked()){
                    map.setChecked(false);
                }else{
                    map.setChecked(true);
                }
                break;
            case R.id.setting_back:
                finish();
                break;
            case R.id.btn_sure:
                if(!pointx.getText().equals("") && !pointy.getText().equals("") &&!derection.getText().equals("")
                        &&!waittime.getText().equals("")) {
                    robotDBHelper.execSQL("update desk set pointx = '"+pointx.getText().toString().trim() +
                            "',pointy = '"+pointy.getText().toString().trim()+"',derection = '" +
                            derection.getText().toString().trim() + "',waittime = '"+
                            waittime.getText().toString().trim()+"' where id = '"+deskId+"'");
                    finish();
                    Toast.makeText(getApplicationContext(),"更新成功",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),"请输入坐标",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.up:
                derection.setText("90");
                break;
            case R.id.down:
                derection.setText("270");
                break;
            case R.id.right:
                derection.setText("0");
                break;
            case R.id.left:
                derection.setText("180");
                break;
//            case R.id.map:
//                CoordinateView.isWrite = true;
//                break;
            default:
                break;

        }
    }
    public void setTextWatcher(){
        pointx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                if (s.toString().contains(".")) {
//                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
//                        s = s.toString().subSequence(0,
//                                s.toString().indexOf(".") + 3);
//                        pointx.setText(s);
//                        pointx.setSelection(s.length());
//                    }
//                }
//                if (s.toString().trim().substring(0).equals(".")) {
//                    s = "0" + s;
//                    pointx.setText(s);
//                    pointx.setSelection(2);
//                }
//
//                if (s.toString().startsWith("0")
//                        && s.toString().trim().length() > 1) {
//                    if (!s.toString().substring(1, 2).equals(".")) {
//                        pointx.setText(s.subSequence(0, 1));
//                        pointx.setSelection(1);
//                        return;
//                    }
//                }

                if(!pointx.getText().toString().equals("") ){
                    CoordinateView.point_x = (Float.valueOf(pointx.getText().toString())+
                            CoordinateView.initial_x) * 20 * CoordinateView.scale  ;
                    Log.e("logutil",CoordinateView.point_x+"x");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pointy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().contains(".")) {
                    if (s.length() - 1 - s.toString().indexOf(".") > 2) {
                        s = s.toString().subSequence(0,
                                s.toString().indexOf(".") + 3);
                        pointy.setText(s);
                        pointy.setSelection(s.length());
                    }
                }
                if (s.toString().trim().substring(0).equals(".")) {
                    s = "0" + s;
                    pointy.setText(s);
                    pointy.setSelection(2);
                }

                if (s.toString().startsWith("0")
                        && s.toString().trim().length() > 1) {
                    if (!s.toString().substring(1, 2).equals(".")) {
                        pointy.setText(s.subSequence(0, 1));
                        pointy.setSelection(1);
                        return;
                    }
                }

                if(!pointy.getText().toString().equals("") ) {
                    CoordinateView.point_y = ((Float.valueOf(pointy.getText().toString()) / -1 -
                            CoordinateView.initial_y) * 20 + Constant.Bitmap_HEIGHT) * CoordinateView.scale ;
                    Log.e("logutil",CoordinateView.point_y+"y");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
