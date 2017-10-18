package com.android.jdrd.robot.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.jdrd.robot.R;
import com.android.jdrd.robot.activity.SJX_MainActivity;
import com.android.jdrd.robot.helper.RobotDBHelper;
import com.android.jdrd.robot.service.Protocol;
import com.android.jdrd.robot.service.ServerSocketUtil;
import com.android.jdrd.robot.util.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/8
 * 描述: 自定义机器人运行轨迹对话框
 */
public class SJX_RobotDialog extends Dialog {
    // 初始化数据库帮助类
    private static RobotDBHelper robotDBHelper;
    private Context context;
    // 横向加载机器人列表
    private GridView gridView;
    // 初始化适配器
    private SimpleAdapter robotAdapter;
    public static int deskid = 0,areaid = 0;
    // 存储机器人列表
    public static List<Map> list;
    public static List<Map> robotList;
    private static Map deskmap,areamap;
    // 存储机器人数据列表
    private List<Map<String, Object>> robotData_list = new ArrayList<>();

    private final String[] from = {"image", "text", "name", "imageback"};
    private final int[] to = {R.id.imageview, R.id.text, R.id.name, R.id.imageback};
    // 当前下标
    public static int CurrentIndex = -1;
    // 发送数据
    private static String sendStr;

    // 字节数组发送命令
    public static byte[] data;

    private static OutputStream out;

    // IP地址
    public static String IP;
    // 创建线程
    public static Thread thread = new Thread();
    public static boolean flag,IsCoordinate = false;

