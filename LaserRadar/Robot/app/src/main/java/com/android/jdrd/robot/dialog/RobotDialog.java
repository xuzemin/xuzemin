package com.android.jdrd.robot.dialog;

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

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.activity.MainActivity;
import com.android.jdrd.robot.adapter.ChooseGridViewAdapter;
import com.android.jdrd.robot.adapter.GridViewAdapter;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.service.ServerSocketUtil;
import com.android.jdrd.robot.util.Constant;
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
    private GridView gridView;
    private SimpleAdapter robotadapter;
    public static List<Map> list,robotlist;
    private static RobotDBHelper robotDBHelper;
    private List<Map> Robotdata_list =  new ArrayList<>();
    private final String [] from ={"image","text","name","imageback"};
    private final int [] to = {R.id.imageview,R.id.text,R.id.name,R.id.imageback};
    private Context context;
    public static  int CurrentIndex = -1;
    private static String sendstr;
    public static String IP;
    public static Thread thread = new Thread();
    public static boolean flag;
    private ChooseGridViewAdapter gridViewAdapter;
    private TextView positiveButton,negativeButton;
    private static float density;
    public RobotDialog(Context context,String str,float density) {
        super(context, R.style.SoundRecorder);
        setCustomDialog(context,density);
        this.context = context;
        this.sendstr = str;
        this.density = density;
        flag = false;
    }

    public RobotDialog(Context context,List<Map> robotlist,float density) {
        super(context, R.style.SoundRecorder);
        setCustomDialog(context,density);
        this.context = context;
        this.robotlist = robotlist;
        flag = true;
    }

    private void setCustomDialog(Context context,float density) {

        Constant.debugLog("density"+density);

        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_robot_dialog, null);
        gridView = (GridView) mView.findViewById(R.id.robotgirdview);
        positiveButton = (TextView) mView.findViewById(R.id.positiveButton);
        negativeButton = (TextView) mView.findViewById(R.id.negativeButton);
        list = new ArrayList<>();
        robotDBHelper = RobotDBHelper.getInstance(context);
        try {
            list = robotDBHelper.queryListMap("select * from robot where area = '"+ MainActivity.CURRENT_AREA_id+"' and outline = '1'" ,null);
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
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IP = list.get(gridViewAdapter.Current_Index).get("ip").toString();
                    if(flag){
                        CurrentIndex = -1;
                        sendCommandList();
                        dismiss();
                    }else {
                        sendCommand();
                        dismiss();
                    }
                    gridViewAdapter.Current_Index = -1;
                }
            });
            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        dismiss();
                }
            });
        }

        super.setContentView(mView);
    }
    public static void sendCommand(){
        for(Map map: ServerSocketUtil.socketlist){
            if(map.get("ip").equals(IP)){
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (out != null) {
                            try {
                                out.write(sendstr.getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
            }
        }
    }

    public static void sendCommandList(){
        Constant.debugLog(robotlist.toString());
        for(Map map: ServerSocketUtil.socketlist){
            if(map.get("ip").equals(IP)){
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (out != null) {
                            try {
                                if(CurrentIndex == -1){
                                    out.write("*s+6+#".getBytes());
                                    synchronized (thread){
                                        thread.wait();
                                    }
                                }
                                Constant.debugLog("CurrentIndex"+CurrentIndex);
                                int size;
                                for(size = robotlist.size();CurrentIndex<size;CurrentIndex++){
                                    switch ((int)robotlist.get(CurrentIndex).get("type")){
                                        case 0:
                                            List<Map> card_list = robotDBHelper.queryListMap("select * from card where id = '"+robotlist.get(CurrentIndex).get("goal")+"'" ,null);
                                            if(card_list !=null && card_list.size()>0){
                                                sendstr="*g+"+card_list.get(0).get("address")+"+"+robotlist.get(CurrentIndex).get("direction")+"+"+robotlist.get(CurrentIndex).get("speed")
                                                        +"+"+robotlist.get(CurrentIndex).get("music")+"+"+robotlist.get(CurrentIndex).get("outime")+"+"
                                                        +robotlist.get(CurrentIndex).get("shownumber")+"+"+robotlist.get(CurrentIndex).get("showcolor");
                                            }
                                            setSendstr(out,sendstr);
                                            synchronized (thread){
                                                thread.wait();
                                            }
                                            break;
                                        case 1:
                                            sendstr="*d+"+robotlist.get(CurrentIndex).get("speed")
                                                    +"+"+robotlist.get(CurrentIndex).get("music")+"+"+robotlist.get(CurrentIndex).get("outime")+"+"
                                                    +robotlist.get(CurrentIndex).get("shownumber")+"+"+robotlist.get(CurrentIndex).get("showcolor");
                                            setSendstr(out,sendstr);
                                            synchronized (thread){
                                                thread.wait();
                                            }
                                            break;
                                        case 2:
                                            sendstr="*r+"+robotlist.get(CurrentIndex).get("speed")
                                                    +"+"+robotlist.get(CurrentIndex).get("music")+"+"+robotlist.get(CurrentIndex).get("outime")+"+"
                                                    +robotlist.get(CurrentIndex).get("shownumber")+"+"+robotlist.get(CurrentIndex).get("showcolor");
                                            setSendstr(out,sendstr);
                                            synchronized (thread){
                                                thread.wait();
                                            }
                                            break;
                                        case 3:
                                            sendstr="*w+"+robotlist.get(CurrentIndex).get("music")+"+"+robotlist.get(CurrentIndex).get("outime")+"+"
                                                    +robotlist.get(CurrentIndex).get("shownumber")+"+"+robotlist.get(CurrentIndex).get("showcolor");
                                            setSendstr(out,sendstr);
                                            synchronized (thread){
                                                thread.wait();
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                thread.start();
            }
        }
    }

    private static void setSendstr(OutputStream out,String str){
        if(str.length() >= 6){
            str = str + "+"+(str.length()+5)+"+#";
        }else{
            str = str + "+"+(str.length()+4)+"+#";
        }
        try {
            out.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}