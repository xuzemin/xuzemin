package com.android.zbrobot.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.zbrobot.R;
import com.android.zbrobot.activity.ZB_MainActivity;
import com.android.zbrobot.helper.RobotDBHelper;
import com.android.zbrobot.service.Protocol;
import com.android.zbrobot.service.ServerSocketUtil;
import com.android.zbrobot.util.Constant;

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
public class ZB_RobotDialog extends Dialog {
    // 初始化数据库帮助类
    private static RobotDBHelper robotDBHelper;
    private Context context;
    // 横向加载机器人列表
    private GridView gridView;
    // 初始化适配器
    private SimpleAdapter robotAdapter;
    public static int deskid = 0, areaid = 0;
    // 存储机器人列表
    public static List<Map> list;
    private List idList = new ArrayList();
    public static List<Map> commandall;
    private static Map deskmap, areamap;
    private static List<Map> areaList;
    // 存储机器人数据列表
    private List<Map<String, Object>> robotData_list = new ArrayList<>();
    private final String[] from = {"image", "text", "name", "imageback"};
    private final int[] to = {R.id.imageview, R.id.text, R.id.name, R.id.imageback};
    // 当前下标
    public static int CurrentIndex = 0;
    // 发送数据
    private static String sendStr;

    // 字节数组发送命令
    public static byte[] data;

    private static OutputStream out;

    // IP地址
    public static String IP;
    public static int GOALID;
    public static int OUTIME;//menumber;
    public static int TURNBACK;
    public static int up_obstacle,down_obstacle,side_obstacle;

    // 创建线程
    public static Thread thread = new Thread();
    public static boolean flag, IsCoordinate = false;

    // 传输字符串
    public ZB_RobotDialog(Context context, String str) {
        super(context, R.style.SoundRecorder);
        setCustomDialog();
        this.context = context;
        this.sendStr = str;
        flag = false;
    }

    // 传输16进制
    public ZB_RobotDialog(Context context, byte[] data) {
        super(context, R.style.SoundRecorder);
        this.context = context;
        this.data = data;
        flag = false;
        setCustomDialog();

    }

    // 集合列表
    public ZB_RobotDialog(Context context) {
        super(context, R.style.SoundRecorder);
        // 初始化数据
        this.context = context;
        flag = true;
        setCustomDialog();
    }
    // id集合列表
    public ZB_RobotDialog(Context context, List idList) {
        super(context, R.style.SoundRecorder);
        // 初始化数据
        this.context = context;
        flag = true;
        this.idList = idList;
        setCustomDialog();
    }

    /**
     * 初始化
     */
    private void setCustomDialog() {
        // 加载要执行的机器人布局
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.zb_fragment_robot_dialog, null);
        // 初始化要执行的机器人列表
        gridView = (GridView) mView.findViewById(R.id.robot_girdview);
        list = new ArrayList<>();
        // 初始化数据库
        robotDBHelper = RobotDBHelper.getInstance(context);
        areaList = robotDBHelper.queryListMap("select * from area where id = '" + ZB_MainActivity.CURRENT_AREA_id + "'", null);

