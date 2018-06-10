package com.android.zbrobot.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.zbrobot.R;
import com.android.zbrobot.dialog.ZB_MyDialog;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.util.Constant;
import com.android.zbrobot.view.CoordinateView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by SJX on 2017/9/13.
 */

public class ZB_AreaRedact extends Activity implements View.OnClickListener {
    private RobotDBHelper robotDBHelper;
    private int areaId;
    private TextView pointx,pointy,derection,areaname,point_x_area,point_y_area,derection_area;
    private Button btn_delete;
    private Map areainfo;
    private File file;
    private Button btn_sure;
    private Bitmap bitmap;
    private ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sjx_activity_arearedact);
        robotDBHelper = RobotDBHelper.getInstance(getApplicationContext());
        Intent intent = getIntent();
        areaId = intent.getIntExtra("area", 0);

        areaname = (TextView) findViewById(R.id.areaname);
        areaname.setOnClickListener(this);
        pointx = (TextView) findViewById(R.id.pointx);
        pointx.setOnClickListener(this);
        pointy = (TextView) findViewById(R.id.pointy);
        pointy.setOnClickListener(this);
//        derection = (EditText) findViewById(R.id.derection);
        point_x_area =  (TextView) findViewById(R.id.pointx_area);
        point_x_area.setOnClickListener(this);
        point_y_area =  (TextView) findViewById(R.id.pointy_area);
        point_y_area.setOnClickListener(this);
        derection_area =  (TextView) findViewById(R.id.derection_area);
        derection_area.setOnClickListener(this);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(this);
        findViewById(R.id.setting_back).setOnClickListener(this);
        findViewById(R.id.image).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        imageView = (ImageView) findViewById(R.id.bitmap);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
        getImage();
    }

    public void refresh(){
        if(areaId!=0){
            List<Map> areaList = robotDBHelper.queryListMap("select * from area where id = '" + areaId + "'", null);
            if(areaList !=null && areaList.size() > 0){
                areainfo = areaList.get(0);
            }
            if(areainfo.get("name") != null){
                areaname.setText(areainfo.get("name").toString());
            }
            if(areainfo.get("pointx") != null){
                pointx.setText(areainfo.get("pointx").toString());
            }
            if(areainfo.get("pointy") != null){
                pointy.setText(areainfo.get("pointy").toString());
            }
//            if(areainfo.get("derection") != null){
//                derection.setText(areainfo.get("derection").toString());
//            }
            if(areainfo.get("point_x_back") != null){
                point_x_area.setText(areainfo.get("point_x_back").toString());
            }
            if(areainfo.get("point_y_back") != null){
                point_y_area.setText(areainfo.get("point_y_back").toString());
            }
            if(areainfo.get("derection") != null){
                derection_area.setText(areainfo.get("derection").toString());
            }
        }else{
            btn_delete.setVisibility(View.GONE);
        }
    }

    public void getImage(){
        imageView.setVisibility(View.GONE);
        if(areaId !=0){
            File appDir = new File(Environment.getExternalStorageDirectory(), Constant.DIR_NAME);
            if(appDir.exists()){
                String fileName = areaId + ".png";
                // 查询机器人列表
                file = new File(appDir, fileName);
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap = BitmapFactory.decodeStream(fis);
                if(bitmap!=null){
                    imageView.setImageBitmap(bitmap);
                    imageView.setVisibility(View.VISIBLE);
                }else{
                }
            }else{
            }
        }else{
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pointx:
                dialog_Text(1);
                 break;
            case R.id.pointy:
                dialog_Text(2);
                break;
            case R.id.areaname:
                dialog_Text(0);
                break;
            case R.id.pointx_area:
                dialog_Text(3);
                break;
            case R.id.pointy_area:
                dialog_Text(4);
                break;
            case R.id.derection_area:
                dialog_Text(5);
                break;
            case R.id.setting_back:
                finish();
                break;
            case R.id.back:
                finish();
                break;
            case R.id.image:
                if(areaId != 0){
                    Intent intent = new Intent(ZB_AreaRedact.this,ZB_ImageChoose.class);
                    intent.putExtra("area",areaId);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(),"请先创建区域",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_sure:
                if(!areaname.getText().toString().equals("") ) {
                    if (areaId == 0) {
                        if(!pointx.getText().toString().equals("") &&!pointy.getText().toString().equals("")
                                &&!point_x_area.getText().toString().equals("") &&!point_y_area.getText().toString().equals("")
                                &&!derection_area.getText().toString().equals("")){
                            robotDBHelper.insert("area", new String[]{"name", "pointx", "pointy","point_x_back","point_y_back","derection"}, new Object[]{
                                    areaname.getText().toString().trim(), pointx.getText().toString().trim(),
                                    pointy.getText().toString().trim(),point_x_area.getText().toString().trim(),
                                    point_y_area.getText().toString().trim(),derection_area.getText().toString().trim()});
                        }else{
//                            robotDBHelper.insert("area", new String[]{"name"}, new Object[]{
//                                    areaname.getText().toString().trim(), });
//                            Toast.makeText(getApplicationContext(),"请输入地图无轨坐标"，)
                            robotDBHelper.insert("area", new String[]{"name", "pointx", "pointy","point_x_back","point_y_back","derection"}, new Object[]{
                                    areaname.getText().toString().trim(), 0,
                                    0,0,0,0});
                        }
                        List<Map> areaList = robotDBHelper.queryListMap("select * from area ", null);
                        areainfo = areaList.get(areaList.size() - 1);
                        areaId = (int) areainfo.get("id");
                        Toast.makeText(getApplicationContext(),"创建区域成功",Toast.LENGTH_SHORT).show();
                    } else {
                        if(!pointx.getText().toString().equals("") &&!pointy.getText().toString().equals("")
                                &&!point_x_area.getText().toString().equals("") &&!point_y_area.getText().toString().equals("")
                                &&!derection_area.getText().toString().equals("")){
                            robotDBHelper.execSQL("update area set name= '" + areaname.getText().toString().trim()
                                    + "',pointx = '" + pointx.getText().toString().trim() + "',pointy = '" +
                                    pointy.getText().toString().trim() + "',point_x_back = '" +
                                    point_x_area.getText().toString().trim() +"',point_y_back = '" +
                                    point_y_area.getText().toString().trim() +"',derection = '" +
                                    derection_area.getText().toString().trim() +"' where id= '" + areaId + "'");
                        }else{
                            robotDBHelper.execSQL("update area set name= '" + areaname.getText().toString().trim()
                                    + "' where id= '" + areaId + "'");
                        }
                        Toast.makeText(getApplicationContext(),"更新区域成功",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"请填写区域名称",Toast.LENGTH_SHORT).show();
                }
                refresh();
                getImage();
                break;
            case R.id.btn_delete:
                robotDBHelper.execSQL("delete from area where id= '" + areaId + "'");
                if(file.exists()){
                    file.delete();
                }
                finish();
                break;
            default:
                break;
        }
    }

    private ZB_MyDialog textDialog;
    private EditText editText;

    private void dialog_Text(final int type) {
        textDialog = new ZB_MyDialog(this);
        editText = (EditText) textDialog.getEditText();
        // 输入类型为数字文本
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        switch (type) {
            case 0:
                if(areaId == 0){
                    textDialog.getTitle().setText("新增区域");
                    textDialog.getTitleTemp().setText("请输入区域名");
                }else{
                    textDialog.getTitle().setText("修改区域");
                    textDialog.getTitleTemp().setText("请输入区域名");
                }
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
            case 1:
                textDialog.getTitle().setText("初始化横坐标修改");
                textDialog.getTitleTemp().setText("请输入横坐标值");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case 2:
                textDialog.getTitle().setText("初始化纵坐标修改");
                textDialog.getTitleTemp().setText("请输入纵坐标值");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case 3:
                textDialog.getTitle().setText("返回横坐标修改");
                textDialog.getTitleTemp().setText("请输入返回横坐标值");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case 4:
                textDialog.getTitle().setText("返回纵坐标修改");
                textDialog.getTitleTemp().setText("请输入返回纵坐标值");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                break;
            case 5:
                textDialog.getTitle().setText("返回点方向修改");
                textDialog.getTitleTemp().setText("请输入返回点方向值");
                break;
        }
        // 确定Dialog
        textDialog.setOnPositiveListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 修改数据库 type=  0->修改速度  1->修改MP3通道  2->修改超时时间  3->修改显示编号  4->修改显示颜色
                switch (type) {
                    case 0:
                        if(areaId !=0){
                            if (!editText.getText().toString().trim().equals("")) {
                                robotDBHelper.execSQL("update area set name = '" + editText.getText().toString().trim() + "' where id= '" + areaId + "'");
                                textDialog.dismiss();
                                areaname.setText(editText.getText().toString().trim());
                            } else {
                                Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                            }
                        }else{

                            if (!editText.getText().toString().trim().equals("")) {
                                robotDBHelper.insert("area", new String[]{"name", "pointx", "pointy","point_x_back","point_y_back","derection"}, new Object[]{
                                        editText.getText().toString().trim(), 0,
                                        0,0,0,0});
                                textDialog.dismiss();
                                areaname.setText(editText.getText().toString().trim());
                                List<Map> areaList = robotDBHelper.queryListMap("select * from area ", null);
                                areainfo = areaList.get(areaList.size() - 1);
                                areaId = (int) areainfo.get("id");
                            } else {
                                Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                            }
                            refresh();
                        }

                        break;
                    // 修改横坐标
                    case 1:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update area set pointx = '" + editText.getText().toString().trim() + "' where id= '" + areaId + "'");
                            textDialog.dismiss();
                            pointx.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    // 修改超时时间
                    case 2:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update area set pointy = '" + editText.getText().toString().trim() + "' where id= '" + areaId + "'");
                            textDialog.dismiss();
                            pointy.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    // 修改显示编号
                    case 3:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update area set point_x_back = '" + editText.getText().toString().trim() + "' where id= '" + areaId + "'");
                            textDialog.dismiss();
                            point_x_area.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    // 修改显示颜色
                    case 4:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update area set point_y_back = '" + editText.getText().toString().trim() + "' where id= '" + areaId + "'");
                            textDialog.dismiss();
                            point_y_area.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case 5:
                        if (!editText.getText().toString().trim().equals("")) {
                            robotDBHelper.execSQL("update area set derection = '" + editText.getText().toString().trim() + "' where id= '" + areaId + "'");
                            textDialog.dismiss();
                            derection_area.setText(editText.getText().toString().trim());
                        } else {
                            Toast.makeText(getApplicationContext(), "请输入参数", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        // 取消Dialog
        textDialog.setOnNegativeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 销毁窗体
                textDialog.dismiss();
            }
        });
        // 显示Dialog
        textDialog.show();
    }
}