    // 传输字符串
    public SJX_RobotDialog(Context context, String str) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
        this.context = context;
        this.sendStr = str;
        flag = false;
    }

    // 传输16进制
    public SJX_RobotDialog(Context context, byte[] data) {
        super(context, R.style.SoundRecorder);
        this.context = context;
        this.data = data;
        flag = false;
        setCustomDialog();

    }

    // 集合列表
    public SJX_RobotDialog(Context context, List<Map> robotList) {
        super(context, R.style.SoundRecorder);
        // 初始化数据
        this.context = context;
        this.robotList = robotList;
        flag = true;
        setCustomDialog();
    }

    /**
     * 初始化
     */
    private void setCustomDialog() {
        // 加载要执行的机器人布局
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.sjx_fragment_robot_dialog, null);
        // 初始化要执行的机器人列表
        gridView = (GridView) mView.findViewById(R.id.robot_girdview);
        list = new ArrayList<>();
        // 初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(context);
        try {
            // 查询机器人列表 根据区域名称 and 在线状态
            list = robotDBHelper.queryListMap("select * from robot where area = '" + SJX_MainActivity.CURRENT_AREA_id + "' and outline = '1'", null);
            // 打印log
            Constant.debugLog("要执行的机器人列表----->" + list.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            if(list.size() == 1){
                IP = list.get(0).get("ip").toString();
                int pathway = (int) list.get(0).get("pathway");
                if (pathway == 0) {
                    List<Map> commandList = robotDBHelper.queryListMap("select * from command where desk = '" + deskid + "'", null);
                    if (commandList != null && commandList.size() > 0) {
                        if (flag) {
                            CurrentIndex = 0;
                            // 发送命令
                            sendCommandList();
//                        // 销毁当前Dialog
                        } else {
//                        // 发送字符串命令
//                        //sendCommand();
//                        // 发送字节数组命令
//                        btSendBytes();
                        }
                    }
                } else if (pathway == 1) {
                    IsCoordinate = true;
                    CurrentIndex = 0;
                    List list = new ArrayList();
                    list.add(deskid);
                    ServerSocketUtil.sendCommandCoordinate(IP,list);
//                    sendCommandCoordinate();
                }
                Toast.makeText(context,"已发送指令",Toast.LENGTH_SHORT).show();
            }else {
                int i = 0;
                int j = list.size();
                Map<String, Object> map;
                while (i < j) {
                    // 以键值对的形式存储数据
                    map = new HashMap<>();
                    map.put("image", R.mipmap.zaixian);
                    map.put("name", list.get(i).get("name"));
                    map.put("ip", list.get(i).get("ip"));
                    map.put("pathway", list.get(i).get("pathway"));
                    // 机器人状态 0->空闲  1->送餐  2->故障
                    switch ((int) list.get(i).get("robotstate")) {
                        case 0:
                            map.put("text", "空闲");
                            map.put("imageback", R.mipmap.kongxian);
                            break;
                        case 1:
                            map.put("text", "送餐");
                            map.put("imageback", R.mipmap.fuwuzhong);
                            break;
                        case 2:
                            map.put("text", "故障");
                            map.put("imageback", R.mipmap.guzhang);
                            break;
                    }
                    robotData_list.add(map);
                    i++;
                }
                // 打印log
                Constant.debugLog(robotData_list.toString());
                // 简单的适配器   没有自定义  调用系统提供的适配器
                robotAdapter = new SimpleAdapter(getContext(), robotData_list, R.layout.sjx_robot_grid_item, from, to);
                // 加载适配器
                gridView.setAdapter(robotAdapter);
                // 要执行的机器人子项列表
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        IP = robotData_list.get(position).get("ip").toString();
                        int pathway = (int) robotData_list.get(position).get("pathway");
                        if (pathway == 0) {
                            if (flag) {
                                CurrentIndex = 0;
                                // 发送命令
                                sendCommandList();
                                // 销毁当前Dialog
                                dismiss();
                            } else {
                                // 发送字符串命令
                                //sendCommand();
                                // 发送字节数组命令
                                btSendBytes();
                                // 销毁当前Dialog
                                dismiss();
                            }
                        } else if (pathway == 1) {
                            IsCoordinate = true;
                            CurrentIndex = 0;
                            List list = new ArrayList();
                            list.add(deskid);
                            ServerSocketUtil.sendCommandCoordinate(IP,list);
//                            sendCommandCoordinate();
                            dismiss();
                        }
                    }
                });
            }
        }else{
            Toast.makeText(context,"当前区域没有可操控机器人",Toast.LENGTH_SHORT).show();
        }
        super.setContentView(mView);
        if(list != null && list.size() > 0 && list.size() != 1){
            show();
        }
    }



    //发送命令
    public static void sendCommand() {
        for (Map map : ServerSocketUtil.socketList) {
            if (map.get("ip").equals(IP)) {
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (out != null) {
                            try {
                                out.write(sendStr.getBytes());
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

    /**
     * 发送字节数组
     */
    public void btSendBytes() {
        for (Map map : ServerSocketUtil.socketList) {
            if (map.get("ip").equals(IP)) {
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (out != null) {
                            try {
                                out.write(data);
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
//    public static void sendCommandCoordinate(){
//        for (Map map : ServerSocketUtil.socketList) {
//            // 检查IP是否相同
//            if (map.get("ip").equals(IP)) {
//                out = (OutputStream) map.get("out");
//                sendlist(out);
//            }
//        }
//    }

    public static void sendlist(final OutputStream out){
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(out!=null){
                    try {
                        for(CurrentIndex = 1 ;CurrentIndex < 3 ;CurrentIndex++) {
                            switch (CurrentIndex) {
                                case 0:
                                    out.write(Protocol.getSendData(Protocol.START, Protocol.getCommandData(Protocol.ROBOT_START)));
                                    synchronized (thread) {
                                        thread.wait();
                                    }
                                    break;
                                case 1:
                                    List<Map> desk_list = robotDBHelper.queryListMap("select * from desk where id = '" + deskid + "'", null);
                                    if (desk_list != null && desk_list.size() > 0) {
                                        deskmap = desk_list.get(0);
                                        Constant.debugLog("areamap Protocol.coordinate_x" +Protocol.coordinate_x +"Protocol.coordinate_y"+
                                                Protocol.coordinate_y+"Protocol.orientation"+Protocol.orientation);
                                        if (deskmap.get("pointx") != null && deskmap.get("pointy") != null && deskmap.get("derection") != null
                                                && deskmap.get("waittime") != null) {
                                            areaid = (int) deskmap.get("area");
                                            Protocol.coordinate_x = (int)(Float.valueOf(deskmap.get("pointx").toString().trim()) * 100);
                                            Protocol.coordinate_y = (int)(Float.valueOf(deskmap.get("pointy").toString().trim()) * 100);
                                            Protocol.orientation = 450 - Integer.valueOf(deskmap.get("derection").toString().trim());
                                            Protocol.wait_time = Integer.valueOf(deskmap.get("waittime").toString().trim());
                                            if(Protocol.orientation>360){
                                                Protocol.orientation -= 360;
                                            }
                                            out.write(Protocol.getSendData(Protocol.COORDINATE_RUN, Protocol.getCommandDataByte(Protocol.RUN_COORDINATE)));
                                        }
                                        Constant.debugLog("areamap Protocol.coordinate_x" +Protocol.coordinate_x +"Protocol.coordinate_y"+
                                                Protocol.coordinate_y+"Protocol.orientation"+Protocol.orientation);
                                    }
                                    synchronized (thread) {
                                        thread.wait();
                                    }
                                    break;
                                case 2:
                                    Constant.debugLog("areaid"+areaid);
                                    List<Map> area_list = robotDBHelper.queryListMap("select * from area where id = '" + areaid + "'", null);
                                    Constant.debugLog("area_list" + area_list.toString());
                                    if (area_list != null && area_list.size() > 0) {
                                        areamap = area_list.get(0);
                                        Constant.debugLog("areamap" + areamap.toString());
                                        if (areamap.get("point_x_back") != null && areamap.get("point_y_back") != null && areamap.get("derection") != null) {
                                            Protocol.coordinate_x = Integer.valueOf(areamap.get("point_x_back").toString().trim()) * 100;
                                            Protocol.coordinate_y = Integer.valueOf(areamap.get("point_y_back").toString().trim()) * 100;
                                            Protocol.orientation = 450 - Integer.valueOf(areamap.get("derection").toString().trim());
                                            if(Protocol.orientation>360){
                                                Protocol.orientation -= 360;
                                            }
                                            out.write(Protocol.getSendData(Protocol.COORDINATE_CONFIG, Protocol.getCommandDataByte(Protocol.CONFIG_COORDINATE)));
                                        }
                                    }
                                    Constant.debugLog("areamap Protocol.coordinate_x" +Protocol.coordinate_x +"Protocol.coordinate_y"+
                                            Protocol.coordinate_y+"Protocol.orientation"+Protocol.orientation);
                                    synchronized (thread) {
                                        thread.wait();
                                    }
                                    break;
                                case 3:
                                    // 命令发送完成
                                    data = Protocol.getSendData(Protocol.END, Protocol.getCommandData(Protocol.ROBOT_END));
                                    setSendStr(out, data);
                                    synchronized (thread) {
                                        thread.wait();
                                    }
                                    IsCoordinate = false;
                                    break;
                                default:
                                    break;
                            }
                        }
                    }catch (Exception e){
                        Constant.debugLog(e.toString());
                    }
                }
            }
        });
        thread.start();
    }

    // 发送命令列表
    public static void sendCommandList() {
        // 打印log
        Constant.debugLog(robotList.toString());

        for (Map map : ServerSocketUtil.socketList) {
            // 检查IP是否相同
            if (map.get("ip").equals(IP)) {
                final OutputStream out = (OutputStream) map.get("out");
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (out != null) {
                            try {
                                if (CurrentIndex == 0) {
                                    // 开始发送命令集
                                    out.write(Protocol.getSendData(Protocol.START, Protocol.getCommandData(Protocol.ROBOT_START)));
                                    setThread(thread);
                                }
                                Constant.debugLog("当前下标CurrentIndex----->" + CurrentIndex);
                                int size;
                                for (size = robotList.size(); CurrentIndex < size; CurrentIndex++) {
                                    switch ((int) robotList.get(CurrentIndex).get("type")) {
                                        // 直行
                                        case 0:
                                            // 查询系统卡 根据ID查询
                                            List<Map> card_list = robotDBHelper.queryListMap("select * from card where id = '" + robotList.get(CurrentIndex).get("goal") + "'", null);
                                            if (card_list != null && card_list.size() > 0) {
                                                Protocol.address = (int) card_list.get(0).get("address");
                                                Protocol.direction = (int) robotList.get(CurrentIndex).get("direction");
                                                Protocol.speed = (int) robotList.get(CurrentIndex).get("speed");
                                                Protocol.music = (int) robotList.get(CurrentIndex).get("music");
                                                Protocol.outime = (int) robotList.get(CurrentIndex).get("outime");
                                                Protocol.shownumber = (int) robotList.get(CurrentIndex).get("shownumber");
                                                Protocol.showcolor = (int) robotList.get(CurrentIndex).get("showcolor");
                                                Protocol.up_obstacle = (int) robotList.get(CurrentIndex).get("up_obstacle");
                                                Protocol.down_obstacle = (int) robotList.get(CurrentIndex).get("down_obstacle");
                                                Protocol.side_obstacle = (int) robotList.get(CurrentIndex).get("side_obstacle");
                                                // 解析
                                                data = Protocol.getSendData(Protocol.LIST_UP, Protocol.getCommandDataByte(Protocol.ROBOT_LIST_UP));
                                                //data = Protocol.getSendData(Protocol.LIST_UP, Protocol.getCommandData(Protocol.ROBOT_LIST_UP));
                                                // 发送 data[]
                                                setSendStr(out, data);
                                            }
                                            break;
                                        // 脱轨运行
                                        case 1:
/*                                            Protocol.speed = (int) robotList.get(CurrentIndex).get("speed");
                                            Protocol.music = (int) robotList.get(CurrentIndex).get("music");
                                            Protocol.outime = (int) robotList.get(CurrentIndex).get("outime");
                                            Protocol.shownumber = (int) robotList.get(CurrentIndex).get("shownumber");
                                            Protocol.showcolor = (int) robotList.get(CurrentIndex).get("showcolor");
                                            data = Protocol.getSendData(Protocol.LIST_DERAIL, Protocol.getCommandData(Protocol.ROBOT_LIST_DERAIL));*/
/*                                            synchronized (thread) {
                                                thread.wait();
                                            }*/
                                            break;

                                        // 脱轨旋转
                                        case 2:
                                            Protocol.speed = (int) robotList.get(CurrentIndex).get("speed");
                                            Protocol.music = (int) robotList.get(CurrentIndex).get("music");
                                            Protocol.outime = (int) robotList.get(CurrentIndex).get("outime");
                                            Protocol.shownumber = (int) robotList.get(CurrentIndex).get("shownumber");
                                            Protocol.showcolor = (int) robotList.get(CurrentIndex).get("showcolor");
                                            Protocol.up_obstacle = (int) robotList.get(CurrentIndex).get("up_obstacle");
                                            Protocol.down_obstacle = (int) robotList.get(CurrentIndex).get("down_obstacle");
                                            Protocol.side_obstacle = (int) robotList.get(CurrentIndex).get("side_obstacle");
                                            // 解析
                                            data = Protocol.getSendData(Protocol.LIST_DERAILMENT, Protocol.getCommandData(Protocol.ROBOT_LIST_DERAILMENT));
                                            // 发送 data[]
                                            setSendStr(out, data);
                                            setThread(thread);
                                            break;

                                        // 等待退出
                                        case 3:
                                            //Protocol.music = (int) robotList.get(CurrentIndex).get("music");
                                            Protocol.outime = (int) robotList.get(CurrentIndex).get("outime");
                                            Protocol.shownumber = (int) robotList.get(CurrentIndex).get("shownumber");
                                            Protocol.showcolor = (int) robotList.get(CurrentIndex).get("showcolor");
                                            // 解析
                                            data = Protocol.getSendData(Protocol.LIST_WAIT, Protocol.getCommandData(Protocol.ROBOT_LIST_WAIT));
                                            // 发送 data[]
                                            setSendStr(out, data);
                                            setThread(thread);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        // 命令发送完成
                        data = Protocol.getSendData(Protocol.END, Protocol.getCommandData(Protocol.ROBOT_END));
                        setSendStr(out, data);
                        // 清除电机故障
                        data = Protocol.getSendData(Protocol.CLEAR_FAULT, Protocol.getCommandDataByte(Protocol.ROBOT_CONTROL_FAULT));
                        setSendStr(out, data);
                    }
                });
                thread.start();
            }
        }
    }

    /**
     * 发送命令格式
     *
     * @param out 输出流
     * @param str 发送内容
     */
    private static void setSendStr(OutputStream out, String str) {
        if (str.length() >= 6) {
            str = str + "+" + (str.length() + 5) + "+#";
        } else {
            str = str + "+" + (str.length() + 4) + "+#";
        }
        try {
            out.write(str.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     *
     * @param out  写入
     * @param data 数据内容
     */
    private static void setSendStr(OutputStream out, byte[] data) {
        if (out != null && data != null) {
            try {
                out.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 等待
     *
     * @param thread
     */
    private static void setThread(Thread thread) {
        synchronized (thread) {
            try {
                thread.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}