        try {
            // 查询机器人列表 根据区域名称 and 在线状态
            list = robotDBHelper.queryListMap("select * from robot where area = '" + ZB_MainActivity.CURRENT_AREA_id + "' and outline = '1'", null);
            // 打印log
            Constant.debugLog("要执行的机器人列表----->" + list.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (list != null && list.size() > 0) {
            if (list.size() == 1) {
                IP = list.get(0).get("ip").toString();
                GOALID = (int) list.get(0).get("goal");
                TURNBACK = (int) list.get(0).get("turnback");
                OUTIME = (int) list.get(0).get("outtime");
                if(((int) list.get(0).get("up_obstacle")==0)){
                    up_obstacle = 1;
                }else{
                    up_obstacle = 0;
                }
                if(((int) list.get(0).get("down_obstacle")==0)){
                    down_obstacle = 1;
                }else{
                    down_obstacle = 0;
                }
                if(((int) list.get(0).get("side_obstacle")==0)){
                    side_obstacle = 1;
                }else{
                    side_obstacle = 0;
                }
                int pathway = (int) list.get(0).get("pathway");
                if (pathway == 0) {
                    CurrentIndex = 0;
                    // 发送命令
                    if (idList == null || idList.size() <= 0) {
                        idList = new ArrayList();
                        idList.add(deskid);
                    }
                    sendCommandList(idList);
                } else if (pathway == 1) {
                    IsCoordinate = true;
                    CurrentIndex = 0;
                    if (idList == null || idList.size() <= 0) {
                        idList = new ArrayList();
                        idList.add(deskid);
                    }

                    List<Map> area_list = robotDBHelper.queryListMap("select * from area where id = '" + areaid + "'", null);
                    if(area_list!=null && area_list.size()>0){
                        areamap = area_list.get(0);
                    }
                    ServerSocketUtil.LsCurrent = 0;
                    Constant.debugLog("idList"+idList.toString());
                    ServerSocketUtil.sendLSList(idList);
                    //Socket通讯
//                    ServerSocketUtil.sendCommandCoordinate(IP, idList);
//                    sendCommandCoordinate();
                }
                Toast.makeText(context, "已发送指令", Toast.LENGTH_SHORT).show();
            } else {
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
                    map.put("id", list.get(i).get("id"));
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
                robotAdapter = new SimpleAdapter(getContext(), robotData_list, R.layout.zb_robot_grid_item, from, to);
                // 加载适配器
                gridView.setAdapter(robotAdapter);
                // 要执行的机器人子项列表
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        IP = robotData_list.get(position).get("ip").toString();
                        List<Map> robotList = robotDBHelper.queryListMap("select * from robot where id = '" + robotData_list.get(position).get("id") + "'", null);
                        Map map1 = robotList.get(0);
                        GOALID = (int) map1.get("goal");
                        TURNBACK = (int) map1.get("turnback");
                        OUTIME = (int) map1.get("outtime");
                        if(((int) list.get(0).get("up_obstacle")==0)){
                            up_obstacle = 1;
                        }else{
                            up_obstacle = 0;
                        }
                        if(((int) list.get(0).get("down_obstacle")==0)){
                            down_obstacle = 1;
                        }else{
                            down_obstacle = 0;
                        }
                        if(((int) list.get(0).get("side_obstacle")==0)){
                            side_obstacle = 1;
                        }else{
                            side_obstacle = 0;
                        }
                        int pathway = (int) robotData_list.get(position).get("pathway");
                        if (pathway == 0) {
                            if (flag) {
                                CurrentIndex = 0;
                                // 发送命令
                                if (idList == null || idList.size() <= 0) {
                                    idList = new ArrayList();
                                    idList.add(deskid);
                                }
                                sendCommandList(idList);
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
                            if(idList==null && idList.size()<=0){
                                idList = new ArrayList();
                                idList.add(deskid);
                            }
                            ServerSocketUtil.LsCurrent = 0;
                            ServerSocketUtil.sendLSList(idList);
                            Constant.debugLog("idList"+idList.toString());
//                            ServerSocketUtil.sendCommandCoordinate(IP,idList);
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

    // 发送命令列表
    public static void sendCommandList(final List isList) {
        // 打印log
        Constant.debugLog("isList" + isList.toString());
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
//                                    // 清除所有命令集
                                    data = Protocol.getSendData(Protocol.CONTROL_CLEAR, Protocol.getCommandData(Protocol.ROBOT_CONTROL_CLEAR));
                                    setSendStr(out, data);
                                    //setThread(thread);

                                    commandall = new ArrayList<>();
                                    Constant.debugLog("isList"+isList.size());
                                    for (int i = 0, idSize = isList.size(); i < idSize; i++) {
                                        List<Map> robotList = robotDBHelper.queryListMap("select * from command where desk = '" + isList.get(i) + "'", null);
                                        commandall.addAll(robotList);
                                    }
                                    Constant.debugLog("commandall" + commandall.size());
                                    if(TURNBACK == 1){
                                        data = Protocol.getSendData(Protocol.START, Protocol.getCommandDataByte(Protocol.ROBOT_START, commandall.size()+3));
                                    }else{
                                        data = Protocol.getSendData(Protocol.START, Protocol.getCommandDataByte(Protocol.ROBOT_START, commandall.size()+1));
                                    }
                                    setSendStr(out, data);
                                    //setThread(thread);
                                }
                                for (int size = commandall.size(); CurrentIndex < size; CurrentIndex++) {
                                    Constant.debugLog("robotList" + size + "" + CurrentIndex);
                                    switch ((int) commandall.get(CurrentIndex).get("type")) {
                                        // 直行
                                        case 0:
                                            // 查询系统卡 根据ID查询
                                            Constant.debugLog("直行"+CurrentIndex);
                                            List<Map> card_list = robotDBHelper.queryListMap("select * from card where id = '" + commandall.get(CurrentIndex).get("goal") + "'", null);
                                            if (card_list != null && card_list.size() > 0) {
                                                Protocol.address = (int) card_list.get(0).get("address");
                                                Protocol.direction = (int) commandall.get(CurrentIndex).get("direction");
                                                Protocol.speed = (int) commandall.get(CurrentIndex).get("speed");
                                                Protocol.music = (int) commandall.get(CurrentIndex).get("music");
                                                Protocol.outime = (int) commandall.get(CurrentIndex).get("outime");
                                                Protocol.shownumber = (int) commandall.get(CurrentIndex).get("shownumber");
                                                Protocol.showcolor = (int) commandall.get(CurrentIndex).get("showcolor");
                                                if(((int)commandall.get(CurrentIndex).get("up_obstacle")) == 0){
                                                    Protocol.up_obstacle = 1;
                                                }else{
                                                    Protocol.up_obstacle = 0;
                                                }
                                                //Protocol.up_obstacle = (((int) robotList.get(CurrentIndex).get("up_obstacle"))==0);
                                                if(((int)commandall.get(CurrentIndex).get("down_obstacle")) == 0){
                                                    Protocol.down_obstacle = 1;
                                                }else{
                                                    Protocol.down_obstacle = 0;
                                                }
                                                //Protocol.down_obstacle = (int) robotList.get(CurrentIndex).get("down_obstacle");
                                                if(((int)commandall.get(CurrentIndex).get("side_obstacle")) == 0){
                                                    Protocol.side_obstacle = 1;
                                                }else{
                                                    Protocol.side_obstacle = 0;
                                                }

                                                //Protocol.side_obstacle = (int) robotList.get(CurrentIndex).get("side_obstacle");
                                                // 解析
                                                data = Protocol.getSendData(Protocol.LIST_UP, Protocol.getCommandDataByte(Protocol.ROBOT_LIST_UP));
                                                //data = Protocol.getSendData(Protocol.LIST_UP, Protocol.getCommandData(Protocol.ROBOT_LIST_UP));
                                                // 发送 data[]
                                                setSendStr(out, data);
                                                //setThread(thread);
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
                                            Protocol.speed = (int) commandall.get(CurrentIndex).get("speed");
                                            Protocol.direction = (int) commandall.get(CurrentIndex).get("direction");
                                            Protocol.music = (int) commandall.get(CurrentIndex).get("music");
                                            Protocol.outime = (int) commandall.get(CurrentIndex).get("outime");
                                            Protocol.shownumber = (int) commandall.get(CurrentIndex).get("shownumber");
                                            Protocol.showcolor = (int) commandall.get(CurrentIndex).get("showcolor");
                                            //Protocol.up_obstacle = (int) robotList.get(CurrentIndex).get("up_obstacle");
                                            //Protocol.down_obstacle = (int) robotList.get(CurrentIndex).get("down_obstacle");
                                            //Protocol.side_obstacle = (int) robotList.get(CurrentIndex).get("side_obstacle");
                                            if(((int)commandall.get(CurrentIndex).get("up_obstacle")) == 0){
                                                Protocol.up_obstacle = 1;
                                            }else{
                                                Protocol.up_obstacle = 0;
                                            }
                                            //Protocol.up_obstacle = (((int) robotList.get(CurrentIndex).get("up_obstacle"))==0);
                                            if(((int)commandall.get(CurrentIndex).get("down_obstacle")) == 0){
                                                Protocol.down_obstacle = 1;
                                            }else{
                                                Protocol.down_obstacle = 0;
                                            }
                                            //Protocol.down_obstacle = (int) robotList.get(CurrentIndex).get("down_obstacle");
                                            if(((int)commandall.get(CurrentIndex).get("side_obstacle")) == 0){
                                                Protocol.side_obstacle = 1;
                                            }else{
                                                Protocol.side_obstacle = 0;
                                            }
                                            // 解析
                                            data = Protocol.getSendData(Protocol.LIST_DERAILMENT, Protocol.getCommandDataByte(Protocol.ROBOT_LIST_DERAILMENT));
                                            // data = Protocol.getSendData(Protocol.LIST_DERAILMENT, Protocol.getCommandData(Protocol.ROBOT_LIST_DERAILMENT));
                                            // 发送 data[]
                                            setSendStr(out, data);
                                            //setThread(thread);
                                            break;

                                        // 等待退出
                                        case 3:
                                            Protocol.music = (int) commandall.get(CurrentIndex).get("music");

                                            Protocol.outime = (int) commandall.get(CurrentIndex).get("outime");

                                            Protocol.shownumber = (int) commandall.get(CurrentIndex).get("shownumber");
                                            Protocol.showcolor = (int) commandall.get(CurrentIndex).get("showcolor");
                                            // 解析
                                            data = Protocol.getSendData(Protocol.LIST_WAIT, Protocol.getCommandDataByte(Protocol.ROBOT_LIST_WAIT));
                                            //data = Protocol.getSendData(Protocol.LIST_WAIT, Protocol.getCommandData(Protocol.ROBOT_LIST_WAIT));
                                            // 发送 data[]
                                            setSendStr(out, data);
                                            //setThread(thread);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        CurrentIndex = 0;
                        Constant.debugLog("旋转end");
                        if(((int)areaList.get(0).get("moredesk")) == 1) {
                            if (TURNBACK == 1) {
                                Protocol.speed = 300;
                                Protocol.direction = 0;
                                Protocol.music = 1;
                                Protocol.outime = OUTIME;
                                Protocol.shownumber = 0;
                                Protocol.showcolor = 0;
                                Protocol.up_obstacle = up_obstacle;//(int) robotList.get(CurrentIndex).get("up_obstacle");
                                Protocol.down_obstacle = down_obstacle;//(int) robotList.get(CurrentIndex).get("down_obstacle");
                                Protocol.side_obstacle = side_obstacle;//(int) robotList.get(CurrentIndex).get("side_obstacle");
                                data = Protocol.getSendData(Protocol.LIST_DERAILMENT, Protocol.getCommandDataByte(Protocol.ROBOT_LIST_DERAILMENT));
                                setSendStr(out, data);
                                //setThread(thread);
                            }
                        }
                        Constant.debugLog("直行end");
                        List<Map> card_list = robotDBHelper.queryListMap("select * from card where id = '" + GOALID + "'", null);
                        if (card_list != null && card_list.size() > 0) {
                            Protocol.address = (int) card_list.get(0).get("address");
                            Protocol.direction = 0;//(int) robotList.get(CurrentIndex).get("direction");
                            Protocol.speed = 1200;//(int) robotList.get(CurrentIndex).get("speed");
                            Protocol.music = 0;//(int) robotList.get(CurrentIndex).get("music");
                            Protocol.outime = 6000;//(int) robotList.get(CurrentIndex).get("outime");
                            Protocol.shownumber = 0;//(int) robotList.get(CurrentIndex).get("shownumber");
                            Protocol.showcolor = 0;//(int) robotList.get(CurrentIndex).get("showcolor");
                            Protocol.up_obstacle = 1;
                            Protocol.down_obstacle = 1;
                            Protocol.side_obstacle = 1;
                            data = Protocol.getSendData(Protocol.LIST_UP, Protocol.getCommandDataByte(Protocol.ROBOT_LIST_UP));
                            setSendStr(out, data);
                            //setThread(thread);down_obstacle
                        }

                        Constant.debugLog("旋转end");
                        if(TURNBACK == 1){
                            Protocol.speed = 300;
                            Protocol.direction = 1;
                            Protocol.music = 1;
                            Protocol.outime = OUTIME;
                            Protocol.shownumber = 0;
                            Protocol.showcolor = 0;
                            Protocol.up_obstacle = up_obstacle;//(int) robotList.get(CurrentIndex).get("up_obstacle");
                            Protocol.down_obstacle = down_obstacle;//(int) robotList.get(CurrentIndex).get("down_obstacle");
                            Protocol.side_obstacle = side_obstacle;//(int) robotList.get(CurrentIndex).get("side_obstacle");
                            data = Protocol.getSendData(Protocol.LIST_DERAILMENT, Protocol.getCommandDataByte(Protocol.ROBOT_LIST_DERAILMENT));
                            setSendStr(out, data);
                            //setThread(thread);
                        }

                        // 命令发送完成
                        data = Protocol.getSendData(Protocol.END, Protocol.getCommandData(Protocol.ROBOT_END));
                        setSendStr(out, data);
                        //setThread(thread);


/*                       // 清除电机故障
                        data = Protocol.getSendData(Protocol.CLEAR_FAULT, Protocol.getCommandDataByte(Protocol.ROBOT_CONTROL_FAULT));
                        setSendStr(out, data);
                        setThread(thread);*/

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
                Constant.debugLog(data.length+"");
                Thread.sleep(100);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
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