package com.android.zbrobot.service;

import com.android.zbrobot.util.Constant;

import java.util.LinkedList;
import java.util.List;

/**
 * 作者: jiayi.zhang
 * 时间: 2017/8/29
 * 描述: 发送命令
 */

public class Protocol {
    // 以 *< 开始
    private static final String MSP_HEADER = "*>";

    // 发送的命令 测试
    public static int cdhms, gjyxms, upsend;

    // 遥控器数据
    public static int
            up_spend,// 前进速度
            up_down_direction,// 前进后退方向
            rotate_spend,//旋转速度
            rotate_direction;// 旋转方向

    // 直行命令
    public static int
            address,// 编码
            direction,// 方向
            speed,// 速度
            music,// MP3
            outime,// 超时时间
            shownumber,// 显示编号
            showcolor,// 显示颜色
            up_obstacle,// 上边障碍物
            down_obstacle,// 下边障碍物
            side_obstacle;// 侧边障碍物

    // 无轨的命令
    public static int
            coordinate_x,// X坐标
            coordinate_y,// Y坐标
            orientation,// 方向
            wait_time;//等待时间


    /*    // 有轨0 无轨1
        public static int
                rail,// 有轨
                trackless;// 无轨*/
    // 成功0 失败1
    public static int
            success, // 成功
            fail;// 失败


    // 发送的命令
    public static final int
            MN_PATTERN = 0,// 设置成磁导航模式
            MN_PATTERN_FUNCTION = 16,// 设置成脱轨运行模式
            UP_SPEED = 17,// 设置前进速度为500
            UP_STOP = 23,
            RUN_COORDINATE = 30,
            CONFIG_COORDINATE = 31,
            HEART_BEAT = 65;

    // 表示发送命令
    public static final int
            ROBOT_UP = 1,// 前进
            ROBOT_DOWN = 2,// 后退
            ROBOT_LEFT = 3,// 左转
            ROBOT_RIGHT = 4,// 右转
            ROBOT_STOP = 5,//停止
            ROBOT_LIST_UP = 10,// 直行命令
            ROBOT_LIST_DERAILMENT = 11,// 脱轨旋转
            ROBOT_LIST_WAIT = 12,// 等待退出
            ROBOT_LIST_DERAIL = 13,// 脱轨运行
            ROBOT_START = 14,// 开始发送命令集
            ROBOT_END = 15,// 命令集合发送完成
            ROBOT_CONTROL_FAULT = 19,// 清除电机故障
            ROBOT_CONTROL_CLEAR = 51,// 清除所有命令
            ROBOT_CONTROL_REMOTE = 52,// 发送遥控命令
            ROBOT_CONTROL_SPEND_UP = 53,// 前进
            ROBOT_CONTROL_ROTATE_LEFT = 54,// 左旋转
            ROBOT_CONTROL_ROTATE_RIGHT = 55,// 右旋转
            ROBOT_CONTROL_STOP = 56,// 停止遥控
            ROBOT_CONTROL_CLEAR_SPEND = 57,// 速度清0
            ROBOT_CONTROL_SPEND_DOWN = 58,// 后退
            ROBOT_HRARTBEAD = 64; //心跳
    // 表示接收命令
    public static final int
            ROBOT_RAIL_SUCCESS = 6,// 有轨返回成功
            ROBOT_TRACKLESS_SUCCESS = 7, // 无轨返回成功
            ROBOT_RAIL_FAIL = 8, // 有轨返回失败
            ROBOT_TRACKLESS_FAIL = 9;// 无轨返回失败


    // 机器人命令码
    public static final int
            START = 40,// 开始发送命令集命令码
            UP = 'u',// 前
            DOWN = 'd',// 后
            LEFT = 'l',// 左
            RIGHT = 'r',// 右
            STOP = 's',// 停
            LIST_UP = 44,// 直行命令
            LIST_DERAILMENT = 42,// 脱轨旋转
            LIST_WAIT = 41,// 等待退出
            CLEAR_FAULT = 43,// 清除电机故障
            END = 45,// 命令集合发送完成命令码
            CONTROL_CLEAR = 50,// 清除所有命令
            CONTROL_REMOTE = 60,// 发送遥控命令
            CONTROL_SPEND = 61,// 前进
            CONTROL_ROTATE = 62,// 旋转
            CONTROL_STOP = 63,// 停止遥控
            COORDINATE_RUN = 'c', //无轨目标命令
            COORDINATE_CONFIG = 'a'; //无轨返回目标

