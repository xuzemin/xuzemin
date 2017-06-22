package com.android.jdrd.robot.dialog;

/*
 * Created by Administrator on 2017/2/16.
 * text for Map
 */

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.activity.MainActivity;
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
    private static List<Map> list,robotlist;
    private static RobotDBHelper robotDBHelper;
    private List<Map<String, Object>> Robotdata_list =  new ArrayList<>();
    private final String [] from ={"image","text"};
    private final int [] to = {R.id.image,R.id.text};
    private Context context;
    private static String sendstr;
    private static String IP;
    public static Thread thread = new Thread();
    public static boolean flag;
    public RobotDialog(Context context,String str) {
        super(context, R.style.MyDialog);
        setCustomDialog();
        this.context = context;
        this.sendstr = str;
        flag = false;
    }

    public RobotDialog(Context context,List<Map> robotlist) {
        super(context, R.style.MyDialog);
        setCustomDialog();
        this.context = context;
        this.robotlist = robotlist;
        flag = true;
    }

    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_robot_dialog, null);
        gridView = (GridView) mView.findViewById(R.id.robot_girdview);
        list = new ArrayList<>();
        robotDBHelper = RobotDBHelper.getInstance(context);
        try {
            list = robotDBHelper.queryListMap("select * from robot where area = '"+ MainActivity.CURRENT_AREA_id+"' and outline = '1'" ,null);
            Log.e("Robot",list.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        if(list !=null && list.size()>0){
            int i =0;
            int j = list.size();
            Map<String, Object> map ;
            while(i<j){
                map = new HashMap<>();
                map.put("image", R.mipmap.zuo_xs);
                map.put("text",list.get(i).get("name"));
                map.put("ip",list.get(i).get("ip"));
                Robotdata_list.add(map);
                i++;
            }
            Constant.debugLog(Robotdata_list.toString());
            robotadapter = new SimpleAdapter(getContext(), Robotdata_list, R.layout.item, from, to);
            gridView.setAdapter(robotadapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    IP = Robotdata_list.get(position).get("ip").toString();
                    if(flag){
                        sendCommandList();
                    }else {
                        sendCommand();
                    }
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
                                for(int i =0,size = robotlist.size();i<size;i++){
                                    switch ((int)robotlist.get(i).get("type")){
                                        case 0:
                                            sendstr="*g+"+robotlist.get(i).get("goal")+"+"+robotlist.get(i).get("direction")+"+"+robotlist.get(i).get("speed")
                                                    +"+"+robotlist.get(i).get("music")+"+"+robotlist.get(i).get("outime")+"+"
                                                    +robotlist.get(i).get("shownumber")+"+"+robotlist.get(i).get("showcolor");
                                            break;
                                        case 1:
                                            sendstr="*d";
                                            break;
                                        case 2:
                                            sendstr="*r";
                                            break;
                                        case 3:
                                            sendstr="*w";
                                            break;
                                    }
                                    if(sendstr.length() >= 6){
                                        sendstr = sendstr + "+"+(sendstr.length()+5)+"+#";
                                    }else{
                                        sendstr = sendstr + "+"+(sendstr.length()+4)+"+#";
                                    }
                                    out.write(sendstr.getBytes());
                                    Constant.debugLog("length"+sendstr.length());
                                    synchronized (thread){
                                        thread.wait();
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

}