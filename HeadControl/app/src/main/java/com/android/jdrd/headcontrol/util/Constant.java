package com.android.jdrd.headcontrol.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import com.android.jdrd.headcontrol.dialog.CustomDialog;
import com.android.jdrd.headcontrol.service.ServerSocketUtil;
import com.google.gson.Gson;
import com.jiadu.mapdemo.util.SerialPortUtil;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Constant {

    //wifi静态ip参数
    static final String dns1 = "192.168.1.1";
    static final String dns2 = "192.168.0.1";
    static String gateway = "";
    static int prefix = 24;
    public static final int SCALE_NUMBER = 90;
    static String IP = "";
    static final String IPLast = "106";
    static String isConnectSocket = "";
    public static final String wifiname = "TimeBox_8b319a";
    public static final String password = "12345678";
//    public static final String wifiname = "HUAWEI-GDRD";
//    public static final String password = "88391477";
    //测试用WI-FI
//    public static final String wifiname = "GDRD-3F";
//    public static final String password = "88391477";
//    public static final String Reason = "reason";
    public static double Current_x = 0;
    public static double Current_y = 0;
    public static float Current_degree = 90;
    public static final int ServerPort = 12345;
    public static final String ip_bigScreen = "/192.168.1.102";
    public static final String ip_ros = "/192.168.1.100";
    public static float Scale = 1 ;
    private static final boolean isDebug = true;
    private static final String TAG = "HeadControl";
    public static final String filePath = "data/data/com.android.jdrd.headcontrol/cache/map.xml";
    public static final String Type = "type";
    public static final String Function = "function";
    public static final String Data = "data";
    public static final String Command = "command";
    public static final String State = "state";
    public static final String Walk = "walk";
//    public static final String Navigation = "navigation";
    public static final String Peoplesearch = "peoplesearch";
    public static final String Turn = "turn";
    public static final String StopSearch = "stop";
    public static final String Result = "result";
//    public static final String Distance = "distance";
//    public static final String Degree = "degree";
    public static final String Camera = "3dcamera";
    private static Constant constant;
    private static SerialPortUtil spu  = SerialPortUtil.getInstance();

    public static Constant getConstant(){
        if(constant != null){
            return constant;
        }else {
            constant = new Constant();
            return constant;
        }
    }

    public static void debugLog(String string){
        if(isDebug){
            Log.e(TAG,string);
        }
    }
    public void showWarntext(Context context, final Handler handler){
        CustomDialog dialog = new CustomDialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.builder.setTitle("提醒")
                .setMessage("是否离开并删除路线规划")
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        handler.sendEmptyMessage(1);
                    }
                }).setNegativeButton("", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                handler.sendEmptyMessage(2);
            }
        }).create().show();
    }
    public  void showWarn(Context context, final Handler handler){
        CustomDialog dialog = new CustomDialog(context);
        dialog.builder.setTitle("提醒")
                .setMessage("正在执行路线,点击确定按钮停止")
                .setPositiveButton("", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        handler.sendEmptyMessage(3);
                    }
                }).create().show();
    }
    public void sendCamera(Float scope,Context context){
        Intent intent = new Intent();
        if(scope == 0){
            intent.putExtra("msg", "远");
        }else if(scope == 1){
            intent.putExtra("msg", "中");
        }else if(scope == 2){
            intent.putExtra("msg", "近");
        }else{
            intent.putExtra("msg", "关闭");
        }
        intent.setAction("com.jdrd.CursorSDKExample.TD_CAMERA");
        context.sendBroadcast(intent);
    }

    public void sendBundle(String type,String function,Map data){
        try {
            Gson gson = new Gson();
            Map map = new LinkedHashMap();
            map.put("type", type);
            map.put("function", function);
            map.put("data", data);
            String s = gson.toJson(map);
            ServerSocketUtil.sendDateToClient(s);
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getDegree(){
        Current_degree = spu.getBean().pose[2];
    }

    public void sendBundle(String type,String function,String data){
        try {
            Gson gson = new Gson();
            Map map = new LinkedHashMap();
            map.put(Constant.Type, type);
            map.put(Constant.Function, function);
            map.put(Constant.Data, data);
            String s = gson.toJson(map);
            ServerSocketUtil.sendDateToClient(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendBundle(String type,String function,Float data){
        try {
            Gson gson = new Gson();
            Map map = new LinkedHashMap();
            map.put(Constant.Type, type);
            map.put(Constant.Function, function);
            map.put(Constant.Data, data);
            String s = gson.toJson(map);
            ServerSocketUtil.sendDateToClient(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 字符串转换成十六进制字符串
     * 待转换的ASCII字符串
     * 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str)
    {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++)
        {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转换字符串
     *  str Byte字符串(Byte之间无分隔符 如:[616C6B])
     * 对应的字符串
     */
    public static String hexStr2Str(String hexStr)
    {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }

    /**
     * bytes转换成十六进制字符串
     * 数组
     * 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b)
    {
        String stmp;
        StringBuilder sb = new StringBuilder("");
        for(int n=0,length = b.length;n<length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytes字符串转换为Byte值
     * 字符串，每个Byte之间没有分隔符
     * @return byte[]
     */
    public static byte[] hexStr2Bytes(String src)
    {
        int m,n;
        int l=src.length()/2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++)
        {
            m=i*2+1;
            n=m+1;
            ret[i] = Byte.decode("0x" + src.substring(i*2, m) + src.substring(m,n));
        }
        return ret;
    }

    /**
     * String的字符串转换成unicode的String
     * 全角字符串
     * 每个unicode之间无分隔符
     * Exception
     */
    public static String strToUnicode(String strText)
            throws Exception
    {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++)
        {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * unicode的String转换成String的字符串
     * 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    public static String unicodeToString(String hex)
    {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++)
        {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转
            String s2 = s.substring(4);
            // 将16进制的string转为int
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

}
