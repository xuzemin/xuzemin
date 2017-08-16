package com.android.jdrd.robotclient.dialog;

/*
 * Created by Administrator on 2017/2/16.
 * text for Map
 */

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jdrd.robotclient.R;
import com.android.jdrd.robotclient.activity.MainActivity;
import com.android.jdrd.robotclient.adapter.ChooseGridViewAdapter;
import com.android.jdrd.robotclient.helper.RobotDBHelper;
import com.android.jdrd.robotclient.service.ClientSocketUtil;
import com.android.jdrd.robotclient.service.ClientUdpSocketUtil;
import com.android.jdrd.robotclient.util.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义对话框
 * 2013-10-28
 * 下午12:37:43
 *  Tom.Cai
 */
public class RobotDialog extends Dialog {
    private static GridView gridView;
    public static List<Map> list,robotlist;
    private static RobotDBHelper robotDBHelper;
    private static Context context;
    public static  int CurrentIndex = -1;
    public static Thread thread = new Thread();
    public static boolean flag = false;
    public static ChooseGridViewAdapter gridViewAdapter = null;
    private TextView positiveButton,negativeButton;
    private static int deskid;
    public RobotDialog(Context context,String str,float density,int type) {
        super(context, R.style.SoundRecorder);
        setCustomDialog(context,density);
        this.context = context;
    }

    public RobotDialog(Context context,List<Map> robotlist,float density,int deskid) {
        super(context, R.style.SoundRecorder);
        setCustomDialog(context,density);
        this.context = context;
        this.deskid = deskid;
        this.robotlist = robotlist;
    }

    private void setCustomDialog(final Context context, float density) {
        Constant.debugLog("density"+density);
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_robot_dialog, null);
        gridView = (GridView) mView.findViewById(R.id.robotgirdview);
        positiveButton = (TextView) mView.findViewById(R.id.positiveButton);
        negativeButton = (TextView) mView.findViewById(R.id.negativeButton);
        list = new ArrayList<>();
        robotDBHelper = RobotDBHelper.getInstance(context);
        try {
            list = robotDBHelper.queryListMap("select * from robot " ,null);
            Log.e("Robot",list.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(list !=null && list.size()>0){
            Constant.debugLog("Robotdata_list"+list.toString());
            int size = list.size();
            int length = 76;
            int height = 137;
            int gridviewWidth = (int) (size * (length + 30) * density);
            if(gridviewWidth<=280 * density){
                gridviewWidth = (int) (280 * density);
            }
            int itemWidth = (int) (length * density);
            int itemHeight = (int) (height * density);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    gridviewWidth, itemHeight);
            Constant.linearWidth = (int) (86 * density);
            gridView.setLayoutParams(params); // 重点
            gridView.setColumnWidth(itemWidth); // 重点
            gridView.setHorizontalSpacing((int) (8 * density)); // 间距
            gridView.setStretchMode(GridView.NO_STRETCH);
            gridView.setNumColumns(size); // 重点
            gridViewAdapter = new ChooseGridViewAdapter(context,
                    list);
            gridView.setAdapter(gridViewAdapter);
            Constant.debugLog("count"+gridViewAdapter.getCount());
            Constant.debugLog("gridviewWidth"+gridviewWidth);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    gridViewAdapter.Current_Index = position;
                    Constant.debugLog("position"+position);
                    Constant.debugLog("gridViewAdapter.Current_Index"+gridViewAdapter.Current_Index);
                    gridViewAdapter.notifyDataSetChanged();
                }
            });
        }
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gridViewAdapter.Current_Index ==-1){
                    dismiss();
                    return;
                }
                if(flag){
                    int id = (int) list.get(gridViewAdapter.Current_Index).get("id");
                    ClientUdpSocketUtil.sendCommand(id,deskid);
                    dismiss();
                    gridViewAdapter.Current_Index = -1;
                }else {
                    if (list.get(gridViewAdapter.Current_Index).get("state").equals("空闲")) {
                        int id = (int) list.get(gridViewAdapter.Current_Index).get("id");
                        ClientUdpSocketUtil.sendCommand(id, deskid);
                        dismiss();
                        gridViewAdapter.Current_Index = -1;
                    } else {
                        Toast.makeText(context, "当前机器人正忙，请选择其他机器人", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        super.setContentView(mView);
    }

    public static void getData(){
        Constant.debugLog("context" + context);
        if(context != null){
            list = robotDBHelper.queryListMap("select * from robot " ,null);
            gridViewAdapter = new ChooseGridViewAdapter(context,
                    list);
            gridView.setAdapter(gridViewAdapter);
        }
    }
}