    // 接收应答  格式:*< + 数据长度 + r + 是否有轨 0 <有轨>  1 <无轨> + 成功与否 0 <成功>  1 <失败>
    public static final int
            RECEIVE = 'r';// 接收   ASCII码值为->114

    // 获取命令数据
    public static byte[] getCommandData(int cmd) {
        int[] bytes = new int[0];

        switch (cmd) {
            // 设置成磁导航模式
            case MN_PATTERN:
                //cdhms = 0;
                bytes = new int[]{01, 01};
                break;
            // 设置成脱轨运行模式
            case MN_PATTERN_FUNCTION:
                gjyxms = 275;
                bytes = new int[]{gjyxms};
                break;
            // 设置前进速度为500
            case UP_SPEED:
                upsend = 3000;
                bytes = new int[]{gjyxms, 0};
                break;
            case UP_STOP:
                bytes = new int[]{0, 0, 0};
                break;
            // 前
            case ROBOT_UP:
                break;
            // 后
            case ROBOT_DOWN:
                break;
            // 左
            case ROBOT_LEFT:
                break;
            // 右
            case ROBOT_RIGHT:
                break;
            // 停
            case ROBOT_STOP:
                break;
            // 开始
            case ROBOT_START:
                bytes = new int[]{01};
                break;
            // 结束
            case ROBOT_END:
                break;
            // 直行
            case ROBOT_LIST_UP:
                bytes = new int[]{outime, shownumber, showcolor, music, up_obstacle, down_obstacle, side_obstacle, speed, direction, address};
                break;
            // 脱轨旋转
            case ROBOT_LIST_DERAILMENT:
                bytes = new int[]{outime, shownumber, showcolor, music, up_obstacle, down_obstacle, side_obstacle, speed};
                break;
            // 等待退出
            case ROBOT_LIST_WAIT:
                bytes = new int[]{outime, shownumber, showcolor,music};
                break;
            // 脱轨运行
            case ROBOT_LIST_DERAIL:
                bytes = new int[]{outime, shownumber, showcolor, music, speed};
                break;
            // 清除电机故障
            case ROBOT_CONTROL_FAULT:
                break;
            // 清除所有命令
            case ROBOT_CONTROL_CLEAR:
                break;

            // 发送遥控命令
            case ROBOT_CONTROL_REMOTE:
                break;
            // 遥控前进方向 0->前进  1->后退
            case ROBOT_CONTROL_SPEND_UP:
                up_down_direction = 0;
                bytes = new int[]{up_spend, up_down_direction};
                break;
            // 遥控后退方向 0->前进  1->后退
            case ROBOT_CONTROL_SPEND_DOWN:
                up_down_direction = 1;
                bytes = new int[]{up_spend, up_down_direction};
                break;

            // 旋转左方向  0->左  1->右
            case ROBOT_CONTROL_ROTATE_LEFT:
                rotate_direction = 0;
                bytes = new int[]{rotate_spend, rotate_direction};
                break;
            // 旋转右方向  0->左  1->右
            case ROBOT_CONTROL_ROTATE_RIGHT:
                rotate_direction = 1;
                bytes = new int[]{rotate_spend, rotate_direction};
                break;
            // 停止遥控
            case ROBOT_CONTROL_STOP:
                break;
            // 速度清0
            case ROBOT_CONTROL_CLEAR_SPEND:
                speed = 0;
                bytes = new int[]{speed, speed};
                break;

            // 有轨 成功
            case ROBOT_RAIL_SUCCESS:
                // 是否成功 0成功 1失败
                success = 0;
                bytes = new int[]{success};
                break;

            // 有轨 失败
            case ROBOT_RAIL_FAIL:
                fail = 1;
                bytes = new int[]{fail};
                break;

            // 无轨 成功
            case ROBOT_TRACKLESS_SUCCESS:
                success = 0;
                bytes = new int[]{success};
                break;

            // 无轨 失败
            case ROBOT_TRACKLESS_FAIL:
                fail = 1;
                bytes = new int[]{fail};
                break;

            case RUN_COORDINATE:
                bytes = new int[]{coordinate_x, coordinate_y, orientation, wait_time};
                break;
            case CONFIG_COORDINATE:
                bytes = new int[]{coordinate_x, coordinate_y, orientation};
                break;
            case ROBOT_HRARTBEAD:
                //bytes = new int[]{01, 01};
                break;
        }

        return getSendCmd(bytes);
    }
    public static byte[] getCommandDataByte(int cmd,int cmdSize) {
        List<Byte> cmdData = new LinkedList<Byte>();
        switch (cmd){
            case ROBOT_START:
                //cmdData.add((byte) ((1) & 0xff));
                cmdData.add((byte) ((cmdSize) & 0xff));
                break;
        }
        // 获取数据的数量
        byte[] commandData = new byte[cmdData.size()];
        int i = 0;
        // 遍历数据
        for (byte b : cmdData) {
            commandData[i++] = b;
        }
        return commandData;

    }
    // 获取命令数据
    public static byte[] getCommandDataByte(int cmd) {
        List<Byte> cmdData = new LinkedList<Byte>();
        switch (cmd) {
            // 清除电机故障
            case ROBOT_CONTROL_FAULT:
                break;
            // 开始
            case ROBOT_START:
                cmdData.add((byte) ((1) & 0xff));
                //cmdData.add((byte) ((1) & 0xff));
                break;
            // 前进
            case ROBOT_LIST_UP:
                // 超时时间
                cmdData.add((byte) ((outime >> 8) & 0xff));
                cmdData.add((byte) ((outime) & 0xff));
                // 显示编号
//                cmdData.add((byte) ((shownumber >> 24) & 0xff));
//                cmdData.add((byte) ((shownumber >> 16) & 0xff));
//                cmdData.add((byte) ((shownumber >> 8) & 0xff));
                cmdData.add((byte) ((shownumber) & 0xff));
                // 显示颜色
                //cmdData.add((byte) ((showcolor >> 8) & 0xff));
                cmdData.add((byte) ((showcolor) & 0xff));
                // MP3
                //cmdData.add((byte) ((music >> 8) & 0xff));
                cmdData.add((byte) ((music) & 0xff));
                // 障碍物 上
                //cmdData.add((byte) ((up_obstacle >> 8) & 0xff));
                cmdData.add((byte) ((up_obstacle) & 0xff));
                 // 障碍物 下
                // cmdData.add((byte) ((down_obstacle >> 8) & 0xff));
                cmdData.add((byte) ((down_obstacle) & 0xff));
                // 障碍物 侧
                // cmdData.add((byte) ((side_obstacle >> 8) & 0xff));
                cmdData.add((byte) ((side_obstacle) & 0xff));
                // 速度
                cmdData.add((byte) ((speed >> 8) & 0xff));
                cmdData.add((byte) ((speed) & 0xff));
                // 方向
                //cmdData.add((byte) ((direction >> 8) & 0xff));
                cmdData.add((byte) ((direction) & 0xff));
                // ID编码
                cmdData.add((byte) ((address >> 24) & 0xff));
                cmdData.add((byte) ((address >> 16) & 0xff));
                cmdData.add((byte) ((address >> 8) & 0xff));
                cmdData.add((byte) ((address) & 0xff));
                break;
            // 旋转
            case ROBOT_LIST_DERAILMENT:
                cmdData.add((byte) ((outime >> 8) & 0xff));
                cmdData.add((byte) ((outime) & 0xff));

//                cmdData.add((byte) ((shownumber >> 24) & 0xff));
//                cmdData.add((byte) ((shownumber >> 16) & 0xff));
//                cmdData.add((byte) ((shownumber >> 8) & 0xff));
                cmdData.add((byte) ((shownumber) & 0xff));

                cmdData.add((byte) ((showcolor) & 0xff));

                cmdData.add((byte) ((music) & 0xff));

                // 障碍物 上
                cmdData.add((byte) ((up_obstacle) & 0xff));
                // 障碍物 下
                cmdData.add((byte) ((down_obstacle) & 0xff));
                // 障碍物 侧
                cmdData.add((byte) ((side_obstacle) & 0xff));

                cmdData.add((byte) ((speed >> 8) & 0xff));
                cmdData.add((byte) ((speed) & 0xff));
                cmdData.add((byte) ((direction) & 0xff));
                break;
            // 等待退出
            case ROBOT_LIST_WAIT:
                cmdData.add((byte) ((outime >> 8) & 0xff));
                cmdData.add((byte) ((outime) & 0xff));

//                cmdData.add((byte) ((shownumber >> 24) & 0xff));
//                cmdData.add((byte) ((shownumber >> 16) & 0xff));
//                cmdData.add((byte) ((shownumber >> 8) & 0xff));
                cmdData.add((byte) ((shownumber) & 0xff));

                cmdData.add((byte) ((showcolor) & 0xff));

                break;

            // 无轨执行命令
            case RUN_COORDINATE:
                // X坐标
                cmdData.add((byte) ((coordinate_x >> 24) & 0xff));
                cmdData.add((byte) ((coordinate_x >> 16) & 0xff));
                cmdData.add((byte) ((coordinate_x >> 8) & 0xff));
                cmdData.add((byte) ((coordinate_x) & 0xff));
                // Y坐标
                cmdData.add((byte) ((coordinate_y >> 24) & 0xff));
                cmdData.add((byte) ((coordinate_y >> 16) & 0xff));
                cmdData.add((byte) ((coordinate_y >> 8) & 0xff));
                cmdData.add((byte) ((coordinate_y) & 0xff));
                // 方向
                cmdData.add((byte) ((orientation >> 8) & 0xff));
                cmdData.add((byte) ((orientation) & 0xff));
                // 等待时间
                cmdData.add((byte) ((wait_time >> 8) & 0xff));
                cmdData.add((byte) ((wait_time) & 0xff));
                break;
            // 配置返回坐标
            case CONFIG_COORDINATE:
                // X坐标
                cmdData.add((byte) ((coordinate_x >> 24) & 0xff));
                cmdData.add((byte) ((coordinate_x >> 16) & 0xff));
                cmdData.add((byte) ((coordinate_x >> 8) & 0xff));
                cmdData.add((byte) ((coordinate_x) & 0xff));
                // Y坐标
                cmdData.add((byte) ((coordinate_y >> 24) & 0xff));
                cmdData.add((byte) ((coordinate_y >> 16) & 0xff));
                cmdData.add((byte) ((coordinate_y >> 8) & 0xff));
                cmdData.add((byte) ((coordinate_y) & 0xff));
                // 方向
                cmdData.add((byte) ((orientation >> 8) & 0xff));
                cmdData.add((byte) ((orientation) & 0xff));
                break;
            // 获取版本号
            case MN_PATTERN:
                break;
        }

        // 获取数据的数量
        byte[] commandData = new byte[cmdData.size()];
        int i = 0;
        // 遍历数据
        for (byte b : cmdData) {
            commandData[i++] = b;
        }
        return commandData;
    }


    // 获取发送数据
    public static byte[] getSendData(int cmd, byte[] data) {
        if (cmd < 0)
            return null;
        // 存储数据
        List<Byte> bf = new LinkedList<Byte>();
        // 存储头数据  *<
        for (byte c : MSP_HEADER.getBytes()) {
            bf.add(c);
        }
        // 检索总数
        byte checksum = 0;
        // byte pl_size = (byte)((payload != null ? PApplet.parseInt(payload.length) : 0)&0xFF);
        byte dataSize = (byte) ((data != null) ? (data.length) : 0);
        bf.add(dataSize);
        checksum ^= (dataSize & 0xFF);

        bf.add((byte) (cmd & 0xFF));
        checksum ^= (cmd & 0xFF);

        if (data != null) {
            for (byte c : data) {
                bf.add((byte) (c & 0xFF));
                checksum ^= (c & 0xFF);
            }
        }
        // 检索的总长度
        bf.add(checksum);

        // 遍历所有数据
        byte[] sendData = new byte[bf.size()];
        int i = 0;
        for (byte b : bf) {
            sendData[i++] = b;
        }

        return (sendData);
    }

    public static byte[] getSendCmd(int[] mData) {
        List<Byte> cmdData = new LinkedList<Byte>();
        int cmd = 0;
        for (int i = 0; i < mData.length; i++) {
            cmd = mData[i];
            if (cmd >= 0 && cmd <= 255) {
                //cmdData.add((byte) ((cmd >> 8) & 0xff));
                cmdData.add((byte) ((cmd) & 0xff));
            } else if (cmd > 255 && cmd <= 65535) {
                cmdData.add((byte) ((cmd >> 8) & 0xff));
                cmdData.add((byte) ((cmd) & 0xff));
            } else if (cmd > 65535 && cmd <= 2147483647) {
                cmdData.add((byte) ((cmd >> 24) & 0xff));
                cmdData.add((byte) ((cmd >> 16) & 0xff));
                cmdData.add((byte) ((cmd >> 8) & 0xff));
                cmdData.add((byte) ((cmd) & 0xff));
            }
        }
        // 获取数据的数量
        byte[] commandData = new byte[cmdData.size()];
        int i = 0;
        // 遍历数据
        for (byte b : cmdData) {
            commandData[i++] = b;
        }
        return commandData;
    }


}
